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
package com.aptana.ide.views.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.environment.JSGuessedObject;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSArray;
import com.aptana.ide.editor.js.runtime.JSBoolean;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNull;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSRegExp;
import com.aptana.ide.editor.js.runtime.JSString;
import com.aptana.ide.editor.js.runtime.Reference;

/**
 * @author Kevin Lindsey
 */
public class ModelLabelProvider extends LabelProvider
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
	
	private static final Map<Class<?>, Image> IMAGE_MAP;
	
	/**
	 * Static constructor
	 */
	static
	{
		IMAGE_MAP = new HashMap<Class<?>, Image>();
		
		IMAGE_MAP.put(JSArray.class, ARRAY_ICON);
		IMAGE_MAP.put(JSBoolean.class, BOOLEAN_ICON);
		IMAGE_MAP.put(JSFunction.class, FUNCTION_ICON);
		IMAGE_MAP.put(JSNull.class, NULL_ICON);
		IMAGE_MAP.put(JSNumber.class, NUMBER_ICON);
		IMAGE_MAP.put(JSObject.class, OBJECT_LITERAL_ICON);
		IMAGE_MAP.put(JSRegExp.class, REGEX_ICON);
		IMAGE_MAP.put(JSString.class, STRING_ICON);
		
		IMAGE_MAP.put(JSGuessedObject.class, OBJECT_LITERAL_ICON);
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result;
		
		if (element instanceof Reference)
		{
			IObject value = ((Reference) element).getValue(Integer.MAX_VALUE, Integer.MAX_VALUE);
			Class<?> valueClass = value.getClass();
			
			if (IMAGE_MAP.containsKey(valueClass))
			{
				result = IMAGE_MAP.get(valueClass);
			}
			else
			{
				if (value instanceof IFunction)
				{
					result = FUNCTION_ICON;
				}
				else if (value instanceof IScope)
				{
					result = OBJECT_LITERAL_ICON;
				}
				else
				{
					result = PROPERTY_ICON;
				}
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
		
		if (element instanceof Reference)
		{
			result = ((Reference) element).getPropertyName();
		}
		else
		{
			result = super.getText(element);
		}
		
		return result;
	}
}
