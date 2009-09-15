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
package com.aptana.ide.syncing.ui.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ISiteConnectionManager;
import com.aptana.ide.syncing.core.SiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewSiteWidget implements SelectionListener, MouseListener,
        SiteEndPointComposite.Listener {

    public static interface Client {
        public void validationChanged(String error);
    }

    private Composite fMain;
    private Table fSitesTable;
    private TableEditor fSitesEditor;
    private MenuItem fDuplicateItem;
    private Button fAddButton;
    private Button fEditButton;
    private Button fRemoveButton;

    private SiteEndPointComposite fSrcComposite;
    private SiteEndPointComposite fDestComposite;

    private boolean fCreateNew;
    private java.util.List<ISiteConnection> fSites;
    private java.util.List<ISiteConnection> fOriginalSites;

    private Client fClient;

    // the sorter for the site connections
    private static class SitesComparator implements Comparator<ISiteConnection> {

        public int compare(ISiteConnection o1, ISiteConnection o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    public NewSiteWidget(Composite parent, Client client) {
        this(parent, false, client);
    }

    /**
     * @param parent
     *            the parent composite
     * @param createNew
     *            true if a new site entry should be created by default, false
     *            otherwise
     */
    public NewSiteWidget(Composite parent, boolean createNew, Client client) {
        fCreateNew = createNew;
        fClient = client;
        fSites = new ArrayList<ISiteConnection>();
        fOriginalSites = new ArrayList<ISiteConnection>();
        loadSites();

        fMain = createControl(parent);
    }

    public Control getControl() {
        return fMain;
    }

    public void setSelectedSite(String siteName) {
        if (siteName == null) {
            return;
        }
        ISiteConnection site;
        int count = fSites.size();
        for (int i = 0; i < count; ++i) {
            site = fSites.get(i);
            if (site.getName().equals(siteName)) {
                fSitesTable.select(i);
                updateSelection(site);
                break;
            }
        }
    }

    public void setSource(IAdaptable source) {
        fSrcComposite.setSource(source);
    }

    public void setDestination(IAdaptable target) {
        fDestComposite.setSource(target);
    }

    public boolean apply() {
        String message = verify();
        if (message != null) {
            MessageDialog.openError(fMain.getShell(), "Error", message); //$NON-NLS-1$
            return false;
        }

        // finds what changed and updates the connections list
        ISiteConnectionManager manager = SyncingPlugin.getSiteConnectionManager();
        List<ISiteConnection> deletedSites = new ArrayList<ISiteConnection>();
        deletedSites.addAll(fOriginalSites);
        for (ISiteConnection site : fSites) {
            if (fOriginalSites.contains(site)) {
                // has not been removed or added
                deletedSites.remove(site);
            } else {
                manager.addSiteConnection(site);
            }
        }
        for (ISiteConnection site : deletedSites) {
            manager.removeSiteConnection(site);
        }
        return true;
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();

        if (source == fAddButton) {
            createNewSite(Messages.NewSiteWidget_TXT_NewSite);
        } else if (source == fEditButton) {
            int index = fSitesTable.getSelectionIndex();
            if (index > -1) {
                editSiteName(fSitesTable.getSelection()[0], fSites.get(index));
            }
        } else if (source == fRemoveButton) {
            int index = fSitesTable.getSelectionIndex();
            if (index > -1) {
                fSites.remove(index);
                clearTableEditor();
                fSitesTable.remove(index);
                packSitesTable();

                index = fSitesTable.getSelectionIndex();
                if (index > -1) {
                    updateSelection(fSites.get(index));
                }
            }
        } else if (source == fSitesTable) {
            clearTableEditor();

            int index = fSitesTable.getSelectionIndex();
            if (index > -1) {
                updateSelection(fSites.get(index));
            }
        } else if (source == fDuplicateItem) {
            SiteConnectionPoint selectedSite = getSelectedSite();
            if (selectedSite == null) {
                return;
            }
            createNewSite(selectedSite.getName());
        }
    }

    public void mouseDoubleClick(MouseEvent e) {
        Object source = e.getSource();

        if (source == fSitesTable) {
            // edits the site name when the selection is double-clicked
            editSiteName(fSitesTable.getSelection()[0], (SiteConnection) fSites.get(fSitesTable.getSelectionIndex()));
        }
    }

    public void mouseDown(MouseEvent e) {
    }

    public void mouseUp(MouseEvent e) {
    }

    public void categoryChanged(SiteEndPointComposite source, String newCategory) {
    	ISiteConnection site = getSelectedSite();
        if (site == null) {
            return;
        }
        if (source == fSrcComposite) {
            site.setSourceCategory(newCategory);
        } else if (source == fDestComposite) {
            site.setDestinationCategory(newCategory);
        }
    }

    public void sourceChanged(SiteEndPointComposite source, String newSource) {
        ISiteConnection site = getSelectedSite();
        if (site == null) {
            return;
        }
        if (source == fSrcComposite) {
            site.setSource(newSource);
        } else if (source == fDestComposite) {
            site.setDestination(newSource);
        }
    }

    public void validationError(SiteEndPointComposite source, String error) {
        if (fClient != null) {
            fClient.validationChanged(error);
        }
    }

    protected Composite createControl(Composite parent) {
        SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        sash.setLayout(layout);

        // the left side shows the list of existing sites
        Composite left = createSitesList(sash);
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // the right side shows the details on the selected site from the left
        Composite right = createSiteDetails(sash);
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sash.setWeights(new int[] { 1, 2 });
        // adds the existing sites
        for (ISiteConnection site : fOriginalSites) {
            createNewSiteItem(site);
        }
        if (fCreateNew) {
            createNewSite(Messages.NewSiteWidget_TXT_NewSite);
        } else {
            clearTableEditor();
            fSitesTable.select(0);
            updateSelection(fOriginalSites.get(0));
        }

        packSitesTable();

        return sash;
    }

    private Composite createSitesList(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.NewSiteWidget_LBL_Sites);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // uses table widget for an editable list
        fSitesTable = new Table(group, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        fSitesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumn sitesColumn = new TableColumn(fSitesTable, SWT.NONE);
        sitesColumn.setWidth(150);
        fSitesEditor = new TableEditor(fSitesTable);
        fSitesEditor.horizontalAlignment = SWT.LEFT;
        fSitesEditor.grabHorizontal = true;
        fSitesEditor.minimumWidth = 50;
        fSitesTable.addSelectionListener(this);
        fSitesTable.addMouseListener(this);
        fSitesTable.setMenu(createMenu(fSitesTable));

        // the actions to modify the connection list
        Composite buttons = new Composite(group, SWT.NULL);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        buttons.setLayout(layout);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

        fAddButton = new Button(buttons, SWT.PUSH);
        fAddButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$
        fAddButton.setToolTipText(StringUtils.ellipsify(CoreStrings.ADD));
        fAddButton.addSelectionListener(this);

        fEditButton = new Button(buttons, SWT.PUSH);
        fEditButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/edit.png")); //$NON-NLS-1$
        fEditButton.setToolTipText(StringUtils.ellipsify(CoreStrings.EDIT));
        fEditButton.addSelectionListener(this);

        fRemoveButton = new Button(buttons, SWT.PUSH);
        fRemoveButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$
        fRemoveButton.setToolTipText(CoreStrings.REMOVE);
        fRemoveButton.addSelectionListener(this);

        return group;
    }

    private Menu createMenu(Control parent) {
        Menu menu = new Menu(parent);
        // for duplicating the currently selected connection
        fDuplicateItem = new MenuItem(menu, SWT.PUSH);
        fDuplicateItem.setText(Messages.NewSiteWidget_LBL_Duplicate);
        fDuplicateItem.addSelectionListener(this);

        return menu;
    }

    private Composite createSiteDetails(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);

        // the source info
        fSrcComposite = new SiteEndPointComposite(main, Messages.NewSiteWidget_LBL_Source, false);
        fSrcComposite.setDescription(Messages.NewSiteWidget_LBL_SelectSrcLocation);
        fSrcComposite.getControl().setLayoutData(
                new GridData(GridData.FILL, GridData.FILL, true, true));
        fSrcComposite.addListener(this);

        // the destination info
        fDestComposite = new SiteEndPointComposite(main, Messages.NewSiteWidget_LBL_Destination,
                true);
        fDestComposite.setDescription(Messages.NewSiteWidget_LBL_SelectDestTarget);
        fDestComposite.getControl().setLayoutData(
                new GridData(GridData.FILL, GridData.FILL, true, true));
        fDestComposite.addListener(this);

        return main;
    }

    /**
     * Creates a new site connection. If an existing site is passed in,
     * duplicates its information in the new site.
     * 
     * @param existingSite
     *            the existing site
     */
    private void createNewSite(String baseName) {
        SiteConnection newSite = new SiteConnection();
        newSite.setName(getUniqueNewSiteName(baseName));
        newSite.setSourceCategory(fSrcComposite.getCategory());
        newSite.setSource(fSrcComposite.getSourceName());
        newSite.setDestinationCategory(fDestComposite.getCategory());
        newSite.setDestination(fDestComposite.getSourceName());
        fSites.add(newSite);
        // creates the table item
        createNewSiteItem(newSite);
        fSitesTable.select(fSitesTable.getItemCount() - 1);

        return newSite;
    }

    private TableItem createNewSiteItem(SiteConnection newSite) {
        TableItem item = new TableItem(fSitesTable, SWT.NONE);
        item.setText(newSite.getName());
        editSiteName(item, newSite);

        return item;
    }

    private void clearTableEditor() {
        Control oldEditor = fSitesEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }
    }

    private void editSiteName(TableItem item, final SiteConnection site) {
        // cleans up any previous editor control
        clearTableEditor();
        if (item == null) {
            return;
        }

        // makes the text editable
        Text newEditor = new Text(fSitesTable, SWT.NONE);
        newEditor.setText(item.getText(0));
        newEditor.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                // handles the "Enter" key
                if (e.keyCode == '\r') {
                    clearTableEditor();
                    packSitesTable();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

        });
        newEditor.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent me) {
                Text text = (Text) fSitesEditor.getEditor();
                fSitesEditor.getItem().setText(0, text.getText());
                site.setName(text.getText());
            }
        });
        newEditor.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                // re-adjusts the column width
                packSitesTable();
            }
        });
        newEditor.selectAll();
        newEditor.setFocus();
        fSitesEditor.setEditor(newEditor, item, 0);
    }

    private void loadSites() {
        fSites.clear();
        fOriginalSites.clear();

        fOriginalSites.addAll(Arrays.asList(SyncingPlugin.getSiteConnectionManager().getSiteConnections()));
        Collections.sort(fOriginalSites, new SitesComparator());

        for (ISiteConnection site : fOriginalSites) {
            fSites.add(site);
        }
    }

    private void updateSelection(ISiteConnection connection) {
        // updates the source
        fSrcComposite.setCategory(connection.getSourceCategory());
        IConnectionPoint source = connection.getSource();
        String name = (source == null) ? "" : source.getName(); //$NON-NLS-1$
        fSrcComposite.setSourceName(name);

        // updates the destination
        fDestComposite.setCategory(connection.getDestinationCategory());
        source = connection.getDestination();
        name = (source == null) ? "" : source.getName(); //$NON-NLS-1$
        fDestComposite.setSourceName(name);
    }

    /**
     * @return null if there is no error in the inputs, or a descriptive message
     *         if an error is detected
     */
    private String verify() {
        Set<String> siteNames = new HashSet<String>();
        String name;
        for (ISiteConnection site : fSites) {
            name = site.getName();
            if (siteNames.contains(name)) {
                return MessageFormat.format(Messages.NewSiteWidget_ERR_DuplicateNames, name);
            }
            siteNames.add(name);
        }

        String category;
        LocalConnectionPoint connection;
        for (ISiteConnection site : fSites) {
            category = site.getSourceCategory();
            // only needs to worry about the filesytem case
            if (category.equals(LocalConnectionPoint.CATEGORY)) {
                connection = (LocalConnectionPoint) site.getSource();
                if (connection == null) {
                    return MessageFormat.format(Messages.NewSiteWidget_ERR_InvalidFileSource, site
                            .getName());
                }
            }

            category = site.getDestinationCategory();
            if (category.equals(LocalConnectionPoint.CATEGORY)) {
                connection = (LocalConnectionPoint) site.getDestination();
                if (connection == null) {
                    return MessageFormat.format(Messages.NewSiteWidget_ERR_InvalidFileTarget, site
                            .getName());
                }
            }
        }
        return null;
    }

    private String getUniqueNewSiteName(String baseName) {
        // removes any number from the end
        int index = baseName.lastIndexOf(" "); //$NON-NLS-1$
        if (index > -1) {
            String lastSegment = baseName.substring(index + 1);
            try {
                Integer.parseInt(lastSegment);
                baseName = baseName.substring(0, index);
            } catch (NumberFormatException e) {
            }
        }
        // finds the maximum number among the sites that contains the base name
        int maxCount = 0;
        String siteName;
        for (ISiteConnection site : fSites) {
            siteName = site.getName();
            index = siteName.indexOf(baseName);
            if (index > -1) {
                try {
                    int count = Integer.parseInt(siteName.substring(index + baseName.length())
                            .trim());
                    if (maxCount < count) {
                        maxCount = count;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return MessageFormat.format("{0} {1}", baseName, maxCount + 1); //$NON-NLS-1$
    }

    private ISiteConnection getSelectedSite() {
        int index = fSitesTable.getSelectionIndex();
        return (index == -1) ? null : fSites.get(index);
    }

    private void packSitesTable() {
        fSitesTable.getColumn(0).pack();
    }
}
