/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.internal.filesystem.text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 */
public class FileCharSequenceProvider
{

	private static int NUMBER_OF_BUFFERS = 3;
	/**
	 * 
	 */
	public static int BUFFER_SIZE = 2 << 18; // public for testing

	private FileCharSequence fReused = null;

	/**
	 * @param file
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public CharSequence newCharSequence(File file) throws CoreException, IOException
	{
		if (this.fReused == null)
		{
			return new FileCharSequence(file);
		}
		FileCharSequence curr = this.fReused;
		this.fReused = null;
		curr.reset(file);
		return curr;
	}

	/**
	 * @param seq
	 * @throws CoreException
	 * @throws IOException
	 */
	public void releaseCharSequence(CharSequence seq) throws CoreException, IOException
	{
		if (seq instanceof FileCharSequence)
		{
			FileCharSequence curr = (FileCharSequence) seq;
			try
			{
				curr.close();
			}
			finally
			{
				if (this.fReused == null)
				{
					this.fReused = curr;
				}
			}
		}
	}

	/**
	 * @author Pavel Petrochenko
	 *
	 */
	public static class FileCharSequenceException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		/* package */FileCharSequenceException(IOException e)
		{
			super(e);
		}

		/* package */FileCharSequenceException(CoreException e)
		{
			super(e);
		}

		/**
		 * @throws CoreException
		 * @throws IOException
		 */
		public void throwWrappedException() throws CoreException, IOException
		{
			Throwable wrapped = this.getCause();
			if (wrapped instanceof CoreException)
			{
				throw (CoreException) wrapped;
			}
			else if (wrapped instanceof IOException)
			{
				throw (IOException) wrapped;
			}
			// not possible
		}
	}

	/**
	 * 
	 * @author Pavel Petrochenko
	 *
	 */
	private static final class CharSubSequence implements CharSequence
	{

		private final int fSequenceOffset;
		private final int fSequenceLength;
		private final FileCharSequence fParent;

		/**
		 * @param parent
		 * @param offset
		 * @param length
		 */
		public CharSubSequence(FileCharSequence parent, int offset, int length)
		{
			this.fParent = parent;
			this.fSequenceOffset = offset;
			this.fSequenceLength = length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.CharSequence#length()
		 */
		/**
		 * @see java.lang.CharSequence#length()
		 */
		public int length()
		{
			return this.fSequenceLength;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.CharSequence#charAt(int)
		 */
		/**
		 * @see java.lang.CharSequence#charAt(int)
		 */
		public char charAt(int index)
		{
			if (index < 0)
			{
				throw new IndexOutOfBoundsException("index must be larger than 0"); //$NON-NLS-1$
			}
			if (index >= this.fSequenceLength)
			{
				throw new IndexOutOfBoundsException("index must be smaller than length"); //$NON-NLS-1$
			}
			return this.fParent.charAt(this.fSequenceOffset + index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.CharSequence#subSequence(int, int)
		 */
		/**
		 * @see java.lang.CharSequence#subSequence(int, int)
		 */
		public CharSequence subSequence(int start, int end)
		{
			if (end < start)
			{
				throw new IndexOutOfBoundsException("end cannot be smaller than start"); //$NON-NLS-1$
			}
			if (start < 0)
			{
				throw new IndexOutOfBoundsException("start must be larger than 0"); //$NON-NLS-1$
			}
			if (end > this.fSequenceLength)
			{
				throw new IndexOutOfBoundsException("end must be smaller or equal than length"); //$NON-NLS-1$
			}
			return this.fParent.subSequence(this.fSequenceOffset + start, this.fSequenceOffset + end);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			try
			{
				return this.fParent.getSubstring(this.fSequenceOffset, this.fSequenceLength);
			}
			catch (IOException e)
			{
				throw new FileCharSequenceException(e);
			}
			catch (CoreException e)
			{
				throw new FileCharSequenceException(e);
			}
		}
	}

	/**
	 * 
	 * @author Pavel Petrochenko
	 *
	 */
	private static final class Buffer
	{
		private final char[] fBuf;
		private int fOffset;
		private int fLength;

		private Buffer fNext;
		private Buffer fPrevious;

		/**
		 * 
		 */
		public Buffer()
		{
			this.fBuf = new char[FileCharSequenceProvider.BUFFER_SIZE];
			this.reset();
			this.fNext = this;
			this.fPrevious = this;
		}

		/**
		 * @param pos
		 * @return
		 */
		public boolean contains(int pos)
		{
			int offset = this.fOffset;
			return (offset <= pos) && (pos < offset + this.fLength);
		}

		/**
		 * Fills the buffer by reading from the given reader.
		 * 
		 * @param reader
		 *            the reader to read from
		 * @param pos
		 *            the offset of the reader in the file
		 * @return returns true if the end of the file has been reached
		 * @throws IOException
		 */
		public boolean fill(Reader reader, int pos) throws IOException
		{
			int res = reader.read(this.fBuf);
			if (res == -1)
			{
				this.fOffset = pos;
				this.fLength = 0;
				return true;
			}

			int charsRead = res;
			while (charsRead < FileCharSequenceProvider.BUFFER_SIZE)
			{
				res = reader.read(this.fBuf, charsRead, FileCharSequenceProvider.BUFFER_SIZE - charsRead);
				if (res == -1)
				{
					this.fOffset = pos;
					this.fLength = charsRead;
					return true;
				}
				charsRead += res;
			}
			this.fOffset = pos;
			this.fLength = FileCharSequenceProvider.BUFFER_SIZE;
			return false;
		}

		/**
		 * @param pos
		 * @return
		 */
		public char get(int pos)
		{
			return this.fBuf[pos - this.fOffset];
		}

		/**
		 * @param buf
		 * @param start
		 * @param length
		 * @return
		 */
		public StringBuffer append(StringBuffer buf, int start, int length)
		{
			return buf.append(this.fBuf, start - this.fOffset, length);
		}

		/**
		 * @param buf
		 * @return buf
		 */
		public StringBuffer appendAll(StringBuffer buf)
		{
			return buf.append(this.fBuf, 0, this.fLength);
		}

		/**
		 * @return of
		 */
		public int getEndOffset()
		{
			return this.fOffset + this.fLength;
		}

		/**
		 * 
		 */
		
		public void removeFromChain()
		{
			this.fPrevious.fNext = this.fNext;
			this.fNext.fPrevious = this.fPrevious;

			this.fNext = this;
			this.fPrevious = this;
		}

		/**
		 * @param other
		 */
		public void insertBefore(Buffer other)
		{
			this.fNext = other;
			this.fPrevious = other.fPrevious;
			this.fPrevious.fNext = this;
			other.fPrevious = this;
		}

		/**
		 * @return
		 */
		public Buffer getNext()
		{
			return this.fNext;
		}

		/**
		 * @return prev
		 */
		public Buffer getPrevious()
		{
			return this.fPrevious;
		}

		/**
		 * 
		 */
		public void reset()
		{
			this.fOffset = -1;
			this.fLength = 0;
		}
	}

	/**
	 * 
	 * @author Pavel Petrochenko
	 *
	 */
	private final class FileCharSequence implements CharSequence
	{

		private Reader fReader;
		private int fReaderPos;

		private Integer fLength;

		private Buffer fMostCurrentBuffer; // access to the buffer chain
		private int fNumberOfBuffers;

		private File fFile;

		/**
		 * @param file
		 * @throws CoreException
		 * @throws IOException
		 */
		public FileCharSequence(File file) throws CoreException, IOException
		{
			this.fNumberOfBuffers = 0;
			this.reset(file);
		}

		/**
		 * @param file
		 * @throws CoreException
		 * @throws IOException
		 */
		public void reset(File file) throws CoreException, IOException
		{
			this.fFile = file;
			this.fLength = null; // only calculated on demand

			Buffer curr = this.fMostCurrentBuffer;
			if (curr != null)
			{
				do
				{
					curr.reset();
					curr = curr.getNext();
				}
				while (curr != this.fMostCurrentBuffer);
			}
			this.initializeReader();
		}

		private void initializeReader() throws CoreException, IOException
		{
			if (this.fReader != null)
			{
				this.fReader.close();
			}
			String charset = ResourcesPlugin.getEncoding();
			this.fReader = new InputStreamReader(this.getInputStream(charset), charset);
			this.fReaderPos = 0;
		}

		private InputStream getInputStream(String charset) throws CoreException, IOException
		{
			InputStream contents = new BufferedInputStream(new FileInputStream(this.fFile));
			// try {
			// if (CHARSET_UTF_8.equals(charset)) {
			// /*
			// * This is a workaround for a corresponding bug in Java readers and writer,
			// * see: http://developer.java.sun.com/developer/bugParade/bugs/4508058.html
			// * we remove the BOM before passing the stream to the reader
			// */
			// IContentDescription description= fFile.getContentDescription();
			// if ((description != null) && (description.getProperty(IContentDescription.BYTE_ORDER_MARK) != null)) {
			// int bomLength= IContentDescription.BOM_UTF_8.length;
			// byte[] bomStore= new byte[bomLength];
			// int bytesRead= 0;
			// do {
			// int bytes= contents.read(bomStore, bytesRead, bomLength - bytesRead);
			// if (bytes == -1)
			// throw new IOException();
			// bytesRead += bytes;
			// } while (bytesRead < bomLength);
			//						
			// if (!Arrays.equals(bomStore, IContentDescription.BOM_UTF_8)) {
			// // discard file reader, we were wrong, no BOM -> new stream
			// contents.close();
			// contents= fFile.getContents();
			// }
			// }
			// }
			// ok= true;
			// } finally {
			// if (!ok && contents != null)
			// try {
			// contents.close();
			// } catch (IOException ex) {
			// // ignore
			// }
			// }
			return contents;
		}

		private void clearReader() throws IOException
		{
			if (this.fReader != null)
			{
				this.fReader.close();
			}
			this.fReader = null;
			this.fReaderPos = Integer.MAX_VALUE;
		}

		
		/**
		 * @see java.lang.CharSequence#length()
		 */
		public int length()
		{
			if (this.fLength == null)
			{
				try
				{
					this.getBuffer(Integer.MAX_VALUE);
				}
				catch (IOException e)
				{
					throw new FileCharSequenceException(e);
				}
				catch (CoreException e)
				{
					throw new FileCharSequenceException(e);
				}
			}
			return this.fLength.intValue();
		}

		private Buffer getBuffer(int pos) throws IOException, CoreException
		{
			Buffer curr = this.fMostCurrentBuffer;
			if (curr != null)
			{
				do
				{
					if (curr.contains(pos))
					{
						return curr;
					}
					curr = curr.getNext();
				}
				while (curr != this.fMostCurrentBuffer);
			}

			Buffer buf = this.findBufferToUse();
			this.fillBuffer(buf, pos);
			if (buf.contains(pos))
			{
				return buf;
			}
			return null;
		}

		private Buffer findBufferToUse()
		{
			if (this.fNumberOfBuffers < FileCharSequenceProvider.NUMBER_OF_BUFFERS)
			{
				this.fNumberOfBuffers++;
				Buffer newBuffer = new Buffer();
				if (this.fMostCurrentBuffer == null)
				{
					this.fMostCurrentBuffer = newBuffer;
					return newBuffer;
				}
				newBuffer.insertBefore(this.fMostCurrentBuffer); // insert before first
				return newBuffer;
			}
			return this.fMostCurrentBuffer.getPrevious();
		}

		private boolean fillBuffer(Buffer buffer, int pos) throws CoreException, IOException
		{
			if (this.fReaderPos > pos)
			{
				this.initializeReader();
			}

			do
			{
				boolean endReached = buffer.fill(this.fReader, this.fReaderPos);
				this.fReaderPos = buffer.getEndOffset();
				if (endReached)
				{
					this.fLength = new Integer(this.fReaderPos); // at least we know the size of the file now
					this.fReaderPos = Integer.MAX_VALUE; // will have to reset next time
					return true;
				}
			}
			while (this.fReaderPos <= pos);

			return true;
		}

		
		/**
		 * @see java.lang.CharSequence#charAt(int)
		 */
		public char charAt(final int index)
		{
			final Buffer current = this.fMostCurrentBuffer;
			if ((current != null) && current.contains(index))
			{
				return current.get(index);
			}

			if (index < 0)
			{
				throw new IndexOutOfBoundsException("index must be larger than 0"); //$NON-NLS-1$
			}
			if ((this.fLength != null) && (index >= this.fLength.intValue()))
			{
				throw new IndexOutOfBoundsException("index must be smaller than length"); //$NON-NLS-1$
			}

			try
			{
				final Buffer buffer = this.getBuffer(index);
				if (buffer == null)
				{
					throw new IndexOutOfBoundsException("index must be smaller than length"); //$NON-NLS-1$
				}
				if (buffer != this.fMostCurrentBuffer)
				{
					// move to first
					if (buffer.getNext() != this.fMostCurrentBuffer)
					{ // already before the current?
						buffer.removeFromChain();
						buffer.insertBefore(this.fMostCurrentBuffer);
					}
					this.fMostCurrentBuffer = buffer;
				}
				return buffer.get(index);
			}
			catch (IOException e)
			{
				throw new FileCharSequenceException(e);
			}
			catch (CoreException e)
			{
				throw new FileCharSequenceException(e);
			}
		}

		/**
		 * @param start
		 * @param length
		 * @return string
		 * @throws IOException
		 * @throws CoreException
		 */
		public String getSubstring(int start, int length) throws IOException, CoreException
		{
			int pos = start;
			int endPos = start + length;

			if ((this.fLength != null) && (endPos > this.fLength.intValue()))
			{
				throw new IndexOutOfBoundsException("end must be smaller than length"); //$NON-NLS-1$
			}

			StringBuffer res = new StringBuffer(length);

			Buffer buffer = this.getBuffer(pos);
			while ((pos < endPos) && (buffer != null))
			{
				int bufEnd = buffer.getEndOffset();
				if (bufEnd >= endPos)
				{
					return buffer.append(res, pos, endPos - pos).toString();
				}
				buffer.append(res, pos, bufEnd - pos);
				pos = bufEnd;
				buffer = this.getBuffer(pos);
			}
			return res.toString();
		}

		
		/**
		 * @see java.lang.CharSequence#subSequence(int, int)
		 */
		public CharSequence subSequence(int start, int end)
		{
			if (end < start)
			{
				throw new IndexOutOfBoundsException("end cannot be smaller than start"); //$NON-NLS-1$
			}
			if (start < 0)
			{
				throw new IndexOutOfBoundsException("start must be larger than 0"); //$NON-NLS-1$
			}
			if ((this.fLength != null) && (end > this.fLength.intValue()))
			{
				throw new IndexOutOfBoundsException("end must be smaller than length"); //$NON-NLS-1$
			}
			return new CharSubSequence(this, start, end - start);
		}

		/**
		 * @throws IOException
		 */
		public void close() throws IOException
		{
			this.clearReader();
		}

		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			int len = this.fLength != null ? this.fLength.intValue() : 4000;
			StringBuffer res = new StringBuffer(len);
			try
			{
				Buffer buffer = this.getBuffer(0);
				while (buffer != null)
				{
					buffer.appendAll(res);
					buffer = this.getBuffer(res.length());
				}
				return res.toString();
			}
			catch (IOException e)
			{
				throw new FileCharSequenceException(e);
			}
			catch (CoreException e)
			{
				throw new FileCharSequenceException(e);
			}
		}
	}

}
