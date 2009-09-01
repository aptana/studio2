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
package com.aptana.ide.editors.unified;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.Page;

import com.aptana.ide.core.ui.PreferenceUtils;

/**
 * This message page can be used to display the "Aptana" style message when a view has no content. The setMessage method
 * sets the string displayed centered vertically and horizontally with light gray color and 12 pt Arial font on white
 * background.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UnifiedMessagePage extends Page
{

	private Composite pgComp;
	private Label msgLabel;
	private String message = "";//$NON-NLS-1$
	private Font labelFont;

	/**
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		pgComp = new Composite(parent, SWT.NONE);
		pgComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		pgComp.setLayout(new GridLayout(1, true));
		msgLabel = new Label(pgComp, SWT.CENTER | SWT.WRAP);
		msgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		// pgComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// msgLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		labelFont = new Font(parent.getDisplay(), "Arial", 12, SWT.NONE); //$NON-NLS-1$
		msgLabel.setFont(labelFont);
		PreferenceUtils.ignoreForegroundColorPreference(msgLabel);
		msgLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose()
	{
		if (labelFont != null)
		{
			labelFont.dispose();
		}
		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl()
	{
		return pgComp;
	}

	/**
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	public void setFocus()
	{
		pgComp.setFocus();
	}

	/**
	 * Sets the message to the given string.
	 * 
	 * @param message
	 *            the message text
	 */
	public void setMessage(String message)
	{
		this.message = message;
		if (msgLabel != null)
		{
			msgLabel.setText(this.message);
		}
	}

}
