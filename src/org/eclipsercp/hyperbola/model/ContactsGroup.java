package org.eclipsercp.hyperbola.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;

public class ContactsGroup extends Contact {
	private List<Contact> entries;

	private ContactsGroup parent;

	private String name;

	private ListenerList listeners;

	public ContactsGroup(ContactsGroup parent, String name) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public ContactsGroup getParent() {
		return parent;
	}

	public void rename(String newName) {
		this.name = newName;
		fireContactsChanged(null);
	}

	public void addEntry(Contact entry) {
		if (entries == null)
			entries = new ArrayList<Contact>(5);
		entries.add(entry);
		fireContactsChanged(null);
	}

	public void removeEntry(Contact entry) {
		if (entries != null) {
			entries.remove(entry);
			if (entries.isEmpty())
				entries = null;
		}
		fireContactsChanged(null);
	}

	public Contact[] getEntries() {
		if (entries != null)
			return (Contact[]) entries.toArray(new Contact[entries.size()]);
		return new Contact[0];
	}

	public void addContactsListener(IContactsListener listener) {
		if (parent != null)
			parent.addContactsListener(listener);
		else {
			if (listeners == null)
				listeners = new ListenerList();
			listeners.add(listener);
		}
	}

	public void removeContactsListener(IContactsListener listener) {
		if (parent != null)
			parent.removeContactsListener(listener);
		else {
			if (listeners != null) {
				listeners.remove(listener);
				if (listeners.isEmpty())
					listeners = null;
			}
		}
	}

	protected void fireContactsChanged(ContactsEntry entry) {
		if (parent != null)
			parent.fireContactsChanged(entry);
		else {
			if (listeners == null)
				return;
			Object[] rls = listeners.getListeners();
			for (int i = 0; i < rls.length; i++) {
				IContactsListener listener = (IContactsListener) rls[i];
				listener.contactsChanged(this, entry);
			}
		}
	}
}