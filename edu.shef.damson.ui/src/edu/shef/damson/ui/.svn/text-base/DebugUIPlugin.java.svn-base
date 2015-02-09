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
package edu.shef.damson.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugUIPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static DebugUIPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	private final static String ICONS_PATH = "icons/full/";
	private final static String PATH_OBJECT = ICONS_PATH + "obj16/"; 
    
   
    /**
     * PDA program image
     */
    public final static String IMG_OBJ_DAMSON = "IMB_OBJ_DAMSON";
    public final static String IMG_OBJ_VARIABLES_LOCAL = "IMG_OBJ_VARIBALES_LOCAL";
    public final static String IMG_OBJ_VARIABLES_GLOBAL = "IMG_OBJ_VARIBALES_GLOBAL";
    
 
    
    /**
     * Keyword color
     */
    public final static RGB OP_KEYWORD = new RGB(127,0,85);
    public final static RGB OP_BACKGROUND = new RGB(255,255,255);
    public final static RGB OP_STRINGS = new RGB(0,0,255);
    public final static RGB OP_COMMENTS = new RGB(0,125,0);

    
    /**
     * Managed colours
     */
    private Map<RGB, Color> fColors = new HashMap<RGB, Color>();
    	
	/**
	 * The constructor.
	 */
	public DebugUIPlugin() {
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
        Iterator<Map.Entry<RGB, Color>> colors = fColors.entrySet().iterator();
        while (colors.hasNext()) {
            Map.Entry<RGB, Color> entry = colors.next();
            entry.getValue().dispose();
        }
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebugUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebugUIPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("edu.shef.damson.ui.DebugUIPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	protected void initializeImageRegistry(ImageRegistry reg) {
		declareImage(IMG_OBJ_DAMSON, PATH_OBJECT + "damson.gif");
		declareImage(IMG_OBJ_VARIABLES_LOCAL, PATH_OBJECT + "variable_local.gif");
		declareImage(IMG_OBJ_VARIABLES_GLOBAL, PATH_OBJECT + "variable_global.gif");
	}
	
    /**
     * Declares a workbench image given the path of the image file (relative to
     * the workbench plug-in). This is a helper method that creates the image
     * descriptor and passes it to the main <code>declareImage</code> method.
     * 
     * @param symbolicName the symbolic name of the image
     * @param path the path of the image file relative to the base of the workbench
     * plug-ins install directory
     * <code>false</code> if this is not a shared image
     */
    private void declareImage(String key, String path) {
    	Bundle bundle = Platform.getBundle("edu.shef.damson.ui");
    	URL url = FileLocator.find(bundle, new Path(path), null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        getImageRegistry().put(key, desc);
    }
    
    /**
     * Returns the colour described by the given RGB.
     * 
     * @param rgb
     * @return colour
     */
    public Color getColor(RGB rgb) {
        Color color = fColors.get(rgb);
        if (color == null) {
            color= new Color(Display.getCurrent(), rgb);
            fColors.put(rgb, color);
        }
        return color;
    }
    
	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}   
	
	
    
 }
