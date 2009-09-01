/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.ui.editors.preferences.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.aptana.ide.editors.codeassist.Activator;
import com.aptana.ide.internal.ui.dialogs.PreferencesAccess;

/**
 * 
 *
 */
public class FormatterProfileManager extends ProfileManager {

	/**
	 * 
	 */
	public final static String JAVA_PROFILE = "org.eclipse.jdt.ui.default.sun_profile"; //$NON-NLS-1$
	
	public final static String NO_FORMATTING = "com.aptana.ide.formatting._noformatting"; //$NON-NLS-1$

	/**
	 * 
	 */
	public final static String DEFAULT_PROFILE = JAVA_PROFILE;

	private final static KeySet[] KEY_SETS = new KeySet[] {
			new KeySet(Activator.PLUGIN_ID, new ArrayList(getNoFormattingSettings().keySet())),
			new KeySet(Activator.PLUGIN_ID, Collections.EMPTY_LIST) };

	private final static String PROFILE_KEY = CommentsTabPage.FORMATTER_PROFILE;
	private final static String FORMATTER_SETTINGS_VERSION = "formatter_settings_version"; //$NON-NLS-1$

	

	/**
	 * @param profiles
	 * @param context
	 * @param preferencesAccess
	 * @param pluginId
	 */
	public FormatterProfileManager(List profiles, IScopeContext context,
			PreferencesAccess preferencesAccess,
			 String pluginId) {
		super(addBuiltinProfiles(profiles,pluginId), context,
				preferencesAccess,  KEY_SETS, PROFILE_KEY,
				FORMATTER_SETTINGS_VERSION,pluginId);
	}

	private static List addBuiltinProfiles(List profiles, String pluginId) {
		Map javaSettings = getJavaSettings();
		if (pluginId.equals("com.aptana.ide.editor.php")) //$NON-NLS-1$
		{
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION,
					DefaultCodeFormatterConstants.NEXT_LINE);
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
					DefaultCodeFormatterConstants.NEXT_LINE);
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE,
					DefaultCodeFormatterConstants.NEXT_LINE);
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH,
					DefaultCodeFormatterConstants.NEXT_LINE);
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, Boolean.TRUE
					.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, Boolean.TRUE
					.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH,
					Boolean.TRUE.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, Boolean.TRUE
					.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, Boolean.TRUE
					.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.TRUE
					.toString());
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, Boolean.FALSE
					.toString());
			
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS, "5"); //$NON-NLS-1$
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD, "0"); //$NON-NLS-1$
			javaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, "0"); //$NON-NLS-1$
		}
		final Profile javaProfile = new BuiltInProfile(JAVA_PROFILE,
				FormatterMessages.ProfileManager_java_conventions_profile_name,
				javaSettings, 1, 1,
				""); //$NON-NLS-1$
		
		final Profile noFormatting = new BuiltInProfile(NO_FORMATTING,
				FormatterMessages.ProfileManager_noformatting_profile_name,
				getNoFormattingSettings(), 1, 1,
				""); //$NON-NLS-1$
		
		profiles.add(javaProfile);
		profiles.add(noFormatting);
		return profiles;
	}

	
	/**
	 * @return Returns the settings for the no formatting profile.
	 */
	public static Map getNoFormattingSettings() {
		Map original=getJavaSettings();
		original.put(DefaultCodeFormatterConstants.NO_FORMATTING, "yes"); //$NON-NLS-1$
		return original;
	}

	/**
	 * @return Returns the settings for the default profile.
	 */
	public static Map getEclipse21Settings() {
		final Map options = DefaultCodeFormatterConstants
				.getEclipse21Settings();

		
		return options;
	}

	/**
	 * @return Returns the settings for the new eclipse profile.
	 */
	public static Map getEclipseSettings() {
		final Map options = DefaultCodeFormatterConstants
				.getEclipseDefaultSettings();

		return options;
	}

	/**
	 * @return Returns the settings for the Java Conventions profile.
	 */
	public static Map getJavaSettings() {
		final Map options = DefaultCodeFormatterConstants
				.getJavaConventionsSettings();

		return options;
	}

	/**
	 * @return Returns the default settings.
	 */
	public static Map getDefaultSettings() {
		return getEclipseSettings();
	}

	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ProfileManager#getSelectedProfileId(org.eclipse.core.runtime.preferences.IScopeContext)
	 */
	protected String getSelectedProfileId(IScopeContext instanceScope) {
		String profileId = instanceScope.getNode(pluginId).get(
				PROFILE_KEY, null);
		if (profileId == null) {
			// request from bug 129427
			profileId = new DefaultScope().getNode(pluginId).get(
					PROFILE_KEY, null);
			// fix for bug 89739
			if (DEFAULT_PROFILE.equals(profileId)) { // default default:
				IEclipsePreferences node = instanceScope
						.getNode(pluginId);
				if (node != null) {
					String tabSetting = node.get(
							DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR,
							null);
					if (CommentsTabPage.SPACE.equals(tabSetting)) {
						profileId = JAVA_PROFILE;
					}
				}
			}
		}
		return profileId;
	}


	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ProfileManager#getDefaultProfile()
	 */
	public Profile getDefaultProfile() {
		return getProfile(DEFAULT_PROFILE);
	}

}
