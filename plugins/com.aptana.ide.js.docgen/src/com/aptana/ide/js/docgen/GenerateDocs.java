/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.js.docgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.outline.JSContentProvider;
import com.aptana.ide.editor.js.outline.JSOutlineItem;
import com.aptana.ide.editor.js.outline.JSOutlineItemType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * Utility class for generating documentation
 * @author Ingo Muschenetz
 *
 */
public final class GenerateDocs
{
	/**
	 * Private constructor
	 *
	 */
	private GenerateDocs()
	{
		
	}
	
	/**
	 * Generates XML from a JS parse state
	 * @param parseState The current parse state of the file
	 * @param fileName Optional parameter for name of the file being parsed
	 * @return
	 */
	public static String generateXML(JSParseState parseState, String fileName)
	{
		String xml = null;

		JSContentProvider cp = new JSContentProvider();
		try
		{
			IParseNode results = parseState.getParseResults();
			Object[] nodes = cp.getElements(results);
			xml = getXML(cp, nodes, parseState, fileName);
		}
		catch (Exception ex)
		{
			IdeLog.logError(DocgenPlugin.getDefault(), Messages.GenerateDocs_ERR_GenerateXML, ex);
		}

		return xml;
	}

	/**
	 * Generates HTML documents from a string of XML
	 * @param xml
	 * @param docRoot
	 * @param fileName
	 * @param schemaStream
	 * @return
	 */
	public static String generateHTMLFromXML(String xml, String docRoot, String fileName, InputStream schemaStream)
	{
		StringReader sw = new StringReader(xml);
		try
		{
			String filePath = docRoot;
			Path p = new Path(filePath);
			String indexPath = p.append("index.html").toOSString(); //$NON-NLS-1$
			dump(xml, indexPath + ".xml"); //$NON-NLS-1$
			transform(sw, schemaStream, fileName, indexPath);
			return "file://" + indexPath; //$NON-NLS-1$

		}
		catch (TransformerException ex)
		{
			IdeLog.logError(DocgenPlugin.getDefault(), Messages.GenerateDocs_ERR_TransformDoc, ex);
		}
		catch (IOException ex)
		{
			IdeLog.logError(DocgenPlugin.getDefault(), Messages.GenerateDocs_ERR_TransformDoc, ex);
		}

		return null;
	}

	/**
	 * Retrieves the function documentation from the documentation store.
	 * 
	 * @param lexeme
	 *            The lexeme for which to retrieve documentation
	 * @return A new FunctionDocumentation object, or null if not found
	 */
	protected static PropertyDocumentation getFunctionDocumentation(JSParseState parseState, int offset)
	{
		if (parseState != null)
		{
			IDocumentationStore store = parseState.getDocumentationStore();
			return (PropertyDocumentation) store.getDocumentationFromOffset(offset);
		}
		return null;
	}

	/**
	 * Retrieves the property documentation from the documentation store.
	 * 
	 * @param lexeme
	 *            The lexeme for which to retrieve documentation
	 * @return A new PropertyDocumentation object, or null if not found
	 */
	protected static IDocumentation getPropertyDocumentation(JSParseState parseState, Lexeme lexeme)
	{
		if (lexeme == null)
		{
			return null;
		}
		if (parseState != null)
		{
			IDocumentationStore store = parseState.getDocumentationStore();
			return (IDocumentation) store.getDocumentationFromOffset(lexeme.offset + lexeme.getLength());
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML()
	 */
	public static String getXML(JSContentProvider cp, Object[] nodes, JSParseState ps, String fileName)
	{
		SourceWriter writer = new SourceWriter();
		writer.println("<?xml-stylesheet type=\"text/xsl\" href=\"docs.xsl\"?><javascript fileName=\"" + fileName //$NON-NLS-1$
				+ "\">"); //$NON-NLS-1$
		writer.increaseIndent();
		getXML(writer, cp, nodes, ps, null);
		writer.println("</javascript>"); //$NON-NLS-1$

		return writer.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.IParseNode#getXML(com.aptana.ide.io.SourceWriter)
	 */
	public static void getXML(SourceWriter writer, JSContentProvider cp, Object[] nodes, JSParseState ps, String prefix)
	{
		if (nodes == null)
		{
			return;
		}

		for (int i = 0; i < nodes.length; i++)
		{
			Object item = nodes[i];

			if (item instanceof JSOutlineItem)
			{
				JSOutlineItem jsItem = (JSOutlineItem) item;
				PropertyDocumentation fd = getRelatedDocumentation(ps, jsItem);
				String newPrefix = ""; //$NON-NLS-1$
				if (prefix == null)
				{
					newPrefix = fd.getName();
				}
				else
				{
					newPrefix = prefix + "." + fd.getName(); //$NON-NLS-1$
				}
				if (prefix != null && (newPrefix.endsWith(".prototype") || prefix.endsWith(".prototype"))) //$NON-NLS-1$ //$NON-NLS-2$
				{
					fd.setIsInstance(true);
				}
				startOutlineItem(writer, (FunctionDocumentation) fd, jsItem);
				Object[] children = cp.getChildren(jsItem);
				getXML(writer, cp, children, ps, newPrefix);
				endOutlineItem(writer, jsItem);
			}
		}
	}

	/**
	 * Change the item type code to a string
	 * 
	 * @param type
	 * @return
	 */
	private static String typeAsStringType(int type)
	{
		String returnString = null;

		switch (type)
		{
			case JSOutlineItemType.FUNCTION:
			{
				returnString = "Function"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.ARRAY:
			{
				returnString = "Array"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.BOOLEAN:
			{
				returnString = "Boolean"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.NULL:
			{
				returnString = "null"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.NUMBER:
			{
				returnString = "Number"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.REGEX:
			{
				returnString = "Regex"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.STRING:
			{
				returnString = "String"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.PROPERTY:
			case JSOutlineItemType.OBJECT_LITERAL:
			default:
			{
				returnString = "Object"; //$NON-NLS-1$
				break;
			}			
		}

		return returnString;
	}

	/**
	 * Change the item type code to a string
	 * 
	 * @param type
	 * @return
	 */
	private static String typeAsString(int type)
	{
		String returnString = null;

		switch (type)
		{
			case JSOutlineItemType.FUNCTION:
			{
				returnString = "function"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.OBJECT_LITERAL:
			{
				returnString = "object_literal"; //$NON-NLS-1$
				break;
			}
			case JSOutlineItemType.PROPERTY:
			case JSOutlineItemType.ARRAY:
			case JSOutlineItemType.BOOLEAN:
			case JSOutlineItemType.NULL:
			case JSOutlineItemType.NUMBER:
			case JSOutlineItemType.REGEX:
			case JSOutlineItemType.STRING:
			{
				returnString = "property"; //$NON-NLS-1$
				break;
			}
			default:
			{
				returnString = "unknown"; //$NON-NLS-1$
				break;
			}			
		}

		return returnString;
	}

	/**
	 * Ends an item in the outline
	 * 
	 * @param writer
	 * @param jsItem
	 */
	private static void endOutlineItem(SourceWriter writer, JSOutlineItem jsItem)
	{
		writer.decreaseIndent();
		writer.printlnWithIndent("</" + typeAsString(jsItem.getType()) + ">"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Starts an item in the outline
	 * 
	 * @param writer
	 * @param documentation
	 * @param jsItem
	 */
	private static void startOutlineItem(SourceWriter writer, FunctionDocumentation documentation, JSOutlineItem jsItem)
	{
		writer.printWithIndent("<" + typeAsString(jsItem.getType())).print( //$NON-NLS-1$
				" name=\"" + stripTags(documentation.getName()) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writeAttributes(writer, documentation, jsItem);
		writer.println(">"); //$NON-NLS-1$
		writer.increaseIndent();
		if (documentation != null)
		{
			writeDocumentation(writer, documentation);
			writeParameters(writer, documentation);
			writeExamples(writer, documentation);
			writeAliases(writer, documentation);
			writeSeeAlso(writer, documentation);
			if (jsItem.getType() == JSOutlineItemType.FUNCTION)
			{
				writeTypes(writer, documentation);
			}
		}
	}

	private static void writeTypes(SourceWriter writer, FunctionDocumentation documentation)
	{
		if (documentation.getReturn() != null && documentation.getReturn().getTypes().length > 0)
		{
			writer.printlnWithIndent("<return-types>"); //$NON-NLS-1$
			writer.increaseIndent();
			for (int i = 0; i < documentation.getReturn().getTypes().length; i++)
			{
				String array_element = documentation.getReturn().getTypes()[i];
				writer.printWithIndent("<return-type").println(" type=\"" + array_element + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			writer.decreaseIndent();
			writer.printlnWithIndent("</return-types>"); //$NON-NLS-1$
		}
	}

	/**
	 * Write method attributes
	 * 
	 * @param writer
	 * @param documentation
	 */
	private static void writeAttributes(SourceWriter writer, FunctionDocumentation documentation, JSOutlineItem jsItem)
	{
		if (documentation.getIsInstance())
		{
			writer.print(" scope=\"instance\""); //$NON-NLS-1$
		}
		else
		{
			writer.print(" scope=\"static\""); //$NON-NLS-1$
		}
		writer.print(" constructor=\"" + documentation.getIsConstructor() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writer.print(" deprecated=\"" + documentation.getIsDeprecated() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writer.print(" private=\"" + documentation.getIsPrivate() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writer.print(" protected=\"" + documentation.getIsProtected() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writer.print(" ignored=\"" + documentation.getIsIgnored() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		writer.print(" internal=\"" + documentation.getIsInternal() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		if (documentation.getReturn() != null && documentation.getReturn().getTypes().length > 0)
		{
			writer.print(" type=\"" + documentation.getReturn().getTypes()[0] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			writer.print(" type=\"" + typeAsStringType(jsItem.getType()) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * Write method parameters
	 * 
	 * @param writer
	 * @param documentation
	 */
	private static void writeParameters(SourceWriter writer, FunctionDocumentation documentation)
	{
		TypedDescription[] params = documentation.getParams();
		if (documentation.getParams() != null && documentation.getParams().length > 0)
		{
			writer.printlnWithIndent("<parameters>"); //$NON-NLS-1$
			writer.increaseIndent();
			for (int i = 0; i < params.length; i++)
			{
				TypedDescription description = params[i];
				writer.printWithIndent("<parameter").print(" name=\"" + stripTags(description.getName()) + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (description.getTypes().length > 0)
				{
					writer.print(" type=\"" + description.getTypes()[0] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					writer.print(" type=\"Object\""); //$NON-NLS-1$
				}
				writer.println(">"); //$NON-NLS-1$

				if (description.getDescription() != null && !description.getDescription().trim().equals("")) //$NON-NLS-1$
				{
					writer.increaseIndent();
					writeDocumentation(writer, description);
					writer.decreaseIndent();
				}
				writer.printlnWithIndent("</parameter>"); //$NON-NLS-1$
			}
			writer.decreaseIndent();
			writer.printlnWithIndent("</parameters>"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Write method parameters
	 * 
	 * @param writer
	 * @param documentation
	 */
	private static void writeExamples(SourceWriter writer, FunctionDocumentation documentation)
	{
		String[] params = documentation.getExamples();
		if (params != null && params.length > 0)
		{
			writer.printlnWithIndent("<examples>"); //$NON-NLS-1$
			writer.increaseIndent();
			for (int i = 0; i < params.length; i++)
			{
				String description = params[i];
				description = StringUtils.replace(description, "<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
				description = StringUtils.replace(description, ">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
				writer.printWithIndent("<example>"); //$NON-NLS-1$
					writer.increaseIndent();
					writer.printWithIndent(description);
					writer.decreaseIndent();
				writer.printWithIndent("</example>"); //$NON-NLS-1$
			}
			writer.decreaseIndent();
			writer.printlnWithIndent("</examples>"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Write method parameters
	 * 
	 * @param writer
	 * @param documentation
	 */
	private static void writeSeeAlso(SourceWriter writer, FunctionDocumentation documentation)
	{
		String[] params = documentation.getSees();
		if (params != null && params.length > 0)
		{
			writer.printlnWithIndent("<references>"); //$NON-NLS-1$
			writer.increaseIndent();
			for (int i = 0; i < params.length; i++)
			{
				String description = params[i];
				writer.printWithIndent("<reference").print(" name=\"" + stripTags(description) + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			writer.decreaseIndent();
			writer.printlnWithIndent("</references>"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Write method parameters
	 * 
	 * @param writer
	 * @param documentation
	 */
	private static void writeAliases(SourceWriter writer, FunctionDocumentation documentation)
	{
		TypedDescription params = documentation.getAliases();
		if (params != null && params.getTypes().length > 0)
		{
			writer.printlnWithIndent("<aliases>"); //$NON-NLS-1$
			writer.increaseIndent();
			String[] types = params.getTypes();
			for (int i = 0; i < types.length; i++)
			{
				String description = types[i];
				writer.printWithIndent("<alias").print(" name=\"" + stripTags(description) + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			writer.decreaseIndent();
			writer.printlnWithIndent("</aliases>"); //$NON-NLS-1$
		}
	}

	private static void writeDocumentation(SourceWriter writer, PropertyDocumentation fd)
	{
		if (fd.getDescription() != null && !fd.getDescription().trim().equals("")) //$NON-NLS-1$
		{
			writer.printWithIndent("<description>").println(stripTags(fd.getDescription().trim()) + "</description>"); //$NON-NLS-1$ //$NON-NLS-2$					
		}
	}

	private static void writeDocumentation(SourceWriter writer, TypedDescription fd)
	{
		if (fd.getDescription() != null && !fd.getDescription().trim().equals("")) //$NON-NLS-1$
		{
			writer.printWithIndent("<description>").println(stripTags(fd.getDescription().trim()) + "</description>"); //$NON-NLS-1$ //$NON-NLS-2$					
		}
	}

	/**
	 * Strip < and > from the text
	 * 
	 * @param text
	 * @return
	 */
	private static String stripTags(String text)
	{
		String replaced = StringUtils.replace(text, "&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		replaced = StringUtils.replace(replaced, "<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		replaced = StringUtils.replace(replaced, ">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
		replaced = StringUtils.replace(replaced, "\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
		return StringUtils.replace(replaced, "'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Get the related documentation for the particular node
	 * 
	 * @param ps
	 * @param jsItem
	 * @return
	 */
	private static PropertyDocumentation getRelatedDocumentation(JSParseState ps, JSOutlineItem jsItem)
	{
		int startOffset = jsItem.getStartingOffset();
		LexemeList ll = ps.getLexemeList();
		int startIndex = ll.getLexemeIndex(startOffset);
		Lexeme next = ll.get(startIndex + 1);

		PropertyDocumentation fd = null;
		if (!jsItem.getLabel().equals("prototype") //$NON-NLS-1$
				&& (next == null || next.getToken().getTypeIndex() != JSTokenTypes.DOT))
		{
			fd = searchForDocumentation(ps, ll, startIndex);
		}

		if (fd == null)
		{
			fd = new FunctionDocumentation();
			fd.setIsIgnored(true);
		}

		setDocumentationName(fd, jsItem);
		if (jsItem.getType() == JSOutlineItemType.FUNCTION)
		{
			FunctionDocumentation fd2 = (FunctionDocumentation) fd;
			fd2.setIsMethod(true);
		}
		return fd;
	}

	/**
	 * Searches back through the lexeme list to find related documentation
	 * @param ps
	 * @param ll
	 * @param startIndex
	 * @return
	 */
	private static PropertyDocumentation searchForDocumentation(JSParseState ps, LexemeList ll, int startIndex)
	{
		PropertyDocumentation fd = null;
		for (int k = startIndex; k > startIndex - 10; k--)
		{
			if (k < 0)
			{
				break;
			}

			Lexeme l = ll.get(k);
			fd = getFunctionDocumentation(ps, l.getEndingOffset());
			if (fd != null)
			{
				break;
			}
		}
		return fd;
	}

	/**
	 * Set the name of the documentation function
	 * 
	 * @param fd
	 * @param jsItem
	 */
	private static void setDocumentationName(PropertyDocumentation fd, JSOutlineItem jsItem)
	{
		if (fd.getName() == null || fd.getName().equals("")) //$NON-NLS-1$
		{
			if (jsItem.getType() == JSOutlineItemType.FUNCTION)
			{
				IRange range = null; // jsItem.getRange();
				if (range instanceof IParseNode)
				{
					IParseNode pn = (IParseNode) range;
					fd.setName(pn.getAttribute("name")); //$NON-NLS-1$
				}
				else
				{
					fd.setName(stripParens(jsItem.getLabel()));
				}
			}
			else
			{
				fd.setName(stripParens(jsItem.getLabel()));
			}
		}
	}

	/**
	 * Strips the parenthesis
	 * 
	 * @param label
	 * @return
	 */
	public static String stripParens(String label)
	{
		if (label.indexOf('(') > 0)
		{
			return label.substring(0, label.indexOf('('));
		}
		else
		{
			return label;
		}
	}

	/**
	 * Pull the images outside of the jar and export to disk
	 * 
	 * @param object
	 * @param folderPath
	 * @param fileName
	 */
	public static void exportResource(String folderPath, String fileName)
	{
		InputStream zipStream = DocgenPlugin.class
				.getResourceAsStream("/com/aptana/ide/js/docgen/resources/" + fileName); //$NON-NLS-1$
		(new File(folderPath)).mkdirs();
		FileUtils.writeStreamToFile(zipStream, folderPath + "/" + fileName); //$NON-NLS-1$
	}

	/**
	 * Pull the images outside of the jar and export to disk
	 * 
	 * @param object
	 * @param folderPath
	 * @param fileName
	 */
	public static void exportImage(String folderPath, String fileName)
	{
		InputStream zipStream = DocgenPlugin.class
				.getResourceAsStream("/com/aptana/ide/js/docgen/resources/images/" + fileName); //$NON-NLS-1$
		(new File(folderPath)).mkdirs();
		FileUtils.writeStreamToFile(zipStream, folderPath + "/" + fileName); //$NON-NLS-1$
	}

	/**
	 * Returns the "prefix" of a file (which is the name of the file minus the ".js" at the end.
	 * 
	 * @param file
	 *            The file to parse.
	 * @return The file name minus the ".js"
	 */
	public static String getFilePrefix(IFile file)
	{
		if (file == null)
		{
			return StringUtils.EMPTY;
		}

		String extension = "." + file.getFileExtension(); //$NON-NLS-1$
		return file.getName().replaceAll(extension, StringUtils.EMPTY);
	}

	/**
	 * The parent folder of the file. If the file name has a trailing separator, return that, otherwise, return the
	 * parent folder
	 * 
	 * @param file
	 *            The file to parse
	 * @return A string representing the parent folder name.
	 */
	public static String getParentFolder(IFile file)
	{
		IPath path = file.getLocation();
		if (path.hasTrailingSeparator())
		{
			return path.toString();
		}
		else
		{
			IPath newPath = path.removeLastSegments(1);
			return newPath.toString();
		}

	}

	/**
	 * Transform the XML with an XSL file
	 * 
	 * @param sw
	 *            The stream of XML to transform
	 * @param inputStreamXsl
	 *            The XSL file
	 * @param fileNamePrefix
	 *            The "prefix" of the file.
	 * @param filePath
	 *            The path to the parent folder on disk
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 */
	public static void transform(StringReader sw, InputStream inputStreamXsl, String fileNamePrefix, String filePath)
			throws TransformerException, TransformerConfigurationException
	{

		// Create a transform factory instance.
		TransformerFactory tfactory = TransformerFactory.newInstance();

		// Create a transformer for the stylesheet.
		Transformer transformer = tfactory.newTransformer(new StreamSource(inputStreamXsl));
		transformer.setParameter("fileNamePrefix", fileNamePrefix); //$NON-NLS-1$

		// Transform the source XML to System.out.
		transformer.transform(new StreamSource(sw), new StreamResult(new File(filePath)));

	}

	/**
	 * dump
	 * 
	 * @param text
	 * @param filePath
	 * @throws IOException
	 */
	public static void dump(String text, String filePath) throws IOException
	{
		File outFile = new File(filePath);
		FileWriter out = new FileWriter(outFile);
		out.write(text);
		out.close();

	}
}
