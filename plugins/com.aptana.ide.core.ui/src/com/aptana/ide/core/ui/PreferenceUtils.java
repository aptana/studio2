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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Pavel Petrochenko
 */
public final class PreferenceUtils
{

	/**
	 * IGNORE_COLOR_KEY
	 */
	public static final String IGNORE_COLOR_KEY = "IGNORE_COLOR_KEY"; //$NON-NLS-1$

	private PreferenceUtils()
	{

	}

	/**
	 * stores widths of table columns to preference store and back
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void persistSettingsToPreferenceStore(IPreferenceStore store, String key, Table table)
	{
		TableColumn[] columns = table.getColumns();
		for (int a = 0; a < columns.length; a++)
		{
			int width = columns[a].getWidth();
			store.setValue(getColumnKey(key, a), width);
		}
	}

	/**
	 * restores columns widthes, from preferences, also ecranizes first layout in case when table has TableLayoutManager
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void restoreSettingsFromPreferenceStore(final IPreferenceStore store, final String key,
			final Table table)
	{
		final TableColumn[] columns = table.getColumns();
		final boolean found = actuallyRestore(store, key, columns);
		if (table.getLayout() instanceof TableLayout && found)
		{
			final TableLayout ll = (TableLayout) table.getLayout();
			table.setLayout(null);
			table.addControlListener(new ControlListener()
			{

				int inc = 0;

				public void controlMoved(ControlEvent e)
				{
				}

				public void controlResized(ControlEvent e)
				{
					if (inc == 1)
					{
						table.setLayout(ll);
					}
					inc++;
				}

			});
		}
	}

	private static boolean actuallyRestore(IPreferenceStore store, String key, TableColumn[] columns)
	{
		boolean found = false;
		for (int a = 0; a < columns.length; a++)
		{
			int int1 = store.getInt(getColumnKey(key, a));
			if (int1 != 0)
			{
				found = true;
				columns[a].setWidth(int1);
			}
		}

		return found;
	}

	/**
	 * restoring columns width settings from preference store
	 * 
	 * @param store
	 * @param key
	 * @param tree
	 */
	public static void restoreSettingsFromPreferenceStore(IPreferenceStore store, String key, final Tree tree)
	{
		TreeColumn[] columns = tree.getColumns();
		boolean found = false;
		for (int a = 0; a < columns.length; a++)
		{
			int int1 = store.getInt(getColumnKey(key, a));
			if (int1 != 0)
			{
				columns[a].setWidth(int1);
				found = true;
			}
		}
		if (tree.getLayout() instanceof TableLayout && found)
		{
			final TableLayout ll = (TableLayout) tree.getLayout();
			tree.setLayout(null);
			tree.addControlListener(new ControlListener()
			{

				int inc = 0;

				public void controlMoved(ControlEvent e)
				{
				}

				public void controlResized(ControlEvent e)
				{
					if (inc == 1)
					{
						tree.setLayout(ll);
					}
					inc++;
				}

			});
		}
	}

	private static String getColumnKey(String key, int num)
	{
		return key + ".column." + num; //$NON-NLS-1$
	}

	/**
	 * registers tree for persisting widths of its column to preference store and back
	 * 
	 * @param store
	 * @param key
	 * @param tree
	 */
	public static void persistSettingsToPreferenceStore(IPreferenceStore store, String key, Tree tree)
	{
		TreeColumn[] columns = tree.getColumns();
		for (int a = 0; a < columns.length; a++)
		{
			int width = columns[a].getWidth();
			store.setValue(getColumnKey(key, a), width);
		}
	}

	/**
	 * registers table as table with a persisistent widths of columns restores settings from preference store and adds
	 * listener for storing widths when table will be disposed
	 * 
	 * @param store
	 * @param table
	 * @param key
	 */
	public static void persist(final IPreferenceStore store, final Table table, final String key)
	{
		PreferenceUtils.restoreSettingsFromPreferenceStore(store, key, table);
		table.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				PreferenceUtils.persistSettingsToPreferenceStore(store, key, table);
			}

		});
	}

	/**
	 * registers tree as table with a persisistent widths of columns restores settings from preference store and adds
	 * listener for storing widths when table will be disposed
	 * 
	 * @param store
	 * @param tree
	 * @param key
	 */
	public static void persist(final IPreferenceStore store, final Tree tree, final String key)
	{
		PreferenceUtils.restoreSettingsFromPreferenceStore(store, key, tree);
		tree.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				PreferenceUtils.persistSettingsToPreferenceStore(store, key, tree);
			}

		});
	}

	/**
	 * binds control background to key with a given id from eclipse theme manager
	 * 
	 * @param control
	 * @param id
	 */
	public static void registerBackgroundColorPreference(final Control control, final String id)
	{
		final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

		setBackround(control, id, themeManager);
		final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				// if the user is now using a new theme or if the user
				// has customized the value of the background color
				// update the control
				if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME) || event.getProperty().equals(id))
				{
					setBackround(control, id, themeManager);
				}
			}
		};
		themeManager.addPropertyChangeListener(propertyChangeListener);
		control.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				themeManager.removePropertyChangeListener(propertyChangeListener);
			}

		});
	}

	/**
	 * binds control font to key with a given id from eclipse theme manager
	 * 
	 * @param control
	 * @param id
	 */
	public static void registerFontPreference(final Control control, final String id)
	{
		final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

		setFont(control, id, themeManager);
		final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				// if the user is now using a new theme or if the user
				// has customized the value of the background color
				// update the control
				if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME) || event.getProperty().equals(id))
				{
					setFont(control, id, themeManager);
				}
			}
		};
		themeManager.addPropertyChangeListener(propertyChangeListener);
		control.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				themeManager.removePropertyChangeListener(propertyChangeListener);
			}

		});
	}

	private static void setFont(Control control, String id, IThemeManager themeManager)
	{
		Font color = themeManager.getCurrentTheme().getFontRegistry().get(id);
		control.setFont(color);
	}

	/**
	 * binds control foreground to key with a given id from eclipse theme manager
	 * 
	 * @param control
	 * @param id
	 */
	public static void registerForegroundColorPreference(final Control control, final String id)
	{
		final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

		setForeground(control, id, themeManager);
		final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				// if the user is now using a new theme or if the user
				// has customized the value of the background color
				// update the control
				if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME) || event.getProperty().equals(id))
				{
					setForeground(control, id, themeManager);
				}
			}
		};
		themeManager.addPropertyChangeListener(propertyChangeListener);
		control.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				themeManager.removePropertyChangeListener(propertyChangeListener);
			}

		});

	}

	private static void setForeground(final Control control, final String id, final IThemeManager themeManager)
	{
		Color color = themeManager.getCurrentTheme().getColorRegistry().get(id);
		internalSet(control, color);
	}

	private static void setBackround(final Control control, final String id, final IThemeManager themeManager)
	{
		Color color = themeManager.getCurrentTheme().getColorRegistry().get(id);
		control.setBackground(color);
	}

	/**
	 * Registers a control that will ignore the preference for the foreground color. Designed to allow message areas to
	 * have subtle gray messages but allow custom colors when the control actually has content (more than just a message
	 * about usage)
	 * 
	 * @param control
	 */
	public static void ignoreForegroundColorPreference(Control control)
	{
		if (control != null)
		{
			control.setData(PreferenceUtils.IGNORE_COLOR_KEY, Boolean.TRUE);
		}
	}

	private static void internalSet(final Control control, Color color)
	{
		if (!Boolean.TRUE.equals(control.getData(IGNORE_COLOR_KEY)))
		{
			control.setForeground(color);
		}
		if (control instanceof Composite)
		{
			Composite cm = (Composite) control;
			Control[] children = cm.getChildren();
			for (int a = 0; a < children.length; a++)
			{
				internalSet(children[a], color);
			}
		}
	}
}
