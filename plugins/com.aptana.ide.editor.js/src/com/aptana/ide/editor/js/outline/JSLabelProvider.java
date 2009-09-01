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
package com.aptana.ide.editor.js.outline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.views.outline.UnifiedOutlineProvider;

/**
 * @author Kevin Lindsey
 */
public class JSLabelProvider extends LabelProvider implements IPropertyChangeListener, IColorProvider
{
	private static final Image PROPERTY_ICON = JSPlugin.getImage("icons/js_property.gif"); //$NON-NLS-1$
	private static final Image ARRAY_ICON = JSPlugin.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = JSPlugin.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = JSPlugin.getImage("icons/js_function.gif"); //$NON-NLS-1$
	private static final Image NULL_ICON = JSPlugin.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = JSPlugin.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_LITERAL_ICON = JSPlugin.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image REGEX_ICON = JSPlugin.getImage("icons/regex.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = JSPlugin.getImage("icons/string.png"); //$NON-NLS-1$

	/**
	 * JSLabelProvider
	 */
	public JSLabelProvider()
	{
		// get JS preference store
		IPreferenceStore prefStore = JSPlugin.getDefault().getPreferenceStore();

		// add this object as a listener
		prefStore.addPropertyChangeListener(this);
		
		// set private member prefix
		this.updatePrivateMemberPrefix();
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result;

		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;

			switch (item.getType())
			{
				case JSOutlineItemType.PROPERTY:
					result = PROPERTY_ICON;
					break;
					
				case JSOutlineItemType.ARRAY:
					result = ARRAY_ICON;
					break;
					
				case JSOutlineItemType.BOOLEAN:
					result = BOOLEAN_ICON;
					break;

				case JSOutlineItemType.FUNCTION:
					result = FUNCTION_ICON;
					break;
					
				case JSOutlineItemType.NULL:
					result = NULL_ICON;
					break;
					
				case JSOutlineItemType.NUMBER:
					result = NUMBER_ICON;
					break;

				case JSOutlineItemType.OBJECT_LITERAL:
					result = OBJECT_LITERAL_ICON;
					break;
					
				case JSOutlineItemType.REGEX:
					result = REGEX_ICON;
					break;
					
				case JSOutlineItemType.STRING:
					result = STRING_ICON;
					break;

				default:
					result = super.getImage(element);
					break;
			}
		}
		else
		{
			result = super.getImage(element);
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result;

		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem item = (JSOutlineItem) element;

			result = item.getLabel();
		}
		else
		{
			result = super.getText(element);
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IPreferenceConstants.PREFERENCE_PRIVATE_FIELD_INDICATOR))
		{
			// update prefix
			this.updatePrivateMemberPrefix();
			
			// refresh tree
			UnifiedOutlineProvider.getInstance().refresh();
		}
	}
	
	/**
	 * updatePrivateMemberPrefix
	 *
	 */
	private void updatePrivateMemberPrefix()
	{
		// get JS preference store
		IPreferenceStore prefStore = JSPlugin.getDefault().getPreferenceStore();
		
		// get value
		String prefix = prefStore.getString(IPreferenceConstants.PREFERENCE_PRIVATE_FIELD_INDICATOR);
		
		// store value
		UnifiedOutlineProvider.getInstance().setPrivateMemberPrefix(JSMimeType.MimeType, prefix);
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element)
	{
//		return UnifiedColorManager.getInstance().getColor(new RGB(220, 220, 255));
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element)
	{
//		return UnifiedColorManager.getInstance().getColor(new RGB(0, 128, 0));
		return null;
	}
}
