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
package edu.shef.damson.ui.presentation;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;

import edu.shef.damson.core.DebugCorePlugin;
import edu.shef.damson.core.breakpoints.DamsonLineBreakpoint;
import edu.shef.damson.core.model.DamsonDebugTarget;
import edu.shef.damson.core.model.DamsonStackFrame;
import edu.shef.damson.core.model.DamsonThread;
import edu.shef.damson.core.model.DamsonVariable;
import edu.shef.damson.ui.DebugUIPlugin;

import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;


/**
 * Renders DAMSON debug elements
 */
public class DamsonModelPresentation extends LabelProvider implements IDebugModelPresentation {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attribute, Object value) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof DamsonDebugTarget) {
			return getTargetText((DamsonDebugTarget)element);
		} else if (element instanceof DamsonThread) {
	        return getThreadText((DamsonThread)element);
	    } else if (element instanceof DamsonStackFrame) {
	        return getStackFrameText((DamsonStackFrame)element);
	    } 
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof DamsonVariable)
		{
			DamsonVariable var = (DamsonVariable)element;
			if (!var.isGlobal())
				return DebugUIPlugin.getDefault().getImageRegistry().get(DebugUIPlugin.IMG_OBJ_VARIABLES_LOCAL);
			else
				return DebugUIPlugin.getDefault().getImageRegistry().get(DebugUIPlugin.IMG_OBJ_VARIABLES_GLOBAL);
		}
		return super.getImage(element);
	}
	

    /**
	 * Returns a label for the given debug target
	 * 
	 * @param target debug target
	 * @return a label for the given debug target
	 */
	private String getTargetText(DamsonDebugTarget target) {
		//get the actual current source file not the Damson program that was launched (this may be different is includes have been used)
		String targetSourceFile = target.getTargetSourceFile();
		if (targetSourceFile != null) {
		    if (target.isTerminated()) {
			    return "<terminated> DAMSON [" + targetSourceFile + "]";
		    }
		    else if (target.isStepping()){	
		    	return "DAMSON [" + targetSourceFile + "] (Stepping)";
		    }else{
		        IBreakpoint[] breakpoints = target.getBreakpoints();
		        if (breakpoints.length == 0) {
		        	return "DAMSON [" + targetSourceFile + "] at Node " + target.getCurrentNode() + " (Suspended: Step)";
		        } else {
		            IBreakpoint breakpoint = breakpoints[0]; 
		            if (breakpoint instanceof DamsonLineBreakpoint) {
		            	DamsonLineBreakpoint damsonBreakpoint = (DamsonLineBreakpoint) breakpoint;
		            	if (damsonBreakpoint.isRunToLineBreakpoint()) {
		            		return "DAMSON [" + targetSourceFile + "] at Node " + target.getCurrentNode() + " (Suspended: Run to Line Breakpoint)";
		            	} else {
		            		return "DAMSON [" + targetSourceFile + "] at Node " + target.getCurrentNode() + " (Suspended: Breakpoint)";
		            	}
		            }
		        }
		    		
		    }
		}
		return "DAMSON";
		
	}
	
	/**
	 * Returns a label for the given stack frame
	 * 
	 * @param frame a stack frame
	 * @return a label for the given stack frame 
	 */
	private String getStackFrameText(DamsonStackFrame frame) {
	    try {
	       return frame.getName() + "()  at line " + frame.getLineNumber(); 
	    } catch (DebugException e) {
	    }
	    return null;

	}
	
	/**
	 * Returns a label for the given thread
	 * 
	 * @param thread a thread
	 * @return a label for the given thread
	 */
	private String getThreadText(DamsonThread thread) {
	    String label = thread.getName();
	    if (thread.isStepping()) {
	        label += " (Stepping)";
	    } else if (thread.isTerminated()) {
	        label = "<terminated> " + label;
	    }else{
	    	switch (thread.getStatus()){
	    	case (DebugCorePlugin.THREAD_STATE_RUNNING):
	    		label += " (Running)";
	    		break;
	    	case (DebugCorePlugin.THREAD_STATE_WAITING):
	    		label += " (Waiting)";
	    		break;
	    	case (DebugCorePlugin.THREAD_STATE_DELAYING):
	    		label += " (Delaying)";
	    		break;
	    	default:
	    		label += " (Unknown State)";
	    		break;
	    	}
	    }
	    return label;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return "editor";
		}
		return null;
	}
}
