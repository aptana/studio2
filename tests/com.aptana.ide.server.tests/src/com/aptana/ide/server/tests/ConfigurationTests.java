/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.server.tests;

import java.util.Arrays;

import junit.framework.TestCase;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.core.impl.PreferencesConfiguration;

/**
 * @author Pavel Petrochenko
 *
 */
public class ConfigurationTests extends TestCase
{

	/**
	 * 
	 */
	public void test0(){
		Configuration cf=new Configuration();
		IAbstractConfiguration createSubConfiguration = cf.createSubConfiguration("P.P.A"); //$NON-NLS-1$
		createSubConfiguration.setIntAttribute("weight", 5); //$NON-NLS-1$
		String[] propertyNames = createSubConfiguration.propertyNames();
		assertEquals(propertyNames.length,1);
		assertEquals(propertyNames[0],"weight");		 //$NON-NLS-1$
		assertEquals(createSubConfiguration.getStringAttribute("weight"),"5"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * 
	 */
	public void test1(){
		PreferencesConfiguration cf=new PreferencesConfiguration(ServerCore.getDefault().getPluginPreferences(),"test"); //$NON-NLS-1$
		IAbstractConfiguration createSubConfiguration = cf.createSubConfiguration("P.P.A"); //$NON-NLS-1$
		createSubConfiguration.setIntAttribute("weight", 5); //$NON-NLS-1$
		String[] propertyNames = createSubConfiguration.propertyNames();
		assertEquals(propertyNames.length,1);
		assertEquals(propertyNames[0],"weight");		 //$NON-NLS-1$
		assertEquals(createSubConfiguration.getStringAttribute("weight"),"5"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * 
	 */
	public void test2(){
		PreferencesConfiguration cf=new PreferencesConfiguration(ServerCore.getDefault().getPluginPreferences(),"test"); //$NON-NLS-1$
		IAbstractConfiguration createSubConfiguration = cf.createSubConfiguration("P.P.A"); //$NON-NLS-1$
		String[] strings = new String[]{"A","B"}; //$NON-NLS-1$ //$NON-NLS-2$
		createSubConfiguration.setStringArrayAttribute("servers", strings); //$NON-NLS-1$
		String[] stringArrayAttribute = createSubConfiguration.getStringArrayAttribute("servers"); //$NON-NLS-1$
		assertTrue(Arrays.equals(strings,stringArrayAttribute));
	}
	
	/**
	 * 
	 */
	public void test3(){
		PreferencesConfiguration cf=new PreferencesConfiguration(ServerCore.getDefault().getPluginPreferences(),"test"); //$NON-NLS-1$
		IAbstractConfiguration createSubConfiguration = cf.createSubConfiguration("P.P.A"); //$NON-NLS-1$
		String[] strings = new String[]{};
		createSubConfiguration.setStringArrayAttribute("servers", strings); //$NON-NLS-1$
		String[] stringArrayAttribute = createSubConfiguration.getStringArrayAttribute("servers"); //$NON-NLS-1$
		assertTrue(Arrays.equals(strings,stringArrayAttribute));
	}
	
	/**
	 * 
	 */
	public void test4(){
		PreferencesConfiguration cf=new PreferencesConfiguration(ServerCore.getDefault().getPluginPreferences(),"test"); //$NON-NLS-1$
		IAbstractConfiguration createSubConfiguration = cf.createSubConfiguration("P.P.A"); //$NON-NLS-1$
		String[] strings = new String[]{"A"};		 //$NON-NLS-1$
		createSubConfiguration.setStringArrayAttribute("servers", strings); //$NON-NLS-1$
		String[] stringArrayAttribute = createSubConfiguration.getStringArrayAttribute("servers"); //$NON-NLS-1$
		assertTrue(Arrays.equals(strings,stringArrayAttribute));
	}
}
