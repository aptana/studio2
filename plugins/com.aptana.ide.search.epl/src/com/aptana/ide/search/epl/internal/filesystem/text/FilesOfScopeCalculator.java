/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.internal.filesystem.text;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.search.epl.filesystem.text.FileSystemTextSearchScope;

/**
 * @author Pavel Petrochenko
 */
public class FilesOfScopeCalculator
{

	private final FileSystemTextSearchScope fScope;
	private ArrayList fFiles;

	/**
	 * @param scope
	 * @param status
	 */
	public FilesOfScopeCalculator(FileSystemTextSearchScope scope,
			MultiStatus status)
	{
		this.fScope = scope;
	}

	/**
	 * @param proxy
	 * @return
	 */
	public boolean visit(File proxy)
	{
		boolean inScope = this.fScope.contains(proxy);
		if (inScope && proxy.isFile() && proxy.canRead())
		{
			this.fFiles.add(proxy);
		}
		if (proxy.isDirectory())
		{
			File[] listFiles = proxy.listFiles();
			if (listFiles != null)
			{
				for (int a = 0; a < listFiles.length; a++)
				{
					this.visit(listFiles[a]);
				}
			}
		}
		return inScope;
	}

	/**
	 * @return files
	 */
	public File[] process()
	{
		this.fFiles = new ArrayList();
		if (fScope.isOpenEditors())
		{
			try
			{
				this.fFiles = new ArrayList();
				IWorkbench workbench = SearchPlugin.getDefault().getWorkbench();
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				for (int i = 0; i < windows.length; i++)
				{
					IWorkbenchPage[] pages = windows[i].getPages();
					for (int x = 0; x < pages.length; x++)
					{
						IEditorReference[] editorRefs = pages[x]
								.getEditorReferences();
						for (int z = 0; z < editorRefs.length; z++)
						{
							IEditorPart ep = editorRefs[z].getEditor(false);
							if ((ep instanceof ITextEditor))
							{ // only dirty editors

								IEditorInput input = ep.getEditorInput();
								if (input instanceof IPathEditorInput)
								{
									File file = new File(
											((IPathEditorInput) input)
													.getPath().toOSString());
									fFiles.add(file);
								} else if (input instanceof IURIEditorInput) {
									URI uri = ((IURIEditorInput) input).getURI();
									if ("file".equals(uri.getScheme())) {
										fFiles.add(new File(uri));
									}
								} else if (input instanceof IFileEditorInput)
								{
									IFileEditorInput fi = (IFileEditorInput) input;
									fFiles.add(fi.getFile().getLocation()
											.toFile());
								}
							}
						}
					}
				}
				return (File[]) this.fFiles
						.toArray(new File[this.fFiles.size()]);
			} finally
			{
				this.fFiles = null;
			}
		} else
		{
			try
			{
				File[] roots = this.fScope.getRoots();
				for (int i = 0; i < roots.length; i++)
				{
					File resource = roots[i];
					this.visit(resource);
				}
				return (File[]) this.fFiles
						.toArray(new File[this.fFiles.size()]);
			} finally
			{
				this.fFiles = null;
			}
		}
	}
}