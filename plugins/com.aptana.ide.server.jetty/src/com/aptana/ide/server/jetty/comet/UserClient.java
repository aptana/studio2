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
package com.aptana.ide.server.jetty.comet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;

import sun.misc.BASE64Decoder;

import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IModifiableObject;
import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.model.ServiceError;
import com.aptana.ide.core.model.ServiceErrors;
import com.aptana.ide.core.model.channel.ChannelType;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.model.user.UserRequestBuilder;
import com.aptana.ide.core.model.user.UsernameAvailable;
import com.aptana.ide.core.model.user.UsernameResolver;

import dojox.cometd.Bayeux;
import dojox.cometd.Channel;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UserClient extends CometClient
{

	/**
	 * SUCCESS
	 */
	public static final String SUCCESS = "success"; //$NON-NLS-1$

	/**
	 * USER_VALID
	 */
	public static final String USER_VALID = "userValid"; //$NON-NLS-1$

	/**
	 * USERNAME_VALID
	 */
	public static final String USERNAME_VALID = "usernameValid"; //$NON-NLS-1$

	/**
	 * PASSWORD_VALID
	 */
	public static final String PASSWORD_VALID = "passwordValid"; //$NON-NLS-1$

	/**
	 * RESOLVE_USER_ACTION
	 */
	public static final String RESOLVE_USER_ACTION = "resolveUser"; //$NON-NLS-1$

	/**
	 * CREATE_USER_ACTION
	 */
	public static final String CREATE_USER_ACTION = "createUser"; //$NON-NLS-1$

	/**
	 * LOGIN_USER_ACTION
	 */
	public static final String LOGIN_USER_ACTION = "loginUser"; //$NON-NLS-1$

	/**
	 * LOGOUT_USER
	 */
	public static final String LOGOUT_USER_ACTION = "logoutUser"; //$NON-NLS-1$

	/**
	 * CURRENT_USER_ACTION
	 */
	public static final String CURRENT_USER_ACTION = "currentUser"; //$NON-NLS-1$

	/**
	 * UPDATE_USER_ACTION
	 */
	public static final String UPDATE_USER_ACTION = "updateUser"; //$NON-NLS-1$

	/**
	 * CURRENT_USER
	 */
	public static final String CURRENT_USER = "/portal/user"; //$NON-NLS-1$

	/**
	 * LOGIN_USER
	 */
	public static final String LOGIN_USER = "/portal/user/login"; //$NON-NLS-1$

	/**
	 * LOGOUT_USER
	 */
	public static final String LOGOUT_USER = "/portal/user/logout"; //$NON-NLS-1$

	/**
	 * CREATE_USER
	 */
	public static final String CREATE_USER = "/portal/user/create"; //$NON-NLS-1$

	/**
	 * RESOLVE_USER
	 */
	public static final String RESOLVE_USER = "/portal/user/resolve"; //$NON-NLS-1$

	/**
	 * RESOLVE_USER
	 */
	public static final String UPDATE_USER = "/portal/user/update"; //$NON-NLS-1$

	/**
	 * EMAIL_CHANNEL
	 */
	public static final String EMAIL_CHANNEL = "1"; //$NON-NLS-1$

	private IModelListener listener = new IModelListener()
	{

		public void modelChanged(IModifiableObject object)
		{
			if (object instanceof User)
			{
				Map<Object, Object> responseData = new HashMap<Object, Object>();
				responseData.put(CometConstants.RESPONSE, CURRENT_USER_ACTION);
				User user = (User) object;
				
				if(user.hasCredentials())
				{
					fillResponse(responseData, user);
					Bayeux localBayeux = bayeux;
					if (localBayeux != null) {
						Channel userChannel = localBayeux.getChannel(CURRENT_USER, true);
						userChannel.publish(client, responseData, getID(null));
					}
				}
				else
				{
					responseData.put(CometConstants.RESPONSE, LOGOUT_USER_ACTION);
					responseData.put(SUCCESS, Boolean.TRUE);
					Bayeux localBayeux = bayeux;
					if (localBayeux != null) {
						Channel userChannel = localBayeux.getChannel(LOGOUT_USER, true);
						userChannel.publish(client, responseData, getID(null));
					}
				}
			}
		}
	};

	/**
	 * Creates a new user client
	 */
	public UserClient()
	{
	    AptanaUser.getSignedInUser().addListener(listener);
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		AptanaUser.getSignedInUser().removeListener(listener);
		super.destroy();
	}

	private void fillResponse(Map<Object, Object> responseData, User user)
	{
		if (user.hasCredentials())
		{
			try
			{
				BASE64Decoder decoder = new BASE64Decoder();
				String encryptedPassword = user.getField(User.ENCRYPTED_PASSWORD);
				String password = null;
				if (encryptedPassword != null)
				{
					password = new String(decoder.decodeBuffer(encryptedPassword));
				}
				else
				{
					password = user.getField(User.PASSWORD);
				}
				responseData.put(User.PASSWORD, password);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			responseData.put(User.ID_ELEMENT, user.getField(User.ID_ELEMENT));
			responseData.put(User.USERNAME, user.getField(User.USERNAME));
			responseData.put(User.FIRST_NAME, user.getField(User.FIRST_NAME));
			responseData.put(User.LAST_NAME, user.getField(User.LAST_NAME));
			responseData.put(User.PHONE, user.getField(User.PHONE));
			responseData.put(User.IP_ADDRESS, user.getField(User.IP_ADDRESS));
			responseData.put(User.ADDRESS1, user.getField(User.ADDRESS1));
			responseData.put(User.ADDRESS2, user.getField(User.ADDRESS2));
			responseData.put(User.CITY, user.getField(User.CITY));
			responseData.put(User.STATE, user.getField(User.STATE));
			responseData.put(User.ZIP, user.getField(User.ZIP));
			responseData.put(User.COUNTRY, user.getField(User.COUNTRY));

			responseData.put(User.ROLE, user.getField(User.ROLE));
			responseData.put(User.COMPANY, user.getField(User.COMPANY));
			responseData.put(User.ORG_TYPE, user.getField(User.ORG_TYPE));
			responseData.put(User.ORG_SIZE, user.getField(User.ORG_SIZE));
			responseData.put(User.SITES_PER_YEAR, user.getField(User.SITES_PER_YEAR));
			responseData.put(User.AJAX, Boolean.parseBoolean(user.getField(User.AJAX)));
			responseData.put(User.JAVASCRIPT, Boolean.parseBoolean(user.getField(User.JAVASCRIPT)));
			responseData.put(User.PHP, Boolean.parseBoolean(user.getField(User.PHP)));
			responseData.put(User.RUBY, Boolean.parseBoolean(user.getField(User.RUBY)));
			responseData.put(User.JAVA, Boolean.parseBoolean(user.getField(User.JAVA)));
			responseData.put(User.PYTHON, Boolean.parseBoolean(user.getField(User.PYTHON)));
			responseData.put(User.NET, Boolean.parseBoolean(user.getField(User.NET)));
			responseData.put(User.SITE_DEVELOPMENT, Boolean.parseBoolean(user.getField(User.SITE_DEVELOPMENT)));
			responseData.put(User.APPLICATION_DEVELOPMENT, Boolean.parseBoolean(user.getField(User.APPLICATION_DEVELOPMENT)));
			responseData.put(User.NEWSLETTER, Boolean.parseBoolean(user.getField(User.NEWSLETTER)));
			responseData.put(User.CREATED_AT, user.getField(User.CREATED_AT));

			for (com.aptana.ide.core.model.channel.Channel channel : user.getChannels().getItems())
			{
				ChannelType type = channel.getChannelType();
				if (type != null && EMAIL_CHANNEL.equals(type.getId()) && channel.isPrimary())
				{
					responseData.put(User.EMAIL, channel.getValue());
					break;
				}
			}
		}
		else
		{
			responseData.put(User.ID_ELEMENT, Boolean.FALSE);
			responseData.put(User.USERNAME, Boolean.FALSE);
			responseData.put(User.PASSWORD, Boolean.FALSE);
			responseData.put(User.EMAIL, Boolean.FALSE);
		}
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (CURRENT_USER.equals(toChannel))
		{
			Map<Object, Object> responseData = new HashMap<Object, Object>();
			responseData.put(CometConstants.RESPONSE, CURRENT_USER_ACTION);
			fillResponse(responseData, AptanaUser.getSignedInUser());
			return responseData;
		}
		else if (LOGIN_USER.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map values = (Map) request;
				if (values.containsKey(User.USERNAME) && values.containsKey(User.PASSWORD))
				{
					final String name = values.get(User.USERNAME).toString();
					final String password = values.get(User.PASSWORD).toString();
					Map<Object, Object> responseData = new HashMap<Object, Object>();

					final User user = new User(name, password, null, null, null, null, null);
					user.setRequestBuilder(new UserRequestBuilder());
					user.setServiceProvider(new RESTServiceProvider());

					responseData.put(CometConstants.RESPONSE, LOGIN_USER_ACTION);
					try
					{
						user.setDefaultLocation(new URL(AptanaUser.LOGINS));
						// Get the user location
						user.update();
						if (user.hasLocation())
						{
							// Get the user model
							user.update();
							// Call the sign-in method in the UI thread to avoid issue
                            // http://support.aptana.com/asap/browse/STU-4438
							Display.getDefault().syncExec(new Runnable()
							{

                                public void run()
                                {
                                    AptanaUser.signIn(name, password, user.getLocation(), user.getId());
                                }
							    
							});
							responseData.put(USER_VALID, Boolean.TRUE);
							responseData.put(USERNAME_VALID, Boolean.TRUE);
							responseData.put(PASSWORD_VALID, Boolean.TRUE);
							responseData.put(User.ID_ELEMENT, user.getId());
							responseData.put(User.USERNAME, user.getUsername());
							responseData.put(User.PASSWORD, user.getPassword());
						}
						else
						{
							responseData.put(USER_VALID, Boolean.FALSE);
							responseData.put(USERNAME_VALID, Boolean.FALSE);
							responseData.put(PASSWORD_VALID, Boolean.FALSE);
						}
					}
					catch (MalformedURLException e)
					{
					}
					return responseData;
				}
			}
		}
		else if (LOGOUT_USER.equals(toChannel))
		{
			Map<Object, Object> responseData = new HashMap<Object, Object>();
			responseData.put(CometConstants.RESPONSE, LOGOUT_USER_ACTION);
			try
			{
				AptanaUser.signOut();
				responseData.put(SUCCESS, Boolean.TRUE);
			}
			catch (Exception e)
			{
				responseData.put(SUCCESS, Boolean.FALSE);
			}
			return responseData;
		}
		else if (CREATE_USER.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map values = (Map) request;
				if (values.containsKey(User.USERNAME) && values.containsKey(User.PASSWORD)
						&& values.containsKey(User.EMAIL))
				{
					String name = values.get(User.USERNAME).toString();
					String firstName = values.get(User.FIRST_NAME).toString();
					String lastName = values.get(User.LAST_NAME).toString();
					String password = values.get(User.PASSWORD).toString();
					String email = values.get(User.EMAIL).toString();
					String challenge = values.get(User.CAPTCHA_CHALLENGE).toString();
					String response = values.get(User.CAPTCHA_RESPONSE).toString();
					Map<Object, Object> responseData = new HashMap<Object, Object>();
					responseData.put(CometConstants.RESPONSE, CREATE_USER_ACTION);

					// TODO service for password validity?
					responseData.put(PASSWORD_VALID, Boolean.TRUE);

					final User user = new User(name, password, email, firstName, lastName, challenge, response);
					user.setRequestBuilder(new UserRequestBuilder());
					user.setServiceProvider(new RESTServiceProvider());
					try
					{
						user.setDefaultLocation(new URL(AptanaUser.USERS));
						user.commit();
						if (user.hasLocation())
						{
							user.update();
							responseData.put(USERNAME_VALID, Boolean.TRUE);
							responseData.put(SUCCESS, Boolean.TRUE);
							// Call the sign-in method in the UI thread to avoid issue
                            // http://support.aptana.com/asap/browse/STU-4438
                            Display.getDefault().syncExec(new Runnable()
                            {

                                public void run()
                                {
                                    AptanaUser.signIn(user.getUsername(), user
                                            .getPassword(), user.getLocation(),
                                            user.getId());
                                }
                                
                            });
						}
						else
						{
							responseData.put(USERNAME_VALID, Boolean.valueOf(UsernameAvailable
									.isUsernameAvailable(name)));
							responseData.put(SUCCESS, Boolean.FALSE);
							List<String> errors = new ArrayList<String>();
							ServiceErrors serviceErrors = user.getLastServiceErrors();
							if (serviceErrors != null && serviceErrors.getItems() != null)
							{
								for (ServiceError se : serviceErrors.getItems())
								{
									errors.add(se.getMessage());
								}
							}
							responseData.put(ServiceErrors.ERRORS_ELEMENT, errors);
						}
					}
					catch (MalformedURLException e)
					{
						responseData.put(USERNAME_VALID, Boolean.valueOf(UsernameAvailable.isUsernameAvailable(name)));
						responseData.put(ServiceErrors.ERRORS_ELEMENT, Arrays.asList(new String[] { e.getMessage() }));
					}
					responseData.put(CometConstants.RESPONSE, CREATE_USER_ACTION);
					return responseData;
				}
			}
		}
		else if (RESOLVE_USER.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map values = (Map) request;
				if (values.containsKey(User.EMAIL))
				{
					String email = values.get(User.EMAIL).toString();
					User possibleUser = UsernameResolver.doesUsernameResolve(email);
					if (possibleUser != null)
					{
						Map<Object, Object> responseData = new HashMap<Object, Object>();
						responseData.put(User.USERNAME, possibleUser.getUsername());
						responseData.put(CometConstants.RESPONSE, RESOLVE_USER_ACTION);
						ServiceErrors errors = possibleUser.getLastServiceErrors();
						if (errors != null)
						{
							responseData.put(ServiceErrors.ERRORS_ELEMENT, errors.getErrorStrings());
						}
						return responseData;
					}
				}
			}
		}
		else if (UPDATE_USER.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map values = (Map) request;

				String firstName = values.get(User.FIRST_NAME).toString();
				String lastName = values.get(User.LAST_NAME).toString();
				String phone = values.get(User.PHONE).toString();
				String email = values.get(User.EMAIL).toString();
				String address1 = values.get(User.ADDRESS1).toString();
				String address2 = values.get(User.ADDRESS2).toString();
				String city = values.get(User.CITY).toString();
				String state = values.get(User.STATE).toString();
				String zip = values.get(User.ZIP).toString();
				String country = values.get(User.COUNTRY).toString();
				String role = values.get(User.ROLE).toString();
				String company = values.get(User.COMPANY).toString();
				String organizationType = values.get(User.ORG_TYPE).toString();
				String organizationSize = values.get(User.ORG_SIZE).toString();
				String sitesPerYear = values.get(User.SITES_PER_YEAR).toString();

				String ajax = getBooleanString(values, User.AJAX);
				String javascript = getBooleanString(values, User.JAVASCRIPT);
				String php = getBooleanString(values, User.PHP);
				String ruby = getBooleanString(values, User.RUBY);
				String python = getBooleanString(values, User.PYTHON);
				String java = getBooleanString(values, User.JAVA);
				String net = getBooleanString(values, User.NET);
				String siteDevelopment = getBooleanString(values, User.SITE_DEVELOPMENT);
				String applicationDevelopment = getBooleanString(values, User.APPLICATION_DEVELOPMENT);
				String newsletter = getBooleanString(values, User.NEWSLETTER);
				String createdAt = values.get(User.CREATED_AT).toString();
				// Format the response date to something that the Javascript Part on the portal can parse back to a date instance
				try
				{
					createdAt = DateFormat.getDateInstance(DateFormat.SHORT).parse(createdAt).toString();
				}
				catch (ParseException e)
				{
				}
				
				User user = AptanaUser.getSignedInUser();

				Map<Object, Object> responseData = new HashMap<Object, Object>();
				responseData.put(CometConstants.RESPONSE, UPDATE_USER_ACTION);

				if (user != null)
				{

					user.setField(User.FIRST_NAME, firstName);
					user.setField(User.LAST_NAME, lastName);
					user.setField(User.EMAIL, email);
					user.setField(User.PHONE, phone);
					user.setField(User.ADDRESS1, address1);
					user.setField(User.ADDRESS2, address2);
					user.setField(User.CITY, city);
					user.setField(User.STATE, state);
					user.setField(User.ZIP, zip);
					user.setField(User.COUNTRY, country);
					user.setField(User.ROLE, role);
					user.setField(User.COMPANY, company);
					user.setField(User.ORG_TYPE, organizationType);
					user.setField(User.ORG_SIZE, organizationSize);
					user.setField(User.SITES_PER_YEAR, sitesPerYear);
					user.setField(User.AJAX, ajax);
					user.setField(User.JAVASCRIPT, javascript);
					user.setField(User.PHP, php);
					user.setField(User.RUBY, ruby);
					user.setField(User.PYTHON, python);
					user.setField(User.JAVA, java);
					user.setField(User.NET, net);
					user.setField(User.SITE_DEVELOPMENT, siteDevelopment);
					user.setField(User.APPLICATION_DEVELOPMENT, applicationDevelopment);
					user.setField(User.NEWSLETTER, newsletter);
					user.setField(User.CREATED_AT, createdAt);
					
					// Also set the email in EMAIL_CHANNEL
					for (com.aptana.ide.core.model.channel.Channel channel : user.getChannels().getItems())
					{
						ChannelType type = channel.getChannelType();
						if (type != null && EMAIL_CHANNEL.equals(type.getId()) && channel.isPrimary())
						{
							channel.setField(com.aptana.ide.core.model.channel.Channel.VALUE, user.getField(User.EMAIL));
							break;
						}
					}

					user.commit();
					List<String> errors = new ArrayList<String>();
					ServiceErrors serviceErrors = user.getLastServiceErrors();
					if (serviceErrors != null && serviceErrors.getItems() != null)
					{
						for (ServiceError se : serviceErrors.getItems())
						{
							errors.add(se.getMessage());
						}
						responseData.put(SUCCESS, (errors.size() > 0 ? Boolean.FALSE : Boolean.TRUE));
					}
					else
					{
						responseData.put(SUCCESS, Boolean.TRUE);
					}
					responseData.put(ServiceErrors.ERRORS_ELEMENT, errors);
				}
				else
				{
					responseData.put(SUCCESS, Boolean.FALSE);
				}

				return responseData;
			}
		}

		return null;
	}

	private String getBooleanString(Map values, String key)
	{
		Object value = values.get(key);
		if (value == null)
			return Boolean.FALSE.toString();
		if (value instanceof String)
			return Boolean.toString(Boolean.parseBoolean((String) value));
		if (value instanceof Boolean)
			return Boolean.toString((Boolean) value);
		return Boolean.FALSE.toString();
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { RESOLVE_USER, CURRENT_USER, LOGIN_USER, LOGOUT_USER, CREATE_USER, UPDATE_USER };
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}

}
