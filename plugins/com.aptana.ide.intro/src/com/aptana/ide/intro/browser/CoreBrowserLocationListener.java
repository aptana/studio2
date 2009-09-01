/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

/**
 * Core browser location listener to open custom Eclipse URLs
 * @author Kevin Sawicki
 *
 */
public class CoreBrowserLocationListener implements LocationListener
{
	
	
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
	 * @param event 
	 */
	public void changing(LocationEvent event)
	{
		String url = event.location;
		if (url == null)
		{
			return;
		}

		URLParser parser = new URLParser(url);
		if (parser.hasIntroUrl())
		{
			// stop URL first.
			event.doit = false;
			// execute the action embedded in the IntroURL
			CoreURL introURL = parser.getIntroURL();
			introURL.setBrowser((Browser)event.getSource());
			introURL.execute();

			return;
		}

	}

}
