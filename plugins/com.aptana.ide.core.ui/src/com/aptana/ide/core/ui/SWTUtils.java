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
package com.aptana.ide.core.ui;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.ui.widgets.ISelectableWidget;

/**
 * Utilities to make SWT dialogs easier to manage
 * 
 * @author Ingo Muschenetz
 */
public class SWTUtils
{
	private static Color _greyColor;
	private static Color _errorColor;
	private static String SMALL_FONT = "com.aptana.ide.small_font"; //$NON-NLS-1$
	private static String LARGE_FONT = "com.aptana.ide.large_font"; //$NON-NLS-1$

	static
	{
		RGB greyRGB = new RGB(200, 200, 200);
		ColorRegistry cm = JFaceResources.getColorRegistry();
		cm.put("grey", greyRGB); //$NON-NLS-1$
		_greyColor = cm.get("grey"); //$NON-NLS-1$

		RGB errorRGB = new RGB(255, 255, 180);
		cm.put("error", errorRGB); //$NON-NLS-1$
		_errorColor = cm.get("error"); //$NON-NLS-1$
	}

	/**
	 * Protected constructor for utility class
	 */
	protected SWTUtils()
	{

	}

	/**
	 * Colors the background of a text widget based on the test value
	 * 
	 * @param widget
	 * @param testValue
	 */
	public static void colorBackground(Text widget, String testValue)
	{
		if (widget.getText().equals(testValue))
		{
			widget.setBackground(_greyColor);
		}
		else
		{
			widget.setBackground(null);
		}
	}

	/**
	 * Sets the text of the text widget, but only if the value is non-null;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @param text
	 *            the text to set
	 * @see #setTextWidgetValue(Text, String, String)
	 */
	public static void setTextWidgetValue(Text widget, String text)
	{
		if (text == null)
		{
			return;
		}
		else
		{
			widget.setText(text);
		}
	}
	
	/**
	 * Sets the text of the text widget, but only if the value is non-null;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @param text
	 *            the text to set
	 * @param def
	 *            A default string that will be set in case that the given text is null (nothing will happen in case
	 *            both the default string and the text are null)
	 * @see #setTextWidgetValue(Text, String)
	 */
	public static void setTextWidgetValue(Text widget, String text, String def)
	{
		if (text == null)
		{
			if (def !=  null)
			{
				widget.setText(def);
			}
			return;
		}
		else
		{
			widget.setText(text);
		}
	}

	/**
	 * Tests if the widget value is empty. If so, it adds an error color to the background of the cell;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @return boolean
	 */
	public static boolean testWidgetValue(Text widget)
	{
		if (widget.getText() == null || "".equals(widget.getText())) //$NON-NLS-1$
		{
			widget.setBackground(_errorColor);
			final ModifyListener ml = new ModifyListener()
			{

				public void modifyText(ModifyEvent e)
				{
					Text t = (Text) e.widget;
					if (t.getText() != null && !"".equals(t.getText())) //$NON-NLS-1$
					{
						t.setBackground(null);
					}
					else
					{
						t.setBackground(_errorColor);
					}
				}
			};
			widget.addModifyListener(ml);
			return false;
		}
		return true;
	}

	/**
	 * Tests if the widget value is empty. If so, it adds an error color to the background of the cell;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @param validSelectionIndex
	 *            the first item that is a "valid" selection
	 * @return boolean
	 */
	public static boolean testWidgetValue(Combo widget, int validSelectionIndex)
	{
		final int selectionIndex;
		if (validSelectionIndex > 0)
		{
			selectionIndex = validSelectionIndex;
		}
		else
		{
			selectionIndex = 0;
		}

		if (widget.getText() == null || "".equals(widget.getText()) || widget.getSelectionIndex() < selectionIndex) //$NON-NLS-1$
		{
			widget.setBackground(_errorColor);
			final ModifyListener ml = new ModifyListener()
			{

				public void modifyText(ModifyEvent e)
				{
					Combo t = (Combo) e.widget;
					if (t.getText() != null && !"".equals(t.getText()) || t.getSelectionIndex() >= selectionIndex) //$NON-NLS-1$
					{
						t.setBackground(null);
					}
					else
					{
						t.setBackground(_errorColor);
					}
				}
			};
			widget.addModifyListener(ml);
			return false;
		}
		return true;
	}
	
	/**
	 * Tests if the widget value is empty. If so, it adds an error color to the background of the cell;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @param validSelectionIndex
	 *            the first item that is a "valid" selection
	 * @return boolean
	 */
	public static boolean testWidgetValue(ISelectableWidget widget, int validSelectionIndex)
	{
		final int selectionIndex;
		if (validSelectionIndex > 0)
		{
			selectionIndex = validSelectionIndex;
		}
		else
		{
			selectionIndex = 0;
		}

		if (widget.getText() == null || "".equals(widget.getText()) || widget.getSelectionIndex() < selectionIndex) //$NON-NLS-1$
		{
			widget.setBackground(_errorColor);
			final ModifyListener ml = new ModifyListener()
			{

				public void modifyText(ModifyEvent e)
				{
					Combo t = (Combo) e.widget;
					if (t.getText() != null && !"".equals(t.getText()) || t.getSelectionIndex() >= selectionIndex) //$NON-NLS-1$
					{
						t.setBackground(null);
					}
					else
					{
						t.setBackground(_errorColor);
					}
				}
			};
			widget.addModifyListener(ml);
			return false;
		}
		return true;
	}

	/**
	 * Centers the shell on screen, and re-packs it to the preferred size. Packing is necessary as otherwise dialogs
	 * tend to get cut off on the Mac
	 * 
	 * @param shell
	 *            The shell to center
	 * @param parent
	 *            The shell to center within
	 */
	public static void centerAndPack(Shell shell, Shell parent)
	{
		center(shell, parent);
		pack(shell);
	}

	/**
	 * Centers the shell on screen.
	 * 
	 * @param shell
	 *            The shell to center
	 * @param parent
	 *            The shell to center within
	 */
	public static void center(Shell shell, Shell parent)
	{
		Rectangle parentSize = parent.getBounds();
		Rectangle mySize = shell.getBounds();

		int locationX, locationY;
		locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
		locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

	/**
	 * Re-packs it to the preferred size. Packing is necessary as otherwise dialogs tend to get cut off on the Mac
	 * 
	 * @param shell
	 *            The shell to center
	 */
	public static void pack(Shell shell)
	{
		shell.pack();
	}

	/**
	 * Echos out "*" characters when typing in a text field. This simulates a SWT.PASSWORD field, which does not work on
	 * the Mac.
	 * 
	 * @param text
	 */
	public static void setTextAsPassword(Text text)
	{
		char cbit = '*';
		if (CoreUIUtils.onMacOSX)
		{
			cbit = (char) (('*' << 8) + '*');
		}
		text.setEchoChar(cbit);
	}

	/**
	 * Indicated that this field is a "default", and so it contains a default value and is shaded unless a new value is
	 * entered
	 * 
	 * @param text
	 *            The text widget to designate as "default"
	 * @param defaultValue
	 *            The default string value to use
	 */
	public static void setFieldWithDefaultValue(final Text text, final String defaultValue)
	{
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				SWTUtils.colorBackground(text, defaultValue);
			}
		});
		text.setText(defaultValue);
	}

	/**
	 * Sets a text box to enable/disable based on a button click. Generally this is used to clear a text field based on
	 * a checkbox
	 * 
	 * @param button
	 *            the button to test for the selection state
	 * @param text
	 *            the text field to clear
	 * @param clearFieldOnDisable
	 *            do we blank out the text field if its disabled?
	 */
	public static void linkButtonAndField(final Button button, final Text text, final boolean clearFieldOnDisable)
	{
		button.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				if (button.getSelection())
				{
					text.setEnabled(true);
				}
				else
				{
					text.setEnabled(false);
					if (clearFieldOnDisable)
					{
						text.setText(""); //$NON-NLS-1$
					}
				}
			}
		});
	}

	/**
	 * Sets the columns of a table to percentage widths, rather than pixels
	 * 
	 * @param table
	 *            The table to modify
	 * @param columnPercentages
	 *            The array of percentage widths (ex. 0.2 = 20%). Does not need to equal 100%.
	 * @param columnMinimums
	 *            The array of minimum pixel widths for each column. 0 indicates whatever width works.
	 */
	public static void setTableColumnWidths(Table table, double[] columnPercentages, int[] columnMinimums)
	{
		Rectangle area = table.getClientArea();
		if (area.width == 0)
		{
			return;
		}
		Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int sideSpacer = 0;
		int spacer = table.getGridLineWidth() + table.getBorderWidth() * 2;
		int width = area.width;
		if (preferredSize.y > area.height)
		{
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = table.getVerticalBar().getSize();
			width -= vBarSize.x;
		}

		TableColumn[] columns = table.getColumns();

		for (int i = 0; i < columns.length; i++)
		{
			TableColumn column = columns[i];
			int w = (int) (width * columnPercentages[i]);
			if (i < columns.length - 1)
			{
				w += sideSpacer;
			}
			else
			{
				w += spacer;
			}
			if (w < 20)
			{
				w = 20;
			}
			column.setWidth(w);
		}
	}

	/**
	 * Calculates the percentage width of each column in the table relative to the total table size
	 * 
	 * @param table
	 *            the table to compute
	 * @param padding
	 *            a padding value to add to each table cell to space out the display a bit
	 * @return the array of values
	 */
	public static double[] calculateColumnPercents(Table table, double padding)
	{
		int sideSpacer = 0;
		int spacer = table.getGridLineWidth() + table.getBorderWidth() * 2;
		TableColumn[] columns = table.getColumns();
		double[] newPercentages = new double[columns.length];

		double[] widths = new double[columns.length];
		double totalWidth = 0;
		for (int i = 0; i < columns.length; i++)
		{
			TableColumn column = columns[i];
			int width = column.getWidth();
			int spc = spacer;
			if (i < columns.length - 1)
			{
				spc = sideSpacer;
			}
			widths[i] = width - spc;
			totalWidth += widths[i];
		}

		for (int i = 0; i < columns.length; i++)
		{
			newPercentages[i] = (widths[i] / totalWidth) + padding;
		}

		return newPercentages;
	}

	/**
	 * Calculates the width of each column in the table
	 * 
	 * @param table
	 *            the table to compute
	 * @return the array of values
	 */
	public static int[] calculateColumnWidths(Table table)
	{
		int sideSpacer = 0;
		TableColumn[] columns = table.getColumns();
		int spacer = table.getGridLineWidth() + table.getBorderWidth() * 2;
		int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++)
		{
			TableColumn column = columns[i];
			int width = column.getWidth();
			int spc = spacer;
			if (i < columns.length - 1)
			{
				spc = sideSpacer;
			}
			widths[i] = width - spc;
		}

		return widths;
	}

	/**
	 * Gets the default small font from the JFace font registry
	 * 
	 * @return - default small font
	 */
	public static Font getDefaultSmallFont()
	{
		Font small = JFaceResources.getFontRegistry().get(SMALL_FONT);
		if (small != null)
		{
			return small;
		}

		Font f = JFaceResources.getDefaultFont();
		FontData[] smaller = resizeFont(f, -2);
		JFaceResources.getFontRegistry().put(SMALL_FONT, smaller);
		return JFaceResources.getFontRegistry().get(SMALL_FONT);
	}

	/**
	 * Gets the default large font from the JFace font registry
	 * 
	 * @return - default large font
	 */
	public static Font getDefaultLargeFont()
	{
		Font small = JFaceResources.getFontRegistry().get(LARGE_FONT);
		if (small != null)
		{
			return small;
		}

		Font f = JFaceResources.getDefaultFont();
		FontData[] smaller = resizeFont(f, -2);
		JFaceResources.getFontRegistry().put(LARGE_FONT, smaller);
		return JFaceResources.getFontRegistry().get(LARGE_FONT);
	}

	/**
	 * Returns a version of the specified font, resized by the requested size
	 * 
	 * @param font
	 *            The font to resize
	 * @param size
	 *            The font size
	 * @return - resized font data
	 */
	public static FontData[] resizeFont(Font font, int size)
	{
		FontData[] datas = font.getFontData();
		if (datas.length > 0)
		{
			for (int i = 0; i < datas.length; i++)
			{
				FontData data = datas[0];
				data.setHeight(data.getHeight() + size);
			}
		}

		return datas;
	}

	/**
	 * Bolds a font
	 * 
	 * @param font
	 * @return - bolded font data
	 */
	public static FontData[] boldFont(Font font)
	{
		FontData[] datas = font.getFontData();
		if (datas.length > 0)
		{
			for (int i = 0; i < datas.length; i++)
			{
				FontData data = datas[i];
				data.setStyle(data.getStyle() | SWT.BOLD);
			}
		}
		return datas;
	}

	public static FontData[] italicFont(Font font)
	{
		FontData[] datas = font.getFontData();
		if (datas.length > 0)
		{
			for (int i = 0; i < datas.length; i++)
			{
				FontData data = datas[i];
				data.setStyle(data.getStyle() | SWT.ITALIC);
			}
		}
		return datas;
	}

	/**
	 * Finds and caches the iamge from the image descriptor for this particular plugin
	 * 
	 * @param plugin
	 *            The plugin to search
	 * @param path
	 *            The path to the image
	 * @return The image, or null if not found
	 */
	public static Image getImage(AbstractUIPlugin plugin, String path)
	{
		return getImage(plugin.getBundle(), path);
	}

	/**
	 * Finds and caches the image from the image descriptor for this particular bundle
	 * 
	 * @param bundle
	 *            The bundle to search
	 * @param path
	 *            The path to the image
	 * @return The image, or null if not found
	 */
	public static Image getImage(Bundle bundle, String path)
	{
		if (path.charAt(0) != '/')
		{
			path = "/" + path; //$NON-NLS-1$
		}

		String computedName = bundle.getSymbolicName() + path;
		Image image = JFaceResources.getImage(computedName);
		if (image != null)
		{
			return image;
		}

		ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(bundle.getSymbolicName(), path);
		if (id != null)
		{
			JFaceResources.getImageRegistry().put(computedName, id);
			return JFaceResources.getImage(computedName);
		}
		else
		{
			return null;
		}
	}

}
