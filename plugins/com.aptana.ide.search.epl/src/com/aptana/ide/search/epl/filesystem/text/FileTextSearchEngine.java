/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ide.search.epl.filesystem.text;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.aptana.ide.search.epl.internal.filesystem.text.FileTextSearchVisitor;

/**
 * A {@link FileTextSearchEngine} searches the content of a workspace file resources for matches to a given search
 * pattern.
 * <p>
 * {@link #create()} gives access to an instance of the search engine. By default this is the default text search engine
 * (see {@link #createDefault()}) but extensions can offer more sophisticated search engine implementations.
 * </p>
 * 
 * @since 3.2
 */
public abstract class FileTextSearchEngine
{

	/**
	 * Creates an instance of the search engine. By default this is the default text search engine (see
	 * {@link #createDefault()}), but extensions can offer more sophisticated search engine implementations.
	 * 
	 * @return the created {@link FileTextSearchEngine}.
	 */
	public static FileTextSearchEngine create()
	{
		return null;
		// return SearchPlugin.getDefault().getTextSearchEngineRegistry().getPreferred();
	}

	/**
	 * Creates the default, built-in, text search engine that implements a brute-force search, not using any search
	 * index. Note that clients should always use the search engine provided by {@link #create()}.
	 * 
	 * @return an instance of the default text search engine {@link FileTextSearchEngine}.
	 */
	public static FileTextSearchEngine createDefault()
	{
		return new FileTextSearchEngine()
		{
			public IStatus search(FileSystemTextSearchScope scope, FileTextSearchRequestor requestor,
					Pattern searchPattern, IProgressMonitor monitor)
			{
				return new FileTextSearchVisitor(requestor, searchPattern).search(scope, monitor);
			}

			public IStatus search(File[] scope, FileTextSearchRequestor requestor, Pattern searchPattern,
					IProgressMonitor monitor)
			{
				return new FileTextSearchVisitor(requestor, searchPattern).search(scope, monitor);
			}
		};
	}

	/**
	 * Uses a given search pattern to find matches in the content of workspace file resources. If a file is open in an
	 * editor, the editor buffer is searched.
	 * 
	 * @param requestor
	 *            the search requestor that gets the search results
	 * @param scope
	 *            the scope defining the resources to search in
	 * @param searchPattern
	 *            The search pattern used to find matches in the file contents.
	 * @param monitor
	 *            the progress monitor to use
	 * @return the status containing information about problems in resources searched.
	 */
	public abstract IStatus search(FileSystemTextSearchScope scope, FileTextSearchRequestor requestor,
			Pattern searchPattern, IProgressMonitor monitor);

	/**
	 * Uses a given search pattern to find matches in the content of workspace file resources. If a file is open in an
	 * editor, the editor buffer is searched.
	 * 
	 * @param requestor
	 *            the search requestor that gets the search results
	 * @param scope
	 *            the files to search in
	 * @param searchPattern
	 *            The search pattern used to find matches in the file contents.
	 * @param monitor
	 *            the progress monitor to use
	 * @return the status containing information about problems in resources searched.
	 */
	public abstract IStatus search(File[] scope, FileTextSearchRequestor requestor, Pattern searchPattern,
			IProgressMonitor monitor);

}
