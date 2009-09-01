package com.aptana.ide.desktop.integration.protocolhandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PlatformUtils;
import com.aptana.ide.core.SetExecutableBits;
import com.aptana.ide.desktop.integration.protocolhandler.preferences.IPreferenceConstants;

public class ProtocolHandlerStartup implements IStartup {

    private static final String ECLIPSE_LAUNCHER_TOKEN = Pattern.quote("@eclipse.launcher@"); //$NON-NLS-1$

	public void earlyStartup() {
		// Register protocol handler on the first run only
		IPreferenceStore prefs = getPluginActivator().getPreferenceStore();
    	if (prefs
                .getBoolean(IPreferenceConstants.APTANA_PLUGIN_INSTALLER_PROTOCOL_HANDLER_REGISTERED)
                && !Boolean.getBoolean("ALWAYS_REGISTER_PROTOCOL")) { //$NON-NLS-1$
            return;
        }
    	// Mark it as registered - only one attempt?
        prefs.setValue(IPreferenceConstants.APTANA_PLUGIN_INSTALLER_PROTOCOL_HANDLER_REGISTERED, true);

		if (Platform.OS_WIN32.equals(Platform.getOS())) {
		    registerOnWindows();
        } else if (Platform.OS_LINUX.equals(Platform.getOS())) {
            registerOnLinux();
		} else if (Platform.OS_MACOSX.equals(Platform.getOS())) {
		    registerOnMac();
        }
	}

	public static void registerOnWindows() {
        File protocolhandlerFolder = getProtocolHandlerFolder();
        if (protocolhandlerFolder == null) {
            return;
        }

        String eclipseLauncher = getEclipseLauncherPath();
        if (eclipseLauncher == null) {
            return;
        }

        eclipseLauncher = Matcher.quoteReplacement(eclipseLauncher);
        File templateFile = new File(protocolhandlerFolder, "aptanaplugininstaller.reg.template"); //$NON-NLS-1$
        if (templateFile.exists()) {
            try {
                File regFile = File.createTempFile("aptanaplugininstaller", ".reg"); //$NON-NLS-1$ //$NON-NLS-2$

                IdeLog
                        .logInfo(
                                getPluginActivator(),
                                "aptanaplugininstaller: protocol registration file: " + regFile); //$NON-NLS-1$
                try {
                    // Now copy from templateFile to regFile replacing @ECLIPSE.LAUNCHER@ with Product launcher path
                    BufferedReader br = new BufferedReader(new FileReader(templateFile));
                    PrintWriter pr = new PrintWriter(new FileWriter(regFile));
                    try {
                        String aLine;
                        while ((aLine = br.readLine()) != null) {
                            aLine = aLine.replaceAll(ECLIPSE_LAUNCHER_TOKEN, eclipseLauncher);
                            pr.println(aLine);
                        }
                    } finally {
                        pr.flush();
                        pr.close();                     
                        br.close();
                    }
                    
                    if (PlatformUtils.isUserAdmin()) {
                        ProcessBuilder processBuilder = new ProcessBuilder();
                        processBuilder.command(
                                "cmd"                       //$NON-NLS-1$
                                ,"/C"                       //$NON-NLS-1$
                                ,"regedit.exe"              //$NON-NLS-1$
                                ,"/s"                       //$NON-NLS-1$
                                ,regFile.getAbsolutePath()
                        );
                        
                        IdeLog.logInfo(getPluginActivator(), "Running : " + processBuilder); //$NON-NLS-1$
                        
                        Process process = processBuilder.start();
                        try {
                            int exitStatus = process.waitFor();
                            if (exitStatus != 0) {
                                IdeLog
                                        .logError(
                                                getPluginActivator(),
                                                processBuilder.toString()
                                                        + " Exist status : " + exitStatus); //$NON-NLS-1$
                            }
                        } catch (InterruptedException e) {
                            IdeLog.logError(getPluginActivator(), e.getMessage(), e);
                        }
                    } else {
                        PlatformUtils.runAsAdmin("cmd",     //$NON-NLS-1$
                                new String[] {
                                "/C"                        //$NON-NLS-1$
                                ,"regedit.exe"              //$NON-NLS-1$
                                ,"/s"                       //$NON-NLS-1$
                                ,regFile.getAbsolutePath()});
                    }
                } finally {
                    regFile.deleteOnExit();
                }
            } catch (IOException e) {
                IdeLog.logError(getPluginActivator(), e.getMessage(), e);
            }
        }
	}

	public static void registerOnLinux() {
        File protocolhandlerFolder = getProtocolHandlerFolder();
        if (protocolhandlerFolder == null) {
            return;
        }

        String eclipseLauncher = getEclipseLauncherPath();
        if (eclipseLauncher == null) {
            return;
        }

        File templateFile = new File(protocolhandlerFolder, "registeraptanaplugininstall.sh.template"); //$NON-NLS-1$
        if (templateFile.exists()) {
            try {
                File regFile = File.createTempFile("registeraptanaplugininstall", ".sh"); //$NON-NLS-1$ //$NON-NLS-2$

                IdeLog.logInfo(getPluginActivator(), "aptanaplugininstaller: protocol registration file: " + regFile); //$NON-NLS-1$
                try {
                    // Now copy from templateFile to regFile replacing @ECLIPSE.LAUNCHER@ with Product launcher path
                    BufferedReader br = new BufferedReader(new FileReader(templateFile));
                    PrintWriter pr = new PrintWriter(new FileWriter(regFile));
                    try {
                        String aLine;
                        while ((aLine = br.readLine()) != null) {
                            aLine = aLine.replaceAll(ECLIPSE_LAUNCHER_TOKEN, eclipseLauncher);
                            pr.println(aLine);
                        }
                    } finally {
                        pr.flush();
                        pr.close();
                        br.close();
                    }

                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(
                            "/bin/bash"                                           //$NON-NLS-1$
                            , "--norc"                                            //$NON-NLS-1$
                            , "--noprofile"                                       //$NON-NLS-1$
                            ,regFile.getAbsolutePath()
                    );
                    
                    IdeLog.logInfo(getPluginActivator(), "Running : " + processBuilder); //$NON-NLS-1$
                    
                    Process process;
                    try {
                        process = processBuilder.start();
                        int exitStatus = process.waitFor();
                        if (exitStatus != 0) {
                            IdeLog.logError(getPluginActivator(), processBuilder.toString() + " Exist status : " + exitStatus); //$NON-NLS-1$
                        }
                    } catch (InterruptedException e) {
                        IdeLog.logError(getPluginActivator(), e.getMessage(), e);
                    } catch (IOException e) {
                        IdeLog.logError(getPluginActivator(), e.getMessage(), e);
                    }
                } finally {
                    regFile.deleteOnExit();
                }
            } catch (IOException e) {
                IdeLog.logError(getPluginActivator(), e.getMessage(), e);
            }
        }
	}

	public static void registerOnMac() {
        File protocolhandlerFolder = getProtocolHandlerFolder();
        if (protocolhandlerFolder == null) {
            return;
        }

        String eclipseLauncher = getEclipseLauncherPath();
        if (eclipseLauncher == null) {
            return;
        }

        // unjar the protocol handler app
        File protocolApp = new File(protocolhandlerFolder,
                "AptanaStudio.app.jar"); //$NON-NLS-1$
        IdeLog.logInfo(getPluginActivator(), MessageFormat.format(
                Messages.ProtocolHandlerStartup_INF_UnjarringApp, protocolApp));
        JarInputStream input = null;
        try {
            input = new JarInputStream(new FileInputStream(protocolApp));

            // unjar the content
            File contentFile;
            JarEntry entry;
            byte[] b = new byte[10000];
            int nread;
            while ((entry = input.getNextJarEntry()) != null) {
                contentFile = new File(protocolhandlerFolder, entry.toString());
                // creates the parent directory if it does not exist
                // yet
                if (!contentFile.getParentFile().exists()) {
                    contentFile.getParentFile().mkdirs();
                }

                if (entry.isDirectory()) {
                    contentFile.mkdir();
                } else {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(contentFile);
                        while ((nread = input.read(b, 0, b.length)) >= 0) {
                            out.write(b, 0, nread);
                        }
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            IdeLog.logError(getPluginActivator(), e.getMessage(), e);
        } catch (IOException e) {
            IdeLog.logError(getPluginActivator(), e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        IdeLog.logInfo(getPluginActivator(), MessageFormat.format(
                Messages.ProtocolHandlerStartup_INF_FinishedUnJar, protocolApp));

        // modifies the proper executable permissions
        (new SetExecutableBits(ProtocolHandlerActivator.getDefault().getBundle())).schedule();

        File templateFile = new File(protocolhandlerFolder, "AptanaStudio.app/Contents/Resources/Scripts/API.sh.tmpl"); //$NON-NLS-1$
        if (templateFile.exists()) {
            try {
                File regFile = new File(protocolhandlerFolder, "AptanaStudio.app/Contents/Resources/Scripts/AptanaPluginInstaller.sh"); //$NON-NLS-1$
    
                IdeLog.logInfo(getPluginActivator(), "aptanaplugininstaller: protocol launcher file: " + regFile); //$NON-NLS-1$
                // Now copy from templateFile to regFile replacing @ECLIPSE.LAUNCHER@ with Product launcher path
                BufferedReader br = new BufferedReader(new FileReader(templateFile));
                PrintWriter pr = new PrintWriter(new FileWriter(regFile));
                try {
                    String aLine;
                    while ((aLine = br.readLine()) != null) {
                        aLine = aLine.replaceAll(ECLIPSE_LAUNCHER_TOKEN, eclipseLauncher);
                        pr.println(aLine);
                    }
                } finally {
                    pr.flush();
                    pr.close();                     
                    br.close();
                }
            } catch (IOException e) {
                IdeLog.logError(getPluginActivator(), e.getMessage(), e);
            }
        }
        
        templateFile = new File(protocolhandlerFolder, "AptanaStudio.app/Contents/Resources/Scripts/AOF.sh.tmpl"); //$NON-NLS-1$
        if (templateFile.exists()) {
            try {
                File regFile = new File(protocolhandlerFolder, "AptanaStudio.app/Contents/Resources/Scripts/AptanaOpenFiles.sh"); //$NON-NLS-1$
    
                IdeLog.logInfo(getPluginActivator(), "file open handler file: " + regFile); //$NON-NLS-1$
                // Now copy from templateFile to regFile replacing @ECLIPSE.LAUNCHER@ with Product launcher path
                BufferedReader br = new BufferedReader(new FileReader(templateFile));
                PrintWriter pr = new PrintWriter(new FileWriter(regFile));
                try {
                    String aLine;
                    while ((aLine = br.readLine()) != null) {
                        aLine = aLine.replaceAll(ECLIPSE_LAUNCHER_TOKEN, eclipseLauncher);
                        pr.println(aLine);
                    }
                } finally {
                    pr.flush();
                    pr.close();                     
                    br.close();
                }
            } catch (IOException e) {
                IdeLog.logError(getPluginActivator(), e.getMessage(), e);
            }
        }
	}

    private static File getProtocolHandlerFolder() {
        File protocolhandlerFolder = null; 
        URL protocolhandlerEntry = ProtocolHandlerActivator.getDefault().getBundle().getEntry("/protocolhandler"); //$NON-NLS-1$
        try {
            if (protocolhandlerEntry != null) {
                String protocolhandlerEntryPath = FileLocator.toFileURL(protocolhandlerEntry).getFile();
                if (protocolhandlerEntryPath != null) {
                    protocolhandlerFolder = new File(protocolhandlerEntryPath);
                }
            }
        } catch (IOException el) {
        }
        if (protocolhandlerFolder == null || (protocolhandlerFolder.exists() && (!protocolhandlerFolder.isDirectory()))) {
            IdeLog.logError(getPluginActivator(), Messages.ProtocolHandlerStartup_ERR_CannotLocateProtocolhandler);
            return null;
        }
        return protocolhandlerFolder;
    }

    private static String getEclipseLauncherPath() {
        String eclipseLauncher = System.getProperty("eclipse.launcher"); //$NON-NLS-1$
        if (eclipseLauncher == null) {
            IdeLog.logError(getPluginActivator(), Messages.ProtocolHandlerStartup_ERR_CannotLocateLauncher);
            return null;
        }
        return Matcher.quoteReplacement(eclipseLauncher);
    }

    private static ProtocolHandlerActivator getPluginActivator() {
        return ProtocolHandlerActivator.getDefault();
    }
}
