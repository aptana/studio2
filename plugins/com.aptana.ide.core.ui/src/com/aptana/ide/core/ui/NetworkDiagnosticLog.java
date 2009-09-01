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
package com.aptana.ide.core.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NetworkDiagnosticLog implements IDiagnosticLog
{

	/**
	 * Constructor.
	 */
	public NetworkDiagnosticLog()
	{
	}

	/**
	 * @see com.aptana.ide.core.ui.IDiagnosticLog#getLog()
	 */
	public String getLog()
	{
		StringBuilder sb = new StringBuilder();

		InetAddress address = null;
		try
		{
			address = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e)
		{
		}
		// IP Address
		sb.append(Messages.NetworkDiagnosticLog_Host_IP);
		if (address == null)
		{
			sb.append(Messages.NetworkDiagnosticLog_Unknown);
		}
		else
		{
			sb.append(address.getHostAddress());
		}
		sb.append("\n"); //$NON-NLS-1$

		// Host name
		sb.append(Messages.NetworkDiagnosticLog_HostName);
		if (address == null)
		{
			sb.append(Messages.NetworkDiagnosticLog_Unknown);
		}
		else
		{
			sb.append(address.getHostName());
		}
		sb.append("\n"); //$NON-NLS-1$

		// indicates if the address is reachable
		sb.append(Messages.NetworkDiagnosticLog_AddressReachable);
		if (address == null)
		{
			sb.append(false);
		}
		else
		{
			try
			{
				sb.append(address.isReachable(5000));
			}
			catch (IOException e)
			{
				sb.append(false);
			}
		}
		sb.append("\n"); //$NON-NLS-1$

		return sb.toString();
	}

}
