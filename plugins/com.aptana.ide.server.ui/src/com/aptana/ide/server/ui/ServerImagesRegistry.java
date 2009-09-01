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
package com.aptana.ide.server.ui;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IModuleType;
import com.aptana.ide.server.core.IPublishOperation;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;

/**
 * @author Pavel Petrochenko
 */
public final class ServerImagesRegistry
{

	private ServerImagesRegistry()
	{
		load();
	}

	private static ServerImagesRegistry instance;

	/**
	 * @return
	 */
	public static ServerImagesRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new ServerImagesRegistry();
		}
		return instance;
	}

	/**
	 * @author Pavel Petrochenko
	 */
	static class Element
	{
		final IConfigurationElement item;
		private Image image;

		/**
		 * @param item
		 */
		public Element(IConfigurationElement item)
		{
			super();
			this.item = item;
		}

		/**
		 * @return
		 */
		public String getId()
		{
			return item.getAttribute("typeId"); //$NON-NLS-1$
		}

		/**
		 * @return image
		 */
		public ImageDescriptor getImageDescriptor()
		{
			return AbstractUIPlugin
					.imageDescriptorFromPlugin(item.getNamespaceIdentifier(), item.getAttribute("image")); //$NON-NLS-1$
		}

		/**
		 * @return
		 */
		public Image getImage()
		{
			if ((image == null || image.isDisposed()) && getImageDescriptor() != null)
			{
				image = getImageDescriptor().createImage();
			}
			return image;
		}
	}

	private HashMap<String, Element> serverImages = new HashMap<String, Element>();
	private HashMap<String, Element> moduleImages = new HashMap<String, Element>();
	private HashMap<String, Element> serverTypeImages = new HashMap<String, Element>();
	private HashMap<String, Element> operationImages = new HashMap<String, Element>();

	void onDispose()
	{
		disposeMap(serverImages);
		disposeMap(moduleImages);
		disposeMap(serverTypeImages);
		disposeMap(operationImages);
	}

	private void disposeMap(HashMap<String, Element> serverImages2)
	{
		for (Iterator<Element> elementIt = serverImages2.values().iterator(); elementIt.hasNext();)
		{
			Element name = elementIt.next();
			if (name.image != null && !name.image.isDisposed())
			{
				name.image.dispose();
			}
		}
	}

	void load()
	{
		IConfigurationElement[] configurationElementsFor = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"com.aptana.ide.server.ui.serverImages"); //$NON-NLS-1$
		for (int a = 0; a < configurationElementsFor.length; a++)
		{
			String name = configurationElementsFor[a].getName();
			Element element = new Element(configurationElementsFor[a]);
			if (name.equals("serverImage")) { //$NON-NLS-1$
				serverImages.put(element.getId(), element);
			}
			if (name.equals("moduleImage")) { //$NON-NLS-1$
				moduleImages.put(element.getId(), element);
			}
			if (name.equals("serverTypeImage")) { //$NON-NLS-1$
				serverTypeImages.put(element.getId(), element);
			}
			if (name.equals("publishOperationImage")) { //$NON-NLS-1$
				operationImages.put(element.getId(), element);
			}
		}
	}

	/**
	 * @param element
	 * @return
	 */
	public ImageDescriptor getDescriptor(Object element)
	{
		if (element instanceof IPublishOperation)
		{
			IPublishOperation pop = (IPublishOperation) element;
			Element element2 = operationImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImageDescriptor();
		}
		if (element instanceof IServerType)
		{
			IServerType pop = (IServerType) element;
			Element element2 = serverTypeImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImageDescriptor();
		}
		if (element instanceof IModuleType)
		{
			IModuleType pop = (IModuleType) element;
			Element element2 = moduleImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImageDescriptor();
		}
		if (element instanceof IModule)
		{
			IModule pop = (IModule) element;
			return getDescriptor(pop.getType());
		}
		if (element instanceof IServer)
		{
			IServer pop = (IServer) element;
			Element element2 = serverImages.get(pop.getServerType().getId());
			if (element2 == null)
			{
				return getDescriptor(pop.getServerType());
			}
			return element2.getImageDescriptor();
		}
		return null;
	}

	/**
	 * @param element
	 * @return
	 */
	public Image getImage(Object element)
	{
		if (element instanceof IPublishOperation)
		{
			IPublishOperation pop = (IPublishOperation) element;
			Element element2 = operationImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImage();
		}
		if (element instanceof IServerType)
		{
			IServerType pop = (IServerType) element;
			Element element2 = serverTypeImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImage();
		}
		if (element instanceof IModuleType)
		{
			IModuleType pop = (IModuleType) element;
			Element element2 = moduleImages.get(pop.getId());
			if (element2 == null)
			{
				return null;
			}
			return element2.getImage();
		}
		if (element instanceof IModule)
		{
			IModule pop = (IModule) element;
			return getImage(pop.getType());
		}
		if (element instanceof IServer)
		{
			IServer pop = (IServer) element;
			Element element2 = serverImages.get(pop.getServerType().getId());
			if (element2 == null)
			{
				return getImage(pop.getServerType());
			}
			return element2.getImage();
		}
		return null;
	}
}
