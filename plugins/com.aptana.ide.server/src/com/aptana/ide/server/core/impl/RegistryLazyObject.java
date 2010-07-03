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
package com.aptana.ide.server.core.impl;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.ServerCore;

/**
 * @author Pavel Petrochenko
 * 
 * object that is created basing on configuration element and has lazily created delegate
 */
public class RegistryLazyObject implements IAdaptable
{

	/**
	 * extension registry element related to this object
	 */
	protected final IConfigurationElement element;
	
	private Object object = null;
	
	/**
	 * @return should object be cached
	 */
	protected boolean shouldCache(){
		return true;
	}

	/**
	 * @param element
	 */
	public RegistryLazyObject(IConfigurationElement element)
	{
		this.element=element;
	}

	/**
	 * @return description 
	 */
	public String getDescription()
	{
		return element.getAttribute("description"); //$NON-NLS-1$
	}

	/**
	 * @return id 
	 */
	public String getId()
	{
		return element.getAttribute("id"); //$NON-NLS-1$
	}

	/**
	 * @return name 
	 */
	public String getName()
	{
		return element.getAttribute("name"); //$NON-NLS-1$
	}
	
	/**
	 * @return delegate object
	 */
	protected Object getObject(){
		if (object==null){
			try
			{
				Object createExecutableExtension = element.createExecutableExtension("class"); //$NON-NLS-1$
				if (!shouldCache()){
					return createExecutableExtension;
				}
				object=createExecutableExtension;
			}
			catch (CoreException e)
			{
				IdeLog.logError(ServerCore.getDefault(), "exception while instantiating registry object", e); //$NON-NLS-1$
			}
		}
		return object;		
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		final RegistryLazyObject other = (RegistryLazyObject) obj;		
		return this.getId().equals(other.getId());
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this.getObject(), adapter);
	}
	
	/**
	 * @param string
	 * @return boolean attribute of contribution element wich servers as a base for a given object
	 */
	protected boolean getBooleanAttribute(String string)
	{
		String attribute = this.element.getAttribute(string); 
		if (attribute==null||attribute.length()==0){
			return false;
		}
		return Boolean.parseBoolean(attribute);
	}
	
	/**
	 * @param value
	 * @param attr property name
	 * @return true if a given string property  has a given value
	 */
	protected boolean hasValue(String value, String attr)
	{
		String attribute = element.getAttribute(attr);
		if (attribute==null){
			return false;
		}
		String[] split = attribute.split(","); //$NON-NLS-1$
		return Arrays.asList(split).contains(value);
	}

}