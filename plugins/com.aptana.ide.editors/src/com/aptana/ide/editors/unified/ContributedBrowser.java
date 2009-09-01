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
package com.aptana.ide.editors.unified;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

import com.aptana.ide.core.ui.browser.IBrowser;

/**
 * Abstract class for contributed browsers as a preview for the HTML editor.
 * 
 * @author Kevin Sawicki
 */
public abstract class ContributedBrowser implements IBrowser
{

	/**
	 * Listener list
	 */
	protected ListenerList list = new ListenerList();

	/**
	 * ContributedBrowser
	 */
	public ContributedBrowser()
	{
	}

	/**
	 * Executes a script and returns its success.
	 * 
	 * @param script
	 * @return true if the script succeeded, false otherwise
	 */
	public boolean execute(String script)
	{
		return false;
	}

	/**
	 * Gets the underlying browser object.
	 * 
	 * @return object browser
	 */
	public Object getUnderlyingBrowserObject()
	{
		return null;
	}

	/**
	 * Adds a progress listener.
	 * 
	 * @param listener
	 */
	public void addProgressListener(ProgressListener listener)
	{
		list.add(listener);
	}

	/**
	 * Removes a progress listener.
	 * 
	 * @param listener
	 */
	public void removeProgressListener(ProgressListener listener)
	{
		list.remove(listener);
	}

    /**
     * Adds a location listener.
     * 
     * @param listener
     */
    public void addLocationLister(LocationListener listener)
    {
        addLocationListener(listener);
    }

    /**
     * Removes the location listener.
     * 
     * @param listener
     */
    public void removeLocationLister(LocationListener listener)
    {
        removeLocationListener(listener);
    }

	public void addLocationListener(LocationListener listener)
	{
		// Does nothing, subclasses should override
	}

	public void removeLocationListener(LocationListener listener)
	{
		// Does nothing, subclasses should override
	}

	/**
	 * Notifies listeners that progress has completed.
	 * 
	 * @param event
	 */
	public void progressCompleted(ProgressEvent event)
	{
		Object[] listeners = list.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			((ProgressListener) listeners[i]).completed(event);
		}
	}

	/**
	 * Gets the browser type.
	 * 
	 * @return type
	 */
	public String getBrowserType()
	{
		return this.getClass().getName();
	}

	/**
	 * Display the source content of the current browser page.
	 */
	public void displaySource()
	{
		// Does nothing by default, subclasses can override
	}

	/**
	 * @see com.aptana.ide.core.ui.browser.IBrowser#refresh()
	 */
	public void refresh()
	{
		// Does nothing by default, subclasses can override
	}

	/**
	 * setOutline
	 * 
	 * @param outline
	 */
	public abstract void setOutline(ContributedOutline outline);
}
