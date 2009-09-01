/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.core;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.aptana.ide.core.io.IVirtualFile;

/**
 * Previous org.eclipse.ui.internal.editors.text.JavaFileEditorInput, removed in 3.3
 * 
 * @since 3.0
 */
public class BaseFileEditorInputImpl extends PlatformObject implements BaseFileEditorInput
{
	
	/**
	 * The workbench adapter which simply provides the label.
	 * 
	 * @since 3.1
	 */
	private class WorkbenchAdapter implements IWorkbenchAdapter
	{
		/**
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object o)
		{
			return null;
		}

		/**
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
		 */
		public ImageDescriptor getImageDescriptor(Object object)
		{
			return null;
		}

		/**
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
		 */
		public String getLabel(Object o)
		{
			return ((BaseFileEditorInput) o).getName();
		}

		/**
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
		 */
		public Object getParent(Object o)
		{
			return null;
		}
	}

	/**
	 * fFileStore
	 */
	protected IFileStore fFileStore;
	private WorkbenchAdapter fWorkbenchAdapter = new WorkbenchAdapter();
	private IStorage fStorage;
	private IPath fPath;
	private IVirtualFile virtualFile;

	/**
	 * Creates a new input
	 * 
	 * @param fileStore
	 */
	public BaseFileEditorInputImpl(IFileStore fileStore)
	{
		Assert.isNotNull(fileStore);
		Assert.isTrue(EFS.SCHEME_FILE.equals(fileStore.getFileSystem().getScheme()));
		fFileStore = fileStore;
		fWorkbenchAdapter = new WorkbenchAdapter();
		virtualFile = null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists()
	{
		return fFileStore.fetchInfo().exists();
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName()
	{
		return fFileStore.getName();
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		return fFileStore.toString();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		if (ILocationProvider.class.equals(adapter))
			return this;
		if (IWorkbenchAdapter.class.equals(adapter))
			return fWorkbenchAdapter;
		return super.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element)
	{
		if (element instanceof BaseFileEditorInput)
			return ((BaseFileEditorInput) element).getPath();

		return null;
	}

	/**
	 * @see org.eclipse.ui.IPathEditorInput#getPath()
	 * @since 3.1
	 */
	public IPath getPath()
	{
		if (fPath == null)
			fPath = new Path(fFileStore.toURI().getPath());
		return fPath;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o == this)
			return true;

		if (o instanceof BaseFileEditorInputImpl)
		{
			BaseFileEditorInputImpl input = (BaseFileEditorInputImpl) o;
			return fFileStore.equals(input.fFileStore);
		}

		if (o instanceof IPathEditorInput)
		{
			IPathEditorInput input = (IPathEditorInput) o;
			return getPath().equals(input.getPath());
		}

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return fFileStore.hashCode();
	}

	/**
	 * @see org.eclipse.ui.IStorageEditorInput#getStorage()
	 * @since 3.2
	 */
	public IStorage getStorage() throws CoreException
	{

		if (fStorage == null)
			fStorage = new BaseFileStorage(fFileStore);
		return fStorage;
	}

	/**
	 * @param localFile
	 * @return editor input
	 */
	public static IEditorInput create(IFileStore localFile)
	{
		try
		{
			BaseFileEditorInputImpl baseFileEditorInput = new BaseFileEditorInputImpl(localFile);
			return baseFileEditorInput;
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	public IVirtualFile getVirtualFile()
	{
		return virtualFile;
	}

	public void setVirtualFile(IVirtualFile virtualFile)
	{
		this.virtualFile = virtualFile;
	}

	/**
	 * @see org.eclipse.ui.IURIEditorInput#getURI()
	 */
	public URI getURI()
	{		
		return fFileStore.toURI();
	}

}
