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
package com.aptana.ide.editors.ui.views.problems;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileServiceChangeListener;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.IFileErrorListener;

/**
 * This page represents the problems view associated with the active editor. It knows of the file service of the editor
 * which reports errors to it that become displayed. It removes itself as a listener from the editor file service when
 * disposed.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Paul Colton
 */
public class ProblemsPage extends Page implements IFileErrorListener, IFileServiceChangeListener
{

	private Composite displayArea;
	private TableViewer viewer;
	private Label statusText;
	private static String[] COLUMN_NAMES = new String[] { StringUtils.EMPTY,
			"Description", "Resource", "In Folder", "Location" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static int[] COLUMN_WIDTHS = new int[] { 20, 200, 300, 75, 60 };
	private IFileError[] errors = null;
	private IFileService service;
	private IUnifiedEditor editor;

	/**
	 * ViewContentProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ViewContentProvider implements IStructuredContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent)
		{
			if (errors == null)
			{
				return new Object[0];
			}

			ArrayList list = new ArrayList();

			for (int i = 0; i < errors.length; i++)
			{
				IFileError e = errors[i];
				list.add(new ViewContentItem(e.getSeverity(), e.getMessage(), e.getFileName(), e.getFolderPath(), e
						.getLineNumber(), e.getOffset(), e.getLength()));
			}

			return list.toArray(new Object[0]);
		}
	}

	/**
	 * ViewContentItem
	 * 
	 * @author Ingo Muschenetz
	 */
	class ViewContentItem
	{
		/**
		 * icon
		 */
		public int icon;

		/**
		 * desc
		 */
		public String desc;

		/**
		 * res
		 */
		public String res;

		/**
		 * folder
		 */
		public String folder;

		/**
		 * loc
		 */
		public int loc;

		/**
		 * offset
		 */
		public int offset;

		/**
		 * len
		 */
		public int len;

		/**
		 * ViewContentItem
		 * 
		 * @param ic
		 * @param n
		 * @param i
		 * @param s
		 * @param l
		 * @param o
		 * @param le
		 */
		public ViewContentItem(int ic, String n, String i, String s, int l, int o, int le)
		{
			icon = ic;
			desc = n;
			res = i;
			folder = s;
			loc = l;
			offset = o;
			len = le;
		}
	}

	/**
	 * ViewLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object obj, int index)
		{
			switch (index)
			{
				case 0:
					return StringUtils.EMPTY;
				case 1:
					return ((ViewContentItem) obj).desc;
				case 2:
					return ((ViewContentItem) obj).res;
				case 3:
					return ((ViewContentItem) obj).folder;
				case 4:
					return "line " + ((ViewContentItem) obj).loc; //$NON-NLS-1$
				default:
					return StringUtils.EMPTY;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object obj, int index)
		{
			switch (index)
			{
				case 0:
					return getImage(obj);
				default:
					return null;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj)
		{
			int i = ((ViewContentItem) obj).icon;

			if (i == 2)
			{
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
			}
			else if (i == 1)
			{
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
			}

			else
			{
				return null;
			}
		}
	}

	/**
	 * NameSorter
	 * 
	 * @author Ingo Muschenetz
	 */
	class NameSorter extends ViewerSorter
	{
		/**
		 * NameSorter
		 */
		public NameSorter()
		{
			super();
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element)
		{
			ViewContentItem item = (ViewContentItem) element;
			return item.icon == 2 ? 0 : 1;
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 != cat2)
			{
				return cat1 - cat2;
			}

			ViewContentItem item1 = (ViewContentItem) e1;
			ViewContentItem item2 = (ViewContentItem) e2;

			return item1.loc - item2.loc;
		}
	}

	/**
	 * Gets the current file service
	 * 
	 * @return - file service showing errors for
	 */
	public IFileService getFileService()
	{
		return this.service;
	}

	/**
	 * @param editor
	 */
	public ProblemsPage(IUnifiedEditor editor)
	{
		this.editor = editor;
		this.service = this.editor.getFileContext();
		this.editor.addFileServiceChangeListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.aptana.ide.editors.ProblemsView"); //$NON-NLS-1$

		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		displayArea.setLayout(layout);

		statusText = new Label(displayArea, SWT.NONE);
		statusText.setText("0 errors"); //$NON-NLS-1$
		statusText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		viewer = new TableViewer(displayArea, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		for (int i = 0; i < COLUMN_NAMES.length; i++)
		{
			TableColumn tc1 = new TableColumn(viewer.getTable(), SWT.LEFT);
			tc1.setText(COLUMN_NAMES[i]);
			tc1.setWidth(COLUMN_WIDTHS[i]);
		}

		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				viewerSelectionChanged(selection);
			}
		});
		PreferenceUtils.persist(UnifiedEditorsPlugin.getDefault().getPreferenceStore(), viewer.getTable(),
				ProblemsPage.class.getName());
		PreferenceUtils.registerBackgroundColorPreference(viewer.getControl(),
				"com.aptana.ide.core.ui.background.color.validationView"); //$NON-NLS-1$		
		PreferenceUtils.registerForegroundColorPreference(viewer.getControl(),
		"com.aptana.ide.core.ui.foreground.color.validationView"); //$NON-NLS-1$
		this.service.addErrorListener(this);
		onErrorsChanged(service.getFileErrors());
	}

	/**
	 * viewerSelectionChanged
	 * 
	 * @param selection
	 */
	protected void viewerSelectionChanged(IStructuredSelection selection)
	{

		IEditorPart ed = null;

		try
		{
			ed = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}
		catch (Exception e)
		{
			return;
		}

		if (ed instanceof IUnifiedEditor)
		{
			ViewContentItem item = (ViewContentItem) selection.getFirstElement();
			if (item != null)
			{
				((IUnifiedEditor) ed).selectAndReveal(item.offset, item.len);
			}
		}

	}

	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose()
	{
		if (this.editor != null)
		{
			this.editor.removeFileServiceChangeListener(this);
		}
		if (this.service != null)
		{
			this.service.removeErrorListener(this);
		}
		super.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/**
	 * @see com.aptana.ide.editors.unified.errors.IFileErrorListener#onErrorsChanged(com.aptana.ide.editors.unified.errors.IFileError[])
	 */
	public void onErrorsChanged(final IFileError[] errors)
	{
		this.errors = errors;

		Display display = Display.getDefault();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (viewer != null && viewer.getContentProvider() != null)
				{
					try
					{
						if (viewer.getInput() == null)
						{
							viewer.setInput(getSite());
						}
						else
						{
							viewer.refresh();
						}
						int errorCount = 0;
						int warningCount = 0;
						if (ProblemsPage.this.errors != null)
						{
							for (int i = 0; i < errors.length; i++)
							{
								if (errors[i].getSeverity() == IMarker.SEVERITY_ERROR)
								{
									errorCount++;
								}
								else if (errors[i].getSeverity() == IMarker.SEVERITY_WARNING)
								{
									warningCount++;
								}
							}
						}
						String errorString = errorCount + " error"; //$NON-NLS-1$
						errorString += errorCount > 1 ? "s" : ""; //$NON-NLS-1$ //$NON-NLS-2$
						String warningString = warningCount + " warning"; //$NON-NLS-1$
						warningString += warningCount > 1 ? "s" : ""; //$NON-NLS-1$ //$NON-NLS-2$
						if (errorCount > 0 && warningCount > 0)
						{
							statusText.setText(errorString + ", " + warningString); //$NON-NLS-1$
						}
						else if (errorCount > 0)
						{
							statusText.setText(errorString);
						}
						else if (warningCount > 0)
						{
							statusText.setText(warningString);
						}
						else
						{
							statusText.setText("0 problems"); //$NON-NLS-1$
						}
					}
					catch (Exception e)
					{
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(), "Error", e); //$NON-NLS-1$
					}
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl()
	{
		return displayArea;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileServiceChangeListener#fileServiceChanged(com.aptana.ide.editors.unified.IFileService)
	 */
	public void fileServiceChanged(IFileService newService)
	{
		if (this.service != null)
		{
			this.service.removeErrorListener(this);
		}
		this.service = newService;
		if (this.service != null)
		{
			this.service.addErrorListener(this);
			onErrorsChanged(this.service.getFileErrors());
		}
		else
		{
			onErrorsChanged(null);
		}
	}

}
