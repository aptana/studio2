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
package com.aptana.ide.editor.scriptdoc.parsing.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.FunctionBase;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSFunctionConstructor;
import com.aptana.ide.editor.js.runtime.JSObjectConstructor;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editor.scriptdoc.parsing.AliasEntry;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDoc;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.io.TabledInputStream;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.metadata.UserAgent;

/**
 * @author Kevin Lindsey
 */
public class NativeObjectsReader
{
	private static final int FILE_INDEX = FileContextManager.BUILT_IN_FILE_INDEX;
	private static final int FILE_OFFSET = Range.Empty.getStartingOffset();
	
	private Environment _environment;
	private ScriptDoc _docs;
	private Map<String, TextAttribute> names = new HashMap<String, TextAttribute>();
	private String userAgent = ""; //$NON-NLS-1$

	/**
	 * @return Returns the userAgent.
	 */
	public String getUserAgent()
	{
		return userAgent;
	}

	/**
	 * @param userAgent The userAgent to set.
	 */
	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}

	/**
	 * Get the underlying ScriptDoc that was created during loading of the documentation data
	 * 
	 * @return ScriptDoc
	 */
	public ScriptDoc getScriptDoc()
	{
		return this._docs;
	}

	/**
	 * Create a new instance of NativeObjectsReader
	 * 
	 * @param env
	 *            The JavaScript environment to populate when reading native object documentation
	 */
	public NativeObjectsReader(Environment env)
	{
		this._environment = env;
	}

	/**
	 * Load the native objects from the specified input stream
	 * 
	 * @param stream
	 *            The stream containing the binary native objects documentation
	 * @throws IOException
	 */
	public void load(InputStream stream) throws IOException
	{
		this.load(stream, true);
	}

	/**
	 * Load the native objects from the specified input stream
	 * 
	 * @param stream
	 *            The stream containing the binary native objects documentation
	 * @param autoCreate
	 *            A flag indicating that objects should be created if they are in the documentation but not in the
	 *            environment
	 * @throws IOException
	 */
	public void load(InputStream stream, boolean autoCreate) throws IOException
	{
		// process the resulting documentation
		this._docs = new ScriptDoc();
		//DataInputStream input = new DataInputStream(stream);
		TabledInputStream input = new TabledInputStream(stream);
		this._docs.read(input);

		// post-process the doc data
		this.postProcess(autoCreate);
	}

	/**
	 * Load the specified native objects documentation file
	 * 
	 * @param filename
	 *            The name of the file to load
	 * @throws ScriptDocInitializationException
	 * @throws ScriptDocException
	 */
	public void loadXML(String filename) throws ScriptDocInitializationException, ScriptDocException
	{
		this.loadXML(filename, true);
	}

	/**
	 * Load the specified native objects documentation file
	 * 
	 * @param filename
	 *            The name of the file to load
	 * @param autoCreate
	 *            A flag indicating that objects should be created if they are in the documentation but not in the
	 *            environment
	 * @throws ScriptDocException
	 * @throws ScriptDocInitializationException
	 */
	public void loadXML(String filename, boolean autoCreate) throws ScriptDocInitializationException,
			ScriptDocException
	{
		FileInputStream istream = null;
		try
		{
			// create stream
			istream = new FileInputStream(filename);

			// load stream
			this.loadXML(istream, autoCreate);

		}
		catch (FileNotFoundException e)
		{
			String msg = Messages.NativeObjectsReader_UnalbeToLocateXMLFile + filename;
			ScriptDocException de = new ScriptDocException(msg, e);

			throw de;
		}
		finally
		{
			try
			{
				// close stream
				istream.close();
			}
			catch (IOException e)
			{
				String msg = Messages.NativeObjectsReader_IOError;
				ScriptDocException de = new ScriptDocException(msg, e);

				throw de;
			}
		}
	}

	/**
	 * Load the native objects from the specified input stream
	 * 
	 * @param stream
	 *            The stream containing the native objects documentation
	 * @throws ScriptDocInitializationException
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream) throws ScriptDocInitializationException, ScriptDocException
	{
		this.loadXML(stream, true);
	}

	/**
	 * Load the native objects from the specified input stream
	 * 
	 * @param stream
	 *            The stream containing the native objects documentation
	 * @param autoCreate
	 *            A flag indicating that objects should be created if they are in the documentation but not in the
	 *            environment
	 * @throws ScriptDocInitializationException
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream, boolean autoCreate) throws ScriptDocInitializationException,
			ScriptDocException
	{
		// create a new documentation reader
		ScriptDocReader reader = new ScriptDocReader();

		// load the specified document stream
		reader.loadXML(stream);

		// process the resulting documentation
		this._docs = reader.getDocumentation();

		// post-process the doc data
		this.postProcess(autoCreate);
	}

	/**
	 * createNamespace
	 *
	 * @param root
	 * @param fullyQualifiedType
	 * @return Reference
	 */
	private Reference createNamespace(IObject root, String fullyQualifiedType)
	{
		String[] parts = fullyQualifiedType.split("\\."); //$NON-NLS-1$
		IObject current = root;
		
		// handle namespace
		for (int i = 0; i < parts.length - 1; i++)
		{
			String propertyName = parts[i];
			
			if (current.hasProperty(propertyName))
			{
				current = current.getPropertyValue(propertyName, FILE_INDEX, FILE_OFFSET);
			}
			else
			{
				// create property
				IObject instance = this._environment.createObject(FILE_INDEX, Range.Empty);
				
				current.putPropertyValue(propertyName, instance, FILE_INDEX, Property.DONT_DELETE);
				
				// create user agent
				UserAgent ua = new UserAgent();
				ua.setDescription(""); //$NON-NLS-1$
				ua.setOs(""); //$NON-NLS-1$
				ua.setOsVersion(""); //$NON-NLS-1$
				ua.setPlatform(this.getUserAgent());
				ua.setVersion(""); //$NON-NLS-1$
				
				// create documentation
				PropertyDocumentation documentation = new PropertyDocumentation();
				
				documentation.setName(propertyName);
				documentation.setUserAgent(this.getUserAgent());
				documentation.addUserAgent(ua);
				
				// associate documentation with instance
				instance.setDocumentation(documentation);
				
				current = instance;
			}
		}
		
		String name = parts[parts.length - 1];
		
		return new Reference(current, name);
	}
	
	/**
	 * Post-process the docs that were read in
	 * 
	 * @param docs
	 * @param autoCreate
	 */
	private void postProcess(boolean autoCreate) 
	{
		names.clear();
		
		if (this._environment != null)
		{
			FunctionDocumentation[] functions = this._docs.getFunctions();
			
			for (int i = 0; i < functions.length; i++)
			{
				FunctionDocumentation currentFunction = functions[i];
				currentFunction.setUserAgent(userAgent);
				
				this.processFunction(currentFunction, autoCreate);
				
				String name = currentFunction.getName();
	
				if(!names.containsKey(name))
				{
					names.put(name, null);
				}
			}
	
			PropertyDocumentation[] properties = this._docs.getProperties();
			
			for (int i = 0; i < properties.length; i++)
			{
				PropertyDocumentation currentProperty = properties[i];
				currentProperty.setUserAgent(userAgent);
				
				this.processProperty(currentProperty, autoCreate);
				
				String name = currentProperty.getName();
							
				if(!names.containsKey(name))
				{
					names.put(name, null);
				}
			}
			
			// process aliases
			this.processAliases();
		}
	}

	/**
	 * processAliases
	 */
	private void processAliases()
	{
		IObject global = this._environment.getGlobal();
		
		for (AliasEntry alias : this._docs.getAliases())
		{
			Reference aliasProperty = this.createNamespace(global, alias.name);
			Reference aliasedProperty = this.createNamespace(global, alias.type);
			
			aliasProperty.setValue(aliasedProperty.getValue(FILE_INDEX, FILE_OFFSET), FILE_INDEX);
		}
	}
	
	/**
	 * Incorporate the current class document into the JavaScript environment
	 * 
	 * @param functionDoc
	 *            The class document to process
	 */
	private void processFunction(FunctionDocumentation functionDoc, boolean autoCreate)
	{
		// core and html (or any xml source) objects are always internal
		//functionDoc.setIsInternal(true);

		int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
		int offset = Range.Empty.getStartingOffset();
		// int offset = Environment.BuiltInFileIndex;

		String name = functionDoc.getName();
//if(name.equals("Date"))
//	name += "";
		String type = ""; //$NON-NLS-1$
		//String type = "Object";
		if(functionDoc.getMemberOf().getTypes().length > 0)
		{
			type = functionDoc.getMemberOf().getTypes()[0];
		}
		
		// Handles the "class" case. If not set as "else if"
		// document.getElementById will disappear.
//		else if(functionDoc.getExtends().getTypes().length > 0)
//			type = functionDoc.getExtends().getTypes()[0];

		if (name.equals("#ctor")) //$NON-NLS-1$
		{
			name = "constructor"; //$NON-NLS-1$
			functionDoc.setIsConstructor(true);
		}
		else
		{
			functionDoc.setIsConstructor(false);
		}

		// String desc = functionDoc.getDescription();
		//		
		// String[] ext = functionDoc.getExtends();
		// TypedDescription memberof = functionDoc.getMemberOf();
		// TypedDescription[] params = functionDoc.getParams();
		//		
		// boolean ctor = functionDoc.getIsConstructor();
		// boolean meth = functionDoc.getIsMethod();

		// todo: resolve this based on location in docs
		IObject root = _environment.getGlobal();
		IObject prototype = ObjectBase.UNDEFINED;
		JSFunctionConstructor gFn = (JSFunctionConstructor) root.getPropertyValue("Function", fileIndex, offset); //$NON-NLS-1$
		// JSObjectConstructor gObj = (JSObjectConstructor) root.getPropertyValue("Object", fileIndex, offset);
		
		// global level fn like 'Date'
		if(type.equals("")) //$NON-NLS-1$
		{
			prototype = root;
			functionDoc.getReturn().clearTypes();
			functionDoc.getReturn().addType(name);
		}
		// note: docs like Math come from xml0, and are in Window in the xml
		else if (!type.equals("Global") && !type.equals("Window")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			// global level fn
			IObject ob = root.getPropertyValue(type, fileIndex, offset);
			if (ob == ObjectBase.UNDEFINED)
			{
				root.putPropertyValue(type,
						gFn.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty), fileIndex,
						Property.DONT_DELETE | Property.DONT_ENUM);// | Property.NOT_VISIBLE);

				ob = root.getPropertyValue(type, fileIndex, offset);
				FunctionDocumentation fDoc = new FunctionDocumentation();
				fDoc.setName(type);
				fDoc.setIsConstructor(true);
				//fDoc.setIsInternal(true);
				ob.setDocumentation(fDoc);
			}
			root = ob;
			prototype = ob.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$
		}
		else
		{
			prototype = root.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$
			// for built in's, globabl prototype isn't set yet, but uncreated instaces (like parseInt) can go on global
			// as well.
			if (prototype == null || prototype == ObjectBase.UNDEFINED)
			{
				prototype = root;
			}
		}

		IObject lookup = prototype.getPropertyValue(name, fileIndex, offset);

		// try as static property
		if (lookup == null || lookup == ObjectBase.UNDEFINED)
		{
			lookup = root.getPropertyValue(name, fileIndex, offset);
		}
		// if not, create it
		if (lookup == null || lookup == ObjectBase.UNDEFINED)
		{
			IObject newfn = gFn.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
			prototype.putPropertyValue(name, newfn, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);// |
			// Property.NOT_VISIBLE);
			lookup = newfn;// root.getPropertyValue(type, fileIndex, offset);
		}

		// // temp: until parsing 'static'
		// if(lookup == null)
		// lookup = root.getPropertyValue(name, fileIndex, offset);

		lookup.setDocumentation(functionDoc);
		if(functionDoc != null && functionDoc.getExtends() != null)
		{
			String[] types = functionDoc.getExtends().getTypes();
			if(types.length > 0)
			{
				String superType = types[0];
				if(!superType.equals("Object")) //$NON-NLS-1$
				{
					IObject superObj = _environment.getGlobal().getPropertyValue(superType, fileIndex, 0);
					if(superObj == null || superObj instanceof JSUndefined )
					{
						superObj = gFn.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
						_environment.getGlobal().putPropertyValue(superType, superObj, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
					}
					//IObject obj = lookupReturnTypeFromNameHash(superType, _environment.getGlobal(), _environment);
					if(superObj != null)
					{
						Property prot = superObj.getProperty("prototype"); //$NON-NLS-1$
						if(prot != null)
						{
							// this should only work on local properties, so safe
							lookup.deletePropertyName("prototype"); //$NON-NLS-1$
							
							// probably should be adding via putPropertyValue() here to be safe with refs
							lookup.putPropertyValue("prototype", prot.getAssignment(0), fileIndex); //$NON-NLS-1$
							//func.putLocalProperty("prototype", prot);
						}
					}
				}
			}
		}
		
		
		// process classDoc.getType
		// process classDoc.superClass
		// process classDoc.example
		// process classDoc.references
		// process classDoc.remarks
		// process classDoc.specifications
		// process constructors
	}

	private void processProperty(PropertyDocumentation propertyDoc, boolean autoCreate)
	{
		// core and html (or any xml source) objects are always internal
		//propertyDoc.setIsInternal(true);

		int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
		int offset = Range.Empty.getStartingOffset();
		// int offset = Environment.BuiltInFileIndex;

		String name = propertyDoc.getName();
		String type = propertyDoc.getMemberOf().getTypes()[0];
				
		// Math is a special case for a return type, so we'll leave this
		if(name.equals("Math")) //$NON-NLS-1$
		{
			TypedDescription desc = propertyDoc.getReturn();
			desc.clearTypes();
			desc.addType("Math"); //$NON-NLS-1$
		}
		// todo: resolve this based on location in docs
		IObject root = _environment.getGlobal();
		IObject prototype = ObjectBase.UNDEFINED;
		JSFunctionConstructor gFn = (JSFunctionConstructor) root.getPropertyValue("Function", fileIndex, offset); //$NON-NLS-1$
		JSObjectConstructor gObj = (JSObjectConstructor) root.getPropertyValue("Object", fileIndex, offset); //$NON-NLS-1$

		if (!type.equals("Global") && !type.equals("Window")) // note: docs like Math come from xml0, and are in Window in the xml //$NON-NLS-1$ //$NON-NLS-2$
		{
			// global level fn
			IObject ob = root.getPropertyValue(type, fileIndex, offset);
			if (ob == ObjectBase.UNDEFINED)
			{
				root.putPropertyValue(type,
						gFn.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty), fileIndex,
						Property.DONT_DELETE | Property.DONT_ENUM);// | Property.NOT_VISIBLE);
				ob = root.getPropertyValue(type, fileIndex, offset);
				FunctionDocumentation fDoc = new FunctionDocumentation();
				fDoc.setName(type);
				fDoc.setIsConstructor(true);
				//fDoc.setIsInternal(true);
				ob.setDocumentation(fDoc);
			}
			root = ob;
			prototype = ob.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$
		}
		else
		{
			prototype = root.getPropertyValue("prototype", fileIndex, offset); //$NON-NLS-1$
		}

		IObject lookup = prototype.getPropertyValue(name, fileIndex, offset);

		// try as static property
		if ( (lookup == null || lookup == ObjectBase.UNDEFINED) && 
			 (propertyDoc != null && !propertyDoc.getIsInstance())	)
		{
			lookup = root.getPropertyValue(name, fileIndex, offset);
		}
		// if not, create the object on prototype
		if (lookup == null || lookup == ObjectBase.UNDEFINED)
		{
			//IObject newobj = gObj.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);

			IObject newobj = null;
			if(root instanceof IFunction)
			{
				newobj = ((IFunction)root).construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
			}
			else
			{
				newobj = gObj.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
			}
			
			prototype.putPropertyValue(name, newobj, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);// |
			// Property.NOT_VISIBLE);
			lookup = newobj;// root.getPropertyValue(type, fileIndex, offset);
		}

		// // temp: until parsing 'static'
		// if(lookup == null)//ObjectBase.UNDEFINED)
		// lookup = root.getPropertyValue(name, fileIndex, offset);

		lookup.setDocumentation(propertyDoc);
		
		// make sure it is a member of the correct group
		if(propertyDoc != null && propertyDoc.getMemberOf() != null)
		{
			String[] types = propertyDoc.getMemberOf().getTypes();
			if(types.length > 0)
			{
				String memberType = types[0];
				if(!memberType.equals("Object")) //$NON-NLS-1$
				{
					IObject memberObj = _environment.getGlobal().getPropertyValue(memberType, fileIndex, 0);
					if(memberObj == null || memberObj instanceof JSUndefined )
					{
						memberObj = gFn.construct(_environment, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
						_environment.getGlobal().putPropertyValue(memberType, memberObj, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
					}
					//IObject obj = lookupReturnTypeFromNameHash(superType, _environment.getGlobal(), _environment);
					if(memberObj != null)
					{
						Property prot = memberObj.getProperty("prototype"); //$NON-NLS-1$
						if(prot != null)
						{
							// this should only work on local properties, so safe
							lookup.deletePropertyName("prototype"); //$NON-NLS-1$
							
							// probably should be adding via putPropertyValue() here to be safe with refs
							lookup.putPropertyValue("prototype", prot.getAssignment(0), fileIndex); //$NON-NLS-1$
							//func.putLocalProperty("prototype", prot);
						}
					}
				}
			}
		}
	}

	/**
	 * @return Returns the names.
	 */
	public Map<String, TextAttribute> getNames()
	{
		return names;
	}
}
