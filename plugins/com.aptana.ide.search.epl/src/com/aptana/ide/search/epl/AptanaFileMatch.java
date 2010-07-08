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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.LineElement;

/**
 * @author Pavel Petrochenko
 */
public class AptanaFileMatch extends FileMatch implements IAdaptable
{
	private final File file;

	/**
	 * @param element
	 * @param offset
	 * @param length
	 * @param lineNumber
	 * @param lineContent
	 */
	public AptanaFileMatch(IFile element, int offset, int length, LineElement lineEntry)
	{
		super(element, offset, length, lineEntry);
		this.file = null;
	}

	/**
	 * @param element
	 * @param offset
	 * @param length
	 * @param lineNumber
	 * @param lineContent
	 */
	public AptanaFileMatch(File element, int offset, int length, LineElement lineEntry)
	{
		super(null, offset, length, lineEntry);
		this.file = element;
	}

	/**
	 * @see org.eclipse.search.ui.text.Match#getElement()
	 */
	public Object getElement()
	{
		if (this.file != null)
		{
			return this.file;
		}
		return super.getElement();
	}

	/**
	 * @return
	 */
	public File getFileElement()
	{
		return this.file;
	}

	/**
	 * @return number
	 */
	public int getLineNumber()
	{
		return getLineElement().getLine();
	}

	/**
	 * @return content;
	 */
	public String getLineContent()
	{
	    return getLineElement().getContents();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return this.getFile().getAdapter(adapter);
	}

}
