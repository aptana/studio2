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
package com.aptana.ide.core.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class URLStatusTracker
{

	/**
	 * The listener that gets notified on status events.
	 */
	public static interface Listener
	{
		/**
		 * Indicates the status has changed for accessing a particular URL.
		 * 
		 * @param url
		 *            the specific URL
		 * @param status
		 *            the new status
		 */
		public void statusUpdated(URL url, int status);
	}

	private static URLStatusTracker fURLErrors = null;

	// the list of listeners
	private List<Listener> fListeners;
	// maps URL to its status
	private Map<URL, Integer> fStatus;

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(Listener listener)
	{
		if (!fListeners.contains(listener))
		{
			fListeners.add(listener);
		}
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(Listener listener)
	{
		fListeners.remove(listener);
	}

	/**
	 * Removes all current listeners.
	 */
	public void removeAllListeners()
	{
		fListeners.clear();
	}

	/**
	 * Updates the status on a particular URL.
	 * 
	 * @param url
	 *            the specific URL
	 * @param status
	 *            the new status
	 */
	public void setStatus(URL url, int status)
	{
		Integer oldStatus = fStatus.get(url);
		if (oldStatus == null || oldStatus.intValue() != status)
		{
			fStatus.put(url, new Integer(status));
			fireStatusUpdated(url, status);
		}
	}

	/**
	 * Gets the URLStatusTracker instance.
	 * 
	 * @return the URLStatusTracker instance
	 */
	public static URLStatusTracker getInstance()
	{
		if (fURLErrors == null)
		{
			fURLErrors = new URLStatusTracker();
		}
		return fURLErrors;
	}

	private void fireStatusUpdated(URL url, int status)
	{
		for (Listener listener : fListeners)
		{
			listener.statusUpdated(url, status);
		}
	}

	/**
	 * A private constructor.
	 */
	private URLStatusTracker()
	{
		fListeners = new ArrayList<Listener>();
		fStatus = new HashMap<URL, Integer>();
	}

}
