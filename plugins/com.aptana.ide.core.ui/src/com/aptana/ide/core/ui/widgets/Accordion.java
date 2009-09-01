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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.core.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * This is a generic accordion widget
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Accordion
{

	/**
	 * DEFAULT_SPEED
	 */
	public static final long DEFAULT_SPEED = 10L;

	/**
	 * DEFAULT_DRAWER_HEIGHT
	 */
	public static final int DEFAULT_DRAWER_HEIGHT = 25;

	/**
	 * DEFAULT_STEP_SIZE
	 */
	public static final int DEFAULT_STEP_SIZE = 20;

	/**
	 * DRAWER_BACKGROUND
	 */
	public static final Color DRAWER_BACKGROUND = new Color(Display.getDefault(), 200, 200, 200);

	/**
	 * DRAWER_TEXT
	 */
	public static final Color DRAWER_TEXT = new Color(Display.getDefault(), 50, 50, 50);

	/**
	 * DRAWER_EXPANDED
	 */
	public static final Image DRAWER_EXPANDED = CoreUIPlugin.getImageDescriptor("icons/minimize.png").createImage(); //$NON-NLS-1$

	/**
	 * DRAWER_COLLAPSED
	 */
	public static final Image DRAWER_COLLAPSED = CoreUIPlugin.getImageDescriptor("icons/maximize.png").createImage(); //$NON-NLS-1$

	/**
	 * DRAWER_DRAGGER
	 */
	public static final Image DRAWER_DRAGGER = CoreUIPlugin.getImageDescriptor("icons/dragger.png").createImage(); //$NON-NLS-1$

	/**
	 * VERTICAL
	 */
	public static final int VERTICAL = SWT.VERTICAL;

	/**
	 * HORIZONTAL
	 */
	public static final int HORIZONTAL = SWT.HORIZONTAL;

	private int drawerHeight;
	private long speed;
	private int orientation;
	private List<Composite> drawers;
	private Cursor handCursor;
	private Cursor sizerCursor;
	private Font handleFont;
	private UIJob accordionJob;
	private Composite accordion;
	private Composite openDrawer;
	private Color drawerBackground;
	private Color drawerText;
	private boolean resizable;
	private int initialCoord;
	private int stepSize;
	private boolean hideWhileSliding;
	private Color alternateDrawFg;
	private Color alternateDrawBg;
	private Image drawerExpanded;
	private Image drawerCollapsed;

	private MouseAdapter clickAdapter = new MouseAdapter()
	{

		public void mouseUp(MouseEvent e)
		{
			triggerSliding(((Composite) e.widget).getParent());
			for (int i = 0; i < drawers.size(); i++)
			{
				Composite curr = drawers.get(i);
				curr = getHandleArea(curr);
				curr.redraw();
				curr.update();
			}
		}

	};

	/**
	 * Creates a new accordion
	 * 
	 * @param speed
	 * @param drawerHeight
	 * @param drawerBackground
	 * @param drawerText
	 * @param orientation
	 * @param stepSize
	 * @param resizable
	 */
	public Accordion(long speed, int drawerHeight, Color drawerBackground, Color drawerText, int orientation,
			int stepSize, boolean resizable)
	{
		this.speed = speed;
		this.drawerHeight = drawerHeight;
		this.drawerBackground = drawerBackground;
		this.drawerText = drawerText;
		this.orientation = orientation;
		this.stepSize = stepSize;
		this.resizable = resizable;
		this.drawers = new ArrayList<Composite>();
		this.initialCoord = -1;
		this.hideWhileSliding = false;
	}

	/**
	 * Creates a new accordion
	 */
	public Accordion()
	{
		this(DEFAULT_SPEED, DEFAULT_DRAWER_HEIGHT, DRAWER_BACKGROUND, DRAWER_TEXT, HORIZONTAL, DEFAULT_STEP_SIZE, false);
	}

	/**
	 * Creates a new accordion
	 * 
	 * @param speed
	 */
	public Accordion(long speed)
	{
		this(speed, DEFAULT_DRAWER_HEIGHT, DRAWER_BACKGROUND, DRAWER_TEXT, HORIZONTAL, DEFAULT_STEP_SIZE, false);
	}

	/**
	 * Creates a new accordion
	 * 
	 * @param drawerHeight
	 */
	public Accordion(int drawerHeight)
	{
		this(DEFAULT_SPEED, drawerHeight, DRAWER_BACKGROUND, DRAWER_TEXT, HORIZONTAL, DEFAULT_STEP_SIZE, false);
	}

	/**
	 * Creates a new accordion
	 * 
	 * @param drawerHeight
	 * @param orientation
	 */
	public Accordion(int drawerHeight, int orientation)
	{
		this(DEFAULT_SPEED, drawerHeight, DRAWER_BACKGROUND, DRAWER_TEXT, orientation, DEFAULT_STEP_SIZE, false);
	}

	/**
	 * Creates a new accordion
	 * 
	 * @param drawerHeight
	 * @param orientation
	 * @param stepSize
	 * @param resizable
	 */
	public Accordion(int drawerHeight, int orientation, int stepSize, boolean resizable)
	{
		this(DEFAULT_SPEED, drawerHeight, DRAWER_BACKGROUND, DRAWER_TEXT, orientation, stepSize, resizable);
	}

	/**
	 * Disposes the accordion
	 */
	public void dispose()
	{
		if (handCursor != null)
		{
			handCursor.dispose();
		}
		if (handleFont != null)
		{
			handleFont.dispose();
		}
		if (alternateDrawFg != null)
		{
			alternateDrawFg.dispose();
		}
	}

	/**
	 * @return the drawerHeight
	 */
	public int getDrawerHeight()
	{
		return drawerHeight;
	}

	/**
	 * @param drawerHeight
	 *            the drawerHeight to set
	 */
	public void setDrawerHeight(int drawerHeight)
	{
		this.drawerHeight = drawerHeight;
	}

	/**
	 * @return the speed
	 */
	public long getSpeed()
	{
		return speed;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(long speed)
	{
		this.speed = speed;
	}

	/**
	 * Adds a drawer to the accordion
	 * 
	 * @param label
	 * @return drawer control
	 */
	public Composite addDrawer(final String label)
	{
		return addDrawer(label, null, -1);
	}

	/**
	 * Adds a drawer to the accordion
	 * 
	 * @param label
	 * @param handleImage
	 * @param imageHeight
	 * @return drawer control
	 */
	public Composite addDrawer(final String label, Image handleImage, int imageHeight)
	{
		return this.addDrawer(label, this.drawerBackground, handleImage, imageHeight);

	}

	/**
	 * Adds a drawer to the accordion
	 * 
	 * @param label
	 * @param drawerColor
	 * @param handleImage
	 * @param imageHeight
	 * @return drawer control
	 */
	public Composite addDrawer(final String label, Color drawerColor, final Image handleImage, final int imageHeight)
	{
		final Composite drawer = new Composite(accordion, SWT.NONE);
		drawer.setBackground(drawerColor);
		this.drawers.add(drawer);
		if (this.orientation == VERTICAL)
		{
			GridLayout aLayout = (GridLayout) accordion.getLayout();
			aLayout.numColumns = drawers.size();
			aLayout.makeColumnsEqualWidth = false;
		}
		GridLayout dLayout = new GridLayout(1, true);
		if (this.orientation == VERTICAL)
		{
			dLayout.numColumns = 2;
			dLayout.makeColumnsEqualWidth = false;
		}
		dLayout.marginHeight = 0;
		dLayout.marginWidth = 0;
		dLayout.verticalSpacing = 0;
		dLayout.horizontalSpacing = 0;
		drawer.setLayout(dLayout);
		final GridData dData = new GridData(SWT.FILL, SWT.FILL, true, true);
		if (this.orientation == VERTICAL)
		{
			dData.grabExcessHorizontalSpace = false;
			dData.grabExcessVerticalSpace = true;
		}
		else
		{
			dData.grabExcessHorizontalSpace = true;
			dData.grabExcessVerticalSpace = false;
		}
		drawer.setLayoutData(dData);

		final Composite drawerHandler = new Composite(drawer, SWT.NONE);

		GridLayout dhLayout = new GridLayout(1, true);
		dhLayout.marginHeight = 0;
		dhLayout.marginWidth = 0;
		GridData dhData = new GridData(SWT.FILL, SWT.FILL, true, true);
		if (this.orientation == VERTICAL)
		{
			dhData.grabExcessHorizontalSpace = false;
			dhData.grabExcessVerticalSpace = true;
			dhData.widthHint = this.drawerHeight;
		}
		else
		{
			dhData.heightHint = this.drawerHeight;
			dhData.minimumHeight = this.drawerHeight;
			dhData.grabExcessHorizontalSpace = true;
			dhData.grabExcessVerticalSpace = false;
		}
		if (resizable)
		{
			Composite drawerDragger = new Composite(drawerHandler, SWT.NONE);
			GridData ddData = new GridData(SWT.END, SWT.CENTER, true, true);
			ddData.widthHint = 6;
			ddData.heightHint = 20;
			drawerDragger.setLayoutData(ddData);
			drawerDragger.setBackground(drawerText);
			drawerDragger.setCursor(sizerCursor);
			drawerDragger.setBackgroundImage(DRAWER_DRAGGER);
			drawerDragger.addMouseListener(new MouseAdapter()
			{

				public void mouseDown(MouseEvent e)
				{
					initialCoord = e.x;
				}

				public void mouseUp(MouseEvent e)
				{
					initialCoord = -1;
				}

			});
			drawerDragger.addMouseMoveListener(new MouseMoveListener()
			{

				public void mouseMove(MouseEvent e)
				{
					if (initialCoord != -1)
					{
						int diff = e.x - initialCoord;
						Composite previous = getPreviousDrawer(drawer);
						if (previous != null && openDrawer != null)
						{
							Composite open = openDrawer;
							if (openDrawer == previous)
							{
								open = drawer;
							}
							GridData oData = (GridData) open.getLayoutData();
							GridData pData = (GridData) previous.getLayoutData();
							int size = orientation == VERTICAL ? accordion.getSize().x : accordion.getSize().y;
							int max = size - drawerHeight * (drawers.size() - 1);
							if (diff > 0)
							{
								if (orientation == VERTICAL)
								{
									if (previous.getSize().x + diff < max && open.getSize().x - diff > drawerHeight)
									{
										pData.widthHint += diff;
										oData.widthHint -= diff;
									}
									else
									{
										pData.widthHint = max;
										oData.widthHint = drawerHeight;
									}
								}
								// TODO add horizontal case
							}
							else if (diff < 0)
							{
								if (orientation == VERTICAL)
								{
									if (previous.getSize().x + diff > drawerHeight && open.getSize().x - diff < max)
									{
										pData.widthHint += diff;
										oData.widthHint -= diff;
									}
									else
									{
										pData.widthHint = drawerHeight;
										oData.widthHint = max;
									}
								}
								// TODO add horizontal case
							}
							if (diff != 0)
							{
								Composite area = getDrawerArea(previous);
								if (area != null && !area.isVisible())
								{
									area.setVisible(true);
								}
								area = getDrawerArea(open);
								if (area != null && !area.isVisible())
								{
									area.setVisible(true);
								}
								for (int i = 0; i < drawers.size(); i++)
								{
									Composite drawer = drawers.get(i);
									Composite dArea = getDrawerArea(drawer);
									Rectangle bounds = dArea.getBounds();
									dArea.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
									dArea.update();
									Composite handle = getHandleArea(drawer);
									handle.redraw();
									handle.layout();
								}
								accordion.layout(true);
							}
						}
						else
						{
							initialCoord = -1;
						}
					}
					else
					{
						initialCoord = -1;
					}
				}

			});
		}
		drawerHandler.setCursor(handCursor);
		drawerHandler.setLayout(dhLayout);
		drawerHandler.setLayoutData(dhData);
		drawerHandler.addMouseListener(clickAdapter);

		final Composite placeHolder = new Composite(drawer, SWT.NONE);
		GridData phData = new GridData(SWT.FILL, SWT.FILL, true, true);
		placeHolder.setLayoutData(phData);
		addHandlePainting(drawer, drawerHandler, label, handleImage, imageHeight);

		return drawer;
	}

	private void addHandlePainting(final Composite drawer, final Composite handle, final String label,
			final Image handleImage, final int imageHeight)
	{
		handle.setBackground(handle.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		Color bg = handle.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color fg = handle.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		if (bg.getBlue() == fg.getBlue() && bg.getRed() == fg.getRed() && bg.getGreen() == fg.getGreen())
		{
			if (alternateDrawFg == null)
			{
				int newBlue = fg.getBlue() - 20 > 0 ? fg.getBlue() - 20 : fg.getBlue();
				int newRed = fg.getRed() - 20 > 0 ? fg.getRed() - 20 : fg.getRed();
				int newGreen = fg.getGreen() - 20 > 0 ? fg.getGreen() - 20 : fg.getGreen();
				alternateDrawFg = new Color(handle.getDisplay(), new RGB(newBlue, newRed, newGreen));
			}
		}
		final Image collapsedDrawer = this.drawerCollapsed != null ? drawerCollapsed : DRAWER_COLLAPSED;
		final Image expandedDrawer = this.drawerExpanded != null ? drawerExpanded : DRAWER_EXPANDED;
		handle.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				Point p = handle.getSize();
				if (p.x == 0 || p.y == 0)
				{
					return;
				}
				if (alternateDrawBg != null)
				{
					e.gc.setBackground(alternateDrawBg);
				}
				else
				{
					e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
				if (alternateDrawFg != null)
				{
					e.gc.setForeground(alternateDrawFg);
				}
				else
				{
					e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				}
				e.gc.fillRectangle(0, 0, p.x, p.y);
				e.gc.drawRectangle(0, 0, p.x - 1, p.y - 1);

				// Previous gradient drawer style, possible make option
				// e.gc.fillGradientRectangle(0, 0, e.width, e.height, orientation == HORIZONTAL);

				e.gc.setForeground(drawerText);
				if (orientation == VERTICAL && p.y - 30 > 0)
				{
					Image out = null;
					if (handleImage == null)
					{
						Image image = new Image(e.display, p.y - 30, p.x);
						GC iGc = new GC(image);
						iGc.setForeground(drawerText);
						iGc.setFont(handleFont);
						iGc.drawText(label, 0, 0, true);
						ImageData horizData = image.getImageData();
						ImageData vertData = new ImageData(horizData.height, horizData.width, horizData.depth,
								horizData.palette);
						int white = horizData.palette.getPixel(new RGB(255, 255, 255));
						RGB currRGB = null;
						for (int i = 0; i < horizData.width; i++)
						{
							for (int j = 0; j < horizData.height; j++)
							{
								int curr = horizData.getPixel(i, j);
								currRGB = horizData.palette.getRGB(curr);
								// This check is an attempt correct slightly off white pixels that we want to be
								// transparent
								// instead to remove artifacts
								if ((currRGB.red == 255 && currRGB.green == 255)
										|| (currRGB.green == 255 && currRGB.blue == 255))
								{
									vertData.setPixel(j, horizData.width - i - 1, white);
								}
								else
								{
									vertData.setPixel(j, horizData.width - i - 1, curr);
								}
							}
						}
						iGc.dispose();
						image.dispose();
						vertData.transparentPixel = white;
						out = new Image(e.display, vertData);
					}
					else
					{
						out = handleImage;
					}
					int height = imageHeight;
					if (height == -1)
					{
						height = out.getImageData().height;
					}
					int start = p.y - 25 - height >= 0 ? p.y - 25 - height : 0;
					e.gc.drawImage(out, 0, start);
					if (handleImage == null)
					{
						out.dispose();
					}
				}
				else
				{
					e.gc.drawString(label, 25, 5, true);
				}
				boolean expanded = drawer == openDrawer;
				if (expanded)
				{
					if (orientation == VERTICAL)
					{
						if (p.x - 25 > -1 && p.y - 25 > -1)
						{
							e.gc.drawImage(collapsedDrawer, p.x - 25, p.y - 25);
						}
					}
					else
					{
						e.gc.drawImage(expandedDrawer, 5, 5);
					}
				}
				else
				{
					if (orientation == VERTICAL)
					{
						if (p.x - 25 > -1 && p.y - 25 > -1)
						{
							e.gc.drawImage(expandedDrawer, p.x - 25, p.y - 25);
						}
					}
					else
					{
						e.gc.drawImage(collapsedDrawer, 5, 5);
					}
				}
			}
		});
	}

	private Composite getPreviousDrawer(Composite drawer)
	{
		Composite previous = null;
		int index = drawers.indexOf(drawer);
		if (index > 0)
		{
			previous = drawers.get(index - 1);
		}
		return previous;
	}

	private Composite getNextDrawer(Composite drawer)
	{
		Composite next = null;
		int index = drawers.indexOf(drawer);
		if (index + 1 < drawers.size())
		{
			next = drawers.get(index + 1);
		}
		return next;
	}

	/**
	 * Sets the initial drawer to open
	 * 
	 * @param drawer
	 */
	public void setInitialDrawerOpen(Composite drawer)
	{
		GridData dData = (GridData) drawer.getLayoutData();
		if (this.orientation == VERTICAL)
		{
			dData.grabExcessHorizontalSpace = true;
		}
		else
		{
			dData.grabExcessVerticalSpace = true;
		}
		GridData data = (GridData) getDrawerArea(drawer).getLayoutData();
		data.exclude = false;
		data.grabExcessVerticalSpace = true;
		this.openDrawer = drawer;
		for (int i = 0; i < drawers.size(); i++)
		{
			Composite curr = drawers.get(i);
			if (!curr.equals(drawer))
			{
				dData = (GridData) curr.getLayoutData();
				if (this.orientation == VERTICAL)
				{
					dData.widthHint = this.drawerHeight;
					dData.grabExcessHorizontalSpace = false;
				}
				else
				{
					dData.heightHint = this.drawerHeight;
					dData.minimumHeight = this.drawerHeight;
					dData.grabExcessVerticalSpace = false;
				}
				if (hideWhileSliding)
				{
					Composite area = getDrawerArea(curr);
					area.setVisible(false);
				}
			}
		}
		drawer.layout(true, true);
		accordion.layout(true, true);
	}

	/**
	 * Set handle background color
	 * 
	 * @param color
	 */
	public void setHandleBackground(Color color)
	{
		this.alternateDrawBg = color;
	}

	/**
	 * Set handle trim color
	 * 
	 * @param color
	 */
	public void setHandleTrim(Color color)
	{
		this.alternateDrawFg = color;
	}

	/**
	 * Gets the drawer content area for a drawer. Don't set the layout data on the composite returned.
	 * 
	 * @param drawer
	 * @return - drawer content composition
	 */
	public Composite getDrawerArea(Composite drawer)
	{
		if (this.drawers.contains(drawer))
		{
			if (drawer != null && drawer.getChildren() != null && drawer.getChildren().length == 2)
			{
				return (Composite) drawer.getChildren()[1];
			}
		}
		return null;
	}

	private Composite getHandleArea(Composite drawer)
	{
		if (this.drawers.contains(drawer))
		{
			if (drawer != null && drawer.getChildren() != null && drawer.getChildren().length == 2)
			{
				return (Composite) drawer.getChildren()[0];
			}
		}
		return null;
	}

	/**
	 * Creates the accordion control
	 * 
	 * @param parent
	 * @return accordion control
	 */
	public Control createAccordion(Composite parent)
	{
		handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		sizerCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_SIZEWE);
		handleFont = new Font(parent.getDisplay(), "Arial", 14, SWT.NONE); //$NON-NLS-1$
		accordion = new Composite(parent, SWT.NONE);
		GridLayout sLayout = new GridLayout(1, true);
		sLayout.marginHeight = 0;
		sLayout.marginWidth = 0;
		sLayout.verticalSpacing = 0;
		sLayout.horizontalSpacing = 0;
		accordion.setLayout(sLayout);
		accordion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		accordion.setBackground(accordion.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		accordion.addControlListener(new ControlListener()
		{

			public void controlResized(ControlEvent e)
			{
				int size = orientation == VERTICAL ? accordion.getSize().x : accordion.getSize().y;
				int max = size - drawerHeight * (drawers.size() - 1);
				if (openDrawer != null)
				{
					GridData data = (GridData) openDrawer.getLayoutData();
					if (orientation == VERTICAL)
					{
						data.widthHint = max;
					}
					else
					{
						data.heightHint = max;
					}
					accordion.layout(true);
					for (int i = 0; i < drawers.size(); i++)
					{
						Composite handle = getHandleArea(drawers.get(i));
						handle.redraw();
						handle.update();
					}
				}

			}

			public void controlMoved(ControlEvent e)
			{
				// TODO Auto-generated method stub

			}

		});
		return accordion;
	}

	/**
	 * Triggers sliding
	 * 
	 * @param drawerToOpen
	 */
	public void triggerSliding(Composite drawerToOpen)
	{
		if (drawerToOpen == this.openDrawer)
		{
			Composite other = getNextDrawer(drawerToOpen);
			if (other == null)
			{
				other = getPreviousDrawer(drawerToOpen);
			}
			if (other != null)
			{
				drawerToOpen = other;
			}
			else
			{
				return;
			}
		}
		final Composite openingDrawer = drawerToOpen;
		for (int i = 0; i < drawers.size(); i++)
		{
			Composite curr = drawers.get(i);
			if (curr != drawerToOpen)
			{

				GridData data = (GridData) getDrawerArea(curr).getLayoutData();
				if (curr != this.openDrawer)
				{
					data = (GridData) curr.getLayoutData();
					if (this.orientation == VERTICAL)
					{
						data.widthHint = this.drawerHeight;
					}
					else
					{
						data.heightHint = this.drawerHeight;
						data.minimumHeight = this.drawerHeight;
					}
				}
			}
		}
		final Composite previousDrawer = this.openDrawer;
		this.openDrawer = drawerToOpen;
		final Composite drawerSection = getDrawerArea(drawerToOpen);
		GridData newData = (GridData) drawerSection.getLayoutData();
		newData.exclude = false;
		if (this.orientation == VERTICAL)
		{
			newData.grabExcessHorizontalSpace = true;
		}
		else
		{
			newData.grabExcessVerticalSpace = true;
		}
		int size = this.orientation == VERTICAL ? accordion.getSize().x : accordion.getSize().y;
		final int max = size - this.drawerHeight * (drawers.size() - 1);
		if (accordionJob != null)
		{
			accordionJob.cancel();
		}
		accordionJob = new UIJob("Animating Accordion") //$NON-NLS-1$
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (accordion.isDisposed() || (monitor != null && monitor.isCanceled()))
				{
					return Status.CANCEL_STATUS;
				}
				Point currSize = openingDrawer.getSize();
				int size = orientation == VERTICAL ? currSize.x : currSize.y;
				GridData data = (GridData) openingDrawer.getLayoutData();
				if (max > size)
				{
					if (size + stepSize < max)
					{
						if (orientation == VERTICAL)
						{
							data.widthHint = size + stepSize;
						}
						else
						{
							data.heightHint = size + stepSize;
						}
					}
					else
					{
						if (orientation == VERTICAL)
						{
							data.widthHint = max;
						}
						else
						{
							data.heightHint = max;
						}
						Composite area = getDrawerArea(openingDrawer);
						if (hideWhileSliding && area != null && !area.isVisible())
						{
							area.setVisible(true);
						}
					}
					if (previousDrawer != null)
					{
						data = (GridData) previousDrawer.getLayoutData();
						currSize = previousDrawer.getSize();
						size = orientation == VERTICAL ? currSize.x : currSize.y;
						if (size - stepSize > drawerHeight)
						{
							if (orientation == VERTICAL)
							{
								data.widthHint = size - stepSize;
							}
							else
							{
								data.heightHint = size - stepSize;
							}
						}
						else
						{
							if (orientation == VERTICAL)
							{
								data.widthHint = drawerHeight;
							}
							else
							{
								data.heightHint = drawerHeight;
								data.minimumHeight = drawerHeight;
							}
						}
						Composite area = getDrawerArea(previousDrawer);
						if (hideWhileSliding && area != null && area.isVisible())
						{
							area.setVisible(false);
						}
					}
					data = (GridData) openingDrawer.getLayoutData();
					accordion.layout(true);
					size = orientation == VERTICAL ? data.widthHint : data.heightHint;
					if (size == max)
					{
						for (int i = 0; i < drawers.size(); i++)
						{
							Composite handle = getHandleArea(drawers.get(i));
							handle.redraw();
							handle.update();
						}
					}
					this.schedule(speed);
				}
				return Status.OK_STATUS;
			}

		};
		accordionJob.setSystem(true);
		accordionJob.setPriority(UIJob.INTERACTIVE);
		accordionJob.schedule();
	}

	/**
	 * @return the drawerBackground
	 */
	public Color getDrawerBackground()
	{
		return drawerBackground;
	}

	/**
	 * @param drawerBackground
	 *            the drawerBackground to set
	 */
	public void setDrawerBackground(Color drawerBackground)
	{
		this.drawerBackground = drawerBackground;
	}

	/**
	 * @return the drawerText
	 */
	public Color getDrawerText()
	{
		return drawerText;
	}

	/**
	 * @param drawerText
	 *            the drawerText to set
	 */
	public void setDrawerText(Color drawerText)
	{
		this.drawerText = drawerText;
	}

	/**
	 * @return the hideWhileSliding
	 */
	public boolean isHideWhileSliding()
	{
		return hideWhileSliding;
	}

	/**
	 * @param hideWhileSliding
	 *            the hideWhileSliding to set
	 */
	public void setHideWhileSliding(boolean hideWhileSliding)
	{
		this.hideWhileSliding = hideWhileSliding;
	}

	/**
	 * @param drawerCollapsed
	 *            the drawerCollapsed to set
	 */
	public void setDrawerCollapsed(Image drawerCollapsed)
	{
		this.drawerCollapsed = drawerCollapsed;
	}

	/**
	 * @param drawerExpanded
	 *            the drawerExpanded to set
	 */
	public void setDrawerExpanded(Image drawerExpanded)
	{
		this.drawerExpanded = drawerExpanded;
	}

}
