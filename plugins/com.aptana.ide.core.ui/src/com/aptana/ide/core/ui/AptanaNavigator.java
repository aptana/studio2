/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.core.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * The Aptana Navigator is a direct subclass of ResourceNavigator, used so that we can add items to it directly
 * 
 * @author Ingo Muschenetz
 */
public class AptanaNavigator extends ResourceNavigator implements IShowInTarget
{
	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.core.ui.AptanaNavigator"; //$NON-NLS-1$

	/**
	 * EXTENSION_NAME
	 */
	public static final String EXTENSION_NAME = "decorator"; //$NON-NLS-1$

	/**
	 * EXTENSION_POINT
	 */
	public static final String EXTENSION_POINT = CoreUIPlugin.ID + "." + EXTENSION_NAME; //$NON-NLS-1$

	/**
	 * CLASS_ATTRIBUTE
	 */
	public static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private Map<String, List<IResourceChangeListener>> naturesToListenerLists = null;
	private Map<IProject, String[]> projectsToNatureArrays = new HashMap<IProject, String[]>();
	private List<IResourceChangeListener> globalListeners = null;

	private IHandlerActivation searchHandlerActivation;

	private static final IPartListener partListener = new PartListenerAdapter()
    {
        @Override
        public void partOpened(IWorkbenchPart part)
        {
            if (part instanceof IViewPart)
            {
                IViewPart viewPart = (IViewPart) part;
                if (AptanaNavigator.ID.equals(viewPart.getSite().getId()))
                {
                    IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
                    for (IConfigurationElement element : elements)
                    {
                        if (!EXTENSION_NAME.equals(element.getName()))
                            continue;
                        
                        String className = element.getAttribute(CLASS_ATTRIBUTE);
                        if (className != null)
                        {
                            try
                            {
                                Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
                                if (client instanceof INavigatorDecorator)
                                {
                                    ((INavigatorDecorator) client).addDecorator(((AptanaNavigator) viewPart).getTreeViewer().getTree());
                                }
                            }
                            catch (CoreException e)
                            {
                            }
                        }
                    }
                    viewPart.getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
                }
            }
        }
    };

	/**
	 * AptanaNavigator
	 */
	public AptanaNavigator()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new org.eclipse.core.resources.IResourceChangeListener()
				{

					public void resourceChanged(IResourceChangeEvent event)
					{
						notifyListeners(event);
					}

				});
	}

	private void notifyListeners(IResourceChangeEvent event)
	{
		if (naturesToListenerLists == null)
		{
			loadConfigurationExtensionPoints();
		}
		IResourceDelta delta = event.getDelta();
		if (delta != null)
		{
			IResourceDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++)
			{
				if (deltas[i].getKind() == IResourceDelta.REMOVED && deltas[i].getMovedToPath() == null
						&& deltas[i].getResource() instanceof IProject)
				{
					fireCollectDeletions((IProject) deltas[i].getResource(), event);
				}
				else
				{
					IProject project = deltas[i].getResource().getProject();
					if (project != null)
					{
						fireResourceEvent(project, event);
						projectsToNatureArrays.remove(project);
					}
				}
			}
		}
		else
		{
			if (event.getType() == IResourceChangeEvent.PRE_DELETE && event.getResource() instanceof IProject)
			{
				IProject project = (IProject) event.getResource();
				String[] natures;
				try
				{
					natures = project.getDescription().getNatureIds();
					projectsToNatureArrays.put(project, natures);
				}
				catch (CoreException e)
				{
				}
			}
		}
	}

	/**
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		super.setFocus();
		//WORKAROUND Bug in handlers in Eclipse 3.2 (Related issue STU-1570)
		IHandlerService serv = (IHandlerService) getViewSite().getService(IHandlerService.class);
		if (searchHandlerActivation != null)
		{
			serv.deactivateHandler(searchHandlerActivation);
		}
		ActionHandler searchHandler = new ActionHandler(new org.eclipse.search.internal.ui.OpenSearchDialogAction());
		searchHandlerActivation = serv.activateHandler("org.eclipse.search.ui.openSearchDialog", searchHandler); //$NON-NLS-1$   
	}

	public void dispose()
	{
		//WORKAROUND Bug in handlers in Eclipse 3.2 (Related issue STU-1570)
		if (searchHandlerActivation != null)
		{
			IHandlerService serv = (IHandlerService) getViewSite().getService(IHandlerService.class);
			serv.deactivateHandler(searchHandlerActivation);
		}
		super.dispose();
	}

	private void fireCollectDeletions(IProject project, IResourceChangeEvent event)
	{
		final List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();
		String[] natures = new String[0];
		try
		{
			natures = project.getDescription().getNatureIds();
		}
		catch (CoreException e)
		{
			if (projectsToNatureArrays.containsKey(project))
			{
				natures = projectsToNatureArrays.remove(project);
			}
		}
		for (int i = 0; i < natures.length; i++)
		{
			if (naturesToListenerLists.containsKey(natures[i]))
			{
				List<IResourceChangeListener> natureListeners = naturesToListenerLists.get(natures[i]);
				for (int j = 0; j < natureListeners.size(); j++)
				{
					configurations.addAll(Arrays.asList(natureListeners.get(j).getDeleteCandidates(project, event)));
				}
			}
			for (int j = 0; j < globalListeners.size(); j++)
			{
				configurations.addAll(Arrays.asList(globalListeners.get(j).getDeleteCandidates(project, event)));
			}
		}
		if (configurations.size() > 0)
		{
			UIJob dialogJob = new UIJob("Open delete candidates") //$NON-NLS-1$
			{

				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					DeleteConfigurationsDialog dialog = new DeleteConfigurationsDialog(AptanaNavigator.this
							.getViewSite().getShell(), configurations);
					dialog.open();
					return Status.OK_STATUS;
				}

			};
			dialogJob.schedule();
		}

	}

	private void fireResourceEvent(IProject project, IResourceChangeEvent event)
	{
		try
		{
			String[] natures = project.getDescription().getNatureIds();
			for (int i = 0; i < natures.length; i++)
			{
				if (naturesToListenerLists.containsKey(natures[i]))
				{
					List<IResourceChangeListener> natureListeners = naturesToListenerLists.get(natures[i]);
					for (int j = 0; j < natureListeners.size(); j++)
					{
						natureListeners.get(j).resourceChanged(event);
					}
				}
				for (int j = 0; j < globalListeners.size(); j++)
				{
					globalListeners.get(j).resourceChanged(event);
				}
			}
		}
		catch (CoreException e)
		{
		}
	}

	private void loadConfigurationExtensionPoints()
	{
		naturesToListenerLists = new HashMap<String, List<IResourceChangeListener>>();
		globalListeners = new ArrayList<IResourceChangeListener>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(CoreUIPlugin.ID + ".configuration"); //$NON-NLS-1$
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				String nature = ce[j].getAttribute("nature"); //$NON-NLS-1$
				String listenerClass = ce[j].getAttribute("handler"); //$NON-NLS-1$
				if (listenerClass != null)
				{
					try
					{
						Object listener = ce[j].createExecutableExtension("handler"); //$NON-NLS-1$
						if (listener instanceof IResourceChangeListener)
						{
							if (nature != null)
							{
								List<IResourceChangeListener> natureListenerList = null;
								if (naturesToListenerLists.containsKey(nature))
								{
									natureListenerList = naturesToListenerLists.get(nature);
								}
								else
								{
									natureListenerList = new ArrayList<IResourceChangeListener>();
									naturesToListenerLists.put(nature, natureListenerList);
								}
								natureListenerList.add((IResourceChangeListener) listener);
							}
							else
							{
								globalListeners.add((IResourceChangeListener) listener);
							}
						}
					}
					catch (CoreException e)
					{
					}
				}
			}
		}
	}

	/**
	 * createPartControl
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();

		// Other plug-ins can contribute there actions here
		Separator sep = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
		manager.add(sep);

		// Other plug-ins can contribute there actions here
		sep = new Separator(IWorkbenchActionConstants.HELP_END);
		manager.add(sep);

		updateActionBars((IStructuredSelection) getViewer().getSelection());

		final Tree tree = getTreeViewer().getTree();
		PreferenceUtils.registerBackgroundColorPreference(tree, "com.aptana.ide.core.ui.background.color.navigator"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(tree, "com.aptana.ide.core.ui.foreground.color.navigator"); //$NON-NLS-1$
		
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
	}

	/**
	 * @see org.eclipse.ui.views.navigator.ResourceNavigator#restoreState(org.eclipse.ui.IMemento)
	 */
	protected void restoreState(IMemento memento)
	{
	}

	/**
	 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
	 */
    public boolean show(ShowInContext context) {
        if (getViewer() == null || context == null) {
            return false;
        }
        selectReveal(context.getSelection());
        return true;
    }

}
