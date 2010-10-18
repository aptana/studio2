/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.semantic.ui.text.spelling;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.commons.spelling.engine.SpellingPreferenceInitializer;

public class Activator extends AbstractUIPlugin
{

	private static Activator plugin;

	public Activator()
	{
	}

	public void start(BundleContext context) throws Exception
	{
		plugin = this;
		super.start(context);
		new SpellingPreferenceInitializer().initializeDefaultPreferences();
	}

	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}

	public static IPreferenceStore getSpellingPreferenceStore()
	{
		return EditorsUI.getPreferenceStore();
	}

	public static void log(Exception ex)
	{
		plugin.getLog().log(
				new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(),
						IStatus.ERROR, ex.getMessage(), ex));
	}

	public static Activator getDefault()
	{
		return plugin;
	}

}
