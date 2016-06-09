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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 *
 */
public class OpenViewAction extends Action {

	private final IWorkbenchWindow window;
	private int instanceNum;
	private final String viewId;
	public final static String ID =
			"org.eclipsercp.hyperbola.openView"; //$NON-NLS-1$

	public OpenViewAction(IWorkbenchWindow window, String viewId) {
		this.window = window;
		this.viewId = viewId;
		setId(ID);
		//setActionDefinitionId(ID);
		setText("&New Contacts View"); //$NON-NLS-1$
	}

	@Override
	public void run() {
		try {
			window.getActivePage().showView(viewId,
					Integer.toString(instanceNum),
					IWorkbenchPage.VIEW_ACTIVATE);
			instanceNum++;
		} catch (PartInitException e) {}
	}

}
