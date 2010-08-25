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
package com.aptana.ide.editors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.views.actions.ActionsManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class UnifiedEditorsPlugin extends AbstractUIPlugin {

	private static final String TEMPLATES = "com.aptana.ide.editors.templates"; //$NON-NLS-1$

	private static Hashtable<String, Image> images = new Hashtable<String, Image>();
	private static UnifiedEditorsPlugin plugin;
	private boolean enableThreading = true;
	ProfileManager profileManager;
	ActionsManager actionsManager;

	private HashMap<ContextTypeRegistry, ContributionTemplateStore> fTemplateStoreMap;

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.editors"; //$NON-NLS-1$

	/**
	 * BROWSER_ELEMENT
	 */
	public static final String BROWSER_ELEMENT = "browser";//$NON-NLS-1$

	/**
	 * DECORATOR_ELEMENT
	 */
	public static final String DECORATOR_ELEMENT = "decorator";//$NON-NLS-1$

	/**
	 * CLASS_ATTR
	 */
	public static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	/**
	 * OUTLINE_ATTR
	 */
	public static final String OUTLINE_ATTR = "outline"; //$NON-NLS-1$

	/**
	 * LABEL_NODE
	 */
	public static final String LABEL_NODE = "label"; //$NON-NLS-1$

	/**
	 * OS_ATTR
	 */
	public static final String OS_ATTR = "os"; //$NON-NLS-1$

	/**
	 * VALUE_ATTR
	 */
	public static final String VALUE_ATTR = "value"; //$NON-NLS-1$

	/**
	 * BROWSER_EXTENSION_POINT
	 */
	public static final String BROWSER_EXTENSION_POINT = "com.aptana.ide.editors.browser"; //$NON-NLS-1$

	/**
	 * COLORIZATION_EXTENSION_POINT
	 */
	public static final String COLORIZATION_EXTENSION_POINT = "com.aptana.ide.editors.colorization"; //$NON-NLS-1$

	/**
	 * FOLDING_EXTENSION_POINT
	 */
	public static final String FOLDING_EXTENSION_POINT = "com.aptana.ide.editors.folding"; //$NON-NLS-1$

	/**
	 * CONTENT_ASSISTANT_EXTENSION_POINT
	 */
	public static final String CONTENT_ASSISTANT_EXTENSION_POINT = "com.aptana.ide.editors.contentassistant"; //$NON-NLS-1$

	/**
	 * Returns the shared instance.
	 * 
	 * @return returns default plugin
	 */
	public static UnifiedEditorsPlugin getDefault() {
		return plugin;
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return Image
	 */
	public static Image getImage(String path) {
		if (images.get(path) == null) {
			ImageDescriptor id = getImageDescriptor(path);

			if (id == null) {
				return null;
			}

			Image i = id.createImage();

			images.put(path, i);

			return i;
		} else {
			return images.get(path);
		}
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"com.aptana.ide.editors", path); //$NON-NLS-1$
	}

	/**
	 * The constructor.
	 */
	public UnifiedEditorsPlugin() {
		plugin = this;
	}

	/**
	 * getActionsManager
	 * 
	 * @return ActionsManager
	 */
	public ActionsManager getActionsManager() {
		if (actionsManager == null) {
			actionsManager = new ActionsManager();
		}

		return actionsManager;
	}

	/**
	 * getProfileManager
	 * 
	 * @return ProfileManager
	 */
	public ProfileManager getProfileManager() {
		if (profileManager == null) {
			profileManager = new ProfileManager(enableThreading);
		}

		return profileManager;
	}

	/**
	 * @return Returns true if threading is enabled (true by default).
	 */
	public boolean isThreadingEnabled() {
		return enableThreading;
	}

	/**
	 * @param enableThreading
	 *            Turns threading on or off.
	 */
	public void setEnableThreading(boolean enableThreading) {
		this.enableThreading = enableThreading;
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);

		plugin = null;
	}

	/**
	 * @return the flag indicating if parsing should be done in two-phases or
	 *         one. A fast scan is done on the UI and the full parse is done on
	 *         a delay resulting in a faster perceived response time during
	 *         editing.
	 */
	public boolean useFastScan() {
		boolean result = true;

		try {
			IPreferenceStore prefStore = this.getPreferenceStore();

			result = prefStore.getBoolean(IPreferenceConstants.PARSER_OFF_UI);
		} catch (Exception e) {
			// this seems to occur during unit tests only
		}

		return result;
	}

	/**
	 * @param contextTypeRegistry
	 * @return
	 */
	public ContributionTemplateStore getTemplateStore(
			ContextTypeRegistry contextTypeRegistry) {
		fTemplateStoreMap = new HashMap<ContextTypeRegistry, ContributionTemplateStore>();
		ContributionTemplateStore store = fTemplateStoreMap
				.get(contextTypeRegistry);
		if (store == null) {
			store = new ContributionTemplateStore(contextTypeRegistry,
					UnifiedEditorsPlugin.getDefault().getPreferenceStore(),
					getKey(contextTypeRegistry));
			try {
				store.load();
			} catch (IOException e) {
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e
						.getMessage(), e);
			}
			fTemplateStoreMap.put(contextTypeRegistry, store);
		}
		return store;
	}

	/**
	 * getKey
	 * 
	 * @param contextTypeRegistry
	 * @return
	 */
	protected String getKey(ContextTypeRegistry contextTypeRegistry) {
		Iterator<?> i = contextTypeRegistry.contextTypes();
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		builder.append(TEMPLATES).append("{");

		while (i.hasNext()) {
			TemplateContextType contextType = (TemplateContextType) i.next();

			if (first) {
				first = false;
			} else {
				builder.append(",");
			}

			builder.append(contextType.getName());
		}

		builder.append("}");

		return builder.toString();
	}
}
