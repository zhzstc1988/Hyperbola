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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 *
 */
public class ChatEditorInputFactory implements IElementFactory {

	public static final String ID = "org.eclipsercp.hyperbola.chatinput";

	@Override
	public IAdaptable createElement(IMemento memento) {
		String name = memento.getString(ChatEditorInput.KEY_NAME);
		if (name != null) {
			return new ChatEditorInput(name);
		}
		return null;
	}

}
