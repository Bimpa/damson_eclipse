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
package edu.shef.damson.core;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugCorePlugin extends Plugin {
	//The shared instance.
	private static DebugCorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * Unique identifier for the DAMSON debug model (value 
	 * <code>debugModel</code>).
	 */
	public static final String ID_DAMSON_DEBUG_MODEL = "debugModel";
	
	/**
	 * Name of the string substitution variable that resolves to the
	 * location of the DAMSON executable (value <code>damsonExecutable</code>).
	 */
	public static final String VARIABLE_DAMSON_EXECUTABLE = "damsonExecutable";
	
	/**
	 * Name of DAMSON binary property holding the damson executable path 
	 * within the project properties. Must match DamsonProjectPropertyPage DAMSON_BINARY_PROPERTY
	 */
	public static final String DAMSON_BINARY_PROPERTY = "DAMSON_BINARY_PROPERTY";
	
	/**
	 * Launch configuration attribute key. Value is a path to a DAMSON
	 * program. The path is a string representing a full path
	 * to a DAMSON program in the workspace. 
	 */
	public static final String ATTR_DAMSON_PROGRAM = ID_DAMSON_DEBUG_MODEL + ".ATTR_DAMSON_PROGRAM";
	
	/**
	 * Launch configuration attribute key. Value is a path to a DAMSON
	 * project. The value is a string representing the name of the DAMSON 
	 * project associated with the DAMSON program
	 */
	public static final String ATTR_DAMSON_PROJECT = ID_DAMSON_DEBUG_MODEL + ".ATTR_DAMSON_PROJECT";
	
	/**
	 * Launch configuration options attribute key. Value is a the launch option 
	 * specified by one of the LAUNCH option values.
	 */
	public static final String ATTR_DAMSON_LAUNCH_OPTIONS =  ID_DAMSON_DEBUG_MODEL + ".ATTR_DAMSON_LAUNCH_OPTIONS";
	
	/**
	 * Identifier for the DAMSON launch configuration type
	 * (value <code>launchType</code>)
	 */
	public static final String ID_DAMSON_LAUNCH_CONFIGURATION_TYPE = "launchType";	
	
	/**
	 * Plug-in identifier.
	 */
	public static final String PLUGIN_ID = "edu.shef.damson.core";
	
	/**
	 * REQUEST PORT (Must be the same as in DAMSON source - damson_debugger.h)
	 */
	public static final int REQUEST_PORT = 48174;
	
	/**
	 * EVENT PORT (Must be the same as in DAMSON source - damson_debugger.h)
	 */
	public static final int EVENT_PORT = 48474;
	
	/**
	 * LAUNCH_OPTION_INTERPRETER Launch option for interpreter (no DAMSON arguments)
	 */
	public static final int LAUNCH_OPTION_INTERPRETER = 0;
	/**
	 * LAUNCH_OPTION_DISASSEMBLE Launch option for interpreter (-dis DAMSON argument)
	 */
	public static final int LAUNCH_OPTION_DISASSEMBLE = 1;
	/**
	 * LAUNCH_OPTION_ROUTINGTABLE Launch option for interpreter (-rt DAMSON arguments)
	 */
	public static final int LAUNCH_OPTION_ROUTINGTABLE = 2;
	/**
	 * LAUNCH_OPTION_COMPILE Launch option for interpreter (-o DAMSON arguments)
	 */
	public static final int LAUNCH_OPTION_COMPILE = 3;
	/**
	 * LAUNCH_OPTION_CODEGEN Launch option for interpreter (-c DAMSON arguments)
	 */
	public static final int LAUNCH_OPTION_CODEGEN = 4;
	
	
	public static final int THREAD_STATE_RUNNING	= 0;
	public static final int THREAD_STATE_WAITING 	= 1;
	public static final int THREAD_STATE_DELAYING 	= 2;

	
	/**
	 * The constructor.
	 */
	public DebugCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebugCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebugCorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("edu.shef.damson.core.DebugCorePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	/**
	 * Return a <code>java.io.File</code> object that corresponds to the specified
	 * <code>IPath</code> in the plugin directory, or <code>null</code> if none.
	 */
	public static File getFileInPlugin(IPath path) {
		try {
			
			URL installURL = Platform.getBundle(PLUGIN_ID).getEntry("/");
				//new URL(getDefault().getDescriptor().getInstallURL(), path.toString());
			URL localURL = FileLocator.toFileURL(installURL);
				//Platform.asLocalURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}	
}