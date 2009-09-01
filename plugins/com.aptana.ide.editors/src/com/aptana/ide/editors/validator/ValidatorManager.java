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
package com.aptana.ide.editors.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;



/**
 * Manages registered extensions of the extension point
 * com.aptana.ide.editors.validator
 * 
 * @author Samir Joshi
 *
 */
public class ValidatorManager
{
	private HashMap validators;
	private static ValidatorManager instance;
	
	/**
	 * Prevent uncontrolled creation of instances
	 */
	ValidatorManager()
	{
		initialize();
	}
	
	/**
	 *  
	 * @return the singleton instance
	 */
	public static ValidatorManager getInstance()
	{
		if (instance== null)
		{
			instance = new ValidatorManager();
		}
		return instance;
	}
	
	/**
	 * Get validators registered via 'validator' extension-point
	 * 
	 * @param mimeType
	 * @return Validators of given mime type
	 */
	public  ValidatorRef[] getValidators(String mimeType)
	{
		return (ValidatorRef[] ) validators.get(mimeType);
	}
	
	
	/**
	 * Get validators registered via 'validator' extension-point
	 * 
	 */
	private  void initialize()
	{
		validators = new HashMap();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if(registry == null)
		{
			//Cannot get extension registry--most likely running not as a plugin
			return;
		}

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint("com.aptana.ide.editors", "validator"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(extensionPoint == null)
		{
			//Cannot get com.aptana.ide.editors.validators extension point
			return;
		}
		
		IExtension[] extensions =  extensionPoint.getExtensions();				

		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++)
			{
				try
				{
					IConfigurationElement element = configElements[j];
					ValidatorRef validator = new ValidatorRef();
					validator.setMimeType(element.getAttribute("mimeType")); //$NON-NLS-1$
					validator.setClassName(element.getAttribute("class"));  //$NON-NLS-1$
					validator.setName(element.getAttribute("name")); //$NON-NLS-1$
					validator.setExtenderNameSpace( extensions[i].getNamespace());
					validator.setConfigElement(element);
					
					addValidator(validator);
				}
				catch (Exception ex)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ValidatorManager_ErrorRetrievingExtensionConfig, ex); 
				}
			}
		}
	
		rearrangeValidatorsAsArray();
	}
	
	/**
	 * Rearrange hashmap with elements being sorted arrays of ValidatorRef instead of ArrayList
	 *
	 */
	private void rearrangeValidatorsAsArray()
	{
		Set keys = validators.keySet();
		Iterator iterator = keys.iterator();
		
		while(iterator.hasNext())
		{
			String key = (String) iterator.next();
			
			ArrayList validatorList = (ArrayList) validators.get(key);

			if(validatorList != null)
			{
				ValidatorRef[] validatorArray = (ValidatorRef[] ) validatorList.toArray(new ValidatorRef[0]);
				validators.put(key,validatorArray );
			}
		}
		
	}

	/**
	 * 
	 * @param validator - Store this validator to answer later queries based on mime type
	 */
	private void addValidator(ValidatorRef validator)
	{
		ArrayList validatorList = (ArrayList) validators.get(validator.getMimeType());
		if(validatorList == null)
		{
			validatorList = new ArrayList();
			validators.put(validator.getMimeType(), validatorList);
		}
		validatorList.add(validator);
	}
	

}
