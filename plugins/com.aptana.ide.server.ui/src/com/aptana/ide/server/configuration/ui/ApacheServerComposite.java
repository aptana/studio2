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
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.internal.ApacheServer;

/**
 * @author Pavel Petrochenko
 *
 */
public class ApacheServerComposite extends BasicServerComposite{

	private Text apacheStatup;
	private Text apacheRestart;
	private Text apacheStop;
	private Text portText;
	private Text hostText;
	
	/**
	 * @return String
	 */
	public String getEtcHostsPath(){
		return "";//etcHostsPath.getText(); //$NON-NLS-1$
	}
	
	/**
	 * @return String
	 */
	public String getApacheStart(){
		return apacheStatup.getText();
	}
	
	/**
	 * @return String
	 */
	public String getApacheStop(){
		return apacheStop.getText();
	}
	
	/**
	 * @return String
	 */
	public String getApacheRestart(){
		return apacheRestart.getText();
	}
	
	/**
	 * @param path
	 */
	public void setEtcHostsPath(String path){
		//this.etcHostsPath.setText(path);
	}
	/**
	 * @param value
	 */
	public void setApacheStart(String value){
		this.apacheStatup.setText(value);
	}
	
	/**
	 * @param value
	 */
	public void setApacheStop(String value){
		this.apacheStop.setText(value);
	}
	
	/**
	 * @param value
	 */
	public void setApacheRestart(String value){
		this.apacheRestart.setText(value);
	}
	
	

	/**
	 * @param parent
	 * @param style
	 * @param updater
	 * @param isNested 
	 */
	public ApacheServerComposite(Composite parent, int style, StatusUpdater updater, boolean isNested)
	{
		super(parent, style, updater, isNested);
		
		addExampleText(this, Messages.ApachePathHint);
		
		Label host = new Label(this, SWT.NONE);
		Composite lhost = new Composite(this, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		GridLayout ls = new GridLayout(3, false);
		ls.marginHeight = 0;
		ls.marginWidth = 0;
		lhost.setLayout(ls);
		lhost.setLayoutData(gd);
		hostText = new Text(lhost, SWT.BORDER);
		host.setText(Messages.ApacheServerComposite_HOST);
		Label port = new Label(lhost, SWT.NONE);
		port.setText(Messages.ApacheServerComposite_PORT);
		portText = new Text(lhost, SWT.BORDER);
		portText.setTextLimit(5);
		// Set the default port 80
		portText.setText("80"); //$NON-NLS-1$ 
		// Set a default 'localhost' string
		hostText.setText("localhost"); //$NON-NLS-1$
		hostText.addModifyListener(validationModifyListener);
		portText.addModifyListener(validationModifyListener);
		path.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				initHostAndPort();
			}
		});
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		hostText.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		portText.setLayoutData(gridData);
		Label startup = new Label(this, SWT.NONE);
		startup.setText(Messages.ApacheServerComposite_START_APACHE);
		apacheStatup = new Text(this, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		apacheStatup.setLayoutData(gridData);
		Label restart = new Label(this, SWT.NONE);
		restart.setText(Messages.ApacheServerComposite_RESTART_APACHE);
		apacheRestart = new Text(this, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		apacheRestart.setLayoutData(gridData);
		Label stop = new Label(this, SWT.NONE);
		stop.setText(Messages.ApacheServerComposite_STOP_APACHE);
		apacheStop = new Text(this, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		apacheStop.setLayoutData(gridData);
		
		addHorizontalSpace(this);
		
		addLogPath(this);
		addDocumentRootPath(this);
		addExampleText(this, Messages.ApacheDocRootHint);
	}
	
	private void initHostAndPort()
	{		
		if(path != null && !"".equals(path.getText())) //$NON-NLS-1$
		{
			File apachePath = new File(path.getText()).getParentFile().getParentFile();
			Properties readServerProperties = ApacheServer.readServerProperties(apachePath);
			String property = readServerProperties.getProperty("listen"); //$NON-NLS-1$
			setApacheHost(property);
		}
	}

	/**
	 * @return label
	 */
	protected String getPathLabel() {
		return Messages.ApacheServerComposite_Apache;
	}
	
	
	/**
	 * @see com.aptana.ide.server.configuration.ui.BasicServerComposite#validate()
	 */
	public boolean validate() {
		boolean validate = super.validate();
		if (!validate){
			return validate;
		}
		String host=this.hostText.getText();
		if (host.length()==0){
			updater.updateStatus(false, Messages.ApacheServerComposite_HOST_SHOULD_NOT_BE_EMPTY);			
			return false;
		}
		String name=this.portText.getText();
		if (name.length()==0){
			updater.updateStatus(false, Messages.ApacheServerComposite_PORT_SHOULD_NOT_BE_EMPTY);			
			return false;
		}
		try{
		int parseInt = Integer.parseInt(name);
		if (parseInt<1||parseInt>65535){
			updater.updateStatus(false, Messages.ApacheServerComposite_PORT_SHOULD_BE_BETWEEN);
			return false;
		}
		} catch (NumberFormatException e) {
			updater.updateStatus(false, Messages.ApacheServerComposite_PORT_SHOULD_BE_BETWEEN);
			return false;
		}
		return true;
//		String text = hhtpdPath.getText();
//		checkFile(text);
//		text = etcHostsPath.getText();
//		checkFile(text);
	}

	/**
	 * @param property 
	 * 
	 */
	public void setApacheHost(String property)
	{
		if (property!=null&&property.length()>0){
			int indexOf = property.indexOf(':');
			if (indexOf!=-1){
				this.hostText.setText(property.substring(0,indexOf));
				this.portText.setText(property.substring(indexOf+1));
			}
			else{
				this.hostText.setText("127.0.0.1"); //$NON-NLS-1$
				this.portText.setText(property);
			}
		}
	}
	
	/**
	 * @return apache host
	 */
	public String getApacheHost(){
		return StringUtils.format("{0}:{1}",new Object[]{hostText.getText(),portText.getText()}); //$NON-NLS-1$
	}

//	private void checkFile(String text) {
//		if (text.length()==0){
//			updater.updateStatus(true, "");
//			return;
//		}
//		File file = new File(text);
//		if (file.exists()) {
//			if (file.isFile()) {
//				updater.updateStatus(true, "");
//			}
//			else{ 
//				updater.updateStatus(false, "file should not be directory");
//			}
//		} else {
//			updater.updateStatus(false, "Specified path not exists, please specify correct path");
//		}
//	}
	
	

}
