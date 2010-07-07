/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.ui.WorkbenchHelper;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SubmitBugDialog extends Window implements LocationListener {

	private static final String TITLE = Messages.SubmitBugDialog_Title;
	private static final String CANCEL_URL = "https://content.aptana.com/aptana/studio/issues/cancel.php"; //$NON-NLS-1$

	private Browser browser;
	private String initialUrl;

	/**
	 * @param parentShell
	 *            the parent shell
	 * @param url
	 *            the initial URL
	 */
	public SubmitBugDialog(Shell parentShell, String url) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.DIALOG_TRIM);
		this.initialUrl = url;
	}

	/**
	 * @see org.eclipse.swt.browser.LocationListener#changed(org.eclipse.swt.browser.LocationEvent)
	 */
	public void changed(LocationEvent event) {
	}

	/**
	 * @see org.eclipse.swt.browser.LocationListener#changing(org.eclipse.swt.browser.LocationEvent)
	 */
	public void changing(LocationEvent event) {
		String location = event.location;
		if (location.matches("https?://support\\.aptana\\.com.*")) { //$NON-NLS-1$
			event.doit = false;
			WorkbenchHelper.launchBrowser(location);
		} else if (location.startsWith(CANCEL_URL)) {
			event.doit = false;
			close();
		}
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(TITLE);
	}

	/**
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		browser = new Browser(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 500;
		gridData.heightHint = 630;
		browser.setLayoutData(gridData);
		browser.setUrl(initialUrl);
		browser.addLocationListener(this);

		return browser;
	}

}
