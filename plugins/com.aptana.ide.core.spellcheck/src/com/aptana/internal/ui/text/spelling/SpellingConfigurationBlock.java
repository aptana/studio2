/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * Options configuration block for spell check related settings.
 * 
 * @since 3.0
 */
public class SpellingConfigurationBlock extends OptionsConfigurationBlock {

	/**
	 * Tells whether content assist proposal block should be shown. Currently
	 * the spelling engine cannot return word proposals but only correction
	 * proposals and hence this is disabled.
	 * 
	 * @since 3.3
	 */
	private static final boolean SUPPORT_CONTENT_ASSIST_PROPOSALS = false;

	/** Preference keys for the preferences in this block */
	private static final Key PREF_SPELLING_IGNORE_DIGITS = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_DIGITS);
	private static final Key PREF_SPELLING_IGNORE_MIXED = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_MIXED);
	private static final Key PREF_SPELLING_IGNORE_SENTENCE = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_SENTENCE);
	private static final Key PREF_SPELLING_IGNORE_UPPER = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_UPPER);
	// private static final Key PREF_SPELLING_IGNORE_JAVA_STRINGS=
	// getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_JAVA_STRINGS);
	private static final Key PREF_SPELLING_IGNORE_SINGLE_LETTERS = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_SINGLE_LETTERS);
	private static final Key PREF_SPELLING_IGNORE_NON_LETTERS = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_NON_LETTERS);
	private static final Key PREF_SPELLING_IGNORE_URLS = getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_URLS);
	// private static final Key PREF_SPELLING_IGNORE_AMPERSAND_IN_PROPERTIES=
	// getJDTUIKey(PreferenceConstants.SPELLING_IGNORE_AMPERSAND_IN_PROPERTIES);
	private static final Key PREF_SPELLING_LOCALE = getJDTUIKey(PreferenceConstants.SPELLING_LOCALE);
	private static final Key PREF_SPELLING_PROPOSAL_THRESHOLD = getJDTUIKey(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);
	private static final Key PREF_SPELLING_PROBLEMS_THRESHOLD = getJDTUIKey(PreferenceConstants.SPELLING_PROBLEMS_THRESHOLD);
	private static final Key PREF_SPELLING_USER_DICTIONARY = getJDTUIKey(PreferenceConstants.SPELLING_USER_DICTIONARY);
	private static final Key PREF_SPELLING_USER_DICTIONARY_ENCODING = getJDTUIKey(PreferenceConstants.SPELLING_USER_DICTIONARY_ENCODING);
	private static final Key PREF_SPELLING_ENABLE_CONTENTASSIST = getJDTUIKey(PreferenceConstants.SPELLING_ENABLE_CONTENTASSIST);

	/**
	 * The value for no platform dictionary.
	 * 
	 * @since 3.3
	 */
	private static final String PREF_VALUE_NO_LOCALE = ""; //$NON-NLS-1$

	/**
	 * Creates a selection dependency between a master and a slave control.
	 * 
	 * @param master
	 *            The master button that controls the state of the slave
	 * @param slave
	 *            The slave control that is enabled only if the master is
	 *            selected
	 */
	protected static void createSelectionDependency(final Button master,
			final Control slave) {

		master.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				// Do nothing
			}

			public void widgetSelected(SelectionEvent event) {
				slave.setEnabled(master.getSelection());
			}
		});
		slave.setEnabled(master.getSelection());
	}

	/**
	 * Returns the locale codes for the locale list.
	 * 
	 * @param locales
	 *            The list of locales
	 * @return Array of locale codes for the list
	 */
	protected static String[] getDictionaryCodes(final Set locales) {

		int index = 0;
		Locale locale = null;

		final String[] codes = new String[locales.size() + 1];
		for (final Iterator iterator = locales.iterator(); iterator.hasNext();) {
			locale = (Locale) iterator.next();
			codes[index++] = locale.toString();
		}
		codes[index++] = PREF_VALUE_NO_LOCALE;
		return codes;
	}

	/**
	 * Returns the display labels for the locale list.
	 * 
	 * @param locales
	 *            The list of locales
	 * @return Array of display labels for the list
	 */
	protected static String[] getDictionaryLabels(final Set locales) {

		int index = 0;
		Locale locale = null;

		final String[] labels = new String[locales.size() + 1];
		for (final Iterator iterator = locales.iterator(); iterator.hasNext();) {

			locale = (Locale) iterator.next();
			labels[index++] = locale.getDisplayName();
		}
		labels[index++] = PreferencesMessages.SpellingPreferencePage_dictionary_none;
		return labels;
	}

	/**
	 * Validates that the file with the specified absolute path exists and can
	 * be opened.
	 * 
	 * @param path
	 *            The path of the file to validate
	 * @return a status without error if the path is valid
	 */
	protected static IStatus validateAbsoluteFilePath(String path) {
		if (path == null) {
			path = ""; //$NON-NLS-1$
		}
		final StatusInfo status = new StatusInfo();
		// path= variableManager.performStringSubstitution(path);
		if (path.length() > 0) {

			final File file = new File(path);
			if (!file.exists()
					&& (!file.isAbsolute() || !file.getParentFile().canWrite())) {
				status
						.setError(PreferencesMessages.SpellingPreferencePage_dictionary_error);
			} else if (file.exists()
					&& (!file.isFile() || !file.isAbsolute() || !file.canRead() || !file
							.canWrite())) {
				status
						.setError(PreferencesMessages.SpellingPreferencePage_dictionary_error);
			}
		}
		return status;
	}

	/**
	 * Validates that the specified locale is available.
	 * 
	 * @param localeString
	 *            the locale to validate
	 * @return The status of the validation
	 */
	private static IStatus validateLocale(final String localeString) {
		if (PREF_VALUE_NO_LOCALE.equals(localeString)) {
			return new StatusInfo();
		}

		final Locale locale = SpellCheckEngine.convertToLocale(localeString);

		if (SpellCheckEngine.findClosestLocale(locale) != null) {
			return new StatusInfo();
		}

		return new StatusInfo(IStatus.ERROR,
				PreferencesMessages.SpellingPreferencePage_locale_error);
	}

	/**
	 * Validates that the specified number is positive.
	 * 
	 * @param number
	 *            the number to validate
	 * @return The status of the validation
	 */
	protected static IStatus validatePositiveNumber(final String number) {
		final StatusInfo status = new StatusInfo();
		if (number.length() == 0) {
			status
					.setError(PreferencesMessages.SpellingPreferencePage_empty_threshold);
		} else {
			try {
				final int value = Integer.parseInt(number);
				if (value < 0) {
					status
							.setError(MessageFormat
									.format(
											PreferencesMessages.SpellingPreferencePage_invalid_threshold,
											number));
				}
			} catch (final NumberFormatException exception) {
				status
						.setError(MessageFormat
								.format(
										PreferencesMessages.SpellingPreferencePage_invalid_threshold,
										number));
			}
		}
		return status;
	}

	/** The dictionary path field */
	private Text fDictionaryPath = null;

	/** The status for the workspace dictionary file */
	private IStatus fFileStatus = new StatusInfo();

	/** The status for the proposal threshold */
	private IStatus fThresholdStatus = new StatusInfo();

	/** The status for the encoding field editor */
	private IStatus fEncodingFieldEditorStatus = new StatusInfo();

	/** The encoding field editor. */
	// private EncodingFieldEditor fEncodingEditor;
	/** The encoding field editor's parent. */
	private Composite fEncodingEditorParent;

	/**
	 * All controls
	 * 
	 * @since 3.1
	 */
	private Control[] fAllControls;

	/**
	 * All previously enabled controls
	 * 
	 * @since 3.1
	 */
	private Control[] fEnabledControls;

	/**
	 * Creates a new spelling configuration block.
	 * 
	 * @param context
	 *            the status change listener
	 * @param project
	 *            the Java project
	 * @param container
	 *            the preference container
	 */
	public SpellingConfigurationBlock(final IStatusChangeListener context,
			IWorkbenchPreferenceContainer container) {
		super(context, getAllKeys(), container);

		IStatus status = validateAbsoluteFilePath(this.getValue(PREF_SPELLING_USER_DICTIONARY));
		if (status.getSeverity() != IStatus.OK) {
			this.setValue(PREF_SPELLING_USER_DICTIONARY, ""); //$NON-NLS-1$
		}

		status = validateLocale(this.getValue(PREF_SPELLING_LOCALE));
		if (status.getSeverity() != IStatus.OK) {
			this.setValue(PREF_SPELLING_LOCALE, SpellCheckEngine.getDefaultLocale()
					.toString());
		}
	}

	protected Combo addComboBox(Composite parent, String label, Key key,
			String[] values, String[] valueLabels, int indent) {
		final ControlData data = new ControlData(key, values);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = indent;

		final Label labelControl = new Label(parent, SWT.LEFT | SWT.WRAP);
		labelControl.setText(label);
		labelControl.setLayoutData(gd);

		final Combo comboBox = new Combo(parent, SWT.READ_ONLY);
		comboBox.setItems(valueLabels);
		comboBox.setData(data);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		comboBox.setLayoutData(gd);
		comboBox.addSelectionListener(this.getSelectionListener());

		this.fLabels.put(comboBox, labelControl);

		String currValue = this.getValue(key);

		Locale locale = SpellCheckEngine.convertToLocale(currValue);
		locale = SpellCheckEngine.findClosestLocale(locale);
		if (locale != null) {
			currValue = locale.toString();
		}

		comboBox.select(data.getSelection(currValue));

		this.fComboBoxes.add(comboBox);
		return comboBox;
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		final List allControls = new ArrayList();
		final PixelConverter converter = new PixelConverter(parent);

		final String[] trueFalse = new String[] { IPreferenceStore.TRUE,
				IPreferenceStore.FALSE };

		final Group user = new Group(composite, SWT.NONE);
		user.setText(PreferencesMessages.SpellingPreferencePage_group_user);
		user.setLayout(new GridLayout());
		user.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(user);

		String label = PreferencesMessages.SpellingPreferencePage_ignore_digits_label;
		Control slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_DIGITS,
				trueFalse, 0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_mixed_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_MIXED, trueFalse,
				0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_sentence_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_SENTENCE,
				trueFalse, 0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_upper_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_UPPER, trueFalse,
				0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_url_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_URLS, trueFalse,
				0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_non_letters_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_NON_LETTERS,
				trueFalse, 0);
		allControls.add(slave);

		label = PreferencesMessages.SpellingPreferencePage_ignore_single_letters_label;
		slave = this.addCheckBox(user, label, PREF_SPELLING_IGNORE_SINGLE_LETTERS,
				trueFalse, 0);
		allControls.add(slave);

		// label=
		// PreferencesMessages.SpellingPreferencePage_ignore_java_strings_label;
		// slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_JAVA_STRINGS,
		// trueFalse, 0);
		// allControls.add(slave);
		//		
		// label= PreferencesMessages.
		// SpellingPreferencePage_ignore_ampersand_in_properties_label;
		// slave= addCheckBox(user, label,
		// PREF_SPELLING_IGNORE_AMPERSAND_IN_PROPERTIES, trueFalse, 0);
		// allControls.add(slave);

		final Set locales = SpellCheckEngine
				.getLocalesWithInstalledDictionaries();
		final boolean hasPlaformDictionaries = locales.size() > 0;

		final Group engine = new Group(composite, SWT.NONE);
		if (hasPlaformDictionaries) {
			engine
					.setText(PreferencesMessages.SpellingPreferencePage_group_dictionaries);
		} else {
			engine
					.setText(PreferencesMessages.SpellingPreferencePage_group_dictionary);
		}
		engine.setLayout(new GridLayout(4, false));
		engine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(engine);

		if (hasPlaformDictionaries) {
			label = PreferencesMessages.SpellingPreferencePage_dictionary_label;
			final Combo combo = this.addComboBox(engine, label, PREF_SPELLING_LOCALE,
					getDictionaryCodes(locales), getDictionaryLabels(locales),
					0);
			combo.setEnabled(locales.size() > 0);
			allControls.add(combo);
			allControls.add(this.fLabels.get(combo));

			new Label(engine, SWT.NONE); // placeholder
		}

		label = PreferencesMessages.SpellingPreferencePage_workspace_dictionary_label;
		this.fDictionaryPath = this.addTextField(engine, label,
				PREF_SPELLING_USER_DICTIONARY, 0, 0);
		GridData gd = (GridData) this.fDictionaryPath.getLayoutData();
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = converter.convertWidthInCharsToPixels(40);
		allControls.add(this.fDictionaryPath);
		allControls.add(this.fLabels.get(this.fDictionaryPath));

		final Composite buttons = new Composite(engine, SWT.NONE);
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Button button = new Button(buttons, SWT.PUSH);
		button.setText(PreferencesMessages.SpellingPreferencePage_browse_label);
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent event) {
				SpellingConfigurationBlock.this.handleBrowseButtonSelected();
			}
		});
		SWTUtil.setButtonDimensionHint(button);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		allControls.add(button);

		// Description for user dictionary
		new Label(engine, SWT.NONE); // filler
		final Label description = new Label(engine, SWT.NONE);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		description.setLayoutData(gd);
		description
				.setText(PreferencesMessages.SpellingPreferencePage_user_dictionary_description);
		allControls.add(description);

		this.createEncodingFieldEditor(engine, allControls);

		final Group advanced = new Group(composite, SWT.NONE);
		advanced
				.setText(PreferencesMessages.SpellingPreferencePage_group_advanced);
		advanced.setLayout(new GridLayout(3, false));
		advanced.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(advanced);

		label = PreferencesMessages.SpellingPreferencePage_problems_threshold;
		int digits = 4;
		Text text = this.addTextField(advanced, label,
				PREF_SPELLING_PROBLEMS_THRESHOLD, 0, converter
						.convertWidthInCharsToPixels(digits + 1));
		text.setTextLimit(digits);
		allControls.add(text);
		allControls.add(this.fLabels.get(text));

		label = PreferencesMessages.SpellingPreferencePage_proposals_threshold;
		digits = 3;
		text = this.addTextField(advanced, label, PREF_SPELLING_PROPOSAL_THRESHOLD,
				0, converter.convertWidthInCharsToPixels(digits + 1));
		text.setTextLimit(digits);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		allControls.add(text);
		allControls.add(this.fLabels.get(text));

		if (SUPPORT_CONTENT_ASSIST_PROPOSALS) {
			label = PreferencesMessages.SpellingPreferencePage_enable_contentassist_label;
			button = this.addCheckBox(advanced, label,
					PREF_SPELLING_ENABLE_CONTENTASSIST, trueFalse, 0);
			allControls.add(button);
		}

		this.fAllControls = (Control[]) allControls.toArray(new Control[allControls
				.size()]);

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IJavaHelpContextIds.SPELLING_CONFIGURATION_BLOCK);
		return composite;
	}

	/**
	 * Creates the encoding field editor.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param allControls
	 *            list with all controls
	 * @since 3.3
	 */
	private void createEncodingFieldEditor(Composite composite, List allControls) {
		final Label filler = new Label(composite, SWT.NONE);
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 4;
		filler.setLayoutData(gd);

		// Label label = new Label(composite, SWT.NONE);
		// label
		// .setText(PreferencesMessages.SpellingPreferencePage_encoding_label);
		// label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		// allControls.add(label);

		// fEncodingEditorParent = new Composite(composite, SWT.NONE);
		// GridLayout layout = new GridLayout(2, false);
		// layout.marginWidth = 0;
		// layout.marginHeight = 0;
		// fEncodingEditorParent.setLayout(layout);
		// gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		// gd.horizontalSpan = 3;
		// fEncodingEditorParent.setLayoutData(gd);

		//fEncodingEditor= new EncodingFieldEditor(PREF_SPELLING_USER_DICTIONARY_ENCODING.getName(), "", null, fEncodingEditorParent); //$NON-NLS-1$

		final PreferenceStore store = new PreferenceStore();
		// String defaultEncoding= ResourcesPlugin.getEncoding();
		store.setDefault(PREF_SPELLING_USER_DICTIONARY_ENCODING.getName(),
				Charset.defaultCharset().name());
		final String encoding = this.getValue(PREF_SPELLING_USER_DICTIONARY_ENCODING);
		if ((encoding != null) && (encoding.length() > 0)) {
			store.setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING.getName(),
					encoding);
		}

		// fEncodingEditor.setPreferenceStore(store);

		// Redirect status messages from the field editor to the status change
		// listener
		final DialogPage fakePage = new DialogPage() {
			public void createControl(Composite c) {
			}

			public void setErrorMessage(String newMessage) {
				final StatusInfo status = new StatusInfo();
				if (newMessage != null) {
					status.setError(newMessage);
				}
				SpellingConfigurationBlock.this.fEncodingFieldEditorStatus = status;
				SpellingConfigurationBlock.this.fContext.statusChanged(StatusUtil.getMostSevere(new IStatus[] {
						SpellingConfigurationBlock.this.fThresholdStatus, SpellingConfigurationBlock.this.fFileStatus,
						SpellingConfigurationBlock.this.fEncodingFieldEditorStatus }));
			}
		};
		// fEncodingEditor.setPage(fakePage);
		//		
		// fEncodingEditor.load();
		//		
		// if (encoding == null || encoding.equals(defaultEncoding) ||
		// encoding.length() == 0)
		// fEncodingEditor.loadDefault();

	}

	private static Key[] getAllKeys() {
		if (SUPPORT_CONTENT_ASSIST_PROPOSALS) {
			return new Key[] { PREF_SPELLING_USER_DICTIONARY,
					PREF_SPELLING_USER_DICTIONARY_ENCODING,
					PREF_SPELLING_IGNORE_DIGITS, PREF_SPELLING_IGNORE_MIXED,
					PREF_SPELLING_IGNORE_SENTENCE, PREF_SPELLING_IGNORE_UPPER,
					PREF_SPELLING_IGNORE_URLS,

					PREF_SPELLING_IGNORE_NON_LETTERS,
					PREF_SPELLING_IGNORE_SINGLE_LETTERS, PREF_SPELLING_LOCALE,
					PREF_SPELLING_PROPOSAL_THRESHOLD,
					PREF_SPELLING_PROBLEMS_THRESHOLD,
					PREF_SPELLING_ENABLE_CONTENTASSIST, };
		} else {
			return new Key[] { PREF_SPELLING_USER_DICTIONARY,
					PREF_SPELLING_USER_DICTIONARY_ENCODING,
					PREF_SPELLING_IGNORE_DIGITS, PREF_SPELLING_IGNORE_MIXED,
					PREF_SPELLING_IGNORE_SENTENCE, PREF_SPELLING_IGNORE_UPPER,
					PREF_SPELLING_IGNORE_URLS,

					PREF_SPELLING_IGNORE_NON_LETTERS,
					PREF_SPELLING_IGNORE_SINGLE_LETTERS, PREF_SPELLING_LOCALE,
					PREF_SPELLING_PROPOSAL_THRESHOLD,
					PREF_SPELLING_PROBLEMS_THRESHOLD, };
		}
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * getFullBuildDialogStrings(boolean)
	 */
	protected final String[] getFullBuildDialogStrings(final boolean workspace) {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#performOk
	 * ()
	 * 
	 * @since 3.3
	 */
	public boolean performOk() {
		// fEncodingEditor.store();
		// if (fEncodingEditor.presentsDefaultValue())
		this.setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING, ""); //$NON-NLS-1$
		// else
		// setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING,
		// fEncodingEditor.getPreferenceStore
		// ().getString(PREF_SPELLING_USER_DICTIONARY_ENCODING.getName()));
		return super.performOk();
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * performApply()
	 * 
	 * @since 3.3
	 */
	public boolean performApply() {
		// fEncodingEditor.store();
		// if (fEncodingEditor.presentsDefaultValue())
		this.setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING, ""); //$NON-NLS-1$
		// else
		// setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING,
		// fEncodingEditor.getPreferenceStore
		// ().getString(PREF_SPELLING_USER_DICTIONARY_ENCODING.getName()));
		return super.performApply();
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * performDefaults()
	 * 
	 * @since 3.3
	 */
	public void performDefaults() {
		super.performDefaults();

		this.setValue(PREF_SPELLING_USER_DICTIONARY_ENCODING, ""); //$NON-NLS-1$

		// fEncodingEditor.getPreferenceStore().setValue(fEncodingEditor.
		// getPreferenceName(), ResourcesPlugin.getEncoding());
		// fEncodingEditor.load();
		//		
		// fEncodingEditor.loadDefault();
	}

	/**
	 * Handles selections of the browse button.
	 */
	protected void handleBrowseButtonSelected() {
		final FileDialog dialog = new FileDialog(this.fDictionaryPath.getShell(),
				SWT.OPEN);
		dialog
				.setText(PreferencesMessages.SpellingPreferencePage_filedialog_title);
		dialog.setFilterPath(this.fDictionaryPath.getText());

		final String path = dialog.open();
		if (path != null) {
			this.fDictionaryPath.setText(path);
		}
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * validateSettings(java.lang.String,java.lang.String)
	 */
	protected void validateSettings(final Key key, final String oldValue,
			final String newValue) {
		if ((key == null) || PREF_SPELLING_PROPOSAL_THRESHOLD.equals(key)) {
			this.fThresholdStatus = validatePositiveNumber(this.getValue(PREF_SPELLING_PROPOSAL_THRESHOLD));
		} else {
			this.fThresholdStatus = new StatusInfo();
		}

		if ((key == null) || PREF_SPELLING_PROBLEMS_THRESHOLD.equals(key)) {
			final IStatus status = validatePositiveNumber(this.getValue(PREF_SPELLING_PROBLEMS_THRESHOLD));
			this.fThresholdStatus = StatusUtil.getMostSevere(new IStatus[] {
					this.fThresholdStatus, status });
		}

		if ((key == null) || PREF_SPELLING_USER_DICTIONARY.equals(key)) {
			this.fFileStatus = validateAbsoluteFilePath(this.getValue(PREF_SPELLING_USER_DICTIONARY));
		}

		this.fContext.statusChanged(StatusUtil.getMostSevere(new IStatus[] {
				this.fThresholdStatus, this.fFileStatus, this.fEncodingFieldEditorStatus }));
	}

	/*
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateCheckBox(org.eclipse.swt.widgets.Button)
	 * 
	 * @since 3.1
	 */
	protected void updateCheckBox(Button curr) {
		super.updateCheckBox(curr);
		final Event event = new Event();
		event.type = SWT.Selection;
		event.display = curr.getDisplay();
		event.widget = curr;
		curr.notifyListeners(SWT.Selection, event);
	}

	/**
	 * Sets the enabled state.
	 * 
	 * @param enabled
	 *            the new state
	 * @since 3.1
	 */
	protected void setEnabled(boolean enabled) {
		// fEncodingEditor.setEnabled(enabled, fEncodingEditorParent);

		if (enabled && (this.fEnabledControls != null)) {
			for (int i = this.fEnabledControls.length - 1; i >= 0; i--) {
				this.fEnabledControls[i].setEnabled(true);
			}
			this.fEnabledControls = null;
		}
		if (!enabled && (this.fEnabledControls == null)) {
			final List enabledControls = new ArrayList();
			for (int i = this.fAllControls.length - 1; i >= 0; i--) {
				final Control control = this.fAllControls[i];
				if (control.isEnabled()) {
					enabledControls.add(control);
					control.setEnabled(false);
				}
			}
			this.fEnabledControls = (Control[]) enabledControls
					.toArray(new Control[enabledControls.size()]);
		}
	}

}
