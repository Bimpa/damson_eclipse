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
package edu.shef.damson.core.breakpoints;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

import edu.shef.damson.core.DebugCorePlugin;
import edu.shef.damson.core.model.DamsonDebugTarget;
import edu.shef.damson.core.model.IDamsonEventListener;


/**
 * DAMSON line breakpoint
 */
public class DamsonLineBreakpoint extends LineBreakpoint implements IDamsonEventListener {
	
	// target currently installed in
	private DamsonDebugTarget fTarget;
	private int fAliasCondition;
	private boolean fAliasConditionEnabaled;
	
	public boolean isAliasConditionEnabaled() {
		return fAliasConditionEnabaled;
	}

	public void setAliasConditionEnabaled(boolean fAliasConditionEnabaled) throws CoreException{
		this.fAliasConditionEnabaled = fAliasConditionEnabaled;
	}

	public int getAliasCondition() {
		return fAliasCondition;
	}
	
	public String getAliasConditionText() {
		if (fAliasCondition == 0)
			return "";
		else
			return String.valueOf(fAliasCondition);
	}

	public void setAliasCondition(int alias_condition) throws CoreException{
		this.fAliasCondition = alias_condition;
	}

	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public DamsonLineBreakpoint() {
		fAliasCondition = 0;
		fAliasConditionEnabaled = false;
	}
	
	/**
	 * Constructs a line breakpoint on the given resource at the given
	 * line number. The line number is 1-based (i.e. the first line of a
	 * file is line number 1).
	 * 
	 * @param resource file on which to set the breakpoint
	 * @param lineNumber 1-based line number of the breakpoint
	 * @throws CoreException if unable to create the breakpoint
	 */
	public DamsonLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("edu.shef.damson.core.markerType.lineBreakpoint");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: " + resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return DebugCorePlugin.ID_DAMSON_DEBUG_MODEL;
	}
	
	/**
	 * Returns whether this breakpoint is a run-to-line breakpoint
	 * 
	 * @return whether this breakpoint is a run-to-line breakpoint
	 */
	public boolean isRunToLineBreakpoint() {
		return false;
	}
    
    /**
     * Installs this breakpoint in the given DAMSON instance.
     * Registers this breakpoint as an event listener in the
     * given target and creates the breakpoint specific request.
     * 
     * @param target DAMSON instance
     * @throws CoreException if installation fails
     */
    public void install(DamsonDebugTarget target) throws CoreException {
    	fTarget = target;
    	target.addEventListener(this);
    	createRequest(target);
    }
    
    /**
     * Update the breakpoint alias condition by sending a new set request
     * @throws CoreException
     */
    public void update() throws CoreException{
		if (fTarget != null)
			createRequest(fTarget);
    }
    
    /**
     * Create the breakpoint specific request in the target. Subclasses
     * should override.
     * 
     * @param target DAMSON instance
     * @throws CoreException if request creation fails
     */
    protected void createRequest(DamsonDebugTarget target) throws CoreException {
    	int alias_condition = 0;
    	if (isAliasConditionEnabaled())
    		alias_condition = fAliasCondition;
    	target.sendRequest("set " + getLineNumber() + " " + alias_condition);
    }
    
    /**
     * Removes this breakpoint's event request from the target. Subclasses
     * should override.
     * 
     * @param target DAMSON instance
     * @throws CoreException if clearing the request fails
     */
    protected void clearRequest(DamsonDebugTarget target) throws CoreException {
    	target.sendRequest("clear " + (getLineNumber()));
    }
    
    /**
     * Removes this breakpoint from the given DAMSON instance.
     * Removes this breakpoint as an event listener and clears
     * the request for the DAMSON instance.
     * 
     * @param target DAMSON instance
     * @throws CoreException if removal fails
     */
    public void remove(DamsonDebugTarget target) throws CoreException {
    	target.removeEventListener(this);
    	clearRequest(target);
    	fTarget = null;
    	
    }
    
    /**
     * Returns the target this breakpoint is installed in or <code>null</code>.
     * 
     * @return the target this breakpoint is installed in or <code>null</code>
     */
    protected DamsonDebugTarget getDebugTarget() {
    	return fTarget;
    }
    
    /**
     * Notify's the DAMSON instance that this breakpoint has been hit.
     */
    protected void notifyTarget() {
    	if (fTarget != null) {
			fTarget.notifySuspendBy(this);  		
    	}
    }

	/* (non-Javadoc)
	 * 
	 * Subclasses should override to handle their breakpoint specific event.
	 * 
	 * @see org.eclipse.debug.examples.core.pda.model.IPDAEventListener#handleEvent(java.lang.String)
	 */
	public void handleEvent(String event) {
		if (event.startsWith("suspended breakpoint")) {
			handleHit(event);
		}
	}
    
	/**
     * Determines if this breakpoint was hit and notifies the thread.
     * 
     * @param event breakpoint event
     */
    private void handleHit(String event) {
    	int lastSpace = event.lastIndexOf(' ');
    	if (lastSpace > 0) {
    		String line = event.substring(lastSpace + 1);
    		int lineNumber = Integer.parseInt(line);
    		try {
				if (getLineNumber() == lineNumber) {
					notifyTarget();
				}
    		} catch (CoreException e) {
    		}
    	}
    }		
}
