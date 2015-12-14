package org.eclipsercp.hyperbola;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipsercp.hyperbola.model.*;

public class AdapterFactory implements IAdapterFactory {
	
	private IWorkbenchAdapter groupAdapter = new IWorkbenchAdapter() {

		@Override
		public Object[] getChildren(Object o) {
			return ((ContactsGroup)o).getEntries();
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					Application.PLUGIN_ID, IImageKeys.GROUP);
		}

		@Override
		public String getLabel(Object o) {
			ContactsGroup group = ((ContactsGroup)o);
			int available = 0;
			Contact[] entries = group.getEntries();
			for (int i = 0; i < entries.length; i++) {
				Contact contact = entries[i];
				if (contact instanceof ContactsEntry) {
					if (((ContactsEntry) contact).getPresence()
							!= Presence.INVISIBLE)
						available++;
				}
			}
			return ((ContactsGroup)o).getName() +
					" (" + available + "/" + entries.length + ")";
		}

		@Override
		public Object getParent(Object o) {
			return ((ContactsGroup)o).getParent();
		}
		
	};
	
	private IWorkbenchAdapter entryAdapter = new IWorkbenchAdapter() {

		@Override
		public Object[] getChildren(Object o) {
			return new Object[0];
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			ContactsEntry entry = (ContactsEntry)object;
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					Application.PLUGIN_ID, presenceToKey(entry.getPresence()));
		}

		@Override
		public String getLabel(Object o) {
			ContactsEntry entry = ((ContactsEntry)o);
			return entry.getName() + '-' + entry.getServer();
		}

		@Override
		public Object getParent(Object o) {
			return ((ContactsEntry)o).getParent();			
		}
		
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof ContactsGroup) {
			return (T) groupAdapter;
		}
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof ContactsEntry) {
			return (T) entryAdapter;
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] {IWorkbenchAdapter.class};
	}
	
	private String presenceToKey(Presence presence) {
		if (presence == Presence.ONLINE)
			return IImageKeys.ONLINE;
		if (presence == Presence.AWAY)
			return IImageKeys.AWAY;
		if (presence == Presence.DO_NOT_DISTURB)
			return IImageKeys.DO_NOT_DISTURB;
		if (presence == Presence.INVISIBLE)
			return IImageKeys.OFFLINE;
		return IImageKeys.OFFLINE;
	}

}
