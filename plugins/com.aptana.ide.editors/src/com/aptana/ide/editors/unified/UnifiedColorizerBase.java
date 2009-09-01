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
package com.aptana.ide.editors.unified;

import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.lexer.Lexeme;

/**
 * @author Paul Colton
 */
public abstract class UnifiedColorizerBase implements ILexemeColorMapper, ILexemeColorMapperEx
{
	/** The editor's property change listener. */
	private IPropertyChangeListener _propertyChangeListener = new PropertyChangeListener();
	private boolean isDisposing = false;

	/**
	 * Style attribute for highlighting occurrences
	 */
	protected TextAttribute _occurrenceHighlight;

	/**
	 * UnifiedColorizerBase
	 * 
	 * @param mimeType
	 */
	public UnifiedColorizerBase(String mimeType)
	{
		getPluginPreferenceStore().addPropertyChangeListener(this._propertyChangeListener);
		UnifiedEditorsPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this._propertyChangeListener);
		initializeColorTables();
		initializeBaseColorTable();
	}

	/**
	 * initializeBaseColorTable
	 */
	private void initializeBaseColorTable()
	{
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR);
		
		if (color != null)
		{
			Color newColor = UnifiedColorManager.getInstance().getColor(color);
			
			this._occurrenceHighlight = new TextAttribute(UnifiedColorManager.getInstance().getColor(new RGB(32, 32, 32)), newColor, 0);
		}
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (this.isDisposing)
		{
			return;
		}
		
		this.isDisposing = true;

		if (this._propertyChangeListener != null)
		{
			getPluginPreferenceStore().removePropertyChangeListener(this._propertyChangeListener);
			this._propertyChangeListener = null;
		}
	}

	/**
	 * initializeColorTables
	 */
	protected abstract void initializeColorTables();

	/**
	 * addRange
	 * 
	 * @param styles
	 * @param offset
	 * @param length
	 * @param attr
	 */
	protected void addRange(Vector<StyleRange> styles, int offset, int length, TextAttribute attr)
	{
		if (attr != null)
		{
			styles.add(new StyleRange(offset, length, attr.getForeground(), attr.getBackground(), attr.getStyle()));
		}
	}

	/**
	 * getColorPreference
	 * 
	 * @param preferenceKey
	 * @return TextAttribute
	 */
	public TextAttribute getColorPreference(String preferenceKey)
	{
		return getColorPreference(preferenceKey, 0);
	}

	/**
	 * getColorPreference
	 * 
	 * @param preferenceKey
	 * @param style
	 * @return TextAttribute
	 */
	public TextAttribute getColorPreference(String preferenceKey, int style)
	{
		IPreferenceStore store = getPluginPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, preferenceKey);
		
		if (color != null)
		{
			Color newColor = UnifiedColorManager.getInstance().getColor(color);
			
			return new TextAttribute(newColor, null, style);
		}

		return null;
	}

	/**
	 * @return style attribute for displaying highlight
	 */
	public TextAttribute getHighlightColorPreference()
	{
		return this._occurrenceHighlight;
	}

	/**
	 * Mark lexeme with highlight - change background color per preference setting for occurrence highlight
	 * 
	 * @param styles
	 * @param lx
	 */
	public void highlightMarkedElement(Vector<StyleRange> styles, Lexeme lx)
	{
		addRange(styles, lx.offset, lx.length, getHighlightColorPreference());
	}

	/**
	 * Internal property change listener for handling changes in the editor's preferences.
	 */
	class PropertyChangeListener implements IPropertyChangeListener
	{
		/**
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event)
		{
			handlePreferenceStoreChanged(event);
		}
	}

	/**
	 * Updates the internal color definitions based on editor preference updates.
	 * 
	 * @param event
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		if (IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR.equals(event.getProperty()))
		{
			initializeBaseColorTable();
		}
		else
		{
			initializeColorTables();
		}
	}

	/**
	 * Extended styling, that can be done in the base class, instead of having repeated code in individual extending
	 * class e.g. background change for occurrences marking.
	 * 
	 * @param styles
	 * @param lx
	 * @see ILexemeColorMapperEx#createStyleEx(Lexeme, Vector)
	 */
	public void createStyleEx(Lexeme lx, Vector<StyleRange> styles)
	{
		if (lx.isHighlighted())
		{
			highlightMarkedElement(styles, lx);
		}
	}

	/**
	 * getPluginPreferenceStore
	 * 
	 * @return IPreferenceStore
	 */
	protected abstract IPreferenceStore getPluginPreferenceStore();
}
