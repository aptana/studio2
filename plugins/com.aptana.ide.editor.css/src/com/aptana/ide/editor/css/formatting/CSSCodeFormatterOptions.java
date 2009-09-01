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
package com.aptana.ide.editor.css.formatting;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class CSSCodeFormatterOptions
{

	/**
	 * END_LINE
	 */
	public static final int END_LINE = 0;
	/**
	 * NEXT_LINE
	 */
	public static final int NEXT_LINE = 1;
	/**
	 * NEXT_LINE_SHIFTED
	 */
	public static final int NEXT_LINE_SHIFTED = 2;

	public static final String NEWLINES_BETWEEN_SELECTORS = CSSPlugin.ID + ".formatter.newlines_between_selectors"; //$NON-NLS-1$

	/**
	 * formatterBracePositionForBlock
	 */
	public int formatterBracePositionForBlock;

	/**
	 * formatterTabChar
	 */
	public String formatterTabChar;
	/**
	 * tabSize
	 */
	public int tabSize;

	/**
	 * do formatting
	 */
	public boolean doFormatting = true;

	public boolean newlinesBetweenSelectors = false;

	/**
	 * 
	 */
	public CSSCodeFormatterOptions()
	{
		initFromPreferences();
	}

	private void initFromPreferences()
	{
		IPreferenceStore preferenceStore = CSSPlugin.getDefault().getPreferenceStore();

		formatterTabChar = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		tabSize = preferenceStore.getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		formatterBracePositionForBlock = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
		newlinesBetweenSelectors = preferenceStore.getBoolean(NEWLINES_BETWEEN_SELECTORS);
		String string = preferenceStore.getString(DefaultCodeFormatterConstants.NO_FORMATTING);
		if (string.length() > 0)
		{
			doFormatting = false;
		}
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(IPreferenceStore store, String id)
	{
		String string = store.getString(id);
		return parseOption(string);
	}

	private int parseOption(String string)
	{
		if (string == null)
		{
			return NEXT_LINE_SHIFTED;
		}
		if (string.equals(DefaultCodeFormatterConstants.END_OF_LINE))
		{
			return END_LINE;
		}
		if (string.equals(DefaultCodeFormatterConstants.NEXT_LINE))
		{
			return NEXT_LINE;
		}
		return NEXT_LINE_SHIFTED;
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(Map store, String id)
	{
		return parseOption((String) store.get(id));
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(IEclipsePreferences store, String id)
	{
		return parseOption((String) store.get(id, null));
	}

	/**
	 * @param map
	 * @param project
	 */
	public CSSCodeFormatterOptions(Map map, IProject project)
	{
		if (project != null)
		{
			IEclipsePreferences preferences = new ProjectScope(project).getNode(CSSPlugin.ID);
			String string = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, null);
			if (string == null)
			{
				initFromPreferences();
				return;
			}

			formatterTabChar = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, ""); //$NON-NLS-1$
			newlinesBetweenSelectors = preferences.getBoolean(NEWLINES_BETWEEN_SELECTORS, false);
			tabSize = Integer.parseInt(string);

			formatterBracePositionForBlock = parseOption(preferences,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
			string = preferences.get(DefaultCodeFormatterConstants.NO_FORMATTING, ""); //$NON-NLS-1$
			if (string.length() > 0)
			{
				doFormatting = false;
			}
		}
		else if (map == null)
		{
			initFromPreferences();
		}
		else
		{

			formatterTabChar = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
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
			Object val = map.get(NEWLINES_BETWEEN_SELECTORS);
			if (val instanceof Boolean)
			{
				newlinesBetweenSelectors = (Boolean) val;
			}
			else if (val instanceof String)
			{
				newlinesBetweenSelectors = Boolean.parseBoolean((String) val);
			}
			else
			{
				newlinesBetweenSelectors = false;
			}
			formatterBracePositionForBlock = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
			String sma = (String) map.get(DefaultCodeFormatterConstants.NO_FORMATTING);
			if (sma != null && sma.length() > 0)
			{
				doFormatting = false;
			}
		}

	}

}
