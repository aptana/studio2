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
//package com.aptana.ide.js.documentation2;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Hashtable;
//
//import com.aptana.ide.js.TypeSet;
//import com.aptana.ide.js.documentation.ClassDocumentation;
//import com.aptana.ide.js.documentation.DocumentationException;
//import com.aptana.ide.js.documentation.DocumentationInitializationException;
//import com.aptana.ide.js.documentation.DocumentationReader;
//import com.aptana.ide.js.documentation.JavaScriptDocumentation;
//import com.aptana.ide.js.documentation.MethodDocumentation;
//import com.aptana.ide.js.documentation.ParameterDocumentation;
//import com.aptana.ide.js.documentation.PropertyDocumentation;
//
///**
// * @author Robin Debreuil
// */
//public class GenJSStubsFromXml
//{
//	private static Hashtable types = new Hashtable();
//
//	static
//	{
//		// init hash
//		types.put("Array", " []");
//		types.put("Boolean", " true");
//		types.put("Date", " new Date()");
//		types.put("Error", " new Error()");
//		types.put("Function", " new function(){}");
//		types.put("Null", " null");
//		types.put("Number", " 0");
//		types.put("Object", " {}");
//		types.put("RegExp", " /./");
//		types.put("String", " \"\"");
//		types.put("Undefined", " undefined");
//
//		types.put("EvalError", "  new EvalError()");
//		types.put("RangeError", "  new RangeError()");
//		types.put("ReferenceError", "  new ReferenceError()");
//		types.put("SyntaxError", "  new SyntaxError()");
//		types.put("TypeError", "  new TypeError()");
//		types.put("URIError", "  new URIError()");
//	}
//
//	/**
//	 * Generate code that reflects the given xml documentation
//	 * 
//	 * @param fileLocation
//	 * @param stream
//	 *            The xml stream to generate with.
//	 * @throws DocumentationInitializationException
//	 * @throws DocumentationException
//	 */
//	public static void generate(String fileLocation, InputStream stream) throws DocumentationInitializationException,
//			DocumentationException
//	{
//		// create a new documentation reader
//		DocumentationReader reader = new DocumentationReader();
//
//		// load the specified document stream
//		reader.loadXML(stream);
//
//		// process the resulting documentation
//		JavaScriptDocumentation docs = reader.getDocumentation();
//		StringBuffer sb = new StringBuffer();
//
//		ClassDocumentation[] classes = docs.getClasses();
//
//		for (int i = 0; i < classes.length; i++)
//		{
//			ClassDocumentation currentClass = classes[i];
//
//			genStubCode(currentClass, sb);
//		}
//		dumpGennedStubs(fileLocation, sb.toString());
//	}
//
//	/**
//	 * Generate code that reflects the class docs
//	 * 
//	 * @param classDoc
//	 *            The class to generate.
//	 * @param sb
//	 *            The stringBuffer to write text into.
//	 */
//	private static void genStubCode(ClassDocumentation classDoc, StringBuffer sb)
//	{
//		String type = classDoc.getType();
//		sb.append("\n\n// ******** " + type + " ********\n\n");
//
//		MethodDocumentation cdoc = null;
//		if (classDoc.getConstructors().length > 0)
//			cdoc = classDoc.getConstructors()[0];
//
//		if (cdoc != null)
//		{
//			genFunctionDoc(cdoc, "constructor", sb);
//
//			sb.append("function " + type + "(");
//			ParameterDocumentation[] params = cdoc.getParameters();
//			String comma = "";
//			for (int i = 0; i < params.length; i++)
//			{
//				boolean isParam = params[i].getName().indexOf("...") > -1;
//				if (isParam)
//					sb.append(comma + "params");
//				else
//					sb.append(comma + params[i].getName());
//
//				comma = ", ";
//			}
//			sb.append(")\n{\n\treturn");
//			if (cdoc.getReturnTypes() != null && cdoc.getReturnTypes().size() > 0)
//			{
//				String ret = (String) types.get(cdoc.getReturnTypes().getNames()[0]);
//				if (ret == null)
//				{
//					ret = " null";
//				}
//				sb.append(ret);
//			}
//			sb.append(";\n}\n");
//		}
//		else
//		{
//			sb.append(type + " = {};\n");
//		}
//
//		String name = "";
//
//		// process methods
//		MethodDocumentation[] mdocs = classDoc.getMethods();
//		for (int i = 0; i < mdocs.length; i++)
//		{
//			MethodDocumentation mdoc = mdocs[i];
//			name = mdoc.getName();
//			genFunctionDoc(mdoc, "method", sb);
//
//			if (mdoc.getScope().equals("static"))
//				sb.append(type + "." + name + " = function(");
//			else
//				sb.append(type + ".prototype." + name + " = function(");
//
//			ParameterDocumentation[] params = mdoc.getParameters();
//			String comma = "";
//			for (int j = 0; j < params.length; j++)
//			{
//				boolean isParam = params[j].getName().indexOf("...") > -1;
//				if (isParam)
//					sb.append(comma + "params");
//				else
//					sb.append(comma + params[j].getName());
//
//				comma = ", ";
//			}
//			sb.append(")\n{\n\treturn");
//			if (mdoc.getReturnTypes() != null && mdoc.getReturnTypes().size() > 0)
//			{
//				String ret = (String) types.get(mdoc.getReturnTypes().getNames()[0]);
//				sb.append(ret);
//			}
//			sb.append(";\n}\n");
//		}
//
//		// process properties
//		PropertyDocumentation[] pdocs = classDoc.getProperties();
//		for (int i = 0; i < pdocs.length; i++)
//		{
//			PropertyDocumentation pdoc = pdocs[i];
//			name = pdoc.getName();
//			genPropertyDoc(pdoc, "field", sb);
//
//			if (pdoc.getScope().equals("static"))
//				sb.append(type + "." + name + " = ");
//			else
//				sb.append(type + ".prototype." + name + " = ");
//
//			String ret = (String) types.get(pdoc.getTypes().getNames()[0]);
//			sb.append(ret + ";\n");
//		}
//	}
//
//	private static void genPropertyDoc(PropertyDocumentation pdoc, String usage, StringBuffer sb)
//	{
//		String newline = "\n * ";
//		sb.append("/**" + newline);
//
//		// description
//		addStringWithoutWhitespace(pdoc.getDescription(), sb);
//		sb.append(newline);
//		sb.append("@usage " + usage + newline);
//		if (pdoc.getScope().equals("static"))
//			sb.append("@static " + newline);
//		if (pdoc.getVisibility().equals("private"))
//			sb.append("@private " + newline);
//
//		// return type
//		genReturnType("@type", pdoc.getTypes(), pdoc.getDescription(), sb);
//
//		sb.append("\n */\n");
//	}
//
//	private static void genFunctionDoc(MethodDocumentation mdoc, String usage, StringBuffer sb)
//	{
//		String newline = "\n * ";
//		sb.append("/**" + newline);
//
//		// description
//		addStringWithoutWhitespace(mdoc.getDescription(), sb);
//		sb.append(newline);
//		sb.append("@usage " + usage + newline);
//
//		if (mdoc.getScope().equals("static"))
//			sb.append("@static " + newline);
//		if (mdoc.getVisibility().equals("private"))
//			sb.append("@private " + newline);
//
//		// params
//		genParamDocs(mdoc.getParameters(), sb);
//
//		// return type
//		genReturnType("@returns", mdoc.getReturnTypes(), mdoc.getReturnDescription(), sb);
//
//		sb.append("\n */\n");
//	}
//
//	private static void genReturnType(String tag, TypeSet returnTypes, String desc, StringBuffer sb)
//	{
//		String[] types = returnTypes.getNames();
//		sb.append(tag + " {");
//		String comma = "";
//		if (types == null || types.length == 0)
//		{
//			sb.append("void} ");
//		}
//		else
//		{
//			for (int i = 0; i < types.length; i++)
//			{
//				sb.append(comma + types[i]);
//				comma = ", ";
//				if (i == types.length - 1)
//					sb.append("} ");
//			}
//		}
//		addStringWithoutWhitespace(desc, sb);
//	}
//
//	private static void genParamDocs(ParameterDocumentation[] pdocs, StringBuffer sb)
//	{
//		String newline = "\n * ";
//		for (int i = 0; i < pdocs.length; i++)
//		{
//			ParameterDocumentation pdoc = pdocs[i];
//
//			boolean isParam = pdoc.getName().indexOf("...") > -1;
//			String name = isParam ? "params" : pdoc.getName();
//
//			sb.append("@param " + name);
//
//			TypeSet types = pdoc.getTypes();
//			String open = " {";
//			String comma = "";
//			String[] names = types.getNames();
//			for (int j = 0; j < names.length; j++)
//			{
//				sb.append(open + comma + names[j]);
//				open = "";
//				comma = ", ";
//				if (j == names.length - 1)
//					sb.append("} ");
//			}
//			// add the dotted name set to desc if this has params.
//			if (isParam)
//				sb.append("(" + pdoc.getName() + ") ");
//
//			addStringWithoutWhitespace(pdoc.getDescription(), sb);
//			sb.append(newline);
//		}
//
//	}
//
//	private static void addStringWithoutWhitespace(String src, StringBuffer sb)
//	{
//		src.trim();
//		String[] desc = src.split("\n");
//		for (int j = 0; j < desc.length; j++)
//		{
//			String part = desc[j].trim();
//			if (part.length() > 0)
//				sb.append(desc[j].trim() + " ");
//		}
//	}
//
//	private static void dumpGennedStubs(String dumpName, String text)
//	{
//		File dumpFile = new File(dumpName);
//
//		try
//		{
//			FileWriter dumpWriter = new FileWriter(dumpFile);
//			dumpWriter.write(text);
//			dumpWriter.close();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
//}
