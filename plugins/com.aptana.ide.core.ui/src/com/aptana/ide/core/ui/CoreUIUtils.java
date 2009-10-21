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
package com.aptana.ide.core.ui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.internal.boot.PlatformURLHandler;
import org.eclipse.core.internal.registry.IRegistryConstants;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchEncoding;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.IInstallConfiguration;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.core.VersionedIdentifier;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.EclipseUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.preferences.ApplicationPreferences;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * Functions useful for retrieving interesting information for plug-ins and the environment, but tailored to UI-related
 * functions
 * 
 * @author Ingo Muschenetz
 */
@SuppressWarnings("restriction")
public final class CoreUIUtils
{

	private static final String FILE_COLON = "file:"; //$NON-NLS-1$
	private static final String FILE_SLASH = FILE_COLON + "/"; //$NON-NLS-1$
	private static final String FILE_SLASH_SLASH = FILE_SLASH + "/"; //$NON-NLS-1$
	private static final String FILE_SLASH_SLASH_SLASH =  FILE_SLASH_SLASH + "/"; //$NON-NLS-1$

	/**
	 * New version of boolean for running on mac os x, uses Platform check
	 */
	public static boolean onMacOSX = Platform.OS_MACOSX.equals(Platform.getOS());

	/**
	 * Use Platform.getOS to test again the various *nix platforms
	 */
	public static boolean onNix = Platform.OS_LINUX.equals(Platform.getOS())
			|| Platform.OS_SOLARIS.equals(Platform.getOS()) || Platform.OS_AIX.equals(Platform.getOS())
			|| Platform.OS_HPUX.equals(Platform.getOS()) || Platform.OS_QNX.equals(Platform.getOS());

	/**
	 * Uses Platform.getOS to test if on OS_WIN32
	 */
	public static boolean onWindows = Platform.OS_WIN32.equals(Platform.getOS());

	/**
	 * Uses Platform.getOS to test if on OS_LINUX
	 */
	public static boolean onLinux = Platform.OS_LINUX.equals(Platform.getOS());

	/**
	 * runningOnWindows
	 */
	public static boolean runningOnWindows = System.getProperty("os.name").startsWith("Win"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * 
     */
    public static boolean onCocoa = false;

	/**
	 * Are we in Eclipse 3.4 or higher?
	 */
	public static boolean inEclipse34orHigher = false;
    /**
     * Are we in Eclipse 3.5 or higher?
     */
	public static boolean inEclipse35orHigher = false;

	/**
	 * Separates URL segments
	 */
	public static String URL_SEPARATOR = "/"; //$NON-NLS-1$

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
					int minorVersion = Integer.parseInt(parts[1]);

					if (minorVersion > 3)
					{
						inEclipse34orHigher = true;
					}
					if (minorVersion > 4)
					{
					    inEclipse35orHigher = true;
					    // only available in Eclipse 3.5
					    onCocoa = Platform.WS_COCOA.equals(System.getProperty(IRegistryConstants.PROP_WS));
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
							Messages.CoreUIUtils_UnableToParseEclipseVersion, version), e);
				}
			}
		}
	}

	/**
	 * Private constructor for utility class
	 */
	private CoreUIUtils()
	{
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image descriptor registry. If the image
	 * descriptor cannot be retrieved, attempt to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param pluginId
	 *            the Id of the plug-in to grab the image from
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor associated with resource or the default "missing" image descriptor if one could not
	 *         be found
	 */
	public static ImageDescriptor getImageDescriptor(String pluginId, String imageFilePath)
	{
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, imageFilePath);

		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * getActivePage
	 * 
	 * @return IWorkbenchPage
	 */
	public static IWorkbenchPage getActivePage()
	{
		return CoreUIPlugin.getActivePage();
	}

	/**
	 * Gets the workspace root from the resources plugin
	 * 
	 * @return - workspace root
	 */
	public static IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Gets the display for the workbench
	 * 
	 * @return - display
	 */
	public static Display getDisplay()
	{
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * Gets the active shell for the workbench
	 * 
	 * @return - shell
	 */
	public static Shell getActiveShell()
	{
		Shell shell = getDisplay().getActiveShell();
		if (shell == null) {
		    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		    if (window != null) {
		        shell = window.getShell();
		    }
		}
		return shell;
	}

	/**
	 * Puts the respective dialog in the center of the screen
	 * 
	 * @param parent
	 * @param shell
	 */
	public static void placeDialogInScreenCenter(Shell parent, Shell shell)
	{
		Rectangle parentSize = parent.getBounds();
		Rectangle mySize = shell.getBounds();

		int locationX, locationY;
		locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
		locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;

		shell.setLocation(new Point(locationX, locationY));
	}

	/**
	 * Retrieves the location of the plug-in
	 * 
	 * @param plugin
	 *            The plug-in to search
	 * @return The string plug-in value
	 */
	public static String getPluginLocation(Plugin plugin)
	{
		File f = getPluginFile(plugin);
		if (f != null)
		{
			return f.getAbsolutePath();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Retrieves the location of the plug-in
	 * 
	 * @param plugin
	 *            The plug-in to search
	 * @return The file plug-in value
	 */
	public static File getPluginFile(Plugin plugin)
	{
		try
		{
			Bundle bunble = plugin.getBundle();

			URL u = FileLocator.find(bunble, new Path(StringUtils.EMPTY), null);

			if (u != null)
			{
				u = FileLocator.toFileURL(u);
				return new File(u.getFile());
			}
		}
		catch (Exception ex)
		{
			IdeLog.logError(plugin, Messages.CoreUIUtils_PluginLocationError, ex);
		}

		return null;
	}

	/**
	 * getWorkspaceDirectory
	 * 
	 * @return File
	 */
	public static String getWorkspaceDirectory()
	{
		URL url = Platform.getInstanceLocation().getURL();

		return url.getPath();
	}

	/**
	 * Calls IFile.getLocation if it exists and uses an Eclipse internal mechanism if the file is deleted. Look at the
	 * implementation of IFile.getLocation to see why this is necessary. Basically getLocation() returns null if the
	 * enclosing project doesn't exist so this allows the location of a deleted file to be found.
	 * 
	 * @param file
	 * @return - Absolute OS string of file location
	 */
	public static String getStringOfIFileLocation(IFile file)
	{
		String location = null;
		IPath path = getPathOfIFileLocation(file);
		if (path != null)
		{
			location = path.makeAbsolute().toOSString();
		}
		return location;
	}

	/**
	 * @see com.aptana.ide.core.ui.CoreUIUtils#getStringOfIFileLocation(IFile file)
	 * @param file
	 * @return - path of IFile
	 */
	public static IPath getPathOfIFileLocation(IFile file)
	{
		IPath location = null;
		if (file != null)
		{
			if (file.exists() && file.getProject() != null && file.getProject().exists())
			{
				location = file.getLocation();
			}
			else
			{
				location = ((Workspace) ResourcesPlugin.getWorkspace()).getFileSystemManager().locationFor(file);
			}
		}
		return location;
	}

	/**
	 * Returns the current path to the source file from an editor input.
	 * 
	 * @param input
	 *            the editor input
	 * @return the path, or null if not found
	 */
	public static String getPathFromEditorInput(IEditorInput input)
	{
		try
		{
			if (input instanceof FileEditorInput)
			{
				IFile file = ((FileEditorInput) input).getFile();
				return getStringOfIFileLocation(file);
			}
			else if (input instanceof NonExistingFileEditorInput)
			{
				NonExistingFileEditorInput nin = (NonExistingFileEditorInput) input;
				IPath path = nin.getPath(nin);
				String spath = path.toOSString();
				return spath;
			}
			else if (input instanceof IStorageEditorInput)
			{
				IStorageEditorInput sei = (IStorageEditorInput) input;
				try
				{
					return sei.getStorage().getFullPath().toOSString();
				}
				catch (Exception e)
				{
					if (input instanceof IPathEditorInput)
					{
						IPathEditorInput pin = (IPathEditorInput) input;
						return pin.getPath().toOSString();
					}
				}
			}
			else if (input instanceof IPathEditorInput)
			{
				IPathEditorInput pin = (IPathEditorInput) input;
				return pin.getPath().toOSString();
			} else if (input instanceof IURIEditorInput) {
				URI uri = ((IURIEditorInput) input).getURI();
				if ("file".equals(uri.getScheme())) {
					return new File(uri).getAbsolutePath();
				}
			}
		}
		catch (Exception e)
		{
			return null;
		}

		return null;
	}

	/**
	 * Appends the file:// protocol, if none found
	 * 
	 * @param path
	 * @return String
	 */
	public static String appendProtocol(String path)
	{
		if (path.indexOf("://") < 0) //$NON-NLS-1$
		{
			return FILE_SLASH_SLASH + path;
		}
		return path;
	}

	/**
	 * Returns a URI from a file
	 * 
	 * @param file
	 *            the file to pull from
	 * @return the string path to the file
	 */
	public static String getURI(File file)
	{
		return getURI(file, true);
	}

	/**
	 * Returns a URI from a file
	 * 
	 * @param file
	 *            the file to pull from
	 * @param urlEncode
	 *            do we url encode the file name
	 * @return the string path to the file
	 */
	public static String getURI(File file, boolean urlEncode)
	{
		String filePath = null;

		String path = file.getPath();
		if (path.startsWith("file:\\")) //$NON-NLS-1$
		{
			filePath = path.replaceAll("file:\\\\", FILE_SLASH_SLASH); //$NON-NLS-1$
		}
		else if (path.startsWith("http:\\")) //$NON-NLS-1$
		{
			filePath = path.replaceAll("http:\\\\", "http://"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			try
			{
				filePath = file.getCanonicalPath();
			}
			catch (IOException e)
			{
				filePath = file.getAbsolutePath();
			}

			if (filePath.startsWith("\\\\")) //$NON-NLS-1$
			{
				filePath = filePath.substring(2);
			}
			filePath = appendProtocol(filePath);
		}

		filePath = filePath.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

		if (urlEncode)
		{
			filePath = StringUtils.urlEncodeFilename(filePath.toCharArray());
		}

		URI uri;
		try
		{
			if (urlEncode)
			{
				uri = new URI(filePath).normalize();
				return uri.toString();
			}
			else
			{
				return filePath;
			}
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_UnableToURLEncodeFilename + filePath, e);
			return filePath;
		}
	}

	/**
	 * getURI
	 * 
	 * @param path
	 * @return String
	 */
	public static String getURI(IPath path)
	{
		File f = new File(path.toOSString());
		return getURI(f);
	}

	/**
	 * Returns a valid URI from the passed in editor input. This assumed that the editor input represents a file on disk
	 * 
	 * @param input
	 * @return String
	 */
	public static String getURI(IEditorInput input)
	{
		String s = getPathFromEditorInput(input);
		if (s == null)
		{
			try
			{
				Method method = input.getClass().getMethod("getURI"); //$NON-NLS-1$
				return ((URI) method.invoke(input)).toString();
			}
			catch (Exception e)
			{

			}
			return StringUtils.EMPTY;
		}
		return getURI(new File(s));
	}

	/**
	 * Does the current path contain a protocol?
	 * 
	 * @param path
	 * @return String
	 */
	public static boolean isURI(String path)
	{
		if (path != null && path.indexOf("://") >= 0) //$NON-NLS-1$
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns a valid URI from the passed in path. If the current path contains a protocol (i.e. file://), it just
	 * returns the path unchanged
	 * 
	 * @param path
	 * @return String
	 */
	public static String getURI(String path)
	{
		if (path != null && path.indexOf(FILE_SLASH_SLASH) < 0 && path.indexOf(FILE_SLASH) == 0)
		{
			path = StringUtils.replace(path, FILE_SLASH, FILE_SLASH_SLASH_SLASH);
		}

		if (path != null && !isURI(path))
		{
			return getURI(new File(path));
		}
		else
		{
			return path;
		}
	}

	/**
	 * Returns the URI for the current editor (effectively the file path transformed into file://)
	 * 
	 * @param editor
	 * @return String
	 */
	public static String getURI(IEditorPart editor)
	{
		if (editor != null && editor.getEditorInput() != null)
		{
			return getURI(editor.getEditorInput());
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Returns the IEditorPart for the current editor
	 * 
	 * @return IEditorPart
	 */
	public static IEditorPart getActiveEditor()
	{
		if (EclipseUtils.getWorkbenchInstance() == null)
		{
			return null;
		}

		final Display display = PlatformUI.getWorkbench().getDisplay();

		/**
		 * Inner class to hold a "Result" from grabbing the editor
		 * 
		 * @author Ingo Muschenetz
		 */
		class Result
		{
			public IEditorPart value = null;
		}

		final Result res = new Result();

		// check in case this is being run during the IDE close
		if (display.isDisposed() == false)
		{
			// reconnect the open editor to its JSEnvironment (if it supports
			// JS)
			display.syncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						if (display.isDisposed())
						{
							return;
						}

						if (EclipseUtils.getWorkbenchInstance() == null)
						{
							return;
						}

						IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

						if (workbench == null)
						{
							return;
						}

						IWorkbenchPage workbenchPage = workbench.getActivePage();

						if (workbenchPage == null)
						{
							return;
						}

						// only reset active editor
						res.value = workbenchPage.getActiveEditor();
					}
					catch (Exception e)
					{
						IdeLog.logInfo(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_UnableToRetrieveActiveEditor, e);
					}
				}
			});
		}

		return res.value;
	}

	/**
	 * Returns the URI for the current editor (effectively the file path transformed into file://)
	 * 
	 * @return String
	 */
	public static String getActiveEditorURI()
	{
		IEditorPart editor = getActiveEditor();
		if (editor != null && editor.getEditorInput() != null)
		{
			return getURI(editor.getEditorInput());
		}
		else
		{
			return null;
		}
	}

	/**
	 * getOpenEditorPaths
	 * 
	 * @return String[]
	 */
	public static String[] getOpenEditorPaths()
	{
		if (EclipseUtils.getWorkbenchInstance() == null)
		{
			return new String[0];
		}

		Display display = PlatformUI.getWorkbench().getDisplay();

		/**
		 * Inner class to hold a "Result" from grabbing the editor URI
		 * 
		 * @author Ingo Muschenetz
		 */
		class Result
		{
			public String[] value = new String[0];
		}

		final Result res = new Result();

		try
		{
			// check in case this is being run during the IDE close
			if (display.isDisposed() == false)
			{
				// reconnect the open editor to its JSEnvironment (if it
				// supports JS)
				display.syncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							IWorkbench wb = PlatformUI.getWorkbench();
							if (wb == null)
							{
								return;
							}

							IWorkbenchWindow workbench = wb.getActiveWorkbenchWindow();
							if (workbench == null)
							{
								return;
							}

							IWorkbenchPage workbenchPage = workbench.getActivePage();
							if (workbenchPage == null)
							{
								return;
							}

							IEditorReference[] editors = workbenchPage.getEditorReferences();
							if (editors == null)
							{
								return;
							}

							List<String> list = new ArrayList<String>();

							for (int i = 0; i < editors.length; i++)
							{
								IEditorReference ref = editors[i];
								IEditorPart editor = ref.getEditor(false);
								if (editor != null)
								{
									String id = editor.getEditorSite().getId();

									if (id != null && id.startsWith("com.aptana")) //$NON-NLS-1$
									{
										IEditorInput input = editor.getEditorInput();
										if (input != null)
										{
											String uri = CoreUIUtils.getURI(input);
											if (uri != null)
											{
												list.add(uri);
											}
										}
									}
								}
							}

							res.value = list.toArray(new String[list.size()]);

						}
						catch (Exception e)
						{
							IdeLog.logError(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_ERR_InAsyncCallInGetOpenEditorPaths, e);
						}
					}
				});
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_UnableToGetCurrentEditorPaths, e);
		}

		return res.value;
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
			public List<IWorkbenchPart> targetView = new ArrayList<IWorkbenchPart>();

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
					IdeLog.logError(CoreUIPlugin.getDefault(), e.toString());
					return;
				}
			}
		}

		ViewGetterThread getter = new ViewGetterThread();
		Display display = Display.getDefault();
		display.syncExec(getter);

		IWorkbenchPart[] parts = getter.targetView.toArray(new IWorkbenchPart[getter.targetView.size()]);
		return parts;
	}

	/**
	 * Show a view.
	 * Note: The view is displayed with the IWorkbenchPage.VIEW_ACTIVATE modifier and is being retrieved in a UI thread through the Display.
	 * 
	 * @param viewId
	 * @param secondaryId
	 * @return The {@link IViewPart}, or null if failed.
	 */
	public static IViewPart showView(final String viewId, final String secondaryId)
	{
		class OpenViewThread implements Runnable
		{
			public IViewPart[] viewPart = new IViewPart[1];

			/**
			 * run
			 */
			public void run()
			{
				try
				{
					IWorkbench w = PlatformUI.getWorkbench();
					IWorkbenchWindow ww = w.getActiveWorkbenchWindow();

					if (ww != null)
					{
						IWorkbenchPage wp = ww.getActivePage();

						if (wp != null)
						{
							viewPart[0] = wp.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
						}
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), e.toString());
					return;
				}
			}
		}

		OpenViewThread getter = new OpenViewThread();
		Display display = Display.getDefault();
		display.syncExec(getter);

		return getter.viewPart[0];
	}

	/**
     * Opens a specific editor.
     * 
     * @param editorId
     *            the editor ID
     * @param activate
     *            true if the editor should be activated, false otherwise
     * @return
     */
	public static IEditorPart openEditor(String editorId, boolean activate)
	{
	    IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            try {
                return page.openEditor(new EmptyEditorInput(), editorId,
                        activate, IWorkbenchPage.MATCH_ID);
            } catch (PartInitException e) {
            }
        }
        return null;
	}

	/**
	 * Gets the file path from a URI
	 * 
	 * @param sourceURI
	 *            the source URI
	 * @return the URI converted to a path (removed file:// from the beginning)
	 */
	public static String getPathFromURI(String sourceURI)
	{
		String uri = sourceURI;

		if (sourceURI.startsWith(FILE_SLASH_SLASH))
		{
			uri = sourceURI.substring(FILE_SLASH_SLASH.length());
		}
		if (sourceURI.startsWith(FILE_COLON))
		{
			uri = sourceURI.substring(FILE_COLON.length());
		}

		try
		{
			return URLDecoder.decode(uri, "UTF-8"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_EncodingNotSupported, e);
		}

		return uri;
	}

	/**
	 * Gets the user's name from preferences or the system, whichever is defined
	 * 
	 * @return String
	 */
	public static String getUserName()
	{
		try
		{
			String userName = System.getProperty("user.name"); //$NON-NLS-1$
			IPreferenceStore store = CoreUIPlugin.getDefault().getPreferenceStore();
			if (store != null)
			{
				String testName = store
						.getString(com.aptana.ide.core.ui.preferences.IPreferenceConstants.PREF_USER_NAME);
				if (StringUtils.EMPTY.equals(testName) == false)
				{
					userName = testName;
				}
			}

			return userName;
		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.CoreUIUtils_UnableToGetCurrentUserName, e);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Logs an error
	 * 
	 * @param shell
	 * @param plugin
	 * @param title
	 *            the title of the dialog
	 * @param message
	 *            the message to log
	 */
	public static void logAndDialogError(Shell shell, Plugin plugin, String title, String message)
	{
		MessageDialog.openError(shell, title, message);
		IdeLog.logError(plugin, message);
	}

	/**
	 * Logs an error
	 * 
	 * @param shell
	 * @param plugin
	 * @param title
	 *            the title of the dialog
	 * @param message
	 *            the message to log
	 * @param th
	 */
	public static void logAndDialogError(Shell shell, Plugin plugin, String title, String message, Throwable th)
	{
		MessageDialog.openError(shell, title, message);
		IdeLog.logError(plugin, message, th);
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
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.CoreUIUtils_UnableToCreateJavaFileEditorInput, e);
		}
		return input;
	}

	/**
	 * Creates a new NonExistingFileEditorInput
	 * 
	 * @param file
	 * @param fileName
	 * @return IEditorInput
	 */
	public static IEditorInput createNonExistingFileEditorInput(File file, String fileName)
	{
		IEditorInput input = null;
		try
		{
			IFileSystem fs = EFS.getLocalFileSystem();
			IFileStore localFile = fs.fromLocalFile(file);
			input = new NonExistingFileEditorInput(localFile, fileName);
		}
		catch (Exception e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(),
					Messages.CoreUIUtils_UnableToCreateNonExistingFileEditorInput, e);
		}
		return input;
	}

	/**
	 * joinURI
	 * 
	 * @param url
	 * @param uriSuffix
	 * @return String
	 */
	public static String joinURI(URL url, String uriSuffix)
	{
		return joinURI(url.toString(), uriSuffix);
	}

	/**
	 * joinURI
	 * 
	 * @param uriPrefix
	 * @param uriSuffix
	 * @return String
	 */
	public static String joinURI(String uriPrefix, String uriSuffix)
	{
		if (uriPrefix == null || StringUtils.EMPTY.equals(uriPrefix))
		{
			return uriPrefix;
		}

		if (!uriPrefix.endsWith("/")) //$NON-NLS-1$
		{
			uriPrefix += "/"; //$NON-NLS-1$
		}

		return uriPrefix + uriSuffix;
	}

	/**
	 * Trims the specified # of segments from the end of the URL
	 * 
	 * @param url
	 * @param segments
	 * @return URL
	 */
	public static URL trimURLSegments(URL url, int segments)
	{
		String urlString = url.toExternalForm();

		String[] segment = splitUrlPath(urlString);
		if (segments >= segment.length)
		{
			return null;
		}

		String newUrl = StringUtils.EMPTY;
		for (int i = 0; i < segment.length - segments; i++)
		{
			String string = segment[i];
			newUrl += string + URL_SEPARATOR;
		}

		try
		{
			return new URL(newUrl);
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}

	/**
	 * Splits the URL into segments
	 * 
	 * @param urlPath
	 * @return an array of strings
	 */
	public static String[] splitUrlPath(String urlPath)
	{
		String[] segments = urlPath.split(URL_SEPARATOR);
		return segments;
	}

	/**
	 * Joins the URL from segments
	 * 
	 * @param segments
	 * @return an joined array
	 */
	public static String joinUrlPath(String[] segments)
	{
		return StringUtils.join(URL_SEPARATOR, segments);
	}

	/**
	 * registerFileExtension
	 * 
	 * @param editorID
	 * @param filename
	 *            The name of the file to associate with the given editor ID. Use * if any file for the given extension
	 *            will work
	 * @param extension
	 *            The file extensions without the leading '.' to associate with the given editor ID
	 */
	public static void registerFileExtension(final String editorID, final String filename, final String extension)
	{
		UIJob job = new UIJob("register file extension") //$NON-NLS-1$
		{
			/**
			 * runInUIThread
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				EditorRegistry registry = (EditorRegistry) WorkbenchPlugin.getDefault().getEditorRegistry();
				IFileEditorMapping[] mappings = registry.getFileEditorMappings();
				FileEditorMapping editorMapping = new FileEditorMapping(filename, extension);

				editorMapping.setDefaultEditor((EditorDescriptor) registry.findEditor(editorID));

				FileEditorMapping foundMapping = null;

				for (int i = 0; i < mappings.length; i++)
				{
					if (mappings[i].getName().equals(editorMapping.getName())
							&& mappings[i].getExtension().equals(editorMapping.getExtension()))
					{
						foundMapping = (FileEditorMapping) mappings[i];
						break;
					}
				}

				if (foundMapping != null)
				{
					foundMapping.setDefaultEditor((EditorDescriptor) registry.findEditor(editorID));
					registry.setFileEditorMappings((FileEditorMapping[]) mappings);
				}
				else
				{
					List<IFileEditorMapping> mappingsList = new ArrayList<IFileEditorMapping>();

					Collections.addAll(mappingsList, mappings);
					mappingsList.add(editorMapping);
					registry.setFileEditorMappings(mappingsList.toArray(new FileEditorMapping[mappingsList.size()]));
				}

				registry.saveAssociations();

				PrefUtil.savePrefs();

				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Converts a URI with a bundle name in it to a entry ID.
	 * 
	 * @param url
	 * @return URI
	 */
	public static URI replaceBundleNameWithId(URI url)
	{
		if (!url.getScheme().startsWith(PlatformURLHandler.BUNDLE))
		{
			return url;
		}
		else
		{
			String bundleName = url.getAuthority();
			if (bundleName == null)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.CoreUIUtils_ERR_UnableToFindBundleName, url
						.toString()));
				return url;
			}
			Bundle b = Platform.getBundle(bundleName);
			if (b != null)
			{
				bundleName = String.valueOf(b.getBundleId());
				try
				{
					return new URI(url.getScheme(), bundleName, url.getPath(), null, null);
				}
				catch (URISyntaxException e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
							Messages.CoreUIUtils_ERR_UnableToSwitchOutBundleIdForURL, url.toString()), e);
					return url;
				}
			}
			return url;
		}
	}

	/**
	 * Converts a URI with a bundle name in it to a entry ID.
	 * 
	 * @param url
	 * @return URI
	 */
	public static URL getBundlePathAsFile(URI url)
	{
		if (!url.getScheme().startsWith(PlatformURLHandler.BUNDLE))
		{
			return null;
		}
		else
		{
			String bundleName = url.getAuthority();
			if (bundleName == null)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.CoreUIUtils_ERR_UnableToFindBundleName, url
						.toString()));
				return null;
			}
			Bundle b = Platform.getBundle(bundleName);
			if (b != null)
			{
				URL fileUrl = FileLocator.find(b, new Path(url.getPath()), null);
				if (fileUrl != null)
				{
					try
					{
						URL localUrl = FileLocator.toFileURL(fileUrl);
						return localUrl;
					}
					catch (IOException e)
					{
						return null;
					}
				}
				else
				{
					return null;
				}
			}
			return null;
		}
	}

	/**
	 * Returns the URL as a local URL
	 * 
	 * @param b
	 * @param fullPath
	 * @return the resolved url
	 */
	public static URL getResolvedURL(Bundle b, String fullPath)
	{
		URL url = FileLocator.find(b, new Path(fullPath), null);

		if (url != null)
		{
			try
			{

				URL localUrl = FileLocator.toFileURL(url);
				if (localUrl != null)
				{
					return localUrl;
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Returns a file handle to the folder links to osgi.configuration.area.
	 * 
	 * @return A reference to the configuration directory on disk
	 */
	public static File getConfigurationDirectory()
	{
//		String homeDir = System.getProperty("osgi.configuration.area"); //$NON-NLS-1$
//		URL fileURL = FileUtils.uriToURL(homeDir);
//		File f = FileUtils.urlToFile(fileURL);
//		f.mkdirs();
//		return f;
		return ApplicationPreferences.getConfigurationDirectory();
	}

	/**
	 * @param stringUrl
	 * @return string of resolved url
	 */
	public static String resolveBundleUrl(String stringUrl)
	{

		if (stringUrl == null)
		{
			return null;
		}

		URL url;
		try
		{
			// if we have a bundle symbolic name, we need to strip that out
			url = replaceBundleNameWithId(new URI(stringUrl)).toURL();
			// Now find the path locally on disk
			URL resolvedURL = CoreUIUtils.getResolvedURL(null, url.toExternalForm());
			return resolvedURL.toExternalForm();
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.CoreUIUtils_ERR_UnableToResolveURL, stringUrl), e);
			return null;
		}
		catch (IOException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(Messages.CoreUIUtils_ERR_UnableToResolveURL, stringUrl), e);
			return null;
		}
	}

	/**
	 * @param message
	 * @param e
	 */
	public static void showError(String message, Exception e)
	{
		showError(message, e, true);
	}

	/**
	 * @param message
	 * @param e
	 * @param log
	 */
	public static void showError(final String message, final Exception e, final boolean log)
	{
		UIJob errorJob = new UIJob(Messages.CoreUIUtils_MSG_ShowingError)
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.CoreUIUtils_MSG_Error, message);
				if (log)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), message, e);
				}
				return Status.OK_STATUS;
			}

		};
		errorJob.schedule();
	}

	/**
	 * @param message
	 */
	public static void showMessage(final String message)
	{
		UIJob messageJob = new UIJob(Messages.CoreUIUtils_MSG_ShowingMessage)
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), Messages.CoreUIUtils_MSG_Information, message);
				return Status.OK_STATUS;
			}

		};
		messageJob.schedule();
	}

	/**
	 * Opens a browser url with added info
	 * 
	 * @param browserUrl -
	 *            base url that will be directly appened with ? plus info
	 * @param startWithQuestionMark -
	 *            true to append a ?, false to start appending with &
	 * @param from -
	 *            location triggering
	 */
	public static void openBrowserURLWithInfo(String browserUrl, boolean startWithQuestionMark, String from)
	{
		String product = StringUtils.replaceNullWithEmpty(System.getProperty("eclipse.product")); //$NON-NLS-1$
		if (product.length() == 0)
		{
			String[] args = Platform.getCommandLineArgs();
			if (args != null)
			{
				for (int i = 0; i < args.length; i++)
				{
					if ("-product".equals(args[i]) && i + 1 < args.length) //$NON-NLS-1$
					{
						product = args[i + 1];
						break;
					}
				}
			}
		}
		if (startWithQuestionMark)
		{
			browserUrl += "?"; //$NON-NLS-1$
		}
		else
		{
			browserUrl += "&"; //$NON-NLS-1$
		}
		openBrowserURL(browserUrl + "from=" + from + "&product=" + product); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Opens a browser url with all the added info
	 * 
	 * @param browserUrl -
	 *            base url that will be directly appened with ? plus ALL info
	 * @param startWithQuestionMark -
	 *            true to append a ?, false to start appending with &
	 * @param from -
	 *            location triggering
	 */
	public static void openBrowserURLWithAllInfo(String browserUrl, boolean startWithQuestionMark, String from)
	{
		String key = ApplicationPreferences.getInstance().getString(IPreferenceConstants.ACTIVATION_KEY);
		if (key == null)
		{
			key = StringUtils.EMPTY;
		}
		if (key.length() > 20)
		{
			key = key.substring(0, 20);
		}
		String idAddition;
		if (startWithQuestionMark)
		{
			idAddition = "?id="; //$NON-NLS-1$
		}
		else
		{
			idAddition = "&id="; //$NON-NLS-1$
		}
		openBrowserURLWithInfo(browserUrl + idAddition + "&lic=" + key, false, from); //$NON-NLS-1$
	}

	/**
	 * Opens the url in an external browser according to the browser returned from
	 * IWorkbenchBrowserSupport.getExternalBrowser(). Will not throw exceptions that occur during the opening
	 * 
	 * @param browserUrl -
	 *            url to open
	 */
	public static void openBrowserURL(String browserUrl)
	{
		try
		{
			URL url = new URL(browserUrl);
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			support.getExternalBrowser().openURL(url);
		}
		catch (PartInitException e1)
		{
			// Do nothing
		}
		catch (MalformedURLException e1)
		{
			// Do nothing
		}
	}

	/**
	 * Opens a view with the given ID
	 * 
	 * @param viewID
	 * @return - opened view part
	 * @throws PartInitException
	 */
	public static IViewPart showView(String viewID) throws PartInitException
	{
		IViewPart view = null;
		IWorkbenchWindow window = CoreUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				view = page.showView(viewID);
			}
		}
		return view;
	}

	/**
	 * Finds a view with the given ID
	 * 
	 * @param viewID
	 * @return - opened view part
	 * @throws PartInitException
	 */
	public static IViewPart findView(String viewID) throws PartInitException
	{
		IViewPart view = null;
		IWorkbenchWindow window = CoreUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				view = page.findView(viewID);
			}
		}
		return view;
	}

	/**
	 * Returns true if a feature with this id is enabled
	 * 
	 * @param featureId
	 * @return true if enabled, false otherwise
	 * @deprecated Use the FeatureUtil or other APIs from update plugin
	 */
	public static boolean isFeatureEnabled(String featureId)
	{
		boolean enabled = false;
		try
		{
			ILocalSite localSite = SiteManager.getLocalSite();
			IInstallConfiguration config = localSite.getCurrentConfiguration();
			IConfiguredSite[] sites = config.getConfiguredSites();

			for (int i = 0; i < sites.length && !enabled; i++)
			{
				IFeatureReference[] refs = sites[i].getFeatureReferences();
				for (int j = 0; j < refs.length && !enabled; j++)
				{
					IFeatureReference ref = refs[j];
					IFeature feature = ref.getFeature(null);
					VersionedIdentifier ident = ref.getVersionedIdentifier();
					IConfiguredSite site = ref.getSite().getCurrentConfiguredSite();
					if (ident != null && ident.getIdentifier().equals(featureId) && site.isConfigured(feature))
					{
						enabled = true;
					}
				}
			}
		}
		catch (Exception e)
		{
			enabled = false;
		}
		return enabled;
	}

    /**
     * Returns the defined encoding for the given file.
     * 
     * <pre>
     * The search for the encoding is done in this order:
     * 1. Check the encoding that is set specifically to the IFile.
     * 2. Check the workspace default charset.
     * 3. If all the above fails, get ResourcesPlugin.getEncoding(), which actually gets the encoding from the system.
     * </pre>
     * 
     * @param file an {@link IFile}
     * @return The file's encoding
     */
    public static String getFileEncoding(IFile file)
    {
        String charset = null;
        try
        {
            if (file != null)
            {
                String fileCharset = file.getCharset(true);
                if (fileCharset != null)
                {
                    charset = fileCharset;
                }
            }
        }
        catch (Throwable e)
        {
            // If there is any error, return the default
        }
        if (charset == null)
        {
            try
            {
                IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                charset = workspaceRoot.getDefaultCharset();
            }
            catch (CoreException ce)
            {
                charset = WorkbenchEncoding.getWorkbenchDefaultEncoding();
            }
        }
        if (charset == null)
        {
            // Use the system's encoding
            charset = ResourcesPlugin.getEncoding();
        }
        return charset;
    }

    /**
     * Returns the defined encoding for the given container.
     * 
     * <pre>
     * The search for the encoding is done in this order:
     * 1. Check the encoding that is set specifically to the IContainer.
     * 2. Check the workspace default charset.
     * 3. If all the above fails, get ResourcesPlugin.getEncoding(), which actually gets the encoding from the system.
     * </pre>
     * 
     * @param container an {@link IContainer}
     * @return The container's encoding
     */
    public static String getContainerEncoding(IContainer container)
    {
        String charset = null;
        try
        {
            if (container != null)
            {
                String containerCharset = container.getDefaultCharset();
                if (containerCharset != null)
                {
                    charset = containerCharset;
                }
            }
        }
        catch (Throwable e)
        {
            // If there is any error, return the default
        }
        if (charset == null)
        {
            try
            {
                IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                charset = workspaceRoot.getDefaultCharset();
            }
            catch (CoreException ce)
            {
                charset = WorkbenchEncoding.getWorkbenchDefaultEncoding();
            }
        }
        if (charset == null)
        {
            // Use the system's encoding
            charset = ResourcesPlugin.getEncoding();
        }
        return charset;
    }

}
