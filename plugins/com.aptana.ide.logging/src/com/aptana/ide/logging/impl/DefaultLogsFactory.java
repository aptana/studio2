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
package com.aptana.ide.logging.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.logging.DefaultLogInfo;
import com.aptana.ide.logging.IDefaultLogProvider;
import com.aptana.ide.logging.LoggingPlugin;

/**
 * Factory that collects default log files info.
 * @author Denis Denisenko
 */
public class DefaultLogsFactory
{
    /**
     * Default log extension point ID.
     */
    public static final String DEFAULT_LOG_POINT_ID = "com.aptana.ide.logging.defaultlog"; //$NON-NLS-1$
    
    /**
     * Log element name.
     */
    public static final String LOG_ELEMENT = "log"; //$NON-NLS-1$
    
    /**
     * Provider attribute name.
     */
    public static final String PROVIDER_ATTRIBUTE = "provider"; //$NON-NLS-1$
       
    /**
     * Gets default log URIs
     * @return default log URIs
     */
    public static List<DefaultLogInfo> getLogURIs()
    {
        List<DefaultLogInfo> result = new ArrayList<DefaultLogInfo>();
        
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint ep = registry.getExtensionPoint(DEFAULT_LOG_POINT_ID);

        if (ep != null)
        {
            IExtension[] extensions = ep.getExtensions();

            for (int i = 0; i < extensions.length; i++)
            {
                IExtension extension = extensions[i];
                IConfigurationElement[] elements = extension.getConfigurationElements();

                for (int j = 0; j < elements.length; j++)
                {
                    IConfigurationElement element = elements[j];
                    String elementName = element.getName();

                    if (elementName.equals(LOG_ELEMENT))
                    {
                        IDefaultLogProvider provider;
                        try
                        {
                            provider = (IDefaultLogProvider) element.createExecutableExtension(PROVIDER_ATTRIBUTE);
                            if (provider == null)
                            {
                                continue;
                            }
                            
                            result.addAll(provider.getLogs());
                        } 
                        catch (Throwable e)
                        {
                            IdeLog.logError(LoggingPlugin.getDefault(), 
                                    Messages.DefaultLogsFactory_ERR_LoadProvider, e);
                            continue;
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * DefaultLogsFactory private constructor.
     */
    private DefaultLogsFactory()
    {
    }
}
