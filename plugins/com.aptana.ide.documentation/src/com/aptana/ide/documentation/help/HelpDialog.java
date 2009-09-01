/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.documentation.help;

import java.net.URL;

import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.preferences.IPreferenceConstants;
import com.aptana.ide.documentation.DocumentationPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class HelpDialog extends Window
{

	/**
	 * INTERNAL_PREFIX
	 */
	public static final String INTERNAL_PREFIX = "/com.aptana.ide.documentation/html/gettingstarted/"; //$NON-NLS-1$

	/**
	 * EXTERNAL_PREFIX
	 */
	public static final String EXTERNAL_PREFIX = "http://www.aptana.com/docs/index.php/"; //$NON-NLS-1$

	/**
	 * TOPIC_SERVLET
	 */
	public static final String TOPIC_SERVLET = "/help/nftopic"; //$NON-NLS-1$

	private static HelpDialog dialog;

	private Composite displayArea;
	private Browser browser;

	private HelpDialog()
	{
		super((Shell) null);
		setShellStyle(getDefaultOrientation() | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX | SWT.MIN);
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.HelpDialog_Title);
		newShell.setImage(DocumentationPlugin.getImage("icons/help.png")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = 10;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 10;
		layout.verticalSpacing = 0;
		displayArea.setLayout(layout);
		GridData daData = new GridData(SWT.FILL, SWT.FILL, true, true);
		daData.widthHint = Math.min(1100, parent.getMonitor().getClientArea().width - 200);
		daData.heightHint = Math.min(768, parent.getMonitor().getClientArea().height - 200);
		displayArea.setLayoutData(daData);
		browser = new Browser(displayArea, SWT.BORDER);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return displayArea;
	}

	/**
	 * Gets the help dialog
	 * 
	 * @return - help system interface
	 */
	public static HelpDialog getHelp()
	{
		if (dialog == null || dialog.isDisposed())
		{
			dialog = new HelpDialog();
		}
		return dialog;
	}

	/**
	 * True if the help system dialog is disposed
	 * 
	 * @return - true if disposed
	 */
	public boolean isDisposed()
	{
		return displayArea == null || displayArea.isDisposed();
	}

	private URL getHelpSystemURL(String topic)
	{
		return BaseHelpSystem.resolve(INTERNAL_PREFIX + topic + ".html", TOPIC_SERVLET); //$NON-NLS-1$
	}

	private String getWikiURL(String topic)
	{
		// 
		return EXTERNAL_PREFIX + topic + "?studio=true"; //$NON-NLS-1$
	}

	/**
	 * Shows a topic in the help dialog
	 * 
	 * @param topic
	 */
	public void showTopic(String topic)
	{
		showHelp(!AptanaCorePlugin.getDefault().getPluginPreferences().getBoolean(IPreferenceConstants.SHOW_LIVE_HELP),
				topic);
	}

	/**
	 * Show help topic
	 * 
	 * @param internal
	 * @param topic
	 */
	public void showHelp(boolean internal, String topic)
	{
		if (isDisposed())
		{
			open();
		}
		String url = null;
		if (!internal)
		{
			url = getWikiURL(topic);
		}
		else
		{
			String external = getHelpSystemURL(topic).toExternalForm();
			if (external == null)
			{
				external = getWikiURL(topic);
			}
			url = external;
		}
		if (url != null)
		{
			super.getShell().setMinimized(false);
			super.getShell().forceActive();
			browser.setUrl(url);
		}
	}

}
