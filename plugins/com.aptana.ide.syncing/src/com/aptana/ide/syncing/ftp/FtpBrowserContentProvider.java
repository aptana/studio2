package com.aptana.ide.syncing.ftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.views.fileexplorer.Messages;

/**
 * A content provider for FTP <b>directories</b> browsing.
 */
public class FtpBrowserContentProvider implements ITreeContentProvider
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
	private final boolean directoriesOnly;

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
	public FtpBrowserContentProvider(AbstractTreeViewer viewer, boolean canStart, boolean directoriesOnly)
	{
		_viewer = viewer;
		this.directoriesOnly = directoriesOnly;
	}

	/**
	 * FtpBrowserContentProvider
	 * 
	 * @param viewer
	 */
	public FtpBrowserContentProvider(AbstractTreeViewer viewer)
	{
		this(viewer, true, true);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(final Object element)
	{
		final ReturnValue returnValue = new ReturnValue();

		if (element instanceof ProtocolManager || element instanceof Object[])
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
			Job fetchJob = new Job(Messages.FtpBrowserContentProvider_JOB_Fetching + name)
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
								UIJob refresh = new UIJob(Messages.FtpBrowserContentProvider_UIJOB_RefreshingTree)
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
		else if (element instanceof IVirtualFileManager)
		{
			final IVirtualFileManager fm = (IVirtualFileManager) element;
			IVirtualFile[] files = new IVirtualFile[0];
			if (fm.getBasePath() == null)
			{
				final int start = id;
				UIJob collapseJob = new UIJob(Messages.FtpBrowserContentProvider_UIJob_CollapsingItem)
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
					files = getVirtualFiles(fm.getBaseFile());
				}
				catch (IOException e)
				{
					final int start = id;
					UIJob collapseJob = new UIJob(Messages.FtpBrowserContentProvider_UIJob_CollapsingItem)
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
			if (directoriesOnly)
			{
				files = getDirectories(files);
			}
			returnValue.value = files;
		}
		else if (element instanceof IVirtualFile && ((IVirtualFile) element).hasFiles())
		{
			final IVirtualFile f = (IVirtualFile) element;
			try
			{
				IVirtualFile[] files = getVirtualFiles(f);
				if (directoriesOnly)
				{
					files = getDirectories(files);
				}
				returnValue.value = files;
			}
			catch (IOException e)
			{
				final int start = id;
				UIJob collapseJob = new UIJob(Messages.FtpBrowserContentProvider_UIJob_CollapsingItem)
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

	// filter all the non-directories files
	private IVirtualFile[] getDirectories(IVirtualFile[] files)
	{
		List<IVirtualFile> directories = new ArrayList<IVirtualFile>();
		for (IVirtualFile file : files)
		{
			if (file.isDirectory())
			{
				directories.add(file);
			}
		}
		return directories.toArray(new IVirtualFile[directories.size()]);
	}

	/**
	 * getVirtualFilesProtected
	 * 
	 * @param source
	 * @return IVirtualFile[]
	 * @throws IOException
	 */
	protected IVirtualFile[] getVirtualFiles(IVirtualFile source) throws IOException
	{
		if (source == null)
		{
			throw new IllegalArgumentException(Messages.FileExplorerView_SourceCannotBeNull);
		}

		IVirtualFile[] files = new IVirtualFile[0];
		try
		{
			files = source.getFiles(false, true);
		}
		catch (ConnectionException ex)
		{
			if (source.getFileManager() != null)
			{
				// Display an connection error message
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), Messages.FtpBrowserContentProvider_TTL_ConnectionFailed,
								Messages.FtpBrowserContentProvider_WRN_UnableToConnect);
					}
				});
			}
			else
			{
				throw new IllegalArgumentException(Messages.FileExplorerView_SourceNoFileManagerAttached);
			}
		}
		catch (NullPointerException ex)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
					Messages.FileExplorerView_EncounteredNullWhenRetrievingFiles, source.getAbsolutePath()));
		}

		List<IVirtualFile> newFiles = new ArrayList<IVirtualFile>();
		if (files.length > 0)
		{
			// Here we filter out all files named '.' or '..'
			for (int i = 0; i < files.length; i++)
			{
				IVirtualFile file = files[i];
				if (!file.getName().equals(".") && !file.getName().equals("..")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					newFiles.add(file);
				}
			}
			files = (IVirtualFile[]) newFiles.toArray(new IVirtualFile[0]);
		}

		return files;
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
