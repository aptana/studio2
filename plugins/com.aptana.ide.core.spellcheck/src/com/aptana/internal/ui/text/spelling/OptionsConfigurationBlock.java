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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;

import org.osgi.service.prefs.BackingStoreException;

/**
 * Abstract options configuration block providing a general implementation for
 * setting up an options configuration page.
 * 
 * @since 2.1
 */
public abstract class OptionsConfigurationBlock {

	public static class Key {

		private final String fQualifier;
		private final String fKey;

		public Key(String qualifier, String key) {
			this.fQualifier = qualifier;
			this.fKey = key;
		}

		public String getName() {
			return this.fKey;
		}

		private IEclipsePreferences getNode(IScopeContext context,
				IWorkingCopyManager manager) {
			final IEclipsePreferences node = context.getNode(this.fQualifier);
			if (manager != null) {
				return manager.getWorkingCopy(node);
			}
			return node;
		}

		public String getStoredValue(IScopeContext context,
				IWorkingCopyManager manager) {
			return this.getNode(context, manager).get(this.fKey, null);
		}

		public String getStoredValue(IScopeContext[] lookupOrder,
				boolean ignoreTopScope, IWorkingCopyManager manager) {
			for (int i = ignoreTopScope ? 1 : 0; i < lookupOrder.length; i++) {
				final String value = this.getStoredValue(lookupOrder[i],
						manager);
				if (value != null) {
					return value;
				}
			}
			return null;
		}

		public void setStoredValue(IScopeContext context, String value,
				IWorkingCopyManager manager) {
			if (value != null) {
				this.getNode(context, manager).put(this.fKey, value);
			} else {
				this.getNode(context, manager).remove(this.fKey);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return this.fQualifier + '/' + this.fKey;
		}

		public String getQualifier() {
			return this.fQualifier;
		}

	}

	/**
	 * Key that is only managed locally ans not part of preference store.
	 */
	private static class LocalKey extends Key {
		private final HashMap fValues;

		private LocalKey(String key) {
			super("local", key); //$NON-NLS-1$
			this.fValues = new HashMap();
		}

		public String getStoredValue(IScopeContext context,
				IWorkingCopyManager manager) {
			return (String) this.fValues.get(context);
		}

		public void setStoredValue(IScopeContext context, String value,
				IWorkingCopyManager manager) {
			if (value != null) {
				this.fValues.put(context, value);
			} else {
				this.fValues.remove(context);
			}
		}
	}

	protected static class ControlData {
		private final Key fKey;
		private final String[] fValues;

		public ControlData(Key key, String[] values) {
			this.fKey = key;
			this.fValues = values;
		}

		public Key getKey() {
			return this.fKey;
		}

		public String getValue(boolean selection) {
			final int index = selection ? 0 : 1;
			return this.fValues[index];
		}

		public String getValue(int index) {
			return this.fValues[index];
		}

		public int getSelection(String value) {
			if (value != null) {
				for (int i = 0; i < this.fValues.length; i++) {
					if (value.equals(this.fValues[i])) {
						return i;
					}
				}
			}
			return this.fValues.length - 1; // assume the last option is the
											// least severe
		}
	}

	private static final String REBUILD_COUNT_KEY = "preferences_build_requested"; //$NON-NLS-1$

	private static final String SETTINGS_EXPANDED = "expanded"; //$NON-NLS-1$

	protected final ArrayList fCheckBoxes;
	protected final ArrayList fComboBoxes;
	protected final ArrayList fTextBoxes;
	protected final HashMap fLabels;
	protected final ArrayList fExpandedComposites;

	private SelectionListener fSelectionListener;
	private ModifyListener fTextModifyListener;

	protected IStatusChangeListener fContext;

	protected final Key[] fAllKeys;

	private IScopeContext[] fLookupOrder;

	private Shell fShell;

	private final IWorkingCopyManager fManager;
	private final IWorkbenchPreferenceContainer fContainer;

	private int fRebuildCount; // / used to prevent multiple dialogs that ask
								// for a rebuild

	public OptionsConfigurationBlock(IStatusChangeListener context,
			Key[] allKeys, IWorkbenchPreferenceContainer container) {
		this.fContext = context;
		this.fAllKeys = allKeys;
		this.fContainer = container;
		if (container == null) {
			this.fManager = new WorkingCopyManager();
		} else {
			this.fManager = container.getWorkingCopyManager();
		}

		{
			this.fLookupOrder = new IScopeContext[] { new InstanceScope(),
					new DefaultScope() };
		}

		this.testIfOptionsComplete(allKeys);

		this.settingsUpdated();

		this.fCheckBoxes = new ArrayList();
		this.fComboBoxes = new ArrayList();
		this.fTextBoxes = new ArrayList(2);
		this.fLabels = new HashMap();
		this.fExpandedComposites = new ArrayList();

		this.fRebuildCount = this.getRebuildCount();
	}

	protected final IWorkbenchPreferenceContainer getPreferenceContainer() {
		return this.fContainer;
	}

	protected static Key getKey(String plugin, String key) {
		return new Key(plugin, key);
	}

	protected final static Key getJDTCoreKey(String key) {
		return getKey("org.eclipse.ui.editors", key); //$NON-NLS-1$
	}

	protected final static Key getJDTUIKey(String key) {
		return getKey("org.eclipse.ui.editors", key); //$NON-NLS-1$
	}

	protected final static Key getLocalKey(String key) {
		return new LocalKey(key);
	}

	private void testIfOptionsComplete(Key[] allKeys) {
		for (int i = 0; i < allKeys.length; i++) {
			final Key key = allKeys[i];
			if (!(key instanceof LocalKey)) {
				if (key.getStoredValue(this.fLookupOrder, false, this.fManager) == null) {
					//JavaPlugin.logErrorMessage("preference option missing: " + key + " (" + this.getClass().getName() + ')'); //$NON-NLS-1$//$NON-NLS-2$
				}
			}
		}
	}

	private int getRebuildCount() {
		return 1;
	}

	private void incrementRebuildCount() {
		this.fRebuildCount++;
	}

	protected void settingsUpdated() {
	}

	public void selectOption(String key, String qualifier) {
		for (int i = 0; i < this.fAllKeys.length; i++) {
			final Key curr = this.fAllKeys[i];
			if (curr.getName().equals(key)
					&& curr.getQualifier().equals(qualifier)) {
				this.selectOption(curr);
			}
		}
	}

	public void selectOption(Key key) {
		final Control control = this.findControl(key);
		if (control != null) {
			if (!this.fExpandedComposites.isEmpty()) {
				final ExpandableComposite expandable = this
						.getParentExpandableComposite(control);
				if (expandable != null) {
					for (int i = 0; i < this.fExpandedComposites.size(); i++) {
						final ExpandableComposite curr = (ExpandableComposite) this.fExpandedComposites
								.get(i);
						curr.setExpanded(curr == expandable);
					}
					this.expandedStateChanged(expandable);
				}
			}
			control.setFocus();
		}
	}

	protected Shell getShell() {
		return this.fShell;
	}

	protected void setShell(Shell shell) {
		this.fShell = shell;
	}

	protected abstract Control createContents(Composite parent);

	protected Button addCheckBox(Composite parent, String label, Key key,
			String[] values, int indent) {
		final ControlData data = new ControlData(key, values);

		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		gd.horizontalIndent = indent;

		final Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setFont(JFaceResources.getDialogFont());
		checkBox.setText(label);
		checkBox.setData(data);
		checkBox.setLayoutData(gd);
		checkBox.addSelectionListener(this.getSelectionListener());

		this.makeScrollableCompositeAware(checkBox);

		final String currValue = this.getValue(key);
		checkBox.setSelection(data.getSelection(currValue) == 0);

		this.fCheckBoxes.add(checkBox);

		return checkBox;
	}

	protected Button addCheckBoxWithLink(Composite parent, String label,
			Key key, String[] values, int indent, int widthHint,
			SelectionListener listener) {
		final ControlData data = new ControlData(key, values);

		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		gd.horizontalSpan = 3;
		gd.horizontalIndent = indent;

		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(gd);

		final Button checkBox = new Button(composite, SWT.CHECK);
		checkBox.setFont(JFaceResources.getDialogFont());
		checkBox.setData(data);
		checkBox.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				false, false));
		checkBox.addSelectionListener(this.getSelectionListener());

		gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.widthHint = widthHint;

		final Link link = new Link(composite, SWT.NONE);
		link.setText(label);
		link.setLayoutData(gd);
		if (listener != null) {
			link.addSelectionListener(listener);
		}

		this.makeScrollableCompositeAware(link);
		this.makeScrollableCompositeAware(checkBox);

		final String currValue = this.getValue(key);
		checkBox.setSelection(data.getSelection(currValue) == 0);

		this.fCheckBoxes.add(checkBox);

		return checkBox;
	}

	protected Combo addComboBox(Composite parent, String label, Key key,
			String[] values, String[] valueLabels, int indent) {
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true,
				false, 2, 1);
		gd.horizontalIndent = indent;

		final Label labelControl = new Label(parent, SWT.LEFT);
		labelControl.setFont(JFaceResources.getDialogFont());
		labelControl.setText(label);
		labelControl.setLayoutData(gd);

		final Combo comboBox = this.newComboControl(parent, key, values,
				valueLabels);
		comboBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		this.fLabels.put(comboBox, labelControl);

		return comboBox;
	}

	protected Combo addInversedComboBox(Composite parent, String label,
			Key key, String[] values, String[] valueLabels, int indent) {
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = indent;
		gd.horizontalSpan = 3;

		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(gd);

		final Combo comboBox = this.newComboControl(composite, key, values,
				valueLabels);
		comboBox.setFont(JFaceResources.getDialogFont());
		comboBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		final Label labelControl = new Label(composite, SWT.LEFT | SWT.WRAP);
		labelControl.setText(label);
		labelControl.setLayoutData(new GridData());

		this.fLabels.put(comboBox, labelControl);
		return comboBox;
	}

	protected Combo newComboControl(Composite composite, Key key,
			String[] values, String[] valueLabels) {
		final ControlData data = new ControlData(key, values);

		final Combo comboBox = new Combo(composite, SWT.READ_ONLY);
		comboBox.setItems(valueLabels);
		comboBox.setData(data);
		comboBox.addSelectionListener(this.getSelectionListener());
		comboBox.setFont(JFaceResources.getDialogFont());

		this.makeScrollableCompositeAware(comboBox);

		final String currValue = this.getValue(key);
		comboBox.select(data.getSelection(currValue));

		this.fComboBoxes.add(comboBox);
		return comboBox;
	}

	protected Text addTextField(Composite parent, String label, Key key,
			int indent, int widthHint) {
		final Label labelControl = new Label(parent, SWT.WRAP);
		labelControl.setText(label);
		labelControl.setFont(JFaceResources.getDialogFont());
		labelControl.setLayoutData(new GridData());

		final Text textBox = new Text(parent, SWT.BORDER | SWT.SINGLE);
		textBox.setData(key);
		textBox.setLayoutData(new GridData());

		this.makeScrollableCompositeAware(textBox);

		this.fLabels.put(textBox, labelControl);

		final String currValue = this.getValue(key);
		if (currValue != null) {
			textBox.setText(currValue);
		}
		textBox.addModifyListener(this.getTextModifyListener());

		final GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		if (widthHint != 0) {
			data.widthHint = widthHint;
		}
		data.horizontalIndent = indent;
		data.horizontalSpan = 2;
		textBox.setLayoutData(data);

		this.fTextBoxes.add(textBox);
		return textBox;
	}

	protected ScrolledPageContent getParentScrolledComposite(Control control) {
		Control parent = control.getParent();
		while (!(parent instanceof ScrolledPageContent) && (parent != null)) {
			parent = parent.getParent();
		}
		if (parent instanceof ScrolledPageContent) {
			return (ScrolledPageContent) parent;
		}
		return null;
	}

	protected ExpandableComposite getParentExpandableComposite(Control control) {
		Control parent = control.getParent();
		while (!(parent instanceof ExpandableComposite) && (parent != null)) {
			parent = parent.getParent();
		}
		if (parent instanceof ExpandableComposite) {
			return (ExpandableComposite) parent;
		}
		return null;
	}

	private void makeScrollableCompositeAware(Control control) {
		final ScrolledPageContent parentScrolledComposite = this
				.getParentScrolledComposite(control);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.adaptChild(control);
		}
	}

	protected ExpandableComposite createStyleSection(Composite parent,
			String label, int nColumns) {
		final ExpandableComposite excomposite = new ExpandableComposite(parent,
				SWT.NONE, ExpandableComposite.TWISTIE
						| ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				OptionsConfigurationBlock.this
						.expandedStateChanged((ExpandableComposite) e
								.getSource());
			}
		});
		this.fExpandedComposites.add(excomposite);
		this.makeScrollableCompositeAware(excomposite);
		return excomposite;
	}

	protected final void expandedStateChanged(ExpandableComposite expandable) {
		final ScrolledPageContent parentScrolledComposite = this
				.getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.reflow(true);
		}
	}

	protected void restoreSectionExpansionStates(IDialogSettings settings) {
		for (int i = 0; i < this.fExpandedComposites.size(); i++) {
			final ExpandableComposite excomposite = (ExpandableComposite) this.fExpandedComposites
					.get(i);
			if (settings == null) {
				excomposite.setExpanded(i == 0); // only expand the first node
													// by default
			} else {
				excomposite.setExpanded(settings.getBoolean(SETTINGS_EXPANDED
						+ String.valueOf(i)));
			}
		}
	}

	protected void storeSectionExpansionStates(IDialogSettings settings) {
		for (int i = 0; i < this.fExpandedComposites.size(); i++) {
			final ExpandableComposite curr = (ExpandableComposite) this.fExpandedComposites
					.get(i);
			settings.put(SETTINGS_EXPANDED + String.valueOf(i), curr
					.isExpanded());
		}
	}

	protected SelectionListener getSelectionListener() {
		if (this.fSelectionListener == null) {
			this.fSelectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					OptionsConfigurationBlock.this.controlChanged(e.widget);
				}
			};
		}
		return this.fSelectionListener;
	}

	protected ModifyListener getTextModifyListener() {
		if (this.fTextModifyListener == null) {
			this.fTextModifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					OptionsConfigurationBlock.this.textChanged((Text) e.widget);
				}
			};
		}
		return this.fTextModifyListener;
	}

	protected void controlChanged(Widget widget) {
		final ControlData data = (ControlData) widget.getData();
		String newValue = null;
		if (widget instanceof Button) {
			newValue = data.getValue(((Button) widget).getSelection());
		} else if (widget instanceof Combo) {
			newValue = data.getValue(((Combo) widget).getSelectionIndex());
		} else {
			return;
		}
		final String oldValue = this.setValue(data.getKey(), newValue);
		this.validateSettings(data.getKey(), oldValue, newValue);
	}

	protected void textChanged(Text textControl) {
		final Key key = (Key) textControl.getData();
		final String number = textControl.getText();
		final String oldValue = this.setValue(key, number);
		this.validateSettings(key, oldValue, number);
	}

	protected boolean checkValue(Key key, String value) {
		return value.equals(this.getValue(key));
	}

	protected String getValue(Key key) {

		return key.getStoredValue(this.fLookupOrder, false, this.fManager);
	}

	protected boolean getBooleanValue(Key key) {
		return Boolean.valueOf(this.getValue(key)).booleanValue();
	}

	protected String setValue(Key key, String value) {
		final String oldValue = this.getValue(key);
		key.setStoredValue(this.fLookupOrder[0], value, this.fManager);
		return oldValue;
	}

	protected String setValue(Key key, boolean value) {
		return this.setValue(key, String.valueOf(value));
	}

	/**
	 * Returns the value as actually stored in the preference store.
	 * 
	 * @param key
	 * @return the value as actually stored in the preference store.
	 */
	protected String getStoredValue(Key key) {
		return key.getStoredValue(this.fLookupOrder, false, this.fManager);
	}

	/*
	 * (non-javadoc) Update fields and validate.
	 * 
	 * @param changedKey Key that changed, or null, if all changed.
	 */
	protected abstract void validateSettings(Key changedKey, String oldValue,
			String newValue);

	protected String[] getTokens(String text, String separator) {
		final StringTokenizer tok = new StringTokenizer(text, separator);
		final int nTokens = tok.countTokens();
		final String[] res = new String[nTokens];
		for (int i = 0; i < res.length; i++) {
			res[i] = tok.nextToken().trim();
		}
		return res;
	}

	private boolean getChanges(IScopeContext currContext, List changedSettings) {
		final boolean completeSettings = false;
		boolean needsBuild = false;
		for (int i = 0; i < this.fAllKeys.length; i++) {
			final Key key = this.fAllKeys[i];
			final String oldVal = key.getStoredValue(currContext, null);
			final String val = key.getStoredValue(currContext, this.fManager);
			if (val == null) {
				if (oldVal != null) {
					changedSettings.add(key);
					needsBuild |= !oldVal.equals(key.getStoredValue(
							this.fLookupOrder, true, this.fManager));
				} else if (completeSettings) {
					key.setStoredValue(currContext, key.getStoredValue(
							this.fLookupOrder, true, this.fManager),
							this.fManager);
					changedSettings.add(key);
					// no build needed
				}
			} else if (!val.equals(oldVal)) {
				changedSettings.add(key);
				needsBuild |= (oldVal != null)
						|| !val.equals(key.getStoredValue(this.fLookupOrder,
								true, this.fManager));
			}
		}
		return needsBuild;
	}

	public void useProjectSpecificSettings(boolean enable) {
		final boolean hasProjectSpecificOption = false;
		// if (enable != hasProjectSpecificOption && false) {
		// if (enable) {
		// for (int i= 0; i < fAllKeys.length; i++) {
		// Key curr= fAllKeys[i];
		// String val= (String) fDisabledProjectSettings.get(curr);
		// curr.setStoredValue(fLookupOrder[0], val, fManager);
		// }
		// fDisabledProjectSettings= null;
		// updateControls();
		// validateSettings(null, null, null);
		// } else {
		// fDisabledProjectSettings= new IdentityHashMap();
		// for (int i= 0; i < fAllKeys.length; i++) {
		// Key curr= fAllKeys[i];
		// String oldSetting= curr.getStoredValue(fLookupOrder, false,
		// fManager);
		// fDisabledProjectSettings.put(curr, oldSetting);
		// curr.setStoredValue(fLookupOrder[0], null, fManager); // clear
		// project settings
		// }
		// }
		// }
	}

	public boolean areSettingsEnabled() {
		return true;
	}

	public boolean performOk() {
		return this.processChanges(this.fContainer);
	}

	public boolean performApply() {
		return this.processChanges(null); // apply directly
	}

	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		final IScopeContext currContext = this.fLookupOrder[0];

		final List /* <Key> */changedOptions = new ArrayList();
		boolean needsBuild = this.getChanges(currContext, changedOptions);
		if (changedOptions.isEmpty()) {
			return true;
		}
		if (needsBuild) {
			final int count = this.getRebuildCount();
			if (count > this.fRebuildCount) {
				needsBuild = false; // build already requested
				this.fRebuildCount = count;
			}
		}

		boolean doBuild = false;
		if (needsBuild) {
			final String[] strings = this.getFullBuildDialogStrings(true);
			if (strings != null) {
				final MessageDialog dialog = new MessageDialog(this.getShell(),
						strings[0], null, strings[1], MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL,
								IDialogConstants.CANCEL_LABEL }, 2);
				final int res = dialog.open();
				if (res == 0) {
					doBuild = true;
				} else if (res != 1) {
					return false; // cancel pressed
				}
			}
		}
		if (container != null) {
			// no need to apply the changes to the original store: will be done
			// by the page container
			if (doBuild) { // post build
				this.incrementRebuildCount();
				//container.registerUpdateJob(CoreUtility.getBuildJob(fProject))
				// ;
			}
		} else {
			// apply changes right away
			try {
				this.fManager.applyChanges();
			} catch (final BackingStoreException e) {
				// JavaPlugin.log(e);
				return false;
			}
			if (doBuild) {
				// CoreUtility.getBuildJob(fProject).schedule();
			}

		}
		return true;
	}

	protected abstract String[] getFullBuildDialogStrings(
			boolean workspaceSettings);

	public void performDefaults() {
		for (int i = 0; i < this.fAllKeys.length; i++) {
			final Key curr = this.fAllKeys[i];
			final String defValue = curr.getStoredValue(this.fLookupOrder,
					true, this.fManager);
			this.setValue(curr, defValue);
		}

		this.settingsUpdated();
		this.updateControls();
		this.validateSettings(null, null, null);
	}

	/**
	 * @since 3.1
	 */
	public void performRevert() {
		for (int i = 0; i < this.fAllKeys.length; i++) {
			final Key curr = this.fAllKeys[i];
			final String origValue = curr.getStoredValue(this.fLookupOrder,
					false, null);
			this.setValue(curr, origValue);
		}

		this.settingsUpdated();
		this.updateControls();
		this.validateSettings(null, null, null);
	}

	public void dispose() {
	}

	protected void updateControls() {
		// update the UI
		for (int i = this.fCheckBoxes.size() - 1; i >= 0; i--) {
			this.updateCheckBox((Button) this.fCheckBoxes.get(i));
		}
		for (int i = this.fComboBoxes.size() - 1; i >= 0; i--) {
			this.updateCombo((Combo) this.fComboBoxes.get(i));
		}
		for (int i = this.fTextBoxes.size() - 1; i >= 0; i--) {
			this.updateText((Text) this.fTextBoxes.get(i));
		}
	}

	protected void updateCombo(Combo curr) {
		final ControlData data = (ControlData) curr.getData();

		final String currValue = this.getValue(data.getKey());
		curr.select(data.getSelection(currValue));
	}

	protected void updateCheckBox(Button curr) {
		final ControlData data = (ControlData) curr.getData();

		final String currValue = this.getValue(data.getKey());
		curr.setSelection(data.getSelection(currValue) == 0);
	}

	protected void updateText(Text curr) {
		final Key key = (Key) curr.getData();

		final String currValue = this.getValue(key);
		if (currValue != null) {
			curr.setText(currValue);
		}
	}

	protected Button getCheckBox(Key key) {
		for (int i = this.fCheckBoxes.size() - 1; i >= 0; i--) {
			final Button curr = (Button) this.fCheckBoxes.get(i);
			final ControlData data = (ControlData) curr.getData();
			if (key.equals(data.getKey())) {
				return curr;
			}
		}
		return null;
	}

	protected Combo getComboBox(Key key) {
		for (int i = this.fComboBoxes.size() - 1; i >= 0; i--) {
			final Combo curr = (Combo) this.fComboBoxes.get(i);
			final ControlData data = (ControlData) curr.getData();
			if (key.equals(data.getKey())) {
				return curr;
			}
		}
		return null;
	}

	protected Text getTextControl(Key key) {
		for (int i = this.fTextBoxes.size() - 1; i >= 0; i--) {
			final Text curr = (Text) this.fTextBoxes.get(i);
			final ControlData data = (ControlData) curr.getData();
			if (key.equals(data.getKey())) {
				return curr;
			}
		}
		return null;
	}

	protected Control findControl(Key key) {
		final Combo comboBox = this.getComboBox(key);
		if (comboBox != null) {
			return comboBox;
		}
		final Button checkBox = this.getCheckBox(key);
		if (checkBox != null) {
			return checkBox;
		}
		final Text text = this.getTextControl(key);
		if (text != null) {
			return text;
		}
		return null;
	}

	protected void setComboEnabled(Key key, boolean enabled) {
		final Combo combo = this.getComboBox(key);
		final Label label = (Label) this.fLabels.get(combo);
		combo.setEnabled(enabled);
		label.setEnabled(enabled);
	}
}
