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

import java.nio.charset.Charset;

import com.aptana.ide.logging.IThreadProxy;

/**
 * Log watcher configuration. 
 * @author Denis Denisenko
 */
public class LogWatcherConfiguration
{

    /**
     * Thread proxy.
     */
    private IThreadProxy threadProxy;
    
    /**
     * Max bytes per second.
     */
    private long maxBytesPerSecond;
    
    /**
     * Check wait.
     */
    private long checkWait;
    
    /**
     * Encoding.
     */
    private Charset encoding;
    
    /**
     * Blacklog rows.
     */
    private int backlogRows;

    /**
     * LogWatcherConfiguration constructor.
     * 
     * @param threadProxy - thread proxy.
     * @param maxBytesPerSecond - maximum bytes per second, listeners are able to consume.
     * @param checkWait - check wait time.
     * @param encoding - encoding.
     * @param backlogRows - backlog rows.
     */
    public LogWatcherConfiguration(IThreadProxy threadProxy,
            long maxBytesPerSecond, long maxNotificationSize, long checkWait,
            Charset encoding, int backlogRows)
    {
        super();
        this.threadProxy = threadProxy;
        this.maxBytesPerSecond = maxBytesPerSecond;
        this.checkWait = checkWait;
        this.encoding = encoding;
        this.backlogRows = backlogRows;
    }

    /**
     * Gets thread proxy. 
     * @return the threadProxy
     */
    public IThreadProxy getThreadProxy()
    {
        return threadProxy;
    }

    /**
     * Gets max bytes per second. 
     * @return the maxBytesPerSecond
     */
    public long getMaxBytesPerSecond()
    {
        return maxBytesPerSecond;
    }

    /**
     * Gets wait time between the checks.
     * @return the checkWait
     */
    public long getCheckWait()
    {
        return checkWait;
    }

    /**
     * Gets encoding.
     * @return the encoding
     */
    public Charset getEncoding()
    {
        return encoding;
    }
    
    /**
     * Gets backlog rows.
     * @return backlog rows.
     */
    public int getBacklogRows()
    {
        return backlogRows;
    }

    /**
      * {@inheritDoc}
      */
    public int hashCode()
    {
        final long prime = 31;
        long result = 1;
        result = prime * result + checkWait;
        result = prime * result
                + ((encoding == null) ? 0 : encoding.hashCode());
        result = prime * result + maxBytesPerSecond;
        result = prime * result
                + ((threadProxy == null) ? 0 : threadProxy.hashCode());
        return (int) result;
    }

    /**
      * {@inheritDoc}
      */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LogWatcherConfiguration other = (LogWatcherConfiguration) obj;
        if (checkWait != other.checkWait)
            return false;
        if (encoding == null)
        {
            if (other.encoding != null)
                return false;
        } else if (!encoding.equals(other.encoding))
            return false;
        if (maxBytesPerSecond != other.maxBytesPerSecond)
            return false;
        if (threadProxy == null)
        {
            if (other.threadProxy != null)
                return false;
        } else if (!threadProxy.equals(other.threadProxy))
            return false;
        return true;
    }  
}
