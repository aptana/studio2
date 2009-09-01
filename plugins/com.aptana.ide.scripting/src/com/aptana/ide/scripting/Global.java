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
package com.aptana.ide.scripting;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.views.actions.ActionsViewEventTypes;
import com.aptana.ide.editors.views.profiles.ProfilesViewEventTypes;
import com.aptana.ide.scripting.editors.Editors;
import com.aptana.ide.scripting.editors.Region;
import com.aptana.ide.scripting.io.File;
import com.aptana.ide.scripting.io.PrintStream;
import com.aptana.ide.scripting.io.WebRequest;
import com.aptana.ide.scripting.menus.Menus;
import com.aptana.ide.scripting.parsing.CSSTokenTypes;
import com.aptana.ide.scripting.parsing.HTMLTokenTypes;
import com.aptana.ide.scripting.parsing.JSTokenTypes;
import com.aptana.ide.scripting.parsing.ScriptDocTokenTypes;
import com.aptana.ide.scripting.parsing.TokenCategories;
import com.aptana.ide.scripting.parsing.XMLTokenTypes;
import com.aptana.ide.scripting.views.Views;

/**
 * @author Kevin Lindsey
 */
public class Global extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 1996593475808707552L;
	private static int scriptIndex = 0;

	/**
	 * The property name used to store a libraries unique id
	 */
	public static final String idPropertyName = "__sid__"; //$NON-NLS-1$

	private Menus _menus;
	private Editors _editors;
	private Views _views;
	private PrintStream _err;
	// private PrintStream _out;
	private HashMap _scriptInfo;
	private HashMap _idXref;

	private HashMap _runningSetTimeouts;
	private int _setTimeoutIndex;

	/*
	 * Properties
	 */

	/**
	 * getClassName
	 * 
	 * @return String
	 */
	public String getClassName()
	{
		return "Global"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * GlobalBase
	 * 
	 * @param cx
	 */
	public Global(Context cx)
	{
		this._scriptInfo = new HashMap();
		this._idXref = new HashMap();
		this._runningSetTimeouts = new HashMap();

		// create local instances
		this._menus = new Menus(this);
		this._editors = new Editors(this);
		this._views = new Views(this);

		// wrap output streams
		// CHECKSTYLE:OFF
		this._err = new PrintStream(this, System.err);
		// CHECKSTYLE:ON
		// this._out = new PrintStream(this, System.out);

		// create unsealed standard objects
		cx.initStandardObjects(this, false);

		// make list of functions
		String[] names = {
			"alert", //$NON-NLS-1$
			"clearTimeout", //$NON-NLS-1$
			"confirm", //$NON-NLS-1$
			"execute", //$NON-NLS-1$
			"getPreference", //$NON-NLS-1$
			"getProperty", //$NON-NLS-1$
			"include", //$NON-NLS-1$
			"loadBundle", //$NON-NLS-1$
			"loadLibrary", //$NON-NLS-1$
			"parseXML", //$NON-NLS-1$
			"prompt", //$NON-NLS-1$
			"reloadLibrary", //$NON-NLS-1$
			"runOnUIThread", //$NON-NLS-1$
			"setPreference", //$NON-NLS-1$
			"setTimeout", //$NON-NLS-1$
			"share" //$NON-NLS-1$
		};

		// define functions
		this.defineFunctionProperties(names, Global.class, READONLY | PERMANENT);

		// define properties
		this.defineProperty("global", this, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("editors", this._editors, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("window", this, READONLY | PERMANENT); //$NON-NLS-1$
		// this.defineProperty("document", this, READONLY | PERMANENT);
		this.defineProperty("menus", this._menus, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("views", this._views, READONLY | PERMANENT); //$NON-NLS-1$

		MessageConsoleStream stream = ScriptingEngine.getInstance().getConsoleStream();

		// this.defineProperty("err", stream, READONLY | PERMANENT);
		this.defineProperty("err", _err, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("out", stream, READONLY | PERMANENT); //$NON-NLS-1$

		this.defineProperty("TokenCategories", new TokenCategories(this), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("CSSTokenTypes", new CSSTokenTypes(this), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("HTMLTokenTypes", new HTMLTokenTypes(this), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("JSTokenTypes", new JSTokenTypes(this), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("ScriptDocTokenTypes", new ScriptDocTokenTypes(this), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("XMLTokenTypes", new XMLTokenTypes(this), READONLY | PERMANENT); //$NON-NLS-1$

		this.defineProperty("IPreferenceConstants", new IPreferenceConstants() {}, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("ProfilesViewEventTypes", new ProfilesViewEventTypes(), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("ActionsViewEventTypes", new ActionsViewEventTypes(), READONLY | PERMANENT); //$NON-NLS-1$

		this.defineProperty("SWT", new org.eclipse.swt.SWT(), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("GridData", new GridData(), READONLY | PERMANENT); //$NON-NLS-1$

		try
		{
			ScriptableObject.defineClass(this, File.class);
			ScriptableObject.defineClass(this, WebRequest.class);
			ScriptableObject.defineClass(this, Region.class);
			ScriptableObject.defineClass(this, com.aptana.ide.scripting.swt.Shell.class);
			ScriptableObject.defineClass(this, com.aptana.ide.scripting.swt.Button.class);
			ScriptableObject.defineClass(this, com.aptana.ide.scripting.swt.Text.class);
			ScriptableObject.defineClass(this, com.aptana.ide.scripting.swt.Label.class);
		}
		catch (IllegalAccessException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
		}
		catch (InstantiationException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
		}
		catch (InvocationTargetException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
		}
	}

	/*
	 * Methods
	 */

	/**
	 * Execute a command-line in the system shell
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 *            The string command to execute
	 * @param funObj
	 * @return Returns an object array where the first object is the return code, the second object is the text from
	 *         stdout, and the third object is the text from stderr
	 */
	public static Scriptable execute(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		Scriptable result = null;

		if (args.length > 0)
		{
			String command = args[0].toString();
			String input = StringUtils.EMPTY;

			if (args.length > 1)
			{
				input = args[1].toString();
			}

			Process p = null;
			int retCode = 0;
			String stdout = StringUtils.EMPTY;
			String stderr = StringUtils.EMPTY;

			try
			{
				p = Runtime.getRuntime().exec(command);
				p.getOutputStream().write(input.getBytes());
				p.getOutputStream().flush();
				p.getOutputStream().close();
				retCode = p.waitFor();
				// retCode = p.exitValue();
				stdout = getText(p.getInputStream());
				stderr = getText(p.getErrorStream());

			}
			catch (IOException e)
			{
				if (p != null)
				{
					retCode = p.exitValue();
				}
			}
			catch (InterruptedException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
			}

			// create Object
			Scriptable scope = ScriptableObject.getTopLevelScope(thisObj);

			result = cx.newObject(scope, "Object"); //$NON-NLS-1$

			// set property values
			result.put("code", result, new Integer(retCode)); //$NON-NLS-1$
			result.put("stdout", result, stdout); //$NON-NLS-1$
			result.put("stderr", result, stderr); //$NON-NLS-1$
		}

		// return result
		return result;
	}

	/**
	 * export
	 * 
	 * @param obj
	 */
	public void share(ScriptableObject obj)
	{
		if (obj != null)
		{
			Object[] ids = obj.getIds();

			for (int i = 0; i < ids.length; i++)
			{
				Object id = ids[i];

				if (id instanceof String)
				{
					String propertyName = (String) id;

					this.put(propertyName, this, obj.get(propertyName, obj));
				}
			}
		}
	}
	
	/**
	 * runOnUIThread
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void runOnUIThread(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0 && args[0] instanceof Function)
		{
			Function f = (Function) args[0];
			
			// get display
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();
			
			// execute callback in the correct thread
			display.syncExec(new ScriptThread(f.getParentScope(), f, new Object[0]));
		}
	}

	/**
	 * runOnUIThreadAsync
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void runOnUIThreadAsync(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0 && args[0] instanceof Function)
		{
			Function f = (Function) args[0];

			// get display
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			// execute callback in the correct thread
			display.asyncExec(new ScriptThread(f.getParentScope(), f, new Object[0]));
		}
	}

	/**
	 * parseXML
	 * 
	 * @param xml
	 * @return Document
	 */
	public Document parseXML(String xml)
	{
		Document result = null;

		if (xml != null && xml.length() > 0)
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try
			{
				builder = factory.newDocumentBuilder();
				// parse XML to create our document element
				InputStream in = new ByteArrayInputStream(xml.getBytes());

				result = builder.parse(in);
			}
			catch (ParserConfigurationException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
			}
			catch (SAXException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
			}
			catch (IOException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
			}
		}

		return result;
	}

	/**
	 * Get a string array of all script ids that have been registered
	 * 
	 * @return String[]
	 */
	public String[] getScriptIds()
	{
		Set keySet = _scriptInfo.keySet();

		return (String[]) keySet.toArray(new String[keySet.size()]);
	}

	/**
	 * Get the script info object for the specified script id
	 * 
	 * @param id
	 *            The script id to lookup
	 * @return The ScriptInfo for the specified id
	 */
	public ScriptInfo getScriptInfo(String id)
	{
		ScriptInfo result = null;

		if (this._scriptInfo.containsKey(id))
		{
			result = (ScriptInfo) this._scriptInfo.get(id);
		}

		return result;
	}

	/**
	 * Determine if we have ScriptInfo for the given id
	 * 
	 * @param id
	 *            The id to lookup
	 * @return Returns true if we have a ScriptInfo entry for the given id
	 */
	public boolean hasScriptInfo(String id)
	{
		return this._scriptInfo.containsKey(id);
	}

	/**
	 * Get all text from the given input stream
	 * 
	 * @param stream
	 *            The input stream
	 * @return Returns all text from the input stream
	 * @throws IOException
	 */
	protected static String getText(InputStream stream) throws IOException
	{
		// create output buffer
		StringWriter sw = new StringWriter();

		// read contents into a string buffer
		try
		{
			// get buffered reader
			InputStreamReader isr = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(isr);

			// create temporary buffer
			char[] buf = new char[1024];

			// fill buffer
			int numRead = reader.read(buf);

			// keep reading until the end of the stream
			while (numRead != -1)
			{
				// output temp buffer to output buffer
				sw.write(buf, 0, numRead);

				// fill buffer
				numRead = reader.read(buf);
			}
		}
		finally
		{
			if (stream != null)
			{
				stream.close();
			}
		}

		// return string buffer's content
		return sw.toString();
	}

	/**
	 * Perform any steps required to reset the global scope when reloading the scripting engine
	 */
	public void shutdown()
	{
		this._scriptInfo.clear();
	}

	/**
	 * Popup an alert box with the specified message
	 * 
	 * @param message
	 *            The message to display in an alert dialog
	 */
	public void alert(final String message)
	{
		final Display currentDisplay = Display.getCurrent();

		if (currentDisplay != null)
		{
			currentDisplay.syncExec(new Runnable()
			{
				public void run()
				{
					Shell shell = currentDisplay.getActiveShell();

					if (shell != null)
					{
						MessageDialog.openWarning(shell, "Aptana Studio", message); //$NON-NLS-1$
					}
				}
			});
		}
	}

	/**
	 * Popup an confirm box with the specified message
	 * 
	 * @param message
	 *            The message to display at the confirmation prompt
	 * @return boolean
	 */
	public boolean confirm(final String message)
	{
		/**
		 * inner class for result
		 */
		class Answer
		{
			public boolean result = false;
		}

		// create instance of inner class
		final Answer a = new Answer();

		// get reply from user
		final Display currentDisplay = Display.getCurrent();

		if (currentDisplay != null)
		{
			currentDisplay.syncExec(new Runnable()
			{
				public void run()
				{
					Shell shell = currentDisplay.getActiveShell();

					if (shell != null)
					{
						a.result = MessageDialog.openConfirm(shell, "Aptana Studio", message); //$NON-NLS-1$
					}
				}
			});
		}

		return a.result;
	}

	/**
	 * Popup an prompt box with the specified message
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 *            The message to display in the prompt dialog
	 * @param funObj
	 * @return boolean
	 */
	public static Object prompt(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		final Display currentDisplay = Display.getCurrent();
		String messageArg = Messages.Global_Prompt;
		String defaultValueArg = StringUtils.EMPTY;

		if (args.length > 0)
		{
			messageArg = Context.toString(args[0]);
		}
		if (args.length > 1)
		{
			defaultValueArg = Context.toString(args[1]);
		}

		/**
		 * Answer
		 */
		class Answer
		{
			public Object result = StringUtils.EMPTY;
		}

		final String message = messageArg;
		final String defaultValue = defaultValueArg;
		final Answer a = new Answer();

		if (currentDisplay != null)
		{
			currentDisplay.syncExec(new Runnable()
			{

				public void run()
				{
					Shell shell = currentDisplay.getActiveShell();

					if (shell != null)
					{
						InputDialog dialog = new InputDialog(null, "Aptana Prompt", message, defaultValue, null); //$NON-NLS-1$
						int dialogResult = dialog.open();
						if (dialogResult == Window.OK)
						{
							a.result = dialog.getValue();
						}
						else
						{
							a.result = Context.getUndefinedValue();
						}
					}
				}
			});
		}

		return a.result;
	}

	/**
	 * Get a list of the full paths of all files in the Active Library view
	 * 
	 * @param property
	 *            The system property name to retrieve
	 * @return Returns a string array of full paths
	 */
	public String getProperty(String property)
	{
		String result = null;

		if (property != null && property.length() > 0)
		{
			result = System.getProperty(property, "undefined"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Get preference
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Returns a string array of full paths
	 */
	public static String getPreference(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		String result = StringUtils.EMPTY;

		if (args.length > 0)
		{
			String name = Context.toString(args[0]);
			int pluginIndex = Editors.UNIFIED_EDITOR;
			IPreferenceStore prefStore;

			if (args.length > 1)
			{
				pluginIndex = (int) Context.toNumber(args[1]);
			}

			switch (pluginIndex)
			{
				case Editors.CSS_EDITOR:
					prefStore = CSSPlugin.getDefault().getPreferenceStore();
					break;

				case Editors.HTML_EDITOR:
					prefStore = HTMLPlugin.getDefault().getPreferenceStore();
					break;

				case Editors.JAVASCRIPT_EDITOR:
					prefStore = JSPlugin.getDefault().getPreferenceStore();
					break;

				case Editors.XML_EDITOR:
					prefStore = XMLPlugin.getDefault().getPreferenceStore();
					break;

				case Editors.UNIFIED_EDITOR:
				default:
					prefStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
					break;
			}

			result = prefStore.getString(name);
		}

		return result;
	}

	/**
	 * Set a preference
	 * 
	 * @param name
	 *            The name of the preference to set
	 * @param value
	 *            The new value for the specified preference
	 */
	public void setPreference(String name, String value)
	{
		if (name != null && name.length() > 0 && value != null && value.length() > 0)
		{
			IPreferenceStore prefStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();

			prefStore.setValue(name, value);

			UnifiedEditorsPlugin.getDefault().savePluginPreferences();
		}
	}

	/**
	 * Load a script into a currently executing library
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void include(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0)
		{
			String id = Context.toString(thisObj.get(idPropertyName, thisObj.getParentScope()));
			Global global = (Global) funObj.getParentScope();
			ScriptInfo info = global.getScriptInfo(id);
			String libraryName = Context.toString(args[0]);
			java.io.File library = new java.io.File(libraryName);

			if (library.exists() == false)
			{
				java.io.File parent = info.getFile().getParentFile();
				String name = parent.getAbsolutePath() + java.io.File.separator + libraryName;

				library = new java.io.File(name);
			}

			if (library.exists())
			{
				try
				{
					// grab the script's source
					String source = FileUtilities.getStreamText(new FileInputStream(library));

					// get script where this is executing
					Scriptable scope = info.getScope();

					// compile the script
					Script script = cx.compileString(source, library.getAbsolutePath(), 1, null);

					// exec
					script.exec(cx, scope);
				}
				catch (FileNotFoundException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
				}
			}
			else
			{
				String message = StringUtils.format(Messages.Global_Cannot_Locate_File, new Object[] {info.getFile().getAbsolutePath(), libraryName});
				
				IdeLog.logError(ScriptingPlugin.getDefault(), message);
			}
		}
		else
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Filename_Not_Defined);
		}
	}

	/**
	 * loadBundle
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 */
	public static void loadBundle(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0)
		{
			String id = Context.toString(thisObj.get(idPropertyName, thisObj.getParentScope()));
			Global global = (Global) funObj.getParentScope();
			ScriptInfo info = global.getScriptInfo(id);
			String bundleName = Context.toString(args[0]);
			Bundle bundle = Platform.getBundle(bundleName);

			if (bundle == null)
			{
				throw new RuntimeException(Messages.Global_Bundle_Not_Found + bundleName);
			}
			
			ScriptClassLoader classLoader = info.getClassLoader();
			
			classLoader.addBundle(bundle);
		}
	}

	/**
	 * Load a library into the script engine. Libraries maintain their own scope. As a result, each script's global will
	 * not collide with other library globals. However, each library's global is linked with the ScriptEngine's global.
	 * This reduces duplicate definition of built-in objects like Object, Function, Array, etc. Libraries can access the
	 * ScriptEngine global via the "global" property. Libraries can share data using "global"
	 * 
	 * @param cx
	 *            The script's Context
	 * @param thisObj
	 *            The object or scope where this function was invoked
	 * @param args
	 *            The library path to load
	 * @param funObj
	 * @return Returns the script's id or undefined if the script failed to load
	 */
	public static Object loadLibrary(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		Object result = null;

		if (args.length > 0)
		{
			Global instance = ScriptingEngine.getInstance().getGlobal();
			String libraryPath = Context.toString(args[0]);
			String onLoadFunction = (args.length > 1) ? Context.toString(args[1]) : "onload"; //$NON-NLS-1$

			try
			{
				// grab the script's source
				String source = FileUtilities.getStreamText(new FileInputStream(libraryPath));

				// compile the script
				Script script = cx.compileString(source, libraryPath, 1, null);

				// build script's local global scope
				Scriptable scope = cx.newObject(instance);
				scope.setPrototype(instance);
				scope.setParentScope(null);

				// store script state info
				String id = instance.storeScriptState(libraryPath, script, scope);

				// create local global to script's location
				scope.put("location", scope, libraryPath); //$NON-NLS-1$

				// execute script to load its definitions into its scope
				Context.toBoolean(script.exec(cx, scope));

				// grab the init function used to perform any needed load-time initialization
				Object fObj = scope.get(onLoadFunction, scope);

				// call the init function
				if (fObj instanceof Function)
				{
					Function f = (Function) fObj;
					ScriptInfo info = instance.getScriptInfo(id);
					ClassLoader oldClassLoader = cx.getApplicationClassLoader();
					
					cx.setApplicationClassLoader(info.getClassLoader());

					try
					{
						f.call(cx, scope, scope, new Object[0]);
					}
					catch (Exception e)
					{
						cx.setApplicationClassLoader(oldClassLoader);
					}
				}
				else
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), onLoadFunction + Messages.Global_Function_Does_Not_Exist
							+ libraryPath);
				}

				// set result
				result = scope;
			}
			catch (FileNotFoundException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
			}
		}

		if (result == null)
		{
			result = Context.getUndefinedValue();
		}

		return result;
	}

	/**
	 * reloadLibrary
	 * 
	 * @param filename
	 */
	public void reloadLibrary(String filename)
	{
		if (filename != null && filename.length() > 0)
		{
			String key = this.getXrefId(filename);
			ScriptInfo info = this.getScriptInfo(key);
			Scriptable scope = info.getScope();
			Object onunload = scope.get("onunload", scope); //$NON-NLS-1$

			if (onunload instanceof Function)
			{
				Function unloadFunction = (Function) onunload;

				Context cx = Context.enter();

				try
				{
					unloadFunction.call(cx, scope, scope, new Object[0]);

					this.removeScriptInfo(key);
					this.removeXrefId(filename);

					Global.loadLibrary(cx, this, new Object[] { filename, "onload" }, null); //$NON-NLS-1$
				}
				catch (Exception e1)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e1);
				}

				Context.exit();
			}
		}
	}

	/**
	 * clearTimeout
	 * 
	 * @param timeoutId
	 */
	public void clearTimeout(int timeoutId)
	{
		synchronized (this._runningSetTimeouts)
		{
			Integer id = new Integer(timeoutId);

			if (this._runningSetTimeouts.containsKey(id))
			{
				this._runningSetTimeouts.remove(id);
			}
		}
	}

	/**
	 * setTimeout
	 * 
	 * @param f
	 * @param timeout
	 * @return int
	 */
	public int setTimeout(final Function f, final int timeout)
	{
		final Global self = this;
		final int timeoutIndex = self._setTimeoutIndex++;

		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				boolean active = true;

				try
				{
					// sleep
					Thread.sleep(timeout);

					// check if this was canceled while sleeping
					synchronized (self._runningSetTimeouts)
					{
						active = self._runningSetTimeouts.containsKey(new Integer(timeoutIndex));
					}

					// call the associated function if this is still active
					if (active)
					{
						// get display
						final IWorkbench workbench = PlatformUI.getWorkbench();

						if (workbench == null)
						{
							return;
						}

						Display display = workbench.getDisplay();

						if (display == null || display.isDisposed())
						{
							return;
						}

						// execute callback in the correct thread
						display.asyncExec(new Runnable()
						{
							public void run()
							{
								Scriptable scope = f.getParentScope();
								Context cx = Context.enter();

								f.call(cx, scope, scope, new Object[0]);

								Context.exit();

								self.clearTimeout(timeoutIndex);
							}
						});
					}
				}
				catch (InterruptedException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Global_Error, e);
				}
			}
		}, "Aptana: Script setTimeout"); //$NON-NLS-1$

		// save reference to this thread to indicate that it should run. Note that clearTimeout will
		// remove this entry
		// and if that is down before the thread wakes, then the action in the thread will not
		// execute
		this._runningSetTimeouts.put(new Integer(timeoutIndex), thread);

		thread.setDaemon(true);

		// start the thread
		thread.start();

		// return the timeout index in case the thread wants to abort this timeout
		return timeoutIndex;
	}

	/**
	 * removeScriptInfo
	 * 
	 * @param id
	 */
	public void removeScriptInfo(String id)
	{
		if (this._scriptInfo.containsKey(id))
		{
			this._scriptInfo.remove(id);
		}
	}

	/**
	 * storeScriptState
	 * 
	 * @param uri
	 * @param script
	 * @param scope
	 * @return String
	 */
	public synchronized String storeScriptState(String uri, Script script, Scriptable scope)
	{
		// inject the script id
		// NOTE: we do this before executing the script so it can have access to this value
		String id = "_" + (++scriptIndex) + "_"; //$NON-NLS-1$ //$NON-NLS-2$
		ScriptableObject.defineProperty(scope, idPropertyName, id, READONLY | DONTENUM);

		// save reference to script info
		// NOTE: we save script info before executing so we can call "include" in the script's
		// global scope. Doing
		// this after "exec" will cause "include" to get a null reference when trying to lookup this
		// script
		ScriptInfo info = new ScriptInfo(script, id, new java.io.File(uri), scope);
		this._scriptInfo.put(id, info);
		this._idXref.put(uri, id);

		return id;
	}

	/**
	 * getXrefId
	 * 
	 * @param path
	 * @return String
	 */
	public String getXrefId(String path)
	{
		String result = null;

		if (this._idXref.containsKey(path))
		{
			result = (String) this._idXref.get(path);
		}

		return result;
	}

	/**
	 * removeXrefId
	 * 
	 * @param path
	 */
	public void removeXrefId(String path)
	{
		if (this._idXref.containsKey(path))
		{
			this._idXref.remove(path);
		}
	}

	/**
	 * Get a reference to the editors instance
	 * 
	 * @return Editors
	 */
	public Editors getEditors()
	{
		return this._editors;
	}

	/**
	 * Get a reference to the menus instance
	 * 
	 * @return Menus
	 */
	public Menus getMenus()
	{
		return this._menus;
	}

	/**
	 * Get a reference to the views instance
	 * 
	 * @return Views
	 */
	public Views getViews()
	{
		return this._views;
	}
}
