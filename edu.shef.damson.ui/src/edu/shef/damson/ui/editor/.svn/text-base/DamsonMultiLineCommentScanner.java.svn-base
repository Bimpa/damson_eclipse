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

import edu.shef.damson.ui.DebugUIPlugin;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;


/**
 * DAMSON multi line comment scanner. Scans the partition using a MultiLineRule to identify comments. 
 */
public class DamsonMultiLineCommentScanner extends RuleBasedScanner {
    
	

    /**
     * Constructs a scanner that identifies DAMSON keywords. The default token is set which ensures the 
     * entire partition (as determined by the DamsonPartitionScanner) applies the text attributes of the 
     * comment token.
     */
    public DamsonMultiLineCommentScanner() {
    	TextAttribute attribute = new TextAttribute(DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_KEYWORD), 
    												DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_BACKGROUND), 
    												SWT.BOLD);
    	Token token= new Token(attribute);
    	
        //comments
    	attribute = new TextAttribute(DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_COMMENTS), 
				DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_BACKGROUND), 
				SWT.NORMAL);
    	token= new Token(attribute);
    	MultiLineRule multiline_comments = new MultiLineRule("/*", "*/", token, (char) 0, true);
        
    	//IMPORTANT: set default token (otherwise line based buffering will cause problems when entering new lines)
    	setDefaultReturnToken(token);
    	
        //set rules
        setRules(new IRule[]{multiline_comments});
        
    }
}
