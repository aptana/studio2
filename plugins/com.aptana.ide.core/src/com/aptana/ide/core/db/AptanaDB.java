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
package com.aptana.ide.core.db;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;

/**
 * @author Kevin Lindsey
 */
public class AptanaDB
{
	private static final String DATABASE_NAME = "aptanaDB"; //$NON-NLS-1$
	private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
	private static final String PROTOCOL = "jdbc:derby:"; //$NON-NLS-1$
	
	private static AptanaDB INSTANCE;
	
	private List<IDBShutdownListener> _shutdownListeners = new ArrayList<IDBShutdownListener>();
	private boolean _driverLoaded = false;
	
	/**
	 * AptanaDB
	 */
	private AptanaDB()
	{
	}

	/**
	 * addShutdownListener
	 * 
	 * @param listener
	 */
	public void addShutdownListener(IDBShutdownListener listener)
	{
		if (listener != null)
		{
			if (this._shutdownListeners == null)
			{
				this._shutdownListeners = new ArrayList<IDBShutdownListener>();
			}
			
			this._shutdownListeners.add(listener);
		}
	}
	
	/**
	 * execute
	 * 
	 * @param query
	 */
	public void execute(String query)
	{
		Connection connection = INSTANCE.getConnection();
		
		if (connection != null)
		{
			Statement statement = null;
			
			try
			{
				statement = connection.createStatement();
				statement.execute(query);
			}
			catch (SQLException e)
			{
				IdeLog.logInfo(AptanaCorePlugin.getDefault(), Messages.EventLogger_Query_Error, e);
			}
			finally
			{
				if (statement != null)
				{
					try
					{
						statement.close();
					}
					catch (SQLException e)
					{
						IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Statement_Close_Error, e);
					}
				}
				
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Connection_Close_Error, e);
				}
			}
		}
	}

	/**
	 * execute
	 * 
	 * @param query
	 * @param handler
	 */
	public void execute(String query, IResultSetHandler handler)
	{
		Connection connection = INSTANCE.getConnection();
		
		if (connection != null)
		{
			Statement statement = null;
			ResultSet resultSet = null;
			
			try
			{
				statement = connection.createStatement();
				
				resultSet = statement.executeQuery(query);
				
				while (resultSet.next())
				{
					handler.processResultSet(resultSet);
				}
			}
			catch (SQLException e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Query_Error, e);
			}
			finally
			{
				if (resultSet != null)
				{
					try
					{
						resultSet.close();
					}
					catch (SQLException e)
					{
						IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaDB_Error_Closing_ResultSet, e);
					}
				}
				
				if (statement != null)
				{
					try
					{
						statement.close();
					}
					catch (SQLException e)
					{
						IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Statement_Close_Error, e);
					}
				}
				
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Connection_Close_Error, e);
				}
			}
		}
	}

	/**
	 * getConnection
	 * 
	 * @return
	 */
	public Connection getConnection()
	{
		Connection result = null;
		
		if (this._driverLoaded)
		{
			try
			{
				/*
				 * The connection specifies create=true to cause the database to be created. To remove
				 * the database, remove the directory aptanaDB and its contents. The directory aptanaDB
				 * will be created under the directory that the system property derby.system.home points
				 * to, or the current directory if derby.system.home is not set.
				 */
				result = DriverManager.getConnection(PROTOCOL + DATABASE_NAME + ";create=true"); //$NON-NLS-1$
			}
			catch (SQLException e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Unable_To_Connect, e);
			}
		}
		
		return result;
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static AptanaDB getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new AptanaDB();
			INSTANCE.loadDriver();
		}
		
		return INSTANCE;
	}
	
	/**
	 * loadDriver
	 */
	protected void loadDriver()
	{
		try
		{
			String homeDirectoryName = System.getProperty("osgi.configuration.area"); //$NON-NLS-1$
			URL homeDirectoryURL = FileUtils.uriToURL(homeDirectoryName);
			File homeDirectory = FileUtils.urlToFile(homeDirectoryURL);
			
			// make sure home directory exists
			homeDirectory.mkdirs();
			
			// point Derby home to directory
			System.setProperty("derby.system.home", homeDirectory.getAbsolutePath()); //$NON-NLS-1$
			
			// load driver into VM
			Class.forName(DRIVER).newInstance();
			
			// tag as successfully loaded
			this._driverLoaded = true;
		}
		catch (InstantiationException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Unable_To_Instantiate, e);
		}
		catch (IllegalAccessException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Access_Denied, e);
		}
		catch (ClassNotFoundException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Class_Not_Found, e);
		}
	}

	/**
	 * removeShutdownListener
	 * 
	 * @param listener
	 */
	public void removeShutdownListener(IDBShutdownListener listener)
	{
		if (this._shutdownListeners != null)
		{
			this._shutdownListeners.remove(listener);
		}
	}
	
	/**
	 * shutdown
	 */
	public void shutdown()
	{
		if (this._driverLoaded)
		{
			// fire shutdown listeners
			if (this._shutdownListeners != null)
			{
				for (IDBShutdownListener listener : this._shutdownListeners)
				{
					listener.shutdown();
				}
			}
			
			try
		    {
		        DriverManager.getConnection(PROTOCOL + DATABASE_NAME + ";shutdown=true"); //$NON-NLS-1$
		    }
		    catch (SQLException e)
		    {
		    	// NOTE: We always get an exception when shutting down the database. We make sure it was the right
		    	// one for successful shutdown. SQLState is "08006" and ErrorCode is 45000 for single database shutdown
		        if ( e.getErrorCode() != 45000 && "XJ015".equals(e.getSQLState()) == false && "08006".equals(e.getSQLState()) == false ) //$NON-NLS-1$ //$NON-NLS-2$
		        {
		        	IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.EventLogger_Error_While_Shutting_Down, e);
		        }
		    }
		}
	}
}
