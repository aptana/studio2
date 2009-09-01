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

import java.util.ArrayList;
import java.util.Iterator;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.unified.BaseFileLanguageService;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.editors.unified.help.HelpResource;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.metadata.ElementMetadata;
import com.aptana.ide.metadata.FieldMetadata;
import com.aptana.ide.metadata.IMetadataEnvironment;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public class CSSFileLanguageService extends BaseFileLanguageService
{
	private CSSOffsetMapper offsetMapper;
	private IMetadataEnvironment environment;
	private boolean isProfileMember;

	/**
	 * CSSFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	public CSSFileLanguageService(FileService fileService, IParseState parseState, IParser parser,
			IParentOffsetMapper mapper)
	{
		super(fileService, parseState, parser, mapper);
		createOffsetMapper(mapper);
		environment = (IMetadataEnvironment) CSSLanguageEnvironment.getInstance().getRuntimeEnvironment();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationTitleFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public String getDocumentationTitleFromLexeme(Lexeme lexeme)
	{
		String title = "CSS Editor"; //$NON-NLS-1$

		if(lexeme == null)
		{
			return title;
		}
		else
		{
			String titleLower = lexeme.getText().toLowerCase();

			if(lexeme.typeIndex == CSSTokenTypes.PROPERTY)
			{
				title = "'" + titleLower + "' property"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if(lexeme.typeIndex == CSSTokenTypes.IDENTIFIER || lexeme.typeIndex == CSSTokenTypes.SELECTOR)
			{
				title = "'" + titleLower + "' selector"; //$NON-NLS-1$ //$NON-NLS-2$
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

			if(lexeme.typeIndex == CSSTokenTypes.PROPERTY)
			{
				addPropertyHelpTopics(topics, generalTopics, lowerName);

//				IFileService context = getFileContext();
//				Lexeme openTag = HTMLUtils.getTagOpenLexeme(lexeme.offset, context.getLexemeList());
//				if(openTag != null)
//				{
//					String lowerTagName = openTag.getText().toLowerCase();
//					addStartTagHelpTopics(topics, generalTopics, lowerTagName);
//				}
			}
			else if(lexeme.typeIndex == CSSTokenTypes.IDENTIFIER || lexeme.typeIndex == CSSTokenTypes.SELECTOR)
			{
				addSelectorHelpTopics(topics, generalTopics, lowerName);
			}			

			if(generalTopics.size() == 0)
			{
				HelpResource index = new HelpResource("CSS Reference", "/com.aptana.ide.documentation/html/reference/api/CSS.index.html"); //$NON-NLS-1$ //$NON-NLS-2$
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
	private void addPropertyHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {

		FieldMetadata el = (FieldMetadata)environment.getGlobalFields().get(lowerName);
		String anchor = ""; //$NON-NLS-1$
		if(el != null)
		{
			anchor = el.getName();
			String url = "/com.aptana.ide.documentation/html/reference/api/CSS.field." + el.getName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("'" + el.getName() + "' Property", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);

			HelpResource index = new HelpResource("CSS Property Reference", "/com.aptana.ide.documentation/html/reference/api/CSS.index-fields.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
			generalTopics.add(index);
		}
	}
	

	/**
	 * 
	 * @param topics
	 * @param lowerName
	 */
	private void addSelectorHelpTopics(ArrayList topics, ArrayList generalTopics, String lowerName) {

		ElementMetadata el = environment.getElement(lowerName);
		String anchor = ""; //$NON-NLS-1$
		if(el != null)
		{
			anchor = el.getFullName();
			String url = "/com.aptana.ide.documentation/html/reference/api/CSS.element." + el.getFullName() + ".html"; //$NON-NLS-1$ //$NON-NLS-2$
			HelpResource hr = new HelpResource("'" + lowerName + "' Selector", url); //$NON-NLS-1$ //$NON-NLS-2$
			topics.add(hr);
		}
		
		HelpResource index = new HelpResource("CSS Selector Reference", "/com.aptana.ide.documentation/html/reference/api/CSS.index-elements.html#" + anchor); //$NON-NLS-1$ //$NON-NLS-2$
		generalTopics.add(index);
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
			String docs = StringUtils.EMPTY;
			if (lexeme.typeIndex == CSSTokenTypes.IDENTIFIER || lexeme.typeIndex == CSSTokenTypes.SELECTOR
					|| lexeme.typeIndex == CSSTokenTypes.PROPERTY)
			{
				String lowerName = lexeme.getText().toLowerCase();
				FieldMetadata el = (FieldMetadata) environment.getGlobalFields().get(lowerName);
				if (el != null)
				{
					docs += StringUtils.format(Messages.CSSFileLanguageService_FieldDescription, new String[] {
							el.getName(), el.getDescription() });
				}
				else
				{
					ElementMetadata em = environment.getElement(lowerName);
					if (em != null)
					{
						docs += StringUtils.format(Messages.CSSFileLanguageService_FieldDescription, new String[] {
								em.getName(), em.getDescription() });
					}
					else
					{
						docs += StringUtils.format(Messages.CSSFileLanguageService_FieldDescription, new String[] {
								lexeme.getType(), StringUtils.EMPTY })
								+ Messages.CSSFileLanguageService_NoInformationAvailable;
					}
				}
			}
			else if (lexeme.typeIndex == CSSTokenTypes.STRING)
			{
				docs += StringUtils.format(Messages.CSSFileLanguageService_StringLiteralDescription, lexeme.getText());
			}
			else
			{
				docs = StringUtils.EMPTY;
			}

			return docs;
		}
	}

	/**
	 * @return DefaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return CSSMimeType.MimeType;
	}

	/**
	 * Return the mapper from offsets to lexemes
	 * 
	 * @return IOffsetMapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		return offsetMapper;
	}

	/**
	 * @param parent
	 */
	public void createOffsetMapper(IParentOffsetMapper parent)
	{
		offsetMapper = new CSSOffsetMapper(parent);
	}

	/**
	 * getCSSFileLanguageService
	 * 
	 * @param context
	 * @return CSSFileLanguageService
	 */
	public static CSSFileLanguageService getCSSFileLanguageService(IFileService context)
	{
		CSSFileLanguageService languageService = (CSSFileLanguageService) context
				.getLanguageService(CSSMimeType.MimeType);

		if (languageService == null)
		{
			throw new IllegalStateException(Messages.CSSFileLanguageService_NoLanguageServiceAvailable);
		}

		return languageService;
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
