/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.eclipsemonkey.language.IMonkeyLanguageFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class EclipseMonkeyPlugin extends AbstractUIPlugin implements IStartup {
	
	/**
	 * Marker indicating the start of an Aptana Scripting script
	 */
	public static final String PUBLISH_BEFORE_MARKER = "--- Came wiffling through the eclipsey wood ---"; //$NON-NLS-1$

	/**
	 * Marker indicating the end of an Aptana Scripting script
	 */
	public static final String PUBLISH_AFTER_MARKER = "--- And burbled as it ran! ---"; //$NON-NLS-1$

	// The shared instance.
	private static EclipseMonkeyPlugin plugin;

	private static Map<String, StoredScript> _scriptStore = new ConcurrentHashMap<String, StoredScript>();
	private static Set<IScriptStoreListener> _storeListeners = new HashSet<IScriptStoreListener>();
	private static Map<String, IMonkeyLanguageFactory> _languageStore = new ConcurrentHashMap<String, IMonkeyLanguageFactory>();
	private static Map<String, Object> _scopeStore = new ConcurrentHashMap<String, Object>();
	
	/**
	 * 
	 *
	 */
	public EclipseMonkeyPlugin() {
		plugin = this;
	}
	
	/**
	 * All loaded languages
	 * @return a map of loaded languages
	 */
	public Map<String, IMonkeyLanguageFactory> getLanguageStore()
	{
		return _languageStore;
	}
	
	/**
	 * All loaded scripts
	 * @return a map of loaded scripts
	 */
	public Map<String, StoredScript> getScriptStore() {
		return _scriptStore;
	}

	/**
	 * All loaded scopes
	 * @return a map of loaded scopes
	 */
	public Map<String, Object> getScopeStore() {
		return _scopeStore;
	}

	/**
	 * This method is called upon plug-in activation
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * @return EclipseMonkeyPlugin
	 */
	public static EclipseMonkeyPlugin getDefault() {
		return plugin;
	}

	/**
	 * @param path
	 * @return ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.eclipse.eclipsemonkey", path); //$NON-NLS-1$
	}
	private UpdateMonkeyActionsResourceChangeListener listener;
	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		
		EclipseMonkeyPlugin eclipseMonkeyPlugin = EclipseMonkeyPlugin.getDefault();
		if ( eclipseMonkeyPlugin == null ){
			return;
		}
		listener = new UpdateMonkeyActionsResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		loadScripts();

		runStartupScripts();
	}

	private void loadScripts() {
		String[] extensions = loadLanguageSupport();
		String[] alternateScriptPaths = findAlternateScriptPaths();
		listener.rescanAllFiles(extensions, alternateScriptPaths);
		UpdateMonkeyActionsResourceChangeListener.setExtensions(extensions);
		UpdateMonkeyActionsResourceChangeListener.createTheMonkeyMenu();
	}

	/**
	 * @param name
	 * @param script
	 */
	public void addScript(String name, StoredScript script) {
		/* we are using the full file path as the key into the store
		 * the consequence is that renames or moves are considered deletes and adds
		 * is this what we want?
		 */
		Map<String, StoredScript> store = getScriptStore();
		StoredScript oldScript = store.get(name);
		if (oldScript != null) {
			oldScript.metadata.unsubscribe();
		}
		store.put(name, script);
		script.metadata.subscribe();
		this.notifyScriptsChanged();
	}

	/**
	 * @param name
	 */
	public void removeScript(String name) {
		Map<String, StoredScript> store = getScriptStore();
		StoredScript oldScript = store.remove(name);
		if (oldScript == null) return;
		oldScript.metadata.unsubscribe();
		this.notifyScriptsChanged();
	}

	/**
	 * 
	 */
	public void clearScripts() {
		for (Iterator<StoredScript> iter = getScriptStore().values().iterator(); iter.hasNext();) {
			StoredScript script = iter.next();
			script.metadata.unsubscribe();
		}
		getScriptStore().clear();
		this.notifyScriptsChanged();
	}

	/**
	 * 
	 */
	public void notifyScriptsChanged() {
		for (Iterator<IScriptStoreListener> iter = _storeListeners.iterator(); iter.hasNext();) {
			IScriptStoreListener element = iter.next();
			element.storeChanged();
		}
	}

	/**
	 * @param listener
	 */
	public void addScriptStoreListener( IScriptStoreListener listener ) {
		_storeListeners.add(listener);
	}
	/**
	 * @param listener
	 */
	public void removeScriptStoreListener( IScriptStoreListener listener ) {
		_storeListeners.remove(listener);
	}

	/**
	 * runStartupScripts
	 */
	private void runStartupScripts() 
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				for (Iterator<StoredScript> iter = getDefault().getScriptStore().values().iterator(); iter.hasNext();) 
				{
					StoredScript script = iter.next();
					String onLoadFunction = script.metadata.getOnLoadFunction();
					if(onLoadFunction != null)
					{
						MenuRunMonkeyScript runner = new MenuRunMonkeyScript(script.scriptPath);
						try {
							runner.run(onLoadFunction, new Object[0]);
						} catch (RunMonkeyException e) {
							// Do nothing
						}
					}
				}
			}
		});
	}
	private  IIdentifierListener identifierListener = new IIdentifierListener() {
		public void identifierChanged(IdentifierEvent identifierEvent) {
			if ( identifierEvent.hasEnabledChanged() ) {
				IIdentifier identifier = identifierEvent.getIdentifier();
					loadScripts();
			}
		}
	};
	private static final IActivityManager activityManager  = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();
	private  final boolean isActivityEnabled(IConfigurationElement element){
		
		String extensionId = element.getAttribute("id"); //$NON-NLS-1$
		String extensionPluginId = element.getNamespaceIdentifier();
		String extensionString = null;
		if (extensionPluginId != null && extensionId != null
				&& extensionPluginId.length() > 0
				&& extensionId.length() > 0) {
			 extensionString = extensionPluginId + "/" + extensionId; //$NON-NLS-1$
		}
		if ( extensionString != null) {
			final IIdentifier id = activityManager.getIdentifier(extensionString);
			if ( id != null ){
				id.addIdentifierListener(identifierListener);
				return id.isEnabled();
			}
		}
		return true;
	}
	

	/**
	 * findAlternateScriptPaths
	 * 
	 * @return List of alternate paths to use to find scripts
	 */
	private String[] findAlternateScriptPaths()
	{
		List<String> list = new ArrayList<String>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("org.eclipse.eclipsemonkey.scriptpath"); //$NON-NLS-1$
		
		if (point != null) 
		{
			IExtension[] extensions = point.getExtensions();
			
			for (int i = 0; i < extensions.length; i++) 
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] configurations = extension.getConfigurationElements();
				
				for (int j = 0; j < configurations.length; j++) 
				{
					IConfigurationElement element = configurations[j];
					
					if ( !isActivityEnabled(element)){
						continue;
					}
					try 
					{
						IExtension declaring = element.getDeclaringExtension();
						
//						String declaringPluginID = declaring
//								.getDeclaringPluginDescriptor()
//								.getUniqueIdentifier();
						
						String declaringPluginID = declaring.getNamespaceIdentifier();
						
						String fullPath = element.getAttribute("directory"); //$NON-NLS-1$

						Bundle b = Platform.getBundle(declaringPluginID);
						
						URL url = Platform.find(b, new Path(fullPath));
						
						if(url != null)
						{
							try {
							
								URL localUrl = Platform.asLocalURL(url);
								
								if(localUrl != null)
								{
									String filename = localUrl.getFile();
									list.add(filename);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} 
					catch (InvalidRegistryObjectException x) 
					{
						// ignore bad extensions
					} 
				}
			}
		}

		return list.toArray(new String[0]);
	}
	
	/**
	 * loadLanguageSupport
	 * @return String[]
	 */
	private String[] loadLanguageSupport() 
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("org.eclipse.eclipsemonkey.language"); //$NON-NLS-1$
			
		if (point != null) 
		{
			IExtension[] extensions = point.getExtensions();
			
			for (int i = 0; i < extensions.length; i++) 
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] configurations = extension.getConfigurationElements();
				
				for (int j = 0; j < configurations.length; j++) 
				{
					IConfigurationElement element = configurations[j];
					try 
					{
						if ( !isActivityEnabled(element) ){
							continue;
						}
						IExtension declaring = element.getDeclaringExtension();
						
//						String declaring_plugin_id = declaring
//								.getDeclaringPluginDescriptor()
//								.getUniqueIdentifier();
						
						String declaringPluginID = declaring.getNamespaceIdentifier();
						
						String languageName = element.getAttribute("languageName"); //$NON-NLS-1$
						String languageExtension = element.getAttribute("languageExtension"); //$NON-NLS-1$
						String[] languageExtensions = null;

						if(languageExtension != null)
						{
							languageExtensions = languageExtension.split("\\,"); //$NON-NLS-1$
						
							Object object = element.createExecutableExtension("class"); //$NON-NLS-1$

							IMonkeyLanguageFactory langFactory = (IMonkeyLanguageFactory) object;

							for (int k = 0; k < languageExtensions.length; k++) 
							{
								EclipseMonkeyPlugin.getDefault().getLanguageStore().put(languageExtensions[k], langFactory);
							}

							langFactory.init(declaringPluginID, languageName);
						}
					} 
					catch (InvalidRegistryObjectException x) 
					{
						// ignore bad extensions
					} 
					catch (CoreException x) 
					{
						// ignore bad extensions
					}
				}
			}
		}

		String[] extensions = (String []) EclipseMonkeyPlugin.getDefault().getLanguageStore().keySet().toArray(new String[0]);
		
		if(extensions == null)
		{
			return new String[0];
		}
		else
		{
			return extensions;
		}
	}
	
}
