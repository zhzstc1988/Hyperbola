package org.eclipsercp.hyperbola;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jivesoftware.smack.util.StringUtils;

public class ChatEditorInput implements IEditorInput, IPersistableElement {

	public final static String KEY_NAME = "user"; //$NON-NLS-1$

	private String participant;

	public ChatEditorInput(String participant) {
		super();
		this.participant = StringUtils.parseBareAddress(participant);
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return participant;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return participant;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return participant.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChatEditorInput)) {
			return false;
		}
		ChatEditorInput other = (ChatEditorInput) obj;
		if (participant == null) {
			if (other.participant != null) {
				return false;
			}
		} else if (!participant.equals(other.participant)) {
			return false;
		}
		return true;
	}

	@Override
	public String getFactoryId() {
		return ChatEditorInputFactory.ID;
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(KEY_NAME, getName());
	}

}
