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
package com.aptana.ide.editor.xml;

import java.util.Hashtable;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.Messages;
import com.aptana.ide.editor.xml.formatting.XMLUtils;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editors.unified.BaseFileLanguageService;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.metadata.ElementMetadata;
import com.aptana.ide.metadata.FieldMetadata;
import com.aptana.ide.metadata.IMetadataEnvironment;
import com.aptana.ide.metadata.IMetadataItem;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public class XMLFileLanguageService extends BaseFileLanguageService
{
	private boolean isProfileMember;
	private XMLOffsetMapper offsetMapper;

	/**
	 * XMLFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	protected XMLFileLanguageService(FileService fileService, IParseState parseState, IParser parser,
			IParentOffsetMapper mapper)
	{
		super(fileService, parseState, parser, mapper);
		createOffsetMapper(mapper);
	}

	/**
	 * @return DefaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return XMLMimeType.MimeType;
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

	/**
	 * Return the mapper from offsets to lexemes
	 * 
	 * @return OffsetMapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		return offsetMapper;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseFileLanguageService#createOffsetMapper(com.aptana.ide.editors.unified.IParentOffsetMapper)
	 */
	public void createOffsetMapper(IParentOffsetMapper parent)
	{
		offsetMapper = new XMLOffsetMapper(parent);
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
		LexemeList lexemeList = getParseState().getLexemeList();
		IMetadataEnvironment environment = XMLEnvironmentRegistry
				.getEnvironment(lexemeList, lexeme.getStartingOffset());

		if (lexeme == null)
		{
			return StringUtils.EMPTY;
		}
		else
		{
			String docs = StringUtils.EMPTY;
			String lowerName = lexeme.getText().toLowerCase();

			if (lexeme.typeIndex == XMLTokenTypes.START_TAG)
			{
				lowerName = lowerName.replaceAll("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
				ElementMetadata el = environment.getElement(lowerName);
				if (el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML, new String[] {
							el.getName(), el.getDescription() });
				}
				else
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_NoInformationAvailableHTML, lexeme
							.getType());
				}
			}
			else if (lexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				lowerName = lowerName.replaceAll("</", ""); //$NON-NLS-1$ //$NON-NLS-2$
				ElementMetadata el = environment.getElement(lowerName);
				if (el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableEndTagHTML,
							new String[] { el.getName(), el.getDescription() });
				}
				else
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_NoInformationAvailableHTML, lexeme
							.getType());
				}
			}
			else if (lexeme.typeIndex == XMLTokenTypes.NAME)
			{
				Lexeme tagOpenLexeme = XMLUtils.getTagOpenLexeme(lexeme, lexemeList);
				String stripTagEndings = XMLUtils.stripTagEndings(tagOpenLexeme.getText());
				ElementMetadata element = environment.getElement(stripTagEndings);
				if (element != null)
				{
					Hashtable<String, IMetadataItem> fields = element.getFields();
					if (fields != null)
					{
						IMetadataItem metadataItem = fields.get(lowerName);
						if (metadataItem != null)
						{
							docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML,
									new String[] { metadataItem.getName(), metadataItem.getDescription() });
						}
					}
					fields = element.getEvents();
					if (fields != null)
					{
						IMetadataItem metadataItem = fields.get(lowerName);
						if (metadataItem != null)
						{
							docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML,
									new String[] { metadataItem.getName(), metadataItem.getDescription() });
						}
					}
				}
				FieldMetadata el = (FieldMetadata) environment.getGlobalFields().get(lowerName);
				if (el != null)
				{
					docs += StringUtils.format(Messages.HTMLFileLanguageService_InformationAvailableHTML, new String[] {
							el.getName(), el.getDescription() });
				}
			}
			else if (lexeme.typeIndex == XMLTokenTypes.STRING)
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
}
