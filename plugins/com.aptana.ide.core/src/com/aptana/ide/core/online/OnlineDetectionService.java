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
package com.aptana.ide.core.online;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;

public class OnlineDetectionService
{
	protected static final long REGULAR_SLEEP_DELAY = 30 * 1000;
	protected static final long LONG_SLEEP_DELAY = 10 * 60 * 1000;

	private static final int URL_TIMEOUT = 10 * 1000;
	private static OnlineDetectionService INSTANCE = null;
	private List<URL> _detectionURLs = new ArrayList<URL>();
	private static ListenerList listeners = new ListenerList();

	public enum StatusMode
	{
		UNKNOWN, ONLINE, OFFLINE
	};

	private StatusMode _status = StatusMode.UNKNOWN;

	private Job _detectionJob;
	private long _delay;

	/**
	 * 
	 */
	private OnlineDetectionService()
	{
		_delay = LONG_SLEEP_DELAY;
		startDetectionThread();
	}

	/**
	 * @return
	 */
	public static OnlineDetectionService getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new OnlineDetectionService();
		}

		return INSTANCE;
	}

	/**
	 * @return
	 */
	public StatusMode getStatus()
	{
		return _status;
	}

	/**
	 * Add a listener
	 * 
	 * @param listener
	 */
	public static void addListener(IOnlineStateChangedListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	public static void removeListener(IOnlineStateChangedListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * @param url
	 */
	public void addDetectionURL(URL url)
	{
		if(this._detectionURLs.contains(url) == false)
		{
			this._detectionURLs.add(url);
			detectOnlineStatus();
		}
		
	}

	/**
	 * @param url
	 */
	public void removeDetectionURL(URL url)
	{
		this._detectionURLs.remove(url);
	}

	/**
	 * Sets the online detection service to have a long or regular polling
	 * period.
	 * 
	 * @param isLongDelay
	 *            if true, the detection service will poll much less frequently;
	 *            if false, it will poll at a regular interval
	 */
	public void setDelay(boolean isLongDelay)
	{
		long oldDelay = _delay;
		if (isLongDelay)
		{
			_delay = LONG_SLEEP_DELAY;
		}
		else
		{
			_delay = REGULAR_SLEEP_DELAY;
		}
		if (_delay != oldDelay)
		{
			// delay changed; restarts the job
			_detectionJob.cancel();
			_detectionJob.schedule();
		}
	}

	/**
	 * 
	 */
	private void detectOnlineStatus()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				StatusMode oldStatus = _status;

				try
				{
					if (_detectionURLs.size() == 0)
					{
						_status = StatusMode.UNKNOWN;
						return;
					}

					for (URL url : _detectionURLs)
					{
						if (isAvailable(url) == false)
						{
							_status = StatusMode.OFFLINE;
							return;
						}
					}

					_status = StatusMode.ONLINE;
				}
				finally
				{
					if (oldStatus != _status)
					{
						notifyListeners(oldStatus);
					}
				}
			}
		}, "DetectOnlineStatus URL Checker").run(); //$NON-NLS-1$
	}

	/**
	 * @param oldStatus
	 */
	private void notifyListeners(final StatusMode oldStatus)
	{
		Object[] oListeners = listeners.getListeners();
		for (int i = 0; i < oListeners.length; i++)
		{
			final IOnlineStateChangedListener listener = (IOnlineStateChangedListener) oListeners[i];
			ISafeRunnable job = new ISafeRunnable()
			{
				public void handleException(Throwable exception)
				{
					IdeLog.logInfo(AptanaCorePlugin.getDefault(), Messages.getString("OnlineDetectionService.Error_Notifying_Listener"), exception); //$NON-NLS-1$
				}

				public void run() throws Exception
				{
					listener.stateChanged(oldStatus, getStatus());
				}
			};
			SafeRunner.run(job);
		}
	}

	/**
	 * @param url
	 * @return
	 */
	public static boolean isAvailable(URL url)
	{
		try
		{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD"); //$NON-NLS-1$
			conn.setReadTimeout(URL_TIMEOUT);
			conn.connect();
			conn.getResponseCode();
			conn.disconnect();
		}
		catch (SSLHandshakeException ssl)
		{
		}
		catch (Exception e)
		{
			String message = MessageFormat.format(
				Messages.getString("OnlineDetectionService.Checking_URL_Availability"), //$NON-NLS-1$
				new Object[] {
					url,
					e.getMessage()
				}
			);
			IdeLog.logInfo(AptanaCorePlugin.getDefault(), message);
			return false;
		}

		return true;
	}

	/**
	 * 
	 */
	private void startDetectionThread()
	{
		_detectionJob = new Job("Aptana: Online Detection Service") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				detectOnlineStatus();
				schedule(_delay);
				return Status.OK_STATUS;
			}

		};
		_detectionJob.setPriority(Job.BUILD);
		_detectionJob.setSystem(true);
		_detectionJob.schedule();
	}

	/*
	 * public static void main(String[] args) throws Exception { OnlineDetectionService ods =
	 * OnlineDetectionService.getInstance(); ods.addDetectionURL(new URL("http://www.aptana.com")); Thread.sleep(10
	 * 1000); System.err.println(ods.getStatus()); }
	 */
}
