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
package com.aptana.ide.intro.messaging;

import java.io.Serializable;
import java.util.Date;

/**
 * Message model class
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Message implements Serializable
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private String address;
	private String content;
	private String preview;
	private String id;
	private String site;
	private Date date;
	private boolean read;
	private boolean urgent;
	private boolean deleted;
	private boolean purged;
	private String channelTitle = ""; //$NON-NLS-1$

	/**
	 * Creates a new empty message
	 */
	public Message()
	{
		this(null, null, null, null, null, null, null, null);
	}

	/**
	 * Creates a new message from the specified fields
	 * 
	 * @param title
	 * @param address
	 * @param content
	 * @param preview
	 * @param id
	 * @param site
	 * @param date
	 * @param channelTitle 
	 */
	public Message(String title, String address, String content, String preview, String id, String site, Date date, String channelTitle)
	{
		this.title = title;
		this.address = address;
		this.content = content;
		this.preview = preview;
		this.id = id;
		read = false;
		urgent = false;
		deleted = false;
		purged = false;
		this.site = site;
		this.date = date;
		this.channelTitle = channelTitle;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * @return the preview
	 */
	public String getPreview()
	{
		return preview;
	}

	/**
	 * @param preview
	 *            the preview to set
	 */
	public void setPreview(String preview)
	{
		this.preview = preview;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the read
	 */
	public boolean isRead()
	{
		return read;
	}

	/**
	 * @param read
	 *            the read to set
	 */
	public void setRead(boolean read)
	{
		this.read = read;
	}

	/**
	 * @return the site
	 */
	public String getSite()
	{
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(String site)
	{
		this.site = site;
	}

	/**
	 * @return the urgent
	 */
	public boolean isUrgent()
	{
		return urgent;
	}

	/**
	 * @param urgent
	 *            the urgent to set
	 */
	public void setUrgent(boolean urgent)
	{
		this.urgent = urgent;
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted()
	{
		return deleted;
	}
	
	/**
	 * @param deleted
	 *            the deleted to set
	 */
	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
		
	/**
	 * @return the purged
	 */
	public boolean isPurged()
	{
		return purged;
	}

	/**
	 * @param purged
	 *            the purged to set
	 */
	public void setPurged(boolean purged)
	{
		this.purged = purged;
	}
	
	/**
	 * @return the title
	 */
	public String getChannelTitle()
	{
		return channelTitle == null ? "" : channelTitle; //$NON-NLS-1$
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setChannelTitle(String title)
	{
		this.channelTitle = title;
	}

}
