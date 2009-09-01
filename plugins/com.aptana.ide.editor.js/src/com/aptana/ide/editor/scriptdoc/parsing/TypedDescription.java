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
package com.aptana.ide.editor.scriptdoc.parsing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class that holds type info and descriptions, used when representing return types, params, and exceptions.
 * 
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class TypedDescription
{
	private static final Map<String, String> BUILTIN_ALIASES;
	private static final String[] NO_STRINGS = new String[0];
	private static final TypedDescription[] NO_TYPED_DESCRIPTIONS = new TypedDescription[0];

	private List<String> _types;
	private List<TypedDescription> _defaultValues;
	private String _description = ""; //$NON-NLS-1$
	private String _name = ""; //$NON-NLS-1$
	
	/**
	 * static type initialization
	 */
	static
	{
		BUILTIN_ALIASES = new HashMap<String, String>();
		
		BUILTIN_ALIASES.put("array", "Array"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("boolean", "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Bool", "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("bool", "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("char", "String"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Char", "String"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("date", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("double", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Double", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("error", "Error"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("float", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Float", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("function", "Function"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("int", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Int", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("integer", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("Integer", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("number", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("object", "Object"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("regEx", "RegExp"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("RegEx", "RegExp"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("regex", "RegExp"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("regExp", "RegExp"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("regexp", "RegExp"); //$NON-NLS-1$ //$NON-NLS-2$
		BUILTIN_ALIASES.put("string", "String"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Creates a description of a prototype based class (type) that includes the given name if appropriate, and a
	 * description.
	 */
	public TypedDescription()
	{
	}
	
	/**
	 * Creates a description of a prototype based class (type) that includes the given name if appropriate, and a
	 * description.
	 * 
	 * @param description
	 *            Description for this usage.
	 */
	public TypedDescription(String description)
	{
		this.setDescription(description);
	}
	
	/**
	 * Creates a description of a prototype based class (type) that includes the given name if appropriate, and a
	 * description.
	 * 
	 * @param description
	 *            Description for this usage.
	 * @param name
	 *            The given name (eg, param name, 'return', or exeption arg name). This can be empty where not
	 *            appropriate - like extends and memberof.
	 */
	public TypedDescription(String description, String name)
	{
		this.setDescription(description);
		this.setName(name);
	}

	/**
	 * Adds a default value to the default value list.
	 * 
	 * @param value
	 *            The full name (including namespaces, if any) of value to add.
	 */
	public void addDefaultValue(TypedDescription value)
	{
		if (value != null)
		{
			if (this._defaultValues == null)
			{
				this._defaultValues = new ArrayList<TypedDescription>();
			}
	
			this._defaultValues.add(value);
		}
	}

	/**
	 * Adds a type to the type list.
	 * 
	 * @param value
	 *            The full name (including namespaces, if any) of type to add.
	 */
	public void addType(String value)
	{
		value = (value == null) ? "" : value; //$NON-NLS-1$

		if (this._types == null)
		{
			this._types = new ArrayList<String>();
		}

		this._types.add(checkForBuiltInAlias(value));
	}

	/**
	 * @param value
	 * @return Returns the alias type name based on common ScriptDoc usages
	 */
	private String checkForBuiltInAlias(String value)
	{
		if (BUILTIN_ALIASES.containsKey(value))
		{
			return BUILTIN_ALIASES.get(value);
		}
		else
		{
			return value;
		}
	}

	/**
	 * Clear known types, this can be removed once the doc return types coming in are stable (eg Math should return
	 * nothing or Math, not default to Object)
	 */
	public void clearDefaultValues()
	{
		if (this._defaultValues != null)
		{
			this._defaultValues.clear();
		}
	}

	/**
	 * Clear known types, this can be removed once the doc return types coming in are stable (eg Math should return
	 * nothing or Math, not default to Object)
	 */
	public void clearTypes()
	{
		if (this._types != null)
		{
			this._types.clear();
		}
	}

	/**
	 * A list of default values for this object.
	 * 
	 * @return Returns a list of default values (really useful for parameters).
	 */
	public TypedDescription[] getDefaultValues()
	{
		TypedDescription[] result = NO_TYPED_DESCRIPTIONS;
		
		if (this._defaultValues != null)
		{
			result = this._defaultValues.toArray(new TypedDescription[this._defaultValues.size()]);
		}

		return result;
	}

	/**
	 * Gets the description of this object. This can include simple html.
	 * 
	 * @return Returns the description of this object.
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * Gets the name, if any, that this object represents. This includes param names, or 'return' in the case of a
	 * return type. This can be left empty in the case of memberof or extends.
	 * 
	 * @return Returns the name, if any, that this object represents. This includes param names, or 'return' in the case
	 *         of a return type.
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * A list of types this object represents.
	 * 
	 * @return Returns a list of types, by full name (including namespaces, if any).
	 */
	public String[] getTypes()
	{
		String[] result = NO_STRINGS;
		
		if (this._types != null)
		{
			result = this._types.toArray(new String[this._types.size()]);;
		}

		return result;
	}

	/**
	 * Read in a binary representation of this object
	 * 
	 * @param input
	 *            The stream to read from
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		int size = input.readInt();

		if (size > 0)
		{
			this._defaultValues = new ArrayList<TypedDescription>();

			for (int i = 0; i < size; i++)
			{
				TypedDescription param = new TypedDescription();

				param.read(input);
				this._defaultValues.add(param);
			}
		}

		size = input.readInt();

		if (size > 0)
		{
			this._types = new ArrayList<String>();

			for (int i = 0; i < size; i++)
			{
				String type = input.readUTF();

				this._types.add(type);
			}
		}

		this._description = input.readUTF();
		this._name = input.readUTF();
	}

	/**
	 * Sets the description of this object. This can include simple html.
	 * 
	 * @param value
	 *            The description of this object.
	 */
	public void setDescription(String value)
	{
		this._description = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * Gets the name, if any, that this object represents. This includes param names, or 'return' in the case of a
	 * return type. This can be left empty in the case of memberof or extends.
	 * 
	 * @param value
	 *            The name, if any, that this object represents.
	 */
	public void setName(String value)
	{
		this._name = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * Write out a binary representation of this object
	 * 
	 * @param output
	 *            The stream to write to
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		if (this._defaultValues != null)
		{
			output.writeInt(this._defaultValues.size());

			for (int i = 0; i < this._defaultValues.size(); i++)
			{
				TypedDescription param = this._defaultValues.get(i);

				param.write(output);
			}
		}
		else
		{
			output.writeInt(0);
		}

		if (this._types != null)
		{
			output.writeInt(this._types.size());

			for (int i = 0; i < this._types.size(); i++)
			{
				output.writeUTF(this._types.get(i));
			}
		}
		else
		{
			output.writeInt(0);
		}

		output.writeUTF(this._description);
		output.writeUTF(this._name);
	}
}
