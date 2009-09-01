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
import java.util.List;

import com.aptana.ide.io.SourceWriter;

/**
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class FunctionDocumentation extends PropertyDocumentation
{
	private String _classDescription = ""; //$NON-NLS-1$
	private String _methodName = ""; //$NON-NLS-1$
	private TypedDescription _extends = new TypedDescription();
	private List<MixinDocumentation> _mixins;
	private List<TypedDescription> _params;
	private List<TypedDescription> _exceptions;
	private boolean _isConstructor = false;
	private boolean _isMethod = false;

	/**
	 * Adds an exception that this function can throw.
	 * 
	 * @param value
	 *            The name, type and description of an exception that this function can throw.
	 */
	public void addException(TypedDescription value)
	{
		if (this._exceptions == null)
		{
			this._exceptions = new ArrayList<TypedDescription>();
		}
		
		this._exceptions.add(value);
	}

	/**
	 * addMixin
	 * 
	 * @param string
	 * @param sourceInstanceProperties
	 * @param targetInstanceProperties
	 */
	public void addMixin(String string, boolean sourceInstanceProperties, boolean targetInstanceProperties)
	{
		if (this._mixins == null)
		{
			this._mixins = new ArrayList<MixinDocumentation>();
		}
		
		this._mixins.add(new MixinDocumentation(string, sourceInstanceProperties, targetInstanceProperties));
	}
	
	/**
	 * Adds a TypedDescription object (prototype based class name and description) that describes a parameter used by
	 * this function. To add multiple unknown parameters use the name '...'.
	 * 
	 * @param value
	 *            A TypedDescription object (prototype based class name and description) that describes a parameter used
	 *            by this function.
	 */
	public void addParam(TypedDescription value)
	{
		if (this._params == null)
		{
			this._params = new ArrayList<TypedDescription>();
		}
		
		this._params.add(value);
	}

	/**
	 * Clears the list of exceptions, used when merging docs.
	 */
	public void clearExceptions()
	{
		if (this._exceptions != null)
		{
			this._exceptions.clear();
		}
	}

	/**
	 * Clears the params, used when merging param lists with external script doc files.
	 */
	public void clearParams()
	{
		if (this._params != null)
		{
			this._params.clear();
		}
	}

	/**
	 * Gets the description of the class if this function represents a constructor of the class.
	 * 
	 * @return Returns the description of the class.
	 */
	public String getClassDescription()
	{
		return this._classDescription;
	}

	/**
	 * Gets any exceptions this function can throw.
	 * 
	 * @return Returns an array of exceptions this function can throw.
	 */
	public TypedDescription[] getExceptions()
	{
		if (this._exceptions == null)
		{
			return new TypedDescription[0];
		}
		
		return this._exceptions.toArray(new TypedDescription[this._exceptions.size()]);
	}

	/**
	 * Gets the prototype based class this function extends, if any (default is Object).
	 * 
	 * @return Returns the prototype based class this function extends, if any (default is Object).
	 */
	public TypedDescription getExtends()
	{
		if (this._extends == null)
		{
			return new TypedDescription();
		}
		
		return this._extends;
	}

	/**
	 * Gets true if this object is used as a constructor (an object can be used both as a method and a constructor, or
	 * neither).
	 * 
	 * @return Returns true if this object is used as a constructor.
	 */
	public boolean getIsConstructor()
	{
		return this._isConstructor;
	}

	/**
	 * Gets true if this object is used as a method (an object can be used both as a method and a constructor, or
	 * neither).
	 * 
	 * @return Returns true if this object is used as a method.
	 */
	public boolean getIsMethod()
	{
		return this._isMethod;
	}

	/**
	 * Gets the name of the method if different than the computed value.
	 * 
	 * @return Returns the name of the method.
	 */
	public String getMethodName()
	{
		return this._methodName;
	}

	/**
	 * getMixins
	 * 
	 * @return
	 */
	public MixinDocumentation[] getMixins()
	{
		MixinDocumentation[] result = new MixinDocumentation[0];
		
		if (this._mixins != null)
		{
			result = this._mixins.toArray(new MixinDocumentation[this._mixins.size()]);
		}
		
		return result;
	}
	
	/**
	 * Gets an array of TypedDescription objects (prototype based class name and description) that describe the
	 * parameters used by this function.
	 * 
	 * @return Returns the params, each described by a TypedDescription object.
	 */
	public TypedDescription[] getParams()
	{
		if (this._params == null)
		{
			return new TypedDescription[0];
		}

		return this._params.toArray(new TypedDescription[this._params.size()]);
	}

	/**
	 * @throws IOException
	 * @see com.aptana.ide.editor.scriptdoc.parsing.DocumentationBase#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		super.read(input);

		this._classDescription = input.readUTF();
		this._extends = new TypedDescription();
		this._extends.read(input);

		int size = input.readInt();
		
		if (size > 0)
		{
			this._params = new ArrayList<TypedDescription>();

			for (int i = 0; i < size; i++)
			{
				TypedDescription param = new TypedDescription();

				param.read(input);
				this._params.add(param);
			}
		}

		size = input.readInt();
		if (size > 0)
		{
			this._exceptions = new ArrayList<TypedDescription>();

			for (int i = 0; i < size; i++)
			{
				TypedDescription exception = new TypedDescription();

				exception.read(input);
				this._exceptions.add(exception);
			}
		}

		this._isConstructor = input.readBoolean();
		this._isMethod = input.readBoolean();
		this.setIsIgnored(input.readBoolean());
	}

	/**
	 * Sets the description of the class if this function represents a constructor of the class.
	 * 
	 * @param value
	 *            The description of the class.
	 */
	public void setClassDescription(String value)
	{
		this._classDescription = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * Sets the prototype based class this function extends, if any (default is Object).
	 * 
	 * @param value
	 *            The name (or names, comma separated) of the prototype based class(es) this function extends.
	 */
	public void setExtends(TypedDescription value)
	{
		this._extends = value;
	}

	/**
	 * Set to true if this object is used as a constructor (an object can be used both as a method and a constructor, or
	 * neither).
	 * 
	 * @param value
	 *            True if this object is used as a constructor.
	 */
	public void setIsConstructor(boolean value)
	{
		this._isConstructor = value;
	}

	/**
	 * Set to true if this object is used as a method (an object can be used both as a method and a constructor, or
	 * neither).
	 * 
	 * @param value
	 *            True if this object is used as a method.
	 */
	public void setIsMethod(boolean value)
	{
		this._isMethod = value;
	}

	/**
	 * Sets the name of the method if different than the computed value.
	 * 
	 * @param value
	 *            The name of the method.
	 */
	public void setMethodName(String value)
	{
		this._methodName = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation#printBody(com.aptana.ide.io.SourceWriter)
	 */
	protected void printBody(SourceWriter writer)
	{
		super.printBody(writer);
		
		this.printTag(writer, "@classDescription", this._classDescription); //$NON-NLS-1$
		this.printTag(writer, "@method", this._methodName); //$NON-NLS-1$
		this.printTag(writer, "@extends", this._extends); //$NON-NLS-1$
		
		if (this._params != null)
		{
			for (TypedDescription typeInfo : this._params)
			{
				this.printTag(writer, "@param", typeInfo); //$NON-NLS-1$
			}
		}
		
		if (this._exceptions != null)
		{
			for (TypedDescription exception : this._exceptions)
			{
				this.printTag(writer, "@exception", exception); //$NON-NLS-1$
			}
		}
		
		this.printTag(writer, "@constructor", this._isConstructor); //$NON-NLS-1$
		this.printTag(writer, "@method", this._isMethod); //$NON-NLS-1$
	}
	
	/**
	 * @throws IOException
	 * @see com.aptana.ide.editor.scriptdoc.parsing.DocumentationBase#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		super.write(output);

		output.writeUTF(this._classDescription);
		
		this._extends.write(output);

		if (this._params != null)
		{
			output.writeInt(this._params.size());

			for (int i = 0; i < this._params.size(); i++)
			{
				TypedDescription param = this._params.get(i);

				param.write(output);
			}
		}
		else
		{
			output.writeInt(0);
		}

		if (this._exceptions != null)
		{
			output.writeInt(this._exceptions.size());

			for (int i = 0; i < this._exceptions.size(); i++)
			{
				TypedDescription exception = this._exceptions.get(i);

				exception.write(output);
			}
		}
		else
		{
			output.writeInt(0);
		}

		output.writeBoolean(this._isConstructor);
		output.writeBoolean(this._isMethod);
		output.writeBoolean(this.getIsIgnored());
	}
}
