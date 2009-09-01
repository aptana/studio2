/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.ui.editors.preferences.formatter;

import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.core.resources.IProject;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;


import com.aptana.ide.internal.ui.dialogs.PreferencesAccess;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileConfigurationBlock;

/**
 * 
 *
 */
public abstract class ProfilePreferencePage extends PropertyAndPreferencePage {

	private ProfileConfigurationBlock fConfigurationBlock;
	

	/**
	 * @param store
	 */
	public ProfilePreferencePage(IPreferenceStore store) {
		super(store);
	}

	/**
	 * @param access
	 * @return ProfileConfigurationBlock
	 */
	protected abstract ProfileConfigurationBlock createConfigurationBlock(PreferencesAccess access);

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
    	IPreferencePageContainer container= getContainer();
    	IWorkingCopyManager workingCopyManager;
    	if (container instanceof IWorkbenchPreferenceContainer) {
    		workingCopyManager= ((IWorkbenchPreferenceContainer) container).getWorkingCopyManager();
    	} else {
    		workingCopyManager= new WorkingCopyManager(); // non shared 
    	}
    	PreferencesAccess access= PreferencesAccess.getWorkingCopyPreferences(workingCopyManager);
    	fConfigurationBlock= createConfigurationBlock(access);
    	
    	super.createControl(parent);
    }

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#createPreferenceContent(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createPreferenceContent(Composite composite) {
    	return fConfigurationBlock.createContents(composite);
    }

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#hasProjectSpecificOptions(org.eclipse.core.resources.IProject)
	 */
	protected boolean hasProjectSpecificOptions(IProject project) {
    	return fConfigurationBlock.hasProjectSpecificOptions(project);
    }

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#enableProjectSpecificSettings(boolean)
	 */
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
    	super.enableProjectSpecificSettings(useProjectSpecificSettings);
    	if (fConfigurationBlock != null) {
    		fConfigurationBlock.enableProjectSpecificSettings(useProjectSpecificSettings);
    	}
    }

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
    	if (fConfigurationBlock != null) {
    		fConfigurationBlock.dispose();
    	}
    	super.dispose();
    }

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
    	if (fConfigurationBlock != null) {
    		fConfigurationBlock.performDefaults();
    	}
    	super.performDefaults();
    }

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
    	if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
    		return false;
    	}	
    	return super.performOk();
    }

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	public void performApply() {
    	if (fConfigurationBlock != null) {
    		fConfigurationBlock.performApply();
    	}	
    	super.performApply();
    }

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element) {
    	super.setElement(element);
    	setDescription(null); // no description for property page
    }

}
