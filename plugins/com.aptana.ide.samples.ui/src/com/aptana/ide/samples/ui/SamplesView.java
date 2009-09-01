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
package com.aptana.ide.samples.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.intro.browser.CoreBrowserEditor;
import com.aptana.ide.intro.browser.CoreBrowserEditorInput;
import com.aptana.ide.samples.SamplesManager;
import com.aptana.ide.samples.handlers.IPreviewHandler;
import com.aptana.ide.samples.model.SamplesEntry;
import com.aptana.ide.samples.model.SamplesInfo;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SamplesView extends ViewPart
{

	private Composite displayArea;
	private TreeViewer _viewer;

	private Action doubleClickAction;
	private Action importAction;
	private Action viewHelpAction;
	private Action collapseAllAction;
	private Action viewPreviewAction;

	private String firstReveal;

	private static String LINE_DELIM = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Problems view ID
	 */
	public static final String ID = "com.aptana.ide.samples.ui.SamplesView"; //$NON-NLS-1$

	/**
	 * SnippetsView
	 */
	public SamplesView()
	{
	}

	/**
	 * createActions
	 */
	private void createActions()
	{
		createImportAction();
		createViewInfoAction();
		createDoubleClickAction();
		createCollapseAllAction();
		createViewPreviewAction();
	}

	private void createImportAction()
	{
		importAction = new Action(Messages.SamplesView_ImportSample)
		{

			public void run()
			{
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				SamplesEntry entry = null;
				if (firstElement instanceof SamplesEntry)
				{
					entry = ((SamplesEntry) firstElement).getRoot();
				}
				if (entry != null)
				{
					SamplesProjectCreator.createSamplesProject(entry);
				}
			}

		};
		importAction.setEnabled(false);
		importAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor("icons/import_wiz.gif")); //$NON-NLS-1$
	}

	private void createCollapseAllAction()
	{
		this.collapseAllAction = new Action(Messages.SamplesView_CollapseAll)
		{

			public void run()
			{
				if (_viewer != null)
				{
					_viewer.collapseAll();
				}
			}

		};
		this.collapseAllAction.setToolTipText(Messages.SamplesView_CollapseAll);
		this.collapseAllAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor("icons/collapseall.gif")); //$NON-NLS-1$
	}

	private void createViewPreviewAction()
	{
		this.viewPreviewAction = new Action(Messages.SamplesView_PreviewSample)
		{

			public void run()
			{
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				SamplesEntry entry = null;
				if (firstElement instanceof SamplesEntry)
				{
					entry = ((SamplesEntry) firstElement).getRoot();
				}

				if (entry != null)
				{
					IPreviewHandler handler = entry.getParent().getPreviewHandler();
					if (handler != null)
					{
						handler.previewRequested(entry);
					}
				}
			}

		};
		this.viewPreviewAction.setToolTipText(Messages.SamplesView_ViewPreview);
		this.viewPreviewAction.setEnabled(false);
		this.viewPreviewAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor("icons/preview.gif")); //$NON-NLS-1$
	}

	private void createViewInfoAction()
	{
		viewHelpAction = new Action(Messages.SamplesView_ViewHelp)
		{

			public void run()
			{
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				SamplesInfo info = null;
				if (firstElement instanceof SamplesInfo)
				{
					info = (SamplesInfo) firstElement;
				}
				else if (firstElement instanceof SamplesEntry)
				{
					info = ((SamplesEntry) firstElement).getParent();
				}

				if (info != null)
				{
					try
					{
						URL url = info.getInfoFile();
						if (url != null)
						{
							CoreBrowserEditorInput input = new CoreBrowserEditorInput(url);
							input.setImage(SamplesUIPlugin.getImageDescriptor("icons/window1616.png")); //$NON-NLS-1$
							IWorkbenchWindow window = CoreUIPlugin.getActiveWorkbenchWindow();
							if (window != null)
							{
								IWorkbenchPage page = window.getActivePage();
								IDE.openEditor(page, input, CoreBrowserEditor.ID);
							}
						}

					}
					catch (PartInitException e)
					{
					}
				}
			}

		};
		viewHelpAction.setImageDescriptor(SamplesUIPlugin.getImageDescriptor("icons/book_open.png")); //$NON-NLS-1$
		viewHelpAction.setEnabled(false);
	}

	private void toggleActionState()
	{
		ISelection selection = _viewer.getSelection();
		Object firstElement = ((IStructuredSelection) selection).getFirstElement();
		SamplesInfo info = null;
		SamplesEntry entry = null;
		if (firstElement instanceof SamplesEntry)
		{
			entry = (SamplesEntry) firstElement;
			info = entry.getParent();
		}
		else if (firstElement instanceof SamplesInfo)
		{
			info = (SamplesInfo) firstElement;
		}

		viewHelpAction.setEnabled(info != null && info.getInfoFile() != null);
		viewPreviewAction.setEnabled(entry != null && info != null && info.getPreviewHandler() != null);
		importAction.setEnabled(entry != null);
	}

	/**
	 * Gets an editor id
	 * 
	 * @param file
	 * @return - editor id
	 */
	protected IEditorDescriptor getEditorId(File file)
	{
		IWorkbench workbench = SamplesUIPlugin.getDefault().getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file.getName());
		if (descriptor != null)
		{
			return descriptor;
		}
		else
		{
			return editorRegistry.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		}
	}

	/**
	 * createDoubleClickAction
	 */
	private void createDoubleClickAction()
	{
		doubleClickAction = new Action()
		{
			public void run()
			{
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof SamplesEntry)
				{
					SamplesEntry entry = (SamplesEntry) firstElement;
					final File file = entry.getFile();

					String name = entry.getRoot().getFile().getName() + "-"; //$NON-NLS-1$
					if (file != null && file.isFile())
					{
						IEditorDescriptor editorDesc = getEditorId(file);
						if (editorDesc.getId().equals(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID))
						{
							openExternalFile(file);
						}
						else
						{
							String filePrefix = FileUtils.stripExtension(file.getName());
							String fileExt = "." + FileUtils.getExtension(file.getName()); //$NON-NLS-1$

							String newFileName = FileUtils.getRandomFileName(filePrefix, fileExt);

							File newFile = new File(FileUtils.systemTempDir + File.separator + newFileName);
							IEditorInput input = CoreUIUtils.createNonExistingFileEditorInput(newFile, name
									+ filePrefix);
							IWorkbench workbench = SamplesUIPlugin.getDefault().getWorkbench();
							IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
							try
							{
								IEditorPart part = page.openEditor(input, editorDesc.getId());
								if (part instanceof ITextEditor)
								{
									ITextEditor editor = (ITextEditor) part;
									IDocumentProvider dp = editor.getDocumentProvider();
									IDocument doc = dp.getDocument(editor.getEditorInput());
									try
									{
										BufferedReader stream = new BufferedReader(new FileReader(file));
										StringBuffer sb = new StringBuffer();
										while (stream.ready())
										{
											sb.append(stream.readLine() + LINE_DELIM);
										}
										if (sb.length() > 0)
										{
											doc.replace(0, 0, sb.toString());
										}
									}
									catch (BadLocationException e)
									{
									}
									catch (FileNotFoundException e)
									{
									}
									catch (IOException e)
									{
									}
								}
							}
							catch (PartInitException e)
							{
								IdeLog.logError(SamplesUIPlugin.getDefault(), StringUtils.format(
										Messages.SamplesView_UnableToOpenFile, newFile
												.getAbsolutePath()), null);
							}
						}
					}
				}
			}

		};
		doubleClickAction.setText(Messages.SamplesView_TXT_OpenCopy);
	}

	/**
	 * @param file
	 */
	private void openExternalFile(final File file)
	{

		File newFile = createTemporaryFile(file);
		if (file == null)
		{
			return;
		}

		FileUtils.copy(file, newFile);
		IWorkbench workbench = SamplesUIPlugin.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

		IEditorDescriptor editorDesc = getEditorId(newFile);
		IEditorPart editorPart;
		try
		{
			editorPart = IDE.openEditor(page, CoreUIUtils.createJavaFileEditorInput(newFile), editorDesc.getId());
			if (editorPart != null && editorPart instanceof ErrorEditorPart)
			{
				page.closeEditor(editorPart, false);
				IdeLog.logError(SamplesUIPlugin.getDefault(), StringUtils.format(
						Messages.SamplesView_UnableToOpenFile, newFile.getAbsolutePath()), null);
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(), StringUtils.format(
					Messages.SamplesView_UnableToOpenFile, newFile.getAbsolutePath()), null);
		}
	}

	private File createTemporaryFile(final File file)
	{
		String fileName = FileUtils.stripExtension(file.getName());
		String fileExt = "." + FileUtils.getExtension(file.getName()); //$NON-NLS-1$
		File newFile = null;
		try
		{
			newFile = File.createTempFile(fileName, fileExt);
		}
		catch (final IOException e1)
		{
			_viewer.getControl().getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					MessageDialog.openError(_viewer.getControl().getShell(),
							Messages.SamplesView_UnableToPreview,
							Messages.SamplesView_ErrorOpening);

					IdeLog
							.logError(SamplesUIPlugin.getDefault(), StringUtils.format(
									Messages.SamplesView_UnableToCreateTemp, file
											.getAbsolutePath()), e1);
				}
			});
			return null;
		}
		return newFile;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this._viewer = this.createTreeViewer(displayArea);
		this._viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.createActions();
		this.hookContextMenu();

		this._viewer.addDoubleClickListener(new IDoubleClickListener()
		{

			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}

		});

		this._viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				toggleActionState();
			}

		});

		this.hookToolbarActions();

		this._viewer.setInput(SamplesViewContentProvider.LOADING);
		Job loadingJob = new Job(Messages.SamplesView_Job_Loading)
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				final SamplesManager snippets = SamplesManager.getInstance();
				UIJob job = new UIJob(Messages.SamplesView_Job_Updating)
				{

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						if (_viewer != null && !_viewer.getTree().isDisposed())
						{
							_viewer.setInput(snippets);
							if (firstReveal != null)
							{
								selectAndReveal(firstReveal);
							}
						}
						return Status.OK_STATUS;
					}

				};
				job.setSystem(true);
				job.schedule();
				return Status.OK_STATUS;
			}

		};
		loadingJob.schedule();
		PreferenceUtils.registerBackgroundColorPreference(_viewer.getControl(),
				"com.aptana.ide.core.ui.background.color.samplesView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(_viewer.getControl(),
				"com.aptana.ide.core.ui.foreground.color.samplesView"); //$NON-NLS-1$
		getSite().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp(parent, ID);
	}

	private void hookToolbarActions()
	{
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(importAction);
		manager.add(viewPreviewAction);
		manager.add(viewHelpAction);
		manager.add(collapseAllAction);
	}

	

	private IActivityManager activityManager = PlatformUI
	.getWorkbench().getActivitySupport()
	.getActivityManager();
	
	
	IIdentifierListener identifierListener;
	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	private TreeViewer createTreeViewer(Composite parent)
	{
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		final TreeViewer viewer = new TreeViewer(tree);

		viewer.setContentProvider(new SamplesViewContentProvider());
		viewer.setLabelProvider(new SamplesViewLabelProvider());
		
		identifierListener = new IIdentifierListener () {

			public void identifierChanged(IdentifierEvent identifierEvent) {
				if ( identifierEvent.hasEnabledChanged() ){
					Job job = new UIJob (Messages.SamplesView_Job_UpdatingExplorer) {
						public IStatus runInUIThread(IProgressMonitor monitor) {
							ViewerFilter[] filters = viewer.getFilters();
							viewer.resetFilters();
							for (int i = 0; i < filters.length; i++)
							{
								viewer.addFilter(filters[i]);
							}
							viewer.refresh();
							return Status.OK_STATUS;
						};
					};
					job.setPriority(UIJob.INTERACTIVE);
					job.schedule();
				}
				return;
			}
		};
		viewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof SamplesInfo ){
					SamplesInfo si = (SamplesInfo)element;

					String extensionId = si.getExtensionId();
					String extensionPluginId = si.getExtensionPluginId();
					if (extensionPluginId != null && extensionId != null
							&& extensionPluginId.length() > 0
							&& extensionId.length() > 0) {
						final IIdentifier id = activityManager
								.getIdentifier(extensionPluginId + "/" //$NON-NLS-1$
										+ extensionId);
						if ( id != null ) {
							id.addIdentifierListener(identifierListener);
							return id.isEnabled();
						}
					}
				}
				return true;
			}
		}
		);

		return viewer;
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 * @param element
	 */
	private void fillContextMenu(IMenuManager manager, Object element)
	{
		if (element instanceof SamplesInfo)
		{
			manager.add(viewHelpAction);
		}
		else if (element instanceof SamplesEntry)
		{
			manager.add(importAction);
			SamplesEntry entry = (SamplesEntry) element;
			SamplesInfo info = entry.getParent();
			if (entry.getRoot() != null && info != null && info.getPreviewHandler() != null)
			{
				manager.add(viewPreviewAction);
			}
			if (entry instanceof SamplesEntry)
			{
				final File file = entry.getFile();
				if (file != null && file.isFile())
				{
					manager.add(doubleClickAction);
				}
			}
		}
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				fillContextMenu(manager, firstElement);
			}
		});

		Menu menu = menuMgr.createContextMenu(this._viewer.getControl());

		this._viewer.getControl().setMenu(menu);

		this.getSite().registerContextMenu(menuMgr, this._viewer);
	}

	/**
	 * listChanged
	 * 
	 * @param list
	 */
	public void listChanged(final SamplesManager list)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (!_viewer.getTree().isDisposed() && _viewer.getContentProvider() != null)
				{
					_viewer.setInput(list);
				}
			}
		});
	}

	/**
	 * Selects and reveals a samples entry with the given name
	 * 
	 * @param entryName
	 */
	public void selectAndReveal(String entryName)
	{
		if (entryName == null)
		{
			return;
		}
		if (_viewer.getInput() == SamplesViewContentProvider.LOADING)
		{
			this.firstReveal = entryName;
		}
		SamplesInfo[] infos = SamplesManager.getInstance().getSamplesInfos();
		for (int i = 0; i < infos.length; i++)
		{
			if (infos[i].getName().equals(entryName))
			{
				_viewer.expandToLevel(infos[i], 1);
				_viewer.setSelection(new StructuredSelection(infos[i]), true);
				break;
			}
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
	}

}
