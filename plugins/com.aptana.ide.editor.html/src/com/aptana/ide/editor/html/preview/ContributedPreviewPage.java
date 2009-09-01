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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.aptana.ide.editor.html.BrowserExtensionLoader;
import com.aptana.ide.editor.html.MultiPageHTMLEditor;
import com.aptana.ide.editors.unified.ContributedBrowser;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ContributedPreviewPage extends BasePreviewConfigurationPage
{

	private Composite displayArea;
	private Composite browserArea;

	private ProgressListener listener;
	private Listener locListener;
	private Event lastEvent = null;
	private boolean created = false;

	/**
	 * Creates a new preview configuration page
	 * 
	 * @param editor
	 */
	public ContributedPreviewPage(MultiPageHTMLEditor editor)
	{
		super(editor);
		locListener = new Listener()
		{

			public void handleEvent(Event event)
			{
				if (lastEvent == null || event.time > lastEvent.time)
				{
					lastEvent = event;
					if (event.data instanceof Image)
					{
						ContributedPreviewPage.this.editor.setTabIcon(ContributedPreviewPage.this, (Image) event.data);
					}
					String addOn = event.text;
					if (addOn != null)
					{
						ContributedPreviewPage.this.editor.setTabTooltip(ContributedPreviewPage.this, url + " " //$NON-NLS-1$
								+ event.text);
					}
				}
			}

		};
		listener = new ProgressListener()
		{

			public void completed(ProgressEvent event)
			{
				BrowserExtensionLoader.getDecorator(browser, locListener);
			}

			public void changed(ProgressEvent event)
			{

			}

		};
	}

	/**
	 * Creates the preview page control
	 * 
	 * @param parent -
	 *            parent of preview page
	 */
	public void createControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, false);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.browserArea = new Composite(displayArea, SWT.NONE);
		GridData baData = new GridData(SWT.FILL, SWT.FILL, true, true);
		baData.exclude = true;
		GridLayout baLayout = new GridLayout(1, false);
		baLayout.marginHeight = 0;
		baLayout.marginWidth = 0;
		this.browserArea.setLayout(baLayout);
		this.browserArea.setLayoutData(baData);
		this.browserArea.setVisible(false);
	}

	/**
	 * Sets the browser object and its name albe
	 * 
	 * @param browser
	 * @param label
	 */
	public void setBrowser(ContributedBrowser browser, String label)
	{
		if (this.browser != null)
		{
			this.browser.dispose();
		}
		this.browserLabel = label;
		if (this.browser != null)
		{
			this.browser.removeProgressListener(listener);
		}
		this.browser = browser;
		this.created = false;
	}

	/**
	 * Displays the edit area of this preview page
	 */
	public void showEditArea()
	{
		displayArea.layout(true, true);
	}

	/**
	 * Displays the browser area of this preview page
	 */
	public void showBrowserArea()
	{
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = false;
		this.browserArea.setVisible(true);
		displayArea.layout(true, true);
	}

	private void create()
	{
		if (!created)
		{
			created = true;
			this.browser.createControl(browserArea);
			this.browser.addProgressListener(listener);
			browserArea.layout(true, true);
		}
	}

	/**
	 * Sets the url of the preview page
	 * 
	 * @param url
	 */
	public void setURL(String url)
	{
		if (this.browser != null && this.value != null)
		{
			create();
			editor.setTabTooltip(this, this.value);
			this.browser.setURL(this.value);
		}
	}

	/**
	 * Sets the editor for this tab
	 * 
	 * @param editor
	 */
	public void setEditor(MultiPageHTMLEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * Gets the control of the preview page
	 * 
	 * @return - control
	 */
	public Control getControl()
	{
		return this.displayArea;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isDeletable()
	 */
	public boolean isDeletable()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isReadOnly()
	 */
	public boolean isReadOnly()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#run(java.lang.String)
	 */
	public boolean run(String actionID)
	{
		return false;
	}

}
