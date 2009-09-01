/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.model.channel;

import java.net.MalformedURLException;
import java.net.URL;

import com.aptana.ide.core.model.CoreGroupObject;
import com.aptana.ide.core.model.ILocationObject;
import com.aptana.ide.core.model.IServiceRequest;
import com.aptana.ide.core.model.IServiceRequestBuilder;
import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.model.SimpleServiceRequest;
import com.aptana.ide.core.model.user.AptanaUser;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ChannelTypes extends CoreGroupObject<ChannelType>
{

	/**
	 * CHANNEL_TYPES
	 */
	public static final String CHANNEL_TYPES = AptanaUser.BASE_URL + "/channel_types"; //$NON-NLS-1$

	private static URL CHANNEL_TYPES_URL;

	static
	{
		try
		{
			CHANNEL_TYPES_URL = new URL(CHANNEL_TYPES);
		}
		catch (MalformedURLException e)
		{
			CHANNEL_TYPES_URL = null;
		}
	}

	/**
	 * CHANNEL_TYPES_ELEMENT
	 */
	public static final String CHANNEL_TYPES_ELEMENT = "channel_types"; //$NON-NLS-1$

	private static ChannelTypes channelTypes;

	private ChannelTypes()
	{
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#getItems()
	 */
	public ChannelType[] getItems()
	{
		if (children.isEmpty())
		{
			super.update();
		}
		return super.getItems();
	}

	/**
	 * Gets the channel types singleton
	 * 
	 * @return - channel type
	 */
	public static ChannelTypes getInstance()
	{
		if (channelTypes == null)
		{
			channelTypes = new ChannelTypes();
			channelTypes.setServiceProvider(new RESTServiceProvider());
			channelTypes.setRequestBuilder(new IServiceRequestBuilder()
			{

				public IServiceRequest generateRequest(ILocationObject object, String type)
				{
					return new SimpleServiceRequest(null, "", IServiceRequest.UPDATE, null); //$NON-NLS-1$
				}

			});
			channelTypes.setLocation(CHANNEL_TYPES_URL);
			channelTypes.update();
		}
		return channelTypes;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getLoggingPrefix()
	 */
	public String getLoggingPrefix()
	{
		return Messages.getString("ChannelTypes.LoggingPrefix"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#createEmptyArray()
	 */
	protected ChannelType[] createEmptyArray()
	{
		return new ChannelType[0];
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#createItem()
	 */
	public ChannelType createItem()
	{
		return new ChannelType();
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#getGroupString()
	 */
	protected String getGroupString()
	{
		return CHANNEL_TYPES_ELEMENT;
	}

	/**
	 * @param item
	 * @return - true if the item should be added
	 * @see com.aptana.ide.core.model.CoreGroupObject#shouldAdd(com.aptana.ide.core.model.CoreModelObject)
	 */
	public boolean shouldAdd(ChannelType item)
	{
		return item.getId() != null && item.getName() != null;
	}

}
