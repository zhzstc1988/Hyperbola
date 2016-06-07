package org.eclipsercp.hyperbola;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.eclipsercp.hyperbola.perspective"; //$NON-NLS-1$

	@Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { new DebugConsole() });
	}

	@Override
	public void preStartup() {
		hookIncomingChatListener();
	}

	private void hookIncomingChatListener() {
		XMPPConnection connection = Session.getInstance().getConnection();
		if (connection != null) {
			PacketListener listener = new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getType() == Message.Type.chat) {
						startChat(message);
					}
				}
			};
			PacketFilter filter = new PacketTypeFilter(Message.class);
			connection.addPacketListener(listener, filter);
		}
	}

	void startChat(Message message) {
		String user = StringUtils.parseBareAddress(message.getFrom());
		Chat chat = Session.getInstance().getChat(user, false);
		if (chat != null) {
			return;
		}

		IWorkbench workbench = getWorkbenchConfigurer().getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (PlatformUI.isWorkbenchRunning()) {
					openChatEditor(message);
				}
			}
		});
	}

	void openChatEditor(Message message) {
		IWorkbench workbench = getWorkbenchConfigurer().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			String user = message.getFrom();
			ChatEditorInput editorInput = new ChatEditorInput(user);
			try {
				IEditorPart editor = page.openEditor(editorInput, ChatEditor.ID);
				if (editor instanceof ChatEditor) {
					((ChatEditor)editor).processFirstMessage(message);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
