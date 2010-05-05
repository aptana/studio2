/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.ingo;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.aptana.ide.core.io.ingo.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.ui.io.navigator.INavigatorDecorator;

public class SynchronizationConnectionDecorator implements INavigatorDecorator {
	/**
	 * CLOUD_DECORATOR
	 */
	private static final Image SYNC_TARGET_DECORATOR = SyncingUIPlugin.getImage("icons/sync_connection.gif"); //$NON-NLS-1$
	
	/**
	 * PADDING
	 */
	private static final int PADDING = CoreUIUtils.onMacOSX ? 5 : 2;
	
	private Tree projectTree;

	public SynchronizationConnectionDecorator() {
	}

	/**
	 * @see com.aptana.ide.core.ui.INavigatorDecorator#addDecorator(org.eclipse.swt.widgets.Tree)
	 */
	public void addDecorator(Tree tree) {
	    this.projectTree = tree;
	
	    if (isDisposed())
	    {
	        return;
	    }
	    projectTree.addListener(SWT.MeasureItem, new Listener()
	    {
	        public void handleEvent(Event event)
	        {
	            int startWidth = event.width;
	            try
	            {
	                if (!isDisposed() && event.item instanceof TreeItem)
	                {
	                    TreeItem item = (TreeItem) event.item;
	                    if (item.getData() instanceof IProject)
	                    {
	                        IProject project = (IProject) item.getData();
											
	                        String lastSyncConnectionSerializableString = ProjectSynchronizationUtils.getLastSyncConnection(project);
	                        if (lastSyncConnectionSerializableString != null) {
	                            VirtualFileManagerSyncPair virtualFileManagerSyncPair = new VirtualFileManagerSyncPair();
	                            virtualFileManagerSyncPair.fromSerializableString(lastSyncConnectionSerializableString);
	                            if (virtualFileManagerSyncPair.isValid()) {
	                                if (!ProjectSynchronizationUtils.isCloudConnection(virtualFileManagerSyncPair)) {
	                                    String virtualFileManagerSyncPairNickName =
	                                        virtualFileManagerSyncPair.getDestinationFileManager().getNickName();
	                                    Point stringExtent = event.gc.stringExtent(virtualFileManagerSyncPairNickName);
	                                    event.width += SYNC_TARGET_DECORATOR.getBounds().width + PADDING + stringExtent.x + PADDING;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	            catch (Exception e)
	            {
	                // Catch all exception so tree painting is never hindered by decoration
	                event.width = startWidth;
	            }
	            catch (Error e)
	            {
	                // Catch all exception so tree painting is never hindered by decoration
	                event.width = startWidth;
	            }
	        }
	    });
	    projectTree.addListener(SWT.PaintItem, new Listener()
	    {
	        public void handleEvent(Event event)
	        {
	            try
	            {
	                if (!isDisposed() && event.item instanceof TreeItem)
	                {
	                    TreeItem item = (TreeItem) event.item;
	                    if (item.getData() instanceof IProject)
	                    {
	                        IProject project = (IProject) item.getData();
	                        String lastSyncConnectionSerializableString = ProjectSynchronizationUtils.getLastSyncConnection(project);
	                        if (lastSyncConnectionSerializableString != null) {
	                            VirtualFileManagerSyncPair virtualFileManagerSyncPair = new VirtualFileManagerSyncPair();
	                            virtualFileManagerSyncPair.fromSerializableString(lastSyncConnectionSerializableString);
	                            if (virtualFileManagerSyncPair.isValid()) {
	                                if (!ProjectSynchronizationUtils.isCloudConnection(virtualFileManagerSyncPair)) {
	                                    String virtualFileManagerSyncPairNickName =
	                                        virtualFileManagerSyncPair.getDestinationFileManager().getNickName();
	                                    int x = event.x + event.width + PADDING;
	                                    int itemHeight = projectTree.getItemHeight();
	                                    int imageHeight = SYNC_TARGET_DECORATOR.getBounds().height;
	                                    int y = event.y + (itemHeight - imageHeight) / 2;
	                                    event.gc.drawImage(SYNC_TARGET_DECORATOR, x, y);
	                                    event.x = x + SYNC_TARGET_DECORATOR.getBounds().width;
														
	                                    x = event.x + PADDING;
														
	                                    Point stringExtent = event.gc.stringExtent(virtualFileManagerSyncPairNickName);
	                                    y = event.y + (itemHeight - stringExtent.y) / 2;
	                                    event.gc.drawString(virtualFileManagerSyncPairNickName, x, y, true);
	                                    event.x += stringExtent.x;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	            catch (Exception e)
	            {
	                // Catch all exception so tree painting is never hindered by decoration
	            }
	            catch (Error e)
	            {
	                // Catch all exception so tree painting is never hindered by decoration
	            }
	        }
	    });
	}

    private boolean isDisposed()
    {
        return projectTree == null || projectTree.isDisposed();
    }

}
