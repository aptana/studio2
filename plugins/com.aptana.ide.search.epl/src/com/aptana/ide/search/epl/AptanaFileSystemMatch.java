/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.search.epl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.core.StringUtils;

/**
 * @author Pavel Petrochenko
 */
public class AptanaFileSystemMatch extends FileSystemMatch implements IAdaptable
{
	final int lineNumber;
	String lineContent;

	/**
	 * @param element
	 * @param offset
	 * @param length
	 * @param lineNumber
	 * @param lineContent
	 */
	public AptanaFileSystemMatch(File element, int offset, int length, int lineNumber, String lineContent)
	{
		super(element, offset, length);
		this.lineContent = lineContent != null ? lineContent.trim() : null;
		this.lineNumber = lineNumber;

	}

	/**
	 * @return number
	 */
	public int getLineNumber()
	{
		return this.lineNumber;
	}

	/**
	 * @return content;
	 */
	public String getLineContent()
	{
		if (this.lineContent == null)
		{
			try
			{
				File file = this.getFile();
				if (file != null)
				{
					String readContent = StreamUtils.readContent(new FileInputStream(file), Charset.defaultCharset()
							.name());
					int from = Math.max(0, this.getOffset() - 40);
					int to = Math.min(readContent.length(), this.getOffset() + this.getLength() + 30);
					this.lineContent = readContent.substring(from, to);
				}
			}
			catch (IOException e)
			{
				this.lineContent = StringUtils.format(com.aptana.ide.search.epl.Messages.AptanaFileMatch_IO_ERROR, e
						.getMessage());
				IdeLog.logError(Activator.getDefault(), this.lineContent);
			}

		}
		return this.lineContent;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return null;
	}
}
