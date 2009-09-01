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
package com.aptana.ide.server.jetty.comet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.jetty.JettyPlugin;

import dojox.cometd.Bayeux;
import dojox.cometd.Channel;
import dojox.cometd.Client;
import dojox.cometd.Message;

/**
 * Comet client
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class CometClient extends ListenerAdapter implements ICometClient
{

	/**
	 * The bayeux object that this client publishes messages to
	 */
	protected Bayeux bayeux;

	/**
	 * Client id
	 */
	protected String id;

	/**
	 * Comet client object
	 */
	protected Client client;
	private List<Channel> channels;

	/**
	 * Creates a new cloud client
	 */
	public CometClient()
	{
		this.id = null;
		this.client = null;
		this.bayeux = null;
		this.channels = new ArrayList<Channel>();
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ICometClient#init(dojox.cometd.Bayeux)
	 */
	public void init(Bayeux bayeux)
	{
		this.bayeux = bayeux;
		this.client = this.bayeux.newClient(getID());
		this.client.addListener(this);
		for (String channelId : getSubscriptionIDs())
		{
			Channel cometChannel = this.bayeux.getChannel(channelId, true);
			cometChannel.subscribe(this.client);
			this.channels.add(cometChannel);
		}
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ICometClient#destroy()
	 */
	public void destroy()
	{
		for (Channel channel : this.channels)
		{
			channel.unsubscribe(this.client);
		}
		if (this.client != null)
		{
			this.client.removeListener(this);
		}
		this.bayeux = null;
		this.channels.clear();
		this.client = null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ICometClient#getChannels()
	 */
	public Channel[] getChannels()
	{
		return this.channels.toArray(new Channel[0]);
	}

	/**
	 * Gets the subscription ids to subscribe to in the init(Bayeux bayeux)
	 * 
	 * @return - channel ids
	 */
	protected abstract String[] getSubscriptionIDs();

	/**
	 * @see dojox.cometd.MessageListener#deliver(dojox.cometd.Client, dojox.cometd.Client, dojox.cometd.Message)
	 */
	public void deliver(final Client fromClient, Client toClient, final Message msg)
	{
		if (fromClient != this.client)
		{
			IdeLog.logInfo(JettyPlugin.getDefault(), MessageFormat.format(
                    Messages.CometClient_INF_ReceivedMessage,
                    msg.getChannel(), StringUtils.getPublishableMessage(msg.getData())));
			Job job = new Job("Publishing comet message") //$NON-NLS-1$
			{

				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						Object response = getResponse(msg.getChannel(), msg.getData());
						if (response != null)
						{
							String replyChannel = null;
							if (msg.getData() instanceof Map)
							{
								Object channelObject = ((Map) msg.getData()).get(CometConstants.RETURN_CHANNEL);
								if (channelObject != null)
								{
									replyChannel = channelObject.toString();
								}
							}
							if (replyChannel == null)
							{
								replyChannel = getReplyChannel(msg.getChannel(), fromClient.getId());
							}
							String rId = getID(msg.getId());
							
							// Avoid NPE
							Bayeux localBayeux = bayeux;
							if (localBayeux != null && replyChannel != null
                                    && replyChannel.trim().length() != 0) {
								Channel channel = localBayeux.getChannel(replyChannel, true);
								if (channel != null)
								{
									IdeLog.logInfo(JettyPlugin.getDefault(), MessageFormat
                                            .format(
                                                    Messages.CometClient_INF_RespondMessage,
                                                    replyChannel, StringUtils.getPublishableMessage(response), rId));
									channel.publish(client, response, rId);
								}
							}
						}
					}
					catch (Exception e)
					{
						IdeLog.logError(JettyPlugin.getDefault(), Messages.CometClient_ERR_CometResponse, e);
					}
					return Status.OK_STATUS;
				}

			};
			job.setSystem(true);
			job.setPriority(Job.BUILD);
			job.schedule();
		}
	}

	/**
	 * Generates a response object from a request object delivered on this client's channel
	 * 
	 * @param toChannel
	 * @param request
	 * @return - response
	 */
	protected abstract Object getResponse(String toChannel, Object request);

	/**
	 * Generates a response id from a request id
	 * 
	 * @param msgId
	 * @return - published message id
	 */
	protected abstract String getID(String msgId);

	/**
	 * Generates a reply channel from the channel received with the message id
	 * 
	 * @param toChannel
	 * @param publisherId
	 * @return - by default the same channel
	 */
	protected String getReplyChannel(String toChannel, String publisherId)
	{
		return toChannel;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ListenerAdapter#removed(java.lang.String, boolean)
	 */
	public void removed(String clientId, boolean timeout)
	{

	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ICometClient#getID()
	 */
	public String getID()
	{
		return this.id;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.ICometClient#setID(java.lang.String)
	 */
	public void setID(String id)
	{
		if (id != null && this.id == null)
		{
			this.id = id;
		}
	}

}
