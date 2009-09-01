package com.aptana.ide.index.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.internal.index.core.ReadWriteMonitor;

public class IndexManager
{

	private static IndexManager instance;

	private Map<String, Index> indexes;

	public synchronized static IndexManager getInstance()
	{
		if (instance == null)
			instance = new IndexManager();
		return instance;
	}

	private IndexManager()
	{
		indexes = new HashMap<String, Index>();
		IResourceChangeListener listener = new IResourceChangeListener()
		{

			public void resourceChanged(IResourceChangeEvent event)
			{
				IResourceDelta delta = event.getDelta();
				if (delta == null)
					return;
				try
				{
					delta.accept(new IResourceDeltaVisitor()
					{
						public boolean visit(IResourceDelta delta) throws CoreException
						{
							IResource resource = delta.getResource();
							if (resource.getType() == IResource.FILE)
							{
								if (delta.getKind() == IResourceDelta.REMOVED)
								{
									removeDocument(resource.getProject().getFullPath(), resource.getProjectRelativePath().toPortableString());
								}
							}
							return true;
						}
					});
				}
				catch (CoreException e)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(), e.getMessage(), e);
				}
			}
		};
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

	public Index getIndex(String path)
	{
		Index index = indexes.get(path);
		if (index == null)
		{
			try
			{
				index = new Index(path);
				indexes.put(path, index);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return index;
	}

	public IPath computeIndexLocation(String path)
	{
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(path.getBytes());
		String fileName = Long.toString(crc.getValue()) + ".index"; //$NON-NLS-1$
		return Activator.getDefault().getStateLocation().append(fileName);
	}

	private void removeDocument(IPath container, String documentPath)
	{
		Index index = getIndex(container.toPortableString());
		if (index == null)
			return;
		ReadWriteMonitor monitor = index.monitor;
		if (monitor == null)
			return; // index got deleted since acquired

		try
		{
			monitor.enterWrite(); // ask permission to write
			index.remove(documentPath);
		}
		finally
		{
			monitor.exitWrite(); // free write lock
		}
	}

	// FIXME IndexManager should listen for resource changes and handle the removal of deleted files on it's own!
	// TODO Do we want to use build participants to handle indexing? This means they'll only happen on builds/auto
	// builds, not if user turns that off!
}
