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
package com.aptana.ide.editor.html.preferences;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;

/**
 * 
 *
 */
public class IndentationTabPage extends BaseHTMLFormatterPage
{

	private NewLineController doNotIndent = new NewLineController(Messages.IndentationTabPage_DO_NOT_INDENT_ON,
			DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS);

	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public IndentationTabPage(ModifyDialog modifyDialog, Map workingValues, String editor)
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
		doNotIndent.doCreatePartControl(composite);
	}

	/**
	 * updates it
	 */
	protected void update()
	{
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS, doNotIndent.createString());
		fUpdater.update(null, this);
		notifyValuesModified();
	}

}
