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
package com.aptana.ide.editors.wizards;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Kevin Lindsey
 *
 */
public class UntitledColorizationFileWizard extends UntitledTextFileWizard
{
/**
	 * @see com.aptana.ide.editors.wizards.UntitledTextFileWizard#getFriendlyName()
	 */
	protected String getFriendlyName()
	{
		return Messages.UntitledColorizationFileWizard_Friendly_Name;
	}

	/**
	 * @see com.aptana.ide.editors.wizards.UntitledTextFileWizard#getFileExtension()
	 */
	protected String getFileExtension()
	{
		return ".col"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.wizards.UntitledTextFileWizard#getInitialFileContents()
	 */
	protected String getInitialFileContents()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); //$NON-NLS-1$
		pw.println("<colorizer"); //$NON-NLS-1$
		pw.println("	xmlns=\"http://www.aptana.com/2007/colorizer/1.0\""); //$NON-NLS-1$
		pw.println("	language=\"text/plain\""); //$NON-NLS-1$
		pw.println("	background=\"rgb(255,255,255)\""); //$NON-NLS-1$
		pw.println("	line-highlight=\"rgb(232,242,254)\""); //$NON-NLS-1$
		pw.println("	selection-foreground=\"COLOR_LIST_SELECTION_TEXT\""); //$NON-NLS-1$
		pw.println("	selection-background=\"COLOR_LIST_SELECTION\""); //$NON-NLS-1$
		pw.println("	folding-background=\"COLOR_WIDGET_BACKGROUND\""); //$NON-NLS-1$
		pw.println("	folding-foreground=\"rgb(139,139,139)\""); //$NON-NLS-1$
		pw.println("	caret-color=\"rgb(0,0,0)\">"); //$NON-NLS-1$
		pw.println();
		pw.println("    <category name=\"WHITESPACE\" style=\"whitespace\"/>"); //$NON-NLS-1$
		pw.println("    <category name=\"ERROR\" style=\"error\"/>"); //$NON-NLS-1$
		pw.println();
		pw.println("	<style id=\"whitespace\" foreground=\"rgb(0,128,0)\"/>"); //$NON-NLS-1$
		pw.println("	<style id=\"error\" foreground=\"rgb(128,0,0)\"/>"); //$NON-NLS-1$
		pw.println();
		pw.println("</colorizer>"); //$NON-NLS-1$
		pw.close();
		
		return sw.toString();
	}
}
