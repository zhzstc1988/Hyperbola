package org.eclipsercp.hyperbola;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class HyperbolaContentProvider implements ITreeContentProvider {
	
	protected IWorkbenchAdapter getAdapter(Object element) {
		IWorkbenchAdapter adapter = null;
		if (element instanceof IAdaptable)
			adapter = (IWorkbenchAdapter) ((IAdaptable) element).getAdapter(
					IWorkbenchAdapter.class);
		if (element != null && adapter == null)
			adapter = (IWorkbenchAdapter) Platform.getAdapterManager().loadAdapter(
					element, IWorkbenchAdapter.class.getName());
		
		return adapter;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		IWorkbenchAdapter adapter = getAdapter(parentElement);
		if (adapter != null) 
			return adapter.getChildren(parentElement);
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null) 
			return adapter.getParent(element);
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
