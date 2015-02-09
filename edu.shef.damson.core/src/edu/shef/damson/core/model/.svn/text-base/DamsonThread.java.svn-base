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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

/**
 * A DAMSON thread. DAMSON Debugger is single threaded.
 */
public class DamsonThread extends DamsonDebugElement implements IThread {
	
	private String fName;
	private int fStatus;
	private int fProcessHandle;
	private DamsonStackFrame[] fFrames;
	
	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param target debugger
	 */
	public DamsonThread(DamsonDebugTarget target, String name, int process_handle, int state) {
		super(target);
		fName = name;
		fProcessHandle = process_handle;
		fStatus = state;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	public DamsonStackFrame[] getStackFrames() {
		return fFrames;
	}
	
	public void updateThreadData()
	{
		if (isSuspended() && !isTerminated()) {
			//update the stack frames
			String framesData = null;
			try {
				framesData = sendRequest("stack "+fProcessHandle);
			} catch (DebugException e) {}
			if (framesData != null) {
				String[] frames = framesData.split("#");
				fFrames = new DamsonStackFrame[frames.length];
				for (int i = 0; i < frames.length; i++) {
					String frame_format = frames[i];
					fFrames[i] = new DamsonStackFrame(this, frame_format, frames.length-i-1, i);
					//fFrames[i].updateFrameData();
				}
			}
		}
		else{
			fFrames = new DamsonStackFrame[0];
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	public int getPriority() throws DebugException {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	public IStackFrame getTopStackFrame() {
		if (fFrames.length > 0) {
			return fFrames[0];
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	public String getName() {
		return fName;
	}
	
	public int getStatus()
	{
		return fStatus;
	}
	
	public int getProcessHandle()
	{
		return fProcessHandle;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints() {
		return getDamsonDebugTarget().getBreakpoints();
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return getDamsonDebugTarget().isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		DamsonStackFrame frames[] = getStackFrames();
		for(int i=0;i<frames.length;i++)
		{
			frames[i].clearVariables();
		}
		getDamsonDebugTarget().resume();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		getDamsonDebugTarget().suspend();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return getDamsonDebugTarget().canStepReturn();
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		getDamsonDebugTarget().stepInto();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		getDamsonDebugTarget().stepOver();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		getDamsonDebugTarget().stepReturn();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return ((DamsonDebugTarget)getDebugTarget()).isStepping();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}
	




	
	

	
	/**
	 * Notification the target has resumed for the given reason.
	 * Fires a resume event.
	 * 
	 * @param detail reason for the resume
	 */
	public void notifyResume(int detail) {
		fireResumeEvent(detail);
	}

    

	
	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail reason for the suspend
	 */
	public void notifySuspend(int detail) {
		fireSuspendEvent(detail);
	}


	


	
}
