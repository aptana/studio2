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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aptana.ide.editor.js.environment.LexemeConsumerHelper;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Robin Debreuil
 */
public class ScriptDocStore implements IDocumentationStore 
{
	private Map<Integer,IDocumentation> _scriptObjects;
	private IParseState _parseState;
	
	/**
	 * ScriptDocStore
	 * @param parseState 
	 */
	public ScriptDocStore(IParseState parseState)
	{
		this._parseState = parseState;
		this._scriptObjects = new HashMap<Integer,IDocumentation>();
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentationStore#clear()
	 */
	public void clear()
	{
		this._scriptObjects.clear();
		//idMap.clear();
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentationStore#addScriptDocObject(int, com.aptana.ide.lexer.Lexeme, com.aptana.ide.metadata.IDocumentation)
	 */
	public void addScriptDocObject(int offset, Lexeme lx, IDocumentation doc)
	{
		String uri = FileContextManager.getURIFromFileIndex(this._parseState.getFileIndex());
		
		// dups shouldn't happen as list is cleared each parse
		if (this._scriptObjects.containsKey(offset))
		{
			this._scriptObjects.remove(offset);
		}

		// deal with @id mapped scripts
		if (doc.getIDs().length > 0)
		{			 
			Map<String, IDocumentation> idMap = EnvironmentManager.getDocumentationIdMap();
			String[] ids = doc.getIDs();
			String id = ids[0]; // todo: use all ids
			
			if (idMap.containsKey(id))
			{
				IDocumentation oldDoc = idMap.get(id);
				
				mergeScriptDocs(id, oldDoc, doc);
				this._scriptObjects.put(offset, oldDoc);
				
				// add the location to the doc if it has an @id regardless, as we want to know all of these
				if ("".equals(uri) == false && oldDoc instanceof DocumentationBase) //$NON-NLS-1$
				{
					CodeLocation[] codeLoc = doc.getID(id);
					
					for (int i = 0; i < codeLoc.length; i++)
					{
						CodeLocation location = codeLoc[i];
						oldDoc.setID(id, location);						
					}
					//DocumentationBase db = (DocumentationBase) oldDoc;
					//CodeLocation codeLoc = new CodeLocation(fullPath, lx);
					//db.addIDLocation(codeLoc);
				}
			}
			else
			{
				idMap.put(id, doc);	
				
				Map refMap = EnvironmentManager.getDocumentationRefMap();
				
				this._scriptObjects.put(offset, doc);	
				
				Reference ref = LexemeConsumerHelper.addDocHolderToEnvironment(id, doc, this._parseState);
				
				if (ref != null)
				{
					refMap.put(id, ref);
				}
				
				// add the location to the doc if it has an @id regardless, as we want to know all of these
//				if(fullPath != "" && doc instanceof DocumentationBase)
//				{
//					DocumentationBase db = (DocumentationBase) doc;
//					CodeLocation codeLoc = new CodeLocation(fullPath, null, lx);
//					db.addIDLocation(codeLoc);
//				}
			}
		}
		else
		{		
			this._scriptObjects.put(offset, doc);
		}
	}
	/**
	 * @see com.aptana.ide.metadata.IDocumentationStore#getDocumentationFromOffset(int)
	 */
	public IDocumentation getDocumentationFromOffset(int offset)
	{
		return this._scriptObjects.get(offset);
	}
	
	/**
	 * Merges the second doc into the originalDoc (only if they have the same ID). 
	 * AddedDoc always takes precidence in case of conflict.
	 * @param originalDoc
	 * @param addedDoc
	 */
	private void mergeScriptDocs(String id, IDocumentation originalDoc, IDocumentation addedDoc)
	{
		String[] ids = addedDoc.getIDs();
		boolean hasMatch = false;
		
		for (int i = 0; i < ids.length; i++)
		{
			if (id.equals(ids[i]))
			{
				hasMatch = true;
				break;
			}			
		}
		
		if (!hasMatch)
		{
			return;
		}
		
		// default props
		
		if (!addedDoc.getAuthor().equals("")) //$NON-NLS-1$
		{
			originalDoc.setAuthor(addedDoc.getAuthor());
		}
		
		if (!addedDoc.getDescription().equals("")) //$NON-NLS-1$
		{
			originalDoc.setDescription(addedDoc.getDescription());
		}
		
		//if(originalDoc.getDocumentType() == IDocumentation.TYPE_FUNCTION) // default
		//	originalDoc.setDocumentType(addedDoc.getDocumentType());
		
		String[] examples = addedDoc.getExamples();
		
		if (examples.length > 0) //$NON-NLS-1$
		{
			for (int i = 0; i < examples.length; i++)
			{
				originalDoc.addExample(examples[i]);
			}
		}

		if (!addedDoc.getName().equals("")) //$NON-NLS-1$
		{
			originalDoc.setName(addedDoc.getName());
		}
		
		if (!addedDoc.getRemarks().equals("")) //$NON-NLS-1$
		{
			originalDoc.setRemarks(addedDoc.getRemarks());
		}
		
		if (!addedDoc.getUserAgent().equals("")) //$NON-NLS-1$
		{
			originalDoc.setUserAgent(addedDoc.getUserAgent());
		}
		
		if (!addedDoc.getVersion().equals("")) //$NON-NLS-1$
		{
			originalDoc.setVersion(addedDoc.getVersion());
		}
		
		if (originalDoc instanceof PropertyDocumentation && addedDoc instanceof PropertyDocumentation)
		{
			PropertyDocumentation doc = (PropertyDocumentation)originalDoc;
			PropertyDocumentation doc2 = (PropertyDocumentation)addedDoc;			
			
			if (!doc2.getDeprecatedDescription().equals("")) //$NON-NLS-1$
			{
				doc.setDeprecatedDescription(doc2.getDeprecatedDescription());
			}
			
			//if(doc2.getIsDeprecatedSet())
				doc.setIsDeprecated(doc2.getIsDeprecated());
			
			//if(doc2.getIsIgnoredSet())
				doc.setIsIgnored(doc2.getIsIgnored());
			
			//if(doc2.getIsInstanceSet())
				doc.setIsInstance(doc2.getIsInstance());
			
			//if(doc2.getIsInternalSet())
				doc.setIsInternal(doc2.getIsInternal());
			
			//if(doc2.getIsInvocationOnlySet())
				doc.setIsInvocationOnly(doc2.getIsInvocationOnly());
			
			//if(doc2.getIsNativeSet())
				doc.setIsNative(doc2.getIsNative());
			
			//if(doc2.getIsPrivateSet())
				doc.setIsPrivate(doc2.getIsPrivate());
			
			//if(doc2.getIsProtectedSet())
				doc.setIsProtected(doc2.getIsProtected());
			
			if (!doc2.getSince().equals("")) //$NON-NLS-1$
			{
				doc.setSince(doc2.getSince());
			}

			mergeTypedDescriptions(doc.getAliases(), doc2.getAliases());
			mergeTypedDescriptions(doc.getMemberOf(), doc2.getMemberOf());
			mergeTypedDescriptions(doc.getReturn(), doc2.getReturn());
			
		}
		
		if (originalDoc instanceof FunctionDocumentation && addedDoc instanceof FunctionDocumentation)
		{
			FunctionDocumentation doc = (FunctionDocumentation)originalDoc;		
			FunctionDocumentation doc2 = (FunctionDocumentation)addedDoc;		
			
			if (!doc2.getDeprecatedDescription().equals("")) //$NON-NLS-1$
			{
				doc.setClassDescription(doc2.getDeprecatedDescription());				
			}
			
			//if(doc2.getIsConstructorSet())
				doc.setIsConstructor(doc2.getIsConstructor());	
			
			//if(doc2.getIsMethodSet())
				doc.setIsMethod(doc2.getIsMethod());	

			if (doc2.getParams().length != 0)
			{
				// todo: may need to merge these based on names and actual function def
				doc.clearParams();
				
				TypedDescription[] params = doc2.getParams();
				
				for (int i = 0; i < params.length; i++)
				{
					TypedDescription description = params[i];
					
					doc.addParam(description);
					
				}			
			}
			
			if (doc2.getExceptions().length != 0)
			{
				TypedDescription[] exceptions = doc2.getExceptions();
				
				doc.clearExceptions();
				
				for (int i = 0; i < exceptions.length; i++)
				{
					TypedDescription description = exceptions[i];
					
					doc.addParam(description);
					
				}			
			}
			
			mergeTypedDescriptions(doc.getExtends(), doc2.getExtends());			
		}	
	}
	
	/**
	 * Merges the addedDesc into the dominatDesc parameter
	 * @param originalDesc
	 * @param addedDesc
	 */
	private void mergeTypedDescriptions(TypedDescription originalDesc, TypedDescription addedDesc)
	{
		if (!addedDesc.getName().equals("")) //$NON-NLS-1$
		{
			originalDesc.setName(addedDesc.getName());
		}
		
		if (!addedDesc.getDescription().equals("")) //$NON-NLS-1$
		{
			originalDesc.setDescription(addedDesc.getDescription()); 
		}

		if (addedDesc.getTypes().length != 0)
		{
			String[] types = addedDesc.getTypes();
			
			originalDesc.clearTypes();
			
			for (int i = 0; i < types.length; i++)
			{
				originalDesc.addType(types[i]);
			}
		}
		
		if (originalDesc.getDefaultValues().length == 0)
		{
			TypedDescription[] vals = addedDesc.getDefaultValues();
			
			originalDesc.clearDefaultValues();
			
			for (int i = 0; i < vals.length; i++)
			{
				originalDesc.addDefaultValue(vals[i]);
			}
		}
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentationStore#getDocumentationObjects()
	 */
	public IDocumentation[] getDocumentationObjects()
	{
		return this._scriptObjects.values().toArray(new IDocumentation[0]);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentationStore#getDocumentationOffsets()
	 */
	public int[] getDocumentationOffsets()
	{
		Set<Integer> docs = this._scriptObjects.keySet();
		int[] array = new int[docs.size()];
		int i = 0;
		
		for (Iterator<Integer> iter = docs.iterator(); iter.hasNext(); ) {
			Integer element = iter.next();
			
			array[i] = element.intValue();
			i++;
		}
		
		Arrays.sort(array);
		
		return array;
	}
}
