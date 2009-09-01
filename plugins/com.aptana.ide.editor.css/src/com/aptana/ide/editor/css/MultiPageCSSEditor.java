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
package com.aptana.ide.editor.css;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.editors.ISaveAsEvent;
import com.aptana.ide.core.ui.editors.ISaveEvent;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.preferences.IPreferenceConstants;
import com.aptana.ide.editor.css.preview.CSSPreviewPropertyPage;
import com.aptana.ide.editor.css.preview.PreviewConfigurationPage;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.toolbar.ToolbarWidget;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileServiceChangeListener;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.views.outline.UnifiedOutlinePage;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;

/** The tabbed style CSS editor. */
public class MultiPageCSSEditor extends MultiPageEditorPart implements ITextEditor, ITextEditorExtension, IUnifiedEditor
{

	/** CSS source editor */
	private CSSEditor editor;
	private ToolbarWidget toolbar;
	private Composite displayArea;

	private SourceEditorSite _siteEditor;

	/**
	 * prevTempFile
	 */
	protected File prevTempFile = null;

	private IElementStateListener elementListener = new IElementStateListener()
	{

		public void elementMoved(Object originalElement, Object movedElement)
		{

		}

		public void elementDirtyStateChanged(Object element, boolean isDirty)
		{

		}

		public void elementDeleted(Object element)
		{
			if (element.equals(getEditorInput()))
			{
				IWorkbenchPartSite site = MultiPageCSSEditor.this.getSite();
				if (site == null)
				{
					return;
				}
				IWorkbenchWindow window = site.getWorkbenchWindow();
				if (window == null)
				{
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null)
				{
					return;
				}
				page.closeEditor(MultiPageCSSEditor.this, true);
			}
		}

		public void elementContentReplaced(Object element)
		{

		}

		public void elementContentAboutToBeReplaced(Object element)
		{

		}

	};

	private IPropertyListener propertyListener = new IPropertyListener()
	{

		public void propertyChanged(Object source, int propId)
		{
			if (propId == IEditorPart.PROP_INPUT && source instanceof CSSEditor)
			{
				IEditorInput newInput = ((CSSEditor) source).getEditorInput();
				if (newInput != null)
				{
					setInput(newInput);
					setPartName(newInput.getName());
					setTitleToolTip(newInput.getToolTipText());
				}
			}
		}

	};

	/** Manually created preview pages */
	private Map previews;

	private String url;

	/**
	 * Creates a new multi page css editor
	 */
	public MultiPageCSSEditor()
	{
		super();
		previews = new HashMap();
		editor = new CSSEditor();

	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 */
	protected IEditorSite createSite(IEditorPart editor)
	{
		this._siteEditor = new SourceEditorSite(this, editor, getEditorSite());
		return _siteEditor;
	}

	private void createPage0()
	{
		try
		{
			addPage(editor, getEditorInput());
			setPageText(0, " Source "); //$NON-NLS-1$
			setPartName(getEditorInput().getName());
			this.editor.addPropertyListener(propertyListener);
			this.editor.getDocumentProvider().addElementStateListener(elementListener);
		}
		catch (PartInitException e)
		{
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus()); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPageContainer(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createPageContainer(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		IPreferenceStore store = CSSPlugin.getDefault().getPreferenceStore();
		boolean show = store.getBoolean(IPreferenceConstants.SHOW_CSS_TOOLBAR);
		if (show)
		{
			toolbar = new ToolbarWidget(new String[] { CSSMimeType.MimeType }, new String[] { CSSMimeType.MimeType },
					CSSPlugin.getDefault().getPreferenceStore(), IPreferenceConstants.LINK_CURSOR_WITH_CSS_TOOLBAR_TAB,
					this);
			toolbar.createControl(displayArea);
		}
		Composite editorArea = new Composite(displayArea, SWT.NONE);
		editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout eaLayout = new GridLayout(1, true);
		eaLayout.marginHeight = 0;
		eaLayout.marginWidth = 0;
		editorArea.setLayout(new FillLayout());
		return editorArea;
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages()
	{

		getSite().setSelectionProvider(new MultiPageSelectionProvider(this));
		if (getContainer() instanceof CTabFolder)
		{
			final CTabFolder tabs = (CTabFolder) getContainer();

			tabs.addListener(SWT.Traverse, new Listener()
			{

				public void handleEvent(Event event)
				{
					if (tabs.getItemCount() == 1 && displayArea != null && !displayArea.isDisposed())
					{
						Composite parent = displayArea.getParent();
						if (parent != null && parent.getParent() != null)
						{
							if (event.keyCode == SWT.PAGE_UP)
							{
								parent.getParent().traverse(SWT.TRAVERSE_PAGE_PREVIOUS);
							}
							else if (event.keyCode == SWT.PAGE_DOWN)
							{
								parent.getParent().traverse(SWT.TRAVERSE_PAGE_NEXT);
							}
						}
					}
				}

			});

			Composite toolbar = new Composite(tabs, SWT.NONE);
			toolbar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END, GridData.VERTICAL_ALIGN_BEGINNING));
			GridLayout layout = new GridLayout(1, true);
			final ToolBar tb = new ToolBar(toolbar, SWT.FLAT);
			GridData tbData = new GridData(GridData.HORIZONTAL_ALIGN_END);
			tabs.setTabHeight(21);
			tb.setLayoutData(tbData);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			toolbar.setLayout(layout);
			tb.setLayout(layout);
			tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			final ToolItem configure = new ToolItem(tb, SWT.DROP_DOWN);
			configure.setImage(CSSPlugin.getImage("icons/configure.gif")); //$NON-NLS-1$
			configure.setToolTipText(Messages.MultiPageCSSEditor_TTP_ConfigureCSSPreview);
			configure.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					if (e.detail != SWT.ARROW)
					{
						PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
								.getActiveShell(), "com.aptana.ide.editor.css.preferences.PreviewPreferencePage", //$NON-NLS-1$
								new String[] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
						dialog.open();
					}
				}

			});
			final Menu menu = new Menu(tabs.getShell(), SWT.POP_UP);
			MenuItem editTemplate = new MenuItem(menu, SWT.PUSH);
			editTemplate.setText(Messages.MultiPageCSSEditor_LBL_EditDefaultPreviewTemplate);
			editTemplate.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
							.getActiveShell(), "com.aptana.ide.editor.css.preferences.PreviewPreferencePage", //$NON-NLS-1$
							new String[] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
					dialog.open();
				}

			});
			MenuItem editFileSettings = new MenuItem(menu, SWT.PUSH);
			if (getEditorInput() instanceof IFileEditorInput)
			{
				editFileSettings.setText(Messages.MultiPageCSSEditor_LB_FilePreviewSettings);
				editFileSettings.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						if (getEditorInput() instanceof IFileEditorInput)
						{
							IFile file = ((IFileEditorInput) getEditorInput()).getFile();
							PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(Display.getDefault()
									.getActiveShell(), file,
									"com.aptana.ide.editor.css.preview.cssPreviewPropertyPage", //$NON-NLS-1$
									new String[] { "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" }, null); //$NON-NLS-1$
							dialog.open();
						}
					}
				});
				MenuItem editProjectSettings = new MenuItem(menu, SWT.PUSH);
				editProjectSettings.setText(Messages.MultiPageCSSEditor_LBL_ProjectPreviewSettings);
				editProjectSettings.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						if (getEditorInput() instanceof IFileEditorInput)
						{
							IFile file = ((IFileEditorInput) getEditorInput()).getFile();
							PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(Display.getDefault()
									.getActiveShell(), file.getProject(),
									"com.aptana.ide.editor.css.preview.cssPreviewPropertyPage", //$NON-NLS-1$
									new String[] { "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" }, null); //$NON-NLS-1$
							dialog.open();
						}
					}
				});
			}
			MenuItem editWorkspaceSettings = new MenuItem(menu, SWT.PUSH);
			editWorkspaceSettings.setText(Messages.MultiPageCSSEditor_LBL_WorkspacePreviewSettings);
			editWorkspaceSettings.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
							.getActiveShell(), "com.aptana.ide.editor.css.preferences.PreviewPreferencePage", //$NON-NLS-1$
							new String[] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
					dialog.open();
				}

			});
			configure.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					if (e.detail == SWT.ARROW)
					{
						Rectangle rect = configure.getBounds();
						Point pt = new Point(rect.x, rect.y + rect.height);
						pt = tb.toDisplay(pt);
						menu.setLocation(pt.x, pt.y);
						menu.setVisible(true);
					}
					else
					{

					}
				}

			});

			tabs.setTopRight(toolbar, SWT.RIGHT);
		}
		createPage0();
		loadBrowsers();
	}

	// private void loadPreviewPages()
	// {
	// String name = CoreUIUtils.getPathFromEditorInput(getEditorInput());
	// if (name == null)
	// {
	// return;
	// }
	// String[] pNames = PreviewPageManager.getNames(name);
	// String[] pBrowsers = PreviewPageManager.getBrowsers(name);
	// String[] pUrls = PreviewPageManager.getURLs(name);
	// if (pNames.length == pBrowsers.length && pNames.length == pUrls.length)
	// {
	// for (int i = 0; i < pNames.length; i++)
	// {
	// PreviewConfigurationPage page = new PreviewConfigurationPage(MultiPageHTMLEditor.this);
	// page.createControl(getContainer());
	// page.setURL(pUrls[i]);
	// page.setTitle(pNames[i]);
	// IExtensionRegistry reg = Platform.getExtensionRegistry();
	// IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
	// IExtension[] extensions = ep.getExtensions();
	// ContributedBrowser browser = null;
	// for (int k = 0; k < extensions.length && browser == null; k++)
	// {
	// IConfigurationElement[] ce = extensions[k].getConfigurationElements();
	// for (int j = 0; j < ce.length && browser == null; j++)
	// {
	// String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
	// if (pBrowsers[i].equals(BrowserExtensionLoader.getBrowserLabel(ce[j])))
	// {
	// if (browserClass != null && name != null)
	// {
	// try
	// {
	// Object obj = ce[j].createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
	// if (obj instanceof ContributedBrowser)
	// {
	// browser = (ContributedBrowser) obj;
	// }
	// }
	// catch (CoreException e)
	// {
	// }
	// }
	// }
	//
	// }
	// }
	// if (browser != null)
	// {
	// page.setBrowser(browser, pBrowsers[i]);
	// page.showBrowserArea();
	// int index = addPage(page.getControl());
	// previews.put(Integer.toString(index), page);
	// page.setIndex(index);
	// setPageText(index, page.getTitle());
	// }
	// }
	// }
	// }

	/**
	 * Saves the preview pages
	 */
	// public void savePreviewsPages()
	// {
	// String name = CoreUIUtils.getPathFromEditorInput(getEditorInput());
	// if (name == null)
	// {
	// return;
	// }
	// Iterator iter = previews.values().iterator();
	// StringBuffer names = new StringBuffer("");
	// StringBuffer urls = new StringBuffer("");
	// StringBuffer browser = new StringBuffer("");
	// while (iter.hasNext())
	// {
	// PreviewConfigurationPage page = (PreviewConfigurationPage) iter.next();
	// names.append(page.getTitle() + PreviewPageManager.PREFERENCE_DELIMITER);
	// urls.append(page.getURL() + PreviewPageManager.PREFERENCE_DELIMITER);
	// browser.append(page.getBrowserLabel() + PreviewPageManager.PREFERENCE_DELIMITER);
	// }
	// PreviewPageManager.setBrowser(name, browser.toString());
	// PreviewPageManager.setName(name, names.toString());
	// PreviewPageManager.setURL(name, urls.toString());
	// }
	/**
	 * @param index
	 * @param title
	 */
	public void setPreviewPageText(int index, String title)
	{
		setPageText(index, title);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (editor != null)
		{
			if (editor.getDocumentProvider() != null)
			{
				editor.getDocumentProvider().removeElementStateListener(elementListener);
			}
			editor.removePropertyListener(propertyListener);
			editor.dispose();
		}

		if (previews != null && previews.size() != 0)
		{
			disposePreviews();
			previews = null;
		}

		if (this.toolbar != null)
		{
			this.toolbar.dispose();
		}

		editor = null;
		_siteEditor = null;

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		IEditorPart editor = getEditor(0);
		editor.doSave(monitor);
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs()
	{
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
	}

	/**
	 * gotoMarker
	 * 
	 * @param marker
	 */
	public void gotoMarker(IMarker marker)
	{
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
	{
		super.init(site, editorInput);
		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		final Command nextMultipageEditorTabCommand = commandService.getCommand("com.aptana.ide.editors.nextMultipageEditorTab"); //$NON-NLS-1$
		final Command previousMultipageEditorTabCommand = commandService.getCommand("com.aptana.ide.editors.previousMultipageEditorTab"); //$NON-NLS-1$
		IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
		handlerService.activateHandler(nextMultipageEditorTabCommand.getId(), 
		new AbstractHandler() {
			public Object execute(ExecutionEvent event)	throws ExecutionException {
				gotoNextMultipageEditorTab();
				return null;
			}
		});
		handlerService.activateHandler(previousMultipageEditorTabCommand.getId(), 
				new AbstractHandler() {
					public Object execute(ExecutionEvent event)	throws ExecutionException {
						gotoPreviousMultipageEditorTab();
						return null;
					}
				});
	}
	
	private void gotoNextMultipageEditorTab() {
		Composite comp = getContainer();
		if (comp instanceof CTabFolder) {
			CTabFolder tabFolder = (CTabFolder) comp;
			int itemCount = tabFolder.getItemCount();
			if (itemCount > 1) {
				int selectionIndex = tabFolder.getSelectionIndex();
				selectionIndex++;
				if (selectionIndex >= itemCount) {
					selectionIndex = 0;
				}
				setActivePage(selectionIndex);
			}
		}
	}

	private void gotoPreviousMultipageEditorTab() {
		Composite comp = getContainer();
		if (comp instanceof CTabFolder) {
			CTabFolder tabFolder = (CTabFolder) comp;
			int itemCount = tabFolder.getItemCount();
			if (itemCount > 1) {
				int selectionIndex = tabFolder.getSelectionIndex();
				selectionIndex--;
				if (selectionIndex < 0) {
					selectionIndex = itemCount -1;
				}
				setActivePage(selectionIndex);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * Change to the source editor, and move caret to the specified offset.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		setActivePage(0);
		editor.selectAndReveal(offset, 0);
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
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex)
	{
		super.pageChange(newPageIndex);
		if (newPageIndex > 0)
		{
			updatePreview();
			setToolbarVisible(false);
		}
		else if (newPageIndex == 0)
		{
			setToolbarVisible(true);
		}
	}

	private String getExternalPreviewUrl(IEditorInput input) throws CoreException
	{

		String url = null;
		FileEditorInput fei = (FileEditorInput) input;
		IFile file = fei.getFile();
		IProject project = file.getProject();
		url = file.getPersistentProperty(new QualifiedName("", CSSPreviewPropertyPage.CSS_PREVIEW_PATH)); //$NON-NLS-1$

		// If no file url, use project url if it exists
		if (url == null)
		{
			url = project.getPersistentProperty(new QualifiedName("", CSSPreviewPropertyPage.CSS_PREVIEW_PATH)); //$NON-NLS-1$
		}

		return url;
	}

	private void updatePreview()
	{
		String url = null;
		IEditorInput input = this.editor.getEditorInput();
		try
		{
			if (input instanceof FileEditorInput)
			{
				url = getExternalPreviewUrl(input);
			}

			if (url != null && !"".equals(url)) //$NON-NLS-1$
			{
				this.setBrowserURL(url);
			}
			else
			{
				boolean useTemplate = CSSPlugin.getDefault().getPreferenceStore().getBoolean(
						IPreferenceConstants.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE);
				if (!useTemplate)
				{
					url = CSSPlugin.getDefault().getPreferenceStore().getString(
							IPreferenceConstants.CSSEDITOR_BROWSER_URL_PREFERENCE);
					this.setBrowserURL(url);
				}
				else
				{
					IDocumentProvider docProvider = editor.getDocumentProvider();
					String css = docProvider.getDocument(input).get();
					IPreferenceStore store = CSSPlugin.getDefault().getPreferenceStore();
					String template = store.getString(IPreferenceConstants.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE);
					css = "<html>"+template + "<style>" + css + "</style></html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

					File tmpFile;
					tmpFile = writeTemporaryPreviewFile(editor, input, css, charset);
					String tmpUrl = CoreUIUtils.getURI(tmpFile, false);

					if (prevTempFile != null && prevTempFile.equals(tmpFile))
					{
						setBrowserURL(tmpUrl);
					}
					else
					{
						if (prevTempFile != null)
						{
							prevTempFile.delete();
						}
						prevTempFile = tmpFile;
						setBrowserURL(tmpUrl);
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), Messages.MultiPageCSSEditor_ERR_UnableToUpdatePreview, e);
		}
	}

	private File writeTemporaryPreviewFile(CSSEditor editor, IEditorInput input, String html, String charset)
			throws CoreException, FileNotFoundException, UnsupportedEncodingException
	{

		File tmpFile = editor.getTempFile();

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

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{

		// Context is just plugin ID + name of class. Matches contexts.xml file
		if (editor != null)
		{
			return editor.getAdapter(adapter);
		}
		else
		{
			return null;
		}
	}

	/** IEditorSite for the source editor. */
	private static class SourceEditorSite extends MultiPageEditorSite
	{

		private CSSEditor _editor = null;
		private IEditorSite _site;
		private ArrayList _menuExtenders;
		private boolean isDisposing = false;

		/**
		 * SourceEditorSite
		 * 
		 * @param multiPageEditor
		 * @param editor
		 * @param site
		 */
		public SourceEditorSite(MultiPageEditorPart multiPageEditor, IEditorPart editor, IEditorSite site)
		{
			super(multiPageEditor, editor);
			this._site = site;
			this._editor = (CSSEditor) editor;
		}

		/**
		 * @see org.eclipse.ui.part.MultiPageEditorSite#getId()
		 */
		public String getId()
		{
			return _site.getId();
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#getActionBarContributor()
		 */
		public IEditorActionBarContributor getActionBarContributor()
		{
			return _site.getActionBarContributor();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(java.lang.String,
		 *      org.eclipse.jface.action.MenuManager, org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider)
		{
			if (_editor != null)
			{
				if (_menuExtenders == null)
				{
					_menuExtenders = new ArrayList(1);
				}
				_menuExtenders.add(new PopupMenuExtender(menuId, menuManager, selectionProvider, _editor));
			}
		}

		/**
		 * @see org.eclipse.ui.part.MultiPageEditorSite#dispose()
		 */
		public void dispose()
		{
			if (isDisposing)
			{
				return;
			}
			isDisposing = true;

			super.dispose();

			if (_menuExtenders != null)
			{
				for (int i = 0; i < _menuExtenders.size(); i++)
				{
					((PopupMenuExtender) _menuExtenders.get(i)).dispose();
				}
				_menuExtenders = null;
			}

			_editor = null;
			if (_site != null && _site instanceof EditorSite)
			{
				((EditorSite) _site).dispose();
			}
			_site = null;

		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getKeyBindingService()
		 */
		public IKeyBindingService getKeyBindingService()
		{
			return _site.getKeyBindingService();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getPluginId()
		 */
		public String getPluginId()
		{
			return _site.getPluginId();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getRegisteredName()
		 */
		public String getRegisteredName()
		{
			return _site.getRegisteredName();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selProvider)
		{
			_site.registerContextMenu(menuManager, selProvider);
		}
	}

	private void setBrowserURL(String url)
	{
		String index = Integer.toString(this.getActivePage());
		if ((previews.containsKey(index)))
		{
			((PreviewConfigurationPage) previews.get(index)).setURL(url);
		}
		this.url = url;
	}

	/**
	 * Gets the url of this editor
	 * 
	 * @return - url
	 */
	public String getURL()
	{
		return this.url;
	}

	private void loadBrowsers()
	{
		previews.clear();
		List browserList = BrowserExtensionLoader.loadBrowsers();
		for (int j = 0; j < browserList.size(); j++)
		{
			IConfigurationElement element = (IConfigurationElement) browserList.get(j);
			String name = BrowserExtensionLoader.getBrowserLabel(element);
			String outlineClass = element.getAttribute(UnifiedEditorsPlugin.OUTLINE_ATTR);
			try
			{
				Object obj = element.createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
				if (obj instanceof ContributedBrowser)
				{
					ContributedBrowser browser = (ContributedBrowser) obj;
					PreviewConfigurationPage page = new PreviewConfigurationPage(this);
					page.createControl(getContainer());
					page.setBrowser(browser, name);
					page.setTitle(StringUtils.format(Messages.MultiPageCSSEditor_TTL_Preview, name));
					page.showBrowserArea();
					int index = addPage(page.getControl());
					previews.put(Integer.toString(index), page);
					page.setIndex(index);
					setPageText(index, StringUtils.SPACE + page.getTitle() + StringUtils.SPACE);
					browser.getControl().addKeyListener(new KeyAdapter()
					{
						public void keyReleased(KeyEvent e)
						{
							((CTabFolder) getContainer()).traverse(SWT.TRAVERSE_TAB_NEXT);
						}
					});

					if (outlineClass != null)
					{
						Object ol = element.createExecutableExtension(UnifiedEditorsPlugin.OUTLINE_ATTR);
						if (ol instanceof ContributedOutline)
						{
							ContributedOutline outline = (ContributedOutline) ol;
							browser.setOutline(outline);
							outline.setBrowser(browser);
							editor.getOutlinePage().addOutline(outline, name);
						}
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(CSSPlugin.getDefault(), StringUtils.format(Messages.MultiPageCSSEditor_ERR_UnableToCreateBrowserControl,
						name), e);
			}
			catch (Error e)
			{
				IdeLog.logError(CSSPlugin.getDefault(), StringUtils.format(Messages.MultiPageCSSEditor_ERR_UnableToCreateBrowserControl,
						name), e);
			}
		}
	}

	/**
	 * Saves the preview pages
	 */
	public void savePreviewsPages()
	{
		// Does nothing
	}

	private void disposePreviews()
	{
		Object[] _previews = previews.values().toArray();
		for (int i = 0; i < _previews.length; i++)
		{
			((PreviewConfigurationPage) _previews[i]).dispose();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#addFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void addFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		if (editor != null)
		{
			editor.addFileServiceChangeListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#close(boolean)
	 */
	public void close(boolean save)
	{
		if (editor != null)
		{
			editor.close(save);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getBaseContributor()
	 */
	public IUnifiedEditorContributor getBaseContributor()
	{
		if (editor != null)
		{
			return editor.getBaseContributor();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getConfiguration()
	 */
	public SourceViewerConfiguration getConfiguration()
	{
		if (editor != null)
		{
			return editor.getConfiguration();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		if (editor != null)
		{
			return editor.getContextAwareness();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		if (editor != null)
		{
			return editor.getDefaultFileExtension();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getDocumentProvider()
	 */
	public IDocumentProvider getDocumentProvider()
	{
		if (editor != null)
		{
			return editor.getDocumentProvider();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getEditor()
	 */
	public IEditorPart getEditor()
	{
		if (editor != null)
		{
			return editor.getEditor();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getFileContext()
	 */
	public EditorFileContext getFileContext()
	{
		if (editor != null)
		{
			return editor.getFileContext();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getOutlinePage()
	 */
	public UnifiedOutlinePage getOutlinePage()
	{
		if (editor != null)
		{
			return editor.getOutlinePage();
		}
		return null;
	}
	
	/**
     * {@inheritDoc}
     */
    public UnifiedQuickOutlinePage createQuickOutlinePage()
    {
        return editor.createQuickOutlinePage();
    }

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getPairMatch(int)
	 */
	public PairMatch getPairMatch(int offset)
	{
		if (editor != null)
		{
			return editor.getPairMatch(offset);
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getParentDirectoryHint()
	 */
	public String getParentDirectoryHint()
	{
		if (editor != null)
		{
			return editor.getParentDirectoryHint();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getViewer()
	 */
	public ISourceViewer getViewer()
	{
		if (editor != null)
		{
			return editor.getViewer();
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#removeFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void removeFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		if (editor != null)
		{
			editor.removeFileServiceChangeListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#selectAndReveal(int, int)
	 */
	public void selectAndReveal(int offset, int length)
	{
		if (editor != null)
		{
			editor.selectAndReveal(offset, length);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#setParentDirectoryHint(java.lang.String)
	 */
	public void setParentDirectoryHint(String hint)
	{
		if (editor != null)
		{
			editor.setParentDirectoryHint(hint);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showWhitespace(boolean)
	 */
	public void showWhitespace(boolean state)
	{
		if (editor != null)
		{
			editor.showWhitespace(state);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#addSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 */
	public void addSaveAsListener(ISaveAsEvent listener)
	{
		if (editor != null)
		{
			editor.addSaveAsListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#addSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 */
	public void addSaveListener(ISaveEvent listener)
	{
		if (editor != null)
		{
			editor.addSaveListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveAsListener(com.aptana.ide.core.ui.editors.ISaveAsEvent)
	 */
	public void removeSaveAsListener(ISaveAsEvent listener)
	{
		if (editor != null)
		{
			editor.removeSaveAsListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveEventListener#removeSaveListener(com.aptana.ide.core.ui.editors.ISaveEvent)
	 */
	public void removeSaveListener(ISaveEvent listener)
	{
		if (editor != null)
		{
			editor.removeSaveListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#doRevertToSaved()
	 */
	public void doRevertToSaved()
	{
		if (editor != null)
		{
			editor.doRevertToSaved();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getAction(java.lang.String)
	 */
	public IAction getAction(String actionId)
	{
		if (editor != null)
		{
			return editor.getAction(actionId);
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getHighlightRange()
	 */
	public IRegion getHighlightRange()
	{
		if (editor != null)
		{
			return editor.getHighlightRange();
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider()
	{
		if (editor != null)
		{
			return editor.getSelectionProvider();
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#isEditable()
	 */
	public boolean isEditable()
	{
		if (editor != null)
		{
			return editor.isEditable();
		}
		return false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#removeActionActivationCode(java.lang.String)
	 */
	public void removeActionActivationCode(String actionId)
	{
		if (editor != null)
		{
			editor.removeActionActivationCode(actionId);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#resetHighlightRange()
	 */
	public void resetHighlightRange()
	{
		if (editor != null)
		{
			editor.resetHighlightRange();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setAction(java.lang.String, org.eclipse.jface.action.IAction)
	 */
	public void setAction(String actionID, IAction action)
	{
		if (editor != null)
		{
			editor.setAction(actionID, action);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setActionActivationCode(java.lang.String, char, int, int)
	 */
	public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode,
			int activationStateMask)
	{
		if (editor != null)
		{
			editor.setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#setHighlightRange(int, int, boolean)
	 */
	public void setHighlightRange(int offset, int length, boolean moveCursor)
	{
		if (editor != null)
		{
			editor.setHighlightRange(offset, length, moveCursor);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showHighlightRangeOnly(boolean)
	 */
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly)
	{
		if (editor != null)
		{
			editor.showHighlightRangeOnly(showHighlightRangeOnly);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditor#showsHighlightRangeOnly()
	 */
	public boolean showsHighlightRangeOnly()
	{
		if (editor != null)
		{
			return editor.showsHighlightRangeOnly();
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showPianoKeys(boolean)
	 */
	public void showPianoKeys(boolean state)
	{
		if (editor != null)
		{
			editor.showPianoKeys(state);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#addRulerContextMenuListener(IMenuListener)
	 */
	public void addRulerContextMenuListener(IMenuListener listener)
	{
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#isEditorInputReadOnly()
	 */
	public boolean isEditorInputReadOnly()
	{
		if (editor == null)
		{
			return false;
		}

		return editor.isEditorInputReadOnly();
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#removeRulerContextMenuListener(IMenuListener)
	 */
	public void removeRulerContextMenuListener(IMenuListener listener)
	{
	}

	/**
	 * @see org.eclipse.ui.texteditor.ITextEditorExtension#setStatusField(IStatusField, String)
	 */
	public void setStatusField(IStatusField field, String category)
	{
		if (editor == null)
		{
			return;
		}

		editor.setStatusField(field, category);
	}
}
