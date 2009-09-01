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

import org.eclipse.jface.viewers.ICellEditorValidator;

import com.aptana.ide.core.StringUtils;

/**
 * @author Dan
 */
public class EditableIntegerProperty extends EditableProperty implements INumericProperty
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private int maximum = Integer.MAX_VALUE;
	private int minimum = Integer.MIN_VALUE;
	private int increment = 1;

	/**
	 * EditableIntegerProperty
	 * 
	 * @param id
	 * @param name
	 * @param defaultValue
	 */
	public EditableIntegerProperty(String id, String name, int defaultValue)
	{
		super(id, name);
		setDefaultValue(defaultValue);
	}

	/**
	 * setMinimum
	 *
	 * @param minimum
	 */
	public void setMinimum(int minimum)
	{
		this.minimum = minimum;
	}

	/**
	 * setMaximum
	 *
	 * @param maximum
	 */
	public void setMaximum(int maximum)
	{
		this.maximum = maximum;
	}

	/**
	 * setMinimumMaximum
	 *
	 * @param minimum
	 * @param maximum
	 */
	public void setMinimumMaximum(int minimum, int maximum)
	{
		setMinimum(minimum);
		setMaximum(maximum);
	}

	/**
	 * setDefaultValue
	 *
	 * @param i
	 */
	public void setDefaultValue(int i)
	{
		setUnderlyingDefaultValue(String.valueOf(i));
	}

	/**
	 * setValue
	 *
	 * @param i
	 */
	public void setValue(int i)
	{
		setUnderlyingValue(String.valueOf(i));
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.EditableProperty#getValidator()
	 */
	public ICellEditorValidator getValidator()
	{
		if (validator == null)
		{
			validator = new SerializableValidator()
			{
				private static final long serialVersionUID = -5817169248707618007L;

				public String isValid(Object value)
				{
					int intValue = -1;
					try
					{
						intValue = Integer.parseInt((String) value);
					}
					catch (NumberFormatException exc)
					{
						return Messages.EditableNumberProperty_NotANumber;
					}

					if (intValue > maximum)
					{
						return StringUtils.format(Messages.EditableNumberProperty_ValueMustBeLessThan, maximum);
					}
					else if (intValue < minimum)
					{
						return StringUtils.format(Messages.EditableNumberProperty_ValueMustBeGreaterThan, minimum);
					}

					return null;
				}
			};
		}

		return validator;
	}

	/**
	 * Ensures that the returned value is an integer object. If the actual value
	 * 
	 * @return an Integer object.
	 */
	public Integer getIntegerValue()
	{
		return getInteger(getUnderlyingValue());
	}

	/**
	 * Returns the EditableProperty value in the form of an int
	 * 
	 * @return an int
	 */
	public int getIntValue()
	{
		return getIntegerValue().intValue();
	}

	/**
	 * @param value
	 *            The Object value of the property
	 * @return The Integer value of the property. If the object is already an integer, it is simply returned. If it is a
	 *         String, getInteger will attempt to parse it and return and integer. Otherwise, if it is not an intger or
	 *         a String, the default value is returned.
	 */
	private Integer getInteger(Object value)
	{
		if (value instanceof Integer)
		{
			return (Integer) value;
		}

		else if (value instanceof String)
		{
			String str = (String) value;
			try
			{
				return new Integer(Integer.parseInt(str));
			}
			catch (NumberFormatException exc)
			{
				// "Not a number";
			}
		}

		return (Integer) getDefaultValue();
	}

	/**
	 * getIncrement
	 *
	 * @return int
	 */
	public int getIncrement()
	{
		return increment;
	}

	/**
	 * setIncrement
	 *
	 * @param increment
	 */
	public void setIncrement(int increment)
	{
		this.increment = increment;
	}

	/**
	 * getMaximum
	 *
	 * @return int
	 */
	public int getMaximum()
	{
		return maximum;
	}

	/**
	 * getMinimum
	 *
	 * @return int
	 */
	public int getMinimum()
	{
		return minimum;
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.INumericProperty#getNumberValue()
	 */
	public Number getNumberValue()
	{
		return getIntegerValue();
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.INumericProperty#setNumberValue(java.lang.Number)
	 */
	public void setNumberValue(Number value)
	{
		setValue(value.intValue());
	}
}
