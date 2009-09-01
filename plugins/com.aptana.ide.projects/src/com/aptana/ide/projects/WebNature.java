/**
 * 
 */
package com.aptana.ide.projects;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.ide.core.builder.UnifiedProjectBuilder;

/**
 * A WebNature basic implementation
 * 
 * @author Shalom G
 * @author cwilliams
 */
public class WebNature implements IProjectNature
{

	private IProject project;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException
	{
		IProjectDescription desc = getProject().getDescription();
		if (containsUnifiedBuilder(desc.getBuildSpec()))
			return;
		ICommand unifiedBuilderCommand = desc.newCommand();
		unifiedBuilderCommand.setBuilderName(UnifiedProjectBuilder.BUILDER_ID);

		int size = desc.getBuildSpec().length;
		ICommand[] newBuildSpec = new ICommand[size + 1];
		System.arraycopy(desc.getBuildSpec(), 0, newBuildSpec, 0, size);
		newBuildSpec[size] = unifiedBuilderCommand;

		desc.setBuildSpec(newBuildSpec);
		getProject().setDescription(desc, new NullProgressMonitor());
	}

	private boolean containsUnifiedBuilder(ICommand[] builders)
	{
		for (ICommand command : builders)
		{
			if (command.getBuilderName().equals(UnifiedProjectBuilder.BUILDER_ID))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException
	{
		// TODO Remove the unified project builder!
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject()
	{
		return project;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project)
	{
		this.project = project;
	}

}
