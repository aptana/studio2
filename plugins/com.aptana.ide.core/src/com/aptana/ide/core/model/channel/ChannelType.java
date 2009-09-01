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

import org.w3c.dom.Node;

import com.aptana.ide.core.model.CoreModelObject;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ChannelType extends CoreModelObject
{

	/**
	 * CHANNEL_TYPE_ELEMENT
	 */
	public static final String CHANNEL_TYPE_ELEMENT = "channel_type"; //$NON-NLS-1$

	/**
	 * NAME_ELEMENT
	 */
	public static final String NAME_ELEMENT = "name"; //$NON-NLS-1$

	private String name;

	/**
	 * Creates an empty channel type
	 */
	public ChannelType()
	{
		this(null, null);
	}

	/**
	 * Creates a new channel type with the specified id and name
	 * 
	 * @param id
	 * @param name
	 */
	public ChannelType(String id, String name)
	{
		this.name = name;
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getLoggingPrefix()
	 */
	public String getLoggingPrefix()
	{
		return Messages.getString("ChannelType.Logging_Prefix"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromNode(org.w3c.dom.Node)
	 */
	public void fromNode(Node node)
	{
		String name = getTextContent(ChannelType.NAME_ELEMENT, node);
		if (name != null && name.length() > 0)
		{
			setName(name);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromXML(java.lang.String)
	 */
	public void fromXML(String xml)
	{

	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toNode()
	 */
	public Node toNode()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toXML()
	 */
	public String toXML()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getItemString()
	 */
	protected String getItemString()
	{
		return CHANNEL_TYPE_ELEMENT;
	}

}
