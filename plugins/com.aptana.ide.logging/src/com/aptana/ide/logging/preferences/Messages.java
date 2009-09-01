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
package com.aptana.ide.logging.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * Messages.
 * @author Denis Denisenko
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "com.aptana.ide.logging.preferences.messages"; //$NON-NLS-1$
    
    public static String EditRuleDialog_0;
    public static String EditRuleDialog_1;
    public static String EditRuleDialog_2;
    public static String EditRuleDialog_RuleError_RegexpRule_Content;
    public static String EditRuleDialog_RuleError_SimpleRule_Content;
    public static String LoggingColorizationWidget_29;
    public static String LoggingPreferenceWidget_0;
    public static String LoggingPreferenceWidget_Autobolding_Label;
    public static String LoggingPreferenceWidget_1;
    public static String LoggingPreferenceWidget_CursorLineColor_Label;

	public static String LoggingPreferenceWidget_CUSTOMIZE_LABEL;
    public static String LoggingPreferenceWidget_Font_Label;
    public static String LoggingPreferenceWidget_4;
    public static String LoggingPreferenceWidget_5;
    public static String LoggingPreferenceWidget_6;
    public static String LoggingPreferenceWidget_7;
    public static String LoggingPreferenceWidget_8;
    public static String LoggingPreferenceWidget_9;
    public static String LoggingPreferenceWidget_10;
    public static String LoggingPreferenceWidget_11;
    public static String LoggingPreferenceWidget_12;
    public static String LoggingPreferenceWidget_14;
    public static String LoggingPreferenceWidget_15;
    public static String LoggingPreferenceWidget_13;
    public static String LoggingPreferenceWidget_Wrapping_Label;
    public static String LoggingColorizationWidget_30;
    public static String LoggingColorizationWidget_32;
    public static String LoggingColorizationWidget_31;
    public static String LoggingColorizationWidget_UpRuleButton;
    public static String LoggingColorizationWidget_DownRuleButton;
    public static String LoggingColorizationWidget_27;
    public static String LoggingColorizationWidget_33;
    public static String LoggingColorizationWidget_34;
    public static String LoggingColorizationWidget_35;
    public static String LoggingColorizationWidget_36;
    public static String LoggingColorizationWidget_37;
    public static String LoggingColorizationWidget_39;
    public static String LoggingColorizationWidget_40;
    public static String LoggingColorizationWidget_38;
    public static String LoggingColorizationWidget_41;
    public static String LoggingColorizationWidget_42;
    public static String LoggingColorizationWidget_43;
    public static String LoggingColorizationWidget_44;
    public static String LoggingColorizationWidget_45;
    public static String LoggingColorizationWidget_46;
    public static String LoggingColorizationWidget_47;
    public static String LoggingColorizationWidget_48;
    public static String LoggingColorizationWidget_49;
    public static String LoggingColorizationWidget_50;
    public static String LoggingColorizationWidget_51;
    public static String LoggingColorizationWidget_52;
    public static String LoggingColorizationWidget_53;
    public static String LoggingColorizationWidget_54;
    public static String LoggingColorizationWidget_55;
    public static String LoggingColorizationWidget_60;
    public static String LoggingColorizationWidget_63;
    public static String LoggingColorizationWidget_64;
    public static String LoggingColorizationWidget_66;
    public static String LoggingColorizationWidget_68;
    public static String NewRuleDialog_6;
    public static String NewRuleDialog_7;
    public static String NewRuleDialog_0;
    public static String NewRuleDialog_Rule_Message;
    public static String NewRuleDialog_2;
    public static String NewRuleDialog_4;
    public static String NewRuleDialog_8;
    public static String NewRuleDialog_RuleError_Name;
    public static String NewRuleDialog_RuleError_RegexpRule_Content;
    public static String General_Tab_Name;
    public static String General_Tab_ToolTip;
    public static String Coloring_Tab_Name;
    public static String Coloring_Tab_ToolTip;

    public static String CaseSensitiveLabel;

    public static String LoggingColorizationWidget_ConfirmRewritingTitle;

    public static String LoggingColorizationWidget_ConfirmRewritingMessage;
    public static String LoggingColorizationWidget_ConfirmRewritingCurrentMessage;

    public static String NewRuleDialog_RuleError_Name_AlreadyExists;

    public static String NewRuleDialog_TTP_RegularExpression;

    public static String NewRuleDialog_TTP_Search;

    public static String LoggingPreferenceWidget_TextForegroundColor_Label;

    public static String LoggingStructureProvider_ERR_Loading;

//    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
//            .getBundle(BUNDLE_NAME);
    
    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }

//    public static String getString(String key)
//    {
//        try
//        {
//            return RESOURCE_BUNDLE.getString(key);
//        } catch (MissingResourceException e)
//        {
//            return '!' + key + '!';
//        }
//    }
}
