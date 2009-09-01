/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.intro.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.update.FeatureUtil;

public class FeatureRegistry
{

//	private static List<ProFeatureDescriptor> fgProFeatures;
	private static List<FeatureDescriptor> fgFeatures;

	private FeatureRegistry()
	{
	}

//	public static List<ProFeatureDescriptor> getProFeatures()
//	{
//		if (fgProFeatures == null)
//		{
//			List<ProFeatureDescriptor> list = new ArrayList<ProFeatureDescriptor>();
//			IExtensionRegistry registry = Platform.getExtensionRegistry();
//			IConfigurationElement[] elements = registry.getConfigurationElementsFor(IntroPlugin.PLUGIN_ID,
//					"proFeatures"); //$NON-NLS-1$
//			for (int i = 0; i < elements.length; i++)
//			{
//				String featureId = elements[i].getAttribute("id"); //$NON-NLS-1$
//				String name = elements[i].getAttribute("label"); //$NON-NLS-1$
//				String url = elements[i].getAttribute("url"); //$NON-NLS-1$
//				list.add(new ProFeatureDescriptor(featureId, name, url, null));
//			}
//			fgProFeatures = list;
//		}
//		return fgProFeatures;
//	}
	
	public static List<FeatureDescriptor> getRequiredFeatures()
	{
		if (fgFeatures == null)
		{
			List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(IntroPlugin.PLUGIN_ID,
					"requiredFeatures"); //$NON-NLS-1$
			for (int i = 0; i < elements.length; i++)
			{
				String featureId = elements[i].getAttribute("id"); //$NON-NLS-1$
				String name = elements[i].getAttribute("label"); //$NON-NLS-1$
				String url = elements[i].getAttribute("url"); //$NON-NLS-1$
				IConfigurationElement[] children = elements[i].getChildren("conflictsWith"); //$NON-NLS-1$
				List<String> conflicts = new ArrayList<String>();
				for (int j = 0; j < children.length; j++)
				{
					IConfigurationElement configurationElement = children[j];
					String con = configurationElement.getAttribute("featureId"); //$NON-NLS-1$
					conflicts.add(con);
				}
				list.add(new FeatureDescriptor(featureId, name, url, conflicts.toArray(new String[0])));
			}
			fgFeatures = list;
		}
		return fgFeatures;
	}
	
	/**
	 * Returns a list of all features that are listed as "required" but are uninstalled
	 * @return
	 */
	public static List<FeatureDescriptor> gatherUninstalledRequiredFeatures()
	{
		List<FeatureDescriptor> uninstalledFeatures = new ArrayList<FeatureDescriptor>();
		
		List<? extends FeatureDescriptor> features = FeatureRegistry.getRequiredFeatures();
		for (FeatureDescriptor descriptor : features)
		{
			if (!FeatureUtil.isInstalled(descriptor.getId()))
			{
				uninstalledFeatures.add(descriptor);
			}
		}
		
		return uninstalledFeatures;
	}
	
	/**
	 * Returns a list of all features that are listed as "required" and are installed
	 * @return
	 */
	public static List<FeatureDescriptor> gatherInstalledRequiredFeatures()
	{
		List<FeatureDescriptor> installedFeatures = new ArrayList<FeatureDescriptor>();
		
		List<? extends FeatureDescriptor> features = FeatureRegistry.getRequiredFeatures();
		for (FeatureDescriptor descriptor : features)
		{
			if (FeatureUtil.isInstalled(descriptor.getId()))
			{
				installedFeatures.add(descriptor);
			}
		}
		
		return installedFeatures;
	}
	
//	/**
//	 * Returns a list of all pro features that are uninstalled
//	 * @return
//	 */
//	public static List<FeatureDescriptor> gatherUninstalledProFeatures()
//	{
//		List<FeatureDescriptor> uninstalledFeatures = new ArrayList<FeatureDescriptor>();
//		
//		List<? extends FeatureDescriptor> features = FeatureRegistry.getProFeatures();
//		for (FeatureDescriptor descriptor : features)
//		{
//			if (!FeatureUtil.isInstalled(descriptor.getId()))
//			{
//				uninstalledFeatures.add(descriptor);
//			}
//		}
//		return uninstalledFeatures;
//	}
	
	/**
	 * Has the user chosen to ignore the following feature?
	 * @param featureId
	 */
	public static boolean isFeatureIgnored(String featureId, String[] featureList)
	{
		if(featureList == null)
		{
			return false;
		}
		
		for (int i = 0; i < featureList.length; i++)
		{
			String string = featureList[i];
			if(string.equals(featureId))
			{
				return true;
			}
		}
		
		return false;
	}	
	
	/**
	 * Does the requested feature conflict with a currently installed feature
	 * @param featureId The feature to check
	 * @param installedFeatureList The list of features to check against
	 */
	public static boolean doesFeatureConflict(FeatureDescriptor featureId, String[] installedFeatureList)
	{
		List<String> installed = Arrays.asList(installedFeatureList);
		String[] conflicts = featureId.getConflicts();
		if(conflicts == null)
		{
			return false;
		}
		
		for (int i = 0; i < conflicts.length; i++)
		{
			String string = conflicts[i];
			if(installed.contains(string))
			{
				return true;
			}
			// check plugins too
			Bundle bundle = Platform.getBundle(string);
			if (bundle != null)
			{
				return true;
			}
		}		
		return false;
	}
}
