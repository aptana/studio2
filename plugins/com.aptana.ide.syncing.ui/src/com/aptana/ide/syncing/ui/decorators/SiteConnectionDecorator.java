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
package com.aptana.ide.syncing.ui.decorators;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.INavigatorDecorator;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteConnectionDecorator implements INavigatorDecorator {

    private static final Image DECORATOR = SyncingUIPlugin
            .getImage("icons/full/obj16/sync_connection.gif"); //$NON-NLS-1$

    /**
     * padding pixels
     */
    private static final int PADDING = CoreUIUtils.onMacOSX ? 5 : 2;

    public SiteConnectionDecorator() {
    }

    /**
     * @see com.aptana.ide.core.ui.INavigatorDecorator#addDecorator(org.eclipse.swt.widgets.Tree)
     */
    public void addDecorator(final Tree tree) {
        if (isDisposed(tree)) {
            return;
        }

        tree.addListener(SWT.MeasureItem, new Listener() {

            public void handleEvent(Event event) {
                int startWidth = event.width;
                try {
                    if (!isDisposed(tree) && event.item instanceof TreeItem) {
                        Object data = ((TreeItem) event.item).getData();

                        if (data instanceof IContainer) {
                            String lastConnection = getLastSyncConnection((IContainer) data);
                            if (lastConnection != null) {
                                Point stringExtent = event.gc.stringExtent(lastConnection);
                                event.width += DECORATOR.getBounds().width + PADDING
                                        + stringExtent.x + PADDING;
                            }
                        } else if (data instanceof ISiteConnection) {
                            String text = data.toString();
                            Point stringExtent = event.gc.stringExtent(text);
                            event.width += stringExtent.x + PADDING;
                        }
                    }
                } catch (Exception e) {
                    // Catch all exception so tree painting is never hindered by
                    // decoration
                    event.width = startWidth;
                } catch (Error e) {
                    // Catch all exception so tree painting is never hindered by
                    // decoration
                    event.width = startWidth;
                }
            }
        });

        tree.addListener(SWT.PaintItem, new Listener() {

            public void handleEvent(Event event) {
                try {
                    if (!isDisposed(tree) && event.item instanceof TreeItem) {
                        Object data = ((TreeItem) event.item).getData();

                        if (data instanceof IContainer) {
                            String lastConnection = getLastSyncConnection((IContainer) data);
                            if (lastConnection != null) {
                                int x = event.x + event.width + PADDING;
                                int itemHeight = tree.getItemHeight();
                                int imageHeight = DECORATOR.getBounds().height;
                                int y = event.y + (itemHeight - imageHeight) / 2;
                                event.gc.drawImage(DECORATOR, x, y);
                                event.x = x + DECORATOR.getBounds().width;

                                x = event.x + PADDING;

                                Point stringExtent = event.gc.stringExtent(lastConnection);
                                y = event.y + (itemHeight - stringExtent.y) / 2;
                                event.gc.drawString(lastConnection, x, y, true);
                                event.x += stringExtent.x;
                            }
                        } else if (data instanceof ISiteConnection) {
                            String text = data.toString();
                            Point stringExtent = event.gc.stringExtent(text);
                            int x = event.x + event.width + PADDING;
                            int y = event.y + (tree.getItemHeight() - stringExtent.y) / 2;

                            boolean selected = false;
                            TreeItem[] selection = tree.getSelection();
                            for (TreeItem item : selection) {
                                if (item == event.item) {
                                    selected = true;
                                    break;
                                }
                            }
                            if (!selected) {
                                event.gc.setForeground(Display.getCurrent().getSystemColor(
                                        SWT.COLOR_DARK_GREEN));
                            }
                            event.gc.drawString(text, x, y, true);
                            event.x += stringExtent.x;
                        }
                    }
                } catch (Exception e) {
                    // Catch all exception so tree painting is never hindered by
                    // decoration
                } catch (Error e) {
                    // Catch all exception so tree painting is never hindered by
                    // decoration
                }
            }
        });
    }

    private static String getLastSyncConnection(IContainer container) {
        if (container == null) {
            return null;
        }
        // only shows the decorator when user chooses to
        // remember the decision
        boolean remember = ResourceSynchronizationUtils.isRememberDecision(container);
        if (!remember) {
            return null;
        }

        String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(container);
        if (lastConnection == null) {
            return null;
        }

        ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(container, true);
        String target;
        for (ISiteConnection site : sites) {
            target = site.getDestination().getName();
            if (target.equals(lastConnection)) {
                return target;
            }
        }
        return null;
    }

    private static boolean isDisposed(Tree tree) {
        return tree == null || tree.isDisposed();
    }
}
