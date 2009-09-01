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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.editor.js.JSDocumentProvider.JSFileInfo;
import com.aptana.ide.editor.js.context.JSContextAwareness;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.toolbar.ToolbarWidget;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.views.model.ModelContributer;
import com.aptana.ide.views.outline.UnifiedOutlinePage;

/**
 * @author Robin Debreuil
 * @author Pavel Petrochenko
 */
public class JSEditor extends UnifiedEditor
{
	private static final String JSCOMMENT = "JSCOMMENT"; //$NON-NLS-1$
	private static final String SDCOMMENT = "SDCOMMENT"; //$NON-NLS-1$
	
	private boolean _isDisposing = false;
	private boolean _toolbarEnabled;
	private JSContributor _contributor;
	private ToolbarWidget _toolbar;
	private Composite _displayArea;

	/**
	 * JSEditor
	 */
	public JSEditor()
	{
		this(true);
	}
	
	@Override
	protected void initializeKeyBindingScopes()
	{
		setKeyBindingScopes(new String[] { "com.aptana.ide.editors.UnifiedEditorsScope", "com.aptana.ide.editors.JSEditorScope" }); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * JSEditor
	 * 
	 * @param toolbarEnabled if this parameter is set to false editor will never create toolbar    
	 */
	public JSEditor(boolean toolbarEnabled)
	{
		super();
		
		addPluginToPreferenceStore(JSPlugin.getDefault());
		
		this._toolbarEnabled = toolbarEnabled;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createLocalContributor()
	 */
	protected IUnifiedEditorContributor createLocalContributor()
	{
		this._contributor = new JSContributor();

		return this._contributor;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getFileServiceFactory()
	 */
	public IFileServiceFactory getFileServiceFactory()
	{
		return JSFileServiceFactory.getInstance();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getOutlinePage()
	 */
	// @Override
	public UnifiedOutlinePage getOutlinePage()
	{
		UnifiedOutlinePage result;

		// NOTE: we need to check before calling getOutlinePage since calling super will change this expression as a
		// side effect
		if (this.outlinePage == null || (this.outlinePage.getControl() != null && this.outlinePage.getControl().isDisposed()))
		{
			result = super.getOutlinePage();

			if ("true".equals(System.getProperty("environment.outline", "false"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				ModelContributer outlineTab = new ModelContributer();
				
				result.addOutline(outlineTab, "Model"); //$NON-NLS-1$
			}
		}
		else
		{
			result = super.getOutlinePage();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		return JSContextAwareness.getInstance(this);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);

		// This should work, and greatly annoys me that it doesn't
		if (getSourceViewer() != null && getSourceViewer().getTextWidget() != null)
		{
			getSourceViewer().getTextWidget().redraw();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return "js"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (this._isDisposing)
		{
			return;
		}

		this._isDisposing = true;

		if (this._contributor != null)
		{
			this._contributor.dispose();
		}

		if (this._toolbar != null)
		{
			this._toolbar.dispose();
		}

		super.dispose();
	}

	// TODO: add the code by Actuate: gao

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
	 */
	protected String[] collectContextMenuPreferencePages()
	{
		return new String[] {
				"com.aptana.ide.editor.js.preferences.GeneralPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.ColorizationPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.FoldingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.ProblemsPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.CodeAssistPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.FormatterPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.js.preferences.TypingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.scriptdoc.preferences.GeneralPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.scriptdoc.preferences.ColorizationPreferencePage", //$NON-NLS-1$
				"org.eclipse.ui.preferencePages.GeneralTextEditor", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Annotations", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.QuickDiff", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Accessibility", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Spelling", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.LinkedModePreferencePage", //$NON-NLS-1$
			};
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#isNewInput(org.eclipse.ui.IEditorInput)
	 */
	protected boolean isNewInput(IEditorInput input)
	{
		JSFileInfo cuInfo = getJSFileInfo(input);
		if (cuInfo == null)
		{
			throw new RuntimeException(Messages.JSEditor_JSFileInfo_Not_Defined);
		}
		JSDocumentProvider dp = (JSDocumentProvider) getDocumentProvider();

		// Get document from input
		IDocument document = dp.getDocument(input);

		DocumentSourceProvider provider = new DocumentSourceProvider(document, input);

		if (provider == null)
		{
			throw new RuntimeException(Messages.JSEditor_Provider_Not_Defined);
		}

		return (cuInfo.sourceProvider == null || cuInfo.sourceProvider.equals(provider) == false);
	}

	/**
	 * getJSFileInfo
	 * 
	 * @param input
	 * @return JSFileInfo
	 */
	private JSFileInfo getJSFileInfo(IEditorInput input)
	{
		JSDocumentProvider dp = (JSDocumentProvider) getDocumentProvider();
		
		if (dp == null)
		{
			throw new RuntimeException(Messages.JSEditor_Document_Provider_Not_Defined);
		}
		
		return (JSFileInfo) dp.getFileInfoPublic(input);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#updateFileInfo(org.eclipse.ui.IEditorInput,
	 *      com.aptana.ide.editors.unified.DocumentSourceProvider, org.eclipse.jface.text.IDocument)
	 */
	protected void updateFileInfo(IEditorInput input, DocumentSourceProvider provider, IDocument document)
	{
		super.updateFileInfo(input, provider, document);
		
		if (isNewInput(input))
		{
			// save reference to provider
			getJSFileInfo(input).sourceProvider = provider;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		this._displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		this._displayArea.setLayout(daLayout);
		this._displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		IPreferenceStore store = getPreferenceStore();
		boolean show = store.getBoolean(IPreferenceConstants.SHOW_JS_TOOLBAR);
		
		if (show && this._toolbarEnabled)
		{
			this._toolbar = new ToolbarWidget(new String[] { JSMimeType.MimeType }, new String[] { JSMimeType.MimeType },
					getPreferenceStore(), IPreferenceConstants.LINK_CURSOR_WITH_JS_TOOLBAR_TAB, this);
			this._toolbar.createControl(this._displayArea);
		}
		
		Composite editorArea = new Composite(this._displayArea, SWT.NONE);
		editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout eaLayout = new GridLayout(1, true);
		eaLayout.marginHeight = 0;
		eaLayout.marginWidth = 0;
		editorArea.setLayout(new FillLayout());
		super.createPartControl(editorArea);
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createDocumentProvider()
	 */
	public IDocumentProvider createDocumentProvider()
	{
		return JSDocumentProvider.getInstance();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager findMenuUsingPath = menu.findMenuUsingPath("projection"); //$NON-NLS-1$
		
		if (findMenuUsingPath != null)
		{
			findMenuUsingPath.add(new Action(Messages.JSEditor_CollapseComments,
					org.eclipse.jface.action.Action.AS_PUSH_BUTTON)
			{
				public void run()
				{
					ProjectionViewer viewer2 = (ProjectionViewer) getViewer();
					FoldingExtensionPointLoader.collapseAll(viewer2.getProjectionAnnotationModel(),
							JSMimeType.MimeType, JSCOMMENT);
					FoldingExtensionPointLoader.collapseAll(viewer2.getProjectionAnnotationModel(),
							JSMimeType.MimeType, SDCOMMENT);

				}

			});
			findMenuUsingPath.add(new Action(Messages.JSEditor_ExpandComments,
					org.eclipse.jface.action.Action.AS_PUSH_BUTTON)
			{
				public void run()
				{
					ProjectionViewer viewer2 = (ProjectionViewer) getViewer();
					FoldingExtensionPointLoader.expandAll(viewer2.getProjectionAnnotationModel(), JSMimeType.MimeType,
							JSCOMMENT);
					FoldingExtensionPointLoader.expandAll(viewer2.getProjectionAnnotationModel(), JSMimeType.MimeType,
							SDCOMMENT);

				}
			});
		}
	}

	/**
	 * @return if toolbar is hided despite of settings in preference store
	 */
	public boolean isNeverShowToolbar()
	{
		return !this._toolbarEnabled;
	}
}
