/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.intro.actions.messages"; //$NON-NLS-1$

	public static String CoreIntroAction_ERR_RunAction;

    public static String CoreIntroAction_Job_ErrorRunAction;

    public static String NewFileWizardClient_Job_ErrorLaunchWizard;

    public static String NewFileWizardClient_Job_NewFilesDialog;

    public static String NewWizardClient_INF_ErrorLaunchWizard;

    public static String NewWizardClient_Job_NewProjectDialog;

    public static String OpenWizardClient_INF_ErrorLaunchWizard;

    public static String OpenWizardClient_Job_OpenWizard;

    /**
	 * StartPageTrimWidget_ID_HELP
	 */
	public static String StartPageTrimWidget_ID_HELP;

	/**
	 * StartPageTrimWidget_MyAccount
	 */
	public static String StartPageTrimWidget_MyAccount;

	/**
	 * StartPageTrimWidget_MyAptana
	 */
	public static String StartPageTrimWidget_MyAptana;

	/**
	 * StartPageTrimWidget_MyCloud
	 */
	public static String StartPageTrimWidget_MyCloud;

	/**
	 * StartPageTrimWidget_SignIn
	 */
	public static String StartPageTrimWidget_SignIn;

	/**
	 * StartPageTrimWidget_SignOut
	 */
	public static String StartPageTrimWidget_SignOut;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}

}
