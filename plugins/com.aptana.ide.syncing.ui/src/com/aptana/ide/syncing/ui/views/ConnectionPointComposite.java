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
package com.aptana.ide.syncing.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.ui.io.navigator.FileTreeContentProvider;
import com.aptana.ide.ui.io.navigator.FileTreeNameSorter;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionPointComposite implements SelectionListener, IDoubleClickListener {

    private static final String[] COLUMN_NAMES = {
            Messages.ConnectionPointComposite_Column_Filename,
            Messages.ConnectionPointComposite_Column_Size,
            Messages.ConnectionPointComposite_Column_LastModified };

    private Composite fMain;
    private Combo fEndpointCombo;
    private ToolItem fUpItem;
    private ToolItem fRefreshItem;
    private ToolItem fHomeItem;
    private Label fPathLabel;

    private TreeViewer fTreeViewer;

    private String fName;
    private IConnectionPoint fConnectionPoint;
    private List<IAdaptable> fComboData;

    public ConnectionPointComposite(Composite parent, String name) {
        fName = name;
        fComboData = new ArrayList<IAdaptable>();

        fMain = createControl(parent);
    }

    public Control getControl() {
        return fMain;
    }

    public void setFocus() {
        fMain.setFocus();
    }

    public void setConnectionPoint(IConnectionPoint connection) {
        if (fConnectionPoint == connection) {
            return;
        }
        fConnectionPoint = connection;

        fComboData.clear();
        if (fConnectionPoint == null) {
            fEndpointCombo.setItems(new String[0]);
            fEndpointCombo.clearSelection();
        } else {
            fEndpointCombo.setItems(new String[] { fConnectionPoint.getName() });
            fEndpointCombo.select(0);
            fComboData.add(fConnectionPoint);
        }
        setPath(""); //$NON-NLS-1$

        fTreeViewer.setInput(connection);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == fEndpointCombo) {
            updateContent(fComboData.get(fEndpointCombo.getSelectionIndex()));
        } else if (source == fUpItem) {
            goUp();
        } else if (source == fRefreshItem) {
            refresh();
        } else if (source == fHomeItem) {
            gotoHome();
        }
    }

    public void doubleClick(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        Object object = selection.getFirstElement();
        if (object instanceof IResource) {
            if (object instanceof IContainer) {
                updateContent((IContainer) object);
            } else if (object instanceof IFile) {
                // opens the file in the editor
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                try {
                    IDE.openEditor(page, (IFile) object);
                } catch (PartInitException e) {
                    // ignores the exception
                }
            }
        } else if (object instanceof IAdaptable) {
            IFileInfo fileInfo = getFileInfo((IAdaptable) object);
            if (fileInfo.isDirectory()) {
                updateContent((IAdaptable) object);
            } else {
                // TODO: opens the file in the editor
            }
        }
    }

    protected Composite createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout());

        Composite top = createTopComposite(main);
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        fPathLabel = new Label(main, SWT.NONE);
        fPathLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        final Font font = new Font(fPathLabel.getDisplay(), SWTUtils.boldFont(fPathLabel.getFont()));
        fPathLabel.setFont(font);
        fPathLabel.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                font.dispose();
            }
        });

        TreeViewer treeViewer = createTreeViewer(main);
        treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return main;
    }

    private Composite createTopComposite(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        Label label = new Label(main, SWT.NONE);
        label.setText(fName + ":"); //$NON-NLS-1$

        fEndpointCombo = new Combo(main, SWT.READ_ONLY);
        fEndpointCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fEndpointCombo.addSelectionListener(this);

        createActionsBar(main);

        return main;
    }

    private ToolBar createActionsBar(Composite parent) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT);

        fUpItem = new ToolItem(toolbar, SWT.PUSH);
        fUpItem.setImage(SyncingUIPlugin.getImage("icons/full/obj16/up.png")); //$NON-NLS-1$
        fUpItem.setToolTipText(Messages.ConnectionPointComposite_TTP_Up);
        fUpItem.addSelectionListener(this);

        fRefreshItem = new ToolItem(toolbar, SWT.PUSH);
        fRefreshItem.setImage(SyncingUIPlugin.getImage("icons/full/obj16/refresh.gif")); //$NON-NLS-1$
        fRefreshItem.setToolTipText(Messages.ConnectionPointComposite_TTP_Refresh);
        fRefreshItem.addSelectionListener(this);

        fHomeItem = new ToolItem(toolbar, SWT.PUSH);
        fHomeItem.setImage(SyncingUIPlugin.getImage("icons/full/obj16/home.png")); //$NON-NLS-1$
        fHomeItem.setToolTipText(Messages.ConnectionPointComposite_TTP_Home);
        fHomeItem.addSelectionListener(this);

        return toolbar;
    }

    private TreeViewer createTreeViewer(Composite parent) {
        fTreeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        Tree tree = fTreeViewer.getTree();
        tree.setHeaderVisible(true);

        TreeColumn column = new TreeColumn(tree, SWT.LEFT);
        column.setWidth(300);
        column.setText(COLUMN_NAMES[0]);

        column = new TreeColumn(tree, SWT.LEFT);
        column.setWidth(50);
        column.setText(COLUMN_NAMES[1]);

        column = new TreeColumn(tree, SWT.LEFT);
        column.setWidth(125);
        column.setText(COLUMN_NAMES[2]);

        fTreeViewer.setContentProvider(new FileTreeContentProvider());
        fTreeViewer.setLabelProvider(new ConnectionPointLabelProvider());
        fTreeViewer.setComparator(new FileTreeNameSorter());
        fTreeViewer.addDoubleClickListener(this);

        return fTreeViewer;
    }

    private void goUp() {
        int index = fEndpointCombo.getSelectionIndex();
        if (index == fEndpointCombo.getItemCount() - 1) {
            // we're at root already
            return;
        }
        updateContent(fComboData.get(index + 1));
    }

    private void refresh() {
        Object input = fTreeViewer.getInput();
        if (input instanceof IResource) {
            try {
                ((IResource) input).refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
            }
        }
    }

    private void gotoHome() {
        updateContent(fConnectionPoint);
    }

    private void setComboData(IAdaptable data) {
        fComboData.clear();
        fComboData.add(fConnectionPoint);
        List<String> items = new ArrayList<String>();
        items.add(fConnectionPoint.getName());

        if (data instanceof IContainer) {
            IContainer container = (IContainer) data;

            IPath path = container.getProjectRelativePath();
            IContainer segmentPath = container.getProject();
            String[] segments = path.segments();
            for (String segment : segments) {
                segmentPath = (IContainer) segmentPath.findMember(segment);
                // adds the path segment in reverse order
                fComboData.add(0, segmentPath);
                items.add(0, segment);
            }
        } else {
            IFileStore fileStore = getFileStore(data);
            if (fileStore != null) {
                IFileStore homeFileStore = getFileStore(fConnectionPoint);
                while (fileStore.getParent() != null && !fileStore.equals(homeFileStore)) {
                    fComboData.add(fComboData.size() - 1, fileStore);
                    items.add(items.size() - 1, fileStore.getName());
                    fileStore = fileStore.getParent();
                }
            }
        }

        fEndpointCombo.setItems(items.toArray(new String[items.size()]));
        fEndpointCombo.setText(items.get(0));
    }

    private void setPath(String path) {
        if (!path.startsWith("/")) { //$NON-NLS-1$
            path = "/" + path; //$NON-NLS-1$
        }
        fPathLabel.setText(Messages.ConnectionPointComposite_LBL_Path + path);
    }

    private void updateContent(IAdaptable rootElement) {
        setComboData(rootElement);

        if (rootElement instanceof IContainer) {
            setPath(((IContainer) rootElement).getProjectRelativePath().toString());
        } else {
            IFileStore fileStore = getFileStore(rootElement);
            if (fileStore != null) {
                String path = fileStore.toString();
                IFileStore homeFileStore = getFileStore(fConnectionPoint);
                if (homeFileStore != null) {
                    String homePath = homeFileStore.toString();
                    int index = path.indexOf(homePath);
                    if (index > -1) {
                        path = path.substring(index + homePath.length());
                    }
                }
                setPath(path);
            }
        }
        fTreeViewer.setInput(rootElement);
    }

    private static IFileInfo getFileInfo(IAdaptable adaptable) {
        IFileInfo fileInfo = (IFileInfo) adaptable.getAdapter(IFileInfo.class);
        if (fileInfo == null) {
            IFileStore fileStore = getFileStore(adaptable);
            if (fileStore != null) {
                try {
                    fileInfo = fileStore.fetchInfo(EFS.NONE, null);
                } catch (CoreException e) {
                    // ignores the exception
                }
            }
        }
        return fileInfo;
    }

    private static IFileStore getFileStore(IAdaptable adaptable) {
        return (IFileStore) adaptable.getAdapter(IFileStore.class);
    }

}
