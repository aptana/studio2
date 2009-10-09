package com.aptana.ide.server.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

public class WorkspaceHttpFolderResourceTest extends TestCase
{

	private IFile iFile;
	private final String iFileName = "file.txt";
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
		iFile = project.getFile(iFileName);
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

	public void testContentTypeAndLength() throws Exception
	{
		WorkspaceHttpFolderResource resource = new WorkspaceHttpFolderResource(project);
		assertEquals(-1, resource.getContentLength());
		assertNull(resource.getContentType());
	}

	public void testGetFileNames() throws Exception
	{
		WorkspaceHttpFolderResource resource = new WorkspaceHttpFolderResource(project);
		String[] files = resource.getFileNames();
		assertNotNull(files);
		assertEquals(2, files.length);
		assertEquals(".project", files[0]);
		assertEquals(iFileName, files[1]);
	}

	public void testGetFolderNames() throws Exception
	{
		IFolder folder = null;
		final String folderName = "folder";
		try
		{
			folder = project.getFolder(folderName);
			folder.create(true, true, new NullProgressMonitor());
			WorkspaceHttpFolderResource resource = new WorkspaceHttpFolderResource(project);
			String[] folders = resource.getFolderNames();
			assertNotNull(folders);
			assertEquals(1, folders.length);
			assertEquals(folderName, folders[0]);
		}
		finally
		{
			if (folder != null)
			{
				folder.delete(true, new NullProgressMonitor());
				folder = null;
			}
		}
	}

	public void testGetContentInputStream() throws Exception
	{
		try
		{
			WorkspaceHttpFolderResource resource = new WorkspaceHttpFolderResource(project);
			resource.getContentInputStream(null);
			fail("getContentInputStream should throw IOException");
		}
		catch (IOException e)
		{

		}
	}

}
