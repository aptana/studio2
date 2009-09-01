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
package com.aptana.ide.core.ui.views.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.VirtualFileManagerGroup;
import com.aptana.ide.core.ui.io.file.LocalFile;
import com.aptana.ide.core.ui.io.file.LocalFileManager;

/**
 * FileTreeContentProvider
 * 
 * @author Paul Colton
 */
public class FileTreeContentProvider implements ITreeContentProvider
{
	private AbstractTreeViewer _viewer = null;
	private int id = 0;

	/**
	 * LOADING
	 */
	public static final Object LOADING = new Object();

	/**
	 * LOADING_ELEMENT
	 */
	public static final Object[] LOADING_ELEMENT = new Object[] { LOADING };

	private HashMap<Object, Object[]> mappings = new HashMap<Object, Object[]>();

	/**
	 * ReturnValue
	 */
	class ReturnValue
	{
		/**
		 * value
		 */
		public Object[] value = new Object[0];
	}

	/**
	 * Creates a content provider that is ready to serve content if canStart is true, else this provider will return no
	 * content
	 * 
	 * @param viewer
	 * @param canStart
	 */
	public FileTreeContentProvider(AbstractTreeViewer viewer, boolean canStart)
	{
		_viewer = viewer;
	}

	/**
	 * FileTreeContentProvider
	 * 
	 * @param viewer
	 */
	public FileTreeContentProvider(AbstractTreeViewer viewer)
	{
		this(viewer, true);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(final Object element)
	{
		final ReturnValue returnValue = new ReturnValue();

		if (element instanceof LocalFile || element instanceof LocalFileManager || element instanceof ProtocolManager
				|| element instanceof VirtualFileManagerGroup || element instanceof Object[])
		{
			getChildrenOfElement(element, returnValue);
		}
		else if (!mappings.containsKey(element))
		{
			// Return hour glass until fetched
			returnValue.value = LOADING_ELEMENT;
			// spawn fetching
			String name = element.toString();
			if (element instanceof IVirtualFileManager)
			{
				name = ((IVirtualFileManager) element).getDescriptiveLabel();
			}
			else if (element instanceof IVirtualFile)
			{
				name = ((IVirtualFile) element).getName();
			}
			final int start = id;
			Job fetchJob = new Job(Messages.FileTreeContentProvider_Fetching_job_title + name)
			{

				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						getChildrenOfElement(element, returnValue);
						if (start == id)
						{
							if (returnValue.value != LOADING_ELEMENT)
							{
								mappings.put(element, returnValue.value);
								UIJob refresh = new UIJob(Messages.FileTreeContentProvider_Refreshing_tree_job_title)
								{

									public IStatus runInUIThread(IProgressMonitor monitor)
									{
										try
										{
											if (start == id && _viewer != null && !_viewer.getControl().isDisposed())
											{
												_viewer.refresh(element);
											}
										}
										catch (Exception e)
										{
											// Catch everything to prevent error dialog
										}
										catch (Error e)
										{
											// Catch everything to prevent error dialog
										}
										return Status.OK_STATUS;
									}

								};
								refresh.schedule();
							}
						}
					}
					catch (Exception e)
					{
						// Catch everything to prevent error dialog
					}
					catch (Error e)
					{
						// Catch everything to prevent error dialog
					}
					return Status.OK_STATUS;
				}

			};
			fetchJob.schedule();
		}
		else
		{
			returnValue.value = mappings.remove(element);
		}

		return returnValue.value;
	}

	/**
	 * @param element
	 * @param returnValue
	 */
	private void getChildrenOfElement(final Object element, final ReturnValue returnValue)
	{
		if (element instanceof Object[])
		{
			returnValue.value = (Object[]) element;
		}
		else if (element instanceof ProtocolManager)
		{
			ProtocolManager pm = (ProtocolManager) element;
			if (pm.hasCustomContent())
			{
				returnValue.value = pm.getContent();
			}
			else
			{
				ArrayList<IVirtualFileManager> list = new ArrayList<IVirtualFileManager>();
				IVirtualFileManager[] managers = pm.getFileManagers();
				for (int i = 0; i < managers.length; i++)
				{
					IVirtualFileManager manager = managers[i];
					if (!manager.isTransient())
					{
						list.add(manager);
					}
				}
				returnValue.value = list.toArray(new IVirtualFileManager[0]);
			}
		}
		else if (element instanceof VirtualFileManagerGroup)
		{
			VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
			returnValue.value = group.getFileManagers();
		}
		else if (element instanceof IVirtualFileManager)
		{
			final IVirtualFileManager fm = (IVirtualFileManager) element;
			IVirtualFile[] files = new IVirtualFile[0];
			if (fm.getBasePath() == null)
			{
				final int start = id;
				UIJob collapseJob = new UIJob(Messages.FileTreeContentProvider_Collapsing_item_job_title)
				{

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						if (start == id && _viewer != null && !_viewer.getControl().isDisposed())
						{
							_viewer.collapseToLevel(fm, 0);
						}
						return Status.OK_STATUS;
					}

				};
				collapseJob.schedule();
			}
			else
			{
				try
				{
					files = FileExplorerView.getVirtualFilesProtected(fm.getBaseFile());
				}
				catch (IOException e)
				{
					final int start = id;
					UIJob collapseJob = new UIJob(Messages.FileTreeContentProvider_Collapsing_item_job_title)
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (start == id && _viewer != null && !_viewer.getControl().isDisposed())
							{
								_viewer.collapseToLevel(fm, 0);
							}
							return Status.OK_STATUS;
						}

					};
					collapseJob.schedule();
				}
			}
			returnValue.value = files;
		}
		else if (element instanceof IVirtualFile && ((IVirtualFile) element).hasFiles())
		{
			final IVirtualFile f = (IVirtualFile) element;
			try
			{
				returnValue.value = FileExplorerView.getVirtualFilesProtected(f);
			}
			catch (IOException e)
			{
				final int start = id;
				UIJob collapseJob = new UIJob(Messages.FileTreeContentProvider_Collapsing_item_job_title)
				{

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						if (start == id && _viewer != null && !_viewer.getControl().isDisposed())
						{
							mappings.put(f, new Object[0]);
							_viewer.refresh(f);
						}
						return Status.OK_STATUS;
					}

				};
				collapseJob.schedule();
			}
		}
		else if (element instanceof ITreeContentProvider)
		{
			returnValue.value = ((ITreeContentProvider) element).getChildren(element);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof ProtocolManager)
		{
			ProtocolManager pm = (ProtocolManager) element;
			if (pm.hasCustomContent())
			{
				return pm.getContent().length > 0;
			}
			else
			{
				return pm.getFileManagers().length > 0;
			}
		}
		else if (element instanceof VirtualFileManagerGroup)
		{
			VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
			return group.getFileManagers().length > 0;
		}
		else if (element instanceof IVirtualFileManager)
		{
			// IVirtualFileManager fm = (IVirtualFileManager) element;
			return true; // fm.getBaseFile().hasFiles();
		}
		else if (element instanceof IVirtualFile)
		{
			IVirtualFile f = (IVirtualFile) element;
			return f.hasFiles();
		}
		else if (element instanceof ITreeContentProvider)
		{
			return ((ITreeContentProvider) element).hasChildren(element);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof File)
		{
			return ((File) element).getParent();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		id++;
		mappings.clear();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object old_input, Object new_input)
	{
	}
}
