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
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.HashSet;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.logging.ILogResource;
import com.aptana.ide.logging.ILogResourceListener;
import com.aptana.ide.logging.ILogTailListener;
import com.aptana.ide.logging.ILogWatcher;
import com.aptana.ide.logging.IThreadProxy;
import com.aptana.ide.logging.LoggingPlugin;

/**
 * Abstract superclass for watchers.
 * 
 * @author Denis Denisenko
 */
public abstract class AbstractLogWatcher implements ILogWatcher, Runnable
{
    
    protected static class DataChange
    {
        /**
         * Data.
         */
        private String data;
        
        /**
         * Length of text to replace.
         */
        private long globalLength;
        
        /**
         * Global offset.
         */
        private long globalOffset;
        
        /**
         * Data constructor.
         * @param data - data.
         * @param globalOffset - global data offset (measured in characters).
         * @param globalLength - global length of text to replace (measured in characters).
         */
        public DataChange(String data, int globalOffset, int globalLength)
        {
            this.data = data;
            this.globalLength = globalLength;
            this.globalOffset = globalOffset;
        }

        /**
         * Gets data.
         * @return data.
         */
        public String getData()
        {
            return data;
        }

        /**
         * Gets global data offset.
         * @return the globalOffset.
         */
        public long getGlobalOffset()
        {
            return globalOffset;
        }
        
        /**
         * Gets global length to replace. 
         * @return global length to replace.
         */
        public long getGlobalLength()
        {
            return globalLength;
        }
    }
    
    /**
     * Thread stop timeout. 
     */
    private static final int TIMEOUT = 1000;

    /**
     * Thread proxy.
     */
    private final IThreadProxy threadProxy;

    /**
     * Synchronized map of tail listeners.
     */
    private final HashSet<ILogTailListener> tailListeners = new HashSet<ILogTailListener>();
    
    /**
     * Synchronized map of resource listeners.
     */
    private final HashSet<ILogResourceListener> resourceListeners = 
    	new HashSet<ILogResourceListener>();
    
    /**
     * Watcher configuration.
     */
    private final LogWatcherConfiguration configuration;

    /**
     * Check wait time.
     */
    private final long checkWait;

    /**
     * Whether to check data.
     */
    private volatile boolean checkData;
    
    /**
     * Current thread.
     */
    private Thread thread;
    
    /**
     * Resource.
     */
    private final ILogResource resource;

	private boolean _notifyListeners = true;

    /**
     * AbstractLogWatcher constructor.
     * 
     * @param config - watcher configuration.
     */
    public AbstractLogWatcher(LogWatcherConfiguration config, ILogResource resource)
    {
        this.threadProxy = config.getThreadProxy();
        this.checkWait = config.getCheckWait();
        
        this.configuration = config;
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    public final void registerListener(ILogTailListener listener)
    {
        tailListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeListener(ILogTailListener listener)
    {
        tailListeners.remove(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void registerListener(ILogResourceListener listener)
    {
        resourceListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeListener(ILogResourceListener listener)
    {
        resourceListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public final void startWatching()
    {
        if (thread != null && thread.isAlive())
        {
        	checkData = true;
            return;
        }
        checkData = true;
        thread = new Thread(this, resource.getURI().toString() + " watching thread"); //$NON-NLS-1$
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    public final void stopWatching()
    {
        //turning the check off.
        checkData = false;
        /*if (thread == null || !thread.isAlive())
        {
            return;
        }
        
        try
        {
            thread.join(TIMEOUT);
        } catch (InterruptedException e)
        {
            IdeLog.logError(LoggingPlugin.getDefault(), 
                    "Unexpected exception while stopping watcher thread", e);
        }
        
        if (thread.isAlive())
        {
            thread.interrupt();
        }*/
    }
    
    /**
     * Stops watching and wait until the stop is complete.
     */
    public final void synchronizedStopWatching()
    {
        //turning the check off.
        checkData = false;
        if (thread == null || !thread.isAlive())
        {
            return;
        }
        
        try
        {
        	//waiting for a thread to stop its work
            thread.join(0);
        } catch (InterruptedException e)
        {
            IdeLog.logError(LoggingPlugin.getDefault(), 
                    Messages.AbstractLogWatcher_ERR_Exception, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void run()
    {
        try
        {
            while (checkData)
            {
                //getting data reader
                DataChange change = getData();
                if (change == null)
                {
                    try
                    {
                        Thread.sleep(checkWait);
                    } catch (InterruptedException e)
                    {
                        return;
                    }
                    continue;
                }
                
                String toSend = change.getData();

                //notifying listeners we have new data
                notifyListeners(toSend, change.getGlobalOffset(), change.getGlobalLength());

                //notifying listeners that resource is available.
                notifyListenersResourceAvailable(true);
                
                try
                {
                    Thread.sleep(checkWait);
                } catch (InterruptedException e)
                {
                    return;
                }
            }
        } catch (Throwable th)
        {
            notifyListeners(th);
            stopWatching();
        }
    }

    /**
     * Notifies listeners.
     * @param data - data.
     * @param globalLength - length of text to replace.
     * @param globalOffset - global offset of the data. 
     */
    protected void notifyListeners(final String data, final long globalOffset, final long globalLength)
    {
    	if (_notifyListeners)
    	{
	        threadProxy.run(new Runnable()
	        {
	
	            public void run()
	            {
	                for(ILogTailListener listener : tailListeners)
	                {
	                    listener.dataAvailable(data, globalOffset, globalLength);
	                }
	            }
	        });
    	}
    }

    /**
     * Notifies listeners.
     * @param error - error.
     */
    protected void notifyListeners(final Throwable error)
    {
    	if (_notifyListeners)
    	{
	        threadProxy.run(new Runnable()
	        {
	            public void run()
	            {
	                for (ILogTailListener listener : tailListeners)
	                {
	                    listener.errorHappened(error);
	                }
	            }
	        });
    	}
    }
    
    /**
     * Sets whether to notify listeners.
     * @param notify - notify listeners or not.
     */
    protected void setNotifyListeners(boolean notify)
    {
    	_notifyListeners = notify;
    }
    
    /**
     * Notifies listeners if resource is available.
     * @param available - whether resource is available.
     */
    protected void notifyListenersResourceAvailable(final boolean available)
    {
        threadProxy.run(new Runnable()
        {
            public void run()
            {
                for (ILogResourceListener listener : resourceListeners)
                {
                    listener.resourceAvailable(available);
                }
            }
        });
    }
    
    /**
     * Gets watcher configuration.
     * @return
     */
    protected LogWatcherConfiguration getConfiguration()
    {
        return configuration;
    }
    
    /**
      * {@inheritDoc}
      */
    public void close()
    {
        stopWatching();
        tailListeners.clear();
        resourceListeners.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWatching()
    {
        return thread != null && thread.isAlive();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean watchingStatus()
    {
    	return checkData;
    }
    
    public ILogResource getResource()
    {
        return resource;
    }

    /**
     * Gets data change.
     * 
     * @return reader for data change as well as whether data should be appended or replaced and data offset.
     * Reader is not required to implement {@link Reader#read(char[], int, int)},
     * but it should implement  {@link Reader#read(CharBuffer)} in efficient way instead.
     * Also reader is expected to read 0 bytes when no data is available and >0 bytes otherwise,
     * even if a previous read resulted in 0 bytes read.
     * Returning null means no data is available at the moment.
     * 
     * @throws IOException IF IO error occurred
     */
    protected abstract DataChange getData() throws IOException;
}
