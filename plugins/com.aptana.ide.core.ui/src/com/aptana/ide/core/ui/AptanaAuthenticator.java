/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.Base64;
import com.aptana.ide.core.IAuthentificationULRFilter;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.MultipleAuthenticationFilter;
import com.aptana.ide.core.SkipCloudFilter;
import com.aptana.ide.core.SkipPathAuthentificationFilter;
import com.aptana.ide.core.SkipSitemanagerAuthFilter;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.preferences.IPreferenceConstants;

/**
 * Aptana authenticator
 */
public class AptanaAuthenticator extends UpdateManagerAuthenticator
{
	private static final int THREE_SECONDS = 3 * 1000;
	private static final String HOST_DELIMETER = ","; //$NON-NLS-1$
	private static final String ALGORITHM = "AES/ECB/PKCS5Padding"; //$NON-NLS-1$

	private static final String DEBUG_HOST_VALUE = RESTServiceProvider.DEBUG_HOST;

	private static final AptanaAuthenticator authenticator = new AptanaAuthenticator();

	/**
	 * Keeps track of request timestamps, for determining if we're getting re-prompted for same request quickly.
	 * (Indicator that credentials are bad)
	 */
	private static Map<String, Long> requestMap = new HashMap<String, Long>();

	/**
	 * In-memory cache of IP to auth
	 */
	private static Map<String, PasswordAuthentication> table = new Hashtable<String, PasswordAuthentication>();

	/**
	 * Filter.
	 */
	private final IAuthentificationULRFilter filter;

	/**
	 * Correct authenticator to be aptana authenticator singleton
	 */
	public static void correctAuthenticator()
	{
		Authenticator.setDefault(authenticator);
	}

	/**
	 * AptanaAuthenticator constructor.
	 */
	public AptanaAuthenticator()
	{
		this(new MultipleAuthenticationFilter(new IAuthentificationULRFilter[] {
				new SkipPathAuthentificationFilter(new String[] { "/update/xul/3.2/messages.xml" }), //$NON-NLS-1$
				new SkipSitemanagerAuthFilter(), new SkipCloudFilter() }));
	}

	/**
	 * AptanaAuthenticator constructor.
	 * 
	 * @param filter -
	 *            filter.
	 */
	public AptanaAuthenticator(IAuthentificationULRFilter filter)
	{
		this.filter = filter;
	}

	/**
	 * @see com.aptana.ide.core.ui.UpdateManagerAuthenticator#getPasswordAuthentication()
	 */
	protected PasswordAuthentication getPasswordAuthentication()
	{
		InetAddress address = getRequestingSite();
		String hostString = null;
		if (address != null)
		{
			hostString = address.getHostName();
		}
		if (hostString == null)
		{
			hostString = ""; //$NON-NLS-1$
		}

		if (hostString.indexOf("aptanacloud.com") != -1 && AptanaUser.getSignedInUser() != null //$NON-NLS-1$
				&& AptanaUser.getSignedInUser().hasCredentials())
		{
			return new PasswordAuthentication(AptanaUser.getSignedInUser().getUsername(), AptanaUser.getSignedInUser()
					.getPassword().toCharArray());
		}

		if (hostString.indexOf("sitemanager.aptana.com") != -1 && AptanaUser.getSignedInUser() != null //$NON-NLS-1$
				&& AptanaUser.getSignedInUser().hasCredentials())
		{
			return new PasswordAuthentication(AptanaUser.getSignedInUser().getUsername(), AptanaUser.getSignedInUser()
					.getPassword().toCharArray());
		}
		if (DEBUG_HOST_VALUE != null && hostString.indexOf(DEBUG_HOST_VALUE) != -1
				&& AptanaUser.getSignedInUser() != null && AptanaUser.getSignedInUser().hasCredentials())
		{
			return new PasswordAuthentication(AptanaUser.getSignedInUser().getUsername(), AptanaUser.getSignedInUser()
					.getPassword().toCharArray());
		}

		if (filter != null && !filter.requiresCheck(getRequestingURL()))
		{
			return null;
		}

		// if hostname doesn't contain "aptana" don't worry about caching, just
		// prompt
		if (hostString.indexOf("aptana") == -1) //$NON-NLS-1$
		{
			return doGetPasswordAuthentication();
		}

		// if caching is enabled and it looks like those credentials are OK, just
		// return the cached credentials
		if (cachingEnabled() && !looksLikeCachedAreBad(hostString))
		{
			PasswordAuthentication cached = getCachedAuthentication(hostString);
			if (cached != null)
			{
				return cached;
			}
		}

		// should only be here if host contains "aptana" _and_ caching is
		// disabled/cached doesn't exist/cached looks
		// "bad"
		PasswordAuthentication auth = super.getPasswordAuthentication(); // prompt
		// for
		// credentials

		if (auth != null && cachingEnabled()) // if we have credentials and
		// caching is enabled, cache
		// them
		{
			IdeLog.logInfo(AptanaCorePlugin.getDefault(), StringUtils.format(Messages.AptanaAuthenticator_INF_LoggingPasswordForHost,
					hostString));
			cacheAuthentication(hostString, auth);
		}
		return auth;
	}

	/**
	 * Moved from ProxyAuthenticator. Caches authentication in-memory per IP.
	 * 
	 * @return - password auth
	 */
	private PasswordAuthentication doGetPasswordAuthentication()
	{
		String address = null;
		PasswordAuthentication auth = null;

		try
		{
			address = InetAddress.getByName(getRequestingHost()).getHostAddress();
			auth = (PasswordAuthentication) table.get(address);
		}
		catch (Exception exc)
		{
			// Intentionally left empty.
		}

		// We didn't find a host address in our table so we will
		// prompt the user for one.
		if (auth == null)
		{
			auth = super.getPasswordAuthentication();
			if (address != null && auth != null)
			{
				table.put(address, auth);
			}
		}

		return auth;
	}

	private static SecretKeySpec getKeySpec()
	{
        String ksPref = Platform.getPreferencesService().getString(
                AptanaCorePlugin.ID, IPreferenceConstants.CACHED_KEY, "", null); //$NON-NLS-1$
		byte[] key = null;
		
		if (!"".equals(ksPref)) //$NON-NLS-1$
		{
			try
			{
				byte[] bytes = Base64.decode(ksPref);
				if(bytes != null)
				{
					key = bytes;
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_UnableToDecodeExistingKey, e);
			}
		}

		KeyGenerator kgen;
		if (key == null || key.length == 0)
		{
			try
			{
				kgen = KeyGenerator.getInstance("AES"); //$NON-NLS-1$
				kgen.init(128);

				SecretKey skey = kgen.generateKey();
				key = skey.getEncoded();
				String b64 = Base64.encodeBytes(skey.getEncoded());
				IEclipsePreferences prefs = getPreferences();
				prefs.put(IPreferenceConstants.CACHED_KEY, b64);
				try {
                    prefs.flush();
                } catch (BackingStoreException e) {
                }
			}
			catch (NoSuchAlgorithmException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
				return null;
			}
		}

		SecretKeySpec skeySpec;
		skeySpec = new SecretKeySpec(key, "AES"); //$NON-NLS-1$
		return skeySpec;
	}

	private static byte[] encrypt(SecretKeySpec skeySpec, String password)
	{
		Cipher cipher;
		try
		{
			cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal(password.getBytes());
		}
		catch (NoSuchAlgorithmException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
		}
		catch (NoSuchPaddingException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchPadding, e);
		}
		catch (InvalidKeyException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_InvalidKey, e);
		}
		catch (IllegalBlockSizeException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_IllegalBlockSize, e);
		}
		catch (BadPaddingException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_BadPadding, e);
		}
		return null;
	}

	private static byte[] decrypt(SecretKeySpec skeySpec, byte[] encryptedPassword)
	{
		Cipher cipher;
		try
		{
			cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return cipher.doFinal(encryptedPassword);
		}
		catch (NoSuchAlgorithmException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
		}
		catch (NoSuchPaddingException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchPadding, e);
		}
		catch (InvalidKeyException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_InvalidKey, e);
		}
		catch (IllegalBlockSizeException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_IllegalBlockSize, e);
		}
		catch (BadPaddingException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AptanaAuthenticator_ERR_BadPadding, e);
		}
		return null;
	}

	/**
	 * Takes in a PasswordAuthentication and returns an identical version. It used to return a modified version with
	 * encrypted password. We've left it here in case we need to modify it in some way in the future.
	 * 
	 * @param auth
	 *            a PasswordAuthentication
	 * @return a PasswordAuthentication
	 */
	public PasswordAuthentication encrypt(PasswordAuthentication auth)
	{
		if (auth == null)
		{
			return null;
		}
		char[] password = auth.getPassword();
		String username = auth.getUserName();

		return new PasswordAuthentication(username, password);
	}

	private boolean looksLikeCachedAreBad(String hostString)
	{
		String serialized = getSerializedRequest(hostString);
		return looksLikeCachedAreBad(hostString, serialized);
	}

	/**
	 * True is cached values appear to be bad
	 * 
	 * @param hostString
	 * @param serializedRequest
	 * @return - true if they look bad, false otherwise
	 */
	public static boolean looksLikeCachedAreBad(String hostString, String serializedRequest)
	{
		try
		{
			// never been asked before, so assume cached are good
			if (!requestMap.containsKey(serializedRequest))
			{
				return false;
			}

			Long lastRequestTimestamp = requestMap.get(serializedRequest);
			// prompted exact same in last 3 seconds, credentials are probably
			// bad
			if (lastRequestTimestamp.longValue() > (System.currentTimeMillis() - THREE_SECONDS))
			{
				return true;
			}

			// last request was longer than 3 seconds ago...
			return false;
		}
		finally
		{
			requestMap.put(serializedRequest, new Long(System.currentTimeMillis()));
		}
	}

	private String getSerializedRequest(String hostString)
	{
		return serializeRequest(hostString, getRequestingPrompt(), getRequestingProtocol(), getRequestingScheme(),
				getRequestingPort(), getRequestingURL());
	}

	/**
	 * Serialize a request
	 * 
	 * @param hostString
	 * @param prompt
	 * @param protocol
	 * @param scheme
	 * @param port
	 * @param url
	 * @return - serialized request string
	 */
	public static String serializeRequest(String hostString, String prompt, String protocol, String scheme, int port,
			URL url)
	{
		StringBuffer request = new StringBuffer();
		request.append(hostString);
		request.append(";"); //$NON-NLS-1$
		request.append(prompt);
		request.append(";"); //$NON-NLS-1$
		request.append(protocol);
		request.append(";"); //$NON-NLS-1$
		request.append(scheme);
		request.append(";"); //$NON-NLS-1$
		request.append(port);
		request.append(";"); //$NON-NLS-1$
		request.append(url.toString());
		return request.toString();
	}

	/**
	 * Returns true if caching enable, false otherwise
	 * 
	 * @return - true if caching enabled
	 */
	public static boolean cachingEnabled()
	{
        return Platform.getPreferencesService().getBoolean(AptanaCorePlugin.ID,
                IPreferenceConstants.PREF_ENABLE_PASSWORD_CACHING, true, null);
	}

	/**
	 * Cache the authentication, without any processing.
	 * 
	 * @param hostString
	 * @param auth
	 */
	public void cacheAuthentication(String hostString, PasswordAuthentication auth)
	{
		if (auth == null)
		{
			return;
		}
		char[] password = auth.getPassword();
		String username = auth.getUserName();

		IEclipsePreferences prefs = getPreferences();
        String hostsString = Platform.getPreferencesService().getString(
                AptanaCorePlugin.ID, IPreferenceConstants.SAVED_PASSWORD_HOSTS,
                "", null); //$NON-NLS-1$
		int index = hostsString.indexOf(hostString);
        // if we haven't yet saved this host, add it to comma delimited list
		if (index == -1)
		{
			hostsString += HOST_DELIMETER + hostString;
			prefs.put(IPreferenceConstants.SAVED_PASSWORD_HOSTS, hostsString);
		}

		SecretKeySpec key = getKeySpec();
		byte[] encrypted = encrypt(key, new String(password));
		if (encrypted != null && encrypted.length > 0)
		{
			String b64 = Base64.encodeBytes(encrypted);
			prefs.put(generatePrefKey(IPreferenceConstants.USERNAME, hostString), username);
			prefs.put(generatePrefKey(IPreferenceConstants.PASSWORD, hostString), b64);
		}
		try {
            prefs.flush();
        } catch (BackingStoreException e) {
        }
	}

	/**
	 * Gets the cached authentication
	 * 
	 * @param hostString
	 * @return - password authentication
	 */
	public static PasswordAuthentication getCachedAuthentication(String hostString)
	{
		char[] password = getPassword(hostString);
		if (password.length == 0)
		{
			return null;
		}
		String username = getUserName(hostString);
		if ("".equals(username)) //$NON-NLS-1$
		{
			return null;
		}
		return new PasswordAuthentication(username, password);
	}

	private static IEclipsePreferences getPreferences()
	{
		return (new InstanceScope()).getNode(AptanaCorePlugin.ID);
	}

	/**
	 * Gets the user name for a host name
	 * 
	 * @param hostString
	 * @return - user name string
	 */
	public static String getUserName(String hostString)
	{
        String username = Platform.getPreferencesService().getString(
                AptanaCorePlugin.ID,
                generatePrefKey(IPreferenceConstants.USERNAME, hostString),
                "", null); //$NON-NLS-1$
		if (username.length() > 30)
		{
			username = username.substring(0, 30);
		}
		return username;
	}

	private static String generatePrefKey(String prefix, String hostString)
	{
		return prefix + "." + hostString; //$NON-NLS-1$
	}

	private static char[] getPassword(String hostString)
	{
        String encrypted = Platform.getPreferencesService().getString(
                AptanaCorePlugin.ID,
                generatePrefKey(IPreferenceConstants.PASSWORD, hostString), "", //$NON-NLS-1$
                null);
		byte[] bytes = Base64.decode(encrypted);
		
		if(bytes != null && bytes.length > 0)
		{
			SecretKeySpec key = getKeySpec();
			byte[] decrypted = decrypt(key, bytes);
			return new String(decrypted).toCharArray();
		}
		else
		{
			return new char[0];
		}
	}

	/**
	 * Gets the saved hosts
	 * 
	 * @return - set of hosts
	 */
	public static final Set<String> getSavedHosts()
	{
        String[] hosts = Platform.getPreferencesService().getString(
                AptanaCorePlugin.ID, IPreferenceConstants.SAVED_PASSWORD_HOSTS,
                "", null).split(AptanaAuthenticator.HOST_DELIMETER); //$NON-NLS-1$
		Set<String> hostSet = new HashSet<String>();
		for (int i = 0; i < hosts.length; i++)
		{
			hostSet.add(hosts[i]);
		}
		return hostSet;
	}

	/**
	 * Clears the saved credentials
	 * 
	 * @param hostname -
	 *            host name
	 */
	public static void removeCachedAuthentication(String hostname)
	{
		StringBuffer buffer = new StringBuffer();
		Set<String> hosts = getSavedHosts();
		for (Iterator<String> iter = hosts.iterator(); iter.hasNext();)
		{
			String host = iter.next();
			if (host.equals(hostname))
			{
				continue; // rebuild hosts string without the cleared host
			}
			buffer.append(host);
			buffer.append(HOST_DELIMETER);
		}
		if (buffer.length() > 0)
		{
			buffer.deleteCharAt(buffer.length() - 1);
		}
		IEclipsePreferences prefs = getPreferences();
		prefs.put(IPreferenceConstants.SAVED_PASSWORD_HOSTS, buffer.toString());
		prefs.put(generatePrefKey(IPreferenceConstants.USERNAME, hostname), ""); //$NON-NLS-1$
		prefs.put(generatePrefKey(IPreferenceConstants.PASSWORD, hostname), ""); //$NON-NLS-1$
		try {
            prefs.flush();
        } catch (BackingStoreException e) {
        }
	}

	/**
	 * addProxyEntry
	 * 
	 * @param hostname
	 * @param userid
	 * @param password
	 */
	public static void addProxyEntry(String hostname, String userid, String password)
	{
		try
		{
			String address = InetAddress.getByName(hostname).getHostAddress();
			PasswordAuthentication auth = new PasswordAuthentication(userid, password.toCharArray());
			if (address != null && auth != null)
			{
				table.put(address, auth);
			}
		}
		catch (Exception exc)
		{
			// If the host is not known we will just not add this information to
			// the table.
		}
	}
}
