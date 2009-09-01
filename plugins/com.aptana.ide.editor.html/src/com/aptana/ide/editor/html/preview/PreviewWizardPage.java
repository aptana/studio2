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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.ide.core.ui.wizards.BaseWizard;
import com.aptana.ide.core.ui.wizards.IBaseWizardPage;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.server.core.IServer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewWizardPage extends WizardPage implements IBaseWizardPage
{

	private Composite displayArea;
	private PreviewTypeSelectionBlock block;
	private Button defineProjectSettings;

	/**
	 * Preview wizard page
	 * 
	 * @param pageName
	 */
	public PreviewWizardPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.IBaseWizardPage#performFinish()
	 */
	public void performFinish()
	{
		try
		{
			IProject project = ((BaseWizard) getWizard()).getCreatedProject();
			if (defineProjectSettings.getSelection() && project != null)
			{
				String type = ""; //$NON-NLS-1$
				if (block.getServerButton().getSelection())
				{
					if (block.getServerAppendButton().getSelection())
					{
						type = HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE;
					}
					else
					{
						type = HTMLPreviewPropertyPage.SERVER_BASED_TYPE;
					}
				}
				else if (block.getConfigurationButton().getSelection())
				{
					type = HTMLPreviewPropertyPage.CONFIG_BASED_TYPE;
				}
				else if (block.getStartURLButton().getSelection())
				{
					type = HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE;
				}
				else
				{
					type = HTMLPreviewPropertyPage.FILE_BASED_TYPE;
				}
				String value = ""; //$NON-NLS-1$
				if (block.getServerButton().getSelection())
				{
					Object obj = block.getServerText().getData();
					if (obj != null && obj instanceof IServer)
					{
						value = ((IServer) obj).getId();
					}
					else
					{
						value = block.getServerText().getText();
					}
				}
				else if (block.getConfigurationButton().getSelection())
				{
					value = block.getConfigurationText().getText();
				}
				else if (block.getStartURLButton().getSelection())
				{
					value = block.getStartURLText().getText().trim();
				}

				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE), //$NON-NLS-1$
						type);
				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE), value); //$NON-NLS-1$
				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE), //$NON-NLS-1$
						HTMLPreviewPropertyPage.TRUE);
				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.CONTEXT_ROOT), "/"); //$NON-NLS-1$ //$NON-NLS-2$
				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_BROWSERS), //$NON-NLS-1$
						HTMLPlugin.getDefault().getPreferenceStore().getString(
								IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE));
			}
			else
			{
				project.setPersistentProperty(new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE), //$NON-NLS-1$
						HTMLPreviewPropertyPage.FALSE);
			}
		}
		catch (CoreException e)
		{
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			setPageComplete(true);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.verticalSpacing = 15;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		defineProjectSettings = new Button(displayArea, SWT.CHECK);
		defineProjectSettings.setText(Messages.PreviewWizardPage_LBL_DefineSettings);
		defineProjectSettings.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				block.setEnabled(defineProjectSettings.getSelection());
				block.updateControls();
			}

		});
		
		block = new PreviewTypeSelectionBlock();
		block.useSampleURL();

		String type = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
		String value = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		block.createStartActionSection(displayArea, type, value);
		block.setCurrentURLLabel(Messages.PreviewWizardPage_LBL_SampleURL);
		block.setEnabled(false);
		block.updateControls();
		block.updateCurrentURL();

		Link l = new Link(displayArea, SWT.CHECK);
		l.setText(Messages.PreviewWizardPage_LBL_PreferencesLink);
		l.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
						.getActiveShell(), "com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
						new String[] { "com.aptana.ide.editor.html.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
				dialog.open();
			}

		});		

		setControl(displayArea);
	}

}
