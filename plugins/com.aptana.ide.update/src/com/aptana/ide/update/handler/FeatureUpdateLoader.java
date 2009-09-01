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
package com.aptana.ide.update.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.IInstallConfiguration;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.SiteManager;

import com.aptana.ide.update.Activator;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FeatureUpdateLoader
{

	private static FeatureUpdateLoader instance;

	private Map<String, IFeatureUpdateHandler> ids;

	private FeatureUpdateLoader()
	{
		ids = new HashMap<String, IFeatureUpdateHandler>();
		loadExtensionPoints();
	}

	private void loadExtensionPoints()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(Activator.FEATURE_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				String id = ce[j].getAttribute("id"); //$NON-NLS-1$
				String handler = ce[j].getAttribute("handler"); //$NON-NLS-1$
				if (id != null)
				{
					IFeatureUpdateHandler handlerObject = null;
					if (handler != null)
					{
						try
						{
							Object createdHandler = ce[j].createExecutableExtension("handler"); //$NON-NLS-1$
							if (createdHandler instanceof IFeatureUpdateHandler)
							{
								handlerObject = (IFeatureUpdateHandler) createdHandler;
							}
						}
						catch (CoreException e)
						{
							// Do nothing and move on to next ext. point
						}
					}
					ids.put(id, handlerObject);
				}
			}
		}
	}

	/**
	 * Gets a collection of String feature ids
	 * 
	 * @return - string ids of features
	 */
	public Collection<String> getFeatureIds()
	{
		return ids.keySet();
	}

	/**
	 * Gets the handler for a feature id or null if none specified
	 * 
	 * @param featureId
	 * @return - handler or null if none registered via extension point
	 */
	public IFeatureUpdateHandler getHandler(String featureId)
	{
		return ids.get(featureId);
	}

	/**
	 * Gets the instance of the feature update loader
	 * 
	 * @return the instance
	 */
	public static FeatureUpdateLoader getInstance()
	{
		if (instance == null)
		{
			instance = new FeatureUpdateLoader();
		}
		return instance;
	}

	/**
	 * Gets the features that should be updated as part of the ide
	 * 
	 * @return array of ifeatures
	 */
	public static IFeature[] getFeaturesToUpdate()
	{
		IFeature[] features = getFeature("com.aptana.ide.feature.rcp", false); //$NON-NLS-1$

		// No rcp feature found, so we'll assume we are in the regular Eclipse ID
		// and search for the base feature.
		if (features.length == 0)
		{
			features = getFeature("com.aptana.ide.feature", false); //$NON-NLS-1$
		}

		try
		{
			features = addFeatureToUpdate(features, "com.aptana.ide.framework.iphone"); //$NON-NLS-1$
			features = addFeatureToUpdate(features, "com.aptana.ide.feature.framework.air"); //$NON-NLS-1$
			features = addFeatureToUpdate(features, "com.aptana.ide.feature.editor.php"); //$NON-NLS-1$
			//features = addFeatureToUpdate(features, "com.aptana.ide.feature.professional"); //$NON-NLS-1$
			features = addFeatureToUpdate(features, "org.radrails.rails_feature"); //$NON-NLS-1$
			Collection<String> others = FeatureUpdateLoader.getInstance().getFeatureIds();
			Iterator<String> iter = others.iterator();
			while (iter.hasNext())
			{
				String featureId = iter.next();
				IFeatureUpdateHandler handler = FeatureUpdateLoader.getInstance().getHandler(featureId);
				boolean add = true;
				if (handler != null)
				{
					add = handler.shouldUpdate();
				}
				if (add)
				{
					features = addFeatureToUpdate(features, featureId);
				}
			}
		}
		catch (Exception ex)
		{
			Activator.log(IStatus.ERROR, ex.getMessage(), ex);
			features = new IFeature[0];
		}
		return features;
	}

	/**
	 * Adds a new feature to the list of features to check as "Aptana updates"
	 * 
	 * @param features
	 * @param featureId
	 * @return - updated features to update
	 */
	private static IFeature[] addFeatureToUpdate(IFeature[] features, String featureId)
	{
		IFeature[] ifeature = getFeature(featureId, false);

		if (ifeature.length > 0)
		{
			Set<IFeature> v = new HashSet<IFeature>();
			for (int i = 0; i < features.length; i++)
			{
				v.add(features[i]);
			}

			for (int j = 0; j < ifeature.length; j++)
			{
				v.add(ifeature[j]);
			}

			features = v.toArray(new IFeature[v.size()]);
		}
		return features;
	}

    /**
     * @param id
     *            The id of the feature
     * @param onlyConfigured
     *            Is it configured
     * @return IFeature[] with features matching feature ID
     */
    private static IFeature[] getFeature(String id, boolean onlyConfigured)
    {
        List<IFeature> features = new ArrayList<IFeature>();
        try
        {
            ILocalSite localSite = SiteManager.getLocalSite();
            IInstallConfiguration config = localSite.getCurrentConfiguration();
            IConfiguredSite[] isites = config.getConfiguredSites();

            for (IConfiguredSite isite : isites)
            {
                IFeature[] result = searchSite(id, isite, onlyConfigured);
                for (IFeature installedFeature : result)
                {
                    features.add(installedFeature);
                }
            }
        }
        catch (CoreException e)
        {
            Activator.log(IStatus.ERROR, e.toString(), null);
        }
        return features.toArray(new IFeature[features.size()]);
    }

    private static IFeature[] searchSite(String featureId, IConfiguredSite site, boolean onlyConfigured)
            throws CoreException
    {
        IFeatureReference[] references = null;

        if (onlyConfigured)
        {
            references = site.getConfiguredFeatures();
        }
        else
        {
            references = site.getSite().getFeatureReferences();
        }

        List<IFeature> result = new ArrayList<IFeature>();
        for (int i = 0; i < references.length; i++)
        {
            IFeature feature = references[i].getFeature(null);
            String id = feature.getVersionedIdentifier().getIdentifier();
            if (featureId.equals(id))
            {
                result.add(feature);
            }
        }
        return result.toArray(new IFeature[result.size()]);
    }
}
