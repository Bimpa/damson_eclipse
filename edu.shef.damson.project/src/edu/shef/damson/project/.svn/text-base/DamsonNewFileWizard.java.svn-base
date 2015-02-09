package edu.shef.damson.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

public class DamsonNewFileWizard extends Wizard implements INewWizard {

	
	private static final String WIZARD_TITLE = "New Damson File Wizard";
	private static final String WIZARD_DESCRIPTION = "Create a new Damson file.";
	
	private WizardNewFileCreationPage newfilePage;
	private IStructuredSelection selection;

	public DamsonNewFileWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(WIZARD_TITLE);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final IPath containerName = newfilePage.getContainerFullPath();
		final String fileName = newfilePage.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}

	private void doFinish(IPath containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(containerName);
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName.toString()
					+ "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * File is initialised with a template damson structure
	 */
	private InputStream openContentStream() {
		String contents =
			"#node test\n" +
			"#timestamp on\n\n" +
			"//ToDo: Global variable definitions\n\n" +
			"//ToDo: Function Prototypes\n\n" +
			"/**\n " +
			"* main function will initialise the node then force the system to wait for interupts\n" +
			" * i.e. main does not cause system exit instead use 'exit(int status)'	\n" +
			" */\n" +
			"int main()\n" +
			"{\n" +
			"    //ToDo: Node initialisation code\n\n" +
			"    exit(0);\n\n" +
			"    return 1; //exit before reached\n" +
			"}\n\n" +
			"//ToDo: Function and interupt definitions\n\n" +
			"//Alias definition" + 
			"#alias test 1\n";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "edu.shef.damson.project", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	@Override
	public void addPages() {
	    super.addPages();
	    newfilePage = new WizardNewFileCreationPage(WIZARD_TITLE, selection);
	    newfilePage.setTitle(WIZARD_TITLE);
	    newfilePage.setDescription(WIZARD_DESCRIPTION);
	    newfilePage.setFileExtension("d");
	    newfilePage.setFileName("newfile.d");
	    addPage(newfilePage);
	}
}
