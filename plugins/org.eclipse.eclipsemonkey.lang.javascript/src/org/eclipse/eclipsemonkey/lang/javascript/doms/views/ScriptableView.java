/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.views;

import org.eclipse.eclipsemonkey.lang.javascript.events.LocationChangedEvent;
import org.eclipse.eclipsemonkey.lang.javascript.events.LocationChangingEvent;
import org.eclipse.eclipsemonkey.ui.scriptableView.GenericScriptableView;
import org.eclipse.eclipsemonkey.utils.StringUtils;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mozilla.javascript.Scriptable;

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
	 * @return getView
	 */
	public IWorkbenchPart getView()
	{
		IWorkbenchPart result = super.getView();

		if (result == null)
		{
			result = Views.getViewInternal("org.eclipse.eclipsemonkey.ui.scriptableView.GenericScriptableView", this._id); //$NON-NLS-1$

			this.updateListeners(result, null);
		}

		return result;
	}

	/**
	 * @param view 
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

						if (innerEvent.location.indexOf("monkey:") == 0) //$NON-NLS-1$
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
					page.showView("org.eclipse.eclipsemonkey.ui.scriptableView.GenericScriptableView", _id, //$NON-NLS-1$
							makeVisible == true ? IWorkbenchPage.VIEW_VISIBLE : IWorkbenchPage.VIEW_CREATE);
				}
				catch (PartInitException e)
				{
					System.err.println(Messages.ScriptableView_Error + ": " + e); //$NON-NLS-1$
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
					GenericScriptableView view = (GenericScriptableView) part;
					view.setUrl(finalUrl);
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
