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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.aptana.ide.logging.ILogResource;
import com.aptana.ide.logging.ILogWatcher;
import com.aptana.ide.logging.IThreadProxy;

/**
 * Base class for {@link ILogResource} implementations.
 * @author Denis Denisenko
 */
public abstract class AbstractLogResource implements ILogResource
{
    /**
     * Resource URI.
     */
    private URI uri;
    
    /**
     * Watchers map: {@link LogWatcherConfiguration} -> {@link ILogWatcher}
     */
    private Map<LogWatcherConfiguration, ILogWatcher> watchers = 
    	new HashMap<LogWatcherConfiguration, ILogWatcher>();

    /**
     * Encoding
     */
    private Charset encoding;
    
    /**
     * AbstractLogResource constructor.
     * @param uri - resource URI.
     */
    public AbstractLogResource(URI uri)
    {
        this.uri = uri;
    }
    
    /**
      * {@inheritDoc}
      */
    public ILogWatcher getResourceWatcher(IThreadProxy threadProxy, 
            int maxBytesPerSecond, int maxNotificationSize,
            int checkWait, Charset encoding, int backlogRows)
    {
        LogWatcherConfiguration config = new LogWatcherConfiguration(threadProxy,
                maxBytesPerSecond, maxNotificationSize, checkWait, encoding, backlogRows);
        
        ILogWatcher watcher = (ILogWatcher) watchers.get(config);
        if (watcher != null)
        {
            return watcher;
        }
        
        watcher = LogWatcherFactory.createWatcher(this, config);
        watchers.put(config, watcher);
        
        return watcher;
    }
    
    /**
      * {@inheritDoc}
     */
    public void releaseWatcher(ILogWatcher watcher) throws IllegalArgumentException
    {
       if (watchers.containsValue(watcher))
       {
           throw new IllegalArgumentException("Watcher is not obtained from current resource"); //$NON-NLS-1$
       }
       
       //removing watcher
       Set<Entry<LogWatcherConfiguration, ILogWatcher>> entrySet = watchers.entrySet();
       Iterator<Entry<LogWatcherConfiguration, ILogWatcher>> it = entrySet.iterator();
       
       while (it.hasNext())
       {
           Entry<LogWatcherConfiguration, ILogWatcher> entry = it.next();
           if (entry.getValue().equals(watcher))
           {
               watchers.remove(entry.getKey());
               break;
           }
       }
    }

    /**
      * {@inheritDoc}
      */
    public URI getURI()
    {
        return uri;
    }

    /**
      * {@inheritDoc}
      */
    public void close() throws IOException
    {
        //closing watchers
        Collection<ILogWatcher> values = watchers.values();
        Iterator<ILogWatcher> it = values.iterator();
        while(it.hasNext())
        {
            ILogWatcher watcher = (ILogWatcher) it.next();
            watcher.close();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Charset getEncoding()
    {
        return encoding;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setEncoding(Charset encoding)
    {
        this.encoding = encoding;
    }

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(Class adapter)
	{
	       return null;
	}
}
