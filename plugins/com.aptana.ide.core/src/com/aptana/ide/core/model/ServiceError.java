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
package com.aptana.ide.core.model;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ServiceError extends CoreModelObject
{

	/**
	 * ERROR_ELEMENT
	 */
	public static final String ERROR_ELEMENT = "error"; //$NON-NLS-1$

	private String message;

	/**
	 * Creates an empty service error
	 */
	public ServiceError()
	{
		this.message = null;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getLoggingPrefix()
	 */
	public String getLoggingPrefix()
	{
		return Messages.getString("ServiceError.Logging_Prefix"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromNode(org.w3c.dom.Node)
	 */
	public void fromNode(Node node)
	{
		try
		{
			String message = XPATH.evaluate("text()", node); //$NON-NLS-1$
			if (message != null && message.length() > 0)
			{
				setMessage(message);
			}
		}
		catch (XPathExpressionException e1)
		{
			logError(Messages.getString("ServiceError.XPath_Parsing_Error") + e1.getMessage()); //$NON-NLS-1$
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
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getItemString()
	 */
	protected String getItemString()
	{
		return ERROR_ELEMENT;
	}

}
