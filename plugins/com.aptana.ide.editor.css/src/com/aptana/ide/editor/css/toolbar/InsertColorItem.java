package com.aptana.ide.editor.css.toolbar;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editors.toolbar.IToolBarMember;
import com.aptana.ide.editors.unified.IUnifiedEditor;

public class InsertColorItem implements IToolBarMember
{
	private static ColorDialog colorDialog;

	public void execute(IUnifiedEditor editor, String string)
	{
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		ITextSelection sel = (ITextSelection) editor.getViewer().getSelectionProvider().getSelection();
		if (sel.isEmpty())
			return;
		try
		{
			int offset = sel.getOffset();
			int len = sel.getLength();
			String original = doc.get(offset, len);
			String replacement = getUserColor(original);
			if (replacement == null)
				return;
			if (replacement.equals(original))
				return;
			doc.replace(offset, len, replacement);
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e.getMessage());
		}
	}

	private String getUserColor(String highlighted)
	{
		RGB startrgb = guessColor(highlighted);
		if (startrgb != null)
		{
			getColorDialog().setRGB(startrgb);
		}
		getColorDialog().open();
		RGB selectedcolor = getColorDialog().getRGB();
		if (selectedcolor == null)
		{
			return highlighted;
		}
		String color = toHex(selectedcolor);
		if (highlighted.startsWith("#") || highlighted.trim().length() == 0) //$NON-NLS-1$
			color = "#" + color; //$NON-NLS-1$
		if (highlighted.endsWith(";") || highlighted.trim().length() == 0) //$NON-NLS-1$
			color = color + ";"; //$NON-NLS-1$
		return color;
	}

	private String toHex(RGB selectedcolor)
	{
		return twoDigitHex(selectedcolor.red) + twoDigitHex(selectedcolor.green) + twoDigitHex(selectedcolor.blue);
	}

	private String twoDigitHex(int color)
	{
		return pad(Integer.toHexString(color));
	}

	private ColorDialog getColorDialog()
	{
		if (colorDialog == null)
		{
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			colorDialog = new ColorDialog(shell);
		}
		return colorDialog;
	}

	private RGB guessColor(String input)
	{
		if (input == null)
			return null;
		input = input.trim();
		if (input.startsWith("#")) //$NON-NLS-1$
		{
			input = input.substring(1);
		}
		if (input.endsWith(";")) //$NON-NLS-1$
		{
			input = input.substring(0, input.length() - 1);
		}
		if (input.length() == 0)
			return null;
		try
		{
			String red = "00"; //$NON-NLS-1$
			String green = "00"; //$NON-NLS-1$
			String blue = "00"; //$NON-NLS-1$
			if (input.length() == 3)
			{
				red = input.substring(0, 1);
				red = red + red;
				green = input.substring(1, 2);
				green = green + green;
				blue = input.substring(2, 3);
				blue = blue + blue;
			}
			else if (input.length() >= 6)
			{
				red = input.substring(0, 2);
				green = input.substring(2, 4);
				blue = input.substring(4, 6);
			}
			return new RGB(Integer.parseInt(red, 16), Integer.parseInt(green, 16), Integer.parseInt(blue, 16));
		}
		catch (Exception e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e.getMessage());
		}
		return null;
	}

	private String pad(String str)
	{
		if (str.length() == 0)
			return "00"; //$NON-NLS-1$
		if (str.length() == 1)
			return "0" + str; //$NON-NLS-1$
		else
			return str;
	}
}
