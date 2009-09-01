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
package com.aptana.ide.editor.xml.formatting;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class XMLCodeFormatterOptions
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
	 * spacesInMultiline
	 */
	public int spacesInMultiline;

	/**
	 * notWrappingTags
	 */
	public final HashSet notWrappingTags = new HashSet();

	/**
	 * allwayswrapTags
	 */
	public final HashSet allwaysWrap = new HashSet();

	/**
	 * do formatting
	 */
	public boolean doFormatting = true;

	/**
	 * preserve returns
	 */
	public boolean preserveReturns = true;

	/**
	 * preserve whitespaces in CDATA
	 */
	public boolean preserveWhitespacesInCDATA = true;

	/**
	 * do not wrap simple tags
	 */
	public boolean doNotWrapSimple = true;

	/**
	 * 
	 */
	public XMLCodeFormatterOptions()
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
		map.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2, "a,b"); //$NON-NLS-1$
		return map;
	}

	/**
	 * @return default options with no preserving of carriage returns
	 */
	public static Map createNotPreservingReturnsOptions()
	{
		Map map = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		map.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.FALSE);
		return map;
	}

	/**
	 * initializes options from preference store
	 */
	private void initFromPreferences()
	{
		IPreferenceStore preferenceStore = XMLPlugin.getDefault().getPreferenceStore();

		formatterTabChar = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		tabSize = preferenceStore.getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		spacesInMultiline = preferenceStore
				.getInt(DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE);
		String sm = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2);
		initTags(sm, notWrappingTags);
		sm = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2);
		initTags(sm, allwaysWrap);
		String string = preferenceStore.getString(DefaultCodeFormatterConstants.NO_FORMATTING);

		if (string.length() > 0)
		{
			doFormatting = false;
		}

		preserveReturns = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS);
		preserveWhitespacesInCDATA = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA);
		doNotWrapSimple = preferenceStore.getBoolean(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS);
	}

	/**
	 * inits set of tag names from comma sepatated string
	 * @param sm
	 * @param set
	 */
	private void initTags(String sm, HashSet set)
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
				set.add(trim);
			}
		}
	}

	/**
	 * @param map
	 * @param project
	 */
	public XMLCodeFormatterOptions(Map map, IProject project)
	{

		if (project != null)
		{
			//we have project so initing from it's preferences  
			IEclipsePreferences preferences = new ProjectScope(project).getNode(XMLPlugin.getDefault().getBundle()
					.getSymbolicName());
			String string = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, null);
			if (string == null)
			{
				initFromPreferences();
				return;
			}

			formatterTabChar = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, ""); //$NON-NLS-1$
			String sm = preferences.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2,
					DefaultCodeFormatterConstants.INITIAL_TAGS_DO_NOT_WRAP);
			initTags(sm, notWrappingTags);

			sm = preferences.get(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2,
					DefaultCodeFormatterConstants.INITIAL_TAGS_TO_WRAP);
			initTags(sm, allwaysWrap);

			tabSize = Integer.parseInt(string);
			string = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "1"); //$NON-NLS-1$
			if (string != null)
			{
				spacesInMultiline = Integer.parseInt(string);
			}
			string = preferences.get(DefaultCodeFormatterConstants.NO_FORMATTING, ""); //$NON-NLS-1$
			if (string.length() > 0)
			{
				doFormatting = false;
			}

			preserveReturns = preferences.getBoolean(
					DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, true);
			preserveWhitespacesInCDATA = preferences.getBoolean(
					DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA, true);
			doNotWrapSimple = preferences.getBoolean(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, true);

		}
		else if (map == null)
		{
			//init from default plugin preference store  
			initFromPreferences();
		}
		else
		{
			//init from map with options
			formatterTabChar = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
			String sm = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2);
			initTags(sm, notWrappingTags);
			sm = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2);
			initTags(sm, allwaysWrap);
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
			Object object11 = map.get(DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE);
			if (object11 != null)
			{
				spacesInMultiline = Integer.parseInt(object11.toString());
			}
			else
			{
				spacesInMultiline = 1;
			}
			String sma = (String) map.get(DefaultCodeFormatterConstants.NO_FORMATTING);
			if (sma != null && sma.length() > 0)
			{
				doFormatting = false;
			}
			Object object19a = map.get(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS);
			if (object19a != null)
			{
				preserveReturns = Boolean.parseBoolean(object19a.toString());
			}
			Object object20a = map.get(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA);
			if (object20a != null)
			{
				preserveWhitespacesInCDATA = Boolean.parseBoolean(object20a.toString());
			}
			Object object21a = map.get(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS);
			if (object21a != null)
			{
				doNotWrapSimple = Boolean.parseBoolean(object21a.toString());
			}
		}
	}

}
