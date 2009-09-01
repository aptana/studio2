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
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.runtime.Assignment;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.FunctionBase;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.NativeConstructorBase;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editor.scriptdoc.parsing.AliasEntry;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.MixinDocumentation;
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
public class NativeObjectsReader2
{
	private class FunctionDocumentationPair
	{
		private IObject _function;
		private FunctionDocumentation _documentation;
		private String _typeName;
		
		/**
		 * FunctionDocumentationPair
		 * 
		 * @param function
		 * @param documentation
		 */
		public FunctionDocumentationPair(IObject function, FunctionDocumentation documentation)
		{
			this._function = function;
			this._documentation = documentation;
			this._typeName = this._documentation.getName();
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			boolean result = false;
			
			if (obj == this)
			{
				result = true;
			}
			else if (obj instanceof FunctionDocumentation)
			{
				((FunctionDocumentation) obj).getName().equals(this.getTypeName());
			}
			
			return result;
		}

		/**
		 * @return the documentation
		 */
		public FunctionDocumentation getDocumentation()
		{
			return this._documentation;
		}

		/**
		 * @return the function
		 */
		public IObject getFunction()
		{
			return this._function;
		}

		public String[] getSuperTypes()
		{
			return this._documentation.getExtends().getTypes();
		}

		/**
		 * @return the name
		 */
		public String getTypeName()
		{
			return this._typeName;
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return this._typeName.hashCode();
		}
	}
	
	private static final String CONSTRUCTOR = "constructor";	//$NON-NLS-1$
	private static final String CTOR = "#ctor";	//$NON-NLS-1$
	private static final String EMPTY_STRING = "";	//$NON-NLS-1$
	private static final String GLOBAL = "Global";	//$NON-NLS-1$
	private static final String MATH = "Math";	//$NON-NLS-1$
	private static final String OBJECT = "Object";	//$NON-NLS-1$
	private static final String PROTOTYPE = "prototype";	//$NON-NLS-1$
	private static final String WINDOW = "Window";	//$NON-NLS-1$	
	
	private static final int FILE_OFFSET = Range.Empty.getStartingOffset();
	
	private int _fileIndex;
	private Environment _environment;
	private ScriptDoc _docs;
	private String _userAgent;
	private List<Assignment> _assignments;

	/**
	 * Create a new instance of NativeObjectsReader
	 * 
	 * @param env
	 *            The JavaScript environment to populate when reading native object documentation
	 */
	public NativeObjectsReader2(Environment env)
	{
		this._environment = env;
		this._userAgent = EMPTY_STRING;
		this._fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
		this._assignments = new ArrayList<Assignment>();
	}

	/**
	 * copyProperties
	 * 
	 * @param source
	 * @param target
	 */
	private void copyProperties(IObject source, IObject target)
	{
		if (source != null && source != ObjectBase.UNDEFINED && target != null && target != ObjectBase.UNDEFINED)
		{
			// merge parent prototype into child prototype
			String[] names = source.getPropertyNames(true);
			
			for (String name : names)
			{
				if (target.hasLocalProperty(name) == false)
				{
					this.putLocalProperty(target, name, source.getProperty(name));
				}
			}
		}
	}
	
	/**
	 * createFunction
	 *
	 * @return function instance
	 */
	private IObject createFunction()
	{
		return this._environment.createFunction(this._fileIndex, Range.Empty);
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
				current = current.getPropertyValue(propertyName, this._fileIndex, FILE_OFFSET);
			}
			else
			{
				// create property
				IObject instance = this._environment.createObject(this._fileIndex, Range.Empty);
				
//				current.putPropertyValue(propertyName, instance, this._fileIndex, Property.DONT_DELETE);
				this.putPropertyValue(current, propertyName, instance, Property.NONE); //Property.DONT_DELETE);
				
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
	 * getAssignments
	 *
	 * @return
	 */
	public Assignment[] getAssignments()
	{
		// return empty hash for now
		return this._assignments.toArray(new Assignment[this._assignments.size()]);
	}

	/**
	 * getFileIndex
	 */
	public int getFileIndex()
	{
		return this._fileIndex;
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
	 * @return Returns the userAgent.
	 */
	public String getUserAgent()
	{
		return this._userAgent;
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
	public void loadXML(InputStream stream, boolean autoCreate) throws ScriptDocInitializationException, ScriptDocException
	{
		try
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
		catch (IllegalStateException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.NativeObjectsReader2_ERR_Loading_scriptdoc_file, e);
		}
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
	public void loadXML(String filename, boolean autoCreate) throws ScriptDocInitializationException, ScriptDocException
	{
		FileInputStream istream = null;
		
		try
		{
			// create stream
			istream = new FileInputStream(filename);

			// load stream
			this.loadXML(istream, autoCreate);

		}
		catch (IllegalStateException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.NativeObjectsReader2_ERR_Loading_scriptdoc_file, e);
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
	 * Post-process the docs that were read in
	 * 
	 * @param docs
	 * @param autoCreate
	 */
	private void postProcess(boolean autoCreate) 
	{
		if (this._environment != null)
		{
			// process functions first so we'll have constructors when creating
			// properties later
			FunctionDocumentation[] functions = this._docs.getFunctions();
			IObject[] functionInstances = new IObject[functions.length];
			
			for (int i = 0; i < functions.length; i++)
			{
				FunctionDocumentation currentFunction = functions[i];
				
				// set user agent
				currentFunction.setUserAgent(this._userAgent);
				
				// process function
				functionInstances[i] = this.processFunction(currentFunction, autoCreate);
			}
			
			// process properties
			PropertyDocumentation[] properties = this._docs.getProperties();
			
			for (int i = 0; i < properties.length; i++)
			{
				PropertyDocumentation currentProperty = properties[i];
				
				// set user agent
				currentProperty.setUserAgent(this._userAgent);
				
				// process properties
				this.processProperty(currentProperty, autoCreate);
			}
			
			// process mix-ins
			this.processMixins(functions, functionInstances);
			
			// process inheritance last so we get all properties and mix-ins that
			// were added to the super classes
			this.processInheritance(functions, functionInstances);
			
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
			
//			aliasProperty.setValue(aliasedProperty.getValue(this._fileIndex, FILE_OFFSET), this._fileIndex);
			this.putPropertyValue(
				aliasProperty.getObjectBase(),
				aliasProperty.getPropertyName(),
				aliasedProperty.getValue(this._fileIndex, FILE_OFFSET),
				Property.NONE
			);
		}
	}

	/**
	 * Incorporate the current class document into the JavaScript environment
	 * 
	 * @param documentation
	 *            The class document to process
	 * @param autoCreate
	 */
	private IObject processFunction(FunctionDocumentation documentation, boolean autoCreate)
	{
		IObject[] info = processOwningType(documentation);
		IObject root = info[0];
		IObject prototype = info[1];
		
		// get function name
		String name = documentation.getName();
		
		// setup flags relating to dotted names
		boolean dottedName = (name.indexOf('.') != -1);
		
		// see if this exists already
		IObject targetFunction = null;
		
		// adjust constructor names
		if (name.equals(CTOR))
		{
			name = CONSTRUCTOR;
			documentation.setIsConstructor(true);
			targetFunction = root;
		}
		
		if (targetFunction == null && documentation.getIsInstance() && prototype.hasLocalProperty(name))
		{
			targetFunction = prototype.getPropertyValue(name, this._fileIndex, FILE_OFFSET);
		}

		// if nothing, try as a namespaced function
		if ((targetFunction == null || targetFunction == ObjectBase.UNDEFINED) && dottedName)
		{
			Reference ref = this.createNamespace(root, name);
			
			name = ref.getPropertyName();
			root = ref.getObjectBase();
			prototype = root.getPropertyValue(PROTOTYPE, this._fileIndex, FILE_OFFSET);
			targetFunction = ref.getValue(this._fileIndex, FILE_OFFSET);
		}
		
		// if nothing, try as static property
		if (targetFunction == null || targetFunction == ObjectBase.UNDEFINED)
		{
			if (root.hasLocalProperty(name))
			{
				targetFunction = root.getPropertyValue(name, this._fileIndex, FILE_OFFSET);
			}
		}
	
		// if still nothing, then create it
		if (targetFunction == null || targetFunction == ObjectBase.UNDEFINED)
		{
			IObject newFunction = this.createFunction();
			
			if (documentation.getIsInstance() == false)
			{
//				root.putPropertyValue(name, newFunction, this._fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
				this.putPropertyValue(root, name, newFunction, Property.DONT_DELETE | Property.DONT_ENUM);
			}
			else
			{
//				prototype.putPropertyValue(name, newFunction, this._fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
				this.putPropertyValue(prototype, name, newFunction, Property.DONT_DELETE | Property.DONT_ENUM);
			}
			
			targetFunction = newFunction;
		}

//		// process any types this inherits from
//		String[] types = (documentation != null && documentation.getExtends() != null) ? documentation.getExtends().getTypes() : null;
//		
//		processParentType(targetFunction, documentation, types);
		
		// associate documentation with object, but don't override documentation for built-ins
		if (targetFunction instanceof NativeConstructorBase == false)
		{
			targetFunction.setDocumentation(documentation);
		}
		
		return targetFunction;
	}

	/**
	 * processInheritance
	 * 
	 * @param functions
	 * @param functionInstances
	 */
	private void processInheritance(FunctionDocumentation[] functions, IObject[] functionInstances)
	{
		FunctionDocumentationPair[] pairs = this.sortByInheritanceDependency(functions, functionInstances);
		
		// setup any inheritance
		for (FunctionDocumentationPair pair : pairs)
		{
			IObject functionInstance = pair.getFunction();
			FunctionDocumentation documentation = pair.getDocumentation();
			String[] types = new String[0];
			
			if (documentation != null)
			{
				TypedDescription typeInfo = documentation.getExtends();
				
				if (typeInfo != null)
				{
					types = typeInfo.getTypes();
				}
			}
					
			for (int i = types.length - 1; i >= 0; i--)
			{
				String superTypeName = types[i];
				
				// process all non-Object types
				if (superTypeName.equals(OBJECT) == false)
				{
					Reference ref = this.createNamespace(this._environment.getGlobal(), superTypeName);
					IObject superTypeConstructor = ref.getValue(this._fileIndex, FILE_OFFSET);
					
					// see if the super type already exists
					if (superTypeConstructor != null && superTypeConstructor != ObjectBase.UNDEFINED)
					{
						Property parentPrototypeProperty = superTypeConstructor.getProperty(PROTOTYPE);
						IObject parentPrototype = parentPrototypeProperty.getAssignment(0);
						Property childPrototypeProperty = functionInstance.getProperty(PROTOTYPE);
						IObject childPrototype = childPrototypeProperty.getAssignment(0);
						
						// merge parent prototype into child prototype
						this.copyProperties(parentPrototype, childPrototype);
					}
				}
			}
		}
	}

	/**
	 * processMixins
	 * @param functions
	 * @param functionInstances
	 */
	private void processMixins(FunctionDocumentation[] functions, IObject[] functionInstances)
	{
		// setup any inheritance
		for (int i = 0; i < functions.length; i++)
		{
			IObject functionInstance = functionInstances[i];
			FunctionDocumentation documentation = functions[i];
			MixinDocumentation[] mixins = new MixinDocumentation[0];
			
			if (documentation != null)
			{
				mixins = documentation.getMixins();
			}
					
			for (int j = mixins.length - 1; j >= 0; j--)
			{
				MixinDocumentation mixin = mixins[j];
				String type = mixin.getType();
				
				// process all non-Object types
				if (type.equals(OBJECT) == false)
				{
					IObject sourceObject = this._environment.getGlobal().getPropertyValue(type, this._fileIndex, FILE_OFFSET);
					
					if (sourceObject != null && sourceObject != ObjectBase.UNDEFINED)
					{
						IObject targetObject = functionInstance;
						
						if (mixin.getSourceInstanceProperties() == false)
						{
							// grab prototype
							sourceObject = sourceObject.getProperty(PROTOTYPE).getAssignment(0);
						}
						
						if (mixin.getTargetInstanceProperties() == false)
						{
							// grab prototype
							targetObject = targetObject.getProperty(PROTOTYPE).getAssignment(0);
						}
						
						this.copyProperties(sourceObject, targetObject);
					}
				}
			}
		}
	}

	/**
	 * processOwningType
	 *
	 * @return Reference
	 */
	private IObject[] processOwningType(FunctionDocumentation documentation)
	{
		IObject root = this._environment.getGlobal();
		IObject prototype = ObjectBase.UNDEFINED;
		String owningType = EMPTY_STRING;
		
		// return owning type from docs
		if (documentation.getMemberOf().getTypes().length > 0)
		{
			owningType = documentation.getMemberOf().getTypes()[0];
		}

		// global level function like 'Date'
		if (owningType.equals(EMPTY_STRING))
		{
			prototype = root;
			documentation.getReturn().clearTypes();
			documentation.getReturn().addType(documentation.getName());
		}
		// note: docs like Math come from xml0, and are in Window in the xml
		else if (owningType.equals(GLOBAL) == false && owningType.equals(WINDOW) == false)
		{
			// global level function
			IObject ob = root.getPropertyValue(owningType, this._fileIndex, FILE_OFFSET);
			
			if (ob == ObjectBase.UNDEFINED && owningType.indexOf('.') != -1) //$NON-NLS-1$
			{
				Reference ref = this.createNamespace(root, owningType);
				
				owningType = ref.getPropertyName();
				root = ref.getObjectBase();
				ob = ref.getValue(this._fileIndex, FILE_OFFSET);
			}
				
			if (ob == ObjectBase.UNDEFINED)
			{
//				root.putPropertyValue(
//					owningType,
//					this.createFunction(),
//					this._fileIndex,
//					Property.DONT_DELETE | Property.DONT_ENUM
//				);
				this.putPropertyValue(root, owningType, this.createFunction(), Property.DONT_DELETE | Property.DONT_ENUM);

				// create documentation
				FunctionDocumentation fDoc = new FunctionDocumentation();
				
				fDoc.setName(owningType);
				fDoc.setIsConstructor(true);
				
				// associate documentation with owning type
				ob = root.getPropertyValue(owningType, this._fileIndex, FILE_OFFSET);
				ob.setDocumentation(fDoc);
			}
			
			root = ob;
			prototype = ob.getPropertyValue(PROTOTYPE, this._fileIndex, FILE_OFFSET);
		}
		else
		{
			prototype = root.getPropertyValue(PROTOTYPE, this._fileIndex, FILE_OFFSET);
			
			// for built in's, global prototype isn't set yet, but uncreated instances (like parseInt) can go on global
			// as well.
			if (prototype == null || prototype == ObjectBase.UNDEFINED)
			{
				prototype = root;
			}
		}
		
		return new IObject[] { root, prototype };
	}

	/**
	 * processProperty
	 *
	 * @param documentation
	 * @param autoCreate
	 */
	private void processProperty(PropertyDocumentation documentation, boolean autoCreate)
	{
		IObject[] info = this.processPropertyType(documentation);
		IObject root = info[0];
		IObject prototype = info[1];
		
		// determine if this property is static
		boolean isStatic = (documentation != null && documentation.getIsInstance() == false);
		
		// get property name
		String name = documentation.getName();
		
		// see if we have the property
		IObject propertyObject = null;
		
		if (isStatic == false && prototype.hasLocalProperty(name))
		{
			propertyObject = prototype.getPropertyValue(name, this._fileIndex, FILE_OFFSET);
		}

		// if nothing, try accessing as a static property
		if ((propertyObject == null || propertyObject == ObjectBase.UNDEFINED) && isStatic)
		{
			if (root.hasLocalProperty(name))
			{
				propertyObject = root.getPropertyValue(name, this._fileIndex, FILE_OFFSET);
			}
		}
		
		// if still nothing, create the object
		if (propertyObject == null || propertyObject == ObjectBase.UNDEFINED)
		{
			String[] types = documentation.getReturn().getTypes();
			IObject type = null;
			IObject newobj = null;
			
			if (types != null && types.length > 0)
			{
				String typeName = types[0];
				Reference ref = this.createNamespace(this._environment.getGlobal(), typeName);
				
				type = ref.getValue(this._fileIndex, FILE_OFFSET);
			}
			
			if (type instanceof IFunction)
			{
				newobj = ((IFunction) type).construct(this._environment, FunctionBase.EmptyArgs, this._fileIndex, Range.Empty);
			}
			else
			{
				newobj = this._environment.createObject(this._fileIndex, Range.Empty);
			}
			
			if (isStatic)
			{
//				root.putPropertyValue(name, newobj, this._fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
				this.putPropertyValue(root, name, newobj, Property.DONT_DELETE | Property.DONT_ENUM);
			}
			else
			{
//				prototype.putPropertyValue(name, newobj, this._fileIndex, Property.DONT_DELETE | Property.DONT_ENUM);
				this.putPropertyValue(prototype, name, newobj, Property.DONT_DELETE | Property.DONT_ENUM);
			}
			
			propertyObject = newobj;
		}

		// process owning type
		propertyObject.setDocumentation(documentation);
//		processParentType(propertyObject, documentation, documentation.getMemberOf().getTypes());
	}

	/**
	 * processPropertyType
	 *
	 * @return IObject[]
	 */
	private IObject[] processPropertyType(PropertyDocumentation propertyDoc)
	{
		IObject root = this._environment.getGlobal();
		IObject prototype = ObjectBase.UNDEFINED;
		String name = propertyDoc.getName();
		String type = propertyDoc.getMemberOf().getTypes()[0];
				
		// Math is a special case for a return type, so we'll leave this
		if (name.equals(MATH))
		{
			TypedDescription desc = propertyDoc.getReturn();
			
			desc.clearTypes();
			desc.addType(MATH); //$NON-NLS-1$
		}
		
		// note: docs like Math come from xml0, and are in Window in the xml
		if (type.equals(GLOBAL) == false && type.equals(WINDOW) == false)
		{
			// global level function
			IObject ob = root.getPropertyValue(type, this._fileIndex, FILE_OFFSET);
			
			if (ob == ObjectBase.UNDEFINED && type.indexOf('.') != -1) //$NON-NLS-1$
			{
				Reference ref = this.createNamespace(root, type);
				
				type = ref.getPropertyName();
				root = ref.getObjectBase();
				ob = ref.getValue(this._fileIndex, FILE_OFFSET);
			}
			
			if (ob == ObjectBase.UNDEFINED)
			{
//				root.putPropertyValue(
//					type,
//					this.createFunction(),
//					this._fileIndex,
//					Property.DONT_DELETE | Property.DONT_ENUM
//				);
				this.putPropertyValue(root, type, this.createFunction(), Property.DONT_DELETE | Property.DONT_ENUM);
				
				// create documentation
				FunctionDocumentation fDoc = new FunctionDocumentation();
				fDoc.setName(type);
				fDoc.setIsConstructor(true);
				
				// associate documentation with owning type
				ob = root.getPropertyValue(type, this._fileIndex, FILE_OFFSET);
				ob.setDocumentation(fDoc);
			}
			
			root = ob;
			prototype = ob.getPropertyValue(PROTOTYPE, this._fileIndex, FILE_OFFSET);
		}
		else
		{
			prototype = root.getPropertyValue(PROTOTYPE, this._fileIndex, FILE_OFFSET);
		}
		
		return new IObject[] { root, prototype };
	}
	
	/**
	 * putLocalProperty
	 *
	 * @param parentObject
	 * @param propertyName
	 * @param property
	 */
	private void putLocalProperty(IObject parentObject, String propertyName, Property property)
	{
		// set property
		parentObject.putLocalProperty(propertyName, property);
		
		// create reference and assignment
		Reference reference = new Reference(parentObject, propertyName);
		Assignment assignment = new Assignment(reference, null);
		
		// add to list of updated properties
		this._assignments.add(assignment);
	}

	/**
	 * putPropertyValue
	 *
	 * @param parentObject
	 * @param propertyName
	 * @param value
	 * @param attributes
	 */
	private void putPropertyValue(IObject parentObject, String propertyName, IObject value, int attributes)
	{
		// set property value
		parentObject.putPropertyValue(propertyName, value, this._fileIndex, attributes);
		
		// create reference and assignment
		Reference reference = new Reference(parentObject, propertyName);
		Assignment assignment = new Assignment(reference, value);
		
		// add to list of updated properties
		this._assignments.add(assignment);
	}
	
	/**
	 * setFileIndex
	 *
	 * @param fileIndex
	 */
	public void setFileIndex(int fileIndex)
	{
		this._fileIndex = fileIndex;
	}
	
	/**
	 * @param userAgent The userAgent to set.
	 */
	public void setUserAgent(String userAgent)
	{
		this._userAgent = userAgent;
	}

	/**
	 * FunctionDocumentationPair
	 * 
	 * @param functions
	 * @param functionInstances
	 * @return
	 */
	private FunctionDocumentationPair[] sortByInheritanceDependency(FunctionDocumentation[] functions, IObject[] functionInstances)
	{
		SimpleDependencyGraph<FunctionDocumentationPair> graph = new SimpleDependencyGraph<FunctionDocumentationPair>();
		
		// generate mappings and vertices
		for (int i = 0; i < functions.length; i++)
		{
			// combine function and documentation
			FunctionDocumentationPair pair = new FunctionDocumentationPair(functionInstances[i], functions[i]);
			
			// get type name
			String type = pair.getTypeName();
			
			if (CTOR.equals(type) == false)
			{
				// store reference into graph for later lookup by name
				graph.addMapping(type, pair);
				
				// add vertex
				graph.addVertex(type);
			}
		}
		
		// process all vertices
		for (FunctionDocumentation documentation : functions)
		{
			String type = documentation.getName();
			
			if (CTOR.equals(type) == false)
			{
				for (String superType : documentation.getExtends().getTypes())
				{
					if ("Object".equals(superType) == false && CTOR.equals(superType) == false) //$NON-NLS-1$
					{
						graph.addEdge(type, superType);
					}
				}
			}
		}
		
		// sort
		String[] types = graph.topologicalSort();
		
		// build result
		FunctionDocumentationPair[] result = new FunctionDocumentationPair[types.length];
		
		for (int i = 0; i < types.length; i++)
		{
			result[i] = graph.getItem(types[i]);
		}
		
		return result;
	}
}
