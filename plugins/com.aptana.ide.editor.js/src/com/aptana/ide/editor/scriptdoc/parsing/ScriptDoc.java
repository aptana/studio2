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

/**
 * @author Robin Debreuil
 */
public class ScriptDoc
{
	private ProjectDocumentation _project = new ProjectDocumentation();;
	private FunctionDocumentation[] _functions = new FunctionDocumentation[0];
	private PropertyDocumentation[] _properties = new PropertyDocumentation[0];
	private AliasEntry[] _aliases = new AliasEntry[0];

	/**
	 * getAliases
	 *
	 * @return
	 */
	public AliasEntry[] getAliases()
	{
		if (this._aliases == null)
		{
			this._aliases = new AliasEntry[0];
		}
		
		return this._aliases;
	}
	
	/**
	 * Gets all the functions in the project.
	 * 
	 * @return Returns all the functions in the project.
	 */
	public FunctionDocumentation[] getFunctions()
	{
		if (this._functions == null)
		{
			this._functions = new FunctionDocumentation[0];
		}

		return this._functions;
	}

	/**
	 * Gets the documentation of the project
	 * 
	 * @return Returns the documentation of the project
	 */
	public ProjectDocumentation getProject()
	{
		if (this._project == null)
		{
			this._project = new ProjectDocumentation();
		}

		return this._project;
	}

	/**
	 * Gets all the properties in the project.
	 * 
	 * @return Returns all the properties in the project.
	 */
	public PropertyDocumentation[] getProperties()
	{
		if (this._properties == null)
		{
			this._properties = new PropertyDocumentation[0];
		}

		return this._properties;
	}

	/**
	 * Read in a binary version of ScriptDoc
	 * 
	 * @param input
	 *            The input stream to read from
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		this._project = new ProjectDocumentation();
		this._project.read(input);

		int size = input.readInt();
		this._functions = new FunctionDocumentation[size];
		for (int i = 0; i < size; i++)
		{
			FunctionDocumentation function = new FunctionDocumentation();

			function.read(input);
			this._functions[i] = function;
		}

		size = input.readInt();
		this._properties = new PropertyDocumentation[size];
		for (int i = 0; i < size; i++)
		{
			PropertyDocumentation property = new PropertyDocumentation();

			property.read(input);
			this._properties[i] = property;
		}
		
		// NOTE: older formats did not include aliases, so we may be at the end
		// of the stream already
		try
		{
			size = input.readInt();
		}
		catch (IOException e)
		{
			size = 0;
		}
		
		this._aliases = new AliasEntry[size];
		for (int i = 0; i < size ;i++)
		{
			this._aliases[i] = AliasEntry.read(input);
		}
	}

	/**
	 * setAliases
	 *
	 * @param aliases
	 */
	public void setAliases(AliasEntry[] aliases)
	{
		this._aliases = aliases;
	}
	
	/**
	 * Sets the value for all the functions in the project.
	 * 
	 * @param functions
	 *            An array of all the functions in the project.
	 */
	public void setFunctions(FunctionDocumentation[] functions)
	{
		this._functions = functions;
	}

	/**
	 * Sets the current project's documentation.
	 * 
	 * @param project
	 */
	public void setProject(ProjectDocumentation project)
	{
		this._project = (project == null) ? new ProjectDocumentation() : project;
	}

	/**
	 * Sets the value for all the properties in the project.
	 * 
	 * @param properties
	 *            An array of all the properties in the project.
	 */
	public void setProperties(PropertyDocumentation[] properties)
	{
		this._properties = properties;
	}

	/**
	 * Write out a binary version of ScriptDoc
	 * 
	 * @param output
	 *            The output stream to write to
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		this._project.write(output);

		output.writeInt(this._functions.length);
		for (int i = 0; i < this._functions.length; i++)
		{
			FunctionDocumentation function = this._functions[i];

			function.write(output);
		}

		output.writeInt(this._properties.length);
		for (int i = 0; i < this._properties.length; i++)
		{
			PropertyDocumentation property = this._properties[i];

			property.write(output);
		}
		
		output.writeInt(this._aliases.length);
		for (int i = 0; i < this._aliases.length; i++)
		{
			AliasEntry alias = this._aliases[i];
			
			alias.write(output);
		}
	}
}
