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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Content Assistant Processor
 * Very basic content assistant offers key words, macros and system functions as content 
 * assistance options regardless of context.
 */
public class DamsonContentAssistProcessor implements IContentAssistProcessor {

	class CompletionProposalComparitor implements java.util.Comparator<CompletionProposal>{

	    public int compare(CompletionProposal o1, CompletionProposal o2) {
	        return o1.getDisplayString().compareTo(o2.getDisplayString());
	    }
	}
	
	/**
	 * Returns a list of proposals. In this case system functions, procedures, directives and keywords
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        int index = offset - 1;
        StringBuffer prefix = new StringBuffer();
        IDocument document = viewer.getDocument();
        while (index > 0) {
            try {
                char prev = document.getChar(index);
                if (Character.isWhitespace(prev)) {
                    break;
                }
                prefix.insert(0, prev);
                index--;
            } catch (BadLocationException e) {
            }
        }
        
        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

        //Started typing
        if (prefix.length() > 0) {
            String word = prefix.toString();
            // propose matching Op keywords
            for (int i = 0; i < DamsonScanner.damsonKeywords.length; i++) {
                String keyword = DamsonScanner.damsonKeywords[i];
                if (keyword.startsWith(word) && word.length() < keyword.length()) {
                    proposals.add(new CompletionProposal(keyword + " ", index + 1, offset - (index + 1), keyword.length() + 1));
                }
            }
            // propose matching special Op keywords
            for (int i = 0; i < DamsonScanner.damsonDirectives.length; i++) {
                String keyword = DamsonScanner.damsonDirectives[i];
                if (keyword.startsWith(word) && word.length() < keyword.length()) {
                    proposals.add(new CompletionProposal(keyword + " ", index + 1, offset - (index + 1), keyword.length() + 1));
                }
            }
            // propose matching Op keywords
            for (int i = 0; i < DamsonScanner.damsonProcs.length; i++) {
                String keyword = DamsonScanner.damsonProcs[i];
                if (keyword.startsWith(word) && word.length() < keyword.length()) {
                    proposals.add(new CompletionProposal(keyword + " ", index + 1, offset - (index + 1), keyword.length() + 1));
                }
            }
            // propose matching additional keywords
            for (int i = 0; i < DamsonScanner.damsonFunctions.length; i++) {
                String keyword = DamsonScanner.damsonFunctions[i];
                if (keyword.startsWith(word) && word.length() < keyword.length()) {
                    proposals.add(new CompletionProposal(keyword + " ", index + 1, offset - (index + 1), keyword.length() + 1));
                }
            }
        //No filter
        } else {
            // propose all Op keywords
            for (int i = 0; i < DamsonScanner.damsonKeywords.length; i++) {
                String keyword = DamsonScanner.damsonKeywords[i];
                proposals.add(new CompletionProposal(keyword + " ", offset, 0, keyword.length() + 1));
            }
            // propose all special op keywords
            for (int i = 0; i < DamsonScanner.damsonDirectives.length; i++) {
                String keyword = DamsonScanner.damsonDirectives[i];
                proposals.add(new CompletionProposal(keyword + " ", offset, 0, keyword.length() + 1));
            }
            //propose call keywords
            for (int i = 0; i < DamsonScanner.damsonProcs.length; i++) {
                String keyword = DamsonScanner.damsonProcs[i];
                proposals.add(new CompletionProposal(keyword + " ", offset, 0, keyword.length() + 1));
            }
            //propose call keywords
            for (int i = 0; i < DamsonScanner.damsonFunctions.length; i++) {
                String keyword = DamsonScanner.damsonFunctions[i];
                proposals.add(new CompletionProposal(keyword + " ", offset, 0, keyword.length() + 1));
            }
        }
        
        if (!proposals.isEmpty()) {
        	//sort them for good measure!
        	Collections.sort(proposals, new CompletionProposalComparitor());
            return proposals.toArray(new ICompletionProposal[proposals.size()]);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

}
