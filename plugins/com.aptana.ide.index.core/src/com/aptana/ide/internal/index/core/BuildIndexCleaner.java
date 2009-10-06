package com.aptana.ide.internal.index.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.BuildParticipant;
import com.aptana.ide.core.builder.IAptanaModelMarker;
import com.aptana.ide.index.core.Index;
import com.aptana.ide.index.core.IndexManager;

public class BuildIndexCleaner extends BuildParticipant
{

	@Override
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		for (BuildContext context : contexts)
		{
			cleanProblemMarkers(context);
			// Wipe the index for this document
			Index index = getIndex(context);
			if (index != null)
				index.remove(getContainerRelativePath(context));
		}
	}

	private void cleanProblemMarkers(BuildContext context)
	{
		if (context != null && context.getFile() != null)
			try
			{
				context.getFile().deleteMarkers(IAptanaModelMarker.PROBLEM_MARKER, false, IResource.DEPTH_ONE);
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private Index getIndex(BuildContext context)
	{
		IProject project = context.getFile().getProject();
		return IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
	}

	private String getContainerRelativePath(BuildContext context)
	{
		return context.getFile().getProjectRelativePath().toPortableString();
	}

	@Override
	public void cleanStarting(IProject project)
	{
	}

	@Override
	public boolean isActive(IProject project)
	{
		return true;
	}

}
