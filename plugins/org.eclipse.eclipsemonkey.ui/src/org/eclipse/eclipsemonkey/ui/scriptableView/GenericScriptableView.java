/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.ui.scriptableView;

import org.eclipse.eclipsemonkey.utils.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public class GenericScriptableView extends BrowserView {
	boolean firstRun = true;

	/*
	 * Fields
	 */
	//private Menu menu = null;

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
	 * @return String
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
		return this.getBrowser().getUrl();
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
				System.err.println(StringUtils.format(Messages.GenericScriptableView_UnableToSetUrlForViewTo, url) + ": " + e);  //$NON-NLS-1$
			}
			firstRun = false;
		}

		if (this.getBrowser().setUrl(url) == false)
		{
			System.err.println(Messages.GenericScriptableView_SetUrlFailed); 
		}

	}

	/**
	 * setText
	 * 
	 * @param text
	 */
	public void setText(String text)
	{
		this.getBrowser().setText(text);

	}

	/**
	 * execute
	 * 
	 * @param script
	 */
	public void execute(String script)
	{
		this.getBrowser().execute(script);
	}

	/**
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
	 * @return The ID of the part
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

		/*
		// Removed right-click menu override to allow regular access to browser menu options
		if (menu == null)
		{
			menu = new Menu(getBrowser());
			MenuItem item = new MenuItem(menu, SWT.DROP_DOWN);
			item.setText(Messages.GenericScriptableView_PoweredByEclipseMonkey); 
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
		*/
	}
}
