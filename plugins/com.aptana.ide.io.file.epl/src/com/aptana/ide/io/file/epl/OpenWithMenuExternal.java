/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.io.file.epl;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeSettings;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * A menu for opening files in the workbench.
 * <p>
 * An <code>OpenWithMenu</code> is used to populate a menu with "Open With" actions. One action is added for each
 * editor which is applicable to the selected file. If the user selects one of these items, the corresponding editor is
 * opened on the file.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class OpenWithMenuExternal extends ContributionItem
{

	private IVirtualFile file;

	private IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();

	private ViewPart view;

	private static Hashtable imageCache = new Hashtable(11);

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".OpenWithMenu";//$NON-NLS-1$

	/*
	 * Compares the labels from two IEditorDescriptor objects
	 */
	private static final Comparator comparer = new Comparator()
	{
		private Collator collator = Collator.getInstance();

		public int compare(Object arg0, Object arg1)
		{
			String s1 = ((IEditorDescriptor) arg0).getLabel();
			String s2 = ((IEditorDescriptor) arg1).getLabel();
			return collator.compare(s1, s2);
		}
	};

	/**
	 * Constructs a new instance of <code>OpenWithMenu</code>.
	 * 
	 * @param view
	 * @param file
	 *            the selected file
	 */
	public OpenWithMenuExternal(ViewPart view, IVirtualFile file)
	{
		super(ID);
		this.view = view;
		this.file = file;
	}

	/**
	 * Returns an image to show for the corresponding editor descriptor.
	 * 
	 * @param editorDesc
	 *            the editor descriptor, or null for the system editor
	 * @return the image or null
	 */
	private Image getImage(IEditorDescriptor editorDesc)
	{
		ImageDescriptor imageDesc = getImageDescriptor(editorDesc);
		if (imageDesc == null)
		{
			return null;
		}
		Image image = (Image) imageCache.get(imageDesc);
		if (image == null)
		{
			image = imageDesc.createImage();
			imageCache.put(imageDesc, image);
		}
		return image;
	}

	/**
	 * Returns the image descriptor for the given editor descriptor, or null if it has no image.
	 */
	private ImageDescriptor getImageDescriptor(IEditorDescriptor editorDesc)
	{
		ImageDescriptor imageDesc = null;
		if (editorDesc == null)
		{
			imageDesc = registry.getImageDescriptor(file.getName());
			// TODO: is this case valid, and if so, what are the implications for content-type editor bindings?
		}
		else
		{
			imageDesc = editorDesc.getImageDescriptor();
		}
		if (imageDesc == null)
		{
			if (editorDesc.getId().equals(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID))
			{
				imageDesc = registry.getSystemExternalEditorImageDescriptor(file.getName());
			}
		}
		return imageDesc;
	}

	/**
	 * Creates the menu item for the editor descriptor.
	 * 
	 * @param menu
	 *            the menu to add the item to
	 * @param descriptor
	 *            the editor descriptor, or null for the system editor
	 * @param preferredEditor
	 *            the descriptor of the preferred editor, or <code>null</code>
	 */
	private void createMenuItem(Menu menu, final IEditorDescriptor descriptor, final IEditorDescriptor preferredEditor)
	{
		// XXX: Would be better to use bold here, but SWT does not support it.
		final MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
		boolean isPreferred = preferredEditor != null && descriptor.getId().equals(preferredEditor.getId());
		menuItem.setSelection(isPreferred);
		menuItem.setText(descriptor.getLabel());
		Image image = getImage(descriptor);
		if (image != null)
		{
			menuItem.setImage(image);
		}
		Listener listener = new Listener()
		{
			public void handleEvent(Event event)
			{
				switch (event.type)
				{
					case SWT.Selection:
					{
						if (menuItem.getSelection())
						{
							openEditor(descriptor);
						}
						break;
					}
					default:
					{
						break;
					}
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
	 */
	public void fill(Menu menu, int index)
	{

		IEditorDescriptor defaultEditor = registry.findEditor(IDEWorkbenchPlugin.DEFAULT_TEXT_EDITOR_ID); // may be
		// null
		IEditorDescriptor preferredEditor = null;
		try
		{
			preferredEditor = IDE.getEditorDescriptor(file.getName());
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			IdeLog.logError(CoreUIPlugin.getDefault(), e.getMessage());
		} // may be null

		IContentType finalType = null;
		for (IContentType type : Platform.getContentTypeManager().getAllContentTypes())
		{
			if (finalType != null)
			{
				break;
			}
			try
			{
				for (String settings : type.getSettings(null).getFileSpecs(IContentTypeSettings.FILE_NAME_SPEC))
				{
					if (settings.equals(file.getName()))
					{
						finalType = type;
						break;
					}
				}
				if (finalType == null)
				{
					for (String settings : type.getSettings(null)
							.getFileSpecs(IContentTypeSettings.FILE_EXTENSION_SPEC))
					{
						if (settings.equals(FileUtils.getExtension(file.getName())))
						{
							finalType = type;
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(Activator.getDefault(), OpenWithMenuExternalMessages.OpenWithMenuExternal_ERR_ErrorFindingContentType, e);
			}
		}
		Object[] editors = registry.getEditors(file.getName(), finalType);
		Collections.sort(Arrays.asList(editors), comparer);

		boolean defaultFound = false;

		// Check that we don't add it twice. This is possible
		// if the same editor goes to two mappings.
		ArrayList alreadyMapped = new ArrayList();

		for (int i = 0; i < editors.length; i++)
		{
			IEditorDescriptor editor = (IEditorDescriptor) editors[i];
			if (!alreadyMapped.contains(editor))
			{
				createMenuItem(menu, editor, preferredEditor);
				if (defaultEditor != null && editor.getId().equals(defaultEditor.getId()))
				{
					defaultFound = true;
				}
				alreadyMapped.add(editor);
			}
		}

		// Only add a separator if there is something to separate
		if (editors.length > 0)
		{
			new MenuItem(menu, SWT.SEPARATOR);
		}

		// Add default editor. Check it if it is saved as the preference.
		if (!defaultFound && defaultEditor != null)
		{
			createMenuItem(menu, defaultEditor, preferredEditor);
		}

		// Add system editor (should never be null)
		IEditorDescriptor descriptor = registry.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		createMenuItem(menu, descriptor, preferredEditor);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	public boolean isDynamic()
	{
		return true;
	}

	/**
	 * Opens the given editor on the selected file.
	 * 
	 * @param editor
	 *            the editor descriptor, or null for the system editor
	 */
	private void openEditor(IEditorDescriptor editor)
	{

		String editorId = editor == null ? IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID : editor.getId();

		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();

		IEditorDescriptor desc = editorReg.findEditor(editorId);
		CoreUIUtils.openFileInEditor(file, desc);
	}
}
