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

/**
 * Preview configuration.
 * 
 * @author Denis Denisenko
 */
public interface IPreviewConfiguration
{
	/**
	 * Gets preview type like on-server preview, preview based on configuration etc.
	 * 
	 * @return preview type.
	 */
	String getPreviewType();

	/**
	 * Gets preview name like server ID or configuration name.
	 * 
	 * @return preview name.
	 */
	String getPreviewName();

	/**
	 * Gets preview type from project properties.
	 * 
	 * @param project
	 * @return preview type.
	 */
	String getPreviewType(IProject project);

	/**
	 * Gets preview name from project properties.
	 * 
	 * @param project
	 * @return preview name.
	 */
	String getPreviewName(IProject project);

	/**
	 * Gets whether a type specified is a file-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if file-specified, false otherwise.
	 */
	boolean isFileBasedType(String type);

	/**
	 * Gets whether a type specified is a server-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if server-specified, false otherwise.
	 */
	boolean isServerBasedType(String type);

	/**
	 * Gets whether a type specified is a appended server-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if appended server-specified, false otherwise.
	 */
	boolean isAppendedServerBasedType(String type);

	/**
	 * Gets whether a type specified is a configuration-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if configuration-specified, false otherwise.
	 */
	boolean isConfigurationBasedType(String type);

	/**
	 * Gets whether a type specified is a absolute-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if absolute-based, false otherwise.
	 */
	boolean isAbsoluteBasedType(String type);

	/**
	 * Gets whether a type specified is an appended absolute-based type.
	 * 
	 * @param type -
	 *            type to check.
	 * @return true if appended absolute-based, false otherwise.
	 */
	boolean isAppendedAbsoluteBasedType(String type);

	/**
	 * Whether project contains preview override.
	 * 
	 * @param project -
	 *            project.
	 * @return true if overrides, false otherwise.
	 */
	boolean projectOverridesPreview(IProject project);
}
