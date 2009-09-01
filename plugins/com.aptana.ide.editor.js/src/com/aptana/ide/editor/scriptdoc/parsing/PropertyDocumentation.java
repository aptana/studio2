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
import com.aptana.ide.metadata.UserAgent;

/**
 * Documentation available for properties or functions.
 * 
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class PropertyDocumentation extends DocumentationBase
{
	private TypedDescription _returns = new TypedDescription();
	private TypedDescription _member = new TypedDescription();
	private TypedDescription _alias = new TypedDescription();
	private String _deprecatedDescription = ""; //$NON-NLS-1$
	private String _since = ""; //$NON-NLS-1$
	private List<UserAgent> _userAgents;

	private boolean _isDeprecated = false;
	private boolean _isPrivate = false;
	private boolean _isProtected = false;
	private boolean _isInternal = false;
	private boolean _isNative = false;
	private boolean _isInstance = false;
	private boolean _isInvocationOnly = false;
	private boolean _isIgnored = false;

	/**
	 * Adds a type to the type list.
	 * 
	 * @param value
	 *            The full name (including namespaces, if any) of type to add.
	 */
	public void addUserAgent(UserAgent value)
	{
		if (value != null)
		{
			String platform = value.getPlatform();
			
			if (platform != null && platform.length() > 0)
			{
				if (this._userAgents == null)
				{
					this._userAgents = new ArrayList<UserAgent>();
				}
	
				this._userAgents.add(value);
			}
		}
	}

	/**
	 * Gets a list of aliases, if any. Aliases are used in cases where functions are defined in one place (perhaps
	 * anonymously or in a deep namespace) and then aliased to a new (usually simpler) name.
	 * 
	 * @return Returns a list of aliases, if any.
	 */
	public TypedDescription getAliases()
	{
		if (this._alias == null)
		{
			this._alias = new TypedDescription();
		}

		return this._alias;
	}

	/**
	 * Gets information about the deprecation of this object (optional).
	 * 
	 * @return Returns a description of the deprecated object.
	 */
	public String getDeprecatedDescription()
	{
		return this._deprecatedDescription;
	}

	/**
	 * Gets true if this member has been deprecated.
	 * 
	 * @return Returns true if this member has been deprecated.
	 */
	public boolean getIsDeprecated()
	{
		return this._isDeprecated;
	}

	/**
	 * Gets true if this object is "ignored" or not really meant to be seen publically in documentation.
	 * 
	 * @return Returns true if this object is supposed to be ignored.
	 */
	public boolean getIsIgnored()
	{
		return this._isIgnored;
	}

	/**
	 * Gets true if this is an instance only property (so belongs on the prototype).
	 * 
	 * @return Returns true if this is an instance only property (so belongs on the prototype).
	 */
	public boolean getIsInstance()
	{
		return this._isInstance;
	}

	/**
	 * Gets true if this object is not to be visible at all to javascript (eg. HTMLTableCellElement.createCaption() is
	 * internal to html, and not meant to be visible to javascript) (optional, default is false).
	 * 
	 * @return Returns true if this object is internal.
	 */
	public boolean getIsInternal()
	{
		return this._isInternal;
	}

	/**
	 * Gets true if this is available only at invocation time (like the arguments property inside a function).
	 * 
	 * @return Returns true if this is available only at invocation time (like the arguments property inside a
	 *         function).
	 */
	public boolean getIsInvocationOnly()
	{
		return this._isInvocationOnly;
	}

	/**
	 * Gets true if this object is from native code (eg. Math.abs() is native in javascript and thus can not be deleted)
	 * (optional, default is false).
	 * 
	 * @return Returns true if this object is native.
	 */
	public boolean getIsNative()
	{
		return this._isNative;
	}

	/**
	 * Gets true if this object is not to be accessed from outside the class (optional, default is false).
	 * 
	 * @return Returns true if this object is not to be accessed from outside the class.
	 */
	public boolean getIsPrivate()
	{
		return this._isPrivate;
	}

	/**
	 * Gets true if this object is to be accessed from itself or subclasses (optional, default is false).
	 * 
	 * @return Returns true if this object is to be accessed from itself or subclasses.
	 */
	public boolean getIsProtected()
	{
		return this._isProtected;
	}

	/**
	 * Gets the prototype based class this function is a member of, if any. If this function belongs to multiple types,
	 * they can be added in the TypedDescription types list and the member can be commented accordingly.
	 * 
	 * @return Returns the base type, if any described by a TypedDescription object.
	 */
	public TypedDescription getMemberOf()
	{
		if (this._member == null)
		{
			this._member = new TypedDescription();
		}

		return this._member;
	}

	/**
	 * Gets the return value of this element. This can be from
	 * 
	 * @type for functions and for properties
	 * @return The return type of the object, and its descrpititon.
	 */
	public TypedDescription getReturn()
	{
		if (this._returns == null)
		{
			this._returns = new TypedDescription();
		}

		return this._returns;
	}

	/**
	 * Gets the version that this element was introduced in (optional).
	 * 
	 * @return Returns the version that this element was introduced in (optional).
	 */
	public String getSince()
	{
		return this._since;
	}

	/**
	 * Returns a list of all the platforms this item is supported by
	 * 
	 * @return Returns a list of all the platforms this item is supported by
	 */
	public String[] getUserAgentPlatformNames()
	{
		List<String> al = new ArrayList<String>();

		if (this._userAgents != null)
		{
			for (int i = 0; i < this._userAgents.size(); i++)
			{
				UserAgent param = this._userAgents.get(i);
				
				al.add(param.getPlatform());
			}
		}

		if (this.getUserAgent() != null && !this.getUserAgent().equals("")) //$NON-NLS-1$
		{
			al.add(this.getUserAgent());
		}

		return al.toArray(new String[al.size()]);
	}

	/**
	 * Returns a list of new user agents
	 * 
	 * @return Returns a list of new user agents
	 */
	public UserAgent[] getUserAgents()
	{
		if (this._userAgents == null)
		{
			return new UserAgent[0];
		}

		return this._userAgents.toArray(new UserAgent[this._userAgents.size()]);
	}

	/**
	 * @see com.aptana.ide.editor.scriptdoc.parsing.DocumentationBase#printBody(com.aptana.ide.io.SourceWriter)
	 */
	protected void printBody(SourceWriter writer)
	{
		super.printBody(writer);

		this.printTag(writer, "@return", this._returns); //$NON-NLS-1$
		this.printTag(writer, "@member", this._member); //$NON-NLS-1$
		this.printTag(writer, "@alias", this._alias); //$NON-NLS-1$
		
		if (this._isDeprecated)
		{
			String description = (this._deprecatedDescription != null) ? this._deprecatedDescription : ""; //$NON-NLS-1$
			
			this.printTag(writer, "@deprecated", description); //$NON-NLS-1$
		}
		
		this.printTag(writer, "@private", this._isPrivate); //$NON-NLS-1$
		this.printTag(writer, "@protected", this._isProtected); //$NON-NLS-1$
		this.printTag(writer, "@internal", this._isInternal); //$NON-NLS-1$
		this.printTag(writer, "@native", this._isNative); //$NON-NLS-1$
		this.printTag(writer, "@instance", this._isInstance); //$NON-NLS-1$
		this.printTag(writer, "@invocationOnly", this._isInvocationOnly); //$NON-NLS-1$
		this.printTag(writer, "@ignored", this._isIgnored); //$NON-NLS-1$
	}

	/**
	 * @throws IOException
	 * @see com.aptana.ide.editor.scriptdoc.parsing.DocumentationBase#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		super.read(input);

		int size = input.readInt();
		
		if (size > 0)
		{
			this._userAgents = new ArrayList<UserAgent>();

			for (int i = 0; i < size; i++)
			{
				UserAgent param = new UserAgent();

				param.read(input);
				this._userAgents.add(param);
			}
		}

		this._returns = new TypedDescription();
		this._returns.read(input);
		this._member = new TypedDescription();
		this._member.read(input);
		this._deprecatedDescription = input.readUTF();
		this._since = input.readUTF();
		this._isDeprecated = input.readBoolean();
		this._isPrivate = input.readBoolean();
		this._isProtected = input.readBoolean();
		this._isInternal = input.readBoolean();
		this._isNative = input.readBoolean();
		this._isInstance = input.readBoolean();
		this._isInvocationOnly = input.readBoolean();
		this._isIgnored = input.readBoolean();
	}

	/**
	 * Sets information about the deprecation of this object (optional).
	 * 
	 * @param value
	 *            The information about the deprecation of this object (optional).
	 */
	public void setDeprecatedDescription(String value)
	{
		this._deprecatedDescription = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * Gets true if this member has been deprecated.
	 * 
	 * @param value
	 *            True if this member has been deprecated.
	 */
	public void setIsDeprecated(boolean value)
	{
		this._isDeprecated = value;
	}

	/**
	 * Set to true if this object is "ignored" or not really meant to be seen publically in documentation.
	 * 
	 * @param value
	 *            True if this object is supposed to be ignored.
	 */
	public void setIsIgnored(boolean value)
	{
		this._isIgnored = value;
	}

	/**
	 * Set to true if this is an instance only property (so belongs on the prototype).
	 * 
	 * @param value
	 *            Boolean parameter, true if this is an instance property.
	 */
	public void setIsInstance(boolean value)
	{
		this._isInstance = value;
	}

	/**
	 * Set to true if this object is not to be visible at all to javascript (eg. HTMLTableCellElement.createCaption() is
	 * internal to html, and not meant to be visible to javascript) (optional, default is false).
	 * 
	 * @param value
	 *            Boolean parameter, true if internal.
	 */
	public void setIsInternal(boolean value)
	{
		this._isInternal = value;
	}

	/**
	 * Set to true if this is available only at invocation time (like the arguments property inside a function).
	 * 
	 * @param value
	 *            Boolean parameter, true if this is available only at invocation time.
	 */
	public void setIsInvocationOnly(boolean value)
	{
		this._isInvocationOnly = value;
	}

	/**
	 * Set to true if this object is from native code (eg. Math.abs() is native in javascript and thus can not be
	 * deleted) (optional, default is false).
	 * 
	 * @param value
	 *            Boolean parameter, true if native.
	 */
	public void setIsNative(boolean value)
	{
		this._isNative = value;
	}

	/**
	 * Sets to true if this object is not to be accessed from outside the class (optional, default is false).
	 * 
	 * @param value
	 *            Boolean parameter, true if private.
	 */
	public void setIsPrivate(boolean value)
	{
		this._isPrivate = value;
	}

	/**
	 * Sets to true if this object is only to be accessed from itself or subclasses (optional, default is false).
	 * 
	 * @param value
	 *            Boolean parameter, true if private.
	 */
	public void setIsProtected(boolean value)
	{
		this._isProtected = value;
	}

	/**
	 * Sets the version that this element was introduced in (optional).
	 * 
	 * @param value
	 *            The version that this element was introduced in (optional).
	 */
	public void setSince(String value)
	{
		this._since = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * @throws IOException
	 * @see com.aptana.ide.editor.scriptdoc.parsing.DocumentationBase#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		super.write(output);

		if (this._userAgents != null)
		{
			output.writeInt(this._userAgents.size());

			for (int i = 0; i < this._userAgents.size(); i++)
			{
				UserAgent param = this._userAgents.get(i);

				param.write(output);
			}
		}
		else
		{
			output.writeInt(0);
		}

		this._returns.write(output);
		this._member.write(output);
		output.writeUTF(this._deprecatedDescription);
		output.writeUTF(this._since);
		output.writeBoolean(this._isDeprecated);
		output.writeBoolean(this._isPrivate);
		output.writeBoolean(this._isProtected);
		output.writeBoolean(this._isInternal);
		output.writeBoolean(this._isNative);
		output.writeBoolean(this._isInstance);
		output.writeBoolean(this._isInvocationOnly);
		output.writeBoolean(this._isIgnored);
	}
}
