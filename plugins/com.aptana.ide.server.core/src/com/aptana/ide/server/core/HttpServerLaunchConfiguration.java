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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.server.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PlatformUtils;
import com.aptana.ide.core.StringUtils;

/**
 * @author removeServerListener
 */
public class HttpServerLaunchConfiguration
{
	/*
	 * Fields
	 */
	private static final String BROWSER_EXE = "browserExecutable"; //$NON-NLS-1$
	private static final String WINDOWS_BROWSER = PlatformUtils.expandEnvironmentStrings("%ProgramFiles%\\Internet Explorer\\iexplore.exe"); //$NON-NLS-1$

	private static final String START_ACTION_TYPE = "startActionType"; //$NON-NLS-1$
	private static final String ENUM_START_ACTION_CURRENT_PAGE = "startCurrentPage"; //$NON-NLS-1$
	private static final String ENUM_START_ACTION_SPECIFIC_PAGE = "startSpecificPage"; //$NON-NLS-1$
	private static final String ENUM_START_ACTION_START_URL = "startUrl"; //$NON-NLS-1$

	/**
	 * START_ACTION_CURRENT_PAGE
	 */
	public static final int START_ACTION_CURRENT_PAGE = 1;

	/**
	 * START_ACTION_SPECIFIC_PAGE
	 */
	public static final int START_ACTION_SPECIFIC_PAGE = 2;

	/**
	 * START_ACTION_START_URL
	 */
	public static final int START_ACTION_START_URL = 3;

	private static final String EXTERNAL_BASE_URL = "externalBaseUrl"; //$NON-NLS-1$
	private static final String START_PAGE_PATH = "startPagePath"; //$NON-NLS-1$
	private static final String START_PAGE_URL = "startPageUrl"; //$NON-NLS-1$

	private static final String SERVER_TYPE = "serverType"; //$NON-NLS-1$
	private static final String TYPE_INTERNAL = "internal"; //$NON-NLS-1$
	private static final String TYPE_EXTERNAL = "external"; //$NON-NLS-1$

	/**
	 * SERVER_INTERNAL
	 */
	public static final int SERVER_INTERNAL = 4;

	/**
	 * SERVER_EXTERNAL
	 */
	public static final int SERVER_EXTERNAL = 5;

	private int serverType = SERVER_INTERNAL;
	private int startActionType = START_ACTION_CURRENT_PAGE;
	private String baseUrl;
	private String startPagePath;
	private String startPageUrl;
	private String browserExe;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of HttpServerLaunchConfiguration
	 */
	public HttpServerLaunchConfiguration()
	{
	}

	/**
	 * Create a new instance of HttpServerLaunchConfiguration
	 * 
	 * @param configuration
	 */
	public HttpServerLaunchConfiguration(ILaunchConfiguration configuration)
	{
		load(configuration);
	}

	/*
	 * Methods
	 */

	/**
	 * load
	 * 
	 * @param configuration
	 */
	public void load(ILaunchConfiguration configuration)
	{
		try
		{
			browserExe = configuration.getAttribute(BROWSER_EXE, StringUtils.EMPTY);

			String startTypeStr = configuration.getAttribute(START_ACTION_TYPE, ENUM_START_ACTION_CURRENT_PAGE);
			if (startTypeStr.equals(ENUM_START_ACTION_CURRENT_PAGE))
			{
				setStartActionType(START_ACTION_CURRENT_PAGE);
			}
			else if (startTypeStr.equals(ENUM_START_ACTION_SPECIFIC_PAGE))
			{
				setStartActionType(START_ACTION_SPECIFIC_PAGE);
			}
			else if (startTypeStr.equals(ENUM_START_ACTION_START_URL))
			{
				setStartActionType(START_ACTION_START_URL);
			}
			else
			{
				IdeLog.logError(ServerCorePlugin.getDefault(), StringUtils.format("Unknown launch start action {0}", startTypeStr)); //$NON-NLS-1$
			}
			startPagePath = configuration.getAttribute(START_PAGE_PATH, StringUtils.EMPTY);
			startPageUrl = configuration.getAttribute(START_PAGE_URL, StringUtils.EMPTY);

			String serverTypeStr = configuration.getAttribute(SERVER_TYPE, TYPE_INTERNAL);
			if (serverTypeStr.equals(TYPE_INTERNAL))
			{
				setServerType(SERVER_INTERNAL);
			}
			else if (serverTypeStr.equals(TYPE_EXTERNAL))
			{
				setServerType(SERVER_EXTERNAL);
			}
			else
			{
				IdeLog.logError(ServerCorePlugin.getDefault(), StringUtils.format(Messages.HttpServerLaunchConfiguration_UnknownLaunchServerType, serverTypeStr));
			}

			baseUrl = configuration.getAttribute(EXTERNAL_BASE_URL, StringUtils.EMPTY);
			startPagePath = configuration.getAttribute(START_PAGE_PATH, StringUtils.EMPTY);
			startPageUrl = configuration.getAttribute(START_PAGE_URL, StringUtils.EMPTY);
		}
		catch (CoreException ce)
		{
			IdeLog.logError(ServerCorePlugin.getDefault(), ce.getMessage(), ce);
		}
	}

	/**
	 * save
	 * 
	 * @param configuration
	 */
	public void save(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(BROWSER_EXE, browserExe);

		String startTypeStr = TYPE_INTERNAL;
		if (startActionType == START_ACTION_CURRENT_PAGE)
		{
			startTypeStr = ENUM_START_ACTION_CURRENT_PAGE;
		}
		else if (startActionType == START_ACTION_SPECIFIC_PAGE)
		{
			startTypeStr = ENUM_START_ACTION_SPECIFIC_PAGE;
		}
		else if (startActionType == START_ACTION_START_URL)
		{
			startTypeStr = ENUM_START_ACTION_START_URL;
		}
		configuration.setAttribute(START_ACTION_TYPE, startTypeStr);
		configuration.setAttribute(START_PAGE_PATH, startPagePath);
		configuration.setAttribute(START_PAGE_URL, startPageUrl);

		String serverTypeStr = TYPE_INTERNAL;
		if (serverType == SERVER_INTERNAL)
		{
			serverTypeStr = TYPE_INTERNAL;
		}
		else if (serverType == SERVER_EXTERNAL)
		{
			serverTypeStr = TYPE_EXTERNAL;
		}
		configuration.setAttribute(SERVER_TYPE, serverTypeStr);

		configuration.setAttribute(EXTERNAL_BASE_URL, baseUrl);
	}

	/**
	 * setDefaults
	 * 
	 * @param configuration
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(START_ACTION_TYPE, ENUM_START_ACTION_CURRENT_PAGE);
		configuration.setAttribute(SERVER_TYPE, TYPE_INTERNAL);

		// if IE is installed in the default location, force the launch config to use it explicitly
		// This fixes a bug that causes a really slow launch of IE if no exe is explicitly set.
		if (new File(WINDOWS_BROWSER).exists())
		{
			configuration.setAttribute(BROWSER_EXE, WINDOWS_BROWSER);
		}
	}

	/**
	 * Returns the server type.
	 * 
	 * @return int
	 */
	public int getServerType()
	{
		return serverType;
	}

	/**
	 * Sets the server type. (supported values: SERVER_INTERNAL or SERVER_EXTERNAL)
	 * 
	 * @param type
	 */
	public void setServerType(int type)
	{
		if (type != SERVER_EXTERNAL && type != SERVER_INTERNAL)
		{
			throw new IllegalArgumentException(StringUtils.format(Messages.HttpServerLaunchConfiguration_UnknownServerType, type));
		}
		this.serverType = type;
	}

	/**
	 * getExternalBaseUrl
	 * 
	 * @return String
	 */
	public String getExternalBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * setExternalBaseUrl
	 * 
	 * @param url
	 */
	public void setExternalBaseUrl(String url)
	{
		baseUrl = url;
	}

	/**
	 * getStartPagePath
	 * 
	 * @return String
	 */
	public String getStartPagePath()
	{
		return startPagePath;
	}

	/**
	 * setStartPagePath
	 * 
	 * @param startPagePath
	 */
	public void setStartPagePath(String startPagePath)
	{
		this.startPagePath = startPagePath;
	}

	/**
	 * getStartPageUrl
	 * 
	 * @return getStartPageUrl
	 */
	public String getStartPageUrl()
	{
		return startPageUrl;
	}

	/**
	 * setStartPageUrl
	 * 
	 * @param startPageUrl
	 */
	public void setStartPageUrl(String startPageUrl)
	{
		this.startPageUrl = startPageUrl;
	}

	/**
	 * getStartActionType
	 * 
	 * @return int
	 */
	public int getStartActionType()
	{
		return startActionType;
	}

	/**
	 * setStartActionType
	 * 
	 * @param startActionType
	 */
	public void setStartActionType(int startActionType)
	{
		if (startActionType != START_ACTION_CURRENT_PAGE && startActionType != START_ACTION_SPECIFIC_PAGE
				&& startActionType != START_ACTION_START_URL)
		{
			throw new IllegalArgumentException(StringUtils.format(Messages.HttpServerLaunchConfiguration_UnknownServerStartActionType, startActionType));
		}
		this.startActionType = startActionType;
	}

	/**
	 * getBrowserExe
	 * 
	 * @return String
	 */
	public String getBrowserExe()
	{
		return browserExe;
	}

	/**
	 * setBrowserExe
	 * 
	 * @param browserExe
	 */
	public void setBrowserExe(String browserExe)
	{
		this.browserExe = browserExe;
	}
}
