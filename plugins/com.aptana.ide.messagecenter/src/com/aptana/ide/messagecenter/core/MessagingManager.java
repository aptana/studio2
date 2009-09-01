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
package com.aptana.ide.messagecenter.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.intro.messaging.Message;
import com.aptana.ide.messagecenter.MessageCenterPlugin;
import com.aptana.ide.messagecenter.preferences.FeedDescriptor;
import com.aptana.ide.messagecenter.preferences.IPreferenceConstants;

/**
 * Messaging manager for the Aptana Message Center
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MessagingManager
{

	private static ListenerList listeners = new ListenerList();
	private static MessagesReader reader = null;
	private static List<Message> messages = null;
	private static Map<String, FeedDescriptor> feedMap = new ConcurrentHashMap<String, FeedDescriptor>();

	/**
	 * INTERVAL_CHECK
	 */
	private static long INTERVAL_CHECK = 180000;

	/**
	 * Last checked date
	 */
	private static Date lastChecked = new Date(0);

	/**
	 * Add a listener
	 * 
	 * @param listener
	 */
	public static void addListener(IMessageListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	public static void removeListener(IMessageListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Add a feed to the list of feeds to process
	 * 
	 * @param feed
	 */
	public static void addFeed(FeedDescriptor feed)
	{
		if (feedMap.containsKey(feed.getUrl()))
		{
			feedMap.remove(feed.getUrl());
		}

		feedMap.put(feed.getUrl(), feed);
	}

	/**
	 * Remove a feed from the list of feeds to process
	 * 
	 * @param feed
	 * @param clearMessages
	 */
	public static void removeFeed(FeedDescriptor feed, boolean clearMessages)
	{
	    removeFeed(feed.getUrl(), clearMessages);
	}

	/**
	 * Remove a feed from the list of feeds to process
	 * 
	 * @param feedUrl
	 * @param clearMessages
	 */
	public static void removeFeed(String feedUrl, boolean clearMessages)
	{
		if (feedMap.containsKey(feedUrl))
		{
			feedMap.remove(feedUrl);
			if (clearMessages)
			{
				clearMessages(feedUrl);
			}
		}
	}

	/**
	 * Removes all messages containing this particular feed URL
	 * 
	 * @param feedUrl
	 */
	private static void clearMessages(final String feedUrl)
	{
		Job job = new Job(Messages.MessagingManager_Job_ClearingMsgs)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				List<Message> remove = new ArrayList<Message>();
				for (Message message : messages)
				{
					if (message.getSite().equals(feedUrl))
					{
						remove.add(message);
					}
				}
				for (Message message : remove)
				{
					purgeMessage(message);
				}
				return Status.OK_STATUS;
			}

		};
		job.setPriority(Job.LONG);
		job.schedule();
	}

	/**
	 * Notify listeners a message has changed
	 * 
	 * @param changedMessage
	 *            - changed message
	 * @param eventType
	 */
	public static void notifyListeners(final Message changedMessage, final int eventType)
	{
		Object[] oListeners = listeners.getListeners();
		for (int i = 0; i < oListeners.length; i++)
		{
			final IMessageListener listener = (IMessageListener) oListeners[i];
			ISafeRunnable job = new ISafeRunnable()
			{
				public void handleException(Throwable exception)
				{
					IdeLog.logInfo(MessageCenterPlugin.getDefault(), Messages.MessagingManager_INF_ErrorNotify, exception);
				}

				public void run() throws Exception
				{
					listener.messageChanged(changedMessage, eventType);
				}
			};
			SafeRunner.run(job);
		}
		storeMessages();
		storeFeeds();
	}

	/**
	 * Loads the new remote messages
	 * 
	 * @param feeds
	 * @param monitor
	 */
	private static void loadRemoteMessages(FeedDescriptor[] feeds, IProgressMonitor monitor)
	{
		if (reader == null)
		{
			reader = new MessagesReader();
		}
		reader.loadMessages(feeds, monitor);
		Message[] retrieved = reader.getMessages();
		for (int i = 0; i < retrieved.length; i++)
		{
			addMessage(retrieved[i]);
		}
	}

	/**
	 * Gets the number of urgent unread messages
	 * 
	 * @return - number of urgent unread messages
	 */
	public static int getUnreadUrgentMessages()
	{
		int urgentsUnread = 0;
		List<Message> msgs = messages;
		synchronized (msgs)
		{
			for (Message message : msgs)
			{
				if (message != null && message.isUrgent() && !message.isRead())
				{
					urgentsUnread++;
				}
			}
		}
		return urgentsUnread;
	}

	/**
	 * Gets the number of unread messages
	 * 
	 * @return - count of unread messages
	 */
	public static int getUnreadMessages()
	{
		int unread = 0;
		List<Message> msgs = messages;
		synchronized (msgs)
		{
			for (Message message : msgs)
			{
				if (message != null && !message.isRead())
				{
					unread++;
				}
			}
		}
		return unread;
	}

	/**
	 * Adds a new message under the manager's control
	 * 
	 * @param message
	 *            - new message
	 */
	private static void addMessage(Message message)
	{
		boolean newMessage = true;
		for (Message msg : messages)
		{
			if (msg != null && message != null && msg.getId() != null && message.getId() != null
					&& msg.getId().equals(message.getId()))
			{
				newMessage = false;
				break;
			}

		}
		if (newMessage)
		{
			messages.add(message);
			sortMessages();
			notifyListeners(message, IMessageListener.MESSAGE_ADDED);
		}
	}

	/**
	 * Purges a message from the list of messages
	 * 
	 * @param message
	 *            - new message
	 */
	private static void purgeMessage(Message message)
	{
		message.setPurged(true);
		messages.remove(message);
		notifyListeners(message, IMessageListener.MESSAGE_REMOVED);
	}

	private static void sortMessages()
	{
		Collections.sort(messages, new Comparator<Message>()
		{

			public int compare(Message m1, Message m2)
			{
				if (m1.isUrgent() && !m2.isUrgent())
				{
					return -1;
				}
				if (!m1.isUrgent() && m2.isUrgent())
				{
					return 1;
				}

				if (m1.getDate() == null && m2.getDate() != null)
				{
					return 1;
				}
				if (m1.getDate() != null && m2.getDate() == null)
				{
					return -1;
				}
				if (m1.getDate() != null && m2.getDate() != null)
				{
					return m1.getDate().after(m2.getDate()) ? -1 : 1;
				}
				return 0;
			}

		});
	}

	/**
	 * Starts the message checking service
	 */
	public static void startMessageService()
	{
		loadCustomFeeds();

		Job job = new Job("Initializing message loader") //$NON-NLS-1$
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				getNewMessages(lastChecked);
				lastChecked = new Date();
				this.schedule(INTERVAL_CHECK);
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.setPriority(Job.LONG);
		job.schedule(10000);
	}

	/**
	 * Load the feeds from preferences
	 */
	private static void loadCustomFeeds()
	{
		FeedDescriptor[] feeds = getFeeds();
		for (int i = 0; i < feeds.length; i++)
		{
			addFeed(feeds[i]);
		}
	}

	/**
	 * Gets the list of messages
	 * 
	 * @return - messages
	 */
	private synchronized static List<Message> loadStoredMessages()
	{
		List<Message> messages = new ArrayList<Message>();
		List<Message> stored = MessagingPreferences.getInstance().loadPreferences();

		if (stored != null)
		{
			for (Message message : stored)
			{
				String description = message.getContent();
				if (description != null && !"null".equals(description.trim())) //$NON-NLS-1$
				{
					messages.add(message);
				}
			}
		}

		return messages;
	}

	/**
	 * Gets the list of messages
	 * 
	 * @return - messages
	 */
	public synchronized static List<Message> getMessages()
	{
		if (messages == null)
		{
			messages = loadStoredMessages();
			sortMessages();
		}
		return messages;
	}

	/**
	 * Gets the list of messages
	 * 
	 * @param since
	 */
	public synchronized static void getNewMessages(final Date since)
	{
		Job job = new Job(Messages.MessagingManager_Job_LoadingMsgs)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				FeedDescriptor[] overdue = getOverdueFeeds(since);
				loadRemoteMessages(overdue, monitor);
				return Status.OK_STATUS;
			}

		};
		job.setPriority(Job.LONG);
		job.schedule();
	}

	/**
	 * Returns a list of feeds which are due to be checked based on when they
	 * were last checked and their polling interval
	 * 
	 * @param date
	 * @return - array of feed descriptors
	 */
	public static FeedDescriptor[] getOverdueFeeds(Date date)
	{
		Collection<FeedDescriptor> feeds = feedMap.values();
		List<FeedDescriptor> overdue = new ArrayList<FeedDescriptor>();
		for (FeedDescriptor feedDescriptor : feeds)
		{
			if (Math.abs(date.getTime() - feedDescriptor.getLastPoll().getTime()) > feedDescriptor.getPollInterval())
			{
				overdue.add(feedDescriptor);
			}
		}
		return overdue.toArray(new FeedDescriptor[overdue.size()]);
	}

	/**
	 * Store messages to disk
	 */
	private static void storeMessages()
	{
		MessagingPreferences.getInstance().savePreferences(messages);
	}

	/**
	 * Store feed preferences to disk
	 */
	public static void storeFeeds()
	{
		IEclipsePreferences prefs = (new InstanceScope().getNode(MessageCenterPlugin.PLUGIN_ID));
		if (prefs != null)
		{
			prefs.put(IPreferenceConstants.MESSAGE_CENTER_FEEDS, FeedDescriptor.serializeFeedDescriptors(feedMap
					.values().toArray(new FeedDescriptor[0])));
			try {
                prefs.flush();
            } catch (BackingStoreException e) {
            }
		}
	}

	/**
	 * Retrieve feed preferences from disk
	 * 
	 * @return - array of feed descriptors
	 */
	public static FeedDescriptor[] getFeeds()
	{
		String editors = Platform.getPreferencesService().getString(MessageCenterPlugin.PLUGIN_ID, IPreferenceConstants.MESSAGE_CENTER_FEEDS, null, null);
		if (editors == null)
		{
		    return new FeedDescriptor[0];
		}
		return FeedDescriptor.deserializeFeedDescriptors(editors);
	}

}
