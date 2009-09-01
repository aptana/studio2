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
package com.aptana.ide.editor.scriptdoc;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSOffsetMapper;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.editors.unified.UnifiedConfiguration;
import com.aptana.ide.editors.unified.utils.LineBreakingReader;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.UserAgent;

/**
 * Helper for generating various forms of the JS documentation.
 * @author Spike Washburn
 *
 */
public final class ScriptDocHelper {
	private ScriptDocHelper(){
		//static utility, no instances allowed
	}
	
	/**
	 * DEFAULT_DELIMITER
	 */
	public static final String DEFAULT_DELIMITER = System.getProperty("line.separator", "\r\n") + "\u2022\t"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/**
	 * Creates a HTML version of the method documentation (suitable for displaying in documentation popups)
	 * @param identifier The string "name" of the item
	 * @param fDoc the method documentation object (usually the same as doc parameter)
	 * @param obj Object used to read actual params on a function
	 * @return string
	 */
	public static String createMethodDocumentationHTML(String identifier,  FunctionDocumentation fDoc, IObject obj) {
		return createMethodDocumentationHTML(identifier,  fDoc, obj, true, false);
	}
	/**
	 * Creates a HTML version of the method documentation (suitable for displaying in documentation popups)
	 * @param identifier The string "name" of the item
	 * @param fDoc the method documentation object (usually the same as doc parameter)
	 * @param obj Object used to read actual params on a function
	 * @param addReturnTypes true if return type info should be added to the information
	 * @param extended 
	 * @return string 
	 */ 
	public static String createMethodDocumentationHTML(String identifier,  FunctionDocumentation fDoc, IObject obj,  boolean addReturnTypes, boolean extended) {

		StringBuffer sb = new StringBuffer();
		TypedDescription[] pDocs = fDoc.getParams();
		
		if(pDocs.length == 0)
		{
			sb.append(ScriptDocHelper.createMethodSignatureString(identifier, fDoc, obj, true, addReturnTypes));
			if(fDoc.getDescription().trim() != "") //$NON-NLS-1$
			{
				sb.append("<br><br>" + fDoc.getDescription()); //$NON-NLS-1$
			}
		}
		else
		{
			sb.append(ScriptDocHelper.createMethodSignatureString(identifier, fDoc, obj, true, addReturnTypes));
			sb.append("<br><br>" + fDoc.getDescription());								 //$NON-NLS-1$
		}	
		
		UserAgent[] agents = fDoc.getUserAgents();
		if(agents.length > 0)
		{
			sb.append(Messages.ScriptDocHelper_Supported1);
			for(int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				sb.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if(i < agents.length - 1)
				{
					sb.append(", "); //$NON-NLS-1$
				}
			}
		}
		
		if(extended)
		{
			String[] examples = fDoc.getExamples();
			if(examples.length > 0) //$NON-NLS-1$
			{
				for (int i = 0; i < examples.length; i++)
				{
					String example = examples[i];
					if(!example.startsWith("<b>Example:")) //$NON-NLS-1$
					{
						example = "<b>Example:</b><br>" + example; //$NON-NLS-1$
					}
					sb.append("<br><br>" + example); //$NON-NLS-1$
				}
			}

			if(fDoc.getRemarks().trim() != "") //$NON-NLS-1$
			{
				sb.append("<br><br>" + fDoc.getRemarks()); //$NON-NLS-1$
			}
			
			if(fDoc.getAliases() != null && fDoc.getAliases().getTypes().length > 0)
			{
				sb.append("<br><br><b>Also known as:</b> " + StringUtils.join(", ", fDoc.getAliases().getTypes())); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		return sb.toString();
	}
	/**
	 * Generates the return string for the method documentation
	 * <b>methodName</b>(type argName, type argName) : return types
	 * @param identifier The string "name" of the item
	 * @param mDoc The method to document
	 * @param obj Object used to read actual params on a function
	 * @param asHTML Do we use HTML?
	 * @return string
	 */
	public static String createMethodSignatureString(String identifier, FunctionDocumentation mDoc, IObject obj, boolean asHTML)
	{
		return createMethodSignatureString( identifier, mDoc, obj, asHTML, true );
	}
	/**
	 * Generates the return string for the method documentation
	 * <b>methodName</b>(type argName, type argName) : return types
	 * @param identifier The string "name" of the item
	 * @param mDoc The method to document
	 * @param asHTML Do we use HTML?
	 * @param obj Object used to read actual params on a function
	 * @param addReturnTypes true if return type info should be added to the information
	 * @return string
	 */
	public static String createMethodSignatureString(String identifier, FunctionDocumentation mDoc, IObject obj, boolean asHTML, boolean addReturnTypes)
	{
		StringBuffer methodSignature = new StringBuffer();
		
//		if(mDoc.getIsStatic().equals("static"))
//			methodSignature.append(mDoc.getIsStatic() + " ");
			
		methodSignature.append("<b>" + identifier + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
	
		TypedDescription[] pDocs = mDoc.getParams();
		String[] objParams = null;
		if(obj != null && obj instanceof JSFunction)
		{
			objParams = ((JSFunction)obj).getParameterNames();
		}
		int objLen = objParams == null ? 0 : objParams.length;
		int docLen = pDocs == null ? 0 : pDocs.length;
		
		int maxLen = Math.max(objLen, docLen);

		if(maxLen > 0)
		{
			methodSignature.append("("); //$NON-NLS-1$
		}

		for(int k = 0; k < maxLen; k++)
		{
			String docName = (k < docLen) ?  pDocs[k].getName() : ""; //$NON-NLS-1$
			String docCmpr = docName.replaceAll("\\[(.*)\\]", "$1"); //$NON-NLS-1$ //$NON-NLS-2$
			String objName = (k < objLen) ?  objParams[k] : ""; //$NON-NLS-1$
			String name = (objName.equals("")) ? docName : objName; //$NON-NLS-1$
			name = (docCmpr.equals(name)) ? docName : name;
			String types = ""; //$NON-NLS-1$
			if(k < docLen) 
			{
				types = StringUtils.join( "|" , pDocs[k].getTypes() ) ; //$NON-NLS-1$
			}
			
			if(!types.equals("")) //$NON-NLS-1$
			{
				methodSignature.append("<b>" + name + "</b>: " + types); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				methodSignature.append("<b>" + name + "</b>: Object"); //$NON-NLS-1$ //$NON-NLS-2$
			}
				
			if(k < maxLen - 1)
			{
				methodSignature.append(", "); //$NON-NLS-1$
			}
		}
		
		if(maxLen > 0)
		{
			methodSignature.append(")"); //$NON-NLS-1$
		}
		
		if(addReturnTypes)
		{
			if(mDoc.getReturn().getTypes().length > 0)
			{
				methodSignature.append(" : " + StringUtils.join(", ", mDoc.getReturn().getTypes() )); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else 
			{
				methodSignature.append(Messages.ScriptDocHelper_None1);
			}
		}
		
		if(!asHTML)
		{
			return StringUtils.stripHTML(methodSignature.toString());
		}
		else
		{
			return methodSignature.toString();
		}
	}

	/**
	 * 
	 * @param offsetMapper 
	 * @param lexeme
	 * @param extended Do we show "extended" information?
	 * @return Returns
	 */
	 
	public static String getInformationForLexeme(JSOffsetMapper offsetMapper, Lexeme lexeme, boolean extended) 
	{
//		String result = "";
//		
//		/*
//		if(jsFileEnvironment.getJSContextAwareness().isEnvironmentLoaderEnabled())
//		{
			return getInformationForLexemeSimple(offsetMapper, lexeme, extended);
//		}
//		*/
//
//		if(lexeme.getToken().getCategoryIndex() == JSTokenCategories.KEYWORD)
//			return " The javascript keyword <b>" + lexeme.getText() + "</b>. ";
//		
//		if(lexeme.getToken().getCategoryIndex() == JSTokenCategories.LITERAL)
//			return " The javascript literal <b>" + lexeme.getText() + "</b>. ";
//		
//		if(lexeme.getToken().getCategoryIndex() != JSTokenCategories.IDENTIFIER)
//			return result;
//		
//		// find docs if there are any
//		IDocumentation doc = null;
//
//		//int offset = lexeme.getRootCommandNode().getEndingOffset();
//		// use offset by semicolon to make this workable without commandnodes
//		int offset = jsFileEnvironment.getFilter().getSource().indexOf(";", lexeme.offset);
//		if(offset == -1) offset = lexeme.getEndingOffset();
//
//		// find if this is a property that is doc'd - if so, just use those docs
//		// (property docs are attached to the property object, not the IObject instances).
//		int curIndex = offsetMapper.getLexemeIndexFromDocumentOffset(lexeme.offset + lexeme.length);		
//		
//		IScope scope = lexeme.getCommandNode().getParentScope();
//		if (scope == null) scope = (IScope)jsFileEnvironment.getGlobal();	
//
//		IScope scope = jsFileEnvironment.getScope(lexeme,  (IScope)jsFileEnvironment.getGlobal());
//		
//		Property prop = JSFileEnvironment.lookupTypeFromNameHash(fullName, scope, offset, jsFileEnvironment);		
//		if(prop != null)
//		{
//			doc = prop.getDocumentation();
//			if(doc != null && doc instanceof PropertyDocumentation)
//			{
//				return createPropertyDocumentationHTML(lexeme.getText(), (PropertyDocumentation)doc, true);
//			}
//		}
//
//		//int offset = lexeme.offset;
//		CommandNode cmdNode = lexeme.getCommandNode();
//		CommandNode parent = cmdNode.getParentNode();
//		boolean isParameter = false;
//		if(cmdNode instanceof IdentifierNode)
//		{
//			if(parent instanceof GetPropertyNode)
//			{
//				if( ((GetPropertyNode)parent).getIdentifier() == cmdNode )
//					cmdNode = parent;
//			}
//			else if(parent instanceof FunctionNode)
//			{
//				cmdNode = parent;
//			}
//			else if(parent instanceof ParametersNode)
//			{
//				cmdNode = parent.getParentNode();
//				isParameter = true;
//			}
//		}
//		// end special cases TODO: move these to method when we've found them all
//		Environment env = jsFileEnvironment.getEnvironment().environment;
//		IObject objectNode = cmdNode.getInstance(env, jsFileEnvironment.getFileIndex(), offset);
//		
// 		if(objectNode != null)
//		{
//			doc = objectNode.getDocumentation();
//
//			if(	doc == null &&
//				objectNode instanceof ObjectBase && 
//				((ObjectBase)objectNode).getRange() instanceof ConstructNode )
//			{
//				ConstructNode cNode = (ConstructNode)((ObjectBase)objectNode).getRange(); 
//				IObject cObj = cNode.getInstance(env, jsFileEnvironment.getFileIndex(), offset);
//				doc = objectNode.getDocumentation();
//			} 
//			
//			if(doc != null)
//			{
//				if(isParameter)
//				{
//					result = createParameterDocumentationHTML(lexeme.getText(), (FunctionDocumentation)doc);
//				}
//				else if(doc instanceof FunctionDocumentation)
//					result = createMethodDocumentationHTML(lexeme.getText(), (FunctionDocumentation)doc, objectNode);
//				else if (objectNode instanceof FunctionNode)
//				{
//					JSFileEnvironment functionFileEnvironment = jsFileEnvironment.getEnvironment().findFileEnvironment(cmdNode.getStartingLexeme(), jsFileEnvironment);
//					result = createFunctionDocs((FunctionNode)objectNode, functionFileEnvironment);
//				}
//			}else
//			{
//				if(objectNode instanceof IFunction)
//				{
//					IFunction fn = (IFunction)objectNode;
//					result = "function(";
//					String comma = "";
//					String[] params = fn.getParameterNames();
//					for (int i = 0; i < params.length; i++)
//					{
//						result += comma + params[i];
//						comma = ", ";
//					}
//					result += ")";
//				}
//				else
//				{
//					IObject type = objectNode.getPropertyValue("constructor", jsFileEnvironment.getFileIndex(), offset);
//					if(type != null)
//					{
//						IDocumentation typeDoc = type.getDocumentation();
//						if(typeDoc != null && typeDoc instanceof PropertyDocumentation)
//						{
//							//retval = typeDoc.
//							TypedDescription retval = ((PropertyDocumentation)typeDoc).getReturn();
//							if(retval.getTypes().length > 0)
//							{
//								result = retval.getTypes()[0];
//							}
//						}
//					}
//				}
//			}
//		}
//		return result;
	}

	/**
	 * The non-commandNode version of getting info by lexeme for hover etc.
	 * @param extended
	 * @return String
	 */
	private static String getInformationForLexemeSimple(JSOffsetMapper offsetMapper, Lexeme lexeme, boolean extended) 
	{
		String result = ""; //$NON-NLS-1$
		
		if(lexeme.getCategoryIndex() == TokenCategories.KEYWORD)
		{
			return Messages.ScriptDocHelper_JSKeyword + lexeme.getText() + "</b>. "; //$NON-NLS-1$
		}
		
		if(lexeme.getCategoryIndex() == TokenCategories.LITERAL)
		{
			return Messages.ScriptDocHelper_JSLiteral + lexeme.getText() + "</b>. "; //$NON-NLS-1$
		}
		
		if(lexeme.getCategoryIndex() != TokenCategories.IDENTIFIER)
		{
			return result;
		}
		
		IDocumentation doc = null;
		
		// get offset after current position so locally assigned objects get picked up.
		//int offset = lexeme.getEndingOffset();
		
		// TODO: getFilter() no longer exists
		//int offset = offsetMapper.getFilter().getSource().indexOf(";" + lexeme.offset);
		//if(offset == -1) offset = lexeme.getEndingOffset(); // guard for eof before ';'

		// get lexeme index
		int curIndex = offsetMapper.getLexemeIndexFromDocumentOffset(lexeme.offset + lexeme.length);
		// get full string name
		String fullName = JSOffsetMapper.getIdentName(curIndex, offsetMapper.getLexemeList());

		IScope scope = offsetMapper.getScope(lexeme,  offsetMapper.getGlobal());
		
		IObject obj = offsetMapper.lookupReturnTypeFromNameHash(fullName, scope, true);		
		if(obj != null)
		{
			doc = obj.getDocumentation();
//			// not sure why this is here, we no longer put docs on properties
//			if(doc == null)
//			{
//				Property prop = JSOffsetMapper.lookupTypeFromNameHash(fullName, scope, lexeme.offset, offsetMapper);	
//				if(prop != null)
//					doc = prop.getDocumentation();
//			}
			if(doc != null && doc instanceof FunctionDocumentation)
			{
				return createMethodDocumentationHTML(lexeme.getText(), (FunctionDocumentation)doc, obj, true, extended);
			}
			else if(doc != null && doc instanceof PropertyDocumentation)
			{
				return createPropertyDocumentationHTML(lexeme.getText(), (PropertyDocumentation)doc, true, true, extended);
			}
			else if(obj instanceof JSFunction)
			{
				// Create new function documentation object if we have no documentation otherwise
				doc = new FunctionDocumentation();
				doc.setName(fullName);
				return createMethodDocumentationHTML(lexeme.getText(), (FunctionDocumentation)doc, obj, true, extended);
			}
		}
		
		
		
		return result;
	}

//	private static String createParameterDocumentationHTML(String text, FunctionDocumentation fDoc)
//	{
//		String result = "<b>" + text + "</b> parameter: ";
//		TypedDescription[] params = fDoc.getParams();
//		for(int i = 0; i < params.length; i++)
//		{
//			if(params[i].getName().equals(text))
//			{
//				result += " " + params[i].getDescription();
//				break;
//			}
//		}
//		return result;
//	}

	/**
	 * Get the signature of the function as an unformatted string
	 * @param node
	 * @param list
	 * @return String
	 */
//	private static String getFunctionSignature(FunctionNode node, LexemeList list) {
//		StringBuffer sb = new StringBuffer();		
//		int startIndex = list.getLexemeIndex(node.getStartingOffset());
//		
//		//add function
//		sb.append(list.get(startIndex).getText());
//		if(node.hasIdentifier())
//		{
//			//add function name
//			sb.append(" ");
//			sb.append(node.getIdentifier().getName());
//		}
//		sb.append("(");
//		if(node.hasParameters())
//		{
//			ParametersNode params = node.getParameters();
//			sb.append(StringUtils.join(", ", params.getParameterNames()));
//		}		
//		sb.append(")");
//		
//		return sb.toString();
//	}

//	private static String createFunctionDocs(FunctionNode functionNode, JSFileEnvironment fileEnvironment) {
//		String result;
//		result = "<b>" + getFunctionSignature(functionNode, fileEnvironment.getLexemeList()) + "</b>";
//		if(functionNode.hasDocumentation()){
//			String description = functionNode.getDocumentation().getDescription();
//			if(description != null)
//				result += "<p><p>" + description + "</p>";
//		}
//		return result;
//	}

	/**
	 * Generates the return string for the property documentation
	 * <b>propertyName</b>(type argName, type argName) : return types
	 * @param identifier The string "name" of the item
	 * @param pDoc The property to document
	 * @param asHTML Do we use HTML?
	 * @return String
	 */
	public static String createPropertyDocumentationHTML(String identifier, PropertyDocumentation pDoc, boolean asHTML)
	{
		return createPropertyDocumentationHTML(identifier, pDoc, asHTML, true, false);
	}
	/**
	 * Generates the return string for the property documentation
	 * <b>propertyName</b>(type argName, type argName) : return types
	 * @param identifier The string "name" of the item
	 * @param pDoc The property to document
	 * @param asHTML Do we use HTML?
	 * @param addReturnTypes true if return type info should be added to the information
	 * @param extended 
	 * @return String
	 */
	public static String createPropertyDocumentationHTML(String identifier, PropertyDocumentation pDoc, boolean asHTML, boolean addReturnTypes, boolean extended)
	{
		StringBuffer propertySignature = new StringBuffer();

//		if(pDoc.getScope().equals("static"))
//			propertySignature.append(pDoc.getScope() + " ");
		
		propertySignature.append("<b>" + identifier + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(addReturnTypes)
		{
			if(pDoc.getReturn().getTypes().length > 0)			
			{
				propertySignature.append(" : " + StringUtils.join(", ", pDoc.getReturn().getTypes())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				propertySignature.append(Messages.ScriptDocHelper_None2);
			}
		}

		propertySignature.append("<br><br>" + pDoc.getDescription() + "<br>"); //$NON-NLS-1$ //$NON-NLS-2$

		UserAgent[] agents = pDoc.getUserAgents();
		if(agents.length > 0)
		{
			propertySignature.append(Messages.ScriptDocHelper_Supported2);
			for(int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				propertySignature.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if(i < agents.length - 1)
				{
					propertySignature.append(", "); //$NON-NLS-1$
				}
			}
		}
		
		if(extended)
		{
			String[] examples = pDoc.getExamples();
			if(examples.length > 0) //$NON-NLS-1$
			{
				for (int i = 0; i < examples.length; i++)
				{
					String example = examples[i];
					if(!example.startsWith("<b>Example:")) //$NON-NLS-1$
					{
						example = "<b>Example:</b><br>" + example; //$NON-NLS-1$
					}
					propertySignature.append("<br><br>" + example + "<br>"); //$NON-NLS-1$ //$NON-NLS-2$					
				}
			}

			if(pDoc.getRemarks().trim() != "") //$NON-NLS-1$
			{
				propertySignature.append("<br><br>" + pDoc.getRemarks() + "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		String propString = propertySignature.toString();

		if(asHTML)
		{
			return propString;
		}
		else
		{
			return StringUtils.stripHTML(propString);
		}
	}
	/**
	 * Generates the return string for the property documentation withou
	 * <b>propertyName</b>(type argName, type argName) : return types
	 * @param identifier The string "name" of the item
	 * @param pDoc The property to document
	 * @param asHTML Do we use HTML?
	 * @return String
	 */
	public static String createPropertyDocumentationHTMLSimple(String identifier, PropertyDocumentation pDoc, boolean asHTML)
	{
		StringBuffer propertySignature = new StringBuffer();
		
		propertySignature.append("<b>" + identifier + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(pDoc.getReturn().getTypes().length > 0)			
		{
			propertySignature.append(" : " + StringUtils.join(", ", pDoc.getReturn().getTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		propertySignature.append("<br><br>" + pDoc.getDescription() + "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		UserAgent[] agents = pDoc.getUserAgents();
		if(agents.length > 0)
		{
			propertySignature.append(Messages.ScriptDocHelper_Supported3);
			for(int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				propertySignature.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if(i < agents.length - 1)
				{
					propertySignature.append(", "); //$NON-NLS-1$
				}
			}
		}

		String propString = propertySignature.toString();

		if(asHTML)
		{
			return propString;
		}
		else
		{
			return StringUtils.stripHTML(propString);
		}
	}
	/**
	 * Generates the return string for the a documentation type, if there is no other type available
	 * @param identifier The string "name" of the item
	 * @param pDoc The item to document
	 * @param asHTML Do we use HTML?
	 * @return  return string for the a documentation type, if there is no other type available
	 */
	public static String createGenericDocumentationHTML(String identifier, IDocumentation pDoc, boolean asHTML)
	{
		StringBuffer propertySignature = new StringBuffer();
		
		propertySignature.append("<b>" + identifier + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		propertySignature.append(" : " + propertySignature.append(pDoc.getClass().getName())); //$NON-NLS-1$
		
		String propString = propertySignature.toString();
		
		if(asHTML)
		{
			return propString;
		}
		else
		{
			return StringUtils.stripHTML(propString);
		}
	}
	
	/**
	 * Creates a formatted documentation string for the arg insight parameter listing 
	 * @param parameterDocs 
	 * @param obj 
	 * @return The argument insight with carriage reutrns between lines
	 */
	public static String createParameterDocumentationList(TypedDescription[] parameterDocs, IObject obj)
	{
		StringBuffer paramText = new StringBuffer();
		String[] parameterNames = null;
		if(obj != null && obj instanceof JSFunction)
		{
			parameterNames = ((JSFunction)obj).getParameterNames();
		}
		
		if(containsNonEmptyDescription(parameterDocs) == false)
		{
			return null;
		}
				
		// sort by name of actually call site order, if available
		if(parameterNames != null)
		{				
			for(int i = 0; i < parameterNames.length; i++)
			{
				String parameterName = parameterNames[i];
				TypedDescription curResult = findMatchingParameterDocumentation(parameterName, parameterDocs);
				paramText.append(createParameterDocumentation(curResult));
				
				if(i < parameterDocs.length - 1)
				{
					paramText.append(DEFAULT_DELIMITER);
				}
			}
		}
		else // call site function args not available (as in core objects) so use docs only
		{
			for(int i = 0; i < parameterDocs.length; i++)
			{
				TypedDescription parameter = parameterDocs[i];
				paramText.append(createParameterDocumentation(parameter));
								
				if(i < parameterDocs.length - 1)
				{
					paramText.append(DEFAULT_DELIMITER);
				}
			}
		}
		
		return wrapString(paramText.toString(), 420, UnifiedConfiguration.getNewlineString());
	}

	/**
	 * 
	 * @param parameterDoc
	 * @param showDescriptions
	 * @return String
	 */
	private static String createParameterDocumentation(TypedDescription parameterDoc) {
				
		String paramInfo = ""; //$NON-NLS-1$
		String name = ""; //$NON-NLS-1$
		if(parameterDoc != null)
		{
			paramInfo = wrapString(StringUtils.formatAsPlainText(parameterDoc.getDescription()), 370, UnifiedConfiguration.getNewlineString() + "\t"); //$NON-NLS-1$
			name = parameterDoc.getName();
		}

		return name + ":" + UnifiedConfiguration.getNewlineString() + "\t"  + paramInfo; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Find the parameter documentation matching the name passed in. Currently, it
	 * correctly strips any [] from the name to resolve conficts with optional named characters
	 * @param parameterName
	 * @param parameterDocs
	 * @return TypedDescription
	 */
	private static TypedDescription findMatchingParameterDocumentation(String parameterName, TypedDescription[] parameterDocs) {
		
		TypedDescription curResult = null;
		for (int j = 0; j < parameterDocs.length; j++)
		{
			TypedDescription param = parameterDocs[j];
			String name = StringUtils.trimBrackets(param.getName().toUpperCase());
			if(name.equals(parameterName.toUpperCase()))
			{
				curResult = param;
				break;
			}
			else if(j == parameterDocs.length - 1 && name.equals("...")) //$NON-NLS-1$
			{
				curResult = param;
				break;
			}
			
		}
		return curResult;
	}
	
	/**
	 * Do any aprameters contain a valid, non-null description
	 * @param params The list of parameters
	 * @return True if yes, false if no
	 */
	private static boolean containsNonEmptyDescription(TypedDescription[] params) {
		
		boolean foundDescription = false;
		for (int j = 0; j < params.length; j++)
		{
			TypedDescription param = params[j];
			if(param.getDescription() != null && !"".equals(param.getDescription())) //$NON-NLS-1$
			{
				foundDescription = true;
			}
		}
		
		return foundDescription;

	}
	private static String wrapString(String s, int maxWidth, String delimiter)
	{
		StringReader sr = new StringReader(s);
		GC gc= new GC(Display.getCurrent());
		String result = ""; //$NON-NLS-1$
		com.aptana.ide.editors.unified.utils.LineBreakingReader r = new LineBreakingReader(sr, gc, maxWidth);
		
		try
		{
			String line = r.readLine();
			while(line != null)
			{
				result += line;
				line = r.readLine();
				if(line != null)
				{
					result += delimiter;
				}
			}
		}
		catch (IOException e)
		{
		}
		
		gc.dispose();
		
		return result;
	}
}
