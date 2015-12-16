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
import org.eclipsercp.hyperbola.model.ContactsEntry;

public class ChatAction extends Action implements ISelectionListener, IWorkbenchAction {

	public final static String ID = "org.eclipsrcp.hyperbola.chat";

	private final IWorkbenchWindow window;
	private IStructuredSelection selection;

	public ChatAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Chat");
		setToolTipText("Chat with the selected Contact.");
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
						selection.getFirstElement() instanceof ContactsEntry;
			}
		}
		setEnabled(enable);
	}
	
	@Override
	public void run() {
		Object item = selection.getFirstElement();
		ContactsEntry entry = (ContactsEntry) item;
		ChatEditorInput input = new ChatEditorInput(entry.getName());
		try {
			window.getActivePage().openEditor(input, ChatEditor.ID);
		} catch (PartInitException e) {
			// Handle error.
		}
	}

}
