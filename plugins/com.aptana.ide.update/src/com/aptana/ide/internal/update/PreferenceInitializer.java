package com.aptana.ide.internal.update;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ide.update.Activator;
import com.aptana.ide.update.IPreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	private static final String DEFAULT_LOCAL_PLUGIN_LISTING_PATH = "plugins2.0.xml"; //$NON-NLS-1$
	private static final String DEFAULT_PLUGINS_XML_URL = "http://ide.aptana.com/content_ide/plugins2.0.xml"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences()
	{
        IEclipsePreferences prefs = (new DefaultScope())
                .getNode(Activator.PLUGIN_ID);
        prefs.put(IPreferenceConstants.REMOTE_PLUGIN_LISTING_URL,
                DEFAULT_PLUGINS_XML_URL);
		URL defaultLocalListingURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(
				DEFAULT_LOCAL_PLUGIN_LISTING_PATH),
				null);
		if (defaultLocalListingURL != null)
		{
            prefs.put(IPreferenceConstants.LOCAL_PLUGIN_LISTING_URL,
                    defaultLocalListingURL.toString());
		}

	}

}
