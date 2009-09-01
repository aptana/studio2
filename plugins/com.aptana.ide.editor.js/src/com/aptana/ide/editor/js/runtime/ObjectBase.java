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
package com.aptana.ide.editor.js.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.metadata.IDocumentation;

/**
 * @author Kevin Lindsey
 * @author Robin Debreuil
 */
public abstract class ObjectBase implements IObject
{
	/**
	 * NULL singleton
	 */
	public static final JSNull NULL = JSNull.getSingletonInstance();

	/**
	 * UNDEFINED singleton
	 */
	public static final JSUndefined UNDEFINED = JSUndefined.getSingletonInstance();

	private IObject _prototype;
	private Map<String,Property> _properties;
	private IDocumentation _documentation;
	private IRange _range;

	/**
	 * Get the property hash table for this object. The hash is lazily instantiated so all access to
	 * property hash should be done through this method
	 * 
	 * @return Returns the property hash table
	 */
	private Map<String,Property> getProperties()
	{
		if (this._properties == null)
		{
			this._properties = new HashMap<String,Property>();
		}

		return this._properties;
	}

	/**
	 * Create a new instance of ObjectBase
	 */
	public ObjectBase()
	{
		this(null);
	}

	/**
	 * Creat a new instance of ObjectBase
	 * 
	 * @param range
	 *            The range of text within the source file that represents this object
	 */
	public ObjectBase(IRange range)
	{
		this._range = range;
	}

	/*
	 * IObject implementation
	 */

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#canPut(java.lang.String)
	 */
	public boolean canPut(String propertyName)
	{
		boolean result = true;

		if (this._properties != null && this.getProperties().containsKey(propertyName))
		{
			Property p = this.getLocalProperty(propertyName);

			result = (p.isReadOnly() == false);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getProperty(java.lang.String)
	 */
	public Property getProperty(String propertyName)
	{
		Property result = null;

		if (this._properties != null && this.getProperties().containsKey(propertyName))
		{
			result = this.getLocalProperty(propertyName);
		}
		else
		{
			if (this._prototype != null)
			{
				result = this._prototype.getProperty(propertyName);
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyValue(java.lang.String, int, int)
	 */
	public IObject getPropertyValue(String propertyName, int fileIndex, int offset)
	{
		IObject result = ObjectBase.UNDEFINED;

		if (this._properties != null && this.getProperties().containsKey(propertyName))
		{
			Property p = this.getLocalProperty(propertyName);

			result = p.getValue(fileIndex, offset);
		}
		else
		{
			if (this._prototype != null)
			{
				result = this._prototype.getPropertyValue(propertyName, fileIndex, offset);
			}
		}

		// XXX Not sure if we should be mixing CommandNode logic at this level,
		// but we need this code to allow
		// descendants of a command node to be used as the value so that we can
		// control the offset being used when
		// storing a value. In particular, any of the binary operators that
		// assign need to use their operator as the
		// offset when storing the value

		// if (result instanceof CommandNode)
		// {
		// CommandNode node = (CommandNode) result;
		// CommandNode parent = node.getParentNode();
		//
		// if (parent instanceof BinaryOperatorAssignNode)
		// {
		// result = parent;
		// }
		// }

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putPropertyValue(java.lang.String,
	 *      com.aptana.ide.editor.js.runtime.IObject, int)
	 */
	public void putPropertyValue(String propertyName, IObject value, int fileIndex)
	{
		this.putPropertyValue(propertyName, value, fileIndex, Property.NONE);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putPropertyValue(java.lang.String,
	 *      com.aptana.ide.editor.js.runtime.IObject, int, int)
	 */
	public void putPropertyValue(String propertyName, IObject value, int fileIndex, int attributes)
	{
		if (this.canPut(propertyName))
		{
			Map<String,Property> properties = this.getProperties();
			Property p;

			if ("Jaxer".equals(propertyName)) //$NON-NLS-1$
			{
				String message = "putPropertyValue put " + value + "\n" + this.getStackTrace(); //$NON-NLS-1$ //$NON-NLS-2$
				
				IdeLog.logInfo(JSPlugin.getDefault(), message);
			}
			
			if (properties.containsKey(propertyName))
			{
				p = this.getLocalProperty(propertyName);
				
				try
				{
					p.setValue(value, fileIndex);
				}
				catch (IllegalStateException e)
				{
					IdeLog.logError(
						JSPlugin.getDefault(),
						StringUtils.format(
							Messages.ObjectBase_AttemptedToOverwritePropertyNameAtFileIndex,
							new String[] {
								propertyName,
								String.valueOf(fileIndex)
							}
						),
						e
					); 
				}
			}
			else
			{
				p = new Property(value, fileIndex, attributes);
				
				properties.put(propertyName, p);
			}

			p.addReference();
		}
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		return this.getPropertyNames(false);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPropertyNames(boolean)
	 */
	public String[] getPropertyNames(boolean getAll)
	{
		Map<String,Boolean> result = new HashMap<String,Boolean>();

		IObject current = this;

		while (current != null)
		{
			String[] localNames = current.getLocalPropertyNames();

			if (getAll)
			{
				for (int i = 0; i < localNames.length; i++)
				{
					result.put(localNames[i], Boolean.TRUE);
				}
			}
			else
			{
				for (int i = 0; i < localNames.length; i++)
				{
					String name = localNames[i];

					if (result.containsKey(name) == false)
					{
						Property p = current.getLocalProperty(name);

						if (p.isEnumerable())
						{
							result.put(localNames[i], Boolean.TRUE);
						}
					}
				}
			}

			current = current.getPrototype();
		}

		Set<String> s = result.keySet();

		return s.toArray(new String[s.size()]);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String propertyName)
	{
		boolean result = false;

		if (this._properties != null && this.getProperties().containsKey(propertyName))
		{
			result = true;
		}
		else
		{
			if (this._prototype != null)
			{
				result = this._prototype.hasProperty(propertyName);
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalProperty(java.lang.String)
	 */
	public Property getLocalProperty(String propertyName)
	{
		Property result = null;

		if (this._properties != null)
		{
			Map<String,Property> properties = this.getProperties();

			if (properties.containsKey(propertyName) == false)
			{
				throw new IllegalArgumentException(Messages.ObjectBase_LocalPropertyNameDoesNotExist + propertyName);
			}

			result = properties.get(propertyName);
		}
		else
		{
			throw new IllegalArgumentException(Messages.ObjectBase_LocalPropertyNameDoesNotExist2 + propertyName);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#putLocalProperty(java.lang.String,
	 *      com.aptana.ide.editor.js.runtime.Property)
	 */
	public void putLocalProperty(String propertyName, Property property)
	{
		Map<String,Property> properties = this.getProperties();

		if ("Jaxer".equals(propertyName)) //$NON-NLS-1$
		{
			String message = "putLocalProperty put Jaxer\n" + this.getStackTrace(); //$NON-NLS-1$
			
			IdeLog.logInfo(JSPlugin.getDefault(), message);
		}
		
		properties.put(propertyName, property);
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalPropertyNames()
	 */
	public String[] getLocalPropertyNames()
	{
		String[] result;

		if (this._properties != null)
		{
			Set<String> names = this.getProperties().keySet();

			result = names.toArray(new String[names.size()]);
		}
		else
		{
			result = new String[0];
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#hasLocalProperty(java.lang.String)
	 */
	public boolean hasLocalProperty(String propertyName)
	{
		boolean result = false;

		if (this._properties != null)
		{
			result = this.getProperties().containsKey(propertyName);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#unputPropertyName(java.lang.String, int, int)
	 */
	public void unputPropertyName(String propertyName, int fileIndex, int offset)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			throw new NullPointerException(Messages.ObjectBase_PropertyNameMustBeDefined);
		}

		Property p = this.getProperty(propertyName);

		if (p == null)
		{
			throw new NullPointerException(Messages.ObjectBase_TryingToUnputAPropertyThatDoesNotExist + propertyName);
		}

		if (p.removeReference() == 0)
		{
			if (p.isPermanent() == false)
			{
				this.deletePropertyName(propertyName);
			}
		}
		else
		{
			if ("Jaxer".equals(propertyName)) //$NON-NLS-1$
			{
				String message = "unputPropertyValue:\n" + this.getStackTrace(); //$NON-NLS-1$
				
				IdeLog.logInfo(JSPlugin.getDefault(), message);
			}
			
			p.unsetValue(fileIndex, offset);
		}
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#deletePropertyName(java.lang.String)
	 */
	public boolean deletePropertyName(String propertyName)
	{
		boolean result = true;

		if (this._properties != null)
		{
			Map<String,Property> properties = this.getProperties();

			if (properties.containsKey(propertyName))
			{
				Property p = this.getLocalProperty(propertyName);

				if (p.isReadOnly())
				{
					result = false;
				}
				else
				{
					if ("Jaxer".equals(propertyName)) //$NON-NLS-1$
					{
						String message = "deletePropertyName deleted Jaxer\n" + this.getStackTrace(); //$NON-NLS-1$
						
						IdeLog.logInfo(JSPlugin.getDefault(), message);
					}
					
					properties.remove(propertyName);
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getPrototype()
	 */
	public IObject getPrototype()
	{
		return this._prototype;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#setPrototype(com.aptana.ide.editor.js.runtime.IObject)
	 */
	public void setPrototype(IObject prototype)
	{
		this._prototype = prototype;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getClassName()
	 */
	public abstract String getClassName();

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getInstance(com.aptana.ide.editor.js.runtime.Environment,
	 *      int, int)
	 */
	public IObject getInstance(Environment environment, int fileIndex, int offset)
	{
		// built-in and primitive types simply return "this"
		return this;
	}

	/*
	 * IRange implementation
	 */

	/**
	 * @see com.aptana.ide.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		int result = -1;

		if (this._range != null)
		{
			result = this._range.getEndingOffset();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		int result = 0;

		if (this._range != null)
		{
			result = this._range.getLength();
		}

		return result;
	}

	/**
	 * Return the range used to define this object
	 * 
	 * @return Returns this object's range in the source document
	 */
	public IRange getRange()
	{
		return this._range;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		int result = -1;

		if (this._range != null)
		{
			result = this._range.getStartingOffset();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		boolean result = true;

		if (this._range != null)
		{
			result = this._range.isEmpty();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IRange#containsOffset(int)
	 */
	public boolean containsOffset(int offset)
	{
		boolean result = false;

		if (this._range != null)
		{
			result = this._range.containsOffset(offset);
		}

		return result;
	}

	/*
	 * IDocumentationContainer implementation
	 */

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#getDocumentation()
	 */
	public IDocumentation getDocumentation()
	{
		return this._documentation;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#hasDocumentation()
	 */
	public boolean hasDocumentation()
	{
		return this._documentation != null;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentationContainer#setDocumentation(com.aptana.ide.metadata.IDocumentation)
	 */
	public void setDocumentation(IDocumentation documentation)
	{
		this._documentation = documentation;
	}

	/**
	 * @see com.aptana.ide.editor.js.runtime.IObject#getLocalPropertyCount()
	 */
	public int getLocalPropertyCount()
	{
		int result = 0;

		if (this._properties != null)
		{
			result = this._properties.size();
		}

		return result;
	}
	
	/**
	 * [KEL] temporary for debugging purposes only
	 * 
	 * getStackTrace
	 * @return
	 */
	private String getStackTrace()
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		
		for (StackTraceElement element : elements)
		{
			sb.append(element.toString()).append("\n"); //$NON-NLS-1$
		}
		
		return sb.toString();
	}
}
