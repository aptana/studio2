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
package com.aptana.ide.installer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import com.aptana.ide.core.ui.update.PluginsImageRegistry;
import com.aptana.ide.update.manager.InstallerCategory;
import com.aptana.ide.update.manager.Plugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class PluginsTreeViewer implements ICheckStateListener
{

    public static interface Listener
    {
        public void itemsChecked(int count);
    }

    private static final Color FOREGROUND_INSTALLED = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
    private static final Color FOREGROUND_REGULAR = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);

    /**
     * The content provider for the plugin tree viewer.
     */
    private static class ContentProvider implements ITreeContentProvider
    {

        public Object[] getChildren(Object parentElement)
        {
            if (parentElement instanceof PluginsTreeModel)
            {
                // the first level of the tree contains all the plugins with
                // category "Platforms" and all the other categories
                PluginsTreeModel model = (PluginsTreeModel) parentElement;
                return model.getCategories();
            }
            if (parentElement instanceof PluginTreeNode)
            {
                PluginTreeNode node = (PluginTreeNode) parentElement;
                return node.getChildren();
            }
            return null;
        }

        public Object getParent(Object element)
        {
            return null;
        }

        public boolean hasChildren(Object element)
        {
            return getChildren(element).length > 0;
        }

        public Object[] getElements(Object inputElement)
        {
            return getChildren(inputElement);
        }

        public void dispose()
        {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }

    }

    /**
     * The label provider for the plugin tree viewer.
     */
    private class LabelProvider implements ITableLabelProvider
    {

        public Image getColumnImage(Object element, int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    Image image = null;
                    if (element instanceof PluginTreeNode)
                    {
                        Plugin plugin = getPlugin((PluginTreeNode) element);
                        if (plugin != null)
                        {
                            image = fImages.getImage((Plugin) plugin);
                        }
                    }
                    return image == null ? PluginsImageRegistry.getDefaultImage() : image;
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    if (element instanceof PluginTreeNode)
                    {
                        PluginTreeNode node = (PluginTreeNode) element;
                        Object data = node.getData();
                        if (data == null)
                        {
                            return node.getID();
                        }
                        String name = data.toString();
                        if (node.isInstalled())
                        {
                            return name + " (installed)"; //$NON-NLS-1$
                        }
                        return name;
                    }
                    break;
                case 1:
					if (element instanceof PluginTreeNode)
					{
						Plugin plugin = getPlugin((PluginTreeNode) element);
						if (plugin != null)
						{
							String description = plugin.getDescription();
							if (description != null)
							{
								return description;
							}
						}
					}
					break;
            }
            return null;
        }

        public void addListener(ILabelProviderListener listener)
        {
        }

        public void dispose()
        {
        }

        public boolean isLabelProperty(Object element, String property)
        {
            return false;
        }

        public void removeListener(ILabelProviderListener listener)
        {
        }

    }

    /*
     * Returns the Plugin instance attached to the given node. 
     * 
     * @param node A PluginTreeNode
     * @return The Plugin, or <code>null</code> in case that the node data does not hold a Plugin
     */
    private Plugin getPlugin(PluginTreeNode node) {
        Object data = node.getData();
        if (data != null && data instanceof Plugin) {
        	return (Plugin) data;
        }
        return null;
    }
    
    private class PluginsComparator extends ViewerComparator
    {

        public int category(Object element)
        {
            if (element instanceof PluginTreeNode)
            {
                PluginTreeNode node = (PluginTreeNode) element;
                Object plugin = node.getData();
                if (plugin != null)
                {
                    if (plugin instanceof InstallerCategory)
                    {
                        return ((InstallerCategory) plugin).getSortWeight();
                    }
                    if (plugin instanceof Plugin)
                    {
                        return ((Plugin) plugin).getSortweight();
                    }
                }
            }
            return super.category(element);
        }

    }
    
    private ContainerCheckedTreeViewer fTreeViewer;
    private PluginsTreeModel fModel;
    private Object[] fCheckedElements;
    private PluginsImageRegistry fImages;
    private List<Listener> fListeners;

    public PluginsTreeViewer(Composite parent)
    {
        fImages = new PluginsImageRegistry(parent.getDisplay());
        fCheckedElements = new Object[0];
        fListeners = new ArrayList<Listener>();
        createContents(parent);
    }

    public void addListener(Listener listener)
    {
        fListeners.add(listener);
    }

    public void removeListener(Listener listener)
    {
        fListeners.remove(listener);
    }

    /**
     * Disposes the viewer.
     */
    public void dispose()
    {
        fModel.clear();
        fListeners.clear();
    }

    /**
     * Returns the main control of the viewer
     * 
     * @return the main control
     */
    public Control getControl()
    {
        return fTreeViewer.getControl();
    }

    /**
     * Returns the list of plug-ins user selected to install.
     * 
     * @return an array of plug-ins to install
     */
    public Plugin[] getSelectedPlugins()
    {
        List<Plugin> plugins = new ArrayList<Plugin>();
        Object[] elements = fTreeViewer.getCheckedElements();
        PluginTreeNode node;
        for (Object element : elements)
        {
            if (element instanceof PluginTreeNode)
            {
                node = (PluginTreeNode) element;
                // filters out the ones that either are already installed or do
                // not correspond to a plug-in node
                if (!node.isInstalled() && node.getData() instanceof Plugin)
                {
                    plugins.add((Plugin) node.getData());
                }
            }
        }
        return plugins.toArray(new Plugin[plugins.size()]);
    }

    /**
     * Makes the specific categories expanded.
     * 
     * @param categoryIDs the array of category ids
     */
    public void setExpandedCategories(String[] categoryIDs)
    {
        if (categoryIDs == null || categoryIDs.length == 0)
        {
            return;
        }
        // finds the categories that need to be expanded
        List<PluginTreeNode> categories = new ArrayList<PluginTreeNode>();
        PluginTreeNode category;
        for (String id : categoryIDs)
        {
            category = fModel.getCategory(id);
            if (category != null)
            {
                categories.add(category);
            }
        }
        fTreeViewer.setExpandedElements(categories.toArray(new PluginTreeNode[categories.size()]));
        if (categories.size() > 0)
        {
            // auto-selects the first element
            fTreeViewer.setSelection(new StructuredSelection(categories.get(0)));
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
     */
    public void checkStateChanged(CheckStateChangedEvent event)
    {
        Object element = event.getElement();
        if (element instanceof PluginTreeNode)
        {
            PluginTreeNode node = (PluginTreeNode) element;
            if (node.isInstalled() || hasInstalledChildren(node))
            {
                // disables checking of the box for installed plug-ins and any
                // node that contains them
                fTreeViewer.setCheckedElements(fCheckedElements);
            }
            else
            {
                // stores only the list of non-grayed, checked elements
                List<Object> checked = new ArrayList<Object>();
                Object[] elements = fTreeViewer.getCheckedElements();
                for (Object checkedElement : elements)
                {
                    if (!fTreeViewer.getGrayed(checkedElement))
                    {
                        checked.add(checkedElement);
                    }
                }
                fCheckedElements = checked.toArray(new Object[checked.size()]);
                fireItemsCheckedEvent();
            }
        }
    }

    private void createContents(Composite parent)
    {
        fTreeViewer = new ContainerCheckedTreeViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.BORDER);
        Tree tree = fTreeViewer.getTree();
        tree.setHeaderVisible(true);
        fTreeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        
        TreeColumn column = new TreeColumn(tree, SWT.LEFT);
        column.setText("Plugin"); //$NON-NLS-1$
        column.setWidth(350);
        
        column = new TreeColumn(tree, SWT.LEFT);
        column.setText("Description"); //$NON-NLS-1$
        column.setWidth(350);

        fTreeViewer.setContentProvider(new ContentProvider());
        fTreeViewer.setLabelProvider(new LabelProvider());
        fTreeViewer.setComparator(new PluginsComparator());
        fTreeViewer.setInput(fModel = new PluginsTreeModel());
        fTreeViewer.addCheckStateListener(this);
        fTreeViewer.addTreeListener(new ITreeViewerListener()
        {

            public void treeCollapsed(TreeExpansionEvent event)
            {
                updateForeground();
            }

            public void treeExpanded(TreeExpansionEvent event)
            {
               updateForeground();
            }

        });

        updateExpansionState();
        updateForeground();
    }

    private void updateExpansionState()
    {
        fTreeViewer.setExpandedElements(fModel.getExpandedCategories());
    }

    private void updateForeground()
    {
        // SWT currently does not have an API to disable individual TreeItem, so
        // have to modify the appearance manually
        List<TreeItem> items = new ArrayList<TreeItem>();
        getAllVisibleItems(items, fTreeViewer.getTree().getItems());
        Object data;
        PluginTreeNode node;
        for (TreeItem item : items)
        {
            data = item.getData();
            if (data != null && data instanceof PluginTreeNode)
            {
                node = (PluginTreeNode) data;
                if (node.isInstalled())
                {
                    item.setForeground(FOREGROUND_INSTALLED);
                }
                else
                {
                    item.setForeground(FOREGROUND_REGULAR);
                }
            }
        }
    }

    private void fireItemsCheckedEvent()
    {
        int count = fCheckedElements.length;
        for (Listener listener : fListeners)
        {
            listener.itemsChecked(count);
        }
    }

    private static void getAllVisibleItems(List<TreeItem> items, TreeItem[] topLevelItems)
    {
        for (TreeItem item : topLevelItems)
        {
            items.add(item);
            getAllVisibleItems(items, item.getItems());
        }
    }

    private static boolean hasInstalledChildren(PluginTreeNode node)
    {
        PluginTreeNode[] children = node.getChildren();
        for (PluginTreeNode child : children)
        {
            if (child.isInstalled() || hasInstalledChildren(child))
            {
                return true;
            }
        }
        return false;
    }

}
