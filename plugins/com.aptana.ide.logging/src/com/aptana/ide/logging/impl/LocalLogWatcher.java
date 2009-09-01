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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.logging.LoggingPlugin;

/**
 * Log watcher for local file.
 * @author Denis Denisenko
 */
public class LocalLogWatcher extends AbstractLogWatcher
{
    
    /**
     * Random access file reader.
     * @author Denis Denisenko
     */
    private class RandomAccessReader extends Reader
    {
        /**
         * Stream.
         */
        private Reader reader;
        
        public RandomAccessReader(final RandomAccessFile file, long startPos,
                Charset encoding) throws IOException
        {
            final InputStream stream = new InputStream()
            {

                @Override
                public int read() throws IOException
                {
                    throw new UnsupportedOperationException();
                }

                /**
                  * {@inheritDoc}
                  */
                @Override
                public int read(byte[] b, int off, int len) throws IOException
                {
                    return file.read(b, off, len);
                }

                /**
                  * {@inheritDoc}
                  */
                @Override
                public int read(byte[] b) throws IOException
                {
                    return file.read(b);
                }
            };
            file.seek(startPos);
            this.reader = new InputStreamReader(stream, encoding);
        }
        
        /**
          * {@inheritDoc}
          */
        @Override
        public void close() throws IOException
        {
            reader.close();
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException
        {
            return reader.read(cbuf, off, len);
        }
    }

    /**
     * Increase coefficient
     */
    private static final int INCREASE_K = 2;
    
    /**
     * Mean number of chars per string.
     */
    private final int MEAN_CHARS_PER_STRING = 80;
    
    /**
     * Max buffer.
     */
    private final int MAX_BUFFER = 1024*1024;
    
    /**
     * Border of linear buffer increasing.  
     */
    private final int LINEAR_BORDER = 64*1024;
    
    /**
     * Estimated buffer size.
     */
    private final int estimatedBufferSize;
    
    /**
     * Buffer for backwards reading.
     */
    private char[] buffer;
    
    /**
     * Last file end position.
     */
    private long lastFileLength;
    
    /**
     * Whether whole file was read.
     */
    private boolean wholeFileRead = false;
    
    /**
     * LocalFileLogWatcher constructor.
     * 
     * @param resource - resource.
     * @param config - watcher configuration.
     */
    public LocalLogWatcher(AbstractLogResource resource, LogWatcherConfiguration config)
    {
        super(config, resource);
        estimatedBufferSize = estimateSize(config.getBacklogRows(), config.getEncoding());
        buffer = new char[estimatedBufferSize];
    }

    /**
     * {@inheritDoc}
     */
    protected DataChange getData()
    {
        RandomAccessFile raFile = null;
        try
        {
            raFile = new RandomAccessFile(getLocalResource()
                    .getFile(), "r"); //$NON-NLS-1$
            boolean wholeView = false;
            if (raFile.length() == lastFileLength)
            {
                // update not needed
                return null;
            } 
            else 
            {
//                if (raFile.length() < lastFileLength)
//                {
//                    wholeView = true;
//                }
                // saving new file length
                lastFileLength = raFile.length();
            }
            

            //reading data
            int bufLength = readBuffer(raFile);
            if (wholeFileRead)
            {
                return buildChange(bufLength, 0, wholeView);
            }
            
            int upperLineOffset;
            while((upperLineOffset = checkLinesNumber(bufLength)) == -1)
            {
                if (!increaseBuffer())
                {
                    return buildChange(bufLength, 0, wholeView);
                }
                
                bufLength = readBuffer(raFile);
                if (wholeFileRead)
                {
                    return buildChange(bufLength, 0, wholeView);
                }
            }
            
            //data is read, now returning it
            return buildChange(bufLength, upperLineOffset, wholeView);
            
            
        } catch (IOException e)
        {
            // we were unable to access file for some reason, but still watching
            // it.
            notifyListenersResourceAvailable(false);
            return null;
        }
        finally
        {
            try
            {
                if (raFile != null)
                {
                    raFile.close();
                }
            } catch (IOException e)
            {
                IdeLog.logError(LoggingPlugin.getDefault(), Messages.LocalLogWatcher_ERR_Exception, e);
            }
        }
    }



    /**
     * Builds change using current data.
     * @param bufLength - current buffer length.
     * @param upperLineOffset - upper line offset.
     * @param wholeView - whether to replace whole view.
     * @return
     */
    private DataChange buildChange(int bufLength, int upperLineOffset, boolean wholeView)
    {
        int dataLength = bufLength - upperLineOffset;
        String data = new String(buffer, upperLineOffset, dataLength);
        DataChange change = null;
        if (wholeView)
        {
            change = new DataChange(data, (int) (lastFileLength - dataLength) , Integer.MAX_VALUE);
        }
        else
        {
            change = new DataChange(data, (int) (lastFileLength - dataLength) , dataLength);
        }
        
        return change;
    }


    /**
     * Increases buffer.
     * @return true if buffer increased, false otherwise.
     */
    private boolean increaseBuffer()
    {
        int currentSize = buffer.length;
        if (currentSize < LINEAR_BORDER)
        {
            currentSize *= INCREASE_K;
        }
        else
        {
            currentSize += LINEAR_BORDER;
        }
        
        if (currentSize >= MAX_BUFFER)
        {
            return false;
        }
        
        try 
        {
            char[] newBuffer = new char[currentSize];
            buffer = newBuffer;
        } 
        catch(OutOfMemoryError e)
        {
            return false;
        }
        
        return true;
    }

    /**
     * Check whether number of lines read is ok.
     * @param length - buffer length.
     * @return offset of the upper line, or -1 if not enough lines
     * @return true if number of lines is ok, false otherwise.
     */
    private int checkLinesNumber(int length)
    {
        int okNumberOfLines = getConfiguration().getBacklogRows();
        
        int linesNumber = 0;
        List<Integer> linesInfo = new ArrayList<Integer>();
        for (int i = 0; i < length; i++)
        {
            int ch = buffer[i];
            switch (ch)
            {
            case '\r':
                //checking for following '\n'
                if (i < length - 1)
                {
                    int nextChar = buffer[i+1];
                    if (nextChar == '\n')
                    {
                        i++;
                    }
                }
                linesInfo.add(i);
                linesNumber++;
                break;
            case '\n':
                linesInfo.add(i);
                linesNumber++;
                break;
            default:
                break;
            }
        }
        
        if (linesNumber > okNumberOfLines)
        {
            int diff = linesNumber - okNumberOfLines;
            return linesInfo.get(diff) + 1;
        }
        else if (linesNumber == okNumberOfLines)
        {
            return 0;
        }
        
        return -1;
    }

    /**
     * Reads buffer.
     * @param raFile - file to read.
     * @return number of read characters.
     * @throws IOException
     */
    private int readBuffer(RandomAccessFile raFile) throws IOException
    {
        long startPos = lastFileLength - buffer.length;
        if (startPos < 0)
        {
            startPos = 0;
            //TODO implement a better way of notification
            wholeFileRead = true;
        }
        else
        {
            wholeFileRead = false;
        }

        Reader reader = new RandomAccessReader(raFile, startPos,
                getResource().getEncoding());
        int read = 0;
        int bufPos = 0;
        while (read != -1 && bufPos != buffer.length)
        {
            bufPos += read;
            read = reader.read(buffer, bufPos, buffer.length - bufPos);
        }
        
        return bufPos;
    }

    /**
     * {@inheritDoc}
     */
    public void resetWatching()
    {
        stopWatching();
        
        //resetting reader
        lastFileLength = 0;
        wholeFileRead = false;
    }
    
    /**
     * Gets local log resource. 
     * @return local log resource.
     */
    protected LocalLogResource getLocalResource()
    {
        return (LocalLogResource) getResource();
    }
    
    /**
     * Estimates buffer size.
     * @param backlogRows - rows.
     * @param encoding - encoding.
     * @return estimated buffer size required to store rows.
     */
    private int estimateSize(int backlogRows, Charset encoding)
    {
        float averageCharsPerByte = encoding.newDecoder().averageCharsPerByte();
        if (averageCharsPerByte == 0)
        {
            return 0;
        }
        
        return (int) (((float)backlogRows) * ((float)MEAN_CHARS_PER_STRING) * (1f / averageCharsPerByte));
    }
}
