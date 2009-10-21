package com.aptana.ide.editors.tasks;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.BuildParticipant;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

public class TaskBuildParticipant extends BuildParticipant
{

	private static final String TASK_MARKER_ID = "com.aptana.ide.editors.task";

	private TaskParser parser;

	@Override
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		parser = new TaskParser();
	}

	@Override
	public void build(BuildContext buildContext, IProgressMonitor monitor)
	{
		try
		{
			removeExistingTasks(buildContext);
		}
		catch (CoreException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage(), e);
		}
		List<TaskTag> tasks = parser.parse(buildContext);
		for (TaskTag task : tasks)
		{
			recordNewProblems(buildContext, task);
		}

	}

	@Override
	public void buildFinishing(IProgressMonitor monitor)
	{
		// do nothing
	}

	private void removeExistingTasks(BuildContext buildContext) throws CoreException
	{
		if (buildContext != null && buildContext.getFile() != null)
			buildContext.getFile().deleteMarkers(TASK_MARKER_ID, false, IResource.DEPTH_ONE);
	}

	private void recordNewProblems(BuildContext buildContext, TaskTag task)
	{
		try
		{
			IMarker marker = buildContext.getFile().createMarker(TASK_MARKER_ID);
			HashMap<String, Comparable> map = new HashMap<String, Comparable>();
			map.put(IMarker.PRIORITY, new Integer(task.getPriority()));
			map.put(IMarker.MESSAGE, task.getMessage());
			map.put(IMarker.LINE_NUMBER, new Integer(task.getLineNumber()));
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
			map.put(IMarker.USER_EDITABLE, new Boolean(false));
			map.put(IMarker.TRANSIENT, new Boolean(false));
			map.put(IMarker.CHAR_START, new Integer(task.getStartOffset()));
			map.put(IMarker.CHAR_END, new Integer(task.getEndOffset()));
			marker.setAttributes(map);
		}
		catch (CoreException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage(), e);
		}
	}

	@Override
	public void cleanStarting(IProject project)
	{
		try
		{
			if (project != null)
				project.deleteMarkers(TASK_MARKER_ID, false, IResource.DEPTH_INFINITE);
		}
		catch (CoreException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage(), e);
		}
	}

	@Override
	public boolean isActive(IProject project)
	{
		return true;
	}

}
