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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.URLEncoder;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.html.BrowserExtensionLoader;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.MultiPageHTMLEditor;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.ServerFileTypeHandlers;
import com.aptana.ide.server.core.ServerFileTypeHandlers.PreviewInfo;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class DefaultPreviewConfigurationPage extends BasePreviewConfigurationPage
{
	
	/**
	 * Temp extension.
	 */
	private static final String TEMP_EXTENSION = "tmp"; //$NON-NLS-1$

	private Composite displayArea;
	private Composite browserArea;

	private ProgressListener listener;
	private Listener locListener;
	private Event lastEvent = null;

	/**
	 * Creates a new preview configuration page
	 * 
	 * @param editor
	 */
	public DefaultPreviewConfigurationPage(MultiPageHTMLEditor editor)
	{
		super(editor);
		this.url = this.editor.getURL();
		locListener = new Listener()
		{

			public void handleEvent(Event event)
			{
				if (lastEvent == null || event.time > lastEvent.time)
				{
					lastEvent = event;
					if (event.data instanceof Image)
					{
						DefaultPreviewConfigurationPage.this.editor.setTabIcon(DefaultPreviewConfigurationPage.this,
								(Image) event.data);
					}
					String addOn = event.text;
					if (addOn != null)
					{
						DefaultPreviewConfigurationPage.this.editor.setTabTooltip(DefaultPreviewConfigurationPage.this,
								url + " " + event.text); //$NON-NLS-1$
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
		this.browserArea = new Composite(displayArea, SWT.NONE);
		GridData baData = new GridData(SWT.FILL, SWT.FILL, true, true);
		baData.exclude = true;
		GridLayout baLayout = new GridLayout(1, false);
		baLayout.marginHeight = 0;
		baLayout.marginWidth = 0;
		this.browserArea.setLayout(baLayout);
		this.browserArea.setLayoutData(baData);
		this.browserArea.setVisible(false);
	}

	/**
	 * Sets the browser object and its name
	 * 
	 * @param browser
	 * @param label
	 */
	public void setBrowser(ContributedBrowser browser, String label)
	{
		if (this.browser != null)
		{
			this.browser.dispose();
		}
		this.browserLabel = label;
		if (this.browser != null)
		{
			this.browser.removeProgressListener(listener);
		}
		this.browser = browser;
		this.created = false;

		// Always create browsers as pages are created on mac os x due to safari must load before firefox
		if (CoreUIUtils.onMacOSX)
		{
			create();
		}
	}

	/**
	 * Displays the edit area of this preview page
	 */
	public void showEditArea()
	{
		displayArea.layout(true, true);
	}

	/**
	 * Displays the browser area of this preview page
	 */
	public void showBrowserArea()
	{
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = false;
		this.browserArea.setVisible(true);
		displayArea.layout(true, true);
	}

	private void create()
	{
		if (!created)
		{
			created = true;
			this.browser.createControl(browserArea);
			this.browser.addProgressListener(listener);
			browserArea.layout(true, true);
		}
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
			create();
			String value = null;
			String type = null;
			IEditorInput input = this.editor.getEditorInput();
			boolean isProjectFile = false;
			
			String extension = getExtensionByURL(url);
			IPreviewConfiguration configuration = null;
			if (extension != null)
			{
				configuration = PreviewConfigurations.getConfiguration(extension);
			}
			
			if (configuration == null)
			{
				configuration = new HTMLPreviewConfiguration();
			}
			
			if (input instanceof IFileEditorInput)
			{
				isProjectFile = true;
				IFile file = ((IFileEditorInput) input).getFile();

				if (configuration.projectOverridesPreview(file.getProject()))
				{
					type = configuration.getPreviewType(file.getProject());
					value = configuration.getPreviewName(file.getProject());
				}
			}
			else
			{
				type = HTMLPreviewPropertyPage.FILE_BASED_TYPE;
				value = url;
			}
			
			if (type == null || value == null)
			{
				type = configuration.getPreviewType();
				value = configuration.getPreviewName();
			}

			PreviewInfo previewInfo = ServerFileTypeHandlers.getPreviewInfoFromURL(url);
			String alternativeValue = null;
			String serverTypeRestriction = null;
			if (previewInfo != null)
			{
				alternativeValue = previewInfo.serverID;
				serverTypeRestriction = previewInfo.serverTypeID;
			}

			if (configuration.isFileBasedType(type))
			{
				this.url = url;
			}
			else if (configuration.isServerBasedType(type)
					|| configuration.isAppendedServerBasedType(type))
			{
				if (isProjectFile)
				{
					IServer[] servers = ServerCore.getServerManager().getServers();
					this.url = null;
					if (alternativeValue != null && alternativeValue.length() != 0)
					{
						for (int i = 0; i < servers.length; i++)
						{
							final IServer curr = servers[i];
							if (curr.getId().equals(alternativeValue)
									&& curr.getServerType().getId().equals(serverTypeRestriction))
							{
								this.url = HTMLPreviewHelper.getServerURL(curr, input,
										configuration.isAppendedServerBasedType(type),
										previewInfo.pathHeader);
								break;
							}
						}
					}
					if (this.url == null)
					{
						for (int i = 0; i < servers.length; i++)
						{
							final IServer curr = servers[i];
							if (curr.getId().equals(value))
							{
								if (alternativeValue != null && alternativeValue.length() == 0
										& curr.getServerType().getId().equals(serverTypeRestriction))
								{
									this.url = HTMLPreviewHelper.getServerURL(curr, input,
											configuration.isAppendedServerBasedType(type),
											previewInfo.pathHeader);
								}
								else
								{
									this.url = HTMLPreviewHelper.getServerURL(curr, input,
											configuration.isAppendedServerBasedType(type));
								}
								break;
							}
						}
					}
				}
			}
			else if (configuration.isConfigurationBasedType(type))
			{
				ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType launchType = launchManager
						.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
				try
				{
					ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(launchType);
					for (int i = 0; i < configs.length; i++)
					{
						final ILaunchConfiguration current = configs[i];
						if (current.getName().equals(value))
						{
							this.url = HTMLPreviewHelper.getConfigURL(current, input);
							break;
						}
					}
				}
				catch (CoreException e)
				{
				}
			}
			else if (configuration.isAbsoluteBasedType(type)
					|| configuration.isAppendedAbsoluteBasedType(type))
			{
				this.url = HTMLPreviewHelper.getAbsoluteURL(value, input,
						configuration.isAppendedAbsoluteBasedType(type));
			}
			if (this.url == null)
			{
				this.url = url;
			}
			editor.setTabTooltip(this, this.url);
			try {
				this.url = URLEncoder.encode(new URL(this.url)).toExternalForm();
			} catch (MalformedURLException e) {
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.DefaultPreviewConfigurationPage_ERR_Encode, e);
			}
			this.browser.setURL(this.url);
		}
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

	/**
	 * @return the defaultPage
	 */
	public boolean isDefaultPage()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isDeletable()
	 */
	public boolean isDeletable()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#isReadOnly()
	 */
	public boolean isReadOnly()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.editor.html.preview.IPreviewConfigurationPage#run(java.lang.String)
	 */
	public boolean run(String actionID)
	{
		return false;
	}
	
	/**
	 * Gets file extension by url.
	 * @param urlStr - urlStr to get and extension by.
	 * @return file extension.
	 */
	private String getExtensionByURL(String urlStr)
	{
		String file = urlStr;
		
		int questionPos = urlStr.indexOf('?');
		if (questionPos >= 0)
		{
			file = file.substring(0, questionPos);
		}
		
		int lastPointPos = file.lastIndexOf('.');
		if (lastPointPos == -1 || lastPointPos == file.length() - 1)
		{
			return null;
		}
		
		StringBuilder result = new StringBuilder();
		for (int i = lastPointPos + 1; i < file.length(); i++)
		{
			char ch = file.charAt(i);
			if(Character.isJavaIdentifierPart(ch))
			{
				result.append(ch);
			}
			else 
			{
				break;
			}
		}
		
		if (TEMP_EXTENSION.equals(result.toString()))
		{
			return getExtensionByURL(file.substring(0, lastPointPos));
		}
		
		return result.toString();
	}
}
