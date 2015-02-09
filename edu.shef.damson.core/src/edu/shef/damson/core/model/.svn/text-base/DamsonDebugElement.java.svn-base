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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import edu.shef.damson.core.DebugCorePlugin;


/**
 * Common function for DAMSON debug elements.
 */
public class DamsonDebugElement extends DebugElement {

	/**
	 * Constructs a new debug element in the given target.
	 * 
	 * @param target debug target
	 */
	public DamsonDebugElement(IDebugTarget target) {
		super(target);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return DebugCorePlugin.ID_DAMSON_DEBUG_MODEL;
	}
	
	/**
	 * Sends a request to the DAMSON debugger, waits for and returns the reply.
	 * <p>
	 * Debugger debugger requests are as follows:
	 * <ul>
	 * 
	 * <li><code>set N</code> - sets the breakpoint on line <code>N</code>;
	 * 		reply is <code>OK set</code> or <code>FAILED set</code> 
	 * </li>
	 * 
	 * <li><code>clear N</code> - clears the breakpoint on line <code>N</code>;
	 * 		reply is <code>OK clear</code> or <code>FAILED set</code> 
	 * </li>
	 * 
	 * <li><code>resume</code> - resume execution of the program; reply is <code>OK resume</code>
	 * </li>
	 * 
	 * <li><code>step into</code> - step a single source code line; reply is <code>OK step</code>
	 * </li>
	 * 
	 * <li><code>step over</code> - step over single source code line passing over any function 
	 *   	entries; reply is <code>OK step</code>
	 * </li>
	 * 
	 * <li><code>step out</code> - step out of the current function on the call stack; reply is 
	 *   	<code>OK step</code>
	 * </li>
	 * 
	 * <li><code>stack</code> - return the contents of the call stack (program counters, function and
	 * 		variable names); reply is control stack from oldest to newest as a single string
	 * 		<code>frame#frame...#frame</code> where each frame is a string
	 * 		<code>"filename|source line|function name|variable name|variable name|...|variable name"</code>
	 * </li>
	 * 
	 * <li><code>var N M</code> - return the contents of variable <code>M</code> in the control
	 * 		stack frame <code>N</code> (stack frames are indexed from 0, 0 being the top of the call stack 
	 *      i.e. current function);	reply is variable value or <code>[S]</code> if variable is an array 
	 *      of size S.
	 * </li>
	 * 
	 * <li><code>var N M X</code> - return the contents of variable <code>M</code> in the control
	 * 		stack frame <code>N</code> (stack frames are indexed from 0, 0 being the top of the call stack 
	 *      i.e. current function) with indices <code>X</code> (where the format of X is number of indices 
	 *      followed by indices i.e. 3011);	reply is variable value or <code>[S]</code> if variable is an 
	 *      array of size S.
	 * </li>
	 * 
	 * <li><code>exit</code> - end the DAMSON debugger; reply is <code>OK exit</code>
	 * </li>
	 * 
	 * </ul>
	 * </p>
	 * 
	 * @param request command
	 * @return reply
	 * @throws DebugException if the request fails
	 */	
	public String sendRequest(String request) throws DebugException {
		return getDamsonDebugTarget().sendRequest(request);
	}
	
	/**
	 * Returns the debug target as a DAMSON target.
	 * 
	 * @return DAMSON debug target
	 */
	protected DamsonDebugTarget getDamsonDebugTarget() {
	    return (DamsonDebugTarget) getDebugTarget();
	}
	
	/**
	 * Returns the breakpoint manager
	 * 
     * @return the breakpoint manager
     */
    protected IBreakpointManager getBreakpointManager() {
        return DebugPlugin.getDefault().getBreakpointManager();
    }	
}
