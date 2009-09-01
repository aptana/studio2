/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.editors.unified.actions;

import java.io.StringWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.ui.texteditor.TextOperationAction;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.actions.GotoNextMemberAction;
import com.aptana.ide.editors.actions.GotoPreviousMemberAction;
import com.aptana.ide.editors.actions.OpenDeclarationAction;
import com.aptana.ide.editors.actions.QuickOutlineAction;
import com.aptana.ide.editors.actions.ShowPianoKeys;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.editors.unified.messaging.UnifiedMessages;

/**
 * UnifiedActionContributor
 * 
 * @author Ingo Muschenetz
 */
public class UnifiedActionContributor extends TextEditorActionContributor
{

	/**
	 * CODE_FORMAT_ACTION_ID
	 */
	public static final String CODE_FORMAT_ACTION_ID = "com.aptana.ide.editors.unified.format"; //$NON-NLS-1$

	/**
	 * GOTO_NEXT_ID
	 */
	public static final String GOTO_NEXT_ACTIONID = "com.aptana.ide.editor.nextMember"; //$NON-NLS-1$

	/**
	 * GOTO_PREVIOUS_ID
	 */
	public static final String GOTO_PREVIOUS_ACTIONID = "com.aptana.ide.editor.previousMember"; //$NON-NLS-1$

	/**
	 * SHOW_DOC_ACTION_ID
	 */
	public static final String SHOW_DOC_ACTION_ID = UnifiedEditorsPlugin.getDefault().getBundle().getSymbolicName()
			+ ".showDocAction"; //$NON-NLS-1$

	/**
	 * OPEN_DECLARATION_ACTION_ID
	 */
	public static final String OPEN_DECLARATION_ACTION_ID = UnifiedEditorsPlugin.getDefault().getBundle()
			.getSymbolicName()
			+ ".openDeclarationAction"; //$NON-NLS-1$

	/**
	 * QUICK_OUTLINE_ACTION_ID
	 */
	public static final String QUICK_OUTLINE_ACTION_ID = UnifiedEditorsPlugin.getDefault().getBundle()
			.getSymbolicName()
			+ ".quickOutlineAction"; //$NON-NLS-1$

	/**
	 * @since 1.5.2
	 */
	public static final String GOTO_MATCHING_BRACKET_ID = UnifiedEditorsPlugin.getDefault().getBundle()
			.getSymbolicName()
			+ ".goto.matching.bracket"; //$NON-NLS-1$

	private IUnifiedEditor activeEditor = null;

	private TextOperationAction fTextOperationAction;
	private InformationDispatchAction fShowDocAction;
	private OpenDeclarationAction fOpenDeclarationAction;
	private QuickOutlineAction fQuickOutlineAction;
	private PairMatchAction pairMatchAction;
	private CodeFormatAction fCodeFormatAction;
	private GotoNextMemberAction gotoNextAction;
	private GotoPreviousMemberAction gotoPreviousMemberAction;
	private PairSelectAction pairSelectAction;
	private PairSelectContentAction pairSelectContentAction;
	private RetargetTextEditorAction fGotoMatchingBracket;
	private ShowPianoKeys showPianoKeys;
	private boolean dynamicActionsInitialized;

	/**
	 * Constructor
	 */
	public UnifiedActionContributor()
	{
		dynamicActionsInitialized = false;

		// FIXME These pair actions look to be legacy ones that Ingo never implemented. the Pair match appears to be an unimplemented version of what my GotoMatchingBracket impl does
		pairMatchAction = new PairMatchAction();
		pairSelectAction = new PairSelectAction();
		pairSelectContentAction = new PairSelectContentAction();
		fCodeFormatAction = new CodeFormatAction();
		gotoNextAction = new GotoNextMemberAction();
		gotoNextAction.setActionDefinitionId(GOTO_NEXT_ACTIONID);
		gotoPreviousMemberAction = new GotoPreviousMemberAction();
		gotoPreviousMemberAction.setActionDefinitionId(GOTO_PREVIOUS_ACTIONID);
		fOpenDeclarationAction = new OpenDeclarationAction();
		fOpenDeclarationAction.setActionDefinitionId(OPEN_DECLARATION_ACTION_ID);
		fQuickOutlineAction = new QuickOutlineAction();
		fQuickOutlineAction.setActionDefinitionId(QUICK_OUTLINE_ACTION_ID);
		fCodeFormatAction.setActionDefinitionId(CODE_FORMAT_ACTION_ID);

		fGotoMatchingBracket = new RetargetTextEditorAction(Messages.RESOURCE_BUNDLE, "GotoMatchingBracket_"); //$NON-NLS-1$
		fGotoMatchingBracket.setActionDefinitionId(GOTO_MATCHING_BRACKET_ID);

		showPianoKeys = new ShowPianoKeys();
	}

	private IToolBarManager toolBarManager;

	/**
	 * @param toolBarManager
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
		this.toolBarManager = toolBarManager;
		super.contributeToToolBar(toolBarManager);
	}

	/**
	 * @param bars
	 * @param page
	 */
	public void init(IActionBars bars, IWorkbenchPage page)
	{
		super.init(bars, page);

		bars.setGlobalActionHandler("com.aptana.ide.editors.actions.ShowPianoKeys", showPianoKeys); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.editors.text.TextEditorActionContributor#dispose()
	 */
	public void dispose()
	{
		if (fTextOperationAction != null)
		{
			fTextOperationAction = null;
		}
		if (fShowDocAction != null)
		{
			fShowDocAction.dispose();
			fShowDocAction = null;
		}
		if (fOpenDeclarationAction != null)
		{
			fOpenDeclarationAction.dispose();
			fOpenDeclarationAction = null;
		}
		if (fQuickOutlineAction != null)
		{
			fQuickOutlineAction.dispose();
			fQuickOutlineAction = null;
		}
		if (pairMatchAction != null)
		{
			pairMatchAction = null;
		}
		if (fGotoMatchingBracket != null)
		{
			fGotoMatchingBracket = null;
		}
		if (pairSelectAction != null)
		{
			pairSelectAction = null;
		}
		if (pairSelectContentAction != null)
		{
			pairSelectContentAction = null;
		}

		super.dispose();
	}

	/**
	 * Initialize the editor's actions.
	 * 
	 * @param editor
	 */
	protected void initializeDynamicActions(ITextEditor editor)
	{
		fTextOperationAction = new TextOperationAction(UnifiedMessages.getResourceBundle(),
				"ShowTooltip.", editor, ISourceViewer.INFORMATION, true); //$NON-NLS-1$
		fShowDocAction = new InformationDispatchAction(UnifiedMessages.getResourceBundle(),
				"ShowTooltip.", fTextOperationAction, editor); //$NON-NLS-1$
		fShowDocAction.setActionDefinitionId("com.aptana.ide.editors.showDocAction"); //$NON-NLS-1$
		fShowDocAction.setText(Messages.UnifiedActionContributor_ShowToolTip);

		dynamicActionsInitialized = true;
	}

	/**
	 * Add actions to the editor's menus.
	 * 
	 * @param menu
	 */
	public void contributeToMenu(IMenuManager menu)
	{
		if (fShowDocAction == null)
		{
			return;
		}

		super.contributeToMenu(menu);

		IMenuManager editMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null)
		{
			editMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, fShowDocAction);
			editMenu.add(this.fCodeFormatAction);
			editMenu.setVisible(true);
		}

		if (fOpenDeclarationAction != null)
		{
			IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
			if (navigateMenu != null)
			{
				navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fOpenDeclarationAction);
				navigateMenu.setVisible(true);
			}
		}

		if (fQuickOutlineAction != null)
		{
			IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
			if (navigateMenu != null)
			{
				navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fQuickOutlineAction);
				navigateMenu.setVisible(true);
			}
		}

		IMenuManager gotoMenu = menu.findMenuUsingPath("navigate/goTo"); //$NON-NLS-1$
		if (gotoMenu != null)
		{
			gotoMenu.add(new Separator("additions2")); //$NON-NLS-1$
			gotoMenu.appendToGroup("additions2", fGotoMatchingBracket); //$NON-NLS-1$
			if (!gotoMenu.isVisible())
				gotoMenu.setVisible(true);
		}

	}

	/**
	 * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IEditorPart part)
	{

		IWorkbenchPartSite bindingPartSite = part.getSite();

		if (part instanceof IUnifiedEditor)
		{
			activeEditor = (IUnifiedEditor) ((IUnifiedEditor) part).getEditor();
			bindingPartSite = activeEditor.getSite();
			FoldingExtensionPointLoader.updateActions(activeEditor);
		}
		else
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
					Messages.UnifiedActionContributor_PartNotCorrectEditor, part + "\n" + getStackTrace())); //$NON-NLS-1$
			activeEditor = null;
		}

		super.setActiveEditor(part);

		ITextEditor textEditor = null;
		if (part instanceof ITextEditor)
			textEditor = (ITextEditor) part;

		fGotoMatchingBracket.setAction(getAction(textEditor, GotoMatchingBracketAction.GOTO_MATCHING_BRACKET));
		
		if (activeEditor != null)
		{
			doSetActiveEditor((ITextEditor) activeEditor, bindingPartSite);
			// ToolBarContributionRegistryImpl.initToolBar(activeEditor,toolBarManager);
		}
		else
		{
			toolBarManager.removeAll();
			toolBarManager.update(true);
		}
	}

	private void doSetActiveEditor(ITextEditor editor, IWorkbenchPartSite bindingPartSite)
	{
		showPianoKeys.setEditor(editor);

		if (dynamicActionsInitialized == false && editor != null)
		{
			initializeDynamicActions(editor);
			contributeToMenu(getActionBars().getMenuManager());
		}

		// Set active editor for any unified actions
		IFileLanguageService fileEnvironment = null;
		if (editor instanceof UnifiedEditor)
		{
			EditorFileContext fs = ((UnifiedEditor) editor).getFileContext();
			fileEnvironment = fs.getLanguageService(fs.getDefaultLanguage());
			if (fileEnvironment != null)
			{
				fOpenDeclarationAction.setActiveEditor((UnifiedEditor) editor, fileEnvironment);
				fQuickOutlineAction.setActiveEditor((UnifiedEditor) editor, fileEnvironment);
			}
		}
		gotoNextAction.setActiveEditor(null, editor);
		gotoPreviousMemberAction.setActiveEditor(null, editor);
		fCodeFormatAction.setActiveEditor(null, editor);
		// Register actions with key binding service
		IKeyBindingService kbs = bindingPartSite.getKeyBindingService();
		kbs.registerAction(fCodeFormatAction);
		kbs.registerAction(gotoNextAction);
		kbs.registerAction(gotoPreviousMemberAction);
		kbs.registerAction(fShowDocAction);
		kbs.registerAction(fOpenDeclarationAction);
		kbs.registerAction(fQuickOutlineAction);
		kbs.registerAction(pairMatchAction);
		kbs.registerAction(pairSelectAction);
		kbs.registerAction(pairSelectContentAction);
	}

	private String getStackTrace()
	{
		String s = null;
		try
		{
			throw new Exception();
		}
		catch (Exception e)
		{
			StringWriter sw = new StringWriter();
			s = sw.toString();
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedActionContributor_Error, e);
		}
		return s;
	}

	/**
	 * PairMatchAction
	 * 
	 * @author Ingo Muschenetz
	 */
	class PairMatchAction extends Action
	{

		PairMatchAction()
		{
			super(Messages.UnifiedActionContributor_FindMatchingPair);
			setActionDefinitionId("net.sf.colorer.eclipse.editors.pairmatch"); //$NON-NLS-1$
			setToolTipText(Messages.UnifiedActionContributor_FindMatchingPair);
		}

		/**
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run()
		{
			// activeEditor.matchPair();
		}
	}

	/**
	 * PairSelectAction
	 * 
	 * @author Ingo Muschenetz
	 */
	class PairSelectAction extends Action
	{

		PairSelectAction()
		{
			super(Messages.UnifiedActionContributor_SelectMatchingPair);
			setActionDefinitionId("net.sf.colorer.eclipse.editors.pairselect"); //$NON-NLS-1$
			setToolTipText(Messages.UnifiedActionContributor_SelectMatchingPair);
		}

		/**
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run()
		{
			// activeEditor.selectPair();
		}
	}

	/**
	 * PairSelectContentAction
	 * 
	 * @author Ingo Muschenetz
	 */
	class PairSelectContentAction extends Action
	{

		PairSelectContentAction()
		{
			super(Messages.UnifiedActionContributor_SelectContentsToPair);
			setActionDefinitionId("net.sf.colorer.eclipse.editors.pairselectcontent"); //$NON-NLS-1$
			setToolTipText(Messages.UnifiedActionContributor_SelectContentsToPair);
		}

		/**
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run()
		{
			// activeEditor.selectContentPair();
		}
	}
}
