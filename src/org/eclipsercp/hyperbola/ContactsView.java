package org.eclipsercp.hyperbola;

import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;

public class ContactsView extends ViewPart {

	public static final String ID =
			"org.eclipsercp.hyperbola.views.contacts"; //$NON-NLS-1$

	TreeViewer treeViewer;

	Action chatAction;

	private AdapterFactory adapterFactory = new AdapterFactory();

	public ContactsView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new HyperbolaLabelProvider());
		treeViewer.setContentProvider(new HyperbolaContentProvider());
		Roster roster = Session.getInstance().getConnection().getRoster();
		treeViewer.setInput(roster);
		if(roster != null) {
			roster.addRosterListener(new RosterListener() {
				@Override
				public void entriesAdded(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void entriesDeleted(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void entriesUpdated(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void presenceChanged(org.jivesoftware.smack.packet.Presence arg0) {
					refresh();
				};
			});
		}

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}

	private void makeActions() {

		chatAction = new Action() {
			@Override
			public void run() {
				ITreeSelection selection = treeViewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof RosterEntry) {
					ChatEditorInput input = new ChatEditorInput(((RosterEntry) obj).getUser());
					try {
						getSite().getWorkbenchWindow().getActivePage().openEditor(input, ChatEditor.ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				chatAction.run();
			}
		});
	}

	private void hookContextMenu() {
	}

	void refresh() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				treeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		super.dispose();
	}

}
