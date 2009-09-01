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

import java.util.ArrayList;
import java.util.Iterator;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
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
import com.aptana.ide.metadata.ElementMetadata;
import com.aptana.ide.metadata.EventMetadata;
import com.aptana.ide.metadata.FieldMetadata;
import com.aptana.ide.metadata.IMetadataEnvironment;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * @author Robin Debreuil
 */
public class HTMLFileLanguageService extends BaseFileLanguageService {	
	
	private HTMLOffsetMapper offsetMapper;
	private IMetadataEnvironment environment;
	HTMLEnvironmentLoader loader;
	private boolean isProfileMember;
	private IFileContextListener _delayedFileListener;
	private static boolean isFirstConnection = true;
	
	/**
	 * HTMLFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	public HTMLFileLanguageService(
			FileService fileService, IParseState parseState, IParser parser, IParentOffsetMapper mapper)
	{
		super(fileService, parseState, parser, mapper);
		createOffsetMapper(mapper);
		this.environment = (IMetadataEnvironment)HTMLLanguageEnvironment.getInstance().getRuntimeEnvironment();

		loader = new HTMLEnvironmentLoader((IRuntimeEnvironment)this.environment);
		
		final IParseState finalParseState = parseState;
		_delayedFileListener = new IFileContextListener()
		{
			public void onContentChanged(FileContextContentEvent evt)
			{
				IFileService context = getFileContext();
				
				if(context != null)
				{
					loader.reloadEnvironment(finalParseState, context.getLexemeList(), finalParseState.getFileIndex());
				}
				else
				{
					throw new IllegalStateException(Messages.HTMLFileLanguageService_IFileContextShouldNotBeNull);
				}
			}
		};
		this.fileService.addDelayedFileListener(_delayedFileListener);		
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationTitleFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public String getDocumentationTitleFromLexeme(Lexeme lexeme)
	{
		String title = "HTML Editor"; //$NON-NLS-1$

		if(lexeme == null)
		{
			return "HTML Editor"; //$NON-NLS-1$
		}
		else
		{
			String titleLower = lexeme.getText().toLowerCase();

			if(lexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				title = "'" + titleLower + ">' tag"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if(lexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				title = titleLower.replaceAll("</", ""); //$NON-NLS-1$ //$NON-NLS-2$
				title = "'<" + title + ">' tag"; //$NON-NLS-1$ //$NON-NLS-2$
			}			
			else if(lexeme.typeIndex == HTMLTokenTypes.NAME)
			{
				FieldMetadata el = (FieldMetadata)environment.getGlobalFields().get(titleLower);
				if(el != null)
				{
					title = "'" + titleLower + "' attribute"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					EventMetadata fm = (EventMetadata)environment.getGlobalEvents().get(titleLower);
					if(fm != null)
					{
						title = "'" + titleLower + "' event"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
					
		return title;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationResourcesFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public HelpResource[] getDocumentationResourcesFromLexeme(Lexeme lexeme)
	{
		if(lexeme == null)
		{
			return new HelpResource[0];
		}
		else
		{
			ArrayList topics = new ArrayList();
			ArrayList generalTopics = new ArrayList();
			String lowerName = lexeme.getText().toLowerCase();

			if(lexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				addStartTagHelpTopics(topics, generalTopics, lowerName);
			}
			else if(lexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				addEndTagHelpTopics(topics, generalTopics, lowerName);

			}			
			else if(lexeme.typeIndex == HTMLTokenTypes.NAME)
			{
				addAttributeHelpTopics(topics, generalTopics, lowerName);
				addEventHelpTopics(topics, generalTopics, lowerName);

				IFileService context = getFileContext();
				Lexeme openTag = HTMLUtils.getTagOpenLexeme(lexeme.offset, context.getLexemeList());
				if(openTag != null)
				{
					String lowerTagName = openTag.getText().toLowerCase();
					addStartTagHelpTopics(topics, generalTopics, lowerTagName);
				}
			}

			if(generalTopics.size() == 0)
			{
				HelpResource index = new HelpResource("HTML Reference", "/com.aptana.ide.documentation/html/reference/api/HTML.index.html"); //$NON-NLS-1$ //$NON-NLS-2$
				generalTopics.add(index);				
			}

			for (Iterator iter = generalTopics.iterator(); iter.hasNext();) {
				topics.add(iter.next());
			}
			
			return (HelpResource[])topics.toArray(new HelpResource[0]);

		}

	}

	/**
	 * 
	 * @param topics
	 * @param el
	 */
	private void addAttributeHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {

		FieldMetadata el = (FieldMetadata)environment.getGlobalFields().get(lowerName);
		String anchor = ""; //$NON-NLS-1$
		if(el != null)
		{
			anchor = el.getName();
			String url = "/com.aptana.ide.documentation/html/reference/api/HTML.field." + el.getName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("'" + el.getName() + "' Attribute", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);

			HelpResource index = new HelpResource("HTML Attribute Reference", "/com.aptana.ide.documentation/html/reference/api/HTML.index-fields.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
			generalTopics.add(index);
		}
	}
	

	/**
	 * 
	 * @param topics
	 * @param lowerName
	 * @param el
	 * @param anchor
	 */
	private void addEventHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {
		EventMetadata fm = (EventMetadata)environment.getGlobalEvents().get(lowerName);
		String anchor = ""; //$NON-NLS-1$

		if(fm != null)
		{
			anchor = fm.getName();
			String url = "/com.aptana.ide.documentation/html/reference/api/HTML.event." + fm.getName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("'" + fm.getName() + "' Event", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);

			HelpResource index = new HelpResource("HTML Event Reference", "/com.aptana.ide.documentation/html/reference/api/HTML.index-events.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
			generalTopics.add(index);
		}
	}

	/**
	 * 
	 * @param topics
	 * @param lowerName
	 */
	private void addStartTagHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {
		lowerName = lowerName.replaceAll("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
		ElementMetadata el = environment.getElement(lowerName);
		String anchor = ""; //$NON-NLS-1$
		if(el != null)
		{
			anchor = el.getFullName();
			String url = "/com.aptana.ide.documentation/html/reference/api/HTML.element." + el.getFullName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("<" + lowerName + "> Element", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);
		}
		
		HelpResource index = new HelpResource("HTML Element Reference", "/com.aptana.ide.documentation/html/reference/api/HTML.index-elements.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
		generalTopics.add(index);
	}

	/**
	 * 
	 * @param topics
	 * @param lowerName
	 */
	private void addEndTagHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {
		lowerName = lowerName.replaceAll("</", ""); //$NON-NLS-1$ //$NON-NLS-2$
		ElementMetadata el = environment.getElement(lowerName);
		String anchor = ""; //$NON-NLS-1$
		if(el != null)
		{
			anchor = el.getFullName();
			String url = "/com.aptana.ide.documentation/html/reference/api/HTML.element." + el.getFullName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("<" + lowerName + "> Element", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);
		}

		HelpResource index = new HelpResource("HTML Element Reference", "/com.aptana.ide.documentation/html/reference/api/HTML.index-elements.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
		generalTopics.add(index);
	}

	/**
	 * Given a lexeme, returns the documentation for that lexeme based upon the current 
	 * "environment"
	 * @param lexeme The lexeme to search documentation for
	 * @return A string representing the code assist documentation
	 */
	public String getDocumentationFromLexeme(Lexeme lexeme) {
		if(lexeme == null)
		{
			return StringUtils.EMPTY;
		}
		else
		{	
			String docs = StringUtils.EMPTY;
			String lowerName = lexeme.getText().toLowerCase();

			if(lexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				lowerName = lowerName.replaceAll("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
				ElementMetadata el = environment.getElement(lowerName);
				if(el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML, new String[] {el.getName(), el.getDescription()});
				}
				else
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_NoInformationAvailableHTML, lexeme.getType());
				}
			}
			else if(lexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				lowerName = lowerName.replaceAll("</", ""); //$NON-NLS-1$ //$NON-NLS-2$
				ElementMetadata el = environment.getElement(lowerName);
				if(el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableEndTagHTML, new String[] {el.getName(), el.getDescription()});
				}
				else
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_NoInformationAvailableHTML, lexeme.getType());
				}
			}			
			else if(lexeme.typeIndex == HTMLTokenTypes.NAME)
			{
				FieldMetadata el = (FieldMetadata)environment.getGlobalFields().get(lowerName);
				if(el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML, new String[] {el.getName(), el.getDescription()});
				}				
			}
			else if(lexeme.typeIndex == HTMLTokenTypes.STRING)
			{
				docs += Messages.HTMLFileLanguageService_StringLiteralHTML;
				docs += lexeme.getText();
			}
			else
			{
				docs += ""; //$NON-NLS-1$
			}
			
			return docs;
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
	 * Finds the first identifier or keyword in HTML
	 * @param lexeme
	 * @return Lexeme
	 */
	private Lexeme findPreviousValidLexeme(Lexeme lexeme)
	{
		if(isLexemeOfInterest(lexeme))
		{
			return lexeme;
		}
		
		LexemeList ll = getFileContext().getLexemeList();
		if(ll == null)
		{
			return null;
		}

		Lexeme newLexeme = null;
		int index = ll.getLexemeIndex(lexeme);
		
		while(index > 0)
		{
			Lexeme l = ll.get(index);
			if(l == null)
			{
				return null;
			}
			
			if(isLexemeOfInterest(l))
			{
				newLexeme = l;
				break;
			}						
			
			index--;
		}
		
		return newLexeme;
	}
	
	/**
	 * Is this lexeme on we consider "important" to documentation
	 * @return true on lexeme we consider "important" to documentation
	 */
	private boolean isLexemeOfInterest(Lexeme lexeme)
	{
		return (lexeme.typeIndex == HTMLTokenTypes.NAME
				|| lexeme.getCategoryIndex() == TokenCategories.KEYWORD
				|| lexeme.typeIndex == HTMLTokenTypes.START_TAG
				|| lexeme.typeIndex == HTMLTokenTypes.END_TAG);
	}
	
	/**
	 * 
	 * @return DefaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return HTMLMimeType.MimeType;
	}

	/**
	 * Return the mapper from offsets to lexemes
	 * @return OffsetMapper
	 */
	public IOffsetMapper getOffsetMapper() {
		return offsetMapper;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseFileLanguageService#createOffsetMapper(com.aptana.ide.editors.unified.IParentOffsetMapper)
	 */
	public void createOffsetMapper(IParentOffsetMapper parent)
	{
		offsetMapper = new HTMLOffsetMapper(parent);
	}
	
	/**
	 * getHTMLFileLanguageService
	 * 
	 * @param fileContext
	 * @return HTMLFileLanguageService
	 */
	public static HTMLFileLanguageService getHTMLFileLanguageService(IFileService fileContext){
		HTMLFileLanguageService languageService = (HTMLFileLanguageService)fileContext.getLanguageService(HTMLMimeType.MimeType);
		if(languageService == null)
		{
			throw new IllegalStateException(Messages.HTMLFileLanguageService_NoHTMLLanguageServiceAvailable);
		}
		return languageService;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#connectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void connectSourceProvider(IFileSourceProvider sourceProvider) 
	{
		if(isFirstConnection)
		{
			isFirstConnection = false;
//			ProfileManager pm = UnifiedEditorsPlugin.getDefault().getProfileManager();
//			pm.applyProfiles();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#disconnectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void disconnectSourceProvider(IFileSourceProvider sourceProvider) 
	{
		if(_delayedFileListener != null)
		{
			this.fileService.addDelayedFileListener(_delayedFileListener);
			_delayedFileListener = null;
		}
		loader.unloadEnvironment(this.getParseState().getFileIndex());
		offsetMapper.dispose();
		
		offsetMapper = null;
		loader = null;
		environment = null;
	}

	/**
	 * isProfileMember
	 *
	 * @return boolean
	 */
	public boolean isProfileMember() 
	{
		return this.isProfileMember;
	}
	
	/**
	 * 
	 * setProfileMember
	 *
	 * @param isProfileMember
	 */
	public void setProfileMember(boolean isProfileMember) 
	{
		this.isProfileMember = isProfileMember;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#reset(boolean)
	 */
	public void reset(boolean resetFileIndex)
	{
	}
}
