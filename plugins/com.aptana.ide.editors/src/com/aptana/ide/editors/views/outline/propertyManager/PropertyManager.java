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
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.ide.core.StringUtils;

/**
 * This class is designed to make integration with the propery view easier. ReadableProperties are added to the property
 * manager, which handles the interaction between the property view and the actual properties.
 * 
 * @author Dan Phifer June 8, 2005
 */
public class PropertyManager implements PropertyChangeListener, IPropertySource, IPropertySourceProvider, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Contains the references to the properties that have been added to this
	 */
	protected ArrayList properties;

	/**
	 * Contains a list of property descriptors that have been previously created and can be reused. Dynamic descriptors
	 * are not cached.
	 * 
	 * @see ReadableProperty#hasDynamicPropertyDescriptor()
	 */
	private transient Hashtable knownDescriptors;

	/**
	 * A list of property change listeners
	 */
	private transient ArrayList propertyChangeListeners;

	/**
	 * The object to which the properties belong. All events are fired on belhalf of the owner.
	 * 
	 * @see PropertyManager#notifyPropertyChangeListeners(PropertyChangeEvent)
	 */
	protected Object owner;

	/**
	 * Creates a new instance of the PropertyManager class.
	 * 
	 * @param owner
	 *            The object to which the properties belong.
	 */
	public PropertyManager(Object owner)
	{
		this.owner = owner;
		properties = new ArrayList();
		knownDescriptors = new Hashtable();
		linkPropertyOwner(owner);
	}

	/**
	 * If the owner of this property is another property, link them together
	 * 
	 * @param owner
	 */
	private void linkPropertyOwner(Object owner)
	{
		if (owner instanceof ReadableProperty)
		{
			ReadableProperty property = (ReadableProperty) owner;
			property.setSubPropertyManager(this);
		}
	}

	/**
	 * Adds a ReadableProperty to the PropertyManager. If a property with the same ID already exists, this method has no
	 * effect.
	 * 
	 * @param readableProperty
	 *            An ReadableProperty
	 */
	public void addProperty(ReadableProperty readableProperty)
	{
		if (!hasProperty(readableProperty.getID()))
		{
			readableProperty.addPropertyChangeListener(this);
			properties.add(readableProperty);
		}
	}

	/**
	 * Removes an ReadableProperties from the PropertyManager
	 * 
	 * @param readableProperty
	 *            An ReadableProperty
	 */
	public void removeProperty(ReadableProperty readableProperty)
	{
		readableProperty.removePropertyChangeListener(this);
		properties.remove(readableProperty);
	}

	/**
	 * Adds a property change listener to be notified when any of the managed ReadableProperties are modifed.
	 * 
	 * @param listener
	 *            A PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeListeners().add(listener);
	}

	/**
	 * Removes the given property change listener.
	 * 
	 * @param listener
	 *            a property listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeListeners().remove(listener);
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		notifyPropertyChangeListeners(event);
	}

	/**
	 * Notify listeners that a property has changed and pass the event
	 * 
	 * @param event
	 *            The event describing the property change
	 */
	private void notifyPropertyChangeListeners(PropertyChangeEvent event)
	{
		// fire an event on behalf of the property manager owner
		PropertyChangeEvent newEvent = new PropertyChangeEvent(owner, event.getPropertyName(), event.getOldValue(),
				event.getNewValue());

		for (Iterator iter = getPropertyChangeListeners().iterator(); iter.hasNext();)
		{
			PropertyChangeListener listener = (PropertyChangeListener) iter.next();
			listener.propertyChange(newEvent);
		}
	}

	/**
	 * @param id
	 * @return The EditableProperty that corresponds to the given ID
	 */
	public EditableProperty getEditableProperty(Object id)
	{
		ReadableProperty prop = getProperty(id);
		if (prop != null)
		{
			if (prop instanceof EditableProperty)
			{
				return (EditableProperty) prop;
			}
		}
		return null;
	}

	/**
	 * @param id
	 *            The ID of the property to be returned
	 * @return The EditableProperty that corresponds the the given ID
	 * @throws IllegalArgumentException
	 *             if a property with the given ID has not been added to the PropertyManager
	 */
	public ReadableProperty getProperty(Object id)
	{
		ReadableProperty prop = findProperty(id);

		// if the property was found, return it, otherwise throw an error
		if (prop != null)
		{
			return prop;
		}

		String error = StringUtils.format(Messages.PropertyManager_CouldNotFindProperty,id);

		throw new IllegalArgumentException(error);
	}

	/**
	 * @param id
	 *            The ID of a property
	 * @return true if the property exists in the PropertyManager class
	 */
	public boolean hasProperty(Object id)
	{
		return findProperty(id) != null;
	}

	/**
	 * This method simply calls getProperty and is provided for consistency
	 * 
	 * @param id
	 *            The ID of a property
	 * @return The corresponding property
	 */
	public ReadableProperty getReadableProperty(Object id)
	{
		return getProperty(id);
	}

	private ReadableProperty findProperty(Object id)
	{
		for (Iterator iter = properties.iterator(); iter.hasNext();)
		{
			ReadableProperty prop = (ReadableProperty) iter.next();
			if (prop.getID().equals(id))
			{
				return prop;
			}
		}
		return null;
	}

	/**
	 * Creates an array of property descriptors which will be used by the properties view. One property descriptor is
	 * created for each EditableProperty managed by the PropertyManager
	 * 
	 * @return an array of property descriptors for use with the property view.
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		IPropertyDescriptor[] descriptors = new PropertyDescriptor[properties.size()];
		int p = 0;
		for (Iterator iter = properties.iterator(); iter.hasNext();)
		{
			ReadableProperty readableProperty = (ReadableProperty) iter.next();

			if (readableProperty != null)
			{
				// if the descriptor is dynamic, do not cache the output
				if (readableProperty.hasDynamicPropertyDescriptor())
				{
					descriptors[p] = readableProperty.getPropertyDescriptor();
				}

				// if it is not dynamic, cache the output and reuse the descriptor
				// if it is already available
				else
				{
					String id = readableProperty.getID();

					if (!getKnownDescriptors().containsKey(id))
					{
						getKnownDescriptors().put(id, readableProperty.getPropertyDescriptor());
					}

					descriptors[p] = (IPropertyDescriptor) getKnownDescriptors().get(id);
				}

				p++;
			}
		}
		return descriptors;
	}

	/**
	 * @param id
	 *            The id of the EditableProperty
	 * @return the value for the given property. This value should be valid for the properties view. Subclasses of
	 *         EditableProperty should provide other methods for returning the value in a different form. For instance,
	 *         <code>EditableComboProperty</code> will return an Integer which represents the index of the selected
	 *         item in the combo box. However, the correspoding value (which is ultimately what is wanted by the class
	 *         that is using the <code>EditableComboProperty</code>) is returned by getComboValue().
	 */
	public Object getPropertyValue(Object id)
	{
		ReadableProperty readableProperty = getProperty(id);
		if (readableProperty != null)
		{
			if (readableProperty.hasSubProperties())
			{
				return readableProperty.getSubPropertyManager();
			}
			else
			{
				return readableProperty.getUnderlyingValue();
			}
		}
		return null;
	}

	/**
	 * @param id
	 *            The id of the property
	 * @return A Number if the given property implements INumericProperty
	 * @throws IllegalArgumentException
	 *             if the property does not exist or does not implement INumericProperty
	 */
	public Number getPropertyNumberValue(Object id)
	{
		ReadableProperty readableProperty = getProperty(id);
		if (readableProperty instanceof INumericProperty)
		{
			return ((INumericProperty) readableProperty).getNumberValue();
		}
		else
		{
			throw new IllegalArgumentException(StringUtils.format(Messages.PropertyManager_NotNumericProperty, id));
		}
	}

	/**
	 * @param id
	 *            The id of the property
	 * @return An int if the given property implements INumericProperty
	 * @throws IllegalArgumentException
	 *             if the property does not exist or does not implement INumericProperty
	 */
	public int getPropertyIntValue(Object id) throws IllegalArgumentException
	{
		return getPropertyNumberValue(id).intValue();
	}

	/**
	 * @param id
	 *            The id of the property
	 * @return A float if the given property implements INumericProperty
	 * @throws IllegalArgumentException
	 *             if the property does not exist or does not implement INumericProperty
	 */
	public float getPropertyFloatValue(Object id) throws IllegalArgumentException
	{
		return getPropertyNumberValue(id).floatValue();
	}

	/**
	 * @param id
	 *            The id of the property
	 * @return A double if the given property implements INumericProperty
	 * @throws IllegalArgumentException
	 *             if the property does not exist or does not implement INumericProperty
	 */
	public double getPropertyDoubleValue(Object id) throws IllegalArgumentException
	{
		return getPropertyNumberValue(id).doubleValue();
	}

	/**
	 * @param id
	 *            The id of the property
	 * @return A long if the given property implements INumericProperty
	 * @throws IllegalArgumentException
	 *             if the property does not exist or does not implement INumericProperty
	 */
	public long getPropertyLongValue(Object id) throws IllegalArgumentException
	{
		return getPropertyNumberValue(id).longValue();
	}

	/**
	 * @param id
	 *            The id of the EditableProperty
	 * @return true if the property is anything other than the defaultValue
	 */
	public boolean isPropertySet(Object id)
	{
		ReadableProperty readableProperty = getProperty(id);
		if (readableProperty != null)
		{
			if (readableProperty.hasDefaultValue() && readableProperty.hasValue())
			{
				return !readableProperty.getUnderlyingDefaultValue().equals(readableProperty.getUnderlyingValue());
			}
		}
		return false;
	}

	/**
	 * Resets the property value to the defaultValue. If not default value was specified, this method has not effect.
	 * 
	 * @param id
	 *            The id of the EditableProperty
	 */
	public void resetPropertyValue(Object id)
	{
		EditableProperty editableProperty = getEditableProperty(id);
		if (editableProperty != null)
		{
			if (editableProperty.hasDefaultValue())
			{
				editableProperty.setUnderlyingValue(editableProperty.getUnderlyingDefaultValue());
			}
		}
	}

	/**
	 * Sets the value of the given property
	 * 
	 * @param id
	 *            The id of the ReadableProperty
	 * @param value
	 *            The new value
	 */
	public void setPropertyValue(Object id, Object value)
	{
		ReadableProperty readableProperty = getProperty(id);
		if (readableProperty != null)
		{
			readableProperty.setUnderlyingValue(value);
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue()
	{
		if (owner instanceof ReadableProperty)
		{
			ReadableProperty property = (ReadableProperty) owner;
			return property.getUnderlyingValue();
		}
		return owner;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
	 */
	public IPropertySource getPropertySource(Object object)
	{
		return this;
	}

	/**
	 * @return A List with a reference to all properties managed by the property manager
	 */
	public ArrayList getProperties()
	{
		return new ArrayList(properties);
	}

	/**
	 * @return Returns the propertyChangeListeners. If the object has not been initialized, it is initialized and
	 *         returned
	 */
	protected ArrayList getPropertyChangeListeners()
	{
		if (propertyChangeListeners == null)
		{
			propertyChangeListeners = new ArrayList();
		}

		return propertyChangeListeners;
	}

	/**
	 * @return Returns the knownDescriptors. If the object has not been initialized, it is initialized and returned
	 */
	protected Hashtable getKnownDescriptors()
	{
		if (knownDescriptors == null)
		{
			knownDescriptors = new Hashtable();
		}

		return knownDescriptors;
	}
}
