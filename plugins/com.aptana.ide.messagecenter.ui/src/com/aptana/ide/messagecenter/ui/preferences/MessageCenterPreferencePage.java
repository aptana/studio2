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
package com.aptana.ide.messagecenter.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.dialogs.IAddItemListener;
import com.aptana.ide.core.ui.dialogs.TableEditor;
import com.aptana.ide.messagecenter.core.MessagingManager;
import com.aptana.ide.messagecenter.preferences.FeedDescriptor;

/**
 * Start page preference page
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class MessageCenterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IAddItemListener
{

	private TableEditor _tableEditor;
	private FeedDescriptor[] oldFeeds;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{

		Composite entryTable = new Composite(parent, SWT.NULL);

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout layout = new GridLayout();
		entryTable.setLayout(layout);

		Composite colorComposite = new Composite(entryTable, SWT.NONE);

		colorComposite.setLayout(new GridLayout());

		// Create a data that takes up the extra space in the dialog.
		colorComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group errorFilter = new Group(entryTable, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		errorFilter.setLayout(gridLayout);
		errorFilter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		errorFilter.setText(Messages.IntroPreferencePage_LBL_RSSFeeds);

		_tableEditor = new TableEditor(errorFilter, SWT.NULL, true);
		_tableEditor.setDescription(Messages.IntroPreferencePage_LBL_Description);

		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 150;
		_tableEditor.setLayoutData(data);

		oldFeeds = MessagingManager.getFeeds();
		List<Object> _items = new ArrayList<Object>(Arrays.asList(oldFeeds));

		_tableEditor.setLabelProvider(new TableLabelProvider());
		_tableEditor.addAddItemListener(this);

		new TableColumn(_tableEditor.getTable(), SWT.LEFT);
		_tableEditor.setItems(_items);

		return entryTable;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has been pressed. Sets the contents of the
	 * color field to the default value in the preference store.
	 */
	protected void performDefaults()
	{
		FeedDescriptor[] descriptors = MessagingManager.getFeeds();
		List<Object> _items = new ArrayList<Object>(Arrays.asList(descriptors));
		_tableEditor.setItems(_items);
	}

	/**
	 * Method declared on IPreferencePage. Save the color preference to the preference store.
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{
		List<Object> items = _tableEditor.getItems();
		for (Object object : items)
		{
			FeedDescriptor fd = (FeedDescriptor) object;
			MessagingManager.addFeed(fd);
		}
		for (int i = 0; i < oldFeeds.length; i++)
		{
			if (!items.contains(oldFeeds[i]))
			{
				MessagingManager.removeFeed(oldFeeds[i], true);
			}
		}
		MessagingManager.storeFeeds();
		MessagingManager.getNewMessages(new Date(0));

		return super.performOk();
	}

	/**
	 * Edits an item
	 * 
	 * @param item
	 * @return - object
	 */
	public Object editItem(Object item)
	{
		FeedDescriptor feed = (FeedDescriptor) item;
		InputDialog dialog = new InputDialog(CoreUIUtils.getActiveShell(), Messages.IntroPreferencePage_EditFeed_Title,
				Messages.IntroPreferencePage_EditFeed_Message, feed.getUrl(), new IInputValidator()
				{
					public String isValid(String newText)
					{
						if ("".equals(newText.trim())) //$NON-NLS-1$
						{
							return Messages.IntroPreferencePage_ValidURLWarning;
						}
						return null;
					}

				});

		int rc = dialog.open();
		if (rc == InputDialog.OK)
		{
			feed.setUrl(dialog.getValue().trim());
			return feed;
		}
		return null;
	}

	/**
	 * Prompt for resource type.
	 * 
	 * @return Object
	 */
	public Object addItem()
	{
		InputDialog dialog = new InputDialog(CoreUIUtils.getActiveShell(), Messages.IntroPreferencePage_AddFeed_Title,
				Messages.IntroPreferencePage_AddFeed_Message, null, new IInputValidator()
				{
					public String isValid(String newText)
					{
						if ("".equals(newText.trim())) //$NON-NLS-1$
						{
							return Messages.IntroPreferencePage_ValidURLWarning;
						}
						return null;
					}

				});

		int rc = dialog.open();
		if (rc == InputDialog.OK)
		{
			FeedDescriptor feed = new FeedDescriptor();
			feed.setUrl(dialog.getValue().trim());
			return feed;
		}
		return null;
	}

	/**
	 * TableLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	private static class TableLabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			Image image = null;
			switch (columnIndex)
			{
				default:
					break;
			}
			return image;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			String name = StringUtils.EMPTY;
			FeedDescriptor ed = (FeedDescriptor) element;
			switch (columnIndex)
			{
				case 0:
					name = ed.getUrl();
					break;
				default:
					break;
			}
			return name;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

}
