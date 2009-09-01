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
package com.aptana.ide.editor.html.toolbar;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.formatting.HTMLCodeFormatter;
import com.aptana.ide.io.SourceWriter;

/**
 * @author Pavel Petrochenko
 */
public class InsertTableDialog extends TitleAreaDialog
{

	private Text t_rows;
	private Text t_cols;
	private Text t_width;
	private Combo cmb_size;
	private Text t_thickness;
	private Text t_padding;
	private Text t_spacing;
	private Button b_alignNone;
	private Button b_alignLeft;
	private Button b_alignTop;
	private Button b_alignBoth;
	private Text t_caption;
	private StyledText t_summary;
	private String result;
	private Combo cmb_alignCaption;
	private String lineDelimeter;
	private IProject project;

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite createDialogArea = (Composite) super.createDialogArea(parent);
		Composite cl = new Composite(createDialogArea, SWT.NONE);
		GridLayout grl0 = new GridLayout(1, false);
		grl0.marginHeight = 5;
		grl0.marginWidth = 5;
		cl.setLayout(grl0);
		Group gr = new Group(cl, SWT.NONE);
		gr.setText(Messages.InsertTableDialog_TableSize);
		createLabel(gr, Messages.InsertTableDialog_Rows);
		t_rows = new Text(gr, SWT.BORDER);
		setFillHorizontal(t_rows);
		createLabel(gr, Messages.InsertTableDialog_Columns);
		t_cols = new Text(gr, SWT.BORDER);
		setFillHorizontal(t_cols);
		gr.setLayout(new GridLayout(4, false));
		cl.setLayoutData(new GridData(GridData.FILL_BOTH));
		createLabel(gr, Messages.InsertTableDialog_Width);
		Composite cm = new Composite(gr, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		cm.setLayout(gridLayout);
		t_width = new Text(cm, SWT.BORDER);
		setFillHorizontal(t_width);
		cmb_size = new Combo(cm, SWT.BORDER);
		setFillHorizontal(cm);
		cmb_size.setItems(new String[] { Messages.InsertTableDialog_Pixels, Messages.InsertTableDialog_Percents });
		cmb_size.select(0);
		createLabel(gr, Messages.InsertTableDialog_Thickness);
		Composite cm1 = new Composite(gr, SWT.NONE);
		GridLayout gridLayout1 = new GridLayout(2, false);
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		cm1.setLayout(gridLayout1);
		t_thickness = new Text(cm1, SWT.BORDER);
		setFillHorizontal(t_thickness);
		createLabel(cm1, Messages.InsertTableDialog_PX);
		setFillHorizontal(cm1);

		createLabel(gr, Messages.InsertTableDialog_CellPadding);
		t_padding = new Text(gr, SWT.BORDER);
		setFillHorizontal(t_padding);
		createLabel(gr, Messages.InsertTableDialog_CellSpacing);
		t_spacing = new Text(gr, SWT.BORDER);
		setFillHorizontal(t_spacing);
		setFillHorizontal(gr);
		Group gr1 = new Group(cl, SWT.NONE);
		setFillHorizontal(gr1);
		gr1.setText(Messages.InsertTableDialog_Header);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		gr1.setLayout(fillLayout);
		b_alignNone = new Button(gr1, SWT.RADIO);
		b_alignNone.setSelection(true);
		b_alignNone.setText(Messages.InsertTableDialog_None);
		b_alignLeft = new Button(gr1, SWT.RADIO);
		b_alignLeft.setText(Messages.InsertTableDialog_Left);
		b_alignTop = new Button(gr1, SWT.RADIO);
		b_alignTop.setText(Messages.InsertTableDialog_Top);
		b_alignBoth = new Button(gr1, SWT.RADIO);
		b_alignBoth.setText(Messages.InsertTableDialog_Both);
		Group gr2 = new Group(cl, SWT.NONE);
		setFillHorizontal(gr1);
		gr2.setText(Messages.InsertTableDialog_Accessibility);
		setFillHorizontal(gr2);
		fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginHeight = 10;
		fillLayout.marginWidth = 10;
		gr1.setLayout(fillLayout);
		gr2.setLayout(new GridLayout(4, false));
		createLabel(gr2, Messages.InsertTableDialog_Caption);
		t_caption = new Text(gr2, SWT.BORDER);
		createLabel(gr2, Messages.InsertTableDialog_AlignCaption);
		cmb_alignCaption = new Combo(gr2, SWT.BORDER);
		setFillHorizontal(t_caption);
		cmb_alignCaption.setItems(new String[] { Messages.InsertTableDialog_Default, Messages.InsertTableDialog_left,
				Messages.InsertTableDialog_right, Messages.InsertTableDialog_Top, Messages.InsertTableDialog_Bottom });
		cmb_alignCaption.select(0);
		this.setTitle(Messages.InsertTableDialog_InsertTable);
		Group gr3 = new Group(gr2, SWT.NONE);
		gr3.setLayout(new FillLayout());
		gr3.setText(Messages.InsertTableDialog_Summary);
		t_summary = new StyledText(gr3, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData5 = new GridData(GridData.FILL_BOTH);
		gridData5.minimumHeight = 100;
		gridData5.horizontalSpan = 4;
		gr3.setLayoutData(gridData5);
		this.setMessage(Messages.InsertTableDialog_Description);
		this.getShell().setText(Messages.InsertTableDialog_Title);
		
		setIntValue(t_cols, 5);
		setIntValue(t_rows, 5);
		setIntValue(t_width, 200);
		setIntValue(t_thickness, 1);
		addValidation(t_caption);
		addValidation(t_cols);
		addValidation(t_padding);
		addValidation(t_rows);
		addValidation(t_spacing);
		addValidation(t_thickness);
		addValidation(t_width);
		validate();
		return createDialogArea;
	}

	private void setFillHorizontal(Control t1)
	{
		t1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void setIntValue(Text t, int value)
	{
		t.setText(Integer.toString(value));
	}

	private void createLabel(Composite gr, String string)
	{
		Label l1 = new Label(gr, SWT.NONE);
		l1.setText(string);
	}

	/**
	 * @param t
	 */
	public void addValidation(Text t)
	{
		t.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}

		});
	}

	/**
	 * 
	 */
	public void validate()
	{
		Button button = getOKButton();
		if (button != null)
		{
			button.setEnabled(true);
		}
		setErrorMessage(null);
		validateInt(t_rows, Messages.InsertTableDialog_RowsV, 0);
		validateInt(t_cols, Messages.InsertTableDialog_ColumnsV, 0);
		validateIntOrSpace(t_padding, Messages.InsertTableDialog_PaddingV);
		validateIntOrSpace(t_spacing, Messages.InsertTableDialog_SpacingV);
		validateIntOrSpace(t_thickness, Messages.InsertTableDialog_ThicknessV);
		validateInt(t_width, Messages.InsertTableDialog_WidthV, 0);
	}

	/**
	 * @param t
	 * @param string
	 * @param min
	 */
	public void validateInt(Text t, String string, int min)
	{
		String text = t.getText();
		Button button = getOKButton();
		try
		{
			int parseInt = Integer.parseInt(text);
			if (parseInt <= min)
			{
				setErrorMessage(StringUtils.format(Messages.InsertTableDialog_GreaterErrorMessage, new Object[] {
						string, new Integer(min) }));
				if (button != null)
				{
					button.setEnabled(false);
				}
			}
		}
		catch (NumberFormatException e)
		{
			setErrorMessage(StringUtils.format(Messages.InsertTableDialog_NumberValidationMessage, string));
			if (button != null)
			{
				button.setEnabled(false);
			}
		}
	}

	/**
	 * @param t
	 * @param string
	 */
	public void validateIntOrSpace(Text t, String string)
	{
		String text = t.getText();
		Button button = getOKButton();
		try
		{
			int parseInt = Integer.parseInt(text);
			if (parseInt < 0)
			{
				setErrorMessage(StringUtils.format(Messages.InsertTableDialog_GreaterErrorMessage, new Object[] {
						string, new Integer(0) }));
				if (button != null)
				{
					button.setEnabled(false);
				}
			}
		}
		catch (NumberFormatException e)
		{
			if (text.trim().length() > 0)
			{
				setErrorMessage(StringUtils.format(Messages.InsertTableDialog_NumberValidationMessage, string));
				if (button != null)
				{
					button.setEnabled(false);
				}
			}
		}
	}

	/**
	 * @param parentShell
	 */
	public InsertTableDialog(Shell parentShell,String lineDelimeter,IProject project)
	{
		super(parentShell);
		this.lineDelimeter=lineDelimeter;
		this.project=project;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		SourceWriter wr = new SourceWriter();
		wr.print("<table "); //$NON-NLS-1$
		String width = t_width.getText();
		if (cmb_size.getSelectionIndex() == 0)
		{
			wr.print(StringUtils.format("width=\"{0}\" ", width)); //$NON-NLS-1$
		}
		else
		{
			wr.print(StringUtils.format("width=\"{0}%\" ", width)); //$NON-NLS-1$	
		}
		String text = t_thickness.getText();
		if (text.trim().length() > 0)
		{
			wr.print(StringUtils.format("border=\"{0}\" ", text)); //$NON-NLS-1$
		}
		text = t_spacing.getText();
		if (text.trim().length() > 0)
		{
			wr.print(StringUtils.format("cellspacing=\"{0}\" ", text)); //$NON-NLS-1$
		}
		text = t_padding.getText();
		if (text.trim().length() > 0)
		{
			wr.print(StringUtils.format("cellpadding=\"{0}\" ", text)); //$NON-NLS-1$
		}

		text = t_summary.getText();
		if (text.trim().length() > 0)
		{
			wr.print(StringUtils.format("summary=\"{0}\" ", text)); //$NON-NLS-1$
		}

		wr.println(">"); //$NON-NLS-1$
		text = t_caption.getText();
		wr.increaseIndent();
		if (text.trim().length() > 0)
		{
			wr.printIndent();
			wr.print("<caption"); //$NON-NLS-1$wr.println(">");
			int k = cmb_alignCaption.getSelectionIndex();
			if (k > 0)
			{
				String vl = ""; //$NON-NLS-1$
				switch (k)
				{
					case 1:
						vl = "left"; //$NON-NLS-1$
						break;
					case 2:
						vl = "right"; //$NON-NLS-1$
						break;
					case 3:
						vl = "top"; //$NON-NLS-1$
						break;
					case 4:
						vl = "bottom"; //$NON-NLS-1$
						break;
					default:
						break;
				}
				wr.print(StringUtils.format(" align=\"{0}\" ", vl)); //$NON-NLS-1$
			}
			wr.print(">"); //$NON-NLS-1$			
			wr.print(text);
			wr.println("</caption>"); //$NON-NLS-1$
		}
		int rows = Integer.parseInt(t_rows.getText());
		int cols = Integer.parseInt(t_cols.getText());
		for (int a = 0; a < rows; a++)
		{
			wr.printlnWithIndent("<tr>"); //$NON-NLS-1$
			wr.increaseIndent();
			for (int b = 0; b < cols; b++)
			{
				boolean c = getCellOrHeader(a, b);
				if (c)
				{
					wr.printlnWithIndent("<td>"); //$NON-NLS-1$
					wr.printlnWithIndent("</td>"); //$NON-NLS-1$
				}
				else
				{
					wr.printlnWithIndent("<th>"); //$NON-NLS-1$
					wr.printlnWithIndent("</th>"); //$NON-NLS-1$	
				}
			}
			wr.decreaseIndent();
			wr.printlnWithIndent("</tr>"); //$NON-NLS-1$
		}
		wr.decreaseIndent();
		wr.println("</table>"); //$NON-NLS-1$
		result = wr.toString();
		HTMLCodeFormatter fr=new HTMLCodeFormatter();		
		result=fr.format(result, false, null, project , lineDelimeter);
		super.okPressed();
	}

	private boolean getCellOrHeader(int a, int b)
	{	
		if (b_alignNone.getSelection()){
			return true;
		}
		if (b_alignLeft.getSelection()){
			return b!=0;
		}
		if (b_alignTop.getSelection()){
			return a!=0;
		}
		if (b_alignBoth.getSelection()){
			return a!=0&&b!=0;
		}
		return false;
	}

	/**
	 * @return result;
	 */
	public String getHTMLText()
	{
		return result;
	}

}
