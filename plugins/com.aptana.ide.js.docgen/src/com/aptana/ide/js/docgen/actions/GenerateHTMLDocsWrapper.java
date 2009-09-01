/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.js.docgen.actions;

import com.aptana.ide.editor.scriptdoc.parsing.ProjectDocumentation;
import com.aptana.ide.editors.unified.context.ContextItem;

/**
 * @author Ingo Muschenetz
 */
public class GenerateHTMLDocsWrapper
{
	private ContextItem fileContext;
	private ProjectDocumentation projectDocumentation;
	private String fileName;
	
	/**
	 * setFileContext
	 *
	 * @param fileContext
	 */
	public void setFileContext(ContextItem fileContext)
	{
		this.fileContext = fileContext;
	}
	
	/**
	 * setProjectDocumentation
	 *
	 * @param projectDocumentation
	 */
	public void setProjectDocumentation(ProjectDocumentation projectDocumentation)
	{
		this.projectDocumentation = projectDocumentation;
	}		

	/**
	 * setFileName
	 *
	 * @param fileName
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}	
	
	/**
	 * getContextItem
	 *
	 * @return ContextItem
	 */
	public ContextItem getContextItem()
	{
		return this.fileContext;
	}
	
	/**
	 * getProjectDocumentation
	 *
	 * @return ProjectDocumentation
	 */
	public ProjectDocumentation getProjectDocumentation()
	{
		return this.projectDocumentation;
	}		

	/**
	 * getFileName
	 *
	 * @return String
	 */
	public String getFileName()
	{
		return this.fileName;
	}	
}