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
package com.aptana.ide.intro.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;

import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IModifiableObject;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.WebPerspectiveFactory;
import com.aptana.ide.core.ui.dialogs.AptanaSignInDialog;
import com.aptana.ide.intro.IntroPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class StartPageTrimWidget extends AbstractWorkbenchTrimWidget
{

	private static final String HOME_ICON = "icons/aptana_home.png"; //$NON-NLS-1$
	private static final String UP_ARROW = "icons/up_arrow.png"; //$NON-NLS-1$
	private static final String DOWN_ARROW = "icons/down_arrow.png"; //$NON-NLS-1$

	private Composite displayArea;
	private Link accountNameLabel;
	private Cursor hand;

	private User user;

	/**
	 * Account trim widget constructor
	 */
	public StartPageTrimWidget()
	{
		user = AptanaUser.getSignedInUser();
		user.addListener(new IModelListener()
		{

			public void modelChanged(IModifiableObject object)
			{
                CoreUIUtils.getDisplay().asyncExec(new Runnable()
                {

                    public void run()
                    {
                        updateLabel();
                    }

                });
			}

		});
		IntroPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(
				new PerspectiveAdapter()
				{

					public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
					{
						if (displayArea != null && !displayArea.isDisposed())
						{
							displayArea.setVisible(WebPerspectiveFactory.isValidAptanaPerspective(perspective));
						}
					}

				});
	}

	private void updateLabel()
	{
		if (accountNameLabel == null || accountNameLabel.isDisposed())
		{
			return;
		}
		if (user.getUsername() != null && user.getUsername().length() > 0)
		{
			accountNameLabel.setText("<a>" + user.getUsername() + "</a>"); //$NON-NLS-1$//$NON-NLS-2$
			accountNameLabel.setToolTipText(user.getUsername() + " @ " + Messages.StartPageTrimWidget_MyAptana); //$NON-NLS-1$
		}
		else
		{
			accountNameLabel.setText("<a>" + Messages.StartPageTrimWidget_SignIn + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
			accountNameLabel.setToolTipText(Messages.StartPageTrimWidget_MyAptana);
		}
		adjustLink();
	}

	/**
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#dispose()
	 */
	public void dispose()
	{
		if (displayArea != null && !displayArea.isDisposed())
		{
			displayArea.dispose();
		}
	}

	private void loadMenu(Menu menu)
	{
	    ActionUtils.buildMenu(menu, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	}

	private void signIn()
	{
	    AptanaSignInDialog dialog = new AptanaSignInDialog(CoreUIUtils.getActiveShell());
        dialog.open();
	}

	private void adjustLink()
	{
		GridData anlData = (GridData) accountNameLabel.getLayoutData();
		int size = accountNameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 5;
		if (size > 120)
		{
			size =120;
			GC gc = new GC(accountNameLabel);
			int width = gc.getFontMetrics().getAverageCharWidth();
			int dotsSize = gc.stringExtent("...").x; //$NON-NLS-1$
			int remaining = size - dotsSize;
			int charCount = remaining / width;
			String label = accountNameLabel.getText();
			if (label.length() > charCount)
			{
				accountNameLabel.setText(label.substring(0, charCount) + "...</a>"); //$NON-NLS-1$
			}
			gc.dispose();
		}
		anlData.widthHint = size;
		displayArea.layout(true, true);
		displayArea.getParent().layout(true, true);
	}

	/**
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#fill(org.eclipse.swt.widgets.Composite,
	 *      int, int)
	 */
	public void fill(Composite parent, int oldSide, final int newSide)
	{
		if (hand == null || hand.isDisposed())
		{
			hand = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
		}
		if (displayArea == null || displayArea.isDisposed())
		{
			displayArea = new Composite(parent, SWT.NONE);
			displayArea.setToolTipText(Messages.StartPageTrimWidget_MyAptana);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			displayArea.setLayout(layout);

			final Composite main = new Composite(displayArea, SWT.NONE);
			main.setToolTipText(Messages.StartPageTrimWidget_MyAptana);
			layout = new GridLayout(3, false);
			layout.marginHeight = 2;
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			main.setLayout(layout);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.widthHint = 170;
			main.setLayoutData(gridData);

			Label cloudIcon = new Label(main, SWT.LEFT);
			cloudIcon.setCursor(hand);
			cloudIcon.setImage(IntroPlugin.getImage(HOME_ICON));
			cloudIcon.addMouseListener(new MouseAdapter()
			{

				public void mouseDown(MouseEvent e)
				{
				    ShowMyAptanaAction.openEditor();
				}

			});
			cloudIcon.setToolTipText(Messages.StartPageTrimWidget_MyAptana);

			accountNameLabel = new Link(main, SWT.NONE);
			accountNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
			accountNameLabel.setToolTipText(Messages.StartPageTrimWidget_MyAptana);
			accountNameLabel.setText("<a>" + Messages.StartPageTrimWidget_SignIn + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
			accountNameLabel.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					if (AptanaUser.getSignedInUser().hasCredentials())
					{
					    ShowMyAptanaAction.openEditor();
					}
					else
					{
						signIn();
					}
				}

			});
			updateLabel();

			final ToolBar arrowBar = new ToolBar(main, SWT.WRAP);
			ToolItem arrowItem = new ToolItem(arrowBar, SWT.PUSH);
			if (newSide == SWT.BOTTOM)
			{
				arrowItem.setImage(IntroPlugin.getImage(UP_ARROW));
			}
			else
			{
				arrowItem.setImage(IntroPlugin.getImage(DOWN_ARROW));
			}
			arrowItem.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					Rectangle rect = arrowBar.getBounds();
					Point pt = new Point(rect.x, rect.y);
					pt = main.toDisplay(pt);

					Menu menu = new Menu(main);
					menu.setLocation(pt);
					loadMenu(menu);
					menu.setVisible(true);
				}

			});

			IWorkbenchPage page = CoreUIPlugin.getActivePage();
			if (page != null && page.getPerspective() != null)
			{
				displayArea.setVisible(WebPerspectiveFactory.isValidAptanaPerspective(page.getPerspective()));
			}
		}
	}

}
