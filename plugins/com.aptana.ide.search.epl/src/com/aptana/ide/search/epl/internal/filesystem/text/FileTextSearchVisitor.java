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
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.internal.core.text.DocumentCharSequence;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope;
import com.aptana.ide.search.epl.filesystem.text.FileTextSearchMatchAccess;
import com.aptana.ide.search.epl.filesystem.text.FileTextSearchRequestor;

/**
 * The visitor that does the actual work.
 */
public class FileTextSearchVisitor
{

	/**
	 * @author Pavel Petrochenko
	 */
	public static class ReusableMatchAccess extends FileTextSearchMatchAccess
	{

		private static final int MAX_LINE_LENGTH = 300;
		private int fOffset;
		private int fLength;
		private int lineNumber = -1;
		private File fFile;
		private CharSequence fContent;
		private String lineContent;

		/**
		 * @return line number
		 */
		public int getLineNumber()
		{
			return this.lineNumber;
		}

		/**
		 * @return line content
		 */
		public String getLineContent()
		{
			return this.lineContent;
		}

		/**
		 * @param file
		 * @param offset
		 * @param length
		 * @param content
		 */
		public void initialize(File file, int offset, int length, CharSequence content)
		{
			this.fFile = file;
			this.fOffset = offset;
			this.fLength = length;
			this.fContent = content;
		}

		/**
		 * @param file
		 * @param start
		 * @param length
		 * @param content
		 * @param document
		 */
		public void initialize(File file, int start, int length, CharSequence content, IDocument document)
		{
			initialize(file, start, length, content);
			if (document != null)
			{
				try
				{

					this.lineNumber = document.getLineOfOffset(start);
					IRegion lineInformation = document.getLineInformation(this.lineNumber);
					if (lineInformation.getLength() < ReusableMatchAccess.MAX_LINE_LENGTH)
					{
						this.lineContent = document.get(lineInformation.getOffset(), lineInformation.getLength());
					}
					else
					{
						int ka = start - ReusableMatchAccess.MAX_LINE_LENGTH / 3;
						if (ka < 0)
						{
							ka = 0;
						}
						int max = Math.min(document.getLength() - 1, ka + ReusableMatchAccess.MAX_LINE_LENGTH);
						this.lineContent = document.get(ka, max);
					}
				}
				catch (BadLocationException e)
				{
				}
			}
			else
			{
				int ka = start - ReusableMatchAccess.MAX_LINE_LENGTH / 3;
				if (ka < 0)
				{
					ka = 0;
				}
				int max = Math.min(content.length() - 1, ka + ReusableMatchAccess.MAX_LINE_LENGTH);
				this.lineContent = content.subSequence(ka, max).toString();
			}
		}

		/**
		 * @see com.aptana.ide.search.epl.filesystem.text.FileTextSearchMatchAccess#getFileSystemFile()
		 */
		public File getFileSystemFile()
		{
			return this.fFile;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchMatchAccess#getMatchOffset()
		 */
		public int getMatchOffset()
		{
			return this.fOffset;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchMatchAccess#getMatchLength()
		 */
		public int getMatchLength()
		{
			return this.fLength;
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchMatchAccess#getFileContentLength()
		 */
		public int getFileContentLength()
		{
			return this.fContent.length();
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchMatchAccess#getFileContentChar(int)
		 */
		public char getFileContentChar(int offset)
		{
			return this.fContent.charAt(offset);
		}

		/**
		 * @see org.eclipse.search.core.text.TextSearchMatchAccess#getFileContent(int, int)
		 */
		public String getFileContent(int offset, int length)
		{
			return this.fContent.subSequence(offset, offset + length).toString(); // must pass a copy!
		}

	}

	private final FileTextSearchRequestor fCollector;
	private final Matcher fMatcher;

	private Map fDocumentsInEditors;

	private IProgressMonitor fProgressMonitor;

	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;
	private File fCurrentFile;

	private final MultiStatus fStatus;

	private final FileCharSequenceProvider fFileCharSequenceProvider;

	private final ReusableMatchAccess fMatchAccess;

	/**
	 * @param collector
	 * @param searchPattern
	 */
	public FileTextSearchVisitor(FileTextSearchRequestor collector, Pattern searchPattern)
	{
		this.fCollector = collector;
		this.fStatus = new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK,
				SearchMessages.TextSearchEngine_statusMessage, null);

		this.fMatcher = searchPattern.pattern().length() == 0 ? null : searchPattern.matcher(new String());

		this.fFileCharSequenceProvider = new FileCharSequenceProvider();
		this.fMatchAccess = new ReusableMatchAccess();
	}

	/**
	 * @param files
	 * @param monitor
	 * @return
	 */
	public IStatus search(File[] files, IProgressMonitor monitor)
	{
		this.fProgressMonitor = monitor == null ? new NullProgressMonitor() : monitor;
		this.fNumberOfScannedFiles = 0;
		this.fNumberOfFilesToScan = files.length;
		this.fCurrentFile = null;
		Job monitorUpdateJob = new Job(SearchMessages.TextSearchVisitor_progress_updating_job)
		{
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(IProgressMonitor inner)
			{
				while (!inner.isCanceled())
				{
					File file = FileTextSearchVisitor.this.fCurrentFile;
					if (file != null)
					{
						String fileName = file.getName();
						Object[] args = { fileName, new Integer(FileTextSearchVisitor.this.fNumberOfScannedFiles),
								new Integer(FileTextSearchVisitor.this.fNumberOfFilesToScan) };
						FileTextSearchVisitor.this.fProgressMonitor.subTask(Messages.format(
								SearchMessages.TextSearchVisitor_scanning, args));
						int steps = FileTextSearchVisitor.this.fNumberOfScannedFiles - this.fLastNumberOfScannedFiles;
						FileTextSearchVisitor.this.fProgressMonitor.worked(steps);
						this.fLastNumberOfScannedFiles += steps;
					}
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};

		try
		{
			String taskName = this.fMatcher == null ? SearchMessages.TextSearchVisitor_filesearch_task_label : Messages
					.format(SearchMessages.TextSearchVisitor_textsearch_task_label, this.fMatcher.pattern().pattern());
			this.fProgressMonitor.beginTask(taskName, this.fNumberOfFilesToScan);
			monitorUpdateJob.setSystem(true);
			monitorUpdateJob.schedule();
			try
			{
				initDocuments();
				
				this.fCollector.beginReporting();
				this.processFiles(files);
				return this.fStatus;
			}
			finally
			{
				monitorUpdateJob.cancel();
			}
		}
		finally
		{
			this.fProgressMonitor.done();
			this.fCollector.endReporting();

		}
	}

	/**
	 * @param scope
	 * @param monitor
	 * @return result
	 */
	public IStatus search(FileSystemTextSearchScope scope, IProgressMonitor monitor)
	{
		monitor.setTaskName(com.aptana.ide.search.epl.internal.filesystem.text.Messages.START_SEARCH);
		File[] evaluateFilesInScope = scope.evaluateFilesInScope(this.fStatus);
		monitor.setTaskName(com.aptana.ide.search.epl.internal.filesystem.text.Messages.PERFORM_SEARCH);
		return this.search(evaluateFilesInScope, monitor);
	}

	/**
	 * 
	 */
	public void initDocuments()
	{
		this.fDocumentsInEditors = this.evalNonFileBufferDocuments();
	}

	private void processFiles(File[] files)
	{
		this.fDocumentsInEditors = this.evalNonFileBufferDocuments();
		for (int i = 0; i < files.length; i++)
		{
			this.fCurrentFile = files[i];
			boolean res = this.processFile(this.fCurrentFile);
			if (!res)
			{
				break;
			}
		}
		this.fDocumentsInEditors = null;
	}

	/**
	 * @return returns a map from File to IDocument for all open, dirty editors
	 */
	private Map evalNonFileBufferDocuments()
	{
		Map result = new HashMap();
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
					if ((ep instanceof ITextEditor) && ep.isDirty())
					{ // only dirty editors
						this.evaluateTextEditor(result, ep);
					}
				}
			}
		}
		return result;
	}

	private void evaluateTextEditor(Map result, IEditorPart ep)
	{
		IEditorInput input = ep.getEditorInput();
		if (input instanceof IPathEditorInput)
		{
			File file = new File(((IPathEditorInput) input).getPath().toOSString());
			processFile(result, ep, input, file);
		}
		else if (input instanceof IURIEditorInput) {
			URI uri = ((IURIEditorInput) input).getURI();
			if ("file".equals(uri.getScheme())) {
				processFile(result, ep, input, new File(uri));				
			}
		}
		else if (input instanceof IFileEditorInput){
			IFileEditorInput fi=(IFileEditorInput) input;
			processFile(result, ep, input, fi.getFile().getLocation().toFile());
		}
	}

	private void processFile(Map result, IEditorPart ep, IEditorInput input,
			File file)
	{
		if (!result.containsKey(file))
		{ // take the first editor found
			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(new Path(file.getAbsolutePath()), LocationKind.LOCATION);
			if (textFileBuffer != null)
			{
				// file buffer has precedence
				result.put(file, textFileBuffer.getDocument());
			}
			else
			{
				// use document provider
				IDocument document = ((ITextEditor) ep).getDocumentProvider().getDocument(input);
				if (document != null)
				{
					result.put(file, document);
				}
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public boolean processFile(File file)
	{
		try
		{
			if (!this.fCollector.acceptFile(file) || (this.fMatcher == null))
			{
				return true;
			}

			IDocument document = this.getOpenDocument(file);

			if (document != null)
			{
				DocumentCharSequence documentCharSequence = new DocumentCharSequence(document);
				// assume all documents are non-binary
				this.locateMatches(file, documentCharSequence, document);
			}
			else
			{
				CharSequence seq = null;
				try
				{	
					seq = this.fFileCharSequenceProvider.newCharSequence(file);
					Document doc = null;
					if (seq.length() < 1000 * 1000)
					{
						String string = seq.toString();
						doc = new Document(string);
						seq = string;
					}
					if (!this.fCollector.reportBinaryFile(file) && this.hasBinaryContent(seq, file))
					{
						return true;
					}
					this.locateMatches(file, seq, doc);
				}
				catch (FileCharSequenceProvider.FileCharSequenceException e)
				{
					e.throwWrappedException();
				}
				finally
				{
					if (seq != null)
					{
						try
						{
							this.fFileCharSequenceProvider.releaseCharSequence(seq);
						}
						catch (IOException e)
						{
							SearchPlugin.log(e);
						}
					}
				}
			}
		}
		catch (UnsupportedCharsetException e)
		{
			String[] args = { this.getCharSetName(file), file.getAbsolutePath().toString() };
			String message = Messages.format(SearchMessages.TextSearchVisitor_unsupportedcharset, args);
			this.fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		}
		catch (IllegalCharsetNameException e)
		{
			String[] args = { this.getCharSetName(file), file.getAbsolutePath().toString() };
			String message = Messages.format(SearchMessages.TextSearchVisitor_illegalcharset, args);
			this.fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		}
		catch (IOException e)
		{
			String[] args = { this.getExceptionMessage(e), file.getAbsolutePath().toString() };
			String message = Messages.format(SearchMessages.TextSearchVisitor_error, args);
			this.fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		}
		catch (CoreException e)
		{
			String[] args = { this.getExceptionMessage(e), file.getAbsolutePath().toString() };
			String message = Messages.format(SearchMessages.TextSearchVisitor_error, args);
			this.fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		}
		catch (StackOverflowError e)
		{
			String message = SearchMessages.TextSearchVisitor_patterntoocomplex0;
			this.fStatus.add(new Status(IStatus.ERROR, NewSearchUI.PLUGIN_ID, IStatus.ERROR, message, e));
			return false;
		}
		finally
		{
			this.fNumberOfScannedFiles++;
		}
		if (this.fProgressMonitor.isCanceled())
		{
			throw new OperationCanceledException(SearchMessages.TextSearchVisitor_canceled);
		}

		return true;
	}

	private boolean hasBinaryContent(CharSequence seq, File file) throws CoreException
	{
        try {
            int limit = FileCharSequenceProvider.BUFFER_SIZE;
            for (int i = 0; i < limit; i++) {
                if (seq.charAt(i) == '\0') {
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return false;
	}

	private void locateMatches(File file, CharSequence searchInput, IDocument document) throws CoreException
	{
		try
		{
			this.fMatcher.reset(searchInput);
			int k = 0;
			while (this.fMatcher.find())
			{
				int start = this.fMatcher.start();
				int end = this.fMatcher.end();
				if (end != start)
				{ // don't report 0-length matches
					
					//converting from character indices to code point indices
					start = Character.codePointCount(searchInput, 0, start);
					end = Character.codePointCount(searchInput, 0, end);
					
					//reporting
					this.fMatchAccess.initialize(file, start, end - start, searchInput, document);
					boolean res = this.fCollector.acceptPatternMatch(this.fMatchAccess);
					if (!res)
					{
						return; // no further reporting requested
					}
				}
				if (k++ == 20)
				{
					if (this.fProgressMonitor.isCanceled())
					{
						throw new OperationCanceledException(SearchMessages.TextSearchVisitor_canceled);
					}
					k = 0;
				}
			}
		}
		finally
		{
			this.fMatchAccess.initialize(null, 0, 0, new String()); // clear references
		}
	}

	private String getExceptionMessage(Exception e)
	{
		String message = e.getLocalizedMessage();
		if (message == null)
		{
			return e.getClass().getName();
		}
		return message;
	}

	private IDocument getOpenDocument(File file)
	{
		IDocument document = (IDocument) this.fDocumentsInEditors.get(file);
		if (document == null)
		{
			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(new Path(file.getAbsolutePath()), LocationKind.LOCATION);
			if (textFileBuffer != null)
			{
				document = textFileBuffer.getDocument();
			}
		}
		return document;
	}

	private String getCharSetName(File file)
	{
		return Charset.defaultCharset().name();
	}

	/**
	 * @return
	 */
	public IProgressMonitor getFProgressMonitor()
	{
		return this.fProgressMonitor;
	}

	/**
	 * @param progressMonitor
	 */
	public void setFProgressMonitor(IProgressMonitor progressMonitor)
	{
		this.fProgressMonitor = progressMonitor;
	}
}
