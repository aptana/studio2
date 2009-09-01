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
import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultUndoManager;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.commons.spelling.engine.SpellingCorrectionProcessor;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;
import com.aptana.ide.editors.formatting.UnifiedBracketInserterManager;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistant;
import com.aptana.ide.editors.unified.hyperlinks.UnifiedHyperlinkDetector;
import com.aptana.ide.editors.unified.utils.HTMLTextPresenter;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Range;

/**
 * UnifiedConfiguration
 * 
 * @author Ingo Muschenetz
 */
public class UnifiedConfiguration extends SourceViewerConfiguration
{

	/**
	 * CLASS_ATTR
	 */
	public static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	/**
	 * NAME_ATTR
	 */
	public static final String NAME_ATTR = "name"; //$NON-NLS-1$

	/**
	 * UNIFIED_PARTITIONING
	 */
	public static final String UNIFIED_PARTITIONING = "__unified_partitioning"; //$NON-NLS-1$

	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(
			ISourceViewer sourceViewer)
	{
		final QuickAssistAssistant quickAssistAssistant = new QuickAssistAssistant();
		quickAssistAssistant
				.setQuickAssistProcessor(new SpellingCorrectionProcessor());
		return quickAssistAssistant;		
	}

	/**
	 * Default history buffer size if not set in preferences
	 */
	public static final int HISTORY_BUFFER_SIZE = 500;

	UnifiedEditor editor;
	UnifiedColorManager colorManager;

	/**
	 * The preference store used to initialize this configuration.
	 * <p>
	 * Note: protected since 3.1
	 * </p>
	 */
	protected IPreferenceStore fPreferenceStore;
	private boolean useSpaces = false;
	private int tabWidth = 4;
	private MonoReconciler _reconciler;
	private UnifiedReconcilingStrategy _strategy;

	/**
	 * Creates a text source viewer configuration and initializes it with the given preference store.
	 * 
	 * @param editor
	 * @param preferenceStore
	 *            the preference store used to initialize this configuration
	 */
	public UnifiedConfiguration(UnifiedEditor editor, IPreferenceStore preferenceStore)
	{
		this.editor = editor;
		this.editor.getBaseContributor().setParentConfiguration(this);
		fPreferenceStore = preferenceStore;

		useSpaces = fPreferenceStore.getBoolean(IPreferenceConstants.INSERT_SPACES_FOR_TABS);
		tabWidth = fPreferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (_strategy != null)
		{
			_strategy.dispose();
		}

		this._strategy = null;
		this._reconciler = null;

		if (this.editor.getBaseContributor() != null)
		{
			this.editor.getBaseContributor().setParentConfiguration(null);
		}

		this.editor = null;
		this.colorManager = null;
		this.fPreferenceStore = null;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		return this.editor.getBaseContributor().getAutoEditStrategies(sourceViewer, contentType);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
		return this.editor.getBaseContributor().getContentTypes();
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer)
	{
		return UNIFIED_PARTITIONING;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
	{
		IContentAssistant assistant = loadExtensionContentAssistant();

		if (assistant instanceof IUnifiedContentAssistant)
		{
			IUnifiedContentAssistant uAssist = (IUnifiedContentAssistant) assistant;
			uAssist.setEditor(this.editor);
			String[] contentTypes = this.editor.getBaseContributor().getContentTypes();

			String docPart = getConfiguredDocumentPartitioning(sourceViewer);
			uAssist.setDocumentPartitioning(docPart);

			for (int j = 0; j < contentTypes.length; j++)
			{
				IContentAssistProcessor processor = this.editor.getBaseContributor().getContentAssistProcessor(
						sourceViewer, contentTypes[j]);

				if (processor != null)
				{
					uAssist.setContentAssistProcessor(processor, contentTypes[j]);
				}
			}

			IInformationControlCreator icc = getInformationControlCreator(sourceViewer);
			uAssist.setInformationControlCreator(icc);

			if (fPreferenceStore != null)
			{
				int delay = fPreferenceStore.getInt(IPreferenceConstants.CONTENT_ASSIST_DELAY);
				uAssist.setAutoActivationDelay(delay);
			}

			boolean activation = editor.autoActivateCodeAssist();
			uAssist.enableAutoActivation(activation);

			// assistant.setProposalPopupOrientation(ContentAssistant.PROPOSAL_STACKED);
			uAssist.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);

			// assistant.enableAutoInsert(true);

			// Configure the TextHovers now
			// initTextHovers(sourceViewer);
		}

		return assistant;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public UnifiedBracketInserterManager getBracketInserterManager(ISourceViewer sourceViewer)
	{
		UnifiedBracketInserterManager assistant = new UnifiedBracketInserterManager(sourceViewer);
		String[] contentTypes = this.editor.getBaseContributor().getContentTypes();

		String docPart = getConfiguredDocumentPartitioning(sourceViewer);
		assistant.setDocumentPartitioning(docPart);

		for (int j = 0; j < contentTypes.length; j++)
		{
			IUnifiedBracketInserter processor = this.editor.getBaseContributor().getBracketInserter(sourceViewer,
					contentTypes[j]);

			if (processor != null)
			{
				assistant.setBracketInserter(processor, contentTypes[j]);
			}
		}

		return assistant;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationControlCreator(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer)
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new UnifiedInformationControl(parent, SWT.NONE, new HTMLTextPresenter(false)
				// , "getInformationControlCreator"
				);
			}
		};
	}

	/**
	 * getInformationPresenterControlCreator
	 * 
	 * @return IInformationControlCreator
	 */
	public static IInformationControlCreator getInformationPresenterControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				int shellStyle = SWT.RESIZE | SWT.TOOL;
				int style = SWT.NONE;
				return new UnifiedInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false)
				// , "getInformationPresenterControlCreator"
				);
			}
		};
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer)
	{

		// This ensures we obey the Eclipse Text Editor preference for enabling editor hyperlinks
		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
		{
			return null;
		}

		IHyperlinkDetector[] supers = super.getHyperlinkDetectors(sourceViewer);
		EditorFileContext fs = this.editor.getFileContext();
		IFileLanguageService fileEnvironment = fs.getLanguageService(fs.getDefaultLanguage());
		UnifiedHyperlinkDetector detector = new UnifiedHyperlinkDetector(fileEnvironment);
		IHyperlinkDetector[] plusUnified = new IHyperlinkDetector[supers.length + 1];
		System.arraycopy(supers, 0, plusUnified, 0, supers.length);
		plusUnified[plusUnified.length - 1] = detector;
		return plusUnified;
	}

	/**
	 * getInformationPresenterControlCreator
	 * 
	 * @param sourceViewer
	 * @return IInformationControlCreator
	 */
	private IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer)
	{
		return getInformationPresenterControlCreator();
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationPresenter(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer)
	{
		UnifiedInformationPresenter informationPresenter = new UnifiedInformationPresenter(
				getInformationPresenterControlCreator(sourceViewer));

		informationPresenter.setDocumentPartitioning(UNIFIED_PARTITIONING);

		IInformationProvider provider = new UnifiedInformationProvider(sourceViewer, this.editor.getBaseContributor()
				.getFileContext());

		String[] types = this.getConfiguredContentTypes(sourceViewer);

		informationPresenter.setInformationProvider(provider, UNIFIED_PARTITIONING);

		for (int j = 0; j < types.length; j++)
		{
			informationPresenter.setInformationProvider(provider, types[j]);
		}

		informationPresenter.setSizeConstraints(60, 10, true, true);

		return informationPresenter;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		return this.editor.getBaseContributor().getDoubleClickStrategy(sourceViewer, contentType);

		/*
		 * if (doubleClickStrategy == null) doubleClickStrategy = new UnifiedDoubleClickStrategy(); return
		 * doubleClickStrategy;
		 */
	}

	/**
	 * Gets triple click strategy.
	 * 
	 * @param sourceViewer -
	 *            source viewer.
	 * @param contentType -
	 *            content type.
	 * @return triple click strategy
	 */
	public ITextTripleClickStrategy getTripleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		return this.editor.getBaseContributor().getTripleClickStrategy(sourceViewer, contentType);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = new PresentationReconciler();

		// DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
		// reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		// reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		this.editor.getBaseContributor().configPresentationReconciler(reconciler);

		reconciler.setDocumentPartitioning(UNIFIED_PARTITIONING);

		return reconciler;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask)
	{
		return this.editor.getBaseContributor().getTextHover(sourceViewer, contentType);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		return this.editor.getBaseContributor().getTextHover(sourceViewer, contentType);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAnnotationHover(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
	{
		return new UnifiedAnnotationHover();
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		_strategy = this.editor.getBaseContributor().getReconcilingStrategy();
		if (_strategy == null)
		{
			return null;
		}
		_strategy.setEditor(editor);

		_reconciler = new MonoReconciler(_strategy, false);
		_reconciler.setDelay(1000);

		return _reconciler;
	}

	// TODO: get this from prefs in way it is updated as user pref changes, make it per file.
	private static String LINE_DELIM = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * getNewlineString
	 * 
	 * @return String
	 */
	public static String getNewlineString()
	{
		return LINE_DELIM;// "\r\n";
	}

	/**
	 * isNewlineString
	 * 
	 * @param string
	 * @return is the item a new line character?
	 */
	public static boolean isNewlineString(String string)
	{
		return string.equals("\r") || string.equals("\n") || string.equals("\r\n");// "\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDefaultPrefixes(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType)
	{
		return super.getDefaultPrefixes(sourceViewer, contentType);
		// return new String[] {"_"};
	}

	/**
	 * this override sets the valid chars that can be at the start of an indent that will be tabbed to the right during
	 * a multiline tab indent
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getIndentPrefixes(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType)
	{
		if (useSpacesAsTabs())
		{
			return getSpacesIndexPrefix();
		}
		else
		{
			return getTabsIndexPrefix();
		}
		// return this.editor.getBaseContributor().getIndentPrefixes(sourceViewer, contentType);
	}

	/**
	 * Returns the array of prefixes for use in indenting (where it is set to indent with spaces)
	 * 
	 * @return String[]
	 */
	public String[] getSpacesIndexPrefix()
	{
		String spaces = getTabAsSpaces();
		ArrayList prefixes = new ArrayList();

		// Spaces before tabs. In UnifiedViewer, it will use the [0] item for the indent
		// but the rest of the array for removing prefixes
		prefixes.addAll(Arrays.asList(StringUtils.getArrayOfSpaces(spaces.length())));
		prefixes.add(StringUtils.TAB);

		return (String[]) prefixes.toArray(new String[0]);

	}

	/**
	 * Returns the array of prefixes for use in indenting (where it is set to indent with tabs)
	 * 
	 * @return String[]
	 */
	public String[] getTabsIndexPrefix()
	{
		String spaces = getTabAsSpaces();
		ArrayList prefixes = new ArrayList();

		// tab before spaces. In UnifiedViewer, it will use the [0] item for the indent
		// but the rest of the array for removing prefixes
		prefixes.add(StringUtils.TAB);
		prefixes.addAll(Arrays.asList(StringUtils.getArrayOfSpaces(spaces.length())));

		return (String[]) prefixes.toArray(new String[0]);
	}

	/**
	 * Returns the tab width for the particular source viewer
	 * 
	 * @param sourceViewer
	 *            The source viewer
	 * @return The tab width
	 */
	public int getTabWidth(ISourceViewer sourceViewer)
	{
		if (fPreferenceStore == null)
		{
			return super.getTabWidth(sourceViewer);
		}
		return fPreferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	/**
	 * Use spaces instead of tabs
	 * 
	 * @return Use spaces instead of tabs
	 */
	public boolean useSpacesAsTabs()
	{
		return useSpaces;
	}

	/**
	 * The current tab indent, expressed as spaces
	 * 
	 * @return The string of spaces, equal to the current tab width;
	 */
	public String getTabAsSpaces()
	{
		return StringUtils.getSpaces(tabWidth);
	}

	/**
	 * The current tab indent, expressed as spaces or a tab character
	 * 
	 * @return The current indent character;
	 */
	public String getIndent()
	{
		if (useSpacesAsTabs())
		{
			return getTabAsSpaces();
		}
		else
		{
			return "\t"; //$NON-NLS-1$
		}
	}

	/**
	 * Sets the current tab width, and whether to use tabs or space.
	 * 
	 * @param tabWidth
	 *            The number of "spaces" to use for a tab.
	 * @param useSpaces
	 *            Insert spaces instead of tabs
	 * @param sourceViewer
	 *            The current source viewer
	 */
	public void setTabWidth(int tabWidth, boolean useSpaces, ISourceViewer sourceViewer)
	{
		this.useSpaces = useSpaces;
		this.tabWidth = tabWidth;

		String[] prefixes;
		if (useSpaces)
		{
			prefixes = getSpacesIndexPrefix();
		}
		else
		{
			prefixes = getTabsIndexPrefix();
		}

		this.editor.getBaseContributor().setAllIndentPrefixes(prefixes, sourceViewer);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getUndoManager(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IUndoManager getUndoManager(ISourceViewer sourceViewer)
	{
		IPreferenceStore store = org.eclipse.ui.internal.editors.text.EditorsPlugin.getDefault().getPreferenceStore();
		int historySize = store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE);
		if (historySize <= 0)
		{
			historySize = UnifiedConfiguration.HISTORY_BUFFER_SIZE;
		}
		return new DefaultUndoManager(historySize);
	}

	/**
	 * Gets the strategy for this configuration
	 * 
	 * @return - reconciling strategy
	 */
	public UnifiedReconcilingStrategy getStrategy()
	{
		return _strategy;
	}

	/**
	 * Loads and registers the first content assistnant contributed via extension point
	 * 
	 * @return IContentAssistant
	 */
	public IContentAssistant loadExtensionContentAssistant()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.CONTENT_ASSISTANT_EXTENSION_POINT);
		if (ep == null)
		{
			return new ContentAssistant();
		}
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			try
			{
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++)
				{
					String assistantType = elements[j].getAttribute(CLASS_ATTR);
					if (assistantType != null)
					{
						return (IContentAssistant) elements[j].createExecutableExtension(CLASS_ATTR);
					}
				}
			}
			catch (InvalidRegistryObjectException e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedConfiguration_ERR_UnableToLoadContentAssistant);
			}
			catch (Exception e)
			{
				// We want to catch everything so one colorizers errors doesn't affect anothers
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
			}
		}

		return new ContentAssistant();

	}


	/**
	 * returns customized hyperlink presenter
	 * @param sourceViewer
	 * @return customized hyperlink presenter
	 */
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer)
	{
		DefaultHyperlinkPresenter pr = new DefaultHyperlinkPresenter(new RGB(0, 0, 255))
		{

			private IRange range;

			public void applyTextPresentation(TextPresentation textPresentation)
			{
				super.applyTextPresentation(textPresentation);
			}

			public void hideHyperlinks()
			{
				if (range == null || editor.getViewer().getDocument().getLength() > range.getEndingOffset())
				{
					editor.setUnderlinedRange(range);
				}
				super.hideHyperlinks();
				range = null;
			}

			public void showHyperlinks(IHyperlink[] hyperlinks)
			{
				if (hyperlinks.length == 1)
				{
					if (range != null && editor.getViewer().getDocument().getLength() > range.getEndingOffset())
					{
						editor.setUnderlinedRange(range);
					}
					IRegion hyperlinkRegion = hyperlinks[0].getHyperlinkRegion();
					range = new Range(hyperlinkRegion.getOffset(), hyperlinkRegion.getLength()
							+ hyperlinkRegion.getOffset());
					editor.removeUnderlinedRange(range);
				}
				super.showHyperlinks(hyperlinks);
			}

		};
		return pr;
	}

}
