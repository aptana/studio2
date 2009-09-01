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
package com.aptana.ide.core.ui.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.URLEncoder;

/**
 * 
 * 
 * @author Kevin Lindsey
 * @notes  Please be cautious about adding aptana dependencies to this file as the Python plugin now uses this class.
 *         A logging should be completed using the standard Eclipse Logging mechanism.
 */
public final class ApplicationPreferences
{
	private static final String aptanaSettings = ".aptana-settings"; //$NON-NLS-1$
	private static ApplicationPreferences instance;
	private Map<String,String> _keyValuePairs;
	private boolean _hasLoaded;
	private ListenerList listenerList;
	

	/**
	 * ApplicationPreferences
	 */
	private ApplicationPreferences()
	{
		this._keyValuePairs = new HashMap<String,String>();
		this.listenerList = new ListenerList();
	}

	/**
	 * getInstance
	 * 
	 * @return preference singleton
	 */
	public static ApplicationPreferences getInstance()
	{
		if (instance == null)
		{
			instance = new ApplicationPreferences();
			instance.loadPreferences();
		}

		return instance;
	}

	/**
	 * Returns the settings file on disk
	 * 
	 * @return settings file
	 */
	private File getSettingsFile()
	{
		/* Removing dependency on CoreUIUtils */
//		File config = CoreUIUtils.getConfigurationDirectory();
		File config = ApplicationPreferences.getConfigurationDirectory();
		return new File(config, aptanaSettings);
	}
	
	/**
	 * Returns a file handle to the folder links to osgi.configuration.area.
	 * 
	 * @return A reference to the configuration directory on disk
	 */
	public static File getConfigurationDirectory()
	{
		String homeDir = System.getProperty("osgi.configuration.area"); //$NON-NLS-1$
		URL fileURL = ApplicationPreferences.uriToURL(homeDir);
		File f = ApplicationPreferences.urlToFile(fileURL);
		f.mkdirs();
		return f;
	}
	
	/**
	 * LoadPreferences
	 */
	public void loadPreferences()
	{
		File settings = getSettingsFile();
		if (this._hasLoaded || settings.exists() == false)
		{
			return;
		}

		FileReader fr = null;
		StringBuilder errors = new StringBuilder();
		try
		{
			fr = new FileReader(settings);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			int lineNumber = 1;
			while (line != null)
			{
				int colonIndex = line.indexOf(":"); //$NON-NLS-1$

				if (colonIndex != -1)
				{
					// split key/value pair
					String key = line.substring(0, colonIndex);
					String value = line.substring(colonIndex + 1, line.length());

					// store key/value pair
					this._keyValuePairs.put(key, value);
				}
				else
				{
					errors.append("\t[line ");//$NON-NLS-1$
					errors.append(lineNumber);
					errors.append("] - Expected key-value pair, but found '");//$NON-NLS-1$
					errors.append(line);
					errors.append("'\n");//$NON-NLS-1$
				}
				// read next line
				line = br.readLine();
				lineNumber++;
			}
		}
		catch (Exception e)
		{
			logError(Messages.ApplicationPreferences_ERR_UnableToReadAptanaSettings, e);
		}
		finally
		{
			if (fr != null)
			{
				try
				{
					fr.close();
				}
				catch (IOException e)
				{		
					logError(Messages.ApplicationPreferences_ERR_UnableToCloseAptanaSettings, e);
				}
			}
			if (errors.length() > 0)
			{
				// Log any errors found while reading the Aptana settings file
				logError("Errors found when reading the Aptana settings file:\n" + errors.toString(), null);//$NON-NLS-1$
			}
			this._hasLoaded = true;
		}
	}

	/**
	 * Returns a the location of .aptanaSettings file from earlier
	 * releases
	 * 
	 * @return file
	 */
	private File getPreviousDefaultSettingsFile() {
		File previousReleaseSettingsFile = null;
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			previousReleaseSettingsFile = new File(
					System.getProperty("user.home") + "/Library/Application Support/Aptana/Aptana Studio/configuration", //$NON-NLS-1$ //$NON-NLS-2$
					aptanaSettings);
		} else if (Platform.OS_WIN32.equals(Platform.getOS())) {
			previousReleaseSettingsFile = new File(
					System.getProperty("user.home") + "\\Application Data\\Aptana\\Aptana Studio\\configuration", //$NON-NLS-1$ //$NON-NLS-2$
					aptanaSettings);
		} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
			previousReleaseSettingsFile = new File(
					System.getProperty("user.home") + "/.Aptana/Aptana Studio/configuration", //$NON-NLS-1$ //$NON-NLS-2$
					aptanaSettings);
		}
		
		if (previousReleaseSettingsFile != null && previousReleaseSettingsFile.exists()) {
			return previousReleaseSettingsFile;
		}
		return null;
	}

	/**
	 * load preferences from .aptanaSettings file from earlier
	 * releases if found
	 */
	public Map<String,String> loadPreviousPreferences()
	{
		Map<String,String> previousPreferences = new HashMap<String, String>();
		File previousDefaultSettingsFile = getPreviousDefaultSettingsFile();
		if (previousDefaultSettingsFile != null) {
			FileReader fr = null;
			try
			{
				fr = new FileReader(previousDefaultSettingsFile);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();

				while (line != null)
				{
					int colonIndex = line.indexOf(":"); //$NON-NLS-1$

					if (colonIndex != -1)
					{
						// split key/value pair
						String key = line.substring(0, colonIndex);
						String value = line.substring(colonIndex + 1, line.length());
						
						previousPreferences.put(key, value);
						
						// read next line
						line = br.readLine();
					}
				}
			}
			catch (Exception e)
			{
				logError("Unable to read previous Aptana settings", e); //$NON-NLS-1$ 
			}
			finally
			{
				if (fr != null)
				{
					try
					{
						fr.close();
					}
					catch (IOException e)
					{		
						logError("Unable to close previous Aptana settings", e);  //$NON-NLS-1$
					}
				}
			}
		}
		return previousPreferences;
	}

	/**
	 * Add property change listener
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		listenerList.add(listener);
	}

	/**
	 * Remove property change listener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		listenerList.remove(listener);
	}

	/**
	 * Fire a property change event
	 * 
	 * @param name
	 * @param oldValue
	 * @param newValue
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue)
	{
		firePropertyChangeEvent(new PropertyChangeEvent(this, name, oldValue, newValue));
	}

	private void firePropertyChangeEvent(PropertyChangeEvent event)
	{
		Object[] listeners = listenerList.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			((IPropertyChangeListener) listeners[i]).propertyChange(event);
		}
	}

	/**
	 * SavePreferences
	 */
	public void savePreferences()
	{
		BufferedWriter bw = null;

		try
		{
			File settings = getSettingsFile();
			FileWriter fw = new FileWriter(settings);
			bw = new BufferedWriter(fw);

			Set<Map.Entry<String,String>> entries = this._keyValuePairs.entrySet();
			Iterator<Map.Entry<String,String>> iter = entries.iterator();

			while (iter.hasNext())
			{
				Map.Entry<String,String> entry = iter.next();

				bw.write(entry.getKey());
				bw.write(":"); //$NON-NLS-1$
				bw.write(entry.getValue());
				bw.newLine();
			}
		}
		catch (Exception e)
		{
			logError(Messages.ApplicationPreferences_ERR_UnableToWriteAptanaSettings, e);
		}
		finally
		{
			if (bw != null)
			{
				try
				{
					bw.close();
				}
				catch (IOException e)
				{
					logError(Messages.ApplicationPreferences_ERR_UnableToCloseAptanaSettings, e);
				}
			}
		}
	}

	/**
	 * getBoolean
	 * 
	 * @param preferenceName
	 * @return boolean
	 */
	public boolean getBoolean(String preferenceName)
	{
		return "true".equals(this.getString(preferenceName)); //$NON-NLS-1$
	}

	/**
	 * getString
	 * 
	 * @param preferenceName
	 * @return preference string value
	 */
	public String getString(String preferenceName)
	{
		if (preferenceName == null)
		{
			throw new IllegalArgumentException("preference name must be defined"); //$NON-NLS-1$
		}

		String result = null;

		if (this._keyValuePairs.containsKey(preferenceName))
		{
			result = this._keyValuePairs.get(preferenceName);
		}

		return result;
	}

	/**
	 * setBoolean
	 * 
	 * @param preferenceName
	 * @param preferenceValue
	 */
	public void setBoolean(String preferenceName, boolean preferenceValue)
	{
		this.setString(preferenceName, preferenceValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * setString
	 * 
	 * @param preferenceName
	 * @param preferenceValue
	 */
	public void setString(String preferenceName, String preferenceValue)
	{
		if (preferenceName == null)
		{
			throw new IllegalArgumentException("preference name must be defined"); //$NON-NLS-1$
		}
		this._keyValuePairs.put(preferenceName, preferenceValue);
		firePropertyChangeEvent(preferenceName, StringUtils.EMPTY, preferenceValue); //$NON-NLS-1$
	}
	
	/**
	 * uriToURL and urlToFile are both pulled from FileUtils
	 * The have been moved locally to remove a dependency so that
	 * Python does not need to depend on FileUtils as well.
	 */
	public static URL uriToURL(String uri)
	{
		URI uri2;
		String encodedUri;
		try
		{
			encodedUri = URLEncoder.encode(uri, null, null);
			uri2 = new URI(encodedUri);
			// NOTE: normalizing causes paths with ".." to lose their drive specification
			// uri2.normalize();
			return uri2.toURL();
		}
		catch (MalformedURLException e)
		{
			logError(StringUtils.format(Messages.ApplicationPreferences_ERR_UnableToConvertURIToURL, uri), e);
			return null;
		}
		catch (URISyntaxException e)
		{			
			logError(StringUtils.format(Messages.ApplicationPreferences_ERR_UnableToConvertURIToURLSyntaxIsIncorrect, uri), e);
			return null;
		}
	}
	

	/**
	 * Converts a file://-based url into a file
	 * 
	 * @param url
	 *            The url
	 * @return the File, or null if not a file URL
	 */
	public static File urlToFile(URL url)
	{
		try
		{
			//Done for Pydev: Instead of using 
			//
			//url.toURI()
			//
			//use:
			//
			//new URI(url.toString())
			//
			//Which is the same thing with a difference: it can be retroweaved to be compatible
			//with Java 1.4 (which is a requisite for Pydev)
			URI uri = new URI(url.toString());
			if ("file".equals(uri.getScheme())) //$NON-NLS-1$
			{
				return new File(uri.getSchemeSpecificPart());
			}
		}
		catch (Exception e)
		{
			logError(Messages.ApplicationPreferences_ERR_FailedToConvertURLToFile, e);
		}
		return null;
	}
	
	private static void logError(String errorMessage, Throwable e ){
		IStatus status = new Status( IStatus.ERROR, ResourcesPlugin.getPlugin().getBundle().getSymbolicName(), IStatus.OK, errorMessage, e);
		ResourcesPlugin.getPlugin().getLog().log(status);
	}
}
