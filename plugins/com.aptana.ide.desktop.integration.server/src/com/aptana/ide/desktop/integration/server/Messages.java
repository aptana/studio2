/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.desktop.integration.server;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Sandip Chitale
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.desktop.integration.server.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * LaunchHelper_UnableToCLoseWelcome
	 */
	public static String LaunchHelper_UnableToCLoseWelcome;
	
	/**
	 * LaunchHelper_ErrorOpeningFileOnStartup
	 */
	public static String LaunchHelper_ErrorOpeningFileOnStartup;
	
	/**
	 * LaunchHelper_UnableToGetFileContents
	 */
	public static String LaunchHelper_UnableToGetFileContents;
	
	/**
	 * LaunchHelper_AptanaPortCachedInFile
	 */
	public static String LaunchHelper_AptanaPortCachedInFile;
	
	/**
	 * LaunchHelper_PortCacheFile
	 */
	public static String LaunchHelper_PortCacheFile;
	
	/**
	 * LaunchHelper_ErrorInChdeckingForCurrentInstance
	 */
	public static String LaunchHelper_ErrorInChdeckingForCurrentInstance;
	
	/**
	 * LaunchHelper_UnableToFindCurrentPort
	 */
	public static String LaunchHelper_UnableToFindCurrentPort;
	
	/**
	 * LaunchHelper_ErrorInClosingFileReader
	 */
	public static String LaunchHelper_ErrorInClosingFileReader;
	
	/**
	 * LaunchHelper_UnknownLocalHost
	 */
	public static String LaunchHelper_UnknownLocalHost;
	
	/**
	 * LaunchHelper_CouldNotGetIOConnection
	 */
	public static String LaunchHelper_CouldNotGetIOConnection;
	
	/**
	 * LaunchHelper_TryingToConnectToUnknownHost
	 */
	public static String LaunchHelper_TryingToConnectToUnknownHost;
	
	/**
	 * LaunchHelper_IOExceptionEncountered
	 */
	public static String LaunchHelper_IOExceptionEncountered;
	
	/**
	 * LaunchHelper_CouldNotFindOpenPort
	 */
	public static String LaunchHelper_CouldNotFindOpenPort;
	
	/**
	 * LaunchHelper_BoundAptanaToPort
	 */
	public static String LaunchHelper_BoundAptanaToPort;
	
	/**
	 * LaunchHelper_UnableToBindToPort
	 */
	public static String LaunchHelper_UnableToBindToPort;
	
	/**
	 * LaunchHelper_TheStartupListenerClassIsNotAvailable
	 */
	public static String LaunchHelper_TheStartupListenerClassIsNotAvailable;
	
	/**
	 * LaunchHelper_ErrorHookingStartupListener
	 */
	public static String LaunchHelper_ErrorHookingStartupListener;
	
	/**
	 * LaunchHelper_UnableToRecognizeCommandLineLaunchArguments
	 */
	public static String LaunchHelper_UnableToRecognizeCommandLineLaunchArguments;
}
