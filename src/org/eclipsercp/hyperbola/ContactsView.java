package org.eclipsercp.hyperbola;

import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;

public class ContactsView extends ViewPart {

	public static final String ID =
			"org.eclipsercp.hyperbola.views.contacts";

	private TreeViewer treeViewer;

	private AdapterFactory adapterFactory = new AdapterFactory();

	public ContactsView() {
		// TODO Auto-generated constructor stub
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
	}

	private void refresh() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh();
			}
		});
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		super.dispose();
	}

}
