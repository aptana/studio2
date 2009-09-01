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
package com.aptana.ide.scripting.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * Simple File wrapper.
 * 
 * @author Kevin Lindsey
 */
public class File extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 622033917776635991L;
	private java.io.File file;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of File From Norris Boyd's sample: When Context.defineClass is called with this class, it
	 * will construct File.prototype using this constructor.
	 */
	public File()
	{
	}

	/*
	 * Methods
	 */

	/**
	 * finishInit
	 * 
	 * @param scope
	 * @param constructor
	 * @param prototype
	 */
	public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype)
	{
		constructor.defineProperty("separator", java.io.File.separator, READONLY | PERMANENT); //$NON-NLS-1$
	}

	/**
	 * Returns the name of this JavaScript class, "File".
	 * 
	 * @return String
	 */
	public String getClassName()
	{
		return "File"; //$NON-NLS-1$
	}

	/**
	 * Construct a new JS File object
	 * 
	 * @param cx
	 * @param args
	 * @param ctorObj
	 * @param inNewExpr
	 * @return Scriptable
	 */
	public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr)
	{
		File result = new File();
		Object arg = args[0];

		if (arg instanceof java.io.File)
		{
			// we use this to create File instances internally directly from java.io.File instances
			// this probably won't ever be used from script
			result.file = (java.io.File) arg;
		}
		else
		{
			String name = Context.toString(arg);

			result.file = new java.io.File(name);
		}

		return result;
	}

	/**
	 * Create a new text file for this File
	 * 
	 * @return boolean
	 */
	public boolean jsFunction_createNewFile()
	{
		boolean result = false;

		try
		{
			result = this.file.createNewFile();
		}
		catch (IOException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.File_Error, e);
		}

		return result;
	}

	/**
	 * Create a new directory for this File
	 * 
	 * @param recursive
	 * @return boolean
	 */
	public boolean jsFunction_createDirectory(boolean recursive)
	{
		boolean result = false;

		if (recursive)
		{
			result = this.file.mkdirs();
		}
		else
		{
			result = this.file.mkdir();
		}

		return result;
	}

	/**
	 * jsFunction_deleteOnExit
	 */
	public void jsFunction_deleteOnExit()
	{
		if (this.file != null)
		{
			this.file.deleteOnExit();
		}
	}

	/**
	 * readLines
	 * 
	 * @return A string array of all lines in the file
	 */
	public Scriptable jsFunction_readLines()
	{
		Context cx = Context.getCurrentContext();
		ArrayList lines = new ArrayList();
		BufferedReader br = null;

		try
		{
			FileReader fr = new FileReader(this.file);

			br = new BufferedReader(fr);

			String line = br.readLine();

			while (line != null)
			{
				lines.add(line);
				line = br.readLine();
			}
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.File_Error, e);
		}
		catch (IOException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.File_Error, e);
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.File_Error, e);
				}
			}
		}

		return cx.newArray(this.getParentScope(), lines.toArray());
	}

	/**
	 * write
	 * 
	 * @param text
	 * @return boolean
	 */
	public boolean jsFunction_write(String text)
	{
		try
		{
			FileWriter fw = new FileWriter(this.file);
			fw.write(text);
			fw.close();
		}
		catch (IOException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.File_Error, e);
			return false;
		}

		return true;
	}

	/**
	 * @return String
	 */
	public String jsGet_absolutePath()
	{
		return this.file.getAbsolutePath();
	}

	/**
	 * @return String
	 */
	public String jsGet_baseName()
	{
		String name = this.file.getName();
		int index = name.lastIndexOf('.');
		String result = name;

		if (index != -1)
		{
			result = name.substring(0, index);
		}

		return result;
	}

	/**
	 * @return boolean
	 */
	public boolean jsGet_canRead()
	{
		return this.file.canRead();
	}

	/**
	 * @return boolean
	 */
	public boolean jsGet_canWrite()
	{
		return this.file.canWrite();
	}

	/**
	 * @return boolean
	 */
	public boolean jsGet_exists()
	{
		return this.file.exists();
	}

	/**
	 * @return String
	 */
	public String jsGet_extension()
	{
		String name = this.file.getName();
		int index = name.lastIndexOf('.');
		String result = StringUtils.EMPTY;

		if (index != -1)
		{
			result = name.substring(index);
		}

		return result;
	}

	/**
	 * @return boolean
	 */
	public boolean jsGet_isFile()
	{
		return this.file.isFile();
	}

	/**
	 * @return boolean
	 */
	public boolean jsGet_isDirectory()
	{
		return this.file.isDirectory();
	}

	/**
	 * @return Scriptable
	 */
	public Scriptable jsGet_list()
	{
		Context cx = Context.getCurrentContext();
		Scriptable scope = this.getParentScope();
		String baseName = this.file.getAbsolutePath() + java.io.File.separator;
		String[] names = this.file.list();
		int length = names.length;
		Object[] result = new Object[length];

		for (int i = 0; i < length; i++)
		{
			String name = baseName + names[i];
			result[i] = cx.newObject(scope, "File", new Object[] { name }); //$NON-NLS-1$
		}

		return cx.newArray(scope, result);
	}

	/**
	 * Get the name of the file.
	 * 
	 * @return String
	 */
	public String jsGet_name()
	{
		return this.file.getName();
	}

	/**
	 * Get the parent directory for this file
	 * 
	 * @return Returns a new File object or undefined
	 */
	public Scriptable jsGet_parentFile()
	{
		Context cx = Context.getCurrentContext();
		Scriptable scope = this.getParentScope();
		String parent = this.file.getParent();

		return cx.newObject(scope, "File", new Object[] { parent }); //$NON-NLS-1$
	}

	// /**
	// * Get the file system independent character that separates directories
	// *
	// * @return String
	// */
	// public static String jsStaticFunction_getSeparator()
	// {
	// return java.io.File.separator;
	// }
}
