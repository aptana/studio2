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
package com.aptana.ide.snippets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.ui.SWTUtils;

/**
 * @author Kevin Lindsey
 */
public class SnippetDialog extends Dialog
{

	/**
	 * A public flag indicating if the user pressed the OK button or canceled the dialog
	 */
	public boolean OK;

	private Text[] textFields;

	private SnippetVariable[] _variables;

	private Snippet snippet;

	/**
	 * @param parentShell
	 * @param snippet
	 *            to fill variables
	 */
	protected SnippetDialog(Shell parentShell, Snippet snippet)
	{
		super(parentShell);
		// Make the dialog resizable
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.snippet = snippet;
	}

	private void createContents(final Shell shell)
	{
		// create group
		Group group = new Group(shell, SWT.SHADOW_IN);
		group.setText(Messages.SnippetDialog_Variable_Group_Header);

		// set group layout
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		group.setLayout(groupLayout);

		// set group's layout data
		GridData groupLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		groupLayoutData.grabExcessHorizontalSpace = true;
		group.setLayoutData(groupLayoutData);

		textFields = new Text[this._variables.length];
		for (int i = 0; i < this._variables.length; i++)
		{
			SnippetVariable variable = this._variables[i];
			String name = variable.getName();
			String tooltip = variable.getDescription();

			// create label
			Label description = new Label(group, SWT.LEFT);
			description.setText(tooltip);
			description.setFont(new Font(Display.getDefault(), SWTUtils.resizeFont(description.getFont(), -3)));

			GridData textLayoutData = new GridData(GridData.FILL_HORIZONTAL);
			textLayoutData.minimumWidth = 120;
			textLayoutData.horizontalSpan = 2;
			description.setLayoutData(textLayoutData);

			// create label
			Label label = new Label(group, SWT.RIGHT);
			label.setText(name);

			// set label layout data
			// does not enough some times;
			// GridData labelLayoutData = new GridData(64, 16);
			// label.setLayoutData(labelLayoutData);

			// create text field
			Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
			text.setText(""); //$NON-NLS-1$
			text.setToolTipText(tooltip);
			// set text layout data
			textLayoutData = new GridData(GridData.FILL_HORIZONTAL);
			textLayoutData.minimumWidth = 120;
			text.setLayoutData(textLayoutData);
			textFields[i] = text;
			
		}
	}

	/**
	 * 
	 */
	public void okPressed()
	{
		for (int i = 0; i < _variables.length; i++)
		{
			_variables[i].setValue(textFields[i].getText());
		}
		OK = true;
		super.okPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		// save reference to snippet variables
		this._variables = snippet.getVariables();
		// create window
		Shell shell = getShell();
		int width = 230;
		int height = 240;
		shell.setText(snippet.getName());
		Point centerPoint = null;
		Rectangle bounds = parent.getBounds();
		Monitor monitor = parent.getMonitor();
		Rectangle monitorBounds = monitor.getClientArea();
		centerPoint = Geometry.centerPoint(bounds);
		centerPoint = new Point(centerPoint.x - (width / 2), Math.max(monitorBounds.y, Math.min(centerPoint.y
				- (height * 2 / 3), monitorBounds.y + monitorBounds.height - height)));

		shell.setBounds(centerPoint.x, centerPoint.y, width, height);
		shell.setMinimumSize(width, height);
		// create window's layout
		GridLayout windowLayout = new GridLayout();
		windowLayout.numColumns = 1;
		windowLayout.marginHeight = 5;
		windowLayout.marginWidth = 5;
		shell.setLayout(windowLayout);
		// create contents
		this.createContents(shell);
		shell.pack(true);
		return super.createContents(parent);
	}
}
