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
package com.aptana.ide.editors.unified;

import java.util.ArrayList;

import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;
import com.aptana.ide.editors.unified.contentassist.UnifiedTemplateCompletionProcessor;
import com.aptana.ide.editors.unified.hover.LexemeTextHover;

/**
 * BaseContributor
 */
public abstract class BaseContributor implements IUnifiedEditorContributor
{
	/*
	 * Fields
	 */

	private String language;

	/**
	 * parent
	 */
	protected IUnifiedEditorContributor parent;

	private String[] contentTypes;
	private EditorFileContext fileService;
	private String[] indentPrefixes = new String[] { "\t", "    " }; //$NON-NLS-1$ //$NON-NLS-2$
	private SourceViewerConfiguration parentConfiguration;
	private ITextDoubleClickStrategy doubleClickStrategy;
	private ITextTripleClickStrategy tripleClickStrategy;

	/**
	 * Creates a new base contributor with a language type. This constructor was added jointly with a default
	 * implementation of the getLocalContentType method thats returns the language parameter for this constructur making
	 * it no longer abstract.
	 * 
	 * @param language
	 *            - mime type
	 */
	protected BaseContributor(String language)
	{
		this.language = language;
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#dispose()
	 */
	public void dispose()
	{
		if (parent != null)
		{
			parent.dispose();
			parent = null;
		}
		fileService = null;
		parentConfiguration = null;
		doubleClickStrategy = null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#isAutoActivateContentAssist()
	 */
	public boolean isAutoActivateContentAssist()
	{
		return false;
	}

	/**
	 * Returns the language specified in the BaseContributor(String language) constructor.
	 * 
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalContentType()
	 */
	public String getLocalContentType()
	{
		return language;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getIndentPrefixes(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType == null || contentType.equals(StringUtils.EMPTY)
				|| contentType.equals(this.getLocalContentType()))
		{
			return getLocalIndentPrefixes();
		}

		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				if (ccs[i].getLocalContentType().equals(contentType) && ccs[i] instanceof BaseContributor)
				{
					((BaseContributor) ccs[i]).getLocalIndentPrefixes();
					break;
				}
			}
		}
		// this is the default, so don't care about contentType
		return indentPrefixes;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getIndentString(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public String getIndentString(ISourceViewer sourceViewer, String contentType)
	{
		String[] indents = getIndentPrefixes(sourceViewer, contentType);
		boolean hasIndents = (indents == null) || (indents.length == 0);
		String indentString = hasIndents ? "\t" : indents[0]; //$NON-NLS-1$
		return indentString;
	}

	/**
	 * getLocalIndentPrefixes
	 * 
	 * @return String[]
	 */
	protected String[] getLocalIndentPrefixes()
	{
		return this.indentPrefixes;
	}

	/**
	 * setLocalIndentPrefixes
	 * 
	 * @param prefixes
	 */
	protected void setLocalIndentPrefixes(String[] prefixes)
	{
		this.indentPrefixes = prefixes;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#setIndentPrefixes(java.lang.String[],
	 *      org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public void setIndentPrefixes(String[] prefixes, ISourceViewer sourceViewer, String contentType)
	{
		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				if (ccs[i].getLocalContentType().equals(contentType) && ccs[i] instanceof BaseContributor)
				{
					((BaseContributor) ccs[i]).setLocalIndentPrefixes(prefixes);
					sourceViewer.setIndentPrefixes(prefixes, contentType);
					break;
				}
			}
		}
	}

	/**
	 * Sets index prefixes for all lanugages, overriding any current settings
	 * 
	 * @param prefixes
	 * @param sourceViewer
	 */
	public void setAllIndentPrefixes(String[] prefixes, ISourceViewer sourceViewer)
	{
		this.indentPrefixes = prefixes;
		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				if (ccs[i] instanceof BaseContributor)
				{
					((BaseContributor) ccs[i]).setLocalIndentPrefixes(prefixes);
					sourceViewer.setIndentPrefixes(prefixes, ccs[i].getLocalContentType());
				}
			}
		}
		sourceViewer.setIndentPrefixes(prefixes, this.getLocalContentType());
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getChildContributors()
	 */
	public IUnifiedEditorContributor[] getChildContributors()
	{
		return null; // default is no child contributors
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#findChildContributor(java.lang.String)
	 */
	public IUnifiedEditorContributor findChildContributor(String contentType)
	{
		if (contentType.equals(getLocalContentType()))
		{
			return this;
		}
		else
		{
			IUnifiedEditorContributor[] contributors = getChildContributors();
			if (contributors != null)
			{
				for (int i = 0; i < contributors.length; i++)
				{

					IUnifiedEditorContributor child = contributors[i].findChildContributor(contentType);
					if (child != null)
					{
						return child;
					}
				}
			}
			return null;
		}
	}

	/**
	 * configLocalPresentationReconciler
	 * 
	 * @param reconciler
	 */
	public void configLocalPresentationReconciler(PresentationReconciler reconciler)
	{
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IContentAssistProcessor getLocalContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		return null;
	}

	/*
	 * Final Methods
	 */

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#configPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler)
	 */
	public final void configPresentationReconciler(PresentationReconciler reconciler)
	{
		// configLocalPresentationReconciler(reconciler);
		//		
		// IUnifiedEditorContributor[] ccs = getChildContributors();
		// if(ccs != null)
		// {
		// for (int i = 0; i < ccs.length; i++)
		// {
		// ccs[i].configPresentationReconciler(reconciler);
		// }
		// }
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getContentTypes()
	 */
	public final String[] getContentTypes()
	{
		// this can be computed only once at first call, and cached
		if (contentTypes == null)
		{
			ArrayList al = new ArrayList();

			// find all contentTypes below this level
			String localCTS = getLocalContentType();
			int len = 0;
			if (localCTS != null)
			{
				al.add(new String[] { localCTS });
				len = 1;
			}

			IUnifiedEditorContributor[] ccs = getChildContributors();
			if (ccs != null)
			{
				for (int i = 0; i < ccs.length; i++)
				{
					String[] ct = ccs[i].getContentTypes();
					if (ct != null)
					{
						al.add(ct);
						len += ct.length;
					}
				}
			}

			// now consolidate them into an array
			contentTypes = new String[len];
			int index = 0;
			for (int i = 0; i < al.size(); i++)
			{
				String[] types = (String[]) al.get(i);
				for (int j = 0; j < types.length; j++)
				{
					contentTypes[index] = types[j];
					index++;
				}
			}
		}
		return contentTypes;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public final IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		// this will only return one IContentAssistProcessor, so the first match wins,
		// but don't cache because result is based on content type
		IContentAssistProcessor localCA = getLocalContentAssistProcessor(sourceViewer, contentType);
		if (localCA != null)
		{
			return new MergingContentProcessor(localCA, new UnifiedTemplateCompletionProcessor(contentType));
		}

		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				IContentAssistProcessor ca = ccs[i].getContentAssistProcessor(sourceViewer, contentType);
				if (ca != null)
				{
					return ca;
				}
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getBracketInserter(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public final IUnifiedBracketInserter getBracketInserter(ISourceViewer sourceViewer, String contentType)
	{
		// this will only return one IContentAssistProcessor, so the first match wins,
		// but don't cache because result is based on content type
		IUnifiedBracketInserter localCA = getLocalBracketInserter(sourceViewer, contentType);
		if (localCA != null)
		{
			return localCA;
		}

		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				IUnifiedBracketInserter ca = ccs[i].getBracketInserter(sourceViewer, contentType);
				if (ca != null)
				{
					return ca;
				}
			}
		}
		return null;
	}

	/**
	 * isValidIdentifier
	 * 
	 * @param c
	 * @param keyCode
	 * @return boolean
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return false;
	}

	/**
	 * isValidActivationCharacter
	 * 
	 * @param c
	 * @param keyCode
	 * @return boolean
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public final ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		// this will only return one ITextHover, so the first match wins,
		// but don't cache because result is based on content type
		ITextHover localHover = getLocalTextHover(sourceViewer, contentType);
		if (localHover != null)
		{
			return localHover;
		}

		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				ITextHover hover = ccs[i].getTextHover(sourceViewer, contentType);
				if (hover != null)
				{
					return hover;
				}
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public final IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		// this result is based on contentType, so isn't cached

		ArrayList al = new ArrayList();

		// find all autoEditStrategies below this level
		IAutoEditStrategy[] localAES = getLocalAutoEditStrategies(sourceViewer, contentType);
		int len = 0;
		if (localAES != null)
		{
			al.add(localAES);
			len = localAES.length;
		}
		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null && ccs.length > 0)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				IAutoEditStrategy[] ct = ccs[i].getAutoEditStrategies(sourceViewer, contentType);
				if (ct != null && ct.length > 0)
				{
					al.add(ct);
					len += ct.length;
				}
			}
		}

		// now consolidate them into an array
		IAutoEditStrategy[] autoEditStrategies = new IAutoEditStrategy[len];
		int index = 0;
		for (int i = 0; i < al.size(); i++)
		{
			IAutoEditStrategy[] aes = (IAutoEditStrategy[]) al.get(i);
			for (int j = 0; j < aes.length; j++)
			{
				autoEditStrategies[index] = aes[j];
				index++;
			}
		}
		return autoEditStrategies;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getFileContext()
	 */
	public final EditorFileContext getFileContext()
	{
		if (fileService == null && parent != null)
		{
			return parent.getFileContext();
		}
		else
		{
			return fileService;
		}
	}

	/**
	 * Sets the current language service
	 * 
	 * @param fileContext
	 */
	public void setFileContext(EditorFileContext fileContext)
	{
		this.fileService = fileContext;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#setParent(com.aptana.ide.editors.unified.IUnifiedEditorContributor)
	 */
	public final void setParent(IUnifiedEditorContributor parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns an instance of the TextHover class for the specified content type
	 * 
	 * @param sourceViewer
	 *            The current ISourceViewer
	 * @param contentType
	 *            The current content type (generally a mine type such as text/html)
	 * @return A new TextHover class, or null if not relevant.
	 */
	public ITextHover getLocalTextHover(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(getLocalContentType()))
		{
			return new LexemeTextHover(getFileContext());
		}

		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getParentConfiguration()
	 */
	public SourceViewerConfiguration getParentConfiguration()
	{
		if (parentConfiguration != null)
		{
			return parentConfiguration;
		}
		else if (parent != null)
		{
			return parent.getParentConfiguration();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#setParentConfiguration(org.eclipse.jface.text.source.SourceViewerConfiguration)
	 */
	public void setParentConfiguration(SourceViewerConfiguration parentConfiguration)
	{
		this.parentConfiguration = parentConfiguration;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getDoubleClickStrategy(ISourceViewer, String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{

		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null && ccs.length > 0)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				ITextDoubleClickStrategy dcs = ccs[i].getDoubleClickStrategy(sourceViewer, contentType);
				if (dcs != null)
				{
					return dcs;
				}
			}
		}

		if (doubleClickStrategy == null)
		{
			doubleClickStrategy = new DefaultTextDoubleClickStrategy();
		}

		return doubleClickStrategy;
	}

	/**
	 * Gets triple click strategy.
	 * 
	 * @param sourceViewer
	 *            - source viewer.
	 * @param contentType
	 *            - content type.
	 * @return triple click strategy
	 */
	public ITextTripleClickStrategy getTripleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		IUnifiedEditorContributor[] ccs = getChildContributors();
		if (ccs != null && ccs.length > 0)
		{
			for (int i = 0; i < ccs.length; i++)
			{
				ITextTripleClickStrategy dcs = ccs[i].getTripleClickStrategy(sourceViewer, contentType);
				if (dcs != null)
				{
					return dcs;
				}
			}
		}

		if (tripleClickStrategy == null)
		{
			tripleClickStrategy = new UnifiedTripleClickStrategy();
		}

		return tripleClickStrategy;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalBracketInserter(ISourceViewer, String)
	 */
	public IUnifiedBracketInserter getLocalBracketInserter(ISourceViewer sourceViewer, String contentType)
	{
		return null;
	}
}
