/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Erich Gamma (erich_gamma@ch.ibm.com) and
 * 	   Kent Beck (kent@threeriversinstitute.org)
 *******************************************************************************/
package com.aptana.ide.editors.junit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.scripting.ScriptingEngine;

/**
 * TestProject
 * 
 * @author Ingo Muschenetz
 */
public final class TestProject
{
	private static TestProject instance;

	/**
	 * PLUGIN_ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.editors.tests";

	/**
	 * project
	 */
	public IProject project;

	/**
	 * projectName
	 */
	public String projectName = "TestProject"; //$NON-NLS-1$

	/**
	 * jsFolder
	 */
	public IFolder jsFolder;

	/**
	 * test0_html_path
	 */
	public Path test0_html_path;

	/**
	 * test0_html_file
	 */
	public IFile test0_html_file;

	/**
	 * test1_html_path
	 */
	public Path test1_html_path;

	/**
	 * test1_html_file
	 */
	public IFile test1_html_file;

	/**
	 * test0_js_path
	 */
	public Path test0_js_path;

	/**
	 * test0_js_file
	 */
	public IFile test0_js_file;

	/**
	 * test1_js_path
	 */
	public Path test1_js_path;

	/**
	 * test1_js_file
	 */
	public IFile test1_js_file;

	/**
	 * test2_js_path
	 */
	public Path test2_js_path;

	/**
	 * test2_js_file
	 */
	public IFile test2_js_file;

	/**
	 * test0_sdoc_path
	 */
	public Path test0_sdoc_path;

	/**
	 * test0_sdoc_file
	 */
	public IFile test0_sdoc_file;

	/**
	 * unbound_sdoc_path
	 */
	public Path unbound_sdoc_path;

	/**
	 * unbound_sdoc_file
	 */
	public IFile unbound_sdoc_file;

	/**
	 * getInstance
	 * 
	 * @return TestProject
	 */
	public static TestProject getInstance()
	{
		if (instance == null)
		{
			instance = new TestProject();
		}
		return instance;
	}

	private TestProject()
	{
		ProjectTestUtils.setAptanaPerspective();

		// create project
		this.project = ProjectTestUtils.createProject(this.projectName);

		// add sdoc folder
		ProjectTestUtils.createFolder("sdoc", this.project); //$NON-NLS-1$

		// add all files to project
		test0_html_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test0.html"); //$NON-NLS-1$ //$NON-NLS-2$
		test0_html_file = ProjectTestUtils.addFileToProject(test0_html_path, this.project);
		test1_html_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test1.html"); //$NON-NLS-1$ //$NON-NLS-2$
		test1_html_file = ProjectTestUtils.addFileToProject(test1_html_path, this.project);

		test0_js_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test0.js"); //$NON-NLS-1$ //$NON-NLS-2$
		test0_js_file = ProjectTestUtils.addFileToProject(test0_js_path, this.project);
		test1_js_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test1.js"); //$NON-NLS-1$ //$NON-NLS-2$
		test1_js_file = ProjectTestUtils.addFileToProject(test1_js_path, this.project);
		test2_js_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test2.js"); //$NON-NLS-1$ //$NON-NLS-2$
		test2_js_file = ProjectTestUtils.addFileToProject(test2_js_path, this.project);

		test0_sdoc_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/test0.sdoc"); //$NON-NLS-1$ //$NON-NLS-2$
		test0_sdoc_file = ProjectTestUtils.addFileToProject(test0_sdoc_path, this.project);
		unbound_sdoc_path = ProjectTestUtils.findFileInPlugin(PLUGIN_ID, "testProject/unbound.sdoc"); //$NON-NLS-1$
		unbound_sdoc_file = ProjectTestUtils.addFileToProject(unbound_sdoc_path, "sdoc", this.project); //$NON-NLS-1$

		// ensure scripting engine is started
		ScriptingEngine se = ScriptingEngine.getInstance();
		se.earlyStartup();

	}

	/**
	 * getProject
	 * 
	 * @return IProject
	 */
	public IProject getProject()
	{
		return project;
	}

	/**
	 * dispose
	 * 
	 * @throws CoreException
	 */
	public void dispose() throws CoreException
	{
		// waitForIndexer();
		project.delete(true, true, null);
	}
}