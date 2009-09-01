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
package com.aptana.ide.editor.html.preview;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface IPreviewConfigurationPage
{

	/**
	 * Sets the url of the page
	 * 
	 * @param url
	 */
	void setURL(String url);

	/**
	 * Show the configuration area of this page
	 */
	void showEditArea();

	/**
	 * Dispose the preview configuration pages
	 */
	void dispose();

	/**
	 * True if this configuration can't be edited
	 * 
	 * @return - true if not modifiable
	 */
	boolean isReadOnly();

	/**
	 * True if this configuration page can be removed
	 * 
	 * @return - true if deletable
	 */
	boolean isDeletable();

	/**
	 * Runs an action on the preview page
	 * 
	 * @param actionID
	 * @return true if run
	 */
	boolean run(String actionID);

	/**
	 * Gets the type attribute of this config page
	 * 
	 * @return - type
	 */
	String getType();

	/**
	 * Gets the value attribute of this config page
	 * 
	 * @return - value
	 */
	String getValue();

	/**
	 * Sets the type of this config page
	 * 
	 * @param type
	 */
	void setType(String type);

	/**
	 * Sets the value of this config page
	 * 
	 * @param value
	 */
	void setValue(String value);

	/**
	 * Gets the url of this page
	 * 
	 * @return - url
	 */
	String getURL();

	/**
	 * Refreshes the browser preview
	 */
	void refresh();

	/**
	 * Gets the title of the preview page
	 * 
	 * @return - string title
	 */
	String getTitle();

	/**
	 * Sets the title of the preview page
	 * 
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * Gets the browser type
	 * 
	 * @return - string browser type
	 */
	String getBrowserLabel();

	/**
	 * Gets the class name of the browser
	 * 
	 * @return - class name of browser
	 */
	String getBrowserType();

	/**
	 * Clears the browser by setting the URL to about:blank
	 */
	void clearBrowser();

	/**
	 * Views the source of the preview browser
	 */
	void viewSource();

	/**
	 * Gets the preview page main control
	 * 
	 * @return - display area
	 */
	Control getControl();

	/**
	 * Sets the index of the preview page
	 * 
	 * @param index
	 */
	void setIndex(int index);

	/**
	 * Gets the tab image
	 * 
	 * @return - image for tab
	 */
	Image getTabImage();
}
