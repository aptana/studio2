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
package com.aptana.ide.messagecenter.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.intro.messaging.Message;
import com.aptana.ide.messagecenter.core.IMessageListener;
import com.aptana.ide.messagecenter.core.MessagingManager;
import com.aptana.ide.server.jetty.comet.CometClient;
import com.aptana.ide.server.jetty.comet.CometConstants;

import dojox.cometd.Channel;

/**
 * Comet client for interacting with the Message Center
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MessagesClient extends CometClient
{

	/**
	 * MESSAGES_CHANNEL
	 */
	public static final String MESSAGES_CHANNEL = "/portal/messages"; //$NON-NLS-1$

	/**
	 * DESCRIBE_MESSAGES
	 */
	public static final String DESCRIBE_MESSAGES = "describeMessages"; //$NON-NLS-1$

	/**
	 * URGENT_COUNT
	 */
	public static final String URGENT_COUNT = "urgentCount"; //$NON-NLS-1$

	/**
	 * UNREAD_COUNT
	 */
	public static final String UNREAD_COUNT = "unreadCount"; //$NON-NLS-1$

	/**
	 * SHOW_MESSAGES
	 */
	public static final String SHOW_MESSAGES = "showMessages"; //$NON-NLS-1$

	private IMessageListener messageListener = new IMessageListener()
	{

		public void messageChanged(Message message, int eventType)
		{
			Map responseData = describeMessages();
			if (responseData != null)
			{
				Channel channel = bayeux.getChannel(MESSAGES_CHANNEL, true);
				if (channel != null)
				{
					channel.publish(client, responseData, getID(null));
				}
			}
		}

	};

	/**
	 * Creates a messages client
	 */
	public MessagesClient()
	{
		MessagingManager.addListener(messageListener);
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		super.destroy();
		MessagingManager.removeListener(messageListener);
	}

	private Map<Object, Object> describeMessages()
	{
		Map<Object, Object> responseData = new HashMap<Object, Object>();
		responseData.put(CometConstants.RESPONSE, DESCRIBE_MESSAGES);
		responseData.put(URGENT_COUNT, Integer.valueOf(MessagingManager.getUnreadUrgentMessages()));
		responseData.put(UNREAD_COUNT, Integer.valueOf(MessagingManager.getUnreadMessages()));
		return responseData;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (MESSAGES_CHANNEL.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map requestData = (Map) request;
				if (requestData.containsKey(CometConstants.REQUEST))
				{
					String requestType = requestData.get(CometConstants.REQUEST).toString();
					if (DESCRIBE_MESSAGES.equals(requestType))
					{
						return describeMessages();
					}
					else if (SHOW_MESSAGES.equals(requestType))
					{
						UIJob job = new UIJob(Messages.MessagesClient_Job_OpenMC)
						{

							public IStatus runInUIThread(IProgressMonitor monitor)
							{
								MessagesEditor.openMessageCenter();
								return Status.OK_STATUS;
							}

						};
						job.schedule();
					}
				}
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { MESSAGES_CHANNEL };
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}

}
