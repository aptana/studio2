/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.scripting.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.views.browser.BrowserView;

/**
 * @author Paul Colton
 */
public class GenericScriptableView extends BrowserView
{
	boolean firstRun = true;

	/*
	 * Fields
	 */
	private Menu menu = null;

	/*
	 * Constructors
	 */

	/**
	 * GenericScriptableView
	 */
	public GenericScriptableView()
	{
		super();
	}

	/*
	 * Methods
	 */

	/**
	 * init
	 * 
	 * @param site
	 * @param memento
	 * @throws PartInitException
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, null);
	}

	/**
	 * @see com.aptana.ide.core.ui.views.browser.BrowserView#getStartUrl()
	 */
	public String getStartUrl()
	{
		return null;
	}

	/**
	 * getUrl
	 * 
	 * @return String
	 */
	public String getUrl()
	{
		return this.getURL();
	}

	/**
	 * setUrl
	 * 
	 * @param url
	 */
	public void setUrl(String url)
	{
		// On the first run, the browser control takes time to get started, this just gives
		// it a chance to be there. It wasn't clear how to programatically check for this. The
		// 'handle' ID seemed to always be there.
		if (this.firstRun)
		{
			try
			{
				Thread.sleep(1500);
			}
			catch (InterruptedException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.GenericScriptableView_UnableToSetUrlForViewTo, url), e); 
			}
			firstRun = false;
		}
	}


	/**
	 * @see com.aptana.ide.core.ui.views.browser.BrowserView#addToolBarActions()
	 */
	protected void addToolBarActions()
	{
	}

	/**
	 * setViewTitle
	 * 
	 * @param title
	 */
	public void setViewTitle(String title)
	{
		setPartName(title);
	}

	/**
	 * @see com.aptana.ide.core.ui.views.browser.BrowserView#getPartId()
	 */
	public String getPartId()
	{
		return "ScriptableViewId"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		// TODO Auto-generated method stub
		super.createPartControl(parent);

		if (menu == null)
		{
			menu = new Menu(getBrowser());
			MenuItem item = new MenuItem(menu, SWT.DROP_DOWN);
			item.setText(Messages.GenericScriptableView_PoweredByAptanaScriptingEngine); 
			item.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// TODO:
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					// TODO:
				}
			});
			getBrowser().setMenu(menu);
		}
	}
}
