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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;

/**
 * Contributes the default eclipse SWT browser as a preview tab.
 * 
 * @author Kevin Sawicki
 */
public class DefaultBrowser extends ContributedBrowser
{
	private Browser browser;
	private ContributedOutline outline;
	private ProgressListener listener;
	private String url;

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		listener = new ProgressAdapter()
		{

			public void completed(ProgressEvent event)
			{
				progressCompleted(event);
			}

		};
		browser.addProgressListener(listener);
	}

	private String addCacheBuster(String baseURL)
	{
		String bustedURL = null;
		if (UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.CACHE_BUST_BROWSERS))
		{
			if (baseURL != null)
			{
				String buster = "APTANA_NOCACHE_" + System.currentTimeMillis() + "=" + System.currentTimeMillis(); //$NON-NLS-1$ //$NON-NLS-2$
				if (baseURL.indexOf('?') == -1)
				{
					bustedURL = baseURL + "?" + buster; //$NON-NLS-1$
				}
				else
				{
					bustedURL = baseURL + "&" + buster; //$NON-NLS-1$
				}
			}
		}
		else
		{
			bustedURL = baseURL;
		}
		return bustedURL;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#setURL(java.lang.String)
	 */
	public void setURL(String url)
	{

		this.url = url;
		if (browser != null && !browser.isDisposed() && this.url != null)
		{
			if (!"about:blank".equals(url) && !url.startsWith("file:/")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				browser.setUrl(addCacheBuster(url));
			}
			else
			{
				browser.setUrl(url);
			}
			if (outline != null)
			{
				outline.refresh();
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#getControl()
	 */
	public Control getControl()
	{
		return browser;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#addLocationListener(org.eclipse.swt.browser.LocationListener)
	 */
	public void addLocationListener(LocationListener listener)
	{
		if (browser != null)
		{
			browser.addLocationListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#removeLocationLister(org.eclipse.swt.browser.LocationListener)
	 */
	public void removeLocationLister(LocationListener listener)
	{
		if (browser != null)
		{
			browser.removeLocationListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#getUnderlyingBrowserObject()
	 */
	public Object getUnderlyingBrowserObject()
	{
		return this.browser;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#refresh()
	 */
	public void refresh()
	{
		if (browser != null && !browser.isDisposed() && url != null)
		{
			browser.setUrl(addCacheBuster(url));
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#dispose()
	 */
	public void dispose()
	{
		browser.dispose();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#setOutline(com.aptana.ide.editors.unified.ContributedOutline)
	 */
	public void setOutline(ContributedOutline outline)
	{
		this.outline = outline;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#back()
	 */
	public void back()
	{
		browser.back();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#forward()
	 */
	public void forward()
	{
		browser.forward();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#execute(java.lang.String)
	 */
	public boolean execute(String script)
	{
		return browser.execute(script);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#displaySource()
	 */
	public void displaySource()
	{
		browser.execute("window.status='empty'"); //$NON-NLS-1$
		browser.addStatusTextListener(new StatusTextListener()
		{

			public void changed(StatusTextEvent event)
			{
				if (event.text != null)
				{
					try
					{
						String newFileName = FileUtils.getRandomFileName("source", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
						final File temp = new File(FileUtils.systemTempDir + File.separator + newFileName);
						temp.deleteOnExit();
						FileUtils.writeStringToFile(event.text, temp);
						IEditorInput input = CoreUIUtils.createJavaFileEditorInput(temp);
						try
						{
							IDE.openEditor(HTMLPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
									.getActivePage(), input, HTMLEditor.ID);
						}
						catch (PartInitException e)
						{
							e.printStackTrace();
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				browser.removeStatusTextListener(this);
			}

		});
		browser.execute("window.status=document.documentElement.outerHTML.toString()"); //$NON-NLS-1$
	}
}
