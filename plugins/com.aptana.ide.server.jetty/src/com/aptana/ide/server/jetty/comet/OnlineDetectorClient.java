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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.aptana.ide.core.model.ServiceErrors;
import com.aptana.ide.core.online.IOnlineStateChangedListener;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.online.OnlineDetectionService.StatusMode;

import dojox.cometd.Channel;

/**
 * @author Paul Colton
 */
public class OnlineDetectorClient extends CometClient
{

	private static final String NETWORK_ONLINE_CHANNEL = "/portal/network/online"; //$NON-NLS-1$
	
	public static final String CHECK_ONLINE_STATUS = "checkOnlineStatus"; //$NON-NLS-1$

	public static final String ONLINE_STATE_CHANGE = "onlineStateChange"; //$NON-NLS-1$
	
	public static final String ADD_URL = "addURL"; //$NON-NLS-1$
	
	public static final String STATUS = "status"; //$NON-NLS-1$
	
	public static final String OLD_STATUS = "old_status"; //$NON-NLS-1$

	public static final String URL_MONITOR = "url"; //$NON-NLS-1$

	private IOnlineStateChangedListener onlineStateChangeListener = new IOnlineStateChangedListener()
	{
		public void stateChanged(StatusMode oldState, StatusMode newState)
		{
			Map<Object, Object> responseData = new HashMap<Object, Object>();
			
			responseData.put(CometConstants.RESPONSE, ONLINE_STATE_CHANGE);
			responseData.put(STATUS, newState.toString());
			responseData.put(OLD_STATUS, oldState.toString());
			
			Channel channel = bayeux.getChannel(NETWORK_ONLINE_CHANNEL, true);

			if (channel != null)
			{
				channel.publish(client, responseData, getID(null));
			}
		}

	};
	
	/**
	 * Creates a detector client
	 */
	public OnlineDetectorClient()
	{
		OnlineDetectionService.addListener(onlineStateChangeListener);
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		OnlineDetectionService.removeListener(onlineStateChangeListener);
		super.destroy();
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (NETWORK_ONLINE_CHANNEL.equals(toChannel) && request instanceof Map)
		{
			Map requestData = (Map) request;
			
			if (CHECK_ONLINE_STATUS.equals(requestData.get(CometConstants.REQUEST)))
			{
				Map<Object, Object> response = new HashMap<Object, Object>();
				response.put(CometConstants.RESPONSE, CHECK_ONLINE_STATUS);
				response.put(STATUS, OnlineDetectionService.getInstance().getStatus().toString());
				return response;
			}
			else if (ADD_URL.equals(requestData.get(CometConstants.REQUEST)))
			{
				String url = (String) requestData.get(URL_MONITOR);

				Map<Object, Object> response = new HashMap<Object, Object>();
				response.put(CometConstants.RESPONSE, ADD_URL);
				
				if(url != null)
				{
					try
					{
						OnlineDetectionService.getInstance().addDetectionURL(new URL(url));
					}
					catch (MalformedURLException e)
					{
						response.put(ServiceErrors.ERRORS_ELEMENT, new String[] { e.getMessage() });
					}
					return response;
				}
				else
				{
					response.put(ServiceErrors.ERRORS_ELEMENT, new String[] { "'url' is null" }); //$NON-NLS-1$
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
		return new String[] { NETWORK_ONLINE_CHANNEL };
	}
	
	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}
	
}
