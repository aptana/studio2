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
package com.aptana.ide.server.jetty;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.server.jetty.comet.CometConstants;
import com.aptana.ide.server.jetty.comet.CometResponderClient;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreferenceClient extends CometResponderClient
{

	/**
	 * PREFERENCE_CHANNEL
	 */
	public static final String PREFERENCE_CHANNEL = "/portal/preferences"; //$NON-NLS-1$

	/**
	 * SET_ACTION
	 */
	public static final String SET_ACTION = "set"; //$NON-NLS-1$

	/**
	 * GET_ACTION
	 */
	public static final String GET_ACTION = "get"; //$NON-NLS-1$

	/**
	 * SHOW_ACTION
	 */
	public static final String SHOW_ACTION = "show"; //$NON-NLS-1$

	/**
	 * NAME
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * VALUE
	 */
	public static final String VALUE = "value"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (PREFERENCE_CHANNEL.equals(toChannel) && request instanceof Map)
		{
			Map requestData = (Map) request;
			if (requestData.containsKey(CometConstants.REQUEST))
			{
				String type = requestData.get(CometConstants.REQUEST).toString();
				if (SET_ACTION.equals(type) && requestData.containsKey(VALUE) && requestData.containsKey(NAME))
				{
					String name = requestData.get(NAME).toString();
					String value = requestData.get(VALUE).toString();
					JettyPlugin.getDefault().getPreferenceStore().setValue(name, value);
				}
				else if (GET_ACTION.equals(type) && requestData.containsKey(NAME))
				{
					String name = requestData.get(NAME).toString();
					Map<String, String> responseData = new HashMap<String, String>();
					responseData.put(CometConstants.RESPONSE, GET_ACTION);
					responseData.put(NAME, name);
					responseData.put(VALUE, JettyPlugin.getDefault().getPreferenceStore().getString(name));
					return responseData;
				}
				else if (SHOW_ACTION.equals(type))
				{
					if (requestData.containsKey(CometConstants.ID))
					{
						final String pageId = requestData.get(CometConstants.ID).toString();
						UIJob job = new UIJob(Messages.PreferenceClient_Job_RunJaxer)
						{

							public IStatus runInUIThread(IProgressMonitor monitor)
							{
								PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getDisplay()
										.getActiveShell(), pageId, new String[] {}, null);
								dialog.open();
								return Status.OK_STATUS;
							}

						};
						job.setSystem(true);
						job.schedule();
					}
				}
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { PREFERENCE_CHANNEL };
	}
}
