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
package com.aptana.ide.logging.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.logging.ILogResource;

/**
 * Log watcher for web files.
 * @author Denis Denisenko
 */
public abstract class LineBasedLogWatcher extends AbstractLogWatcher
{
	/**
	 * Line count result.
	 * @author Denis Denisenko
	 *
	 */
	private static class LineCountResult
	{
		/**
		 * Number of lines.
		 */
		public int lines;
		
		/**
		 * Line offsets.
		 */
		int[] lineOffsets;
	}
	
	/**
	 * Chunk read result.
	 * @author Denis Denisenko
	 *
	 */
	private static class ChunkReadResult
	{
		/**
		 * Information of line count for the whole primary buffer after the chunk reading.
		 */
		public LineCountResult lineCount;
		
		/**
		 * Indicates whether buffer limit was reached during the chunk reading.
		 */
		public boolean bufferLimitReached = false;
		
		/**
		 * Indicates whether input limit was reached during the chunk reading.
		 */
		public boolean inputLimitReached = false;
		
		/**
		 * IO error occurred.
		 */
		public boolean ioError = false;
		
		/**
		 * Number of bytes read.
		 */
		public int bytesRead = 0;
	}
	
	/**
     * Increase coefficient
     */
    private static final int INCREASE_K = 2;

    /**
     * Maximum number of iterations in a single data read.
     */
	private static final int MAX_ITERATIONS = 3;

	/**
	 * Acceptable difference between required lines and got lines.
	 */
	private static final float ACCEPTABLE_DIFFERENCE = 0.1f;
    
    /**
     * Mean number of chars per string.
     */
    private final int MEAN_CHARS_PER_LINE = 80;
    
    /**
     * Max buffer.
     */
    private final int MAX_BUFFER = 1024*1024;
    
    /**
     * Border of linear buffer increasing.  
     */
    private final int LINEAR_BORDER = 64*1024;
    
    /**
     * Current estimation of mean characters per line.
     */
    private int _meanCharactersPerLine = MEAN_CHARS_PER_LINE; 
	
	/**
     * Character buffer.
     */
    private CharBuffer _charBuffer;
    
    /**
     * Byte buffer.
     */
    private ByteBuffer _primaryByteBuffer;
    
    /**
     * Secondary byte buffer.
     */
    private ByteBuffer _secondaryByteBuffer;

	/**
     * Last file end position.
     */
    private long _lastFileLength = 0;
    
    /**
     * Last position in a document that was updated by the watcher.
     */
    private long _lastDocumentPosition = 0;
    
    
    /**
	 * Gets current log length.
	 * @return current log length.
	 */
	protected abstract long getCurrentLogLength() throws IOException;
	
	/**
	 * Reads data from the specified position.
	 * 
	 * Buffer may be filled with less data then requested if, and only if the end of input is
	 * reached.
	 * 
	 * Method must return the buffer into the initial state (position = 0, limit = number of bytes read).
	 * 
	 * @param startPos - position in a file in bytes, to start reading from.  
	 * @param buffer - buffer to read data to.
	 * @param maxBytesToRead - maximum bytes to read.
	 */
	protected abstract void readData(int startPos, ByteBuffer buffer, int maxBytesToRead)
		throws IOException;
    
	/**
	 * WebLogWatcher constructor.
	 * @param config
	 * @param resource
	 */
	public LineBasedLogWatcher(LogWatcherConfiguration config, ILogResource resource)
	{
		super(config, resource);
		int estimatedByteBufferSize = 
			estimateByteBufferSize(config.getBacklogRows(), config.getEncoding());
		int estimatedCharBufferSize = 
			estimateCharBufferSize(config.getBacklogRows());
		
        _charBuffer = CharBuffer.allocate(estimatedCharBufferSize);
        _primaryByteBuffer = ByteBuffer.allocate(estimatedByteBufferSize);
        _secondaryByteBuffer = ByteBuffer.allocate(estimatedByteBufferSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataChange getData() throws IOException
	{
		long currentFileLength = getCurrentLogLength();
		
		//if current file length is equal to the last file length or 
		//file length is undefined (resource inaccessible), we should not provide any data update
		if (currentFileLength == _lastFileLength || currentFileLength == -1)
		{
			return null;
		}
		
		//if current file length is less then last file length, treat it as full file rewrite
		if (currentFileLength < _lastFileLength)
		{
			resetWatching();
		}
		
		//starting position for initial read
		long currentReadEndingPosition = currentFileLength;
		
		//initial number of lines to read
		int linesToRead = getConfiguration().getBacklogRows();
		
		boolean documentAdditionMode = false;
		
		ChunkReadResult readResult = null;
		
		//reading chunks
		for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++)
		{
			readResult = readChunk(currentReadEndingPosition, linesToRead);
			
			if (readResult.ioError)
			{
				return null;
			}
			
			documentAdditionMode = readResult.inputLimitReached;
			
			if (readResult.bufferLimitReached || readResult.inputLimitReached
					|| acceptableNumberOfLines(readResult.lineCount.lines))
			{
				break;
			}
		}
		
		if (readResult != null && readResult.lineCount != null
				&& readResult.lineCount.lineOffsets.length == 0)
		{
			return null;
		}
		
		DataChange result = null;
		//building change
		if (_lastFileLength == 0 || !documentAdditionMode)
		{
			//if "document addition" mode is off, we should replace the whole document with the
			//N full lines from the character buffer
			String data = _charBuffer.subSequence(0, _charBuffer.limit()).toString();
			result = new DataChange(data, 0, Integer.MAX_VALUE);
			
			//setting last document position
			_lastDocumentPosition = data.length();
		}
		else
		{
			//if "document addition" mode is on, we should add all the character buffer to the
			//end of the document
			String data = _charBuffer.toString();
			result = new DataChange(data, (int) _lastDocumentPosition, data.length());
			
			//increasing last document position
			_lastDocumentPosition = _lastDocumentPosition + data.length();
		}
		
		_lastFileLength = currentFileLength;
		
		return result;
	}
	
	/**
	 * Checks whether the number of lines read is acceptable.
	 * @param lines - lines read.
	 * @return true if acceptable, false otherwise.
	 */
	private boolean acceptableNumberOfLines(int lines)
	{
		int maxLines = getConfiguration().getBacklogRows(); 
		if (lines >= maxLines)
		{
			return true;
		}
		
		return maxLines - lines < maxLines * ACCEPTABLE_DIFFERENCE;
	}

	/**
	 * Reads a chunk of data. The read ends with the position specified.
	 * Tries to read a number of lines specified.
	 * 
	 * @param readEndPosition - the read end position.
	 * @param linesToRead - number of lines to read.
	 * 
	 * @return chunk read result.
	 */
	private ChunkReadResult readChunk(long readEndPosition, int linesToRead)
	{
		ChunkReadResult result = new ChunkReadResult();
		
		int bytesToRead = 
			estimateByteBufferSize(linesToRead, getConfiguration().getEncoding());
		
		//where to read bytes to
		ByteBuffer byteBufferTarget = null;
		int maxCapacity = 0;
		
		//if byte buffer is empty, using is as a target
		if (_primaryByteBuffer.position() == 0)
		{
			byteBufferTarget = _primaryByteBuffer;
			maxCapacity = ensureByteBufferCapacity(bytesToRead);
		}
		//in other case, using secondary byte buffer
		else
		{
			byteBufferTarget = _secondaryByteBuffer;
			
			int oldMainBufferCapacity = _primaryByteBuffer.capacity();
			int maxMainBufferCapacity = 
				ensureSecondaryByteBufferCapacity(oldMainBufferCapacity + bytesToRead);
			
			int allowedSecondaryBufferCapacity = maxMainBufferCapacity - oldMainBufferCapacity;
			
			maxCapacity = ensureSecondaryByteBufferCapacity(allowedSecondaryBufferCapacity);
		}
		
		//if we can't allow reading all the bytes estimated due to the buffer limit,
		//reading only what buffer allows.
		if (maxCapacity < bytesToRead)
		{
			bytesToRead = maxCapacity;
			result.bufferLimitReached = true;
		}
		
		//calculating how many data remain unread in file
		long inputLimit = readEndPosition - _lastFileLength;
		
		//if we have no enough data to read, reading as much as is available
		if (inputLimit < bytesToRead)
		{
			result.inputLimitReached = true;
			bytesToRead = (int) inputLimit;
		}
		
		//returning if we have no data to read
		if (inputLimit == 0)
		{
			 return result;
		}
		
		//calculating data read start position
		int startPos = (int) (readEndPosition - (long) bytesToRead);
		
		byteBufferTarget.clear();
		try
		{
			readData(startPos, byteBufferTarget, bytesToRead);
		} catch (IOException e)
		{
			result.ioError = true;
			return result;
		}
		
		if (byteBufferTarget == _secondaryByteBuffer)
		{
			addSecondaryByteBufferToPrimary();
		}
		
		//converting all the collected bytes (primary byte buffer) to the characters
		decodeBytes();
		
		result.lineCount = countNumberOfLines(_charBuffer);
		
		return result;
	}

	/**
	 * Decodes primary byte buffer to the character buffer
	 */
	private void decodeBytes()
	{
		int charBufferCapacityEstimation = (int) ((float)_primaryByteBuffer.limit()*
			getConfiguration().getEncoding().newDecoder().averageCharsPerByte()) + 1024;
		
		ensureCharBufferCapacity(charBufferCapacityEstimation);
		
		_charBuffer.clear();
		CoderResult coderResult = 
			getConfiguration().getEncoding().newDecoder().decode(_primaryByteBuffer, _charBuffer, true);
		//TODO add error handling
		_primaryByteBuffer.flip();
		_charBuffer.flip();
	}

	/**
	 * Adds secondary byte buffer contents in the beginning of the main byte buffer. 
	 */
	private void addSecondaryByteBufferToPrimary()
	{
		int capacity = _primaryByteBuffer.limit() + _secondaryByteBuffer.limit();
		if (_primaryByteBuffer.capacity() > capacity)
		{
			capacity = _primaryByteBuffer.capacity();
		}
		
		ByteBuffer tempBuffer = ByteBuffer.allocate(capacity);
		tempBuffer.put(_secondaryByteBuffer);
		tempBuffer.put(_primaryByteBuffer);
		
		_primaryByteBuffer.flip();
		_secondaryByteBuffer.flip();
	}

	/**
	 * @param charBuffer2
	 * @return
	 */
	private LineCountResult countNumberOfLines(CharBuffer charBuffer2)
	{        
        int linesNumber = 0;
        List<Integer> linesInfo = new ArrayList<Integer>();
        for (int i = 0; i < charBuffer2.limit(); i++)
        {
            int ch = charBuffer2.get(i);
            switch (ch)
            {
            case '\r':
                //checking for following '\n'
                if (i < charBuffer2.limit() - 1)
                {
                    int nextChar = charBuffer2.get(i+1);
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
        
        LineCountResult result = new LineCountResult();
        result.lines = linesInfo.size();
        
        result.lineOffsets = new int[linesInfo.size()];
        for (int i = 0; i < linesInfo.size(); i++)
        {
        	result.lineOffsets[i] = linesInfo.get(i);
        }
        
        return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetWatching()
	{
		setNotifyListeners(false);
		
		try
		{
			synchronizedStopWatching();
			
			_lastFileLength = 0;
			_lastDocumentPosition = 0;
			_charBuffer.clear();
			_primaryByteBuffer.clear();
			_secondaryByteBuffer.clear();
		}
		finally
		{
			setNotifyListeners(true);
		}
	}
	
	
	
	/**
	 * Gets log URL.
	 * @return log URL.
	 */
	private URL getLogURL()
	{
		try
		{
			return ((AbstractLogResource) getResource()).getURI().toURL();
		} 
		catch (MalformedURLException e)
		{
			return null;
		}
	}
    
    /**
     * Estimates character buffer size.
     * @param backlogRows - rows.
     * @return estimated buffer size required to store rows.
     */
    private int estimateCharBufferSize(int backlogRows)
    {
        return backlogRows * _meanCharactersPerLine;
    }

	/**
     * Estimates byte buffer size.
     * @param backlogRows - rows.
     * @param encoding - encoding.
     * @return estimated buffer size required to store rows.
     */
    private int estimateByteBufferSize(int backlogRows, Charset encoding)
    {
        float averageCharsPerByte = encoding.newDecoder().averageCharsPerByte();
        if (averageCharsPerByte == 0)
        {
            return 0;
        }
        
        return (int) (((float)backlogRows) * ((float)_meanCharactersPerLine) * (1f / averageCharsPerByte));
    }
    
    /**
     * Increases character buffer.
     * @return true if buffer increased, false otherwise.
     */
    private boolean increaseCharBuffer()
    {
    	int currentSize = _charBuffer.capacity();
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
            CharBuffer newBuffer = CharBuffer.allocate(currentSize);
            _charBuffer = newBuffer;
        } 
        catch(OutOfMemoryError e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Increases byte buffer.
     * @return true if buffer increased, false otherwise.
     */
    private boolean increaseByteBuffer()
    {
        int currentSize = _primaryByteBuffer.capacity();
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
            ByteBuffer newBuffer = ByteBuffer.allocate(currentSize);
            _primaryByteBuffer = newBuffer;
        } 
        catch(OutOfMemoryError e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Increases secondary byte buffer.
     * @return true if buffer increased, false otherwise.
     */
    private boolean increaseSecondaryByteBuffer()
    {
        int currentSize = _secondaryByteBuffer.capacity();
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
            ByteBuffer newBuffer = ByteBuffer.allocate(currentSize);
            _secondaryByteBuffer = newBuffer;
        } 
        catch(OutOfMemoryError e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
	 * Ensures byte buffer has enough capacity to store the number of bytes specified.
	 * @param capacity - capacity to ensure.
	 * 
	 * @return result capacity. if result capacity is less then capacity to ensure,
	 * then buffer can not increase capacity any more.
	 */
	private int ensureByteBufferCapacity(int capacity)
	{
		while (_primaryByteBuffer.capacity() < capacity)
		{
			boolean increased = increaseByteBuffer();
			if (!increased)
			{
				return _primaryByteBuffer.capacity();
			}
		}
		
		return _primaryByteBuffer.capacity();
	}
	
	/**
	 * Ensures char buffer has enough capacity to store the number of chars specified.
	 * @param capacity - capacity to ensure.
	 * 
	 * @return result capacity. if result capacity is less then capacity to ensure,
	 * then buffer can not increase capacity any more.
	 */
	private int ensureCharBufferCapacity(int capacity)
	{
		while (_charBuffer.capacity() < capacity)
		{
			boolean increased = increaseCharBuffer();
			if (!increased)
			{
				return _charBuffer.capacity();
			}
		}
		
		return _charBuffer.capacity();
	}
	
	/**
	 * Ensures byte buffer has enough capacity to store the number of bytes specified.
	 * @param capacity - capacity to ensure.
	 * 
	 * @return result capacity. if result capacity is less then capacity to ensure,
	 * then buffer can not increase capacity any more.
	 */
	private int ensureSecondaryByteBufferCapacity(int capacity)
	{
		while (_secondaryByteBuffer.capacity() < capacity)
		{
			boolean increased = increaseSecondaryByteBuffer();
			if (!increased)
			{
				return _secondaryByteBuffer.capacity();
			}
		}
		
		return _secondaryByteBuffer.capacity();
	}
}
