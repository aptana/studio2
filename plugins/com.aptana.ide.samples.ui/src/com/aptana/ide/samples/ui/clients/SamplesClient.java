/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.samples.ui.clients;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.samples.ui.SamplesView;
import com.aptana.ide.server.jetty.comet.CometConstants;
import com.aptana.ide.server.jetty.comet.CometResponderClient;

/**
 * 
 * @author Sandip V. Chitale
 *
 */
public class SamplesClient extends CometResponderClient
{
	// Channel
	/**
	 * SAMPLES
	 */
	public static final String SAMPLES = "/portal/samples"; //$NON-NLS-1$
	
	/**
	 * SHOW_SAMPLES
	 */
	public static final String SHOW_SAMPLES = "showSamplesView"; //$NON-NLS-1$


	public SamplesClient()
	{
	
	}
	
	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		super.destroy();
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (SAMPLES.equals(toChannel) && request instanceof Map)
		{
			Map requestData = (Map) request;
			if (requestData.containsKey(CometConstants.REQUEST))
			{
				String requestType = requestData.get(CometConstants.REQUEST).toString();
				Map<Object, Object> responseData = null;
				if (SHOW_SAMPLES.equals(requestType))
				{
					UIJob job = new UIJob("Opening samples view") //$NON-NLS-1$
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							try
							{
								IViewPart part = CoreUIUtils.showView(SamplesView.ID);
							}
							catch (PartInitException e1)
							{
								// Do nothing, view didn't open
							}
							
							return Status.OK_STATUS;
						}

					};
					job.setSystem(true);
					job.schedule();
				}
				
			}
		}
		return null;
	}

	@Override
	protected String[] getSubscriptionIDs()
	{
		return new String[] { SAMPLES };
	}
	
	@Override
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}
	
}
