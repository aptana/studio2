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
package com.aptana.ide.editor.css;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
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

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.css.CSSDocumentProvider.CSSFileInfo;
import com.aptana.ide.editor.css.context.CSSContextAwareness;
import com.aptana.ide.editor.css.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.editors.unified.context.IContextAwareness;

/**
 * @author Robin Debreuil
 * @author Pavel Petrochenko
 */
public class CSSEditor extends UnifiedEditor
{
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
			if (part instanceof MultiPageCSSEditor) {
				fActivePart = ((MultiPageCSSEditor) part).getEditor();
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

			if (fActivePart == CSSEditor.this) {
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

	private boolean isDisposing = false;

	/**
	 * CSSEditor
	 */
	public CSSEditor()
	{
		super();
		addPluginToPreferenceStore(CSSPlugin.getDefault());
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createLocalContributor()
	 */
	protected IUnifiedEditorContributor createLocalContributor()
	{
		return new CSSContributor();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getFileServiceFactory()
	 */
	public IFileServiceFactory getFileServiceFactory()
	{
		return CSSFileServiceFactory.getInstance();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		return CSSContextAwareness.getInstance(this);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return "css"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		fActivationListener= new ActivationListener(site.getWorkbenchWindow().getPartService());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (isDisposing)
		{
			return;
		}
		isDisposing = true;

		if (fActivationListener != null) {
			fActivationListener.dispose();
			fActivationListener= null;
		}

		super.dispose();
	}

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
				String location = getTempFileLocation(f, file.getName() + ".html"); //$NON-NLS-1$
				outFile = new File(location);
			}
		}
		else if (in instanceof NonExistingFileEditorInput)
		{
			NonExistingFileEditorInput nin = (NonExistingFileEditorInput) in;
			IPath path = nin.getPath(nin);
			String spath = path.toOSString();
			File f = new File(spath);
			String location = getTempFileLocation(f, path.lastSegment() + ".html"); //$NON-NLS-1$
			outFile = new File(location);
		}
		else if (in instanceof IPathEditorInput)
		{
			IPathEditorInput pin = (IPathEditorInput) in;
			File path = pin.getPath().toFile();
			String location = getTempFileLocation(path, pin.getName() + ".html"); //$NON-NLS-1$
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
		if (CSSPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW))
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
			return file + File.separator + ".tmp_" + name + ((int) (Math.random() * 100000)) + "~"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return FileUtils.systemTempDir + File.separator + ".tmp_" + name + "~"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
	 */
	protected String[] collectContextMenuPreferencePages()
	{
		return new String[] {
				"com.aptana.ide.editor.css.preferences.GeneralPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.ColorizationPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.FoldingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.ProblemsPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.PreviewPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.FormattingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.CodeAssistPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.css.preferences.TypingPreferencePage", //$NON-NLS-1$
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
		CSSFileInfo cuInfo = getCSSFileInfo(input);
		if (cuInfo == null)
		{
			throw new RuntimeException(Messages.CSSEditor_cuInfo_Null_At_DoSetInput);
		}
		CSSDocumentProvider dp = (CSSDocumentProvider) getDocumentProvider();

		// Get document from input
		IDocument document = dp.getDocument(input);

		DocumentSourceProvider provider = new DocumentSourceProvider(document, input);

		if (provider == null)
		{
			throw new RuntimeException(Messages.CSSEditor_Provider_Null);
		}

		return (cuInfo.sourceProvider == null || cuInfo.sourceProvider.equals(provider) == false);
	}

	private CSSFileInfo getCSSFileInfo(IEditorInput input)
	{
		CSSDocumentProvider dp = (CSSDocumentProvider) getDocumentProvider();
		if (dp == null)
		{
			throw new RuntimeException(Messages.CSSEditor_Document_Provier_Null);
		}
		return (CSSFileInfo) dp.getFileInfoPublic(input);
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
			getCSSFileInfo(input).sourceProvider = provider;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createDocumentProvider()
	 */
	public IDocumentProvider createDocumentProvider()
	{
		return CSSDocumentProvider.getInstance();
	}
}
