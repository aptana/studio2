/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.editor.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.operations.OperationHistoryActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditor.Gradient;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.editors.ISaveAsEvent;
import com.aptana.ide.core.ui.editors.ISaveEvent;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.properties.PreviewPropertyPage;
import com.aptana.ide.editors.toolbar.ToolbarWidget;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileServiceChangeListener;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.ui.editors.EditorMessages;
import com.aptana.ide.views.outline.UnifiedOutlinePage;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;

/**
 * @author Robin Debreuil
 */
public class HTMLEditor extends EditorPart implements ITextEditor, ITextEditorExtension, IUnifiedEditor
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$

	private Composite displayArea;
	private ToolbarWidget toolbar;

	/**
	 * editor
	 */
	protected EditorPart editor;

	/**
	 * sourceEditor
	 */
	protected HTMLSourceEditor sourceEditor;

	private HTMLSourceEditor ctorSourceEditor;

	/**
	 * prevTempFile
	 */
	protected File prevTempFile = null;

	private IPropertyListener _propertyListener;

	private IMenuListener _menuListener;

	private boolean isDisposing = false;

	// private IUnifiedBracketMatcherListener _bracketMatcherListener;

	private ISaveAsEvent _saveAsListener;

	/**
	 * TAB_MODE
	 */
	public static final String TAB_MODE = "tab"; // The default //$NON-NLS-1$

	/**
	 * HORIZONTAL_MODE
	 */
	public static final String HORIZONTAL_MODE = "horizontal"; //$NON-NLS-1$

	/**
	 * VERTICAL_MODE
	 */
	public static final String VERTICAL_MODE = "vertical"; //$NON-NLS-1$

	/**
	 * SOURCE_MODE
	 */
	public static final String SOURCE_MODE = "source"; //$NON-NLS-1$

	/**
	 * HTMLEditor
	 */
	public HTMLEditor()
	{
		super();

		IPreferenceStore store = getPreferenceStore();
		String editorMode = store.getString(IPreferenceConstants.HTML_EDITOR_VIEW_CHOICE);

		if (editorMode.equals(HORIZONTAL_MODE))
		{
			ctorSourceEditor = createSourceEditor();
			editor = new SplitPageHTMLEditor(this, true, ctorSourceEditor);
		}
		else if (editorMode.equals(VERTICAL_MODE))
		{
			ctorSourceEditor = createSourceEditor();
			editor = new SplitPageHTMLEditor(this, false, ctorSourceEditor);
		}
		else if (editorMode.equals(TAB_MODE))
		{
			// Previously tab case
			ctorSourceEditor = createSourceEditor();
			editor = new MultiPageHTMLEditor(this, ctorSourceEditor);
		}
		else
		{
			// No tabs option
			editor = createSourceEditor();
			_propertyListener = new IPropertyListener()
			{
				public void propertyChanged(Object source, int propertyId)
				{
					firePropertyChange(propertyId);
				}
			};
			editor.addPropertyListener(_propertyListener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#addRulerContextMenuListener(org.eclipse.jface.action.IMenuListener)
	 */
	public void addRulerContextMenuListener(IMenuListener listener)
	{
		if (sourceEditor == null)
		{
			return;
		}

		_menuListener = listener;
		sourceEditor.addRulerContextMenuListener(listener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#close(boolean)
	 */
	public void close(boolean save)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.close(save);
	}

	/**
	 * createHTMLSourceEditor
	 * 
	 * @return HTMLSourceEditor
	 */
	protected HTMLSourceEditor createSourceEditor()
	{
		this.sourceEditor = new HTMLSourceEditor()
		{

			protected void createActions()
			{
				super.createActions();
				TextEditorAction cut;
				final IAction previousCut = sourceEditor.getAction(ITextEditorActionConstants.CUT);
				cut = new TextEditorAction(EditorMessages.getBundleForConstructedKeys(), "Editor.Cut.", this) //$NON-NLS-1$
				{

					public void run()
					{
						if (editor instanceof MultiPageHTMLEditor)
						{
							MultiPageHTMLEditor multi = (MultiPageHTMLEditor) editor;
							if (!multi.run(getActionDefinitionId()))
							{
								if (previousCut != null)
								{
									previousCut.run();
								}
							}
						}
						else if (previousCut != null)
						{
							previousCut.run();
						}
					}

				};
				cut.setHelpContextId(IAbstractTextEditorHelpContextIds.CUT_ACTION);
				cut.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
				setAction(ITextEditorActionConstants.CUT, cut);

				TextEditorAction copy;
				final IAction previousCopy = sourceEditor.getAction(ITextEditorActionConstants.COPY);
				copy = new TextEditorAction(EditorMessages.getBundleForConstructedKeys(), "Editor.Copy.", this) //$NON-NLS-1$
				{

					public void run()
					{
						if (editor instanceof MultiPageHTMLEditor)
						{
							MultiPageHTMLEditor multi = (MultiPageHTMLEditor) editor;
							if (!multi.run(getActionDefinitionId()))
							{
								if (previousCopy != null)
								{
									previousCopy.run();
								}
							}
						}
						else if (previousCopy != null)
						{
							previousCopy.run();
						}
					}

				};
				copy.setHelpContextId(IAbstractTextEditorHelpContextIds.COPY_ACTION);
				copy.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
				setAction(ITextEditorActionConstants.COPY, copy);

				TextEditorAction paste;
				final IAction previousPaste = sourceEditor.getAction(ITextEditorActionConstants.PASTE);
				paste = new TextEditorAction(EditorMessages.getBundleForConstructedKeys(), "Editor.Paste.", this) //$NON-NLS-1$
				{

					public void run()
					{
						if (editor instanceof MultiPageHTMLEditor)
						{
							MultiPageHTMLEditor multi = (MultiPageHTMLEditor) editor;
							if (!multi.run(getActionDefinitionId()))
							{
								if (previousPaste != null)
								{
									previousPaste.run();
								}
							}
						}
						else if (previousPaste != null)
						{
							previousPaste.run();
						}
					}

				};
				paste.setHelpContextId(IAbstractTextEditorHelpContextIds.PASTE_ACTION);
				paste.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
				setAction(ITextEditorActionConstants.PASTE, paste);
			}

		};

		return this.sourceEditor;
	}

	/**
	 * Sets the toolbar as visible or not
	 * 
	 * @param visible -
	 *            true if visible
	 */
	public void setToolbarVisible(boolean visible)
	{
		if (toolbar != null && visible != toolbar.isVisible())
		{
			toolbar.setVisible(visible);
			displayArea.setRedraw(false);
			displayArea.layout(true, true);
			displayArea.setRedraw(true);
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		getSite().setSelectionProvider(sourceEditor.getSelectionProvider());

		// parent.addListener(SWT.Activate, new Listener() {
		// public void handleEvent(Event event) {
		// if (event.type == SWT.Activate)
		// {
		// IEditorPart e = editor;
		// EditorSite innerSite = (EditorSite) e.getEditorSite();
		// ((WorkbenchPage) innerSite.getPage()).requestActivation(e);
		// }
		// }
		// });

		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		IPreferenceStore store = getPreferenceStore();
		boolean show = store.getBoolean(IPreferenceConstants.SHOW_HTML_TOOLBAR);
		if (show)
		{
			String[] languages = new String[] { HTMLMimeType.MimeType, JSMimeType.MimeType, CSSMimeType.MimeType };
			String[] labels = new String[] { " html ", "  js  ", " css  " }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			toolbar = new ToolbarWidget(languages, labels, getPreferenceStore(),
					IPreferenceConstants.LINK_CURSOR_WITH_HTML_TOOLBAR_TAB, sourceEditor);
			toolbar.createControl(displayArea);
		}
		Composite editorArea = new Composite(displayArea, SWT.NONE);
		editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout eaLayout = new GridLayout(1, true);
		eaLayout.marginHeight = 0;
		eaLayout.marginWidth = 0;
		editorArea.setLayout(new FillLayout());
		editor.createPartControl(editorArea);
		if (toolbar != null)
		{
			toolbar.hookCursorListener();
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (isDisposing)
		{
			return;
		}

		isDisposing = true;

		IAction undo = this.getAction(ITextEditorActionConstants.UNDO);
		IAction redo = this.getAction(ITextEditorActionConstants.REDO);
		if (_propertyListener != null)
		{
			editor.removePropertyListener(_propertyListener);
			_propertyListener = null;
		}
		if (_menuListener != null)
		{
			sourceEditor.removeRulerContextMenuListener(_menuListener);
			_menuListener = null;
		}
		if (_saveAsListener != null)
		{
			sourceEditor.removeSaveAsListener(_saveAsListener);
			_saveAsListener = null;
		}
		// if(_bracketMatcherListener != null)
		// {
		// sourceEditor.removeBracketMatchListener(_bracketMatcherListener);
		// _bracketMatcherListener = null;
		// }

		// these cause editor part to hang on to an internal compatibilityTitleListener the refs the
		// editor part
		// assigning to them seem to be the only way to trigger a remove(?)
		setPartName(null);
		// setContentDescription(null);

		super.dispose();

		if (ctorSourceEditor != null)
		{
			ctorSourceEditor.dispose();
		}
		if (editor != null)
		{
			editor.dispose();
		}
		if (sourceEditor != null)
		{
			sourceEditor.dispose();
		}
		if (toolbar != null)
		{
			toolbar.dispose();
		}

		if (undo instanceof OperationHistoryActionHandler)
		{
			((OperationHistoryActionHandler) undo).dispose();
		}
		if (redo instanceof OperationHistoryActionHandler)
		{
			((OperationHistoryActionHandler) redo).dispose();
		}
		// ctorSourceEditor = null;
		// editor = null;
		// sourceEditor = null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#doRevertToSaved()
	 */
	public void doRevertToSaved()
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.doRevertToSaved();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		if (editor == null)
		{
			return;
		}

		editor.doSave(monitor);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs()
	{
		if (editor == null)
		{
			return;
		}

		editor.doSaveAs();
	}

	/**
	 * drawGradient
	 * 
	 * @param innerEditor
	 * @param g
	 */
	protected void drawGradient(IEditorPart innerEditor, Gradient g)
	{
	}

	/**
	 * firePropertyChange2
	 * 
	 * @param propertyId
	 */
	public void firePropertyChange2(int propertyId)
	{
		super.firePropertyChange(propertyId);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getAction(java.lang.String)
	 */
	public IAction getAction(String actionId)
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getAction(actionId);
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		if (editor == null)
		{
			return null;
		}

		// Context is just plugin ID + name of class. Matches contexts.xml file
		return editor.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart2#getContentDescription()
	 */
	public String getContentDescription()
	{
		if (editor == null)
		{
			return ""; //$NON-NLS-1$
		}

		return editor.getContentDescription();
	}

	// --- implement ITextEditor so that global menu commands are properly activated ---//

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getDocumentProvider()
	 */
	public IDocumentProvider getDocumentProvider()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getDocumentProvider();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#getEditorInput()
	 */
	public IEditorInput getEditorInput()
	{
		if (editor == null)
		{
			return null;
		}

		return editor.getEditorInput();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#getEditorSite()
	 */
	public IEditorSite getEditorSite()
	{
		if (editor == null)
		{
			return null;
		}

		return editor.getEditorSite();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getHighlightRange()
	 */
	public IRegion getHighlightRange()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getHighlightRange();
	}

	/**
	 * getSourceEditor
	 * 
	 * @return HTMLSourceEditor
	 */
	public HTMLSourceEditor getSourceEditor()
	{
		if (editor == null)
		{
			return null;
		}

		if (editor instanceof HTMLSourceEditor)
		{
			return (HTMLSourceEditor) editor;
		}
		else
		{
			return ((IHTMLEditorPart) editor).getSourceEditor();
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart2#getPartName()
	 */
	public String getPartName()
	{
		if (editor == null)
		{
			return ""; //$NON-NLS-1$
		}

		return editor.getPartName();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getSelectionProvider();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getSite()
	 */
	public IWorkbenchPartSite getSite()
	{
		if (editor == null)
		{
			return null;
		}

		return editor.getSite();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitle()
	 */
	public String getTitle()
	{
		if (editor == null)
		{
			return ""; //$NON-NLS-1$
		}

		return editor.getTitle();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
	 */
	public Image getTitleImage()
	{
		if (editor == null)
		{
			return null;
		}

		return editor.getTitleImage();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
	 */
	public String getTitleToolTip()
	{
		if (editor == null)
		{
			return ""; //$NON-NLS-1$
		}

		return editor.getTitleToolTip();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if (editor == null)
		{
			return;
		}

		editor.init(site, input);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty()
	{
		if (editor == null)
		{
			return false;
		}

		return editor.isDirty();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#isEditable()
	 */
	public boolean isEditable()
	{
		if (sourceEditor == null)
		{
			return false;
		}

		return sourceEditor.isEditable();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#isEditorInputReadOnly()
	 */
	public boolean isEditorInputReadOnly()
	{
		if (sourceEditor == null)
		{
			return false;
		}

		return sourceEditor.isEditorInputReadOnly();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		if (editor == null)
		{
			return false;
		}

		return editor.isSaveAsAllowed();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded()
	{
		if (editor == null)
		{
			return false;
		}

		return editor.isSaveOnCloseNeeded();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#removeActionActivationCode(java.lang.String)
	 */
	public void removeActionActivationCode(String actionId)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.removeActionActivationCode(actionId);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#removeRulerContextMenuListener(org.eclipse.jface.action.IMenuListener)
	 */
	public void removeRulerContextMenuListener(IMenuListener listener)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.removeRulerContextMenuListener(listener);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#resetHighlightRange()
	 */
	public void resetHighlightRange()
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.resetHighlightRange();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#selectAndReveal(int, int)
	 */
	public void selectAndReveal(int offset, int length)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.selectAndReveal(offset, length);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setAction(java.lang.String, org.eclipse.jface.action.IAction)
	 */
	public void setAction(String actionID, IAction action)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.setAction(actionID, action);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setActionActivationCode(java.lang.String, char, int, int)
	 */
	public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode,
			int activationStateMask)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		if (editor == null)
		{
			return;
		}

		editor.setFocus();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setHighlightRange(int, int, boolean)
	 */
	public void setHighlightRange(int offset, int length, boolean moveCursor)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.setHighlightRange(offset, length, moveCursor);
	}

	/**
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
	{
		if (editor == null)
		{
			return;
		}

		editor.setInitializationData(config, propertyName, data);
	}

	/**
	 * change to the source editor, and move caret to the specified offset.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		if (editor == null)
		{
			return;
		}

		if (editor instanceof SplitPageHTMLEditor)
		{
			((SplitPageHTMLEditor) editor).setOffset(offset);
		}
		else if (editor instanceof MultiPageHTMLEditor)
		{
			((MultiPageHTMLEditor) editor).setOffset(offset);
		}
		else if (editor instanceof HTMLSourceEditor)
		{
			((HTMLSourceEditor) editor).selectAndReveal(offset, 0);
		}
	}

	// --- implement ITextEditorExtension so that we get a status bar ---//

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#setStatusField(org.eclipse.ui.texteditor.IStatusField,
	 *      java.lang.String)
	 */
	public void setStatusField(IStatusField field, String category)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.setStatusField(field, category);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#showBusy(boolean)
	 */
	public void showBusy(boolean busy)
	{
		if (editor == null)
		{
			return;
		}

		editor.showBusy(busy);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showHighlightRangeOnly(boolean)
	 */
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.showHighlightRangeOnly(showHighlightRangeOnly);
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showsHighlightRangeOnly()
	 */
	public boolean showsHighlightRangeOnly()
	{
		if (sourceEditor == null)
		{
			return false;
		}

		return sourceEditor.showsHighlightRangeOnly();
	}

	/**
	 * updateSource
	 */
	public void updateSource()
	{
		// IHTMLEditorPart editor = (IHTMLEditorPart) this.editor;
		// IEditorInput input = this.editor.getEditorInput();
		// editor.getSourceEditor().getDocumentProvider().getDocument(input).set(html);
	}

	/**
	 * updateEdit
	 */
	public void updateEdit()
	{
		if (editor == null)
		{
			return;
		}

		if (!(editor instanceof IHTMLEditorPart))
		{
			return;
		}

		if (!((IHTMLEditorPart) editor).isFileEditorInput())
		{
			return;
		}

		IHTMLEditorPart editor = (IHTMLEditorPart) this.editor;
		IEditorInput input = this.editor.getEditorInput();

		String html = editor.getSourceEditor().getDocumentProvider().getDocument(input).get();

		String js = "fill(\"" //$NON-NLS-1$
				+ html.replaceAll("\r", "&return;").replaceAll("\n", "&newline;").replaceAll("&", "&amp;").replaceAll( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						"<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&#39;") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
				+ "\");"; //$NON-NLS-1$

		editor.getBrowser2().execute(js);
	}

	/**
	 * Update preview
	 */
	public void updatePreview()
	{
		if (editor == null)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLEditor_UnableToUpdatePreview + ": editor = null");
			return;
		}

		if (!(editor instanceof IHTMLEditorPart))
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLEditor_UnableToUpdatePreview + ": editor !instanceof IHTMLEditorPart");
			return;
		}

		try
		{
			if (!((IHTMLEditorPart) editor).isFileEditorInput())
			{
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLEditor_UnableToUpdatePreview + ": !isFileEditorInput");
				return;
			}

			// write to temporary file
			IHTMLEditorPart editor = (IHTMLEditorPart) this.editor;
			IEditorInput input = this.editor.getEditorInput();

			String urlPrefix = null;
			if (input instanceof FileEditorInput)
			{
				urlPrefix = getExternalPreviewUrl(input);
			}

			if (urlPrefix != null && !"".equals(urlPrefix)) //$NON-NLS-1$
			{
				FileEditorInput fei = (FileEditorInput) input;
				IFile file = fei.getFile();
				String tmpUrl = CoreUIUtils.joinURI(urlPrefix, file.getProjectRelativePath().toPortableString());
				editor.setBrowserURL(tmpUrl);
			}
			else
			{
				if (HTMLPlugin.getDefault().getPreferenceStore().getBoolean(
						IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW))
				{
					IDocumentProvider docProvider = editor.getSourceEditor().getDocumentProvider();
					String html = docProvider.getDocument(input).get();

					String charset = null;

					if (input instanceof IFileEditorInput)
					{
						charset = ((IFileEditorInput) input).getFile().getCharset();
					}
					else if (docProvider instanceof TextFileDocumentProvider)
					{
						charset = ((TextFileDocumentProvider) docProvider).getEncoding(input);
						if (charset == null)
						{
							charset = ((TextFileDocumentProvider) docProvider).getDefaultEncoding();
						}
					}

					File tmpFile = writeTemporaryPreviewFile(editor, input, html, charset);
					String tmpUrl = CoreUIUtils.getURI(tmpFile, false);

					if (prevTempFile != null && prevTempFile.equals(tmpFile))
					{
						editor.setBrowserURL(tmpUrl);
					}
					else
					{
						if (prevTempFile != null)
						{
							if (!prevTempFile.delete()) {
								prevTempFile.deleteOnExit();
							}
						}
						prevTempFile = tmpFile;
						editor.setBrowserURL(tmpUrl);
					}
				}
				else if(input instanceof IFileEditorInput)
				{
					IFileEditorInput fei = (IFileEditorInput) input;
					IFile file = fei.getFile();
					editor.setBrowserURL(CoreUIUtils.getURI(file.getLocation().toFile(), false));
				}
				else if(input instanceof IPathEditorInput)
				{
					IPathEditorInput fei = (IPathEditorInput) input;
					File file = fei.getPath().toFile();
					editor.setBrowserURL(CoreUIUtils.getURI(file, false));
				}
				else
				{
					IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLEditor_UnableToUpdatePreview);
				}
			}
		}
		catch (Exception ex)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLEditor_UnableToUpdatePreview, ex);
		}
	}

	private File writeTemporaryPreviewFile(IHTMLEditorPart editor, IEditorInput input, String html, String charset)
			throws CoreException, FileNotFoundException, UnsupportedEncodingException
	{

		File tmpFile = editor.getSourceEditor().getTempFile();

		if (tmpFile.exists())
		{
			tmpFile.delete();
		}

		FileOutputStream out = new FileOutputStream(tmpFile);
		PrintWriter pw = null;

		if (charset != null)
		{
			pw = new PrintWriter(new OutputStreamWriter(out, charset), true);
		}
		else
		{
			pw = new PrintWriter(new OutputStreamWriter(out), true);
		}

		pw.write(html);
		pw.close();

		try
		{
			out.close();
		}
		catch (IOException e)
		{
		}

		// Delete this file on exit, and hide it from windows
		tmpFile.deleteOnExit();
		FileUtils.setHidden(tmpFile);

		return tmpFile;
	}

	private String getExternalPreviewUrl(IEditorInput input) throws CoreException
	{

		String urlPrefix = null;
		FileEditorInput fei = (FileEditorInput) input;
		IFile file = fei.getFile();
		IProject project = file.getProject();
		urlPrefix = ((IResource) project).getPersistentProperty(new QualifiedName(
				"", PreviewPropertyPage.PREVIEW_PREFIX)); //$NON-NLS-1$

		return urlPrefix;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getFileContext()
	 */
	public EditorFileContext getFileContext()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getFileContext();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getViewer()
	 */
	public ISourceViewer getViewer()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getViewer();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getConfiguration()
	 */
	public SourceViewerConfiguration getConfiguration()
	{
		if (this.getSourceEditor() == null)
		{
			return null;
		}

		return this.getSourceEditor().getConfiguration();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		if (sourceEditor == null)
		{
			return null;
		}

		return sourceEditor.getContextAwareness();
	}

	/**
	 * getCaretOffset
	 * 
	 * @return int
	 */
	public int getCaretOffset()
	{
		ISourceViewer sv = this.getViewer();

		if (sv != null)
		{
			return sv.getTextWidget().getCaretOffset();
		}
		else
		{
			return -1;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getEditor()
	 */
	public IEditorPart getEditor()
	{
		return sourceEditor;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showWhitespace(boolean)
	 */
	public void showWhitespace(boolean state)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.showWhitespace(state);
	}

	// /**
	// * @see
	// com.aptana.ide.editors.unified.IUnifiedEditor#addBracketMatchListener(com.aptana.ide.editors.unified.IUnifiedBracketMatcherListener)
	// */
	// public void addBracketMatchListener(IUnifiedBracketMatcherListener obj)
	// {
	// if(sourceEditor == null)
	// return;
	//		
	// _bracketMatcherListener = obj;
	// sourceEditor.addBracketMatchListener(obj);
	// }
	//
	// public void removeBracketMatchListener(IUnifiedBracketMatcherListener obj)
	// {
	// if(sourceEditor == null)
	// return;
	//		
	// sourceEditor.removeBracketMatchListener(obj);
	// }

	/**
	 * see
	 * com.aptana.ide.core.ui.editors.ISaveEventListener#addSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 * 
	 * @param listener
	 */
	public void addSaveAsListener(ISaveAsEvent listener)
	{
		if (sourceEditor == null)
		{
			return;
		}

		_saveAsListener = listener;
		sourceEditor.addSaveAsListener(listener);
	}

	/**
	 * see
	 * com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 * 
	 * @param listener
	 */
	public void removeSaveAsListener(ISaveAsEvent listener)
	{
		if (sourceEditor == null)
		{
			return;
		}

		sourceEditor.removeSaveAsListener(listener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getOutlinePage()
	 */
	public UnifiedOutlinePage getOutlinePage()
	{
		return sourceEditor.getOutlinePage();
	}

	/**
	 * {@inheritDoc}
	 */
	public UnifiedQuickOutlinePage createQuickOutlinePage()
	{
		return sourceEditor.createQuickOutlinePage();
	}

	// public void matchPair()
	// {
	// sourceEditor.matchPair();
	// }
	//
	// public void selectPair()
	// {
	// sourceEditor.selectPair();
	// }
	//
	// public void selectContentPair()
	// {
	// sourceEditor.selectContentPair();
	// }

	/**
	 * getPairMatch
	 * 
	 * @param offset
	 * @return PairMatch
	 */
	public PairMatch getPairMatch(int offset)
	{
		return sourceEditor.getPairMatch(offset);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getParentDirectoryHint()
	 */
	public String getParentDirectoryHint()
	{
		return sourceEditor.getParentDirectoryHint();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#setParentDirectoryHint(java.lang.String)
	 */
	public void setParentDirectoryHint(String hint)
	{
		sourceEditor.setParentDirectoryHint(hint);
	}

	/**
	 * see com.aptana.ide.editors.unified.IUnifiedEditor#addSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 * 
	 * @param listener
	 */
	public void addSaveListener(ISaveEvent listener)
	{
		sourceEditor.addSaveListener(listener);
	}

	/**
	 * see
	 * com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 * 
	 * @param listener
	 */
	public void removeSaveListener(ISaveEvent listener)
	{
		sourceEditor.removeSaveListener(listener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getBaseContributor()
	 */
	public IUnifiedEditorContributor getBaseContributor()
	{
		return sourceEditor.getBaseContributor();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return sourceEditor.getDefaultFileExtension();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#addFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void addFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		sourceEditor.addFileServiceChangeListener(listener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#removeFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void removeFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		sourceEditor.removeFileServiceChangeListener(listener);
	}

	/**
	 * Gets the local preference store
	 * 
	 * @return - preference store
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return HTMLPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showPianoKeys(boolean)
	 */
	public void showPianoKeys(boolean state)
	{
		if (sourceEditor != null)
		{
			sourceEditor.showPianoKeys(state);
		}
	}

	/**
	 * Returns the internal editor part
	 * 
	 * @return - editor part
	 */
	public EditorPart getInnerEditor()
	{
		return editor;
	}

}
