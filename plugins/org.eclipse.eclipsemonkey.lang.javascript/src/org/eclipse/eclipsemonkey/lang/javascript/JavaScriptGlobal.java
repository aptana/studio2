/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.eclipsemonkey.utils.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Provides global functions to JavaScript environment
 * 
 * @author Paul Colton
 * @author Kevin Lindsey
 */
public class JavaScriptGlobal extends ScriptableObject
{
	/**
	 * The "location" property name
	 */
	public static final String LOCATION_PROPERTY = "location";	//$NON-NLS-1$
	
	/**
	 * The "classLoader" property name
	 */
	public static final String CLASS_LOADER_PROPERTY = "classLoader";	//$NON-NLS-1$
	
	private static final String INCLUDES_PROPERTY = "includes";	//$NON-NLS-1$
	private static final long serialVersionUID = -8969608471837413334L;
	
	private static JavaScriptConsole _console;
	private static MessageConsoleStream _consoleStream;
	
	private JavaScriptPrintStream _err;
	private Map _runningSetTimeouts = new HashMap();
	private int _setTimeoutIndex;
	
	private static ImageDescriptor scriptingDescriptor = JavaScriptPlugin.getImageDescriptor("icons/js_file.gif"); //$NON-NLS-1$

	/**
	 * Provides global functions to the JavaScript environment
	 * 
	 * @param cx
	 *            The currently active script context
	 */
	public JavaScriptGlobal(Context cx)
	{
		// create unsealed standard objects
		cx.initStandardObjects(this, false);
	
		// create global properties
		this.createAllProperties();
	}
	
	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "JavaScriptGlobal"; //$NON-NLS-1$
	}
	
	/**
	 * Returns a reference to the current console, initializing it if it's not created
	 * 
	 * @return A console stream
	 */
	public static MessageConsoleStream getConsoleStream()
	{
		if (_console == null)
		{
			_console = new JavaScriptConsole(Messages.JavaScriptGlobal_TTL_javascript_console, scriptingDescriptor);
			_consoleStream = _console.newMessageStream();

			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					_consoleStream.setColor(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE));
				}
			});

			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { _console });
			_consoleStream.println(Messages.JavaScriptGlobal_MSG_javascript_console_started);
		}

		return _consoleStream;
	}
	
	/**
	 * Return a list of all property names. All names return by this method must appear within the definition of this
	 * class. Sub-classes should override this method to augment the return value to include functions defined within
	 * the sub-class itself.
	 * 
	 * @return Returns a list of function property names to add to this global
	 */
	protected String[] getFunctionPropertyNames()
	{
		return new String[] {
			"alert", //$NON-NLS-1$
			"clearTimeout", //$NON-NLS-1$
			"confirm", //$NON-NLS-1$
			"execute", //$NON-NLS-1$
			"getProperty", //$NON-NLS-1$
			"include", //$NON-NLS-1$
			"loadBundle", //$NON-NLS-1$
			"parseXML", //$NON-NLS-1$
			"prompt", //$NON-NLS-1$
			"runOnUIThread", //$NON-NLS-1$
			"runOnUIThreadAsync", //$NON-NLS-1$
			"setClassLoader", //$NON-NLS-1$
			"setTimeout", //$NON-NLS-1$
		};
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
				e.printStackTrace();
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
						MessageDialog.openWarning(shell, Messages.JavaScriptGlobal_TTL_Alert_dialog, message);
					}
				}
			});
		}
	}

	/**
	 * Stop a setTimeout from firing its associated function
	 * 
	 * @param timeoutId
	 *            The setTimeout id returned when setTimeout was invoked. If the id is not recognized, this method does
	 *            nothing
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
						a.result = MessageDialog.openConfirm(shell, Messages.JavaScriptGlobal_TTL_Confirm_dialog, message);
					}
				}
			});
		}

		return a.result;
	}

	/**
	 * Create all properties for this global instance
	 */
	protected void createAllProperties()
	{
		this.createProperties();
		this.createFunctionProperties();
	}

	/**
	 * Create all function properties for this global. Sub-classes can override this method to modify how function
	 * properties are added to global; however, most sub-classes will need only to override getFunctionPropertyNames to
	 * include the additional function names provided by that instance.
	 */
	protected void createFunctionProperties()
	{
		String[] propertyNames = this.getFunctionPropertyNames();

		if (propertyNames != null)
		{
			this.defineFunctionProperties(propertyNames, this.getClass(), READONLY | PERMANENT);
		}
	}

	/**
	 * Create all non-function properties for this global. Sub-classes should override this method to add their own
	 * non-function properties to this global
	 */
	protected void createProperties()
	{
		// create standard error stream, cache, and add to global
		this._err = new JavaScriptPrintStream(this, System.err);
		this.defineProperty("err", _err, READONLY | PERMANENT); //$NON-NLS-1$

		// get standard out stream and add to global
		MessageConsoleStream stream = getConsoleStream();
		this.defineProperty("out", stream, READONLY | PERMANENT); //$NON-NLS-1$
	}

	/**
	 * Load a script into a currently executing library
	 * 
	 * @param cx
	 *            The scripting context
	 * @param thisObj
	 *            The object that activated this function call
	 * @param args
	 *            The arguments passed to this function call
	 * @param funObj
	 *            The function object that invoked this method
	 */
	public static void include(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0)
		{
			String libraryName = Context.toString(args[0]);
			java.io.File library = new java.io.File(libraryName);

			if (library.exists() == false)
			{
				String location = Context.toString(thisObj.get(LOCATION_PROPERTY, thisObj));
				java.io.File parent = new java.io.File(location).getParentFile();
				String name = parent.getAbsolutePath() + java.io.File.separator + libraryName;

				library = new java.io.File(name);
			}

			if (library.exists())
			{
				String absolutePath = library.getAbsolutePath();
				Scriptable includes;
				
				if (thisObj.has(INCLUDES_PROPERTY, thisObj))
				{
					includes = (Scriptable) thisObj.get(INCLUDES_PROPERTY, thisObj);
				}
				else
				{
					includes = cx.newObject(thisObj);
					
					((ScriptableObject) thisObj).defineProperty(INCLUDES_PROPERTY, includes, READONLY | PERMANENT);
				}
				
				// only compile and execute script if we haven't already
				if (includes.has(absolutePath, includes) == false)
				{
					try
					{
						// grab the script's source
						String source = JavaScriptGlobal.getText(new FileInputStream(library));
	
						// compile the script
						Script script = cx.compileString(source, absolutePath, 1, null);
	
						// tag as loaded
						includes.put(absolutePath, includes, ""); //$NON-NLS-1$
						
						// exec
						script.exec(cx, thisObj);
					}
					catch (IOException e)
					{
						// I/O error reading library
					}
				}
			}
			else
			{
				// cannot locate include
			}
		}
		else
		{
			// no include file defined
		}
	}
	
	/**
	 * Load the specified bundle and add it to the active JS class loader
	 * 
	 * @param cx
	 *            The scripting context
	 * @param thisObj
	 *            The object that activated this function call
	 * @param args
	 *            The arguments passed to this function call
	 * @param funObj
	 *            The function object that invoked this method
	 */
	public static void loadBundle(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		if (args.length > 0)
		{
			String bundleName = Context.toString(args[0]);
			Bundle bundle = Platform.getBundle(bundleName);
	
			if (bundle == null)
			{
				throw new RuntimeException("Global_Bundle_Not_Found: " + bundleName); //$NON-NLS-1$
			}
	
			ClassLoader c = cx.getApplicationClassLoader();
	
			if (c instanceof JavaScriptClassLoader)
			{
				JavaScriptClassLoader classLoader = (JavaScriptClassLoader) c;
				classLoader.addBundle(bundle);
			}
			else
			{
				throw new RuntimeException("JavaScriptClassLoader not the application Classloader."); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Parse the specified XML string and return a W3C DOM as a result
	 * 
	 * @param xml
	 *            The source XML string
	 * @return Document The resulting Document element or null
	 */
	public Document parseXML(String xml)
	{
		Document result = null;

		if (xml != null && xml.length() > 0)
		{
			try
			{
				// get the document builder factory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				// create the document builder
				DocumentBuilder builder = factory.newDocumentBuilder();

				// create a stream from our XML string
				InputStream in = new ByteArrayInputStream(xml.getBytes());

				// parse XML to create our document element
				result = builder.parse(in);
			}
			catch (ParserConfigurationException e)
			{
				e.printStackTrace();
			}
			catch (SAXException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * Popup an prompt box with the specified message
	 * 
	 * @param cx
	 *            The scripting context
	 * @param thisObj
	 *            The object that activated this function call
	 * @param args
	 *            The message to display in the prompt dialog
	 * @param funObj
	 *            The function object that invoked this method
	 * @return boolean
	 */
	public static Object prompt(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		final Display currentDisplay = Display.getCurrent();
		String messageArg = Messages.JavaScriptGlobal_MSG_Prompt_dialog_default;
		String defaultValueArg = ""; //$NON-NLS-1$
	
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
			public Object result = ""; //$NON-NLS-1$
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
						InputDialog dialog = new InputDialog(null, Messages.JavaScriptGlobal_TTL_Prompt_dialog, message, defaultValue, null);
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
	 * Execute the specified function on the UI thread
	 * 
	 * @param cx
	 *            The scripting context
	 * @param thisObj
	 *            The object that activated this function call
	 * @param args
	 *            The message to display in the prompt dialog
	 * @param funObj
	 *            The function object that invoked this method
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
			display.syncExec(new JavaScriptThread(f.getParentScope(), f, new Object[0]));
		}
	}
	
	/**
	 * Execute the specified function on the UI thread, asynchronously
	 * 
	 * @param cx
	 *            The scripting context
	 * @param thisObj
	 *            The object that activated this function call
	 * @param args
	 *            The message to display in the prompt dialog
	 * @param funObj
	 *            The function object that invoked this method
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
			display.asyncExec(new JavaScriptThread(f.getParentScope(), f, new Object[0]));
		}
	}
	
	/**
	 * setClassLoader
	 * 
	 * @param cx 
	 * @param thisObj 
	 * @param args 
	 * @param funObj 
	 * @return new class loader or class loader passed into method
	 */
	public static ClassLoader setClassLoader(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		ClassLoader result = null;
		
		if (args.length > 0)
		{
			Object arg = args[0];
			
			if (arg instanceof ClassLoader)
			{
				result = cx.getApplicationClassLoader();
				
				cx.setApplicationClassLoader((ClassLoader) arg);
			}
		}
//		else
//		{
//			result = cx.getApplicationClassLoader();
//			
//			cx.setApplicationClassLoader(new JavaScriptClassLoader());
//		}
		
		return result;
	}
	
	/**
	 * setTimeout
	 * 
	 * @param function
	 *            The function to invoke once this timer completes
	 * @param timeout
	 *            The amount of time to wait in milliseconds before firing the specified function
	 * @return int Returns a handle to this specific timeout instance. This handle can be used in clearTimeout to cancel
	 *         the timer before it fires
	 */
	public int setTimeout(final Function function, final int timeout)
	{
		final JavaScriptGlobal self = this;
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
								Scriptable scope = function.getParentScope();
								Context cx = Context.enter();
	
								function.call(cx, scope, scope, new Object[0]);
	
								Context.exit();
	
								self.clearTimeout(timeoutIndex);
							}
						});
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}, "Aptana Scripting JavaScript setTimeout"); //$NON-NLS-1$
	
		// save reference to this thread to indicate that it should run. Note that clearTimeout will remove this entry
		// and if that is done before the thread wakes, then the action in the thread will not execute
		this._runningSetTimeouts.put(new Integer(timeoutIndex), thread);
	
		thread.setDaemon(true);
	
		// start the thread
		thread.start();
	
		// return the timeout index in case the thread wants to abort this timeout
		return timeoutIndex;
	}
}
