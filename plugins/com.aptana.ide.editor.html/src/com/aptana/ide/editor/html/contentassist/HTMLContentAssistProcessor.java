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
package com.aptana.ide.editor.html.contentassist;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.KeyValuePair;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.css.CSSLanguageEnvironment;
import com.aptana.ide.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.ide.editor.html.HTMLLanguageEnvironment;
import com.aptana.ide.editor.html.HTMLOffsetMapper;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLDocumentType;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.contentassist.CodeAssistExpression;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.ElementMetadata;
import com.aptana.ide.metadata.EventMetadata;
import com.aptana.ide.metadata.FieldMetadata;
import com.aptana.ide.metadata.IMetadataEnvironment;
import com.aptana.ide.metadata.MetadataEnvironment;
import com.aptana.ide.metadata.ValueMetadata;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseNodeAttribute;
import com.aptana.ide.parsing.xpath.ParseNodeXPath;
import com.aptana.ide.server.jetty.server.HTMLContextRootUtils;

/**
 * 
 */
public class HTMLContentAssistProcessor extends UnifiedContentAssistProcessor implements IContentAssistProcessor
{
	private IContextInformationValidator validator;

	private static Map<String, String> entities;

	// icons
	private static Image fIconField = UnifiedEditorsPlugin.getImage("icons/field_public.gif"); //$NON-NLS-1$
	private static Image fIconFieldGuess = UnifiedEditorsPlugin.getImage("icons/field_public_guess.gif"); //$NON-NLS-1$
	private static Image fIconTag = UnifiedEditorsPlugin.getImage("icons/html_tag.gif"); //$NON-NLS-1$
	private static Image fIconTagGuess = UnifiedEditorsPlugin.getImage("icons/html_tag_guess.gif"); //$NON-NLS-1$
	private static Image fIconEvent = UnifiedEditorsPlugin.getImage("icons/event.gif"); //$NON-NLS-1$
	private static Image fIconFile = UnifiedEditorsPlugin.getImage("icons/file.gif"); //$NON-NLS-1$
	private static Image fIconFolder = UnifiedEditorsPlugin.getImage("icons/folder.gif"); //$NON-NLS-1$
	// private static Image fIconFirefox = UnifiedEditorsPlugin.getImage("icons/firefox_icon.gif");
	// private static Image fIconIE = UnifiedEditorsPlugin.getImage("icons/ie_icon.gif");

	private HTMLCompletionProposalComparator contentAssistComparator;

	private String AUTO_ADDED = "Auto-added from environment"; //$NON-NLS-1$

	/**
	 * ERROR indicates we are in some error state and we just escape.
	 */
	public static String ERROR = "ERROR"; //$NON-NLS-1$

	/**
	 * OUTSIDE_ELEMENT indicates we are outside a tag. This allows suggestion of any possible tag names (not looking at
	 * document semantics yet.
	 */
	public static String OUTSIDE_ELEMENT = "OUTSIDE_ELEMENT"; //$NON-NLS-1$

	/**
	 * INSIDE_OPEN_ELEMENT indicates we are between a < and a > Here we show the full list of HTML properties, or
	 * filtered by what is already typed
	 */
	public static String INSIDE_OPEN_ELEMENT = "INSIDE_OPEN_ELEMENT"; //$NON-NLS-1$

	/**
	 * INSIDE_OPEN_ELEMENT indicates we are between a </ and a > Here we basically the "close" tag version of what the
	 * immediately prior open tag is
	 */
	public static String INSIDE_END_TAG = "INSIDE_END_TAG"; //$NON-NLS-1$

	private EditorFileContext context;
	private IMetadataEnvironment environment;
	private Hashtable<String, ArrayList<String>> additionalProposals = new Hashtable<String, ArrayList<String>>();

	/**
	 * Provides code assist information for HTML.
	 * 
	 * @param context
	 */
	public HTMLContentAssistProcessor(EditorFileContext context)
	{
		this.context = context;
		this.environment = (IMetadataEnvironment) HTMLLanguageEnvironment.getInstance().getRuntimeEnvironment();
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor#computeInnerCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int, int, com.aptana.ide.lexer.LexemeList, char, char)
	 */
	public ICompletionProposal[] computeInnerCompletionProposals(ITextViewer viewer, int offset, int position,
			LexemeList lexemeList, char activationChar, char previousChar)
	{
		additionalProposals.clear();

		// Tricky bug. If we are Ctrl + Space in a document that has
		// not been typed in yet, a current lexeme will not have been calculated
		HTMLContextLocation currentLocation = getLocation(offset, lexemeList);
		String tagPrefix = currentLocation.getTagName();

		ICompletionProposal[] result = null;

		// if we are inside a closing tag, just show the closure completions
		// rather than trying to guess the tag, just show the list of
		// all tags again

		if (unifiedViewer != null && unifiedViewer.isHotkeyActivated())
		{
			unifiedViewer.setHotkeyActivated(false);
			activationChar = DEFAULT_CHARACTER;
		}

		if (position < 0)
		{
			return null;
		}

		Lexeme currentLexeme = lexemeList.get(position);

		// if we are in the end tag, just complete
		if (currentLocation.getLocation().equals(ERROR))
		{
			return result;
		}

		// if we are in the end tag, just complete
		if (currentLocation.getLocation().equals(INSIDE_END_TAG) && currentLocation.getAttributes().size() == 0)
		{
			return getTagCompletionProposals(tagPrefix, previousChar, offset, currentLexeme, lexemeList);
		}

		// if we are outside tag, don't show anything
		if (currentLocation.getLocation().equals(OUTSIDE_ELEMENT) && previousChar == '/')
		{
			return getTagCompletionProposals("", activationChar, offset, currentLexeme, lexemeList); //$NON-NLS-1$
		}

		// if we are outside tag, don't show anything, unless we have typed "<"
		if (currentLocation.getLocation().equals(OUTSIDE_ELEMENT)
				&& (activationChar == '<' || activationChar == DEFAULT_CHARACTER))
		{
			if (currentLexeme != null && currentLexeme.typeIndex == HTMLTokenTypes.TEXT
					&& currentLexeme.getText().trim().startsWith("&"))
			{
				result = computeHTMLEntityProposals(currentLexeme, currentLexeme.getText()).values().toArray(
						new ICompletionProposal[0]);
				setSelection(currentLexeme.getText(), result);
				return result;
			}
			return getTagCompletionProposals("", previousChar, offset, currentLexeme, lexemeList); //$NON-NLS-1$
		}

		if (currentLocation.getLocation().equals(OUTSIDE_ELEMENT) && activationChar == '&')
		{
			String prefix = currentLexeme.getText();
			int length = offset - currentLexeme.getStartingOffset();
			if (length <= 0)
			{
				prefix = "";
			}
			else if (length <= prefix.length())
			{
				prefix = prefix.substring(0, length);
			}
			result = computeHTMLEntityProposals(currentLexeme, prefix).values().toArray(
					new ICompletionProposal[0]);
			setSelection(currentLexeme.getText(), result);
			return result;
		}

		ArrayList<KeyValuePair> attributes = currentLocation.getAttributes();
		String attributePrefix = null;
		String valuePrefix = null;
		if (attributes.size() > 0)
		{
			for (KeyValuePair kvp : attributes)
			{
				if (kvp.getLocation() == currentLexeme.offset)
				{
					attributePrefix = (String) kvp.getKey();
					valuePrefix = (String) kvp.getValue();
					break;
				}
			}
		}

		// if we are inside an open element
		if (currentLocation.getLocation().equals(INSIDE_OPEN_ELEMENT))
		{

			// If the previous char is a " ", we are now into attributes
			if ((previousChar == ' ' || previousChar == '\t') && tagPrefix != null)
			{
				// We don't want to show arg assist inside a string with spaces.
				// However, our cursor actually has to be _inside_ the lexeme
				// text to bail
				if (HTMLUtils.insideQuotedString(currentLexeme, offset))
				{
					return null;
				}

				attributePrefix = ""; //$NON-NLS-1$
				if (currentLexeme.getCategoryIndex() != TokenCategories.ERROR)
				{
					valuePrefix = null;
				}
			}

			// if tagPrefix == "", or attributePrefix == null
			// we are finishing up an open tag, or we might have backtracked
			// into one we already finished
			if (attributePrefix == null || currentLexeme.typeIndex == HTMLTokenTypes.START_TAG
					&& currentLexeme.containsOffset(offset))
			{

				// What if we're inside a "style" attribute?
				KeyValuePair stylePair = get(attributes, "style");
				if (stylePair != null
						&& (offset > stylePair.getLocation() && offset <= (stylePair.getLocation() + 2 + ((String) stylePair
								.getValue()).length())))
				{
					// We're inside "style" attribute value!
					return new CSSContentAssistProcessor(context).computeInnerCompletionProposals(viewer, offset,
							position, lexemeList, activationChar, previousChar);
				}
				result = getTagCompletionProposals(tagPrefix, previousChar, offset, currentLexeme, lexemeList);
			}

			// if valuePrefix != we are starting a new value
			else if (valuePrefix != null)
			{
				// Hack fix for align=/> (it would delete the /)
				if (valuePrefix.equals("/")) //$NON-NLS-1$
				{
					valuePrefix = ""; //$NON-NLS-1$
				}

				result = getAttributeValueCompletionProposals(tagPrefix, attributePrefix, valuePrefix, offset);

				String strippedValue = StringUtils.trimStringQuotes(valuePrefix);
				setSelection(strippedValue, result);

			}

			// otherwise, we are adding attributes
			else
			{
				Lexeme startTag = HTMLUtils.getTagOpenLexeme(currentLexeme, lexemeList);
				Lexeme endTag = HTMLUtils.getTagCloseLexeme(currentLexeme, lexemeList);
				Map<String, String> attribs = HTMLUtils.gatherAttributes(startTag, endTag, lexemeList);
				boolean attributeQuoted = attribs.get(attributePrefix) == null ? false : true;
				result = getAttributeCompletionProposals(tagPrefix, attributePrefix, previousChar, attributes,
						attributeQuoted, offset);

				setSelection(attributePrefix.toLowerCase(), result);
			}

		}

		return result;
	}

	private KeyValuePair get(ArrayList<KeyValuePair> attributes, String key)
	{
		for (KeyValuePair keyValuePair : attributes)
		{
			if (keyValuePair.getKey().equals(key))
				return keyValuePair;
		}
		return null;
	}

	/**
	 * getAttributeValueCompletionProposals
	 * 
	 * @param tagPrefix2
	 * @param attributePrefix2
	 * @param valuePrefix
	 * @param offset
	 * @return ICompletionProposal[]
	 */
	public ICompletionProposal[] getAttributeValueCompletionProposals(String tagPrefix2, String attributePrefix2,
			String valuePrefix, int offset)
	{
		if (tagPrefix2 == null || attributePrefix2 == null)
		{
			return null;
		}

		String strippedValue = StringUtils.trimStringQuotes(valuePrefix);

		ArrayList<ICompletionProposal> completionProposals = new ArrayList<ICompletionProposal>();
		ArrayList<String> addedFields = new ArrayList<String>();

		// Global fields are the list of "all" fields
		Hashtable<String, FieldMetadata> fields = environment.getGlobalFields();

		String tagNameLower = tagPrefix2.toLowerCase();
		String propertyNameLower = attributePrefix2.toLowerCase();
		String valueNameLower = strippedValue.toLowerCase();

		int beginOffset = getOffsetForInsertion(getHTMLOffsetMapper().getCurrentLexeme(), offset);
		int replaceLength = valueNameLower.length();

		Lexeme curLexeme = getHTMLOffsetMapper().getCurrentLexeme();

		if (curLexeme.getCategoryIndex() == TokenCategories.ERROR)
		{
			beginOffset = curLexeme.offset;
		}

		if (valuePrefix.startsWith("\"") || valuePrefix.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			beginOffset++;
		}

		/*
		 * else if(valuePrefix.startsWith("\"") || valuePrefix.startsWith("'") ) { beginOffset = beginOffset -
		 * (valuePrefix.length() - 1); replaceLength = valuePrefix.length() - 1; }
		 */

		int sortingType = HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY;

		ElementMetadata em = environment.getElement(tagNameLower);
		FieldMetadata fm = null;
		if (em == null)
		{
			fm = fields.get(propertyNameLower);
		}
		else
		{
			fm = (FieldMetadata) em.getFields().get(propertyNameLower);
		}

		if (fm != null)
		{
			ICompletionProposal[] props = getFieldMetadataCompletionProposals(em, fm, beginOffset, replaceLength,
					sortingType);
			completionProposals.addAll(Arrays.asList(props));
		}

		try
		{
			if (propertyNameLower.equals("src")) //$NON-NLS-1$
			{
				ICompletionProposal[] fileProps = getFilePathCompletionProposals(valuePrefix, beginOffset,
						replaceLength, sortingType);
				completionProposals.addAll(Arrays.asList(fileProps));
			}
		}
		catch (Exception ex)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.HTMLContentAssistProcessor_ErrorComputingFilePath, ex);
		}
		
		try
		{
			if (propertyNameLower.equals("class")) //$NON-NLS-1$
			{
				ICompletionProposal[] fileProps = getCSSClassCompletionProposals(valuePrefix, beginOffset,
						replaceLength, sortingType);
				completionProposals.addAll(Arrays.asList(fileProps));
			}
			else if (propertyNameLower.equals("id")) //$NON-NLS-1$
			{
				ICompletionProposal[] fileProps = getCSSIdCompletionProposals(valuePrefix, beginOffset,
						replaceLength, sortingType);
				completionProposals.addAll(Arrays.asList(fileProps));
			}
		}
		catch (Exception ex)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.HTMLContentAssistProcessor_ErrorComputingFilePath, ex);
		}

		ICompletionProposal[] xPathProps = getXPathCompletionProposals(propertyNameLower, beginOffset, replaceLength,
				sortingType);
		completionProposals.addAll(Arrays.asList(xPathProps));

		if (additionalProposals.containsKey(propertyNameLower))
		{
			ArrayList<String> addl = additionalProposals.get(fm.getName());
			for (int i = 0; i < addl.size(); i++)
			{
				String s = (String) addl.get(i);
				String trimmedValue = StringUtils.trimStringQuotes(StringUtils.trimStringQuotes(s));
				if (trimmedValue.equals("")) //$NON-NLS-1$
				{
					continue;
				}

				String replaceString = trimmedValue;
				String displayString = trimmedValue;
				int cursorPosition = replaceString.length();

				HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
						cursorPosition, fIconFieldGuess, displayString, null, "<b>" + trimmedValue + "</b><br>" //$NON-NLS-1$ //$NON-NLS-2$
								+ AUTO_ADDED, sortingType, unifiedViewer, null);

				if (cp != null && !addedFields.contains(s))
				{
					addedFields.add(s);
					completionProposals.add(cp);
				}
			}
		}

		ICompletionProposal[] result = completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);
		Arrays.sort(result, getProposalComparator());

		return result;

	}

	/**
	 * Returns the "location" we are currently in. What this means in the end is that it helps us figure out which of
	 * three states we are in. This also sets the current name hash (which should be moved to HTMLOffsetMapper
	 * eventually)
	 * 
	 * @param offset
	 *            The current offset
	 * @param ll
	 * @return One of the location enumerations
	 */
	public HTMLContextLocation getLocation(int offset, LexemeList ll)
	{
		HTMLContextLocation cl = new HTMLContextLocation();
		cl.setLocation(OUTSIDE_ELEMENT);

		if (offset == 0)
		{
			return cl;
		}

		int currentLexemePosition = computeCurrentLexemeIndex(offset, ll);
		KeyValuePair currentAttribute = null;

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (currentLexemePosition >= 0)
		{
			Lexeme curLexeme = ll.get(currentLexemePosition);

			// attributes can be NAME EQUALS STRING OR NAME EQUALS NAME
			if (curLexeme.typeIndex == HTMLTokenTypes.NAME)
			{

				boolean isAttributeValue = false;

				// If previous lexeme is an equals, we are an attribute value
				if (currentLexemePosition > 0)
				{
					Lexeme prev = ll.get(currentLexemePosition - 1);

					if (prev.typeIndex == HTMLTokenTypes.EQUAL)
					{
						isAttributeValue = true;
					}
				}

				String lexemeText = curLexeme.getText();

				// create a new KeyValuePair, but don't add it yet
				if (isAttributeValue)
				{
					currentAttribute = new KeyValuePair("PLACEHOLDER", lexemeText, curLexeme.offset); //$NON-NLS-1$
				}

				else if (currentAttribute != null)
				{
					// add at beginning as we are parsing in reverse order
					KeyValuePair foundAttribute = cl.find(lexemeText, currentAttribute.getLocation());

					if (foundAttribute != null && foundAttribute.getLocation() != curLexeme.offset)
					{
						cl.getAttributes().remove(foundAttribute);
					}

					cl.getAttributes().add(0,
							new KeyValuePair(lexemeText, currentAttribute.getValue(), currentAttribute.getLocation()));
					currentAttribute = null;
				}
				else
				{
					// add at beginning as we are parsing in reverse order
					KeyValuePair foundAttribute = cl.find(lexemeText, curLexeme.offset);

					if (foundAttribute != null && foundAttribute.getLocation() != curLexeme.offset)
					{
						cl.getAttributes().remove(foundAttribute);
					}

					cl.getAttributes().add(0, new KeyValuePair(lexemeText, null, curLexeme.offset));
					currentAttribute = null;
				}
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.EQUAL)
			{
				if (currentAttribute == null)
				{
					currentAttribute = new KeyValuePair("PLACEHOLDER", "", curLexeme.offset); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.STRING)
			{
				currentAttribute = new KeyValuePair("PLACEHOLDER", curLexeme.getText(), curLexeme.offset); //$NON-NLS-1$
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				cl.setTagName(curLexeme.getText().replaceAll("<", "")); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.END_TAG)
			{
				cl.setTagName(curLexeme.getText().replaceAll("</", "")); //$NON-NLS-1$ //$NON-NLS-2$
				cl.setLocation(INSIDE_END_TAG);
				break;
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.GREATER_THAN)
			{
				cl.setLocation(OUTSIDE_ELEMENT);
				break;
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.ERROR && curLexeme.getText().equals("/")) //$NON-NLS-1$
			{
				cl.setLocation(ERROR);
				break;
			}
			if (curLexeme.typeIndex == HTMLTokenTypes.SLASH_GREATER_THAN
					&& (curLexeme.containsOffset(offset) || offset >= curLexeme.getEndingOffset()))
			{
				cl.setLocation(OUTSIDE_ELEMENT);
				break;
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.START_TAG || curLexeme.typeIndex == HTMLTokenTypes.ERROR
					&& curLexeme.getText().equals("<")) //$NON-NLS-1$
			{
				cl.setLocation(INSIDE_OPEN_ELEMENT);
				break;
			}

			currentLexemePosition--;
		}

		return cl;
	}

	/**
	 * The characters that triggers completion proposals (dot for completion, and space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '<', '/', ' ', '\t', '=', '>', '&' };
	}

	/**
	 * Characters that trigger tooltip popup help
	 * 
	 * @return Returns the trigger characters for auto activation.
	 */
	public char[] getContextInformationAutoActivationCharacters()
	{
		// Make context popup automatically after the following characters
		return new char[] { '=' };
	}

	/**
	 * The characters that triggers competion proposals (dot for completion, and space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public int[] getCompletionProposalSeparatorLexemes()
	{
		return new int[] { HTMLTokenTypes.GREATER_THAN, HTMLTokenTypes.SLASH_GREATER_THAN, HTMLTokenTypes.EQUAL,
				HTMLTokenTypes.START_TAG, HTMLTokenTypes.END_TAG };
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#getCompletionProposalIdleActivationTokens()
	 */
	public int[] getCompletionProposalIdleActivationTokens()
	{
		return new int[] { HTMLTokenTypes.START_TAG, HTMLTokenTypes.END_TAG, HTMLTokenTypes.NAME };
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		if (validator == null)
			validator = new HTMLContextInformationValidator(this);
		return validator;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return null;
	}

	/**
	 * Gets the offset for inserting a new item into the document
	 * 
	 * @param curLexeme
	 * @param offset
	 * @return The index at which to insert
	 */
	public static int getOffsetForInsertion(Lexeme curLexeme, int offset)
	{
		if (curLexeme == null)
		{
			return offset;
		}

		int beginOffset = offset;

		if (curLexeme.typeIndex == HTMLTokenTypes.TEXT)
		{
			return beginOffset;
		}

		// If we're in an identifier, it's likely we'll want to replace that
		// current value
		if (curLexeme.getCategoryIndex() == TokenCategories.KEYWORD
				|| curLexeme.getCategoryIndex() == TokenCategories.LITERAL || HTMLUtils.isStartTag(curLexeme)
				|| HTMLUtils.isEndTag(curLexeme))
		{
			beginOffset = curLexeme.getStartingOffset();
		}

		return beginOffset;
	}

	/**
	 * Returns a set of valid completion proposals.
	 * 
	 * @param tagPrefix
	 *            The tag already typed (to filter on)
	 * @param attributePrefix
	 *            The attribute already typed (to filter on)
	 * @param activationChar
	 *            That char used to trigger the content
	 * @param addedAttributes
	 *            The lsit of attributes already added. We'll remove those
	 * @param hasAttachedValue
	 *            Does it already have a "value" attached at the end
	 * @param offset
	 * @return Returns an array of completion proposals.
	 */
	public ICompletionProposal[] getAttributeCompletionProposals(String tagPrefix, String attributePrefix,
			char activationChar, ArrayList<KeyValuePair> addedAttributes, boolean hasAttachedValue, int offset)
	{

		if (tagPrefix == null)
		{
			return null;
		}

		Hashtable<String, HTMLCompletionProposal> completionProposals = new Hashtable<String, HTMLCompletionProposal>();

		String tagPrefixLower = tagPrefix.toLowerCase();
		String attributePrefixLower = attributePrefix.toLowerCase();

		ElementMetadata em = environment.getElement(tagPrefixLower);

		if (em == null)
		{
			return null;
		}

		int beginOffset = offset;
		Lexeme curLexeme = getHTMLOffsetMapper().getCurrentLexeme();

		if (curLexeme.typeIndex == HTMLTokenTypes.NAME)
		{
			beginOffset = curLexeme.getStartingOffset();
		}

		Iterator iter = em.getFields().values().iterator();
		while (iter.hasNext())
		{
			FieldMetadata fm = (FieldMetadata) iter.next();
			// if (!addedAttributes.contains(fm.getName())) {
			HTMLCompletionProposal cp = createFieldProposal(attributePrefixLower, beginOffset, em, fm, hasAttachedValue);
			completionProposals.put(fm.getName(), cp);

			// cp.scheduleContentAssistOnInsert(true);
			// }
		}

		Iterator iterEvents = em.getEvents().values().iterator();
		while (iterEvents.hasNext())
		{
			EventMetadata evm = (EventMetadata) iterEvents.next();
			// if (!addedAttributes.contains(evm.getName())) {
			HTMLCompletionProposal cp = createEventProposal(attributePrefixLower, beginOffset, em, evm,
					hasAttachedValue);
			completionProposals.put(evm.getName(), cp);

			// cp.scheduleContentAssistOnInsert(true);
			// }
		}

		ICompletionProposal[] result = completionProposals.values().toArray(
				new ICompletionProposal[completionProposals.size()]);
		Arrays.sort(result, getProposalComparator());

		return result;

	}

	/**
	 * @param attributePrefix
	 * @param beginOffset
	 * @param em
	 * @param fm
	 * @param hasAttachedValue
	 *            Does it already have a "value" attached at the end
	 * @return A new completion proposal for fields
	 */
	public HTMLCompletionProposal createFieldProposal(String attributePrefix, int beginOffset, ElementMetadata em,
			FieldMetadata fm, boolean hasAttachedValue)
	{
		String docText = environment.getFieldDocumentation(fm);

		String replaceString = fm.getName();
		String quoteString = HTMLUtils.quoteAttributeValue(getPreferenceStore(), ""); //$NON-NLS-1$
		boolean insertEquals = HTMLUtils.insertEquals(getPreferenceStore());

		// [IM] Don't add on '=' for the moment
		if (!hasAttachedValue && insertEquals)
		{
			replaceString = replaceString + "=" + quoteString; //$NON-NLS-1$
		}

		String displayString = fm.getName();
		int cursorPosition = replaceString.length();

		// Place that cursor inside the attribute value location
		if (!hasAttachedValue && insertEquals && quoteString.length() == 2)
		{
			cursorPosition--;
		}

		int replaceLength = attributePrefix.length();

		Image[] userAgents = null;
		if (fm.getUserAgents().length == 0)
		{
			userAgents = getUserAgentImages(getUserAgents(), em.getUserAgentPlatformNames());
		}
		else
		{
			userAgents = getUserAgentImages(getUserAgents(), fm.getUserAgentPlatformNames());
		}

		HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
				cursorPosition, fIconField, displayString, null, docText,
				HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, userAgents);
		return cp;
	}

	/**
	 * @param attributePrefix
	 * @param beginOffset
	 * @param em
	 * @param fm
	 * @param hasAttachedValue
	 *            Does it already have a "value" attached at the end
	 * @return A new completion proposal for events
	 */
	public HTMLCompletionProposal createEventProposal(String attributePrefix, int beginOffset, ElementMetadata em,
			EventMetadata fm, boolean hasAttachedValue)
	{
		String docText = environment.getEventDocumentation(fm);

		String replaceString = fm.getName();
		String quoteString = HTMLUtils.quoteAttributeValue(getPreferenceStore(), ""); //$NON-NLS-1$
		boolean insertEquals = HTMLUtils.insertEquals(getPreferenceStore());

		// [IM] Don't add on '=' for the moment
		if (!hasAttachedValue && insertEquals)
		{
			replaceString = replaceString + "=" + quoteString; //$NON-NLS-1$
		}

		String displayString = fm.getName();
		int cursorPosition = replaceString.length();
		int replaceLength = attributePrefix.length();

		// Place that cursor inside the attribute value location, assuming we are quoting attributes
		if (!hasAttachedValue && insertEquals && quoteString.length() == 2)
		{
			cursorPosition--;
		}

		Image[] userAgents = null;

		if (fm.getUserAgents().length == 0)
		{
			userAgents = getUserAgentImages(getUserAgents(), em.getUserAgentPlatformNames());
		}
		else
		{
			userAgents = getUserAgentImages(getUserAgents(), fm.getUserAgentPlatformNames());
		}

		HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
				cursorPosition, fIconEvent, displayString, null, docText,
				HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, userAgents);
		return cp;
	}

	/**
	 * Returns a set of valid completion proposals.
	 * 
	 * @param prefix
	 *            The text already typed (to filter on)
	 * @param activationChar
	 *            The char used to activate the code assist
	 * @param offset
	 * @param currentLexeme
	 * @param lexemeList
	 * @return Returns an array of completion proposals.
	 */
	public ICompletionProposal[] getTagCompletionProposals(String prefix, char activationChar, int offset,
			Lexeme currentLexeme, LexemeList lexemeList)
	{

		IParseState parseState = context.getParseState();
		HTMLParseState htmlParseState = (HTMLParseState) parseState.getParseState(HTMLMimeType.MimeType);

		Hashtable<String, HTMLCompletionProposal> completionProposals = new Hashtable<String, HTMLCompletionProposal>();

		if (currentLexeme == null)
		{
			return null;
		}

		if (currentLexeme.typeIndex != HTMLTokenTypes.TEXT && !HTMLUtils.isStartTag(currentLexeme)
				&& !HTMLUtils.isEndTag(currentLexeme))
		{
			return null;
		}

		// If we are in an already closed tag, then don't display code assist, unless forced
		int tagClosedType = HTMLUtils.isTagClosed(currentLexeme, lexemeList);
		boolean tagClosed = tagClosedType != HTMLUtils.TAG_OPEN;

		// Commented out to fix bug #1211 [IM]. To fix this correctly, on auto-pop
		// the activation char needs to be computed differently. For right now, we don't show
		// anything if
		// autopop has been set.
		// IM....we actually made a change like that, and now '<' are auto-inserted.
		// if (tagClosed && activationChar == DEFAULT_CHARACTER && unifiedViewer != null)
		// {
		// return null;
		// }

		String textPrefix = HTMLUtils.getOpenTagName(currentLexeme, offset);

		if (tagClosed)
		{
			textPrefix = HTMLUtils.getOpenTagName(currentLexeme, currentLexeme.getEndingOffset());
		}

		String testPrefix = textPrefix.toLowerCase();

		int lastAmpersand = textPrefix.lastIndexOf('&');
		// We have an ampersand, and it's not followed by a semicolon or space
		if (lastAmpersand > -1 && lastAmpersand > textPrefix.lastIndexOf(';')
				&& lastAmpersand > textPrefix.lastIndexOf(' '))
		{
			testPrefix = textPrefix.substring(lastAmpersand);
			completionProposals.putAll(computeHTMLEntityProposals(currentLexeme, textPrefix));
		}
		else
		{
			testPrefix = textPrefix.toLowerCase();
		}

		if (currentLexeme.typeIndex == HTMLTokenTypes.TEXT)
		{
			textPrefix = ""; //$NON-NLS-1$
		}

		// We need to test against lower-case items

		int beginOffset = getOffsetForInsertion(currentLexeme, offset);

		String[] em = environment.getAllElements();

		for (int i = 0; i < em.length; i++)
		{

			String e = em[i];

			String docText = environment.getElementDocumentation(e);
			String displayString = e;
			String replaceString = e;

			int replaceLength = textPrefix.length();
			int cursorPosition = replaceString.length();

			// We are now correctly parsing the initial "<" as the start
			// of a tag, so we'll want to replace that too.
			if (HTMLUtils.isEndTag(currentLexeme))
			{

				// Strange case where ctrl + spacing in an already completed HTML tag.
				// this can most likely be simplified
				if (tagClosed)
				{
					replaceString = HTMLUtils.createOpenTag(replaceString, false);
				}
				else
				{
					replaceString = HTMLUtils.createOpenTag(replaceString, true);
				}

				replaceLength = currentLexeme.length;
				cursorPosition = replaceString.length();

			}
			else
			{

				boolean insertClosingTags = true;

				if (getPreferenceStore() != null)
				{
					insertClosingTags = getPreferenceStore().getBoolean(IPreferenceConstants.AUTO_INSERT_CLOSE_TAGS);
				}

				if (insertClosingTags)
				{
					int cursorOffset = 1;
					boolean emptyTag = htmlParseState.isEmptyTagType(replaceString);

					// [IM] Fix for 5951 to not auto-close doctype
					boolean isDocType = replaceString != null && replaceString.toLowerCase().startsWith("!doctype"); //$NON-NLS-1$

					String startString = null;
					if (emptyTag && !tagClosed && htmlParseState.getDocumentType() >= HTMLDocumentType.XHTML_1_0_STRICT)
					{
						startString = HTMLUtils.createSelfClosedTag(replaceString);
						cursorOffset = 2;
					}
					else
					{
						startString = HTMLUtils.createOpenTag(replaceString, !tagClosed);
					}

					cursorPosition = startString.length();

					if (!tagClosed && !HTMLUtils.isStartTagBalanced(currentLexeme, lexemeList, htmlParseState)
							&& !emptyTag && !isDocType)
					{
						// TODO: There is definitely a bug here with !DOCTYPE, but fixing it will
						// have to wait
						replaceString = startString + HTMLUtils.createCloseTag(replaceString, true);
						cursorOffset = 1;
					}
					else
					{
						replaceString = startString;
					}

					// If the tag is already closed, we back up one char to allow to
					// insert more attributes
					if (!tagClosed)
					{
						cursorPosition = cursorPosition - cursorOffset;
					}
				}
				else
				{
					replaceString = HTMLUtils.createOpenTag(replaceString, true);
					cursorPosition = replaceString.length();
				}

				if (currentLexeme.typeIndex != HTMLTokenTypes.TEXT)
				{
					replaceLength++;
				}
			}

			HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
					cursorPosition, fIconTag, displayString, null, docText,
					HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, getUserAgentImages(
							getUserAgents(), environment.getUserAgentPlatformNames(e)));

			completionProposals.put(displayString, cp);
		}

		boolean addedCloseTag = false;
		Lexeme unclosed = HTMLUtils.getPreviousUnclosedTag(currentLexeme, lexemeList, htmlParseState);

		if (unclosed != null && !HTMLUtils.isStartTagBalanced(unclosed, lexemeList, htmlParseState))
		{
			String displayString = HTMLUtils.stripTagEndings(unclosed.getText());
			String replaceString = displayString;

			boolean emptyTag = htmlParseState.isEmptyTagType(displayString);
			if (!emptyTag)
			{
				addedCloseTag = true;

				// Strange case where ctrl + spacing in an already completed HTML tag.
				// this can most likely be simplified
				if (tagClosed)
				{
					replaceString = HTMLUtils.createCloseTag(replaceString, false);
				}
				else
				{
					replaceString = HTMLUtils.createCloseTag(replaceString, true);
				}

				int replaceLength = getReplaceLengthByLexeme(currentLexeme, offset);

				int cursorPosition = replaceString.length();
				displayString = "/" + displayString; //$NON-NLS-1$

				ElementMetadata e = environment.getElement(unclosed);
				String docText = null;

				if (e != null)
				{
					docText = environment.getElementDocumentation(e.getName());
				}

				HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString,
						currentLexeme.getStartingOffset(), replaceLength, cursorPosition, fIconTag, displayString,
						null, docText, HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer,
						getAllUserAgentImages(getUserAgents()));

				completionProposals.put(displayString, cp);
			}
		}

		ICompletionProposal[] result = completionProposals.values().toArray(
				new ICompletionProposal[completionProposals.size()]);
		Arrays.sort(result, getProposalComparator());

		if (HTMLUtils.isEndTag(currentLexeme) && addedCloseTag)
		{
			setSelection("/" + testPrefix, result); //$NON-NLS-1$
		}
		else
		{
			setSelection(testPrefix, result);
		}

		return result;
	}

	private Map<String, HTMLCompletionProposal> computeHTMLEntityProposals(Lexeme currentLexeme, String textPrefix)
	{
		Map<String, HTMLCompletionProposal> completionProposals = new HashMap<String, HTMLCompletionProposal>();
		Map<String, String> entityMap = getEntities();
		String trimmedPrefix = StringUtils.trimStart(textPrefix);
		trimmedPrefix = trimmedPrefix.substring(trimmedPrefix.lastIndexOf("&"));
		for (Map.Entry<String, String> entry : entityMap.entrySet())
		{
			String displayString = '&' + entry.getKey() + ';';
			String replaceString = displayString;
			int beginOffset = currentLexeme.getStartingOffset() + textPrefix.lastIndexOf('&');
			int replaceLength = textPrefix.length() - textPrefix.lastIndexOf('&');
			String doc = entry.getValue();
			if (!displayString.startsWith(trimmedPrefix))
				continue; // Filter the proposals by prefix matching?
			HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
					replaceString.length(), fIconTag, displayString, null, doc,
					HTMLCompletionProposalComparator.OBJECT_TYPE_PROPERTY, unifiedViewer, getUserAgentImages(
							getUserAgents(), new String[0]));

			completionProposals.put(displayString, cp);
		}
		return completionProposals;
	}

	private Map<String, String> getEntities()
	{
		if (entities == null)
		{
			entities = new HashMap<String, String>();
			try
			{
				InputStream stream = FileLocator.openStream(HTMLPlugin.getDefault().getBundle(), new Path(
						"src/com/aptana/ide/editor/html/resources/entities.properties"), false); //$NON-NLS-1$
				Properties loadedEntities = new Properties();
				loadedEntities.load(stream);
				for (Object entityObject : loadedEntities.keySet())
				{
					String entity = (String) entityObject;
					entities.put(entity, loadedEntities.getProperty(entity));
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage(), e);
			}
		}
		return entities;
	}

	/**
	 * Returns a reference to the current offsetMapper
	 * 
	 * @return The reference to the mapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		IFileLanguageService ls = this.context.getLanguageService(HTMLMimeType.MimeType);

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
	 * Returns a reference to the current offsetMapper
	 * 
	 * @return The reference to the mapper
	 */
	public HTMLOffsetMapper getHTMLOffsetMapper()
	{
		return (HTMLOffsetMapper) getOffsetMapper();
	}

	/**
	 * @see UnifiedContentAssistProcessor#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(HTMLPlugin.getDefault()))
		{
			return HTMLPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor#computeInnerContextInformation(java.lang.String,
	 *      int, int, com.aptana.ide.lexer.LexemeList)
	 */
	public IContextInformation[] computeInnerContextInformation(String documentSource, int offset, int position,
			LexemeList lexemeList)
	{

		IContextInformation[] ici = null;

		// char activationChar = getActivationChar(documentSource, offset,
		// getCompletionProposalAllActivationCharacters());
		HTMLContextLocation currentLocation = getLocation(offset, lexemeList);
		String tagPrefix = currentLocation.getTagName();

		if (tagPrefix == null)
		{
			return null;
		}

		Hashtable fields = environment.getGlobalFields();

		ArrayList<KeyValuePair> attributes = currentLocation.getAttributes();

		if (attributes.size() == 0)
		{
			return null;
		}

		KeyValuePair attribute = attributes.get(attributes.size() - 1);
		String propertyNameLower = ((String) attribute.getKey()).toLowerCase();

		FieldMetadata fm = (FieldMetadata) fields.get(propertyNameLower);

		if (fm == null || fm.getValues().size() > 0)
		{
			return null;
		}

		// Right now we just print the description. This should be a list of
		// arguments, like VS 2003
		ContextInformation ci = new ContextInformation("contextDisplayString", fm.getDescription()); //$NON-NLS-1$
		if (ci != null)
		{
			ici = new IContextInformation[] { ci };
		}
		return ici;

	}

	/**
	 * @see UnifiedContentAssistProcessor#getProposalComparator()
	 */
	public Comparator<ICompletionProposal> getProposalComparator()
	{
		if (contentAssistComparator == null)
			contentAssistComparator = new HTMLCompletionProposalComparator();
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
			int replaceLength, int sortingType)
	{
		ArrayList<HTMLCompletionProposal> completionProposals = new ArrayList<HTMLCompletionProposal>();
		Map<String, Image> ht = com.aptana.ide.core.ui.ImageUtils.fileIconsHash;

		IEditorInput pathEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();

		String urlPrefix = null;
		if (pathEditor instanceof FileEditorInput)
		{
			urlPrefix = getProjectContextRoot(pathEditor);
		}

		String editorPath = CoreUIUtils.getPathFromEditorInput(pathEditor);

		if (urlPrefix != null && !"".equals(urlPrefix) && valuePrefix != null && valuePrefix.indexOf('/') == 1) //$NON-NLS-1$
		{
			editorPath = urlPrefix;
		}

		String currentPath = editorPath;
		if (valuePrefix != null)
		{
			String s = StringUtils.trimStringQuotes(valuePrefix);
			if (!"".equals(s)) //$NON-NLS-1$
			{
				File current = new File(currentPath);

				if (current.isDirectory())
				{
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

		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];

			if (f.getName().startsWith(".")) //$NON-NLS-1$
			{
				continue;
			}

			String fileType = ""; //$NON-NLS-1$

			try
			{
				fileType = FileSystemView.getFileSystemView().getSystemTypeDescription(f);
			}
			catch (Exception ex)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLContentAssistProcessor_TypeDescriptionError, ex);
			}

			// Don't include the current file in the list
			if (f.toString().equals(editorPath))
			{
				continue;
			}

			Image image = null;

			if (fileType != null)
			{
				image = ht.get(fileType);
			}

			if (image == null)
			{
				image = fIconFile;

				if (f.isDirectory())
				{
					image = fIconFolder;
				}
			}

			String replaceString = FileUtils.makeFilePathRelative(new File(editorPath), f);
			replaceString = replaceString.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			String displayString = CoreUIUtils.getPathFromURI(replaceString);

			int cursorPosition = replaceString.length();

			HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
					cursorPosition, image, displayString, null, f.toString(), sortingType, unifiedViewer, new Image[0]);

			if (cp != null)
			{
				completionProposals.add(cp);
			}
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}

	
	/**
	 * getCSSClassCompletionProposals
	 * 
	 * @param valuePrefix
	 * @param beginOffset
	 * @param replaceLength
	 * @param sortingType
	 */
	private ICompletionProposal[] getCSSClassCompletionProposals(String valuePrefix, int beginOffset,
			int replaceLength, int sortingType)
	{
		String trimmedValue = StringUtils.trimStringQuotes(valuePrefix);
		ArrayList<HTMLCompletionProposal> completionProposals = new ArrayList<HTMLCompletionProposal>();
		String path = "";
		IEditorInput pathEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.getActiveEditor().getEditorInput();
		if (pathEditor instanceof IFileEditorInput)
		{
			IFileEditorInput fileEI = (IFileEditorInput) pathEditor;
			path = fileEI.getFile().getProject().getFullPath().toPortableString();
		}		
		Collection<String> classes = CSSLanguageEnvironment.getInstance().getClasses(path, trimmedValue);
		for (String cssClass : classes)
		{
			int cursorPosition = cssClass.length();
			HTMLCompletionProposal cp = new HTMLCompletionProposal(cssClass, beginOffset, replaceLength,
					cursorPosition, fIconTagGuess, cssClass, null, cssClass, sortingType, unifiedViewer, getAllUserAgentImages(getUserAgents()));
			completionProposals.add(cp);
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}
	
	/**
	 * getCSSIdCompletionProposals
	 * 
	 * @param valuePrefix
	 * @param beginOffset
	 * @param replaceLength
	 * @param sortingType
	 */
	private ICompletionProposal[] getCSSIdCompletionProposals(String valuePrefix, int beginOffset,
			int replaceLength, int sortingType)
	{
		String trimmedValue = StringUtils.trimStringQuotes(valuePrefix);
		ArrayList<HTMLCompletionProposal> completionProposals = new ArrayList<HTMLCompletionProposal>();
		String path = "";
		IEditorInput pathEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.getActiveEditor().getEditorInput();
		if (pathEditor instanceof IFileEditorInput)
		{
			IFileEditorInput fileEI = (IFileEditorInput) pathEditor;
			path = fileEI.getFile().getProject().getFullPath().toPortableString();
		}		
		
		Collection<String> ids = CSSLanguageEnvironment.getInstance().getIds(path, trimmedValue);
		for (String cssId : ids)
		{
			int cursorPosition = cssId.length();
			HTMLCompletionProposal cp = new HTMLCompletionProposal(cssId, beginOffset, replaceLength,
					cursorPosition, fIconTagGuess, cssId, null, cssId, sortingType, unifiedViewer, getAllUserAgentImages(getUserAgents()));
			completionProposals.add(cp);
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}
	
	/**
	 * Get the name of the project context root
	 * 
	 * @param input
	 * @return
	 * @throws CoreException
	 */
	private String getProjectContextRoot(IEditorInput input)
	{
		String urlPrefix = null;
		FileEditorInput fei = (FileEditorInput) input;
		IFile file = fei.getFile();
		IProject project = file.getProject();
		urlPrefix = HTMLContextRootUtils.resolveURL(project, "/"); //$NON-NLS-1$
		urlPrefix = project.getLocation().append(urlPrefix).toOSString();

		return urlPrefix;
	}

	/**
	 * getXPathCompletionProposals
	 * 
	 * @param attributeName
	 * @param offset
	 * @param replaceLength
	 * @param sortingType
	 * @return ICompletionProposal[]
	 */
	public ICompletionProposal[] getXPathCompletionProposals(String attributeName, int offset, int replaceLength,
			int sortingType)
	{
		ArrayList<HTMLCompletionProposal> completionProposals = new ArrayList<HTMLCompletionProposal>();

		IParseNode root = context.getParseState().getRoot().getParseResults();

		String editors = null;
		if (getPreferenceStore() != null)
		{
			editors = getPreferenceStore().getString(
					com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_EXPRESSIONS);
		}
		CodeAssistExpression[] expressions = CodeAssistExpression.deserializeErrorDescriptors(editors);
		for (int i = 0; i < expressions.length; i++)
		{
			CodeAssistExpression expression = expressions[i];
			if (attributeName.matches(expression.getExpression()))
			{
				try
				{
					XPath xpath = new ParseNodeXPath(expression.getXPath());
					Object result = xpath.evaluate(root);
					if (result instanceof List)
					{
						List xpathResult = (List) result;
						for (Iterator iter = xpathResult.iterator(); iter.hasNext();)
						{
							Object element = (Object) iter.next();
							if (element instanceof ParseNodeAttribute)
							{
								ParseNodeAttribute pna = (ParseNodeAttribute) element;
								String replaceString = pna.getValue();
								int cursorPosition = replaceString.length();

								HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, offset,
										replaceLength, cursorPosition, fIconFieldGuess, replaceString, null,
										"<b>" + replaceString + "</b><br>" //$NON-NLS-1$ //$NON-NLS-2$
												+ AUTO_ADDED, sortingType, unifiedViewer, null);

								if (cp != null)
								{
									completionProposals.add(cp);
								}
							}
						}
					}
				}
				catch (JaxenException e)
				{
					IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
				}
			}
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}

	/**
	 * getFieldMetadataCompletionProposals
	 * 
	 * @param em
	 * @param fm
	 * @param beginOffset
	 * @param replaceLength
	 * @param sortingType
	 * @return ICompletionProposal[]
	 */
	private ICompletionProposal[] getFieldMetadataCompletionProposals(ElementMetadata em, FieldMetadata fm,
			int beginOffset, int replaceLength, int sortingType)
	{

		ArrayList<HTMLCompletionProposal> completionProposals = new ArrayList<HTMLCompletionProposal>();

		for (int i = 0; i < fm.getValues().size(); i++)
		{
			ValueMetadata value = (ValueMetadata) fm.getValues().get(i);
			// * values mean "anything". Not sure how to represent that in
			// content
			// assist
			if (value.getName() != "*") //$NON-NLS-1$
			{
				String docText = ""; //$NON-NLS-1$
				docText = MetadataEnvironment.getValueDocumentation(value);

				// TODO Proper-case string back to what they originally had
				String replaceString = value.getName();
				String displayString = value.getName();

				// Fixed #592. We now insert "" in place of "*"
				if (replaceString.equals("*")) //$NON-NLS-1$
				{
					replaceString = ""; //$NON-NLS-1$
				}

				int cursorPosition = replaceString.length();

				Image[] userAgents = null;
				if (value.getUserAgents().length == 0 && fm.getUserAgents().length == 0)
				{
					userAgents = getUserAgentImages(getUserAgents(), em.getUserAgentPlatformNames());
				}
				else if (value.getUserAgents().length == 0)
				{
					userAgents = getUserAgentImages(getUserAgents(), fm.getUserAgentPlatformNames());
				}
				else
				{
					userAgents = getUserAgentImages(getUserAgents(), value.getUserAgentPlatformNames());
				}

				HTMLCompletionProposal cp = new HTMLCompletionProposal(replaceString, beginOffset, replaceLength,
						cursorPosition, fIconField, displayString, null, docText, sortingType, unifiedViewer,
						userAgents);

				if (cp != null)
				{
					completionProposals.add(cp);
				}
			}
		}
		return completionProposals.toArray(new ICompletionProposal[0]);

	}

	/**
	 * Gets replace length by lexeme.
	 * 
	 * @param lexeme
	 *            - lexeme.
	 * @param offset
	 *            - caret offset
	 * @return replace length.
	 */
	private int getReplaceLengthByLexeme(Lexeme lexeme, int offset)
	{
		// Use the minimum of the trimmed length of the lexeme and the
		// length of the part of lexeme ahead of the caret offset
		return Math.min(lexeme.getText().trim().length(), offset - lexeme.offset);
	}
}
