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
package com.aptana.ide.editors.unified.help;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * LexemeUIHelp
 * 
 * @author Ingo Muschenetz
 */
public class LexemeUIHelp
{

	/**
	 * APTANA_DOCS_KEYWORD
	 */
	public static final String APTANA_DOCS_KEYWORD = "aptana_docs"; //$NON-NLS-1$

	/**
	 * Protected constructor for utility class
	 */
	protected LexemeUIHelp()
	{

	}

	/**
	 * setHelp
	 * 
	 * @param editor
	 * @param text
	 * @param fileService
	 */
	public static void setHelp(IUnifiedEditor editor, StyledText text, IFileService fileService)
	{

		LexemeHelpListener listener = new LexemeHelpListener(editor, fileService);
		text.addHelpListener(listener);

	}

	/**
	 * Creates and returns a help context provider for the given part.
	 * 
	 * @param part
	 *            the part for which to create the help context provider
	 * @param fileService
	 * @return the help context provider
	 */
	public static IContextProvider getHelpContextProvider(IWorkbenchPart part, IFileService fileService)
	{

		Lexeme lexeme = null;
		int offset = 0;
		IFileLanguageService fileLanguageService = null;

		if (part instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) part;
			lexeme = getLexeme(editor, fileService);
			offset = editor.getViewer().getTextWidget().getCaretOffset();

			if (lexeme != null)
			{
				fileLanguageService = fileService.getLanguageService(lexeme.getToken().getLanguage());
			}
		}

		return new LexemeHelpContextProvider(fileLanguageService, lexeme, offset);

	}

	/**
	 * Currently, we only provide documentation on identifiers or keywords
	 * 
	 * @param lexeme
	 * @return true if lexeme is valid
	 */
	public static boolean isValidLexeme(Lexeme lexeme)
	{
		if (lexeme == null)
		{
			return false;
		}

		// if (lexeme.getCategoryIndex() == JSTokenCategories.IDENTIFIER
		// || lexeme.getCategoryIndex() == JSTokenCategories.KEYWORD)
		return true;
		// else
		// return false;
	}

	/**
	 * Returns a valid lexeme (we wish to show in help), or a null object otherwise;
	 * 
	 * @param editor
	 * @param fileService
	 * @return Returns a valid lexeme (we wish to show in help), or a null object otherwise;
	 */
	public static Lexeme getLexeme(IUnifiedEditor editor, IFileService fileService)
	{
		int offset = editor.getViewer().getTextWidget().getCaretOffset();
		LexemeList lexemes = fileService.getLexemeList();
		if (lexemes == null)
		{
			return null;
		}

		int floorIndex = lexemes.getLexemeFloorIndex(offset);
		Lexeme lexeme = null;

		if (floorIndex >= 0)
		{
			lexeme = lexemes.get(floorIndex);
		}
		int ceilingIndex = lexemes.getLexemeCeilingIndex(offset);

		if (lexeme == null && ceilingIndex >= 0)
		{
			lexemes.get(lexemes.getLexemeCeilingIndex(offset));
		}

		if (isValidLexeme(lexeme))
		{
			return lexeme;
		}
		else
		{
			return null;
		}
	}

	/**
	 * LexemeHelpListener
	 * 
	 * @author Ingo Muschenetz
	 */
	private static class LexemeHelpListener implements HelpListener
	{
		private IUnifiedEditor fEditor;
		private IFileService fileService;

		/**
		 * LexemeHelpListener
		 * 
		 * @param editor
		 * @param fileService
		 */
		public LexemeHelpListener(IUnifiedEditor editor, IFileService fileService)
		{
			fEditor = editor;
			this.fileService = fileService;
		}

		/**
		 * Invoked when F1 is pressed.
		 * 
		 * @see org.eclipse.swt.events.HelpListener#helpRequested(org.eclipse.swt.events.HelpEvent)
		 */
		public void helpRequested(HelpEvent e)
		{

			Lexeme lexeme = null;
			int offset = 0;

			if (fEditor != null)
			{

				lexeme = getLexeme(fEditor, fileService);
				offset = fEditor.getViewer().getTextWidget().getCaretOffset();
				if (lexeme != null)
				{
					String language = lexeme.getToken().getLanguage();
					LexemeHelpContext.displayHelp("com.aptana.ide.editors.HTMLEditor", fileService //$NON-NLS-1$
							.getLanguageService(language), lexeme, offset);
				}
			}

		}
	}

	/**
	 * The Help view tracks activation of the workbench parts (views and editors) and checks if they adapt to
	 * org.eclipse.help.IContextProvider interface. If they do, the view will use the context provider to locate the
	 * IContext object and get the required information from it.
	 * 
	 * @author Ingo Muschenetz
	 */
	private static class LexemeHelpContextProvider implements IContextProvider
	{
		private String fId = "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$
		private IFileLanguageService fileLanguageService;
		private Lexeme fLexeme;
		private int offset = 0;

		/**
		 * LexemeHelpContextProvider
		 * 
		 * @param fileLanguageService
		 * @param lexeme
		 * @param offset
		 */
		public LexemeHelpContextProvider(IFileLanguageService fileLanguageService, Lexeme lexeme, int offset)
		{
			this.fileLanguageService = fileLanguageService;
			fLexeme = lexeme;
			this.offset = offset;
		}

		/**
		 * If context change mask returns NONE, context object will need to be provided when the workbench part is
		 * activated. If SELECTION is returned, you will need to provide context object that is sensitive to the current
		 * selection in the part. Each time part selection provider fires a selection change event, the context provider
		 * will be asked to provide context object. NOTE: The behavior will be different depending on if the editor has
		 * a selection provider that implements IPostSelectionProvider. See HelpView line 154.
		 * 
		 * @return ContextChangeMask
		 */
		public int getContextChangeMask()
		{
			return SELECTION;
		}

		/**
		 * Context object for "About..." part of help. This pulls default content from the contexts.xml file, and adds
		 * in other content.
		 * 
		 * @param target
		 * @return returns the context
		 */
		public IContext getContext(Object target)
		{

			IContext context = null;

			if (fileLanguageService == null)
			{
				return context;
			}

			if (fileLanguageService.getOffsetMapper() == null)
			{
				return HelpSystem.getContext(fId);
			}

			Lexeme lexeme = fileLanguageService.getOffsetMapper().getCurrentLexeme();

			if (lexeme != null)
			{
				context = new LexemeHelpContext(context, fileLanguageService, lexeme, offset);
			}
			else
			{
				context = HelpSystem.getContext(fId);
			}

			return context;
		}

		/**
		 * Search expression passed to "dynamic help" area. This does a search against an array of federated search
		 * engines.
		 * 
		 * @param target
		 * @return Returns the search expression passed to "dynamic help" area.
		 */
		public String getSearchExpression(Object target)
		{
			if (fLexeme != null)
			{
				String lexemeLanguage = fLexeme.getLanguage();
				if(lexemeLanguage != null)
				{
					lexemeLanguage = lexemeLanguage.replace('/', '_');
				}
				return lexemeLanguage + " " + fLexeme.getText() + " " + APTANA_DOCS_KEYWORD; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				return Messages.LexemeUIHelp_Editor;
			}
		}
	}
}
