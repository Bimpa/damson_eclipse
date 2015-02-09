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
package edu.shef.damson.ui.launcher;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import edu.shef.damson.core.DebugCorePlugin;
import edu.shef.damson.ui.DebugUIPlugin;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;


/**
 * Tab to specify the DAMSON program to run/debug.
 */
public class DamsonMainTab extends AbstractLaunchConfigurationTab {
	
	private Text fProgramText;
	private Text fProjectText;
	private Button fProgramButton;
	
	private Button interpretButton;
	private Button disassembleButton;
	private Button rtButton;
	private Button codegenButton;
	private Button outputButton;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
	
		//row for program
		Label programLabel = new Label(comp, SWT.NONE);
		programLabel.setText("&Program:");
		GridData gd = new GridData(GridData.BEGINNING);
		programLabel.setLayoutData(gd);
		programLabel.setFont(font);
		fProgramText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProgramText.setLayoutData(gd);
		fProgramText.setFont(font);
		fProgramText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		fProgramButton = createPushButton(comp, "&Browse...", null); //$NON-NLS-1$
		fProgramButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseDAMSONFiles();
			}
		});
		
		//row for project
		Label projectLabel = new Label(comp, SWT.NONE);
		projectLabel.setText("Project:");
		gd = new GridData(GridData.BEGINNING);
		projectLabel.setLayoutData(gd);
		projectLabel.setFont(font);
		fProjectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectText.setLayoutData(gd);
		fProjectText.setFont(font);
		fProjectText.setEnabled(false);
		new Label(comp, SWT.NULL);	//Spacing
		
		//row for Interpreter
		Label optionsLabel = new Label(comp, SWT.NONE);
		optionsLabel.setText("&Options:");
		gd = new GridData(GridData.BEGINNING);
		optionsLabel.setLayoutData(gd);
		optionsLabel.setFont(font);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		interpretButton = new Button (comp, SWT.RADIO);
		interpretButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		interpretButton.setText ("Run in DAMSON emulator");
		interpretButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NULL);	//Spacing
		
		//row for Disassemble
		gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(comp, SWT.NULL);	//Spacing
		disassembleButton = new Button (comp, SWT.RADIO);
		disassembleButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		disassembleButton.setText ("Disassemble (-dis)");
		disassembleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NULL);	//Spacing
		
		//row for Routing Table
		gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(comp, SWT.NULL);	//Spacing
		rtButton = new Button (comp, SWT.RADIO);
		rtButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		rtButton.setText ("Generate Routing Table (-rt)");
		rtButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NULL);	//Spacing
		
		//row for compile
		gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(comp, SWT.NULL);	//Spacing
		codegenButton = new Button (comp, SWT.RADIO);
		codegenButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		codegenButton.setText ("Code Generate (-c)");
		codegenButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NULL);	//Spacing
		
		//row for compile
		gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(comp, SWT.NULL);	//Spacing
		outputButton = new Button (comp, SWT.RADIO);
		outputButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		outputButton.setText ("Compile to binary (-o)");
		outputButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NULL);	//Spacing

	}
	
	/**
	 * Open a resource chooser to select a DAMSON program 
	 */
	protected void browseDAMSONFiles() {
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		dialog.setTitle("DAMSON Program");
		dialog.setMessage("Select DAMSON Program (type *.d to list available programs)");
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			fProgramText.setText(file.getFullPath().toString());
		}
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String program = null;
			String project = null;
			program = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, (String)null);
			project = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_PROJECT, (String)null);
			if (program != null) {
				fProgramText.setText(program);
				if (project != null)
					fProjectText.setText(project);
			}
			//load the launch options
			int launch_option = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_INTERPRETER);
			switch(launch_option)
			{
				case(DebugCorePlugin.LAUNCH_OPTION_INTERPRETER):
				{
					interpretButton.setSelection(true);
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_DISASSEMBLE):
				{
					disassembleButton.setSelection(true);
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_ROUTINGTABLE):
				{
					rtButton.setSelection(true);
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_COMPILE):
				{
					outputButton.setSelection(true);
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_CODEGEN):
				{
					codegenButton.setSelection(true);
					break;
				}
			}
			
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		//set configuration for program name
		String program = fProgramText.getText().trim();
		if (program.length() == 0) {
			program = null;
		}
		String project = fProjectText.getText().trim();
		if (project.length() == 0) {
			project = null;
		}
		
		configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, program);
		configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_PROJECT, project);
		
		//set configuration for launch option
		if (disassembleButton.getSelection())
			configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_DISASSEMBLE);
		else if (rtButton.getSelection())
			configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_ROUTINGTABLE);
		else if (codegenButton.getSelection())
			configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_CODEGEN);
		else if (outputButton.getSelection())
			configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_COMPILE);
		else //interpretButton must be true (assume this anyway)
			configuration.setAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_INTERPRETER);
		 
		
		// perform resource mapping for contextual launch
		IResource[] resources = null;
		if (program!= null) {
			IPath path = new Path(program);
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (res != null) {
				resources = new IResource[]{res};
			}
		}
		configuration.setMappedResources(resources);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Main";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);
		String text = fProgramText.getText();
		if (text.length() > 0) {
			IPath path = new Path(text);
			IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(path); 
			if (member == null) {
				setErrorMessage("Specified program does not exist");
				fProjectText.setText("");
				return false;
			}else{
				fProjectText.setText(member.getProject().getName());
				try {
					if (!member.getProject().isNatureEnabled("edu.shef.damson.project.natures.DamsonNature")){
						setErrorMessage("Specified program does not belong to a DAMSON project");
						return false;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
		} else {
			setMessage("Select a DAMSON program");
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return DebugUIPlugin.getDefault().getImageRegistry().get(DebugUIPlugin.IMG_OBJ_DAMSON);
	}
}