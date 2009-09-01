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
package com.aptana.ide.editor.text.preferences;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.FileExtensionDialog;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.editor.text.GenericTextEditor;
import com.aptana.ide.editor.text.TextPlugin;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.PreferenceMastHead;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizationWidget;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.colorizer.LanguageStructureProvider;
import com.aptana.ide.lexer.TokenList;
import com.aptana.sax.AttributeSniffer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class TextEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private Composite displayArea;
	private PreferenceMastHead header;
	private Label description;
	private Button add;
	private Button remove;
	private Table typesTable;
	private Text grammarText;
	private Button browseGrammar;
	private Button applyChanges;
	private EditorDescriptor descriptor;
	private List<FileEditorMapping> mappings;
	private List<Image> imagesToDispose;
	private boolean changed;
	private Group grammarGroup;
	private Group colorizerGroup;
	private IPreferenceStore store;
	private Map<String, String> mappingsToGrammars;
	private Map<String, LanguageStructureProvider> activeProviders;
	private LanguageColorizationWidget colorizer;

	private SelectionAdapter tableSelectionListener = new SelectionAdapter()
	{

		public void widgetSelected(SelectionEvent e)
		{
			TableItem item = typesTable.getSelection()[0];
			browseGrammar.setEnabled(true);
			String gText = mappingsToGrammars.containsKey(item.getText()) ? (String) mappingsToGrammars.get(item
					.getText()) : ""; //$NON-NLS-1$
			grammarText.setText(gText);
			String colorizerId = TextPlugin.getColorizerPreference(item.getText());
			if (activeProviders.containsKey(colorizerId))
			{
				final LanguageStructureProvider provider = (LanguageStructureProvider) activeProviders.get(colorizerId);
				colorizer.setProvider(provider);
			}
			else
			{
				AttributeSniffer sniffer = new AttributeSniffer("lexer", "language"); //$NON-NLS-1$ //$NON-NLS-2$
				try
				{
					sniffer.read(gText);
					final String language = sniffer.getMatchedValue();
					if (language != null)
					{
						if (!LanguageRegistry.hasTokenList(language))
						{
							FileInputStream input = new FileInputStream(gText);
							TokenList tokenList = LanguageRegistry.createTokenList(input);
							LanguageRegistry.registerTokenList(tokenList);
						}

						ColorizerReader reader = new ColorizerReader();
						LanguageColorizer lc = reader.loadColorization(colorizerId, true);
						if (lc == null)
						{
							lc = new LanguageColorizer(language);
							LanguageRegistry.registerLanguageColorizer(language, lc);
							LanguageRegistry.setPreferenceId(language, colorizerId);
						}
						LanguageStructureProvider provider = new LanguageStructureProvider(language);
						colorizer.setProvider(provider);
						activeProviders.put(colorizerId, provider);
					}
				}
				catch (Exception e1)
				{
					colorizer.setProvider(null);
				}
			}
			remove.setEnabled(true);
		}

	};

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		this.noDefaultAndApplyButton();
		changed = false;
		imagesToDispose = new ArrayList<Image>();
		mappings = new ArrayList<FileEditorMapping>();
		mappingsToGrammars = new HashMap<String, String>();
		activeProviders = new HashMap<String, LanguageStructureProvider>();
		displayArea = new Composite(parent, SWT.NONE);
		final Composite buffer = new Composite(displayArea, SWT.NONE);
		header = new PreferenceMastHead(buffer,
				Messages.TextEditorPreferencePage_Allows_Users_To_Create_Custom_Editors, 3,
				TextPlugin.getImageDescriptor("images/generic_file.png")); //$NON-NLS-1$
		buffer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buffer.setBackground(PreferenceMastHead.HEADER_BG_COLOR);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 10;
		displayArea.setLayout(layout);
		buffer.setLayout(layout);
		buffer.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				GC gc = new GC(buffer);
				gc.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
				if (buffer.getSize().y - 4 >= 0)
				{
					gc.fillRectangle(0, buffer.getSize().y - 5, buffer.getSize().x, 5);
				}
			}

		});
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		description = new Label(displayArea, SWT.WRAP | SWT.LEFT);
		description.setText(Messages.TextEditorPreferencePage_ASSOCIATED_FILE_EXTENSIONS);
		Composite middle = new Composite(displayArea, SWT.NONE);
		layout = new GridLayout(2, false);
		GridData middleData = new GridData(SWT.FILL, SWT.FILL, true, false);
		middleData.heightHint = 120;
		middle.setLayout(layout);
		middle.setLayoutData(middleData);
		typesTable = new Table(middle, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		typesTable.setLayout(new GridLayout(1, true));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 100;
		typesTable.setLayoutData(data);
		typesTable.addSelectionListener(tableSelectionListener);
		Composite buttons = new Composite(middle, SWT.NONE);
		layout = new GridLayout(1, true);
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		add = new Button(buttons, SWT.PUSH);
		add.setText(StringUtils.ellipsify(Messages.TextEditorPreferencePage_ADD));
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		add.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileExtensionDialog dialog = new FileExtensionDialog(displayArea.getShell());
				if (dialog.open() == Window.OK)
				{
					changed = true;
					String newName = dialog.getName();
					String newExtension = dialog.getExtension();
					// Find the index at which to insert the new entry.
					String newFilename = (newName + (newExtension == null || newExtension.length() == 0 ? "" : "." + newExtension)).toUpperCase();//$NON-NLS-1$ //$NON-NLS-2$
					IFileEditorMapping newMapping;
					TableItem[] items = typesTable.getItems();
					boolean found = false;
					int i = 0;

					while (i < items.length && !found)
					{
						newMapping = (IFileEditorMapping) items[i].getData();
						int result = newFilename.compareToIgnoreCase(newMapping.getLabel());
						if (result == 0)
						{
							// Same resource type not allowed!
							MessageDialog.openInformation(getControl().getShell(),
									Messages.TextEditorPreferencePage_FILE_TYPE_EXISTS,
									Messages.TextEditorPreferencePage_FILE_TYPE_EXISTS_TITLE);
							return;
						}

						if (result < 0)
						{
							found = true;
						}
						else
						{
							i++;
						}
					}
					newMapping = getFileEditorMapping(newName, newExtension);
					addTypesTableItem(newMapping, i);
					typesTable.setFocus();
					typesTable.setSelection(i);
					tableSelectionListener.widgetSelected(null);
				}
			}

		});
		remove = new Button(buttons, SWT.PUSH);
		remove.setText(Messages.TextEditorPreferencePage_REMOVE_ASSOCIATION);
		remove.setEnabled(false);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		remove.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (typesTable.getSelectionCount() == 1)
				{
					changed = true;
					TableItem item = typesTable.getSelection()[0];
					FileEditorMapping data = (FileEditorMapping) item.getData();
					mappings.remove(data);
					browseGrammar.setEnabled(false);
					grammarText.setText(""); //$NON-NLS-1$
					remove.setEnabled(false);
					String label = item.getText();
					String prefId = TextPlugin.getColorizerPreference(label);
					UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(prefId, ""); //$NON-NLS-1$
					LanguageStructureProvider provider = (LanguageStructureProvider) activeProviders.remove(prefId);
					if (provider != null)
					{
						LanguageRegistry.unregisterLanguageColorizer(provider.getLanguage());
						TokenList tokenList = LanguageRegistry.getTokenList(provider.getLanguage());
						if (tokenList != null)
						{
							LanguageRegistry.unregisterTokenList(tokenList);
						}
						UnifiedEditorsPlugin.getDefault().getPreferenceStore().firePropertyChangeEvent(
								"Colorization removed", "Colorization removed", "Colorization removed"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					colorizer.setProvider(null);
					item.dispose();
				}
			}

		});
		IEditorRegistry registry = EclipseUIUtils.getWorkbenchEditorRegistry();
		// Look up generic text editor
		descriptor = (EditorDescriptor) registry.findEditor(GenericTextEditor.ID);
		IFileEditorMapping[] array = registry.getFileEditorMappings();
		int count = 0;
		for (int i = 0; i < array.length; i++)
		{
			FileEditorMapping mapping = (FileEditorMapping) array[i];
			mapping = (FileEditorMapping) mapping.clone(); // want a copy
			mappings.add(mapping);
			if (mapping.getDefaultEditor() != null && mapping.getDefaultEditor().getId().equals(GenericTextEditor.ID))
			{
				String label = mapping.getLabel();
				String extension = mapping.getExtension();

				// This check allows us to internally contribute generic text editor association without making them
				// visible in the pref page (but configurable elsewhere)
				if (LanguageRegistry.getTokenListByExtension(extension) == null)
				{
					String grammarFile = store.getString(TextPlugin.getGrammarPreference(label));
					if (!grammarFile.equals("")) //$NON-NLS-1$
					{
						mappingsToGrammars.put(label, grammarFile);
					}
					addTypesTableItem(mapping, count);
					count++;
				}
			}
		}
		grammarGroup = new Group(displayArea, SWT.NONE);
		grammarGroup.setLayout(new GridLayout(2, false));
		grammarGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grammarGroup.setText(Messages.TextEditorPreferencePage_GRAMMAR_FILE);
		grammarText = new Text(grammarGroup, SWT.READ_ONLY | SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		ModifyListener gListener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				if (typesTable.getSelectionCount() == 1)
				{
					TableItem item = typesTable.getSelection()[0];
					mappingsToGrammars.put(item.getText(), grammarText.getText());
					changed = true;
				}
			}

		};
		grammarText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grammarText.addModifyListener(gListener);
		browseGrammar = new Button(grammarGroup, SWT.PUSH);
		browseGrammar.setText(StringUtils.ellipsify(Messages.TextEditorPreferencePage_BROWSE));
		browseGrammar.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(displayArea.getShell());
				fileDialog.setFilterExtensions(new String[] { "*.lxr" }); //$NON-NLS-1$
				String fileName = fileDialog.open();
				if (fileName != null)
				{

					try
					{
						AttributeSniffer sniffer = new AttributeSniffer("lexer", "language"); //$NON-NLS-1$ //$NON-NLS-2$
						sniffer.read(fileName);
						String newLanguage = sniffer.getMatchedValue();
						if (newLanguage != null)
						{
							if (!LanguageRegistry.hasTokenList(newLanguage))
							{
								grammarText.setText(fileName);
								tableSelectionListener.widgetSelected(e);
							}
							else
							{
								UIJob errorJob = new UIJob(Messages.TextEditorPreferencePage_Error_Loading_Language)
								{

									public IStatus runInUIThread(IProgressMonitor monitor)
									{
										MessageDialog.openError(getShell(),
												Messages.TextEditorPreferencePage_Error_Adding_Language,
												Messages.TextEditorPreferencePage_Language_Already_Supported);
										return Status.OK_STATUS;
									}

								};
								errorJob.schedule();
							}
						}
					}
					catch (final Exception e1)
					{
						UIJob errorJob = new UIJob(Messages.TextEditorPreferencePage_Error_Loading_Lexer_File)
						{

							public IStatus runInUIThread(IProgressMonitor monitor)
							{
								MessageDialog.openError(getShell(),
										Messages.TextEditorPreferencePage_Error_Loading_Lexer_File, e1.getMessage());
								return Status.OK_STATUS;
							}

						};
						errorJob.schedule();
					}
				}
			}

		});
		browseGrammar.setEnabled(false);
		colorizerGroup = new Group(displayArea, SWT.NONE);
		colorizerGroup.setText(Messages.TextEditorPreferencePage_COLORIATION_FILE);
		colorizerGroup.setLayout(new GridLayout(1, false));
		colorizerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		applyChanges = new Button(colorizerGroup, SWT.PUSH);
		applyChanges.setText(Messages.TextEditorPreferencePage_Apply_Changes);
		applyChanges.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (typesTable.getSelectionCount() == 1)
				{
					TableItem item = typesTable.getSelection()[0];
					String colorizerId = TextPlugin.getColorizerPreference(item.getText());
					if (activeProviders.containsKey(colorizerId))
					{
						LanguageStructureProvider provider = (LanguageStructureProvider) activeProviders
								.get(colorizerId);
						LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(provider.getLanguage());
						provider.buildLanguageColorizer(colorizer, colorizerId);
					}
				}
			}

		});
		applyChanges.setEnabled(true);
		colorizer = new LanguageColorizationWidget();
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		colorizer.createControl(colorizerGroup, data);
		colorizer.collapseAll();

		return displayArea;
	}

	private IFileEditorMapping getFileEditorMapping(String name, String extension)
	{
		FileEditorMapping mapping = null;
		String label = (name + (extension == null || extension.length() == 0 ? "" : "." + extension)).toUpperCase();//$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < mappings.size(); i++)
		{
			FileEditorMapping curr = (FileEditorMapping) mappings.get(i);
			if (label.equalsIgnoreCase(curr.getLabel()))
			{
				mapping = curr;
				break;
			}
		}
		if (mapping == null)
		{
			mapping = new FileEditorMapping(name, extension);
			mappings.add(mapping);
		}
		mapping.setDefaultEditor(descriptor);
		return mapping;
	}

	private void addTypesTableItem(IFileEditorMapping mapping, int index)
	{
		Image image = mapping.getImageDescriptor().createImage(false);
		TableItem item = new TableItem(typesTable, SWT.NONE, index);
		if (image != null)
		{
			imagesToDispose.add(image);
			item.setImage(image);
		}
		item.setText(mapping.getLabel());
		item.setData(mapping);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		if (imagesToDispose != null)
		{
			for (Iterator e = imagesToDispose.iterator(); e.hasNext();)
			{
				((Image) e.next()).dispose();
			}
			imagesToDispose = null;
		}
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		if (changed)
		{
			Iterator grammarIter = mappingsToGrammars.keySet().iterator();
			while (grammarIter.hasNext())
			{
				String label = (String) grammarIter.next();
				String grammarFile = (String) mappingsToGrammars.get(label);
				store.setValue(TextPlugin.getGrammarPreference(label), grammarFile);
			}
			FileEditorMapping[] _mappings = new FileEditorMapping[mappings.size()];
			for (int i = 0; i < _mappings.length; i++)
			{
				_mappings[i] = (FileEditorMapping) mappings.get(i);
			}
			EditorRegistry registry = (EditorRegistry) WorkbenchPlugin.getDefault().getEditorRegistry();
			registry.setFileEditorMappings(_mappings);
			registry.saveAssociations();
			PrefUtil.savePrefs();
		}
		Iterator iter = activeProviders.keySet().iterator();
		while (iter.hasNext())
		{
			String prefId = (String) iter.next();
			LanguageStructureProvider provider = (LanguageStructureProvider) activeProviders.get(prefId);
			LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(provider.getLanguage());
			provider.buildLanguageColorizer(colorizer, prefId);
			provider.disposeImages();
		}
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performCancel()
	 */
	public boolean performCancel()
	{
		Iterator iter = activeProviders.keySet().iterator();
		while (iter.hasNext())
		{
			String prefId = (String) iter.next();
			LanguageStructureProvider provider = (LanguageStructureProvider) activeProviders.get(prefId);
			provider.disposeImages();
		}
		return super.performCancel();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		store = TextPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		if (header != null)
		{
			header.setVisible(visible);
		}
		super.setVisible(visible);
	}

}
