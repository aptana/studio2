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

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public abstract class BrowserView extends ViewPart {
	private String partId; //$NON-NLS-1$
	private Browser browser;
	private String url;

	// TODO: Add to plugin strings for localization

	/**
	 * Browser_homeButton_tooltip
	 */
	public static String Browser_homeButton_tooltip = Messages.BrowserView_Home;

	/**
	 * Browser_forwardButton_tooltip
	 */
	public static String Browser_forwardButton_tooltip = Messages.BrowserView_NavigateToNextTopic;

	/**
	 * Browser_backwardButton_tooltip
	 */
	public static String Browser_backwardButton_tooltip = Messages.BrowserView_NavigateToPreviousTopic;

	/**
	 * Browser_invalidConfig
	 */
	public static String Browser_invalidConfig = Messages.BrowserView_InvalidConfiguration;

	/**
	 * urlListener
	 */
	protected BrowserIntroPartLocationListener urlListener = new BrowserIntroPartLocationListener(this);

	/**
	 * history
	 */
	protected History history = new History();

	// Global actions

	/**
	 * backAction
	 */
	protected Action backAction = new Action()
	{

		{
			setToolTipText(BrowserView.Browser_backwardButton_tooltip);
			setImageDescriptor(BrowserView.getImageDescriptor("icons/backward_nav_on.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(BrowserView.getImageDescriptor("icons/backward_nav_off.gif")); //$NON-NLS-1$
		}

		public void run()
		{
			navigateBackward();
		}
	};

	/**
	 * forwardAction
	 */
	protected Action forwardAction = new Action()
	{
		{
			setToolTipText(BrowserView.Browser_forwardButton_tooltip);
			setImageDescriptor(BrowserView.getImageDescriptor("icons/forward_nav_on.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(BrowserView.getImageDescriptor("icons/forward_nav_off.gif")); //$NON-NLS-1$
		}

		public void run()
		{
			navigateForward();
		}
	};

	/**
	 * Returns the startup URL (to display when this control initially loads
	 * 
	 * @return String
	 */
	public abstract String getStartUrl();

	/**
	 * homeAction
	 */
	protected Action homeAction = new Action()
	{
		{
			setToolTipText(BrowserView.Browser_homeButton_tooltip); //$NON-NLS-1$
			setImageDescriptor(BrowserView.getImageDescriptor("icons/home_nav_on.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(BrowserView.getImageDescriptor("icons/home_nav_off.gif")); //$NON-NLS-1$
		}

		public void run()
		{
			navigateHome();
		}
	};

	/**
	 * The constructor.
	 */
	public BrowserView()
	{
		url = getStartUrl();
		partId = getPartId();
	}

	/**
	 * Returns the "part ID" which is used in the contexts.xml file
	 * 
	 * @return String
	 */
	public abstract String getPartId();

	/**
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{
			url = memento.getString(partId);
			sendMessage(url);
		}
	}

	/**
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento)
	{
		if (url != null)
		{
			memento.putString(partId, url);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		browser = new Browser(parent, SWT.NONE);
		if (this.url != null)
		{
			browser.setUrl(this.url);
		}

		PlatformUI.getWorkbench().getHelpSystem().setHelp(browser, "com.aptana.ide.js.ui." + partId); //$NON-NLS-1$

		// add a location listener on the browser so we can intercept
		// LocationEvents. Responsible for intercepting URLs and updating UI
		// with history.
		browser.addLocationListener(urlListener);

		// add a location listener that will clear a flag at the end of any
		// navigation to a page. This is used in conjunction with the location
		// listener to filter out redundant navigations due to frames.
		browser.addProgressListener(new ProgressListener()
		{

			public void changed(ProgressEvent event)
			{
				// no-op
			}

			public void completed(ProgressEvent event)
			{
				urlListener.flagEndOfNavigation();
				urlListener.flagEndOfFrameNavigation();
				urlListener.flagRemovedTempUrl();
				updateNavigationActionsState();
			}
		});

		addToolBarActions();
	}

	/*******************************************************************************************************************
	 * Pulled from BrowserIntroPartImplementation
	 ******************************************************************************************************************/

	protected void addToolBarActions()
	{

		IActionBars actionBars = getViewSite().getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		actionBars.setGlobalActionHandler(ActionFactory.FORWARD.getId(), forwardAction);
		actionBars.setGlobalActionHandler(ActionFactory.BACK.getId(), backAction);
		toolBarManager.add(homeAction);
		toolBarManager.add(backAction);
		toolBarManager.add(forwardAction);
		toolBarManager.update(true);
		actionBars.updateActionBars();
		updateNavigationActionsState();
	}

	/**
	 * Return a reference to the browser
	 * 
	 * @return Browser
	 */
	public Browser getBrowser()
	{
		return browser;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		browser.setFocus();
	}

	/**
	 * @param url 
	 */
	public void sendMessage(String url)
	{
		if (url != null)
		{
			this.url = url;
			if (browser != null)
			{
				browser.setUrl(url);
			}
		}
	}

	/**
	 * updateNavigationActionsState
	 */
	protected void updateNavigationActionsState()
	{
		// in static html intro, use browser history.
		forwardAction.setEnabled(browser.isForwardEnabled());
		backAction.setEnabled(browser.isBackEnabled());
	}

	/**
	 * navigateBackward
	 * 
	 * @return boolean
	 */
	public boolean navigateBackward()
	{
		return browser.back();
	}

	/**
	 * navigateForward
	 * 
	 * @return boolean
	 */
	public boolean navigateForward()
	{
		return browser.forward();
	}

	/**
	 * navigateHome
	 * 
	 * @return boolean
	 */
	public boolean navigateHome()
	{
		String location = url;
		boolean success = browser.setUrl(location);
		updateHistory(location);

		return success;
	}

	/**
	 * Updates the UI navigation history with either a real URL.
	 * 
	 * @param location
	 */
	public void updateHistory(String location)
	{
		history.updateHistory(location);
		updateNavigationActionsState();
	}
	
	
	/**
	 * Retrieves the image descriptor associated with resource from the image
	 * descriptor registry. If the image descriptor cannot be retrieved, attempt
	 * to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor assocated with resource or the default
	 *         "missing" image descriptor if one could not be found
	 */
	private static ImageDescriptor getImageDescriptor(String imageFilePath) {
		
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.eclipse.eclipsemonkey.ui", imageFilePath); //$NON-NLS-1$

		if (imageDescriptor == null) {
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * A Location Listener that knows how to intercept OOBE action URLs. It also knows how to update UI navigation
	 * history.
	 */
	private class BrowserIntroPartLocationListener implements LocationListener
	{
		private BrowserView implementation;

		/**
		 * Takes the implementation as an input.
		 * 
		 * @param implementation
		 */
		public BrowserIntroPartLocationListener(BrowserView implementation)
		{
			this.implementation = implementation;
		}

		/**
		 * @see org.eclipse.swt.browser.LocationListener#changed(org.eclipse.swt.browser.LocationEvent)
		 */
		public void changed(LocationEvent event)
		{
			String url = event.location;
			if (url == null)
			{
				return;
			}

			// guard against unnecessary History updates.
			Browser browser = (Browser) event.getSource();
			if (browser.getData("navigation") != null //$NON-NLS-1$
					&& browser.getData("navigation").equals("true")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return;
			}
		}

		/**
		 * Intercept any LocationEvents on the browser. If the event location is a valid IntroURL, cancel the event and
		 * execute the intro action that is embedded in the URL
		 * 
		 * @param event
		 */
		public void changing(LocationEvent event)
		{
			String url = event.location;
			if (url == null)
			{
				return;
			}
		}

		/**
		 * flagStartOfFrameNavigation
		 */
		public void flagStartOfFrameNavigation()
		{
			if (implementation.getBrowser().getData("frameNavigation") == null) //$NON-NLS-1$
			{
				implementation.getBrowser().setData("frameNavigation", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		/**
		 * flagEndOfFrameNavigation
		 */
		public void flagEndOfFrameNavigation()
		{
			implementation.getBrowser().setData("frameNavigation", null); //$NON-NLS-1$
		}

		/**
		 * flagStartOfNavigation
		 */
		public void flagStartOfNavigation()
		{
			if (implementation.getBrowser().getData("navigation") == null) //$NON-NLS-1$
			{
				implementation.getBrowser().setData("navigation", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		/**
		 * flagEndOfNavigation
		 */
		public void flagEndOfNavigation()
		{
			implementation.getBrowser().setData("navigation", null); //$NON-NLS-1$
		}

		/**
		 * flagStoredTempUrl
		 */
		public void flagStoredTempUrl()
		{
			if (implementation.getBrowser().getData("tempUrl") == null) //$NON-NLS-1$
			{
				implementation.getBrowser().setData("tempUrl", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		/**
		 * flagRemovedTempUrl
		 */
		public void flagRemovedTempUrl()
		{
			implementation.getBrowser().setData("tempUrl", null); //$NON-NLS-1$
		}
	}

	/**
	 * All browser history
	 * 
	 * @author Ingo Muschenetz
	 */
	private class History
	{
		// History of Intro Pages and real URL visited by Intro Browser. All
		// elements are all of type HistoryObject.
		private Vector history = new Vector();

		private int navigationLocation = 0;

		/**
		 * Model class for history objects. A history object may be a URL or an Intro page. A URL is a regular URL
		 * navigated to from a fully qualified link. An intro page may be an IFrame page. IFrame pages are not created
		 * for every Help topic navigated in an embedded IFrame. Instead the same IFrame is stored in history as a
		 * different object with the IFrameURL set. This way the model actually creates one page for every embedded Help
		 * Topic target but the navigation history updates the IFrame accordingly.
		 */
		class HistoryObject
		{
			String iframeUrl;

			String url;

			HistoryObject(Object location)
			{
				if (location instanceof String)
				{
					this.url = (String) location;
				}
			}

			String getIFrameUrl()
			{
				return iframeUrl;
			}

			String getUrl()
			{
				return url;
			}

			boolean isURL()
			{
				return (url != null) ? true : false;
			}

			boolean isIFramePage()
			{
				return (iframeUrl != null) ? true : false;
			}

		}

		/**
		 * Updates the UI navigation history with either a real URL, or a page ID.
		 * 
		 * @param location
		 */
		public void updateHistory(String location)
		{
			// quick exit.
			if (!history.isEmpty() && isSameLocation(location))
			{
				// resetting the same location is useless.
				return;
			}
			doUpdateHistory(location);
		}

		private void doUpdateHistory(Object location)
		{
			// we got here due to an intro URL listener or an SWT Form hyperlink
			// listener. location may be a URL or an IntroPage.
			if (navigationLocation == getHistoryEndPosition())
			{
				// we are at the end of the vector, just push.
				pushToHistory(location);
			}
			else
			{
				// we already navigated. add item at current location, and clear
				// rest of history. (Same as browser behavior.)
				trimHistory(location);
			}
		}

		private boolean isSameLocation(Object location)
		{
			HistoryObject currentLocation = getCurrentLocation();
			if (location instanceof String && currentLocation.isURL())
			{
				return currentLocation.getUrl().equals(location);
			}

			return false;
		}

		private void pushToHistory(Object location)
		{
			history.add(new HistoryObject(location));
			// point the nav location to the end of the vector.
			navigationLocation = getHistoryEndPosition();
		}

		/**
		 * removeLastHistory
		 */
		public void removeLastHistory()
		{
			history.remove(getHistoryEndPosition());
			// point the nav location to the end of the vector.
			navigationLocation = getHistoryEndPosition();
		}

		private void trimHistory(Object location)
		{
			List newHistory = history.subList(0, navigationLocation + 1);
			history = new Vector(newHistory);
			history.add(new HistoryObject(location));
			// point the nav location to the end of the vector.
			navigationLocation = getHistoryEndPosition();
		}

		/**
		 * Return the position of the last element in the navigation history. If vector is empty, return 0.
		 * 
		 * @param vector
		 * @return int
		 */
		private int getHistoryEndPosition()
		{
			if (history.isEmpty())
			{
				return 0;
			}
			return history.size() - 1;
		}

		/**
		 * navigateHistoryBackward
		 */
		public void navigateHistoryBackward()
		{
			if (badNavigationLocation(navigationLocation - 1))
			{
				// do nothing. We are at the beginning.
				return;
			}
			--navigationLocation;
		}

		/**
		 * Navigate forward in the history.
		 */
		public void navigateHistoryForward()
		{
			if (badNavigationLocation(navigationLocation + 1))
			{
				// do nothing. We are at the beginning.
				return;
			}
			++navigationLocation;
		}

		private boolean badNavigationLocation(int navigationLocation)
		{
			if (navigationLocation < 0 || navigationLocation >= history.size())
			{
				// bad nav location.
				return true;
			}
			return false;
		}

		/**
		 * Returns true if the current location in the navigation history represents a URL. False if the location is an
		 * Intro Page id.
		 * 
		 * @return Returns the locationIsURL.
		 */
		private HistoryObject getCurrentLocation()
		{
			return (HistoryObject) history.elementAt(navigationLocation);
		}

		/**
		 * canNavigateForward
		 * 
		 * @return boolean
		 */
		public boolean canNavigateForward()
		{
			return navigationLocation != getHistoryEndPosition() ? true : false;
		}

		/**
		 * canNavigateBackward
		 * 
		 * @return boolean
		 */
		public boolean canNavigateBackward()
		{
			return navigationLocation == 0 ? false : true;
		}

		/**
		 * currentLocationIsUrl
		 * 
		 * @return boolean
		 */
		public boolean currentLocationIsUrl()
		{
			return getCurrentLocation().isURL();
		}

		/**
		 * getCurrentLocationAsUrl
		 * 
		 * @return boolean
		 */
		public String getCurrentLocationAsUrl()
		{
			return getCurrentLocation().getUrl();
		}

		/**
		 * clear
		 */
		public void clear()
		{
			history.clear();
			navigationLocation = 0;
		}

	}
}
