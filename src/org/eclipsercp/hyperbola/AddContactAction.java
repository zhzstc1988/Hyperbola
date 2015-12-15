package org.eclipsercp.hyperbola;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipsercp.hyperbola.model.ContactsEntry;
import org.eclipsercp.hyperbola.model.ContactsGroup;

public class AddContactAction extends Action implements ISelectionListener, IWorkbenchAction {

	private final IWorkbenchWindow window;
	public final static String ID =
			"org.eclipsercp.hyperbola.addContact";
	private IStructuredSelection selection;

	public AddContactAction(IWorkbenchWindow iwindow) {
		this.window = iwindow;
		setId(ID);
		setText("&Add Contact...");
		setToolTipText("Add a contact to your contacts list.");
		setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.ADD_CONTACT));
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection iselection) {
		if (part instanceof ContactsView)
			if (iselection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) iselection;
				setEnabled(selection.size() == 1 &&
						selection.getFirstElement() instanceof ContactsGroup);
			} else {
				setEnabled(false);
			}
	}

	@Override
	public void run() {
		AddContactDialog d = new AddContactDialog(window.getShell());
		int code = d.open();
		if (code == Window.OK) {
			Object item = selection.getFirstElement();
			ContactsGroup group = (ContactsGroup) item;
			ContactsEntry entry =
					new ContactsEntry(group, d.getUserId(), d.getNickname(),
							d.getServer());
			group.addEntry(entry);
		}
	}

}
