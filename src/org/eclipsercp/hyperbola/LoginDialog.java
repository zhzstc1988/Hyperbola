package org.eclipsercp.hyperbola;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class LoginDialog extends Dialog {

	Combo userIdText;

	Text serverText;

	Text passwordText;

	private ConnectionDetails connectionDetails;

	HashMap<String, ConnectionDetails> savedDetails = new HashMap<>();

	private Image[] images;

	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private static final String SERVER = "server"; //$NON-NLS-1$

	private static final String SAVED = "saved-connections"; //$NON-NLS-1$

	private static final String LAST_USER = "prefs_last_connection"; //$NON-NLS-1$

	public LoginDialog(Shell parent) {
		super(parent);
		loadDescriptors();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label accountLabel = new Label(composite, SWT.NONE);
		accountLabel.setText("Account details:"); //$NON-NLS-1$
		accountLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false, 2, 1));

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText("&User ID"); //$NON-NLS-1$
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
		serverLabel.setText("&Server:"); //$NON-NLS-1$
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		serverText = new Text(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText("&Password"); //$NON-NLS-1$
		passwordLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		passwordText = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Button autoLogin = new Button(composite, SWT.CHECK);
		autoLogin.setText("Login &automatically at start up"); //$NON-NLS-1$
		autoLogin.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, true, 2, 1));
		autoLogin.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IEclipsePreferences prefs = ConfigurationScope.INSTANCE
						.getNode(Application.PLUGIN_ID);
				prefs.putBoolean(GeneralPreferencePage.AUTO_LOGIN, autoLogin.getSelection());
			}
		});
		IPreferencesService service = Platform.getPreferencesService();
		boolean auto_login = service.getBoolean(Application.PLUGIN_ID, GeneralPreferencePage.AUTO_LOGIN, true, null);
		autoLogin.setSelection(auto_login);

		String lastUser = "none"; //$NON-NLS-1$
		if (connectionDetails != null) {
			lastUser = connectionDetails.getUserId();
		}
		initializeUsers(lastUser);

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button deleteUser = createButton(parent, IDialogConstants.CLIENT_ID,
				"&Delete User", false); //$NON-NLS-1$
		deleteUser.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				savedDetails.remove(userIdText.getText());
				initializeUsers(""); //$NON-NLS-1$
			}
		});

		createButton(parent, IDialogConstants.OK_ID, "&Login", true); //$NON-NLS-1$
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		if (connectionDetails.getUserId().isEmpty()) {
			MessageDialog.openError(getShell(), "Invalid User ID", "User ID field must not be emply."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if (connectionDetails.getServer().isEmpty()) {
			MessageDialog.openError(getShell(), "Invalid Server", "Server field must not be emply."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		super.okPressed();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		connectionDetails = new ConnectionDetails(userIdText.getText(),
				serverText.getText(), passwordText.getText());
		savedDetails.put(userIdText.getText(), connectionDetails);

		if (buttonId == IDialogConstants.OK_ID ||
				buttonId == IDialogConstants.CANCEL_ID) {
			saveDescriptors();
		}

		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Hyperbola Login"); //$NON-NLS-1$
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
		userIdText.removeAll();
		passwordText.setText(""); //$NON-NLS-1$
		serverText.setText(""); //$NON-NLS-1$
		for (String text : savedDetails.keySet()) {
			userIdText.add(text);
		}
		int index = Math.max(userIdText.indexOf(defaultUser), 0);
		userIdText.select(index);
	}

	/**
	 * @param csl The icon's path, e.g. "icons/alt16.gif,icons/alt32.gif"
	 * @return Array of icons' paths, e.g. [icons/alt16.gif, icons/alt32.gif]
	 */
	private String[] parseCSL(String csl) {
		if (csl == null) {
			return null;
		}

		StringTokenizer tokens = new StringTokenizer(csl, ","); //$NON-NLS-1$
		ArrayList<String> array = new ArrayList<String>(10);
		while (tokens.hasMoreTokens()) {
			array.add(tokens.nextToken().trim());
		}

		return array.toArray(new String[array.size()]);
	}

	private void saveDescriptors() {
		IEclipsePreferences preferences = ConfigurationScope.INSTANCE
				.getNode(Application.PLUGIN_ID);
		// Upper-most preference saving, using the Last logged in user
		preferences.put(LAST_USER, connectionDetails.getUserId());
		Preferences connections = preferences.node(SAVED);
		for (String name : savedDetails.keySet()) {
			ConnectionDetails d = savedDetails.get(name);
			Preferences connection = connections.node(name);
			connection.put(SERVER, d.getServer());
			connection.put(PASSWORD, d.getPassword());
		}
		try {
			connections.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void loadDescriptors() {
		IEclipsePreferences preferences = ConfigurationScope.INSTANCE
				.getNode(Application.PLUGIN_ID);
		Preferences connections = preferences.node(SAVED);
		try {
			String[] userNames = connections.childrenNames();
			for (String userName : userNames) {
				Preferences node = connections.node(userName);
				savedDetails.put(userName, new ConnectionDetails(
						userName,
						node.get(SERVER, ""), //$NON-NLS-1$
						node.get(PASSWORD, ""))); //$NON-NLS-1$
			}
			connectionDetails = savedDetails.get(preferences.get(LAST_USER, "")); //$NON-NLS-1$
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

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
