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
package com.aptana.ide.editors.views.outline.propertyManager;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Dan
 */
public class EditableProperty extends ReadableProperty
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * validator
	 */
	protected ICellEditorValidator validator;

	/**
	 * @param ID
	 *            The ID of the EditableProperty, usually Class.PropertyName
	 * @param name
	 *            The text to be shown to the user
	 */
	public EditableProperty(String ID, String name)
	{
		super(ID, name);
	}

	/**
	 * Sets the value of the EditableProperty and fires the PropertyChange event WARNING: this setValue is designed to
	 * work with the PropertiesView. Therefore, the EditableComboProperty expects Integer indexes as new values
	 * 
	 * @param newValue
	 *            The new PropertyValue
	 */
	public void setUnderlyingValue(Object newValue)
	{
		if (newValue.equals(value))
		{
			return;
		}

		Object oldValue = value;
		value = newValue;
		PropertyChangeEvent event = new PropertyChangeEvent(this, getID(), oldValue, newValue);
		notifyPropertyChangeListeners(event);
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.ReadableProperty#setValue(java.lang.Object)
	 */
	public void setValue(Object newValue)
	{
		setUnderlyingValue(newValue);
	}

	/**
	 * getValidator
	 * 
	 * @return ICellEditorValidator
	 */
	public ICellEditorValidator getValidator()
	{
		return validator;
	}

	/**
	 * setValidator
	 * 
	 * @param validator
	 */
	public void setValidator(ICellEditorValidator validator)
	{
		this.validator = validator;
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.ReadableProperty#getCustomPropertyDescriptor()
	 */
	public PropertyDescriptor getCustomPropertyDescriptor()
	{
		PropertyDescriptor propertyDescriptor = new TextPropertyDescriptor(getID(), getName());
		propertyDescriptor.setValidator(getValidator());
		return propertyDescriptor;
	}
}
