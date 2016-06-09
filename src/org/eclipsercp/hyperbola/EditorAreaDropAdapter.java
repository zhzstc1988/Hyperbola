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

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorInputTransfer;

/**
 *
 */
public class EditorAreaDropAdapter extends DropTargetAdapter {

	private final IWorkbenchWindow window;

	/**
	 *
	 */
	public EditorAreaDropAdapter(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	@Override
	public void drop(DropTargetEvent event) {
		Display d = window.getShell().getDisplay();
		final IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			d.asyncExec(() -> asyncDrop(event, page));
		}
	}

	private void asyncDrop(DropTargetEvent event, IWorkbenchPage page) {

		if (EditorInputTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			EditorInputTransfer.EditorInputData[] editorInputs =
					(EditorInputTransfer.EditorInputData[]) event.data;
			if (editorInputs == null) {
				return;
			}
			for (int i = 0; i < editorInputs.length; i++) {
				IEditorInput editorInput = editorInputs[i].input;
				String editorId = editorInputs[i].editorId;
				openNonExternalEditor(page, editorInput, editorId);
			}
		}
	}

	private IEditorPart openNonExternalEditor(IWorkbenchPage page,
			IEditorInput editorInput, String editorId) {
		IEditorPart result;
		try {
			IEditorRegistry editorReg = PlatformUI.getWorkbench()
					.getEditorRegistry();
			IEditorDescriptor editorDesc = editorReg.findEditor(editorId);
			if (editorDesc != null && !editorDesc.isOpenExternal()) {
				result = page.openEditor(editorInput, editorId);
			} else {
				result = null;
			}
		} catch (PartInitException e) {
			result = null;
		}

		return result;
	}

}
