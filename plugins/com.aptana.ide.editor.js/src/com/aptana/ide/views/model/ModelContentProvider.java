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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.views.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.Reference;

/**
 * @author Kevin Lindsey
 */
public class ModelContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_OBJECTS = new Object[0];
	private static final Comparator<Reference> REF_COMPARATOR = new Comparator<Reference>()
	{
		public int compare(Reference o1, Reference o2)
		{
			return o1.getPropertyName().compareTo(o2.getPropertyName());
		}
	};
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = NO_OBJECTS;
		
		if (parentElement instanceof Reference)
		{
			List<Reference> references = new ArrayList<Reference>();
			
			Reference reference = (Reference) parentElement;
			IObject value = reference.getValue(Integer.MAX_VALUE, Integer.MAX_VALUE);
			String[] names = value.getLocalPropertyNames();
			IObject proto = value.getPrototype();
			
			if (proto != null)
			{
				// create placeholder reference to get to private proto
				IObject temp = new JSObject();
				temp.putPropertyValue(Messages.getString("ModelContentProvider.0"), proto, Integer.MAX_VALUE); //$NON-NLS-1$
				
				references.add(new Reference(temp, "[[proto]]")); //$NON-NLS-1$
			}
			
			if (value instanceof IFunction)
			{
				IScope scope = ((IFunction) value).getBodyScope();
				
				if (scope != null)
				{
					// create placeholder reference to get to private proto
					IObject temp = new JSObject();
					temp.putPropertyValue("[[scope]]", scope, Integer.MAX_VALUE); //$NON-NLS-1$
					
					references.add(new Reference(temp, "[[scope]]")); //$NON-NLS-1$
				}
			}
				
			for (int i = 0; i < names.length; i++)
			{
				references.add(new Reference(value, names[i]));
			}
			
			result = references.toArray(new Reference[references.size()]);
			Arrays.sort((Reference[]) result, REF_COMPARATOR);
		}
		
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;
		
		if (element instanceof Reference)
		{
			Reference reference = (Reference) element;
			IObject value = reference.getValue(Integer.MAX_VALUE, Integer.MAX_VALUE);
			String[] names = value.getPropertyNames(true);
			
			result = (names.length > 0);
		}
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] result = NO_OBJECTS;
		
		if (inputElement instanceof Environment)
		{
			Environment env = (Environment) inputElement;
			JSScope global = env.getGlobal();
			String[] names = global.getVariableNames();
			
			result = new Reference[names.length];
			
			for (int i = 0; i < names.length; i++)
			{
				result[i] = new Reference(global, names[i]);
			}
		}
		
		Arrays.sort((Reference[]) result, REF_COMPARATOR);
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub
	}
}
