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
package com.aptana.ide.scripting;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.http.HttpContentTypes;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.resources.IHttpResource;

/**
 * @author Kevin Lindsey
 */
public class ScriptingHttpResource implements IHttpResource
{
	/*
	 * Fields
	 */
	private static String RUNAT_ATTR = "runat"; //$NON-NLS-1$
	private static String SERVER = "server"; //$NON-NLS-1$
	private static String SERVER_ONLY = "server-only"; //$NON-NLS-1$
	private static String BOTH = "both"; //$NON-NLS-1$

	private File _file;
	private String _text;
	private String _type;

	private Document _document;
	private ScriptingHttpServer _server;

	/*
	 * Properties
	 */

	/**
	 * Try to retrieve the document node from a previous load of this page
	 * 
	 * @return Document
	 */
	private Document getCachedDocument()
	{
		ScriptInfo info = this.getScriptInfo();
		Document result = null;

		if (info != null)
		{
			Object doc = info.getScope().get("document", info.getScope()); //$NON-NLS-1$

			if (doc instanceof NativeJavaObject)
			{
				NativeJavaObject nativeObject = (NativeJavaObject) doc;

				result = (Document) nativeObject.unwrap();
			}
		}

		return result;
	}

	/**
	 * setCachedDocument
	 */
	private void setCachedDocument()
	{
		ScriptInfo info = this.getScriptInfo();

		if (info != null)
		{
			Context.enter();

			Scriptable global = info.getScope();
			Object wrappedDocument = Context.javaToJS(this._document, global);

			global.put("document", global, wrappedDocument); //$NON-NLS-1$
			global.put("location", global, info.getFile().getAbsolutePath()); //$NON-NLS-1$

			Context.exit();
		}
	}

	/**
	 * @see com.aptana.ide.server.resources.IHttpResource#getContentLength()
	 */
	public long getContentLength()
	{
		long result;

		if (this._text != null)
		{
			result = this._text.length();
		}
		else
		{
			result = this._file.length();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.server.resources.IHttpResource#getContentType()
	 */
	public String getContentType()
	{
		return this._type;
	}

	/**
	 * getScriptInfo
	 * 
	 * @return ScriptInfo
	 */
	private ScriptInfo getScriptInfo()
	{
		Global global = this._server.getGlobal();
		String id = global.getXrefId(this.getUri());
		ScriptInfo result = null;

		if (global.hasScriptInfo(id))
		{
			result = global.getScriptInfo(id);
		}

		return result;
	}

	/**
	 * Determines if this resource is one that we should process
	 * 
	 * @return Returns true if this a resource we need to pre-process
	 */
	private boolean isScriptanaResource()
	{
		boolean result = false;

		int fileExtIndex = this._file.getName().lastIndexOf('.');

		if (fileExtIndex != -1)
		{
			String fileExtension = this._file.getName().substring(fileExtIndex);

			this._type = HttpContentTypes.getContentType(fileExtension);

			if (this._type.equals("application/xhtml+xml")) //$NON-NLS-1$
			{
				this._type = "text/html"; //$NON-NLS-1$

				result = true;
			}
		}

		return result;
	}

	/**
	 * Get the URI for this resource
	 * 
	 * @return String
	 */
	public String getUri()
	{
		String result = StringUtils.EMPTY;

		try
		{
			result = this._file.getCanonicalPath();
		}
		catch (IOException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingHttpResource_Error, e);
		}

		return result;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of FileHttpResource
	 * 
	 * @param file
	 */
	public ScriptingHttpResource(File file)
	{
		this._file = file;
		this._text = null;
		this._type = "text/plain"; //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * getContentInputStream
	 * 
	 * @param server
	 * @return InputStream
	 */
	public InputStream getContentInputStream(HttpServer server)
	{
		InputStream result = null;

		// save reference to server
		this._server = (ScriptingHttpServer) server;

		try
		{
			if (this.isScriptanaResource())
			{
				// init document
				this._document = this.getCachedDocument();

				if (this._document == null)
				{
					// create a new scripting environment and load the page into it
					this.loadXHTML();
				}
				else
				{
					ScriptInfo info = this.getScriptInfo();

					if (info.needsRefresh())
					{
						// remove the stale scripting environment for this file
						this._server.removeScriptEnvironment(this.getUri());

						// create a new scripting environment and load the page into it
						this.loadXHTML();
					}
					else
					{
						Script[] scripts = info.getScripts();

						// exec
						Context cx = Context.enter();

						for (int i = 0; i < scripts.length; i++)
						{
							scripts[i].exec(cx, info.getScope());
						}

						Context.exit();
					}
				}

				// save buffer contents so we can return the size in getContentLength
				this._text = this.nodeToString(this._document);

				// create stream
				result = new ByteArrayInputStream(this._text.getBytes("UTF-8")); //$NON-NLS-1$
			}
			else
			{
				// create stream from file
				result = new FileInputStream(this._file);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
		catch (FileNotFoundException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}

		catch (TransformerFactoryConfigurationError e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}

		return result;
	}

	/**
	 * Process an XHTML file
	 */
	private void loadXHTML()
	{
		this._server.createScriptEnvironment(this.getUri());

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// parse XML to create our document element
			this._document = builder.parse(this._file);

			// save a reference to this document in the scripting environment
			this.setCachedDocument();

			// process <script> blocks
			this.processScriptElements();
		}
		catch (ParserConfigurationException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
		catch (SAXException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
		catch (IOException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
		catch (TransformerFactoryConfigurationError e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
	}

	/**
	 * getNodeText
	 * 
	 * @param node
	 * @return String
	 */
	private String nodeToString(Node node)
	{
		String result = StringUtils.EMPTY;

		try
		{
			DOMSource source = new DOMSource(node);
			StringWriter writer = new StringWriter();
			StreamResult streamResult = new StreamResult(writer);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();

			// output to buffer
			xformer.transform(source, streamResult);

			// save buffer contents so we can return the size later
			result = writer.toString();
		}
		catch (TransformerConfigurationException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}
		catch (TransformerException e)
		{
			String message = StringUtils.format(Messages.ScriptingHttpResource_Processing_Error, this.getUri());
			
			IdeLog.logError(ScriptingPlugin.getDefault(), message, e);
		}

		return result;
	}

	/**
	 * processScriptElements
	 */
	private void processScriptElements()
	{
		// grab all script elements
		NodeList scripts = this._document.getElementsByTagName("script"); //$NON-NLS-1$
		Element[] scriptElements = new Element[scripts.getLength()];
		ArrayList serverScripts = new ArrayList();
		ArrayList serverOnlyScripts = new ArrayList();
		ArrayList clientServerScripts = new ArrayList();

		// make an array of script elements since NodeList doesn't act properly when we remove its children from the DOM
		for (int i = 0; i < scripts.getLength(); i++)
		{
			scriptElements[i] = (Element) scripts.item(i);
		}

		// sort server-side scripts into categories for easy processing
		for (int i = 0; i < scriptElements.length; i++)
		{
			Element script = scriptElements[i];

			if (script.hasAttribute(RUNAT_ATTR))
			{
				String runAt = script.getAttribute(RUNAT_ATTR);

				if (runAt.equals(SERVER))
				{
					serverScripts.add(script);
				}
				else if (runAt.equals(SERVER_ONLY))
				{
					serverOnlyScripts.add(script);
				}
				else if (runAt.equals(BOTH))
				{
					clientServerScripts.add(script);
				}
			}
		}

		HashSet baselineFunctions = this.getFunctionNames();
		HashSet bothFunctions = new HashSet();
		HashSet serverFunctions = new HashSet();

		// process scripts that have runat="both" and remove attribute
		for (int i = 0; i < clientServerScripts.size(); i++)
		{
			Element script = (Element) clientServerScripts.get(i);

			this.runScript(script);
			script.removeAttribute(RUNAT_ATTR);
			bothFunctions.addAll(this.getFunctionNames());
		}

		// process and remove scripts that have runat="server"
		for (int i = 0; i < serverScripts.size(); i++)
		{
			Element script = (Element) serverScripts.get(i);

			this.runScript(script);
			script.getParentNode().removeChild(script);
			serverFunctions.addAll(this.getFunctionNames());
		}

		// process and remove scripts that have runat="server-only"
		for (int i = 0; i < serverOnlyScripts.size(); i++)
		{
			Element script = (Element) serverOnlyScripts.get(i);

			this.runScript(script);
			script.getParentNode().removeChild(script);
			// serverFunctions.addAll(this.getFunctionNames());
		}

		// remove baseline functions
		bothFunctions.removeAll(baselineFunctions);
		serverFunctions.removeAll(baselineFunctions);

		// remove "both" functions from "server"
		serverFunctions.removeAll(bothFunctions);

		// create <script> element with wrapped functions
		Element wrapper = this.createWrappers((String[]) serverFunctions.toArray(new String[0]));

		// add <script> to tree
		if (wrapper != null)
		{
			// create a script element for our library
			Element script = this._document.createElement("script"); //$NON-NLS-1$
			script.setAttribute("type", "text/javascript"); //$NON-NLS-1$ //$NON-NLS-2$
			script.setAttribute("src", "/aptana/libs/xmlhttp.js"); //$NON-NLS-1$ //$NON-NLS-2$

			// find the first head element or create one
			NodeList heads = this._document.getElementsByTagName("head"); //$NON-NLS-1$
			Element head;

			if (heads.getLength() > 0)
			{
				head = (Element) heads.item(0);
			}
			else
			{
				Element html = this._document.getDocumentElement();

				head = this._document.createElement("head"); //$NON-NLS-1$

				if (html.hasChildNodes())
				{
					html.insertBefore(head, html.getFirstChild());
				}
				else
				{
					html.appendChild(head);
				}
			}

			// add script elements as first two children of head
			if (head.hasChildNodes())
			{
				head.insertBefore(wrapper, head.getFirstChild());
			}
			else
			{
				head.appendChild(wrapper);
			}

			// insert wrappers
			head.insertBefore(script, wrapper);
		}
	}

	/**
	 * createWrappers
	 */
	private Element createWrappers(String[] names)
	{
		Element result = null;

		if (names.length > 0)
		{
			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < names.length; i++)
			{
				buffer.append("\n"); //$NON-NLS-1$
				buffer.append(this.createFunctionWrapper(names[i]));
			}

			buffer.append("\n"); //$NON-NLS-1$

			String genCode = buffer.toString();
			result = this._document.createElement("script"); //$NON-NLS-1$
			Text wrapperCode = this._document.createTextNode(genCode);

			result.setAttribute("type", "text/javascript"); //$NON-NLS-1$ //$NON-NLS-2$
			result.appendChild(wrapperCode);
		}

		return result;
	}

	/**
	 * @param name
	 * @return String
	 */
	private String createFunctionWrapper(String name)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("function ").append(name).append("() {").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buffer.append("return ___invokeFunction.call(null, \"").append(name).append("\", arguments);").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buffer.append("}"); //$NON-NLS-1$

		return buffer.toString();
	}

	/**
	 * getServerFunctionNames
	 * 
	 * @return HashSet
	 */
	private HashSet getFunctionNames()
	{
		ScriptInfo info = this.getScriptInfo();
		ScriptableObject global = (ScriptableObject) info.getScope();
		Object[] ids = global.getIds();
		HashSet names = new HashSet();

		for (int i = 0; i < ids.length; i++)
		{
			Object idObject = ids[i];

			if (idObject instanceof String)
			{
				String id = (String) idObject;
				int attrs = global.getAttributes(id);
				boolean readonly = (attrs & ScriptableObject.READONLY) == ScriptableObject.READONLY;

				if (readonly == false)
				{
					Object value = global.get(id, global);

					if (value instanceof Callable)
					{
						names.add(id);
					}
				}
			}
		}

		return names;
	}

	/**
	 * runScript
	 * 
	 * @param script
	 */
	private void runScript(Element script)
	{
		String code = null;

		if (script.hasAttribute("src")) //$NON-NLS-1$
		{
			String filename = script.getAttribute("src"); //$NON-NLS-1$
			File file = new File(filename);

			if (file.exists() == false)
			{
				String parentDirectory = this._file.getParent();
				String candidate = parentDirectory + File.separator + filename;

				file = new File(candidate);
			}

			if (file.exists() == false && filename.startsWith("/")) //$NON-NLS-1$
			{
				String rootServerPath = _server.getRootPath();
				String candidate = rootServerPath + filename;

				file = new File(candidate);
			}

			if (file.exists())
			{
				try
				{
					FileInputStream input = new FileInputStream(file);
					code = FileUtilities.getStreamText(input);
				}
				catch (FileNotFoundException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingHttpResource_Error, e);
				}
			}
			else
			{
				String message = StringUtils.format(Messages.ScriptingHttpResource_File_Does_Not_Exist, filename);
				
				IdeLog.logError(ScriptingPlugin.getDefault(), message);
			}
		}
		else
		{
			StringBuffer codePieces = new StringBuffer();
			Node child = script.getFirstChild();

			while (child != null)
			{
				codePieces.append(child.getNodeValue());
				child = child.getNextSibling();
			}

			code = codePieces.toString();
		}

		if (code != null && code.length() > 0)
		{
			this.runScript(code);
		}
	}

	/**
	 * runScript
	 * 
	 * @param script
	 */
	private void runScript(String script)
	{
		this._server.include(this.getUri(), script);
	}
}
