/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class EclipseUIUtils
{

	/**
	 * IDEWorkbenchPlugin_IDE_WORKBENCH
	 */
	public static final String IDEWorkbenchPlugin_IDE_WORKBENCH = IDEWorkbenchPlugin.IDE_WORKBENCH;

	/**
	 * ResourceMessages_NewProject_errorMessage
	 */
	public static final String ResourceMessages_NewProject_errorMessage = ResourceMessages.NewProject_errorMessage;

	/**
	 * ResourceMessages_NewProject_internalError
	 */
	public static final String ResourceMessages_NewProject_internalError = ResourceMessages.NewProject_internalError;

	/**
	 * ResourceMessages_NewProject_title
	 */
	public static final String ResourceMessages_NewProject_title = ResourceMessages.NewProject_title;

	/**
	 * ResourceMessages_NewProject_description
	 */
	public static final String ResourceMessages_NewProject_description = ResourceMessages.NewProject_description;

	/**
	 * ResourceMessages_NewProject_caseVariantExistsError
	 */
	public static final String ResourceMessages_NewProject_caseVariantExistsError = ResourceMessages.NewProject_caseVariantExistsError;

	/**
	 * IDEWorkbenchMessages_WizardNewProjectCreationPage_nameLabel
	 */
	public static final String IDEWorkbenchMessages_WizardNewProjectCreationPage_nameLabel = IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel;

	/**
	 * IDEWorkbenchMessages_WizardNewProjectCreationPage_projectNameEmpty
	 */
	public static final String IDEWorkbenchMessages_WizardNewProjectCreationPage_projectNameEmpty = IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty;

	/**
	 * IDEWorkbenchMessages_WizardNewProjectCreationPage_projectExistsMessage
	 */
	public static final String IDEWorkbenchMessages_WizardNewProjectCreationPage_projectExistsMessage = IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage;

	private EclipseUIUtils()
	{
		// Does nothing
	}

	/**
	 * Gets the IDE workbench plugin
	 * 
	 * @return - IDEWorkbenchPlugin
	 */
	public static IDEWorkbenchPlugin getIDEWorkbenchPlugin()
	{
		return IDEWorkbenchPlugin.getDefault();
	}

	/**
	 * Gets the debug ui plugin image label
	 * 
	 * @param element
	 * @return - image
	 */
	public static Image getDebugUIPluginImageLabel(Object element)
	{
		return DebugUIPlugin.getDefaultLabelProvider().getImage(element);
	}

	/**
	 * Creates a new decorating label provider from a workbench label provider created around the label decorator from
	 * IDE workbench plugin
	 * 
	 * @return - created workbench label provider
	 */
	public static ILabelProvider createWorkbenchLabelProvider()
	{
		ILabelProvider labelProvider = new DecoratingLabelProvider(new WorkbenchLabelProvider(), IDEWorkbenchPlugin
				.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator());
		return labelProvider;
	}

	/**
	 * Gets the workbench plugin editor registry
	 * 
	 * @return - editor registry
	 */
	public static IEditorRegistry getWorkbenchEditorRegistry()
	{
		return WorkbenchPlugin.getDefault().getEditorRegistry();
	}

}
