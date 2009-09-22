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
package com.aptana.ide.syncing.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.epl.IMemento;
import com.aptana.ide.core.epl.XMLMemento;
import com.aptana.ide.syncing.core.connection.DefaultSiteConnectionPoint;

/**
 * The activator class controls the plug-in life cycle
 */
public class SyncingPlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.aptana.ide.syncing.core"; //$NON-NLS-1$

    private static final String STATE_FILENAME = "defaultConnection"; //$NON-NLS-1$
    private static final String ELEMENT_ROOT = "connections"; //$NON-NLS-1$
    private static final String ELEMENT_CONNECTION = "connection"; //$NON-NLS-1$

    // The shared instance
    private static SyncingPlugin plugin;

    /**
     * The constructor
     */
    public SyncingPlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // loads the state of default connection
        ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this,
                new WorkspaceSaveParticipant());
        if (lastState != null) {
            IPath location = lastState.lookup(new Path(STATE_FILENAME));
            if (location != null) {
                IPath path = getStateLocation().append(location);
                File file = path.toFile();
                if (file.exists()) {
                    FileReader reader = null;
                    try {
                        reader = new FileReader(file);
                        XMLMemento memento = XMLMemento.createReadRoot(reader);
                        DefaultSiteConnectionPoint.getInstance().loadState(
                                memento.getChild(ELEMENT_CONNECTION));
                    } catch (IOException e) {
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                // ignored
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static SyncingPlugin getDefault() {
        return plugin;
    }

    private class WorkspaceSaveParticipant implements ISaveParticipant {

        public void prepareToSave(ISaveContext context) throws CoreException {
        }

        public void saving(ISaveContext context) throws CoreException {
            // saves the state of default connection
            XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
            DefaultSiteConnectionPoint defaultConnection = DefaultSiteConnectionPoint.getInstance();
            IMemento child = memento.createChild(ELEMENT_CONNECTION, defaultConnection.getId());
            defaultConnection.saveState(child);

            IPath savePath = new Path(STATE_FILENAME).addFileExtension(Integer.toString(context
                    .getSaveNumber()));
            IPath path = getStateLocation().append(savePath);
            FileWriter writer = null;
            try {
                writer = new FileWriter(path.toFile());
                memento.save(writer);
            } catch (IOException e) {
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        // ignored
                    }
                }
            }
            context.map(new Path(STATE_FILENAME), savePath);
            context.needSaveNumber();
        }

        public void doneSaving(ISaveContext context) {
            IPath prevSavePath = new Path(STATE_FILENAME).addFileExtension(Integer.toString(context
                    .getPreviousSaveNumber()));
            getStateLocation().append(prevSavePath).toFile().delete();
        }

        public void rollback(ISaveContext context) {
            IPath savePath = new Path(STATE_FILENAME).addFileExtension(Integer.toString(context
                    .getSaveNumber()));
            getStateLocation().append(savePath).toFile().delete();
        }
    }
}
