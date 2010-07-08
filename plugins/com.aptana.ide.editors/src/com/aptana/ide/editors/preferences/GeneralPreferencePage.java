/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.preferences.TabbedFieldEditorPreferencePage;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class GeneralPreferencePage extends TabbedFieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private Scale pianoKeySlider;

	/**
	 * Color selector for occurrence highlighting
	 */
	protected ColorSelector occurrenceColor;

	/**
	 * Button for enabling occurrence highlighting
	 */
	protected Button enableOccurrences;

	private Button spaces;
	private Button tabs;

	private IPropertyChangeListener tabWidthListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			if (spaces != null && !spaces.isDisposed())
			{
				spaces.setText(StringUtils.format(Messages.GeneralPreferencePage_UseSpaces, event.getNewValue()));
			}
		}

	};

	/**
	 * GeneralPreferencePage
	 */
	public GeneralPreferencePage()
	{
		super(GRID);
		setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.GeneralPreferencePage_PreferenceDescription);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		addTab(Messages.GeneralPreferencePage_General);

		Composite appearanceComposite = getFieldEditorParent();
		Composite group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.GeneralPreferencePage_Formatting);

		Composite occurrenceComp = new Composite(group, SWT.NONE);
		GridLayout occLayout = new GridLayout(2, false);
		occLayout.marginWidth = 0;
		occLayout.marginHeight = 0;
		occurrenceComp.setLayout(occLayout);
		occurrenceComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		enableOccurrences = new Button(occurrenceComp, SWT.CHECK);

		enableOccurrences.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				occurrenceColor.setEnabled(enableOccurrences.getSelection());
			}

		});
		enableOccurrences.setSelection(getPreferenceStore().getBoolean(
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED));
		enableOccurrences.setText(Messages.GeneralPreferencePage_MarkOccurrences);
		occurrenceColor = new ColorSelector(occurrenceComp);
		occurrenceColor.setEnabled(enableOccurrences.getSelection());
		occurrenceColor.setColorValue(PreferenceConverter.getColor(getPreferenceStore(),
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR));

		// addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_WORD_WRAP,
		// Messages.GeneralPreferencePage_EnableWordWrap, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.INSERT_ON_TAB,
				Messages.GeneralPreferencePage_InsertSelectedProposal, group));

		addField(new RadioGroupFieldEditor(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END,
				Messages.GeneralPreferencePage_HomeEndBehavior, 1, new String[][] {
						{ Messages.GeneralPreferencePage_ToggleBetween, "true" }, //$NON-NLS-1$
						{ Messages.GeneralPreferencePage_JumpsStartEnd, "false" } }, //$NON-NLS-1$
				appearanceComposite, true));

		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.GeneralPreferencePage_LBL_Colorization);
		Composite pianoKeyComp = new Composite(group, SWT.NONE);
		GridLayout pkcLayout = new GridLayout(3, false);
		pkcLayout.marginWidth = 0;
		pkcLayout.marginHeight = 0;
		pianoKeyComp.setLayout(pkcLayout);
		pianoKeyComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Label pianoKeyLabel = new Label(pianoKeyComp, SWT.LEFT);
		pianoKeyLabel.setText(Messages.GeneralPreferencePage_LBL_PianoKeyColorDifference);
		GridData pklData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pklData.horizontalSpan = 3;
		pianoKeyLabel.setLayoutData(pklData);
		Label less = new Label(pianoKeyComp, SWT.LEFT);
		less.setText(Messages.GeneralPreferencePage_LBL_Less);
		pianoKeySlider = new Scale(pianoKeyComp, SWT.HORIZONTAL);
		pianoKeySlider.setIncrement(5);
		pianoKeySlider.setMinimum(1);
		pianoKeySlider.setMaximum(50);
		pianoKeySlider.setSelection(getPreferenceStore().getInt(IPreferenceConstants.PIANO_KEY_DIFFERENCE));
		Label more = new Label(pianoKeyComp, SWT.LEFT);
		more.setText(Messages.GeneralPreferencePage_LBL_More);

		Composite wsGroup = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.GeneralPreferencePage_TabInsertion);
		Composite wsComp = new Composite(wsGroup, SWT.NONE);

		GridLayout wsLayout = new GridLayout(3, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsComp.setLayout(wsLayout);
		wsComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		tabs = new Button(wsComp, SWT.RADIO);
		Composite spaceComp = new Composite(wsComp, SWT.NONE);
		wsLayout = new GridLayout(2, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsLayout.horizontalSpacing = 0;
		spaceComp.setLayout(wsLayout);
		spaceComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spaces = new Button(spaceComp, SWT.RADIO);
		final Link currentTabSize = new Link(spaceComp, SWT.NONE);
		IPreferenceStore store = EditorsPlugin.getDefault().getPreferenceStore();
		int size = store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		spaces.setText(StringUtils.format(Messages.GeneralPreferencePage_UseSpaces, size));
		tabs.setText(Messages.GeneralPreferencePage_UseTabs);
		store.addPropertyChangeListener(tabWidthListener);
		currentTabSize.setText(Messages.GeneralPreferencePage_EditLink);
		currentTabSize.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
			}

		});
		boolean useSpaces = getPreferenceStore().getBoolean(IPreferenceConstants.INSERT_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);
		tabs.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				spaces.setSelection(!tabs.getSelection());
			}

		});
		spaces.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				tabs.setSelection(!spaces.getSelection());
			}

		});
		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.GeneralPreferencePage_GeneralTextEditorPrefLink);
		link.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
			}

		});
		
		addTab(Messages.GeneralPreferencePage_Advanced);
		appearanceComposite = getFieldEditorParent();

		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_WORD_WRAP,
				Messages.GeneralPreferencePage_EnableWordWrap, appearanceComposite));

		appearanceComposite = getFieldEditorParent();
		addField(new IntegerFieldEditor(IPreferenceConstants.COLORIZER_MAXCOLUMNS,
				Messages.GeneralPreferencePage_MaxColorizeColumns, appearanceComposite, 4));
		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.GeneralPreferencePage_CodeAssist);

		addField(new IntegerFieldEditor(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				Messages.GeneralPreferencePage_DelayBeforeShowing, group, 4));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	public void dispose()
	{
		IPreferenceStore store = EditorsPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(tabWidthListener);
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		enableOccurrences.setSelection(getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED));
		occurrenceColor.setColorValue(PreferenceConverter.getDefaultColor(getPreferenceStore(),
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR));
		occurrenceColor.setEnabled(enableOccurrences.getSelection());
		boolean useSpaces = getPreferenceStore().getDefaultBoolean(IPreferenceConstants.INSERT_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);
		pianoKeySlider.setSelection(getPreferenceStore().getDefaultInt(IPreferenceConstants.PIANO_KEY_DIFFERENCE));
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		getPreferenceStore().setValue(IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED,
				enableOccurrences.getSelection());
		getPreferenceStore().setValue(IPreferenceConstants.INSERT_SPACES_FOR_TABS, spaces.getSelection());
		PreferenceConverter.setValue(getPreferenceStore(),
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR, occurrenceColor.getColorValue());
		getPreferenceStore().setValue(IPreferenceConstants.PIANO_KEY_DIFFERENCE, pianoKeySlider.getSelection());
		LanguageColorizer.fireColorizationEvent();
		return super.performOk();
	}

}
