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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import edu.shef.damson.core.DebugCorePlugin;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;


/**
 * Launches a DAMSON File
 */
public class DamsonLaunchShortcut implements ILaunchShortcut {

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
     */
    public void launch(ISelection selection, String mode) {
        // must be a structured selection with one file selected
        IFile file = (IFile) ((IStructuredSelection)selection).getFirstElement();

        // check for an existing launch configuration for the DAMSON file
        String path = file.getFullPath().toString(); 
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(DebugCorePlugin.ID_DAMSON_LAUNCH_CONFIGURATION_TYPE);
        try {
            ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations(type);
            for (int i = 0; i < configurations.length; i++) {
                ILaunchConfiguration configuration = configurations[i];
                String attribute = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, (String)null);
                if (path.equals(attribute)) {
                    DebugUITools.launch(configuration, mode);
                    return;
                }
            }
        } catch (CoreException e) {
            return;
        }
        
        try {
            // create a new configuration for the DAMSON file
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, file.getName());
            workingCopy.setAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, path);
            IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
            if (member != null)
            	workingCopy.setAttribute(DebugCorePlugin.ATTR_DAMSON_PROJECT, member.getProject().getName());
            workingCopy.setMappedResources(new IResource[]{file});
            ILaunchConfiguration configuration = workingCopy.doSave();
            DebugUITools.launch(configuration, mode);
        } catch (CoreException e1) {
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
     */
    public void launch(IEditorPart editor, String mode) {
    }

}