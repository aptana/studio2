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

import org.eclipse.ui.IStartup;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;

/**
 * Base timing startup to be extended by every startup job referenced in the plugin.xml of all plugins
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class BaseTimingStartup implements IStartup
{

	protected long initialStartTime;

	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		initialStartTime = System.currentTimeMillis();
		startup();
	}

	/**
	 * Start up method to be implemented by extending classes, should be used for code normally put in earlyStartup()
	 */
	protected abstract void startup();

	/**
	 * Gets the startup name of this object
	 * 
	 * @return - startup name
	 */
	public abstract String getStartupName();

	/**
	 * Callback for when this startup object is done. This should be called when all the jobs this object spawns or when
	 * startup() returns, whatever comes later so the full length this runs can be realized.
	 */
	protected void startupDone()
	{
		IdeLog.logInfo(CoreUIPlugin.getDefault(), StringUtils.format(Messages.BaseTimingStartup_INF_StartupTookMilliseconds,
				new Object[] {getStartupName(), (System.currentTimeMillis() - initialStartTime)}));
	}

}
