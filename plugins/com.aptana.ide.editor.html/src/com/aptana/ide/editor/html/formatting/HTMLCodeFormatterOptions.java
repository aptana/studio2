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
package com.aptana.ide.editor.html.formatting;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class HTMLCodeFormatterOptions
{

	/**
	 * formatterTabChar
	 */
	public String formatterTabChar;
	/**
	 * tabSize
	 */
	public int tabSize;

	/**
	 * notWrappingTags
	 */
	public final HashSet<String> notWrappingTags = new HashSet<String>();

	/**
	 * allwayswrapTags
	 */
	public final HashSet<String> allwaysWrap = new HashSet<String>();

	/**
	 * do not indent
	 */
	public final HashSet<String> doNotIndent = new HashSet<String>();

	/**
	 * do formatting
	 */
	public boolean doFormatting = true;

	/**
	 * do not wrap simple tags
	 */
	public boolean doNotWrapSimple = true;

	/**
	 * 
	 */
	public HTMLCodeFormatterOptions()
	{
		initFromPreferences();
	}

	/**
	 * @return default options with not wrapping simple tags
	 */
	public static Map createNotWrappingOptions()
	{
		Map map = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		map.put(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, Boolean.TRUE);
		return map;
	}

	/**
	 * @return default options with not wrapping simple tags
	 */
	public static Map createNotIndentingOptions()
	{
		Map map = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		map.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS, "html"); //$NON-NLS-1$
		return map;
	}

	private void initFromPreferences()
	{
		IPreferenceStore preferenceStore = HTMLPlugin.getDefault().getPreferenceStore();

		formatterTabChar = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		tabSize = preferenceStore.getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		String sm = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS);
		initTags(sm, notWrappingTags);
		sm = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS);
		initTags(sm, allwaysWrap);
		sm = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS);
		initTags(sm, doNotIndent);
		String string = preferenceStore.getString(DefaultCodeFormatterConstants.NO_FORMATTING);
		if (string.length() > 0)
		{
			doFormatting = false;
		}
		doNotWrapSimple = preferenceStore.getBoolean(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS);
	}

	private void initTags(String sm, HashSet<String> set)
	{
		if (sm == null)
		{
			return;
		}

		String[] nodes = sm.split(","); //$NON-NLS-1$
		for (int i = 0; i < nodes.length; i++)
		{
			String trim = nodes[i].trim();
			if (trim.length() > 0)
			{
				set.add(trim.toLowerCase());
			}
		}
	}

	/**
	 * @param map
	 * @param project
	 */
	public HTMLCodeFormatterOptions(Map map, IProject project)
	{
		if (project != null)
		{
			IEclipsePreferences preferences = new ProjectScope(project).getNode(HTMLPlugin.ID);
			String string = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, null);
			if (string == null)
			{
				initFromPreferences();
				return;
			}

			formatterTabChar = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, ""); //$NON-NLS-1$
			String sm = preferences.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS,
					DefaultCodeFormatterConstants.INITIAL_TAGS_DO_NOT_WRAP);
			initTags(sm, notWrappingTags);

			sm = preferences.get(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS,
					DefaultCodeFormatterConstants.INITIAL_TAGS_TO_WRAP);
			initTags(sm, allwaysWrap);

			sm = preferences.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS,
					DefaultCodeFormatterConstants.INITIAL_TAGS_TO_NOT_INDENT);
			initTags(sm, doNotIndent);

			tabSize = Integer.parseInt(string);
			string = preferences.get(DefaultCodeFormatterConstants.NO_FORMATTING, ""); //$NON-NLS-1$
			if (string.length() > 0)
			{
				doFormatting = false;
			}
			doNotWrapSimple = preferences.getBoolean(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, true);

		}
		else if (map == null)
		{
			initFromPreferences();
		}
		else
		{

			formatterTabChar = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
			String sm = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS);
			initTags(sm, notWrappingTags);
			sm = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS);
			initTags(sm, allwaysWrap);
			sm = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS);
			initTags(sm, doNotIndent);
			if (formatterTabChar == null)
			{
				formatterTabChar = " "; //$NON-NLS-1$
			}
			Object object10 = map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			if (object10 != null)
			{
				tabSize = Integer.parseInt(object10.toString());
			}
			else
			{
				tabSize = 4;
			}
			String sma = (String) map.get(DefaultCodeFormatterConstants.NO_FORMATTING);
			if (sma != null && sma.length() > 0)
			{
				doFormatting = false;
			}
			Object object21a = map.get(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS);
			if (object21a != null)
			{
				doNotWrapSimple = Boolean.parseBoolean(object21a.toString());
			}
		}
	}

}
