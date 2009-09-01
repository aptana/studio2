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
package com.aptana.ide.core.ui.actions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.ConfigurationInfo;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.online.OnlineDetectionService.StatusMode;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.RunDiagnosticActionDelegate;
import com.aptana.ide.core.ui.dialogs.AptanaSignInDialog;

public class SubmitBugAction implements IWorkbenchWindowActionDelegate {

	private static final String FORM_URL = "https://content.aptana.com/aptana/studio/issues/start_issue.php"; //$NON-NLS-1$

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		if (OnlineDetectionService.getInstance().getStatus() == StatusMode.OFFLINE) {
			// user is offline; pops up a warning message
			showOfflineWarning();
			return;
		}
		User user = AptanaUser.getSignedInUser();
		boolean anonymous = false;
		if (user == null || !user.hasCredentials()) {
			// asks user to sign in first
			AptanaSignInDialog dialog = new AptanaSignInDialog(CoreUIUtils
					.getActiveShell(), true);
			int ret = dialog.open();
			if (ret == Dialog.CANCEL) {
				return;
			} else if (ret == IDialogConstants.IGNORE_ID) {
				anonymous = true;
			}
		}

		submitBug(anonymous);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void showOfflineWarning() {
		MessageDialog.openWarning(CoreUIUtils.getActiveShell(),
				Messages.SubmitBugAction_Offline_Title,
				Messages.SubmitBugAction_Offline_Message);
	}

	private void submitBug(boolean anonymous) {
		URLConnection connection;
		try {
			connection = new URL(FORM_URL).openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			DataOutputStream dos = new DataOutputStream(connection
					.getOutputStream());

			// computes the query to post
			StringBuilder content = new StringBuilder();
			if (anonymous) {
				content.append("username=").append("studio"); //$NON-NLS-1$ //$NON-NLS-2$
				content.append("&password=").append("studio101"); //$NON-NLS-1$	//$NON-NLS-2$			
			} else {
				User user = AptanaUser.getSignedInUser();
				content.append("username=").append(encode(user.getUsername())); //$NON-NLS-1$
				content.append("&password=").append(encode(user.getPassword())); //$NON-NLS-1$
			}
			content.append("&build_version=").append( //$NON-NLS-1$
					encode(PluginUtils.getPluginVersion(CoreUIPlugin
							.getDefault())));
			content.append("&log_file=").append(encode(getLogContent())); //$NON-NLS-1$
			content.append("&config_file=").append(encode(getConfigContent())); //$NON-NLS-1$
			content.append("&diagnostic_file=").append( //$NON-NLS-1$
					encode(getDiagnosisContent()));
			content.append("&user_os=").append(encode(getUserOS())); //$NON-NLS-1$
			// posts the query string
			dos.writeBytes(content.toString());
			dos.flush();
			dos.close();

			// reads the response
			BufferedReader input = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			if ((line = input.readLine()) != null) {
				openSubmitDialog(line);
			}
			try {
				input.close();
			} catch (IOException e) {
				// ignores the exception when closing the stream
			}
		} catch (MalformedURLException e) {
			IdeLog.logError(CoreUIPlugin.getDefault(), MessageFormat.format(
					Messages.SubmitBugAction_ERR_Access, FORM_URL), e);
		} catch (IOException e) {
			// likely indicates the user is offline
			showOfflineWarning();
			IdeLog.logInfo(CoreUIPlugin.getDefault(), MessageFormat.format(
					Messages.SubmitBugAction_ERR_Interact, FORM_URL), e);
		}
	}

	private void openSubmitDialog(String url) {
		SubmitBugDialog dialog = new SubmitBugDialog(CoreUIUtils
				.getActiveShell(), url);
		dialog.open();
	}

	private static String encode(String text)
			throws UnsupportedEncodingException {
		// no need to encode when we do a post
		return text;
		//return URLEncoder.encode(text, "UTF-8"); //$NON-NLS-1$
	}

	private static String getLogContent() {
		String logFile = System.getProperty("osgi.logfile"); //$NON-NLS-1$
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(logFile));
			// reads the content and locates the last session
			List<String> lines = new ArrayList<String>();
			String line;
			int index = 0;
			int lastSessionIndex = 0;
			while ((line = input.readLine()) != null) {
				lines.add(line);
				if (line.startsWith("!SESSION")) { //$NON-NLS-1$
					lastSessionIndex = index;
				}
				index++;
			}

			// only returns the logs from last session
			StringBuilder content = new StringBuilder();
			int size = lines.size();
			for (int i = lastSessionIndex; i < size; ++i) {
				content.append(lines.get(i)).append("\n"); //$NON-NLS-1$
			}
			return content.toString();
		} catch (FileNotFoundException e) {
			return "Unable to locate the log file"; //$NON-NLS-1$
		} catch (IOException e) {
			return "IO Exception when reading the log file"; //$NON-NLS-1$
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// ignores the exception when closing the file
				}
			}
		}
	}

	private static String getConfigContent() {
	    String content;
	    try {
	        content = ConfigurationInfo.getSystemSummary();
	    } catch (Exception e) {
	        return e.getLocalizedMessage();
	    }
		StringBuilder shortenedContent = new StringBuilder();
		StringTokenizer tk = new StringTokenizer(content, "\n"); //$NON-NLS-1$
		String line;
		while (tk.hasMoreTokens()) {
			line = tk.nextToken();
			// the osgi.bundles and org.osgi.framework.system.packages
			// properties could be very long, so skips it
			if (line.startsWith("osgi.bundles=") //$NON-NLS-1$
					|| line.startsWith("org.osgi.framework.system.packages=")) { //$NON-NLS-1$
				continue;
			}
			if (line.startsWith("*** User Preferences:")) { //$NON-NLS-1$
				break;
			}
			// reduces the size of the log by leaving out the content from
			// "User Preferences" on
			shortenedContent.append(line).append("\n"); //$NON-NLS-1$
		}
		return shortenedContent.toString();
	}

	private static String getDiagnosisContent() {
		return RunDiagnosticActionDelegate.getLogContent(true);
	}

	private static String getUserOS() {
		String os = System.getProperty("os.name"); //$NON-NLS-1$
		if (os.startsWith("Linux")) { //$NON-NLS-1$
			return "Other Linux"; //$NON-NLS-1$
		}
		if (os.startsWith("Mac OS")) { //$NON-NLS-1$
			return "Mac OS X"; //$NON-NLS-1$
		}
		return os;
	}

}
