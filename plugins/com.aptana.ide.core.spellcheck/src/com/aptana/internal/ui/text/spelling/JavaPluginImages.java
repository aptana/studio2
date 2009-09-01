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
package com.aptana.internal.ui.text.spelling;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.semantic.ui.text.spelling.Activator;

public class JavaPluginImages {

	public static final String IMG_OBJS_NLS_NEVER_TRANSLATE = null;
	public static final String IMG_CORRECTION_RENAME = "rename"; //$NON-NLS-1$
	public static final String IMG_CORRECTION_ADD = "add"; //$NON-NLS-1$

	static Image change;
	static Image add;
	static Image ignore;

	static {
		change = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/correction_change.gif").createImage(); //$NON-NLS-1$
		add = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/add.gif").createImage(); //$NON-NLS-1$
		ignore = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/ignore.gif").createImage(); //$NON-NLS-1$
	}

	public static Image get(String imgObjsNlsNeverTranslate) {
		if (imgObjsNlsNeverTranslate != null) {
			if (imgObjsNlsNeverTranslate.equals(IMG_CORRECTION_RENAME)) {
				return change;
			}
			if (imgObjsNlsNeverTranslate.equals(IMG_CORRECTION_ADD)) {
				return add;
			}
		}
		return ignore;
	}

}
