package edu.shef.damson.project.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class DamsonProjectPropertyPage extends PropertyPage {

	private static final String DAMSON_BINARY_PROPERTY = "DAMSON_BINARY_PROPERTY";
	
	private static final String DEFAULT_DAMSON_BINARY_PATH = "/bin/damson";

	private static final int TEXT_FIELD_WIDTH = 50;

	private Text damsonPathText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public DamsonProjectPropertyPage() {
		super();
	}


	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("DAMSON Binary Location: ");

		// Owner text field
		damsonPathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		damsonPathText.setLayoutData(gd);

		// Populate path text field
		try {
			String owner =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("damson", DAMSON_BINARY_PROPERTY));
			damsonPathText.setText((owner != null) ? owner : DEFAULT_DAMSON_BINARY_PATH);
		} catch (CoreException e) {
			damsonPathText.setText(DEFAULT_DAMSON_BINARY_PATH);
		}
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		// Populate the path text field with the default value
		damsonPathText.setText(DEFAULT_DAMSON_BINARY_PATH);
	}
	
	public boolean performOk() {
		// store the value in the path text field
		try {
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName("damson", DAMSON_BINARY_PROPERTY),
				damsonPathText.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}