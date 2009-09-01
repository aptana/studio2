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
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

/**
 * Core browser editor input adding an image descriptor to the Web browser editor input
 * 
 * @author Kevin Sawicki
 */
public class CoreBrowserEditorInput extends WebBrowserEditorInput
{

	private ImageDescriptor image;
	private String script = null;
	private boolean onlyOneAllowed;
	private boolean alreadyOpened;
	private boolean saveAsAllowed;
	private String saveAsFileName;
	private File saveAsFile;
	private String inputID;

	/**
	 * Default constructor
	 * 
	 * @param style
	 */
	public CoreBrowserEditorInput(int style)
	{
		super(null, style);
		saveAsAllowed = false;
		onlyOneAllowed = false;
		alreadyOpened = false;
		saveAsFileName = "page.html"; //$NON-NLS-1$
		saveAsFile = null;
	}

	/**
	 * Creates a new input
	 * 
	 * @param resolved -
	 *            URL
	 * @param style
	 */
	public CoreBrowserEditorInput(URL resolved, int style)
	{
		super(resolved, style);
	}

	/**
	 * Creates a new input
	 * 
	 * @param resolved -
	 *            URL
	 */
	public CoreBrowserEditorInput(URL resolved)
	{
		super(resolved);
	}

	/**
	 * Gets the image descriptor
	 * 
	 * @return image descriptor
	 */
	public ImageDescriptor getImage()
	{
		return image;
	}

	/**
	 * Sets the image descriptor
	 * 
	 * @param image
	 */
	public void setImage(ImageDescriptor image)
	{
		this.image = image;
	}

	/**
	 * Get script
	 * 
	 * @return - javascript
	 */
	public String getScript()
	{
		return script;
	}

	/**
	 * Sets the javascript
	 * 
	 * @param script
	 */
	public void setScript(String script)
	{
		this.script = script;
	}

	/**
	 * Is save as allowed
	 * 
	 * @return true if allowed
	 */
	public boolean isSaveAsAllowed()
	{
		return saveAsAllowed;
	}

	/**
	 * Set save as as allowed
	 * 
	 * @param saveAsAllowed -
	 *            true if allowed
	 */
	public void setSaveAsAllowed(boolean saveAsAllowed)
	{
		this.saveAsAllowed = saveAsAllowed;
	}

	/**
	 * @return the saveAsFileName
	 */
	public String getSaveAsFileName()
	{
		return saveAsFileName;
	}

	/**
	 * @param saveAsFileName
	 *            the saveAsFileName to set
	 */
	public void setSaveAsFileName(String saveAsFileName)
	{
		this.saveAsFileName = saveAsFileName;
	}

	/**
	 * @return the saveAsFile
	 */
	public File getSaveAsFile()
	{
		return saveAsFile;
	}

	/**
	 * @param saveAsFile
	 *            the saveAsFile to set
	 */
	public void setSaveAsFile(File saveAsFile)
	{
		this.saveAsFile = saveAsFile;
	}

	/**
	 * @see org.eclipse.ui.internal.browser.WebBrowserEditorInput#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof CoreBrowserEditorInput && this.getURL() != null)
		{
			return this.getURL().equals(((CoreBrowserEditorInput) obj).getURL());
		}
		return super.equals(obj);
	}

	/**
	 * @return the onlyOneAllowed
	 */
	public boolean isOnlyOneAllowed()
	{
		return onlyOneAllowed;
	}

	/**
	 * @param onlyOneAllowed
	 *            the onlyOneAllowed to set
	 */
	public void setOnlyOneAllowed(boolean onlyOneAllowed)
	{
		this.onlyOneAllowed = onlyOneAllowed;
	}

	/**
	 * @return the alreadyOpened
	 */
	public boolean isAlreadyOpened()
	{
		return alreadyOpened;
	}

	/**
	 * @param alreadyOpened
	 *            the alreadyOpened to set
	 */
	public void setAlreadyOpened()
	{
		this.alreadyOpened = true;
	}

	/**
	 * @return the inputID
	 */
	public String getInputID()
	{
		return inputID;
	}

	/**
	 * @param inputID
	 *            the inputID to set
	 */
	public void setInputID(String inputID)
	{
		this.inputID = inputID;
	}

}
