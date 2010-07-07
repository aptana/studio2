/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.installer.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.ui.update.PluginsImageRegistry;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.Plugin;

public class InstalledPluginsLabelProvider extends LabelProvider implements ITableLabelProvider
{

	private static final int COLUMN_IMAGE = 0;
	private static final int COLUMN_NAME = 1;
	private static final int COLUMN_VERSION = 2;
	private static final int COLUMN_RELEASE_DATE = 3;
	private static final int COLUMN_DESCRIPTION = 4;

	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String RELEASE_DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$

	private PluginsImageRegistry fImages;

	public InstalledPluginsLabelProvider(Display display)
	{
		fImages = new PluginsImageRegistry(display);
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex != COLUMN_IMAGE)
			return null;
		if (element instanceof Plugin)
			return fImages.getImage((Plugin) element);
		return PluginsImageRegistry.getDefaultImage();
	}

	public String getColumnText(Object element, int columnIndex)
	{
		if (!(element instanceof IPlugin))
		{
			return EMPTY;
		}
		IPlugin plugin = (IPlugin) element;

		switch (columnIndex)
		{
			case COLUMN_NAME:
				return plugin.getName();
			case COLUMN_VERSION:
				return plugin.getVersion();
			case COLUMN_RELEASE_DATE:
				if (plugin instanceof Plugin)
				{
					Calendar release = ((Plugin) plugin).getReleaseDate();
					if (release == null)
						return EMPTY;
					DateFormat format = new SimpleDateFormat(RELEASE_DATE_FORMAT);
					return format.format(release.getTime());
				}
			case COLUMN_DESCRIPTION:
				if (plugin instanceof Plugin)
				{
					return ((Plugin) plugin).getDescription();
				}
		}
		return EMPTY;
	}

}
