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
package com.aptana.ide.logging;

import java.io.IOException;
import java.nio.charset.Charset;

import com.aptana.ide.core.resources.IUniformResource;

/**
 * Log resource.
 * @author Denis Denisenko
 */
public interface ILogResource extends IUniformResource
{    
    /**
     * Sets encoding.
     * @param encoding - encoding to set.
     */
    void setEncoding(Charset encoding);
    
    /**
     * Closes watcher.
     * 
     * @throws IOException 
     */
    void close() throws IOException;
    
    /**
     * Gets resource watcher.
     * 
     * @param threadProxy - thread proxy.
     * @param maxBytesPerSecond - maximum bytes per second, listeners are able to consume.
     * @param maxNotificationSize - maximum size of the data for single notification.
     * @param checkWait - check wait time.
     * @param encoding - encoding.
     * @param backlogRows - backlog rows.
     * 
     * @return resource watcher
     */
    ILogWatcher getResourceWatcher(IThreadProxy threadProxy, 
            int maxBytesPerSecond, int maxNotificationSize,
            int checkWait, Charset encoding, int backlogRows);
    
    /**
     * Releases watcher got from this resource.
     * @param watcher - watcher to release.
     * 
     * @throws IllegalArgumentException if watcher was not got from current resource.
     */
    void releaseWatcher(ILogWatcher watcher) throws IllegalArgumentException;

    /**
     * Clears resource.
     */
    void clear() throws IOException;
    
    /**
     * Gets encoding.
     * @return
     */
    Charset getEncoding(); 
}
