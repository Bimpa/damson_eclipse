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
package edu.shef.damson.ui.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import edu.shef.damson.core.model.DamsonDebugTarget;
import edu.shef.damson.core.model.DamsonStackFrame;
import edu.shef.damson.core.model.DamsonThread;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;


/**
 * Produces debug hover for the DAMSON debugger.
 */
public class TextHover implements ITextHover {

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        String varName = null;
        try {
            varName = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
        } catch (BadLocationException e) {
           return null;
        }
   
        DamsonStackFrame frame = null;
        IAdaptable debugContext = DebugUITools.getDebugContext();
        if (debugContext instanceof DamsonStackFrame) {
           frame = (DamsonStackFrame) debugContext;
        } else if (debugContext instanceof DamsonThread) {
            DamsonThread thread = (DamsonThread) debugContext;
            frame = (DamsonStackFrame) thread.getTopStackFrame();
        } else if (debugContext instanceof DamsonDebugTarget) {
            DamsonDebugTarget target = (DamsonDebugTarget) debugContext;
            try {
                IThread[] threads = target.getThreads();
                if (threads.length > 0) {
                    frame = (DamsonStackFrame) threads[0].getTopStackFrame();
                }
            } catch (DebugException e) {
                return null;
            }
        }
        if (frame != null) {
            try {
            	//check the top stack frame for variables with corresponding name
                IVariable[] variables = frame.getVariables();
                for (int i = 0; i < variables.length; i++) {
                    IVariable variable = variables[i];
                    if (variable.getName().equals(varName)) {
                        return varName + " = " + variable.getValue().getValueString(); 
                    }
                }
            } catch (DebugException e) {
            }
        }
        return null;
    }
    	
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
     */
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return WordFinder.findWord(textViewer.getDocument(), offset);
    }

}
