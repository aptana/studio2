package com.aptana.ide.logging.impl;

import java.net.URI;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;

import com.aptana.ide.logging.DefaultLogInfo;

public class WorkspaceLogProviderTest extends TestCase
{

	public void testGetLogs()
	{
		List<DefaultLogInfo> logs = new WorkspaceLogProvider().getLogs();
		assertNotNull(logs);
		assertFalse(logs.isEmpty());
		assertEquals("Workspace log", logs.get(0).getName());
		URI uri = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().append(".metadata/.log").toFile().toURI();
		assertEquals(uri, logs.get(0).getUri());
		assertTrue(logs.get(0).supportsLogErase());
		
	}

}
