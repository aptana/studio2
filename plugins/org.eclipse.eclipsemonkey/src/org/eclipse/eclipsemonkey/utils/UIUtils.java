/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.utils;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

/**
 * @author Paul Colton
 */
public class UIUtils
{
	/*
	 * Are we in Eclipse 3.2 or higher?
	 */
	static boolean inEclipse32orHigher = false;

	static
	{

		String version = System.getProperty("osgi.framework.version"); //$NON-NLS-1$

		if (version != null && version.startsWith("3.")) //$NON-NLS-1$
		{
			String[] parts = version.split("\\."); //$NON-NLS-1$
			if (parts.length > 1)
			{
				try
				{
					if (Integer.parseInt(parts[1]) > 1)
						inEclipse32orHigher = true;
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @param secondaryId
	 * @return IWorkbenchPart
	 */
	public static IWorkbenchPart getViewInternal(final String id, final String secondaryId)
	{
		/**
		 * Internal class for getting a view.
		 * 
		 * @author Ingo Muschenetz
		 */
		IWorkbenchPart[] parts = getViewsInternal(id, secondaryId);
		if (parts.length == 0)
		{
			return null;
		}
		else
		{
			return parts[0];
		}
	}

	/**
	 * Gets all views with the primary part id, and any secondary part id
	 * 
	 * @param id
	 * @return IWorkbenchPart
	 */
	public static IWorkbenchPart[] getViewsInternal(final String id)
	{
		return getViewsInternal(id, null);
	}

	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @param secondaryId
	 * @return IWorkbenchPart
	 */
	public static IWorkbenchPart[] getViewsInternal(final String id, final String secondaryId)
	{
		/**
		 * Internal class for getting a view.
		 * 
		 * @author Ingo Muschenetz
		 */
		class ViewGetterThread implements Runnable
		{
			/*
			 * Fields
			 */
			public ArrayList targetView = new ArrayList();

			/**
			 * run
			 */
			public void run()
			{
				IViewReference[] views = null;

				try
				{
					IWorkbench w = PlatformUI.getWorkbench();
					IWorkbenchWindow ww = w.getActiveWorkbenchWindow();

					if (ww != null)
					{
						IWorkbenchPage wp = ww.getActivePage();

						if (wp != null)
						{
							views = wp.getViewReferences();

							for (int i = 0; i < views.length; i++)
							{
								if (id.equals(views[i].getId()))
								{
									if (secondaryId != null)
									{
										if (secondaryId.equals(views[i].getSecondaryId()))
										{
											targetView.add(views[i].getPart(false));
										}
									}
									else
									{
										targetView.add(views[i].getPart(false));
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					System.err.println(e.toString());
					return;
				}
			}
		}

		ViewGetterThread getter = new ViewGetterThread();
		Display display = Display.getDefault();
		display.syncExec(getter);

		IWorkbenchPart[] parts = (IWorkbenchPart[]) getter.targetView.toArray(new IWorkbenchPart[0]);
		return parts;
	}

	/**
	 * Creates a new JavaFileEditorInput
	 * 
	 * @param file
	 * @return IEditorInput
	 */
	public static IEditorInput createJavaFileEditorInput(File file)
	{
		IEditorInput input = null;
		try
		{
			IFileSystem fs = EFS.getLocalFileSystem();
			IFileStore localFile = fs.fromLocalFile(file);
			input = new FileStoreEditorInput(localFile);
		}
		catch (Exception e)
		{
			System.err.println("UnableToCreateJavaFileEditorInput: " + e); //$NON-NLS-1$
		}
		return input;
	}

}
