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
 *     Channing Walton - bug 143456
 *******************************************************************************/

package org.eclipse.eclipsemonkey.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.eclipsemonkey.EclipseMonkeyPlugin;
import org.eclipse.eclipsemonkey.MenuRunMonkeyScript;
import org.eclipse.eclipsemonkey.RunMonkeyException;
import org.eclipse.eclipsemonkey.ScriptMetadata;
import org.eclipse.eclipsemonkey.StoredScript;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 *
 */
public class RecreateMonkeyCoolbarAction implements IWorkbenchWindowActionDelegate {

	public static Hashtable toolbars = new Hashtable();
	
	/**
	 * 
	 */
	public RecreateMonkeyCoolbarAction() {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		clearTheToolbar();		
		Collection metaDatas = getAllMetadatas();
		List toolbarData = createToolbarFromMetadatas(metaDatas);
		createTheToolbar(toolbarData, action);
	}

	private Collection getAllMetadatas() {
		ArrayList result = new ArrayList();
		Iterator iter = EclipseMonkeyPlugin.getDefault().getScriptStore()
				.values().iterator();
		for (; iter.hasNext();) {
			StoredScript element = (StoredScript) iter.next();
			result.add(element.metadata);
		}
		return result;
	}

	private void clearTheToolbar() {
		CoolBarManager manager = ((WorkbenchWindow) window).getCoolBarManager();
		if(manager != null)
		{
			for (Iterator iter = toolbars.values().iterator(); iter.hasNext();) {
				IToolBarManager element = (IToolBarManager) iter.next();
				IContributionItem[] items = element.getItems();
				for (int i = 0; i < items.length; i++) {
					element.remove(items[i]);
				}
			}
		}
	}
	
	private Pattern subtoolbar_pattern = Pattern.compile("^(.+?)>(.*)$"); //$NON-NLS-1$

	class MonkeyToolbarStruct {
		String key;
		IToolBarManager toolbar;
		MonkeyToolbarStruct subtoolbar;
	}

	private void createTheToolbar(List toolbarData, final IAction action) {
		CoolBarManager outerManager = ((WorkbenchWindow) window).getCoolBarManager();

		MonkeyToolbarStruct current = new MonkeyToolbarStruct();
		current.key = ""; //$NON-NLS-1$
		current.subtoolbar = new MonkeyToolbarStruct();

		SortedSet sorted = new TreeSet();
		sorted.addAll(toolbarData);

		Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			Association element = (Association) iter.next();
			final IPath script_file_to_run = element.path;
			addNestedToolbarAction(current, outerManager, element.key, script_file_to_run,
					element.imagePath);
		}

		outerManager.update(true);
	}
	
	private void addNestedToolbarAction(MonkeyToolbarStruct current, ICoolBarManager manager,
			String toolbar_string, final IPath script_file_to_run,
			String imagePath) {
		
		if (toolbar_string == null)
			return;
		
		Matcher match = subtoolbar_pattern.matcher(toolbar_string);
		if (match.find()) {
			String primary_key = match.group(1).trim();
			String secondary_key = match.group(2).trim();
			IToolBarManager tManager = (IToolBarManager)toolbars.get(primary_key);
			
			if(tManager == null)
			{
				tManager = new ToolBarManager();
				toolbars.put(primary_key, tManager);
				manager.add(tManager);
			}

			ActionContributionItem item = new ActionContributionItem(toolbarAction(secondary_key, script_file_to_run, imagePath));
			tManager.add(item);
		}
	}
	
	private Action toolbarAction(String key, final IPath path, String imagePath) {
		final MenuRunMonkeyScript runner = new MenuRunMonkeyScript(path,
				window);
		Action action = new Action(key) {
			public void run() {
				try {
					runner.run("main", new Object[] {}); //$NON-NLS-1$
				} catch (RunMonkeyException x) {
					MessageDialog.openError(window.getShell(), x.exceptionName,
					x.errorMessage + "\n" + x.fileName + x.optionalLineNumber()); //$NON-NLS-1$
				}
			}
		};
		action.setId(key);
		if(imagePath != null)
		{
			IPath newPath = path.removeLastSegments(1).append(imagePath);
			ImageDescriptor id = ImageDescriptor.createFromFile(null, newPath.toFile().getAbsolutePath());
			if(id != null)
			{
				action.setImageDescriptor(id);
			}
		}
		return action;
	}

	private List createToolbarFromMetadatas(Collection metaDatas) {
		List toolbarData = new ArrayList();
		for (Iterator iter = metaDatas.iterator(); iter.hasNext();) {
			ScriptMetadata data = (ScriptMetadata) iter.next();
			if (data.getToolbarName() != null)
				toolbarData.add(new Association(data.getToolbarName(),
						data.getPath(), data.getAccelerator(), data.getImage()));
		}
		return toolbarData;
	}

	private static int id = 0;

	class Association implements Comparable {
		String accelerator;
		String key;
		IPath path;
		int uniqueId;
		String imagePath;

		Association(String k, IPath p, String accelerator, String imagePath) {
			this.key = k;
			this.path = p;
			this.accelerator = accelerator;
			this.uniqueId = id++;
			this.imagePath = imagePath;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object arg0) {
			Association b = (Association) arg0;
			int value = key.compareTo(b.key);
			if (value == 0) {
				if (uniqueId < b.uniqueId)
					return -1;
				else
					return 1;
			} else
				return value;
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

	private IWorkbenchWindow window;
}