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

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * DAMSON text editor with ruler and editor menu and content assistance
 */
public class DamsonEditor extends AbstractDecoratedTextEditor {
    
    /**
     * Creates a DAMSON editor
     */
    public DamsonEditor() {
        super();
        setSourceViewerConfiguration(new DamsonSourceViewerConfiguration());
        setRulerContextMenuId("editor.rulerMenu");
        setEditorContextMenuId("editor.editorMenu");
        
        setDocumentProvider(new DamsonDocumentProvider());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {
        super.createActions();
        ResourceBundle bundle = ResourceBundle.getBundle("edu.shef.damson.ui.editor.DamsonEditorMessages");
        IAction action = new ContentAssistAction(bundle, "ContentAssistProposal.", this); 
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);
        markAsStateDependentAction("ContentAssistProposal", true);
    }
    
    
}
