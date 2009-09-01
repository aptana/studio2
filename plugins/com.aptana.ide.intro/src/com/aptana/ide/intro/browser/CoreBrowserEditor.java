/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.browser;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.WebBrowserEditor;

import com.aptana.ide.core.FileUtils;

/**
 * Core browser editor for displaying a url.
 * 
 * @author Kevin Sawicki
 */
public class CoreBrowserEditor extends WebBrowserEditor
{

	private CoreBrowserEditorInput browserInput;
	private Composite displayArea;

	/**
	 * Core browser editor ID
	 */
	public static final String ID = "com.aptana.ide.intro.browserEditor"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.ui.internal.browser.WebBrowserEditor#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		if (browserInput != null)
		{
			return browserInput.isSaveAsAllowed();
		}
		return false;
	}

	/**
	 * @see org.eclipse.ui.internal.browser.WebBrowserEditor#doSaveAs()
	 */
	public void doSaveAs()
	{
		if (browserInput != null)
		{
			String fileName = browserInput.getSaveAsFileName();
			File file = browserInput.getSaveAsFile();
			if (fileName != null && file != null && file.exists() && file.canRead())
			{
				FileDialog dialog = new FileDialog(displayArea.getShell(), SWT.SAVE);
				dialog.setFileName(fileName);
				dialog.setFilterExtensions(new String[] { "*.html", "*.htm" }); //$NON-NLS-1$ //$NON-NLS-2$
				String outputName = dialog.open();
				if (outputName != null)
				{
					FileInputStream stream;
					try
					{
						stream = new FileInputStream(file);
						FileUtils.writeStreamToFile(stream, outputName);
					}
					catch (Exception e)
					{
						MessageDialog.openError(displayArea.getShell(), Messages.CoreBrowserEditor_ErrorSave_Title, Messages.CoreBrowserEditor_ErrorSave_Message
								+ e.getMessage());
					}

				}
			}
		}
	}

	/**
	 * Adds a dispose listener to the main control of this editor
	 * 
	 * @param listener
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		if (displayArea != null)
		{
			displayArea.addDisposeListener(listener);
		}
	}

	/**
	 * Removes a dispose listener from the main control of this editor
	 * 
	 * @param listener
	 */
	public void removeDisposeListener(DisposeListener listener)
	{
		if (displayArea != null)
		{
			displayArea.removeDisposeListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.internal.browser.WebBrowserEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 1;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		super.createPartControl(displayArea);

		Browser browser = webBrowser.getBrowser();
		if (browser != null)
		{
			webBrowser.getBrowser().addLocationListener(new CoreBrowserLocationListener());
			webBrowser.getBrowser().addProgressListener(new ProgressListener()
			{

				public void completed(ProgressEvent event)
				{
					if (browserInput.getScript() != null)
					{
						webBrowser.getBrowser().execute(browserInput.getScript());
					}
				}

				public void changed(ProgressEvent event)
				{
					// TODO Auto-generated method stub

				}

			});
			webBrowser.getBrowser().addListener(SWT.MenuDetect, new Listener()
			{

				public void handleEvent(Event event)
				{
					event.doit = false;
				}
			});
		}
	}

	/**
	 * @see org.eclipse.ui.internal.browser.WebBrowserEditor#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		if (input instanceof CoreBrowserEditorInput)
		{
			browserInput = (CoreBrowserEditorInput) input;
			if (!browserInput.isAlreadyOpened())
			{
				browserInput.setAlreadyOpened();
			}
			else if (browserInput.isOnlyOneAllowed())
			{
				throw new PartInitException("Browser already opened with same input"); //$NON-NLS-1$
			}
			if (browserInput.getImage() != null)
			{
				Image oldImage = image;
				image = browserInput.getImage().createImage();
				setTitleImage(image);
				if (oldImage != null && !oldImage.isDisposed())
				{
					oldImage.dispose();
				}
			}
		}
		else
		{
			throw new PartInitException("Input must be CoreBrowserEditorInput"); //$NON-NLS-1$
		}
	}

	/**
	 * Executes the script in the editor input
	 */
	public void executeScript()
	{
		if (browserInput != null && browserInput.getScript() != null)
		{
			if (webBrowser != null && webBrowser.getBrowser() != null)
			{
				webBrowser.getBrowser().execute(browserInput.getScript());
			}
		}
	}

	/**
	 * @param url
	 */
	public void setURL(String url)
	{
		if (webBrowser != null && webBrowser.getBrowser() != null)
		{
			webBrowser.getBrowser().setUrl(url);
		}
	}
}
