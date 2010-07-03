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
package com.aptana.ide.server.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class OpenStatisticsAction extends Action
{

	private ISelectionProvider provider;

	/**
	 * Constructor
	 * 
	 * @param provider
	 */
	public OpenStatisticsAction(ISelectionProvider provider)
	{
		this.setImageDescriptor(ServerUIPlugin.getImageDescriptor("/icons/stats.png")); //$NON-NLS-1$
		this.setToolTipText(Messages.OpenStatisticsAction_TTP_ViewStats);
		this.provider = provider;
		this.provider.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection && !selection.isEmpty())
				{
					StructuredSelection ss = (StructuredSelection) selection;
					if (ss.getFirstElement() instanceof IServer)
					{
						IServer server = (IServer) ss.getFirstElement();
						setEnabled(server.suppliesStatistics());
					}
					else
					{
						setEnabled(false);
					}
				}
				else
				{
					setEnabled(false);
				}
			}

		});
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		ISelection selection = provider.getSelection();
		if (selection instanceof StructuredSelection && !selection.isEmpty())
		{
			StructuredSelection ss = (StructuredSelection) selection;
			if (ss.getFirstElement() != null && ss.getFirstElement() instanceof IServer)
			{
				IServer server = (IServer) ss.getFirstElement();
				if (server.suppliesStatisticsInterface())
				{
					server.showStatisticsInterface();
				}
				else
				{
					String stats = server.fetchStatistics();
					MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), StringUtils.format(
							Messages.OpenStatisticsAction_Stats_Title, server.getName()), null, stats, MessageDialog.INFORMATION,
							new String[] { IDialogConstants.OK_LABEL }, 0)
					{

						protected Control createMessageArea(Composite composite)
						{
							super.createMessageArea(composite);
							GridData mlData = new GridData(SWT.FILL, SWT.FILL, true, true);
							super.messageLabel.setLayoutData(mlData);
							return composite;
						}

						protected int getMessageLabelStyle()
						{
							return SWT.LEFT;
						}

					};
					dialog.open();
				}
			}

		}
	}
}
