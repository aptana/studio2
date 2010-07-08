/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.FileExtensionDialog;
import org.eclipse.ui.internal.registry.FileEditorMapping;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.dialogs.IAddItemListener;
import com.aptana.ide.core.ui.dialogs.TableEditor;

/**
 * Base preference page for showing a table of editable items based loosely on file types and extensions
 * 
 * @author Ingo Muschenetz
 */
public abstract class FileExtensionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
		IAddItemListener
{
	private ImageRegistry imageRegistry;

	private TableEditor _tableEditor;

	private List<Object> _items = new ArrayList<Object>();

	private static Image blank_icon = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

	public String addResource(String newName, String newExtension)
	{
		// newName or extension must not be blank
		if ((newName == null || StringUtils.EMPTY.equals(newName))
				&& (newExtension == null || StringUtils.EMPTY.equals(newExtension)))
		{
			MessageDialog.openInformation(getControl().getShell(), Messages.FileExtensionPreferencePage_TTL_ErrorAddingItem,
					Messages.FileExtensionPreferencePage_MSG_MustEnterAtleastNameOfAnItemOrExtension);
			return null;
		}

		int index = newName.indexOf('*');
		if (index > -1)
		{
			if (newName.equals("*") && (newExtension == null || StringUtils.EMPTY.equals(newExtension))) //$NON-NLS-1$
			{
				MessageDialog.openInformation(getControl().getShell(), Messages.FileExtensionPreferencePage_TTL_ErrorAddingItem,
						Messages.FileExtensionPreferencePage_MSG_MustEnterExtensionWithAWildcard);
				return null;
			}
		}

		String newFilename = newName;
		if (newExtension != null && newExtension.length() > 0)
		{
			newFilename += "." + newExtension; //$NON-NLS-1$
		}
		newFilename = newFilename.toUpperCase();

		int i = 0;
		while (i < _items.size())
		{
			String item = (String) _items.get(i);
			int result = newFilename.compareToIgnoreCase(item);
			if (result == 0)
			{
				MessageDialog.openInformation(getControl().getShell(),
						Messages.FileExplorerPreferencePage_FileTypeExists,
						Messages.FileExplorerPreferencePage_AnEntryAlreadyExistsTitle);
				return null;
			}
			i++;
		}

		if (StringUtils.EMPTY.equals(newExtension))
		{
			return newName;
		}
		else
		{
			return newName + "." + newExtension; //$NON-NLS-1$
		}
	}

	/**
	 * Creates the page's UI content.
	 * 
	 * @param parent
	 * @return Control
	 */
	protected Control createContents(Composite parent)
	{
		_tableEditor = new TableEditor(parent, SWT.NULL);
		_tableEditor.setDescription(getTableDescription());

		_tableEditor.setLabelProvider(new TableLabelProvider());
		_tableEditor.addAddItemListener(this);

		new TableColumn(_tableEditor.getTable(), SWT.LEFT);
		fillCurrentResourceTypeTable();
		if (_tableEditor.getTable().getItemCount() > 0)
		{
			_tableEditor.getTable().setSelection(0);
		}

		applyDialogFont(_tableEditor);
		return _tableEditor;
	}

	/**
	 * getTableDescription
	 * 
	 * @return String
	 */
	protected String getTableDescription()
	{
		return StringUtils.EMPTY;
	}

	/**
	 * Fills the resource type table with the current preference value
	 */
	protected void fillCurrentResourceTypeTable()
	{
		IPreferenceStore store = doGetPreferenceStore();
		String editors = store.getString(doGetPreferenceID());
		fillResourceTypeTable(editors);
	}

	/**
	 * Fills the resource type table with the default preference value
	 */
	protected void fillDefaultResourceTypeTable()
	{
		IPreferenceStore store = doGetPreferenceStore();
		String editors = store.getDefaultString(doGetPreferenceID());
		fillResourceTypeTable(editors);
	}

	/**
	 * fillResourceTypeTable
	 * 
	 * @param editors
	 *            ';' seperated list string
	 */
	protected void fillResourceTypeTable(String editors)
	{
		if (editors != null)
		{
			_items.clear();
			String[] array = editors.split(";"); //$NON-NLS-1$
			_items.addAll(Arrays.asList(array));
			_tableEditor.setItems(_items);
		}
	}

	/**
	 * getSelectedResourceType
	 * 
	 * @return FileEditorMapping
	 */
	protected FileEditorMapping getSelectedResourceType()
	{
		TableItem[] items = _tableEditor.getTable().getSelection();
		if (items.length > 0)
		{
			return (FileEditorMapping) items[0].getData(); // Table is single
			// select
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench aWorkbench)
	{
	}

	/**
	 * Returns the icon for the file type with the specified extension.
	 * 
	 * @param extension
	 * @return Image
	 */
	private Image getIcon(String extension)
	{
		if (imageRegistry == null)
		{
			imageRegistry = new ImageRegistry();
		}
		Image image = imageRegistry.get(extension);
		if (image != null)
		{
			return image;
		}

		Program program = Program.findProgram(extension);
		ImageData imageData = (program == null ? null : program.getImageData());
		if (imageData != null)
		{
			image = new Image(this.getControl().getDisplay(), imageData);
			imageRegistry.put(extension, image);
		}
		else
		{
			image = blank_icon;
		}

		return image;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{

		fillDefaultResourceTypeTable();
	}

	/**
	 * Saves the items from the table editor into the preference store
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{
		IPreferenceStore store = doGetPreferenceStore();

		List<Object> items = _tableEditor.getItems();
		String resources = StringUtils.join(";", items.toArray(new String[items.size()])); //$NON-NLS-1$

		store.setValue(doGetPreferenceID(), resources);

		return true;
	}

	/**
	 * Prompt for item
	 * 
	 * @return Object
	 */
	public Object addItem()
	{
		FileExtensionDialog dialog = new FileExtensionDialog(getControl().getShell());
		if (dialog.open() == Window.OK)
		{
			String name = dialog.getName();
			String extension = dialog.getExtension();
			return addResource(name, extension);
		}
		return null;
	}

	/**
	 * doGetPreferenceID
	 * 
	 * @return String
	 */
	protected abstract String doGetPreferenceID();

	/**
	 * doGetPreferenceStore
	 * 
	 * @return IPreferenceStore
	 */
	protected abstract IPreferenceStore doGetPreferenceStore();

	/**
	 * doGetPlugin
	 * 
	 * @return Plugin
	 */
	protected abstract Plugin doGetPlugin();

	/**
	 * Sets the item
	 * 
	 * @param item
	 * @return Object
	 */
	public Object setItem(Object item)
	{
		return null;
	}

	/**
	 * editItem
	 * 
	 * @param item
	 * @return Object
	 */
	public Object editItem(Object item)
	{
		return null;
	}

	/**
	 * TableLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	public class TableLabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			Image image = null;
			switch (columnIndex)
			{
				case 0:
					String newExtension = StringUtils.replace((String) element, "*.", ""); //$NON-NLS-1$ //$NON-NLS-2$
					image = getIcon(newExtension);
					break;
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
			switch (columnIndex)
			{
				case 0:
					name = element.toString();
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
