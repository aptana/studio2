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
package com.aptana.ide.core.ui.ping;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.Base64;
import com.aptana.ide.core.DateUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.db.EventInfo;
import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.db.FeatureInfo;
import com.aptana.ide.core.db.FeatureTracker;
import com.aptana.ide.core.db.LogEventTypes;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.WorkbenchHelper;
import com.aptana.ide.core.ui.preferences.ApplicationPreferences;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.IPlugin;
import com.eaio.uuid.MACAddress;

public class PingStartup implements IStartup
{

	private static final String FEATURE_IU_SUFFIX = ".feature.group"; //$NON-NLS-1$
	private static final String INSTALL_URL = "http://check.aptana.com/install.php"; //$NON-NLS-1$
	private static final String UPDATE_URL = "https://check.aptana.com/update2.php"; //$NON-NLS-1$
	private static final int READ_TIMEOUT = 15 * 60 * 1000; // 15 seconds in millis
	private static final long TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

	private static boolean ranOnce = false;

	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		if (Platform.inDevelopmentMode()) {
			return;
		}
		Job job = new Job("Sending Ping...") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				updateAnonymousId();
				if (!ranOnce)
				{
					ranOnce = true;
				}
				schedule(TWENTY_FOUR_HOURS);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.BUILD);
		job.schedule();
	}

	/**
	 * updateAnonymousId Updates the IDE ID if it hasn't been set. This ID is used just to track updates, no personal
	 * information is shared or retained.
	 */
	private static void updateAnonymousId()
	{
		URL url = null;
		boolean needId = false;
		String previousId = null;

		boolean success = false;

		// load application preferences
		ApplicationPreferences preferences = ApplicationPreferences.getInstance();
		preferences.loadPreferences();

		// get application run and id values
		boolean hasRun = preferences.getBoolean(IPreferenceConstants.P_IDE_HAS_RUN);
		String applicationId = preferences.getString(IPreferenceConstants.P_IDE_ID);

		// get workspace id value
		Preferences p = CoreUIPlugin.getDefault().getPluginPreferences();
		String workspaceId = p.getString(IPreferenceConstants.P_IDE_ID);

		// tag id as "none" if it wasn't defined
		if (applicationId == null || applicationId.length() == 0)
		{
			if (workspaceId == null || workspaceId.length() == 0)
			{
				applicationId = "none"; //$NON-NLS-1$

				// Look for id from previous release's location
				Map<String, String> previousPreferences = preferences.loadPreviousPreferences();
				previousId = previousPreferences.get(IPreferenceConstants.P_IDE_ID);

				needId = true;
			}
			else
			{
				applicationId = workspaceId;

				// move value to application preferences
				preferences.setString(IPreferenceConstants.P_IDE_ID, applicationId);
				preferences.savePreferences();
			}
		}
		else
		{
			// merge ids if they do not match
			if (workspaceId != null && workspaceId.length() > 0 && workspaceId.equals(applicationId) == false)
			{
				// id from prefs wins right now
				applicationId = workspaceId;
			}
		}

		String queryString = null;

		try
		{
			List<String> keyValues = new ArrayList<String>();

			addKeyPair(keyValues, "id", applicationId); //$NON-NLS-1$
			addKeyPair(keyValues, "v", PluginUtils.getPluginVersion(AptanaCorePlugin.getDefault())); //$NON-NLS-1$
			addKeyPair(keyValues, "p", System.getProperty("eclipse.product")); //$NON-NLS-1$ //$NON-NLS-2$
			addKeyPair(keyValues, "ev", System.getProperty("osgi.framework.version")); //$NON-NLS-1$ //$NON-NLS-2$
			addKeyPair(keyValues, "osa", System.getProperty("os.arch")); //$NON-NLS-1$ //$NON-NLS-2$
			addKeyPair(keyValues, "osn", System.getProperty("os.name")); //$NON-NLS-1$ //$NON-NLS-2$
			addKeyPair(keyValues, "osv", System.getProperty("os.version")); //$NON-NLS-1$ //$NON-NLS-2$
			addKeyPair(keyValues, "pre", getPreviewString()); //$NON-NLS-1$
			addKeyPair(keyValues, "pro", CoreUIUtils.getEncryptedProValue()); //$NON-NLS-1$

			// add current date/time before extracting events
			EventLogger.getInstance().logEvent(LogEventTypes.DATE_TIME);

			// add studio key
			EventLogger.getInstance().logEvent(LogEventTypes.STUDIO_KEY, MACAddress.getMACAddress());

			// add plugins
			addFeatureList();

			// add custom key/value pairs
			EventInfo[] events = EventLogger.getInstance().getEvents();

			for (EventInfo event : events)
			{
				// ignore preview events
				if (event.getEventType().equals(LogEventTypes.PREVIEW) == false)
				{
					// make sure to specify the key as an array in case we have
					// more than one value of the same event type
					String key = event.getEventType() + "[]"; //$NON-NLS-1$

					// combine datetime stamp and value
					String value = Long.toString(event.getDateTime()) + ":" + event.getMessage(); //$NON-NLS-1$

					addKeyPair(keyValues, key, value);
				}
			}

			// create POST string
			queryString = StringUtils.join("&", keyValues.toArray(new String[keyValues.size()])); //$NON-NLS-1$

			// gzip POST string
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(queryString.getBytes());
			gos.flush();
			gos.finish();
			gos.close();
			baos.close();
			byte[] gzippedData = baos.toByteArray();

			// create URL
			url = new URL(UPDATE_URL);

			// open connection and configure it
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Encoding", "gzip"); //$NON-NLS-1$ //$NON-NLS-2$
			connection.setRequestProperty("Content-Length", String.valueOf(gzippedData.length)); //$NON-NLS-1$
			connection.setRequestProperty("User-Agent", getUserAgent()); //$NON-NLS-1$
			connection.setReadTimeout(READ_TIMEOUT); // 15 second read timeout

			// write POST
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			output.write(gzippedData);
			// output.writeBytes(queryString);
			output.flush();

			// Get the response
			// NOTE: we really only need to read one line, but we read all of them since this lets
			// us examine error messages and helps with debugging
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = input.readLine()) != null)
			{
				sb.append(line);
			}

			output.close();
			input.close();

			String newId = sb.toString();

			// only save the new id if we need one and if it is valid
			if (needId == true && newId.matches("^[0-9]+$")) //$NON-NLS-1$
			{
				if (previousId != null)
				{
					// Use the id from previous release
					newId = previousId;
				}
				preferences.setString(IPreferenceConstants.P_IDE_ID, newId);
				preferences.setBoolean(IPreferenceConstants.P_IDE_HAS_RUN, true);
				preferences.savePreferences();
			}

			success = true;
		}
		catch (UnknownHostException e)
		{
			// happens when user is offline or can't resolve aptana.com
			return;
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils
					.format(Messages.SchedulerStartup_UrlIsMalformed, url), e);
		}
		catch (IOException e)
		{
			if (needId && hasRun == false)
			{
				WorkbenchHelper.launchBrowser(INSTALL_URL + "?" + queryString); //$NON-NLS-1$
				preferences.setBoolean(IPreferenceConstants.P_IDE_HAS_RUN, true);
				preferences.savePreferences();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.SchedulerStartup_UnableToContactUpdateServer, e);
		}

		if (success)
		{
			// clear event log
			EventLogger.getInstance().clearEvents();
		}
		else
		{
			// remove anything older than 28 days.
			long fourWeeksAgo = DateUtils.addDayInterval(System.currentTimeMillis(), -28);

			EventLogger.getInstance().clearEvents(fourWeeksAgo);
		}
	}

	/**
	 * addKeyPair
	 * 
	 * @param keyValues
	 * @param key
	 * @param value
	 */
	private static void addKeyPair(List<String> keyValues, String key, String value)
	{
		if (value == null)
		{
			value = StringUtils.EMPTY;
		}

		keyValues.add(StringUtils.urlEncodeKeyValuePair(key, value));
	}

	/**
	 * addFeatureList
	 * 
	 * @return
	 */
	private static void addFeatureList()
	{
		Set<FeatureInfo> featureList = new HashSet<FeatureInfo>();
		List<IPlugin> installed = FeatureUtil.getInstalledFeatures();
		for (IPlugin iu : installed)
		{
			// NOTE: Prior to P2, features could be enabled or not. P2 no longer has this
			// concept, so we hard-code that the feature is enabled to keep the format
			// consistent with pre-P2 pings.
			featureList.add(new FeatureInfo(stripFeatureNameToBaseID(iu.getId()), iu.getVersion(), true));
		}

		FeatureInfo[] features = featureList.toArray(new FeatureInfo[featureList.size()]);
		if (featuresMatch(features) == false)
		{
			FeatureTracker tracker = FeatureTracker.getInstance();

			// clear old list of features
			tracker.clearFeatures();

			// emit feature list and add to feature tracker
			EventLogger logger = EventLogger.getInstance();

			for (FeatureInfo feature : featureList)
			{
				logger.logEvent(LogEventTypes.FEATURE, feature.toString());
				tracker.addFeature(feature);
			}
		}
	}

	/**
	 * featuresMatch
	 * 
	 * @param currentFeatures
	 * @return
	 */
	private static boolean featuresMatch(FeatureInfo[] currentFeatures)
	{
		FeatureInfo[] savedFeatures = FeatureTracker.getInstance().getFeatures();
		boolean result = (currentFeatures.length == savedFeatures.length);

		if (result)
		{
			// sort both lists
			Arrays.sort(savedFeatures);
			Arrays.sort(currentFeatures);

			// now compare for any diffs
			for (int i = 0; i < savedFeatures.length; i++)
			{
				if (savedFeatures[i].equals(currentFeatures[i]) == false)
				{
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * stripFeatureNameToBaseID
	 * 
	 * @param name
	 * @return
	 */
	private static String stripFeatureNameToBaseID(String name)
	{
		if (name == null || !name.endsWith(FEATURE_IU_SUFFIX))
		{
			return name;
		}

		return name.substring(0, name.length() - FEATURE_IU_SUFFIX.length());
	}

	/**
	 * getPreviewString
	 * 
	 * @return
	 */
	private static String getPreviewString()
	{
		EventInfo[] events = EventLogger.getInstance().getEvents(LogEventTypes.PREVIEW);
		String result = "-"; //$NON-NLS-1$

		if (events.length > 0)
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(byteStream);

			// determine width of each interval
			int minuteInterval = 10;

			// round current time up to next interval
			long stopTime = DateUtils.roundDownToMinuteInterval(System.currentTimeMillis(), minuteInterval);
			stopTime = DateUtils.addMinuteInterval(stopTime, minuteInterval);

			// round first time down to previous interval
			long nextTime = events[0].getDateTime();
			nextTime = DateUtils.roundDownToMinuteInterval(nextTime, minuteInterval);

			// init byte
			int currentByte = 0;
			int shiftCount = 0;
			int eventIndex = 0;

			try
			{
				// write time for first interval
				output.writeLong(nextTime);

				// write number of minutes between intervals
				output.writeInt(minuteInterval);

				// advance to next interval to process all events up to that interval
				nextTime = DateUtils.addMinuteInterval(nextTime, minuteInterval);

				do
				{
					boolean match = false;

					while (eventIndex < events.length)
					{
						if (events[eventIndex].getDateTime() >= nextTime)
						{
							// process this bit
							break;
						}
						else
						{
							// set this bit
							match = true;

							// go to next event
							eventIndex++;
						}
					}

					// write bit
					if (match)
					{
						currentByte = (currentByte << 1) | 1;
					}
					else
					{
						currentByte = currentByte << 1;
					}

					shiftCount++;

					// emit if that was the 8th bit
					if (shiftCount == 8)
					{
						output.writeByte(currentByte);

						// reset
						currentByte = 0;
						shiftCount = 0;
					}

					// advance to next interval
					nextTime = DateUtils.addMinuteInterval(nextTime, minuteInterval);
				}
				while (nextTime <= stopTime);

				// fill the rest of the byte with zero and emit
				if (shiftCount > 0)
				{
					currentByte = (currentByte << (8 - shiftCount));
					output.writeByte(currentByte);
				}

				// we're done
				output.close();

				// grab bytes
				byte[] data = byteStream.toByteArray();

				// compress as base64
				result = Base64.encodeBytes(data, Base64.DONT_BREAK_LINES);
			}
			catch (IOException e)
			{
			}
		}

		return result;
	}

	/**
	 * getUserAgent
	 * 
	 * @return
	 */
	private static String getUserAgent()
	{
		return "Aptana/1.5"; //$NON-NLS-1$
	}
}
