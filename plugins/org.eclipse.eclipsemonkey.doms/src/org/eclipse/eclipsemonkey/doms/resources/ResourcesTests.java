/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey.doms.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Run this class with Run As... JUnit Plug-in Test
 */
public class ResourcesTests extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ResourcesTests.class);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		IWorkspace w = ResourcesPlugin.getWorkspace();
		IProject project = w.getRoot().getProject(
				"Aptana Scripting Resources DOM Test"); //$NON-NLS-1$

		if (!project.exists())
			project.create(null);
		project.open(null);

		IFolder folder = project.getFolder("if_i_ran_the_zoo"); //$NON-NLS-1$
		folder.create(IResource.NONE, true, null);

		IFile file = folder.getFile("lunk.java"); //$NON-NLS-1$
		byte[] buf = new byte[0];
		InputStream stream = new ByteArrayInputStream(buf);
		file.create(stream, false, null);
		stream.close();

		file = folder.getFile("joat.txt"); //$NON-NLS-1$
		String s = "I'll load up five boats with a family of Joats\n" //$NON-NLS-1$
				+ "Whose feet are like cows', but wear squirrel-skin coats\n"; //$NON-NLS-1$
		stream = new ByteArrayInputStream(s.getBytes());
		file.create(stream, false, null);
		stream.close();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		IWorkspace w = ResourcesPlugin.getWorkspace();
		IProject project = w.getRoot().getProject(
				"Aptana Scripting Resources DOM Test"); //$NON-NLS-1$
		if (project.exists())
			project.delete(true, true, null);
	}

	/**
	 * @throws Exception
	 */
	public void testFilesMatching() throws Exception {

		Resources resources = (Resources) new ResourcesDOMFactory()
				.getDOMroot();
		Object[] result = resources.filesMatching(".*\\.java"); //$NON-NLS-1$
		assertEquals(1, result.length);
		assertTrue(result[0] instanceof File);
		assertEquals("lunk.java", ((File) result[0]).getEclipseObject() //$NON-NLS-1$
				.getName());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testGetLines() throws Exception {
		Resources resources = (Resources) new ResourcesDOMFactory()
				.getDOMroot();
		Object[] result = resources.filesMatching(".*\\.txt"); //$NON-NLS-1$
		File file = (File) result[0];
		Line[] lines = file.getLines();
		assertEquals(2, lines.length);
		assertEquals(
				"Whose feet are like cows', but wear squirrel-skin coats", //$NON-NLS-1$
				lines[1].getString());
	}

}
