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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IHelpResource;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.lexer.Lexeme;

/**
 * LexemeHelpContext
 * 
 * @author Ingo Muschenetz
 */
public class LexemeHelpContext implements IContext2
{

	private IHelpResource[] fHelpResources;
	private String fText;
	private String fTitle;

	/**
	 * LexemeHelpContext
	 * 
	 * @param context
	 * @param fileLanguageService
	 * @param lexeme
	 * @param offset
	 */
	public LexemeHelpContext(IContext context, IFileLanguageService fileLanguageService, Lexeme lexeme, int offset)
	{

		List helpResources = new ArrayList();

		// Add static help topics
		if (context != null)
		{
			IHelpResource[] resources = context.getRelatedTopics();
			if (resources != null)
			{
				for (int j = 0; j < resources.length; j++)
				{
					helpResources.add(resources[j]);
				}
			}
		}
		
		if (lexeme != null)
		{
			Lexeme newLexeme = fileLanguageService.getValidDocumentationLexeme(lexeme);

			IHelpResource[] resources = fileLanguageService.getDocumentationResourcesFromLexeme(lexeme);
			if(resources != null)
			{
				for (int i = 0; i < resources.length; i++) {
					helpResources.add(resources[i]);				
				}
			}
			
			fText = fileLanguageService.getDocumentationFromLexeme(newLexeme);
			if( fText == null || fText.length() == 0) {
				fText = Messages.LexemeHelpContext_MSG_NoDocumentationAvailable;
			}
			
			fText = stripUnusedTags(fText);

			// Title is the text that comes after the "About..." in the help window
			if (newLexeme != null)
			{
				String title = fileLanguageService.getDocumentationTitleFromLexeme(newLexeme);
				fTitle = StringUtils.format(Messages.LexemeHelpContext_About, new String[] {title});
			}
		}
		else if (context != null)
		{
			fText = context.getText();
		}

		Pattern p = Pattern.compile("<a href=\"(.*)\">(.*)</a>"); //$NON-NLS-1$
		Matcher m = p.matcher(fText);
		while(m.find())
		{
			String href = m.group(1);
			String label = m.group(2);
			HelpResource hr = new HelpResource(Messages.LexemeHelpContext_LBL_ExternalLink + label, href);
			helpResources.add(hr);
		}
		
		// Strip any links
		fText = fText.replaceAll("<a href=\"(.*)\">(.*)</a>", "$2"); //$NON-NLS-1$ //$NON-NLS-2$
		
		fHelpResources = (IHelpResource[]) helpResources.toArray(new IHelpResource[helpResources.size()]);

		if (fText == null)
		{
			fText = "No documentation available"; //$NON-NLS-1$
		}

		// Remove any more than two newlines
		fText = fText.replaceAll("\n\n\n*", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		String oldWhitespace = StringUtils.findEndWhitespace(fText);
		int newLines = StringUtils.getNumberOfNewlines(oldWhitespace);
		if(newLines <= 2)
		{
			for(int i = newLines; i < 2; i++)
			{
				fText = fText + "\n";			 //$NON-NLS-1$
			}
		}
		else
		{
			fText = StringUtils.trimEnd(fText);
			fText += "\n\n"; //$NON-NLS-1$
		}
	}
	
	private String stripUnusedTags(String text)
	{
		if(text == null)
		{
			return null;
		}
		
		String tempText = text;

		tempText = tempText.replaceAll("<h2>", "<b>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("</h2>", "</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("<hr>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("<h3>", "<b>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("</h3>", "</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("<pre>", "<code>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("</pre>", "</code>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("</?warning>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</?tip>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</?glossary>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</?method>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</?varname>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</?specification>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("<p>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = tempText.replaceAll("</p>", "<br><br>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("<br> ", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = tempText.replaceAll("<br>", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return tempText;
	}

	/**
	 * @see org.eclipse.help.IContext#getRelatedTopics()
	 */
	public IHelpResource[] getRelatedTopics()
	{
		return fHelpResources;
	}

	/**
	 * Plain text output.
	 * 
	 * @return Returns the plain text output.
	 */
	public String getText()
	{
		return fText;
	}

	/**
	 * Help UI only recognizes bold markers. Need to update to recognize
	 * 
	 * <pre>
	 *  and other colorization.
	 *  @return Returns the styled text
	 * 
	 */
	public String getStyledText()
	{
		return fText;
	}

	/**
	 * @see org.eclipse.help.IContext2#getCategory(org.eclipse.help.IHelpResource)
	 */
	public String getCategory(IHelpResource topic)
	{
		return null;
	}

	/**
	 * The title text after "About..." in the help window
	 * 
	 * @return returns The title text after "About..." in the help window
	 */
	public String getTitle()
	{
		return fTitle;
	}

	/**
	 * If lexeme is null, will then pull default content from contexts.xml file.
	 * 
	 * @param contextId
	 * @param fileLanguageService
	 * @param lexeme
	 * @param offset
	 */
	public static void displayHelp(String contextId, IFileLanguageService fileLanguageService, Lexeme lexeme, int offset)
	{

		IContext context = HelpSystem.getContext(contextId);
		if (context != null)
		{
			if (lexeme != null && lexeme.length > 0)
			{
				context = new LexemeHelpContext(context, fileLanguageService, lexeme, offset);
			}
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(context);

		}
	}
}
