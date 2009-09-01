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

import java.text.MessageFormat;

import org.w3c.dom.Node;

import com.aptana.ide.core.model.FieldModelObject;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Channel extends FieldModelObject
{

	/**
	 * CHANNEL_ELEMENT
	 */
	public static final String CHANNEL_ELEMENT = "channel"; //$NON-NLS-1$

	/**
	 * USER_ID_ELEMENT
	 */
	public static final String USER_ID_ELEMENT = "user_id"; //$NON-NLS-1$

	/**
	 * PRIMARY_ELEMENT
	 */
	public static final String PRIMARY_ELEMENT = "is_primary"; //$NON-NLS-1$

	/**
	 * VALUE_ELEMENT
	 */
	public static final String VALUE_ELEMENT = "value"; //$NON-NLS-1$

	/**
	 * CHANNEL_TYPE_ID_ELEMENT
	 */
	public static final String CHANNEL_TYPE_ID_ELEMENT = "channel_type_id"; //$NON-NLS-1$

	private ChannelType channelType;

	/**
	 * Creates an empty channel
	 */
	public Channel()
	{
		this(null, null);
	}

	/**
	 * Creates a new channel
	 * 
	 * @param value
	 * @param channeType
	 */
	public Channel(String value, ChannelType channeType)
	{
		this.channelType = channeType;
		addField(PRIMARY_ELEMENT, null, null);
		addField(VALUE_ELEMENT, null, null);
		setField(VALUE_ELEMENT, value);
	}

	/**
	 * @return the channelType
	 */
	public ChannelType getChannelType()
	{
		return channelType;
	}

	/**
	 * @param channelType
	 *            the channelType to set
	 */
	public void setChannelType(ChannelType channelType)
	{
		this.channelType = channelType;
	}

	/**
	 * @see com.aptana.ide.core.model.FieldModelObject#parseNestedElements(org.w3c.dom.Node)
	 */
	protected boolean parseNestedElements(Node node)
	{
		boolean changed = false;
		try
		{

			String type = getTextContent(CHANNEL_TYPE_ID_ELEMENT, node);
			for (ChannelType channelType : ChannelTypes.getInstance().getItems())
			{
				if (channelType.getId().equals(type))
				{
					setChannelType(channelType);
					changed = true;
					break;
				}
			}
		}
		catch (Exception e1)
		{
			String message = MessageFormat.format(
				Messages.getString("Channel.XPath_Error_Building_XML"), //$NON-NLS-1$
				new Object[] {
					e1.getMessage()
				}
			);
			logError(message);
		}
		return changed;
	}

	/**
	 * @see com.aptana.ide.core.model.FieldModelObject#addNestedElementXML(java.lang.StringBuffer)
	 */
	protected void addNestedElementXML(StringBuffer buffer)
	{
		if (getChannelType() != null)
		{
			buffer.append("<" + Channel.CHANNEL_TYPE_ID_ELEMENT + ">" + getChannelType().getId() + "</" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ Channel.CHANNEL_TYPE_ID_ELEMENT + ">"); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toNode()
	 */
	public Node toNode()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getLoggingPrefix()
	 */
	public String getLoggingPrefix()
	{
		return Messages.getString("Channel.LoggingPrefix"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getItemString()
	 */
	protected String getItemString()
	{
		return CHANNEL_ELEMENT;
	}

	/**
	 * Gets the value of the channel
	 * 
	 * @return - channel value field
	 */
	public String getValue()
	{
		return getField(VALUE_ELEMENT);
	}

	/**
	 * Gets the value of the is primary field for this channel
	 * 
	 * @return - is primary field
	 */
	public boolean isPrimary()
	{
		return getBooleanField(PRIMARY_ELEMENT);
	}
}
