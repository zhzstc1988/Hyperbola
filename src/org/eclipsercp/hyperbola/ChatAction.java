package org.eclipsercp.hyperbola;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jivesoftware.smack.RosterEntry;

public class ChatAction extends Action implements ISelectionListener, IWorkbenchAction {

	public final static String ID = "org.eclipsrcp.hyperbola.chat"; //$NON-NLS-1$

	private final IWorkbenchWindow window;
	private IStructuredSelection selection;

	public ChatAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(ID);
		setText("&Chat"); //$NON-NLS-1$
		setToolTipText("Chat with the selected Contact."); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.CHAT));
		this.window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection iselection) {
		boolean enable = false;
		if (part instanceof ContactsView) {
			if (iselection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) iselection;
				enable = selection.size() == 1 &&
						selection.getFirstElement() instanceof RosterEntry;
			}
		}
		setEnabled(enable);
	}
	
	@Override
	public void run() {
		Object item = selection.getFirstElement();
		RosterEntry entry = (RosterEntry) item;
		ChatEditorInput input = new ChatEditorInput(entry.getUser());
		try {
			window.getActivePage().openEditor(input, ChatEditor.ID);
		} catch (PartInitException e) {
			// Handle error.
		}
	}

}
