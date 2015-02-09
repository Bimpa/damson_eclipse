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
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Value of a DAMSON variable. Is recursive in that the value may have more variables 
 * (i.e. an array). As such the value keeps track of the associated DamsonVariable.
 */
public class DamsonValue extends DamsonDebugElement implements IValue {
	
	private String fValue;
	private DamsonVariable fVariable;
	private DamsonVariable fVariables[] = null;
	
	
	public DamsonValue(DamsonDebugTarget target, DamsonVariable variable, String value) {
		super(target);
		fVariable = variable;
		fValue = value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		try {
			Integer.parseInt(fValue);
		} catch (NumberFormatException e) {
			return "text";
		}
		return "integer";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		return fValue;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	/**
	 * Gets the associated DamsonVariable
	 * @return a DamsonVariable
	 */
	public DamsonVariable getDamsonVariable()
	{
		return fVariable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		//only update if the value is null (i.e) has'nt already been fetched.
		if (fVariables == null)
			updateVariablesData();
		return fVariables;
	}
	
	public void updateVariablesData()
	{
		int vars = Integer.parseInt(fValue.substring(1, fValue.length()-1));
		fVariables = new DamsonVariable[vars];
		for (int i = 0; i < vars; i++) {
			String display_name = getDamsonVariable().getVaribleName();
			//create a new indices array and update the display name
			int [] indices = new int[getDamsonVariable().getIndices().length+1];
			indices[0] = getDamsonVariable().getIndices()[0]+1;
			for (int j=1; j< getDamsonVariable().getIndices().length; j++){
				indices[j] = getDamsonVariable().getIndices()[j];
				display_name += "["+getDamsonVariable().getIndices()[j]+"]";
			}
			indices[getDamsonVariable().getIndices().length] = i;
			display_name += "["+i+"]";
			
			fVariables[i] = new DamsonVariable(getDamsonVariable().getStackFrame(), display_name, fVariable.getVaribleName(), indices, fVariable.isGlobal());
		}
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return fValue.matches("\\[[0-9]+\\]");
	}
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj) {
        return obj instanceof DamsonValue && ((DamsonValue)obj).fValue.equals(fValue);
    }
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fValue.hashCode();
    }
}
