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
import org.eclipse.ui.progress.UIJob;

/**
 * @author Ingo Muschenetz
 */
public class ConfigExtensionLoader {

    private static Map<String, List<IResourceChangeListener>> naturesToListenerLists = null;
    private static Map<IProject, String[]> projectsToNatureArrays = new HashMap<IProject, String[]>();
    private static List<IResourceChangeListener> globalListeners = null;

    public static void init() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
                new org.eclipse.core.resources.IResourceChangeListener() {

                    public void resourceChanged(IResourceChangeEvent event) {
                        notifyListeners(event);
                    }
                });
    }

    private ConfigExtensionLoader() {
    }

    private static void notifyListeners(IResourceChangeEvent event) {
        if (naturesToListenerLists == null) {
            loadConfigurationExtensionPoints();
        }

        IResourceDelta delta = event.getDelta();
        if (delta == null) {
            if (event.getType() == IResourceChangeEvent.PRE_DELETE
                    && event.getResource() instanceof IProject) {
                IProject project = (IProject) event.getResource();
                try {
                    String[] natures = project.getDescription().getNatureIds();
                    projectsToNatureArrays.put(project, natures);
                } catch (CoreException e) {
                }
            }
        } else {
            IResourceDelta[] deltas = delta.getAffectedChildren();
            for (IResourceDelta rDelta : deltas) {
                if (rDelta.getKind() == IResourceDelta.REMOVED && rDelta.getMovedToPath() == null
                        && rDelta.getResource() instanceof IProject) {
                    fireCollectDeletions((IProject) rDelta.getResource(), event);
                } else {
                    IProject project = rDelta.getResource().getProject();
                    if (project != null) {
                        fireResourceEvent(project, event);
                        projectsToNatureArrays.remove(project);
                    }
                }
            }
        }
    }

    private static void fireCollectDeletions(IProject project, IResourceChangeEvent event) {
        String[] natures = new String[0];
        try {
            natures = project.getDescription().getNatureIds();
        } catch (CoreException e) {
            if (projectsToNatureArrays.containsKey(project)) {
                natures = projectsToNatureArrays.remove(project);
            }
        }
        final List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();
        for (String nature : natures) {
            if (naturesToListenerLists.containsKey(nature)) {
                List<IResourceChangeListener> natureListeners = naturesToListenerLists.get(nature);
                for (IResourceChangeListener listener : natureListeners) {
                    configurations.addAll(Arrays.asList(listener
                            .getDeleteCandidates(project, event)));
                }
            }
            for (IResourceChangeListener listener : globalListeners) {
                configurations.addAll(Arrays.asList(listener.getDeleteCandidates(project, event)));
            }
        }
        if (configurations.size() > 0) {
            UIJob job = new UIJob("Open delete candidates") { //$NON-NLS-1$

                public IStatus runInUIThread(IProgressMonitor monitor) {
                    DeleteConfigurationsDialog dialog = new DeleteConfigurationsDialog(CoreUIUtils
                            .getActiveShell(), configurations);
                    dialog.open();
                    return Status.OK_STATUS;
                }
            };
            job.setSystem(true);
            job.schedule();
        }
    }

    private static void fireResourceEvent(IProject project, IResourceChangeEvent event) {
        try {
            String[] natures = project.getDescription().getNatureIds();
            for (int i = 0; i < natures.length; i++) {
                if (naturesToListenerLists.containsKey(natures[i])) {
                    List<IResourceChangeListener> natureListeners = naturesToListenerLists
                            .get(natures[i]);
                    for (int j = 0; j < natureListeners.size(); j++) {
                        natureListeners.get(j).resourceChanged(event);
                    }
                }
                for (int j = 0; j < globalListeners.size(); j++) {
                    globalListeners.get(j).resourceChanged(event);
                }
            }
        } catch (CoreException e) {
        }
    }

    private static void loadConfigurationExtensionPoints() {
        naturesToListenerLists = new HashMap<String, List<IResourceChangeListener>>();
        globalListeners = new ArrayList<IResourceChangeListener>();
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint ep = reg.getExtensionPoint(CoreUIPlugin.ID + ".configuration"); //$NON-NLS-1$
        IExtension[] extensions = ep.getExtensions();
        for (IExtension extension : extensions) {
            IConfigurationElement[] ce = extension.getConfigurationElements();
            for (int j = 0; j < ce.length; j++) {
                String nature = ce[j].getAttribute("nature"); //$NON-NLS-1$
                String listenerClass = ce[j].getAttribute("handler"); //$NON-NLS-1$
                if (listenerClass != null) {
                    try {
                        Object listener = ce[j].createExecutableExtension("handler"); //$NON-NLS-1$
                        if (listener instanceof IResourceChangeListener) {
                            if (nature != null) {
                                List<IResourceChangeListener> natureListenerList = null;
                                if (naturesToListenerLists.containsKey(nature)) {
                                    natureListenerList = naturesToListenerLists.get(nature);
                                } else {
                                    natureListenerList = new ArrayList<IResourceChangeListener>();
                                    naturesToListenerLists.put(nature, natureListenerList);
                                }
                                natureListenerList.add((IResourceChangeListener) listener);
                            } else {
                                globalListeners.add((IResourceChangeListener) listener);
                            }
                        }
                    } catch (CoreException e) {
                    }
                }
            }
        }
    }
}
