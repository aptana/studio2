/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.samples.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.samples.ui.messages"; //$NON-NLS-1$

	/**
	 * SamplesProjectWizard_CreateProject
	 */
	public static String SamplesProjectWizard_CreateProject;

	/**
	 * SamplesProjectWizard_CreatingProject
	 */
	public static String SamplesProjectWizard_CreatingProject;

	/**
	 * SamplesProjectWizard_NewSamplesProject
	 */
	public static String SamplesProjectWizard_NewSamplesProject;

	/**
	 * SamplesProjectWizard_SamplesProject
	 */
	public static String SamplesProjectWizard_SamplesProject;

	/**
	 * SamplesView_CollapseAll
	 */
	public static String SamplesView_CollapseAll;

	/**
	 * SamplesView_ErrorOpening
	 */
	public static String SamplesView_ErrorOpening;

	/**
	 * SamplesView_ImportSample
	 */
	public static String SamplesView_ImportSample;

	/**
	 * SamplesView_Job_Loading
	 */
    public static String SamplesView_Job_Loading;

    /**
     * SamplesView_Job_Updating
     */
    public static String SamplesView_Job_Updating;

    /**
     * SamplesView_Job_UpdatingExplorer
     */
    public static String SamplesView_Job_UpdatingExplorer;

	/**
	 * SamplesView_PreviewSample
	 */
	public static String SamplesView_PreviewSample;

	/**
	 * SamplesView_TXT_OpenCopy
	 */
    public static String SamplesView_TXT_OpenCopy;

	/**
	 * SamplesView_UnableToCreateTemp
	 */
	public static String SamplesView_UnableToCreateTemp;

	/**
	 * SamplesView_UnableToOpenFile
	 */
	public static String SamplesView_UnableToOpenFile;

	/**
	 * SamplesView_UnableToPreview
	 */
	public static String SamplesView_UnableToPreview;

	/**
	 * SamplesView_ViewHelp
	 */
	public static String SamplesView_ViewHelp;

	/**
	 * SamplesView_ViewPreview
	 */
	public static String SamplesView_ViewPreview;

	/**
	 * SamplesViewLabelProvider_INF_ImageNotFound
	 */
    public static String SamplesViewLabelProvider_INF_ImageNotFound;

    /**
     * SamplesViewLabelProvider_TXT_Loading
     */
    public static String SamplesViewLabelProvider_TXT_Loading;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
