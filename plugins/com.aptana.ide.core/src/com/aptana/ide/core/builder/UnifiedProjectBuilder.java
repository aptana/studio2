package com.aptana.ide.core.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;

public class UnifiedProjectBuilder extends IncrementalProjectBuilder
{

	public static final String BUILDER_ID = AptanaCorePlugin.ID + ".unifiedBuilder";
	private static final String BUILD_PARTICIPANT_EXTENSION_POINT_ID = "buildParticipants";
	private static final boolean DEBUG = false;
	private static final Integer DEFAULT_PRIORITY = 0;

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		if (getProject() == null || !getProject().isAccessible())
			return new IProject[0];

		List<BuildParticipant> participants = getBuildParticipants();
		List<BuildContext> contexts = getBuildContexts(kind);
		boolean isBatch = isBatch(kind);
		for (BuildParticipant buildParticipant : participants)
		{
			long start = System.currentTimeMillis();
			buildParticipant.buildStarting(contexts, isBatch, monitor);
			if (DEBUG)
				System.out.println("Took " + (System.currentTimeMillis() - start) + "ms for build participant: "
						+ buildParticipant.getClass().getSimpleName());
		}
		for (BuildContext context : contexts)
		{
			cleanProblemMarkers(context);
			List<IProblem> problems = context.getRecordedProblems();
			if (problems.isEmpty())
				continue;
			// Generate markers for each problem!
			for (IProblem problem : problems)
			{
				addMarker(context, problem);
			}
		}
		return new IProject[0];
	}

	private void cleanProblemMarkers(BuildContext context) throws CoreException
	{
		if (context != null && context.getFile() != null)
			context.getFile().deleteMarkers(IAptanaModelMarker.PROBLEM_MARKER, false, IResource.DEPTH_ONE);
	}

	private String getContainerRelativePath(BuildContext context)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void addMarker(BuildContext context, IProblem problem) throws CoreException
	{
		IMarker marker = context.getFile().createMarker(IAptanaModelMarker.PROBLEM_MARKER);
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(IMarker.CHAR_START, problem.startOffset());
		attributes.put(IMarker.CHAR_END, problem.endOffset());
		attributes.put(IMarker.MESSAGE, problem.getMessage());
		attributes.put(IMarker.SEVERITY, problem.getSeverity());
		if (problem.lineNumber() > 0)
			attributes.put(IMarker.LINE_NUMBER, problem.lineNumber());
		attributes.put(IAptanaModelMarker.ID, problem.getId());
		marker.setAttributes(attributes);
	}

	private List<BuildContext> getBuildContexts(int kind)
	{
		if (isBatch(kind))
			return getFullProjectContexts();
		return getIncrementalContexts();
	}

	private List<BuildContext> getFullProjectContexts()
	{
		final List<BuildContext> contexts = new ArrayList<BuildContext>();
		try
		{
			getProject().accept(new IResourceProxyVisitor()
			{
				public boolean visit(IResourceProxy proxy) throws CoreException
				{
					if (proxy.getType() == IResource.FILE)
					{
						contexts.add(new BuildContext((IFile) proxy.requestResource()));
					}
					return true;
				}
			}, IResource.NONE);
		}
		catch (CoreException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), e.getMessage(), e);
		}
		return contexts;
	}

	private List<BuildContext> getIncrementalContexts()
	{
		final List<BuildContext> contexts = new ArrayList<BuildContext>();
		try
		{
			getDelta(getProject()).accept(new IResourceDeltaVisitor()
			{
				public boolean visit(IResourceDelta delta) throws CoreException
				{
					IResource resource = delta.getResource();
					if (resource.getType() == IResource.FILE)
					{
						if (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED)
						{
							contexts.add(new BuildContext((IFile) resource));
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
		return contexts;
	}

	private boolean isBatch(int kind)
	{
		return kind == FULL_BUILD || kind == CLEAN_BUILD;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException
	{
		if (getProject() == null || !getProject().isAccessible())
			return;

		List<BuildParticipant> participants = getBuildParticipants();
		for (BuildParticipant buildParticipant : participants)
		{
			buildParticipant.cleanStarting(getProject());
		}
		super.clean(monitor);
	}

	private List<BuildParticipant> getBuildParticipants()
	{
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(AptanaCorePlugin.ID,
				BUILD_PARTICIPANT_EXTENSION_POINT_ID);
		if (extensionPoint == null)
			return Collections.emptyList();
		// TODO Store the configElements long term and create new instances of classes each time
		final Map<BuildParticipant, Integer> participants = new HashMap<BuildParticipant, Integer>();
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions)
		{
			IConfigurationElement[] configElements = extension.getConfigurationElements();
			for (IConfigurationElement configElement : configElements)
			{
				try
				{
					BuildParticipant participant = (BuildParticipant) configElement.createExecutableExtension("class");
					if (participant.isActive(getProject()))
					{
						String rawPriority = configElement.getAttribute("priority");
						Integer priority = DEFAULT_PRIORITY;
						try
						{
							if (rawPriority != null)
								priority = Integer.parseInt(rawPriority);
						}
						catch (NumberFormatException e)
						{
							priority = DEFAULT_PRIORITY;
						}
						participants.put(participant, priority);
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(), e.getMessage(), e);
				}
			}
		}
		// sort by priority
		List<BuildParticipant> sorted = new ArrayList<BuildParticipant>(participants.keySet());
		Collections.sort(sorted, new Comparator<BuildParticipant>()
		{

			public int compare(BuildParticipant o1, BuildParticipant o2)
			{
				return participants.get(o2).compareTo(participants.get(o1));
			}
		});
		return sorted;
	}
}
