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
package com.aptana.ide.editor.js.parsing;

import java.util.Iterator;
import java.util.Map;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeFactory;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.OrderedObject;
import com.aptana.ide.editor.js.runtime.OrderedObjectCollection;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParseState;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IRuntimeEnvironment;
import com.aptana.ide.parsing.ParseStateChild;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class JSParseState extends ParseStateChild
{
	/**
	 * Create a new instance of JSParseState
	 */
	public JSParseState()
	{
		super(JSMimeType.MimeType);
	}

	/**
	 * Create a new instance of JSParseState
	 * 
	 * @param parent
	 *            The parent IParseState
	 */
	public JSParseState(IParseState parent)
	{
		super(JSMimeType.MimeType,parent);
	}

	/**
	 * getDocumentationStore
	 * 
	 * @return IDocumentationStore
	 */
	public IDocumentationStore getDocumentationStore()
	{
		IParseState[] children = this.getChildren();
		IDocumentationStore result = null;
		
		if (children != null)
		{
			for (int i = 0; i < children.length; i++)
			{
				IParseState state = children[i];
				
				if (state instanceof ScriptDocParseState)
				{
					result = ((ScriptDocParseState) state).getDocumentationStore();
					break;
				}
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.ParseStateChild#createParseNodeFactory()
	 */
	protected IParseNodeFactory createParseNodeFactory()
	{
		return new JSParseNodeFactory(this); 
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#unloadFromEnvironment()
	 */
	public void unloadFromEnvironment()
	{
		IRuntimeEnvironment environment = JSLanguageEnvironment.getInstance().getRuntimeEnvironment();
		
		synchronized(environment)
		{
			Map<Object,Object> updatedProperties = this.getUpdatedProperties();
			Iterator<Object> props = updatedProperties.keySet().iterator();

			//if(updatedProperties.size() > 0)
			//	System.out.println("** removed: " + this.getFullPath());
			while (props.hasNext())
			{
				Property p = (Property) props.next();
				Reference reference = (Reference) updatedProperties.get(p);
				String propertyName = reference.getPropertyName();
				IObject parentObject = reference.getObjectBase();

				unsetPropertyValue(p, propertyName, parentObject, this.getFileIndex());
			}
			
			//scopeList.clear();
			updatedProperties.clear();
		}
	}
	
	/**
	 * unsetPropertyValue
	 *
	 * @param p
	 * @param propertyName
	 * @param parentObject
	 * @param fileIndex
	 */
	private void unsetPropertyValue(Property p, String propertyName, IObject parentObject, int fileIndex) 
	{
		if (p == null)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSParseState_NullPropertyPassedIn);
			return;
		}		
		if (propertyName == null)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSParseState_NullPropertyNamePassedIn);
		}		
		
		OrderedObjectCollection c = p.getAssignments();	
		
		for (int i = 0; i < c.size(); i++)
		{
			OrderedObject obj = c.get(i);
			
			if (obj == null)
			{
				IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(Messages.JSParseState_ObjectNullForProperty, propertyName));
				continue;
			}	
			
			if (obj.fileIndex == fileIndex)
			{
				IObject objObject = obj.object;
				
				if(objObject != null)
				{
					c.remove(obj.fileIndex, objObject.getStartingOffset());
				}
				else
				{
					IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(Messages.JSParseState_ObjObjectNullForProperty, propertyName));					
				}
				
				i--;
			}
		}
		
		if (parentObject == null)
		{
			IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(Messages.JSParseState_ParentObjectNullForProperty, propertyName));
			return;
		}
		
		if (!p.hasAssignments() && !p.isPermanent())
		{
			parentObject.deletePropertyName(propertyName);
		}
	}
}
