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
package com.aptana.ide.editors.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.parsing.IParseState;

/**
 * A centralized manager of all FileService usage. All classes that need a file context should use this class and not
 * cache a local copy.
 * 
 * @author Paul Colton
 * @author Robin Debreuil
 */
public final class FileContextManager
{
	static final FileContextManager INSTANCE = new FileContextManager();

	private Map<String, FileService> _fileServices;

	/**
	 * The file index of all files not in an active profile, and not the current file
	 */
	public static final int DEFAULT_FILE_INDEX = -1;

	/**
	 * The file index of the currently active file
	 */
	public static final int CURRENT_FILE_INDEX = Integer.MAX_VALUE;

	/**
	 * The file index for any "built-in" files (i.e. JS Core, HTML DOM, etc.)
	 */
	public static final int BUILT_IN_FILE_INDEX = Integer.MIN_VALUE;

	/**
	 * FileContextManager constructor
	 */
	private FileContextManager()
	{
		this._fileServices = new HashMap<String, FileService>();
	}

	/**
	 * getFileServiceManager
	 * 
	 * @return FileContextManager
	 */
	public static FileContextManager getFileServiceManager()
	{
		return INSTANCE;
	}

	/**
	 * get
	 * 
	 * @param input
	 * @return FileService
	 */
	public static FileService get(IEditorInput input)
	{
		return get(CoreUIUtils.getURI(input));

	}

	/**
	 * get
	 * 
	 * @param fileURI
	 * @return FileService
	 */
	public static FileService get(String fileURI)
	{
		fileURI = CoreUIUtils.getURI(fileURI);
		
		return INSTANCE._fileServices.get(fileURI);
	}

	/**
	 * get
	 * 
	 * @param file
	 * @return FileService
	 */
	public static FileService get(File file)
	{
		String fileURI = CoreUIUtils.getURI(file);
		
		return INSTANCE._fileServices.get(fileURI);
	}

	/**
	 * add
	 * 
	 * @param file
	 * @param context
	 */
	public static void add(File file, FileService context)
	{
		add(CoreUIUtils.getURI(file), context);
	}

	/**
	 * add
	 * 
	 * @param path
	 * @param context
	 */
	public static void add(String path, FileService context)
	{
		path = CoreUIUtils.getURI(path);

		Trace.info(StringUtils.format(Messages.FileContextManager_AddingFileContext, new String[] { path,
				context.toString() }));

		if (INSTANCE._fileServices.containsKey(path))
		{
			FileService fs = get(path);
			
			if (fs == context)
			{
				return;
			}

			IFileLanguageService ls = fs.getLanguageService(fs.getDefaultLanguage());
			
			ls.reset(false);
			Trace.info(StringUtils.format(Messages.FileContextManager_ResettingFileContext, new String[] { path,
					context.toString() }));
		}

		synchronized (INSTANCE._fileServices)
		{
			INSTANCE._fileServices.put(path, context);
		}
	}

	/**
	 * disconnectSourceProvider
	 * 
	 * @param fileURI
	 * @param sourceProvider
	 */
	public static void disconnectSourceProvider(String fileURI, IFileSourceProvider sourceProvider)
	{
		fileURI = CoreUIUtils.getURI(fileURI);

		FileService context = INSTANCE._fileServices.get(fileURI);

		if (context != null)
		{
			context.disconnectSourceProvider(sourceProvider);
		}
	}

	/**
	 * connectSourceProvider
	 * 
	 * @param fileURI
	 * @param sourceProvider
	 */
	public static void connectSourceProvider(String fileURI, DocumentSourceProvider sourceProvider)
	{
		fileURI = CoreUIUtils.getURI(fileURI);

		FileService context = INSTANCE._fileServices.get(fileURI);

		if (context != null)
		{
			context.connectSourceProvider(sourceProvider);
		}
	}

	/**
	 * remove Remove the FileService object, but also set the FileIndex to -1 beforehand
	 * 
	 * @param path
	 */
	public static void remove(IPath path)
	{
		remove(path.toOSString());
	}

	/**
	 * remove
	 * 
	 * @param path
	 */
	public static void remove(String path)
	{
		path = CoreUIUtils.getURI(path);

		if (INSTANCE._fileServices.containsKey(path))
		{
			Trace.info(StringUtils.format(Messages.FileContextManager_RemovingFileContext, path));

			FileService fs = get(path);
			IParseState parseState = fs.getParseState();

			if (parseState != null)
			{
				parseState.unloadFromEnvironment();
				parseState.setFileIndex(FileContextManager.DEFAULT_FILE_INDEX);
			}

			synchronized (INSTANCE._fileServices)
			{
				INSTANCE._fileServices.remove(path);
			}
		}
	}

	/**
	 * getKeySet
	 * 
	 * @return Collection
	 */
	public static String[] getKeySet()
	{
		synchronized (INSTANCE._fileServices)
		{
			return INSTANCE._fileServices.keySet().toArray(new String[0]);
		}
	}

	/**
	 * clearAll
	 */
	public static void clearAll()
	{
		String[] keys = getKeySet();

		for (int i = 0; i < keys.length; i++)
		{
			String path = keys[i];
			
			remove(path);
		}
	}

	/**
	 * getURIFromFileIndex
	 * 
	 * @param index
	 * @return String
	 */
	public static String getURIFromFileIndex(int index)
	{
		String[] keys = getKeySet();

		for (int i = 0; i < keys.length; i++)
		{
			String path = keys[i];

			FileService c = get(path);

			if (c == null)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
						StringUtils.format(Messages.FileContextManager_ERR_FileServiceNullForPathWithIndex,
								new Object[] { path, index}));
			}
			else
			{
				IParseState state = c.getParseState();
				
				if (state == null)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							StringUtils.format(Messages.FileContextManager_ERR_ParseStateNullForPathWithIndex,
									new Object[] { path, index}));
				}
				else if (state.getFileIndex() == index)
				{
					return path;
				}
			}

		}

		return StringUtils.EMPTY;
	}
}
