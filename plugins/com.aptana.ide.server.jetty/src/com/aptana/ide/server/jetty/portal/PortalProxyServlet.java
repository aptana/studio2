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
package com.aptana.ide.server.jetty.portal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.db.AptanaDB;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.online.OnlineDetectionService.StatusMode;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.Messages;
import com.aptana.ide.server.jetty.JettyPlugin;

/**
 * This servlet is used to grab content from a url passed via a query parameter. The response of this servlet is the
 * body and status code of the url streamed.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PortalProxyServlet extends HttpServlet
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * TIMEOUT
	 */
	public static final int TIMEOUT = 30000;

	/**
	 * URL
	 */
	public static final String URL = "url"; //$NON-NLS-1$

	/**
	 * CLEAR_CACHE Specifies whether the cache should be cleared, a 'true' value will do this
	 */
	public static final String CLEAR_CACHE = "clearCache"; //$NON-NLS-1$

	private static final String NO_CACHE = "noCache"; //$NON-NLS-1$

	/**
	 * 
	 */
	public PortalProxyServlet()
	{
		initCache();
	}

	class RemoteContents
	{
		long lastModifiedDate = 0;
		byte[] data = null;
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String address = request.getParameter(URL);
		String clearCache = request.getParameter(CLEAR_CACHE);
		String noCache = request.getParameter(NO_CACHE);

		if (clearCache != null && clearCache.equals("true")) //$NON-NLS-1$
		{
			clearCache();
		}

		if (address != null)
		{
			// If we are offline, then we'll try serving from cache
			if (OnlineDetectionService.getInstance().getStatus() == StatusMode.OFFLINE)
			{
				serveFromCache(response, address);
				return;
			}

			// If we are online (or unknown state), we'll try to get the file, if it fails, then we'll try from cache
			RemoteContents remoteContents = null;

			try
			{
				long lastModifiedDate = -1; //getModifiedDateFromCache(address);
				remoteContents = getRemoteContents(lastModifiedDate, address);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				remoteContents = null;
			}

			// If we couldn't fetch the remote contents, let's try serving it from cache
			if (remoteContents == null)
			{
				serveFromCache(response, address);
				return;
			}

			// Serve the file out
			response.setStatus(HttpURLConnection.HTTP_OK);
			response.setContentLength(remoteContents.data.length);
			FileUtils.pipe(new ByteArrayInputStream(remoteContents.data), response.getOutputStream(), true);

			// Only cache if the noCache option is NOT true (it doesn't exist usually for false state)
			if ("true".equals(noCache) == false) //$NON-NLS-1$
			{
				// Save the file to the cache
				saveToCache(address, remoteContents);
			}
		}
	}

	private RemoteContents getRemoteContents(long lastModifiedDate, String address) throws MalformedURLException, IOException,
			ProtocolException
	{
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection)
		{
			connection.setReadTimeout(TIMEOUT);
			connection.setDoInput(true);

			long remoteLastModified = 0;
			
			/* TODO
			if (lastModifiedDate != -1)
			{
				((HttpURLConnection) connection).setRequestMethod("GET");
				((HttpURLConnection) connection).addRequestProperty("Last-Modified", // TODO
				connection.connect();
				remoteLastModified = connection.getLastModified();

				Map fields = ((HttpURLConnection) connection).getHeaderFields();
				
				if(remoteLastModified <= lastModifiedDate)
				{
					return getFromCache(address);
				}
				else
				{
					connection = url.openConnection();
				}
			}
			*/

			((HttpURLConnection) connection).setRequestMethod("GET"); //$NON-NLS-1$
			((HttpURLConnection) connection).connect();
			
			// Copy the contents to memory first
			ByteArrayOutputStream fileContents = new ByteArrayOutputStream();
			FileUtils.pipe(connection.getInputStream(), fileContents, true);

			RemoteContents contents = new RemoteContents();
			contents.data = fileContents.toByteArray();
			contents.lastModifiedDate = remoteLastModified;
			
			return contents;

		}

		return null;
	}

	private void serveFromCache(HttpServletResponse response, String address) throws IOException
	{
		RemoteContents contents = getFromCache(address);

		if (contents != null && contents.data != null)
		{
			FileUtils.pipe(new ByteArrayInputStream(contents.data), response.getOutputStream(), true);
			response.setStatus(HttpURLConnection.HTTP_OK);
		}
		else
		{
			response.setStatus(HttpURLConnection.HTTP_UNAVAILABLE);
		}
	}

	private RemoteContents getFromCache(String address)
	{
		AptanaDB db = AptanaDB.getInstance();

		Connection conn = db.getConnection();
		
		if (conn != null)
		{
			Statement s;
			try
			{
				String addr = java.net.URLEncoder.encode(address, "UTF-8"); //$NON-NLS-1$
				s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT * FROM PROXY_CACHE WHERE address='" + addr + "'"); //$NON-NLS-1$ //$NON-NLS-2$
				boolean result = rs.next();
				if (result == true)
				{
					RemoteContents contents = new RemoteContents();
					contents.data = rs.getBytes("data"); //$NON-NLS-1$
					contents.lastModifiedDate = rs.getLong("lastmodified"); //$NON-NLS-1$
					return contents;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					conn.close();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return null;
	}

	/**
	 * 
	 */
	private void initCache()
	{
		try
		{
			if (checkTable() == false)
			{
				dropTable();

				AptanaDB
						.getInstance()
						.execute(
								"CREATE TABLE PROXY_CACHE (lastmodified BIGINT, address varchar(1024) PRIMARY KEY, data BLOB(50000))"); //$NON-NLS-1$
			}
		}
		catch (SQLException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_ErrorInitializingDbConnection, e);

			if (e.getNextException() != null)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_ErrorInitializingDbConnection, e
						.getNextException());
			}
		}
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	private static boolean checkTable() throws SQLException
	{
		Connection conn = null;
		Statement s = null;
		boolean result = true;

		try
		{
			conn = AptanaDB.getInstance().getConnection();
			if (conn == null)
			{
				SQLException sqle = new SQLException("Connection to AptanaDB is null."); //$NON-NLS-1$
				IdeLog.logError(JettyPlugin.getDefault(), sqle.getMessage());
				throw sqle;
			}
			else
			{
				s = conn.createStatement();
				s.execute("select address FROM PROXY_CACHE where lastmodified = 0"); //$NON-NLS-1$
			}
		}
		catch (SQLException sqle)
		{
			String theError = (sqle).getSQLState();

			/** If table exists will get - WARNING 02000: No row was found * */
			if (theError != null && theError.equals("42X05")) // Table does not exist //$NON-NLS-1$
			{
				result = false;
			}
			else if (theError != null && theError.equals("42X04")) // The column does not exist //$NON-NLS-1$
			{
				result = false;
			}
			else if (theError != null && (theError.equals("42X14") || theError.equals("42821"))) //$NON-NLS-1$ //$NON-NLS-2$ 
			{
				IdeLog.logError(JettyPlugin.getDefault(), "IncorrectTableDefinition: " + sqle.getMessage()); //$NON-NLS-1$

				throw sqle;
			}
			else
			{
				IdeLog.logError(JettyPlugin.getDefault(), "SQLException: " + sqle.getMessage()); //$NON-NLS-1$

				throw sqle;
			}
		}
		finally
		{
			if (s != null)
			{
				s.close();
			}

			if (conn != null)
			{
				conn.close();
			}
		}

		return result;
	}

	private void clearCache()
	{
		AptanaDB.getInstance().execute("TRUNCATE PROXY_CACHE"); //$NON-NLS-1$
	}

	private void dropTable()
	{
		try
		{
			AptanaDB.getInstance().execute("DROP TABLE PROXY_CACHE"); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			// ignore the failure in case we're dropping a table that does not exist
		}
	}

	private void saveToCache(String address, RemoteContents contents)
	{
		Connection conn = null;
		PreparedStatement prepStmt = null;

		String sql = "insert into PROXY_CACHE (lastmodified, address, data) values (?,?,?)"; //$NON-NLS-1$

		try
		{
			conn = AptanaDB.getInstance().getConnection();
			
			if (conn != null)
			{
				prepStmt = conn.prepareStatement(sql);
	
				prepStmt.setLong(1, contents.lastModifiedDate);
				prepStmt.setString(2, URLEncoder.encode(address, "UTF-8")); //$NON-NLS-1$
				prepStmt.setBytes(3, contents.data);
				prepStmt.executeUpdate();
			}
		}
		catch (Exception e)
		{
			try
			{
				sql = "update PROXY_CACHE set lastmodified = ?, data = ? where address = ?"; //$NON-NLS-1$

				conn = AptanaDB.getInstance().getConnection();
				
				if (conn != null)
				{
					prepStmt = conn.prepareStatement(sql);
	
					prepStmt.setLong(1, contents.lastModifiedDate);
					prepStmt.setBytes(2, contents.data);
					prepStmt.setString(3, URLEncoder.encode(address, "UTF-8")); //$NON-NLS-1$
					prepStmt.executeUpdate();
				}
			}
			catch (Exception e2)
			{
			}
		}
		finally
		{
			try
			{
				if (prepStmt != null)
				{
					prepStmt.close();
				}

				if (conn != null)
				{
					conn.close();
				}
			}
			catch (Exception e3)
			{
			}
		}
	}
}
