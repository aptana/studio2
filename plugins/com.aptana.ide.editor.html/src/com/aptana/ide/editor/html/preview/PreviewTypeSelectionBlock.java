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
package com.aptana.ide.editor.html.preview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchGroupExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.core.EclipseUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.jetty.server.PreviewServerProvider;
import com.aptana.ide.server.ui.ServerImagesRegistry;
import com.aptana.ide.server.ui.generic.dialogs.ServerTypeSelectionDialog;
import com.aptana.ide.server.ui.views.actions.NewServerAction;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewTypeSelectionBlock
{

	private String type;
	private String value;

	private String sampleProjectName = null;

	private Label currentURL;
	private Text currentURLText;

	private Button useServerButton;
	private Composite serverImage;
	private Label serverText;
	private ToolBar serversBar;
	private ToolItem selectedServer;
	private Button serverAppendProjectName;
	private Menu serverMenu;
	private Image serverImg;

	private Button useConfigButton;
	private Composite configImage;
	private Label configText;
	private ToolBar configBar;
	private ToolItem selectedConfig;
	private Menu configMenu;
	private Image configImg;

	private Button startUrlButton;
	private Text startURLText;
	private Button startURLAppendProjectName;
	private ToolBar startURLBar;
	private ToolItem selectedStartURL;
	private Menu startURLMenu;
	private IEditorPart editor;

	private boolean useSample = false;

	private Button useFileURLButton;

	/**
	 * Creates a new preview type block
	 */
	public PreviewTypeSelectionBlock()
	{

	}

	/**
	 * Sets the editor for this block, will show the current url based on selection if the editor exists
	 * 
	 * @param editor
	 */
	public void setEditor(IEditorPart editor)
	{
		this.editor = editor;
	}

	/**
	 * Sets the current URL label
	 * 
	 * @param label
	 */
	public void setCurrentURLLabel(String label)
	{
		this.currentURL.setText(label);
	}

	/**
	 * Sets to use a sample url instead of an actual valid url
	 */
	public void useSampleURL()
	{
		this.useSample = true;
	}

	/**
	 * Gets the text field for the start url
	 * 
	 * @return - start url text field
	 */
	public Text getStartURLText()
	{
		return this.startURLText;
	}

	/**
	 * Gets the text field for the current url
	 * 
	 * @return - current url text field
	 */
	public Text getCurrentURLText()
	{
		return this.currentURLText;
	}

	/**
	 * Gets the text field for the server text (actually a label)
	 * 
	 * @return - server text field
	 */
	public Label getServerText()
	{
		return this.serverText;
	}

	/**
	 * Gets the text field for the configuration text (actually a label)
	 * 
	 * @return - configuration text field
	 */
	public Label getConfigurationText()
	{
		return this.configText;
	}

	/**
	 * Gets the start url button
	 * 
	 * @return - start url button
	 */
	public Button getStartURLButton()
	{
		return this.startUrlButton;
	}

	/**
	 * Gets the configuration button
	 * 
	 * @return - config button
	 */
	public Button getConfigurationButton()
	{
		return this.useConfigButton;
	}

	/**
	 * Gets the server button
	 * 
	 * @return - server button
	 */
	public Button getServerButton()
	{
		return this.useServerButton;
	}

	/**
	 * Gets the file button
	 * 
	 * @return - file button
	 */
	public Button getFileButton()
	{
		return this.useFileURLButton;
	}

	/**
	 * Gets the start url menu
	 * 
	 * @return - start url menu
	 */
	public Menu getStartURLMenu()
	{
		return this.startURLMenu;
	}

	/**
	 * Gets the append project name button
	 * 
	 * @return - append project name button
	 */
	public Button getServerAppendButton()
	{
		return this.serverAppendProjectName;
	}

	/**
	 * Gets the append project name button
	 * 
	 * @return - append project name button
	 */
	public Button getStartURLAppendButton()
	{
		return this.startURLAppendProjectName;
	}

	/**
	 * Sets the type field
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the type field
	 * 
	 * @return - type field
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Sets the value field
	 * 
	 * @param value
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the value field
	 * 
	 * @return - value field
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * Sets the block as enabled or disabled
	 * 
	 * @param enabled -
	 *            true if enabled
	 */
	public void setEnabled(boolean enabled)
	{
		useConfigButton.setEnabled(enabled);
		useServerButton.setEnabled(enabled);
		startUrlButton.setEnabled(enabled);
		useFileURLButton.setEnabled(enabled);
	}

	private void createServerSection(Composite parent)
	{
		final Composite serverComposite = new Composite(parent, SWT.NONE);
		GridLayout scLayout = new GridLayout(2, false);
		scLayout.marginHeight = 0;
		scLayout.marginWidth = 0;
		scLayout.horizontalSpacing = 0;
		scLayout.verticalSpacing = 0;
		serverComposite.setLayout(scLayout);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, false);
		serverComposite.setLayoutData(scData);

		final Composite inner = new Composite(serverComposite, SWT.BORDER);

		MouseAdapter listener = new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				if (serversBar.isEnabled() && serverText.isEnabled())
				{
					Rectangle rect = inner.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = serverComposite.toDisplay(pt);
					serverMenu.setLocation(pt.x, pt.y);
					serverMenu.setVisible(true);
				}
			}

		};

		inner.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout iLayout = new GridLayout(3, false);
		iLayout.marginHeight = 0;
		iLayout.marginWidth = 0;
		GridData iData = new GridData(SWT.FILL, SWT.FILL, false, false);
		iData.widthHint = 200;
		inner.setLayout(iLayout);
		inner.setLayoutData(iData);
		inner.addMouseListener(listener);

		serverImage = new Composite(inner, SWT.NONE);
		serverImage.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		serverImage.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (serverImg != null)
				{
					e.gc.drawImage(serverImg, 2, 2);
				}

			}

		});
		serverImage.addMouseListener(listener);
		GridData siData = new GridData(SWT.FILL, SWT.FILL, false, false);
		siData.heightHint = 16;
		siData.widthHint = 20;
		serverImage.setLayoutData(siData);
		serverText = new Label(inner, SWT.LEFT);
		serverText.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		serverText.addMouseListener(listener);
		GridData stData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		serverText.setLayoutData(stData);
		serversBar = new ToolBar(inner, SWT.FLAT);
		serversBar.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout sbLayout = new GridLayout(1, false);
		sbLayout.marginHeight = 0;
		sbLayout.marginWidth = 0;
		sbLayout.horizontalSpacing = 0;
		scLayout.verticalSpacing = 0;
		serversBar.setLayout(sbLayout);
		serversBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		selectedServer = new ToolItem(serversBar, SWT.PUSH);
		Image arrow = EclipseUtils.getArrowImage();
		selectedServer.setImage(arrow);
		serverMenu = new Menu(serverComposite);
		selectedServer.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = inner.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = serverComposite.toDisplay(pt);
				serverMenu.setLocation(pt.x, pt.y);
				serverMenu.setVisible(true);
			}

		});
		addServers();
		addAddServer();

		serverAppendProjectName = new Button(serverComposite, SWT.CHECK);
		serverAppendProjectName.setText(Messages.PreviewTypeSelectionBlock_LBL_Append);
		serverAppendProjectName.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateCurrentURL();
			}

		});
		GridData apnData = new GridData(SWT.FILL, SWT.FILL, false, false);
		apnData.horizontalIndent = 5;
		serverAppendProjectName.setLayoutData(apnData);
	}

	private void addAddServer()
	{
		final MenuItem addItem = new MenuItem(serverMenu, SWT.PUSH);
		addItem.setText(Messages.PreviewTypeSelectionBlock_LBL_AddServer);
		addItem.setImage(HTMLPlugin.getImage("icons/add_tab.gif")); //$NON-NLS-1$
		addItem.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// Open servers dialog
				ServerTypeSelectionDialog dialog = new ServerTypeSelectionDialog(serverMenu.getShell());
				dialog.open();
				IServerType result = dialog.getResult();
				String id = null;
				if (result != null)
				{
					NewServerAction action = new NewServerAction(result);
					action.run();
					id = action.getCreatedServerID();
				}
				MenuItem[] currents = serverMenu.getItems();
				for (int i = 0; i < currents.length; i++)
				{
					currents[i].dispose();
				}
				addServers();
				addAddServer();
				if (id != null)
				{
					IServer[] servers = ServerCore.getServerManager().getServers();
					for (int i = 0; i < servers.length; i++)
					{
						final IServer curr = servers[i];
						if (curr.isWebServer())
						{
							if (id.equals(curr.getId()))
							{
								serverText.setText(curr.getName());
								serverText.setData(curr);
								final Image img = ServerImagesRegistry.getInstance().getImage(curr);
								serverImg = img;
								serverImage.redraw();
								serverImage.update();
								updateCurrentURL();
								serverAppendProjectName.setEnabled(true);
							}
						}
					}
				}
			}

		});
	}

	private void addServers()
	{
		IServer[] servers = ServerCore.getServerManager().getServers();
		for (int i = 0; i < servers.length; i++)
		{
			final IServer curr = servers[i];
			if (curr.isWebServer())
			{
				final MenuItem server = new MenuItem(serverMenu, SWT.PUSH);
				server.setData(curr);
				server.setText(curr.getName());
				final Image img = ServerImagesRegistry.getInstance().getImage(curr);
				if (img != null)
				{
					server.setImage(img);
				}
				server.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						serverText.setText(server.getText());
						serverText.setData(curr);
						serverImg = server.getImage();
						serverImage.redraw();
						serverImage.update();
						updateCurrentURL();
						if (PreviewServerProvider.INTERNAL_PREVIEW_SERVER_ID.equals(curr.getId()))
						{
							serverAppendProjectName.setSelection(true);
							serverAppendProjectName.setEnabled(false);
						}
						else
						{
							serverAppendProjectName.setEnabled(true);
						}
					}

				});
			}
		}
	}

	/**
	 * Creates the start action section
	 * 
	 * @param parent
	 * @param type
	 * @param value
	 */
	public void createStartActionSection(Composite parent, String type, String value)
	{
		this.type = type;
		this.value = value;
		final Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewTypeSelectionBlock_LBL_TypeGroup);
		group.setFont(parent.getFont());
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 2;
		group.setLayoutData(gData);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		if (editor != null || useSample)
		{
			Composite internal = new Composite(group, SWT.NONE);
			GridLayout iLayout = new GridLayout(2, false);
			iLayout.marginHeight = 0;
			iLayout.marginWidth = 0;
			GridData iData = new GridData(SWT.FILL, SWT.FILL, true, false);
			iData.horizontalSpan = 2;
			internal.setLayoutData(iData);
			internal.setLayout(iLayout);
			this.currentURL = new Label(internal, SWT.LEFT);
			this.currentURL.setText(Messages.PreviewTypeSelectionBlock_LBL_URL);
			this.currentURLText = new Text(internal, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
			GridData cuData = new GridData(SWT.FILL, SWT.FILL, true, false);
			this.currentURLText.setLayoutData(cuData);
		}

		useServerButton = new Button(group, SWT.RADIO);
		useServerButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				serversBar.setEnabled(useServerButton.getSelection());
				serverText.setEnabled(useServerButton.getSelection());
				serverAppendProjectName.setEnabled(useServerButton.getSelection());
				if (useServerButton.getSelection())
				{
					if (serverMenu.getItemCount() > 0 && serverText.getData() == null)
					{
						MenuItem menuItem = serverMenu.getItem(0);
						Object obj = menuItem.getData();
						if (obj != null && obj instanceof IServer)
						{
							serverText.setText(menuItem.getText());
							serverText.setData(obj);
							serverImg = menuItem.getImage();
							serverImage.redraw();
							serverImage.update();
						}
					}
					if (serverText.getData() != null && serverText.getData() instanceof IServer)
					{
						IServer server = (IServer) serverText.getData();
						if (PreviewServerProvider.INTERNAL_PREVIEW_SERVER_ID.equals(server.getId()))
						{
							serverAppendProjectName.setSelection(true);
							serverAppendProjectName.setEnabled(false);
						}
					}
				}
				updateCurrentURL();
			}

		});
		useServerButton.setText(Messages.PreviewTypeSelectionBlock_LBL_UseServer);
		createServerSection(group);
		serversBar.setEnabled(false);

		useConfigButton = new Button(group, SWT.RADIO);
		useConfigButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				configBar.setEnabled(useConfigButton.getSelection());
				configText.setEnabled(useConfigButton.getSelection());
				if (useConfigButton.getSelection())
				{
					if (configMenu.getItemCount() > 0 && configText.getData() == null)
					{
						MenuItem menuItem = configMenu.getItem(0);
						Object obj = menuItem.getData();
						if (obj != null && obj instanceof ILaunchConfiguration)
						{
							configText.setText(menuItem.getText());
							configText.setData(obj);
							configImg = menuItem.getImage();
							configImage.redraw();
							configImage.update();
						}
					}
				}
				updateCurrentURL();
			}

		});
		useConfigButton.setText(Messages.PreviewTypeSelectionBlock_LBL_UseRunConfig);
		createConfigSection(group);
		configBar.setEnabled(false);

		startUrlButton = new Button(group, SWT.RADIO);
		startUrlButton.setText(Messages.PreviewTypeSelectionBlock_LBL_UseAbsURL);
		createStartURLSection(group);
		startUrlButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				startURLBar.setEnabled(startUrlButton.getSelection());
				startURLText.setEnabled(startUrlButton.getSelection());
				startURLAppendProjectName.setEnabled(startUrlButton.getSelection());
				if (startUrlButton.getSelection())
				{
					if (startURLMenu.getItemCount() > 0 && startURLText.getText().length() == 0)
					{
						MenuItem menuItem = startURLMenu.getItem(0);
						startURLText.setText(menuItem.getText());
					}
				}
				updateCurrentURL();
			}

		});
		startURLText.setEnabled(false);
		startURLBar.setEnabled(false);

		useFileURLButton = new Button(group, SWT.RADIO);
		useFileURLButton.setText(Messages.PreviewTypeSelectionBlock_LBL_UseFileURL);
		useFileURLButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateCurrentURL();
			}

		});
	}

	private void createStartURLSection(Composite parent)
	{
		final Composite startURLComposite = new Composite(parent, SWT.NONE);
		GridLayout suLayout = new GridLayout(2, false);
		suLayout.marginHeight = 0;
		suLayout.marginWidth = 0;
		suLayout.horizontalSpacing = 0;
		suLayout.verticalSpacing = 0;
		startURLComposite.setLayout(suLayout);
		GridData suData = new GridData(SWT.FILL, SWT.FILL, true, false);
		startURLComposite.setLayoutData(suData);

		final Composite inner = new Composite(startURLComposite, SWT.BORDER);
		inner.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout iLayout = new GridLayout(2, false);
		iLayout.marginHeight = 0;
		iLayout.marginWidth = 0;
		iLayout.horizontalSpacing = 0;
		iLayout.verticalSpacing = 0;
		inner.setLayout(iLayout);
		GridData iData = new GridData(SWT.FILL, SWT.FILL, false, false);
		iData.widthHint = 250;
		inner.setLayoutData(iData);

		startURLText = new Text(inner, SWT.SINGLE);
		startURLText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				updateCurrentURL();
			}

		});
		startURLText.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		startURLText.setLayoutData(data);

		startURLBar = new ToolBar(inner, SWT.FLAT);
		startURLBar.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout subLayout = new GridLayout(1, false);
		subLayout.marginHeight = 0;
		subLayout.marginWidth = 0;
		subLayout.horizontalSpacing = 0;
		subLayout.verticalSpacing = 0;
		startURLBar.setLayout(subLayout);
		startURLBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		selectedStartURL = new ToolItem(startURLBar, SWT.PUSH);
		Image arrow = EclipseUtils.getArrowImage();
		selectedStartURL.setImage(arrow);

		startURLMenu = new Menu(inner);

		selectedStartURL.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = inner.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = inner.toDisplay(pt);
				startURLMenu.setLocation(pt.x, pt.y);
				startURLMenu.setVisible(true);
			}

		});

		startURLAppendProjectName = new Button(startURLComposite, SWT.CHECK);
		startURLAppendProjectName.setText(Messages.PreviewTypeSelectionBlock_LBL_AppendPath);
		startURLAppendProjectName.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateCurrentURL();
			}

		});
		GridData apnData = new GridData(SWT.FILL, SWT.FILL, false, false);
		apnData.horizontalIndent = 5;
		startURLAppendProjectName.setLayoutData(apnData);

		String currs = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_LAST_START_URLS);
		String[] urls = currs.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
		for (int i = 0; i < urls.length; i++)
		{
			if (urls[i].trim().length() > 0)
			{
				final MenuItem startURL = new MenuItem(startURLMenu, SWT.PUSH);
				startURL.setText(urls[i]);
				startURL.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						startURLText.setText(startURL.getText());
					}

				});
			}
		}
	}

	/**
	 * Updates the controls
	 */
	public void updateControls()
	{
		useConfigButton.setSelection(false);
		useServerButton.setSelection(false);
		startUrlButton.setSelection(false);
		useFileURLButton.setSelection(false);
		configBar.setEnabled(false);
		configText.setEnabled(false);
		serversBar.setEnabled(false);
		serverText.setEnabled(false);
		startURLText.setEnabled(false);
		serverAppendProjectName.setEnabled(false);
		startURLAppendProjectName.setEnabled(false);
		if (this.type != null)
		{
			if (HTMLPreviewPropertyPage.CONFIG_BASED_TYPE.equals(this.type))
			{
				useConfigButton.setSelection(true);
				configBar.setEnabled(useConfigButton.getEnabled());
				configText.setEnabled(useConfigButton.getEnabled());
				if (this.value != null)
				{
					ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
					ILaunchConfigurationType type = launchManager
							.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
					try
					{
						ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(type);
						for (int i = 0; i < configs.length; i++)
						{
							if (this.value.equals(configs[i].getName()))
							{
								configText.setText(this.value);
								configText.setData(configs[i]);
								final Image img = EclipseUIUtils.getDebugUIPluginImageLabel(configs[i]);
								if (img != null)
								{
									configImg = img;
									configImage.redraw();
									configImage.update();
								}
								break;
							}
						}
					}
					catch (Exception e)
					{
					}
				}
			}
			else if (HTMLPreviewPropertyPage.SERVER_BASED_TYPE.equals(this.type)
					|| HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(this.type))
			{
				useServerButton.setSelection(true);
				serversBar.setEnabled(useServerButton.getEnabled());
				serverText.setEnabled(useServerButton.getEnabled());
				serverAppendProjectName.setEnabled(useServerButton.getEnabled());
				serverAppendProjectName.setSelection(HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE
						.equals(this.type));
				if (this.value != null)
				{
					IServer[] servers = ServerCore.getServerManager().getServers();
					for (int i = 0; i < servers.length; i++)
					{
						if (this.value.equals(servers[i].getId()))
						{
							if (PreviewServerProvider.INTERNAL_PREVIEW_SERVER_ID.equals(this.value))
							{
								serverAppendProjectName.setSelection(true);
								serverAppendProjectName.setEnabled(false);
							}
							serverText.setText(servers[i].getName());
							serverText.setData(servers[i]);
							final Image img = ServerImagesRegistry.getInstance().getImage(servers[i]);
							if (img != null)
							{
								serverImg = img;
								serverImage.redraw();
								serverImage.update();
							}
							break;
						}
					}
				}
			}
			else if (HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE.equals(this.type)
					|| HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE.equals(this.type))
			{
				startUrlButton.setSelection(true);
				startURLText.setEnabled(startUrlButton.getEnabled());
				startURLBar.setEnabled(startUrlButton.getEnabled());
				startURLAppendProjectName.setEnabled(startUrlButton.getSelection());
				startURLAppendProjectName.setSelection(HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE
						.equals(this.type));
				if (this.value != null)
				{
					startURLText.setText(this.value);
				}
			}
			else
			{
				useFileURLButton.setSelection(true);
				if (this.editor != null)
				{
					IEditorInput input = this.editor.getEditorInput();
					if (input instanceof FileEditorInput)
					{
						IFile file = ((FileEditorInput) input).getFile();
						currentURLText.setText(file.getLocation().makeAbsolute().toOSString());
					}
				}
				else if (useSample)
				{
					currentURLText.setText("file://workspace/" + getProjectName() + "/file.html"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	private void createConfigSection(Composite parent)
	{
		final Composite configComposite = new Composite(parent, SWT.NONE);
		GridLayout ccLayout = new GridLayout(1, false);
		ccLayout.marginHeight = 0;
		ccLayout.marginWidth = 0;
		ccLayout.horizontalSpacing = 0;
		configComposite.setLayout(ccLayout);
		GridData ccData = new GridData(SWT.FILL, SWT.FILL, true, false);
		configComposite.setLayoutData(ccData);

		final Composite inner = new Composite(configComposite, SWT.BORDER);
		inner.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout iLayout = new GridLayout(3, false);
		iLayout.marginHeight = 0;
		iLayout.marginWidth = 0;
		GridData iData = new GridData(SWT.FILL, SWT.FILL, false, false);
		iData.widthHint = 200;
		inner.setLayout(iLayout);
		inner.setLayoutData(iData);

		MouseAdapter listener = new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				if (configBar.isEnabled() && configText.isEnabled())
				{
					Rectangle rect = inner.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = configComposite.toDisplay(pt);
					configMenu.setLocation(pt.x, pt.y);
					configMenu.setVisible(true);
				}
			}

		};

		inner.addMouseListener(listener);

		configImage = new Composite(inner, SWT.NONE);
		configImage.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		configImage.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (configImg != null)
				{
					e.gc.drawImage(configImg, 2, 2);
				}
			}

		});
		configImage.addMouseListener(listener);

		GridData ciData = new GridData(SWT.FILL, SWT.FILL, false, false);
		ciData.heightHint = 16;
		ciData.widthHint = 20;
		configImage.setLayoutData(ciData);

		configText = new Label(inner, SWT.LEFT);
		configText.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData stData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		configText.setLayoutData(stData);
		configText.addMouseListener(listener);

		configBar = new ToolBar(inner, SWT.FLAT);
		configBar.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout cbLayout = new GridLayout(1, false);
		cbLayout.marginHeight = 0;
		cbLayout.marginWidth = 0;
		cbLayout.horizontalSpacing = 0;
		configBar.setLayout(cbLayout);
		configBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		selectedConfig = new ToolItem(configBar, SWT.PUSH);
		Image arrow = EclipseUtils.getArrowImage();
		selectedConfig.setImage(arrow);

		configMenu = new Menu(configComposite);

		selectedConfig.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = inner.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = configComposite.toDisplay(pt);
				configMenu.setLocation(pt.x, pt.y);
				configMenu.setVisible(true);
			}

		});

		addRunConfigs();
		addAddRunConfiguration();
	}

	private void addAddRunConfiguration()
	{
		final MenuItem addItem = new MenuItem(configMenu, SWT.PUSH);
		addItem.setText(Messages.PreviewTypeSelectionBlock_LBL_AddConfig);
		addItem.setImage(HTMLPlugin.getImage("icons/add_tab.gif")); //$NON-NLS-1$
		addItem.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				LaunchGroupExtension ext = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchGroup(
						"org.eclipse.debug.ui.launchGroup.run"); //$NON-NLS-1$
				LaunchConfigurationsDialog dialog = new LaunchConfigurationsDialog(configMenu.getShell(), ext);
				dialog.open();
				MenuItem[] currents = configMenu.getItems();
				for (int i = 0; i < currents.length; i++)
				{
					currents[i].dispose();
				}
				addRunConfigs();
				addAddRunConfiguration();
			}

		});
	}

	private void addRunConfigs()
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
		try
		{
			ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(type);
			for (int i = 0; i < configs.length; i++)
			{
				final MenuItem config = new MenuItem(configMenu, SWT.PUSH);
				final ILaunchConfiguration current = configs[i];
				config.setData(current);
				config.setText(current.getName());
				final Image img = EclipseUIUtils.getDebugUIPluginImageLabel(current);
				if (img != null)
				{
					config.setImage(img);
				}
				config.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						configText.setText(config.getText());
						configText.setData(current);
						configImg = config.getImage();
						configImage.redraw();
						configImage.update();
						updateCurrentURL();
					}

				});
			}
		}
		catch (CoreException e)
		{
		}
	}

	/**
	 * Refresh the absolute urls
	 */
	public void refreshURLs()
	{
		String currs = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_LAST_START_URLS);
		String[] urls = currs.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
		MenuItem[] currents = getStartURLMenu().getItems();
		for (int i = 0; i < currents.length; i++)
		{
			currents[i].dispose();
		}
		for (int i = 0; i < urls.length; i++)
		{
			if (urls[i].trim().length() > 0)
			{
				final MenuItem startURL = new MenuItem(getStartURLMenu(), SWT.PUSH);
				startURL.setText(urls[i]);
				startURL.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						getStartURLText().setText(startURL.getText());
					}

				});
			}
		}
	}

	/**
	 * Saves the url history
	 */
	public void saveURLs()
	{
		String urlToAdd = getStartURLText().getText();
		String currs = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_LAST_START_URLS);
		String[] urls = currs.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
		List<String> newSettings = new ArrayList<String>();
		newSettings.add(urlToAdd);
		int max = Math.min(urls.length, 10);
		for (int i = 0; i < max; i++)
		{
			if (!urlToAdd.equals(urls[i]) && urlToAdd.trim().length() > 0)
			{
				newSettings.add(urls[i]);
			}
		}
		HTMLPlugin.getDefault().getPreferenceStore().setValue(HTMLPreviewPropertyPage.HTML_PREVIEW_LAST_START_URLS,
				StringUtils.join(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER, newSettings.toArray(new String[0])));
	}

	private String getProjectName()
	{
		return this.sampleProjectName != null ? this.sampleProjectName : "project"; //$NON-NLS-1$
	}

	/**
	 * Updates the current url field if it is present
	 */
	public void updateCurrentURL()
	{
		if (editor == null && !useSample)
		{
			return;
		}
		if (useServerButton.getSelection())
		{
			Object data = serverText.getData();
			if (data != null && data instanceof IServer)
			{
				String serverURL = null;
				if (useSample)
				{
					serverURL = HTMLPreviewHelper.getServerHostURL((IServer) data);
					if (serverAppendProjectName.getSelection())
					{
						serverURL += getProjectName() + "/"; //$NON-NLS-1$
					}
					serverURL += "file.html"; //$NON-NLS-1$
				}
				else
				{
					serverURL = HTMLPreviewHelper.getServerURL((IServer) data, editor.getEditorInput(),
							serverAppendProjectName.getSelection());
				}
				if (serverURL != null)
				{
					currentURLText.setText(serverURL);
				}
				else
				{
					currentURLText.setText(""); //$NON-NLS-1$
				}
			}
			else
			{
				currentURLText.setText(""); //$NON-NLS-1$
			}
		}
		else if (useConfigButton.getSelection())
		{
			Object data = configText.getData();
			if (data != null && data instanceof ILaunchConfiguration)
			{
				String configURL = null;
				if (useSample)
				{
					configURL = HTMLPreviewHelper.getConfigSampleURL((ILaunchConfiguration) data, getProjectName());
				}
				else
				{
					configURL = HTMLPreviewHelper.getConfigURL((ILaunchConfiguration) data, editor.getEditorInput());
				}
				if (configURL != null)
				{
					currentURLText.setText(configURL);
				}
				else
				{
					currentURLText.setText(""); //$NON-NLS-1$
				}
			}
			else
			{
				currentURLText.setText(""); //$NON-NLS-1$
			}
		}
		else if (startUrlButton.getSelection())
		{
			String text = startURLText.getText();
			if (startURLAppendProjectName.getSelection())
			{
				if (!text.endsWith("/")) //$NON-NLS-1$
				{
					text += "/"; //$NON-NLS-1$
				}
				text += "folder/file.html"; //$NON-NLS-1$
			}
			currentURLText.setText(text);
		}
		else if (useFileURLButton.getSelection())
		{
			if (this.editor != null)
			{
				IEditorInput input = this.editor.getEditorInput();
				if (input instanceof FileEditorInput)
				{
					IFile file = ((FileEditorInput) input).getFile();
					currentURLText.setText(file.getLocation().makeAbsolute().toOSString());
				}
			}
			else if (useSample)
			{
				currentURLText.setText("file://workspace/" + getProjectName() + "/file.html"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * @return the sampleProjectName
	 */
	public String getSampleProjectName()
	{
		return sampleProjectName;
	}

	/**
	 * @param sampleProjectName
	 *            the sampleProjectName to set
	 */
	public void setSampleProjectName(String sampleProjectName)
	{
		this.sampleProjectName = sampleProjectName;
	}
}
