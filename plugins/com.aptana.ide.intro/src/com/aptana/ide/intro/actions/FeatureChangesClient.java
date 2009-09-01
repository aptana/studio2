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
package com.aptana.ide.intro.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.intro.FeatureChange;
import com.aptana.ide.intro.FeatureChangeManager;
import com.aptana.ide.server.jetty.comet.CometConstants;
import com.aptana.ide.server.jetty.comet.CometResponderClient;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FeatureChangesClient extends CometResponderClient
{

	/**
	 * FEATURE_CHANGE_CHANNEL
	 */
	public static final String FEATURE_CHANGE_CHANNEL = "/portal/features/changes"; //$NON-NLS-1$

	/**
	 * FEATURE_CHANGES
	 */
	public static final String FEATURE_CHANGES = "featureChanges"; //$NON-NLS-1$

	/**
	 * ID
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 * NEW_VERSION
	 */
	public static final String NEW_VERSION = "newVersion"; //$NON-NLS-1$

	/**
	 * OLD_VERSION
	 */
	public static final String OLD_VERSION = "oldVersion"; //$NON-NLS-1$

	/**
	 * PROVIDER
	 */
	public static final String PROVIDER = "provider"; //$NON-NLS-1$

	/**
	 * LABEL
	 */
	public static final String LABEL = "label"; //$NON-NLS-1$

	/**
	 * CHANGES
	 */
	public static final String CHANGES = "changes"; //$NON-NLS-1$

	/**
	 * CHANGES_EXIST
	 */
	public static final String CHANGES_EXIST = "changesExist"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (FEATURE_CHANGE_CHANNEL.equals(toChannel))
		{
			Map<Object, Object> responseData = new HashMap<Object, Object>();
			responseData.put(CometConstants.RESPONSE, FEATURE_CHANGES);
			boolean changed = FeatureChangeManager.getManager().areFeaturesChanged();
			List changes = FeatureChangeManager.getManager().getFeatureChangeList();
			List<Map<String, String>> responseChanges = new ArrayList<Map<String, String>>();
			if (changed)
			{
				for (int i = 0; i < changes.size(); i++)
				{
					Object obj = changes.get(i);
					if (obj instanceof FeatureChange)
					{
						Map<String, String> changeMap = new HashMap<String, String>();
						FeatureChange change = (FeatureChange) obj;
						changeMap.put(ID, change.getId());
						changeMap.put(LABEL, change.getLabel());
						changeMap.put(NEW_VERSION, change.getNewVersion());
						changeMap.put(OLD_VERSION, change.getOldVersion());
						changeMap.put(PROVIDER, change.getProvider());
						responseChanges.add(changeMap);
					}
				}
			}
			responseData.put(CHANGES, responseChanges);
			responseData.put(CHANGES_EXIST, Boolean.valueOf(changed));
			return responseData;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { FEATURE_CHANGE_CHANNEL };
	}

}
