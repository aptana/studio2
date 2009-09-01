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

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.ui.preferences.ApplicationPreferences;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class EclipseDiagnosticLog implements IDiagnosticLog
{

	public String getLog()
	{
		StringBuilder buf = new StringBuilder();

		// Host OS
		buf.append(Messages.EclipseDiagnosticLog_HostOS);
		buf.append(System.getProperty("os.name")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// OS arch
		buf.append(Messages.EclipseDiagnosticLog_OSArch);
		buf.append(Platform.getOSArch());
		buf.append("\n"); //$NON-NLS-1$

		// JRE version
		buf.append(Messages.EclipseDiagnosticLog_JREVersion);
		buf.append(System.getProperty("java.version")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// Java vendor
		buf.append(Messages.EclipseDiagnosticLog_JREVendor);
		buf.append(System.getProperty("java.vendor")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// JRE home
		buf.append(Messages.EclipseDiagnosticLog_JREHome);
		buf.append(System.getProperty("java.home")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// Aptana home
		buf.append(Messages.EclipseDiagnosticLog_HomeDir);
		buf.append(Platform.getInstallLocation().getURL());
		buf.append("\n"); //$NON-NLS-1$

		// Eclipse version
		buf.append(Messages.EclipseDiagnosticLog_EclipseVersion);
		String property = System.getProperty("osgi.framework.version"); //$NON-NLS-1$
		int index = property.indexOf(".v"); //$NON-NLS-1$
		if (index > -1)
		{
			property = property.substring(0, index);
		}
		buf.append(property);
		buf.append("\n"); //$NON-NLS-1$

		// VM arguments
		buf.append(Messages.EclipseDiagnosticLog_VMArgs);
		property = System.getProperty("eclipse.vmargs"); //$NON-NLS-1$
		buf.append((property == null) ? "" : property); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// workspace area
		buf.append(Messages.EclipseDiagnosticLog_WorkspaceDir);
		buf.append(Platform.getInstanceLocation().getURL());
		buf.append("\n"); //$NON-NLS-1$

		// Language
		buf.append(Messages.EclipseDiagnosticLog_Language);
		buf.append(Platform.getNL());
		buf.append("\n"); //$NON-NLS-1$

		// Studio ID
		buf.append(Messages.EclipseDiagnosticLog_StudioID);
		buf.append(ApplicationPreferences.getInstance().getString(IPreferenceConstants.P_IDE_ID));
		buf.append("\n"); //$NON-NLS-1$

		// License username
		buf.append(Messages.EclipseDiagnosticLog_LicenseUser);
		buf.append(ApplicationPreferences.getInstance().getString(IPreferenceConstants.ACTIVATION_EMAIL_ADDRESS));
		buf.append("\n"); //$NON-NLS-1$

		return buf.toString();
	}

}
