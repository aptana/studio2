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
package com.aptana.ide.editor.css.contentassist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.css.CSSColors;
import com.aptana.ide.editor.css.CSSLanguageEnvironment;
import com.aptana.ide.editor.css.CSSOffsetMapper;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.preferences.IPreferenceConstants;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.contentassist.UnifiedCompletionProposal;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.FieldMetadata;
import com.aptana.ide.metadata.IMetadataEnvironment;
import com.aptana.ide.metadata.MetadataEnvironment;
import com.aptana.ide.metadata.ValueMetadata;
import com.aptana.ide.parsing.IOffsetMapper;

/**
 * 
 */
public class CSSContentAssistProcessor extends UnifiedContentAssistProcessor implements IContentAssistProcessor
{
	private static final String VALUE_PROPOSAL_ALL = "*"; //$NON-NLS-1$

	private static final String CSS_CLASS_PREFIX = "."; //$NON-NLS-1$

	private static final String CSS_ID_PREFIX = "#"; //$NON-NLS-1$

	private static final String COLON = ":"; //$NON-NLS-1$

	private static final String HTML_MIME_TYPE = "text/html"; //$NON-NLS-1$

	private IContextInformationValidator validator;

	boolean initalPopup = false;
	boolean forceActivated = false;

	// icons
	private static Image fIconField = UnifiedEditorsPlugin.getImage("icons/field_public.gif"); //$NON-NLS-1$
	private static Image fIconFile = UnifiedEditorsPlugin.getImage("icons/file.gif"); //$NON-NLS-1$
	private static Image fIconFolder = UnifiedEditorsPlugin.getImage("icons/folder.gif"); //$NON-NLS-1$
	private static Image fIconTag = UnifiedEditorsPlugin.getImage("icons/html_tag.gif"); //$NON-NLS-1$
	private static Image fIconTagGuess = UnifiedEditorsPlugin.getImage("icons/html_tag_guess.gif"); //$NON-NLS-1$

	private CSSCompletionProposalComparator contentAssistComparator;

	/** The current offset into the document */
	private int offset;

	/**
	 * INSIDE_RULE indicates we are between a { and a } Here we show the full list of CSS properties, or filtered by
	 * what is already typed
	 */
	public static String INSIDE_RULE = "INSIDE_RULE"; //$NON-NLS-1$

	/** OUTSIDE_RULE indicates we are between two rules (between a } and a { */
	public static String OUTSIDE_RULE = "OUTSIDE_RULE"; //$NON-NLS-1$

	/** ARG_ASSIST indicates we are between a : and a ; */
	public static String ARG_ASSIST = "ARG_ASSIST"; //$NON-NLS-1$

	/**
	 * ERROR indicates we in a problem state, and should bail
	 */
	public static String ERROR = "ERROR"; //$NON-NLS-1$

	private String AUTO_ADDED = Messages.CSSContentAssistProcessor_AutoAdded;

	// private IFileLanguageService languageService;

	/** The current location of the cursor. Once of the static values above */
	private String currentLocation;

	/**
	 * The current lexeme hash. Can be one of the following: "": we're typing a new property "propPrefix": We've typed
	 * some part of a new property "propPrefix:" We've typed a whole new property, and are on to argument assist
	 * "propPrefix:valPrefix" : We've typed a whole new property, and have typed some part of the value name
	 */
	private String lexemeHash = StringUtils.EMPTY;

	/** The "property" from above */
	private String propertyPrefix;

	/** The "value" from above */
	private String valuePrefix;

	private EditorFileContext context;

	private IMetadataEnvironment environment;

	/**
	 * Provides code assist information for CSS.
	 * 
	 * @param context
	 */
	public CSSContentAssistProcessor(EditorFileContext context)
	{

		this.context = context;

		environment = (IMetadataEnvironment) CSSLanguageEnvironment.getInstance().getRuntimeEnvironment();
		contentAssistComparator = new CSSCompletionProposalComparator();
		validator = new CSSContextInformationValidator(this);
	}

	/**
	 * The characters that triggers competion proposals (dot for completion, and space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{

		// For right now, we only activate on ':', which shows arg assist for the
		// current
		// property. Future work would do a VS-like insertion of \n\t on '{' to
		// immediately allow people to easily type properties.
		// Additionally ';', should trigger \n insertion, where they can close
		// the rule, or \t and type more properties.

		// Remove '}' since it is not really necessary.
		return new char[] { ':', ' ', '\t', '{', ';' };
	}

	/**
	 * The characters that triggers completion proposals (dot for completion, and space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public int[] getCompletionProposalSeparatorLexemes()
	{
		return new int[] { CSSTokenTypes.COLON, CSSTokenTypes.SEMICOLON, CSSTokenTypes.RCURLY, CSSTokenTypes.LCURLY };
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor#computeInnerCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int, int, com.aptana.ide.lexer.LexemeList, char, char)
	 */
	public ICompletionProposal[] computeInnerCompletionProposals(ITextViewer viewer, int offset, int position,
			LexemeList lexemeList, char activationChar, char previousChar)
	{
		this.offset = offset;
		if (position < 0)
		{
			return new ICompletionProposal[0];
		}

		Lexeme currentLexeme = lexemeList.get(position);

		if (unifiedViewer != null && unifiedViewer.isHotkeyActivated())
		{
			unifiedViewer.setHotkeyActivated(false);
			activationChar = DEFAULT_CHARACTER;
		}

		if (currentLexeme != null && currentLexeme.getLanguage().equals(HTML_MIME_TYPE) && position > 0)
		{
			currentLexeme = lexemeList.get(position - 1);
		}

		currentLocation = getLocation(offset, position, lexemeList, previousChar);

		if (currentLocation.equals(ERROR))
		{
			return new ICompletionProposal[0];
		}

		setPrefixes(currentLocation);

		/*
		 * CSSCompletionProposal cp = new CSSCompletionProposal( "", offset, 0, offset, fIconField, "No completions
		 * available", null, null, CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY);
		 */

		ICompletionProposal[] result = null; // new ICompletionProposal[] { cp };

		// if we are outside a rule, return no code assist
		if (currentLocation.equals(OUTSIDE_RULE))
		{
			if ((propertyPrefix != null && !propertyPrefix.equals(StringUtils.EMPTY))
					&& (previousChar == ' ' || previousChar == '\t'))
			{
				return new ICompletionProposal[0];
			}

			result = getElementCompletionProposals(propertyPrefix, previousChar, currentLexeme);
			setSelection(propertyPrefix, result);
			return result;
		}

		// previous char seems to be a default character if I have typed some content to start activation, like 'b'
		if (currentLocation.equals(INSIDE_RULE)
				&& ((activationChar != ' ' && activationChar != '\t') || previousChar == DEFAULT_CHARACTER))
		{
			boolean addColon = addColon(currentLexeme, lexemeList);
			boolean colonPref = insertColon(getPreferenceStore());
			result = getAllPropertiesCompletionProposals(propertyPrefix, currentLexeme, addColon && colonPref);
			setSelection(propertyPrefix.toLowerCase(), result);
			return result;
		}

		if (currentLocation.equals(ARG_ASSIST))
		{

			result = getSpecificPropertyCompletionProposals(offset, propertyPrefix, valuePrefix, currentLexeme,
					lexemeList);
			setSelectionUnsorted(valuePrefix, result);

			if (result.length == 0)
			{
				// show argument assist instead. For the moment, this is just info
				if (unifiedViewer != null)// && unifiedViewer instanceof SourceViewer)
				{
					((SourceViewer) unifiedViewer).doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
					return result;
				}
			}
			else
			{
				return result;
			}
		}

		// if(!fullName.equals(CSSOffsetMapper.INSIDE_RULE))
		// prefix = fullName;

		// calculate all the completion proposals. Right now, we just provide a
		// listing
		// of all possible CSS elements any time we are inside a rule.
		// This can be more sophisticated later, as for HTML elements, we also
		// know which elements can have which styles applied.
		// ICompletionProposal[] result =
		// getAllPropertiesCompletionProposals(prefix);

		return result;
	}

	/**
	 * setPrefixes
	 * 
	 * @param location
	 */
	public void setPrefixes(String location)
	{
		propertyPrefix = StringUtils.EMPTY;
		valuePrefix = StringUtils.EMPTY;

		// No semi-colon, so only a prop prefix
		if (!lexemeHash.equals(StringUtils.EMPTY))
		{
			if (location.equals(OUTSIDE_RULE))
			{
				if (lexemeHash.indexOf(COLON) < 0)
				{
					propertyPrefix = lexemeHash;
				}
				else
				{
					String[] lexemes = lexemeHash.split(COLON);
					propertyPrefix = lexemes[lexemes.length - 1];
				}
			}
			else
			{
				if (lexemeHash.indexOf(COLON) < 0)
				{
					propertyPrefix = lexemeHash;
				}
				else
				{
					// In this case, we have both items in hash, so we give the one
					// before
					// the colon to the propertyName, and the one afterwards to the
					// value;
					String[] lexemes = lexemeHash.split(COLON);
					if (lexemes.length > 0)
					{
						propertyPrefix = lexemes[0];
						if (lexemes.length > 1)
						{
							valuePrefix = lexemes[lexemes.length - 1];
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the "location" we are currently in. What this means in the end is that it helps us figure out which of
	 * three states we are in. This also sets the current name hash (which should be moved to CSSOffsetMapper
	 * eventually)
	 * 
	 * @param offset
	 *            The current offset
	 * @param currentLexemePosition
	 * @param ll
	 * @param activationCharacter
	 * @return One of the location enumerations
	 */
	public String getLocation(int offset, int currentLexemePosition, LexemeList ll, char activationCharacter)
	{
		if (offset == 0)
		{
			return OUTSIDE_RULE;
		}

		if (currentLexemePosition < 0)
		{
			return ERROR;
		}

		String location = OUTSIDE_RULE;

		int position = currentLexemePosition;
		ArrayList<String> currentHash = new ArrayList<String>();

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{

			Lexeme curLexeme = ll.get(position);

			if (!curLexeme.getLanguage().equals(CSSMimeType.MimeType))
			{
				// for STU-1241
				if (curLexeme.getLanguage().equals(HTML_MIME_TYPE))
				{
					// Only iterate over range of css tokens!
					int start = 0;
					for (int i = position - 1; i >= 0; i--)
					{
						Lexeme lex = ll.get(i);
						if (lex.getLanguage().equals(HTML_MIME_TYPE))
						{
							start = i + 1;
							break;
						}
					}
					if (position == start)
					{
						return INSIDE_RULE;
					}
					int size = position - start;
					String recursiveLocation = getLocation(offset, size - 1, new LexemeList(ll.copyRange(start,
							position - 1)), activationCharacter);
					this.offset = offset - lexemeHash.length();
					if (recursiveLocation.equals(OUTSIDE_RULE) || recursiveLocation.equals(ERROR))
						return INSIDE_RULE;
					return recursiveLocation;
				}
				break;
			}

			if (curLexeme.getText().startsWith(CSS_ID_PREFIX) || curLexeme.getText().startsWith(CSS_CLASS_PREFIX))
			{
				if (curLexeme.getCategoryIndex() == TokenCategories.LITERAL)
				{
					currentHash.add(0, curLexeme.getText());
				}
				else if (curLexeme.getCategoryIndex() == TokenCategories.ERROR && curLexeme.getText().length() == 1)
				{
					currentHash.add(0, curLexeme.getText());
				}
				else if (curLexeme.getCategoryIndex() == TokenCategories.ERROR)
				{
					location = ERROR;
					break;
				}
			}

			if (curLexeme.getCategoryIndex() == TokenCategories.IDENTIFIER)
			{
				currentHash.add(0, curLexeme.getText());
			}

			if (curLexeme.getCategoryIndex() == TokenCategories.LITERAL && curLexeme.typeIndex == CSSTokenTypes.STRING)
			{
				currentHash.add(0, curLexeme.getText());
			}

			if (curLexeme.getCategoryIndex() == TokenCategories.KEYWORD && curLexeme.typeIndex == CSSTokenTypes.URL)
			{
				currentHash.add(0, curLexeme.getText());
			}

			// If the current lexeme starts with a " as part of a value, it may not have an ending
			// ". This will result in an error
			if (curLexeme.getCategoryIndex() == TokenCategories.ERROR && curLexeme.getText().startsWith("\"")) //$NON-NLS-1$
			{
				currentHash.add(0, curLexeme.getText());
			}

			if (curLexeme.typeIndex == CSSTokenTypes.RCURLY)
			{
				location = OUTSIDE_RULE;
				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.COLON)
			{
				location = ARG_ASSIST;
				// Watch it! I want to backtrack from where I am now, not where I was
				// so pass in curLexeme.startingOffset, not offset
				Lexeme prevIdentifier = getPreviousLexemeOfType(curLexeme.getStartingOffset(), new int[] {
						CSSTokenTypes.IDENTIFIER, CSSTokenTypes.SELECTOR, CSSTokenTypes.PROPERTY }, ll, false);
				if (prevIdentifier != null)
				{
					currentHash.add(0, prevIdentifier.getText());
				}
				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.SEMICOLON)
			{
				// same issue here as with colon
				Lexeme prevIdentifier = getPreviousLexemeOfType(curLexeme.getStartingOffset(),
						new int[] { CSSTokenTypes.LCURLY }, new int[] { CSSTokenTypes.RCURLY }, ll, false);

				if (prevIdentifier != null)
				{
					location = INSIDE_RULE;
				}
				else
				{
					location = ERROR;
				}

				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.LCURLY)
			{
				location = INSIDE_RULE;
				break;
			}

			if (curLexeme.typeIndex == CSSTokenTypes.COMMA && location.equals(OUTSIDE_RULE))
			{

				Lexeme prevCurly = getPreviousLexemeOfType(curLexeme.getStartingOffset(),
						new int[] { CSSTokenTypes.LCURLY }, new int[] { CSSTokenTypes.RCURLY }, ll, false);
				if (prevCurly != null)
				{
					location = INSIDE_RULE;
				}
				else
				{
					break;
				}
			}

			position--;
		}

		if (currentHash.size() > 0)
		{
			lexemeHash = StringUtils.join(COLON, currentHash.toArray(new String[currentHash.size()]));

			// We add a trailing ":" as that indicates we've finished typing
			// propertyName
			if (location.equals(ARG_ASSIST) && lexemeHash.indexOf(COLON) < 0)
			{
				lexemeHash += COLON;
			}
		}
		else
		{
			lexemeHash = StringUtils.EMPTY;
		}

		return location;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		return validator;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#getCompletionProposalIdleActivationTokens()
	 */
	public int[] getCompletionProposalIdleActivationTokens()
	{
		return new int[] { CSSTokenTypes.CLASS, CSSTokenTypes.IDENTIFIER, CSSTokenTypes.HASH, CSSTokenTypes.SELECTOR,
				CSSTokenTypes.PROPERTY };
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return null;
	}

	/**
	 * Returns a set of valid completion proposals.
	 * 
	 * @param prefix
	 *            The text already typed (to filter on)
	 * @param activationChar
	 *            The char used to activate the code assist
	 * @param currentLexeme
	 * @return Returns an array of completion proposals.
	 */
	public ICompletionProposal[] getElementCompletionProposals(String prefix, char activationChar, Lexeme currentLexeme)
	{

		Hashtable<String, CSSCompletionProposal> completionProposals = new Hashtable<String, CSSCompletionProposal>();

		// We need to test against lower-case items
		String testPrefix = prefix.toLowerCase();

		int beginOffset = getOffsetForInsertion(offset, currentLexeme);

		String[] em = environment.getAllElements();

		int replaceLength = testPrefix.length();

		if (!prefix.startsWith(CSS_CLASS_PREFIX) && !prefix.startsWith(CSS_ID_PREFIX))
		{
			for (int i = 0; i < em.length; i++)
			{
				String e = em[i];

				String docText = environment.getElementDocumentation(e);

				String displayString = e;
				String replaceString = e;

				int cursorPosition = replaceString.length();

				Image[] userAgents = null;
				userAgents = getUserAgentImages(getUserAgents(), environment.getUserAgentPlatformNames(e));

				CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
						cursorPosition, fIconTag, displayString, null, docText,
						CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, userAgents);

				completionProposals.put(displayString, cp);
			}
		}

		String path = getEditorContentsPath();
		Collection<String> ids = CSSLanguageEnvironment.getInstance().getIds(path, "");
		Collection<String> classes = CSSLanguageEnvironment.getInstance().getClasses(path, "");

		if (ids != null)
		{
			for (String e : ids)
			{
				if (StringUtils.EMPTY.equals(e))
				{
					continue;
				}

				String trimmedValue = CSS_ID_PREFIX + StringUtils.trimStringQuotes(e);

				String docText = StringUtils.format(Messages.CSSContentAssistProcessor_IDSelectorDescription,
						new String[] { trimmedValue, e, AUTO_ADDED });

				String displayString = trimmedValue;
				String replaceString = trimmedValue;

				int cursorPosition = replaceString.length();

				CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
						cursorPosition, fIconTagGuess, displayString, null, docText,
						CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer,
						getAllUserAgentImages(getUserAgents()));

				completionProposals.put(displayString, cp);
			}
		}

		if ((classes == null || classes.size() == 0) && prefix.startsWith(CSS_CLASS_PREFIX))
		{
			return new ICompletionProposal[0];
		}

		if (classes != null)
		{
			for (String e : classes)
			{
				String trimmedValue = CSS_CLASS_PREFIX + StringUtils.trimStringQuotes(e);
				String docText = StringUtils.format(Messages.CSSContentAssistProcessor_ClassSelectorDescription,
						new String[] { trimmedValue, e, AUTO_ADDED });

				String displayString = trimmedValue;
				String replaceString = trimmedValue;

				int cursorPosition = replaceString.length();

				CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
						cursorPosition, fIconTagGuess, displayString, null, docText,
						CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer,
						getAllUserAgentImages(getUserAgents()));

				completionProposals.put(displayString, cp);
			}
		}

		ICompletionProposal[] result = completionProposals.values().toArray(
				new ICompletionProposal[completionProposals.size()]);
		Arrays.sort(result, contentAssistComparator);

		return result;
	}

	private String getEditorContentsPath()
	{
		IEditorInput pathEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();
		if (pathEditor instanceof IFileEditorInput)
		{
			IFileEditorInput fileEI = (IFileEditorInput) pathEditor;
			return fileEI.getFile().getProject().getFullPath().toPortableString();
		}
		return null;
	}

	/**
	 * Returns a set of valid completion proposals.
	 * 
	 * @param offset
	 * @param propertyName
	 *            The text already typed for the "property"
	 * @param valueName
	 *            The text already typed for the "value"
	 * @param currentLexeme
	 * @return Returns an array of completion proposals.
	 */
	public ICompletionProposal[] getSpecificPropertyCompletionProposals(int offset, String propertyName,
			String valueName, Lexeme currentLexeme, LexemeList lexemeList)
	{

		if (valueName.startsWith("url("))
		{
			return getFilePathCompletionProposals(valueName, offset, 0);
		}
		else if (currentLexeme.getText().startsWith("url("))
		{
			return getFilePathCompletionProposals(currentLexeme.getText(), offset, 0);
		}
		
		Lexeme lexeme = currentLexeme;
		String valueString = valueName;

		ArrayList<CSSCompletionProposal> completionProposals = new ArrayList<CSSCompletionProposal>();
		ArrayList<ValueMetadata> addedFields = new ArrayList<ValueMetadata>();

		// Global fields are the list of "all" fields
		Hashtable fields = environment.getGlobalFields();

		String propertyNameLower = propertyName.toLowerCase();
		// String valueNameLower = valueName.toLowerCase();

		FieldMetadata fm = (FieldMetadata) fields.get(propertyNameLower);

		if (fm == null)
		{
			return new ICompletionProposal[0];
		}

		Map<String, Image> colors = new HashMap<String, Image>();
		boolean isColor = fm.getName().equals("background-color") || fm.getName().equals("color"); //$NON-NLS-1$ //$NON-NLS-2$
		// Suggest colors already used in file!
		if (isColor)
		{
			fm.getValues().clear();
			for (Image image : colors.values())
			{
				image.dispose();
			}
			colors.clear();
			Collection<String> colorsUsedInProject = getColorsUsed();
			// Color! Grab all colors defined in file and stick them at top
			for (String color : colorsUsedInProject)
			{
				if (!colors.containsKey(color))
				{
					ValueMetadata value = new ValueMetadata();
					value.setName(color);
					fm.addValue(value);
					colors.put(color, CSSColors.toImage(color, 16, 16));
				}
			}
		}

		if (offset > lexeme.getEndingOffset() && fm.getAllowMultipleValues())
		{
			lexeme = null;
			valueString = StringUtils.EMPTY;
		}

		int beginOffset = getOffsetForInsertion(offset, lexeme);

		boolean insertSemicolonPref = insertSemicolon(getPreferenceStore());

		for (int i = 0; i < fm.getValues().size(); i++)
		{
			ValueMetadata value = (ValueMetadata) fm.getValues().get(i);

			String docText = StringUtils.EMPTY;
			docText = MetadataEnvironment.getValueDocumentation(value);

			// TODO Proper-case string back to what they originally had
			String replaceString = value.getName();
			String displayString = value.getName();

			// Fixed #592. We now insert "" in place of "*"
			if (replaceString.equals(VALUE_PROPOSAL_ALL))
			{
				replaceString = StringUtils.EMPTY;
			}
			else if (!fm.getAllowMultipleValues() && insertSemicolonPref)
			{
				replaceString = addSemicolonIfNecessary(replaceString, lexemeList, currentLexeme);
			}

			int cursorPosition = replaceString.length();

			// TODO: Later, we may wish to replace _all_ values after the
			// colon (up to the next semi-colon) with
			// the added value
			int replaceLength = valueString.length();

			Image[] userAgents = null;
			if (value.getUserAgents().length == 0)
			{
				userAgents = getUserAgentImages(getUserAgents(), fm.getUserAgentPlatformNames());
			}
			else
			{
				userAgents = getUserAgentImages(getUserAgents(), value.getUserAgentPlatformNames());
			}

			Image icon = fIconField;
			if (isColor)
			{
				icon = colors.get(value.getName());
			}
			CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
					cursorPosition, icon, displayString, null, docText, CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, userAgents);

			if (cp != null && !addedFields.contains(value))
			{
				addedFields.add(value);
				completionProposals.add(cp);
			}
		}

		ICompletionProposal[] result = completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);

		// Arrays.sort(result, contentAssistComparator);

		return result;
	}

	private Collection<String> getColorsUsed()
	{
		return CSSLanguageEnvironment.getInstance().getColors(getEditorContentsPath(), "");
	}

	/**
	 * Appends semicolon to replacement string for proposal if next lexeme is not a semicolon.
	 * 
	 * @param replaceString
	 * @param lexemeList
	 * @param currentLexeme
	 * @return
	 */
	private String addSemicolonIfNecessary(String replaceString, LexemeList lexemeList, Lexeme currentLexeme)
	{
		int currentPosition = lexemeList.getLexemeIndex(currentLexeme);
		if (lexemeList.size() <= currentPosition + 1)
			return replaceString + ";"; //$NON-NLS-1$;
		Lexeme next = lexemeList.get(currentPosition + 1);
		if (next != null && next.typeIndex == CSSTokenTypes.SEMICOLON)
			return replaceString;
		return replaceString + ";"; //$NON-NLS-1$
	}

	/**
	 * Gets the offset for inserting a new item into the document
	 * 
	 * @param offset
	 * @param currentLexeme
	 * @return The index at which to insert
	 */
	public int getOffsetForInsertion(int offset, Lexeme currentLexeme)
	{

		if (currentLexeme == null)
		{
			return offset;
		}

		int beginOffset = offset;

		// have to check that the current lexeme is a CSS lexeme
		// TODO: Move this further up the chain.
		if (!currentLexeme.getLanguage().equals(CSSMimeType.MimeType))
		{
			return offset;
		}

		// If we're in an identifier, it's likely we'll want to replace that
		// current value
		if (currentLexeme.typeIndex == CSSTokenTypes.IDENTIFIER || currentLexeme.typeIndex == CSSTokenTypes.PROPERTY
				|| currentLexeme.typeIndex == CSSTokenTypes.SELECTOR
				|| currentLexeme.getCategoryIndex() == TokenCategories.LITERAL

				// here, we assume if we're in an error, we just replace the while thing
				|| (currentLexeme.getCategoryIndex() == TokenCategories.ERROR))
		{
			beginOffset = currentLexeme.getStartingOffset();
		}

		return beginOffset;
	}

	/**
	 * addColon
	 * 
	 * @param currentLexeme
	 * @param lexemeList
	 * @return boolean
	 */
	public boolean addColon(Lexeme currentLexeme, LexemeList lexemeList)
	{
		if (currentLexeme == null)
		{
			return false;
		}

		int lexemeIndex = lexemeList.getLexemeIndex(currentLexeme);
		if (lexemeIndex < lexemeList.size() - 1)
		{
			Lexeme sibling = lexemeList.get(lexemeIndex + 1);
			if (sibling != null && sibling.typeIndex != CSSTokenTypes.COLON)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns a set of valid completion proposals.
	 * 
	 * @param prefix
	 *            The text already typed (to filter on)
	 * @param currentLexeme
	 * @param addColon
	 * @return Returns an array of completion proposals.
	 */
	public ICompletionProposal[] getAllPropertiesCompletionProposals(String prefix, Lexeme currentLexeme,
			boolean addColon)
	{
		Hashtable<String, CSSCompletionProposal> completionProposals = new Hashtable<String, CSSCompletionProposal>();

		// Global fields are the list of "all" fields
		Hashtable fields = environment.getGlobalFields();

		Collection vals = fields.values();
		Iterator iter = vals.iterator();

		// We need to test against lower-case items
		String testPrefix = prefix.toLowerCase();

		int beginOffset = getOffsetForInsertion(offset, currentLexeme);

		while (iter.hasNext())
		{
			FieldMetadata fm = (FieldMetadata) iter.next();

			String docText = StringUtils.EMPTY;
			docText = environment.getFieldDocumentation(fm);

			// TODO Proper-case string back to what they originally had
			// Commented out the ":" as it was giving grief with auto-pop;
			String replaceString = fm.getName();

			// [IM] Don't add on ':' for the moment
			if (addColon)
			{
				replaceString += COLON;
			}

			String displayString = fm.getName();
			int cursorPosition = replaceString.length();

			// TODO: Later, we may wish to replace _all_ values after the
			// colon (up to the next semi-colon) with
			// the added value
			int replaceLength = testPrefix.length();

			Image[] userAgents = getUserAgentImages(getUserAgents(), fm.getUserAgentPlatformNames());

			CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
					cursorPosition, fIconField, displayString, null, docText,
					CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, userAgents);

			if (cp != null)
			{
				completionProposals.put(fm.getName(), cp);
				// cp.scheduleContentAssistOnInsert(true);
			}
		}

		ICompletionProposal[] result = completionProposals.values().toArray(
				new ICompletionProposal[completionProposals.size()]);

		Arrays.sort(result, contentAssistComparator);

		return result;
	}

	/**
	 * getOffsetMapper
	 * 
	 * @return CSSOffsetMapper
	 */
	public CSSOffsetMapper getCSSOffsetMapper()
	{
		return (CSSOffsetMapper) getOffsetMapper();
	}

	/**
	 * Returns a reference to the current offsetMapper
	 * 
	 * @return The reference to the mapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		IFileLanguageService ls = this.context.getLanguageService(CSSMimeType.MimeType);

		if (ls != null)
		{
			return ls.getOffsetMapper();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Calculates the current lexeme in the document
	 * 
	 * @param offset
	 *            the offset of the current insertion point
	 * @return The current lexeme, or null if not found
	 */
	public Lexeme getCurrentLexeme(int offset)
	{
		this.getOffsetMapper().calculateCurrentLexeme(offset);
		Lexeme currentLexeme = this.getOffsetMapper().getCurrentLexeme();
		return currentLexeme;
	}

	/**
	 * @see UnifiedContentAssistProcessor#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(CSSPlugin.getDefault()))
		{
			return CSSPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

	/**
	 * getPropertyPrefix
	 * 
	 * @return String
	 */
	public String getPropertyPrefix()
	{
		return propertyPrefix;
	}

	/**
	 * getValuePrefix
	 * 
	 * @return String
	 */
	public String getValuePrefix()
	{
		return valuePrefix;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor#computeInnerContextInformation(java.lang.String,
	 *      int, int, com.aptana.ide.lexer.LexemeList)
	 */
	public IContextInformation[] computeInnerContextInformation(String documentSource, int offset, int position,
			LexemeList lexemeList)
	{

		IContextInformation[] ici = null;

		if (propertyPrefix == null)
		{
			return new IContextInformation[0];
		}

		Hashtable<String, FieldMetadata> fields = environment.getGlobalFields();

		String propertyNameLower = propertyPrefix.toLowerCase();

		// Don't show context information until I can eliminate the double
		// popups.
		// if (true)
		// return null;

		FieldMetadata fm = (FieldMetadata) fields.get(propertyNameLower);
		if (fm == null)
		{
			return new IContextInformation[0];
		}

		// Right now we just print the description. This should be a list of
		// arguments, like VS 2003
		String hint = fm.getDescription();

		if (fm.getHint() != null)
		{
			hint = fm.getHint();
		}

		if (hint == null)
		{
			hint = Messages.CSSContentAssistProcessor_NoHintAvailable;
		}

		ContextInformation ci = new ContextInformation(
				"contextDisplayString", StringUtils.format(Messages.CSSContentAssistProcessor_ContextHint, new String[] { fm.getName(), hint })); //$NON-NLS-1$
		if (ci != null)
		{
			ici = new IContextInformation[] { ci };
		}

		return ici;
	}

	/**
	 * Do we insert a colon?
	 * 
	 * @param store
	 * @return String
	 */
	public static boolean insertColon(IPreferenceStore store)
	{
		if (store != null)
		{
			return store.getBoolean(IPreferenceConstants.CSSEDITOR_INSERT_COLON);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Do we insert a semicolon?
	 * 
	 * @param store
	 * @return String
	 */
	public static boolean insertSemicolon(IPreferenceStore store)
	{
		if (store != null)
		{
			return store.getBoolean(IPreferenceConstants.CSSEDITOR_INSERT_SEMICOLON);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see UnifiedContentAssistProcessor#getProposalComparator()
	 */
	public Comparator<ICompletionProposal> getProposalComparator()
	{
		return contentAssistComparator;
	}
	
	/**
	 * getFilePathCompletionProposals
	 * 
	 * @param valuePrefix
	 * @param beginOffset
	 * @param replaceLength
	 * @param sortingType
	 */
	private ICompletionProposal[] getFilePathCompletionProposals(String valuePrefix, int beginOffset,
			int replaceLength)
	{
		List<UnifiedCompletionProposal> completionProposals = new ArrayList<UnifiedCompletionProposal>();

		IEditorInput pathEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();

		String urlPrefix = null;
		if (pathEditor instanceof IFileEditorInput)
		{
			urlPrefix = getProjectContextRoot((IFileEditorInput)pathEditor);
		}

		String editorPath = CoreUIUtils.getPathFromEditorInput(pathEditor);
		String basePath = new Path(CoreUIUtils.getPathFromEditorInput(pathEditor)).removeLastSegments(1).toOSString();

		if (urlPrefix != null && !"".equals(urlPrefix)) //$NON-NLS-1$
		{
			basePath = urlPrefix;
		}

		String currentPath = basePath;
		if (valuePrefix != null)
		{
			if (valuePrefix.startsWith("url("))
			{
				valuePrefix = valuePrefix.substring(4);
			}
			if (valuePrefix.endsWith(")"))
			{
				valuePrefix = valuePrefix.substring(0, valuePrefix.length() - 1);
			}
			String s = StringUtils.trimStringQuotes(valuePrefix);
			if (!"".equals(s)) //$NON-NLS-1$
			{
				beginOffset -= s.length();
				replaceLength += s.length();
				File current = new File(currentPath);

				if (current.isDirectory())
				{
					if (currentPath.endsWith(File.separator))
						currentPath = currentPath.substring(0, currentPath.length() - 1);
					currentPath = currentPath + s;
				}
				else
				{
					currentPath = current.getParent().toString() + File.separator + s;
				}
			}
		}

		File[] files = FileUtils.getFilesInDirectory(new File(currentPath));

		if (files == null)
		{
			return new ICompletionProposal[0];
		}

		for (File f : files)
		{
			if (f.getName().startsWith(".")) //$NON-NLS-1$
			{
				continue;
			}
			
			// Don't include the current file in the list
			if (f.toString().equals(editorPath))
			{
				continue;
			}

			Image image = getImage(f);
			String replaceString = FileUtils.makeFilePathRelative(new File(basePath), f);
			replaceString = replaceString.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			String displayString = CoreUIUtils.getPathFromURI(replaceString);

			int cursorPosition = replaceString.length();

			CSSCompletionProposal cp = new CSSCompletionProposal(replaceString, beginOffset, replaceLength,
					cursorPosition, image, displayString, null, f.toString(), CSSCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, new Image[0]);

			if (cp != null)
			{
				completionProposals.add(cp);
			}
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}
	
	private Image getImage(File f)
	{
		String fileType = ""; //$NON-NLS-1$
		try
		{
			fileType = FileSystemView.getFileSystemView().getSystemTypeDescription(f);
		}
		catch (Exception ex)
		{
			IdeLog.logError(CSSPlugin.getDefault(), "FIXME!!!!", ex);
		}

		Image image = null;
		if (fileType != null)
		{
			image = com.aptana.ide.core.ui.ImageUtils.fileIconsHash.get(fileType);
		}

		if (image == null)
		{
			image = fIconFile;

			if (f.isDirectory())
			{
				image = fIconFolder;
			}
		}
		return image;
	}

	/**
	 * Get the name of the project context root
	 * 
	 * @param input
	 * @return
	 * @throws CoreException
	 */
	private String getProjectContextRoot(IFileEditorInput input)
	{
		String urlPrefix = "/";
		IFile file = input.getFile();
		IProject project = file.getProject();
		try
		{
			String contextRoot = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
					"com.aptana.ide.editor.html.preview.CONTEXT_ROOT"));
			if (contextRoot != null && !contextRoot.equals("/")) //$NON-NLS-1$
			{
				urlPrefix = contextRoot + "/";
			}
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return project.getLocation().append(urlPrefix).toOSString();
	}
}
