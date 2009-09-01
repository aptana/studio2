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
package com.aptana.ide.server.ui.generic.dialogs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.generic.GenericServer;
import com.aptana.ide.server.generic.GenericServerTypeDelegate;
import com.aptana.ide.server.ui.IConfigurationDialog;
import com.aptana.ide.server.ui.views.ServerLabelProvider;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class GenericConfigurationDialog extends TitleAreaDialog implements IConfigurationDialog
{

	private boolean isEdit;
	private IAbstractConfiguration configuration;
	private Composite displayArea;
	private Label serverNameLabel;
	private Text serverNameText;
	private Text healthURLText;
	private Text pollingIntervalText;
	private HashSet<String> serverNames = new HashSet<String>();

	/**
	 * Remote jaxer section
	 */
	private Label hostnameLabel;
	private Text hostnameText;
	private Label portLabel;
	private Text portText;
	private Label serverDescLabel;
	private Text serverDescText;
	private Text startText;
	private Text stopText;
	private Text pauseText;
	private Text resumeText;
	private Text path;
	private Button isLocal;
	private IServer server;
	private Text log;
	private Text docRootText;
	private ModifyListener validationModifyListener;

	/**
	 * Default jaxer configuration dialog constructor
	 */
	public GenericConfigurationDialog()
	{
		super(Display.getDefault().getActiveShell());
		validationModifyListener = new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "com.aptana.ide.server.ui.servers_add_generic"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		this.setTitle(Messages.GenericConfigurationDialog_TITLE);
		this.getShell().setText(Messages.GenericConfigurationDialog_SHELL_TITLE);
		this.setMessage(Messages.GenericConfigurationDialog_GENERIC_);
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(3, false);
		daLayout.marginHeight = 10;
		daLayout.marginWidth = 10;
		displayArea.setLayout(daLayout);
		GridData daData = new GridData(SWT.FILL, SWT.FILL, true, true);
		daData.widthHint = 550;
		displayArea.setLayoutData(daData);
		serverNameLabel = new Label(displayArea, SWT.LEFT);
		serverNameLabel.setText(Messages.GenericConfigurationDialog_SERVER_NAME);
		serverNameText = new Text(displayArea, SWT.BORDER | SWT.SINGLE);
		GridData sntData = new GridData(SWT.FILL, SWT.FILL, true, false);
		sntData.horizontalSpan = 2;
		serverNameText.setLayoutData(sntData);
		if (this.configuration != null)
		{
			serverNameText.setText(configuration.getStringAttribute(IServer.KEY_NAME));
		}
		serverNameText.addModifyListener(validationModifyListener);
		serverDescLabel = new Label(displayArea, SWT.LEFT);
		serverDescLabel.setText(Messages.GenericConfigurationDialog_SERVER_DESCRIPTION);
		serverDescText = new Text(displayArea, SWT.BORDER | SWT.SINGLE);
		if (this.configuration != null)
		{
			serverDescText.setText(configuration.getStringAttribute(IServer.KEY_DESCRIPTION));
		}

		GridData sndData = new GridData(SWT.FILL, SWT.FILL, true, false);
		sndData.horizontalSpan = 2;
		serverDescText.setLayoutData(sndData);

		hostnameLabel = new Label(displayArea, SWT.LEFT);
		hostnameLabel.setText(Messages.GenericConfigurationDialog_5);
		hostnameText = new Text(displayArea, SWT.SINGLE | SWT.BORDER);
		if (this.configuration != null)
		{
			String path = this.configuration.getStringAttribute(IServer.KEY_HOST);
			hostnameText.setText(path);
		}
		hostnameText.addModifyListener(validationModifyListener);
		GridData htData = new GridData(SWT.FILL, SWT.FILL, true, false);
		htData.horizontalSpan = 2;
		hostnameText.setLayoutData(htData);

		portLabel = new Label(displayArea, SWT.LEFT);
		portLabel.setText(Messages.GenericConfigurationDialog_HOSTNAME);
		portText = new Text(displayArea, SWT.SINGLE | SWT.BORDER);
		String port = this.configuration.getStringAttribute(IServer.KEY_PORT);
		portText.setText(port);
		portText.addModifyListener(validationModifyListener);
		GridData ptData = new GridData(SWT.FILL, SWT.FILL, true, false);
		ptData.horizontalSpan = 2;
		portText.setLayoutData(ptData);
		log = createOpenExecutable(Messages.GenericConfigurationDialog_PATH_TO_LOG_FILE, displayArea,
				IServer.KEY_LOG_PATH);

		Composite heartbeatComp = new Composite(displayArea, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		heartbeatComp.setLayout(layout);
		GridData hbcData = new GridData(SWT.FILL, SWT.FILL, true, false);
		hbcData.horizontalSpan = 3;
		heartbeatComp.setLayoutData(hbcData);

		Label hbLabel = new Label(heartbeatComp, SWT.NONE);
		hbLabel.setText(Messages.GenericConfigurationDialog_LBL_Heartbeat);
		healthURLText = new Text(heartbeatComp, SWT.SINGLE | SWT.BORDER);
		GridData hutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		healthURLText.setLayoutData(hutData);
		if (configuration != null)
		{
			healthURLText.setText(configuration.getStringAttribute(GenericServerTypeDelegate.HEALTH_URL));
		}
		healthURLText.addModifyListener(validationModifyListener);

		Composite heartbeatComp2 = new Composite(heartbeatComp, SWT.NONE);
		heartbeatComp2.setLayout(new GridLayout(2, false));
		heartbeatComp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		Label pollingLabel = new Label(heartbeatComp2, SWT.LEFT);
		pollingLabel.setText(Messages.GenericConfigurationDialog_LBL_Polling);
		pollingIntervalText = new Text(heartbeatComp2, SWT.SINGLE | SWT.BORDER);
		GridData pollData = new GridData(SWT.FILL, SWT.FILL, false, false);
		pollData.widthHint = 30;
		pollingIntervalText.setLayoutData(pollData);
		int interval = configuration.getIntAttribute(GenericServerTypeDelegate.POLLING_INTERVAL);
		if (interval > 0)
		{
			pollingIntervalText.setText(Integer.toString(interval / 1000));
		}
		else
		{
			pollingIntervalText.setText("30"); //$NON-NLS-1$
		}
		pollingIntervalText.addModifyListener(validationModifyListener);

		new Label(heartbeatComp, SWT.NONE);
		Label healthBanner = new Label(heartbeatComp, SWT.NONE);
		GridData hbData = new GridData();
		hbData.verticalIndent = -5;
		hbData.horizontalSpan = 2;
		healthBanner.setLayoutData(hbData);
		healthBanner.setText(Messages.GenericConfigurationDialog_LBL_Banner);
		final Font smallFont = SWTUtils.getDefaultSmallFont();
		healthBanner.setFont(smallFont);

		isLocal = new Button(displayArea, SWT.CHECK);
		ptData = new GridData(SWT.FILL, SWT.FILL, true, false);
		ptData.verticalIndent = 10;
		ptData.horizontalSpan = 3;
		isLocal.setText(Messages.GenericConfigurationDialog_IS_SERVER_LOCAL);
		isLocal.setLayoutData(ptData);
		path = createOpenExecutable(Messages.GenericConfigurationDialog_PATH, displayArea, IServer.KEY_PATH);
		docRootText = createDirecotrySelectionText(Messages.GenericConfigurationDialog_DOCUMENT_ROOT, displayArea,
				IServer.KEY_DOCUMENT_ROOT);
		startText = createText(Messages.GenericConfigurationDialog_START_SERVER, displayArea,
				GenericServerTypeDelegate.START_SERVER_COMMAND);
		stopText = createText(Messages.GenericConfigurationDialog_STOP_SERVER, displayArea,
				GenericServerTypeDelegate.STOP_SERVER_COMMAND);
		pauseText = createText(Messages.GenericConfigurationDialog_PAUSE_SERVER, displayArea,
				GenericServerTypeDelegate.PAUSE_SERVER_COMMAND);
		resumeText = createText(Messages.GenericConfigurationDialog_RESUME_SERVER, displayArea,
				GenericServerTypeDelegate.PAUSE_SERVER_COMMAND);
		isLocal.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				checkLocal();
				validate();
			}
		});
		if (configuration != null)
		{
			isLocal.setSelection(configuration.getBooleanAttribute(GenericServerTypeDelegate.IS_LOCAL));
		}
		checkLocal();
		return displayArea;
	}

	/**
	 * @param label
	 * @param parent
	 * @param key
	 * @return new Text
	 */
	protected Text createText(String label, Composite parent, String key)
	{
		Label ll = new Label(parent, SWT.NONE);
		ll.setText(label);
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData ptData = new GridData(SWT.FILL, SWT.FILL, true, false);
		ptData.horizontalSpan = 2;
		t.setLayoutData(ptData);
		if (configuration != null)
		{
			t.setText(configuration.getStringAttribute(key));
		}
		t.addModifyListener(validationModifyListener);
		return t;
	}

	/**
	 * Creates a direcoty selection area with a label, browse button and a text. The initial value to fill in that text
	 * is taken by the given key.
	 * 
	 * @param parent
	 */
	protected Text createDirecotrySelectionText(String label, Composite parent, String key)
	{
		// Add the document root text field
		Label l = new Label(parent, SWT.NONE);
		l.setText(label);
		final Text text = new Text(parent, SWT.BORDER);
		if (configuration != null)
		{
			String value = configuration.getStringAttribute(key);
			if (value != null)
			{
				text.setText(value);
			}
		}
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(validationModifyListener);
		Button browseBt = new Button(parent, SWT.PUSH);
		browseBt.setText(Messages.GenericConfigurationDialog_BROWSE);
		browseBt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				String path = dialog.open();
				if (path != null)
				{
					text.setText(path);
				}
			}
		});
		// Set the browse button as the Text data so we will be able to disable it easily when the Text is
		// disabled. This is a hack and we should think of a nicer way to do that.
		text.setData("browse", browseBt);// $NON-NLS-1$ //$NON-NLS-1$
		return text;
	}

	/**
	 * @param label
	 * @param parent
	 * @param key
	 * @return new text
	 */
	protected Text createOpenExecutable(String label, Composite parent, String key)
	{
		Label ll = new Label(parent, SWT.NONE);
		ll.setText(label);
		final Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData ptData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		ptData.horizontalSpan = 1;
		t.setLayoutData(ptData);
		if (configuration != null)
		{
			t.setText(configuration.getStringAttribute(key));
		}
		t.addModifyListener(validationModifyListener);

		Button browser = new Button(parent, SWT.PUSH);
		browser.setText(Messages.GenericConfigurationDialog_BROWSE);
		browser.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fdlg = new FileDialog(getShell(), SWT.OPEN);
				String text = t.getText();
				if (text.length() > 0)
				{
					File file = new File(text);
					File parentFile = file.getParentFile();
					if (parentFile != null)
					{
						fdlg.setFilterPath(parentFile.getPath());
					}
				}
				String open = fdlg.open();
				if (open != null)
				{
					t.setText(open);
				}
			}
		});
		// Set the browse button as the Text data so we will be able to disable it easily when the Text is
		// disabled. This is a hack and we should think of a nicer way to do that.
		t.setData("browse", browser);// $NON-NLS-1$ //$NON-NLS-1$
		return t;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Control c = super.createContents(parent);
		initDublicateNames();
		validate();
		return c;
	}

	private void initDublicateNames()
	{
		IServer[] servers = ServerCore.getServerManager().getServers();

		for (IServer s : servers)
		{
			if (!s.getId().equals(configuration.getStringAttribute(IServer.KEY_ID)))
			{
				serverNames.add(s.getName());
			}
		}
	}

	private void validate()
	{
		String name = serverNameText.getText();
		String hostname = hostnameText.getText();
		Button ok = getButton(IDialogConstants.OK_ID);
		String port = portText.getText();
		boolean error = false;
		if (name.trim().length() == 0)
		{
			setErrorMessage(Messages.GenericConfigurationDialog_SERVER_NAME_MUST_NOT_BE_BLANK);
			error = true;
			ok.setEnabled(false);
		}
		else if (serverNames.contains(name))
		{
			setErrorMessage(Messages.GenericConfigurationDialog_SERVER_NAME_EXISTS);
			error = true;
		}
		else if (hostname.trim().length() == 0)
		{
			setErrorMessage(Messages.GenericConfigurationDialog_HOST_NAME_MUST_NOT_BE_BLANK);
			error = true;
		}
		if (!error)
		{
			try
			{
				Integer i = Integer.parseInt(port);
				if (i < 0 || i > 65535)
				{
					setErrorMessage(Messages.GenericConfigurationDialog_PORT_MUST_BE_IN_RANGE);
					error = true;
				}
			}
			catch (NumberFormatException e)
			{
				setErrorMessage(Messages.GenericConfigurationDialog_PORT_MUST_BE_INTEGER);
				error = true;
			}
		}
		if (!error && isLocal.getSelection())
		{
			File file = new File(path.getText());
			if (file.exists())
			{
				if (file.isFile())
				{
					error = false;

					if (startText.getText().length() == 0)
					{
						setErrorMessage(Messages.GenericConfigurationDialog_START_IS_EMPTY);
						error = true;
					}
					else if (stopText.getText().length() == 0)
					{
						setErrorMessage(Messages.GenericConfigurationDialog_STOP_IS_EMPTY);
						error = true;
					}
					else if (log.getText().length() != 0)
					{
						file = new File(log.getText());
						if (!file.exists() || !file.isFile())
						{
							setErrorMessage(Messages.GenericConfigurationDialog_PATH_TO_LOG_FILE_SHOULD_BE_EMPTY_OR_POINT_TO_FILE);
							error = true;
						}
					}
				}
				else
				{
					error = true;
					setErrorMessage(Messages.GenericConfigurationDialog_PATH_SHOULD_NOT_POINT_TO_DIRECTORY);
				}
			}
			else
			{
				error = true;
				setErrorMessage(Messages.GenericConfigurationDialog_NO_FILE_UNDER_A_GIVEN_PATH);
			}
			if (!error)
			{
				String docRootPath = docRootText.getText().trim();
				if (docRootPath.length() == 0 && !new File(docRootPath).isDirectory())
				{
					error = true;
					setErrorMessage(Messages.GenericConfigurationDialog_DOCUMENT_ROOT_ERROR);
				}
			}
		}
		if (!error)
		{
			if (healthURLText.getText().trim().length() > 0)
			{
				try
				{
					new URL(healthURLText.getText().trim());
				}
				catch (MalformedURLException e)
				{
					error = true;
					setErrorMessage(Messages.GenericConfigurationDialog_MSG_InvalidHeartbeat);
				}
				if (!error)
				{
					try
					{
						Integer i = Integer.parseInt(pollingIntervalText.getText());
						if (i < 1)
						{
							setErrorMessage(Messages.GenericConfigurationDialog_MSG_PollingBound);
							error = true;
						}
					}
					catch (NumberFormatException e)
					{
						setErrorMessage(Messages.GenericConfigurationDialog_MSG_PollingInteger);
						error = true;
					}
				}
			}
		}
		if (!error)
		{
			boolean allowed = false;
			if (server instanceof GenericServer)
			{
				boolean isLocal = configuration.getBooleanAttribute(GenericServerTypeDelegate.IS_LOCAL);
				String healthURL = configuration.getStringAttribute(GenericServerTypeDelegate.HEALTH_URL);
				int pollingInterval = configuration.getIntAttribute(GenericServerTypeDelegate.POLLING_INTERVAL);
				// Allow edits of running servers that are just polling
				if (!isLocal && healthURL != null && healthURL.length() > 0 && pollingInterval > 0)
				{
					allowed = true;
				}
			}
			if (server != null)
			{
				if (!allowed
						&& !(server.getServerState() == IServer.STATE_STOPPED
								|| server.getServerState() == IServer.STATE_UNKNOWN || server.getServerState() == IServer.STATE_NOT_APPLICABLE))
				{
					setErrorMessage(ServerLabelProvider.SERVER_IS_RUNNING_NO_EDIT);
					ok.setEnabled(false);
					return;
				}
			}
			setErrorMessage(null);
		}
		ok.setEnabled(!error);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		configuration.setStringAttribute(IServer.KEY_NAME, serverNameText.getText());
		configuration.setStringAttribute(IServer.KEY_DESCRIPTION, serverDescText.getText());
		configuration.setStringAttribute(IServer.KEY_HOST, hostnameText.getText());
		configuration.setIntAttribute(IServer.KEY_PORT, Integer.parseInt(portText.getText()));
		configuration.setStringAttribute(IServer.KEY_PATH, path.getText());
		configuration.setStringAttribute(GenericServerTypeDelegate.START_SERVER_COMMAND, startText.getText());
		configuration.setStringAttribute(GenericServerTypeDelegate.STOP_SERVER_COMMAND, stopText.getText());
		configuration.setStringAttribute(GenericServerTypeDelegate.PAUSE_SERVER_COMMAND, pauseText.getText());
		configuration.setStringAttribute(GenericServerTypeDelegate.RESUME_SERVER_COMMAND, resumeText.getText());
		configuration.setBooleanAttribute(GenericServerTypeDelegate.IS_LOCAL, isLocal.getSelection());
		configuration.setStringAttribute(IServer.KEY_LOG_PATH, log.getText());
		configuration.setStringAttribute(GenericServerTypeDelegate.HEALTH_URL, healthURLText.getText());
		configuration.setStringAttribute(IServer.KEY_DOCUMENT_ROOT, docRootText.getText());
		try
		{
			int polling = Integer.parseInt(pollingIntervalText.getText());
			configuration.setIntAttribute(GenericServerTypeDelegate.POLLING_INTERVAL, polling * 1000);
		}
		catch (NumberFormatException e)
		{
			IdeLog.logInfo(ServerCorePlugin.getDefault(), Messages.GenericConfigurationDialog_INF_IntervalError, e);
		}

		super.okPressed();
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getDialog()
	 */
	public Dialog getDialog()
	{
		return this;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#isEdit()
	 */
	public boolean isEdit()
	{
		return isEdit;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setEdit(boolean)
	 */
	public void setEdit(boolean isEdit)
	{
		this.isEdit = isEdit;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#getConfiguration()
	 */
	public IAbstractConfiguration getConfiguration()
	{
		return configuration;
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void setConfiguration(IAbstractConfiguration configuration)
	{
		this.configuration = configuration;
	}

	private void checkLocal()
	{
		boolean en = isLocal.getSelection();
		healthURLText.setEnabled(!en);
		pollingIntervalText.setEnabled(!en);
		path.setEnabled(en);
		Object browseButton = path.getData("browse");// $NON-NLS-1$ //$NON-NLS-1$
		if (browseButton instanceof Button) 
		{
			((Button)browseButton).setEnabled(en);
		}
		startText.setEnabled(en);
		stopText.setEnabled(en);
		pauseText.setEnabled(en);
		resumeText.setEnabled(en);
		docRootText.setEnabled(en);
		browseButton = docRootText.getData("browse");// $NON-NLS-1$ //$NON-NLS-1$
		if (browseButton instanceof Button) // $NON-NLS-1$
		{
			((Button)browseButton).setEnabled(en);
		}
	}

	/**
	 * @see com.aptana.ide.server.ui.IConfigurationDialog#setServer(com.aptana.ide.server.core.IServer)
	 */
	public void setServer(IServer server)
	{
		this.server = server;
	}

}
