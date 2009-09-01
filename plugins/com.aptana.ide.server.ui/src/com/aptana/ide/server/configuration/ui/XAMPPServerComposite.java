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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 *
 */
public class XAMPPServerComposite extends BasicServerComposite {

	private Text stoppath;

	/**
	 * XAMPPServerComposite constructor.
	 * @param parent - parent.
	 * @param style - style.
	 * @param updater - updater.
	 */
	public XAMPPServerComposite(Composite parent, int style,
			StatusUpdater updater, boolean requiresPath) {
		super(parent, style, updater,false, requiresPath);
//		Label label = new Label(this, SWT.NONE);
//		label.setText(Messages.XAMPPServerComposite_STOP);
//		stoppath = new Text(this, SWT.BORDER);
//		stoppath.addModifyListener(new ModifyListener() {
//
//			public void modifyText(ModifyEvent e) {
//				validate();
//
//			}
//
//			
//
//		});
//		stoppath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		Button browser = new Button(this, SWT.PUSH);
//		browser.setText(Messages.XAMPPServerComposite_BROWSE);
//		browser.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				FileDialog fdlg = new FileDialog(getShell(), SWT.OPEN);
//				String text = stoppath.getText();
//				if (text.length() > 0) {
//					File file = new File(text);
//					File parentFile = file.getParentFile();
//					if (parentFile!=null){ 
//					fdlg.setFilterPath(parentFile.getPath());
//					}
//				}
//				String open = fdlg.open();
//				if (open != null) {
//					stoppath.setText(open);
//				}
//			}
//		});
		addLogPath(this);
		// TODO - add a path to the document root.
		// addDocumentRootPath(this);
		// addExampleText(this, Messages.ApacheDocRootHint);
	}
	
	
	/**
	 * @see com.aptana.ide.server.configuration.ui.BasicServerComposite#validate()
	 */
	public boolean validate() {
		IdeLog.logInfo(ServerUIPlugin.getDefault(), Messages.XAMPPServerComposite_INF_Validate);
		if (super.validate())
		{
//			IdeLog.logInfo(ServerUIPlugin.getDefault(), "Inside validating stop path.");
//			
//			String originalStopPath = stoppath.getText();
//			IdeLog.logInfo(ServerUIPlugin.getDefault(), "Full stop path is: " + originalStopPath);
//			String filePath = ServerPathUtils.getFileNameByPathWithParameters(originalStopPath);
//			IdeLog.logInfo(ServerUIPlugin.getDefault(), "File path is: " + filePath);
//			if (filePath == null)
//			{
//				updater.updateStatus(false, Messages.XAMPPServerComposite_NOT_DIR);
//				return false;
//			}
//			File file = new File(filePath);
//			IdeLog.logInfo(ServerUIPlugin.getDefault(), "Before checking file existence");
//			if (file.exists()) {
//				IdeLog.logInfo(ServerUIPlugin.getDefault(), "File does exist");
//				if (file.isFile()) {
//					IdeLog.logInfo(ServerUIPlugin.getDefault(), "File checking complete sucessfully");
//					updater.updateStatus(true, ""); //$NON-NLS-1$
//					return true;
//				}
//				else{ 
//					updater.updateStatus(false, Messages.XAMPPServerComposite_NOT_DIR);
//					return false;
//				}
//			} else {
//				IdeLog.logInfo(ServerUIPlugin.getDefault(), "File does NOT exist");
//				updater.updateStatus(false, Messages.XAMPPServerComposite_PATH_NOT_EXIST);
//				return false;
//			}
			return true;
		}
		return false;
	}
	
	/**
	 * @return label
	 */
	protected String getPathLabel() {
		return Messages.XAMPPServerComposite_START;
	}
	
	/**
	 * @return path
	 */
	public String getServerStopPath() {
		if (stoppath != null)
		{
			return stoppath.getText();
		}
		
		return null;
	}
	
	/**
	 * @param serverPath
	 */
	public void setServerStartPath(String serverPath){
		if (stoppath != null)
		{
			this.stoppath.setText(serverPath);
		}
		validate();
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean pathMustBeFile()
	{
		return false;
	}
}