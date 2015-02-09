/******************************************************************************************
 * Copyright (c) 2011, University of Sheffield
 * 
 * The source code for the DAMSON Debugger is available for non commercial use.
 * The code is based up that of Bjorn Freeman-Benson and IBM Corporation which is 
 * described at 
 * 
 * http://www.eclipse.org/articles/Article-Debugger/how-to.html
 * 
 * and is distributed under the Eclipse Public License v1.0
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Paul Richmond (http://www.paulrichmond.staff.shef.ac.uk/) - DAMSON debugger
 *     IBM Corporation and Bjorn Freeman-Benson - initial code developed for a PDA debugger
 ******************************************************************************************/
package edu.shef.damson.core.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import edu.shef.damson.core.DebugCorePlugin;
import edu.shef.damson.core.breakpoints.DamsonLineBreakpoint;
import edu.shef.damson.core.breakpoints.DamsonRunToLineBreakpoint;


/**
 * DAMSON Debug Target
 */
public class DamsonDebugTarget extends DamsonDebugElement implements IDebugTarget, IBreakpointManagerListener, IDamsonEventListener {
	
	// associated system process (DAMSON debugger)
	private IProcess fProcess;
	
	// containing launch object
	private ILaunch fLaunch;
	
	// sockets to communicate with debugger
	private Socket fRequestSocket;
	private PrintWriter fRequestWriter;
	private BufferedReader fRequestReader;
	private Socket fEventSocket;
	private BufferedReader fEventReader;
	
	//DAMSON emulator debugger states
	private boolean fSuspended = false;
	private boolean fStepping = false;
	private IBreakpoint fBreakpoint;	//current breakpoint, null if node

	//target source
	String fTargetSourceFile;
	int fCurrentNode;
	
	// threads
	private DamsonThread[] fThreads;
	
	// event dispatch job
	private EventDispatchJob fEventDispatch;
	// event listeners
	private Vector<IDamsonEventListener> fEventListeners = new Vector<IDamsonEventListener>();
	
	/**
	 * Listens to events from the DAMSON debugger and fires corresponding 
	 * debug events.
	 */
	class EventDispatchJob extends Job {
		
		public EventDispatchJob() {
			super("DAMSON Event Dispatch");
			setSystem(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			String event = "";
			while (!isTerminated() && event != null) {
				try {
					event = fEventReader.readLine();
					if (event != null) {
						Object[] listeners = fEventListeners.toArray();
						for (int i = 0; i < listeners.length; i++) {
							((IDamsonEventListener)listeners[i]).handleEvent(event);	
						}
					}
				} catch (IOException e) {
					notifyTerminate();
				}
			}
			return Status.OK_STATUS;
		}
		
	}
	
	/**
	 * Registers the given event listener. The listener will be notified of
	 * events in the program being interpretted. Has no effect if the listener
	 * is already registered.
	 *  
	 * @param listener event listener
	 */
	public void addEventListener(IDamsonEventListener listener) {
		if (!fEventListeners.contains(listener)) {
			fEventListeners.add(listener);
		}
	}
	
	/**
	 * Deregisters the given event listener. Has no effect if the listener is
	 * not currently registered.
	 *  
	 * @param listener event listener
	 */
	public void removeEventListener(IDamsonEventListener listener) {
		fEventListeners.remove(listener);
	}
	
	/**
	 * Constructs a new debug target in the given launch for the 
	 * associated DAMSON process.
	 * 
	 * @param launch containing launch
	 * @param process DAMSON debugger processes
	 * @exception CoreException if unable to connect to host
	 */
	public DamsonDebugTarget(ILaunch launch, IProcess process) throws CoreException {
		super(null);
		fLaunch = launch;
		fProcess = process;
		addEventListener(this);
		try {
			// give DAMSON chance to parse '*.d' file and start debugger
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			try{
				fRequestSocket = new Socket("localhost", DebugCorePlugin.REQUEST_PORT);
			} catch(ConnectException e){return;} //silent fail (assume compiler failed)
			fRequestWriter = new PrintWriter(fRequestSocket.getOutputStream());
			fRequestReader = new BufferedReader(new InputStreamReader(fRequestSocket.getInputStream()));
			// give DAMSON a chance to open next socket
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			try{
			fEventSocket = new Socket("localhost", DebugCorePlugin.EVENT_PORT);
			} catch(ConnectException e){return;} //silent fail (assume compiler failed)
			fEventReader = new BufferedReader(new InputStreamReader(fEventSocket.getInputStream()));
		} catch (UnknownHostException e) {
			requestFailed("Unable to connect to DAMSON Debugger", e);
		} catch (IOException e) {
			requestFailed("Unable to connect to DAMSON Debugger", e);
		}
		
		//will be removed as getThreads will be dynamic
		fThreads = new DamsonThread[0];
		fEventDispatch = new EventDispatchJob();
		fEventDispatch.schedule();
		
		//add breakpoint manager
		IBreakpointManager breakpointManager = getBreakpointManager();
        breakpointManager.addBreakpointListener(this);
		breakpointManager.addBreakpointManagerListener(this);
	}

    /* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return fProcess;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() {
		return fThreads;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() {
		return (getThreads().length > 0);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		return "DAMSON";
	}
	
	/**
	 * Gets the current node number
	 * 
	 * @return Current Node Number
	 */
	public int getCurrentNode()
	{
		return fCurrentNode;
	}
	
	/**
	 * Gets the current target source file
	 * @return
	 */
	public String getTargetSourceFile()
	{
		return fTargetSourceFile;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (!isTerminated() && breakpoint.getModelIdentifier().equals(getModelIdentifier())) {
			try {
				String program = getLaunch().getLaunchConfiguration().getAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, (String)null);
				//map .dno program to .d source file
				//if (program.endsWith(".dno"))
				//{
				//	program = program.substring(0, program.length()-3) + "d";
				//}
				if (program != null) {
					IResource resource = null;
					if (breakpoint instanceof DamsonRunToLineBreakpoint) {
						DamsonRunToLineBreakpoint rtl = (DamsonRunToLineBreakpoint) breakpoint;
						resource = rtl.getSourceFile();
					} else {
						IMarker marker = breakpoint.getMarker();
						if (marker != null) {
							resource = marker.getResource();
						}
					}
					if (resource != null) {
						IPath p = new Path(program);
						return resource.getFullPath().equals(p);
					}
				}
			} catch (CoreException e) {
			}			
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return this;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return fLaunch;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return getProcess().canTerminate();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return getProcess().isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if ((breakpoint.isEnabled() && getBreakpointManager().isEnabled()) || !breakpoint.isRegistered()) {
					DamsonLineBreakpoint damsonBreakpoint = (DamsonLineBreakpoint)breakpoint;
				    damsonBreakpoint.install(this);
				}
			} catch (CoreException e) {
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
			    DamsonLineBreakpoint pdaBreakpoint = (DamsonLineBreakpoint)breakpoint;
				pdaBreakpoint.remove(this);
			} catch (CoreException e) {
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled() && getBreakpointManager().isEnabled()) {
					breakpointAdded(breakpoint);
				} else {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	/**
	 * Notification we have connected to the DAMSON debugger and it has started.
	 * Resume the debugger.
	 */
	private void started() {
		fireCreationEvent();
		installDeferredBreakpoints();
		try {
			resume();
		} catch (DebugException e) {
		}
	}
	
	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}
	
	/**
	 * Called when this debug target terminates.
	 */
	private synchronized void notifyTerminate() {
		fThreads = new DamsonThread[0];
		IBreakpointManager breakpointManager = getBreakpointManager();
        breakpointManager.removeBreakpointListener(this);
		breakpointManager.removeBreakpointManagerListener(this);
		fireTerminateEvent();
		removeEventListener(this);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.examples.core.pda.model.PDADebugElement#sendRequest(java.lang.String)
	 */
	public String sendRequest(String request) throws DebugException {
		synchronized (fRequestSocket) {
			fRequestWriter.print(request);
			fRequestWriter.flush();
			try {
				// wait for reply
				return fRequestReader.readLine();
			} catch (IOException e) {
				requestFailed("Request failed: " + request, e);
			}
		}
		return null;
	}  
	
	/**
	 * When the breakpoint manager disables, remove all registered breakpoints
	 * requests from the debugger. When it enables, reinstall them.
	 */
	public void breakpointManagerEnablementChanged(boolean enabled) {
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			if (enabled) {
				breakpointAdded(breakpoints[i]);
			} else {
				breakpointRemoved(breakpoints[i], null);
			}
        }
	}	
	
	public void updateTargetData()
	{
		if (isSuspended()&&!isTerminated()) { //should always be true
			//update source details
			String sourceData[] = new String[0];
			try {
				sourceData = sendRequest("source").split("\\|");
			} catch (DebugException e1) {}
			if (sourceData.length == 2)
			{
				fTargetSourceFile = new Path(sourceData[0]).lastSegment();
				fCurrentNode = Integer.parseInt(sourceData[1]);
			}
			
			//update the current threads
			String threads[] = new String[0];
			try {
				threads = sendRequest("threads").split("#");
			} catch (DebugException e) {
				e.printStackTrace();
			}
			fThreads = new DamsonThread[threads.length];
			for (int i=0; i< threads.length; i++)
			{
				String threadData[] = threads[i].split("\\|");
				if (threadData.length == 2){
					int handle = Integer.parseInt(threadData[0]);
					int status = Integer.parseInt(threadData[1]);
					fThreads[i] = new DamsonThread(this, "Thread["+handle+"]", handle, status);
					//update the threads data
					fThreads[i].updateThreadData();
				}
			}

		}else
		{
			fThreads = new DamsonThread[0];
			fTargetSourceFile = "";
			fCurrentNode = 0;
		}
		
	}
	
	/* (non-Javadoc)
	 * 
	 */
	public void handleEvent(String event) {
		// clear previous state
		fBreakpoint = null;
		setStepping(false);
		
		// handle events
		if (event.equals("started")) {
			started();
		} else if (event.equals("terminated")) {
			notifyTerminate();
		} else if (event.startsWith("resumed")) {
			setSuspended(false);
			if (event.endsWith("step")) {
				setStepping(true);
				notifyResume(DebugEvent.STEP_OVER);
			} else if (event.endsWith("breakpoint")) {
				notifyResume(DebugEvent.BREAKPOINT);
			}
		} else if (event.startsWith("suspended")) {
			setSuspended(true);
			if (event.startsWith("suspended breakpoint")) {
				updateTargetData();
				notifySuspend(DebugEvent.BREAKPOINT);
			} else if (event.endsWith("step")) {
				updateTargetData();
				notifySuspend(DebugEvent.STEP_END);
			}
		} else if (event.equals("started")) {
			fireCreationEvent();
		}
		
	}
	
	/**
	 * Sets whether DAMSOn emulator is stepping
	 * 
	 * @param stepping whether stepping
	 */
	private void setStepping(boolean stepping) {
		fStepping = stepping;
	}
	
	/**
	 * Sets whether DAMSON emulator is suspended
	 * 
	 * @param suspended whether suspended
	 */
	private void setSuspended(boolean suspended) {
		fSuspended = suspended;
	}
	
	/**
	 * Notification the target has resumed for the given reason.
	 * Makes sure that the top thread (active thread) fires the resumed event to ensure that it maintains the focus
	 * 
	 * @param detail reason for the resume
	 */
	private void notifyResume(int detail) {
		if(fThreads.length > 0)
			fThreads[0].notifyResume(detail);
	}

	/**
	 * Notification the target has suspended for the given reason (could be step or breakpoint)
	 * Updates the target source (i.e. source filename and current node)
	 * Makes sure that the top thread (active thread) fires the suspended event to ensure that it maintains the focus
	 * 
	 * @param detail reason for the suspend
	 */
	private void notifySuspend(int detail) {
		//make sure that the top thread is notified to ensure it is selected
		if(fThreads.length > 0)
			fThreads[0].notifySuspend(detail);
	}
	
	/**
	 * Notifies the debug target it has been suspended by the given breakpoint.
	 * 
	 * @param breakpoint breakpoint
	 */
	public void notifySuspendBy(IBreakpoint breakpoint) {
		fBreakpoint = breakpoint;
		notifySuspend(DebugEvent.BREAKPOINT);
	}
	
	/**
	 * Checks the top thread (active thread) to see if there is more than one item on the stack frame.
	 * Could be moved to DamsonThread
	 * 
	 * @return is able to perform a step return
	 */
	public boolean canStepReturn() {
		if ((fThreads.length > 0)&&(isSuspended()))
		{
			if (fThreads[0].getStackFrames().length > 1)
				return true;
			else
				return false;
		}
		else return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoint == null) {
			return new IBreakpoint[0];
		}
		return new IBreakpoint[]{fBreakpoint};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return fSuspended && !isTerminated();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return fStepping;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		sendRequest("resume");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
	    sendRequest("suspend");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		sendRequest("step into");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		sendRequest("step over");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		sendRequest("step out");
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		sendRequest("exit");
	}

	
}
