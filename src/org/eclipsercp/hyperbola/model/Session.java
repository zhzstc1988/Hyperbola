/*******************************************************************************
 * Copyright (c) 2004, 2005 Jean-Michel Lemieux, Jeff McAffer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Hyperbola is an RCP application developed for the book 
 *     Eclipse Rich Client Platform - 
 *         Designing, Coding, and Packaging Java Applications 
 *
 * Contributors:
 *     Jean-Michel Lemieux and Jeff McAffer - initial implementation
 *******************************************************************************/
package org.eclipsercp.hyperbola.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Authentication;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.RosterPacket;

/**
 * Encapsulates the state for a session, including the connection details (user
 * name, password, server) and the connection itself.
 */
public class Session {
	
	public static final String HOSTNAME = "zhz-pc";

	private ConnectionDetails connectionDetails;

	private XMPPConnection connection;

	private Map<String, Chat> chats = new HashMap<String, Chat>();

	private static Session INSTANCE;

	public static Session getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Session();
		return INSTANCE;
	}

	private Session() {
		// enforce the singleton patter
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}

	/**
	 * Establishes the connection to the server and logs in. The connection
	 * details must have already been set.
	 */
	public void connectAndLogin(final IProgressMonitor monitor)
			throws XMPPException {
		PacketListener progressPacketListener = new PacketListener() {
			public void processPacket(Packet packet) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();
				String message = null;
				if (packet instanceof Authentication)
					message = "Authenticating...";
				if (packet instanceof RosterPacket)
					message = "Receiving roster...";
				if (message != null)
					monitor.subTask(message);
			}
		};
		try {
			monitor.beginTask("Connecting...", IProgressMonitor.UNKNOWN);
			monitor.subTask("Contacting " + connectionDetails.getServer()
					+ "...");
			connection = new XMPPConnection(connectionDetails.getServer());
			connection.connect();
			connection.addPacketWriterListener(progressPacketListener,
					new OrFilter(new PacketTypeFilter(Authentication.class),
							new PacketTypeFilter(RosterPacket.class)));
			connection.login(connectionDetails.getUserId(), connectionDetails
					.getPassword(), connectionDetails.getResource());
		} finally {
			if (connection != null)
				connection.removePacketWriterListener(progressPacketListener);
			monitor.done();
		}
	}

	/**
	 * Returns the chat for the given participant, optionally creating it if it
	 * hasn't yet been created
	 * 
	 * @param participant
	 *            the user id of the chat participant
	 * @param create
	 *            whether to create the chat
	 * @return the chat or <code>null</code>
	 */
	public Chat getChat(String participant, boolean create) {
		synchronized (chats) {
			Chat chat = (Chat) chats.get(participant);
			if (chat == null && create && connection != null) {
				chat = connection.getChatManager().createChat(participant, null);
				chats.put(participant, chat);
			}
			return chat;
		}
	}

	/**
	 * Terminates the given chat.
	 * 
	 * @param chat
	 *            the chat to terminate
	 */
	public void terminateChat(Chat chat) {
		chats.remove(chat.getParticipant());
	}
}
