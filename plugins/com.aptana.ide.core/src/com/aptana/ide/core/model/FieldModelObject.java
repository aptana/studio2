/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.model;

import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Node;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class FieldModelObject extends CoreModelObject
{

	/**
	 * Fields of this object
	 */
	protected HashMap<String, ModelField> fields = new HashMap<String, ModelField>();

	/**
	 * Model node
	 */
	protected Node modelNode;

	/**
	 * Creates an field model object with an id field
	 */
	public FieldModelObject()
	{
		addField(CoreModelObject.ID_ELEMENT, CoreModelObject.ID_ELEMENT, null);
	}

	/**
	 * Sets a field with the specified name
	 * 
	 * @param name
	 * @param value
	 */
	public void setField(String name, String value)
	{
		ModelField field = fields.get(name);
		if (field != null && isModelChanged(field.getValue(), value))
		{
			field.setValue(value);
			fireChange();
		}
	}

	/**
	 * Gets a field out of this model object
	 * 
	 * @param name
	 * @return - string value or null if none found
	 */
	public String getField(String name)
	{
		return getField(name, true);
	}

	/**
	 * Gets a field out of this model object and optionally searches the current node for the field if it doesn't exist
	 * in the field collection yet
	 * 
	 * @param name
	 * @param search
	 * @return - string value or null if none found
	 */
	public String getField(String name, boolean search)
	{
		String value = null;
		ModelField field = fields.get(name);

		if (field == null && search && modelNode != null)
		{
			addField(name, name, getTextContent(name, modelNode));
			field = fields.get(name);
		}

		if (field != null)
		{
			value = field.getValue();
		}

		return value;
	}

	/**
	 * Gets the fields in this model object
	 * 
	 * @return - collection of model field objects
	 */
	public Collection<ModelField> getFields()
	{
		return fields.values();
	}

	/**
	 * Gets a field as an int
	 * 
	 * @param name
	 * @return - int value or -1 if null string value or non-parseable string value
	 */
	public int getIntegerField(String name)
	{
		int value = -1;
		String stringValue = getField(name);
		if (stringValue != null)
		{
			try
			{
				value = Integer.parseInt(stringValue);
			}
			catch (NumberFormatException e)
			{
			}
		}
		return value;
	}

	/**
	 * Gets a field as a boolean
	 * 
	 * @param name
	 * @return - boolean value or false if null string value or non-parseable string value
	 */
	public boolean getBooleanField(String name)
	{
		return Boolean.parseBoolean(getField(name));
	}

	/**
	 * Adds a field
	 * 
	 * @param remoteName
	 * @param localName
	 * @param value
	 * @return - true if added
	 */
	public boolean addField(String remoteName, String localName, String value)
	{
		boolean added = false;
		if (remoteName != null)
		{
			ModelField field = new ModelField(localName, remoteName, value);
			if (localName != null)
			{
				fields.put(localName, field);
			}
			fields.put(remoteName, field);
			added = true;
		}
		return added;
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromNode(org.w3c.dom.Node)
	 */
	public void fromNode(Node node)
	{
		this.modelNode = node;
		boolean changed = false;
		for (ModelField field : fields.values())
		{
			String newValue = getTextContent(field.getRemoteName(), node);
			if (isNewValueValid(field.getValue(), newValue))
			{
				field.setValue(newValue);
				changed = true;
			}
		}
		changed |= parseNestedElements(node);
		if (changed)
		{
			fireChange();
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toNode()
	 */
	public Node toNode()
	{
		return modelNode;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return getField(ID_ELEMENT);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		setField(ID_ELEMENT, id);
	}

	/**
	 * Parse any nested model elements that aren't handled by model fields
	 * 
	 * @param node
	 * @return - true if the nested elements have changed
	 */
	protected boolean parseNestedElements(Node node)
	{
		// Does nothing by default, subclasses should override
		return false;
	}

	/**
	 * Adds the xml for the nested model elements
	 * 
	 * @param buffer
	 */
	protected void addNestedElementXML(StringBuffer buffer)
	{
		// Does nothing by default, subclasses should override
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#toXML()
	 */
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer("<" + getItemString() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		addNestedElementXML(buffer);
		for (ModelField field : fields.values())
		{
			if (field.getValue() != null)
			{
				buffer.append("<" + field.getRemoteName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append(field.getValue());
				buffer.append("</" + field.getRemoteName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		buffer.append("</" + getItemString() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}

}
