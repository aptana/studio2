/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.editors.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public final class CodeAssistMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.preferences.codeassistmessages"; //$NON-NLS-1$

	private CodeAssistMessages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CodeAssistMessages.class);
	}

	/**
	 * CodeAssistExpressionInfoDialog_AddXPathExpression
	 */
	public static String CodeAssistExpressionInfoDialog_AddXPathExpression;
	
	/**
	 * CodeAssistExpressionInfoDialog_AddXPathExpressionToTriggerAttributes
	 */
	public static String CodeAssistExpressionInfoDialog_AddXPathExpressionToTriggerAttributes;
	
	/**
	 * CodeAssistExpressionInfoDialog_AttributeExpression
	 */
	public static String CodeAssistExpressionInfoDialog_AttributeExpression;
	
	/**
	 * CodeAssistExpressionInfoDialog_AttributeXPath
	 */
	public static String CodeAssistExpressionInfoDialog_AttributeXPath;

	/**
	 * CodeAssistPreferencePage_AutoActivation
	 */
	public static String CodeAssistPreferencePage_AutoActivation;

	/**
	 * CodeAssistPreferencePage_EnableAutoActivation
	 */
	public static String CodeAssistPreferencePage_EnableAutoActivation;

	/**
	 * CodeAssistPreferencePage_PreferencesForCodeAssist
	 */
	public static String CodeAssistPreferencePage_PreferencesForCodeAssist;
	
	/**
	 * CodeAssistPreferencePage_XPathExpressionsForCodeAssist
	 */
	public static String CodeAssistPreferencePage_XPathExpressionsForCodeAssist;
	
	/**
	 * CodeAssistPreferencePage_Expression
	 */
	public static String CodeAssistPreferencePage_Expression;
	
	/**
	 * CodeAssistPreferencePage_XPath
	 */
	public static String CodeAssistPreferencePage_XPath;

	/**
	 * ErrorDescriptorInfoDialog_AddRegEx
	 */
	public static String ErrorDescriptorInfoDialog_AddRegEx;

	/**
	 * ErrorDescriptorInfoDialog_IgnoreWarningError
	 */
	public static String ErrorDescriptorInfoDialog_IgnoreWarningError;

	/**
	 * ErrorDescriptorInfoDialog_Message
	 */
	public static String ErrorDescriptorInfoDialog_Message;

	/**
	 * ProblemsPreferencePage_ProblemsDescription
	 */
	public static String ProblemsPreferencePage_ProblemsDescription;

    /**
     * ProblemsPreferencePage_ProblemViewFilters
     */
    public static String ProblemsPreferencePage_ProblemViewFilters;

    /**
     * ProblemsPreferencePage_Validators
     */
    public static String ProblemsPreferencePage_Validators;

	/**
	 * UserAgentPreferencePage_SelectAll
	 */
	public static String UserAgentPreferencePage_SelectAll;

	/**
	 * UserAgentPreferencePage_SelectBrowsers
	 */
	public static String UserAgentPreferencePage_SelectBrowsers;

	/**
	 * UserAgentPreferencePage_SelectNone
	 */
	public static String UserAgentPreferencePage_SelectNone;

}
