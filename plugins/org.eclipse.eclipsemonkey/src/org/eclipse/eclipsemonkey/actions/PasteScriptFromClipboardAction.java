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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.eclipsemonkey.EclipseMonkeyPlugin;
import org.eclipse.eclipsemonkey.ScriptMetadata;
import org.eclipse.eclipsemonkey.language.IMonkeyLanguageFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * PasteScriptFromClipboardAction
 */
public class PasteScriptFromClipboardAction implements
		IWorkbenchWindowActionDelegate, IObjectActionDelegate {

	private static final String ECLIPSE_MONKEY_PROJECT = Messages.PasteScriptFromClipboardAction_Monkey_project_name;
	private static final String ECLIPSE_MONKEY_DIRECTORY = "scripts"; //$NON-NLS-1$

	/**
	 * 
	 */
	public PasteScriptFromClipboardAction() {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		TextAndRTF text = getTextFromClipboard();
		Collection scripts = extractScriptsFromText(text);
		for (Iterator iter = scripts.iterator(); iter.hasNext();) {
			try {
				String scriptText = (String) iter.next();
				scriptText = collapseEscapedNewlines(scriptText);
				IFolder destination = findDestinationFor(scriptText);
				IFile file = createScriptFile(destination, scriptText);
				highlightNewScriptInNavigator(file);
			} catch (CoreException x) {
				MessageDialog.openInformation(shell, Messages.PasteScriptFromClipboardAction_INF_TTL_Unable_create_examples_project,
						Messages.PasteScriptFromClipboardAction_INF_MSG_Unable_create_examples_project + x);
			} catch (IOException x) {
				MessageDialog.openInformation(shell, Messages.PasteScriptFromClipboardAction_INF_TTL_Unable_create_examples_project,
						Messages.PasteScriptFromClipboardAction_INF_MSG_Unable_create_examples_project + x);
			}
		}
		if (scripts.isEmpty()) {
			MessageDialog
					.openInformation(
							shell,
							Messages.PasteScriptFromClipboardAction_INF_TTL_Cant_find_script_on_clipboard,
							Messages.PasteScriptFromClipboardAction_INF_MSG_Cant_find_script_on_clipboard);

		}
	}

	String collapseEscapedNewlines(String input) {
		Pattern pattern = Pattern.compile("\\\\(\n|(\r\n?))"); //$NON-NLS-1$
		Matcher match = pattern.matcher(input);
		String result = match.replaceAll(""); //$NON-NLS-1$
		return result;
	}

	private void highlightNewScriptInNavigator(IFile file)
			throws PartInitException {
		if (window != null) {
			window.getActivePage().showView(
					"org.eclipse.ui.views.ResourceNavigator"); //$NON-NLS-1$
			IViewReference[] refs = window.getActivePage().getViewReferences();
			for (int i = 0; i < refs.length; i++) {
				IViewReference reference = refs[i];
				if (reference.getId().equals(
						"org.eclipse.ui.views.ResourceNavigator")) { //$NON-NLS-1$
					ResourceNavigator nav = (ResourceNavigator) reference
							.getView(true);
					IStructuredSelection sel = new StructuredSelection(file);
					nav.selectReveal(sel);
				}
			}
		}
	}

	private IFolder findDestinationFor(String script) throws CoreException {
		if (selection != null && selection.getFirstElement() instanceof IFolder) {
			IFolder element = (IFolder) selection.getFirstElement();
			if ((element).getName().equals(ECLIPSE_MONKEY_DIRECTORY)) {
				return element;
			}
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		IProject project = null;
		for (int i = 0; i < projects.length; i++) {
			IProject p = projects[i];
			if (p.getName().equals(ECLIPSE_MONKEY_PROJECT)) {
				project = p;
				break;
			}
		}
		if (project == null) {
			project = workspace.getRoot().getProject(ECLIPSE_MONKEY_PROJECT);
			project.create(null);
			project.open(null);
		}
		IFolder folder = project.getFolder(ECLIPSE_MONKEY_DIRECTORY);
		if (!folder.exists())
			folder.create(IResource.NONE, true, null);
		return folder;
	}

	private IFile createScriptFile(IFolder destination, String script)
			throws CoreException, IOException {
		// FIXME This just assumes that the script must be JavaScript!
		IMonkeyLanguageFactory langFactory = (IMonkeyLanguageFactory) EclipseMonkeyPlugin.getDefault().getLanguageStore().get("js"); //$NON-NLS-1$
		ScriptMetadata metadata = langFactory.getScriptMetadata(script);
		String basename = metadata.getReasonableFilename();
		int ix = basename.lastIndexOf("."); //$NON-NLS-1$
		if (ix > 0) {
			basename = basename.substring(0, ix);
		}
		IResource[] members = destination.members(0);
		Pattern suffix = Pattern.compile(basename + "(-(\\d+))?\\.js"); //$NON-NLS-1$
		int maxsuffix = -1;
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				String filename = file.getName();
				Matcher match = suffix.matcher(filename);
				if (match.matches()) {
					if (match.group(2) == null) {
						maxsuffix = Math.max(maxsuffix, 0);
					} else {
						int n = Integer.parseInt(match.group(2));
						maxsuffix = Math.max(maxsuffix, n);
					}
				}
			}
		}
		String filename = (maxsuffix == -1) ? filename = basename + ".js" //$NON-NLS-1$
				: basename + "-" + (maxsuffix + 1) + ".js"; //$NON-NLS-1$ //$NON-NLS-2$

		ByteArrayInputStream stream = new ByteArrayInputStream(script
				.getBytes());
		IFile file = destination.getFile(filename);
		file.create(stream, false, null);
		stream.close();
		return file;
	}

	private TextAndRTF getTextFromClipboard() {
		TextAndRTF result = new TextAndRTF();
		Clipboard clipboard = new Clipboard(shell.getDisplay());
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			RTFTransfer rtfTransfer = RTFTransfer.getInstance();
			result.text = (String) clipboard.getContents(textTransfer);
			result.rtf = (String) clipboard.getContents(rtfTransfer);
			return result;
		} finally {
			clipboard.dispose();
		}
	}

	private Collection extractScriptsFromText(TextAndRTF text) {
		Collection result = new ArrayList();
		Pattern pattern = Pattern.compile(
				EclipseMonkeyPlugin.PUBLISH_BEFORE_MARKER + "\\s*(.*?)\\s*" //$NON-NLS-1$
						+ EclipseMonkeyPlugin.PUBLISH_AFTER_MARKER,
				Pattern.DOTALL);
		Pattern crpattern = Pattern.compile("\r\n?"); //$NON-NLS-1$
		if (text.text != null) {
			Matcher matcher = pattern.matcher(text.text);
			while (matcher.find()) {
				String string = matcher.group(1);
				Matcher crmatch = crpattern.matcher(string);
				string = crmatch.replaceAll("\n"); //$NON-NLS-1$
				if (string.indexOf("\n") >= 0) { //$NON-NLS-1$
					result.add(string);
				}
			}
		}
		if (result.isEmpty() && text.rtf != null) {
			Matcher matcher = pattern.matcher(text.rtf);
			Pattern escapesPattern = Pattern.compile("\\\\(.)"); //$NON-NLS-1$
			while (matcher.find()) {
				String string = matcher.group(1);
				string = string.replaceAll("\\\\line", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				Matcher escapes = escapesPattern.matcher(string);
				string = escapes.replaceAll("$1"); //$NON-NLS-1$
				Matcher crmatch = crpattern.matcher(string);
				string = crmatch.replaceAll("\n"); //$NON-NLS-1$
				if (string.indexOf("\n") >= 0) { //$NON-NLS-1$
					result.add(string);
				}
			}
		}
		return result;
	}

	private IStructuredSelection selection = null;

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection) selection;
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
		this.shell = window.getShell();
		this.window = window;
	}

	private Shell shell;

	private IWorkbenchWindow window;

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
		this.window = null;
	}

	class TextAndRTF {
		String text;

		String rtf;
	}
}