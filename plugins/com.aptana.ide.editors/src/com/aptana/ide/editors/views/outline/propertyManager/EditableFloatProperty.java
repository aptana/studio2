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
 * @author Dan Phifer The EditableFloatProperty class works with the PropertyManager class to allow Float values to be
 *         entered and validated through the properties view.
 */
public class EditableFloatProperty extends EditableProperty implements INumericProperty
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private float maximum = Float.MAX_VALUE;
	private float minimum = Float.MIN_VALUE;

	/**
	 * @param id
	 *            The unique identifier for this property
	 * @param name
	 *            The name to be displayed to the user
	 * @param defaultValue
	 *            The value to be used by default
	 */
	public EditableFloatProperty(String id, String name, float defaultValue)
	{
		super(id, name);
		setDefaultValue(defaultValue);
	}

	/**
	 * @param minimum
	 *            The minimum allowable value. Any values lower than minimum will be rejected by the validator and the
	 *            user will be informed via the status bar
	 */
	public void setMinimum(float minimum)
	{
		this.minimum = minimum;
	}

	/**
	 * @param maximum
	 *            The maximum allowable value. Any values higher than maximum will be rejected by the validator and the
	 *            user will be informed via the status bar
	 */
	public void setMaximum(float maximum)
	{
		this.maximum = maximum;
	}

	/**
	 * Sets the default value of the property. The default value will be used initially, and the user can revert to the
	 * default value.
	 * 
	 * @param defaultValue
	 *            The defaultValue
	 */
	public void setDefaultValue(float defaultValue)
	{
		setUnderlyingDefaultValue(String.valueOf(defaultValue));
	}

	/**
	 * @param newValue
	 *            The new value of the property.
	 */
	public void setValue(float newValue)
	{
		setUnderlyingValue(String.valueOf(newValue));
	}

	/**
	 * Do simple validation to make sure the float value is between the minimum and maximum value. By default the
	 * minimum and maximum are the minimum and maximum supported by the float data type, however clients may specify a
	 * more restrictive range (inclusive).
	 * 
	 * @see com.aptana.ide.editors.views.outline.propertyManager.EditableProperty#getValidator()
	 */
	public ICellEditorValidator getValidator()
	{
		if (validator == null)
		{
			validator = new SerializableValidator()
			{
				private static final long serialVersionUID = 909362002324676355L;

				public String isValid(Object value)
				{
					float floatValue = -1f;
					try
					{
						floatValue = Float.parseFloat((String) value);
					}
					catch (NumberFormatException exc)
					{
						return Messages.EditableNumberProperty_NotANumber;
					}

					if (floatValue > maximum)
					{
						return StringUtils.format(Messages.EditableNumberProperty_ValueMustBeLessThan, Float.toString(maximum));
					}
					else if (floatValue < minimum)
					{
						return StringUtils.format(Messages.EditableNumberProperty_ValueMustBeGreaterThan, Float.toString(minimum));
					}

					return null;
				}
			};
		}

		return validator;
	}

	/**
	 * Ensures that the returned value is a Float object. If the actual value
	 * 
	 * @return an Float object.
	 */
	public Float getFloatValue()
	{
		return getFloat(getValue());
	}

	/**
	 * @param value
	 *            An object (String or Float) to be interpreted as a Float.
	 * @return A Float object that is equivalent to the given value.
	 */
	private Float getFloat(Object value)
	{
		if (value instanceof Float)
		{
			return (Float) value;
		}

		else if (value instanceof String)
		{
			String str = (String) value;
			try
			{
				return new Float(Float.parseFloat(str));
			}
			catch (NumberFormatException exc)
			{
				// "Not a number";
			}
		}

		// if the object could not be interpreted, return 0
		return (Float) getDefaultValue();
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.INumericProperty#getNumberValue()
	 */
	public Number getNumberValue()
	{
		return getFloatValue();
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.INumericProperty#setNumberValue(java.lang.Number)
	 */
	public void setNumberValue(Number value)
	{
		setValue(value.floatValue());
	}
}
