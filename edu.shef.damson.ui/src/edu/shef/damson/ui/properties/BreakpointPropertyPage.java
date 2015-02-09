package edu.shef.damson.ui.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;


import edu.shef.damson.core.breakpoints.DamsonLineBreakpoint;

public class BreakpointPropertyPage extends PropertyPage {

	private Text conditionText;
    private Button 		 enableConditionButton;
    private Button 		 enabledButton;
	
	/**
	 * Constructor for PropertyPage.
	 */
	public BreakpointPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = createSubComposite(parent, 1);
        noDefaultAndApplyButton();

        createLabels(composite);

        try
        {
            createEnabledButton(composite);
            createConditionEditor(composite);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return null;
        }

        setValid(true);

        // TODO change the dialog window title

        return composite;
	}
	
	protected Composite createSubComposite(Composite parent, int numColumns)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        return composite;
    }
	
	private void createLabels(Composite parent)
    {
        Composite label = createSubComposite(parent, 2);
        DamsonLineBreakpoint breakpoint = (DamsonLineBreakpoint) getElement();

        String lineNumber = null;
        try
        {
            lineNumber = Integer.toString(breakpoint.getLineNumber());

            createLabel(label, "Line: ");
            createLabel(label, lineNumber);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return;
        }
    }
	
	
	private void createEnabledButton(Composite parent) throws CoreException
    {
		DamsonLineBreakpoint breakpoint = (DamsonLineBreakpoint) getElement();
		enabledButton = new Button(parent, SWT.CHECK | SWT.LEFT);
		enabledButton.setFont(parent.getFont());
		enabledButton.setText("Breakpoint Enabled: ");
		enabledButton.setLayoutData(new GridData());
        enabledButton.setSelection(breakpoint.isEnabled());
    }
	
	private void createConditionEditor(Composite parent) throws CoreException
    {
		DamsonLineBreakpoint breakpoint = (DamsonLineBreakpoint) getElement();
		
		Composite groupComposite = new Group(parent, SWT.NONE);
        groupComposite.setFont(parent.getFont());
        groupComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupComposite.setLayout(new GridLayout());
        
        
        String enable_cond_text = "Enter a DAMSON alias number";      
        enableConditionButton = new Button(groupComposite, SWT.CHECK | SWT.LEFT);
        enableConditionButton.setFont(groupComposite.getFont());
        enableConditionButton.setText(enable_cond_text);
        enableConditionButton.setLayoutData(new GridData());
        enableConditionButton.setSelection(breakpoint.isAliasConditionEnabaled());
        enableConditionButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                	conditionText.setEnabled(enableConditionButton.getSelection());
                    validateAliasCondition();
                }
            });

        groupComposite = new Group(parent, SWT.NONE);
        groupComposite.setFont(parent.getFont());
        groupComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        groupComposite.setLayout(new GridLayout());
        
        conditionText = new Text(groupComposite, SWT.LEFT);
        conditionText.setText(breakpoint.getAliasConditionText());
        conditionText.setEnabled(enableConditionButton.getSelection());
        conditionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateAliasCondition();
			}
		});
    }
	
	private void validateAliasCondition()
    {
        if (! enableConditionButton.getSelection())
        {
        	setErrorMessage(null);
        	setValid(true);
            return;
        }
        int alias;
        try{
        	alias = Integer.parseInt(conditionText.getText());
        }catch(NumberFormatException e){
        	alias = 0;
        }
        
        if (alias <= 0)
        {
        	setErrorMessage("Error: Condition must be a single DAMSON alias number (greater than 0)");
        	setValid(false);
        	
        }
        else
        {
        	setErrorMessage(null);
        	setValid(true);
        }
    }
	
	public boolean performOk()
    {
		DamsonLineBreakpoint breakpoint = (DamsonLineBreakpoint) getElement();
		
		try {
			int alias;
			breakpoint.setEnabled(enabledButton.getSelection());
			if (enableConditionButton.getSelection())
			{
				breakpoint.setAliasConditionEnabaled(true);
			}
			else
			{
				breakpoint.setAliasConditionEnabaled(false);
			}
			//set alias condition regardless
			try{
	        	alias = Integer.parseInt(conditionText.getText());
	        }catch(NumberFormatException e){
	        	alias = 0;
	        }
			breakpoint.setAliasCondition(alias);
			breakpoint.update();
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

        return super.performOk();
    }
	
	
	protected Label createLabel(Composite parent, String text)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        label.setFont(parent.getFont());
        label.setLayoutData(new GridData());

        return label;
    }

	
	

	

}