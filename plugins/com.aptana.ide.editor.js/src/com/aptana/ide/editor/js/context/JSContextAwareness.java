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
package com.aptana.ide.editor.js.context;

import java.util.Hashtable;
import java.util.Stack;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.context.ContextItem;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

// used by outliner

/**
 * @author Paul Colton
 */
public class JSContextAwareness implements IContextAwareness
{
	//private UnifiedEditor editor = null;
	private ContextItem fileContext = null;
	
	/**
	 * JSContextAwareness
	 * 
	 * @param editor
	 */
	public JSContextAwareness(IUnifiedEditor editor)
	{
		//this.editor = editor;
		this.fileContext = new ContextItem("global"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.unified.context.IContextAwareness#update(com.aptana.ide.editors.unified.IFileService)
	 */
	public void update(IFileService context)
	{
		int functionDepth = 0;
		boolean inFunction = false;
		Hashtable funcsHash = new Hashtable();	
		Stack functionStack = new Stack();
		ContextItem currentFunction = null;
		
		fileContext.clearAll();
		
		// Get lexemes for the current doc
		LexemeList lexemeList = context.getLexemeList(); //editor.getLanguageService().getLexemeList();
		if(lexemeList == null)
		{
			return;
		}
		
		JSLexemeUtils utils = new JSLexemeUtils(lexemeList);
		
		int lexemeListSize = lexemeList.size();
	
		// Search for function definitions
		for(int lexc = 0; lexc < lexemeListSize; lexc++)
		{
			Lexeme lexeme = lexemeList.get(lexc);

			if (!inFunction && lexeme.typeIndex == JSTokenTypes.VAR)
			{
				lexeme = utils.getNextIdentifier(lexc+1);
				
				if(lexeme == null) 
				{
					continue;
				}

				//int docOffset = utils.findDocOffset(lexc);
				
				String doc = null;
				
				IFileLanguageService langService = context.getLanguageService(lexeme.getToken().getLanguage());
				if(langService != null)
				{
					doc = langService.getDocumentationFromLexeme(lexeme);
				}
					
//				if(docOffset != -1)
//					doc = utils.getFunctionDocumentation(docOffset);

				// Get the name of this field
				String name = lexeme.getText();

				// Set the fully qualified name of the field so there aren't any name collisions
//				if(currentFunction != null)
//				{
//					for(int i=0;i<functionStack.size();i++)
//					{
//						name = ((JSContextItem)functionStack.elementAt(i)).getName() + "." + name;
//					}
//				}

				// Now find the type, i.e. var a = new Foo();
				//String lexemeType = fileEnvironment.getTypeAfterIdentifier(fileEnvironment.getLexemeFloorIndex(lexeme.offset));
				
				if(funcsHash.get(name) != null)
				{
					JSContextItem contentElement = (JSContextItem) funcsHash.get(name);
					contentElement.setName(lexeme.getText());
					contentElement.setOffset(lexeme.offset);
					contentElement.length = lexeme.length;
					//contentElement.type = lexemeType;
					contentElement.doc = doc;
				}
				else
				{
					JSContextItem contentElement = new JSFieldContextItem(lexeme.getText());
					//contentElement.type = lexemeType;
					contentElement.doc = doc;
					contentElement.setOffset(lexeme.offset);
					contentElement.length = lexeme.length;
					funcsHash.put(name, contentElement);
					if(currentFunction == null)
					{
						this.fileContext.addItem(contentElement);
					}
					else
					{
						currentFunction.addItem(contentElement);
					}
				}
			}
			
			else if (lexeme.typeIndex == JSTokenTypes.FUNCTION)
			{
				inFunction = true;
				
				JSFunctionInfo fi = utils.getFunctionInfo(lexc);

				if(fi == null)
				{
					continue;
				}
				
				String doc = null;
				
				if(fi.nameOffset != -1)
				{
					IFileLanguageService langService = context.getLanguageService(lexeme.getToken().getLanguage());
					if(langService != null)
					{
						LexemeList ll = langService.getFileContext().getLexemeList();
						if(ll != null)
						{
							Lexeme l = ll.getLexemeFromOffset(fi.nameOffset);
							doc = langService.getDocumentationFromLexeme(l);
						}
					}
				}
				
//				if(fi.docOffset != -1)
//					doc = utils.getFunctionDocumentation(fi.docOffset);
				
				JSContextItem contentElement = new JSFunctionContextItem(fi.name);
				contentElement.values.put("params", fi.params); //$NON-NLS-1$
				contentElement.offset = fi.offset;
				contentElement.length = fi.length;
				contentElement.doc = doc;
				
				currentFunction = contentElement;

				// Handle various cases:
				//
				// a
				// a.b
				// a.prototype.b
				// a.b.prototype.c
				//
				if(fi.name.indexOf(".") == -1) //$NON-NLS-1$
				{
					contentElement.setName(fi.name);

					if(funcsHash.get(fi.name) != null)
					{
						ContextItem ce = (ContextItem) funcsHash.get(fi.name);
						ce.setOffset(contentElement.getOffset());
					}
					else
					{
						funcsHash.put(fi.name, contentElement);
						this.fileContext.addItem(contentElement);
					}
				}
				else
				{
					String[] parts = fi.name.split("\\."); //$NON-NLS-1$
					String funcPrefix = ""; //$NON-NLS-1$
					ContextItem parentItem = null;

					for(int i=0;i<parts.length-1;i++)
					{
						funcPrefix += parts[i];
						
						if(funcsHash.get(funcPrefix) == null)
						{
							ContextItem c = null;

							if(parts[i].equals("prototype")) //$NON-NLS-1$
							{
								c = new JSPrototypeContextItem("prototype"); //$NON-NLS-1$
							}
							else
							{
								c = new JSFieldContextItem();
							}
							
							// If this is a root element, it needs to be added to the tree
							if(i == 0)
							{
								this.fileContext.addItem(c);
							}

							// Set the name of offset position
							c.setName(parts[i]);
							c.setOffset(contentElement.getOffset());

							funcsHash.put(funcPrefix, c);

							//currentFunction = c;  //TODO: Paul, check with Spike: this confused the endScopeOffset in cases like foo.bar = function(){ ... }
							
							if(parentItem != null)
							{
								parentItem.addItem(c);
							}
							
							parentItem = c;
						}
						else
						{
							parentItem = (ContextItem) funcsHash.get(funcPrefix);
						}
						
						funcPrefix += "."; //$NON-NLS-1$
					}
					
					if(funcPrefix.endsWith(".")) //$NON-NLS-1$
					{
						funcPrefix = funcPrefix.substring(0, funcPrefix.length()-1);
					}

					String baseName = parts[parts.length-1];
					contentElement.setName(baseName);
					
					funcsHash.put(funcPrefix + "." + baseName, contentElement); //$NON-NLS-1$
					ContextItem jsfc = (ContextItem) funcsHash.get(funcPrefix);
					jsfc.addItem(contentElement);
				}
			}
			
			else if (inFunction && lexeme.typeIndex == JSTokenTypes.LCURLY)
			{
				functionDepth++;
				functionStack.push(currentFunction);
			}

			else if (inFunction && lexeme.typeIndex == JSTokenTypes.RCURLY)
			{
				functionDepth--;
				
				if(functionStack.size() > 0)
				{
					currentFunction = (ContextItem) functionStack.pop();
				}

				if(functionDepth == 0)
				{
					inFunction = false;
					currentFunction = null;
				}
				else
				{
					if(functionStack.size() > 0)
					{
						currentFunction = (ContextItem) functionStack.peek();
					}
				}					
			}
		}
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.context.IContextAwareness#getFileContext()
	 */
	public ContextItem getFileContext()
	{
		return this.fileContext;
	}

	private static JSContextAwareness instance;
	
	/**
	 * getInstance
	 * @param editor
	 * @return JSContextAwareness
	 */
	public static JSContextAwareness getInstance(IUnifiedEditor editor){
		if(instance == null)
		{
			instance = new JSContextAwareness(editor);
		}
		return instance;
	}
}
