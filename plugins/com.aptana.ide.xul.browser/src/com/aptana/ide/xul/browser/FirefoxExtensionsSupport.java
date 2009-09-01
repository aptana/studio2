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

package com.aptana.ide.xul.browser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.mozilla.interfaces.nsIFile;
import org.mozilla.interfaces.nsIProperties;
import org.mozilla.xpcom.IAppFileLocProvider;
import org.mozilla.xpcom.Mozilla;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.aptana.ide.core.FirefoxUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PlatformUtils;

/**
 * @author Max Stepanov
 */
public final class FirefoxExtensionsSupport {

	private static final String NS_XPCOM_COMPONENT_DIR_LIST = "ComsDL"; //$NON-NLS-1$
	private static final String NS_CHROME_MANIFESTS_FILE_LIST = "ChromeML"; //$NON-NLS-1$
	private static final String NS_EXT_PREFS_DEFAULTS_DIR_LIST = "ExtPrefDL"; //$NON-NLS-1$
	private static final String NS_APP_APPLICATION_REGISTRY_DIR = "AppRegD"; //$NON-NLS-1$
	private static final String NS_XPCOM_INIT_CURRENT_PROCESS_DIR = "MozBinD"; //$NON-NLS-1$
	private static final String NS_OS_CURRENT_PROCESS_DIR = "CurProcD"; //$NON-NLS-1$
	private static final String NS_XPCOM_CURRENT_PROCESS_DIR = "XCurProcD"; //$NON-NLS-1$
	private static final String NS_GRE_DIR = "GreD"; //$NON-NLS-1$
	private static final String NS_XPCOM_COMPONENT_DIR = "ComsD"; //$NON-NLS-1$
	private static final String NS_GRE_COMPONENT_DIR = "GreComsD"; //$NON-NLS-1$
	private static final String NS_APP_PREF_DEFAULTS_50_DIR = "PrfDef"; //$NON-NLS-1$
	private static final String NS_OS_TEMP_DIR = "TmpD"; //$NON-NLS-1$
	private static final String NS_OS_HOME_DIR = "Home"; //$NON-NLS-1$
	private static final String NS_APP_CACHE_PARENT_DIR = "cachePDir"; //$NON-NLS-1$
	private static final String NS_APP_LOCALSTORE_50_FILE = "LclSt"; //$NON-NLS-1$
	private static final String NS_APP_USER_PROFILE_50_DIR = "ProfD"; //$NON-NLS-1$
	private static final String NS_APP_USER_CHROME_DIR = "UChrm"; //$NON-NLS-1$
	private static final String NS_APP_PREFS_50_DIR = "PrefD"; //$NON-NLS-1$
	private static final String NS_APP_PREFS_50_FILE = "PrefF"; //$NON-NLS-1$
	private static final String NS_APP_USER_MIMETYPES_50_FILE = "UMimTyp"; //$NON-NLS-1$
	private static final String NS_APP_HISTORY_50_FILE = "UHist"; //$NON-NLS-1$
	private static final String NS_APP_PLUGINS_DIR_LIST = "APluginsDL"; //$NON-NLS-1$
	private static final String NS_APP_CHROME_DIR_LIST = "AChromDL"; //$NON-NLS-1$
	private static final String NS_APP_PREFS_DEFAULTS_DIR_LIST = "PrefDL"; //$NON-NLS-1$
	private static final String NS_APP_CHROME_DIR = "AChrom"; //$NON-NLS-1$
	private static final String NS_XPCOM_COMPONENT_REGISTRY_FILE = "ComRegF"; //$NON-NLS-1$
	private static final String NS_XPCOM_XPTI_REGISTRY_FILE = "XptiRegF"; //$NON-NLS-1$
	
	private static final String NS_DIRECTORYSERVICE_CONTRACTID = "@mozilla.org/file/directory_service;1"; //$NON-NLS-1$
	
	// Imported from org.eclipse.swt.browser.Mozilla class
	private static final String XULRUNNER_PATH = "org.eclipse.swt.browser.XULRunnerPath"; //$NON-NLS-1$
	private static final String GRE_INITIALIZED = "org.eclipse.swt.browser.XULRunnerInitialized"; //$NON-NLS-1$
	
	private static final String XULRUNNER_PLUGIN = "org.mozilla.xulrunner"; //$NON-NLS-1$
	private static final String COMPREG_FILE = "compreg_x.dat"; //$NON-NLS-1$
	private static final String XPTI_FILE = "xpti_x.dat"; //$NON-NLS-1$
	private static final String XULAPPINFO_FILE = "XULAppInfo.js"; //$NON-NLS-1$
	
	private static final String XULAPPINFO_COMPONENT_DATA = 
		"const CC = Components.classes;\n" + //$NON-NLS-1$
		"const CI = Components.interfaces;\n" + //$NON-NLS-1$
		"const CR = Components.results;\n" + //$NON-NLS-1$
		"const CU = Components.utils;\n" + //$NON-NLS-1$
		"CU.import(\"resource://gre/modules/XPCOMUtils.jsm\");\n" + //$NON-NLS-1$
		"function XULAppInfo() {}\n" + //$NON-NLS-1$
		"XULAppInfo.prototype = {\n" + //$NON-NLS-1$
		"	classDescription: \"XULAppInfo\",\n" + //$NON-NLS-1$
		"	classID:          Components.ID(\"{c763b610-9d49-455a-bbd2-ede71682a1ac}\"),\n" + //$NON-NLS-1$
		"	contractID:       \"@mozilla.org/xre/app-info;1\",\n" + //$NON-NLS-1$
		"	// nsISupports\n" + //$NON-NLS-1$
		"	QueryInterface: XPCOMUtils.generateQI([CI.nsIXULAppInfo]),\n" + //$NON-NLS-1$
		"	// nsIXULAppInfo\n" + //$NON-NLS-1$
		"	vendor: \"Mozilla\",\n" + //$NON-NLS-1$
		"	name: \"\",\n" + //$NON-NLS-1$
		"	ID: \"\",\n" + //$NON-NLS-1$
		"	version: \"\",\n" + //$NON-NLS-1$
		"	appBuildID: \"\",\n" + //$NON-NLS-1$
		"	platformVersion: \"%platformVersion%\",\n" + //$NON-NLS-1$
		"	platformBuildID: \"%platformBuildID%\"\n" + //$NON-NLS-1$
		"};\n" + //$NON-NLS-1$
		"function NSGetModule(aCompMgr, aFileSpec) {\n" + //$NON-NLS-1$
		"	return XPCOMUtils.generateModule([XULAppInfo]);\n" + //$NON-NLS-1$
		"}\n";	 //$NON-NLS-1$

	private static final String[] SUPPORTED_EXTENSIONS = {
		"{000a9d1c-beef-4f90-9363-039d445309b8}" //$NON-NLS-1$
	};
	private static FirefoxExtensionsSupport instance;
	private File mozillaPath;
	private File profilePath;
	private File tempPath;
	private ArrayList<File> manifests;
	private ArrayList<File> components;
	private ArrayList<File> chromes;
	private ArrayList<File> defaults;

	private class LocationProvider implements IAppFileLocProvider {

		public File getFile(String prop, boolean[] persistent) {
			if (NS_XPCOM_INIT_CURRENT_PROCESS_DIR.equals(prop)
					|| NS_OS_CURRENT_PROCESS_DIR.equals(prop)
					|| NS_XPCOM_CURRENT_PROCESS_DIR.equals(prop)
					|| NS_GRE_DIR.equals(prop)) {
				return mozillaPath;
			} else if (NS_XPCOM_COMPONENT_DIR.equals(prop)
					|| NS_GRE_COMPONENT_DIR.equals(prop)) {
				return new File(mozillaPath, "components"); //$NON-NLS-1$
			} else if (NS_APP_CHROME_DIR.equals(prop)) {
				return new File(mozillaPath, "chrome"); //$NON-NLS-1$
			} else if (NS_APP_PLUGINS_DIR_LIST.equals(prop)) {
				return new File(mozillaPath, "plugins"); //$NON-NLS-1$
			} else if (NS_APP_PREF_DEFAULTS_50_DIR.equals(prop)
					|| NS_APP_CACHE_PARENT_DIR.equals(prop)
					|| NS_APP_USER_PROFILE_50_DIR.equals(prop)
					|| NS_APP_PREFS_50_DIR.equals(prop)) {
				return profilePath;
			} else if (NS_OS_TEMP_DIR.equals(prop)) {
				return tempPath;
			} else if (NS_XPCOM_COMPONENT_REGISTRY_FILE.equals(prop)) {
				return new File(tempPath, COMPREG_FILE);
			} else if (NS_XPCOM_XPTI_REGISTRY_FILE.equals(prop)) {
				return new File(tempPath, XPTI_FILE);
			} else if (NS_OS_HOME_DIR.equals(prop)) {
				return new File(System.getProperty("user.home")); //$NON-NLS-1$
			} else if (NS_APP_LOCALSTORE_50_FILE.equals(prop)) {
				if (profilePath != null) {
					return new File(profilePath, "localstore.rdf"); //$NON-NLS-1$
				}
			} else if (NS_APP_USER_CHROME_DIR.equals(prop)) {
				if (profilePath != null) {
					return new File(profilePath, "chrome"); //$NON-NLS-1$
				}
			} else if (NS_APP_PREFS_50_FILE.equals(prop)) {
				if (profilePath != null) {
					return new File(profilePath, "prefs.js"); //$NON-NLS-1$
				}
			} else if (NS_APP_USER_MIMETYPES_50_FILE.equals(prop)) {
				if (profilePath != null) {
					return new File(profilePath, "mimeTypes.rdf"); //$NON-NLS-1$
				}
			} else if (NS_APP_HISTORY_50_FILE.equals(prop)) {
				if (profilePath != null) {
					return new File(profilePath, "history.dat"); //$NON-NLS-1$
				}
			}
			return null;
		}

		public File[] getFiles(String prop) {
			if (NS_XPCOM_COMPONENT_DIR_LIST.equals(prop) && !components.isEmpty()) {
				return components.toArray(new File[components.size()]);
			} else if (NS_APP_CHROME_DIR_LIST.equals(prop) && !chromes.isEmpty()) {
				return chromes.toArray(new File[chromes.size()]);
			} else if (NS_CHROME_MANIFESTS_FILE_LIST.equals(prop) && !manifests.isEmpty()) {
				return manifests.toArray(new File[manifests.size()]);
			} else if (NS_APP_PREFS_DEFAULTS_DIR_LIST.equals(prop)) {
				return new File[] { new File(mozillaPath, "defaults"+File.separator+"pref") }; //$NON-NLS-1$ //$NON-NLS-2$
			} else if (NS_EXT_PREFS_DEFAULTS_DIR_LIST.equals(prop) && !defaults.isEmpty()) {
				return defaults.toArray(new File[defaults.size()]);
			}
			return null;
		}
	}
				
	/**
	 * 
	 */
	private FirefoxExtensionsSupport() {
		if ("true".equals(System.getProperty(GRE_INITIALIZED))) { //$NON-NLS-1$
			IdeLog.logImportant(Activator.getDefault(), Messages.getString("FirefoxExtensionsSupport.Already_Initialized")); //$NON-NLS-1$
			return;
		}
		String xulrunnerPath = System.getProperty(XULRUNNER_PATH);
		if (xulrunnerPath == null) {
			Bundle bundle = Platform.getBundle(XULRUNNER_PLUGIN+"."+Platform.getWS()+"."+Platform.getOS()+"."+Platform.getOSArch()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (bundle == null) {
				bundle = Platform.getBundle(XULRUNNER_PLUGIN+"."+Platform.getWS()+"."+Platform.getOS()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (bundle == null) {
				bundle = Platform.getBundle(XULRUNNER_PLUGIN+"."+Platform.getWS()); //$NON-NLS-1$
			}
			if (bundle == null) {
				bundle = Platform.getBundle(XULRUNNER_PLUGIN+"."+Platform.getOS()); //$NON-NLS-1$
			}
			if (bundle != null) {
				String bundleVersion = (String) bundle.getHeaders().get(Constants.BUNDLE_VERSION);
				if (bundleVersion.startsWith("1.8.")) { //$NON-NLS-1$
					return;
				}
				URL url = bundle.getEntry("/xulrunner"); //$NON-NLS-1$
				if (url != null) {
					try {
						url = FileLocator.toFileURL(url);
						if (url != null) {
							xulrunnerPath = url.getFile();
						}
					} catch (IOException e) {
						IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxExtensionsSupport.Error_Building_Path"), e); //$NON-NLS-1$
						return;
					}
				}
			}
		}
		if (xulrunnerPath == null) {
			return;
		}
		mozillaPath = new File(xulrunnerPath);
		System.setProperty(XULRUNNER_PATH, mozillaPath.getAbsolutePath());

		manifests = new ArrayList<File>();
		components = new ArrayList<File>();
		chromes = new ArrayList<File>();
		defaults = new ArrayList<File>();

		tempPath = new File(System.getProperty("java.io.tmpdir"), "eclipse_xulrunner"); //$NON-NLS-1$ //$NON-NLS-2$
		String[] tempFiles = new String[] {
				COMPREG_FILE,
				XPTI_FILE,
				XULAPPINFO_FILE,
				"" //$NON-NLS-1$
		};
		if (tempPath.exists()) {
			for (int i = 0; i < tempFiles.length; ++i) {
				File file = new File(tempPath, tempFiles[i]);
				if (file.exists() && !file.delete()) {
					tempPath = new File(tempPath.getParentFile(), tempPath.getName()+System.currentTimeMillis());
					break;
				}
			}
		}
		tempPath.mkdirs();
		for (int i = 0; i < tempFiles.length; ++i) {
			new File(tempPath, tempFiles[i]).deleteOnExit();
		}
				
		createXULAppInfo();
		
		File file = new File(mozillaPath, "chrome"); //$NON-NLS-1$
		if (file.isDirectory()) {
			chromes.add(file);
			manifests.add(file);
		}

		for (Iterator<String> i = loadExtensionsList().iterator(); i.hasNext(); ) {
			String path = i.next();
			file = new File(path, "chrome.manifest"); //$NON-NLS-1$
			if (file.exists()) {
				manifests.add(file);
			}
			file = new File(path, "components"); //$NON-NLS-1$
			if (file.isDirectory()) {
				components.add(file);
			}
			file = new File(path, "chrome"); //$NON-NLS-1$
			if (file.isDirectory()) {
				chromes.add(file);
			}
			file = new File(path, "defaults"+File.separator+"preferences"); //$NON-NLS-1$ //$NON-NLS-2$
			if (file.isDirectory()) {
				defaults.add(file);
			}
		}
		
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			System.load(new File(mozillaPath, "mozcrt19.dll").getAbsolutePath());
		}
		Mozilla.getInstance().initialize(mozillaPath);
		Mozilla.getInstance().initXPCOM(mozillaPath, new LocationProvider());
		System.setProperty(GRE_INITIALIZED, "true"); //$NON-NLS-1$
		
		nsIProperties directoryService = (nsIProperties) Mozilla.getInstance().getServiceManager().getServiceByContractID(
				NS_DIRECTORYSERVICE_CONTRACTID, nsIProperties.NS_IPROPERTIES_IID);
		nsIFile nsFile = (nsIFile) directoryService.get(NS_APP_APPLICATION_REGISTRY_DIR, nsIFile.NS_IFILE_IID);
		if (nsFile != null) {
			profilePath = new File(nsFile.getPath(), "eclipse"); //$NON-NLS-1$
		}
	}
	
	private List<String> loadExtensionsList() {
		List<String> extensions = new ArrayList<String>();
		File profile = FirefoxUtils.findDefaultProfileLocation();
		for (int i = 0; i < SUPPORTED_EXTENSIONS.length; ++i) {
			String extensionID = SUPPORTED_EXTENSIONS[i];
			String value = PlatformUtils.queryRegestryStringValue(
					"HKEY_LOCAL_MACHINE\\SOFTWARE\\Mozilla\\Firefox\\Extensions", //$NON-NLS-1$
					extensionID);
			if (value != null && value.length() > 0) {
				extensions.add(value);
			} else if (profile != null) {
				if (FirefoxUtils.getExtensionVersion(extensionID, profile) != null) {
					extensions.add(new File(new File(profile, "extensions"), extensionID).getAbsolutePath()); //$NON-NLS-1$
				}
			}
		}
		return extensions;
	}
	
	private void createXULAppInfo() {
		String platformVersion = null;
		String platformBuildID = null;
		File platformIni = new File(mozillaPath, "platform.ini"); //$NON-NLS-1$
		if (platformIni.exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(platformIni));
				platformVersion = props.getProperty("Milestone"); //$NON-NLS-1$
				platformBuildID = props.getProperty("BuildID"); //$NON-NLS-1$
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (platformVersion == null) {
			platformVersion = "1.8"; //$NON-NLS-1$
			platformBuildID = ""; //$NON-NLS-1$
		}
		String data = XULAPPINFO_COMPONENT_DATA.
						replaceAll("%platformVersion%", platformVersion). //$NON-NLS-1$
						replaceAll("%platformBuildID%", platformBuildID); //$NON-NLS-1$

		try {
			File file = new File(tempPath, XULAPPINFO_FILE);
			FileWriter writer = new FileWriter(file, false);
			writer.write(data);
			writer.close();
			
			components.add(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	public static void init() {
		if (instance == null) {
			try {
				instance = new FirefoxExtensionsSupport();
			} catch(Exception e) {
				IdeLog.logError(Activator.getDefault(), "Init Firefox extensions support failed", e); //$NON-NLS-1$
			}
		}
	}
}
