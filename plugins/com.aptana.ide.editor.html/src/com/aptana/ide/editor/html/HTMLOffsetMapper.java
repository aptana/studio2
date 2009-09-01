/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.editor.html;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editors.unified.ChildOffsetMapper;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.ICodeLocation;
import com.aptana.ide.server.jetty.server.HTMLPreviewConstants;

/**
 * The HTMLOffsetMapper understands how to map between lexemes, offsets and the environment
 * 
 * @author Ingo Muschenetz
 */
public class HTMLOffsetMapper extends ChildOffsetMapper
{
	// private IRuntimeEnvironment environment;

	/**
	 * @param parent
	 */
	public HTMLOffsetMapper(IParentOffsetMapper parent)
	{
		super(parent);
		// environment = HTMLLanguageEnvironment.getInstance().getRuntimeEnvironment();
	}

	/**
	 * Returns a "hash name" allowing us to properly query code assist for appropriate completions.
	 * 
	 * @return NameHash
	 */
	public String getNameHash()
	{

		String name = ""; //$NON-NLS-1$
		int position = getCurrentLexemeIndex();

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{
			Lexeme curLexeme = getLexemeList().get(position);

			// If we've jsut typed a ">", we jsut closed a tag.
			if (curLexeme.getText().equals(">")) //$NON-NLS-1$
			{
				return ""; //$NON-NLS-1$
			}

			// If we've just typed a "<", we will be in an error state.
			if (curLexeme.typeIndex == HTMLTokenTypes.ERROR && curLexeme.getText().equals("<")) //$NON-NLS-1$
			{
				return ""; //$NON-NLS-1$
			}

			if (curLexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				return curLexeme.getText().replaceAll("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}

			position--;
		}

		return name;

	}

	/**
	 * 
	 */
	public ICodeLocation findTarget(Lexeme lexeme)
	{
		// check for src/href attributes
		if (lexeme.getToken().getLexerGroup().equals("attribute")) //$NON-NLS-1$
		{
			LexemeList lexemeList = getFileService().getLexemeList();
			int index = getLexemeIndexFromDocumentOffset(lexeme.getStartingOffset() + 1);
			if (index < 2)
			{
				return null;
			}
			Lexeme srcLexeme = lexemeList.get(index - 2); // get lexeme two tokens ago (skip the '=')
			if (srcLexeme.getText().equalsIgnoreCase("src")) //$NON-NLS-1$
			{
				return findFile(lexeme.getText());
			}
			else if (srcLexeme.getText().equalsIgnoreCase("href")) //$NON-NLS-1$ // links to stylesheets (hopefully)
			{
				return findFile(lexeme.getText());
			}
		}
		return null;
	}

	/**
	 * Tries to return a CodeLocation for the passed in attribute value. Strips surounding quotes, and then tries to
	 * resolve filename (as absolute path, relative to doc root, or relative to current file).
	 * 
	 * @param name
	 * @return
	 */
	private ICodeLocation findFile(String name)
	{
		// strip off quotes from name, if there
		if (name.startsWith("\"") || name.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			name = name.substring(1);
		}
		if (name.endsWith("\"") || name.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			name = name.substring(0, name.length() - 1);
		}
		// what if it's an http, https, ftp, mailto link?
		if (name.startsWith("http:") || name.startsWith("https:") || name.startsWith("ftp:") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| name.startsWith("mailto:")) //$NON-NLS-1$
		{
			return null;
		}
		Lexeme destLexeme = new Lexeme(new Token(null), "", 0); //$NON-NLS-1$
		if (name.startsWith("file:")) //$NON-NLS-1$
		{
			return new CodeLocation(name, destLexeme);
		}
		// Try docRoot relative
		IProject project = getProject();
		String docRoot = getDocumentRoot(project);
		if (docRoot != null && docRoot.trim().length() > 0)
		{
			String possiblePath = docRoot + '/' + name;
			IFile file = project.getFile(possiblePath);
			if (file.exists())
			{
				return new CodeLocation(file.getLocationURI().toString(), destLexeme);
			}
		}
		// Now try relative to current file
		String pathToCurrent = getFileService().getSourceProvider().getSourceURI();
		String parentPath = pathToCurrent.substring(0, pathToCurrent.lastIndexOf('/'));
		return new CodeLocation(parentPath + '/' + name, destLexeme);
	}

	private String getDocumentRoot(IProject project)
	{
		if (project == null)
			return null;
		try
		{
			return project.getPersistentProperty(new QualifiedName("", HTMLPreviewConstants.CONTEXT_ROOT)); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private IProject getProject()
	{
		String fileURI = getFileService().getSourceProvider().getSourceURI();
		if (fileURI.startsWith("file://"))
		{
			fileURI = "file:" + fileURI.substring(7);
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		URI rootURI = root.getLocationURI();
		String rootPath = rootURI.toString();
		if (fileURI.startsWith(rootPath))
		{
			String leftever = fileURI.substring(rootPath.length());
			IPath leftoverPath = new Path(leftever);
			String firstSegment = leftoverPath.segment(0);
			IProject project = root.getProject(firstSegment);
			if (project.exists())
			{
				return project;
			}
		}
		return null;
	}

}
