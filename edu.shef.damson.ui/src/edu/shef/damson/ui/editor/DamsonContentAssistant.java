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

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.widgets.Shell;

/**
 * DAMSON Content assistant
 */
public class DamsonContentAssistant extends ContentAssistant {
    
    public DamsonContentAssistant() {
        super();
        
        DamsonContentAssistProcessor processor= new DamsonContentAssistProcessor(); 
        setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
    
        enableAutoActivation(true);	//this does'nt seem to work but never mind
        setAutoActivationDelay(200);
        setProposalPopupOrientation(PROPOSAL_OVERLAY);
    	setContextInformationPopupOrientation(CONTEXT_INFO_ABOVE);
 
        enableAutoInsert(false);
        
        setInformationControlCreator(getInformationControlCreator());   
    }

    private IInformationControlCreator getInformationControlCreator() {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent);
            }
        };
    }
}
