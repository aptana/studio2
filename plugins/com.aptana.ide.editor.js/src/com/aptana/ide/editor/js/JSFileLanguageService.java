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
package com.aptana.ide.editor.js;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.environment.LexemeBasedEnvironmentLoader;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.scriptdoc.ScriptDocHelper;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.BaseFileLanguageService;
import com.aptana.ide.editors.unified.FileContextContentEvent;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileContextListener;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.editors.unified.help.HelpResource;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * The file specifics for a language document.
 * 
 * @author Ingo Muschenetz
 */
public class JSFileLanguageService extends BaseFileLanguageService
{
	private JSOffsetMapper _offsetMapper;
	private LexemeBasedEnvironmentLoader _loader;
	private IDocumentationStore _documentationStore;
	private IFileContextListener _delayedFileListener;

	/**
	 * JSFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	public JSFileLanguageService(FileService fileService, final IParseState parseState, IParser parser, IParentOffsetMapper mapper)
	{
		super(fileService, parseState, parser, mapper);
		
		createOffsetMapper(mapper);

		this._loader = new LexemeBasedEnvironmentLoader((Environment) JSLanguageEnvironment.getInstance().getRuntimeEnvironment());

		// add delay listener
		this._delayedFileListener = new IFileContextListener()
		{
			public void onContentChanged(FileContextContentEvent evt)
			{
				if (parseState.getFileIndex() == -1)
				{
					reconnectToEnvironment();
				}
				
				JSParseState jsps = (JSParseState) parseState.getParseState(getDefaultLanguage());
				
				if (jsps != null)
				{
					_loader.reloadEnvironment(jsps);
				}
			}
		};

		this.fileService.addDelayedFileListener(this._delayedFileListener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationTitleFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public String getDocumentationTitleFromLexeme(Lexeme lexeme)
	{
		String title = Messages.JSFileLanguageService_Javascript_editor;

		if (lexeme == null)
		{
			return title;
		}
		else
		{
			String titleLower = lexeme.getText();

			if (lexeme.getCategoryIndex() == TokenCategories.KEYWORD)
			{
				title = MessageFormat.format(Messages.JSFileLanguageService_0_keyword, titleLower);
			}
			else if (lexeme.getCategoryIndex() == TokenCategories.IDENTIFIER)
			{
				title = MessageFormat.format(Messages.JSFileLanguageService_0_identifier, titleLower);
			}
		}

		return title;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationResourcesFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public HelpResource[] getDocumentationResourcesFromLexeme(Lexeme lexeme)
	{
		if (lexeme == null)
		{
			return new HelpResource[0];
		}
		else
		{
			List<HelpResource> topics = new ArrayList<HelpResource>();
			List<HelpResource> generalTopics = new ArrayList<HelpResource>();
			String lowerName = lexeme.getText().toLowerCase();

			if (lexeme.getCategoryIndex() == TokenCategories.KEYWORD)
			{
				addKeywordHelpTopics(topics, generalTopics, lowerName);
			}
			else if (lexeme.getCategoryIndex() == TokenCategories.IDENTIFIER)
			{
				addIdentifierHelpTopics(topics, generalTopics, lowerName);
			}

			if (generalTopics.size() == 0)
			{
				HelpResource index = new HelpResource(Messages.JSFileLanguageService_Javascript_core_reference, "/com.aptana.ide.documentation/html/reference/api/JSCore.index.html"); //$NON-NLS-1$
				
				generalTopics.add(index);
				index = new HelpResource(Messages.JSFileLanguageService_HTML_dom_0_reference, "/com.aptana.ide.documentation/html/reference/api/HTMLDOM0.index.html"); //$NON-NLS-1$
				generalTopics.add(index);
				index = new HelpResource(Messages.JSFileLanguageService_HTML_dom_1_2_reference, "/com.aptana.ide.documentation/html/reference/api/HTMLDOM2.index.html"); //$NON-NLS-1$
				generalTopics.add(index);
			}

			for (Iterator<HelpResource> iter = generalTopics.iterator(); iter.hasNext();)
			{
				topics.add(iter.next());
			}

			return topics.toArray(new HelpResource[0]);
		}
	}

	/**
	 * @param topics
	 * @param el
	 */
	private void addKeywordHelpTopics(List<HelpResource> topics, List<HelpResource> generalTopics, String lowerName)
	{

		HelpResource index = new HelpResource(Messages.JSFileLanguageService_11, "/com.aptana.ide.documentation/html/reference/api/JSKeywords.index.html"); //$NON-NLS-1$
		generalTopics.add(index);
	}

	/**
	 * @param topics
	 * @param lowerName
	 */
	private void addIdentifierHelpTopics(List<HelpResource> topics, List<HelpResource> generalTopics, String lowerName)
	{
		// ElementMetadata el = environment.getElement(lowerName);
		// String anchor = "";
		// if(el != null)
		// {
		// anchor = el.getFullName();
		// String url = "/com.aptana.ide.documentation/html/reference/api/CSS.element." + el.getFullName() + ".html";
		// HelpResource hr = new HelpResource("'" + lowerName + "' Selector", url);
		// topics.add(hr);
		// }

		// HelpResource index = new HelpResource("CSS Selector Reference",
		// "/com.aptana.ide.documentation/html/reference/api/CSS.index-elements.html#" + anchor);
		// generalTopics.add(index);
	}

	/**
	 * Given a lexeme, returns the documentation for that lexeme based upon the current "environment"
	 * 
	 * @param lexeme
	 *            The lexeme to search documentation for
	 * @return A string representing the code assist documentation
	 */
	public String getDocumentationFromLexeme(Lexeme lexeme)
	{
		if (lexeme == null)
		{
			return StringUtils.EMPTY;
		}
		else
		{
			Lexeme l = findPreviousValidLexeme(lexeme);
			
			if (l == null)
			{
				return ""; //$NON-NLS-1$
			}
			else
			{
				return ScriptDocHelper.getInformationForLexeme(this._offsetMapper, l, true);
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getValidDocumentationLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public Lexeme getValidDocumentationLexeme(Lexeme lexeme)
	{
		return findPreviousValidLexeme(lexeme);
	}

	/**
	 * Finds the first identifier or keyword in JavaScript
	 * 
	 * @param lexeme
	 * @return Lexeme
	 */
	private Lexeme findPreviousValidLexeme(Lexeme lexeme)
	{
		if (lexeme.getCategoryIndex() == TokenCategories.IDENTIFIER || lexeme.getCategoryIndex() == TokenCategories.KEYWORD)
		{
			return lexeme;
		}

		LexemeList ll = getFileContext().getLexemeList();
		
		if (ll == null)
		{
			return null;
		}

		Lexeme newLexeme = null;
		int index = ll.getLexemeIndex(lexeme);

		while (index > 0)
		{
			Lexeme l = ll.get(index);
			
			if (l == null)
			{
				return null;
			}

			if (l.getCategoryIndex() == TokenCategories.IDENTIFIER || l.getCategoryIndex() == TokenCategories.KEYWORD)
			{
				newLexeme = l;
				break;
			}

			index--;
		}

		return newLexeme;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getOffsetMapper()
	 */
	public IOffsetMapper getOffsetMapper()
	{
		return this._offsetMapper;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseFileLanguageService#createOffsetMapper(com.aptana.ide.editors.unified.IParentOffsetMapper)
	 */
	public void createOffsetMapper(IParentOffsetMapper parent)
	{
		this._offsetMapper = new JSOffsetMapper(parent);
	}

	/**
	 * Gets the environment loader
	 * 
	 * @return Gets the environment loader
	 */
	public LexemeBasedEnvironmentLoader getEnvironmentLoader()
	{
		return this._loader;
	}

	/**
	 * @return DefaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return JSMimeType.MimeType;
	}

	/**
	 * reset
	 * 
	 * @param resetFileIndex
	 */
	public void reset(boolean resetFileIndex)
	{
		this._loader.unloadEnvironment();
		
		getParseState().reset();

		if (resetFileIndex)
		{
			this.getParseState().setFileIndex(FileContextManager.DEFAULT_FILE_INDEX);
		}
	}

	// TODO: spike implement calling this!

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#connectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void connectSourceProvider(IFileSourceProvider sourceProvider)
	{
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#disconnectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void disconnectSourceProvider(IFileSourceProvider sourceProvider)
	{
		// TODO: what should disconnecting do???
		this._loader.unloadEnvironment();

		// if(this.fileService != null)
		// this.fileService.removeDelayedFileListener(delayedFileListener);

		this.getParseState().setFileIndex(FileContextManager.DEFAULT_FILE_INDEX);

		this._offsetMapper.dispose();

		this._offsetMapper = null;
		this._loader = null;
		this._documentationStore = null;

		// ProfileManager pm = UnifiedEditorsPlugin.getDefault().getProfileManager();
		// pm.applyProfiles();
	}

	/**
	 * getDocumentationStore
	 * 
	 * @return IDocumentationStore
	 */
	public IDocumentationStore getDocumentationStore()
	{
		return this._documentationStore;
	}

	/**
	 * setDocumentationStore
	 * 
	 * @param store
	 */
	public void setDocumentationStore(IDocumentationStore store)
	{
		this._documentationStore = store;
	}

	/**
	 * getJSFileLanguageService
	 * 
	 * @param context
	 * @return JSFileLanguageService
	 */
	public static JSFileLanguageService getJSFileLanguageService(IFileService context)
	{
		return (JSFileLanguageService) context.getLanguageService(JSMimeType.MimeType);
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseFileLanguageService#activateForEditing()
	 */
	public void activateForEditing()
	{
		super.activateForEditing();
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseFileLanguageService#deactivateForEditing()
	 */
	public void deactivateForEditing()
	{
		super.deactivateForEditing();
		
		this._loader.unloadEnvironment();
	}
}
