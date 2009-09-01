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
 *     Jeff Mesnil - bug 132601
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.eclipsemonkey.utils.UIUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;
import org.osgi.framework.Bundle;

/**
 * ScriptMetadata
 */
public class ScriptMetadata
{

	private IPath path;

	private String menuName;

	private String toolbarName;

	private String image;

	private String onLoadFunction = null;

	private String scopeName;

	private String comment;

	private String source;

	private List<DOMDescriptor> doms = new ArrayList<DOMDescriptor>();

	private List<Subscription> subscriptions = new ArrayList<Subscription>();

	private String accelerator;

	/**
	 * @param string
	 */
	public void setMenuName(String string)
	{
		this.menuName = string;
	}

	/**
	 * @param string
	 */
	public void setToolbarName(String string)
	{
		this.toolbarName = string;
	}

	/**
	 * @return
	 */
	public String getToolbarName()
	{
		return this.toolbarName;
	}

	/**
	 * @param string
	 */
	public void setImage(String string)
	{
		this.image = string;
	}

	/**
	 * @return
	 */
	public String getImage()
	{
		return this.image;
	}

	/**
	 * @param string
	 */
	public void setOnLoadFunction(String string)
	{
		this.onLoadFunction = string;
	}

	/**
	 * @param path
	 */
	public void setPath(IPath path)
	{
		this.path = path;

	}

	/**
	 * @return IPath
	 */
	public IPath getPath()
	{
		return path;
	}

	/**
	 * @return String
	 */
	public String getMenuName()
	{
		return menuName;
	}

	/**
	 * @return String
	 */
	public String getOnLoadFunction()
	{
		return this.onLoadFunction;
	}

	/**
	 * @return String
	 */
	public String getScopeName()
	{
		return scopeName;
	}

	/**
	 * @param s
	 */
	public void setScopeName(String s)
	{
		scopeName = s;
	}

	/**
	 * @return List
	 */
	public List<DOMDescriptor> getDOMs()
	{
		return doms;
	}

	/**
	 * @return String
	 */
	public String getReasonableFilename()
	{
		if (path != null)
			return path.toFile().getName();
		if (menuName != null && !menuName.equals("")) //$NON-NLS-1$
		{
			String result = menuName;
			result = result.replaceAll(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			Pattern illegalChars = Pattern.compile("[^\\p{Alnum}_-]"); //$NON-NLS-1$
			Matcher match = illegalChars.matcher(result);
			result = match.replaceAll(""); //$NON-NLS-1$
			if (!result.equals("")) //$NON-NLS-1$
				return result + ".js"; //$NON-NLS-1$
		}
		return "script.js"; //$NON-NLS-1$
	}

	/**
	 * @param plugin_id
	 * @return boolean
	 */
	public boolean containsDOM_by_plugin(String plugin_id)
	{
		for (Iterator<DOMDescriptor> iter = doms.iterator(); iter.hasNext();)
		{
			DOMDescriptor element = iter.next();
			if (element.plugin_name.equals(plugin_id))
				return true;
		}
		return false;
	}

	/**
	 * @param window
	 * @return boolean
	 */
	public boolean ensure_doms_are_loaded(IWorkbenchWindow window)
	{
		String missing_plugin_names = ""; //$NON-NLS-1$
		URLtoPluginMap missing_urls = new URLtoPluginMap();
		for (Iterator<DOMDescriptor> iter = doms.iterator(); iter.hasNext();)
		{
			DOMDescriptor element = iter.next();
			Bundle b = Platform.getBundle(element.plugin_name);
			if (b == null)
			{
				missing_plugin_names += "     " + element.plugin_name + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
				missing_urls.add(element);
			}
			else if (b.getState() == Bundle.UNINSTALLED)
			{
				missing_plugin_names += "     " + element.plugin_name + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (missing_plugin_names.length() > 0)
		{
			missing_plugin_names = missing_plugin_names.substring(0, missing_plugin_names.length() - 1);
			String choice = notifyMissingDOMs(missing_plugin_names);
			if (choice.equals(Messages.ScriptMetadata_LBL_Edit_script))
			{
				openEditor();
			}
			else if (choice.equals(Messages.ScriptMetadata_LBL_Cancel_script))
			{
				return false;
			}
			else
			{
				launchUpdateInstaller(missing_urls);
			}
			return false;
		}
		return true;
	}

	class URLtoPluginMap
	{
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		Iterator<String> iterator()
		{
			return map.keySet().iterator();
		}

		String getPluginNames(String url)
		{
			Set<String> ids = map.get(url);
			String idstr = ""; //$NON-NLS-1$
			for (Iterator<String> iterator = ids.iterator(); iterator.hasNext();)
			{
				String id = iterator.next();
				idstr += id + ", "; //$NON-NLS-1$
			}
			idstr = idstr.substring(0, idstr.length() - 2);
			return idstr;
		}

		void add(DOMDescriptor domdesc)
		{
			Set<String> ids = map.get(domdesc.url);
			if (ids == null)
				ids = new HashSet<String>();
			ids.add(domdesc.plugin_name);
			map.put(domdesc.url, ids);
		}
	}

	private void openEditor()
	{
		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
					path.toFile().getName());
			if (desc == null)
			{
				desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("foo.txt"); //$NON-NLS-1$
			}
			page.openEditor(UIUtils.createJavaFileEditorInput(path.toFile()), desc.getId());
		}
		catch (PartInitException x)
		{
			MessageDialog.openError(null, Messages.ScriptMetadata_ERR_TTL_Unable_open_editor, MessageFormat.format(
					Messages.ScriptMetadata_ERR_MSG_Unable_open_editor, path.toFile().getName(), x.toString()));
		}
	}

	private void launchUpdateInstaller(URLtoPluginMap missing_urls)
	{
		UpdateSearchScope scope = new UpdateSearchScope();
		String[] skips = {};
		for (Iterator<String> iter = missing_urls.iterator(); iter.hasNext();)
		{
			String url = iter.next();
			try
			{
				String idstr = missing_urls.getPluginNames(url);
				boolean isPlural = idstr.indexOf(",") >= 0; //$NON-NLS-1$
				String title = MessageFormat.format(Messages.ScriptMetadata_TTL_Update_site_singular, idstr);
				if (isPlural)
				{
					title = MessageFormat.format(Messages.ScriptMetadata_TTL_Update_site_plural, idstr);
				}
				scope.addSearchSite(title, new URL(url), skips);
			}
			catch (MalformedURLException x)
			{
				// ignore
			}
		}
		UpdateSearchRequest request = new UpdateSearchRequest(UpdateSearchRequest.createDefaultSiteSearchCategory(),
				scope);
		UpdateJob job = new UpdateJob(Messages.ScriptMetadata_TTL_Update_job, request);
		Shell shell = Workbench.getInstance().getWorkbenchWindows()[0].getShell();
		UpdateManagerUI.openInstaller(shell, job);
	}

	private String notifyMissingDOMs(String missing_plugin_names)
	{
		boolean isPlural = missing_plugin_names.indexOf("\n") >= 0; //$NON-NLS-1$
		String installChoice = Messages.ScriptMetadata_LBL_Install_plugin;
		String title = Messages.ScriptMetadata_LBL_Missing_DOM;
		String msg = MessageFormat.format(Messages.ScriptMetadata_MSG_script_0_requires_DOM_1, this.path.toFile()
				.getName(), missing_plugin_names);
		if (isPlural)
		{
			installChoice = Messages.ScriptMetadata_LBL_Install_plugins;
			title = Messages.ScriptMetadata_LBL_Missing_DOMs;
			msg = MessageFormat.format(Messages.ScriptMetadata_MSG_script_0_requires_DOMs_1, this.path.toFile()
					.getName(), missing_plugin_names);
		}
		String[] choices = new String[] { Messages.ScriptMetadata_LBL_Cancel_script,
				Messages.ScriptMetadata_LBL_Edit_script, installChoice };
		MessageDialog dialog = new MessageDialog(null, title, null, msg, MessageDialog.WARNING, choices, 2);
		int result = dialog.open();
		String choice = choices[result];
		return choice;
	}

	/**
	 * @param key
	 */
	public void setKey(String key)
	{
		this.accelerator = key;
	}

	/**
	 * @return String
	 */
	public String getAccelerator()
	{
		return accelerator;
	}

	/**
	 * @return String
	 */
	public boolean hasAccelerator()
	{
		return accelerator != null;
	}

	/**
	 * @return List
	 */
	public List<Subscription> getSubscriptions()
	{
		return subscriptions;
	}

	/**
	 * 
	 */
	public void subscribe()
	{
		for (int i = 0; i < subscriptions.size(); i++)
		{
			Subscription subscription = (Subscription) subscriptions.get(i);
			subscription.subscribe(path);
		}
	}

	/**
	 * 
	 */
	public void unsubscribe()
	{
		for (int i = 0; i < subscriptions.size(); i++)
		{
			Subscription subscription = (Subscription) subscriptions.get(i);
			subscription.unsubscribe();
		}
	}

	/**
	 * Gets the comment
	 * 
	 * @return - the comment
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * Sets the comment
	 * 
	 * @param comment
	 *            - new comment
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the source
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

}
