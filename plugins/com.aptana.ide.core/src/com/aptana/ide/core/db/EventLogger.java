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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Kevin Lindsey
 */
public final class EventLogger
{
	static EventLogger INSTANCE;
	
	private static final String TABLE_NAME = "eventLog"; //$NON-NLS-1$
	private static final String DATE_TIME = "dateTime"; //$NON-NLS-1$
	private static final String EVENT_TYPE = "eventType"; //$NON-NLS-1$
	private static final String MESSAGE = "message"; //$NON-NLS-1$
	
	private static final String GET_EVENTS = MessageFormat.format(
		"SELECT {0},{1},{2} FROM {3} ORDER BY {0}",	//$NON-NLS-1$
		new Object[] {
			DATE_TIME,
			EVENT_TYPE,
			MESSAGE,
			TABLE_NAME
		}
	);
	
	/**
	 * EventLogger
	 */
	private EventLogger()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static EventLogger getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new EventLogger();
			
			// get db
			AptanaDB db = AptanaDB.getInstance();

			if (INSTANCE.tableExists() == false)
			{
				// build table creation query
				String query = MessageFormat.format(
					"CREATE TABLE {0}({1} bigint,{2} varchar(255),{3} varchar(255))", //$NON-NLS-1$
					new Object[] {
						TABLE_NAME,
						DATE_TIME,
						EVENT_TYPE,
						MESSAGE
					}
				);
				
				// create table
				db.execute(query);
			}
		}
		
		return INSTANCE;
	}
	
	/**
	 * logEvent
	 * 
	 * @param eventType
	 */
	public void logEvent(String eventType)
	{
		this.logEvent(eventType, null);
	}
	
	/**
	 * logEvent
	 * 
	 * @param eventType
	 * @param message
	 */
	public void logEvent(String eventType, String message)
	{
		String dateTime = Long.toString(System.currentTimeMillis());
		String query = MessageFormat.format(
			"INSERT INTO {0}({1},{2},{3}) VALUES({4},''{5}'',''{6}'')",	//$NON-NLS-1$
			new Object[] {
				TABLE_NAME,
				DATE_TIME,
				EVENT_TYPE,
				MESSAGE,
				dateTime,
				(eventType != null && eventType.length() > 0) ? eventType : LogEventTypes.UNKNOWN,	//$NON-NLS-1$
				(message != null) ? message : ""	//$NON-NLS-1$
			}
		);
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * clearEvents
	 */
	public void clearEvents()
	{
		String query = "DELETE FROM " + TABLE_NAME; //$NON-NLS-1$
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * clearEvents
	 * 
	 * @param eventType
	 */
	public void clearEvents(String eventType)
	{
		String query = MessageFormat.format(
			"DELETE FROM {0} WHERE {1}=''{2}''", //$NON-NLS-1$
			new Object[] {
				TABLE_NAME,
				EVENT_TYPE,
				eventType
			}
		);
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * clearEvents
	 * 
	 * @param eventType
	 */
	public void clearEvents(long beforeTime)
	{
		String query = MessageFormat.format(
				"DELETE FROM {0} WHERE {1} < {2}", //$NON-NLS-1$
				new Object[] {
						TABLE_NAME,
						DATE_TIME,
						Long.toString(beforeTime)
				}
		);
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * clearEvents
	 * 
	 * @param eventType
	 */
	public void clearEvents(String eventType, long beforeTime)
	{
		String query = MessageFormat.format(
			"DELETE FROM {0} WHERE {1}=''{2}'' AND {3} < {4}", //$NON-NLS-1$
			new Object[] {
				TABLE_NAME,
				EVENT_TYPE,
				eventType,
				DATE_TIME,
				Long.toString(beforeTime)
			}
		);
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * getEvents
	 * 
	 * @return
	 */
	public EventInfo[] getEvents()
	{
		final List<EventInfo> events = new ArrayList<EventInfo>();
		
		AptanaDB.getInstance().execute(
			GET_EVENTS,
			new IResultSetHandler() {
				public void processResultSet(ResultSet resultSet) throws SQLException
				{
					long dateTime = resultSet.getLong(1);
					String eventType = resultSet.getString(2);
					String message = resultSet.getString(3);
					
					events.add(new EventInfo(dateTime, eventType, message));
				}
			}
		);
		
		return events.toArray(new EventInfo[events.size()]);
	}
	
	/**
	 * getEvents
	 * 
	 * @param eventType
	 * @return
	 */
	public EventInfo[] getEvents(String eventType)
	{
		String query = MessageFormat.format(
			"SELECT {0},{1},{2} FROM {3} WHERE {1}=''{4}'' ORDER BY {0}", //$NON-NLS-1$
			new Object[] {
				DATE_TIME,
				EVENT_TYPE,
				MESSAGE,
				TABLE_NAME,
				eventType
			}
		);
		final List<EventInfo> events = new ArrayList<EventInfo>();
		
		AptanaDB.getInstance().execute(
			query,
			new IResultSetHandler() {
				public void processResultSet(ResultSet resultSet) throws SQLException
				{
					long dateTime = resultSet.getLong(1);
					String eventType = resultSet.getString(2);
					String message = resultSet.getString(3);
					
					events.add(new EventInfo(dateTime, eventType, message));
				}
			}
		);
		
		return events.toArray(new EventInfo[events.size()]);
	}
	
	/**
	 * tableExists
	 * 
	 * @return
	 */
	private boolean tableExists()
	{
		Connection connection = null;
		Statement statement = null;
		boolean result = true;
		
		try
		{
			connection = AptanaDB.getInstance().getConnection();
			
			if (connection != null)
			{
				statement = connection.createStatement();
				statement.execute("SELECT COUNT(*) FROM " + TABLE_NAME); //$NON-NLS-1$
			}
			else
			{
				result = false;
			}
		}
		catch (SQLException sqle)
		{
			String errorStateCode = sqle.getSQLState();

			if (errorStateCode.equals("42X05")) // Table does not exist //$NON-NLS-1$
			{
				result = false;
			}
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
				}
			}
			
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
		
		return result;
	}
}
