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
package com.aptana.ide.server.jetty;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.server.jetty.comet.CometClient;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Chris Williams (cwilliams@aptana.com)
 */
public class ShowPerspectiveClient extends CometClient
{

	public static final String PROPERTY_KEY = "CloudSiteId"; //$NON-NLS-1$

	private static final String SHOW_PERSPECTIVE = "/portal/perspectives/show"; //$NON-NLS-1$

	private static final String ID = "id"; //$NON-NLS-1$
	private static final String VIEW_ID = "viewId"; //$NON-NLS-1$
	private static final String EXTRA = "extra"; //$NON-NLS-1$
	private static final String SITE_ID = "siteId"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (SHOW_PERSPECTIVE.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map<String, Object> requestData = (Map<String, Object>) request;
				if (requestData.containsKey(ID))
				{
					final String id = requestData.get(ID).toString();
					String view = null;
					if (requestData.containsKey(VIEW_ID))
					{
						view = requestData.get(VIEW_ID).toString();
					}
					final String viewId = view;
					
					String site = null;
					Object extras = requestData.get(EXTRA);
					if (extras instanceof Map)
					{
						Map<String, Object> extraData = (Map<String, Object>) extras;
						if (extraData.containsKey(SITE_ID))
						{
							site = extraData.get(SITE_ID).toString();
						}
					}
					(new InstanceScope()).getNode(ResourcesPlugin.PI_RESOURCES).put(PROPERTY_KEY, site);

					UIJob job = new UIJob("Opening perspective") //$NON-NLS-1$
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (shouldSwitchPerspective(id))
							{
								try
								{
									JettyPlugin.getDefault().getWorkbench().showPerspective(id,
											JettyPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());
								}
								catch (WorkbenchException e)
								{
								}
							}
							if (viewId != null)
							{
								try
								{
									CoreUIUtils.showView(viewId);
								}
								catch (PartInitException e)
								{
								}
							}
							return Status.OK_STATUS;
						}

					};
					job.schedule();
				}
			}
		}
		return null;
	}

	private boolean shouldSwitchPerspective(String id)
	{
		if (isCurrentPerspective(JettyPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow(), id))
			return false;
		if (shouldAutoOpen(id))
			return true;
		return promptUser(id);
	}

	private boolean promptUser(String id)
	{
		Shell shell = JettyPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialogWithToggle dialog = new MessageDialogWithToggle(
        		shell,
        		Messages.ShowPerspectiveClient_1, 
        		null, // accept the default window icon
        		MessageFormat.format(Messages.ShowPerspectiveClient_2, getPerspectiveName(id)),
                MessageDialogWithToggle.QUESTION,
                new String[] { IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL,
                        IDialogConstants.CANCEL_LABEL }, 0, // YES is the default
                null, 
                false) {
			@Override
			protected Control createCustomArea(Composite parent)
			{
				try
				{
					URL url = JettyPlugin.getDefault().getBundle().getEntry("/icons/animation.gif"); //$NON-NLS-1$
					url = FileLocator.toFileURL(url);
					Browser animation = new Browser(parent, SWT.NULL);
					animation.setText(
							"<html>" + //$NON-NLS-1$
							"<body style='margin: 0; padding:0; border: 1px solid black' >" + //$NON-NLS-1$
							"<center>" + //$NON-NLS-1$
							"<img src='" + url.toExternalForm() + "' />" + //$NON-NLS-1$ //$NON-NLS-2$
							"</center>" + //$NON-NLS-1$
							"</body>" + //$NON-NLS-1$
							"</html>" //$NON-NLS-1$
							);
			        GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
			        gridData.widthHint = 311 + 2; // 2 for border
			        gridData.heightHint = 169 + 2; // 2 for border
			        animation.setLayoutData(gridData);
					return animation;
				}
				catch (IOException e)
				{
				}
				Label animation = new Label(parent, SWT.CENTER);
				animation.setImage(JettyPlugin.getImage("/icons/animation.gif")); //$NON-NLS-1$
		        GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		        animation.setLayoutData(gridData);
				return animation;
			}
		};
		dialog.setPrefStore(JettyPlugin.getDefault().getPreferenceStore());
		dialog.setPrefKey(getPrefKey(id));
		dialog.open();
		
		return (dialog.getReturnCode() == IDialogConstants.YES_ID);
	}

	private String getPerspectiveName(String id)
	{
		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.ui", "perspectives"); //$NON-NLS-1$ //$NON-NLS-2$
		if (extension == null)
			return id;
		IExtension[] extensions = extension.getExtensions();
		// for all extensions of this point...
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++)
			{
				final IConfigurationElement configElement = configElements[j];
				if (id.equals(configElement.getAttribute("id"))) //$NON-NLS-1$
				{
					return configElement.getAttribute("name"); //$NON-NLS-1$
				}
			}
		}
		return id;
	}

	private boolean shouldAutoOpen(final String id)
	{
		String value = JettyPlugin.getDefault().getPreferenceStore().getString(getPrefKey(id));
		if (value == null)
			return false;
		return value.equals(MessageDialogWithToggle.ALWAYS);
	}

	private String getPrefKey(String id)
	{
		return id + ".auto_open"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { SHOW_PERSPECTIVE };
	}

	/**
	 * Returns whether the given perspective identifier matches the identifier of the current perspective.
	 * 
	 * @param window
	 * @param perspectiveId
	 *            the identifier
	 * @return whether the given perspective identifier matches the identifier of the current perspective
	 */
	protected boolean isCurrentPerspective(IWorkbenchWindow window, String perspectiveId)
	{
		boolean isCurrent = false;
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				IPerspectiveDescriptor perspectiveDescriptor = page.getPerspective();
				if (perspectiveDescriptor != null)
				{
					isCurrent = perspectiveId.equals(perspectiveDescriptor.getId());
				}
			}
		}
		return isCurrent;
	}

}
