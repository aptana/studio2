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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.ILoggable;
import com.aptana.ide.core.ILogger;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.LoggerAdapater;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;

/**
 * This class is a loggable service provider that performs a RESTful service for a url and service request.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class RESTServiceProvider implements IServiceProvider, ILoggable
{

	/**
	 * Receives feedbacks on RESTServiceProvider.
	 */
	public static interface Listener
	{
		/**
		 * Indicates the authentication failed for specific user.
		 * 
		 * @param user the user object
		 */
		public void authenticationFailed(User user);

		/**
		 * Makes it be Aptana authentication.
		 */
		public void correctAuthentication();
	}

	/**
	 * DEBUG_HOST
	 */
	public static final String DEBUG_HOST;

	private static final String PROPERTY_KEY = "SiteManagerSelection"; //$NON-NLS-1$

	static
	{
	    IEclipsePreferences prefs = (new DefaultScope()).getNode(ResourcesPlugin.PI_RESOURCES);
        prefs.putBoolean(PROPERTY_KEY, true);
		// boolean useProduction = ResourcesPlugin.getPlugin().getPluginPreferences().getBoolean(PROPERTY_KEY);
		boolean useProduction = true;
		if (useProduction)
		{
			String debugHost = System.getProperty("DEBUG_HOST"); //$NON-NLS-1$
			if (debugHost == null || debugHost.length() == 0)
			{
				debugHost = "cloudmanager.aptana.com"; //$NON-NLS-1$
			}
			DEBUG_HOST = debugHost;
		}
		else
		{
			// default dev site manager
			DEBUG_HOST = "acotak-staging.aptana.com"; //$NON-NLS-1$
		}
	}

	/**
	 * LOCATION_HEADER
	 */
	public static final String LOCATION_HEADER = "Location"; //$NON-NLS-1$

	/**
	 * AUTHORIZATION_HEADER This auth header is used instead of the standard HTTP header due to auth header caching in
	 * the underlying Java classes for HttpURLConnection.
	 */
	public static final String AUTHORIZATION_HEADER = "Aptana-Authorization"; //$NON-NLS-1$

	/**
	 * CONTENT_TYPE_HEADER
	 */
	public static final String CONTENT_TYPE_HEADER = "Content-Type"; //$NON-NLS-1$

	/**
	 * ACCEPT_HEADER
	 */
	public static final String ACCEPT_HEADER = "Accept"; //$NON-NLS-1$

	private SSLContext sslContext;

	private ILogger logger;

	private static List<Listener> listeners = new ArrayList<Listener>();

	/**
	 * Creates an empty site manager
	 */
	public RESTServiceProvider()
	{
		// By default set up a logger which will log to the Eclipse .log
		this.logger = new LoggerAdapater()
		{
			public void logError(String message)
			{
				AptanaCorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, AptanaCorePlugin.ID, 1, message, null));
			}
		};
		try
		{
			if (DEBUG_HOST != null)
			{
				// The following code allows a self-signed certificate to be accepted when use the dev site manager
				// which does not have a standard cert.
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
				{
					public java.security.cert.X509Certificate[] getAcceptedIssuers()
					{
						return null;
					}

					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
					{
					}

					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
					{
						for (int i = 0; i < certs.length; i++)
						{
							String issuer = certs[i].getIssuerDN().getName();
							if (issuer
									.trim()
									.equals(
											"EMAILADDRESS=cwilliams@aptana.com, CN=" //$NON-NLS-1$
													+ DEBUG_HOST
													+ ", OU=Aptana Cloud, O=\"Aptana, Inc.\", L=San Mateo, ST=California, C=US")) //$NON-NLS-1$
							{
								return; // We're ok
							}
						}
					}
				} };
				sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			}
		}
		catch (Exception e)
		{
			logMessage(e.getMessage());
			sslContext = null;
		}
	}

	public static void addListener(Listener listener)
	{
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	public static void removeListener(Listener listener)
	{
		listeners.remove(listener);
	}

	private void fireAuthenticationFailed(User user)
	{
		for (Listener listener : listeners)
		{
			listener.authenticationFailed(user);
		}
	}

	private void fireCorrectAuthentication()
	{
		for (Listener listener : listeners)
		{
			listener.correctAuthentication();
		}
	}

	private IServiceResponse getResponse(URLConnection rawResponse)
	{
		int status = IServiceResponse.STATUS_UNSET;
		IServiceResponse response = null;
		try
		{
			if (rawResponse instanceof HttpURLConnection)
			{
				try
				{
					status = ((HttpURLConnection) rawResponse).getResponseCode();
					// updates the status for the particular URL
					URLStatusTracker.getInstance().setStatus(rawResponse.getURL(), status);
				}
				catch (IOException e)
				{
					if (!(e instanceof UnknownHostException))
					{
						logMessage("Error occured reading status from response for location: " + rawResponse.getURL() //$NON-NLS-1$
								+ " " + e.getMessage()); //$NON-NLS-1$
					}
					throw e;
				}
			}
			// If the authentication fails and the user is logged in we log the user out.
			if (status == IServiceResponse.STATUS_UNAUTHORIZED) {
				User signedInUser = AptanaUser.getSignedInUser();
				if (signedInUser != null && signedInUser.hasCredentials()) {
					fireAuthenticationFailed(signedInUser);
					IdeLog.logImportant(AptanaCorePlugin.getDefault(), Messages.getString("RESTServiceProvider.AuthenticationFailedSigningOut")); //$NON-NLS-1$
					AptanaUser.signOut();
					return new SimpleServiceResponse("", "", "", IServiceResponse.STATUS_UNAUTHORIZED); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			// Use location if present else use body of response
			String location = rawResponse.getHeaderField(LOCATION_HEADER);
			if (location != null)
			{
				try
				{
					response = new SimpleServiceResponse(rawResponse.getContentType(), location, new URL(location),
							status);
					logMessage("Location found in response: " + location); //$NON-NLS-1$
				}
				catch (MalformedURLException e)
				{
					logMessage("Error creating URL from Location header: " + location); //$NON-NLS-1$
				}
			}
			else
			{
				try
				{
					String responseBody = null;
					if (rawResponse instanceof HttpURLConnection)
					{
						responseBody = getResponseBody((HttpURLConnection) rawResponse);
					}
					else
					{
						responseBody = getResponseBody(rawResponse.getInputStream());
					}
					response = new SimpleServiceResponse(rawResponse.getContentType(), responseBody, responseBody,
							status);
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}
		catch (UnknownHostException uhe)
		{
			return new SimpleServiceResponse("", "", "", IServiceResponse.STATUS_UNKNOWN_HOST); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (IOException e)
		{
			logMessage("IOException on getResponse for location: " + rawResponse.getURL() + " " + e.getMessage());  //$NON-NLS-1$//$NON-NLS-2$
			if (rawResponse instanceof HttpURLConnection)
			{
				try
				{
					InputStream stream = ((HttpURLConnection) rawResponse).getErrorStream();
					if (stream != null)
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
						StringBuffer buffer = new StringBuffer();
						String line = reader.readLine();
						while (line != null)
						{
							buffer.append(line);
							buffer.append('\n');
							line = reader.readLine();
						}
						response = new SimpleServiceResponse(rawResponse.getContentType(), buffer.toString(), buffer
								.toString(), status);
					}
				}
				catch (Exception e1)
				{
					logMessage("Error reading error stream from response for location: " + rawResponse.getURL() + " "  //$NON-NLS-1$//$NON-NLS-2$
							+ e.getMessage());
				}
			}
		}
		appendResponseLossage(rawResponse, response);
		return response;
	}

	private String getResponseBody(HttpURLConnection rawResponse) throws IOException
	{
		try
		{
			return getResponseBody(rawResponse.getInputStream());
		}
		catch (IOException e)
		{
			logMessage("Error occured reading from response stream for location: " + rawResponse.getURL() + " "  //$NON-NLS-1$//$NON-NLS-2$
					+ e.getMessage());
		}
		try
		{
			return getResponseBody(rawResponse.getErrorStream());
		}
		catch (IOException e)
		{
			logMessage("Error occured reading from response error stream for location: " + rawResponse.getURL() + " "  //$NON-NLS-1$//$NON-NLS-2$
					+ e.getMessage());
			throw e;
		}

	}

	private String getResponseBody(InputStream stream) throws IOException
	{
		if (stream == null)
		{
			return ""; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			line = reader.readLine();
			while (line != null)
			{
				buffer.append(line);
				buffer.append('\n');
				line = reader.readLine();
			}
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return buffer.toString();
	}

	private void logMessage(String message)
	{
		if (getLogger() != null)
		{
			getLogger().logError(message);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.IServiceProvider#getTimeout()
	 */
	public int getTimeout()
	{
		return 40000;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#getLogger()
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#setLogger(com.aptana.ide.core.ILogger)
	 */
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	/**
	 * @see com.aptana.ide.core.model.IServiceProvider#callService(java.net.URL,
	 *      com.aptana.ide.core.model.IServiceRequest)
	 */
	public IServiceResponse callService(URL location, IServiceRequest request)
	{
		try
		{
			if (sslContext != null)
			{
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			}
		}
		catch (Exception e)
		{
			logMessage(e.getMessage());
		}

		if (request == null)
		{
			return null;
		}
		appendRequestLossage(location, request);
		
		URLConnection connection = null;
		IServiceResponse response = null;
		try
		{
			fireCorrectAuthentication();
			connection = location.openConnection();
			connection.setUseCaches(false);
			connection.setConnectTimeout(getTimeout());
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConn = (HttpURLConnection) connection;

				connection.setReadTimeout(getTimeout());
				connection.setDoInput(true);
				httpConn.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$

				if (request.getRequestType() != null)
				{
					httpConn.setRequestMethod(request.getRequestType());
				}
				if (request.getAuthentication() != null)
				{
					connection.addRequestProperty(AUTHORIZATION_HEADER, request.getAuthentication());
				}
				if (request.getContentType() != null)
				{
					connection.addRequestProperty(CONTENT_TYPE_HEADER, request.getContentType());
				}
				if (request.getAccept() != null)
				{
					connection.addRequestProperty(ACCEPT_HEADER, request.getAccept());
				}
				Map<String, String> requestProperties = request.getRequestProperties();
				if (requestProperties != null) {
					for (String name: requestProperties.keySet()) {
						connection.addRequestProperty(name, requestProperties.get(name));
					}
				}
				if (request.containsBody())
				{
					connection.setDoOutput(true);
					OutputStream outputStream = connection.getOutputStream();
					OutputStreamWriter writer = null;
					if ("application/xml".equals(request.getContentType())) //$NON-NLS-1$
					{
						// If content type is application/xml assume UTF-8 encoding
						writer = new OutputStreamWriter(outputStream, "UTF-8"); //$NON-NLS-1$
					}
					else
					{
						writer = new OutputStreamWriter(outputStream);
					}
					writer.write(request.getContents() + "\n"); //$NON-NLS-1$
					writer.flush();
				}
			}
		}
		catch (Exception e)
		{
			logMessage("Error occured streaming request:" + e.getMessage()); //$NON-NLS-1$
		}
		if (connection != null)
		{
			response = getResponse(connection);
		}
		return response;
	}
	
	private static int maxLossageSize = Integer.getInteger("com.aptana.ide.core.model.maxLossageSize", 524288); //$NON-NLS-1$
	
	private static StringBuffer lossage = new StringBuffer();
	
	public static String getLossage()
	{
		return lossage.toString();
	}

	private void appendRequestLossage(URL location, IServiceRequest request)
	{
		if (location != null) {
			appendlnLossage("Request URL: " + location); //$NON-NLS-1$
		}
		if (request != null) {
			appendlnLossage("Request Type: " + request.getRequestType()); //$NON-NLS-1$
			appendlnLossage("Request Content Type: " + request.getContentType()); //$NON-NLS-1$
			appendlnLossage("Request Authentication: " + request.getAuthentication()); //$NON-NLS-1$
			appendlnLossage("Request Accept: " + request.getAccept()); //$NON-NLS-1$
			if (request.containsBody()) {
				String contents = request.getContents();
				if (contents != null)
				{
					appendlnLossage("Request Body:"); //$NON-NLS-1$
					appendlnLossage(StringUtils.getPublishableMessage(request.getContents()));
				}
			}
		}
	}
	
	private void appendResponseLossage(URLConnection rawResponse, IServiceResponse response)
	{
		if (rawResponse != null) {
			appendlnLossage("Response Request URL: " + rawResponse.getURL()); //$NON-NLS-1$
			appendlnLossage("Response Content Type: " + rawResponse.getContentType()); //$NON-NLS-1$
			appendlnLossage("Response Content Length: " + rawResponse.getContentLength()); //$NON-NLS-1$
			appendlnLossage("Response Content Encoding: " + rawResponse.getContentEncoding()); //$NON-NLS-1$
			appendlnLossage("Response Date: " + rawResponse.getDate()); //$NON-NLS-1$
			appendlnLossage("Response Last Modified: " + rawResponse.getLastModified()); //$NON-NLS-1$
			appendlnLossage("Response Expiration: " + rawResponse.getExpiration()); //$NON-NLS-1$
			appendlnLossage("Response If Modified Since: " + rawResponse.getIfModifiedSince()); //$NON-NLS-1$
			Map<String, List<String>> headerFields = rawResponse.getHeaderFields();
			for (String fieldName : headerFields.keySet())
			{
				appendlnLossage("Response Header field '" + fieldName + "'=" + headerFields.get(fieldName)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			appendlnLossage("Response Connect Timeout: " + rawResponse.getConnectTimeout()); //$NON-NLS-1$
		}
		if (response != null)
		{
			appendlnLossage("Response Status: " + response.getStatus()); //$NON-NLS-1$
			appendlnLossage("Response Content Type: " + response.getContentType()); //$NON-NLS-1$
			String contents = response.getContents();
			if (contents != null) { 
				appendlnLossage("Response Content:"); //$NON-NLS-1$
				appendlnLossage(StringUtils.getPublishableMessage(contents));
			}
		}
	}
	
	private void appendlnLossage(String appendix)
	{
		appendLossage(appendix + "\n"); //$NON-NLS-1$
	}

	private void appendLossage(String appendix)
	{
		int length = appendix.length();
		if (length > maxLossageSize)
		{
			lossage.setLength(0);
			lossage.append(appendix.substring(length - maxLossageSize));
		}
		else
		{
			int lossageLength = lossage.length();
			int totalLength = lossageLength + length;
			if (totalLength > maxLossageSize)
			{
				lossage.delete(0, totalLength - maxLossageSize);
				lossage.append(appendix);
			}
			else
			{
				lossage.append(appendix);
			}
		}
	}
}
