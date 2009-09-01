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
package com.aptana.ide.server.jetty.portal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.server.jetty.preferences.IPreferenceConstants;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PortalServiceLoader
{

	/**
	 * Listens to receive notification on the portal-related events.
	 */
	public static interface IPortalListener
	{
		/**
		 * Indicates a new portal is downloaded from the server.
		 * 
		 * @param newVersion
		 *            the version of the new portal
		 */
		public void newPortalDownloaded(String newVersion);
	}

	/**
	 * ID_ATTRIBUTE
	 */
	public static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	/**
	 * RESOURCE_ATTRIBUTE
	 */
	public static final String RESOURCE_ATTRIBUTE = "resource"; //$NON-NLS-1$

	/**
	 * PORTLET_ELEMENT
	 */
	public static final String PORTLET_ELEMENT = "service"; //$NON-NLS-1$

	/**
	 * PORTLET_EXTENSION
	 */
	public static final String PORTLET_EXTENSION = JettyPlugin.PLUGIN_ID + ".portalService"; //$NON-NLS-1$

	private static final String PORTAL_URL;
	static
	{
		String serverURL = System.getProperty("SERVER_URL"); //$NON-NLS-1$
		if (serverURL == null || serverURL.length() == 0)
		{
			serverURL = "http://ide.aptana.com"; //$NON-NLS-1$
		}
		PORTAL_URL = serverURL + "/portal"; //$NON-NLS-1$
	}

	private static final String PORTAL_XML = "portal.xml"; //$NON-NLS-1$
	private static final String PORTAL_SUFFIX = "portal."; //$NON-NLS-1$
	private static final String LOCAL_INSTALL_JAR = "from local installation"; //$NON-NLS-1$

	private static PortalServiceLoader loader;

	private List<PortalService> portlets;

	private String portalJar;
	private String currentPortalVersion;
	private AtomicBoolean isDownloading = new AtomicBoolean();
	private List<IPortalListener> listeners;

	// the handler to parse the portal.xml file
	private DefaultHandler xmlHandler = new DefaultHandler()
	{

		private static final String PORTAL = "portal"; //$NON-NLS-1$
		private static final String VERSION = "studio-version"; //$NON-NLS-1$
		private static final String JAR = "jar"; //$NON-NLS-1$
		private static final String CHECKSUM = "checksum"; //$NON-NLS-1$

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.equals(PORTAL))
			{
				String versionStr = attributes.getValue(VERSION);
				VersionRange versionRange = new VersionRange(versionStr);
				if (versionRange.isIncluded(new Version(getCoreVersion())))
				{
					String jar = attributes.getValue(JAR);
					File configDir = CoreUIUtils.getConfigurationDirectory();
					String oldJar = portalJar;
					portalJar = PORTAL_URL + "/" + getUpdateType() + "/" + jar; //$NON-NLS-1$ //$NON-NLS-2$

					int index = jar.lastIndexOf("."); //$NON-NLS-1$
					String subdir = (index < 0) ? jar : jar.substring(0, index);
					if ((new File(configDir, subdir)).exists())
					{
						// the jar was downloaded before; no need to do it again
						// since each jar will have an unique number
						return;
					}
					// uses a temporary directory to store the downloaded
					// content so it does not corrupt the existing one if a
					// problem occurs during the download
					File tempDir = new File(configDir, "portal-temp"); //$NON-NLS-1$
					tempDir.mkdir();

					JarInputStream stream = null;
					Checksum checksum = new CRC32();
					URL url = null;
					try
					{
						url = new URL(portalJar);
						URLConnection conn = url.openConnection();
						conn.setUseCaches(false);
						if (conn instanceof HttpURLConnection) {
							conn.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
						}
						conn.setConnectTimeout(10000);
						CheckedInputStream checkedInput = new CheckedInputStream(conn.getInputStream(), checksum);
						stream = new JarInputStream(checkedInput);

						// downloads the content in portal jar to the user
						// configuration area
						FileOutputStream out;
						File contentFile;
						JarEntry entry;
						byte[] b;
						int nread;
						while ((entry = stream.getNextJarEntry()) != null)
						{
							contentFile = new File(tempDir, entry.toString());
							// creates the parent directory if it does not exist
							// yet
							if (!contentFile.getParentFile().exists())
							{
								contentFile.getParentFile().mkdirs();
							}

							if (entry.isDirectory())
							{
								contentFile.mkdir();
							}
							else
							{
								out = new FileOutputStream(contentFile);
								b = new byte[10000];
								while ((nread = stream.read(b, 0, b.length)) >= 0)
								{
									out.write(b, 0, nread);
								}
								out.close();
							}
						}
						// compares with the expected checksum, if there is one
						// defined, to add another level of integrity checking
						String expectedChecksum = attributes.getValue(CHECKSUM);
						if (expectedChecksum == null || checksum.getValue() == Long.parseLong(expectedChecksum))
						{
							// download is successful; copies the temp directory
							// to the actual directory in the configuration area
							FileUtils.copy(tempDir, configDir);
							// deletes the temp directory
							FileUtils.deleteDirectory(tempDir);
							
							String latestVersion = getLatestPortalVersion();
							if (latestVersion.length() > 0 && !latestVersion.equals(currentPortalVersion))
							{
								currentPortalVersion = latestVersion;
								fireNewPortalDownloaded(currentPortalVersion);
								copyPortalContents();
							}
						}
					}
					catch (Exception e)
					{
						String message = (url == null) ? e.getMessage() : "Failed to parse " + url.toString(); //$NON-NLS-1$
						IdeLog.logError(JettyPlugin.getDefault(), message, e);
						// download failed; resets to the previous value
						portalJar = oldJar;
					}
					finally
					{
						if (stream != null)
						{
							try
							{
								stream.close();
							}
							catch (IOException e)
							{
							}
						}
					}
				}
			}
		}

	};
	
	private PortalServiceLoader()
	{
		portlets = getInstalledPortalServices();
		portalJar = LOCAL_INSTALL_JAR;
		currentPortalVersion = getCoreVersion();
		listeners = new ArrayList<IPortalListener>();

		copyPortalContents();
		Job job = new Job("Downloading portal contents") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				downloadPortalContents();
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Gets the first matching portlet with the given id or null if none found
	 * 
	 * @param id
	 * @return - portlet or null
	 */
	public PortalService getPortlet(String id)
	{
		if (id == null)
		{
			return null;
		}
		for (PortalService p : portlets)
		{
			if (id.equals(p.getId()))
			{
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the portlets defined via extension point
	 * 
	 * @return - array of portlets
	 */
	public PortalService[] getPortlets()
	{
		return portlets.toArray(new PortalService[0]);
	}

	/**
	 * Gets the portlet loader
	 * 
	 * @return - returns a loaded portlet loader
	 */
	public static PortalServiceLoader getLoader()
	{
		if (loader == null)
		{
			loader = new PortalServiceLoader();
		}
		return loader;
	}

	public String getPortalJarLocation()
	{
		return portalJar;
	}
	
	public String getPortalFolderLocation()
	{
		for (PortalService service : portlets)
		{
			if (service.getId().equals("")) //$NON-NLS-1$
			{
				return service.getFolder();
			}
		}
		return ""; //$NON-NLS-1$
	}

	public void addListener(IPortalListener listener)
	{
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	public void removeListener(IPortalListener listener)
	{
		listeners.remove(listener);
	}

	public void copyPortalContents()
	{
		// checks and creates the directory in the user configuration area for
		// storing the portal contents
		File configDir = CoreUIUtils.getConfigurationDirectory();
		File portalDir = new File(configDir, PORTAL_SUFFIX + currentPortalVersion);

		// We need to check if the contribution from individual service exists or not.
		// This is because the service like Jaxer may get installed later than
		// Studio
		
		// copies the contents from installed location to the configuration area
		List<PortalService> services = getInstalledPortalServices();
		File file;
		for (PortalService service : services)
		{
			String id = service.getId();
			if ("".equals(id)) { //$NON-NLS-1$
				file = new File(portalDir, id);
				if (file.exists() && System.getProperty("OVERWRITE_PORTAL") == null) { //$NON-NLS-1$
					continue;
				}
				file.mkdir();
	
				FileUtils.copy(new File(service.getFolder()), file);
			}
		}
		for (PortalService service : services)
		{
			String id = service.getId();
			if (!("".equals(id))) { //$NON-NLS-1$
				file = new File(portalDir, service.getId());
				if (file.exists() && System.getProperty("OVERWRITE_PORTAL") == null) { //$NON-NLS-1$
					continue;
				}
				file.mkdir();
	
				FileUtils.copy(new File(service.getFolder()), file);
			}
		}
	}

	public void switchPortalLocations()
	{
		File configDir = CoreUIUtils.getConfigurationDirectory();
		String directory = getLatestPortalDirectory();
		if (directory.length() == 0)
		{
			return;
		}

		// re-directs the resource location for the portal service
		File portalDir = new File(configDir, directory);
		File serviceDir;
		for (PortalService service : portlets)
		{
			serviceDir = new File(portalDir, service.getId());
			if (!serviceDir.exists())
			{
			    // copies the service' content from the local version
			    serviceDir.mkdir();
			    FileUtils.copy(new File(service.getFolder()), serviceDir);
			}
			service.setResource(new File(serviceDir, service.getResourceName()).getAbsolutePath());
		}
	}

	/**
	 * Checks the server to download the latest portal contents.
	 */
	public void downloadPortalContents()
	{
		if (!isDownloading.compareAndSet(false, true))
		{
			// one already in progress
			return;
		}

		URL url = null;
		try
		{
			// locates portal.xml on the server
			url = new URL(PORTAL_URL + "/" + getUpdateType() + "/" + PORTAL_XML); //$NON-NLS-1$ //$NON-NLS-2$
			URLConnection conn = url.openConnection();
			conn.setUseCaches(false);
			if (conn instanceof HttpURLConnection) {
				conn.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			conn.setConnectTimeout(10000);
			InputStream stream = conn.getInputStream();

			// parses the file
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = null;
			saxParser = factory.newSAXParser();
			saxParser.parse(stream, xmlHandler);
			stream.close();
		}
		catch (Exception e)
		{
		}
		finally
		{
			isDownloading.set(false);
		}
	}

	private void fireNewPortalDownloaded(String newVersion)
	{
		for (IPortalListener listener : listeners)
		{
			listener.newPortalDownloaded(newVersion);
		}
	}

	private String getLatestPortalDirectory()
	{
		// locates the configuration directory
		File configDir = CoreUIUtils.getConfigurationDirectory();

		// finds the latest portal directory for the current main version
		String coreVersion = currentPortalVersion;
		int index = coreVersion.lastIndexOf("."); //$NON-NLS-1$
		final String baseVersion = PORTAL_SUFFIX + ((index < 0) ? coreVersion : coreVersion.substring(0, index));
		String[] filenames = configDir.list(new FilenameFilter()
		{

			public boolean accept(File file, String filename)
			{
				return (new File(file, filename)).isDirectory() && filename.contains(baseVersion);
			}

		});
		if (filenames == null || filenames.length == 0)
		{
			// no portal directory in the configuration directory; should not
			// happen
			return ""; //$NON-NLS-1$
		}

		SortedMap<Float, String> map = new TreeMap<Float, String>();
		float buildNumber;
		for (String filename : filenames)
		{
			// parses out the build number
			index = filename.lastIndexOf(baseVersion);
			try
			{
				buildNumber = Float.parseFloat(filename.substring(index + baseVersion.length()));
			}
			catch (NumberFormatException e)
			{
				// m.n.o.qualifier is considered the latest for development
				buildNumber = Float.MAX_VALUE;
			}
			map.put(new Float(buildNumber), filename);
		}
		return map.get(map.lastKey());
	}

	public String getLatestPortalVersion()
	{
		String directory = getLatestPortalDirectory();
		return directory.length() == 0 ? directory : directory.substring(PORTAL_SUFFIX.length());
	}

	/**
	 * @return the version associated with com.aptana.ide.core.ui
	 */
	private static String getCoreVersion()
	{
		return PluginUtils.getPluginVersion(CoreUIPlugin.getDefault());
	}

	private static List<PortalService> getInstalledPortalServices()
	{
		List<PortalService> services = new ArrayList<PortalService>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				PORTLET_EXTENSION);
		for (IConfigurationElement element : elements)
		{
			if (PORTLET_ELEMENT.equals(element.getName()))
			{
				String id = element.getAttribute(ID_ATTRIBUTE);
				String resource = element.getAttribute(RESOURCE_ATTRIBUTE);
				if (id != null && resource != null)
				{
					Bundle bundle = Platform.getBundle(element.getContributor().getName());
					String folder = resource.substring(0, resource.lastIndexOf("/")); //$NON-NLS-1$
					URL folderPath = bundle.getEntry(folder);
					if (folderPath != null)
					{
						try
						{
							FileLocator.toFileURL(folderPath);
						}
						catch (IOException e)
						{
						}
					}
					URL filePath = bundle.getEntry(resource);
					if (filePath != null)
					{
						try
						{
							filePath = FileLocator.toFileURL(filePath);
							PortalService p = new PortalService(id, filePath.getPath());
							services.add(p);
						}
						catch (IOException e)
						{
						}

					}
				}
			}
		}
		return services;
	}

	/**
	 * @return the sub-directory on the server that corresponds to the update type user selected
	 */
	private static String getUpdateType()
	{
		return JettyPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.PORTAL_UPDATE_TYPE);
	}

}
