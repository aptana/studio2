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
package com.aptana.ide.editors.unified.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.IUnifiedViewer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IOffsetMapper;

/**
 * Base class for all code assist processors. Adds on required
 * methods for usage later
 * @author Ingo Muschenetz
 *
 */
public abstract class UnifiedContentAssistProcessor implements
		IUnifiedContentAssistProcessor {

	/**
	 * METADATA_ID
	 */
	public static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$

	/**
	 * METADATA_ID
	 */
	public static final String USERAGENT_ELEMENT = "user-agent"; //$NON-NLS-1$

	/**
	 * ATTR_USER_AGENT_NAME
	 */
	public static final String ATTR_USER_AGENT_NAME = "name"; //$NON-NLS-1$

	/**
	 * ATTR_USER_AGENT_ID
	 */
	public static final String ATTR_USER_AGENT_ID = "id"; //$NON-NLS-1$

	/**
	 * ATTR_ICON
	 */
	public static final String ATTR_ICON = "icon"; //$NON-NLS-1$

	/**
	 * ATTR_ICON
	 */
	public static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$
	
	/**
	 * Has code assist been "forced" via a hotkey?
	 */
	//protected boolean hotkeyActivated = false;

	/**
	 * Has code assist been "forced" during idle?
	 */
	//protected boolean idleActivated = false;
	
	/**
	 * The unified source viewer. contains hooks for indicating we are manually invoking
	 * code assist
	 */
	protected IUnifiedViewer unifiedViewer = null;

	/**
	 * The default code assist trigger character, if we can't find a 
	 * better match
	 */
	public static char DEFAULT_CHARACTER = '\0';
	
	/**
	 * An array of ICompletionProposalContributors
	 */
	private List _contributors = new ArrayList();

	/**
	 * The list of agent images
	 */
	private static HashMap agentImages = new HashMap();
	
	/**
	 * The lsit of agent names
	 */
	private static List agentNames = new ArrayList();
	
	static {
		try
		{
			loadUserAgents();
		}
		catch(Exception ex)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedContentAssistProcessor_ERR_UnableToLoadUserAgents, ex);
		}
	}
		
	/**
	 * Returns the string array of user agents
	 * @return String[]
	 */
	public static String[] getUserAgents()
	{
		if(PluginUtils.isPluginLoaded(UnifiedEditorsPlugin.getDefault()))
		{
			IPreferenceStore prefs = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			String agents = prefs.getString(IPreferenceConstants.USER_AGENT_PREFERENCE);
			return agents.split(","); //$NON-NLS-1$
		}
		else
		{
			return new String[] {Messages.UnifiedContentAssistProcessor_IE, Messages.UnifiedContentAssistProcessor_Mozilla};
		}
	}
	
	/**
	 * Adds a user agent to the list of user agents
	 * @param id
	 * @param normal
	 * @param disabled
	 */
	public static void addUserAgent(String id, String name, Image normal, Image disabled)
	{
		agentImages.put(id, normal);
		agentImages.put(id + "Grey", disabled); //$NON-NLS-1$
		agentNames.add(id);
	}

	/**
	 * Loads user agents from the extension points
	 */
	private static void loadUserAgents()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(UnifiedEditorsPlugin.ID, USERAGENT_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						if (element.getName().equals(USERAGENT_ELEMENT))
						{
							String agentID = element.getAttribute(ATTR_USER_AGENT_ID);
							String agentName = element.getAttribute(ATTR_USER_AGENT_NAME);
							String agentIconPath = element.getAttribute(ATTR_ICON);
							String agentIconDisabledPath = element.getAttribute(ATTR_ICON_DISABLED);
							if (agentID != null)
							{
								IExtension ext = element.getDeclaringExtension();
								String pluginId = ext.getNamespaceIdentifier();
								Bundle bundle = Platform.getBundle(pluginId);
								if(agentIconPath == null || agentIconDisabledPath == null)
								{
									continue;
								}
								
								Image agentIcon = SWTUtils.getImage(bundle, agentIconPath);
								Image agentIconDisabled = SWTUtils.getImage(bundle, agentIconDisabledPath);
								if(agentIcon != null && agentIconDisabled != null)
								{
									addUserAgent(agentID, agentName, agentIcon, agentIconDisabled);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the string array of default user agents
	 * @return String[]
	 */
	public static String[] getDefaultUserAgents()
	{
		return (String[])agentNames.toArray(new String[0]);
	}

	/**
	 * Returns the image hashtable of loaded user agent images
	 * @return HashMap
	 */
	public static HashMap getUserAgentImages()
	{
		return agentImages;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#getCompletionProposalIdleActivationTokens()
	 */
	public abstract int[] getCompletionProposalIdleActivationTokens();

	/**
	 * The characters that triggers completion proposals (dot for completion, and
	 * space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] {};
	}
	
	/**
	 * The characters that triggers completion proposals (dot for completion, and
	 * space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public int[] getCompletionProposalSeparatorLexemes() {
		return new int[] {};
	}
	
	/**
	 * Characters that trigger tooltip popup help
	 * 
	 * @return Returns the trigger characters for auto activation.
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		// Make context popup automatically after the following characters
		return new char[] {};
	}
	
	/**
	 * The characters that triggers completion proposals (dot for completion, and
	 * space for "new XX" in our case). these ones are "private"
	 * in that they don't actually trigger a popup
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalPrivateActivationCharacters() {
		return new char[] {};
	}

	/**
	 * The characters that triggers completion proposals (a combination of all activation chars)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAllActivationCharacters()
	{
		char[] allActivationChars = combine(getCompletionProposalAutoActivationCharacters(), getContextInformationAutoActivationCharacters());
		Arrays.sort(allActivationChars);
		return allActivationChars;
	}
	
	/**
	 * Is this a valid location for auto-assisting (based on lexeme, usually)
	 * @param viewer 
	 * @param offset 
	 * @return boolean
	 */
	public boolean isValidIdleActivationLocation(ITextViewer viewer, int offset) {
		Lexeme currentLexeme = this.getOffsetMapper().getCurrentLexeme();
		return isValidIdleActivationToken(currentLexeme, getCompletionProposalIdleActivationTokens());
	}

	/**
	 * Adds a contributor to the list of contributors
	 * @param contributor
	 */
	public void addCompletionProposalContributor(ICompletionProposalContributor contributor)
	{
		_contributors.add(contributor);
	}

	/**
	 * Removes a contributor from the list of contributors
	 * @param contributor
	 */
	public void removeCompletionProposalContributor(ICompletionProposalContributor contributor)
	{
		_contributors.remove(contributor);
	}

	/**
	 * Removes a contributor from the list of contributors
	 * 
	 * @return ICompletionProposalContributor[]
	 */
	public ICompletionProposalContributor[] getCompletionProposalContributors()
	{
		return (ICompletionProposalContributor[])_contributors.toArray(new ICompletionProposalContributor[0]);
	}

	
	/**
	 * @param viewer 
	 * @param offset 
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,	int offset)
	{	
		return computeCompletionProposals(viewer, offset, DEFAULT_CHARACTER, false);
	}
	
	/**
	 * @param viewer 
	 * @param offset 
	 * @param activationChar
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,	int offset, char activationChar)
	{	
		return computeCompletionProposals(viewer, offset, activationChar, false);
	}
	
	/**
	 * @param viewer 
	 * @param offset 
	 * @param activationChar
	 * @param autoActivated 
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,	int offset, char activationChar, boolean autoActivated)
	{	
		if(viewer instanceof IUnifiedViewer)
		{
			unifiedViewer = (IUnifiedViewer)viewer;
		}
				
		Lexeme currentLexeme = this.getOffsetMapper().getCurrentLexeme();
		int currentIndex = computeCurrentLexemeIndex(offset, this.getOffsetMapper().getLexemeList());

		// TODO: This is temporary, as code assist should be correctly determine where the edit point
		// is based upon the caret position, not the position of the CA box. Using the abs prevents this
		// from triggering on backspace. Unsure if that matters
		//int lastOffset = getOffsetMapper().getLastEditOffset();
		//int newOffset = offset;
		//if(offset < lastOffset && (Math.abs(offset - lastOffset) > 1))
		//	newOffset = lastOffset;
		
		ICompletionProposal[] results = null;
		
		int sourceLength = 50;
		if(offset < 50)
		{
			sourceLength = offset;
		}
		
		String documentSnippet = StringUtils.EMPTY;
		try {
			documentSnippet = viewer.getDocument().get(offset - sourceLength, sourceLength);
		} catch (BadLocationException e) {
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), CoreStrings.ERROR, e);
		}
		
		char previousChar = getActivationChar(documentSnippet, documentSnippet.length(), getCompletionProposalAllActivationCharacters());

		ArrayList proposals = new ArrayList();
		results = computeInnerCompletionProposals(viewer, offset,
				currentIndex, this.getOffsetMapper().getLexemeList(), activationChar, previousChar);

		if(results != null)
		{
			proposals.addAll(Arrays.asList(results));
		}
		
		for (Iterator iter = _contributors.iterator(); iter.hasNext();) {
			ICompletionProposalContributor element = (ICompletionProposalContributor) iter.next();
			ICompletionProposal[] contribs = element.computeCompletionProposals(viewer, offset,
						currentIndex, this.getOffsetMapper().getLexemeList(), activationChar, previousChar, autoActivated);
			
			if(contribs != null)
			{
				proposals.addAll(Arrays.asList(contribs));			
			}
		}

		if(unifiedViewer != null)
		{
			resetViewerState(unifiedViewer);
		}

		ICompletionProposal[] newResults = (ICompletionProposal[])proposals.toArray(new ICompletionProposal[0]);
		Arrays.sort(newResults, getProposalComparator());
		return newResults;
	}
	
	/**
	 * The proposal comparator
	 * 
	 * @return Comparator
	 */
	public abstract Comparator getProposalComparator();

	/**
	 * Resets the viewer state
	 * @param unifiedViewer 
	 */
	public static void resetViewerState(IUnifiedViewer unifiedViewer) {
		unifiedViewer.setHotkeyActivated(false);
	}
	
	/**
	 * Gets the index of the current lexeme. Modified based on the current separator (or activation characters)
	 * @param offset 
	 * @param lexemeList
	 * @return Returns the index of the current lexeme. Modified based on the current separator (or activation characters)
	 */
	public int computeCurrentLexemeIndex(int offset, LexemeList lexemeList)
	{
		return computeCurrentLexemeIndex(offset, lexemeList, getCompletionProposalSeparatorLexemes());
	}
	
	/**
	 * Gets the index of the current lexeme. Modified based on the current separator (or activation characters)
	 * @param offset 
	 * @param lexemeList
	 * @param separatorTokens 
	 * @return Returns the index of the current lexeme. Modified based on the current separator (or activation characters)
	 */
	public static int computeCurrentLexemeIndex(int offset, LexemeList lexemeList, int[] separatorTokens)
	{
		int lexemeIndex = lexemeList.getLexemeFloorIndex(offset);
		
		if(lexemeIndex <= 0)
		{
			return lexemeIndex;
		}
		
		Arrays.sort(separatorTokens);
		Lexeme l = lexemeList.get(lexemeIndex);
		int newIndex = lexemeIndex;
		for(int i = 0; i < separatorTokens.length; i++)
		{
			int test = separatorTokens[i];
			if(l.typeIndex == test && l.offset == offset)
			{
				newIndex = newIndex - 1;
				break;
			}
		}
			
		return newIndex;
	}
	
	/**
	 * Determines what 'tooltip' style highlighting to show. This will be
	 * argument insight for methods. This is formatted in
	 * HTMLContextInformationValidator (of all places) by implementing the
	 * optional IContextInformationPresenter interface.
	 * 
	 * @param viewer
	 * @param offset
	 * @return Returns an array of relevant context info.
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
			
		IContextInformation[] results = null;
		int currentIndex = computeCurrentLexemeIndex(offset, this.getOffsetMapper().getLexemeList());

		results = computeInnerContextInformation(viewer.getDocument().get(), offset,
				currentIndex, this.getOffsetMapper().getLexemeList());
		
		return results;
	}
	
	/**
	 * Returns the base instance of IOffsetMapper
	 * @return IOffsetMapper
	 */
	public abstract IOffsetMapper getOffsetMapper();

	/**
	 * Generates the completion proposals for code assist
	 * @param viewer 
	 * @param offset The integer offset of the current insertion point
	 * @param position 
	 * @param lexemeList 
	 * @param activationChar 
	 * @param previousChar 
	 * @return An array of completion proposals, or null if not valid
	 */
	public abstract ICompletionProposal[] computeInnerCompletionProposals(ITextViewer viewer, int offset, int position, LexemeList lexemeList, char activationChar, char previousChar);

	/**
	 * Generates the context information for code assist
	 * @param documentSource The source text of the document
	 * @param offset The integer offset of the current insertion point
	 * @param position 
	 * @param lexemeList 
	 * @return An array of completion proposals, or null if not valid
	 */
	public abstract IContextInformation[] computeInnerContextInformation(String documentSource, int offset, int position, LexemeList lexemeList);

	/**
	 * Do we pop up code assist while idle? The lexeme
	 * must be of one of the token types in IdleActivationTokens;
	 * @param lexeme The lexeme to check
	 * @param tokens 
	 * @return True if one of the valid idle tokens, false otherwise;
	 */
	public static boolean isValidIdleActivationToken(Lexeme lexeme, int[] tokens)
	{
		if(lexeme == null)
		{
			return false;
		}
		
		Arrays.sort(tokens);
		
		return (Arrays.binarySearch(tokens, lexeme.typeIndex) >= 0);
	}
		
	/**
	 * Selects the first item that is closest to the prefix. Assumes that
	 * results is a sorted array
	 * @param prefix
	 * @param results
	 * @return Returns the index of the selected item, or -1 if nothing is selected.
	 */
	public static int setSelection(String prefix, ICompletionProposal[] results)
	{
		int selectedIndex = -1;		

		if(prefix == null || prefix.equals(StringUtils.EMPTY) || results == null)
		{
			return selectedIndex;
		}
		
        IUnifiedCompletionProposal sensitiveProposal = null;
        IUnifiedCompletionProposal insensitiveProposal = null;
        IUnifiedCompletionProposal suggestedProposal = null;
		for(int i = 0; i < results.length; i++)
		{
            IUnifiedCompletionProposal cp = (IUnifiedCompletionProposal)results[i];
            String display = cp.getDisplayString();

            int result = display.compareToIgnoreCase(prefix);

			if(result >= 0)
			{
	            if(display.toLowerCase().startsWith(prefix.toLowerCase()))
	            {
	            	if(insensitiveProposal == null)
	            	{
	            		insensitiveProposal = cp;
						selectedIndex = i;
	            	}
	            	
	                if(display.startsWith(prefix))
	                {
	                    // we've found an exact case match, so we break;
	            		sensitiveProposal = cp;
						selectedIndex = i;
	                    break;
	                }
	            }
	            else
	            {
	                suggestedProposal = cp;
	                break;
	            }	            
			}
			            
            // if the item we type is greater than the last proposal,
            // we just suggest the last item in the list
            if(i == results.length - 1)
            {
                suggestedProposal = cp;
            }
		}
		
		if(sensitiveProposal != null)
		{
			sensitiveProposal.setDefaultSelection(true);
		}
		else if(insensitiveProposal != null)
		{
			insensitiveProposal.setDefaultSelection(true);
		}
        else if(suggestedProposal != null)
        {
            suggestedProposal.setSuggestedSelection(true);
        }
        else if(results.length > 0)
		{
			((IUnifiedCompletionProposal)results[0]).setSuggestedSelection(true);
		}
		
		return selectedIndex;		
	}
	
	/**
	 * Sets as selected the first item that starts with the given prefix. Generally used
	 * when the list of results is _not_ sorted.
	 * @param prefix
	 * @param results
	 * @return Returns the index of the selected item, or -1 if nothing is selected.
	 */
	public static int setSelectionUnsorted(String prefix, ICompletionProposal[] results)
	{
        IUnifiedCompletionProposal selectedProposal = null;
		int selectedIndex = -1;

		if(prefix == null || prefix.equals(StringUtils.EMPTY))
		{
			return selectedIndex;
		}
		
		for(int i = 0; i < results.length; i++)
		{
            IUnifiedCompletionProposal cp = (IUnifiedCompletionProposal)results[i];
			if(cp.getDisplayString().startsWith(prefix))
			{
				selectedProposal = cp;
				selectedIndex = i;
				break;
			}
		}
		
		if(selectedProposal != null)
		{
			selectedProposal.setDefaultSelection(true);
		}
		else if(results.length > 0)
		{
			((IUnifiedCompletionProposal)results[0]).setDefaultSelection(true);
			selectedIndex = 0;
		}
		
		return selectedIndex;
	}

	/**
	 * getPreviousLexemeOfType
	 * 
	 * @param offset
	 * @param lexemeTypes
	 * @param lexemeList
	 * @param includeCurrent
	 * @return Lexeme
	 */
	public static Lexeme getPreviousLexemeOfType(int offset, int[] lexemeTypes, LexemeList lexemeList, boolean includeCurrent)
	{
		return UnifiedContentAssistProcessor.getPreviousLexemeOfType(offset, lexemeTypes, new int[0], lexemeList, includeCurrent);
	}

	/**
	 * getPreviousLexemeOfType
	 * 
	 * @param offset
	 * @param lexemeTypes
	 * @param lexemeTypesToBail
	 * @param lexemeList
	 * @param includeCurrent
	 * @return Lexeme
	 */
	public static Lexeme getPreviousLexemeOfType(int offset, int[] lexemeTypes, int[] lexemeTypesToBail, LexemeList lexemeList, boolean includeCurrent)
	{
		Arrays.sort(lexemeTypes);
		Arrays.sort(lexemeTypesToBail);
	
		Lexeme startLexeme = lexemeList.getFloorLexeme(offset);
	
		if(!includeCurrent)
		{
			startLexeme = getPreviousLexeme(offset, lexemeList);
		}
	
		if(startLexeme == null)
		{
			return null;
		}
		
		int index = lexemeList.getLexemeIndex(startLexeme);
		
		for(int i = index; i >= 0; i--)
		{
			Lexeme l = lexemeList.get(i);
			if(Arrays.binarySearch(lexemeTypes, l.typeIndex) >= 0)
			{
				return l;
			}
			
			if(Arrays.binarySearch(lexemeTypesToBail, l.typeIndex) >= 0)
			{
				return null;
			}
		}
		
		return null;
	}

	/**
	 * getPreviousLexeme
	 * 
	 * @param offset
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getPreviousLexeme(int offset, LexemeList lexemeList)
	{
		if(offset == 0)
		{
			return null;
		}
		
		Lexeme floor = lexemeList.getFloorLexeme(offset);
		if(floor.offset == offset)
		{
			int index = lexemeList.getLexemeIndex(floor);
			if(index > 0)
			{
				return lexemeList.get(index - 1);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return floor;
		}
	}

	/**
	 * Gets the current activation character. Combines and sorts the arrays
	 * 
	 * @param source 
	 * @param offset
	 *            The current offset
	 * @param activationCharacters1
	 * @param activationCharacters2 
	 * @return The current activation character
	 */
	public static char getActivationChar(String source, int offset, char[] activationCharacters1, char[] activationCharacters2) {

		char[] newArray = new char[activationCharacters1.length + activationCharacters2.length];
		for (int i = 0; i < activationCharacters1.length; i++) {
			newArray[i] = activationCharacters1[i];
		}
		for (int i = 0; i < activationCharacters2.length; i++) {
			newArray[i + activationCharacters1.length] = activationCharacters2[i];
		}
		
		return getActivationChar(source, offset, newArray);
	}
	
	/**
	 * Gets the current activation character
	 * 
	 * @param source 
	 * @param offset
	 *            The current offset
	 * @param activationCharacters 
	 * @return The current activation character
	 */
	public static char getActivationChar(String source, int offset, char[] activationCharacters) {

		Arrays.sort(activationCharacters);
		if(offset > 0)
		{
			char activationCharacter = getPreviousChar(source, offset);
			// this is a special case as we are using backspace as an activation
			// this can't come from the document
			if (Arrays.binarySearch(activationCharacters, activationCharacter) < 0) {
				activationCharacter = UnifiedContentAssistProcessor.DEFAULT_CHARACTER;
			}
			return activationCharacter;
		}
		else
		{
			return DEFAULT_CHARACTER;
		}
	}

	/**
	 * Gets the previous character
	 * 
	 * @param source
	 * @param offset
	 *            The current offset
	 * @return The previous character
	 */
	public static char getPreviousChar(String source, int offset) {

		if(source == null)
		{
			throw new IndexOutOfBoundsException(Messages.UnifiedContentAssistProcessor_StringNotNull);
		}
		
		if(offset > 0 && offset <= source.length())
		{
			char activationCharacter = source.charAt(offset - 1);
			return activationCharacter;
		}
		else
		{
			return DEFAULT_CHARACTER;
		}
	}
	
	/**
	 * Returns the current preference store
	 * @return The current preference store, or null if not found
	 */
	protected abstract IPreferenceStore getPreferenceStore();
	
	/**
	 * Combines two arrays into a single array
	 * @param array1 The first array
	 * @param array2 The second array
	 * @return char[]
	 */
	public static char[] combine(char[] array1, char[] array2) {
		
		char[] newArray = new char[array1.length
				+ array2.length];
		for (int i = 0; i < array1.length; i++) {
			newArray[i] = array1[i];
		}
		for (int i = 0; i < array2.length; i++) {
			newArray[i + array2.length] = array2[i];
		}
		
		return newArray;
	}
	
	/**
	 * Is there a proposal that exactly matches the prefix?
	 * @param proposal The proposal to test for
	 * @param proposals The list of proposals
	 * @return containsExactProposalMatch
	 */
	public static boolean containsExactProposalMatch(String proposal, ICompletionProposal[] proposals)
	{
		if(proposals == null)
		{
			return false;
		}
		
		for(int i = 0; i < proposals.length; i++)
		{
			ICompletionProposal prop = proposals[i];
			String displayProp = prop.getDisplayString();
			if(displayProp.equals(proposal))
			{
				return true;
			}
			
			if(prop instanceof IUnifiedCompletionProposal && ((IUnifiedCompletionProposal)prop).getReplaceString().equals(proposal))
			{
				return true;
			}
			
			String tempProposal = proposal;
			
			if(tempProposal.startsWith("\"") || tempProposal.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				tempProposal = tempProposal.substring(1); 
			}

			if(tempProposal.endsWith("\"") || tempProposal.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				tempProposal = tempProposal.substring(0, tempProposal.length() - 1); 
			}
			
			if(displayProp.equals(tempProposal))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 *  Get the user agent documentation
	 * @param supportedUserAgents 
	 * @param allUserAgents 
	 * @return Image[]
	 */
	public static Image[] getUserAgentImages(String[] supportedUserAgents, String[] allUserAgents)
	{
		if(supportedUserAgents.length > 0)
		{
			ArrayList images = new ArrayList();
			for(int i = 0; i < supportedUserAgents.length; i++)
			{
				String agentName = supportedUserAgents[i];
				Image image = null;
				for(int j = 0; j < allUserAgents.length; j++)
				{
					String ua = allUserAgents[j];
					if(ua.equals(agentName))
					{
						image = (Image)agentImages.get(agentName);
					}
				}
				if(image == null)
				{
					image = (Image)agentImages.get(agentName + "Grey"); //$NON-NLS-1$
				}
					
				images.add(image);
			}
			return (Image[])images.toArray(new Image[0]);			
		}
		else
		{
			return getGreyUserAgentImages(allUserAgents);
		}
	}
	
	/**
	 * Get a list of all agents, greyed out
	 * @param userAgents
	 * @return Image[]
	 */
	public static Image[] getGreyUserAgentImages(String[] userAgents)
	{
		ArrayList images = new ArrayList();
		for(int i = 0; i < userAgents.length; i++)
		{
			String agentName = userAgents[i];
			Image image = (Image)agentImages.get(agentName + "Grey"); //$NON-NLS-1$
			images.add(image);
		}
		return (Image[])images.toArray(new Image[0]);	
	}
	
	/**
	 * Get a list of all agents, default colors
	 * @param userAgents
	 * @return gets AllUserAgentImages
	 */
	public static Image[] getAllUserAgentImages(String[] userAgents)
	{
		ArrayList images = new ArrayList();
		for(int i = 0; i < userAgents.length; i++)
		{
			String agentName = userAgents[i];
			Image image = (Image)agentImages.get(agentName);
			images.add(image);
		}
		return (Image[])images.toArray(new Image[0]);	
	}
}
