package edu.shef.damson.project;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class DamsonNewProjectWizard extends Wizard implements INewWizard, IExecutableExtension{

	private static final String WIZARD_TITLE = "Damson Project Wizard";
	private static final String WIZARD_DESCRIPTION = "Create a new Damson project.";
	
	private WizardNewProjectCreationPage projectPage;
	private IConfigurationElement configurationElement;
	
	public DamsonNewProjectWizard() {
		setWindowTitle(WIZARD_TITLE);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	@Override
	public boolean performFinish() {
	    String name = projectPage.getProjectName();
	    URI location = null;
	    if (!projectPage.useDefaults()) {
	        location = projectPage.getLocationURI();
	    }

	    createProject(name, location);
	    
	    BasicNewProjectResourceWizard.updatePerspective(configurationElement);

	    return true;
	}
	
	@Override
	public void addPages() {
	    super.addPages();
	    projectPage = new WizardNewProjectCreationPage(WIZARD_TITLE);
	    projectPage.setTitle(WIZARD_TITLE);
	    projectPage.setDescription(WIZARD_DESCRIPTION);
	    addPage(projectPage);
	}
	
	/**
	 * Create a empty damson project by creating a minimal project with no contents
	 * 
	 * @param projectName
	 * @param location
	 * @return
	 */
	public static IProject createProject(String projectName, URI location) {
        Assert.isNotNull(projectName);
        Assert.isTrue(projectName.trim().length() > 0);

        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        //add custom damson nature


        if (!newProject.exists()) {
            URI projectLocation = location;
            IProjectDescription description = newProject.getWorkspace().newProjectDescription(newProject.getName());
            if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }
	  	    description.setLocationURI(projectLocation);

	  	    //set cusom nature
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = "edu.shef.damson.project.natures.DamsonNature";
			description.setNatureIds(newNatures);

            try {
                newProject.create(description, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return newProject;
    }

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}


}
