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
package com.aptana.ide.core.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.epl.IMemento;
import com.aptana.ide.core.epl.XMLMemento;
import com.aptana.ide.core.io.internal.ConnectionPointEvent;
import com.aptana.ide.core.io.internal.NotificationManager;

/**
 * @author Max Stepanov
 *
 */
/* package */ final class ConnectionPointManager extends PlatformObject implements IConnectionPointManager {

	protected static final String STATE_FILENAME = "connections";

	private static final String EXTENSION_POINT_ID = CoreIOPlugin.PLUGIN_ID + ".connectionPoint";
	protected static final String TAG_CONNECTION_POINT_TYPE = "connectionPointType";
	protected static final String TAG_CONNECTION_POINT_CATEGORY = "connectionPointCategory";
	protected static final String ATT_ID = "id";
	protected static final String ATT_NAME = "name";
	protected static final String ATT_ORDER = "order";
	protected static final String ATT_CLASS = "class";
	protected static final String ATT_CATEGORY = "category";

	private static final String ELEMENT_ROOT = "connections";
	private static final String ELEMENT_CONNECTION = "connection";
	private static final String ATTR_ID = "id";
	private static final String ATTR_TYPE = "type";

	private static ConnectionPointManager instance;

	private List<ConnectionPoint> connections = new ArrayList<ConnectionPoint>();
	private Map<String, ConnectionPointCategory> categories = new HashMap<String, ConnectionPointCategory>();
	private List<ConnectionPointType> types = new ArrayList<ConnectionPointType>();
	private Map<String, IConfigurationElement> configurationElements = new HashMap<String, IConfigurationElement>();
	private List<IMemento> unresolvedConnections = new ArrayList<IMemento>();
	private boolean dirty = false;
	
	private NotificationManager notificationManager;

	/**
	 * 
	 */
	private ConnectionPointManager() {
		readExtensionRegistry();
		notificationManager = new NotificationManager();
	}
	
	/**
	 * Returns shared instance
	 * @return
	 */
	public static ConnectionPointManager getInstance() {
		if (instance == null) {
			instance = new ConnectionPointManager();
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
				for (IMemento child : memento.getChildren(ELEMENT_CONNECTION)) {
					ConnectionPoint connectionPoint = restoreConnectionPoint(child);
					if (connectionPoint != null) {
						connections.add(connectionPoint);
					} else {
						unresolvedConnections.add(child);
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
		for (ConnectionPoint connectionPoint : connections) {
			IMemento child = memento.createChild(ELEMENT_CONNECTION);
			child.putMemento(storeConnectionPoint(connectionPoint));
		}
		for (IMemento child : unresolvedConnections) {
			memento.copyChild(child);
		}
		try {
			FileWriter writer = new FileWriter(path.toFile());
			memento.save(writer);
			isChanged();
		} catch (IOException e) {
		}
	}
	
	/**
	 * isChanged
	 * @return
	 */
	public boolean isChanged() {
		for (ConnectionPoint connectionPoint : connections) {
			if (connectionPoint.isChanged()) {
				dirty = true;
			}
		}
		try {
			return dirty;
		} finally {
			dirty = false;
		}		
	}
	
	/* package */ void broadcastEvent(IConnectionPointEvent event) {
	    notificationManager.broadcastChanges(event);
	}

	/* package */ IConnectionPoint[] getConnectionPointsForType(String type) {
		List<IConnectionPoint> list = new ArrayList<IConnectionPoint>();
		for (ConnectionPoint connectionPoint : connections) {
			if (type.equals(connectionPoint.getType())) {
				list.add(connectionPoint);
			}
		}
		return list.toArray(new IConnectionPoint[list.size()]);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#addConnectionPoint(com.aptana.ide.core.io.IConnectionPoint)
	 */
	public void addConnectionPoint(IConnectionPoint connectionPoint) {
		if (!(connectionPoint instanceof ConnectionPoint)) {
			throw new IllegalArgumentException();
		}
		if (!connections.contains(connectionPoint)) {
			connections.add((ConnectionPoint) connectionPoint);
			dirty = true;
			broadcastEvent(new ConnectionPointEvent(this, IConnectionPointEvent.POST_ADD, connectionPoint));
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#removeConnectionPoint(com.aptana.ide.core.io.IConnectionPoint)
	 */
	public void removeConnectionPoint(IConnectionPoint connectionPoint) {
		if (connections.contains(connectionPoint)) {
			connections.remove(connectionPoint);
			dirty = true;
			broadcastEvent(new ConnectionPointEvent(this, IConnectionPointEvent.POST_DELETE, connectionPoint));
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#cloneConnectionPoint(com.aptana.ide.core.io.IConnectionPoint)
	 */
	public IConnectionPoint cloneConnectionPoint(IConnectionPoint connectionPoint) throws CoreException {
		if (!(connectionPoint instanceof ConnectionPoint)) {
			throw new IllegalArgumentException();
		}
		ConnectionPoint clonedConnectionPoint = restoreConnectionPoint(storeConnectionPoint((ConnectionPoint) connectionPoint));
		clonedConnectionPoint.setId(UUID.randomUUID().toString());
		return clonedConnectionPoint;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#getTypes()
	 */
	public ConnectionPointType[] getTypes() {
		return types.toArray(new ConnectionPointType[types.size()]);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#getType(java.lang.String)
	 */
	public ConnectionPointType getType(String typeId) {
		for (ConnectionPointType type : types) {
			if (type.getType().equals(typeId)) {
				return type;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#getType(com.aptana.ide.core.io.IConnectionPoint)
	 */
	public ConnectionPointType getType(IConnectionPoint connectionPoint) {
		if (!(connectionPoint instanceof ConnectionPoint)) {
			throw new IllegalArgumentException();
		}
		return getType(((ConnectionPoint) connectionPoint).getType());
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#getConnectionPointCategories()
	 */
	public IConnectionPointCategory[] getConnectionPointCategories() {
		return categories.values().toArray(new IConnectionPointCategory[categories.size()]);
	}

    /* (non-Javadoc)
     * @see com.aptana.ide.core.io.IConnectionPointManager#getConnectionPointCategory(String)
     */
	public IConnectionPointCategory getConnectionPointCategory(String categoryId) {
	    return categories.get(categoryId);
	}

	private IMemento storeConnectionPoint(ConnectionPoint connectionPoint) {
		IMemento saveMemento = XMLMemento.createWriteRoot(ELEMENT_ROOT)
									.createChild(ELEMENT_CONNECTION);
		connectionPoint.saveState(saveMemento);
		saveMemento.putString(ATTR_ID, connectionPoint.getId());
		saveMemento.putString(ATTR_TYPE, connectionPoint.getType());
		return saveMemento;
	}
	
	private ConnectionPoint restoreConnectionPoint(IMemento memento) throws CoreException {
		ConnectionPoint connectionPoint  = null;
		String typeId = memento.getString(ATTR_TYPE);
		if (typeId != null) {
			IConfigurationElement element = configurationElements.get(typeId);
			if (element != null) {
				Object object = element.createExecutableExtension(ATT_CLASS);
				if (object instanceof ConnectionPoint) {
					connectionPoint = (ConnectionPoint) object;
					connectionPoint.setId(memento.getString(ATTR_ID) != null ? memento.getString(ATTR_ID) : memento.getID());
					//TODO: remove memento.getID() before production
					connectionPoint.loadState(memento);
				}
			}
		}
		return connectionPoint;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#createConnectionPoint(com.aptana.ide.core.io.ConnectionPointType)
	 */
	public IConnectionPoint createConnectionPoint(ConnectionPointType type) throws CoreException {
		if (type != null) {
			return createConnectionPoint(type.getType());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#createConnectionPoint(java.lang.String)
	 */
	public IConnectionPoint createConnectionPoint(String typeId) throws CoreException {
		ConnectionPoint connectionPoint  = null;		
		IConfigurationElement element = configurationElements.get(typeId);
		if (element != null) {
			Object object = element.createExecutableExtension(ATT_CLASS);
			if (object instanceof ConnectionPoint) {
				connectionPoint = (ConnectionPoint) object;
			}
		}
		return connectionPoint;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointManager#addConectionPointListener(com.aptana.ide.core.io.IConnectionPointListener)
	 */
    public void addConnectionPointListener(IConnectionPointListener listener) {
        notificationManager.addListener(listener);
    }

    /* (non-Javadoc)
     * @see com.aptana.ide.core.io.IConnectionPointManager#removeConnectionPointListener(com.aptana.ide.core.io.IConnectionPointListener)
     */
    public void removeConnectionPointListener(IConnectionPointListener listener) {
        notificationManager.removeListener(listener);
    }

	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
							.getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i], TAG_CONNECTION_POINT_CATEGORY);
		}
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i], TAG_CONNECTION_POINT_TYPE);
		}
	}
	
	private void readElement(IConfigurationElement element, String elementName) {
		if (!elementName.equals(element.getName())) {
			return;
		}
		if (TAG_CONNECTION_POINT_CATEGORY.equals(element.getName())) {
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0) {
				return;
			}

			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0) {
				return;
			}
			int order = Byte.MAX_VALUE;
			try {
				order = Integer.parseInt(element.getAttribute(ATT_ORDER));
			} catch (NumberFormatException e) {
			}
			categories.put(id, new ConnectionPointCategory(id, name, order));
		} else if (TAG_CONNECTION_POINT_TYPE.equals(element.getName())) {
			String typeId = element.getAttribute(ATT_ID);
			if (typeId == null || typeId.length() == 0) {
				return;
			}

			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0) {
				return;
			}

			String categoryId = element.getAttribute(ATT_CATEGORY);
			if (categoryId == null || categoryId.length() == 0) {
				categoryId = StringUtils.EMPTY;
			}

			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			configurationElements.put(typeId, element);
			
			ConnectionPointCategory category = categories.get(categoryId);
			if (category == null) {
				String defaultCategoryId = "unknown";
				category = categories.get(defaultCategoryId);
				if (category == null) {
					categories.put(defaultCategoryId, category =
								new ConnectionPointCategory(defaultCategoryId, "Unknown", Integer.MAX_VALUE));
				}
			}
			ConnectionPointType type = new ConnectionPointType(typeId, name, category);
            types.add(type);
			category.addType(type);
		}
	}

}
