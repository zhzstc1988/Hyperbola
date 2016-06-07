/*******************************************************************************
* Copyright (c) 2016 ARM Ltd. and others
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* ARM Ltd and ARM Germany GmbH - Initial API and implementation
*******************************************************************************/

package org.eclipsercp.hyperbola;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 *
 */
public class DebugConsole extends MessageConsole {

	MessageConsoleStream outMessageStream;
	MessageConsoleStream inMessageStream;

	private PacketListener outListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
			outMessageStream.println(packet.toXML());
		}
	};

	private PacketListener inListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
			inMessageStream.println(packet.toXML());
		}
	};

	public DebugConsole() {
		super("XMPP Debug", null);
		outMessageStream = newMessageStream();
		outMessageStream.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		inMessageStream = newMessageStream();
		inMessageStream.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

		Session.getInstance().getConnection().addPacketListener(outListener, null);
		Session.getInstance().getConnection().addPacketListener(inListener, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IOConsole#dispose()
	 */
	@Override
	protected void dispose() {
		Session.getInstance().getConnection().removePacketListener(outListener);
		Session.getInstance().getConnection().removePacketListener(inListener);
	}
}
