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
package edu.shef.damson.core.launcher;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import edu.shef.damson.core.DebugCorePlugin;
import edu.shef.damson.core.model.DamsonDebugTarget;


/**
 * Launches DAMSON Program 
 */
public class DamsonLaunchDelegate extends LaunchConfigurationDelegate {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		List<String> commandList = new ArrayList<String>();
		
		// DAMSON executable
		String projectName = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_PROJECT, (String)null);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null){
			abort("DAMSON project not found.", null);
		}

		String damson_path;
		try{
			damson_path = project.getPersistentProperty(new QualifiedName("damson", DebugCorePlugin.DAMSON_BINARY_PROPERTY));
		}catch (CoreException e){
			damson_path = null;
		}
		if (damson_path == null) {
			abort("The DAMSON executable location is unspecified. Check the DAMSON section of the project properties.", null);
		}
		File exe = new File(damson_path);
		if (!exe.exists()) {
			abort(MessageFormat.format("Specified DAMSON executable {0} does not exist. Check the DAMSON section of the project properties.", new Object[]{damson_path}), null);
		}
		commandList.add(damson_path);
		
		// program name
		String program = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_PROGRAM, (String)null);
		if (program == null) {
			abort("The DAMSON program is unspecified.", null);
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(program));
		if (!file.exists()) {
			abort(MessageFormat.format("DAMSON program {0} does not exist.", new Object[] {file.getFullPath().toString()}), null);
		}
		commandList.add(file.getLocation().toOSString());

		
		// if in debug mode, add debug argument - i.e. '-debug'
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			commandList.add("-debug");
			//un-comment the following to allow debugging of the debugger
			//commandList.clear();
			//commandList.add("sleep");
			//commandList.add("1000");
			//try {
			//	Thread.sleep(10000);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//}
		}
		//if not debug mode then add launch options argument
		else
		{
			//add command options
			int launch_option = configuration.getAttribute(DebugCorePlugin.ATTR_DAMSON_LAUNCH_OPTIONS, DebugCorePlugin.LAUNCH_OPTION_INTERPRETER);
			switch(launch_option)
			{
				case(DebugCorePlugin.LAUNCH_OPTION_INTERPRETER):
				{
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_DISASSEMBLE):
				{
					commandList.add("-dis");
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_ROUTINGTABLE):
				{
					commandList.add("-rt");
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_COMPILE):
				{
					commandList.add("-o");
					break;
				}
				case(DebugCorePlugin.LAUNCH_OPTION_CODEGEN):
				{
					commandList.add("-c");
					break;
				}
				default:
				{
					abort("DAMSON program launch option unrecognised.", null);
				}
			}
		}
		
		//run DAMSON
		
		String[] commandLine = commandList.toArray(new String[commandList.size()]);
		System.out.println(commandList.toString());
		Process process = DebugPlugin.exec(commandLine, null);
		IProcess p = DebugPlugin.newProcess(launch, process, damson_path);
		
		// if in debug mode, create a debug target 
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			IDebugTarget target = new DamsonDebugTarget(launch, p);
			launch.addDebugTarget(target);
		}
		
	}
	
	/**
	 * Throws an exception with a new status containing the given
	 * message and optional exception.
	 * 
	 * @param message error message
	 * @param e underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, DebugCorePlugin.getDefault().getBundle().getSymbolicName(), 0, message, e));
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.LaunchConfigurationDelegate#buildForLaunch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		return false;
	}	
}