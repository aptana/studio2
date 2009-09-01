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

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.preferences.ThemeManagerAdapter;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultTabFolder;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultTabItem;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultThemeListener;
import org.eclipse.ui.internal.presentations.util.AbstractTabItem;
import org.eclipse.ui.internal.presentations.util.PartInfo;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.StackPresentation;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Tab tools.
 * @author Denis Denisenko
 */
public final class TabTools
{

    /**
     * Custom presentable part.
     * @TabTools
     * @author Denis Denisenko
     */
    private final static class CustomPresentablePart implements IPresentablePart
    {
        /**
         * Name.
         */
        private String name;
        
        /**
         * Tab control.
         */
        private Control control;
        
        /**
         * CustomPresentablePart constructor.
         * @param tabName - tab name.
         * @param control - control.
         */
        public CustomPresentablePart(String tabName, Control control)
        {
            this.name = tabName;
            this.control = control;
        }

        /**
          * {@inheritDoc}
          */
        public void addPropertyListener(IPropertyListener listener)
        {
            // TODO Auto-generated method stub
            
        }

        /**
          * {@inheritDoc}
          */
        public Control getControl()
        {
            return control;
        }

        /**
          * {@inheritDoc}
          */
        public IPartMenu getMenu()
        {
            // TODO Auto-generated method stub
            return null;
        }

        /**
          * {@inheritDoc}
          */
        public String getName()
        {
            return name;
        }

        /**
          * {@inheritDoc}
          */
        public String getTitle()
        {
            return name;
        }

        /**
          * {@inheritDoc}
          */
        public Image getTitleImage()
        {
            return null;
        }

        /**
          * {@inheritDoc}
          */
        public String getTitleStatus()
        {
            return null;
        }

        /**
          * {@inheritDoc}
          */
        public String getTitleToolTip()
        {
            return null;
        }

        /**
          * {@inheritDoc}
          */
        public Control getToolBar()
        {
            return null;
        }

        /**
          * {@inheritDoc}
          */
        public boolean isBusy()
        {
            return false;
        }

        /**
          * {@inheritDoc}
          */
        public boolean isCloseable()
        {
            return true;
        }

        /**
          * {@inheritDoc}
          */
        public boolean isDirty()
        {
            return false;
        }

        /**
          * {@inheritDoc}
          */
        public void removePropertyListener(IPropertyListener listener)
        {
        }

        /**
          * {@inheritDoc}
          */
        public void setBounds(Rectangle bounds)
        {            
        }

        /**
          * {@inheritDoc}
          */
        public void setFocus()
        {
           
        }

        /**
          * {@inheritDoc}
          */
        public void setVisible(boolean isVisible)
        {
        }

		/**
		 * @see org.eclipse.ui.presentations.IPresentablePart#addPartPropertyListener(org.eclipse.jface.util.IPropertyChangeListener)
		 */
		public void addPartPropertyListener(IPropertyChangeListener listener)
		{
		}

		/**
		 * @see org.eclipse.ui.presentations.IPresentablePart#getPartProperty(java.lang.String)
		 */
		public String getPartProperty(String key)
		{
			return null;
		}

		/**
		 * @see org.eclipse.ui.presentations.IPresentablePart#removePartPropertyListener(org.eclipse.jface.util.IPropertyChangeListener)
		 */
		public void removePartPropertyListener(IPropertyChangeListener listener)
		{
		}

		/**
		 * @see org.eclipse.ui.presentations.IPresentablePart#computePreferredSize(boolean, int, int, int)
		 */
		public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
				int preferredResult)
		{
			// Added for Eclipse 3.4 compatibility
			return 0;
		}

		/**
		 * @see org.eclipse.ui.presentations.IPresentablePart#getSizeFlags(int)
		 */
		public int getSizeFlags(boolean width)
		{
			// Added for Eclipse 3.4 compatibility
			return 0;
		}
    }
    
    /**
     * Creates tab folder.
     * @param parent - parent.
     * @return tab folder.
     */
    public static DefaultTabFolder createTabFolder(Composite parent)
    {
        final DefaultTabFolder fld = new DefaultTabFolder(parent, SWT.CLOSE, false, false);
        fld.setSimpleTabs(false);
        ToolBar toolBar = new ToolBar(fld.getToolbarParent(), SWT.NONE);
        fld.setToolbar(toolBar);
        ToolBarManager man = new ToolBarManager(toolBar);
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        final DefaultThemeListener themeListener = new DefaultThemeListener(fld,
                new ThemeManagerAdapter(themeManager));
        IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
        {

            public void propertyChange(PropertyChangeEvent event)
            {
                themeListener.update();
            }

        };
        themeManager.addPropertyChangeListener(propertyChangeListener);
        themeListener.update();
        
        fld.setActive(StackPresentation.AS_ACTIVE_FOCUS);
        fld.shellActive(false);
        fld.getControl().addControlListener(new ControlListener()
        {

            public void controlMoved(ControlEvent e)
            {

            }

            public void controlResized(ControlEvent e)
            {
                fld.layout(true);
            }

        });
        
        fld.layout(true);
        
        return fld;
    }
    
    /**
     * Adds tab. 
     * @param fld - tab folder.
     * @param name - tab name.
     * @param control - tab control.
     * 
     * @return create tab item.
     */
    public static DefaultTabItem addTab(DefaultTabFolder fld, String name, Control control)
    {
        return addTab(fld, name, control, 0);
    }
    
    /**
     * Adds tab. 
     * @param fld - tab folder.
     * @param name - tab name.
     * @param control - tab control.
     * @param pos - position.
     * 
     * @return create tab item.
     */
    public static DefaultTabItem addTab(DefaultTabFolder fld, String name, Control control, int pos)
    {
        DefaultTabItem add = (DefaultTabItem) fld.add(pos, SWT.NONE);
        add.setInfo(new PartInfo(new CustomPresentablePart(name, control)));
        fld.setSelection(add);
        
        return add;
    }
    
    /**
     * Closes tab.
     * @param tab - tab to close.
     */
    public static void removeTab(AbstractTabItem tab)
    {
        tab.dispose();
    }
    
    /**
     * TabTools private constructor.
     */
    private TabTools()
    {
    }
}
