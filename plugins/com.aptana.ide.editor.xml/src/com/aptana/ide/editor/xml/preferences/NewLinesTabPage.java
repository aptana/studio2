/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.editor.xml.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.CompilationUnitPreview;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.Preview;

/**
 * 
 *
 */
public class NewLinesTabPage extends FormatterTabPage
{

	String editor;

	/**
	 * Constant array for boolean selection
	 */
	private static String[] FALSE_TRUE = { DefaultCodeFormatterConstants.FALSE, DefaultCodeFormatterConstants.TRUE };

	@SuppressWarnings("nls")
	private static final String PREVIEW = "<channel rdf:about=\"http://weblogs.java.net/blog/editors/\">\r\n" + //$NON-NLS-1$
			"<title>Editor&apos;s Daily Blog</title>\r\n" //$NON-NLS-1$
			+ "<link>http://weblogs.java.net/blog/editors/</link>\r\n" //$NON-NLS-1$
			+ "<description>A daily update from our java.net editor, Chris Adamson, and other items from the java.net front page.</description>\r\n" //$NON-NLS-1$
			+ "<dc:language>en-us</dc:language>\r\n" //$NON-NLS-1$
			+ "<dc:creator></dc:creator>\r\n" //$NON-NLS-1$
			+ "<dc:date>2007-10-12T10:48:50+00:00</dc:date>\r\n" //$NON-NLS-1$
			+ "<admin:generatorAgent rdf:resource=\"http://www.movabletype.org/?v=3.01D\" />\r\n" //$NON-NLS-1$
			+ "\r\n" //$NON-NLS-1$
			+ "\r\n" //$NON-NLS-1$
			+ "<items>\r\n" //$NON-NLS-1$
			+ "<rdf:Seq><rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/hammer_and_a_na.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/lay_my_head_dow.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/power_of_two.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/run_1.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/closer_to_fine.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/more_adventurou.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/does_he_love_yo.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/its_a_hit.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/close_call.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/go_ahead.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/everyday.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_space_betwe.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_best_of_wha.html\" />\r\n" + //$NON-NLS-1$
			"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/what_would_you.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/out_of_my_hands.html\" />\r\n" //$NON-NLS-1$
			+ "</rdf:Seq>\r\n" //$NON-NLS-1$
			+ "</items>\r\n" + "\r\n" + "</channel>\r\n" + "\r\n" + "</rdf:RDF>" + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + "\r\n" + "<rdf:RDF\r\n" + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n" + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n" + "xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\"\r\n" + "xmlns:admin=\"http://webns.net/mvcb/\"\r\n" + "xmlns:cc=\"http://web.resource.org/cc/\"\r\n" + "xmlns=\"http://purl.org/rss/1.0/\">\r\n" + "\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$
	/**
	 * 
	 */
	protected CheckboxPreference fThenStatementPref;
	/**
	 * 
	 */
	protected CheckboxPreference fSimpleIfPref;

	private CompilationUnitPreview fPreview;

	/**
	 * 
	 *
	 */
	class NewLineController
	{
		private Group nodeGroup;

		private Composite buttons;

		private Table foldingTable;

		private Button add;

		private Button remove;

		private String title;

		private String key;

		/**
		 * @param title
		 * @param key
		 */
		public NewLineController(String title, String key)
		{
			this.title = title;
			this.key = key;
		}

		/**
		 * @param composite
		 */
		protected void doCreatePartControl(Composite composite)
		{

			nodeGroup = new Group(composite, SWT.NONE);
			GridLayout groupLayout = new GridLayout(1, true);
			nodeGroup.setLayout(groupLayout);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.horizontalSpan = 3;
			nodeGroup.setLayoutData(gridData);
			nodeGroup.setText(title);
			buttons = new Composite(nodeGroup, SWT.NONE);
			GridLayout buttonsLayout = new GridLayout(2, true);
			buttonsLayout.marginHeight = 0;
			buttonsLayout.marginWidth = 0;
			buttons.setLayout(buttonsLayout);
			add = new Button(buttons, SWT.PUSH);
			add.setImage(XMLPlugin.getImage("icons/add_obj.gif")); //$NON-NLS-1$
			add.setToolTipText(Messages.NewLinesTabPage_ADD_TOOLTIP);
			add.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dialog = new InputDialog(nodeGroup.getShell(), Messages.NewLinesTabPage_ADD_TITLE,
							Messages.NewLinesTabPage_ADD_DESCRIPTION, null, null);
					int rc = dialog.open();
					if (rc == InputDialog.OK)
					{
						String newNodes = dialog.getValue();
						String[] nodes = newNodes.split(","); //$NON-NLS-1$
						List<String> nodeList = new ArrayList<String>();
						for (int i = 0; i < nodes.length; i++)
						{
							String trimmed = nodes[i].trim();
							if (trimmed.length() > 0)
							{
								nodeList.add(trimmed);
							}
						}
						TableItem[] items = foldingTable.getItems();
						for (int i = 0; items != null && i < items.length; i++)
						{
							if (!nodeList.contains(items[i].getText()))
							{
								nodeList.add(items[i].getText());
							}
						}
						Collections.sort(nodeList);
						foldingTable.removeAll();
						for (int i = 0; i < nodeList.size(); i++)
						{
							TableItem item = new TableItem(foldingTable, SWT.LEFT);
							item.setText((String) nodeList.get(i));
							item.setImage(XMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
						}
						update();
					}
				}

			});
			remove = new Button(buttons, SWT.PUSH);
			remove.setImage(XMLPlugin.getImage("icons/delete_obj.gif")); //$NON-NLS-1$
			remove.setToolTipText(Messages.NewLinesTabPage_REMOVE_TOOLTIP);
			remove.setEnabled(false);
			remove.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					TableItem[] items = foldingTable.getSelection();
					int[] indices = foldingTable.getSelectionIndices();
					if (items != null)
					{
						for (int i = 0; i < items.length; i++)
						{
							items[i].dispose();
						}
						if (indices.length > 1)
						{
							int last = indices[indices.length - 1];
							if (foldingTable.getItemCount() - 1 >= last)
							{
								foldingTable.setSelection(last);
							}
							else if (foldingTable.getItemCount() > 0)
							{
								foldingTable.setSelection(foldingTable.getItemCount() - 1);
							}
						}
						else if (indices.length == 1)
						{
							if (foldingTable.getItemCount() - 1 >= indices[0])
							{
								foldingTable.setSelection(indices[0]);
							}
							else if (foldingTable.getItemCount() > 0)
							{
								foldingTable.setSelection(foldingTable.getItemCount() - 1);
							}
						}
					}
					update();
					remove.setEnabled(foldingTable.getSelectionCount() > 0);
				}

			});
			buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			foldingTable = new Table(nodeGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			foldingTable.setLayoutData(gridData);
			foldingTable.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					remove.setEnabled(true);
				}

			});
			String foldedNodeList = (String) fWorkingValues.get(key);
			if (foldedNodeList == null)
			{
				return;
			}
			if (foldedNodeList.length() == 0)
			{
				return;
			}
			String[] nodes = foldedNodeList.split(","); //$NON-NLS-1$
			for (int i = 0; i < nodes.length; i++)
			{
				TableItem item = new TableItem(foldingTable, SWT.LEFT);
				item.setText(nodes[i].trim());
				item.setImage(XMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
			}

		}

		/**
		 * @return string
		 */
		protected String createString()
		{
			String nodeListPref = ""; //$NON-NLS-1$
			for (int i = 0; i < foldingTable.getItemCount(); i++)
			{
				TableItem item = foldingTable.getItem(i);
				nodeListPref += item.getText() + Messages.NewLinesTabPage_9;
			}
			return nodeListPref;
		}
	}

	NewLineController doNotWrap = new NewLineController(Messages.NewLinesTabPage_GROUP_TITLE,
			DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2);
	NewLineController allwaysWrap = new NewLineController(Messages.NewLinesTabPage_WRAPGROUP_TITLE,
			DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2);

	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public NewLinesTabPage(ModifyDialog modifyDialog, Map workingValues, String editor)
	{
		super(modifyDialog, workingValues);
		this.editor = editor;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns)
	{
		doNotWrap.doCreatePartControl(composite);
		allwaysWrap.doCreatePartControl(composite);
		createCheckboxPref(composite, 2, Messages.NewLinesTabPage_DO_NOT_WRAP,
				DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, FALSE_TRUE);
	}

	/**
	 * updates it
	 */
	protected void update()
	{
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2, doNotWrap.createString());
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2, allwaysWrap.createString());
		fUpdater.update(null, this);
		notifyValuesModified();
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage()
	{
		fPreview.setPreviewText(PREVIEW);
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected Preview doCreateJavaPreview(Composite parent)
	{
		fPreview = new CompilationUnitPreview(fWorkingValues, parent, editor, null);
		return fPreview;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
	 */
	protected void doUpdatePreview()
	{
		super.doUpdatePreview();
		fPreview.update();
	}

}
