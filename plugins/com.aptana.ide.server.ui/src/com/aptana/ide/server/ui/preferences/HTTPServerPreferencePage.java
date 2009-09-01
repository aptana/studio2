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

package com.aptana.ide.server.ui.preferences;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.SocketUtil;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IHttpLaunchConfigurationConstants;
import com.aptana.ide.server.core.ServerCorePlugin;

/**
 * @author Max Stepanov
 *
 */
public class HTTPServerPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final Pattern PORTS_PATTERN = Pattern.compile("^(\\d+)(-(\\d+))?$"); //$NON-NLS-1$
	
	private Preferences store;
	
	private Combo address;
	private Text ports;

	/**
	 * 
	 */
	public HTTPServerPreferencePage() {
		super();
		store = getPreferences();
	}

	/**
	 * @param title
	 */
	public HTTPServerPreferencePage(String title) {
		super(title);
		store = getPreferences();
	}

	/**
	 * @param title
	 * @param image
	 */
	public HTTPServerPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		store = getPreferences();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.HTTPServerPreferencePage_BuiltinHTTPServer);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.HTTPServerPreferencePage_IPAddress);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		address = new Combo(group, SWT.READ_ONLY);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		data.widthHint = 140;
		address.setLayoutData(data);
		InetAddress[] addresses = SocketUtil.getLocalAddresses();
		for(int i = 0; i < addresses.length; ++i) {
			address.add(addresses[i].getHostAddress());
		}
		
		label = new Label(group, SWT.NONE);
		label.setText(Messages.HTTPServerPreferencePage_PortsLabel);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		ports = new Text(group, SWT.BORDER);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		data.widthHint = 100;
		ports.setLayoutData(data);
		
		ports.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String message = Messages.HTTPServerPreferencePage_EnterValidPort;
				Matcher matcher = PORTS_PATTERN.matcher(((Text)e.widget).getText());
				if ( matcher.matches() ) {
					try {
						int start = Integer.parseInt(matcher.group(1));
						if ( matcher.group(2) != null ) {
							int end = Integer.parseInt(matcher.group(3));
							if ( start < end ) {
								message = null;
							}
						} else {
							message = null;
						}
					} catch (NumberFormatException e1) {
					}
				}
				setErrorMessage(message);
				setValid(message == null);
			}			
		});
		
		setInitialValues();
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * getPreferences
	 * 
	 * @return Preferences
	 */
	protected Preferences getPreferences()
	{
		return ServerCorePlugin.getDefault().getPluginPreferences();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		setDefaultValues();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		setValues();
		return super.performOk();
	}
	
	private void setAddressValue(String value) {
		String[] items = address.getItems();
		address.deselectAll();
		for(int i = 0; i < items.length; ++i) {
			if(items[i].equals(value))
			{
				address.select(i);
				break;
			}
		}		
	}

	private void setInitialValues()
	{
		setAddressValue(store.getString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_ADDRESS));
		ports.setText(store.getString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_PORTS));
	}

	private void setDefaultValues()
	{
		setAddressValue(store.getDefaultString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_ADDRESS));
		ports.setText(store.getDefaultString(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_PORTS));
	}
	
	private void setValues()
	{
		String addr = StringUtils.EMPTY;
		if(address.getSelectionIndex() >= 0) {
			addr = address.getItem(address.getSelectionIndex());
		}
		store.setValue(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_ADDRESS, addr);
		store.setValue(IHttpLaunchConfigurationConstants.PREF_HTTP_SERVER_PORTS, ports.getText());
		ServerCorePlugin.getDefault().savePluginPreferences();
	}
}
