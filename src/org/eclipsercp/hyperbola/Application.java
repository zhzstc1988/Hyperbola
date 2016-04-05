package org.eclipsercp.hyperbola;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipsercp.hyperbola.model.ConnectionDetails;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.XMPPException;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	public static final String PLUGIN_ID = "org.eclipsercp.hyperbola"; //$NON-NLS-1$

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			Session session = Session.getInstance();
			if (!login(session)) {
				return EXIT_OK;
			}
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			} else {
				return IApplication.EXIT_OK;
			}
		} finally {
			display.dispose();
		}

	}

	private boolean login(final Session session) {
		boolean firstTry = true;
		LoginDialog loginDialog = new LoginDialog(null);
		while (session.getConnection() == null ||
				!session.getConnection().isAuthenticated()) {

			IPreferencesService service = Platform.getPreferencesService();
			boolean auto_login = service.getBoolean(Application.PLUGIN_ID,
					GeneralPreferencePage.AUTO_LOGIN, true, null);

			ConnectionDetails details = loginDialog.getConnectionDetails();

			if (!auto_login || details == null || !firstTry) {
				if (loginDialog.open() != Window.OK) {
					return false;
				}
				details = loginDialog.getConnectionDetails();
			}

			firstTry = false;
			session.setConnectionDetails(loginDialog.getConnectionDetails());
			connectWithProgress(session);
		}

		return true;
	}

	private void connectWithProgress(Session session) {
		ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
		progress.setCancelable(true);
		try {
			progress.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) {
					try {
						session.connectAndLogin(monitor);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning()) {
			return;
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed()) {
					workbench.close();
				}
			}
		});
	}
}
