package com.aptana.ide.internal.update.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.aptana.ide.update.Activator;
import com.aptana.ide.update.IPreferenceConstants;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginListener;

/**
 * A base class for plugin managers that performs the common tasks of managing listeners, grabbing the remote plugin
 * list.
 * 
 * @author cwilliams
 */
public abstract class AbstractPluginManager implements IPluginManager, IPreferenceChangeListener
{
	protected static final String FEATURE_IU_SUFFIX = ".feature.group"; //$NON-NLS-1$

	private static final String CACHED_PLUGINS_XML_FILENAME = "cached_plugins.xml"; //$NON-NLS-1$
	private static final int DAY = 1000 * 60 * 60 * 24;

	private final Set<PluginListener> listeners = new HashSet<PluginListener>();
	private String fgRemotePluginsURL;
	private long lastUpdated = -1;

	protected AbstractPluginManager()
	{
	    IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.PLUGIN_ID);
	    prefs.addPreferenceChangeListener(this);
	}

	public void addListener(PluginListener listener)
	{
		listeners.add(listener);
	}

	public Collection<PluginListener> getListeners()
	{
		return listeners;
	}

	public void removeListener(PluginListener pluginsListener)
	{
		listeners.remove(pluginsListener);
	}

	public List<Plugin> getRemotePlugins()
	{
		// returns the local cached copy of feed first, then schedules a job to
		// grab the latest feed from remote site
		List<Plugin> plugins = new ArrayList<Plugin>();
		try
		{
			InputStream xml = (InputStream) getLocalURL().getContent();
			plugins = parseXML(xml);
			// modifies the Plugin objects to use the local cached locations for
			// the images
			loadImages(plugins);
		}
		catch (IOException e)
		{
			plugins = new ArrayList<Plugin>();
		}

		if (haventUpdatedInADay())
		{
			scheduleLoadOfRemotePluginListing();
		}
		return plugins;
	}

	private void scheduleLoadOfRemotePluginListing()
	{
		Job job = new Job(Messages.PluginsManager_RemoteJobTitle)
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					InputStream in = (InputStream) getURL(getRemotePluginsURL()).getContent();
					// TODO Fix STU-2881 (partially) by iterating through the plugins and checking their update
					// sites for the feature id and then re-writing the correct plugin version in
					// cache the plugins_2.0.xml file
					saveCache(in);
					// caches the images referenced in the xml files
					cacheImages();
				}
				catch (IOException e)
				{
					error(e);
					return Status.CANCEL_STATUS;
				}
				finally
				{
					lastUpdated = System.currentTimeMillis();
				}

				// fires the corresponding event
				for (PluginListener listener : listeners)
				{
					listener.remotePluginsRefreshed();
				}
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.schedule(10000); // schedules a 10-second delay
	}

	protected String getRemotePluginsURL()
	{
		if (fgRemotePluginsURL == null)
		{
			// for testing purpose, the plugins.xml file location could be driven by a
			// command line flag
			String location = System.getProperty("PLUGINS_XML_LOCATION"); //$NON-NLS-1$
			if (location == null || location.length() == 0)
			{
				// Grab location from a pref!
                String defaultURL = (new DefaultScope()).getNode(
                        Activator.PLUGIN_ID).get(
                        IPreferenceConstants.REMOTE_PLUGIN_LISTING_URL, ""); //$NON-NLS-1$
                location = Platform.getPreferencesService().getString(
                        Activator.PLUGIN_ID,
                        IPreferenceConstants.REMOTE_PLUGIN_LISTING_URL,
                        defaultURL, null);
			}
			fgRemotePluginsURL = location;
		}
		return fgRemotePluginsURL;
	}

	/**
	 * Caches the remote images that plug-ins are referencing locally for faster loading.
	 */
	private void cacheImages()
	{
		// FIXME This seems like UI stuff that should not be in the plugin manager code!
		// first parses the cached xml file
		List<Plugin> plugins = new ArrayList<Plugin>();
		try
		{
			InputStream xml = (InputStream) getLocalCacheURL().getContent();
			plugins = parseXML(xml);
		}
		catch (IOException e)
		{
			plugins = new ArrayList<Plugin>();
		}

		ImageLoader loader = new ImageLoader();
		IPath directory = Activator.getDefault().getStateLocation();
		String imagePath;
		ImageDescriptor image;
		String id, ext;
		File newFile;
		Map<String, String> urlMap = new HashMap<String, String>();
		for (Plugin plugin : plugins)
		{
			imagePath = plugin.getImagePath();

			if (imagePath != null)
			{
				OutputStream out = null;
				try
				{
					// loads the image
					image = Activator.getImageDescriptor(imagePath);
					if (image == null)
					{
						image = ImageDescriptor.createFromURL(getURL(imagePath));
					}
					loader.data = new ImageData[1];
					loader.data[0] = image.getImageData();

					// caches the image locally
					id = plugin.getId();
					ext = getExtension(imagePath);
					newFile = directory.append(id + ext).toFile();
					newFile.createNewFile();
					out = new FileOutputStream(newFile);
					loader.save(out, getImageFormat(ext));

					// stores the mapping between the two paths
					urlMap.put(imagePath, newFile.toString());
				}
				catch (IOException e)
				{
					error(e);
				}
				finally
				{
					try
					{
						if (out != null)
							out.close();
					}
					catch (IOException e)
					{
						// ignore
					}
				}
			}
		}
		// saves the mapping
		saveImageURLMap(urlMap);
	}

	private static void saveImageURLMap(Map<String, String> map)
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.PLUGIN_ID);
		Iterator<String> iter = map.keySet().iterator();
		String key;
		while (iter.hasNext())
		{
			key = iter.next();
			prefs.put(key, map.get(key));
		}
	}

	private static String getExtension(String filename)
	{
		int index = filename.lastIndexOf("."); //$NON-NLS-1$
		return index < 0 ? "" : filename.substring(index); //$NON-NLS-1$
	}

	private static int getImageFormat(String extension)
	{
		if (extension.equals(".png")) { //$NON-NLS-1$ 
			return SWT.IMAGE_PNG;
		}
		if (extension.equals(".gif")) { //$NON-NLS-1$
			return SWT.IMAGE_GIF;
		}
		if (extension.equals(".bmp")) { //$NON-NLS-1$
			return SWT.IMAGE_BMP;
		}
		if (extension.equals(".jpg")) { //$NON-NLS-1$
			return SWT.IMAGE_JPEG;
		}
		return SWT.IMAGE_ICO;
	}

	private boolean haventUpdatedInADay()
	{
		return lastUpdated < System.currentTimeMillis() - DAY;
	}

	private URL getURL(String location) throws MalformedURLException
	{
		try
		{
			return new URL(location);
		}
		catch (MalformedURLException e)
		{
			return (new File(location)).toURI().toURL();
		}
	}

	private static List<Plugin> parseXML(InputStream xml)
	{
		try
		{
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			PluginsContentHandler handler = new PluginsContentHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(xml));
			// TODO Write out a copy of the contents to the local file if we
			// grabbed from remote URL?

			return handler.getPlugins();
		}
		catch (IOException e)
		{
			error(e);
		}
		catch (SAXException e)
		{
			error(e);
		}
		catch (ParserConfigurationException e)
		{
			error(e);
		}
		finally
		{
			if (xml != null)
			{
				try
				{
					xml.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		// some exception occurred; returns an empty list
		return new ArrayList<Plugin>();
	}

	private static void loadImages(List<Plugin> plugins)
	{
	    IPreferencesService service = Platform.getPreferencesService();
		String imagePath, cachedPath;
		for (Plugin plugin : plugins)
		{
			imagePath = plugin.getImagePath();
			if (imagePath != null)
			{
				// finds the local cached image location
				cachedPath = service.getString(Activator.PLUGIN_ID, imagePath, "", null); //$NON-NLS-1$
				if (cachedPath != null && cachedPath.length() > 0)
				{
					plugin.setImagePath(cachedPath);
				}
			}
		}
	}

	/**
	 * Returns the URL for local cached copy, or if that fails, then returns the original URL packaged in the plug-in.
	 * 
	 * @return the URL
	 * @throws MalformedURLException
	 */
	private URL getLocalURL() throws MalformedURLException
	{
		try
		{
			return getLocalCacheURL();
		}
		catch (MalformedURLException e)
		{
			return getOriginalFileURL();
		}
	}

	private URL getLocalCacheURL() throws MalformedURLException
	{
		return getLocalCacheFile().toURI().toURL();
	}

	private File getLocalCacheFile()
	{
		IPath statePath = Activator.getDefault().getStateLocation().append(getCacheFilename());
		File file = statePath.toFile();
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
				// caches the file
				copyOriginalToCache();
			}
			catch (IOException e)
			{
				error(e);
			}
		}
		return file;
	}

	private String getCacheFilename()
	{
		return CACHED_PLUGINS_XML_FILENAME;
	}

	private URL getOriginalFileURL() throws MalformedURLException
	{
        String defaultURL = (new DefaultScope()).getNode(Activator.PLUGIN_ID)
                .get(IPreferenceConstants.LOCAL_PLUGIN_LISTING_URL, ""); //$NON-NLS-1$
        return new URL(Platform.getPreferencesService().getString(
                Activator.PLUGIN_ID,
                IPreferenceConstants.LOCAL_PLUGIN_LISTING_URL, defaultURL, null));
	}

	/**
	 * Copy the contents of the original local XML feed over to the cached file in the plugin's state location.
	 * 
	 * @param file
	 */
	private void copyOriginalToCache()
	{
		try
		{
			InputStream in = (InputStream) getOriginalFileURL().getContent();
			saveCache(in);
		}
		catch (IOException e)
		{
			error(e);
		}
	}

	private static void error(Exception e)
	{
		Activator.log(IStatus.ERROR, e.getMessage(), e);
	}

	/**
	 * Copy contents from an InputStream to the local cache file.
	 * 
	 * @param xml
	 *            the input stream
	 */
	private void saveCache(InputStream xml)
	{
		// FIXME: Copy using byte array buffers to speed things up, not byte by
		// byte like we do here
		File file = getLocalCacheFile();
		OutputStream writer = null;
		try
		{
			writer = new FileOutputStream(file);
			int b = -1;
			while ((b = xml.read()) != -1)
			{
				writer.write(b);
			}
		}
		catch (FileNotFoundException e)
		{
			error(e);
		}
		catch (IOException e)
		{
			error(e);
		}
		finally
		{
			try
			{
				if (xml != null)
					xml.close();
			}
			catch (IOException e)
			{
				// ignore
			}
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

    public void preferenceChange(PreferenceChangeEvent event) {
        String key = event.getKey();
        if (IPreferenceConstants.REMOTE_PLUGIN_LISTING_URL.equals(key))
        {
            fgRemotePluginsURL = null;
            scheduleLoadOfRemotePluginListing();
        }
        else if (IPreferenceConstants.LOCAL_PLUGIN_LISTING_URL.equals(key))
        {
            File localCache = getLocalCacheFile();
            if (localCache != null)
                localCache.delete();
        }
    }
}
