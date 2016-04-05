package org.eclipsercp.hyperbola;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class ChatEditor extends EditorPart {

	public static String ID = "org.eclipsercp.hyperbola.editors.chat"; //$NON-NLS-1$
	Text transcript;
	private Text entry;
	private Chat chat;
	private MessageListener messageListener;

	public ChatEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(getParticipant());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		top.setLayout(layout);

		transcript = new Text(top, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		transcript.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		transcript.setEditable(false);
		transcript.setBackground(transcript.getDisplay().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		transcript.setForeground(transcript.getDisplay().getSystemColor(
				SWT.COLOR_INFO_FOREGROUND));

		entry = new Text(top, SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.heightHint = entry.getLineHeight() * 2;
		entry.setLayoutData(gridData);
		entry.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					sendMessage();
					e.doit = false;
				}
			}
		});

		messageListener = new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {
				process(message);
			}
		};
		
		getChat().addMessageListener(messageListener);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		if (chat != null) {
			if (messageListener != null) {
				getChat().removeMessageListener(messageListener);
				messageListener = null;
			}
			getSession().terminateChat(chat);
			chat = null;
		}
	}

	private Session getSession() {
		return Session.getInstance();
	}

	void sendMessage() {
		String body = entry.getText();
		if (body.length() == 0) {
			return;
		}
		try {
			chat.sendMessage(body);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		transcript.append(renderMessage(getUser(), body));
		transcript.append("\n"); //$NON-NLS-1$
		scrollToEnd();
		entry.setText("");		 //$NON-NLS-1$
	}

	void scrollToEnd() {
		int n = transcript.getCharCount();
		transcript.setSelection(n, n);
		transcript.showSelection();
	}

	String renderMessage(String from, String body) {
		if (from == null) {
			return body;
		}
		int j = from.indexOf('@');
		if (j > 0) {
			from = from.substring(0, j);
		}
		return "<" + from + ">  " + body; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getUser() {
		return getSession().getConnection().getUser();
	}

	private String getParticipant() {
		return ((ChatEditorInput) getEditorInput()).getName();
	}

	private Chat getChat() {
		if (chat == null) {
			chat = getSession().getChat(getParticipant(), true);
		}
		return chat;
	}

	public void processFirstMessage(Message message) {
		process(message);
	}

	void process(Message message) {
		if (transcript.isDisposed()) {
			return;
		}
		transcript.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (transcript.isDisposed()) {
					return;
				}
				transcript.append(renderMessage(message.getFrom(),
						message.getBody()));
				transcript.append("\n"); //$NON-NLS-1$
				scrollToEnd();
			}
		});
	}

}
