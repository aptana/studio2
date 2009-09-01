/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ScriptsLabelProvider extends LabelProvider
{

	private static Image fProfileIcon = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.eclipsemonkey.ui", //$NON-NLS-1$
			"icons/profile.gif").createImage(); //$NON-NLS-1$

	private static Image fProfileFileIcon = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.eclipsemonkey.ui", //$NON-NLS-1$
			"icons/js_file.gif").createImage(); //$NON-NLS-1$

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		if (element instanceof IScriptAction)
		{
			IScriptAction profile = (IScriptAction) element;
			return profile.getName();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object obj)
	{
		if (obj instanceof ScriptActionSet)
		{
			return fProfileIcon;
		}
		else if (obj instanceof ScriptAction)
		{
			return fProfileFileIcon;
		}
		else
		{
			return null;
		}
	}
}