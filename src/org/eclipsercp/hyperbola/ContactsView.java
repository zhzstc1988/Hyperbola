package org.eclipsercp.hyperbola;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipsercp.hyperbola.model.*;

public class ContactsView extends ViewPart {

	public static final String ID =
			"org.eclipsercp.hyperbola.views.contacts";
	
	private TreeViewer treeViewer;
	
	private Session session;
	
	private AdapterFactory adapterFactory = new AdapterFactory();

	public ContactsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		initializeSession();
		treeViewer = new TreeViewer(parent);//, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		Platform.getAdapterManager().registerAdapters(adapterFactory, Contact.class);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		treeViewer.setInput(session.getRoot());
		session.getRoot().addContactsListener(new IContactsListener() {
			public void contactsChanged(ContactsGroup contacts,
					ContactsEntry entry) {
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
	
	private void initializeSession() {
		session = new Session();
		ContactsGroup root = session.getRoot();
		ContactsGroup friendsGroup = new ContactsGroup(root, "Friends");
		root.addEntry(friendsGroup);
		friendsGroup.addEntry(new ContactsEntry(friendsGroup,
				"Alize", "aliz", "localhost"));
		friendsGroup.addEntry(new ContactsEntry(friendsGroup,
				"Sydney", "syd", "localhost"));
		ContactsGroup otherGroup = new ContactsGroup(root, "Other");
		root.addEntry(otherGroup);
		otherGroup.addEntry(new ContactsEntry(otherGroup, "Nadine", "nad", "localhost"));
	}

}
