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
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;

public class AddContactAction extends Action implements ISelectionListener, IWorkbenchAction {

	private final IWorkbenchWindow window;
	public final static String ID =
			"org.eclipsercp.hyperbola.addContact"; //$NON-NLS-1$
	private IStructuredSelection selection;

	public AddContactAction(IWorkbenchWindow iwindow) {
		this.window = iwindow;
		setId(ID);
		setText("&Add Contact..."); //$NON-NLS-1$
		setToolTipText("Add a contact to your contacts list."); //$NON-NLS-1$
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
		if (part instanceof ContactsView) {
			if (iselection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) iselection;
				setEnabled(selection.size() == 1 &&
						selection.getFirstElement() instanceof RosterGroup);
			} else {
				setEnabled(false);
			}
		}
	}

	@Override
	public void run() {
		AddContactDialog d = new AddContactDialog(window.getShell());
		int code = d.open();
		if (code == Window.OK) {
			Object item = selection.getFirstElement();
			RosterGroup group = (RosterGroup) item;
			Roster list = Session.getInstance().getConnection().getRoster();
			String user = d.getUserId() + "@" + d.getServer(); //$NON-NLS-1$
			String[] groups = new String[] { group.getName() };
			try {
				list.createEntry(user, d.getNickname(), groups);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

}
