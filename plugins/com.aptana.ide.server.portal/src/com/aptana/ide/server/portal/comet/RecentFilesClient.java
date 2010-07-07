/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.server.portal.comet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorHistory;
import org.eclipse.ui.internal.EditorHistoryItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.PartListenerAdapter;
import com.aptana.ide.server.jetty.comet.CometClient;
import com.aptana.ide.server.jetty.comet.CometConstants;

import dojox.cometd.Channel;

/**
 * @author Sandip V. Chitale (schitale@aptana.com)
 */
public class RecentFilesClient extends CometClient
{

	/**
	 * LIST_RECENT_FILES
	 */
	public static final String LIST_RECENT_FILES = "listRecentFiles"; //$NON-NLS-1$

	/**
	 * RECENT_FILES_CHANNEL
	 */
	public static final String RECENT_FILES_CHANNEL = "/portal/recentFiles"; //$NON-NLS-1$

	/**
	 * PROJECTS
	 */
	public static final String PROJECTS = "projects"; //$NON-NLS-1$

	/**
	 * FILES
	 */
	public static final String FILES = "files"; //$NON-NLS-1$

	/**
	 * NAME
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * OPEN_FILE
	 */
	public static final String OPEN_FILE = "openFile"; //$NON-NLS-1$

	/**
	 * PROJECT
	 */
	public static final String PROJECT = "project"; //$NON-NLS-1$

	/**
	 * FILE
	 */
	public static final String FILE = "file"; //$NON-NLS-1$
	
	private IPartListener partListener = new PartListenerAdapter()
    {
        @Override
        public void partOpened(IWorkbenchPart part)
        {
            if (part instanceof IEditorPart)
            {
            	publishRecentFiles();
            }
        }
        @Override
        public void partClosed(IWorkbenchPart part)
        {
            if (part instanceof IEditorPart)
            {
            	publishRecentFiles();          
            }
        }
    };
    
    private IWindowListener windowListener = new IWindowListener()
    {
        public void windowActivated(IWorkbenchWindow window) {}
        public void windowClosed(IWorkbenchWindow window) {
            IPartService partService = window.getPartService();
            if (partService != null)
            {
            	partService.removePartListener(partListener);
            }
        }
        public void windowDeactivated(IWorkbenchWindow window) {}
        public void windowOpened(IWorkbenchWindow window)
        {
            IPartService partService = window.getPartService();
            if (partService != null)
            {
            	partService.addPartListener(partListener);
            }
        }
    };

	/**
	 * RecentFiles client
	 */
	public RecentFilesClient()
	{
		init();
	}
   
    private void init () {
        // Add actions to exiting windows.
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow workbenchWindow : workbenchWindows)
        {
            IPartService partService = workbenchWindow.getPartService();
        	if (partService != null)
            {
            	partService.addPartListener(partListener);
            }
        }
       
        // Listen on any future windows
        PlatformUI.getWorkbench().addWindowListener(windowListener);
    }

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		super.destroy();
		
        // Remove listener from windows.
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow workbenchWindow : workbenchWindows)
        {
        	IPartService partService = workbenchWindow.getPartService();
        	if (partService != null) {
        		partService.removePartListener(partListener);
        	}
        }
        
        partListener = null;
        
        PlatformUI.getWorkbench().removeWindowListener(windowListener);
        windowListener = null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (RECENT_FILES_CHANNEL.equals(toChannel))
		{
			Map requestData = (Map) request;
			if (LIST_RECENT_FILES.equals(requestData.get(CometConstants.REQUEST)))
			{
				return listRecentFiles();
			}
			else if (OPEN_FILE.equals(requestData.get(CometConstants.REQUEST)))
			{
				String project = (String) requestData.get(PROJECT);
				String file = (String) requestData.get(FILE);
				if (project != null && file != null)
				{
					openFile(project, file);
				}
			}
		}
		return null;
	}

	/**
	 * Publish recent files
	 */
	protected void publishRecentFiles()
	{
		if (bayeux == null)
			return;
		Channel listChannel = bayeux.getChannel(getRecentFilesChannel(), true);
		listChannel.publish(this.client, listRecentFiles(), Long.toString(System.currentTimeMillis()));
	}

	/**
	 * List the recent files
	 * 
	 * @return - map representing recent files model
	 */
	protected Map<Object, Object> listRecentFiles()
	{
		Map<Object, Object> returnData = new HashMap<Object, Object>();
		returnData.put(CometConstants.RESPONSE, LIST_RECENT_FILES);
		Map<String, List<String>> projectFilesMap = new HashMap<String, List<String>>();
		EditorHistory editorHistory = getEditorHistory();
		if (editorHistory != null)
		{
			EditorHistoryItem[] editorHistoryItems = editorHistory.getItems();
			for (EditorHistoryItem editorHistoryItem : editorHistoryItems)
			{
                try {
                    if (!editorHistoryItem.isRestored()) {
                        editorHistoryItem.restoreState();
                    }
                } catch (Exception e) {
                }

				IEditorInput editorInput = editorHistoryItem.getInput();
				if (editorInput instanceof FileEditorInput)
				{
					FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
					IFile file = fileEditorInput.getFile();
					if (file != null)
					{
						IProject project = file.getProject();
						String projectName = project.getName();
						List<String> list = projectFilesMap.get(projectName);
						if (list == null)
						{
							list = new LinkedList<String>();
							projectFilesMap.put(projectName, list);
						}
						list.add(editorHistoryItem.getName());
					}
				}
			}
		}

		// Array of projects
		List<Map<String, Object>> projectMapsList = new LinkedList<Map<String, Object>>();
		Set<String> projectNames = projectFilesMap.keySet();
		for (String projectName : projectNames)
		{
			// Per project map
			Map<String, Object> projectMap = new HashMap<String, Object>();
			// Project name
			projectMap.put(NAME, projectName);
			// Array file names
			List<Map<String, String>> fileMapsList = new LinkedList<Map<String, String>>();
			List<String> projectFiles = projectFilesMap.get(projectName);
			for (String file : projectFiles)
			{
				Map<String, String> fileMap = new HashMap<String, String>();
				fileMap.put(NAME, file);
				fileMapsList.add(fileMap);
			}
			projectMap.put(FILES, fileMapsList);
			projectMapsList.add(projectMap);
		}
		returnData.put(PROJECTS, projectMapsList);
		return returnData;
	}

	private void openFile(String project, String file)
	{
		EditorHistory editorHistory = getEditorHistory();
		if (editorHistory != null)
		{
			EditorHistoryItem[] editorHistoryItems = editorHistory.getItems();
			for (EditorHistoryItem editorHistoryItem : editorHistoryItems)
			{
				// File name matches
				if (editorHistoryItem.getName().equals(file))
				{
					// Now check on project
					IEditorInput editorInput = editorHistoryItem.getInput();
					if (editorInput instanceof FileEditorInput)
					{
						FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
						IFile fileEditorInputFile = fileEditorInput.getFile();
						if (fileEditorInputFile != null)
						{
							IProject fileEditorInputFileProject = fileEditorInputFile.getProject();
							if (fileEditorInputFileProject.getName().equals(project))
							{
								open(editorHistory, editorHistoryItem);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the recent files channel
	 * 
	 * @return - recent files channel
	 */
	protected String getRecentFilesChannel()
	{
		return RECENT_FILES_CHANNEL;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { getRecentFilesChannel() };
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}

	private EditorHistory getEditorHistory()
	{
		return ((Workbench) PlatformUI.getWorkbench()).getEditorHistory();
	}
	
	/**
     * Reopens the editor for the given history item.
     */
    private void open(final EditorHistory history, final EditorHistoryItem item) {
    	UIJob uiJob = new UIJob("Opening file.") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				IWorkbenchPage page = CoreUIUtils.getActivePage();
		        if (page != null) {
		            try {
		                String itemName = item.getName();
		                if (!item.isRestored()) {
		                    item.restoreState();
		                }
		                IEditorInput input = item.getInput();
		                IEditorDescriptor desc = item.getDescriptor();
		                if (input == null || desc == null) {
		                    history.remove(item);
		                } else {
		                    page.openEditor(input, desc.getId());
		                }
		            } catch (PartInitException pe) {
		            	CoreUIUtils.showError(pe.getMessage(), pe);
		                history.remove(item);
		            }
		        }
				return Status.OK_STATUS;
			}
    		
    	};
    	uiJob.setPriority(UIJob.INTERACTIVE);
    	uiJob.schedule();
    }
}
