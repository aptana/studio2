/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.configuration.ui;

import java.io.File;
import java.util.HashSet;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.server.core.ServerPathUtils;

/**
 * @author Pavel Petrochenko
 */
public class BasicServerComposite extends Composite
{

	/**
	 * The number of columns that this composite span to.
	 * <code>COLUMNS = 3</code>
	 */
	protected static final int COLUMNS = 3;
	/**
	 * path field of composite
	 */
	protected Text path;
	/**
	 * Updater
	 */
	protected StatusUpdater updater;
	protected Text name;
	private HashSet<String> serverNames = new HashSet<String>();
	protected Text descriptionText;
	private boolean isNested;
	protected Text log;
	protected ModifyListener validationModifyListener;
	
	/**
	 * Whether path is required.
	 */
	protected boolean requiresPath = true;
	protected Text docRoot;

	/**
	 * @author Pavel Petrochenko
	 */
	public abstract static class StatusUpdater
	{

		/**
		 * @param isOk
		 * @param message
		 */
		public abstract void updateStatus(boolean isOk, String message);
	}
	
	/**
	 * Whether the child requires path to be specified.
	 * @return true if requires, false otherwise.
	 */
	private boolean requiresPath()
	{
		return requiresPath;
	}
	
	/**
	 * Whether path must be file.
	 * @return true if must be file, false if folder instead.
	 */
	protected boolean pathMustBeFile()
	{
		return true;
	}

	/**
	 * @return is configuration valid
	 */
	public boolean validate()
	{
		if (requiresPath())
		{
			String originalPath = path.getText();
			String filePath = ServerPathUtils.getFileNameByPathWithParameters(originalPath);
			if (filePath == null)
			{
				updater.updateStatus(false, Messages.BasicServerComposite_FILE_SHOULD_BE_DIR);
				return false;
			}
			File file = new File(filePath);
			if (!isNested)
			{
				String name = this.name.getText();
				if (name.length() == 0)
				{
					updater.updateStatus(false, Messages.BasicServerComposite_EMPTY_NAME);
					return false;
				}
				if (serverNames.contains(name))
				{
					updater.updateStatus(false, Messages.BasicServerComposite_DUBLICATE_NAME);
					return false;
				}
			}
			if (file.exists())
			{
				if (!pathMustBeFile() || file.isFile())
				{
					updater.updateStatus(true, ""); //$NON-NLS-1$
					if (log!=null){
						String text = log.getText();
						if (text.length()!=0){
							file = new File(text);
							if (!file.exists()||!file.isFile()){
								updater.updateStatus(false, Messages.BasicServerComposite_LOG_SHOULD_BE_EMPTY_OR_POINT_TO_FILE);
								return false;
							}
						}
					}
				}
				else
				{
					updater.updateStatus(false, Messages.BasicServerComposite_FILE_SHOULD_BE_DIR);
					return false;
				}
			}
			else
			{
				updater.updateStatus(false, Messages.BasicServerComposite_PATH_NOT_EXISTS);
				return false;
			}
			if (docRoot != null && docRoot.isEnabled())
			{
				String documentRoot = docRoot.getText().trim();
				if (documentRoot.length() == 0 || !new File(documentRoot).isDirectory())
				{
					updater.updateStatus(false, Messages.BasicServerComposite_DOCUMENT_ROOT_ERROR);
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param parent
	 * @param style
	 * @param updater
	 * @param isNested
	 */
	public BasicServerComposite(Composite parent, int style, final StatusUpdater updater, 
			boolean isNested, boolean requiresPath)
	{
		super(parent, style);
		this.requiresPath = requiresPath;
		initialize(updater, isNested);
	}

	/**
	 * @param parent
	 * @param style
	 * @param updater
	 * @param isNested
	 */
	public BasicServerComposite(Composite parent, int style, final StatusUpdater updater, boolean isNested)
	{
		super(parent, style);
		initialize(updater, isNested);
	}

	private void initialize(final StatusUpdater updater, boolean isNested)
	{
		this.updater = updater;
		validationModifyListener = new ValidationModifyListener();
		this.setLayout(new GridLayout(COLUMNS, false));
		this.isNested = isNested;
		if (!isNested)
		{			
			Label nlabel = new Label(this, SWT.NONE);
			nlabel.setText(Messages.BasicServerComposite_NAME);
			name = new Text(this, SWT.BORDER);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			name.setLayoutData(gridData);
			Label ds = new Label(this, SWT.NONE);
			ds.setText(Messages.BasicServerComposite_Description);
			descriptionText = new Text(this, SWT.BORDER);
			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
			gridData2.horizontalSpan = 2;
			descriptionText.setLayoutData(gridData);
		}
		if (requiresPath())
		{
			Label label = new Label(this, SWT.NONE);
			label.setText(getPathLabel());
			path = new Text(this, SWT.BORDER);
			path.addModifyListener(validationModifyListener);
			if (name != null)
			{
				name.addModifyListener(validationModifyListener);
			}
			path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Button browser = new Button(this, SWT.PUSH);
			browser.setText(Messages.BasicServerComposite_BROWSE);
			browser.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(pathMustBeFile())
					{
						FileDialog fdlg = new FileDialog(getShell(), SWT.OPEN);
						String text = path.getText();
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
							path.setText(open);
						}
					}
					else
					{
						DirectoryDialog fdlg = new DirectoryDialog(getShell(), SWT.OPEN);
						String text = path.getText();
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
							path.setText(open);
						}
					}
				}
			});
		}
	}

	/**
	 * @return label
	 */
	protected String getPathLabel()
	{
		return Messages.BasicServerComposite_PATH;
	}

	/**
	 * @param names
	 */
	public void setServerNames(HashSet<String> names)
	{
		this.serverNames = names;
	}

	/**
	 * @return path
	 */
	public String getServerPath()
	{
		if (path != null)
		{
			return path.getText();
		}
		return null;
	}

	/**
	 * @param serverPath
	 */
	public void setServerPath(String serverPath)
	{
		if (path != null)
		{
			this.path.setText(serverPath);
		}
		validate();
	}

	/**
	 * @param name
	 */
	public void setServerName(String name)
	{
		if (this.name != null)
		{
			this.name.setText(name);
		}
	}

	/**
	 * @param name
	 */
	public void setServerDescription(String name)
	{
		if (this.descriptionText != null)
		{
			this.descriptionText.setText(name);
		}
	}

	/**
	 * @return name
	 */
	public String getServerName()
	{
		return this.name == null ? "" : this.name.getText(); //$NON-NLS-1$
	}

	/**
	 * @return description
	 */
	public String getServerDescription()
	{
		return this.descriptionText == null ? "" : this.descriptionText.getText(); //$NON-NLS-1$
	}

	
	/**
	 * @return path to log
	 */
	public String getLogPath(){
		return this.log == null ? "" : this.log.getText(); //$NON-NLS-1$
	}
	
	/**
	 * @param path
	 */
	public void setLogPath(String path){
		if (path==null){
			path=""; //$NON-NLS-1$
		}
		if (this.log!=null){
			this.log.setText(path);
		}
	}

	/**
	 * Returns the document root.
	 * 
	 * @return The doc root, or empty string in case it is not available or not set.
	 */
	public String getDocumentRoot() {
		if (docRoot == null) {
			return ""; //$NON-NLS-1$
		}
		return docRoot.getText();
	}
	
	/**
	 * Sets the document root.
	 * @param path
	 * @throws IllegalStateException in case this method is called before the document root field was initialize.
	 */
	public void setDocumentRoot(String root) throws IllegalStateException
	{
		if (docRoot == null)
		{
			throw new IllegalStateException("Cannot set " + root //$NON-NLS-1$
					+ ". The document root text component was not initialized."); //$NON-NLS-1$
		}
		docRoot.setText(root);
	}
	
	/**
	 * Adds a horizontal space. 
	 */
	protected void addHorizontalSpace(Composite parent) {
		Label spacer = new Label(this, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = COLUMNS;
		spacer.setLayoutData(layoutData);
	}
	
	protected void addDocumentRootPath(Composite parent) {
		// Add the document root text field
		Label docRootLabel = new Label(parent, SWT.NONE);
		docRootLabel.setText(Messages.BasicServerComposite_DOC_ROOT);
		docRoot = new Text(parent, SWT.BORDER);
		docRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		docRoot.addModifyListener(validationModifyListener);
		Button browseBt = new Button(parent, SWT.PUSH);
		browseBt.setText(Messages.BasicServerComposite_BROWSE);
		browseBt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				String path = dialog.open();
				if (path != null) {
					docRoot.setText(path);
				}
			}
		});
	}
	
	protected void addLogPath(Composite parent){
		Label label=new Label(parent,SWT.NONE);
		label.setText(Messages.BasicServerComposite_LOG_PATH_TITLE);
		log=new Text(parent,SWT.BORDER);
		log.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button browser = new Button(parent, SWT.PUSH);
		browser.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fdlg = new FileDialog(getShell(), SWT.OPEN);
				String text = log.getText();
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
					log.setText(open);
				}
			}
		});
		log.addModifyListener(validationModifyListener);
		browser.setText(Messages.BasicServerComposite_LOG_PATH_CHOOSE);
	}
	
	/**
	 * Adds an example text label. 
	 */
	protected void addExampleText(Composite parent, String text)
	{
		new Label(parent,SWT.NONE);
		Label banner=new Label(this,SWT.NONE);
		banner.setText(text);
		Font defaultFont = JFaceResources.getDefaultFont();
		final Font smallFont = new Font(banner.getDisplay(),defaultFont.getFontData()[0].getName(),8,SWT.NONE);
		banner.setFont(smallFont);
		banner.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e)
			{
				smallFont.dispose();				
			}
			
		});
		GridData gds=new GridData(GridData.FILL_HORIZONTAL);
		gds.horizontalSpan=2;
		gds.verticalIndent=-3;
		gds.verticalAlignment=SWT.TOP;
		banner.setLayoutData(gds);
	}
	
	private class ValidationModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e)
		{
			validate();
		}
	}
}
