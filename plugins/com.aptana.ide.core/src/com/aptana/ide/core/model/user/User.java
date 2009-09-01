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
package com.aptana.ide.core.model.user;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.w3c.dom.Node;

import com.aptana.ide.core.ILogger;
import com.aptana.ide.core.model.FieldModelObject;
import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IServiceErrors;
import com.aptana.ide.core.model.IServiceProvider;
import com.aptana.ide.core.model.IServiceRequestBuilder;
import com.aptana.ide.core.model.ServiceErrors;
import com.aptana.ide.core.model.channel.Channel;
import com.aptana.ide.core.model.channel.ChannelType;
import com.aptana.ide.core.model.channel.Channels;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Paul Colton
 */
public class User extends FieldModelObject
{

	/**
	 * USER
	 */
	public static final String USER = "user"; //$NON-NLS-1$

	/**
	 * USERNAME
	 */
	public static final String USERNAME = "username"; //$NON-NLS-1$

	/**
	 * PASSWORD
	 */
	public static final String PASSWORD = "password"; //$NON-NLS-1$
	
	/**
	 * ENCRYPTED_PASSWORD
	 */
	public static final String ENCRYPTED_PASSWORD = "encryptedPassword"; //$NON-NLS-1$

	/**
	 * EMAIL
	 */
	public static final String EMAIL = "email"; //$NON-NLS-1$

	public static final String FIRST_NAME = "first_name"; //$NON-NLS-1$
	public static final String LAST_NAME = "last_name"; //$NON-NLS-1$
	public static final String PHONE = "phone"; //$NON-NLS-1$
	public static final String IP_ADDRESS = "ip_address"; //$NON-NLS-1$
	public static final String ADDRESS1 = "address1"; //$NON-NLS-1$
	public static final String ADDRESS2 = "address2"; //$NON-NLS-1$
	public static final String CITY = "city"; //$NON-NLS-1$
	public static final String STATE = "state"; //$NON-NLS-1$
	public static final String ZIP = "zipcode"; //$NON-NLS-1$
	public static final String COUNTRY = "country"; //$NON-NLS-1$
	
	/** 
	 * Profile fields
	 */
	public static final String ROLE = "role"; //$NON-NLS-1$
	public static final String COMPANY = "company"; //$NON-NLS-1$
	public static final String ORG_SIZE = "organization_size"; //$NON-NLS-1$
	public static final String ORG_TYPE = "organization_type"; //$NON-NLS-1$
	public static final String SITES_PER_YEAR = "sites_per_year"; //$NON-NLS-1$
	public static final String AJAX = "ajax"; //$NON-NLS-1$
	public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	public static final String PHP = "php"; //$NON-NLS-1$
	public static final String RUBY = "ruby"; //$NON-NLS-1$
	public static final String JAVA = "java"; //$NON-NLS-1$
	public static final String PYTHON = "python"; //$NON-NLS-1$
	public static final String NET = "net"; //$NON-NLS-1$
	public static final String SITE_DEVELOPMENT = "site_development"; //$NON-NLS-1$
	public static final String APPLICATION_DEVELOPMENT = "application_development"; //$NON-NLS-1$
	public static final String NEWSLETTER = "newsletter"; //$NON-NLS-1$
	public static final String CAPTCHA_CHALLENGE = "captcha_token"; //$NON-NLS-1$
	public static final String CAPTCHA_RESPONSE = "captcha_response";	 //$NON-NLS-1$
	public static final String CREATED_AT = "created_at";	 //$NON-NLS-1$

	
	/**
	 * User's channels
	 */
	protected Channels channels;

	/**
	 * User
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	public User(String username, String password, String email, String firstName, String lastName, String challenge, String response)
	{
		addField(USERNAME, null, username);
		addField(PASSWORD, null, password);
		addField(ENCRYPTED_PASSWORD, null, null);
		addField(EMAIL, null, email);
		addField(FIRST_NAME, null, firstName);
		addField(LAST_NAME, null, lastName);
		addField(PHONE, null, null);
		addField(IP_ADDRESS, null, null);
		addField(ADDRESS1, null, null);
		addField(ADDRESS2, null, null);
		addField(CITY, null, null);
		addField(STATE, null, null);
		addField(ZIP, null, null);
		addField(COUNTRY, null, null);		
		addField(ROLE, null, null);
		addField(COMPANY, null, null);
		addField(ORG_SIZE, null, null);
		addField(ORG_TYPE, null, null);
		addField(SITES_PER_YEAR, null, null);
		addField(AJAX, null, null);
		addField(JAVASCRIPT, null, null);
		addField(PHP, null, null);
		addField(RUBY, null, null);
		addField(JAVA, null, null);
		addField(PYTHON, null, null);
		addField(CAPTCHA_CHALLENGE, null, challenge);
		addField(CAPTCHA_RESPONSE, null, response);
		addField(CREATED_AT, null, null);
		
		this.channels = new Channels();
	}

	/**
	 * User
	 */
	public User()
	{
		this(null, null, null, null, null, null, null);
	}

	/**
	 * @see com.aptana.ide.core.model.BaseModelObject#addListener(com.aptana.ide.core.model.IModelListener)
	 */
	public void addListener(IModelListener listener)
	{
		super.addListener(listener);
		this.channels.addListener(listener);
	}

	/**
	 * @see com.aptana.ide.core.model.BaseModelObject#removeListener(com.aptana.ide.core.model.IModelListener)
	 */
	public void removeListener(IModelListener listener)
	{
		super.removeListener(listener);
		this.channels.removeListener(listener);
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#update()
	 */
	public IServiceErrors update()
	{
		IServiceErrors errors = super.update();
		if (hasLocation() && this.channels.getItems().length == 0)
		{
			this.channels.update();
		}
		return errors;
	}

	/**
	 * Returns true if the user has valid credentials
	 * 
	 * @return - true if valid, false otherwise
	 */
	public boolean hasCredentials()
	{
		return getUsername() != null && getUsername().length() > 0 && getPassword() != null
				&& getPassword().length() > 0;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return getField(PASSWORD);
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password)
	{
		setField(PASSWORD, password);
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return getField(USERNAME);
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username)
	{
		setField(USERNAME, username);
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return getField(EMAIL);
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email)
	{
		setField(EMAIL, email);
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#setLocation(java.net.URL)
	 */
	public void setLocation(URL location)
	{
		super.setLocation(location);
		if (hasLocation())
		{
			String currentLocation = getLocation().toExternalForm();
			if (!currentLocation.endsWith("/")) //$NON-NLS-1$
			{
				currentLocation += "/"; //$NON-NLS-1$
			}

			try
			{
				URL channelsURL = new URL(currentLocation + Channels.CHANNELS_ELEMENT);
				this.channels.setDefaultLocation(channelsURL);
			}
			catch (MalformedURLException e)
			{
				String message = MessageFormat.format(
					Messages.getString("User.Error_Generating_URL"), //$NON-NLS-1$
					new Object[] {
						e.getMessage()
					}
				);
				logError(message);
			}
		}
	}

	/**
	 * Adds a channel, requires this user to have a location
	 * 
	 * @param value
	 * @param type
	 * @return - added channel or null if error occurred while adding
	 */
	public Channel addChannel(String value, ChannelType type)
	{
		return addChannel(value, type, null);
	}

	/**
	 * Adds a channel, requires this user to have a location
	 * 
	 * @param value
	 * @param type
	 * @param errors
	 * @return - added channel or null if error occurred while adding
	 */
	public Channel addChannel(String value, ChannelType type, ServiceErrors errors)
	{
		Channel channel = null;
		if (hasLocation())
		{
			channel = new Channel(value, type);
			channel.setRequestBuilder(builder);
			channel.setServiceProvider(provider);
			channel.setLogger(logger);
			channel.setDefaultLocation(channels.getLocation());
			IServiceErrors channelErrors = channel.commit();
			if (errors != null && channelErrors instanceof ServiceErrors)
			{
				errors.cloneErrors((ServiceErrors) channelErrors);
			}
			if (channel.hasLocation())
			{
				channelErrors = channel.update();
				if (errors != null && channelErrors instanceof ServiceErrors)
				{
					errors.cloneErrors((ServiceErrors) channelErrors);
				}
				this.channels.update();
				for (Channel c : this.channels.getItems())
				{
					if (c.getId().equals(channel.getId()))
					{
						return c;
					}
				}
			}
			else
			{
				channel = null;
			}
		}
		return channel;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#setRequestBuilder(com.aptana.ide.core.model.IServiceRequestBuilder)
	 */
	public void setRequestBuilder(IServiceRequestBuilder builder)
	{
		super.setRequestBuilder(builder);
		if (this.channels != null)
		{
			this.channels.setRequestBuilder(getRequestBuilder());
		}
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#setServiceProvider(com.aptana.ide.core.model.IServiceProvider)
	 */
	public void setServiceProvider(IServiceProvider provider)
	{
		super.setServiceProvider(provider);
		if (this.channels != null)
		{
			this.channels.setServiceProvider(provider);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#setLogger(com.aptana.ide.core.ILogger)
	 */
	public void setLogger(ILogger logger)
	{
		super.setLogger(logger);
		if (this.channels != null)
		{
			this.channels.setLogger(getLogger());
		}
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#clear()
	 */
	public void clear()
	{
		if (this.channels != null)
		{
			this.channels.clear();
		}
		// clear out the fields
		setField(FIRST_NAME, null);
		setField(LAST_NAME, null);
		setField(PHONE, null);
		setField(IP_ADDRESS, null);
		setField(ADDRESS1, null);
		setField(ADDRESS2, null);
		setField(CITY, null);
		setField(STATE, null);
		setField(ZIP, null);
		setField(COUNTRY, null);		
		setField(ROLE, null);
		setField(COMPANY, null);
		setField(ORG_SIZE, null);
		setField(ORG_TYPE, null);
		setField(SITES_PER_YEAR, null);
		setField(AJAX, null);
		setField(JAVASCRIPT, null);
		setField(PHP, null);
		setField(RUBY, null);
		setField(JAVA, null);
		setField(PYTHON, null);
		setField(CAPTCHA_CHALLENGE, null);
		setField(CAPTCHA_RESPONSE, null);
		setField(CREATED_AT, null);
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
		return Messages.getString("User.LoggingPrefix"); //$NON-NLS-1$
	}

	/**
	 * @return the channels
	 */
	public Channels getChannels()
	{
		return channels;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getItemString()
	 */
	protected String getItemString()
	{
		return USER;
	}

}
