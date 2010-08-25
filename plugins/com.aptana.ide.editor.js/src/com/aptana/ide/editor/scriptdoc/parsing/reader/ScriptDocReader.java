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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.scriptdoc.parsing.AliasEntry;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.ProjectDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDoc;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.metadata.UserAgent;
import com.aptana.sax.NamespaceSniffer;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;

/**
 * @author Kevin Lindsey
 */
public class ScriptDocReader extends ValidatingReader
{
	private ScriptDoc _documentation;
	private ProjectDocumentation _currentProject;
	private PropertyDocumentation _currentProperty;
	private TypedDescription _currentParameter;
	private FunctionDocumentation _currentClass;
	private FunctionDocumentation _currentMethod;
	private TypedDescription _currentValue;
	private boolean _inReturnType;
	private int _returnTypeCount;

	private boolean _currentException = false;

	private List<FunctionDocumentation> _functions;
	private List<PropertyDocumentation> _properties;
	private List<AliasEntry> _aliases;
	private List<String> _references;

	private boolean _bufferText;
	private String _textBuffer;
	private boolean _parsingCtors;
	private UserAgent _currentUserAgent;
	private boolean _sourceInstanceProperties;

	/**
	 * Create a new instance of CoreLoader
	 * 
	 * @throws ScriptDocInitializationException
	 */
	public ScriptDocReader() throws ScriptDocInitializationException
	{
		// get schema for our documentation XML format
		InputStream schemaStream = ScriptDocReader.class.getResourceAsStream("/com/aptana/ide/editor/scriptdoc/resources/DocumentationSchema.xml"); //$NON-NLS-1$

		try
		{
			// create the schema
			this._schema = SchemaBuilder.fromXML(schemaStream, this);
		}
		catch (SchemaInitializationException e)
		{
			String msg = Messages.ScriptDocReader_SchemaError;
			ScriptDocInitializationException ie = new ScriptDocInitializationException(msg, e);

			throw ie;
		}
		finally
		{
			// close the input stream
			try
			{
				schemaStream.close();
			}
			catch (IOException e)
			{
				String msg = Messages.ScriptDocReader_IOError;
				ScriptDocInitializationException ie = new ScriptDocInitializationException(msg, e);

				throw ie;
			}
		}

		this._functions = new ArrayList<FunctionDocumentation>();
		this._properties = new ArrayList<PropertyDocumentation>();
		this._aliases = new ArrayList<AliasEntry>();
		this._references = new ArrayList<String>();
	}

	/**
	 * Process character data
	 * 
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	public void characters(char[] buffer, int offset, int length)
	{
		if (this._bufferText)
		{
			this._textBuffer += new String(buffer, offset, length);
		}
	}

	/**
	 * start processing an alias element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterAlias(String ns, String name, String qname, Attributes attributes)
	{
		String aliasName = attributes.getValue("name"); //$NON-NLS-1$
		String aliasType = attributes.getValue("type");  //$NON-NLS-1$
		
		if (aliasName != null && aliasName.length() > 0 && aliasType != null && aliasType.length() > 0)
		{
			this._aliases.add(new AliasEntry(aliasName, aliasType));
		}
	}
	
	/**
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		UserAgent field = new UserAgent();

		String platform = attributes.getValue("platform"); //$NON-NLS-1$
		field.setPlatform(platform);

		String version = attributes.getValue("version"); //$NON-NLS-1$
		if (version != null)
		{
			field.setVersion(version);
		}

		String os = attributes.getValue("os"); //$NON-NLS-1$
		if (os != null)
		{
			field.setOs(os);
		}

		String osVersion = attributes.getValue("osVersion"); //$NON-NLS-1$
		if (osVersion != null)
		{
			field.setOsVersion(osVersion);
		}

		this._currentUserAgent = field;
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterClass(String ns, String name, String qname, Attributes attributes)
	{
		// create a new class documentation object
		FunctionDocumentation classDoc = new FunctionDocumentation();
		
		// grab and set property values
		String type = attributes.getValue("type"); //$NON-NLS-1$
		String superclass = attributes.getValue("superclass"); //$NON-NLS-1$

		// set type
		classDoc.setName(type);

		// set optional superclass
		if (superclass != null && superclass.length() > 0)
		{
			String[] types = superclass.split("\\s+"); //$NON-NLS-1$
			
			for (String superType : types)
			{
				if (type != null && type.length() > 0)
				{
					classDoc.getExtends().addType(superType);
				}
			}
		}

		// set current class
		this._currentClass = classDoc;
		this._functions.add(this._currentClass);
	}
	
	/**
	 * enterConstructors
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterConstructors(String ns, String name, String qname, Attributes attributes)
	{
		this._parsingCtors = true;
	}

	/**
	 * start processing an exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterException(String ns, String name, String qname, Attributes attributes)
	{
		// set current exception
		this._currentException = true;
	}

	/**
	 * Start processing a method element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterMethod(String ns, String name, String qname, Attributes attributes)
	{
		// create a new method documentation object
		FunctionDocumentation methodDoc = new FunctionDocumentation();

		if (_parsingCtors)
		{
			methodDoc.setExtends(_currentClass.getExtends());
			methodDoc.setIsConstructor(true); // for this xml format isCtor is always one or the other, user code may vary
			methodDoc.setIsMethod(false);
		}
		else
		{
			methodDoc.setIsConstructor(false);
			methodDoc.setIsMethod(true);
		}

		// determine and set method name
		String mname = attributes.getValue("name"); //$NON-NLS-1$
		if (mname == null)
		{
			methodDoc.setName("#ctor"); //$NON-NLS-1$
		}
		else
		{
			methodDoc.setName(mname);
		}

		String scope = attributes.getValue("scope"); //$NON-NLS-1$
		if (scope != null && scope.equals("instance")) //$NON-NLS-1$
		{
			methodDoc.setIsInstance(true);
		}
		else if (scope.equals("invocation")) //$NON-NLS-1$
		{
			methodDoc.setIsInvocationOnly(true);
		}

		String visibility = attributes.getValue("visibility"); //$NON-NLS-1$
		if (visibility != null && visibility.equals("internal")) //$NON-NLS-1$
		{
			methodDoc.setIsInternal(true);
		}

		methodDoc.getMemberOf().addType(_currentClass.getName());

		this._currentMethod = methodDoc;
		
		// reset return type count
		this._returnTypeCount = 0;
	}

	/**
	 * enterMixin
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterMixin(String ns, String name, String qname, Attributes attributes)
	{
		boolean targetInstanceProperties;
		
		String scope = attributes.getValue("scope"); //$NON-NLS-1$
		String type = attributes.getValue("type"); //$NON-NLS-1$
		
		targetInstanceProperties = ("instance".equals(scope)); //$NON-NLS-1$
		
		this._currentClass.addMixin(type, this._sourceInstanceProperties, targetInstanceProperties);
	}
	
	/**
	 * enterMixins
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterMixins(String ns, String name, String qname, Attributes attributes)
	{
		String scope = attributes.getValue("scope"); //$NON-NLS-1$
		
		this._sourceInstanceProperties = ("instance".equals(scope)); //$NON-NLS-1$
	}
	
	/**
	 * Start processing a parameter element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterParameter(String ns, String name, String qname, Attributes attributes)
	{
		// create a new parameter documentation object
		TypedDescription parameter = new TypedDescription();

		// grab and set properties
		String pname = attributes.getValue("name"); //$NON-NLS-1$
		String type = attributes.getValue("type"); //$NON-NLS-1$

		parameter.setName(pname);
		parameter.addType(type);

		// store parameter
		this._currentParameter = parameter;
	}

	/**
	 * Start processing a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterProperty(String ns, String name, String qname, Attributes attributes)
	{
		// create a new property documentation object
		PropertyDocumentation propertyDoc = new PropertyDocumentation();

		// grab and set property values
		String pname = attributes.getValue("name"); //$NON-NLS-1$
		String type = attributes.getValue("type"); //$NON-NLS-1$

		propertyDoc.setName(pname);

		String scope = attributes.getValue("scope"); //$NON-NLS-1$
		if (scope.equals("instance")) //$NON-NLS-1$
		{
			propertyDoc.setIsInstance(true);
		}
		else if (scope.equals("invocation")) //$NON-NLS-1$
		{
			propertyDoc.setIsInvocationOnly(true);
		}

		String[] types = type.split("\\s*\\|\\s*"); //$NON-NLS-1$
		
		for (String propertyType : types)
		{
			if (propertyType != null && propertyType.length() > 0)
			{
				propertyDoc.getReturn().addType(propertyType);
			}
		}
		
		propertyDoc.getMemberOf().addType(_currentClass.getName());

		// set current property
		this._currentProperty = propertyDoc;
	}

	/**
	 * Exit a reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterReference(String ns, String name, String qname, Attributes attributes)
	{
		// grab and set property values
		String rname = attributes.getValue("name"); //$NON-NLS-1$

		// add buffered text
		this._references.add(rname);
	}

	/**
	 * Exit a return-type element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterReturnType(String ns, String name, String qname, Attributes attributes)
	{
		// grab and set property values
		String type = attributes.getValue("type"); //$NON-NLS-1$

		// add to return-types list
		this._currentMethod.getReturn().addType(type);
		// this._returnTypes.add(type);
		
		this._inReturnType = true;
		this._returnTypeCount++;
	}

	/**
	 * start processing a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterValue(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		TypedDescription field = new TypedDescription();

		// grab and set property values
		String fieldName = attributes.getValue("name"); //$NON-NLS-1$
		field.setName(fieldName);

		String fieldType = attributes.getValue("description"); //$NON-NLS-1$
		field.setDescription(fieldType);

		this._currentValue = field;
	}

	/**
	 * Exit a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
	{
		if (this._currentProperty != null)
		{
			// add example to the current property
			this._currentProperty.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentMethod != null)
		{
			// add description to the current method
			this._currentMethod.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentClass != null)
		{
			// add description to the current method
			this._currentClass.addUserAgent(this._currentUserAgent);
		}

		// clear current class
		this._currentUserAgent = null;
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitClass(String ns, String name, String qname)
	{
		// clear current method
		this._currentClass = null;
	}
	
	/**
	 * Exit a constructors element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitConstructors(String ns, String name, String qname)
	{
		_parsingCtors = false;
	}

	/**
	 * Exit a deprecated element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDeprecated(String ns, String name, String qname)
	{
		if (this._currentProperty != null)
		{
			// add example to the current property
			this._currentProperty.setDeprecatedDescription(this._textBuffer);
		}
		else if (this._currentMethod != null)
		{
			// add description to the current method
			this._currentMethod.setDeprecatedDescription(this._textBuffer);
		}
		else if (this._currentClass != null)
		{
			// add description to the current method
			this._currentClass.setDeprecatedDescription(this._textBuffer);
		}		
		else
		{
			// throw error
		}

		// clear buffer and reset text buffering state
		this._textBuffer = StringUtils.EMPTY;
		this._bufferText = false;
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDescription(String ns, String name, String qname)
	{
		if (this._currentParameter != null)
		{
			// add example to the current parameter
			this._currentParameter.setDescription(this._textBuffer);
		}
		else if (this._currentException != false)
		{
			// ignore
			this._currentException = (this._currentException == false ) ? false : true;
		}
		else if (this._currentProperty != null)
		{
			// add example to the current property
			this._currentProperty.setDescription(this._textBuffer);
		}
		else if (this._currentMethod != null)
		{
			if (this._inReturnType)
			{
				String description = this._currentMethod.getDescription();
				String[] returnTypes = this._currentMethod.getReturn().getTypes();
				String text = ""; //$NON-NLS-1$
				
				if (this._returnTypeCount == 1)
				{
					text = "<b>Returns:</b><br>"; //$NON-NLS-1$
				}
				
				if (returnTypes != null && returnTypes.length > 0)
				{
					String returnType = returnTypes[returnTypes.length - 1];
					
					text += "<b>" + returnType + "</b>: " + this._textBuffer; //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					text += this._textBuffer;
				}
				
				if (description != null && description.length() > 0)
				{
					description += "<br><br>" + text; //$NON-NLS-1$
				}
				else
				{
					description = text;
				}
				
				// add description to the current method
				this._currentMethod.setDescription(description);
			}
			else
			{
				this._currentMethod.setDescription(this._textBuffer);
			}
		}
		else if (this._currentClass != null)
		{
			// add description to the current method
			this._currentClass.setDescription(this._textBuffer);
		}
		else if (this._currentProject != null)
		{
			// add description to the current method
			this._currentProject.setDescription(this._textBuffer);
		}
		else if (this._currentUserAgent != null)
		{
			// add description to the current method
			this._currentUserAgent.setDescription(this._textBuffer);
		}		
		else
		{
			// throw error
		}

		// clear buffer and reset text buffering state
		this._textBuffer = StringUtils.EMPTY; 
		this._bufferText = false;
	}

	/**
	 * Exit a example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitExample(String ns, String name, String qname)
	{
		if (this._currentProperty != null)
		{
			// add example to the current property
			this._currentProperty.addExample(this._textBuffer);
		}
		else if (this._currentMethod != null)
		{
			this._currentMethod.addExample(this._textBuffer);
		}
		else
		{
			this._currentClass.addExample(this._textBuffer);
		}

		// clear buffer and reset text buffering state
		this._textBuffer = ""; //$NON-NLS-1$
		this._bufferText = false;
	}

	/**
	 * Exit a exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitException(String ns, String name, String qname)
	{
		this._currentException = false;
	}

	/**
	 * Exit a javascript element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitJavaScript(String ns, String name, String qname)
	{
		// create documentation container
		this._documentation = new ScriptDoc();

		// add buffered docs to JavaScript documentation
		this._documentation.setProject(this._currentProject);
		this._documentation.setFunctions(this._functions.toArray(new FunctionDocumentation[this._functions.size()]));
		this._documentation.setProperties(this._properties.toArray(new PropertyDocumentation[this._properties.size()]));
		this._documentation.setAliases(this._aliases.toArray(new AliasEntry[this._aliases.size()]));

		// clear buffers
		this._functions.clear();
	}

	/**
	 * Exit a method element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitMethod(String ns, String name, String qname)
	{
		// add class to class list
		this._functions.add(this._currentMethod);

		// clear current method
		this._currentMethod = null;
	}

	/**
	 * Exit a parameter element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitParameter(String ns, String name, String qname)
	{
		if (this._currentParameter == null)
		{
			throw new IllegalArgumentException(Messages.ScriptDocReader_ParamNullError);
		}

		// add parameter to parameter list
		this._currentMethod.addParam(this._currentParameter);

		// clear current parameter
		this._currentParameter = null;
	}

	/**
	 * Exit a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitProperty(String ns, String name, String qname)
	{
		if (this._currentProperty == null)
		{
			throw new IllegalArgumentException(Messages.ScriptDocReader_PropertyNullError);
		}

		// add property to property list
		this._properties.add(this._currentProperty);

		// clear current property
		this._currentProperty = null;
	}

	/**
	 * Exit a remarks element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitRemarks(String ns, String name, String qname)
	{
		if (this._currentProperty != null)
		{
			// add remarks to the current property
			this._currentProperty.setRemarks(this._textBuffer);
		}
		else if (this._currentMethod != null)
		{
			this._currentMethod.setRemarks(this._textBuffer);
		}
		else
		{
			this._currentClass.setRemarks(this._textBuffer);
		}

		// reset buffer and clear buffer flash
		this._textBuffer = ""; //$NON-NLS-1$
		this._bufferText = false;
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitReturnDescription(String ns, String name, String qname)
	{
		if (this._currentMethod != null)
		{
			// add description to the current method
			this._currentMethod.getReturn().setDescription(this._textBuffer);
		}

		// clear buffer and reset text buffering state
		this._textBuffer = ""; //$NON-NLS-1$
		this._bufferText = false;
	}
	
	/**
	 * Exit a return-type element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitReturnType(String ns, String name, String qname)
	{
		this._inReturnType = false;
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
		// add class to class list
		this._currentParameter.addDefaultValue(this._currentValue);

		// clear current class
		this._currentValue = null;
	}

	/**
	 * getAptanaDocumentationStream
	 * 
	 * @param stream
	 * @return
	 */
	private InputStream getAptanaDocumentationStream(InputStream stream)
	{
		String source = null;
		
		try
		{
			// grab all the text so we can analyze it
			source = StreamUtils.getText(stream);
		}
		catch (IOException e)
		{
		}
		finally
		{
			// make sure we have something to work with
			if (source == null)
			{
				source = ""; //$NON-NLS-1$
			}
		}
			
		// create a new stream that we can use to determine the XML's default
		// namespace
		InputStream input = new ByteArrayInputStream(source.getBytes());
		
		try
		{
			NamespaceSniffer sniffer = new NamespaceSniffer();
			String namespace;
			
			sniffer.read(input);
			namespace = sniffer.getNamespace();
			
			if (namespace != null && namespace.length() > 0)
			{
				ForeignScriptDocInfo info = ForeignScriptDocManager.getInstance().getInfo(namespace);
				
				if (info != null)
				{
					InputStream stylesheetStream = info.getStylesheetInputStream();
					
					if (stylesheetStream != null)
					{
						// transform input
						source = this.transformToAptana(source, stylesheetStream);
					}
					else
					{
						IdeLog.logError(
							JSPlugin.getDefault(),
							MessageFormat.format(
								"XSL Transform stylesheet for the {0} namespace does not exist: {1}", //$NON-NLS-1$
								new Object[] {
									info.namespace,
									info.stylesheet.toString()
								}
							)
						);
					}
				}
			}
		}
		catch (ParserConfigurationException e)
		{
		}
		catch (IOException e)
		{
		}
		finally
		{
			try
			{
				input.close();
			}
			catch (IOException e)
			{
			}
		}
		
		// return an input stream of whatever source we ended up with
		return new ByteArrayInputStream(source.getBytes());
	}
	
	/**
	 * Get the resulting documentation object created after loading a documentation document
	 * 
	 * @return The resulting documentation object
	 */
	public ScriptDoc getDocumentation()
	{
		return this._documentation;
	}
	
	/**
	 * transformOAAtoAptana
	 * 
	 * @param source
	 * @return
	 */
	private String transformToAptana(String source, InputStream stylesheetStream)
	{
		String result = null;
		
		// Create a transform factory instance.
		TransformerFactory factor = TransformerFactory.newInstance();

		// Create input/output streams
		StreamSource stylesheetSource = new StreamSource(stylesheetStream);
		InputStream sourceStream = new ByteArrayInputStream(source.getBytes());
		StreamSource sourceSource = new StreamSource(sourceStream);
		StringWriter sourceWriter = new StringWriter();
		StreamResult resultStream = new StreamResult(sourceWriter);
		
		try
		{
			// Create a transformer for the stylesheet.
			Transformer transformer = factor.newTransformer(stylesheetSource);
			
			// Transform the source XML to System.out.
			transformer.transform(sourceSource, resultStream);
			
			result = sourceWriter.toString();
		}
		catch (TransformerConfigurationException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "XSL Transform configuration error", e); //$NON-NLS-1$
		}
		catch (TransformerException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "XSL Transformation error", e); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				stylesheetStream.close();
			}
			catch (IOException e)
			{
			}
			
			try
			{
				sourceStream.close();
			}
			catch (IOException e)
			{
			}
			
			try
			{
				sourceWriter.close();
			}
			catch (IOException e)
			{
			}
		}

		return result;
	}

	/**
	 * Load the JavaScript built-in objects documentation using a stream.
	 * 
	 * @param stream
	 *            The input stream for the source XML
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream1) throws ScriptDocException
	{
		InputStream stream = this.getAptanaDocumentationStream(stream1);
		
		// create a new SAX factory class
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);

		// clear properties
		this._textBuffer = ""; //$NON-NLS-1$
		
		SAXParser saxParser = null;
		
		// parse the XML file
		try
		{
			saxParser = factory.newSAXParser();
			saxParser.parse(stream, this);
		}
		catch (ParserConfigurationException e)
		{
			String msg = Messages.ScriptDocReader_SaxError;
			ScriptDocException de = new ScriptDocException(msg, e);

			throw de;
		}
		catch (SAXException e)
		{
			Exception ex = e.getException();
			String msg = Messages.ScriptDocReader_ParseError;
			
			if (ex != null)
			{
				msg += ex.getMessage();
			}
			else
			{
				msg += e.getMessage();
			}

			ScriptDocException de = new ScriptDocException(msg, e);

			throw de;
		}
		catch (IOException e)
		{
			String msg = Messages.ScriptDocReader_IOParseError;
			ScriptDocException de = new ScriptDocException(msg, e);

			throw de;
		}
	}

	/**
	 * Load the JavaScript built-in objects documentation
	 * 
	 * @param filename
	 * @throws ScriptDocException
	 */
	public void loadXML(String filename) throws ScriptDocException
	{
		FileInputStream fi = null;
		try
		{
			fi = new FileInputStream(filename);

			this.loadXML(fi);
		}
		catch (FileNotFoundException e)
		{
			String msg = Messages.ScriptDocReader_XMLLocationError + filename;
			ScriptDocException de = new ScriptDocException(msg, e);

			throw de;
		}
		finally
		{
			try
			{
				fi.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * start buffering text
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void startTextBuffer(String ns, String name, String qname, Attributes attributes)
	{
		this._bufferText = true;
	}
}
