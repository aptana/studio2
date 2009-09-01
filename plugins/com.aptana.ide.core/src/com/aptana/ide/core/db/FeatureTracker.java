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
public final class FeatureTracker
{
	static FeatureTracker INSTANCE;
	
	private static final String TABLE_NAME = "features"; //$NON-NLS-1$
	private static final String FEATURE = "feature"; //$NON-NLS-1$
	private static final String VERSION = "version"; //$NON-NLS-1$
	private static final String ENABLED = "enabled"; //$NON-NLS-1$
	
	private static final String GET_FEATURES = MessageFormat.format(
		"SELECT {0}, {1}, {2} FROM {3}", //$NON-NLS-1$
		new Object[] {
			FEATURE,
			VERSION,
			ENABLED,
			TABLE_NAME
		}
	);
	
	/**
	 * EventLogger
	 */
	private FeatureTracker()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static FeatureTracker getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new FeatureTracker();
			
			// get db
			AptanaDB db = AptanaDB.getInstance();

			if (INSTANCE.tableExists() == false)
			{
				// build table creation query
				String query = MessageFormat.format(
					"CREATE TABLE {0}({1} varchar(255),{2} varchar(255),{3} varchar(255))", //$NON-NLS-1$
					new Object[] {
						TABLE_NAME,
						FEATURE,
						VERSION,
						ENABLED
					}
				);
				
				// create table
				db.execute(query);
			}
		}
		
		return INSTANCE;
	}
	
	/**
	 * addFeature
	 * 
	 * @param feature
	 */
	public void addFeature(FeatureInfo feature)
	{
		this.addFeature(feature.name, feature.version, Boolean.toString(feature.enabled));
	}
	
	/**
	 * logEvent
	 * 
	 * @param eventType
	 */
	public void addFeature(String feature, String version, String enabled)
	{
		String query = MessageFormat.format(
			"INSERT INTO {0}({1},{2},{3}) VALUES(''{4}'',''{5}'',''{6}'')",	//$NON-NLS-1$
			new Object[] {
				TABLE_NAME,
				FEATURE,
				VERSION,
				ENABLED,
				(feature != null && feature.length() > 0) ? feature : "", //$NON-NLS-1$
				(version != null && version.length() > 0) ? version : "", //$NON-NLS-1$
				(enabled != null && enabled.length() > 0) ? enabled : "" //$NON-NLS-1$
			}
		);
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * clearFeatures
	 */
	public void clearFeatures()
	{
		String query = "DELETE FROM " + TABLE_NAME; //$NON-NLS-1$
		
		AptanaDB.getInstance().execute(query);
	}
	
	/**
	 * getFeatures
	 * 
	 * @return
	 */
	public FeatureInfo[] getFeatures()
	{
		final List<FeatureInfo> features = new ArrayList<FeatureInfo>();
		
		AptanaDB.getInstance().execute(
			GET_FEATURES,
			new IResultSetHandler() {
				public void processResultSet(ResultSet resultSet) throws SQLException
				{
					String feature = resultSet.getString(1);
					String version = resultSet.getString(2);
					String enabled = resultSet.getString(3);
					
					features.add(new FeatureInfo(feature, version, Boolean.parseBoolean(enabled)));
				}
			}
		);
		
		return features.toArray(new FeatureInfo[features.size()]);
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
