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
package com.aptana.ide.editors.actions;

import java.io.File;

import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.utils.EditorHelper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * OpenDeclarationAction
 */
public class OpenDeclarationAction extends UnifiedEditorAction
{
	/**
	 * ACTION_ID
	 */
	public static final String ACTION_ID = UnifiedEditorsPlugin.getDefault().getBundle().getSymbolicName()
			+ ".openDeclarationAction"; //$NON-NLS-1$

	/**
	 * 
	 */
	public OpenDeclarationAction()
	{
		super(ACTION_ID, Messages.OpenDeclarationAction_OpenDeclaration);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		IFileLanguageService fileService = this.getFileService();
		ISourceViewer viewer = this.getActiveEditor().getViewer();
		int offset = viewer.getTextWidget().getCaretOffset();
		if (viewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 v5 = (ITextViewerExtension5) viewer;
			offset = v5.widgetOffset2ModelOffset(offset);
		}
		if (offset == -1)
		{
			return;
		}
		IOffsetMapper mapper = fileService.getOffsetMapper();
		int lxOffset = mapper.getLexemeIndexFromDocumentOffset(offset);
		if (lxOffset == -1)
		{
			return;
		}
		Lexeme lx = mapper.getLexemeAtIndex(lxOffset);
		if (lx == null)
		{
			return;
		}
		String mimetype = lx.getLanguage();

		//IRuntimeEnvironment env = EnvironmentManager.getEnvironment(mimetype);

		IFileLanguageService langService = fileService.getFileContext().getLanguageService(mimetype);
		if (langService == null /*|| env == null*/)
		{
			return;
		}

		IOffsetMapper langMapper = langService.getOffsetMapper();
		if (langMapper == null)
		{
			return;
		}

		CodeLocation loc = (CodeLocation) langMapper.findTarget(lx);
		if (loc != null)
		{
			// if(loc.getFile() == null)
			openInEditor(CoreUIUtils.getPathFromURI(loc.getFullPath()), loc.getStartLexeme());
			// else
			// openInEditor(loc.getFile(), loc.getStartLexeme());
		}

	}

	private void openInEditor(String fileName, Lexeme lexeme)
	{
		IEditorPart part = EditorHelper.openInEditor(new File(fileName));
		if (part instanceof IUnifiedEditor && lexeme != null)
		{
			IUnifiedEditor jsEditor = (IUnifiedEditor) part;
			jsEditor.selectAndReveal(lexeme.offset, lexeme.getEndingOffset() - lexeme.offset);
		}
		
	}

	// private void openInEditor(IFile file, Lexeme lexeme)
	// {
	// try {
	// IEditorPart part = EditorHelper.openInEditor(file, true);
	// if(part instanceof UnifiedEditor)
	// {
	// UnifiedEditor jsEditor = (UnifiedEditor)part;
	// jsEditor.selectAndReveal(lexeme.offset, lexeme.getEndingOffset()-lexeme.offset);
	// }
	// }
	// catch (PartInitException e) {
	// IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e);
	// }
	// }
}
