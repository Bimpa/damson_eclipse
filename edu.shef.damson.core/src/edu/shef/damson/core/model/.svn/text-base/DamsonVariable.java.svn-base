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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * A variable in a DAMSON stack frame. Value may be either an integer or an array size 
 * indicating that the DamsonVariable is an array.
 */
public class DamsonVariable extends DamsonDebugElement implements IVariable {
	
	// name & stack frame
	protected String fName;
	protected String fVarName;
	private DamsonStackFrame fFrame;
	//holds the indices of the variable. A value of 0 indicates it is a singleton.
	private int [] fIndices;
	private boolean fGlobal;
	private DamsonValue fValue;

	
	public DamsonVariable(DamsonStackFrame frame, String name, String varName, int [] indices, boolean global) {
		super(frame.getDamsonDebugTarget());
		fFrame = frame;
		fName = name;
		fVarName = varName;
		fIndices = indices;
		fGlobal = global;
		requestVariableData();
	}
	
	public DamsonVariable(DamsonStackFrame frame, String name, boolean global) {
		this(frame, name, name, new int[]{0}, global);
	}
	
	public void requestVariableData()
	{
		String request;
		if (fIndices[0]>0)
		{
			if (fGlobal)
				request = "garrayvar " + getVaribleName();
			else
				request = "arrayvar " + getStackFrame().getThread().getProcessHandle() + " " + getStackFrame().getStackFrame() + " " + getVaribleName();
			for (int i=0; i< getIndices().length; i++)	//append indices to request
				request += " " + getIndices()[i];
		}
		else
		{
			if (fGlobal)
				request = "gvar " + getVaribleName();
			else
				request = "var " + getStackFrame().getThread().getProcessHandle() + " " + getStackFrame().getStackFrame() + " " + getVaribleName();
		}
		String value;
		try {
			value = sendRequest(request);
			fValue = new DamsonValue(this.getDamsonDebugTarget(), this, value);
		} catch (DebugException e) {
			fValue = null;
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return fValue;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return fName;
	}
	
	public String getVaribleName()
	{
		return fVarName;
	}

	public int[] getIndices()
	{
		return fIndices;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return "Thing";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	public void setValue(String expression) throws DebugException {
		boolean match = false;
		String value = expression;
		
		if (expression.matches("[0-9]+"))
		{
			match = true;
		}
		else if(expression.matches("[0-9]+.[0-9]+"))
		{
			//convert decimal to 16:16 fixed point integer
			int intValue = (int)((Double.parseDouble(expression))*65536);
			value = Integer.toString(intValue);
			match = true;
		}
		
		if (match)
		{
			//if array variable then request requires indices
			String request;
			if (fIndices[0]>0)
			{
				if (fGlobal)
					request = "setgarrayvar " + getVaribleName();
				else
					request = "setarrayvar " + getStackFrame().getThread().getProcessHandle() + " " + getStackFrame().getStackFrame() + " " + getVaribleName();
				for (int i=0; i< getIndices().length; i++)	//append indices to request
					request += " " + getIndices()[i];
				request += " " + value;
			}
			else
			{
				if (fGlobal)
					request = "setgvar " + getVaribleName() + " " + value;
				else
					request = "setvar " + getStackFrame().getThread().getProcessHandle() + " " + getStackFrame().getStackFrame() + " " + getVaribleName() + " " + value;
			}
			sendRequest(request);
			fireChangeEvent(DebugEvent.CONTENT);
			requestVariableData();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}
	
	public boolean isGlobal(){
		return fGlobal;
	}
	
	/**
	 * Returns the stack frame owning this variable.
	 * 
	 * @return the stack frame owning this variable
	 */
	protected DamsonStackFrame getStackFrame() {
		return fFrame;
	}
	
	
	

}
