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
package com.aptana.ide.syncing.ui.ingo.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;

import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.ProtocolManager;

/**
 * @author Chris Williams (cwilliams@aptana.com)
 */
public class SyncManagerFileLabelProvider extends LabelProvider implements ITableLabelProvider
{

	private static final String UNKNOWN = Messages.SyncManagerFileLabelProvider_Unknown;

	private HashMap<IEditorDescriptor, Image> images = new HashMap<IEditorDescriptor, Image>();

	private int sizeIndex = 1;
	private int modificationIndex = 2;

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return getFileImage(element);
			default:
				break;
		}
		return null;
	}

	private Image getFileImage(Object element)
	{
//		Image image = null;
//
//		if (element instanceof ProtocolManager)
//		{
//			ProtocolManager pm = (ProtocolManager) element;
//			image = pm.getImage();
//		}
//		else if (element instanceof IVirtualFileManager)
//		{
//			IVirtualFileManager fm = (IVirtualFileManager) element;
//			image = fm.getImage();
//		}
//		else if (element instanceof IVirtualFile)
//		{
//			IVirtualFile f = (IVirtualFile) element;
//			image = f.getImage();
//			if (image == null)
//			{
//				IEditorDescriptor desc = EclipseUIUtils.getWorkbenchEditorRegistry().getDefaultEditor(f.getName());
//				if (desc == null || desc.getImageDescriptor() == null)
//				{
//					IWorkbench workbench = PlatformUI.getWorkbench();
//					if (workbench != null)
//					{
//						ISharedImages sharedImages = workbench.getSharedImages();
//						if (f.isDirectory())
//						{
//							image = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
//						}
//						else
//						{
//							image = ImageUtils.getIcon(f.getExtension());
//							if (image == null)
//							{
//								image = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
//							}
//						}
//					}
//				}
//				else
//				{
//					if (images.containsKey(desc))
//					{
//						image = images.get(desc);
//					}
//					else
//					{
//						image = desc.getImageDescriptor().createImage();
//						images.put(desc, image);
//					}
//				}
//			}
//		}
//		if (element == FileTreeContentProvider.LOADING)
//		{
//			return SyncingPlugin.getImage("icons/full/obj16/hourglass.png"); //$NON-NLS-1$
//		}
		return null; //image;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			return getFileText(element);
		}
		if (columnIndex == sizeIndex)
		{
			return getFilesize(element);
		}
		if (columnIndex == modificationIndex)
		{
			return getLastModified(element);
		}
		return UNKNOWN;
	}

	private static String getLastModified(Object element)
	{
		if (element instanceof IFileStore)
		{
			IFileStore f = (IFileStore) element;
			SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); //$NON-NLS-1$
			if (f.fetchInfo().getLastModified() >= 0)
			{
				return sdfOutput.format(new Date(f.fetchInfo().getLastModified()));
			}
			return UNKNOWN;
		}
		return ""; //$NON-NLS-1$
	}

	private static String getFilesize(Object element)
	{
		if (element instanceof IFileStore)
		{
			IFileStore f = (IFileStore) element;
			if (!f.fetchInfo().isDirectory())
			{
				long rawSize = f.fetchInfo().getLength();
				long leftover = 0;
				String string = Long.toString(rawSize) + " B"; //$NON-NLS-1$
				if (rawSize > 1024)
				{
					rawSize = rawSize / 1024;
					leftover = rawSize % 1024;
					long num = rawSize;
					if (leftover >= 512)
						num++;
					string = num + " KB"; //$NON-NLS-1$
				}
				if (rawSize > 1024)
				{
					rawSize = rawSize / 1024;
					leftover = rawSize % 1024;
					long num = rawSize;
					if (leftover >= 512)
						num++;
					string = num + " MB"; //$NON-NLS-1$
				}
				if (rawSize > 1024)
				{
					rawSize = rawSize / 1024;
					leftover = rawSize % 1024;
					long num = rawSize;
					if (leftover >= 512)
						num++;
					string = num + " GB"; //$NON-NLS-1$
				}
				if (rawSize > 1024)
				{
					rawSize = rawSize / 1024;
					leftover = rawSize % 1024;
					long num = rawSize;
					if (leftover >= 512)
						num++;
					string = num + " TB"; //$NON-NLS-1$
				}
				return string;
			}
		}
		return ""; //$NON-NLS-1$
	}

	private static String getFileText(Object element)
	{
		String retVal = null;
		if (element instanceof ProtocolManager)
		{
			ProtocolManager pm = (ProtocolManager) element;
			retVal = pm.getDisplayName();
		}
		else if (element instanceof IVirtualFileManager)
		{
			retVal = ((IVirtualFileManager) element).getDescriptiveLabel();
		}
		else if (element instanceof IFileStore)
		{
			IFileStore f = (IFileStore) element;
			retVal = f.getName();
		}
//		if (element == FileTreeContentProvider.LOADING)
//		{
//			return Messages.SyncManagerFileLabelProvider_StatusLoading;
//		}
//		if (retVal == null)
//		{
//			retVal = com.aptana.ide.core.ui.views.fileexplorer.Messages.FileExplorerView_UnknownElement;
//		}
		return retVal;
	}

	/**
	 * @param modificationIndex
	 *            the modification index to set
	 */
	public void setModificationIndex(int modificationIndex)
	{
		this.modificationIndex = modificationIndex;
	}

	/**
	 * @param sizeIndex
	 *            the size index to set
	 */
	public void setSizeIndex(int sizeIndex)
	{
		this.sizeIndex = sizeIndex;
	}

}
