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
package com.aptana.ide.scripting.views;

import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.scripting.ScriptingPlugin;
import com.aptana.ide.scripting.events.LocationChangedEvent;
import com.aptana.ide.scripting.events.LocationChangingEvent;

/**
 * @author Kevin Lindsey
 */
public class ScriptableView extends View
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -9171256728962509567L;
	private LocationListener _viewListener;
	// private String _url;
	private String _id;

	/*
	 * Properties
	 */

	/**
	 * @see com.aptana.ide.scripting.views.View#getView()
	 */
	public IWorkbenchPart getView()
	{
		IWorkbenchPart result = super.getView();

		if (result == null)
		{
			result = Views.getViewInternal("com.aptana.ide.js.ui.views.GenericScriptableView", this._id); //$NON-NLS-1$

			this.updateListeners(result, null);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.scripting.views.View#setView(org.eclipse.ui.IWorkbenchPart)
	 */
	public void setView(IWorkbenchPart view)
	{
		IWorkbenchPart currentView = this.getView();

		if (currentView != view)
		{
			this.updateListeners(view, currentView);
		}
	}

	/**
	 * updateListeners
	 *
	 * @param view
	 * @param currentView
	 */
	protected void updateListeners(IWorkbenchPart view, IWorkbenchPart currentView)
	{
		if (currentView != null && this._viewListener != null)
		{
			GenericScriptableView pview = (GenericScriptableView) currentView;

			pview.getBrowser().removeLocationListener(this._viewListener);
		}

		super.setView(view);

		if (view != null)
		{
			if (this._viewListener == null)
			{
				final ScriptableView self = this;

				// create listener
				this._viewListener = new LocationListener()
				{
					public void changing(LocationEvent innerEvent)
					{
						LocationChangingEvent event = new LocationChangingEvent(this, innerEvent);

						self.fireEventListeners(event);

						if (innerEvent.location.indexOf("aptana:") == 0) //$NON-NLS-1$
						{
							innerEvent.doit = false;
						}
					}

					public void changed(LocationEvent innerEvent)
					{
						LocationChangedEvent event = new LocationChangedEvent(this, innerEvent);

						self.fireEventListeners(event);
					}
				};
			}

			if (view instanceof GenericScriptableView)
			{
				final GenericScriptableView pview = (GenericScriptableView) view;
	
				final IWorkbench workbench = PlatformUI.getWorkbench();
				Display display = workbench.getDisplay();
	
				display.syncExec(new Runnable()
				{
					public void run()
					{
						pview.getBrowser().addLocationListener(_viewListener);
					}
				});
			}
		}
	}

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "ScriptableView"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of OutlineView
	 * 
	 * @param scope
	 * @param view
	 * @param id
	 */
	public ScriptableView(Scriptable scope, IWorkbenchPart view, String id)
	{
		super(scope, view);

		this._id = id;

		// define functions
		String[] names = new String[] { "setHTML", "execute", "showView", "setTitle" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.defineFunctionProperties(names, ScriptableView.class, READONLY | PERMANENT);

		// define properties
		this.defineProperty("url", ScriptableView.class, PERMANENT); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * showView
	 * 
	 * @param makeVisible
	 */
	public void showView(final boolean makeVisible)
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try
				{
					page.showView("com.aptana.ide.js.ui.views.GenericScriptableView", _id, //$NON-NLS-1$
							makeVisible == true ? IWorkbenchPage.VIEW_VISIBLE : IWorkbenchPage.VIEW_CREATE);
				}
				catch (PartInitException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptableView_Error, e);
				}
			}
		});
	}

	/**
	 * setTitle
	 * 
	 * @param title
	 */
	public void setTitle(final String title)
	{
		final IWorkbenchPart part = this.getView();

		if (part != null)
		{
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.asyncExec(new Runnable()
			{
				public void run()
				{
					GenericScriptableView view = (GenericScriptableView) part;
					view.setViewTitle(title);
				}
			});
		}
	}

	/**
	 * setHTML
	 * 
	 * @param html
	 */
	public void setHTML(final String html)
	{
		final IWorkbenchPart part = this.getView();

		if (part != null)
		{
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.asyncExec(new Runnable()
			{
				public void run()
				{
					GenericScriptableView view = (GenericScriptableView) part;
					view.setText(html);
				}
			});
		}
	}

	/**
	 * execute
	 * 
	 * @param script
	 */
	public void execute(final String script)
	{
		final IWorkbenchPart part = this.getView();

		if (part != null)
		{
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				public void run()
				{
					GenericScriptableView view = (GenericScriptableView) part;
					view.execute(script);
				}
			});
		}
	}

	/**
	 * setUrl
	 * 
	 * @param url
	 */
	public void setUrl(String url)
	{
		final IWorkbenchPart part = this.getView();

		if (part != null)
		{
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();
			
//			if(url.startsWith("http://localhost"))
//			{
//				url = url + "?nocache=" + System.currentTimeMillis() + "_" + Math.round(Math.random() * 1000000);
//			}
			
			final String finalUrl = url;

			display.asyncExec(new Runnable()
			{
				public void run()
				{
					if (part instanceof GenericScriptableView)
					{
						GenericScriptableView view = (GenericScriptableView) part;
						view.setUrl(finalUrl);
					}
				}
			});
		}
	}

	/**
	 * getUrl
	 * 
	 * @return Url
	 */
	public String getUrl()
	{
		final IWorkbenchPart part = this.getView();
		String result = StringUtils.EMPTY;

		if (part != null)
		{
			/**
			 * Url
			 */
			class Url
			{
				public String value;
			}

			final Url url = new Url();
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				public void run()
				{
					GenericScriptableView view = (GenericScriptableView) part;
					String val = view.getUrl();
					url.value = val;
				}
			});

			result = url.value;
		}

		return result;
	}
}
