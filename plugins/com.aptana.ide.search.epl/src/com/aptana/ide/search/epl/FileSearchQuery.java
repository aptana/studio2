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
package com.aptana.ide.search.epl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.internal.ui.text.LineElement;
import org.eclipse.search.internal.ui.text.SearchResultUpdater;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope;
import com.aptana.ide.search.epl.filesystem.text.FileTextSearchEngine;
import com.aptana.ide.search.epl.filesystem.text.FileTextSearchMatchAccess;
import com.aptana.ide.search.epl.filesystem.text.FileTextSearchRequestor;
import com.aptana.ide.search.epl.internal.filesystem.text.FileNamePatternSearchScope;
import com.aptana.ide.search.epl.internal.filesystem.text.FileTextSearchVisitor;

/**
 * @author Pavel Petrochenko
 */
public class FileSearchQuery extends
		org.eclipse.search.internal.ui.text.FileSearchQuery implements
		ISearchQuery
{

	/**
	 * @author Pavel Petrochenko
	 */
	private static final class AptanaTextEngine extends TextSearchEngine
	{

		private boolean openEditorsOnly;
		private boolean refresh;

		/**
		 * @see org.eclipse.search.core.text.TextSearchEngine#search(org.eclipse.search.core.text.TextSearchScope,
		 *      org.eclipse.search.core.text.TextSearchRequestor,
		 *      java.util.regex.Pattern,
		 *      org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus search(TextSearchScope scope,
				TextSearchRequestor requestor, Pattern searchPattern,
				IProgressMonitor monitor)
		{
		    return new TextSearchVisitor(requestor, searchPattern,
		            this.openEditorsOnly, this.refresh).search(scope, monitor);
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchEngine#search(org.eclipse.core.resources.IFile[],
		 *      org.eclipse.search.core.text.TextSearchRequestor,
		 *      java.util.regex.Pattern,
		 *      org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus search(IFile[] scope, TextSearchRequestor requestor,
				Pattern searchPattern, IProgressMonitor monitor)
		{
		    return new TextSearchVisitor(requestor, searchPattern,
		            this.openEditorsOnly, false).search(scope, monitor);
		}

		/**
		 * @param isOpenEditorsOnly
		 */
		public void setOpenEditorsOnly(boolean isOpenEditorsOnly)
		{
			this.openEditorsOnly = isOpenEditorsOnly;
		}
		
		/**
		 * @param isOpenEditorsOnly
		 */
		public void needsRefresh(boolean isOpenEditorsOnly)
		{
			this.refresh = isOpenEditorsOnly;
		}
	}

	/**
	 * @author Pavel Petrochenko
	 */
	private static final class TextSearchResultCollector extends TextSearchRequestor
	{

		private final AbstractTextSearchResult fResult;
		private final boolean fIsFileSearchOnly;
		private final boolean fSearchInBinaries;
		private ArrayList fCachedMatches;

		private TextSearchResultCollector(AbstractTextSearchResult result,
				boolean isFileSearchOnly, boolean searchInBinaries)
		{
			this.fResult = result;
			this.fIsFileSearchOnly = isFileSearchOnly;
			this.fSearchInBinaries = searchInBinaries;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#acceptFile(org.eclipse.core.resources.IFile)
		 */
		public boolean acceptFile(IFile file) throws CoreException
		{
			if (this.fIsFileSearchOnly)
			{
                this.fResult.addMatch(new AptanaFileMatch(file, 0, 0,
                        new LineElement(file, 0, 0, ""))); //$NON-NLS-1$
			}
			this.flushMatches();
			return true;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#reportBinaryFile(org.eclipse.core.resources.IFile)
		 */
		public boolean reportBinaryFile(IFile file)
		{
			return this.fSearchInBinaries;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#acceptPatternMatch(org.eclipse.search.core.text.TextSearchMatchAccess)
		 */
		public boolean acceptPatternMatch(TextSearchMatchAccess matchRequestor)
				throws CoreException
		{
		    int matchOffset = matchRequestor.getMatchOffset();

		    LineElement lineElement = getLineElement(matchOffset, matchRequestor);
		    if (lineElement != null) {
                AptanaFileMatch fileMatch = new AptanaFileMatch(matchRequestor
                        .getFile(), matchOffset, matchRequestor
                        .getMatchLength(), lineElement);
		        fCachedMatches.add(fileMatch);
		    }
		    return true;
		}

        private LineElement getLineElement(int offset, TextSearchMatchAccess matchRequestor) {
            int lineNumber= 1;
            int lineStart= 0;
            if (!fCachedMatches.isEmpty()) {
                // match on same line as last?
                FileMatch last = (FileMatch) fCachedMatches.get(fCachedMatches.size() - 1);
                LineElement lineElement = last.getLineElement();
                if (lineElement.contains(offset)) {
                    return lineElement;
                }
                // start with the offset and line information from the last match
                lineStart= lineElement.getOffset() + lineElement.getLength();
                lineNumber= lineElement.getLine() + 1;
            }
            if (offset < lineStart) {
                return null; // offset before the last line
            }

            int i= lineStart;
            int contentLength = matchRequestor.getFileContentLength();
            while (i < contentLength) {
                char ch = matchRequestor.getFileContentChar(i++);
                if (ch == '\n' || ch == '\r') {
                    if (ch == '\r' && i < contentLength && matchRequestor.getFileContentChar(i) == '\n') {
                        i++;
                    }
                    if (offset < i) {
                        String lineContent = getContents(matchRequestor, lineStart, i); // include line delimiter
                        return new LineElement(matchRequestor.getFile(), lineNumber, lineStart, lineContent);
                    }
                    lineNumber++;
                    lineStart= i;
                }
            }
            if (offset < i) {
                String lineContent = getContents(matchRequestor, lineStart, i); // until end of file
                return new LineElement(matchRequestor.getFile(), lineNumber, lineStart, lineContent);
            }
            return null; // offset outside of range
        }

        private static String getContents(TextSearchMatchAccess matchRequestor, int start, int end) {
            StringBuffer buf = new StringBuffer();
            for (int i = start; i < end; i++) {
                char ch = matchRequestor.getFileContentChar(i);
                if (Character.isWhitespace(ch) || Character.isISOControl(ch)) {
                    buf.append(' ');
                } else {
                    buf.append(ch);
                }
            }
            return buf.toString();
        }

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#beginReporting()
		 */
		public void beginReporting()
		{
			this.fCachedMatches = new ArrayList();
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#endReporting()
		 */
		public void endReporting()
		{
			this.flushMatches();
			this.fCachedMatches = null;
		}

		private synchronized void flushMatches()
		{
			if (!this.fCachedMatches.isEmpty())
			{
				this.fResult.addMatches((Match[]) this.fCachedMatches
						.toArray(new Match[this.fCachedMatches.size()]));
				this.fCachedMatches.clear();
			}
		}
	}

	/**
	 * @author Pavel Petrochenko
	 */
	private static final class FileTextSearchResultCollector extends
			FileTextSearchRequestor
	{

		private final AbstractTextSearchResult fResult;
		private final boolean fIsFileSearchOnly;
	    private final boolean fSearchInBinaries;
		private ArrayList fCachedMatches;

		private FileTextSearchResultCollector(AbstractTextSearchResult result,
				boolean isFileSearchOnly, boolean searchInBinaries)
		{
			this.fResult = result;
			this.fIsFileSearchOnly = isFileSearchOnly;
			this.fSearchInBinaries = searchInBinaries;
		}

		/**
		 * @param file
		 * @return
		 * @throws CoreException
		 */
		public boolean acceptFile(File file) throws CoreException
		{
			if (this.fIsFileSearchOnly)
			{
				this.fResult.addMatch(new AptanaFileSystemMatch(file, 0, 0, 0,
						"")); //$NON-NLS-1$
			}
			this.flushMatches();
			return true;
		}

		/**
		 * @param file
		 * @return
		 * @see org.eclipse.search.core.text.TextSearchRequestor#reportBinaryFile(org.eclipse.core.resources.IFile)
		 */
		public boolean reportBinaryFile(File file)
		{
			return this.fSearchInBinaries;
		}

		/**
		 * @param matchRequestor
		 * @return
		 * @throws CoreException
		 * @see org.eclipse.search.core.text.TextSearchRequestor#acceptPatternMatch(org.eclipse.search.core.text.TextSearchMatchAccess)
		 */
		public boolean acceptPatternMatch(TextSearchMatchAccess matchRequestor)
				throws CoreException
		{
			return true;
		}

		/**
		 * @param matchRequestor
		 * @return
		 * @throws CoreException
		 * @see org.eclipse.search.core.text.TextSearchRequestor#acceptPatternMatch(org.eclipse.search.core.text.TextSearchMatchAccess)
		 */
		public boolean acceptPatternMatch(
				FileTextSearchMatchAccess matchRequestor) throws CoreException
		{
			FileTextSearchVisitor.ReusableMatchAccess real = (FileTextSearchVisitor.ReusableMatchAccess) matchRequestor;
			this.fCachedMatches.add(new AptanaFileSystemMatch(matchRequestor
					.getFileSystemFile(), matchRequestor.getMatchOffset(), matchRequestor
					.getMatchLength(), real.getLineNumber(), real
					.getLineContent()));
			return true;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#beginReporting()
		 */
		public void beginReporting()
		{
			this.fCachedMatches = new ArrayList();
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchRequestor#endReporting()
		 */
		public void endReporting()
		{
			this.flushMatches();
			this.fCachedMatches = null;
		}

		private synchronized void flushMatches()
		{
			if (!this.fCachedMatches.isEmpty())
			{
				this.fResult.addMatches((Match[]) this.fCachedMatches
						.toArray(new Match[this.fCachedMatches.size()]));
				this.fCachedMatches.clear();
			}
		}
	}

	private final FileTextSearchScope fScope;
	private final String fSearchText;
	private final boolean fIsRegEx;
	private final boolean fIsCaseSensitive;
	private final boolean isOpenEditorsOnly;
	private final String directory;

	private FileSearchResult fResult;
	private FileSystemSearchResult fDResult;
	private boolean fIsIgnoreLineEndings;
	private boolean refresh;

	/**
	 * @param searchText
	 * @param isRegEx
	 * @param isCaseSensitive
	 * @param scope
	 * @param isOpenEditors
	 * @param directory
	 * @param isIgnoreLineEndings
	 */
	public FileSearchQuery(String searchText, boolean isRegEx,
			boolean isCaseSensitive, FileTextSearchScope scope,
			boolean isOpenEditors, String directory, boolean isIgnoreLineEndings)
	{
		super(searchText, isRegEx, isCaseSensitive, scope);
		this.fSearchText = searchText;
		this.directory = directory;
		this.isOpenEditorsOnly = isOpenEditors;
		this.fIsRegEx = isRegEx;
		this.fIsCaseSensitive = isCaseSensitive;
		this.fScope = scope;
		this.fIsIgnoreLineEndings = isIgnoreLineEndings;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getSearchScope()
	 */
	public FileTextSearchScope getSearchScope()
	{
		return this.fScope;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground()
	{
		return true;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(final IProgressMonitor monitor)
	{
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) this
				.getSearchResult();
		textResult.removeAll();

		Pattern searchPattern = this.getSearchPattern();
		boolean isFileSearchOnly = searchPattern.pattern().length() == 0;
		boolean searchInBinaries = !this.isScopeAllFileTypes();

		if (this.directory != null || this.isOpenEditorsOnly)
		{
			boolean onlyFilesEditorInput = isAllOpenEditorsOnWorkspace();
			if (!onlyFilesEditorInput || !isOpenEditorsOnly)
			{
				FileTextSearchResultCollector fcollector = new FileTextSearchResultCollector(
						textResult, isFileSearchOnly, searchInBinaries);
				String fs = directory == null ? "." : directory; //$NON-NLS-1$
				FileSystemTextSearchScope newSearchScope = FileNamePatternSearchScope
						.newSearchScope(new File[] { new File(fs) },
								this.fScope.getFileNamePatterns());
				newSearchScope.setOpenEditors(this.isOpenEditorsOnly);
				return FileTextSearchEngine.createDefault().search(
						newSearchScope, fcollector, searchPattern, monitor);
			}
		}
		TextSearchResultCollector collector = new TextSearchResultCollector(
				textResult, isFileSearchOnly, searchInBinaries);
		AptanaTextEngine aptanaTextEngine = new AptanaTextEngine();
		aptanaTextEngine.setOpenEditorsOnly(this.isOpenEditorsOnly);
		aptanaTextEngine.needsRefresh(this.refresh);
		return aptanaTextEngine.search(this.fScope, collector, searchPattern,
				monitor);
	}

	private boolean isAllOpenEditorsOnWorkspace()
	{
		boolean onlyFilesEditorInput = true;
		IWorkbench workbench = SearchPlugin.getDefault().getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++)
		{
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int x = 0; x < pages.length; x++)
			{
				IEditorReference[] editorRefs = pages[x].getEditorReferences();
				for (int z = 0; z < editorRefs.length; z++)
				{
					IEditorPart ep = editorRefs[z].getEditor(false);
					if ((ep instanceof ITextEditor))
					{ // only dirty editors

						IEditorInput input = ep.getEditorInput();
						if (input instanceof IFileEditorInput){
							continue;
						}
						if (input instanceof IPathEditorInput)
						{
							onlyFilesEditorInput = false;
						}
					}
				}
			}
		}
		return onlyFilesEditorInput;
	}

	private boolean isScopeAllFileTypes()
	{
		String[] fileNamePatterns = this.fScope.getFileNamePatterns();
		if (fileNamePatterns == null)
		{
			return true;
		}
		for (int i = 0; i < fileNamePatterns.length; i++)
		{
			if ("*".equals(fileNamePatterns[i])) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getLabel()
	 */
	public String getLabel()
	{
		return SearchMessages.FileSearchQuery_label;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getSearchString()
	 */
	public String getSearchString()
	{
		String searchText = this.fSearchText;
		searchText = searchText.replace("\r", "\\r"); //$NON-NLS-1$//$NON-NLS-2$
		searchText = searchText.replace("\n", "\\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if (searchText.length() > 50)
		{
			searchText = StringUtils.ellipsify(searchText.substring(0, 45));
		}
		return searchText;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int nMatches)
	{
		String searchString = this.getSearchString();
		String fdesc = this.directory == null ? this.fScope.getDescription()
				: this.directory;
		if (this.isOpenEditorsOnly)
		{
			fdesc = Messages.OPEN_EDITORS;
		}
		if (searchString.length() > 0)
		{

			// text search
			if (this.isScopeAllFileTypes())
			{
				// search all file extensions
				if (nMatches == 1)
				{
					Object[] args = { searchString,
							this.fScope.getDescription() };
					return MessageFormat.format(
							SearchMessages.FileSearchQuery_singularLabel, args);
				}
				Object[] args = { searchString, new Integer(nMatches), fdesc };
				return MessageFormat.format(
						SearchMessages.FileSearchQuery_pluralPattern, args);
			}
			// search selected file extensions
			if (nMatches == 1)
			{
				Object[] args = { searchString, fdesc,
						this.fScope.getFilterDescription() };
				return MessageFormat
						.format(
								SearchMessages.FileSearchQuery_singularPatternWithFileExt,
								args);
			}

			Object[] args = { searchString, new Integer(nMatches), fdesc,
					this.fScope.getFilterDescription() };
			return MessageFormat.format(
					SearchMessages.FileSearchQuery_pluralPatternWithFileExt,
					args);
		}
		// file search
		if (nMatches == 1)
		{
			Object[] args = { this.fScope.getFilterDescription(), fdesc };
			return MessageFormat
					.format(
							SearchMessages.FileSearchQuery_singularLabel_fileNameSearch,
							args);
		}
		Object[] args = { this.fScope.getFilterDescription(),
				new Integer(nMatches), fdesc };
		return MessageFormat.format(
				SearchMessages.FileSearchQuery_pluralPattern_fileNameSearch,
				args);
	}

	/**
	 * @param result
	 *            all result are added to this search result
	 * @param monitor
	 *            the progress monitor to use
	 * @param file
	 *            the file to search in
	 * @return returns the status of the operation
	 */
	public IStatus searchInFile(final AbstractTextSearchResult result,
			final IProgressMonitor monitor, IFile file)
	{
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(
				new IResource[] { file }, new String[] { "*" }, true); //$NON-NLS-1$		
		Pattern searchPattern = this.getSearchPattern();
		boolean isFileSearchOnly = searchPattern.pattern().length() == 0;
		TextSearchResultCollector collector = new TextSearchResultCollector(
				result, isFileSearchOnly, true);
		return new AptanaTextEngine().search(scope, collector, searchPattern,
				monitor);
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getSearchPattern()
	 */
	protected Pattern getSearchPattern()
	{
		return PatternConstructor
				.createPattern(this.fSearchText, this.fIsCaseSensitive,
						this.fIsRegEx);
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#isRegexSearch()
	 */
	public boolean isRegexSearch()
	{
		return this.fIsRegEx;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#isCaseSensitive()
	 */
	public boolean isCaseSensitive()
	{
		return this.fIsCaseSensitive;
	}

	/**
	 * @return
	 */
	public boolean isIgnoreLineEndings()
	{
		return this.fIsIgnoreLineEndings;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#canRerun()
	 */
	public boolean canRerun()
	{
		return true;
	}

	/**
	 * @see org.eclipse.search.internal.ui.text.FileSearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult()
	{
		if (this.directory != null)
		{
			if (this.fDResult == null)
			{
				this.fDResult = new FileSystemSearchResult(this);
			}
			return this.fDResult;
		}
		if (this.isOpenEditorsOnly)
		{
			if (!isAllOpenEditorsOnWorkspace())
			{
				if (this.fDResult == null)
				{
					this.fDResult = new FileSystemSearchResult(this);
				}
				return this.fDResult;
			}
		}
		if (this.fResult == null)
		{
			this.fResult = new FileSearchResult(this);
			new SearchResultUpdater(this.fResult);
		}
		return this.fResult;
	}

	/**
	 * @return boolean
	 */
	public boolean isOpenEditorsOnly()
	{
		return this.isOpenEditorsOnly;
	}

	/**
	 * @return string
	 */
	public String getDirectory()
	{
		return this.directory;
	}

	/**
	 * @param result
	 * @param monitor
	 * @param entry
	 * @return
	 */
	public IStatus searchInFile(AbstractTextSearchResult result,
			IProgressMonitor monitor, File entry)
	{
		FileSystemTextSearchScope scope = FileSystemTextSearchScope
				.newSearchScope(new File[] { entry },
						new String[] { "*" }, true); //$NON-NLS-1$		
		Pattern searchPattern = this.getSearchPattern();
		boolean isFileSearchOnly = searchPattern.pattern().length() == 0;
		FileTextSearchResultCollector collector = new FileTextSearchResultCollector(
				result, isFileSearchOnly, true);
		return FileTextSearchEngine.createDefault().search(scope, collector,
				searchPattern, monitor);
	}

	public void setRefresh(boolean refresh)
	{
		this.refresh=refresh;
	}
	
	public boolean isRefresh(){
		return this.refresh;
	}
}
