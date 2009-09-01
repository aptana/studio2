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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.IFileError;

/**
 * Virtual proxy class for Validator
 * 
 * @author Samir Joshi
 */
public class ValidatorRef implements IValidator
{
	private String mimeType;
	String name;
	private String className;
	private IValidator delegateInstance;
	private IConfigurationElement configElement;
	private String extenderNameSpace; //declaring plugin
	
	boolean instanciationError = false;
	
	/**
	 * 
	 * @return mime type for this validator
	 */
	public String getMimeType()
	{
		return mimeType;
	}
	/**
	 * 
	 * @param mimeType mime type for this validator
	 */
	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	/**
	 * 
	 * @return implementation class
	 */
	public String getClassName()
	{
		return className;
	}
	/**
	 * 
	 * @param className - class implementing this validator
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	/**
	 * This method will be used to lazily instantiate actual validator class
	 * @param path
	 * @param source
	 * @param sourceProvider
	 * @param collectErrors 
	 * @param collectWarnings 
	 * @param collectInfos 
	 * @return return parse errors and warnings
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider, 
								boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{	
		IFileError[] errors = null;

		if(delegateInstance == null && !instanciationError)
		{
			try
			{
				delegateInstance = (IValidator) configElement.createExecutableExtension("class"); //$NON-NLS-1$
			}
			catch (CoreException e)
			{
				instanciationError = true;
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ValidatorRef_InstantiationError,e);
			}
		}	
		
		if(instanciationError)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(),  Messages.ValidatorRef_NoValidatorInstantiated);
		}
		else
		{
			try
			{
				errors = delegateInstance.parseForErrors(path, source, sourceProvider, collectErrors, collectWarnings, collectInfos);
			}
			catch(Throwable th)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ValidatorRef_InvocationError, th);	
			}
		}

		return errors;
	}
	
	/**
	 * @param configElement
	 */
	public void setConfigElement(IConfigurationElement configElement)
	{
		this.configElement = configElement;
	}
	
	/**
	 * 
	 * @return Namespace of bundle that is extending the extension point
	 */
	public String getExtenderNameSpace()
	{
		return extenderNameSpace;
	}
	
	/**
	 * 
	 * @param extenderNameSpace Namespace of bundle that is extending the extension point
	 */
	public void setExtenderNameSpace(String extenderNameSpace)
	{
		this.extenderNameSpace = extenderNameSpace;
	}
	
	/**
	 * 
	 * @return descriptive name of the validator
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	

}
