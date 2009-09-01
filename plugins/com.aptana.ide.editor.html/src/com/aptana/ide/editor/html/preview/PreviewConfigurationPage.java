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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.aptana.ide.editor.html.BrowserExtensionLoader;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.MultiPageHTMLEditor;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.server.core.IServer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewConfigurationPage extends BasePreviewConfigurationPage
{

	/**
	 * DEFAULT_PREVIEW_NAME
	 */
	public static final String DEFAULT_PREVIEW_NAME = "New Preview"; //$NON-NLS-1$

	private Map<String, Button> buttons = new HashMap<String, Button>();
	private Composite displayArea;
	private Composite browserArea;
	private Composite editArea;

	private PreviewTypeSelectionBlock block;

	private Button save;
	private Button cancel;
	private Label status;

	private Label nameLabel;
	private Text nameText;

	private ProgressListener listener;
	private Listener locListener;
	private Event lastEvent = null;

	/**
	 * Creates a new preview configuration page
	 * 
	 * @param editor
	 */
	public PreviewConfigurationPage(MultiPageHTMLEditor editor)
	{
		super(editor);
		this.title = DEFAULT_PREVIEW_NAME;
		this.url = this.editor.getURL();
		this.type = HTMLPreviewPropertyPage.FILE_BASED_TYPE;
		this.value = null;
		block = new PreviewTypeSelectionBlock();
		block.setEditor(editor);
		locListener = new Listener()
		{

			public void handleEvent(Event event)
			{
				if (lastEvent == null || event.time > lastEvent.time)
				{
					lastEvent = event;
					if (event.data instanceof Image)
					{
						PreviewConfigurationPage.this.editor.setTabIcon(PreviewConfigurationPage.this,
								(Image) event.data);
						String addOn = event.text;
						if (addOn != null)
						{
							PreviewConfigurationPage.this.editor.setTabTooltip(PreviewConfigurationPage.this, url + " " //$NON-NLS-1$
									+ event.text);
						}
					}
				}
			}

		};
		listener = new ProgressListener()
		{

			public void completed(ProgressEvent event)
			{
				BrowserExtensionLoader.getDecorator(browser, locListener);
			}

			public void changed(ProgressEvent event)
			{

			}

		};
	}

	/**
	 * Creates the preview page control
	 * 
	 * @param parent -
	 *            parent of preview page
	 */
	public void createControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, false);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.editArea = new Composite(displayArea, SWT.NONE);
		GridLayout eaLayout = new GridLayout(1, false);
		eaLayout.marginWidth = 10;
		eaLayout.marginHeight = 10;
		editArea.setLayout(eaLayout);
		editArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.browserArea = new Composite(displayArea, SWT.NONE);
		GridData baData = new GridData(SWT.FILL, SWT.FILL, true, true);
		baData.exclude = true;
		GridLayout baLayout = new GridLayout(1, false);
		baLayout.marginHeight = 0;
		baLayout.marginWidth = 0;
		this.browserArea.setLayout(baLayout);
		this.browserArea.setLayoutData(baData);
		this.browserArea.setVisible(false);
		Composite optionsArea = createConfigurationSection(this.editArea);
		createStartActionSection(optionsArea);
		createBrowserSection(optionsArea);
		createButtonSection(this.editArea);
		block.updateControls();
		validate();
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#run(java.lang.String)
	 */
	public boolean run(String actionID)
	{
		boolean run = false;
		try
		{
			if (IWorkbenchActionDefinitionIds.CUT.equals(actionID))
			{
				if (this.nameText.isFocusControl())
				{
					this.nameText.cut();
					run = true;
				}
				else if (block.getStartURLText().isFocusControl())
				{
					block.getStartURLText().cut();
					run = true;
				}
				else if (block.getCurrentURLText().isFocusControl())
				{
					run = true;
				}
			}
			else if (IWorkbenchActionDefinitionIds.PASTE.equals(actionID))
			{
				if (this.nameText.isFocusControl())
				{
					this.nameText.paste();
					run = true;
				}
				else if (block.getStartURLText().isFocusControl())
				{
					block.getStartURLText().paste();
					run = true;
				}
				else if (block.getCurrentURLText().isFocusControl())
				{
					run = true;
				}
			}
			else if (IWorkbenchActionDefinitionIds.COPY.equals(actionID))
			{
				if (this.nameText.isFocusControl())
				{
					this.nameText.copy();
					run = true;
				}
				else if (block.getStartURLText().isFocusControl())
				{
					block.getStartURLText().copy();
					run = true;
				}
				else if (block.getCurrentURLText().isFocusControl())
				{
					block.getCurrentURLText().copy();
					run = true;
				}
			}
		}
		catch (Exception e)
		{
			run = false;
		}
		catch (Error e)
		{
			run = false;
		}
		return run;
	}

	/**
	 * Sets the browser object and its name albe
	 * 
	 * @param browser
	 * @param label
	 */
	public void setBrowser(ContributedBrowser browser, String label)
	{
		Button button = (Button) buttons.get(label);
		if (button != null && !button.isDisposed())
		{
			button.setSelection(true);
			Iterator iter = buttons.values().iterator();
			while (iter.hasNext())
			{
				Button other = (Button) iter.next();
				if (other != button && other != null)
				{
					other.setSelection(false);
				}
			}
		}
		if (this.browser != null)
		{
			this.browser.dispose();
			this.browser.removeProgressListener(listener);
		}
		this.browserLabel = label;
		this.browser = browser;
		this.created = false;
	}

	/**
	 * Sets the title of the preview page
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		if (this.nameText != null && !this.nameText.isDisposed())
		{
			this.nameText.setText(title);
		}
		super.setTitle(title);
	}

	/**
	 * Displays the edit area of this preview page
	 */
	public void showEditArea()
	{
		GridData eaData = (GridData) editArea.getLayoutData();
		eaData.exclude = false;
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = true;
		this.browserArea.setVisible(false);
		this.editArea.setVisible(true);
		displayArea.layout(true, true);
	}

	/**
	 * Displays the browser area of this preview page
	 */
	public void showBrowserArea()
	{
		GridData eaData = (GridData) editArea.getLayoutData();
		eaData.exclude = true;
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = false;
		this.browserArea.setVisible(true);
		this.editArea.setVisible(false);
		displayArea.layout(true, true);
		Button button = (Button) buttons.get(this.browserLabel);
		if (button != null && !button.isDisposed())
		{
			button.setSelection(true);
			Iterator iter = buttons.values().iterator();
			while (iter.hasNext())
			{
				Button other = (Button) iter.next();
				if (other != button && other != null)
				{
					other.setSelection(false);
				}
			}
		}
		block.refreshURLs();
		block.updateControls();
		block.updateCurrentURL();
		this.validate();
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.BasePreviewConfigurationPage#getURL()
	 */
	public String getURL()
	{
		return block.getCurrentURLText().getText();
	}

	/**
	 * Sets the url of the preview page
	 * 
	 * @param url
	 */
	public void setURL(String url)
	{
		if (this.browser != null)
		{
			if (!created)
			{
				created = true;
				this.browser.createControl(browserArea);
				this.browser.addProgressListener(listener);
				browserArea.layout(true, true);
			}
			if (block.getFileButton().getSelection())
			{
				this.url = url;
			}
			else if (block.getServerButton().getSelection())
			{
				Object data = block.getServerText().getData();
				if (data != null && data instanceof IServer)
				{
					String serverURL = HTMLPreviewHelper.getServerURL((IServer) data, editor.getEditorInput(), block
							.getServerAppendButton().getSelection());
					if (serverURL != null)
					{
						this.url = serverURL;
					}
				}
			}
			else if (block.getServerButton().getSelection())
			{
				Object data = block.getServerText().getData();
				if (data != null && data instanceof ILaunchConfiguration)
				{
					String configURL = HTMLPreviewHelper.getConfigURL((ILaunchConfiguration) data, editor
							.getEditorInput());

					if (configURL != null)
					{
						this.url = configURL;
					}
				}
			}
			else
			{
				this.url = block.getStartURLText().getText();
				if (block.getStartURLAppendButton().getSelection())
				{
					IEditorInput input = this.editor.getEditorInput();
					this.url = HTMLPreviewHelper.getAbsoluteURL(value, input,
							HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE.equals(type));
				}
				if (this.url == null || this.url.length() == 0)
				{
					this.url = url;
				}
			}

			block.getCurrentURLText().setText(this.url);
			editor.setTabTooltip(this, this.url);
			this.browser.setURL(this.url);
		}
	}

	/**
	 * Saves the browser settings
	 */
	public void saveBrowserSettings()
	{
		Iterator<Button> iter = this.buttons.values().iterator();
		while (iter.hasNext())
		{
			Button b = iter.next();
			if (b.getSelection())
			{
				this.browserLabel = b.getText();
				break;
			}
		}
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		boolean found = false;
		for (int i = 0; i < extensions.length && !found; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length && !found; j++)
			{
				String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
				String name = BrowserExtensionLoader.getBrowserLabel(ce[j]);
				if (browserClass != null && name != null && name.equals(browserLabel))
				{
					found = true;
					Object obj;
					try
					{
						obj = ce[j].createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
						if (obj instanceof ContributedBrowser)
						{
							this.setBrowser((ContributedBrowser) obj, name);
						}
					}
					catch (CoreException e)
					{
					}

				}
			}
		}
	}

	/**
	 * Saves the page
	 */
	public void save()
	{
		if (block.getServerButton().getSelection())
		{
			if (block.getServerAppendButton().getSelection())
			{
				this.type = HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE;
			}
			else
			{
				this.type = HTMLPreviewPropertyPage.SERVER_BASED_TYPE;
			}
		}
		else if (block.getConfigurationButton().getSelection())
		{
			this.type = HTMLPreviewPropertyPage.CONFIG_BASED_TYPE;
		}
		else if (block.getStartURLButton().getSelection())
		{
			if (block.getStartURLAppendButton().getSelection())
			{
				this.type = HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE;
			}
			else
			{
				this.type = HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE;
			}
		}
		else
		{
			this.type = HTMLPreviewPropertyPage.FILE_BASED_TYPE;
		}
		if (block.getServerButton().getSelection())
		{
			Object obj = block.getServerText().getData();
			if (obj != null && obj instanceof IServer)
			{
				this.value = ((IServer) obj).getId();
			}
			else
			{
				this.value = block.getServerText().getText();
			}
		}
		else if (block.getConfigurationButton().getSelection())
		{
			this.value = block.getConfigurationText().getText();
		}
		else if (block.getStartURLButton().getSelection())
		{
			this.value = block.getStartURLText().getText().trim();
		}
		else
		{
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput)
			{
				value = ((IFileEditorInput) input).getFile().getLocation().makeAbsolute().toOSString();
			}
		}
		if (block.getStartURLButton().getSelection())
		{
			block.saveURLs();
		}
		saveBrowserSettings();
		title = nameText.getText();
		editor.setPreviewPageText(this.index, this.title);
		setURL(this.url);
		block.setType(this.type);
		block.setValue(this.value);
		editor.savePreviewsPages();
		showBrowserArea();
	}

	private void createButtonSection(Composite parent)
	{
		Composite buttons = new Composite(parent, SWT.NONE);
		GridLayout bLayout = new GridLayout(3, false);
		bLayout.marginHeight = 0;
		bLayout.marginWidth = 0;
		buttons.setLayout(bLayout);
		GridData bData = new GridData(SWT.FILL, SWT.FILL, true, false);
		buttons.setLayoutData(bData);
		save = new Button(buttons, SWT.PUSH);
		save.setText(Messages.PreviewConfigurationPage_LBL_Save);
		save.setToolTipText(Messages.PreviewConfigurationPage_TTP_Save);
		save.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				save();
			}

		});
		save.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		cancel = new Button(buttons, SWT.PUSH);
		cancel.setText(Messages.PreviewConfigurationPage_LBL_Cancel);
		cancel.setToolTipText(Messages.PreviewConfigurationPage_TTP_Cancel);
		cancel.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (created)
				{
					showBrowserArea();
				}
				else
				{
					// This case if when add is added and then cancel is selected. The page will be removed since it was
					// never saved since being added and so cancel seems appropriate to remove
					if (editor != null && index >= 0 && index < editor.getPageCount())
					{
						editor.removePage(index);
					}
				}
			}

		});
		cancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		status = new Label(buttons, SWT.LEFT);
		status.setForeground(UnifiedColorManager.getInstance().getColor(new RGB(255, 0, 0)));
		status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private Composite createConfigurationSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewConfigurationPage_LBL_OptionsGroup);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		nameLabel = new Label(group, SWT.LEFT);
		nameLabel.setText(Messages.PreviewConfigurationPage_LBL_PreviewName);

		nameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		nameText.setText(title);
		nameText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}

		});

		return group;
	}

	/**
	 * Generates a new preview name
	 * 
	 * @return - unique name
	 */
	public String generateNewPreviewName()
	{
		IFile file = getFile();
		String name = null;
		if (file != null)
		{
			try
			{
				String names = file.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_NAMES));
				if (names != null)
				{
					String[] splitNames = names.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					boolean foundName = false;
					name = DEFAULT_PREVIEW_NAME;
					int tries = 1;
					while (!foundName)
					{
						for (int i = 0; i < splitNames.length; i++)
						{
							if (name.equals(splitNames[i]))
							{
								name = DEFAULT_PREVIEW_NAME + " " + tries; //$NON-NLS-1$
								tries++;
								break;
							}
							else if (i + 1 == splitNames.length)
							{
								foundName = true;
								break;
							}
						}
					}
				}
				else
				{
					name = DEFAULT_PREVIEW_NAME;
				}
			}
			catch (CoreException e)
			{
				name = DEFAULT_PREVIEW_NAME;
			}
		}
		else
		{
			name = DEFAULT_PREVIEW_NAME;
		}
		return name;
	}

	private void validate()
	{
		IFile file = getFile();
		String name = nameText.getText();
		boolean error = false;
		if (name.trim().length() == 0)
		{
			error = true;
			status.setText(Messages.PreviewConfigurationPage_LBL_Status_Name);
		}
		else if (block.getStartURLButton().getSelection())
		{
			if (block.getStartURLText().getText().trim().length() == 0)
			{
				status.setText(Messages.PreviewConfigurationPage_LBL_Status_URL);
				error = true;
			}
		}
		else if (file != null)
		{
			try
			{
				String names = file.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_NAMES));
				if (names != null)
				{
					String[] splitNames = names.split(HTMLPreviewPropertyPage.PREFERENCE_DELIMITER);
					for (int i = 0; i < splitNames.length; i++)
					{
						if (!splitNames[i].equals(title) && name.equals(splitNames[i]))
						{
							status.setText(Messages.PreviewConfigurationPage_LBL_Status_NameExists);
							error = true;
							break;
						}
					}

				}
			}
			catch (CoreException e)
			{

			}
		}
		save.setEnabled(!error);
		if (!error)
		{
			status.setText(""); //$NON-NLS-1$
		}
	}

	private IFile getFile()
	{
		IFile file = null;
		IEditorInput input = this.editor.getEditorInput();
		if (input instanceof FileEditorInput)
		{
			file = ((FileEditorInput) input).getFile();
		}
		return file;
	}

	private IProject getProject()
	{
		IProject project = null;
		IFile file = getFile();
		if (file != null)
		{
			project = file.getProject();
		}
		return project;
	}

	private void createBrowserSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewConfigurationPage_LBL_BrowserGroup);
		group.setFont(parent.getFont());
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.horizontalSpan = 2;
		group.setLayoutData(gData);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		List labels = BrowserExtensionLoader.getAllBrowserLabels();
		for (int i = 0; i < labels.size(); i++)
		{
			String label = (String) labels.get(i);
			final Button browserButton = new Button(group, SWT.RADIO);
			Image image = BrowserExtensionLoader.getBrowserImage(label);
			if (image != null)
			{
				browserButton.setImage(image);
			}
			browserButton.setText(label);
			if (i == 0)
			{
				browserLabel = label;
				browserButton.setSelection(true);
			}
			buttons.put(label, browserButton);
		}
	}

	private void createStartActionSection(Composite parent)
	{
		block.createStartActionSection(parent, this.type, this.value);
		ModifyListener validator = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}

		};
		block.getStartURLButton().addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				validate();
			}

		});
		block.getStartURLText().addModifyListener(validator);
	}

	/**
	 * Gets the control of the preview page
	 * 
	 * @return - control
	 */
	public Control getControl()
	{
		return this.displayArea;
	}

	private ILaunchConfiguration getWorkspaceConfig()
	{
		ILaunchConfiguration config = null;
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String configName = store.getString(IPreferenceConstants.HTMLEDITOR_RUNCONFIG_PREVIEW_PREFERENCE);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
		try
		{
			ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(type);
			if (configs != null)
			{
				for (int i = 0; i < configs.length; i++)
				{
					if (configs[i].getName().equals(configName))
					{
						config = configs[i];
						break;
					}
				}
			}
		}
		catch (CoreException e)
		{
			config = null;
		}

		return config;
	}

	/**
	 * @return the config
	 */
	public ILaunchConfiguration getConfig()
	{
		ILaunchConfiguration config = null;
		IProject project = getProject();
		if (project != null)
		{
			try
			{
				String override = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE));
				if (HTMLPreviewPropertyPage.TRUE.equals(override))
				{
					String configName = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
							HTMLPreviewPropertyPage.HTML_PREVIEW_CONFIG));
					if (configName != null)
					{
						ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
						ILaunchConfigurationType type = launchManager
								.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
						ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(type);
						if (configs != null)
						{
							for (int i = 0; i < configs.length; i++)
							{
								if (configs[i].getName().equals(configName))
								{
									config = configs[i];
									break;
								}
							}
						}
					}
				}
				else
				{
					config = getWorkspaceConfig();
				}
			}
			catch (CoreException e)
			{
				config = null;
			}
		}
		else
		{
			config = getWorkspaceConfig();
		}
		return config;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isDeletable()
	 */
	public boolean isDeletable()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isReadOnly()
	 */
	public boolean isReadOnly()
	{
		return false;
	}

}
