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

import org.eclipse.search.ui.text.Match;

/**
 * @author Pavel Petrochenko
 */
public class FileSystemMatch extends Match
{
	private long fCreationTimeStamp;

	/**
	 * @param element
	 * @param offset
	 * @param length
	 */
	public FileSystemMatch(File element, int offset, int length)
	{
		super(element, offset, length);
		this.fCreationTimeStamp = element.lastModified();
	}

	/**
	 * @return
	 */
	public File getFile()
	{
		return (File) this.getElement();
	}

	/**
	 * @return
	 */
	public long getCreationTimeStamp()
	{
		return this.fCreationTimeStamp;
	}
}