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
package com.aptana.ide.editor.js;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedColorizerBase;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Robin Debreuil
 */
public class JSColorizer extends UnifiedColorizerBase
{
	private String[] fTypes = { "Array", "Boolean", "Date", "Error", "EvalError", "RangeError", "ReferenceError", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			"SyntaxError", "TypeError", "URIError", "Function", "Math", "Null", "Number", "Object", "RegExp", "String", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
			"Undefined" }; //$NON-NLS-1$

	/**
	 * htmlIdents
	 */
	public static Map<String, TextAttribute> htmlIdents = new HashMap<String, TextAttribute>();

	/**
	 * jsCoreIdents
	 */
	public static Map<String, TextAttribute> jsCoreIdents = new HashMap<String, TextAttribute>();

	/**
	 * idents
	 */
	private Map<String, TextAttribute> idents = new Hashtable<String, TextAttribute>();

	// private IDocument doc;
	// private TextAttribute futureTextAttribute;
	private TextAttribute nativeTypeTextAttribute;
	// private TextAttribute documentionTextAttribute;
	private TextAttribute htmlDomTextAttribute;
	private TextAttribute jsCoreTextAttribute;
	private LanguageColorizer colorizer;

	/**
	 * JSDamagerRepairer
	 */
	public JSColorizer()
	{
		super(JSMimeType.MimeType);

		for (int i = 0; i < fTypes.length; i++)
		{
			idents.put(fTypes[i], nativeTypeTextAttribute);
		}
		colorizer = LanguageRegistry.getLanguageColorizer(JSMimeType.MimeType);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ILexemeColorMapper#createStyle(com.aptana.ide.parsing.IParseState,
	 *      com.aptana.ide.lexer.Lexeme, java.util.Vector)
	 */
	public void createStyle(IParseState parseState, Lexeme lexeme, Vector<StyleRange> styles)
	{
		int cat = lexeme.getCategoryIndex();
		if (cat == TokenCategories.IDENTIFIER)
		{
			String text = lexeme.getText();
			if (idents.containsKey(text))
			{
				addRange(styles, lexeme.offset, lexeme.length, idents.get(text));
			}
			else if (jsCoreIdents.containsKey(text))
			{
				addRange(styles, lexeme.offset, lexeme.length, jsCoreTextAttribute);
			}
			else if (htmlIdents.containsKey(text))
			{
				addRange(styles, lexeme.offset, lexeme.length, htmlDomTextAttribute);
			}
			else
			{
				colorizer.createStyle(parseState, lexeme, styles);
			}
		}
		else
		{
			colorizer.createStyle(parseState, lexeme, styles);
		}
	}

	/**
	 * Sets the default presentation colors for text. Called first on initialization, and reset on preference store
	 * change
	 */
	protected void initializeColorTables()
	{
		nativeTypeTextAttribute = getColorPreference(IPreferenceConstants.JSEDITOR_NATIVETYPE_COLOR, SWT.BOLD);
		jsCoreTextAttribute = getColorPreference(IPreferenceConstants.JSEDITOR_JSCORE_COLOR);
		htmlDomTextAttribute = getColorPreference(IPreferenceConstants.JSEDITOR_HTMLDOM_COLOR);
		// futureTextAttribute = new TextAttribute(colorManager.getColor(IJSColorConstants.RESERVED), null,
		// TextAttribute.STRIKETHROUGH);
		// documentionTextAttribute = new TextAttribute(colorManager.getColor(IJSColorConstants.DOCUMENTATION));
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedColorizerBase#getPluginPreferenceStore()
	 */
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}
}
