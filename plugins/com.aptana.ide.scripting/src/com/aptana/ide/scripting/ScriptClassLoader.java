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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.osgi.framework.Bundle;

import com.aptana.ide.core.StringUtils;

/**
 * @author Kevin Lindsey based on code by Patrick Mueller
 */
public class ScriptClassLoader extends ClassLoader
{
	private ArrayList _bundles;

	/**
	 * ScriptClassLoader
	 */
	public ScriptClassLoader()
	{
		super(ScriptClassLoader.class.getClassLoader());

		this._bundles = new ArrayList();
	}

	/**
	 * addBundle
	 *
	 * @param bundle
	 */
	public void addBundle(Bundle bundle)
	{
		if (bundle == null)
		{
			throw new IllegalArgumentException(Messages.ScriptClassLoader_Bundle_Not_Defined);
		}
		
		if (this._bundles.contains(bundle) == false)
		{
			this._bundles.add(bundle);
		}
	}

	/**
	 * findClass
	 * 
	 * @param name
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	protected Class findClass(String name) throws ClassNotFoundException
	{
		Class result = this.loadClassFromBundles(name);

		if (result == null)
		{
			String message = StringUtils.format(Messages.ScriptClassLoader_Unable_To_Find_Class, name);
			
			throw new ClassNotFoundException(message);
		}

		return result;
	}
	
	/**
	 * findResource
	 * 
	 * @param name
	 * @return URL
	 */
	protected URL findResource(String name)
	{
		URL result = super.findResource(name);

		if (result == null)
		{
			Iterator iterator = this._bundles.iterator();
			
			while (iterator.hasNext())
			{
				Bundle bundle = (Bundle) iterator.next();
				
				result = bundle.getResource(name);

				if (result != null)
				{
					break;
				}
			}
		}

		return result;
	}

	/**
	 * findResources
	 * 
	 * @param name
	 * @return Enumeration
	 * @throws IOException
	 */
	protected Enumeration findResources(String name) throws IOException
	{
		Enumeration result = super.findResources(name);

		if (result == null)
		{
			Iterator iterator = this._bundles.iterator();
			
			while (iterator.hasNext())
			{
				Bundle bundle = (Bundle) iterator.next();
				
				result = bundle.getResources(name);

				if (result != null)
				{
					break;
				}
			}
		}

		if (result == null)
		{
			String message = StringUtils.format(Messages.ScriptClassLoader_Unable_To_Find_Resource, name);
			
			throw new IOException(message);
		}

		return result;
	}

	/**
	 * loadClass
	 * 
	 * @param name
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String name) throws ClassNotFoundException
	{
		Class result = super.loadClass(name);

		if (result == null)
		{
			result = this.loadClassFromBundles(name);
		}

		if (result == null)
		{
			String message = StringUtils.format(Messages.ScriptClassLoader_Unable_To_Load_Class, name);
			
			throw new ClassNotFoundException(message);
		}

		return result;
	}

	/**
	 * loadClass
	 * 
	 * @param name
	 * @param resolve
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		Class result = super.loadClass(name, resolve);

		if (result == null)
		{
			result = this.loadClassFromBundles(name);
		}

		if (result == null)
		{
			String message = StringUtils.format(Messages.ScriptClassLoader_Unable_To_Load_Class, name);
			
			throw new ClassNotFoundException(message);
		}

		return result;
	}

	/**
	 * loadClassFromBundles
	 * 
	 * @param name
	 * @return Class
	 */
	private Class loadClassFromBundles(String name)
	{
		Class result = null;
		Iterator iterator = this._bundles.iterator();

		while (iterator.hasNext())
		{
			Bundle bundle = (Bundle) iterator.next();

			try
			{
				result = bundle.loadClass(name);
	
				if (result != null)
				{
					break;
				}
			}
			catch (ClassNotFoundException e)
			{
				// do nothing
			}
		}
		
		return result;
	}
}
