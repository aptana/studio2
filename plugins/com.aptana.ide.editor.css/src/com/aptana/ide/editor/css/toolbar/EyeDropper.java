package com.aptana.ide.editor.css.toolbar;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editors.toolbar.IToolBarMember;
import com.aptana.ide.editors.unified.IUnifiedEditor;

public class EyeDropper implements IToolBarMember
{

	protected int lastX;
	protected int lastY;

	public void execute(IUnifiedEditor editor, String string)
	{
		ITextSelection sel = (ITextSelection) editor.getViewer().getSelectionProvider().getSelection();
		if (sel.isEmpty())
			return;
		try
		{
			String replacement = getUserColor();
			if (replacement == null)
				return;
			int offset = sel.getOffset();
			int len = sel.getLength();
			IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			doc.replace(offset, len, replacement);
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e.getMessage());
		}
	}

	private String getUserColor()
	{
		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display, SWT.SYSTEM_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
		shell.setFullScreen(true);
		final Rectangle rect = display.getPrimaryMonitor().getClientArea();
		shell.setBounds(rect);
		shell.setLocation(rect.x - 5, rect.y - 5);
		shell.addTraverseListener(new TraverseListener()
		{

			public void keyTraversed(TraverseEvent event)
			{
				switch (event.detail)
				{
					case SWT.TRAVERSE_ESCAPE:
						shell.close();
						event.detail = SWT.TRAVERSE_NONE;
						event.doit = false;
						break;
				}
			}
		});
		shell.setLayout(new GridLayout(1, true));
		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.setLayoutData(new GridData(rect.width, rect.height));
		final Image image = takeScreenshot(display, rect);
		canvas.setBackgroundImage(image);
		Image cursorImage = CSSPlugin.getImage("icons/eyedropper.gif"); //$NON-NLS-1$
		ImageData cursordata = cursorImage.getImageData();
		Cursor cursor = new Cursor(display, cursordata, 0, 15);
		canvas.setCursor(cursor);
		canvas.addMouseMoveListener(new MouseMoveListener()
		{

			public void mouseMove(MouseEvent e)
			{
				final int width = 50;
				final int height = 50;
				final int xOffset = 10;
				final int yOffset = -10 - height;
				// Tell canvas to redraw/repair the last place we drew
				canvas.redraw(lastX + (xOffset - 1), lastY + (yOffset - 1), width + 2, height + 2, false);
				canvas.update();
				lastX = e.x;
				lastY = e.y;
				// As user moves mouse, draw a big box of the color they're hovering over up and to the right
				RGB rgb = getColor(image, e);
				Color bg = new Color(display, rgb);
				// Set foreground to white/black depending on what hovered color is, so text is readable!
				Color fg;
				int total = rgb.blue + rgb.green + rgb.red;
				if (total < 384)
				{
					fg = new Color(display, 255, 255, 255);
				}
				else
				{
					fg = new Color(display, 0, 0, 0);
				}
				GC gc = new GC(canvas);
				gc.setBackground(bg);
				gc.setForeground(fg);
				// FIXME The drawing seems slow and "jerky" can we use something like double-buffering?
				gc.drawRectangle(e.x + (xOffset - 1), e.y + (yOffset - 1), width + 1, height + 1);
				gc.fillRectangle(e.x + xOffset, e.y + yOffset, width, width);
				gc.drawString(toHex(rgb), e.x + xOffset, e.y + ((yOffset / 2) - 12));
				bg.dispose();
				fg.dispose();
				gc.dispose();
				// FIXME First time user uses eyedropper, it only draws this box so many times before it just stops
				// doing so.
			}
		});
		final String result[] = new String[1];
		canvas.addMouseListener(new MouseListener()
		{

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				RGB rgb = getColor(image, e);
				result[0] = toHex(rgb);
				image.dispose();
				shell.close();
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		});
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		cursor.dispose();
		if (result[0] != null)
			return "#" + result[0]; //$NON-NLS-1$
		return null;
	}

	private Image takeScreenshot(final Display display, final Rectangle rect)
	{
		final Image snap = new Image(display, rect);
		GC gc = new GC(display);
		gc.copyArea(snap, 0, 0);
		gc.dispose();
		return snap;
	}

	private String toHex(RGB selectedcolor)
	{
		return twoDigitHex(selectedcolor.red) + twoDigitHex(selectedcolor.green) + twoDigitHex(selectedcolor.blue);
	}

	private String twoDigitHex(int color)
	{
		return pad(Integer.toHexString(color));
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

	protected RGB getColor(final Image image, MouseEvent e)
	{
		return getColor(image, e.x, e.y);
	}

	protected RGB getColor(final Image image, int x, int y)
	{
		int pixel = image.getImageData().getPixel(x, y);
		return image.getImageData().palette.getRGB(pixel);
	}
}
