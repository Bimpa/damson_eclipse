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


import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


/**
 * DAMSON editor partition scanner. Partitions the document where there are multi comments.
 */
public class DamsonPartitionScanner extends RuleBasedPartitionScanner {
    
	public static final String MULTILINE_COMMENT = "MULTILINE_COMMENT";
	
	public final static String[] PARTITION_TYPES = new String[] {MULTILINE_COMMENT};
	
    /**
     * Constructs a scanner that partitions a DAMSON document according to multi line comments
     */
    public DamsonPartitionScanner() {
        //comments

    	Token mltoken= new Token(MULTILINE_COMMENT);
    	
        MultiLineRule multiline_comments = new MultiLineRule("/*", "*/", mltoken, (char) 0, true);

        
        //set rules
        setPredicateRules(new IPredicateRule[]{multiline_comments});
    }
}
