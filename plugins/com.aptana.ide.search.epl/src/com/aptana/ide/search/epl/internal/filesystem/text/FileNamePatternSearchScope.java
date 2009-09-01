/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.internal.filesystem.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.search.internal.core.text.PatternConstructor;

import com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope;

/**
 * @author Pavel Petrochenko
 */
public final class FileNamePatternSearchScope extends FileSystemTextSearchScope
{

	/**
	 * Returns a scope for the given resources.
	 * 
	 * @param description
	 *            description of the scope
	 * @param resources
	 *            the resources to be contained
	 * @param includeDerived
	 *            specifies if derived resources are included or not
	 * @return a scope for the given resources.
	 */
	public static FileNamePatternSearchScope newSearchScope(String description, File[] resources, boolean includeDerived)
	{
		return new FileNamePatternSearchScope(description, FileNamePatternSearchScope.removeRedundantEntries(resources,
				includeDerived), includeDerived);
	}

	private static final boolean IS_CASE_SENSITIVE_FILESYSTEM = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

	private final String fDescription;
	private final File[] fRootElements;

	private final Set fFileNamePatterns;
	private Matcher fFileNameMatcher;

	private boolean fVisitDerived;

	private FileNamePatternSearchScope(String description, File[] resources, boolean visitDerived)
	{
		Assert.isNotNull(description);
		this.fDescription = description;
		this.fRootElements = resources;
		this.fFileNamePatterns = new HashSet(3);
		this.fFileNameMatcher = null;
		this.fVisitDerived = visitDerived;
	}

	/**
	 * Returns the description of the scope
	 * 
	 * @return the description of the scope
	 */
	public String getDescription()
	{
		return this.fDescription;
	}

	
	/**
	 * @see com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope#getRoots()
	 */
	public File[] getRoots()
	{
		return this.fRootElements;
	}

	
	/**
	 * @see com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope#contains(java.io.File)
	 */
	public boolean contains(File proxy)
	{
		if (proxy.isFile())
		{
			return this.matchesFileName(proxy.getName());
		}
		return true;
	}

	/**
	 * Adds an file name pattern to the scope.
	 * 
	 * @param pattern
	 */
	public void addFileNamePattern(String pattern)
	{
		if (this.fFileNamePatterns.add(pattern))
		{
			this.fFileNameMatcher = null; // clear cache
		}
	}

	/**
	 * @param pattern
	 */
	public void setFileNamePattern(Pattern pattern)
	{
		this.fFileNameMatcher = pattern.matcher(""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public Pattern getFileNamePattern()
	{
		return this.getFileNameMatcher().pattern();
	}

	/**
	 * Returns if derived resources are included in the scope.
	 * 
	 * @return if set derived resources are included in the scope.
	 */
	public boolean isIncludeDerived()
	{
		return this.fVisitDerived;
	}

	private Matcher getFileNameMatcher()
	{
		if (this.fFileNameMatcher == null)
		{
			Pattern pattern;
			if (this.fFileNamePatterns.isEmpty())
			{
				pattern = Pattern.compile(".*"); //$NON-NLS-1$
			}
			else
			{
				String[] patternStrings = (String[]) this.fFileNamePatterns.toArray(new String[this.fFileNamePatterns
						.size()]);
				pattern = PatternConstructor.createPattern(patternStrings,
						FileNamePatternSearchScope.IS_CASE_SENSITIVE_FILESYSTEM);
			}
			this.fFileNameMatcher = pattern.matcher(""); //$NON-NLS-1$
		}
		return this.fFileNameMatcher;
	}

	/**
	 * Tests if a file name matches to the file name patterns contained in the scope
	 * 
	 * @param fileName
	 *            The file name to test
	 * @return returns true if the file name is matching to a file name pattern
	 */
	private boolean matchesFileName(String fileName)
	{
		return this.getFileNameMatcher().reset(fileName).matches();
	}

	/**
	 * Returns a description for the file name patterns in the scope
	 * 
	 * @return the description of the scope
	 */
	public String getFileNamePatternDescription()
	{
		String[] ext = (String[]) this.fFileNamePatterns.toArray(new String[this.fFileNamePatterns.size()]);
		Arrays.sort(ext);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < ext.length; i++)
		{
			if (i > 0)
			{
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(ext[i]);
		}
		return buf.toString();
	}

	private static File[] removeRedundantEntries(File[] elements, boolean includeDerived)
	{
		ArrayList res = new ArrayList();
		for (int i = 0; i < elements.length; i++)
		{
			File curr = elements[i];
			FileNamePatternSearchScope.addToList(res, curr, includeDerived);
		}
		return (File[]) res.toArray(new File[res.size()]);
	}

	private static void addToList(ArrayList res, File curr, boolean includeDerived)
	{
		if (!includeDerived && FileNamePatternSearchScope.isDerived(curr))
		{
			return;
		}
		IPath currPath = new Path(curr.getAbsolutePath());
		for (int k = res.size() - 1; k >= 0; k--)
		{
			File other = (File) res.get(k);
			IPath otherPath = new Path(other.getAbsolutePath());
			if (otherPath.isPrefixOf(currPath))
			{
				return;
			}
			if (currPath.isPrefixOf(otherPath))
			{
				res.remove(k);
			}
		}
		res.add(curr);
	}

	private static boolean isDerived(File curr)
	{
		return false;
	}

	/**
	 * @param files
	 * @param fileNamePatterns
	 * @return
	 */
	public static FileSystemTextSearchScope newSearchScope(File[] files, String[] fileNamePatterns)
	{
		FileNamePatternSearchScope fileNamePatternSearchScope = new FileNamePatternSearchScope("", files, true); //$NON-NLS-1$
		for (int a = 0; a < fileNamePatterns.length; a++)
		{
			fileNamePatternSearchScope.addFileNamePattern(fileNamePatterns[a]);
		}
		return fileNamePatternSearchScope;
	}

}
