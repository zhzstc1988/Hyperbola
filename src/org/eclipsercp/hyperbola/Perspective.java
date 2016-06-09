package org.eclipsercp.hyperbola;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout folder = layout.createFolder("contacts",
				IPageLayout.LEFT, 0.33f, layout.getEditorArea());
		folder.addPlaceholder(ContactsView.ID + ":*");
		folder.addView(ContactsView.ID);

		IViewLayout viewLayout = layout.getViewLayout(ContactsView.ID);
		viewLayout.setCloseable(false);
		//layout.setEditorAreaVisible(true);
		//layout.addStandaloneView(ContactsView.ID + ":1", false, IPageLayout.LEFT, 0.5f, layout.getEditorArea());
		//layout.addView(ContactsView.ID + ":2", IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		//layout.addView(ContactsView.ID + ":3", IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		layout.addPerspectiveShortcut(PerspectiveDebug.ID);
	}
}
