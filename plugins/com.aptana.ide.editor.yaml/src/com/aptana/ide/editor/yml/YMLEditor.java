/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.ide.editor.yml.YMLDocumentProvider.YMLFileInfo;
import com.aptana.ide.editor.yml.actions.YMLActionGroup;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.UnifiedConfiguration;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.ui.editors.ToggleCommentAction;

/**
 * YMLEditor
 */
public class YMLEditor extends UnifiedEditor
{
	
	private YMLActionGroup fGenerateActionGroup;

	@Override
	protected void initializeKeyBindingScopes() {
		super.initializeKeyBindingScopes();
		setKeyBindingScopes(new String[] { "com.aptana.ide.editors.UnifiedEditorsScope", "com.aptana.ide.editor.yml.ymlEditorScope"}); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @see UnifiedEditor#createPartControl(Composite)
	 */
	public void createPartControl(Composite composite) {
		super.createPartControl(composite);
		setToUseSpaces();
	}
	
	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		super.handlePreferenceStoreChanged(event);
		setToUseSpaces();
	}
	
	/**
	 * Force YML editor to always use spaces instead of tabs, regardless of what user preference is.
	 *
	 */
	private void setToUseSpaces() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
		{
			return;
		}
		IPreferenceStore store = getPreferenceStore();
		if (store == null)
		{
			return;
		}
		SourceViewerConfiguration sv = getSourceViewerConfiguration();
		int prefs = store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		if (sv != null && sv instanceof UnifiedConfiguration)
		{
			UnifiedConfiguration uc = (UnifiedConfiguration) sv;
			uc.setTabWidth(prefs, true, sourceViewer);
		}
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createLocalContributor()
	 */
	protected IUnifiedEditorContributor createLocalContributor()
	{
		return new YMLContributor();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return "yml"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getFileServiceFactory()
	 */
	public IFileServiceFactory getFileServiceFactory()
	{
		return YMLFileServiceFactory.getInstance();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createDocumentProvider()
	 */
	public IDocumentProvider createDocumentProvider()
	{
		return YMLDocumentProvider.getInstance();
	}

	/**
	 * isNewInput
	 * 
	 * @param input
	 * @return is new input
	 */
	protected boolean isNewInput(IEditorInput input)
	{
		YMLFileInfo cuInfo = getYMLFileInfo(input);
		if (cuInfo == null)
		{
			throw new RuntimeException("Error");
		}
		YMLDocumentProvider dp = (YMLDocumentProvider) getDocumentProvider();

		// Get document from input
		IDocument document = dp.getDocument(input);

		DocumentSourceProvider provider = new DocumentSourceProvider(document, input);

		if (provider == null)
		{
			throw new RuntimeException("Error");
		}

		return (cuInfo.sourceProvider == null || cuInfo.sourceProvider.equals(provider) == false);
	}

	private YMLFileInfo getYMLFileInfo(IEditorInput input)
	{
		YMLDocumentProvider dp = (YMLDocumentProvider) getDocumentProvider();
		if (dp == null)
		{
			throw new RuntimeException("Error");
		}
		return (YMLFileInfo) dp.getFileInfoPublic(input);
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
			getYMLFileInfo(input).sourceProvider = provider;
		}
	}
	
	@Override
	protected void createActions() 
	{
		super.createActions();
		fGenerateActionGroup= new YMLActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
		
		getSourceViewer().setDefaultPrefixes(new String[] {"#", ""}, YMLMimeType.MimeType);
		// XXX Hack to provide hash as a comment prefix, since we can't seem to set the default prefixes returned by UnifiedConfiguration
		IAction action = new ToggleCommentAction(this)  
		{
			@Override
			protected String[] getDefaultPrefixes(SourceViewerConfiguration configuration, ISourceViewer sourceViewer, String type) 
			{
				return new String[] {"#", ""}; //$NON-NLS-1$ //$NON-NLS-2$
			}
		
		};
		action.setActionDefinitionId("com.aptana.ide.editor.yml.toggle.comment");
		setAction("ToggleComment", action); //$NON-NLS-1$
		markAsStateDependentAction("ToggleComment", true); //$NON-NLS-1$
		configureToggleCommentAction();		
	}
	
	/**
	 * Configures the toggle comment action
	 * 
	 * @since 1.0.0
	 */
    private void configureToggleCommentAction() 
    {
        IAction action = getAction("ToggleComment"); //$NON-NLS-1$
        if (action instanceof ToggleCommentAction) 
        {
            ISourceViewer sourceViewer = getSourceViewer();
            SourceViewerConfiguration configuration = getSourceViewerConfiguration();
            ((ToggleCommentAction) action).configure(sourceViewer, configuration);
        }
    }
    
    protected void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);        
		menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new GroupMarker(ICommonMenuConstants.GROUP_SHOW));

		ActionContext context= new ActionContext(getSelectionProvider().getSelection());
		fGenerateActionGroup.setContext(context);
		fGenerateActionGroup.fillContextMenu(menu);
		fGenerateActionGroup.setContext(null);
    }

}
