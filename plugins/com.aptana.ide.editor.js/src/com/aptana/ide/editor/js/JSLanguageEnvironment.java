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
package com.aptana.ide.editor.js;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.js.environment.LexemeBasedEnvironmentLoader;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.FunctionBase;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSObjectConstructor;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.reader.NativeObjectsReader;
import com.aptana.ide.editor.scriptdoc.parsing.reader.NativeObjectsReader2;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * Initializes the JavaScript environment
 * 
 * @author Robin Debreuil
 */
public class JSLanguageEnvironment implements ILanguageEnvironment
{
	/*
	 * Fields
	 */
	private static ILanguageEnvironment instance;

	private Environment environment;
	private ObjectBase global;
	private boolean isEnvironmentLoaded = false;

	/**
	 * SCRIPTDOC_ID
	 */
	public static final String SCRIPTDOC_ID = "scriptdoc"; //$NON-NLS-1$

	/**
	 * TAG_BINARY_FILE
	 */
	public static final String TAG_BINARY_FILE = "binary-file"; //$NON-NLS-1$

	/**
	 * TAG_JS_FILE
	 */
	public static final String TAG_JS_FILE = "js-file"; //$NON-NLS-1$

	/**
	 * TAG_XML_FILE
	 */
	public static final String TAG_XML_FILE = "xml-file"; //$NON-NLS-1$

	/**
	 * ATTR_FILE_PATH
	 */
	public static final String ATTR_FILE_PATH = "path"; //$NON-NLS-1$

	/**
	 * ATTR_USER_AGENT
	 */
	public static final String ATTR_USER_AGENT = "user-agent"; //$NON-NLS-1$

	/**
	 * ATTR_ICON
	 */
	public static final String ATTR_ICON = "icon"; //$NON-NLS-1$

	/**
	 * ATTR_LOAD
	 */
	public static final String ATTR_LOAD = "load"; //$NON-NLS-1$

	/**
	 * JS_CORE
	 */
	public static final String JS_CORE = "JS Core"; //$NON-NLS-1$

	/**
	 * DOM_5
	 */
	public static final String DOM_5 = "DOM 5"; //$NON-NLS-1$

	/**
	 * DOM_3
	 */
	public static final String DOM_3 = "DOM 3"; //$NON-NLS-1$

	/**
	 * DOM_1_2
	 */
	public static final String DOM_1_2 = "DOM 1 & 2"; //$NON-NLS-1$

	/**
	 * DOM_0
	 */
	public static final String DOM_0 = "DOM 0"; //$NON-NLS-1$

	/**
	 * SLEEP_DELAY
	 */
	public static int SLEEP_DELAY = 2000;

	/**
	 * True if we wish to include default js objects in the environment (turn off for debugging).
	 */
	public static boolean includeCore = false;
	/**
	 * True if we wish to include default DOM0 objects in the environment (turn off for debugging).
	 */
	public static boolean includeHtml0 = false;
	/**
	 * True if we wish to include default DOM1&2 objects in the environment (turn off for debugging).
	 */
	public static boolean includeHtml12 = false;
	/**
	 * True if we wish to include default DOM3 objects in the environment (turn off for debugging).
	 */
	public static boolean includeHtml3 = false;
	/**
	 * True if we wish to include default DOM5 objects in the environment (turn off for debugging).
	 */
	public static boolean includeHtml5 = false;

	private IObject undef = ObjectBase.UNDEFINED;

	/*
	 * Constructors
	 */

	/**
	 * JSLanguageEnvironment
	 */
	JSLanguageEnvironment()
	{
		isEnvironmentLoaded = false;
		initEnvironment();

		EnvironmentManager.addEnvironmentMapping(JSMimeType.MimeType, environment);
		EnvironmentManager.addEnvironmentMapping(ScriptDocMimeType.MimeType, environment);
	}

	/**
	 * Rebuild the JavaScript environment
	 */
	public static void rebuildEnvironment()
	{
		if (instance != null)
		{
			((JSLanguageEnvironment) instance).buildEnvironment();
			EnvironmentManager.addEnvironmentMapping(JSMimeType.MimeType, getInstance().getRuntimeEnvironment());
			EnvironmentManager.addEnvironmentMapping(ScriptDocMimeType.MimeType, getInstance().getRuntimeEnvironment());
		}
	}

	/**
	 * Resets the JavaScript environment with profiles considered as well
	 */
	public static void resetEnvironment()
	{
		if (instance != null)
		{
			rebuildEnvironment();
			JSLanguageEnvironment.getInstance().cleanEnvironment();
			UnifiedEditorsPlugin.getDefault().getProfileManager().addLanguageSupport(JSMimeType.MimeType,
					JSLanguageEnvironment.getInstance(), null);
			UnifiedEditorsPlugin.getDefault().getProfileManager().onUpdaterThreadUpdate();
		}
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.editors.unified.ILanguageEnvironment#getRuntimeEnvironment()
	 */
	public IRuntimeEnvironment getRuntimeEnvironment()
	{
		return environment;
	}

	/**
	 * Gets specifically the JSEnvironment, rather than needing a cast on getEnvironment
	 * 
	 * @return JS Environment
	 */
	public Environment getJSEnvironment()
	{
		return environment;
	}

	private void buildEnvironment()
	{
		isEnvironmentLoaded = false;
		global = environment.initBuiltInObjects();
		loadEnvironment();
	}

	private void initEnvironment()
	{
		// create javascript environment
		environment = new Environment();
		global = environment.initBuiltInObjects();

		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(SLEEP_DELAY);
					loadEnvironment();
				}
				catch (Exception e1)
				{
					IdeLog.logInfo(JSPlugin.getDefault(), "initEnvironment aborted", e1); //$NON-NLS-1$
				}
			}
		}, "Aptana: JS Language Environment Loader"); //$NON-NLS-1$

		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * getInstance
	 * 
	 * @return JSLanguageEnvironment
	 */
	public static ILanguageEnvironment getInstance()
	{
		if (instance == null)
		{
			instance = new JSLanguageEnvironment();
		}

		return instance;
	}

	// /**
	// * getDocumentationManager
	// *
	// * @return DocumentationManager
	// */
	// public DocumentationManager getDocumentationManager()
	// {
	// return documentationManager;
	// }

	/**
	 * @see com.aptana.ide.editors.unified.ILanguageEnvironment#cleanEnvironment()
	 */
	public void cleanEnvironment()
	{
		synchronized (this)
		{
			String[] keys = FileContextManager.getKeySet();

			for (int i = 0; i < keys.length; i++)
			{
				String path = keys[i];
				FileService fileService = FileContextManager.get(path);
				if (fileService != null)
				{
					// [RD] this must also delete the languages held inside it
					IFileLanguageService jsfs = fileService.getLanguageService(JSMimeType.MimeType);
					if (jsfs != null)
					{
						jsfs.reset(false);
					}

					JSFileLanguageService languageService = JSFileLanguageService.getJSFileLanguageService(fileService);
					if (languageService != null)
					{
						languageService.reset(false);
					}
				}
				else
				{
					// This appears to happen on shutdown. May be an error
					IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(
							Messages.JSLanguageEnvironment_FileServiceNull, path));
				}

				// clears the doc @id mappings
				synchronized (environment)
				{
					// clears out the properties added via orphaned @id props (docs with no mapped object)
					Map idProps = EnvironmentManager.getDocumentationRefMap();
					Iterator props = idProps.values().iterator();
					while (props.hasNext())
					{
						Object o = props.next();
						Reference reference = (Reference) o;
						String propertyName = reference.getPropertyName();
						IObject parentObject = reference.getObjectBase();
						parentObject.deletePropertyName(propertyName);
					}
					idProps.clear();
					EnvironmentManager.getDocumentationIdMap().clear();
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ILanguageEnvironment#loadEnvironment()
	 */
	public void loadEnvironment()
	{
		if (isEnvironmentLoaded)
		{
			return;
		}

		isEnvironmentLoaded = true;

		Map<String, TextAttribute> htmlIdents = JSColorizer.htmlIdents;
		Map<String, TextAttribute> jsCoreIdents = JSColorizer.jsCoreIdents;
		ObjectBase gb = global;
		Environment env = environment;

		// load documentation
		// create native objects documentation reader

		NativeObjectsReader reader = new NativeObjectsReader(env);
		// load core objects and documentation into our JavaScript environment
		try
		{
			int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
			int offset = Range.Empty.getStartingOffset();
			String[] envs = getLoadedEnvironments();
			includeHtml5 = false;
			includeHtml3 = false;
			includeHtml12 = false;
			includeHtml0 = false;
			for (int i = 0; i < envs.length; i++)
			{
				if (DOM_5.equals(envs[i]))
				{
					includeHtml5 = true;
				}
				if (DOM_3.equals(envs[i]))
				{
					includeHtml3 = true;
				}
				if (DOM_1_2.equals(envs[i]))
				{
					includeHtml12 = true;
				}
				else if (DOM_0.equals(envs[i]))
				{
					includeHtml0 = true;
				}
			}

			// Always include core since removing affects other js libraries from loading
			includeCore = true;
			gb.putPropertyValue("this", gb, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM); //$NON-NLS-1$

			if (includeCore)
			{
				// get core JavaScript documentation
				// InputStream jsIn =
				// reader.getClass().getResourceAsStream("/com/aptana/ide/editor/js/resources/js_core.xml");
				InputStream jsIn = this.getClass().getResourceAsStream("/com/aptana/ide/editor/js/parsing/js_core.bin"); //$NON-NLS-1$

				// load core JS documentation
				// try{reader.loadXML(jsIn, false);}catch (Exception e){}
				reader.setUserAgent(JS_CORE); //$NON-NLS-1$
				reader.load(jsIn, false);
				jsCoreIdents.putAll(reader.getNames());
				// GenJSStubsFromXml.generate("C:/jsStubs.js", jsIn);

				// close the input stream
				jsIn.close();

				// add a prototype property to global
				JSObjectConstructor gObj = (JSObjectConstructor) gb.getPropertyValue("Object", fileIndex, offset); //$NON-NLS-1$
				IObject newobj = gObj.construct(env, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
				gb.putPropertyValue("prototype", newobj, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM); //$NON-NLS-1$
				gb.setPrototype(gb.getPropertyValue("prototype", fileIndex, offset)); //$NON-NLS-1$

			}

			if (includeHtml0)
			{
				// add html base

				// window is the gb object
				gb.putPropertyValue("window", gb, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM); //$NON-NLS-1$
				// we must make sure the "Window" class puts its members on global
				gb.putPropertyValue("Window", gb, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM //$NON-NLS-1$
						| Property.INTERNAL | Property.NOT_VISIBLE);

				// IObject winProt = gb.getPropertyValue("Window", fileIndex, offset).getPropertyValue("prototype",
				// fileIndex, offset);
				// gb.setPrototype(winProt);

				// get core HTML documentation
				// InputStream dom0 =
				// reader.getClass().getResourceAsStream("/com/aptana/ide/editor/js/resources/dom_0.xml");
				InputStream dom0 = this.getClass().getResourceAsStream("/com/aptana/ide/editor/js/parsing/dom_0.bin"); //$NON-NLS-1$
				// load core JS documentation
				// try{reader.loadXML(dom0, true);}catch (Exception e){}
				reader.setUserAgent("DOM 0"); //$NON-NLS-1$
				reader.load(dom0, true);
				// add names for code assist
				htmlIdents.putAll(reader.getNames());
				// close the input stream
				dom0.close();
				htmlIdents.put("window", null); //$NON-NLS-1$
			}

			if (includeHtml12)
			{
				// get core HTML documentation
				// InputStream dom2 =
				// reader.getClass().getResourceAsStream("/com/aptana/ide/editor/js/resources/dom_2.xml");
				InputStream dom2 = this.getClass().getResourceAsStream("/com/aptana/ide/editor/js/parsing/dom_2.bin"); //$NON-NLS-1$
				// load core JS documentation
				// try{reader.loadXML(dom2, true);}catch (Exception e){}
				reader.setUserAgent(DOM_1_2); //$NON-NLS-1$
				reader.load(dom2, true);

				htmlIdents.putAll(reader.getNames());
				// close the input stream
				dom2.close();

				IObject hdoc = gb.getPropertyValue("HTMLDocument", fileIndex, offset); //$NON-NLS-1$
				if (hdoc != undef)
				{
					JSFunction htmldocFn = (JSFunction) hdoc;
					IObject doc = htmldocFn.construct(env, FunctionBase.EmptyArgs, fileIndex, Range.Empty);
					// win.attributedPut("Document", doc, Property.DONT_DELETE | Property.DONT_ENUM);
					gb.putPropertyValue("document", doc, fileIndex, Property.DONT_DELETE | Property.DONT_ENUM); //$NON-NLS-1$

					// Not sure why these were here, probably to manually hook up the prototype before the docs were
					// set
					// (docs used to be double defined) - keep in to watch for issues on 'document', but seems to
					// work/
					// IObject docClassProto = gb.getPropertyValue("Document", fileIndex,
					// offset).getPropertyValue("prototype", fileIndex, offset);
					// doc.setPrototype(docClassProto);

					// set docs manually
					((FunctionDocumentation) htmldocFn.getDocumentation()).setIsInstance(true);
					doc.setDocumentation(htmldocFn.getDocumentation());
				}
				htmlIdents.put("document", null); //$NON-NLS-1$
			}

			if (includeHtml3)
			{
				// get core DOM 3 documentation
				InputStream dom3 = this.getClass().getResourceAsStream("/com/aptana/ide/editor/js/parsing/dom_3.bin"); //$NON-NLS-1$
				// load core JS documentation
				reader.setUserAgent(DOM_3); //$NON-NLS-1$
				reader.load(dom3, true);

				htmlIdents.putAll(reader.getNames());
				// close the input stream
				dom3.close();
			}

			if (includeHtml5)
			{
				// get core DOM 5 documentation
				InputStream dom5 = this.getClass().getResourceAsStream("/com/aptana/ide/editor/js/parsing/dom_5.bin"); //$NON-NLS-1$
				// load core JS documentation
				reader.setUserAgent(DOM_5); //$NON-NLS-1$
				reader.load(dom5, true);

				htmlIdents.putAll(reader.getNames());
				// close the input stream
				dom5.close();
			}

			try
			{
				loadContributedFiles();
			}
			catch (Exception e)
			{
				IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSLanguageEnvironment_ErrorLoadingEnvironment, e);
			}
		}
		catch (IOException e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSLanguageEnvironment_ErrorLoadingEnvironment, e);
		}
	}

	/**
	 * Enables a particular environment
	 * 
	 * @param environmentName
	 */
	public static void enableEnvironment(String environmentName)
	{
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String[] envsToLoad = getEnabledEnvironments();
		String[] envsToDisable = getDisabledEnvironments();

		Set<String> envs = new HashSet<String>();
		envs.addAll(Arrays.asList(envsToLoad));

		Set<String> disabledEnvs = new HashSet<String>();
		disabledEnvs.addAll(Arrays.asList(envsToDisable));

		if (!envs.contains(environmentName))
		{
			envs.add(environmentName);
		}

		if (disabledEnvs.contains(environmentName))
		{
			disabledEnvs.remove(environmentName);
		}

		store.setValue(IPreferenceConstants.LOADED_ENVIRONMENTS, StringUtils.join(",", envs.toArray(new String[0]))); //$NON-NLS-1$
		store.setValue(IPreferenceConstants.DISABLED_ENVIRONMENTS, StringUtils.join(",", disabledEnvs //$NON-NLS-1$
				.toArray(new String[0])));
	}

	/**
	 * Disables a particular environment
	 * 
	 * @param environmentName
	 */
	public static void disableEnvironment(String environmentName)
	{
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String[] envsToLoad = getEnabledEnvironments();
		String[] envsToDisable = getDisabledEnvironments();

		Set<String> envs = new HashSet<String>();
		envs.addAll(Arrays.asList(envsToLoad));

		Set<String> disabledEnvs = new HashSet<String>();
		disabledEnvs.addAll(Arrays.asList(envsToDisable));

		if (envs.contains(environmentName))
		{
			envs.remove(environmentName);
		}

		if (!disabledEnvs.contains(environmentName))
		{
			disabledEnvs.add(environmentName);
		}

		store.setValue(IPreferenceConstants.LOADED_ENVIRONMENTS, StringUtils.join(",", envs.toArray(new String[0]))); //$NON-NLS-1$
		store.setValue(IPreferenceConstants.DISABLED_ENVIRONMENTS, StringUtils.join(",", disabledEnvs //$NON-NLS-1$
				.toArray(new String[0])));
	}

	/**
	 * Returns the list of environments to load
	 * 
	 * @return - loaded environments ids
	 */
	public static String[] getLoadedEnvironments()
	{
		// loaded envs are all envs specified in preference store + items marked
		// as load by default that are not marked as disabled.

		String[] loaded = getEnabledEnvironments();
		IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Enabled_environments, StringUtils.join(",", //$NON-NLS-1$
				loaded)));
		Set<String> envs = new HashSet<String>();
		envs.addAll(Arrays.asList(loaded));

		String[] defaults = getDefaultLoadedEnvironments();
		IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Default_loaded_environments, StringUtils.join(
				",", defaults))); //$NON-NLS-1$
		envs.addAll(Arrays.asList(defaults));

		String[] disabled = getDisabledEnvironments();
		IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Disabled_environments, StringUtils.join(",", //$NON-NLS-1$
				disabled)));
		envs.removeAll(Arrays.asList(disabled));

		IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Resulting_set, StringUtils.join(",", envs //$NON-NLS-1$
				.toArray(new String[0]))));
		return envs.toArray(new String[0]);
	}

	/**
	 * gets the list of enabled environments
	 * 
	 * @return - enabled environment ids
	 */
	public static String[] getEnabledEnvironments()
	{
		String envsToLoad = JSPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.LOADED_ENVIRONMENTS);
		if (envsToLoad != null)
		{
			return envsToLoad.split(","); //$NON-NLS-1$
		}
		else
		{
			return new String[0];
		}
	}

	/**
	 * gets the list of enabled environments
	 * 
	 * @return - disabled environment ids
	 */
	public static String[] getDisabledEnvironments()
	{
		String envsToDisable = JSPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.DISABLED_ENVIRONMENTS);
		if (envsToDisable != null)
		{
			return envsToDisable.split(","); //$NON-NLS-1$
		}
		else
		{
			return new String[0];
		}
	}

	/**
	 * Sets the list of currently enabled environments
	 * 
	 * @param environments
	 */
	public static void setEnabledEnvironments(String[] environments)
	{
		String env = StringUtils.join(",", environments); //$NON-NLS-1$
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.LOADED_ENVIRONMENTS, env);
	}

	/**
	 * Sets the list of currently disabled environments
	 * 
	 * @param environments
	 */
	public static void setDisabledEnvironments(String[] environments)
	{
		String env = StringUtils.join(",", environments); //$NON-NLS-1$
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.DISABLED_ENVIRONMENTS, env);
	}

	/**
	 * Adds a user environment that is an absolute path to a file on disk
	 * 
	 * @param toAdd
	 */
	public static void addUserEnvironment(String toAdd)
	{
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String value = store.getString(IPreferenceConstants.ADDED_ENVIRONMENTS);
		value += "," + toAdd; //$NON-NLS-1$
		store.setValue(IPreferenceConstants.ADDED_ENVIRONMENTS, value);
	}

	/**
	 * Removes a user environment for the given absolute path
	 * 
	 * @param toRemove
	 */
	public static void removeUserEnvironment(String toRemove)
	{
		String[] envs = getUserAddedJSEnvironments();
		List<String> newEnvs = new ArrayList<String>();
		for (int i = 0; i < envs.length; i++)
		{
			if (!envs[i].equals(toRemove) && !newEnvs.contains(envs[i]))
			{
				newEnvs.add(envs[i]);
			}
		}
		String toSet = StringUtils.join(",", newEnvs.toArray(new String[0])); //$NON-NLS-1$
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.ADDED_ENVIRONMENTS, toSet);
	}

	/**
	 * Gets the list of absolute paths to global reference files on disk
	 * 
	 * @return - array of string absolute paths
	 */
	public static String[] getUserAddedJSEnvironments()
	{
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String jsEnv = store.getString(IPreferenceConstants.ADDED_ENVIRONMENTS);
		return jsEnv.split(","); //$NON-NLS-1$
	}

	/**
	 * Returns the list of environments to load
	 * 
	 * @return - default environments ids
	 */
	public static String[] getDefaultLoadedEnvironments()
	{
		// If preference is default, then automatically add in any items marked as
		// "load" from extension point. Otherwise, just use preset list.
		String defaultEnvsToLoad = JSPlugin.getDefault().getPreferenceStore().getDefaultString(
				IPreferenceConstants.LOADED_ENVIRONMENTS);

		List<String> list = new ArrayList<String>();

		String[] envs = defaultEnvsToLoad.split(","); //$NON-NLS-1$
		list.addAll(Arrays.asList(envs));
		envs = getLoadedEnvironmentsFromExtension(TAG_BINARY_FILE);
		list.addAll(Arrays.asList(envs));
		envs = getLoadedEnvironmentsFromExtension(TAG_XML_FILE);
		list.addAll(Arrays.asList(envs));
		envs = getLoadedEnvironmentsFromExtension(TAG_JS_FILE);
		list.addAll(Arrays.asList(envs));

		return list.toArray(new String[0]);
	}

	private void addContributedXML(NativeObjectsReader2 reader, URL key, String userAgent)
	{
		if (userAgent != null)
		{
			reader.setUserAgent(userAgent);
		}
		try
		{
			reader.loadXML(key.openStream());
			IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Loaded_environment, key.toExternalForm()));
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSLanguageEnvironment_ErrorLoadingEnvironment, e);
		}
	}

	private void addContributedBinary(NativeObjectsReader2 reader, URL key, String userAgent)
	{
		if (userAgent != null)
		{
			reader.setUserAgent(userAgent);
		}
		try
		{
			reader.load(key.openStream());
			IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSLanguageEnvironment_INF_Loaded_environment, key.toExternalForm()));
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSLanguageEnvironment_ErrorLoadingEnvironment, e);
		}
	}

	private void addContributedJS(URL key, String userAgent)
	{
		if (!DOM_5.equals(userAgent) && !DOM_3.equals(userAgent) && !DOM_1_2.equals(userAgent) && !DOM_0.equals(userAgent))
		{
			try
			{
				key = FileLocator.toFileURL(key);
			}
			catch (IOException e)
			{
			}
			// Load js file
			String path = CoreUIUtils.getPathFromURI(key.getFile());
			String source;
			try
			{
				source = FileUtils.readContent(new File(path));
				IParser parser = LanguageRegistry.getParser(JSMimeType.MimeType);
				JSParseState parseState = (JSParseState) parser.createParseState(null);
				parseState.setEditState(source, source, 0, 0);
				parseState.setFileIndex(FileContextManager.CURRENT_FILE_INDEX);
				parser.parse(parseState);
				LexemeBasedEnvironmentLoader loader = new LexemeBasedEnvironmentLoader(this.environment);
				loader.reloadEnvironment(parseState);
				IDocumentation[] docs = parseState.getDocumentationStore().getDocumentationObjects();
				if (docs != null)
				{
					for (int i = 0; i < docs.length; i++)
					{
						docs[i].setUserAgent(userAgent);
					}
				}
				IdeLog.logInfo(JSPlugin.getDefault(), StringUtils
						.format(Messages.JSLanguageEnvironment_INF_Loaded_environment, key.toExternalForm()));
			}
			catch (Exception e)
			{
				IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(
						Messages.JSLanguageEnvironment_ERR_Loading_contributed_javascript_file, key.toExternalForm()), e);
			}
		}
	}

	/**
	 * loadContributedFiles Loads the files contributed via an extension point
	 */
	private void loadContributedFiles()
	{
		Map<URL, String> xmlFiles = new Hashtable<URL, String>();
		addFromExtension(xmlFiles, TAG_XML_FILE);

		NativeObjectsReader2 reader = new NativeObjectsReader2(environment);

		Set<URL> keys = xmlFiles.keySet();
		for (Iterator<URL> iterator = keys.iterator(); iterator.hasNext();)
		{
			URL key = iterator.next();
			String userAgent = xmlFiles.get(key);
			addContributedXML(reader, key, userAgent);
		}

		Map<URL, String> binaryFiles = new Hashtable<URL, String>();
		addFromExtension(binaryFiles, TAG_BINARY_FILE);

		keys = binaryFiles.keySet();
		for (Iterator<URL> iterator = keys.iterator(); iterator.hasNext();)
		{
			URL key = iterator.next();
			String userAgent = binaryFiles.get(key);
			addContributedBinary(reader, key, userAgent);
		}

		Map<URL, String> jsFiles = new Hashtable<URL, String>();
		addFromExtension(jsFiles, TAG_JS_FILE);
		keys = jsFiles.keySet();
		for (Iterator<URL> iterator = keys.iterator(); iterator.hasNext();)
		{
			URL key = iterator.next();
			String userAgent = jsFiles.get(key);
			addContributedJS(key, userAgent);
		}

		Map<URL, String> userXMLFiles = new Hashtable<URL, String>();
		addUserContributedFiles(userXMLFiles, IPreferenceConstants.ADDED_ENVIRONMENTS);
		keys = userXMLFiles.keySet();
		for (Iterator<URL> iterator = keys.iterator(); iterator.hasNext();)
		{
			URL key = iterator.next();
			String userAgent = userXMLFiles.get(key);
			String keyValue = key.toExternalForm();
			if (keyValue.endsWith(".js") || keyValue.endsWith(".sdoc")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				addContributedJS(key, userAgent);
			}
			else if (keyValue.endsWith(".bin")) //$NON-NLS-1$
			{
				addContributedBinary(reader, key, userAgent);
			}
			else if (keyValue.endsWith(".xml")) //$NON-NLS-1$
			{
				addContributedXML(reader, key, userAgent);
			}

		}

	}

	private static void addUserContributedFiles(Map<URL, String> ids, String prefKey)
	{
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String values = store.getString(prefKey);
		String[] entries = values.split(","); //$NON-NLS-1$
		String[] envs = getLoadedEnvironments();
		for (String entry : entries)
		{
			try
			{
				File file = new File(entry);
				if (file.exists())
				{
					boolean load = false;
					for (int k = 0; k < envs.length; k++)
					{
						if (file.getName().equals(envs[k]))
						{
							load = true;
							break;
						}
					}
					if (load)
					{
						ids.put(file.toURL(), file.getName());
					}
				}
			}
			catch (Exception e1)
			{
			}

		}
	}

	/**
	 * @return Returns the isEnvironmentLoaded flag.
	 */
	public boolean isEnvironmentLoaded()
	{
		return isEnvironmentLoaded;
	}

	/**
	 * addFromExtension
	 * 
	 * @param ids
	 * @param elementName
	 */
	private static void addFromExtension(Map<URL, String> ids, String elementName)
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		String[] envs = getLoadedEnvironments();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(JSPlugin.ID, SCRIPTDOC_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						// InstanceCreator inst = null;

						if (element.getName().equals(elementName))
						{
							String resourceName = element.getAttribute(ATTR_FILE_PATH);
							String userAgent = element.getAttribute(ATTR_USER_AGENT);
							if (userAgent != null)
							{
								boolean load = false;
								for (int k = 0; k < envs.length; k++)
								{
									if (userAgent.equals(envs[k]))
									{
										load = true;
										break;
									}
								}
								if (load)
								{
									IExtension ext = element.getDeclaringExtension();
									String pluginId = ext.getNamespaceIdentifier();
									Bundle bundle = Platform.getBundle(pluginId);
									URL resource = bundle.getResource(resourceName);
									if (resource != null)
									{
										ids.put(resource, userAgent);
									}
									else
									{
										IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(
												Messages.JSLanguageEnvironment_ERR_Unable_load_javascript_env_resource, resourceName));
									}
								}
							}
							//							
							// inst = new InstanceCreator(element, resourceName)
							// {
							// public Object createInstance()
							// {
							// IExtension ext = element.getDeclaringExtension();
							// String pluginId = ext.getNamespaceIdentifier();
							// Bundle bundle = Platform.getBundle(pluginId);
							// URL resource = bundle.getResource(this.attributeName);
							// return resource;
							// }
							// };
							// if (inst != null)
							// {
							// Object url = inst.createInstance();
							// ids.put(url, userAgent);
							// }
						}
					}
				}
			}
		}
	}

	/**
	 * addFromExtension
	 * 
	 * @param ids
	 * @param elementName
	 */
	private static String[] getLoadedEnvironmentsFromExtension(String elementName)
	{
		List<String> ids = new ArrayList<String>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(JSPlugin.ID, SCRIPTDOC_ID);
			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						// InstanceCreator inst = null;

						if (element.getName().equals(elementName))
						{
							String userAgent = element.getAttribute(ATTR_USER_AGENT);
							String loadByDefault = element.getAttribute(ATTR_LOAD);
							if (userAgent != null && Boolean.parseBoolean(loadByDefault))
							{
								ids.add(userAgent);
							}
						}
					}
				}
			}
		}

		return ids.toArray(new String[0]);
	}
}
