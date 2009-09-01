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

package org.eclipse.eclipsemonkey.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.eclipsemonkey.EclipseMonkeyPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.Bundle;

/**
 * CreateMonkeyExamplesAction
 */
public class CreateMonkeyExamplesAction implements
		IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * 
	 */
	public CreateMonkeyExamplesAction() {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IWorkspace w = ResourcesPlugin.getWorkspace();
		IProject project = w.getRoot().getProject(Messages.CreateMonkeyExamplesAction_0);
		try {
			Bundle bundle = EclipseMonkeyPlugin.getDefault().getBundle();

			URL url = Platform.find(bundle, new Path("samples/manifest.txt")); //$NON-NLS-1$
			url = Platform.resolve(url);
			Object content = url.getContent();
			InputStream ins = (InputStream) content;
			int count = ins.available();
			BufferedReader in = new BufferedReader(new InputStreamReader(ins));
			char[] cbuf = new char[count];
			in.read(cbuf);
			in.close();
			String[] lines = new String(cbuf).split("\n"); //$NON-NLS-1$
			List manifest = new ArrayList();
			for (int i = 0; i < lines.length; i++) {
				String string = lines[i];
				string = string.trim();
				if (string.length() > 0)
					manifest.add(string);
			}

			if (!project.exists())
				project.create(null);
			project.open(null);

			String errors = ""; //$NON-NLS-1$
			for (Iterator iter = manifest.iterator(); iter.hasNext();) {
				try {
					String name = (String) iter.next();
					String[] words = name.split("/"); //$NON-NLS-1$
					String pathName = ""; //$NON-NLS-1$
					IFolder folder = null;
					for (int i = 0; i < words.length - 1; i++) {
						String string = words[i];
						pathName = pathName + string + "/"; //$NON-NLS-1$
						folder = project.getFolder(pathName);
						if (!folder.exists())
							folder.create(IResource.NONE, true, null);
					}
					IPath path = new Path("samples/" + name); //$NON-NLS-1$
					InputStream stream = EclipseMonkeyPlugin.getDefault()
							.openStream(path);
					IFile file = folder.getFile(words[words.length - 1]);
					file.create(stream, false, null);
					stream.close();
				} catch (CoreException x) {
					errors += x.toString() + "\n"; //$NON-NLS-1$
				} catch (IOException x) {
					errors += x.toString() + "\n"; //$NON-NLS-1$
				}
			}
			if (errors.length() > 0) {
				MessageDialog.openInformation(window.getShell(),
						Messages.CreateMonkeyExamplesAction_INF_TTL_Errors_creating_examples_project,
						Messages.CreateMonkeyExamplesAction_INF_MSG_Errors_creating_examples_project + errors);
			}
		} catch (CoreException x) {
			MessageDialog.openInformation(window.getShell(), Messages.CreateMonkeyExamplesAction_INF_TTL_unable_create_examples_project,
					Messages.CreateMonkeyExamplesAction_INF_MSG_unable_create_examples_project + x);
		} catch (IOException x) {
			MessageDialog.openInformation(window.getShell(), Messages.CreateMonkeyExamplesAction_INF_TTL_unable_create_examples_project,
					Messages.CreateMonkeyExamplesAction_INF_MSG_unable_create_examples_project + x);
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}