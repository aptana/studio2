package com.aptana.ide.editor.html;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.BuildParticipant;

public abstract class HTMLBuildParticipant extends BuildParticipant
{

	public HTMLBuildParticipant()
	{
		super();
	}

	protected boolean isHTMLFile(BuildContext context)
	{
		IFile file = context.getFile();
		if (file == null)
			return false;
		String extension = file.getFileExtension();
		if (extension == null)
			return false;
		return extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("htm")
				|| extension.equalsIgnoreCase("xhtml") || extension.equalsIgnoreCase("shtml") || file.getName().endsWith(".html.erb");
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
	
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		// do nothing
	}
	
	@Override
	public void buildFinishing(IProgressMonitor monitor)
	{
		// do nothing		
	}

}