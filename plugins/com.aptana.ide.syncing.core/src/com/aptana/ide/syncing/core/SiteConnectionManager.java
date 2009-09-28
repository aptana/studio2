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

package com.aptana.ide.syncing.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;

import com.aptana.ide.core.epl.IMemento;
import com.aptana.ide.core.epl.XMLMemento;
import com.aptana.ide.syncing.core.events.ISiteConnectionListener;
import com.aptana.ide.syncing.core.events.SiteConnectionEvent;

/**
 * @author Max Stepanov
 *
 */
public class SiteConnectionManager implements ISiteConnectionManager {

	protected static final String STATE_FILENAME = "sites";

	private static final String ELEMENT_ROOT = "sites";
	private static final String ELEMENT_SITE = "site";

	private static SiteConnectionManager instance;
	
	private List<SiteConnection> connections = new ArrayList<SiteConnection>();
	private boolean dirty = false;
	
	private ListenerList listeners = new ListenerList();
	
	/**
	 * 
	 */
	private SiteConnectionManager() {
	}
	
	public static SiteConnectionManager getInstance() {
		if (instance == null) {
			instance = new SiteConnectionManager();
		}
		return instance;
	}

	/**
	 * loadState
	 * @param path
	 */
	public void loadState(IPath path) {
		File file = path.toFile();
		if (file.exists()) {
			try {
				FileReader reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				for (IMemento child : memento.getChildren(ELEMENT_SITE)) {
					SiteConnection siteConnection = restoreConnection(child);
					if (siteConnection != null) {
						connections.add(siteConnection);
					}
				}
			} catch (IOException e) {
			} catch (CoreException e) {
			}
		}
	}
	
	/**
	 * saveState
	 * @param path
	 */
	public void saveState(IPath path) {
		XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
		for (SiteConnection siteConnection : connections) {
			IMemento child = memento.createChild(ELEMENT_SITE);
			child.putMemento(storeConnection(siteConnection));
		}
		try {
			FileWriter writer = new FileWriter(path.toFile());
			memento.save(writer);
			isChanged();
		} catch (IOException e) {
		}
	}

	private IMemento storeConnection(SiteConnection siteConnection) {
		IMemento saveMemento = XMLMemento.createWriteRoot(ELEMENT_ROOT)
									.createChild(ELEMENT_SITE);
		siteConnection.saveState(saveMemento);
		return saveMemento;
	}
	
	private SiteConnection restoreConnection(IMemento memento) throws CoreException {
		SiteConnection siteConnection  = new SiteConnection();
		siteConnection.loadState(memento);
		return siteConnection;
	}

	/**
	 * isChanged
	 * @return
	 */
	public boolean isChanged() {
		for (SiteConnection siteConnection : connections) {
			if (siteConnection.isChanged()) {
				dirty = true;
			}
		}
		try {
			return dirty;
		} finally {
			dirty = false;
		}		
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#addSiteConnection(com.aptana.ide.syncing.core.ISiteConnection)
	 */
	public void addSiteConnection(ISiteConnection siteConnection) {
		if (!(siteConnection instanceof SiteConnection)) {
			throw new IllegalArgumentException();
		}
		if (!connections.contains(siteConnection)) {
			connections.add((SiteConnection) siteConnection);
			dirty = true;
			broadcastEvent(new SiteConnectionEvent(this, SiteConnectionEvent.POST_ADD, siteConnection));
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#removeSiteConnection(com.aptana.ide.syncing.core.ISiteConnection)
	 */
	public void removeSiteConnection(ISiteConnection siteConnection) {
		if (connections.contains(siteConnection)) {
			connections.remove(siteConnection);
			dirty = true;
			broadcastEvent(new SiteConnectionEvent(this, SiteConnectionEvent.POST_DELETE, siteConnection));
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#cloneSiteConnection(com.aptana.ide.syncing.core.ISiteConnection)
	 */
	public ISiteConnection cloneSiteConnection(ISiteConnection siteConnection) throws CoreException {
		if (!(siteConnection instanceof SiteConnection)) {
			throw new IllegalArgumentException();
		}
		return restoreConnection(storeConnection((SiteConnection) siteConnection));
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#createSiteConnection()
	 */
	public ISiteConnection createSiteConnection() {
		return new SiteConnection();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#getSiteConnections()
	 */
	public ISiteConnection[] getSiteConnections() {
		return connections.toArray(new ISiteConnection[connections.size()]);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#addListener(com.aptana.ide.syncing.core.events.ISiteConnectionListener)
	 */
	public void addListener(ISiteConnectionListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#removeListener(com.aptana.ide.syncing.core.events.ISiteConnectionListener)
	 */
	public void removeListener(ISiteConnectionListener listener) {
		listeners.add(listener);
	}

	private void broadcastEvent(SiteConnectionEvent event) {
		final Object[] list = listeners.getListeners();
	    for (Object listener : list) {
	        ((ISiteConnectionListener) listener).siteConnectionChanged(event);
	    }
	}

}
