package com.aptana.ide.server.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.ide.core.StreamUtils;

public class WorkspaceHttpResourceTest extends TestCase
{
	private IFile iFile;
	private IProject project;
	private static final String contents = "contents";

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final String projectName = "workspaceHttpResourceTest";
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IProjectDescription description = workspace.newProjectDescription(projectName);
		project.create(description, new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		iFile = project.getFile("file.txt");
		iFile.create(new ByteArrayInputStream(contents.getBytes()), true, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception
	{
		iFile.delete(true, new NullProgressMonitor());
		iFile = null;
		project.close(new NullProgressMonitor());
		project.delete(true, new NullProgressMonitor());
		project = null;
		super.tearDown();
	}

	public void testContentTypeAndLength() throws CoreException, IOException
	{
		WorkspaceHttpResource resource = new WorkspaceHttpResource(iFile);
		assertEquals(contents.length(), resource.getContentLength());
		assertEquals("text/plain", resource.getContentType());
		String read = StreamUtils.readContent(resource.getContentInputStream(null), null);
		assertEquals(contents, read);
	}

}
