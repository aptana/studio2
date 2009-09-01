/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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

import com.aptana.ide.editor.html.MultiPageHTMLEditor;
import com.aptana.ide.editors.unified.ContributedBrowser;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class BasePreviewConfigurationPage implements IPreviewConfigurationPage
{

	/**
	 * Browser label
	 */
	protected String browserLabel = ""; //$NON-NLS-1$

	/**
	 * Browser title
	 */
	protected String title;

	/**
	 * Preview type
	 */
	protected String type;

	/**
	 * Preview value
	 */
	protected String value;

	/**
	 * Created boolean
	 */
	protected boolean created = false;

	/**
	 * Browser widget
	 */
	protected ContributedBrowser browser;

	/**
	 * Browser url
	 */
	protected String url;

	/**
	 * Editor this tab is a part of
	 */
	protected MultiPageHTMLEditor editor;

	/**
	 * The index of this tab
	 */
	protected int index = -1;

	/**
	 * Creates a base preview config page for an editor
	 * 
	 * @param editor
	 */
	public BasePreviewConfigurationPage(MultiPageHTMLEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * Sets the index of this preview page
	 * 
	 * @param index -
	 *            index of page
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#clearBrowser()
	 */
	public void clearBrowser()
	{
		if (created && this.browser != null && this.browser.getControl() != null
				&& !this.browser.getControl().isDisposed())
		{
			this.browser.setURL("about:blank"); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#dispose()
	 */
	public void dispose()
	{
		if (this.browser != null && created)
		{
			this.browser.dispose();
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getBrowserLabel()
	 */
	public String getBrowserLabel()
	{
		return this.browserLabel;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getBrowserType()
	 */
	public String getBrowserType()
	{
		if (browser != null)
		{
			return browser.getBrowserType();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getTitle()
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getType()
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getURL()
	 */
	public String getURL()
	{
		return this.url;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getValue()
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#refresh()
	 */
	public void refresh()
	{
		if (browser != null)
		{
			browser.refresh();
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#setTitle(java.lang.String)
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#setType(java.lang.String)
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#viewSource()
	 */
	public void viewSource()
	{
		if (created && this.browser != null && this.browser.getControl() != null
				&& !this.browser.getControl().isDisposed())
		{
			this.browser.displaySource();
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#getTabImage()
	 */
	public Image getTabImage()
	{
		return null;
	}

}
