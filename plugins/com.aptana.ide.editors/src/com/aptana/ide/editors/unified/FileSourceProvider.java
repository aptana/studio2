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
package com.aptana.ide.editors.unified;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * For non-project based files. This is a non-opened file
 * @author Spike Washburn
 */
public class FileSourceProvider implements IFileSourceProvider
{
	/*
	 * Fields
	 */
	private File file;
	private long lastModStamp = -1;
	private int sourceLength;

	/*
	 * Constructors
	 */

	/**
	 * FileSourceProvider
	 * 
	 * @param file
	 */
	public FileSourceProvider(File file)
	{
		this.file = file;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSource()
	 */
	public String getSource() throws IOException
	{
		InputStream stream = new FileInputStream(file);

		// String charSet = "UTF-8"; // TODO: figure out the right encoding for this file

		// Supposedly, the Reader I/O classes will figure out the correct encoding, but may fail with UTF-16 files, so
		// this may need further work
		String charSet = null;
		
		return StreamUtils.readContent(stream, charSet);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSourceLength()
	 */
	public int getSourceLength()
	{
		long stamp = file.lastModified();
		if (stamp != lastModStamp)
		{
			try
			{
				sourceLength = getSource().length();
				lastModStamp = stamp;
			}
			catch (IOException e)
			{
				sourceLength = 0;
			}
		}
		return sourceLength;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSourceURI()
	 */
	public String getSourceURI()
	{
		return CoreUIUtils.getURI(file);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getLineOfOffset(int)
	 */
	public int getLineOfOffset(int offset)
	{
		throw new UnsupportedOperationException(Messages.FileSourceProvider_MethodNotAvailable);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getLineOffset(int)
	 */
	public int getLineOffset(int line)
	{
		throw new UnsupportedOperationException(Messages.FileSourceProvider_MethodNotAvailable);
	}	
}
