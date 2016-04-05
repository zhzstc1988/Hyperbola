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
package org.eclipsercp.hyperbola;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class GeneralPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public static final String AUTO_LOGIN = "prefs_auto_login"; //$NON-NLS-1$

	private ScopedPreferenceStore preferences;

	@SuppressWarnings("deprecation")
	public GeneralPreferencePage() {
		super(GRID);
		this.preferences = new ScopedPreferenceStore(new ConfigurationScope(),
				Application.PLUGIN_ID);
		setPreferenceStore(preferences);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor boolEditor = new BooleanFieldEditor(AUTO_LOGIN,
				"Login automatically at startup", getFieldEditorParent()); //$NON-NLS-1$
		addField(boolEditor);
	}

	@Override
	public boolean performOk() {
		try {
			preferences.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}
}