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
package com.aptana.ide.editor.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.xml.sax.SAXException;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.resources.IUniformResource;
import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.core.ui.editors.ISaveAsEvent;
import com.aptana.ide.core.ui.editors.ISaveEvent;
import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;
import com.aptana.ide.editors.formatting.UnifiedBracketInserter;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.BaseDocumentProvider;
import com.aptana.ide.editors.unified.BaseFileLanguageService;
import com.aptana.ide.editors.unified.BaseFileServiceFactory;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileServiceChangeListener;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.editors.unified.errors.UnifiedErrorManager;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexerBuilder;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.views.outline.UnifiedOutlinePage;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;
import com.aptana.sax.AttributeSniffer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class GenericTextEditor extends EditorPart implements ITextEditor, ITextEditorExtension, IUnifiedEditor
{

	/**
	 * Generic text editor ID
	 */
	public static final String ID = "com.aptana.ide.editor.text"; //$NON-NLS-1$

	/**
	 * PLAIN_MIME_TYPE
	 */
	public static final String PLAIN_MIME_TYPE = "text/plain"; //$NON-NLS-1$

	private UnifiedEditor editor;

	private BaseContributor contributor;

	private IFileServiceFactory fileService;

	private BaseDocumentProvider documentProvider;

	private IParser parser;

	private File grammarFile;

	private Job grammarFileMonitor;

	private String language;

	private String extension;

	private Shell shell;

	private class GenericDocumentProvider extends BaseDocumentProvider
	{
		/**
		 * Bundle of all required informations to allow working copy management.
		 */
		protected class GenericFileInfo extends FileInfo
		{
			/**
			 * sourceProvider
			 */
			public IFileSourceProvider sourceProvider;
		}

		/**
		 * Creates a new generic document provider
		 */
		public GenericDocumentProvider()
		{
			super();
		}

		/**
		 * getFileInfoPublic
		 * 
		 * @param element
		 * @return FileInfo
		 */
		public FileInfo getFileInfoPublic(Object element)
		{
			return getFileInfo(element);
		}

		/**
		 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createEmptyFileInfo()
		 */
		protected FileInfo createEmptyFileInfo()
		{
			return new GenericFileInfo();
		}

		/**
		 * @see org.eclipse.ui.texteditor.IDocumentProvider#getAnnotationModel(java.lang.Object)
		 */
		public IAnnotationModel getAnnotationModel(Object element)
		{
			IAnnotationModel annotationModel = super.getAnnotationModel(element);
			if (annotationModel == null)
			{
				FileInfo fileInfo = getFileInfo(element);
				if (fileInfo != null)
				{
					annotationModel = fileInfo.fModel;
				}
			}
			return annotationModel;
		}

		/**
		 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createFileInfo(java.lang.Object)
		 */
		protected FileInfo createFileInfo(Object element) throws CoreException
		{
			if (!(element instanceof IEditorInput))
			{
				return null;
			}

			try
			{
				FileInfo info = super.createFileInfo(element);

				if (!(info instanceof GenericFileInfo))
				{
					return null;
				}

				GenericFileInfo cuInfo = (GenericFileInfo) info;
				if (element instanceof IAdaptable)
				{
					// If this is check is not added, workspace files (those in
					// projects) lose their annotations (like
					// debugger markers)
					if (cuInfo.fTextFileBuffer.getAnnotationModel() == null)
					{
						IUniformResource uniformResource = (IUniformResource) ((IAdaptable) element)
								.getAdapter(IUniformResource.class);
						if (uniformResource != null)
						{
						}
						else
						{
							cuInfo.fModel = new AnnotationModel();
						}
					}
					else
					{
						cuInfo.fModel = cuInfo.fTextFileBuffer.getAnnotationModel();
					}
				}

				return info;
			}
			catch (RuntimeException ex)
			{
				IdeLog.logError(TextPlugin.getDefault(), Messages.GenericTextEditor_ERROR, ex);
			}
			return null;
		}

		/**
		 * disconnects the doc provider.
		 * 
		 * @param element
		 */
		public void disconnect(Object element)
		{
			GenericFileInfo cuInfo = (GenericFileInfo) getFileInfo(element);

			if (cuInfo != null && cuInfo.fCount == 1)
			{
				IFileSourceProvider sourceProvider = cuInfo.sourceProvider;

				if (sourceProvider != null)
				{
					String uri = sourceProvider.getSourceURI();

					if (uri != null)
					{
						FileContextManager.disconnectSourceProvider(uri, cuInfo.sourceProvider);
					}
					// cuInfo.fileService.disconnectSourceProvider(cuInfo.sourceProvider);
				}
			}

			// This has to be after the call to getFileInfo() because otherwise
			// what we're
			// looking for will be gone.
			try
			{
				super.disconnect(element);
			}
			catch (RuntimeException ex)
			{
				IdeLog.logError(TextPlugin.getDefault(), Messages.GenericTextEditor_ERROR, ex);
			}
		}

		/**
		 * @see org.eclipse.ui.texteditor.IDocumentProvider#connect(java.lang.Object)
		 */
		public void connect(Object element) throws CoreException
		{
			super.connect(element);
		}

	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		if (editor != null)
		{
			editor.doSave(monitor);
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs()
	{
		if (editor != null)
		{
			editor.doSaveAs();
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getSite()
	 */
	public IWorkbenchPartSite getSite()
	{
		return editor != null ? editor.getSite() : null;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	public IEditorInput getEditorInput()
	{
		return editor != null ? editor.getEditorInput() : null;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getPartName()
	 */
	public String getPartName()
	{
		return editor != null ? editor.getPartName() : ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#getEditorSite()
	 */
	public IEditorSite getEditorSite()
	{
		return editor != null ? editor.getEditorSite() : null;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		// Find file extension
		String fileName = input.getName();
		String ext = FileUtils.getExtension(fileName);
		if (ext == null || ext.length() == 0)
		{
			if (input instanceof NonExistingFileEditorInput)
			{
				IPath path = ((NonExistingFileEditorInput) input).getPath(input);
				if (path != null)
				{
					fileName = path.lastSegment();
					ext = FileUtils.getExtension(fileName);
				}
			}
		}
		IPreferenceStore store = TextPlugin.getDefault().getPreferenceStore();
		IEditorRegistry registry = EclipseUIUtils.getWorkbenchEditorRegistry();
		IFileEditorMapping[] mappings = registry.getFileEditorMappings();
		IFileEditorMapping candidate = null;
		for (int i = 0; i < mappings.length; i++)
		{
			if (mappings[i].getLabel().equals(fileName))
			{
				candidate = mappings[i];
				break;
			}
			else if (mappings[i].getExtension().equals(ext))
			{
				candidate = mappings[i];
			}
		}
		if (candidate != null)
		{
			String label = candidate.getLabel();
			String grammarFilePath = store.getString(TextPlugin.getGrammarPreference(label));
			String colorizerPath = TextPlugin.getColorizerPreference(label);
			extension = ext;

			// This handles file extensions that we want to open with the generic text editor but use both contributed
			// (via extension point) lexers and colorizers
			TokenList tokenList = LanguageRegistry.getTokenListByExtension(extension);
			if (tokenList != null)
			{
				language = tokenList.getLanguage();

				// create parser
				try
				{
					if (LanguageRegistry.hasParser(language))
					{
						parser = LanguageRegistry.getParser(language);
					}
					else
					{
						parser = new UnifiedParser(language);
					}
				}
				catch (ParserInitializationException e)
				{
					throw new PartInitException(e.getMessage());
				}
			}
			else
			{
				grammarFile = new File(grammarFilePath);
			}
			FileInputStream stream = null;
			try
			{
				if (grammarFile != null && grammarFile.exists() && grammarFile.isFile() && grammarFile.canRead())
				{
					if (language == null)
					{
						AttributeSniffer sniffer = new AttributeSniffer("lexer", "language"); //$NON-NLS-1$ //$NON-NLS-2$
						sniffer.read(grammarFilePath);
						if (sniffer.getMatchedValue() != null)
						{
							language = sniffer.getMatchedValue();
						}
						else
						{
							throw new PartInitException(Messages.GenericTextEditor_No_Language_Defined);
						}
					}
					if (parser == null)
					{
						parser = createParser();
					}
				}
				if (language != null)
				{
					ColorizerReader reader = new ColorizerReader();
					if (LanguageRegistry.hasLanguageColorizer(language))
					{
						LanguageRegistry.getLanguageColorizer(language);
					}
					else
					{
						reader.loadColorization(colorizerPath, true);
					}
				}
				else
				{
					language = PLAIN_MIME_TYPE;
					final TokenList list = new TokenList(language);
					parser = new UnifiedParser(language)
					{
						public void addLexerGrammar(ILexerBuilder builder) throws LexerException
						{
							builder.addTokenList(list);
						}
					};
				}
				if (parser != null)
				{
					createFileServiceFactory();
				}
				contributor = createContributor();
				documentProvider = new GenericDocumentProvider();
				createEditor();
				editor.init(site, input);
				if (grammarFile != null && grammarFile.exists())
				{
					createGrammarFileMonitor();
				}
			}
			catch (ParserInitializationException e)
			{
				e.printStackTrace();
				throw new PartInitException(e.getMessage());
			}
			catch (FileNotFoundException e)
			{
				throw new PartInitException(e.getMessage());
			}
			catch (IOException e)
			{
				throw new PartInitException(e.getMessage());
			}
			catch (ParserConfigurationException e)
			{
				throw new PartInitException(e.getMessage());
			}
			catch (SAXException e)
			{
				throw new PartInitException(e.getMessage());
			}
			finally
			{
				if (stream != null)
				{
					try
					{
						stream.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			throw new PartInitException(Messages.GenericTextEditor_ERROR_RETRIEVING_ASSOCIATION);
		}
	}

	/**
	 * Creates the job that monitors the underlying grammar file being used. If the grammar file changes the parser is
	 * recreated and an entire reparse is done. The parser is only changed if the new grammar file parses correctly.
	 */
	private void createGrammarFileMonitor()
	{
		grammarFileMonitor = new Job(Messages.GenericTextEditor_MONITOR_GRAMMAR_FILE)
		{

			private long stamp = grammarFile.lastModified();

			protected IStatus run(IProgressMonitor monitor)
			{
				if (stamp < grammarFile.lastModified())
				{
					stamp = grammarFile.lastModified();
					TokenList oldList = LanguageRegistry.getTokenList(language);

					if (oldList != null)
					{
						LanguageRegistry.unregisterTokenList(oldList);
					}

					try
					{
						IParser newParser = createParser();
						FileService context = FileContextManager.get(GenericTextEditor.this.getEditorInput());
						parser = newParser;
						context.setParser(parser);
						editor.getFileContext().setFileContext(context);
						context.doFullParse();
						UIJob refreshEditor = new UIJob(Messages.GenericTextEditor_REFRESHING_LEXER)
						{

							public IStatus runInUIThread(IProgressMonitor monitor)
							{
								editor.getViewer().getTextWidget().redraw();
								editor.getViewer().getTextWidget().update();
								return Status.OK_STATUS;
							}

						};
						refreshEditor.schedule();
					}
					catch (final Exception e)
					{
						if (oldList != null)
						{
							LanguageRegistry.registerTokenList(oldList);
						}
						UIJob errorJob = new UIJob(Messages.GenericTextEditor_ERROR_PARSING_LEXER)
						{

							public IStatus runInUIThread(IProgressMonitor monitor)
							{
								MessageDialog.openError(shell, Messages.GenericTextEditor_ERROR_PARSING_LEXER,
										Messages.GenericTextEditor_ERROR_OCCURED_DURING_PARSE_LEXER);
								return Status.OK_STATUS;
							}

						};
						errorJob.schedule();
					}
				}
				this.schedule(1000);
				return Status.OK_STATUS;
			}

		};
		grammarFileMonitor.setSystem(true);
		grammarFileMonitor.schedule(1000);
	}

	/**
	 * Creates the underlying unified editor.
	 */
	private void createEditor()
	{
		editor = new UnifiedEditor()
		{

			public IFileServiceFactory getFileServiceFactory()
			{
				return fileService;
			}

			public String getDefaultFileExtension()
			{
				return extension;
			}

			protected IUnifiedEditorContributor createLocalContributor()
			{
				return contributor;
			}

			public IDocumentProvider createDocumentProvider()
			{
				return documentProvider;
			}

			protected String[] collectContextMenuPreferencePages() {
				return GenericTextEditor.this.collectContextMenuPreferencePages();
			}

		};
		editor.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				firePropertyChange(propId);
			}

		});
	}

	protected String[] collectContextMenuPreferencePages()
	{
		return new String[] {
				"com.aptana.ide.editor.text.preferences.TextEditorPreferencePage", //$NON-NLS-1$
				"org.eclipse.ui.preferencePages.GeneralTextEditor", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Annotations", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.QuickDiff", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Accessibility", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Spelling", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.LinkedModePreferencePage", //$NON-NLS-1$
			};
	}

	/**
	 * Creates the base contributor used by the editor
	 */
	protected BaseContributor createContributor()
	{
		return new BaseContributor(language)
		{
			/**
			 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
			 *      java.lang.String)
			 */
			public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
			{
				return new IAutoEditStrategy[] { new UnifiedAutoIndentStrategy(this.getFileContext(), this
						.getParentConfiguration(), sourceViewer)
				{
					/**
					 * getLexemList
					 */
					protected LexemeList getLexemeList()
					{
						return getFileContext().getLexemeList();
					}

					/**
					 * getPreferenceStore
					 */
					public IPreferenceStore getPreferenceStore()
					{
						return TextPlugin.getDefault().getPreferenceStore();
					}
				} };
			}

			/**
			 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalBracketInserter(org.eclipse.jface.text.source.ISourceViewer,
			 *      java.lang.String)
			 */
			public IUnifiedBracketInserter getLocalBracketInserter(ISourceViewer sourceViewer, String contentType)
			{
				EditorFileContext context = getFileContext();
				IUnifiedBracketInserter result = null;

				if (context != null)
				{
					result = new UnifiedBracketInserter(sourceViewer, context)
					{
						protected IPreferenceStore getPreferenceStore()
						{
							return TextPlugin.getDefault().getPreferenceStore();
						}
					};
				}

				return result;
			}

			/**
			 * getReconcilingStrategy
			 * 
			 * @return UnifiedReconcilingStrategy
			 */
			public UnifiedReconcilingStrategy getReconcilingStrategy()
			{
				return new UnifiedReconcilingStrategy();
			}
		};
	}

	/**
	 * Creates the parser around the grammar file
	 * 
	 * @return - created parser
	 * @throws ParserInitializationException
	 * @throws FileNotFoundException
	 */
	private IParser createParser() throws ParserInitializationException, FileNotFoundException
	{
		// create token list from stream
		FileInputStream stream = new FileInputStream(grammarFile);
		TokenList tokenList = LanguageRegistry.createTokenList(stream);
		IParser result;

		if (tokenList != null)
		{
			// register token list
			LanguageRegistry.registerTokenList(tokenList);
		}

		// create parser
		if (LanguageRegistry.hasParser(language))
		{
			result = LanguageRegistry.getParser(language);
		}
		else
		{
			result = new UnifiedParser(language);
		}

		return result;
	}

	/**
	 * Creates the file service factory object used by the editor
	 */
	private void createFileServiceFactory()
	{
		fileService = new GenericTextFileServiceFactory();
	}
	
	private class GenericTextFileServiceFactory extends BaseFileServiceFactory
	{
		public FileService createFileService(IFileSourceProvider sourceProvider)
		{
			return createFileService(sourceProvider, true);
		}

		public FileService createFileService(IFileSourceProvider sourceProvider, boolean parse)
		{
			IParseState parseState = parser.createParseState(null);
			FileService fileService = new FileService(parser, parseState, sourceProvider, language);
			ParentOffsetMapper parentMapper = new ParentOffsetMapper(fileService);
			BaseFileLanguageService languageService = new BaseFileLanguageService(fileService, parseState, parser,
					parentMapper);
			fileService.setErrorManager(new UnifiedErrorManager(fileService, language));
			fileService.addLanguageService(language, languageService);

			if (parse)
			{
				fileService.doFullParse();
			}
			return fileService;
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty()
	{
		return editor != null ? editor.isDirty() : false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return editor != null ? editor.isSaveAsAllowed() : false;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		editor.createPartControl(parent);
		shell = parent.getShell();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();

		if (grammarFileMonitor != null)
		{
			grammarFileMonitor.cancel();
		}

		editor.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		if (editor != null)
		{
			editor.setFocus();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#close(boolean)
	 */
	public void close(boolean save)
	{
		if (editor != null)
		{
			editor.close(save);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		return editor != null ? editor.getContextAwareness() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getEditor()
	 */
	public IEditorPart getEditor()
	{
		return editor;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getFileContext()
	 */
	public EditorFileContext getFileContext()
	{
		return editor != null ? editor.getFileContext() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getOutlinePage()
	 */
	public UnifiedOutlinePage getOutlinePage()
	{
		return editor != null ? editor.getOutlinePage() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public UnifiedQuickOutlinePage createQuickOutlinePage()
	{
		return editor != null ? editor.createQuickOutlinePage() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getPairMatch(int)
	 */
	public PairMatch getPairMatch(int offset)
	{
		return editor != null ? editor.getPairMatch(offset) : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getParentDirectoryHint()
	 */
	public String getParentDirectoryHint()
	{
		return editor != null ? editor.getParentDirectoryHint() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#selectAndReveal(int, int)
	 */
	public void selectAndReveal(int offset, int length)
	{
		if (editor != null)
		{
			editor.selectAndReveal(offset, length);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#setParentDirectoryHint(java.lang.String)
	 */
	public void setParentDirectoryHint(String hint)
	{
		if (editor != null)
		{
			editor.setParentDirectoryHint(hint);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showWhitespace(boolean)
	 */
	public void showWhitespace(boolean state)
	{
		if (editor != null)
		{
			editor.showWhitespace(state);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#doRevertToSaved()
	 */
	public void doRevertToSaved()
	{
		if (editor != null)
		{
			editor.doRevertToSaved();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getAction(java.lang.String)
	 */
	public IAction getAction(String actionId)
	{
		return editor != null ? editor.getAction(actionId) : null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getDocumentProvider()
	 */
	public IDocumentProvider getDocumentProvider()
	{
		return editor != null ? editor.getDocumentProvider() : null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider()
	{
		return editor != null ? editor.getSelectionProvider() : null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#isEditable()
	 */
	public boolean isEditable()
	{
		return editor != null ? editor.isEditable() : false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#removeActionActivationCode(java.lang.String)
	 */
	public void removeActionActivationCode(String actionId)
	{
		if (editor != null)
		{
			editor.removeActionActivationCode(actionId);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#resetHighlightRange()
	 */
	public void resetHighlightRange()
	{
		if (editor != null)
		{
			editor.resetHighlightRange();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setAction(java.lang.String, org.eclipse.jface.action.IAction)
	 */
	public void setAction(String actionID, IAction action)
	{
		if (editor != null)
		{
			editor.setAction(actionID, action);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setActionActivationCode(java.lang.String, char, int, int)
	 */
	public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode,
			int activationStateMask)
	{
		if (editor != null)
		{
			editor.setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setHighlightRange(int, int, boolean)
	 */
	public void setHighlightRange(int offset, int length, boolean moveCursor)
	{
		if (editor != null)
		{
			editor.setHighlightRange(offset, length, moveCursor);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showHighlightRangeOnly(boolean)
	 */
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly)
	{
		if (editor != null)
		{
			editor.showHighlightRangeOnly(showHighlightRangeOnly);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showsHighlightRangeOnly()
	 */
	public boolean showsHighlightRangeOnly()
	{
		return editor != null ? editor.showsHighlightRangeOnly() : false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#addRulerContextMenuListener(org.eclipse.jface.action.IMenuListener)
	 */
	public void addRulerContextMenuListener(IMenuListener listener)
	{
		if (editor != null)
		{
			editor.addRulerContextMenuListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#isEditorInputReadOnly()
	 */
	public boolean isEditorInputReadOnly()
	{
		return editor != null ? editor.isEditorInputReadOnly() : false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#removeRulerContextMenuListener(org.eclipse.jface.action.IMenuListener)
	 */
	public void removeRulerContextMenuListener(IMenuListener listener)
	{
		if (editor != null)
		{
			editor.removeRulerContextMenuListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#setStatusField(org.eclipse.ui.texteditor.IStatusField,
	 *      java.lang.String)
	 */
	public void setStatusField(IStatusField field, String category)
	{
		if (editor != null)
		{
			editor.setStatusField(field, category);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#addSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 */
	public void addSaveAsListener(ISaveAsEvent listener)
	{
		if (editor != null)
		{
			editor.addSaveAsListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#addSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 */
	public void addSaveListener(ISaveEvent listener)
	{
		if (editor != null)
		{
			editor.addSaveListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 */
	public void removeSaveAsListener(ISaveAsEvent listener)
	{
		if (editor != null)
		{
			editor.removeSaveAsListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 */
	public void removeSaveListener(ISaveEvent listener)
	{
		if (editor != null)
		{
			editor.removeSaveListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getHighlightRange()
	 */
	public IRegion getHighlightRange()
	{
		return editor != null ? editor.getHighlightRange() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getConfiguration()
	 */
	public SourceViewerConfiguration getConfiguration()
	{
		return editor != null ? editor.getConfiguration() : null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getViewer()
	 */
	public ISourceViewer getViewer()
	{
		return editor != null ? editor.getViewer() : null;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return editor != null ? editor.getAdapter(adapter) : null;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded()
	{
		return editor != null ? editor.isSaveOnCloseNeeded() : false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
	 */
	public String getTitleToolTip()
	{
		return editor != null ? editor.getTitleToolTip() : ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getContentDescription()
	 */
	public String getContentDescription()
	{
		return editor != null ? editor.getContentDescription() : ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getOrientation()
	 */
	public int getOrientation()
	{
		return editor != null ? editor.getOrientation() : 0;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getTitle()
	 */
	public String getTitle()
	{
		return editor != null ? editor.getTitle() : ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getTitleImage()
	 */
	public Image getTitleImage()
	{
		return editor != null ? editor.getTitleImage() : null;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
	{
		if (editor != null)
		{
			editor.setInitializationData(config, propertyName, data);
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#showBusy(boolean)
	 */
	public void showBusy(boolean busy)
	{
		if (editor != null)
		{
			editor.showBusy(busy);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getBaseContributor()
	 */
	public IUnifiedEditorContributor getBaseContributor()
	{
		return editor.getBaseContributor();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return editor.getDefaultFileExtension();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#addFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void addFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		if (editor != null)
		{
			editor.addFileServiceChangeListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#removeFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void removeFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		if (editor != null)
		{
			editor.removeFileServiceChangeListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showPianoKeys(boolean)
	 */
	public void showPianoKeys(boolean state)
	{
		if (editor != null)
		{
			editor.showPianoKeys(state);
		}
	}
}
