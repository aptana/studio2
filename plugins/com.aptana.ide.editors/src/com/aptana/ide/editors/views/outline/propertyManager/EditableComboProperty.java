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

import java.util.ArrayList;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Dan Phifer
 * @version 1.0
 * 
 * The EditableComboProperty works with the PropertyManager and the
 * Properties View to present a drop down box to the user in the 
 * properties view.  
 * 
 * <pre>
 *  Example:
 *  		// create a new property
 *         EditableComboProperty myColors = new EditableComboProperty("ColorID", "Select a Color");
 *         
 *         // add name/value pairs to the combo property
 *         myColors.addComboItem("Red", Color.RED);
 *         myColors.addComboItem("Blue", Color.BLUE);
 *         myColors.addComboItem("Green", Color.GREEN);
 *         myColors.setDefaultValue(Color.BLUE);
 *         
 *         // add it to your property manager object
 *         propertyManager.addProperty(myColors);
 *         
 *         ...
 *         
 *         // when you want to know what the currently selected color is
 *         // retrieve it with the following code
 *         Color selected = (Color) myColors.getValue();
 *         
 *         // Or, if you do not have a reference to the myColors proprety, but
 *         // you have a reference to the propertyManager, use the ID
 *         EditableProperty myColors = propertyManager.getEditableProperty("ColorID");
 *         Color selected = myColors.getValue();
 * </pre>
 *  
 */
public class EditableComboProperty extends EditableProperty 
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID	= 1L;
	private ArrayList itemTexts 	= new ArrayList();
	private ArrayList itemValues 	= new ArrayList();
	
	/**
	 * EditableComboProperty
	 * 
	 * @param id
	 * @param name
	 */
	public EditableComboProperty(String id, String name) 
	{
		super(id, name);
	}
	
	/**
	 * @param name The name to be shown in the drop down list
	 * @param value The value to be returned when the user selects the given name 
	 */
	public void addComboItem(String name, Object value)
	{
		itemTexts.add(name);
		itemValues.add(value);
	}
	
	/**
	 * Removes all items from the combo list and sets the value and defaultValue to null
	 */
	public void removeAllComboItems()
	{
		itemTexts.clear();
		itemValues.clear();
		setDefaultValue(null);
		setValue(null);
	}
	
	/**
	 * Sets the default value of the combo box.  addComboItem must
	 * be called before setting the default value, sine the list 
	 * of possible values for the value parameter is constructed through calls
	 * to addComboItem
	 * 
	 * @param value a value supplied to the addComboItem function
	 */
	public void setDefaultValue(Object value)
	{
		int idx = itemValues.indexOf(value);
		super.setUnderlyingDefaultValue(new Integer(idx));
	}
	
	/**
	 * Returns the default value specified by the caller, if any
	 * 
	 * @see com.aptana.ide.editors.views.outline.propertyManager.ReadableProperty#getDefaultValue()
	 */
	public Object getDefaultValue()
	{
		Object underlyingValue = super.getUnderlyingDefaultValue();
		if (underlyingValue instanceof Integer) {
			int idx = ((Integer) underlyingValue).intValue();
			return itemValues.get(idx);
		}
		return null;
	}
	
	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.ReadableProperty#getCustomPropertyDescriptor()
	 */
	public PropertyDescriptor getCustomPropertyDescriptor() 
	{
		return new ComboBoxPropertyDescriptor(getID(), getName(), getComboItemNames());
	}
	
	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.ReadableProperty#hasDynamicPropertyDescriptor()
	 */
	public boolean hasDynamicPropertyDescriptor()
	{
		return true;
	}
	
	/**
	 * @return The list of names to be disaplyed in the combo box
	 */
	public String[] getComboItemNames()
	{
		String[] list = new String[itemTexts.size()];
		for (int l=0; l < list.length; l++)
		{
			list[l] = (String) itemTexts.get(l);
		}
		return list;
	}
	
	/**
	 * Sets the value of the drop down to the given value by changing
	 * the index of the drop down
	 * 
	 * @param value
	 */
	public void setValue(Object value)
	{
		int idx = itemValues.indexOf(value);
		if (idx >= 0)
		{
			setUnderlyingValue(new Integer(idx));
		}
	}
	
	/**
	 * Sets the value of the drop down box to the value associated with the given name
	 * <pre>
	 * comboProperty.addComboItem("myName", myValue);
	 * ...
	 * comboProperty.setValueByName("myName");
	 * comboProperty.getValue() will yield myValue
	 * </pre>
	 * @param name The name of the combo item to be selected
	 */
	public void setValueByName(String name)
	{
		int idx = itemTexts.indexOf(name);
		if (idx >= 0)
		{
			setUnderlyingValue(new Integer(idx));		
		}
	}
	
	/**
	 * Sets the value of the property to the value corresponding to the ith comboItem added
	 * <pre>
	 * comboProperty.addComboItem("Item 1", object1);
	 * comboProperty.addComboItem("Item 2", object2);
	 * ...
	 * comboProperty.setIndex(1);
	 * comboProperty.getValue() will yield object2
	 * </pre>
	 *  
	 * @param i The position (0-based) of the combo item to be selected.
	 */
	public void setIndex(int i)
	{
		if (i >= 0 && i < itemTexts.size())
		{
			setUnderlyingValue(new Integer(i));		
		}
	}
	
	/**
	 * @return the value of the selected option
	 */
	public Object getValue()
	{
		Object comboValue = translateToValue(getUnderlyingValue());
		if (comboValue != null)
		{
			return comboValue;
		}
		
		comboValue = translateToValue(getUnderlyingDefaultValue());
		return comboValue;
	}

	
	/**
	 * @return The name of the selected option
	 */
	public String getText() 
	{
		String comboName = translateToName(getUnderlyingValue());
		if (comboName != null)
		{
			return comboName;
		}
		
		comboName = translateToName(getUnderlyingDefaultValue());
		return comboName;
	}
	
	/**
	 * @param value The value used by the properties view (Integer index)
	 * @return the comboValue represented by the value
	 */
	private Object translateToValue(Object value)
	{
		int idx = getIndexOf(value);
		if (idx >= 0 && idx < itemValues.size())
		{
			return itemValues.get(idx);
		}
		return null;
	}

	/**
	 * @param value The value used by the properties view (Integer index)
	 * @return the comboName represented by the value
	 */
	private String translateToName(Object value)
	{
		int idx = getIndexOf(value);
		if (idx >= 0 && idx < itemTexts.size())
		{
			return (String) itemTexts.get(idx);
		}
		return null;
	}
	
	/**
	 * @return the index of the selected value
	 */
	private int getIndexOf(Object v)
	{
		if (v instanceof Integer) {
			return ((Integer) v).intValue();
		}
		return -1;
	}
}
