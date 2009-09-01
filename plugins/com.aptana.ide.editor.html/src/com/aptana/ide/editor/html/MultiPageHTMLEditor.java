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
package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.html.preview.ContributedPreviewPage;
import com.aptana.ide.editor.html.preview.DefaultPreviewConfigurationPage;
import com.aptana.ide.editor.html.preview.HTMLPreviewPropertyPage;
import com.aptana.ide.editor.html.preview.IBrowserTabAdder;
import com.aptana.ide.editor.html.preview.IPreviewConfigurationPage;
import com.aptana.ide.editor.html.preview.PreviewConfigurationPage;
import com.aptana.ide.editor.html.preview.PreviewTabManager;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;

/** The tabbed style HTML editor. */
public class MultiPageHTMLEditor extends MultiPageEditorPart implements
		IHTMLEditorPart {

	/** HTML source editor */
	private HTMLSourceEditor editor;

	private IEditorInput _page0;
	/** wrapper */
	private HTMLEditor wrapper;
	private boolean isDisposing = false;
	private SourceEditorSite _siteEditor;

	/**
	 * Tab actions control - the following flags control wheather the tab
	 * actions are shown
	 */
	private boolean addPreviewCapable = true;
	private boolean removePreviewCapable = true;
	private boolean editPreviewCapable = true;

	/**
	 * Tab actions
	 */
	private ToolItem addToolItem;
	private MenuItem addMenuItem;
	private ToolItem removeToolItem;
	private MenuItem removeMenuItem;
	private ToolItem editToolItem;
	private MenuItem editMenuItem;
	private ToolItem refreshToolItem;
	private MenuItem refreshMenuItem;
	private MenuItem copyURLMenuItem;
	private MenuItem openInBrowserMenuItem;
	private MenuItem viewSourceMenuItem;

	private IElementStateListener elementListener = new IElementStateListener() {

		public void elementMoved(Object originalElement, Object movedElement) {

		}

		public void elementDirtyStateChanged(Object element, boolean isDirty) {

		}

		public void elementDeleted(Object element) {
			if (element.equals(getEditorInput())) {
				IWorkbenchPartSite site = MultiPageHTMLEditor.this.getSite();
				if (site == null) {
					return;
				}
				IWorkbenchWindow window = site.getWorkbenchWindow();
				if (window == null) {
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null) {
					return;
				}
				page.closeEditor(MultiPageHTMLEditor.this, true);
			}
		}

		public void elementContentReplaced(Object element) {

		}

		public void elementContentAboutToBeReplaced(Object element) {

		}

	};

	private IPropertyListener propertyListener = new IPropertyListener() {

		public void propertyChanged(Object source, int propId) {
			if (propId == IEditorPart.PROP_INPUT
					&& source instanceof HTMLSourceEditor) {
				IEditorInput newInput = ((HTMLSourceEditor) source)
						.getEditorInput();
				if (newInput != null) {
					setInput(newInput);
					setPartName(newInput.getName());
					setTitleToolTip(newInput.getToolTipText());
					updateSourceTabTooltip();
				}
			}
		}

	};

	/** Manually created preview pages */
	private Map<String, IPreviewConfigurationPage> previews;

	private String url;

	/**
	 * MultiPageHTMLEditor
	 * 
	 * @param wrapper
	 * @param editor
	 */
	public MultiPageHTMLEditor(HTMLEditor wrapper, HTMLSourceEditor editor) {
		super();
		previews = new TreeMap<String, IPreviewConfigurationPage>(
				new Comparator<String>() {

					public int compare(String o1, String o2) {
						int i1 = Integer.parseInt(o1);
						int i2 = Integer.parseInt(o2);
						return i1 - i2;
					}

				});
		if (wrapper == null) {
			throw new IllegalArgumentException(
					Messages.MultiPageHTMLEditor_WrapperCannotBeNull);
		}
		if (editor == null) {
			throw new IllegalArgumentException(
					Messages.MultiPageHTMLEditor_EditorCannotBeNull);
		}

		this.wrapper = wrapper;
		this.editor = editor;

	}

	/**
	 * Tab actions control - the following flags control wheather the tab
	 * actions are shown
	 */
	public boolean isAddPreviewCapable() {
		return addPreviewCapable;
	}

	public void setAddPreviewCapable(boolean addPreviewCapable) {
		this.addPreviewCapable = addPreviewCapable;
	}

	public boolean isRemovePreviewCapable() {
		return removePreviewCapable;
	}

	public void setRemovePreviewCapable(boolean removePreviewCapable) {
		this.removePreviewCapable = removePreviewCapable;
	}

	public boolean isEditPreviewCapable() {
		return editPreviewCapable;
	}

	public void setEditPreviewCapable(boolean editPreviewCapable) {
		this.editPreviewCapable = editPreviewCapable;
	}

	/**
	 * @param actionID
	 * @return - true if the action was run for the id
	 */
	public boolean run(String actionID) {
		if (IWorkbenchActionDefinitionIds.CUT.equals(actionID)
				|| IWorkbenchActionDefinitionIds.PASTE.equals(actionID)
				|| IWorkbenchActionDefinitionIds.COPY.equals(actionID)) {
			if (getActivePage() != 0) {
				IPreviewConfigurationPage page = previews.get(Integer
						.toString(getActivePage()));
				return page.run(actionID);
			}
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#getBrowser2()
	 */
	public Browser getBrowser2() {
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#getSourceEditor()
	 */
	public HTMLSourceEditor getSourceEditor() {
		return editor;
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 */
	protected IEditorSite createSite(IEditorPart editor) {
		this._siteEditor = new SourceEditorSite(this, editor, getEditorSite());
		return _siteEditor;
	}

	private void updateSourceTabTooltip() {
		if (getContainer() instanceof CTabFolder
				&& !getContainer().isDisposed()) {
			CTabFolder tabs = (CTabFolder) getContainer();
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				if (file != null) {
					IPath location = file.getLocation();
					if (location != null && !CoreUIUtils.onMacOSX) {
						tabs.getItem(0).setToolTipText(
								location.makeAbsolute().toOSString());
					}
				}

			}
		}
	}

	private void createPage0() {
		try {
			_page0 = getEditorInput();
			int index = addPage(editor, _page0);
			setPageText(index, " Source "); //$NON-NLS-1$
			setPartName(getEditorInput().getName());
			updateSourceTabTooltip();
			this.editor.addPropertyListener(propertyListener);
			this.editor.getDocumentProvider().addElementStateListener(
					elementListener);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus()); //$NON-NLS-1$
		}
	}

	private void createRefreshTabOption(ToolBar tb, Menu popupMenu) {
		refreshToolItem = new ToolItem(tb, SWT.PUSH);
		refreshToolItem.setImage(HTMLPlugin.getImage("icons/refresh.gif")); //$NON-NLS-1$
		refreshToolItem
				.setToolTipText(Messages.MultiPageHTMLEditor_RefreshActivePreview);
		SelectionAdapter addSelectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String active = Integer.toString(getActivePage());
				if (previews.containsKey(active)) {
					IPreviewConfigurationPage page = previews.get(active);
					if (getContainer() instanceof CTabFolder) {
						CTabFolder tabs = (CTabFolder) getContainer();
						if (page instanceof DefaultPreviewConfigurationPage) {
							tabs.getItem(Integer.parseInt(active)).setImage(
									null); //$NON-NLS-1$
						} else if (page.getTabImage() != null) {
							tabs.getItem(Integer.parseInt(active)).setImage(
									page.getTabImage()); //$NON-NLS-1$
						} else {
							tabs
									.getItem(Integer.parseInt(active))
									.setImage(
											HTMLPlugin
													.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
						}
					}
					page.refresh();
				}
			}
		};
		refreshToolItem.addSelectionListener(addSelectionAdapter);
		refreshToolItem.setEnabled(false);
		refreshMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		refreshMenuItem.addSelectionListener(addSelectionAdapter);
		refreshMenuItem
				.setText(Messages.MultiPageHTMLEditor_RefreshActivePreview);
		refreshMenuItem.setImage(HTMLPlugin.getImage("icons/refresh.gif")); //$NON-NLS-1$
		refreshMenuItem.setEnabled(false);
	}

	private void createAddTabOption(ToolBar tb, Menu popupMenu) {
		addToolItem = new ToolItem(tb, SWT.PUSH);
		addToolItem.setImage(HTMLPlugin.getImage("icons/add_tab.gif")); //$NON-NLS-1$
		addToolItem.setToolTipText(Messages.MultiPageHTMLEditor_AddNewPreview);
		SelectionAdapter addSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreviewConfigurationPage page = new PreviewConfigurationPage(
						MultiPageHTMLEditor.this);
				page.setType(HTMLPreviewPropertyPage.FILE_BASED_TYPE);
				page.setValue(null);
				page.createControl(getContainer());
				int index = addPage(page.getControl());
				Composite container = getContainer();
				if (container instanceof CTabFolder) {
					((CTabFolder) container).getItem(index).setImage(
							HTMLPlugin.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
				}
				previews.put(Integer.toString(index), page);
				page.setIndex(index);
				page.setTitle(page.generateNewPreviewName());
				setPageText(index, " " + page.getTitle() + " "); //$NON-NLS-1$ //$NON-NLS-2$
				setActivePage(index);
				page.showEditArea();
				if (isEditPreviewCapable()) {
					editToolItem.setEnabled(true);
					editMenuItem.setEnabled(true);
				}
				if (isRemovePreviewCapable()) {
					removeToolItem.setEnabled(true);
					removeMenuItem.setEnabled(true);
				}
			}
		};
		addToolItem.addSelectionListener(addSelectionAdapter);
		addMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		addMenuItem.addSelectionListener(addSelectionAdapter);
		addMenuItem.setText(Messages.MultiPageHTMLEditor_AddNewPreview);
		addMenuItem.setImage(HTMLPlugin.getImage("icons/add_tab.gif")); //$NON-NLS-1$
	}

	private void createEditTabOption(ToolBar tb, Menu popupMenu) {
		editToolItem = new ToolItem(tb, SWT.PUSH);
		editToolItem.setImage(HTMLPlugin.getImage("icons/edit.png")); //$NON-NLS-1$
		editToolItem
				.setToolTipText(Messages.MultiPageHTMLEditor_EditActivePreview);
		SelectionAdapter editSelectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String active = Integer.toString(getActivePage());
				if (previews.containsKey(active)) {
					IPreviewConfigurationPage page = previews.get(active);
					if (page instanceof DefaultPreviewConfigurationPage) {
						if (getEditorInput() instanceof IFileEditorInput) {
							IFile file = ((IFileEditorInput) getEditorInput())
									.getFile();
							PreferenceDialog dialog = PreferencesUtil
									.createPropertyDialogOn(
											Display.getDefault()
													.getActiveShell(),
											file.getProject(),
											"com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage", //$NON-NLS-1$
											new String[] { "com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage" }, //$NON-NLS-1$
											null);
							dialog.open();
						} else {
							PreferenceDialog dialog = PreferencesUtil
									.createPreferenceDialogOn(
											Display.getDefault()
													.getActiveShell(),
											"com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
											new String[] { "com.aptana.ide.editor.html.preferences.PreviewPreferencePage" }, //$NON-NLS-1$
											null);
							dialog.open();
						}
					} else if (!page.isReadOnly()) {
						page.showEditArea();
					}
				}
			}

		};
		editToolItem.addSelectionListener(editSelectionAdapter);
		editToolItem.setEnabled(false);
		editMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		editMenuItem.addSelectionListener(editSelectionAdapter);
		editMenuItem.setImage(HTMLPlugin.getImage("icons/edit.png")); //$NON-NLS-1$
		editMenuItem.setText(Messages.MultiPageHTMLEditor_EditActivePreview);
		editMenuItem.setEnabled(false);
	}

	private void createRemoveTabOption(ToolBar tb, Menu popupMenu) {
		removeToolItem = new ToolItem(tb, SWT.PUSH);
		removeToolItem.setImage(HTMLPlugin.getImage("icons/delete.gif")); //$NON-NLS-1$
		removeToolItem
				.setToolTipText(Messages.MultiPageHTMLEditor_RemoveActivePreview);
		SelectionAdapter removeSelectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				int pageIndex = getActivePage();
				String active = Integer.toString(pageIndex);
				previews.remove(active);
				for (int i = pageIndex + 1; i < getPageCount(); i++) {
					if (previews.containsKey(Integer.toString(i))) {
						IPreviewConfigurationPage obj = previews.remove(Integer
								.toString(i));
						previews.put(Integer.toString(i - 1), obj);
					}
				}
				removePage(pageIndex);
				savePreviewsPages();
			}

		};
		removeToolItem.addSelectionListener(removeSelectionAdapter);
		removeToolItem.setEnabled(false);
		removeMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		removeMenuItem.addSelectionListener(removeSelectionAdapter);
		removeMenuItem.setImage(HTMLPlugin.getImage("icons/delete.gif")); //$NON-NLS-1$
		removeMenuItem
				.setText(Messages.MultiPageHTMLEditor_RemoveActivePreview);
		removeMenuItem.setEnabled(false);
	}

	private void createCopyURLTabOption(Menu popupMenu) {
		SelectionAdapter copySelectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String active = Integer.toString(getActivePage());
				if (previews.containsKey(active)) {
					IPreviewConfigurationPage page = previews.get(active);
					Clipboard cb = new Clipboard(e.display);
					cb.setContents(new Object[] { page.getURL() },
							new Transfer[] { TextTransfer.getInstance() });
					cb.dispose();
				}
			}

		};
		copyURLMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		copyURLMenuItem.addSelectionListener(copySelectionAdapter);
		copyURLMenuItem.setImage(HTMLPlugin.getImage("icons/copy_edit.gif")); //$NON-NLS-1$
		copyURLMenuItem.setText(Messages.MultiPageHTMLEditor_CopyPreviewURL);
		copyURLMenuItem.setEnabled(false);
	}

	private void createOpenInBrowserTabOption(Menu popupMenu) {
		SelectionAdapter openSelectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String active = Integer.toString(getActivePage());
				if (previews.containsKey(active)) {
					IPreviewConfigurationPage page = previews.get(active);
					CoreUIUtils.openBrowserURL(page.getURL());
				}
			}

		};
		openInBrowserMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		openInBrowserMenuItem.addSelectionListener(openSelectionAdapter);
		openInBrowserMenuItem
				.setText(Messages.MultiPageHTMLEditor_OpenInExternalBrowser);
		openInBrowserMenuItem.setEnabled(false);
	}

	private void createViewSourceTabOption(Menu popupMenu) {
		SelectionAdapter viewSourceAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String active = Integer.toString(getActivePage());
				if (previews.containsKey(active)) {
					IPreviewConfigurationPage page = previews.get(active);
					page.viewSource();
				}
			}

		};
		viewSourceMenuItem = new MenuItem(popupMenu, SWT.PUSH);
		viewSourceMenuItem.addSelectionListener(viewSourceAdapter);
		viewSourceMenuItem.setText(Messages.MultiPageHTMLEditor_LBL_ViewSource);
		viewSourceMenuItem.setEnabled(false);
	}

	/*
	 * private void createPage2() { if(isFileEditorInput()){ browser2 = new
	 * Browser(getContainer(),SWT.NONE); browser2.addLocationListener(new
	 * LocationListener() { public void changing(LocationEvent event) { } public
	 * void changed(LocationEvent event) {
	 * if(event.location.endsWith("edit.htm")) wrapper.updateEdit(); } }); int
	 * index = addPage(browser2); setPageText(index, "Edit"); //$NON-NLS-1$ } }
	 */

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getActivePage()
	 */
	public int getActivePage() {
		return super.getActivePage();
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages() {
		// The following code enables the new preview model which allows pages
		// to be added and removed
		// It was commented out since it was not part of 1.0 and will go out for
		// 1.1

		// getSite().setSelectionProvider(new MultiPageSelectionProvider(this));
		if (getContainer() instanceof CTabFolder) {
			final CTabFolder tabs = (CTabFolder) getContainer();
			
			registerTabItemTraversal(getSite(), tabs);

			Menu popupMenu = new Menu(tabs);
			Composite toolbar = new Composite(tabs, SWT.NONE);
			toolbar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
					false));
			GridLayout layout = new GridLayout(2, false);
			ToolBar tb = new ToolBar(toolbar, SWT.FLAT);
			tb.setMenu(popupMenu);
			tabs.setTabHeight(21);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			toolbar.setLayout(layout);
			tb.setLayout(layout);
			tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			if (isAddPreviewCapable()) {
				createAddTabOption(tb, popupMenu);
			}
			if (isEditPreviewCapable()) {
				createEditTabOption(tb, popupMenu);
			}
			if (isRemovePreviewCapable()) {
				createRemoveTabOption(tb, popupMenu);
			}
			createRefreshTabOption(tb, popupMenu);
			createCopyURLTabOption(popupMenu);
			createOpenInBrowserTabOption(popupMenu);
			createViewSourceTabOption(popupMenu);
			tabs.setMenu(popupMenu);

			tabs.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					String active = Integer.toString(MultiPageHTMLEditor.this.getActivePage());
					if (previews.containsKey(active)) {
						IPreviewConfigurationPage page = previews.get(active);
						if (isRemovePreviewCapable()) {
							removeToolItem.setEnabled(page.isDeletable());
							removeMenuItem.setEnabled(page.isDeletable());
						}
						if (isEditPreviewCapable()) {
							editToolItem
									.setEnabled(page instanceof DefaultPreviewConfigurationPage
											|| !page.isReadOnly());
							editMenuItem.setEnabled(true);
						}
						copyURLMenuItem.setEnabled(true);
						openInBrowserMenuItem.setEnabled(true);
						viewSourceMenuItem.setEnabled(true);
						refreshMenuItem.setEnabled(true);
						refreshToolItem.setEnabled(true);
					} else {
						viewSourceMenuItem.setEnabled(false);
						if (isRemovePreviewCapable()) {
							removeToolItem.setEnabled(false);
							removeMenuItem.setEnabled(false);
						}
						if (isEditPreviewCapable()) {
							editToolItem.setEnabled(false);
							editMenuItem.setEnabled(false);
						}
						copyURLMenuItem.setEnabled(false);
						openInBrowserMenuItem.setEnabled(false);
						refreshMenuItem.setEnabled(false);
						refreshToolItem.setEnabled(false);
					}
				}

			});

			Composite config = new Composite(toolbar, SWT.NONE);
			config
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
							false));
			layout = new GridLayout(1, false);
			final ToolBar configTb = new ToolBar(config, SWT.FLAT);
			GridData tbData = new GridData(GridData.HORIZONTAL_ALIGN_END);
			configTb.setLayoutData(tbData);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			config.setLayout(layout);
			configTb.setLayout(layout);
			final ToolItem configure = new ToolItem(configTb, SWT.DROP_DOWN);
			configure.setImage(CSSPlugin.getImage("icons/configure.gif")); //$NON-NLS-1$
			configure
					.setToolTipText(Messages.MultiPageHTMLEditor_ConfigureHTMLPreview);
			configure.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (e.detail != SWT.ARROW) {
						if (getEditorInput() instanceof IFileEditorInput) {
							IFile file = ((IFileEditorInput) getEditorInput())
									.getFile();
							PreferenceDialog dialog = PreferencesUtil
									.createPropertyDialogOn(
											Display.getDefault()
													.getActiveShell(),
											file.getProject(),
											"com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage", //$NON-NLS-1$
											new String[] { "com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage" }, //$NON-NLS-1$
											null);
							dialog.open();
						} else {
							PreferenceDialog dialog = PreferencesUtil
									.createPreferenceDialogOn(
											Display.getDefault()
													.getActiveShell(),
											"com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
											new String[] { "com.aptana.ide.editor.html.preferences.PreviewPreferencePage" }, //$NON-NLS-1$
											null);
							dialog.open();
						}
					}
				}

			});
			final Menu menu = new Menu(tabs.getShell(), SWT.POP_UP);
			if (getEditorInput() instanceof IFileEditorInput) {
				MenuItem editProjectSettings = new MenuItem(menu, SWT.PUSH);
				editProjectSettings
						.setText(Messages.MultiPageHTMLEditor_ProjectPreviewSettings);
				editProjectSettings
						.addSelectionListener(new SelectionAdapter() {

							public void widgetSelected(SelectionEvent e) {
								if (getEditorInput() instanceof IFileEditorInput) {
									IFile file = ((IFileEditorInput) getEditorInput())
											.getFile();
									PreferenceDialog dialog = PreferencesUtil
											.createPropertyDialogOn(
													Display.getDefault()
															.getActiveShell(),
													file.getProject(),
													"com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage", //$NON-NLS-1$
													new String[] { "com.aptana.ide.editor.html.preview.htmlPreviewPropertyPage" }, //$NON-NLS-1$
													null);
									dialog.open();
								}
							}
						});
			}
			MenuItem editWorkspaceSettings = new MenuItem(menu, SWT.PUSH);
			editWorkspaceSettings
					.setText(Messages.MultiPageHTMLEditor_WorkspacePreviewSettings);
			editWorkspaceSettings.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					PreferenceDialog dialog = PreferencesUtil
							.createPreferenceDialogOn(
									Display.getDefault().getActiveShell(),
									"com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
									new String[] { "com.aptana.ide.editor.html.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
					dialog.open();
				}

			});
			MenuItem restoreDefaults = new MenuItem(menu, SWT.PUSH);
			restoreDefaults.setImage(HTMLPlugin
					.getImage("icons/restore_defaults.gif")); //$NON-NLS-1$
			restoreDefaults
					.setText(Messages.MultiPageHTMLEditor_RestoreTabDefaults);
			restoreDefaults.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					while (getPageCount() > 1) {
						removePage(1);
					}
					IEditorInput input = getEditorInput();
					if (input instanceof IFileEditorInput) {
						IFile file = ((IFileEditorInput) input).getFile();
						try {
							file
									.setPersistentProperty(
											new QualifiedName(
													"", //$NON-NLS-1$
													HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_BROWSERS),
											""); //$NON-NLS-1$
							file
									.setPersistentProperty(
											new QualifiedName(
													"", //$NON-NLS-1$
													HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_URLS),
											""); //$NON-NLS-1$
							file
									.setPersistentProperty(
											new QualifiedName(
													"", //$NON-NLS-1$
													HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_NAMES),
											""); //$NON-NLS-1$
						} catch (CoreException e1) {
							IdeLog.logInfo(HTMLPlugin.getDefault(),
									"Error saving preview page add-ons", e1); //$NON-NLS-1$
						}
					}
					previews.clear();
					loadPreviewPages();
					savePreviewsPages();
				}

			});
			configure.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (e.detail == SWT.ARROW) {
						Rectangle rect = configure.getBounds();
						Point pt = new Point(rect.x, rect.y + rect.height);
						pt = configTb.toDisplay(pt);
						menu.setLocation(pt.x, pt.y);
						menu.setVisible(true);
					}
				}

			});

			tabs.setTopRight(toolbar, SWT.FILL);
		}
		createPage0();
		// Obtain preview pages from preferences and build
		UIJob job = new UIJob(Messages.MultiPageHTMLEditor_Job_LoadingPreview) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				loadPreviewPages();
				wrapper.updatePreview();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	private static void registerTabItemTraversal(IWorkbenchPartSite site, CTabFolder tabs) {

		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
		IBindingService bindingService = (IBindingService) site.getService(IBindingService.class);
		IContextService contextService = (IContextService) site.getService(IContextService.class);
		
		
	}

	private void showFirefoxSafariConflictMessage() {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(),
				Messages.MultiPageHTMLEditor_EmbeddedFirefoxLoadingIssue,
				Messages.MultiPageHTMLEditor_FirefoxBrowserCantBeFirst);
	}

	private void showFirefoxSafariConflictMessageDuringSave(
			IPreviewConfigurationPage page) {
		MessageDialog
				.openInformation(
						PlatformUI.getWorkbench().getDisplay().getActiveShell(),
						Messages.MultiPageHTMLEditor_EmbeddedFirefoxLoadingIssue,
						StringUtils
								.format(
										Messages.MultiPageHTMLEditor_FirefoxBrowserCantBeFirstSave,
										page.getTitle()));
	}

	private void loadPreviewPages() {
		List<IConfigurationElement> browserList = null;
		IFile file = null;
		if (getEditorInput() instanceof FileEditorInput) {
			file = ((FileEditorInput) getEditorInput()).getFile();
			String override;
			try {
				override = file
						.getProject()
						.getPersistentProperty(
								new QualifiedName(
										"", HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE)); //$NON-NLS-1$
				if (HTMLPreviewPropertyPage.TRUE.equals(override)) {
					String browserString = file
							.getProject()
							.getPersistentProperty(
									new QualifiedName(
											"", HTMLPreviewPropertyPage.HTML_PREVIEW_BROWSERS)); //$NON-NLS-1$
					if (browserString == null) {
						browserString = ""; //$NON-NLS-1$
					}
					String[] browsers_names = browserString.split(","); //$NON-NLS-1$
					browserList = BrowserExtensionLoader
							.getBrowsers(browsers_names);
					Collections.sort(browserList,
							new Comparator<IConfigurationElement>() {

								public int compare(IConfigurationElement o1,
										IConfigurationElement o2) {
									String name1 = BrowserExtensionLoader
											.getBrowserLabel(o1);
									String name2 = BrowserExtensionLoader
											.getBrowserLabel(o2);
									int compare = 0;
									if (name1 != null && name2 != null) {
										// This is to put the browsers in
										// reverse alphabetical order so Firefox
										// is always last
										// since
										// that affects loading on Mac OS X.
										// Basically 'Safari' must come before
										// 'Firefox'
										if (CoreUIUtils.onMacOSX) {
											return name2.compareTo(name1);
										} else {
											compare = name1.compareTo(name2);
										}
									}
									return compare;
								}
							});
				} else {
					browserList = BrowserExtensionLoader.loadBrowsers();
				}
			} catch (CoreException e) {
				browserList = BrowserExtensionLoader.loadBrowsers();
			}

		} else {
			browserList = BrowserExtensionLoader.loadBrowsers();
		}

		for (int j = 0; j < browserList.size(); j++) {
			IConfigurationElement element = (IConfigurationElement) browserList
					.get(j);
			String name = BrowserExtensionLoader.getBrowserLabel(element);
			IdeLog.logInfo(HTMLPlugin.getDefault(), StringUtils.format(
					Messages.MultiPageHTMLEditor_INF_LoadingPreview, name));

			String outlineClass = element
					.getAttribute(UnifiedEditorsPlugin.OUTLINE_ATTR);

			try {
				Object obj = element
						.createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
				if (obj instanceof ContributedBrowser) {
					ContributedBrowser browser = (ContributedBrowser) obj;
					if (CoreUIUtils.onMacOSX
							&& j == 0
							&& browser.getBrowserType().indexOf("Firefox") != -1) //$NON-NLS-1$
					{
						showFirefoxSafariConflictMessage();
						IdeLog
								.logInfo(
										HTMLPlugin.getDefault(),
										StringUtils
												.format(
														Messages.MultiPageHTMLEditor_INF_SkippingTab,
														name));
					} else {
						DefaultPreviewConfigurationPage page = new DefaultPreviewConfigurationPage(
								this);
						page.setTitle(name);
						page.createControl(getContainer());
						page.setBrowser(browser, name);
						page.showBrowserArea();
						int index = addPage(page.getControl());
						previews.put(Integer.toString(index), page);
						page.setIndex(index);
						setPageText(index, " " + page.getTitle() + " "); //$NON-NLS-1$ //$NON-NLS-2$
						IdeLog
								.logInfo(
										HTMLPlugin.getDefault(),
										StringUtils
												.format(
														Messages.MultiPageHTMLEditor_INF_LoadedPreview,
														name));

						if (outlineClass != null) {
							Object ol = element
									.createExecutableExtension(UnifiedEditorsPlugin.OUTLINE_ATTR);
							if (ol instanceof ContributedOutline) {
								ContributedOutline outline = (ContributedOutline) ol;
								browser.setOutline(outline);
								outline.setBrowser(browser);
								editor.getOutlinePage().addOutline(outline,
										name);
							}
						}
					}
				}
			} catch (Exception e) {
				IdeLog
						.logError(
								HTMLPlugin.getDefault(),
								StringUtils
										.format(
												Messages.MultiPageHTMLEditor_UnableToCreateBrowserControl,
												name), e);
			} catch (Error e) {
				IdeLog
						.logError(
								HTMLPlugin.getDefault(),
								StringUtils
										.format(
												Messages.MultiPageHTMLEditor_UnableToCreateBrowserControl,
												name), e);
			}
		}
		for (IBrowserTabAdder adder : PreviewTabManager.getManager()
				.getTabAdders()) {
			try {
				IPreviewConfigurationPage[] pages = adder.getAddOnTabs(this,
						getContainer());
				if (pages != null) {
					for (IPreviewConfigurationPage page : pages) {
						IdeLog
								.logInfo(
										HTMLPlugin.getDefault(),
										StringUtils
												.format(
														Messages.MultiPageHTMLEditor_INF_LoadingFromExt,
														page.getTitle()));
						addPreviewTab(page);
					}
				}
			} catch (Exception e) {
				IdeLog.logError(HTMLPlugin.getDefault(),
						Messages.MultiPageHTMLEditor_ERR_CreateTab, e);
			} catch (Error e) {
				IdeLog.logError(HTMLPlugin.getDefault(),
						Messages.MultiPageHTMLEditor_ERR_CreateTab, e);
			}
		}
		for (ContributedPreviewPage page : PreviewTabManager.getManager()
				.getStaticTabs()) {
			IdeLog.logInfo(HTMLPlugin.getDefault(), StringUtils.format(
					Messages.MultiPageHTMLEditor_INF_LoadingStaticContent, page
							.getTitle()));
			page.setEditor(this);
			page.createControl(getContainer());
			page.showBrowserArea();
			int index = addPage(page.getControl());
			previews.put(Integer.toString(index), page);
			page.setIndex(index);
			Composite container = getContainer();
			if (container instanceof CTabFolder) {
				if (page.getTabImage() != null) {
					((CTabFolder) container).getItem(index).setImage(
							page.getTabImage());
				} else {
					((CTabFolder) container).getItem(index).setImage(
							HTMLPlugin.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
				}
			}
			setPageText(index, " " + page.getTitle() + " "); //$NON-NLS-1$ //$NON-NLS-2$
			IdeLog.logInfo(HTMLPlugin.getDefault(), StringUtils.format(
					Messages.MultiPageHTMLEditor_INF_LoadedStaticContent, page
							.getTitle()));
		}
		if (file != null) {
			// Add add-on browsers created at the file level
			try {
				String addOnBrowsers = file
						.getPersistentProperty(new QualifiedName(
								"", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_BROWSERS));
				String addOnURLs = file
						.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_URLS));
				String addOnNames = file
						.getPersistentProperty(new QualifiedName(
								"", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_NAMES));
				String addOnTypes = file
						.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE));
				String addOnServers = file
						.getPersistentProperty(new QualifiedName(
								"", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_SERVER_ID));
				String addOnConfigs = file
						.getPersistentProperty(new QualifiedName(
								"", //$NON-NLS-1$
								HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_CONFIG_ID));
				if (addOnBrowsers != null && addOnURLs != null
						&& addOnNames != null && addOnTypes != null
						&& addOnServers != null && addOnConfigs != null) {
					String[] browsers = addOnBrowsers
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					String[] names = addOnNames
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					String[] types = addOnTypes
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					String[] servers = addOnServers
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					String[] configs = addOnConfigs
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					String[] urls = addOnURLs
							.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);

					if (browsers.length == names.length
							&& browsers.length == urls.length
							&& browsers.length == servers.length
							&& browsers.length == configs.length
							&& browsers.length == types.length) {
						browserList = BrowserExtensionLoader
								.getBrowsers(browsers);
						for (int i = 0; i < browsers.length; i++) {
							String name = names[i];
							String browser = browsers[i];
							String type = types[i];
							String value = null;
							if (HTMLPreviewPropertyPage.CONFIG_BASED_TYPE
									.equals(type)) {
								value = configs[i];
							} else if (HTMLPreviewPropertyPage.SERVER_BASED_TYPE
									.equals(type)
									|| HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE
											.equals(type)) {
								value = servers[i];
							} else if (HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE
									.equals(type)
									|| HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE
											.equals(type)) {
								value = urls[i];
							}
							IConfigurationElement element = null;
							for (int j = 0; j < browserList.size(); j++) {
								IConfigurationElement curr = (IConfigurationElement) browserList
										.get(j);
								String browserName = BrowserExtensionLoader
										.getBrowserLabel(curr);
								if (browser != null
										&& browser.equals(browserName)) {
									element = curr;
									break;
								}
							}
							if (element != null) {
								try {
									Object obj = element
											.createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
									if (obj instanceof ContributedBrowser) {
										ContributedBrowser cb = (ContributedBrowser) obj;
										if (getPageCount() == 1
												&& CoreUIUtils.onMacOSX
												&& i == 0
												&& cb.getBrowserType().indexOf(
														"Firefox") != -1) //$NON-NLS-1$
										{
											showFirefoxSafariConflictMessage();
										} else {
											PreviewConfigurationPage page = new PreviewConfigurationPage(
													this);
											IdeLog
													.logInfo(
															HTMLPlugin
																	.getDefault(),
															StringUtils
																	.format(
																			Messages.MultiPageHTMLEditor_INF_LoadingFile,
																			page
																					.getTitle()));
											page.setType(type);
											page.setValue(value);
											page.setTitle(name);
											page.createControl(getContainer());
											page.setBrowser(cb, browser);
											page.showBrowserArea();
											int index = addPage(page
													.getControl());
											Composite container = getContainer();
											if (container instanceof CTabFolder) {
												((CTabFolder) container)
														.getItem(index)
														.setImage(
																HTMLPlugin
																		.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
											}
											previews.put(Integer
													.toString(index), page);
											page.setIndex(index);
											setPageText(index, page.getTitle()
													+ " "); //$NON-NLS-1$
											IdeLog
													.logInfo(
															HTMLPlugin
																	.getDefault(),
															StringUtils
																	.format(
																			Messages.MultiPageHTMLEditor_INF_LoadedFile,
																			page
																					.getTitle()));
										}
									}
								} catch (Exception e) {
									IdeLog
											.logError(
													HTMLPlugin.getDefault(),
													StringUtils
															.format(
																	Messages.MultiPageHTMLEditor_UnableToCreateBrowserControl,
																	name), e);
								} catch (Error e) {
									IdeLog
											.logError(
													HTMLPlugin.getDefault(),
													StringUtils
															.format(
																	Messages.MultiPageHTMLEditor_UnableToCreateBrowserControl,
																	name), e);
								}
							}
						}
					}
				}
			} catch (CoreException e) {
				IdeLog.logError(HTMLPlugin.getDefault(),
						Messages.MultiPageHTMLEditor_ERR_CreateFileTab, e);
			}
		}
	}

	/**
	 * Adds a preview tab to the editor
	 * 
	 * @param page
	 */
	public void addPreviewTab(IPreviewConfigurationPage page) {
		if (page != null) {
			int index = addPage(page.getControl());
			previews.put(Integer.toString(index), page);
			page.setIndex(index);
			Composite container = getContainer();
			if (container instanceof CTabFolder) {
				if (page.getTabImage() != null) {
					((CTabFolder) container).getItem(index).setImage(
							page.getTabImage());
				} else {
					((CTabFolder) container).getItem(index).setImage(
							HTMLPlugin.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
				}
			}
			setPageText(index, " " + page.getTitle() + " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getContainer()
	 */
	public Composite getContainer() {
		return super.getContainer();
	}

	/**
	 * Gets the preview pages for the editor
	 * 
	 * @return - current editor preview pages
	 */
	public IPreviewConfigurationPage[] getPreviewPages() {
		return this.previews.values().toArray(new IPreviewConfigurationPage[0]);
	}

	/**
	 * Saves the preview pages
	 */
	public void savePreviewsPages() {
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			Iterator<IPreviewConfigurationPage> iter = previews.values()
					.iterator();

			List<PreviewConfigurationPage> previews = new ArrayList<PreviewConfigurationPage>();
			List<PreviewConfigurationPage> addPreviews = new ArrayList<PreviewConfigurationPage>();

			while (iter.hasNext()) {
				IPreviewConfigurationPage page = iter.next();
				if (page instanceof PreviewConfigurationPage) {
					if (CoreUIUtils.onMacOSX && isFirefoxBrowser(page)) {
						addPreviews.add((PreviewConfigurationPage) page);
						IdeLog
								.logInfo(
										HTMLPlugin.getDefault(),
										StringUtils
												.format(
														Messages.MultiPageHTMLEditor_INF_MovingTab,
														page.getTitle()));
					} else {
						previews.add((PreviewConfigurationPage) page);
						IdeLog
								.logInfo(
										HTMLPlugin.getDefault(),
										StringUtils
												.format(
														Messages.MultiPageHTMLEditor_INF_SavingTab,
														page.getTitle()));
					}
				} else {
					IdeLog.logInfo(HTMLPlugin.getDefault(), StringUtils.format(
							Messages.MultiPageHTMLEditor_INF_SkippingSave, page
									.getTitle()));
				}
			}

			previews.addAll(addPreviews);

			IFile file = ((IFileEditorInput) input).getFile();
			StringBuffer names = new StringBuffer(""); //$NON-NLS-1$
			StringBuffer browser = new StringBuffer(""); //$NON-NLS-1$
			StringBuffer types = new StringBuffer(""); //$NON-NLS-1$
			StringBuffer servers = new StringBuffer(""); //$NON-NLS-1$
			StringBuffer configs = new StringBuffer(""); //$NON-NLS-1$
			StringBuffer absolutes = new StringBuffer(""); //$NON-NLS-1$

			for (PreviewConfigurationPage pcp : previews) {
				String type = pcp.getType();
				String value = pcp.getValue();
				if (HTMLPreviewPropertyPage.SERVER_BASED_TYPE.equals(type)
						|| HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE
								.equals(type)) {
					servers.append(value
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					configs.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					absolutes.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				} else if (HTMLPreviewPropertyPage.CONFIG_BASED_TYPE
						.equals(type)) {
					servers.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					configs.append(value
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					absolutes.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				} else if (HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE
						.equals(type)) {
					servers.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					configs.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					absolutes.append(value
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				} else {
					servers.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					configs.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					absolutes.append(HTMLPreviewPropertyPage.INVALID
							+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				}
				types.append(type
						+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				names.append(pcp.getTitle()
						+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
				browser.append(pcp.getBrowserLabel()
						+ HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
			}
			try {
				file
						.setPersistentProperty(
								new QualifiedName(
										"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_BROWSERS), //$NON-NLS-1$
								browser.toString());
				file.setPersistentProperty(new QualifiedName(
						"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_NAMES), //$NON-NLS-1$
						names.toString());
				file.setPersistentProperty(new QualifiedName(
						"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE), //$NON-NLS-1$
						types.toString());
				file.setPersistentProperty(new QualifiedName(
						"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_URLS), //$NON-NLS-1$
						absolutes.toString());
				file
						.setPersistentProperty(
								new QualifiedName(
										"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_CONFIG_ID), //$NON-NLS-1$
								configs.toString());
				file
						.setPersistentProperty(
								new QualifiedName(
										"", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_SERVER_ID), //$NON-NLS-1$
								servers.toString());
			} catch (CoreException e) {
				IdeLog.logInfo(HTMLPlugin.getDefault(),
						"Error saving preview page add-ons", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Is this browser a firefox-based browser?
	 * 
	 * @param page
	 * @return
	 */
	private boolean isFirefoxBrowser(IPreviewConfigurationPage page) {
		String type = page.getBrowserLabel();
		String classType = page.getBrowserType();

		if ("Firefox".equals(type) || (classType != null && classType.indexOf("Firefox") != -1)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param index
	 * @param title
	 */
	public void setPreviewPageText(int index, String title) {
		setPageText(index, title);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		if (isDisposing) {
			return;
		}
		isDisposing = true;

		// We are not guaranteed to get the same temp file
		// everytime because of addition of random number
		// to fix STU-2094
		//
		// if (isFileEditorInput())
		// {
		// File tmpFile = editor.getTempFile();
		// if (tmpFile.exists())
		// {
		// tmpFile.delete();
		// }
		// }

		if (wrapper != null) {
			wrapper.dispose();
		}
		if (_siteEditor != null) {
			_siteEditor.dispose();
		}
		if (editor != null) {
			if (editor.getDocumentProvider() != null) {
				editor.getDocumentProvider().removeElementStateListener(
						elementListener);
			}
			editor.removePropertyListener(propertyListener);
			editor.dispose();
		}

		if (previews != null && previews.size() != 0) {
			disposePreviews();
			previews = null;
		}
		// if(_page0 != null)
		// _page0.dispose();

		_page0 = null;
		editor = null;
		wrapper = null;
		_siteEditor = null;

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		IEditorPart editor = getEditor(0);
		editor.doSave(monitor);
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
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
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)ccccc
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
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
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#isFileEditorInput()
	 */
	public boolean isFileEditorInput() {
		return editor.isFileEditorInput();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (newPageIndex == 0) {
			wrapper.getSite().setSelectionProvider(
					editor.getSelectionProvider());
			wrapper.updateSource();
			wrapper.setToolbarVisible(true);
			Iterator<IPreviewConfigurationPage> pages = previews.values()
					.iterator();
			while (pages.hasNext()) {
				pages.next().clearBrowser();
			}
		} else {
			getSite()
					.setSelectionProvider(new MultiPageSelectionProvider(this));
			wrapper.updatePreview();
			wrapper.setToolbarVisible(false);
		}
	}

	/**
	 * Change to the source editor, and move caret to the specified offset.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		setActivePage(0);
		editor.selectAndReveal(offset, 0);
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {

		// Context is just plugin ID + name of class. Matches contexts.xml file
		if (editor != null) {
			return editor.getAdapter(adapter);
		} else {
			IdeLog.logError(HTMLPlugin.getDefault(), StringUtils.format(
					Messages.MultiPageHTMLEditor_EditorIsNull, adapter
							.toString()));
			return null;
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#firePropertyChange(int)
	 */
	protected void firePropertyChange(int propertyId) {
		super.firePropertyChange(propertyId);
		wrapper.firePropertyChange2(propertyId);
	}

	/** IEditorSite for the source editor. */
	private static class SourceEditorSite extends MultiPageEditorSite {

		private HTMLSourceEditor _editor = null;
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
		public SourceEditorSite(MultiPageEditorPart multiPageEditor,
				IEditorPart editor, IEditorSite site) {
			super(multiPageEditor, editor);
			this._site = site;
			this._editor = (HTMLSourceEditor) editor;
		}

		/**
		 * @see org.eclipse.ui.part.MultiPageEditorSite#getId()
		 */
		public String getId() {
			return _site.getId();
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#getActionBarContributor()
		 */
		public IEditorActionBarContributor getActionBarContributor() {
			return _site.getActionBarContributor();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(java.lang.String,
		 *      org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(String menuId, MenuManager menuManager,
				ISelectionProvider selectionProvider) {
			if (_editor != null) {
				if (_menuExtenders == null) {
					_menuExtenders = new ArrayList(1);
				}
				_menuExtenders.add(new PopupMenuExtender(menuId, menuManager,
						selectionProvider, _editor));
			}
		}

		/**
		 * @see org.eclipse.ui.part.MultiPageEditorSite#dispose()
		 */
		public void dispose() {
			if (isDisposing) {
				return;
			}
			isDisposing = true;

			super.dispose();

			if (_menuExtenders != null) {
				for (int i = 0; i < _menuExtenders.size(); i++) {
					((PopupMenuExtender) _menuExtenders.get(i)).dispose();
				}
				_menuExtenders = null;
			}

			_editor = null;
			if (_site != null && _site instanceof EditorSite) {
				((EditorSite) _site).dispose();
			}
			_site = null;

		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getKeyBindingService()
		 */
		public IKeyBindingService getKeyBindingService() {
			return _site.getKeyBindingService();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getPluginId()
		 */
		public String getPluginId() {
			return _site.getPluginId();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getRegisteredName()
		 */
		public String getRegisteredName() {
			return _site.getRegisteredName();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(MenuManager menuManager,
				ISelectionProvider selProvider) {
			_site.registerContextMenu(menuManager, selProvider);
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#setBrowserURL(java.lang.String)
	 */
	public void setBrowserURL(String url) {
		String index = Integer.toString(this.getActivePage());
		if ((previews.containsKey(index))) {
			if (!HTMLPlugin.getDefault().getPreferenceStore().getBoolean(
					IPreferenceConstants.AUTO_SAVE_PROMPTED)) {
				HTMLPlugin.getDefault().getPreferenceStore().setValue(
						IPreferenceConstants.AUTO_SAVE_PROMPTED, true);
				boolean autoSave = MessageDialog.openQuestion(this
						.getContainer().getShell(),
						Messages.MultiPageHTMLEditor_AutoSaveTitlte,
						Messages.MultiPageHTMLEditor_AutoSaveMessage);
				HTMLPlugin.getDefault().getPreferenceStore().setValue(
						IPreferenceConstants.AUTO_SAVE_BEFORE_PREVIEWING,
						autoSave);
			}
			if (editor.isDirty()) {
				boolean autoSave = HTMLPlugin
						.getDefault()
						.getPreferenceStore()
						.getBoolean(
								IPreferenceConstants.AUTO_SAVE_BEFORE_PREVIEWING);
				if (autoSave) {
					editor.doSave(new NullProgressMonitor());
				}
			}
			IPreviewConfigurationPage page = (IPreviewConfigurationPage) previews
					.get(index);
			if (page instanceof DefaultPreviewConfigurationPage) {
				setTabIcon(page, null);
			} else if (page.getTabImage() != null) {
				setTabIcon(page, page.getTabImage());
			} else {
				setTabIcon(page, HTMLPlugin
						.getImage("icons/add_tab_decorator.png")); //$NON-NLS-1$
			}
			page.setURL(url);

			Iterator<IPreviewConfigurationPage> pages = previews.values()
					.iterator();
			while (pages.hasNext()) {
				IPreviewConfigurationPage curr = pages.next();
				if (curr != page) {
					curr.clearBrowser();
				}
			}
		}
		this.url = url;
	}

	/**
	 * Sets the tab icon
	 * 
	 * @param page
	 * @param icon
	 */
	public void setTabIcon(IPreviewConfigurationPage page, Image icon) {
		Iterator<String> keys = previews.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (page != null && page == previews.get(key)) {
				int index = Integer.parseInt(key.toString());
				if (getContainer() instanceof CTabFolder) {
					CTabFolder tabs = (CTabFolder) getContainer();
					if (index < tabs.getItemCount()) {
						tabs.getItem(index).setImage(icon);
					}
				}
				break;
			}
		}

	}

	/**
	 * Sets the tab tooltip text for a page
	 * 
	 * @param page
	 * @param text
	 */
	public void setTabTooltip(IPreviewConfigurationPage page, String text) {
		Iterator<String> keys = previews.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (page != null && page == previews.get(key)) {
				int index = Integer.parseInt(key.toString());
				if (getContainer() instanceof CTabFolder) {
					CTabFolder tabs = (CTabFolder) getContainer();
					if (index < tabs.getItemCount()) {
						tabs.getItem(index).setToolTipText(text);
					}
				}
				break;
			}
		}
	}

	/**
	 * Gets the url of this editor
	 * 
	 * @return - url
	 */
	public String getURL() {
		return this.url;
	}

	private void disposePreviews() {
		Object[] _previews = previews.values().toArray();
		for (int i = 0; i < _previews.length; i++) {
			((IPreviewConfigurationPage) _previews[i]).dispose();
		}
	}

	/**
	 * Sets the currently active page.
	 * 
	 * @param pageIndex
	 *            the index of the page to be activated; the index must be valid
	 */
	public void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
	}

	/**
	 * Returns the number of pages in this multi-page editor.
	 * 
	 * @return the number of pages
	 */
	public int getPageCount() {
		return super.getPageCount();
	}

	/**
	 * Returns the index of the preview page with the specified name, or -1 if
	 * not found
	 * 
	 * @param pageName
	 * @return index
	 */
	public int findPageIndex(String pageName) {
		Set<String> s = previews.keySet();
		for (String object : s) {
			IPreviewConfigurationPage page = (IPreviewConfigurationPage) previews
					.get(object);
			if (page.getTitle().equals(pageName)) {
				return Integer.parseInt(object);
			}
		}
		return -1;
	}
}
