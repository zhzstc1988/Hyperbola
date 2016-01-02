package org.eclipsercp.hyperbola;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipsercp.hyperbola.model.ConnectionDetails;

public class LoginDialog extends Dialog {

	private Combo userIdText;

	private Text serverText;

	private Text passwordText;

	private ConnectionDetails connectionDetails;

	private HashMap<String, ConnectionDetails> savedDetails = new HashMap<>();

	private Image[] images;

	public LoginDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label accountLabel = new Label(composite, SWT.NONE);
		accountLabel.setText("Account details:");
		accountLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false, 2, 1));

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText("&User ID");
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false, 1, 1));

		userIdText = new Combo(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL,
				true, false);
		gridData.widthHint = convertHeightInCharsToPixels(20);
		userIdText.setLayoutData(gridData);
		userIdText.addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ConnectionDetails d = savedDetails.get(userIdText.getText());

				if (d != null) {
					serverText.setText(d.getServer());
					passwordText.setText(d.getPassword());
				}
			}
		});

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("&Server:");
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		serverText = new Text(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText("&Password");
		passwordLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		passwordText = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "&Login", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

	}

	@Override
	protected void okPressed() {
		if (connectionDetails.getUserId().isEmpty()) {
			MessageDialog.openError(getShell(), "Invalid User ID", "User ID field must not be emply.");
			return;
		}
		if (connectionDetails.getServer().isEmpty()) {
			MessageDialog.openError(getShell(), "Invalid Server", "Server field must not be emply.");
			return;
		}
		super.okPressed();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		connectionDetails = new ConnectionDetails(userIdText.getText(),
				serverText.getText(), passwordText.getText());
		savedDetails.put(userIdText.getText(), connectionDetails);

		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Hyperbola Login");
		IProduct product = Platform.getProduct();
		if (product != null) {
			String bundleId = product.getDefiningBundle().getSymbolicName();
			String[] imageUrls = parseCSL(
					product.getProperty(IProductConstants.WINDOW_IMAGES));
			if (imageUrls.length > 0) {
				images = new Image[imageUrls.length];
				for (int i = 0; i < imageUrls.length; i++) {
					ImageDescriptor descriptor =
							AbstractUIPlugin.imageDescriptorFromPlugin(bundleId, imageUrls[i]);
					images[i] = descriptor.createImage();
				}
				newShell.setImages(images);
			}
		}
	}

	protected void initializeUsers(String defaultUser) {

	}

	/**
	 * @param csl The icon's path, e.g. "icons/alt16.gif,icons/alt32.gif"
	 * @return Array of icons' paths, e.g. [icons/alt16.gif, icons/alt32.gif]
	 */
	private String[] parseCSL(String csl) {
		if (csl == null)
			return null;

		StringTokenizer tokens = new StringTokenizer(csl, ","); //$NON-NLS-1$
		ArrayList<String> array = new ArrayList<String>(10);
		while (tokens.hasMoreTokens())
			array.add(tokens.nextToken().trim());

		return array.toArray(new String[array.size()]);
	}

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	@Override
	public boolean close() {
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				images[i].dispose();
			}
		}
		return super.close();
	}
}
