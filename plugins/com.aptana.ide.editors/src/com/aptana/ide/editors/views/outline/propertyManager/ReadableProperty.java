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
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Dan Phifer The ReadableProperty class provides read-only support for Properties. It is used in conjuction
 *         with the PropertyManager class which allows easy integration with the built-in Eclipse Properties View.
 * 
 * <pre>
 *   + ReadableProperty				// read-only support
 *   +--+ EditableProperty			// Text editable
 *   +--+--- EditableBooleanProperty
 *      +--- EditableComboProperty 	// Drop down box
 *      +--- EditableIntegerProperty	(INumericProperty) // Integer Editable
 *      +--- EditableFloatProperty	(INumericProperty) // Float Editable
 * </pre>
 */
public class ReadableProperty implements Serializable
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A unique ID for this property
	 */
	private String ID;

	/**
	 * A human-readable String to be shown to the user
	 */
	private String name;

	/**
	 * The underlying default value. The underlying value is passed between the PropertyManager and the Properties View,
	 * while the value is passed between the PropertiesManager and the client. This avoids rewriting code that
	 * translates the String value needed by the Properties view and the value needed by the client. For instance, if
	 * the client wants to expose a Float property, the client class should not have to worry about translating from
	 * String to Float and vice versa. Therefore, the EditableFloatProperty handles this translation behind the scenes,
	 * and the client can be ignorant of any translation that needs to take place.
	 */
	protected Object defaultValue;

	/**
	 * the current underlying value
	 */
	protected Object value;

	/**
	 * The category of this property. Categories can be used to organize properties in the PropertiesView if sorting
	 * them alphabetically is not managable
	 */
	private String category;

	private LabelProvider labelProvider = null;

	private PropertyManager subProperites = null;

	private ArrayList propertyChangeListeners;

	private PropertyChangeListener subPropertyListener;

	/**
	 * @param ID
	 *            The unique ID to be used for this property.
	 * @param name
	 *            The name to be displayed to the user
	 */
	public ReadableProperty(String ID, String name)
	{
		this.ID = ID;
		this.name = name;
		propertyChangeListeners = new ArrayList();
	}

	/**
	 * @return By default, the default underlying value is returned.
	 */
	public Object getDefaultValue()
	{
		return getUnderlyingDefaultValue();
	}

	/**
	 * @return The underlying default value. This is the value that should be ultimately returned to the properties
	 *         view.
	 */
	public Object getUnderlyingDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Sets the value of the EditableProperty and fires the PropertyChange event
	 * 
	 * @param newValue
	 *            The new PropertyValue
	 */
	public void setUnderlyingValue(Object newValue)
	{
		value = newValue;
	}

	/**
	 * setValue
	 * 
	 * @param newValue
	 */
	public void setValue(Object newValue)
	{
		setUnderlyingValue(newValue);
	}

	/**
	 * Unless overidden by a subclass, this is equivalent to the setUnderlyingDefaultValue. Subclasses may overide this
	 * method if they need to proprocess (or translate) the value before setting the underlying default value, which the
	 * Properties view will ultimately work with.
	 * 
	 * @param defaultValue
	 *            The new default value.
	 */
	public void setDefaultValue(Object defaultValue)
	{
		setUnderlyingDefaultValue(defaultValue);
	}

	/**
	 * Sets the underlying default value. No translation occurs before setting the value
	 * 
	 * @see #setDefaultValue(Object defaultValue)
	 * @param defaultValue
	 *            The new underlying default value
	 */
	public void setUnderlyingDefaultValue(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the unique ID of the object.
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @return The name of the object. The name is displayed to the user
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return By default, the ReadableProperty class returns the underlying value
	 */
	public Object getValue()
	{
		return getUnderlyingValue();
	}

	/**
	 * @return The underlying value. For readable properties, the underlying value is the same as the value. However,
	 *         when dealing with Custom types, it is more convenient for the getValue function to return an object of
	 *         the correct type, rather than a string which must be parsed. Or, in the case of a combo property, the
	 *         value associated with the item selected, instead of the index of the item (which is what the Properties
	 *         view returns).
	 */
	protected Object getUnderlyingValue()
	{
		if (value != null)
		{
			return value;
		}
		else
		{
			return getUnderlyingDefaultValue();
		}
	}

	/**
	 * Sets the category of this property. In the properties view, the user can choose to see the properties by
	 * category, or alphabetically.
	 * 
	 * @param category
	 *            The category ID (and description)
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * Sets the lable provider for this property. This allows for a custom text be shown in the properties view
	 * 
	 * @param provider
	 */
	public void setLabelProvider(LabelProvider provider)
	{
		this.labelProvider = provider;
	}

	/**
	 * @return true if the value is anything other than null
	 */
	public boolean hasValue()
	{
		return value != null;
	}

	/**
	 * @return true if the defaultValue is anything other than null
	 */
	public boolean hasDefaultValue()
	{
		return defaultValue != null;
	}

	/**
	 * @return the property descriptor to be used by this property. This method cannot be overidden. Instead, overide
	 *         the hook method getCustomPropertyDescriptor
	 */
	public final PropertyDescriptor getPropertyDescriptor()
	{
		PropertyDescriptor propertyDescriptor = getCustomPropertyDescriptor();

		if (this.category != null)
		{
			propertyDescriptor.setCategory(this.category);
		}

		if (this.labelProvider != null)
		{
			propertyDescriptor.setLabelProvider(labelProvider);
		}

		return propertyDescriptor;
	}

	/**
	 * This method can be overridden by suclasses. The default implementation returns a read-only PropertyDescriptor.
	 * 
	 * @return the property descriptor to be used for this property
	 */
	public PropertyDescriptor getCustomPropertyDescriptor()
	{
		return getReadOnlyPropertyDescriptor();
	}

	/**
	 * @return A PropertyDescriptor that allows the user to see the value, but not edit it. Subclasses of
	 *         PropertyDescriptor allow editing.
	 */
	private PropertyDescriptor getReadOnlyPropertyDescriptor()
	{
		PropertyDescriptor propertyDescriptor = new PropertyDescriptor(getID(), getName());
		return propertyDescriptor;
	}

	/**
	 * @return true if the property descriptor can change. Mainly, this is for combo property object, so that the items
	 *         in the list can be dynamic. Other types of descriptors do not contain information that is data dependent
	 */
	public boolean hasDynamicPropertyDescriptor()
	{
		return false;
	}

	/**
	 * hasSubProperties
	 * 
	 * @return boolean
	 */
	public boolean hasSubProperties()
	{
		return subProperites != null;
	}

	/**
	 * getSubPropertyManager
	 * 
	 * @return Object
	 */
	public Object getSubPropertyManager()
	{
		return subProperites;
	}

	/**
	 * @param propertyManager
	 */
	public void setSubPropertyManager(PropertyManager propertyManager)
	{
		if (subProperites != null)
		{
			subProperites.removePropertyChangeListener(getSubPropertyListener());
		}

		subProperites = propertyManager;

		if (subProperites != null)
		{
			subProperites.addPropertyChangeListener(getSubPropertyListener());
		}
	}

	/**
	 * @return A property change listener that will simply pass events up to the higher level property manager
	 */
	private PropertyChangeListener getSubPropertyListener()
	{
		if (subPropertyListener == null)
		{
			subPropertyListener = new SubPropertyChangeListener()
			{
				private static final long serialVersionUID = -3330992827410094475L;

				public void propertyChange(PropertyChangeEvent evt)
				{
					notifyPropertyChangeListeners(evt);
				}
			};
		}

		return subPropertyListener;
	}

	/**
	 * addPropertyChangeListener
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeListeners.add(listener);
	}

	/**
	 * removePropertyChangeListener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeListeners.remove(listener);
	}

	/**
	 * Notify the propertChangeListeners that this property has changed
	 * 
	 * @param event
	 */
	protected void notifyPropertyChangeListeners(PropertyChangeEvent event)
	{
		PropertyChangeListener l;
		for (int i = 0; i < propertyChangeListeners.size(); i++)
		{
			l = (PropertyChangeListener) propertyChangeListeners.get(i);
			l.propertyChange(event);
		}
	}

	/**
	 * SubPropertyChangeListener
	 * 
	 * @author Ingo Muschenetz
	 */
	class SubPropertyChangeListener implements PropertyChangeListener, Serializable
	{
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -351877929766054322L;

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt)
		{
			notifyPropertyChangeListeners(evt);
		}
	}

}
