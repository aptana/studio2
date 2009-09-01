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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.messagecenter.preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aptana.ide.core.StringUtils;

/**
 * FeedDescriptor
 * 
 * @author Ingo Muschenetz
 */
public class FeedDescriptor
{
	private String _name;
	private String _url;
	private long _interval = 14400000; // 5 minutes
	private Date _lastPolled = new Date();
	private boolean _trans = false;

	/**
	 * Separates the fields
	 */
	public static String FIELD_SEPARATOR = "~~~~"; //$NON-NLS-1$

	/**
	 * Separates the descriptors
	 */
	public static String DESCRIPTOR_SEPARATOR = "####"; //$NON-NLS-1$

	/**
	 * Creates a new feed descriptor
	 */
	public FeedDescriptor()
	{
	}

	/**
	 * Creates a new descriptor
	 */
	public FeedDescriptor(String name, String url, long interval, Date lastPolled, boolean trans)
	{
		if (name == null)
		{
			this._name = url;
		}
		this._url = url;
		this._interval = interval;
		if (lastPolled != null)
		{
			this._lastPolled = lastPolled;
		}
		this._trans = trans;
	}

	/**
	 * @return is the item transient?
	 */
	public boolean isTransient()
	{
		return _trans;
	}

	/**
	 * Is the item transient (meaning it won't be saved)
	 * 
	 * @param trans
	 */
	public void setTransient(boolean trans)
	{
		this._trans = trans;
	}

	/**
	 * Gets the name of the feed
	 * 
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Gets the url of the feed
	 * 
	 * @return String
	 */
	public String getUrl()
	{
		return _url;
	}

	/**
	 * Gets the interval of the feed polling
	 * 
	 * @return long
	 */
	public long getPollInterval()
	{
		return _interval;
	}

	/**
	 * Gets the last polling time
	 * 
	 * @return long
	 */
	public Date getLastPoll()
	{
		return _lastPolled;
	}

	/**
	 * Sets the name of the feed
	 * 
	 * @param fileName
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * Sets the url of the feed
	 * 
	 * @param folderPath
	 */
	public void setUrl(String url)
	{
		this._url = url;
	}

	/**
	 * Sets the url of the feed
	 * 
	 * @param folderPath
	 */
	public void setPollInterval(long interval)
	{
		this._interval = interval;
	}

	/**
	 * Sets the date/time this item was last polled
	 * 
	 * @param folderPath
	 */
	public void setLastPoll(Date lastPolled)
	{
		this._lastPolled = lastPolled;
	}

	/**
	 * Returns this descriptor as a string
	 * 
	 * @return String
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		if (getName() != null)
		{
			sb.append(getName());
		}
		else
		{
			sb.append(getUrl());
		}
		sb.append(FIELD_SEPARATOR);
		sb.append(getUrl());
		sb.append(FIELD_SEPARATOR);
		sb.append(getPollInterval());
		sb.append(FIELD_SEPARATOR);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");		 //$NON-NLS-1$
		sb.append(sdf.format(getLastPoll()));

		return sb.toString();
	}

	/**
	 * Converts the string into an error descriptor
	 * 
	 * @param values
	 */
	public void fromString(String values)
	{
		String[] vals = values.split(FIELD_SEPARATOR, 4);

		if (!StringUtils.EMPTY.equals(vals[0]))
		{
			_name = vals[0];
		}

		if (!StringUtils.EMPTY.equals(vals[1]))
		{
			_url = vals[1];
		}

		if (vals.length > 2)
		{
			if (!StringUtils.EMPTY.equals(vals[2]))
			{
				_interval = Long.parseLong(vals[2]);
			}
		}

		if (vals.length > 3)
		{
			if (!StringUtils.EMPTY.equals(vals[3]))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
				try
				{
					_lastPolled = sdf.parse(vals[3]);
				}
				catch (ParseException e)
				{
					_lastPolled = new Date();
				}
			}
		}

	}

	/**
	 * Returns a string consisting of all error descriptors
	 * 
	 * @param errors
	 * @return String
	 */
	public static String serializeFeedDescriptors(FeedDescriptor[] errors)
	{
		List<String> al = new ArrayList<String>();
		for (int i = 0; i < errors.length; i++)
		{
			FeedDescriptor descriptor = errors[i];
			if (!descriptor.isTransient())
			{
				al.add(descriptor.toString());
			}
		}

		return StringUtils.join(DESCRIPTOR_SEPARATOR, al.toArray(new String[0]));
	}

	/**
	 * Returns an array consisting of all error descriptors
	 * 
	 * @param errors
	 * @return ErrorDescriptor[]
	 */
	public static FeedDescriptor[] deserializeFeedDescriptors(String errors)
	{
		if (StringUtils.EMPTY.equals(errors))
		{
			return new FeedDescriptor[0];
		}

		List<FeedDescriptor> al = new ArrayList<FeedDescriptor>();
		String[] errorDescriptors = errors.split(DESCRIPTOR_SEPARATOR);
		for (int i = 0; i < errorDescriptors.length; i++)
		{
			String descriptor = errorDescriptors[i];
			FeedDescriptor ed = new FeedDescriptor();
			ed.fromString(descriptor);
			al.add(ed);
		}

		return al.toArray(new FeedDescriptor[0]);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0)
	{
		boolean result = false;

		if (arg0 instanceof FeedDescriptor)
		{
			FeedDescriptor s = ((FeedDescriptor) arg0);
			result = getUrl().equals(s.getUrl());
		}
		return result;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.toString().hashCode();
	}
}
