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
package com.aptana.ide.logging.view;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.IUpdate;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.logging.DefaultLogInfo;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.Messages;
import com.aptana.ide.logging.impl.DefaultLogsFactory;
import com.aptana.ide.logging.preferences.NewRuleDialog;

/**
 * Log view.
 * @author Denis Denisenko
 */
public class LogView extends ViewPart
{
    /**
     * Text listener, used to update content-dependent actions.
     * @author Denis Denisenko
     */
    class TextListener implements ITextListener, ITextInputListener
    {
        /**
         * Current text viewer.
         */
        private TextViewer viewer;
        
        /**
         * TextListener constructor.
         * @param viewer - viewer, listener listens to.
         */
        public TextListener(TextViewer viewer)
        {
            this.viewer = viewer;
        }

        /**
          * {@inheritDoc}
          */
        public void textChanged(org.eclipse.jface.text.TextEvent event)
        {
            update();
        }

        /**
          * {@inheritDoc}
          */
        public void inputDocumentAboutToBeChanged(IDocument oldInput,
                IDocument newInput)
        {
            update();
        }

        /**
          * {@inheritDoc}
          */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput)
        {
            update();
        }
        
        /**
         * Makes an update.
         */
        void update()
        {
            TextViewer currentViewer = getCurrentViewer();
            if (currentViewer != null && currentViewer.equals(viewer))
            {
                updateContentDependentActions();
            }
        }
    }
    
    /**
     * Action that renames tab.
     * @author Denis Denisenko
     *
     */
    class RenameTabAction extends Action implements IUpdate
    {
    	/**
    	 * Workbench part, action is created for.
    	 */
    	private IWorkbenchPart workbenchPart;
    	
    	/**
    	 * RenameTabAction constructor.
    	 * @param workbenchPart - workbench part, action is created for.
    	 */
		public RenameTabAction(IWorkbenchPart workbenchPart)
    	{
    		this.workbenchPart= workbenchPart;
    	}
		
		/**
		 * {@inheritDoc}
		 */
    	public void run()
        {
            LogTab activeTab = getActiveTab();
            if (activeTab != null)
            {
                RenameTabDialog dialog = new RenameTabDialog(shell, activeTab.getName());
                if (dialog.open() == Window.OK)
                {
                    activeTab.setName(dialog.getName());
                }
            }
            
        }

    	/**
    	 * {@inheritDoc}
    	 */
		public void update()
		{
			IFindReplaceTarget target = null;
			

			if (workbenchPart != null)
			{
				target = (IFindReplaceTarget) workbenchPart.getAdapter(IFindReplaceTarget.class);
			}

			setEnabled(target != null);
		}
    }
    
    /**
     * Log viewer settings section name.
     */
    private static final String LOG_VIEW_SETTINGS_SECTION = "org.aptana.ide.logging.viewer.section";  //$NON-NLS-1$

    /**
     * Log viewer tabs URI setting key.
     */
    private static final String LOG_VIEW_SET_TAB_URIS_KEY = "org.aptana.ide.logging.viewer.section.tabs.uris"; //$NON-NLS-1$
    
    /**
     * Log viewer tabs "Supports erasing" setting key.
     */
    private static final String LOG_VIEW_SET_TAB_SUPPORTS_ERASING = "org.aptana.ide.logging.viewer.section.tabs.supportsErasing"; //$NON-NLS-1$
    
    /**
     * Log viewer tabs name setting key.
     */
    private static final String LOG_VIEW_SET_TAB_NAMES_KEY = "org.aptana.ide.logging.viewer.section.tabs.names";  //$NON-NLS-1$
    
    /**
     * FindReplace action key.
     */
    public static final String FIND_REPLACE = "com.aptana.ide.core.ui.FindReplaceAction";  //$NON-NLS-1$

	/**
	 * ID of the view
	 */
	public static final String ID = "com.aptana.ide.logging.LogView";  //$NON-NLS-1$
    
    /**
     * Add action icon descriptor. 
     */
    private static ImageDescriptor fAddActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/open_active.gif");  //$NON-NLS-1$
    
    /**
     * Remove action icon descriptor. 
     */
    private static ImageDescriptor fRemoveActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/remove_active.gif");  //$NON-NLS-1$
    
    /**
     * Remove All action icon descriptor. 
     */
    private static ImageDescriptor fRemoveAllActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/removeall_active.gif");  //$NON-NLS-1$
    
    /**
     * Start action icon descriptor. 
     */
    private static ImageDescriptor fStartActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/start_active.gif");  //$NON-NLS-1$
    
    /**
     * Stop action icon descriptor. 
     */
    private static ImageDescriptor fStopActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/stop_active.gif");  //$NON-NLS-1$
    
    /**
     * Reload action icon descriptor. 
     */
    private static ImageDescriptor fReloadActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/refresh_active.gif");  //$NON-NLS-1$
    
    /**
     * Clear Log action icon descriptor. 
     */
    private static ImageDescriptor fClearLogActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/clear_log.png");  //$NON-NLS-1$
    
    /**
     * Clear Log File action icon descriptor. 
     */
    private static ImageDescriptor fClearLogFileActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/delete_log_active.gif");  //$NON-NLS-1$
    
    /**
     * Scroll Lock action icon descriptor. 
     */
    private static ImageDescriptor fScrollLockActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/scrolllock_active.gif");  //$NON-NLS-1$
    
    /**
     * Move Tab Left action icon descriptor. 
     */
    private static ImageDescriptor fMoveTabLeftActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/backward_nav_on.gif");  //$NON-NLS-1$
    
    /**
     * Move Tab Right action icon descriptor. 
     */
    private static ImageDescriptor fMoveTabRightActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/forward_nav_on.gif");  //$NON-NLS-1$
    
    /**
     * Move Tab Right action icon descriptor. 
     */
    private static ImageDescriptor fToolsActionDescriptor = LoggingPlugin
        .getImageDescriptor("icons/active/color_settings.png");  //$NON-NLS-1$
    
    /**
     * Content dependent actions.
     */
    private Set<Action> contentDependentActions = new HashSet<Action>();
    
    /**
     * Tab folder.
     */
    CTabFolder tabFolder;
    
    /**
     * shell
     */
    protected Shell shell;
    
    /**
     * Add action.
     */
    private Action actionAdd;
    
    /**
     * Delete action.
     */
    private Action actionDelete;
    
    /**
     * Delete All action.
     */
    private Action actionDeleteAll;
    
    /**
     * Start action.
     */
    private Action actionStart;
    
    /**
     * Stop action.
     */
    private Action actionStop;
    
    /**
     * Reload action.
     */
    private Action actionReload;
    
    /**
     * Clear Log action.
     */
    private Action actionLogClear;
    
    /**
     * Clear Log File action.
     */
    private Action actionLogFileClear;
    
    /**
     * Find/replace action.
     */
    private Action actionFindReplace;
    
    /**
     * Follow Tail action.
     */
    private Action actionFollowTail;
    
    /**
     * Rename tab action.
     */
    private Action actionRenameTab;
    
    /**
     * Move Tab Left action.
     */
    private Action actionMoveTabLeft;

    /**
     * Move Tab Right action.
     */
    private Action actionMoveTabRight;
    
    /**
     * Create New String Rule action.
     */
    private Action actionNewStringRule;
    
    /**
     * Tools action.
     */
    private Action toolsAction;
    
    /**
     * Default log actions.
     */
    private List<Action> defaultLogActions = new ArrayList<Action>();
    
    /**
     * Tabs. Map URL->LogTab
     */
    private Map<URI, LogTab> tabs = new HashMap<URI, LogTab>();

    /**
     * Menu manager.
     */
    MenuManager menuMgr;

    /**
     * Font for the toolbar
     */
	private Font toolbarFont;

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent)
    {
        //getting shell
        shell = parent.getShell();
        
        //creating control
        tabFolder = new CTabFolder(parent, SWT.DEFAULT);
        tabFolder.setSimple(true);
        tabFolder.setSelectionBackground(UnifiedColorManager.getInstance().getColor(new RGB(220, 220, 220)));
        
		// Font size
		int fontsize = 8;

		// On Mac OS X 8 pt Arial is hard to read in this widget, bump up to 10
		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			fontsize = 10;
		}

		toolbarFont = new Font(tabFolder.getDisplay(), "Arial", fontsize, SWT.NONE); //$NON-NLS-1$
		tabFolder.setFont(toolbarFont);
        
        tabFolder.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                saveTabsConfiguration();
                closeAllTabs();
            }
        });
        
        tabFolder.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                updateContentDependentActions();
            }

            public void widgetSelected(SelectionEvent e)
            {
                updateContentDependentActions();
                LogTab activeTab = getActiveTab();
                
                if (activeTab != null)
                {
                    activeTab.selected();
                }
            }
        });
        
        tabFolder.addCTabFolder2Listener(new CTabFolder2Listener(){

            public void close(CTabFolderEvent event)
            {
                Widget itemToSearch = event.item;
                
                LogTab toDelete = null; 
                for (LogTab tab : tabs.values())
                {
                
                    if (tab.getItem().equals(itemToSearch))
                    {
                        toDelete = tab;
                        break;
                    }
                }
                
                if (toDelete != null)
                {
                    deleteTab(toDelete);
                }
            }

            public void maximize(CTabFolderEvent event)
            {
            }

            public void minimize(CTabFolderEvent event)
            {
            }

            public void restore(CTabFolderEvent event)
            {   
            }

            public void showList(CTabFolderEvent event)
            {                
            }
        });
        
        //creating actions
        createActions();
        
        //hooking context menu
        hookContextMenu();
        
        //contribute to bars
        contributeToActionBars();
        
        //loading saved tabs
        loadTabsConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        //doing nothing
    }

    /**
      * {@inheritDoc}
      */
    public Object getAdapter(Class adapter)
    {
        if (IFindReplaceTarget.class.equals(adapter))
        {
            TextViewer currentViewer = getCurrentViewer();
            if (currentViewer != null)
            {
                return currentViewer.getFindReplaceTarget();
            }
        }
        return super.getAdapter(adapter);
    }
    
    /**
     * Add new tab.
     * @param path - local file path.
     * @param name - tab name. maybe null.
     * @param supportsLogErase - whether the tab's log supports log erasing.
     */
    public void addTab(String path, String name, boolean supportsLogErase)
    {
        File file = new File(path);
        URI uri = file.toURI();
        addTab(uri, name, supportsLogErase);
    }
    
    /**
     * Add new tab.
     * @param uri - URI to add logger tab for.
     * @param tabName - tab name. maybe null.
     * @param supportsLogErase - whether the tab's log supports log erasing.
     */
    public void addTab(URI uri, String tabName, boolean supportsLogErase)
    {
        LogTab tab = (LogTab) tabs.get(uri);
        
        if (tab == null)
        {
            tab = new LogTab(this, uri, tabName, supportsLogErase);
            
            tabs.put(uri, tab);
            
        }
        else
        {
            tabFolder.setSelection(tab.getItem());
        }
        
        
        
        updateContentDependentActions();
        
    }

    /**
     * Gets current viewer.
     * @return gets current viewer.
     */
    private TextViewer getCurrentViewer()
    {
        LogTab tab = getActiveTab();
        if (tab != null)
        {
            return tab.getViewer();
        }
        
        return null;
    }

    /**
     * Hooks context menu.
     */
    private void hookContextMenu()
    {
        menuMgr = new MenuManager("#PopupMenu");  //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(tabFolder);
        tabFolder.setMenu(menu);
        
        getSite().registerContextMenu(menuMgr, new ISelectionProvider(){

            public void addSelectionChangedListener(
                    ISelectionChangedListener listener)
            {
            }

            public ISelection getSelection()
            {
                return null;
            }

            public void removeSelectionChangedListener(
                    ISelectionChangedListener listener)
            {
            }

            public void setSelection(ISelection selection)
            {    
            }
        });
    }
    
    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    /**
     * Fills toolbar.
     * @param manager - manager.
     */
    private void fillLocalToolBar(IToolBarManager manager)
    {
        manager.add(actionMoveTabLeft);
        manager.add(actionMoveTabRight);
        
        manager.add(new Separator());
        
        manager.add(actionAdd);
        
        manager.add(actionStart);
        manager.add(actionStop);
        manager.add(actionReload);        
        manager.add(actionLogClear);
        manager.add(actionLogFileClear);
        
        manager.add(new Separator());
        
        manager.add(actionDelete);
        manager.add(actionDeleteAll);
        
        manager.add(new Separator());
        
        manager.add(actionFollowTail);
        
        manager.add(new Separator());
        
        manager.add(toolsAction);
    }
    
    /**
     * Fills context menu.
     * @param manager - menu manager.
     */
    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(actionAdd);
        manager.add(actionDelete);
        
        manager.add(new Separator());
        
        manager.add(actionFindReplace);
        
        manager.add(new Separator());
        
        manager.add(actionRenameTab);
        
        manager.add(new Separator());
        
        manager.add(actionNewStringRule);
        
        manager.add(new Separator());
        createDefaultLogActions();
        for (Action action : defaultLogActions)
        {
            manager.add(action);
        }
        
        manager.add(new Separator());
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /**
     * Creates actions.
     */
    private void createActions()
    {
        createAddAction();
        createDeleteAction();
        createStartAction();
        createStopAction();
        createDeleteAllAction();
        createReloadAction();
        createClearLogAction();
        createClearLogFileAction();
        createFindReplaceAction();
        createFollowTailAction();
        createRenameTabAction();
        createOrderActions();
        createNewRuleActions();
        createToolsAction();
    }

    /**
     * Creates New Rule actions.
     */
    private void createNewRuleActions()
    {
        actionNewStringRule = new Action()
        {
            public void run()
            {
                LogTab activeTab = getActiveTab();
                if (activeTab != null)
                {
                    String selection = activeTab.getSelection();
                    if (selection == null || selection.length() == 0)
                    {
                        selection = ""; //$NON-NLS-1$
                    }
                    

                    NewRuleDialog dialog = new NewRuleDialog(selection, shell);
                    List<String> forbiddenNames = new ArrayList<String>();
                    List<LoggingPreferences.Rule> rules = 
                        LoggingPlugin.getDefault().getLoggingPreferences().getRules();
                    for (LoggingPreferences.Rule rule : rules)
                    {
                        forbiddenNames.add(rule.getName());
                    }
                    dialog.setForbiddenNames(forbiddenNames);
                    
                    if (dialog.open() == Window.OK)
                    {
                        rules.add(new LoggingPreferences.Rule(dialog.getName(), dialog.getContent(), dialog.isRegexp(),
                                dialog.isCaseInsensitive()));
                        
                        activeTab.refreshViewer();
                    }
                }
            }
        };
        
        actionNewStringRule.setText(com.aptana.ide.logging.view.Messages.LogView_NewStringRule_name); 
        actionNewStringRule.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_NewStringRule_tooltip); 
        
        
//        actionNewRegexpRule = new Action()
//        {
//            public void run()
//            {
//                LogTab activeTab = getActiveTab();
//                if (activeTab != null)
//                {
//                    String selection = activeTab.getSelection();
//                    if (selection == null || selection.length() == 0)
//                    {
//                        return;
//                    }
//                    
//                    NewRuleDialog dialog = new NewRuleDialog(shell, selection);
//                    if (dialog.open() == Window.OK)
//                    {
//                        List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
//                        rules.add(new LoggingPreferences.Rule(dialog.getName(), dialog.getContent(), false));
//                        
//                        activeTab.refreshViewer();
//                    }
//                }
//            }
//        };
//        
//        actionNewRegexpRule.setText(com.aptana.ide.logging.view.Messages.getString("LogView.NewRegexpRule.name")); 
//        actionNewRegexpRule.setToolTipText(com.aptana.ide.logging.view.Messages.getString("LogView.NewRegexpRule.tooltip")); 
    }

    /**
     * Creates order actions.
     */
    private void createOrderActions()
    {
        actionMoveTabLeft = new Action()
        {
            public void run()
            {
                LogTab activeTab = getActiveTab();
                if (activeTab != null)
                {
                    LogTab tab = getActiveTab();
                    if (tab != null)
                    {
                        //getting old position and name
                        int oldPosition = tabFolder.indexOf(tab.getItem());
                        if (oldPosition == 0)
                        {
                            return;
                        }
                        String name = tab.getName();
                        
                        //removing old item
                        tab.getItem().dispose();
                        
                        //creating new tab item
                        tab.recreateItem(name, oldPosition - 1);
                        tabFolder.setSelection(tab.getItem());
                        tabFolder.redraw();
                    }
                }
                
            }
        };
        
        actionMoveTabLeft.setText(com.aptana.ide.logging.view.Messages.LogView_MoveTabLeftAction_name); 
        actionMoveTabLeft.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_MoveTabLeftAction_tooltip); 
        actionMoveTabLeft.setImageDescriptor(fMoveTabLeftActionDescriptor);
        
        actionMoveTabRight = new Action()
        {
            public void run()
            {
                LogTab activeTab = getActiveTab();
                if (activeTab != null)
                {
                    LogTab tab = getActiveTab();
                    if (tab != null)
                    {
                        //getting old position and name
                        int oldPosition = tabFolder.indexOf(tab.getItem());
                        if (oldPosition == tabFolder.getTabList().length - 1)
                        {
                            return;
                        }
                        String name = tab.getName();
                        
                        //removing old item
                        tab.getItem().dispose();
                        
                        //creating new tab item
                        tab.recreateItem(name, oldPosition + 1);
                        tabFolder.setSelection(tab.getItem());
                        tabFolder.redraw();
                    }
                }
                
            }
        };
        
        actionMoveTabRight.setText(com.aptana.ide.logging.view.Messages.LogView_MoveTabRightAction_name); 
        actionMoveTabRight.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_MoveTabRightAction_tooltip); 
        actionMoveTabRight.setImageDescriptor(fMoveTabRightActionDescriptor);
    }

    /**
     * Creates Rename Tab action.
     */
    private void createRenameTabAction()
    {
//        actionRenameTab = new Action()
//        {
//            public void run()
//            {
//                LogTab activeTab = getActiveTab();
//                if (activeTab != null)
//                {
//                    RenameTabDialog dialog = new RenameTabDialog(shell, activeTab.getName());
//                    if (dialog.open() == Window.OK)
//                    {
//                        activeTab.setName(dialog.getName());
//                    }
//                }
//                
//            }
//        };
    	actionRenameTab = new RenameTabAction(this);
        
        actionRenameTab.setText(com.aptana.ide.logging.view.Messages.LogView_RenameTabAction_name); 
        actionRenameTab.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_RenameTabAction_tooltip);
        markAsContentDependent(actionRenameTab);
    }

    /**
     * Creates Follow Tail action.
     */
    private void createFollowTailAction()
    {
        actionFollowTail = 
            new Action(
                    com.aptana.ide.logging.view.Messages.LogView_FollowTailAction_text,
                    IAction.AS_CHECK_BOX
                    )
        {
            /**
             * Whether action is checked.
             */
            private boolean checked = false;
            
            /**
             * {@inheritDoc}
             */
            public void run()
            {
                checked = !checked; 
                setChecked(checked);
                getActiveTab().invertFollowTail();
            }
        };
         
        actionFollowTail.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_FollowTailAction_tooltip); 
        actionFollowTail.setImageDescriptor(fScrollLockActionDescriptor);
    }

    /**
     * Creates Add action.
     */
    private void createAddAction()
    {
        actionAdd = new Action()
        {
            public void run()
            {
                FileDialog dialog = new FileDialog(shell);
                String path = dialog.open();
                if (path != null)
                {
                    addTab(path, null, true);
                }
            }
        };
        
        actionAdd.setText(com.aptana.ide.logging.view.Messages.LogView_3); 
        actionAdd.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_4); 
        actionAdd.setImageDescriptor(fAddActionDescriptor);
    }
    
    /**
     * Creates Add action.
     */
    private void createDeleteAction()
    {
        actionDelete = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    deleteTab(currentTab);
                }
            }
        };
        
        actionDelete.setText(com.aptana.ide.logging.view.Messages.LogView_5); 
        actionDelete.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_6); 
        actionDelete.setImageDescriptor(fRemoveActionDescriptor);
    }
    
    /**
     * Creates Clear action.
     */
    private void createClearLogAction()
    {
        actionLogClear = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    currentTab.clear();
                }
            }
        };
        
        actionLogClear.setText(com.aptana.ide.logging.view.Messages.LogView_7); 
        actionLogClear.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_8); 
        actionLogClear.setImageDescriptor(fClearLogActionDescriptor);
    }
    
    /**
     * Creates Clear action.
     */
    private void createClearLogFileAction()
    {
        actionLogFileClear = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    if (MessageDialog.openQuestion(
                            LoggingPlugin.getActiveWorkbenchShell(),
                            com.aptana.ide.logging.view.Messages.EraseConfirmDialog_Title,
                            com.aptana.ide.logging.view.Messages.EraseConfirmDialog_Message))
                    {
                        currentTab.clearLogFile();
                    }
                }
            }
        };
        
        actionLogFileClear.setText(com.aptana.ide.logging.view.Messages.LogView_9); 
        actionLogFileClear.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_10); 
        actionLogFileClear.setImageDescriptor(fClearLogFileActionDescriptor);
    }
    
    /**
     * Creates Delete All action.
     */
    private void createDeleteAllAction()
    {
        actionDeleteAll = new Action()
        {
            public void run()
            {
                closeAllTabs();
            }
        };
        
        actionDeleteAll.setText(com.aptana.ide.logging.view.Messages.LogView_11); 
        actionDeleteAll.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_12); 
        actionDeleteAll.setImageDescriptor(fRemoveAllActionDescriptor);
    }
    
    /**
     * Creates Start action.
     */
    private void createStartAction()
    {
        actionStart = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    currentTab.start();
                    updateWatchControlActions();
                    /*LogTab activeTab = getActiveTab();
                    if (activeTab != null)
                    {
	                	actionStart.setEnabled(false);
	                    actionStop.setEnabled(true);
                    }*/
                }
            }
        };
        
        actionStart.setText(com.aptana.ide.logging.view.Messages.LogView_13); 
        actionStart.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_14); 
        actionStart.setImageDescriptor(fStartActionDescriptor);
    }
    
    /**
     * Creates Stop action.
     */
    private void createStopAction()
    {
        actionStop = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    currentTab.stop();
                    updateWatchControlActions();
                    /*LogTab activeTab = getActiveTab();
                    if (activeTab != null)
                    {
	                	actionStart.setEnabled(true);
	                    actionStop.setEnabled(false);
                    }*/
                }
            }
        };
        
        actionStop.setText(com.aptana.ide.logging.view.Messages.LogView_15); 
        actionStop.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_16); 
        actionStop.setImageDescriptor(fStopActionDescriptor);
    }
    
    /**
     * Creates Reload action.
     */
    private void createReloadAction()
    {
        actionReload = new Action()
        {
            public void run()
            {
                LogTab currentTab = getActiveTab();
                if (currentTab != null)
                {
                    currentTab.reload();
                }
            }
        };
        
        actionReload.setText(com.aptana.ide.logging.view.Messages.LogView_17); 
        actionReload.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_18); 
        actionReload.setImageDescriptor(fReloadActionDescriptor);
    }
    
    /**
     * Creates find/replace action.
     */
    private void createFindReplaceAction()
    {
        actionFindReplace = new FindReplaceAction(Messages.getResourceBundle(),
                "FindReplace.", this);  //$NON-NLS-1$
//        actionFindReplace.setId(FIND_REPLACE);
        //actionFindReplace.setActionDefinitionId(FIND_REPLACE);
        //markAsContentDependentAction(FIND_REPLACE, true);
        actionFindReplace.setId(ActionFactory.FIND.getId());
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.FIND.getId(), actionFindReplace);
        markAsContentDependent(actionFindReplace);
    }
    
    /**
     * Creates default log actions.
     */
    private void createDefaultLogActions()
    {
    	defaultLogActions.clear();
        List<DefaultLogInfo> infos = DefaultLogsFactory.getLogURIs();
        for (DefaultLogInfo info : infos)
        {
            final DefaultLogInfo finalInfo = info;
            if (finalInfo.getUri() != null)
            {
                Action action = new Action()
                {
                    public void run()
                    {
                        addTab(finalInfo.getUri(), finalInfo.getName(), finalInfo.supportsLogErase());
                    }
                };
        
                action.setText(finalInfo.getName());
            
                defaultLogActions.add(action);
            }
        }
    }
    
    /**
     * Tools action.
     */
    private void createToolsAction()
    {
        toolsAction = new Action()
        {
            public void run()
            {
                
                PreferenceDialog dialog = 
                    PreferencesUtil.createPreferenceDialogOn(shell, 
                            "com.aptana.ide.logging.preferences.LoggingColorizationPreferencePage", new String[]{}, //$NON-NLS-1$
                            1);
                dialog.open();
            }
        };
        
        toolsAction.setText(com.aptana.ide.logging.view.Messages.LogView_ToolsAction_Label); 
        toolsAction.setToolTipText(com.aptana.ide.logging.view.Messages.LogView_ToolsAction_Tooltip); 
        toolsAction.setImageDescriptor(fToolsActionDescriptor);
    }
 
    /**
     * Gets current tab.
     * @return current tab, or null.
     */
    LogTab getActiveTab()
    {
        int tabIndex = tabFolder.getSelectionIndex();
        if (tabIndex < 0)
        {
            return null;
        }
        CTabItem item = tabFolder.getItem(tabIndex);
        
        for (LogTab tab : tabs.values())
        {
            if (tab.getItem().equals(item))
            {
                return tab;
            }
        }
        
        return null;
    }
    
    /**
     * Saves tabs configuration.
     */
    private void saveTabsConfiguration()
    {
        IDialogSettings settings = LoggingPlugin.getDefault().getDialogSettings();
        IDialogSettings section = settings.getSection(LOG_VIEW_SETTINGS_SECTION);
        if (section == null)
        {
            section = settings.addNewSection(LOG_VIEW_SETTINGS_SECTION);
        }
        
        ArrayList<String> tabURIs = new ArrayList<String>();
        ArrayList<String> tabNames = new ArrayList<String>();
        ArrayList<String> tabSupportsErase = new ArrayList<String>();
        
        for (LogTab tab : tabs.values())
        {
            tabURIs.add(tab.getURI().toString());
            tabNames.add(tab.getName());
            tabSupportsErase.add(Boolean.toString(tab.supportsLogErase()));
        }
        
        String[] uris = new String[tabURIs.size()];
        tabURIs.toArray(uris);
        
        String[] names = new String[tabNames.size()];
        tabNames.toArray(names);
        
        String[] supportsErase = new String[tabSupportsErase.size()];
        tabSupportsErase.toArray(supportsErase);
        
        section.put(LOG_VIEW_SET_TAB_URIS_KEY, uris);
        section.put(LOG_VIEW_SET_TAB_NAMES_KEY, names);
        section.put(LOG_VIEW_SET_TAB_SUPPORTS_ERASING, supportsErase);
    }
    
    /**
     * Loads tab configuration.
     */
    private void loadTabsConfiguration()
    {
        IDialogSettings settings = LoggingPlugin.getDefault().getDialogSettings();
        if (settings == null)
        {
            return;
        }
        IDialogSettings section = settings.getSection(LOG_VIEW_SETTINGS_SECTION);
        if (section == null)
        {
            return;
        }
        
        String[] tabURIs = section.getArray(LOG_VIEW_SET_TAB_URIS_KEY);
        String[] tabNames = section.getArray(LOG_VIEW_SET_TAB_NAMES_KEY);
        String[] tabSupportsEraseArray = section.getArray(LOG_VIEW_SET_TAB_SUPPORTS_ERASING);
        if (tabURIs == null || tabNames == null || tabURIs.length != tabNames.length)
        {
            return;
        }
        
        for (int i = 0; i < tabURIs.length; i++)
        {
            String tabURI = tabURIs[i];
            String tabName = tabNames[i];
            boolean tabSupportsErase = true;
            if (tabSupportsEraseArray != null)
            {
            	tabSupportsErase = Boolean.parseBoolean(tabSupportsEraseArray[i]);
            }
            
            try
            {
                addTab(new URI(tabURI), tabName, tabSupportsErase);
            } catch (URISyntaxException e)
            {
                IdeLog.logError(LoggingPlugin.getDefault(), 
                        com.aptana.ide.logging.view.Messages.LogView_20, e); 
            }
        }
     
        updateContentDependentActions();
    }
    
    /**
     * Marks action as content dependent.
     * @param action - action to mark.
     */
    void markAsContentDependent(Action action)
    {
        contentDependentActions.add(action);
    }
    
    /**
     * Updates content dependent actions.
     */
    private void updateContentDependentActions()
    {
        for (Action action : contentDependentActions)
        {
            if (action instanceof IUpdate)
            {
                ((IUpdate) action).update();
            }
        }
        updateWatchControlActions();
    }
    
    /**
     * Closes all tabs. 
     */
    private void closeAllTabs()
    {
        for (LogTab tab : tabs.values())
        {
            tab.close();
        }
        
        tabs.clear();
    }
    
    /**
     * Deletes tab.
     * @param tab - tab to delete.
     */
    private void deleteTab(LogTab tab)
    {
        tab.close();
        
        for (Entry<URI, LogTab> entry : tabs.entrySet())
        {
            if (entry.getValue().equals(tab))
            {
                tabs.remove(entry.getKey());
                break;
            }
        }
        //updateWatchControlActions();
        updateContentDependentActions();
    }
    
    private void updateWatchControlActions() 
    {
    	LogTab activeTab = getActiveTab();
    	actionStart.setEnabled(activeTab!=null&&!activeTab.isWatching());
        actionStop.setEnabled(activeTab!=null&&activeTab.isWatching());
        actionMoveTabLeft.setEnabled(activeTab!=null&&tabFolder.indexOf(activeTab.getItem())>0);
        actionMoveTabRight.setEnabled(activeTab!=null&&tabFolder.indexOf(activeTab.getItem())<(tabFolder.getItemCount()-1));
        if (activeTab != null)
        {
        	actionLogFileClear.setEnabled(activeTab.supportsLogErase());
        }
    }

    /**
     * 
     */
	public void dispose()
	{
		super.dispose();
		if (toolbarFont != null)
		{
			toolbarFont.dispose();
		}
	}
}
