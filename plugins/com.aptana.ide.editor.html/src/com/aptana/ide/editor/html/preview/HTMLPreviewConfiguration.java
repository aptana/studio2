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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.editor.html.preview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLPlugin;

/**
 * HTML preview configuration.
 * @author Denis Denisenko
 */
public class HTMLPreviewConfiguration implements IPreviewConfiguration
{

	/**
	 * {@inheritDoc}
	 */
	public String getPreviewName()
	{
		return HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE); 
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreviewType()
	{
		return HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean projectOverridesPreview(IProject project)
	{
		String override = null;
		try
		{
			override = project.getPersistentProperty(
					new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE)); //$NON-NLS-1$
		} 
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLPreviewConfiguration_ERR_Exception, e);
		}
		return HTMLPreviewPropertyPage.TRUE.equals(override);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreviewName(IProject project)
	{
		try
		{
			return project.getPersistentProperty(
					new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE)); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLPreviewConfiguration_ERR_Exception, e);
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreviewType(IProject project)
	{
		try
		{
			return project.getPersistentProperty(
					new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE)); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLPreviewConfiguration_ERR_Exception, e);
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFileBasedType(String type)
	{
		return HTMLPreviewPropertyPage.FILE_BASED_TYPE.equals(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAppendedServerBasedType(String type)
	{
		return HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(type);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isServerBasedType(String type)
	{
		return HTMLPreviewPropertyPage.SERVER_BASED_TYPE.equals(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAbsoluteBasedType(String type)
	{
		return HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE.equals(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConfigurationBasedType(String type)
	{
		return HTMLPreviewPropertyPage.CONFIG_BASED_TYPE.equals(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAppendedAbsoluteBasedType(String type)
	{
		return HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE.equals(type);
	}
}
