package org.eclipsercp.hyperbola;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class PerspectiveDebug implements IPerspectiveFactory {

	public static final String ID = "org.eclipsercp.hyperbola.debugperspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.addStandaloneView(ContactsView.ID, false,
				IPageLayout.LEFT, 0.33f, layout.getEditorArea());
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW,
				IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
	}

}
