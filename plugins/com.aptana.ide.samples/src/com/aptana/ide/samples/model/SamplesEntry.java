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
package com.aptana.ide.samples.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SamplesEntry
{

	private SamplesInfo parent;
	private SamplesEntry root;
	private boolean isRoot;
	private File file;
	private List<SamplesEntry> subEntries;

	/**
	 * Creates a new sample entry
	 * 
	 * @param parent
	 * @param file
	 * @param isRoot
	 */
	public SamplesEntry(SamplesInfo parent, File file, boolean isRoot)
	{
		this.parent = parent;
		this.isRoot = isRoot;
		this.file = file;
		if (isRoot)
		{
			this.root = this;
		}
		else
		{
			this.root = null;
		}
	}

	/**
	 * Sets the root (meaning the actual sample root)
	 * 
	 * @param root
	 */
	public void setRoot(SamplesEntry root)
	{
		this.root = root;
	}

	/**
	 * Gets the sample root
	 * 
	 * @return - root sample
	 */
	public SamplesEntry getRoot()
	{
		return this.root;
	}

	/**
	 * Gets the sub entries
	 * 
	 * @return - list of sub entries
	 */
	public List<SamplesEntry> getSubEntries()
	{
		// Cache miss
		if (this.subEntries == null) {
			this.subEntries = new ArrayList<SamplesEntry>();
			File file = getFile();
			if (file != null)
			{
				File[] files = file.listFiles();
				if (files != null)
				{
					for (int i = 0; i < files.length; i++)
					{
						SamplesEntry newEntry = new SamplesEntry(this.parent, files[i], false);
						newEntry.setRoot(getRoot());
						this.subEntries.add(newEntry);
					}
				}
			}
		}
		return this.subEntries;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * @return the isRoot
	 */
	public boolean isRoot()
	{
		return isRoot;
	}

	/**
	 * @param isRoot
	 *            the isRoot to set
	 */
	public void setRoot(boolean isRoot)
	{
		this.isRoot = isRoot;
	}

	/**
	 * @return the parent
	 */
	public SamplesInfo getParent()
	{
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(SamplesInfo parent)
	{
		this.parent = parent;
	}

}
