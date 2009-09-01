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
package com.aptana.ide.core.model.user;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.model.IServiceErrors;
import com.aptana.ide.core.model.RESTServiceProvider;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class AptanaUser
{

	private static final String SECURE_PREF_NODE = "/com/aptana/ide/core"; //$NON-NLS-1$
	private static final String USER_URL_OVERRIDE = "USER_URL_OVERRIDE"; //$NON-NLS-1$
	private static final String PROPERTY_KEY = "SiteManagerSelection"; //$NON-NLS-1$

	/**
	 * BASE_URL
	 */
	public static final String BASE_URL;

	static
	{
	    IEclipsePreferences prefs = (new DefaultScope()).getNode(ResourcesPlugin.PI_RESOURCES);
		prefs.putBoolean(PROPERTY_KEY, true);
		// boolean useProduction = ResourcesPlugin.getPlugin().getPluginPreferences().getBoolean(PROPERTY_KEY);
		boolean useProduction = true;
		if (useProduction)
		{
			String propertyUrl = System.getProperty(USER_URL_OVERRIDE);
			if (propertyUrl != null && propertyUrl.length() > 0)
			{
				BASE_URL = propertyUrl;
			}
			else
			{
				BASE_URL = "https://cloudmanager.aptana.com/cloud"; //$NON-NLS-1$
			}
		}
		else
		{
			// default dev site manager
			BASE_URL = "https://acotak-staging.aptana.com/cloud"; //$NON-NLS-1$
		}
	}

	/**
	 * USERS
	 */
	public static final String USERS = BASE_URL + "/users"; //$NON-NLS-1$

	/**
	 * USERS
	 */
	public static final String LOGINS = BASE_URL + "/logins"; //$NON-NLS-1$
	
	private static final String INSTALL_LOCATION_URL = Platform.getInstallLocation().getURL().toExternalForm();

	/**
	 * ACCOUNT_USERNAME
	 */
	private static final String ACCOUNT_USERNAME = "com.aptana.ide.core.model.ACCOUNT_USERNAME/" + INSTALL_LOCATION_URL; //$NON-NLS-1$

	/**
	 * ACCOUNT_PASSWORD
	 */
	private static final String ACCOUNT_PASSWORD = "com.aptana.ide.core.model.ACCOUNT_PASSWORD/" + INSTALL_LOCATION_URL; //$NON-NLS-1$

	/**
	 * ACCOUNT_ID
	 */
	private static final String ACCOUNT_ID = "com.aptana.ide.core.model.ACCOUNT_ID/" + INSTALL_LOCATION_URL; //$NON-NLS-1$

	private static User user;

	/**
	 * Gets the signed in user
	 * 
	 * @return - user that is signed in
	 */
	public synchronized static User getSignedInUser()
	{
		if (user == null)
		{
			user = new User()
			{

				public IServiceErrors commit()
				{

					IServiceErrors errors = null;
					try
					{
						ISecurePreferences node = getSecurePreferences();
						node.put(ACCOUNT_USERNAME, getUsername(), true /*encrypt*/);
						node.put(ACCOUNT_PASSWORD, getPassword(), true /*encrypt*/);
						node.put(ACCOUNT_ID, getId(), true /*encrypt*/);
					}
					catch (Exception e)
					{
						IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.getString("AptanaUser.Unable_To_Write_User"), e); //$NON-NLS-1$
					}
					errors = super.commit();
					return errors;
				}

				public IServiceErrors update()
				{
					IServiceErrors errors = null;
					try
					{						
						ISecurePreferences node = getSecurePreferences();
						setUsername(node.get(ACCOUNT_USERNAME, null));
						setPassword(node.get(ACCOUNT_PASSWORD, null));
						setId(node.get(ACCOUNT_ID, null));						
					}
					catch (Exception e)
					{
						IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.getString("AptanaUser.Unable_To_Read_User"), e); //$NON-NLS-1$
					}
					errors = super.update();
					return errors;
				}

			};
			user.update();
			user.setServiceProvider(new RESTServiceProvider());
			AuthenticatedUserRequestBuilder builder = new AuthenticatedUserRequestBuilder();
			builder.setUser(user);
			user.setRequestBuilder(builder);
			try
			{
				user.setDefaultLocation(new URL(AptanaUser.LOGINS));
			}
			catch (MalformedURLException e)
			{
			}
			if (user.hasCredentials())
			{
				Job job = new Job(Messages.getString("AptanaUser.UpdateAptanaID")) //$NON-NLS-1$
				{

					protected IStatus run(IProgressMonitor monitor)
					{
						// The first update gets the user location
						user.update();
						// This second update gets the user model
						user.update();
						return Status.OK_STATUS;
					}

				};
				job.setPriority(Job.BUILD);
				job.schedule();
			}
		}
		return user;
	}

	/**
	 * Signs in the Aptana User with the username and password
	 * 
	 * @param username
	 * @param password
	 * @param location
	 * @param id
	 */
	public static void signIn(String username, String password, URL location, String id)
	{
		getSignedInUser().suspendEvents();
		getSignedInUser().setPassword(password);
		getSignedInUser().setUsername(username);
		getSignedInUser().setLocation(location);
		getSignedInUser().setId(id);
		getSignedInUser().commit();
		getSignedInUser().update();
		getSignedInUser().resumeEvents();
	}

	/**
	 * Signs out the current user
	 */
	public static void signOut()
	{
		getSignedInUser().suspendEvents();
		getSignedInUser().setPassword(""); //$NON-NLS-1$
		getSignedInUser().setUsername(""); //$NON-NLS-1$
		getSignedInUser().setId(""); //$NON-NLS-1$
		getSignedInUser().setLocation(null);
		getSignedInUser().clear();
		getSignedInUser().resumeEvents();
		getSignedInUser().commit();
	}
	
	/**
	 * 
	 * @return
	 */
	private static ISecurePreferences getSecurePreferences()
	{
		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = root.node(SECURE_PREF_NODE);
		return node;
	}

}
