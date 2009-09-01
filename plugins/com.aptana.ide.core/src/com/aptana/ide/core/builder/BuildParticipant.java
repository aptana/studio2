package com.aptana.ide.core.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class BuildParticipant
{
	public abstract boolean isActive(IProject project);
	
	public abstract void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor);

	public abstract void cleanStarting(IProject project);
	
//	public abstract void reconcile(ReconcileContext context);
}
