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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.editors.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PlatformUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.core.ui.preferences.IPreferencesConstants2;
import com.aptana.ide.core.ui.preferences.TabbedFieldEditorPreferencePage;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.epl.Activator;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class AdvancedPreferencePage extends TabbedFieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private static final String IE_PREVIEW_KEY = "HKLM\\SOFTWARE\\Microsoft\\Internet Explorer\\View Source Editor\\Editor Name"; //$NON-NLS-1$
	private static final String IE_PREVIEW_NOTEPAD_VALUE = "notepad.exe"; //$NON-NLS-1$

	private Button notepad;
	private Button otherButton;
	private Text text;
	private Button browse;
	private Scale debugSlider;
	private FieldEditor switchPerspectiveField;

	/**
	 * GeneralPreferencePage
	 */
	public AdvancedPreferencePage()
	{
		super(GRID);
		setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.AdvancedPreferencePage_DebuggingAndAdvanced);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		addTab(Messages.AdvancedPreferencePage_User);
		Composite appearanceComposite = getFieldEditorParent();
		addField(new StringFieldEditor(com.aptana.ide.core.ui.preferences.IPreferenceConstants.PREF_USER_NAME,
				com.aptana.ide.core.ui.preferences.Messages.GeneralPreferencePage_EmailAddressForBugReports,
				appearanceComposite));
		switchPerspectiveField = new RadioGroupFieldEditor(IPreferencesConstants2.SWITCH_TO_APTANA_PRESPECTIVE,
				Messages.AdvancedPreferencePage_switchToAptanaPerspective, 3, new String[][] {
						{ Messages.AdvancedPreferencePage_Always, MessageDialogWithToggle.ALWAYS },
						{ Messages.AdvancedPreferencePage_Never, MessageDialogWithToggle.NEVER },
						{ Messages.AdvancedPreferencePage_Prompt, MessageDialogWithToggle.PROMPT } },
						appearanceComposite, true);
		addField(switchPerspectiveField);
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			Group ieGroup = new Group(appearanceComposite, SWT.NONE);
			GridData ieData = new GridData(SWT.FILL, SWT.FILL, true, true);
			ieData.horizontalSpan = 2;
			ieGroup.setLayoutData(ieData);
			ieGroup.setLayout(new GridLayout(1, true));
			ieGroup.setText(Messages.AdvancedPreferencePage_IESettings);

			notepad = new Button(ieGroup, SWT.RADIO);
			notepad.setText(Messages.AdvancedPreferencePage_AssociateWithNotepad);
			notepad.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					setErrorMessage(null);
					setValid(true);
				}

			});

			otherButton = new Button(ieGroup, SWT.RADIO);
			otherButton.setText(Messages.AdvancedPreferencePage_AssociateWithOther);

			Composite other = new Composite(ieGroup, SWT.NONE);
			GridLayout otherLayout = new GridLayout(2, false);
			otherLayout.marginHeight = 0;
			other.setLayout(otherLayout);
			other.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			text = new Text(other, SWT.BORDER | SWT.SINGLE);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			text.setEditable(false);
			text.setEnabled(false);
			browse = new Button(other, SWT.PUSH);
			browse.setEnabled(false);
			browse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
			browse.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					FileDialog dialog = new FileDialog(browse.getShell(), SWT.OPEN);
					String program = dialog.open();
					if (program != null)
					{
						text.setText(program);
						setErrorMessage(null);
						setValid(true);
					}
				}

			});
			otherButton.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					boolean selection = otherButton.getSelection();
					browse.setEnabled(selection);
					text.setEnabled(selection);
					if (!selection)
					{
						text.setText(""); //$NON-NLS-1$
					}
					else
					{
						if (text.getText().length() < 1)
						{
							setErrorMessage(Messages.AdvancedPreferencePage_PleaseSpecifyApplication);
							setValid(false);
						}
					}
				}

			});
			String current = null;
			try
			{
				current = PlatformUtils.queryRegestryStringValue(IE_PREVIEW_KEY, null);
			}
			catch (Exception e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.AdvancedPreferencePage_ERR_ErrorGettingRegistryValue, e);
			}
			if (current != null)
			{
				if (current.equalsIgnoreCase(IE_PREVIEW_NOTEPAD_VALUE) || current.endsWith(IE_PREVIEW_NOTEPAD_VALUE))
				{
					notepad.setSelection(true);
				}
				else
				{
					otherButton.setSelection(true);
					browse.setEnabled(true);
					text.setEnabled(true);
					text.setText(current);
				}
			}
		}
		addTab(Messages.AdvancedPreferencePage_Debugging);

		appearanceComposite = getFieldEditorParent();
		Composite group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.AdvancedPreferencePage_LBL_AdvancedFunctionality);

		addField(new BooleanFieldEditor(IPreferenceConstants.SHOW_DEBUG_HOVER,
				Messages.AdvancedPreferencePage_ShowDebugInformation, group));
		
//		addField(new BooleanFieldEditor(com.aptana.ide.core.preferences.IPreferenceConstants.SHOW_LIVE_HELP,
//				"Show live help", group));
		
		addField(new BooleanFieldEditor(IPreferenceConstants.PARSER_OFF_UI,
				Messages.AdvancedPreferencePage_LBL_ParserOffUI, group));

		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
		Messages.AdvancedPreferencePage_LBL_DebuggingOutputLevel);

		//addField(new BooleanFieldEditor(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_ENABLE_DEBUGGING,
		//		Messages.AdvancedPreferencePage_LogDebuggingMessages, appearanceComposite));

		Composite debugComp = new Composite(group, SWT.NONE);
		GridLayout pkcLayout = new GridLayout(3, false);
		pkcLayout.marginWidth = 0;
		pkcLayout.marginHeight = 0;
		debugComp.setLayout(pkcLayout);
		debugComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Label pianoKeyLabel = new Label(debugComp, SWT.LEFT);
		pianoKeyLabel.setText(Messages.AdvancedPreferencePage_LBL_ControlDebugInformationAmountHelp);
		GridData pklData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pklData.horizontalSpan = 3;
		pianoKeyLabel.setLayoutData(pklData);
		Label less = new Label(debugComp, SWT.LEFT);
		
		less.setText(Messages.AdvancedPreferencePage_LBL_Errors);
		debugSlider = new Scale(debugComp, SWT.HORIZONTAL);
		debugSlider.setIncrement(1);
		debugSlider.setMinimum(1);
		debugSlider.setMaximum(3);

		Preferences p = AptanaCorePlugin.getDefault().getPluginPreferences();
		debugSlider.setSelection(p.getInt(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_DEBUG_LEVEL));
		Label more = new Label(debugComp, SWT.LEFT);
		more.setText(Messages.AdvancedPreferencePage_LBL_All);

		final Label currentValue = new Label(debugComp, SWT.LEFT);
		currentValue.setText(getValueLabel(debugSlider.getSelection()));
		currentValue.setFont(SWTUtils.getDefaultSmallFont());
		pklData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pklData.horizontalSpan = 3;
		currentValue.setLayoutData(pklData);

		debugSlider.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent selectionevent)
			{
				currentValue.setText(getValueLabel(debugSlider.getSelection()));
			}

			public void widgetSelected(SelectionEvent selectionevent)
			{
				currentValue.setText(getValueLabel(debugSlider.getSelection()));
			}
			
		});		
	}

	/**
	 * Override the initialize to fix the preferences store assignment for the different fields.
	 */
	protected void initialize()
	{
		super.initialize();
		switchPerspectiveField.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		switchPerspectiveField.load();
	}

	/**
	 * Returns the logging value names
	 * @param selection
	 * @return
	 */
	private String getValueLabel(int selection)
	{
		switch(selection)
		{
			case 0:
				return Messages.AdvancedPreferencePage_LBL_NoDebuggingOutput;
			case 1:
				return Messages.AdvancedPreferencePage_LBL_OnlyError;
			case 2:
				return Messages.AdvancedPreferencePage_LBL_ErrorsAndImportant;
			case 3:
				return Messages.AdvancedPreferencePage_LBL_AllDebuggingInformation;
			default:
				return Messages.AdvancedPreferencePage_LBL_UnknownLoggingLevel;
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		debugSlider.setSelection(getPreferenceStore().getDefaultInt(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_DEBUG_LEVEL));
		super.performDefaults();
	}

	/**
	 * Method declared on IPreferencePage. Subclasses should override
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{

		boolean ok = super.performOk();
		boolean saved = false;
		boolean optionSelected = false;
		if (ok)
		{
			if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				try
				{
					if (notepad.getSelection())
					{
						saved = PlatformUtils.setRegestryStringValue(IE_PREVIEW_KEY, null, IE_PREVIEW_NOTEPAD_VALUE);
						optionSelected = true;
					}
					else if (otherButton.getSelection())
					{
						saved = PlatformUtils.setRegestryStringValue(IE_PREVIEW_KEY, null, text.getText());
						optionSelected = true;
					}
					if (!saved && optionSelected)
					{
						throw new Exception("Registry value not saved"); //$NON-NLS-1$
					}
				}
				catch (Exception e)
				{
					MessageBox error = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
					error.setMessage(Messages.AdvancedPreferencePage_ErrorSettingRegistry
							+ Messages.AdvancedPreferencePage_CheckPrivileges);
					error.open();
				}
			}
			IPreferenceStore unified = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			boolean value = unified
					.getBoolean(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_ENABLE_DEBUGGING);
			Preferences p = AptanaCorePlugin.getDefault().getPluginPreferences();
			p.setValue(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_ENABLE_DEBUGGING, value);
			p.setValue(com.aptana.ide.core.preferences.IPreferenceConstants.PREF_DEBUG_LEVEL, debugSlider.getSelection());

//			value = unified.getBoolean(com.aptana.ide.core.preferences.IPreferenceConstants.SHOW_LIVE_HELP);
//			p.setValue(com.aptana.ide.core.preferences.IPreferenceConstants.SHOW_LIVE_HELP, value);
			AptanaCorePlugin.getDefault().savePluginPreferences();
		}

		return ok;
	}
}
