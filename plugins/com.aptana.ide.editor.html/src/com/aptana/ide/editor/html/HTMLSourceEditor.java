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
package com.aptana.ide.editor.html;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.html.HTMLDocumentProvider.HTMLFileInfo;
import com.aptana.ide.editor.html.actions.PrettyPrintHTML;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * HTMLSourceEditor
 */
public class HTMLSourceEditor extends UnifiedEditor
{
	/**
	 * 
	 */
	protected HTMLContributor contributor;
	private boolean _isDisposing = false;
	private boolean _isMarkingBothTags = false;
	private Action tidyAction;

	/**
	 * Internal part and shell activation listener for triggering state validation.
	 */
	private class ActivationListener implements IPartListener, IWindowListener {

		/** Cache of the active workbench part. */
		private IWorkbenchPart fActivePart;
		/** Indicates whether activation handling is currently be done. */
		private boolean fIsHandlingActivation= false;
		/**
		 * The part service.
		 */
		private IPartService fPartService;

		/**
		 * Creates this activation listener.
		 *
		 * @param partService the part service on which to add the part listener
		 */
		public ActivationListener(IPartService partService) {
			fPartService= partService;
			fPartService.addPartListener(this);
			PlatformUI.getWorkbench().addWindowListener(this);
		}

		/**
		 * Disposes this activation listener.
		 */
		public void dispose() {
			fPartService.removePartListener(this);
			PlatformUI.getWorkbench().removeWindowListener(this);
			fPartService= null;
		}

		/**
		 * @see IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof HTMLEditor) {
				fActivePart = ((HTMLEditor) part).getEditor();
			} else {
				fActivePart= part;
			}
			handleActivation();
		}

		/**
		 * @see IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			fActivePart= null;
		}

		/**
		 * @see IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
		}
		
		/**
		 * Handles the activation triggering a element state check in the editor.
		 */
		private void handleActivation() {
			if (fIsHandlingActivation)
				return;

			if (fActivePart == HTMLSourceEditor.this) {
				fIsHandlingActivation= true;
				try {
					safelySanityCheckState(getEditorInput());
				} finally {
					fIsHandlingActivation= false;
				}
			}
		}

		/**
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow()) {
				/*
				 * Workaround for problem described in
				 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11731
				 * Will be removed when SWT has solved the problem.
				 */
				window.getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						handleActivation();
					}
				});
			}
		}

		/**
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
		}

		/**
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window) {
		}

		/**
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window) {
		}
	}

	/**
	 * The editor's activation listener.
	 */
	private ActivationListener fActivationListener;
	
	/**
	 * HTMLSourceEditor
	 */
	public HTMLSourceEditor()
	{
		super();
		addPluginToPreferenceStore(HTMLPlugin.getDefault());
		_isMarkingBothTags = getPreferenceStore().getBoolean(IPreferenceConstants.HTMLEDITOR_HIGHLIGHT_START_END_TAGS);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		fActivationListener= new ActivationListener(site.getWorkbenchWindow().getPartService());
	}
	
	@Override
	protected void initializeKeyBindingScopes()
	{
		setKeyBindingScopes(new String[] { "com.aptana.ide.editors.UnifiedEditorsScope", "com.aptana.ide.editors.HTMLEditorScope" }); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createActions()
	 */
	protected void createActions()
	{
		tidyAction = new Action(Messages.HTMLSourceEditor_RunHTMLTidy)
		{
			public void run()
			{
				PrettyPrintHTML formatter = new PrettyPrintHTML();
				formatter.setActiveEditor(null, HTMLSourceEditor.this);
				formatter.run();
			}

		};
		super.createActions();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);
		menu.add(tidyAction);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createLocalContributor()
	 */
	protected IUnifiedEditorContributor createLocalContributor()
	{
		this.contributor = new HTMLContributor();

		return this.contributor;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getFileServiceFactory()
	 */
	public IFileServiceFactory getFileServiceFactory()
	{
		return HTMLFileServiceFactory.getInstance();
	}

	/**
	 * Returns a java.io.File object of a temporary file for preview.
	 * 
	 * @return File
	 */
	public File getTempFile()
	{
		IEditorInput in = this.getEditorInput();
		File outFile = null;

		if (in instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			IPath path = CoreUIUtils.getPathOfIFileLocation(file);
			if (path != null)
			{
				File f = path.makeAbsolute().toFile();
				String location = getTempFileLocation(f, file.getName());
				outFile = new File(location);
			}
		}
		else if (in instanceof NonExistingFileEditorInput)
		{
			NonExistingFileEditorInput nin = (NonExistingFileEditorInput) in;
			IPath path = nin.getPath(nin);
			String spath = path.toOSString();
			File f = new File(spath);
			String location = getTempFileLocation(f, path.lastSegment());
			outFile = new File(location);
		}
		else if (in instanceof IPathEditorInput)
		{
			IPathEditorInput pin = (IPathEditorInput) in;
			File path = pin.getPath().toFile();
			String location = getTempFileLocation(path, pin.getName());
			outFile = new File(location);
		} else if (in instanceof IURIEditorInput) {
			URI uri = ((IURIEditorInput) in).getURI();
			String location;
			if ("file".equals(uri.getScheme())) {
				location = getTempFileLocation(new File(uri), Path.fromPortableString(uri.getPath()).lastSegment());	
			} else {
				location = getTempFileLocation(null, Path.fromPortableString(uri.getPath()).lastSegment());
			}
			outFile = new File(location);
		}

		if (outFile != null)
		{
			outFile.deleteOnExit();
		}

		return outFile;
	}

	/**
	 * The location of the temp file
	 * 
	 * @param file
	 * @param name
	 * @return String
	 */
	public String getTempFileLocation(File file, String name)
	{
		if (file != null) {
			file = file.getParentFile();
		} else {
			try {
				return File.createTempFile(".tmp_" + name, "~").getAbsolutePath();
			} catch (IOException e) {
				return null;
			}
		}
		// Add cache busting random number to fix STU-2094
		return file + File.separator + ".tmp_" + name + "." + ((int) (Math.random() * 100000)) + "~"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * isFileEditorInput
	 * 
	 * @return boolean
	 */
	public boolean isFileEditorInput()
	{
		if (getEditorInput() instanceof IFileEditorInput
				|| getEditorInput() instanceof IPathEditorInput
				|| getEditorInput() instanceof IURIEditorInput
				|| getEditorInput() instanceof NonExistingFileEditorInput)
		{
			return true;
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return "html"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (_isDisposing)
		{
			return;
		}
		_isDisposing = true;
		
		if (fActivationListener != null) {
			fActivationListener.dispose();
			fActivationListener= null;
		}
		if (contributor != null)
		{
			contributor.dispose();
			contributor = null;
		}
		// setSite(null);

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
	 */
	protected String[] collectContextMenuPreferencePages()
	{
		return new String[] {
				"com.aptana.ide.editor.html.preferences.GeneralPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.ColorPizationreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.FoldingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.FormattingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.ProblemsPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.CodeAssistPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.TypingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.html.preferences.TidyPreferencePage", //$NON-NLS-1$
				"org.eclipse.ui.preferencePages.GeneralTextEditor", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Annotations", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.QuickDiff", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Accessibility", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Spelling", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.LinkedModePreferencePage", //$NON-NLS-1$
			};
	}

	/**
	 * isNewInput
	 * 
	 * @param input
	 * @return boolean
	 */
	protected boolean isNewInput(IEditorInput input)
	{
		HTMLFileInfo cuInfo = getHTMLFileInfo(input);
		if (cuInfo == null)
		{
			return false;
			// throw new RuntimeException(Messages.HTMLSourceEditor_cuInfo_Null_At_DoSetInput);
		}
		HTMLDocumentProvider dp = (HTMLDocumentProvider) getDocumentProvider();

		// Get document from input
		IDocument document = dp.getDocument(input);

		DocumentSourceProvider provider = new DocumentSourceProvider(document, input);

		if (provider == null)
		{
			throw new RuntimeException(Messages.HTMLSourceEditor_Provider_Null);
		}

		return (cuInfo.sourceProvider == null || cuInfo.sourceProvider.equals(provider) == false);
	}

	private HTMLFileInfo getHTMLFileInfo(IEditorInput input)
	{
		HTMLDocumentProvider dp = (HTMLDocumentProvider) getDocumentProvider();
		if (dp == null)
		{
			throw new RuntimeException(Messages.HTMLSourceEditor_Document_Provider_Null);
		}
		return (HTMLFileInfo) dp.getFileInfoPublic(input);
	}

	/**
	 * Updates the file information
	 * 
	 * @param input
	 * @param provider
	 * @param document
	 */
	protected void updateFileInfo(IEditorInput input, DocumentSourceProvider provider, IDocument document)
	{
		super.updateFileInfo(input, provider, document);
		if (isNewInput(input))
		{
			// save reference to provider
			getHTMLFileInfo(input).sourceProvider = provider;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createDocumentProvider()
	 */
	public IDocumentProvider createDocumentProvider()
	{
		return HTMLDocumentProvider.getInstance();
	}

	/**
	 * Can this lexeme be highlighted for matching occurrences ?
	 * 
	 * @param lexeme
	 * @return true if this token type is eligible for occurrence marking
	 */
	public boolean canMarkOccurrences(Lexeme lexeme)
	{
		IToken token = lexeme.getToken();

		int typeIndex = token.getTypeIndex();
		if (typeIndex == HTMLTokenTypes.TEXT)
		{
			return false;
		}
		if (typeIndex == HTMLTokenTypes.START_TAG || typeIndex == HTMLTokenTypes.END_TAG)
		{
			return true;
		}
		return super.canMarkOccurrences(lexeme);
	}

	/**
	 * Overridden parent method to handle highlighting of both start and end tag when either is selected If/when we have
	 * common based class for tag based languages (html, xml etc.), this method should be moved there.
	 * 
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#markOccurences(com.aptana.ide.lexer.LexemeList,
	 *      com.aptana.ide.lexer.Lexeme)
	 */
	protected void markOccurences(LexemeList lexemeList, Lexeme selectedLexeme)
	{
		boolean deferToParent = true;

		if (this._isMarkingBothTags
				&& (selectedLexeme.typeIndex == HTMLTokenTypes.START_TAG || selectedLexeme.typeIndex == HTMLTokenTypes.END_TAG))
		{
			deferToParent = false;

			String selectedText = selectedLexeme.getText();
			String normalizedSelectedText = selectedText;

			// convert e.g. "</table" to <table"
			if (selectedLexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				if (selectedLexeme.length >= 3)
				{
					normalizedSelectedText = selectedText.substring(0, 1) + selectedText.substring(2);
				}
			}

			for (int i = 0; i < lexemeList.size(); i++)
			{
				Lexeme lexeme = lexemeList.get(i);
				if (lexeme != null)
				{
					if (lexeme.isHighlighted())
					{
						lexeme.setHighlighted(false);
					}

					if (lexeme.typeIndex == HTMLTokenTypes.START_TAG || lexeme.typeIndex == HTMLTokenTypes.END_TAG)
					{
						if (lexeme.typeIndex == selectedLexeme.typeIndex && lexeme.length == selectedLexeme.length)
						{
							if (selectedText.equals(lexeme.getText()))
							{
								lexeme.setHighlighted(true);
							}
						}
						else if ((lexeme.length - selectedLexeme.length) == 1)
						{
							if (lexeme.typeIndex == HTMLTokenTypes.END_TAG)
							{
								if (lexeme.length >= 3)
								{
									String normalizedText = lexeme.getText().substring(0, 1)
											+ lexeme.getText().substring(2);
									if (normalizedText.equals(selectedText))
									{
										lexeme.setHighlighted(true);
									}
								}
							}
						}
						else if ((selectedLexeme.length - lexeme.length) == 1)
						{

							if (normalizedSelectedText.equals(lexeme.getText()))
							{
								lexeme.setHighlighted(true);
							}
						}

					}

				}
			}
		}

		if (deferToParent)
		{
			super.markOccurences(lexemeList, selectedLexeme);
		}
	}

	/**
	 * Returns name and extension filters for display in file dialog
	 * 
	 * @param fileName
	 * @return FileDialogFilterInfo
	 */
	protected FileDialogFilterInfo getFileDialogFilterInformation(String fileName)
	{
		String fileExtension = getFileExtension(fileName);
		if (Messages.HTMLSourceEditor_ExtensionHTM.equals(fileExtension)
				|| Messages.HTMLSourceEditor_ExtensionHTML.equals(fileExtension))
		{
			FileDialogFilterInfo filterInfo = new FileDialogFilterInfo();
			filterInfo
					.setFilterExtensions(new String[] {
							"*" + Messages.HTMLSourceEditor_ExtensionHTM, "*" + Messages.HTMLSourceEditor_ExtensionHTML, "*" + Messages.HTMLSourceEditor_ExtensionAll }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			filterInfo.setFilterNames(new String[] { Messages.HTMLSourceEditor_DescriptionHTM,
					Messages.HTMLSourceEditor_DescriptionHTML, Messages.HTMLSourceEditor_DescriptionAll });

			return filterInfo;
		}
		else
		{
			return super.getFileDialogFilterInformation(fileName);
		}

	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		String property = event.getProperty();

		if (IPreferenceConstants.HTMLEDITOR_HIGHLIGHT_START_END_TAGS.equals(property))
		{
			_isMarkingBothTags = getPreferenceStore().getBoolean(
					IPreferenceConstants.HTMLEDITOR_HIGHLIGHT_START_END_TAGS);
		}
		else
		{
			super.handlePreferenceStoreChanged(event);
		}

	}

}
