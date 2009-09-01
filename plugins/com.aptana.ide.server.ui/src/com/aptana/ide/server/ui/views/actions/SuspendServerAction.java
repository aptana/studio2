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
package com.aptana.ide.server.ui.views.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IPausableServer;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.views.Messages;

/**
 * @author Pavel Petrochenko
 */
public final class SuspendServerAction extends Action
{
	private ISelectionProvider provider;
	private boolean isPaused;
	private IStatus lastStatus;

	/**
	 * @param provider
	 */
	public SuspendServerAction(ISelectionProvider provider)
	{

		this.provider = provider;
		provider.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				SuspendServerAction.this.selectionChanged(event);
			}

		});
		selectionChanged(new SelectionChangedEvent(provider, provider
				.getSelection()));
	}

	/**
	 * @param event
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{

		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();

		if (!selection.isEmpty())
		{
			boolean enabled2 = isEnabled();
			IServer server = (IServer) selection.getFirstElement();
			if (server instanceof IPausableServer)
			{
				if (server.getServerState() == IPausableServer.STATE_PAUSED)
				{
					initResumeDescriptors(server, enabled2);
				} else
				{
					initPauseDescriptors(server, enabled2);
				}
			} else
			{
				initPauseDescriptors(server, false);
			}
			setEnabled(enabled2);
		} else
		{
			initPauseDescriptors(null, false);
			setEnabled(false);
		}
	}

	private void initPauseDescriptors(IServer server, boolean enabled2)
	{
		String name = server != null ? server.getName() : ""; //$NON-NLS-1$
		// this.setText(StringUtils.format(Messages.ServersView_PAUSE, name));
		if (!enabled2)
		{
			if (lastStatus != null)
			{
				String message = lastStatus.getMessage();
				setToolTipText(message);
			} else
			{
				String format = StringUtils.format(
						Messages.SuspendServerAction_CAN_NOT_BE_PAUSED, name);
				setToolTipText(format);
			}
		} else
		{
			String string = Messages.SuspendServerAction_PAUSE_SERVER;
			setToolTipText(string);
		}
		if (!isPaused)
		{
			this.setImageDescriptor(ServerUIPlugin
					.getImageDescriptor("/icons/server/elcl16/suspend_co.gif")); //$NON-NLS-1$
			this.setDisabledImageDescriptor(ServerUIPlugin
					.getImageDescriptor("/icons/server/dlcl16/suspend_co.gif")); //$NON-NLS-1$			
			isPaused = true;
		}

	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	public boolean isEnabled()
	{
		IStructuredSelection selection = (IStructuredSelection) provider
				.getSelection();
		if (!selection.isEmpty())
		{
			IServer server = (IServer) selection.getFirstElement();
			this.lastStatus = null;
			if (server instanceof IPausableServer)
			{
				IPausableServer ps = (IPausableServer) server;
				if (ps.getServerState() == IPausableServer.STATE_PAUSED)
				{
					IStatus canResume = ps.canResume();
					return canResume.isOK();
				}
				IStatus canPause = ps.canPause();
				return canPause.isOK();
			}
			return false;
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		IStructuredSelection selection = (IStructuredSelection) provider
				.getSelection();
		if (!selection.isEmpty())
		{
			IPausableServer server = (IPausableServer) selection
					.getFirstElement();
			try
			{
				if (server.getServerState() == IPausableServer.STATE_PAUSED)
				{
					server.resume(null, null);
				} else
				{
					server.pause(null, null);
				}
			} catch (Exception e)
			{
				IdeLog.log(ServerUIPlugin.getDefault(), IStatus.ERROR,
						"exception while starting server", e); //$NON-NLS-1$
			}
		}
	}

	private void initResumeDescriptors(IServer server, boolean enabled2)
	{
		String name = server != null ? server.getName() : ""; //$NON-NLS-1$
		if (enabled2)
		{
			String format = StringUtils.format(Messages.ServersView_RESUME,
					name);
			this.setText(format);
			this.setToolTipText(format);
		} else
		{

			String format = lastStatus != null ? lastStatus.getMessage()
					: StringUtils.format(
							Messages.SuspendServerAction_RESUME_TOOLTIP, name);
			this.setToolTipText(format);
			this.setText(format);
		}
		if (isPaused)
		{
			setImageDescriptor(ServerUIPlugin
					.getImageDescriptor("/icons/server/elcl16/resume_co.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(ServerUIPlugin
					.getImageDescriptor("/icons/server/dlcl16/resume_co.gif")); //$NON-NLS-1$
			isPaused = false;
		}
	}
}