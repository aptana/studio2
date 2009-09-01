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
package com.aptana.ide.core.ui.views.browser;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class DefaultBrowserView extends BrowserView {

	private String description = null;
	
	/**
	 * @see BrowserView#getStartUrl()
	 */
	public String getStartUrl() {
		
		if(description == null)
		{
			return null;
		}
		
		try {
			
			if(description.startsWith("http")) //$NON-NLS-1$
			{
				return description;
			}
			
			int lastSlash = description.lastIndexOf('/');
			String parent = description.substring(0, lastSlash);
			String fileName = description.substring(lastSlash + 1, description.length());
			URI parentUri = new URI(parent);
			URL fileUrl = CoreUIUtils.getBundlePathAsFile(parentUri);
			if(fileUrl != null)
			{
				return fileUrl.toExternalForm() + fileName;
			}
			else
			{
				return null;
			}
		} catch (URISyntaxException e) {
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.DefaultBrowserView_ERR_UnableToResolveURL, description), e);
			return null;
		}
	}

	/**
	 * @see BrowserView#getPartId()
	 */
	public String getPartId() {
		return "com.aptana.ide.core.ui.views.browser"; //$NON-NLS-1$
	}
	
    /**
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        super.setInitializationData(cfig, propertyName, data);
        
        IConfigurationElement[] children = cfig.getChildren("description"); //$NON-NLS-1$
        if(children.length > 0)
        {
        	IConfigurationElement child = children[0];
        	description = child.getValue();
    	}
    }

}
