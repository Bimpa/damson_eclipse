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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;


/**
 * DAMSON editor keyword scanner.
 */
public class DamsonScanner extends BufferedRuleBasedScanner {
    
	
	
    /**
     * DAMSON keywords
     */
    public static final String[] damsonKeywords = new String[] {
    	"if", "else", "while",   "do",  "for",   "switch", "break",
    	"continue", "case", "default", "int", "float", "void", "return", "extern"
    };
    
    /**
     * Special Directives
     */
    public static final String[] damsonDirectives = new String[] {
    	"#timestamp", "#node", "#alias", "#monitor", "#define", "#window", "#log", "#snapshot", "#include"
    };
    
    /**
     * DAMSON System functions
     */
    public static final String[] damsonProcs = new String[] {
    	"sendpkt", "delay", "printf", "setclk", "exit", "signal", "wait", "tickrate",
        "putbyte", "putword", "readsdram", "writesdram"
     };
    
    /**
     * DAMSON additional system functions
     */
    public static final String[] damsonFunctions = new String[] {
    	"getclk", "abs", "fabs", "createthread", "deletethread", "getbyte", "getword" 
     };
    
    /**
     * Detects potential keywords
     */
    class DamsonKeywordDetector implements IWordDetector {

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart(char c) {
            return Character.isLetter(c);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart(char c) {
            return Character.isLetter(c);
        }
    }

    
    /**
     * Detects DAMSON chars
     */
    class DamsonCharDetector implements IWordDetector {
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart(char c) {
            return c == '\'';
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart(char c) {
            return Character.isDefined(c);
        }
    }
   
    
  
    /**
     * Detects DAMSON directives
     */
    class DamsonDirectiveDetector implements IWordDetector {
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart(char c) {
            return c == '#';
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart(char c) {
            return Character.isLetter(c);
        }
    }
    

    /**
     * Constructs a scanner that identifies DAMSON keywords.
     */
    public DamsonScanner() {
    	TextAttribute attribute = new TextAttribute(DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_KEYWORD), 
    												DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_BACKGROUND), 
    												SWT.BOLD);
    	Token token= new Token(attribute);
    	
        //keywords
        WordRule keywords = new WordRule(new DamsonKeywordDetector());
        for (int i = 0; i < damsonKeywords.length; i++) {
            String keyword = damsonKeywords[i];
            keywords.addWord(keyword, token);
        }
        
        //keywords
        WordRule directives = new WordRule(new DamsonDirectiveDetector());
        for (int i = 0; i < damsonDirectives.length; i++) {
            String keyword = damsonDirectives[i];
            directives.addWord(keyword, token);
        }
        
        //procs
        WordRule procs = new WordRule(new DamsonKeywordDetector());
        for (int i = 0; i < damsonProcs.length; i++) {
            String keyword = damsonProcs[i];
            procs.addWord(keyword, token);
        }
        
        //functions
        WordRule functions = new WordRule(new DamsonKeywordDetector());
        for (int i = 0; i < damsonFunctions.length; i++) {
            String keyword = damsonFunctions[i];
            functions.addWord(keyword, token);
        }
        
        //strings and includes
        attribute = new TextAttribute(DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_STRINGS), 
				DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_BACKGROUND), 
				SWT.NORMAL);
        token= new Token(attribute);
        MultiLineRule strings = new MultiLineRule("\"", "\"", token);
        //MultiLineRule includes = new MultiLineRule("<", ">", token); //does not work with comparators
        
        //single line comments
    	attribute = new TextAttribute(DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_COMMENTS), 
				DebugUIPlugin.getDefault().getColor(DebugUIPlugin.OP_BACKGROUND), 
				SWT.NORMAL);
    	token= new Token(attribute);
    	EndOfLineRule comments = new EndOfLineRule("//", token);
    	MultiLineRule cppcomments = new MultiLineRule("//*", "*//", token);
        
        //set rules
        setRules(new IRule[]{comments, cppcomments, keywords, directives, procs, functions, strings});   
    }
    
	public IToken nextToken() {

		fTokenOffset= fOffset;
		fColumn= UNDEFINED;

		int prev_char;
		try {
			prev_char = fDocument.getChar(fOffset-1);
		} catch (BadLocationException e) {
			prev_char = ' ';//only if we are at fOffset 0
		}
		if (!Character.isLetterOrDigit(prev_char))
		{
			if (fRules != null) {
				for (int i= 0; i < fRules.length; i++) {
					IToken token= (fRules[i].evaluate(this));
					if (!token.isUndefined())
						return token;
				}
			}
		}


		if (read() == EOF)
			return Token.EOF;
		return fDefaultReturnToken;
	}
    
}
