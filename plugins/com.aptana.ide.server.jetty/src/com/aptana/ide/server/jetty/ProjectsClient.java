/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.server.jetty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.server.jetty.comet.CometClient;
import com.aptana.ide.server.jetty.comet.CometConstants;

import dojox.cometd.Channel;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ProjectsClient extends CometClient implements IResourceChangeListener
{

	/**
	 * AIR_TYPE
	 */
	public static final String AIR_TYPE = "air"; //$NON-NLS-1$

	/**
	 * WEB_TYPE
	 */
	public static final String WEB_TYPE = "web"; //$NON-NLS-1$

	/**
	 * RAILS_TYPE
	 */
	public static final String RAILS_TYPE = "rails"; //$NON-NLS-1$

	/**
	 * UNKNOWN_TYPE
	 */
	public static final String UNKNOWN_TYPE = "unknown"; //$NON-NLS-1$

	/**
	 * PHP_TYPE
	 */
	public static final String PHP_TYPE = "php"; //$NON-NLS-1$
	
	/**
	 * PYTHON_TYPE
	 */
	public static final String PYTHON_TYPE = "python"; //$NON-NLS-1$

	/**
	 * JAVA_TYPE
	 */
	public static final String JAVA_TYPE = "java"; //$NON-NLS-1$
	
	/**
	 * TYPE
	 */
	public static final String TYPE = "type"; //$NON-NLS-1$
	
	/**
	 * PYTHON_PROJECT
	 */
	public static final String PYTHON_PROJECT = "org.python.pydev.pythonNature"; //$NON-NLS-1$
	
	/**
	 * RAILS_PROJECT
	 */
	public static final String RAILS_PROJECT = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$

	/**
	 * JAXER_PROJECT
	 */
	public static final String JAXER_PROJECT = "com.aptana.ide.framework.jaxernature"; //$NON-NLS-1$

	/**
	 * WEB_PROJECT
	 */
	public static final String WEB_PROJECT = "com.aptana.ide.project.nature.web"; //$NON-NLS-1$

	/**
	 * AIR_PROJECT
	 */
	public static final String AIR_PROJECT = "com.aptana.ide.apollo.apollonature"; //$NON-NLS-1$

	/**
	 * PHP_PROJECT
	 */
	public static final String PHP_PROJECT = "com.aptana.ide.editor.php.phpnature"; //$NON-NLS-1$
	
	/**
	 * WTP JAVA Web Project
	 */
	public static final String WTP_JAVA_PROJECT = "org.eclipse.wst.common.project.facet.core.nature"; //$NON-NLS-1$

	/**
	 * LIST_PROJECTS
	 */
	public static final String LIST_PROJECTS = "listProjects"; //$NON-NLS-1$

	/**
	 * NAME
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * PROJECTS_CHANNEL
	 */
	public static final String PROJECTS_CHANNEL = "/portal/projects"; //$NON-NLS-1$

	/**
	 * PROJECTS
	 */
	public static final String PROJECTS = "projects"; //$NON-NLS-1$

	/**
	 * PROJECTS_EXIST
	 */
	public static final String PROJECTS_EXIST = "projectsExist"; //$NON-NLS-1$

	/**
	 * PROJECTS_ARRAY
	 */
	public static final String PROJECTS_ARRAY = "projects"; //$NON-NLS-1$

	/**
	 * Projects client
	 */
	public ProjectsClient()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#destroy()
	 */
	public void destroy()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.destroy();
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (PROJECTS_CHANNEL.equals(toChannel))
		{
			return listProjects();
		}
		return null;
	}

	/**
	 * Publish projects
	 */
	protected void publishProjects()
	{
		if (bayeux != null) {
			Channel listChannel = bayeux.getChannel(getProjectsChannel(), true);
			listChannel.publish(this.client, listProjects(), Long.toString(System.currentTimeMillis()));
		}
	}

	/**
	 * List the project
	 * 
	 * @return - map representing project model
	 */
	protected Map<Object, Object> listProjects()
	{
		IProject[] projects = CoreUIUtils.getWorkspaceRoot().getProjects();
		Map<Object, Object> returnData = new HashMap<Object, Object>();
		returnData.put(PROJECTS_EXIST, Boolean.valueOf(projects.length > 0));
		returnData.put(CometConstants.RESPONSE, LIST_PROJECTS);
		List<Object> names = new ArrayList<Object>();
		for (IProject project : projects)
		{
			if (project.exists() && project.isOpen())
			{
				StringBuilder type = new StringBuilder(); // $NON-NLS-1$
				Map<Object, Object> projectData = new HashMap<Object, Object>();
				try
				{
					// TODO If we have conflicting types, some should override, some should go to unknown
					// i.e Java and Rails override all, but if we have both then we should make the user choose (maybe it's JRuby?)
					// if PHP and web, it's really php
					// if anything plus web, it's pretty much the other thing.
					String[] natureIds = project.getDescription().getNatureIds();
					// The first nature in that list is the one that we should check
					if (natureIds != null && natureIds.length > 0)
					{
						for (String nature : natureIds)
						{
							type.append(nature);
							type.append(',');
						}
						if (type.length() > 0) // remove the last comma
						{
							type.deleteCharAt(type.length() - 1);
						}
					}
				}
				catch (CoreException e)
				{
				}
				projectData.put(TYPE, type.toString());
				projectData.put(NAME, project.getName());
				names.add(projectData);
			}
		}
		returnData.put(PROJECTS_ARRAY, names);
		return returnData;
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event)
	{
		IResourceDelta delta = event.getDelta();
		if (delta != null)
		{
			IResourceDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++)
			{
				IProject project = null;
				if (deltas[i].getKind() == IResourceDelta.REMOVED && deltas[i].getMovedToPath() == null
						&& deltas[i].getResource() instanceof IProject)
				{
					project = (IProject) deltas[i].getResource();
				}
				else
				{
					project = deltas[i].getResource().getProject();
				}
				if (project != null)
				{
					publishProjects();
				}
			}
		}
		else
		{
			if (event.getType() == IResourceChangeEvent.PRE_DELETE && event.getResource() instanceof IProject)
			{
				IProject project = (IProject) event.getResource();
				if (project != null)
				{
					publishProjects();
				}
			}
		}
	}

	/**
	 * Gets the projects channel
	 * 
	 * @return - projects channel
	 */
	protected String getProjectsChannel()
	{
		return PROJECTS_CHANNEL;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { getProjectsChannel() };
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return Long.toString(System.currentTimeMillis());
	}

}