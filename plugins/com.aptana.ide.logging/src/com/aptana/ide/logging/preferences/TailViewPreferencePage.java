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
package com.aptana.ide.logging.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.colorizer.IErrorHandler;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.coloring.TokenTypes;

/**
 * Logging colorization preference page.
 * @author Denis Denisenko
 */
public class TailViewPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage, IErrorHandler
{

    /**
     * Main widget.
     */
    private LoggingPreferenceWidget mainWidget;

    /**
     * Provider.
     */
    private LoggingStructureProvider provider;
    
    /**
     * Creates a new colorization preference page
     */
    public TailViewPreferencePage()
    {
        setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        mainWidget = new LoggingPreferenceWidget();
        
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.heightHint = 700;
        parent.setData(gridData);
        mainWidget.createControl(parent, gridData);
        
        LoggingPreferences.createLanguageColorizer();
        provider = new LoggingStructureProvider();
        
        mainWidget.setProvider(provider);
        provider.refreshTokens();
        mainWidget.setErrorHandler(this);
        
        
        return mainWidget.getControl();
    }

	/**
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose()
    {
        mainWidget.dispose();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
        
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performApply()
     */
    protected void performApply()
    {
        mainWidget.saveStyles();
        super.performApply();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        mainWidget.resetToDefaults();
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        mainWidget.saveStyles();
        provider.applyChanges();
        mainWidget.getProvider().buildLanguageColorizer(
                LanguageRegistry.getLanguageColorizer(TokenTypes.LANGUAGE),
                LoggingPreferences.COLORIZER_ID);
        return super.performOk();
    }

    /**
      * {@inheritDoc}
      */
    @Override
    public void applyData(Object data)
    {
        if (data != null && data instanceof Integer)
        {
            int tabNumber = ((Integer) data);
            if (mainWidget != null)
            {
                mainWidget.activateTab(tabNumber);
            }
        }
    }

    /**
     * @see com.aptana.ide.editors.unified.colorizer.IErrorHandler#setErrorMessage(java.lang.String)
     */
    public void setErrorMessage(String message)
    {
    	super.setErrorMessage(message);
    	setValid(message == null);
    }

}
