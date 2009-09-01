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
package com.aptana.ide.server.ui.decorators;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.StartPage;

/**
 * StartPageDecorator
 */
public class StartPageDecorator implements ILightweightLabelDecorator, Observer
{
	private static final ImageDescriptor START_PAGE;
	private IResource decoratedResource;
	private ListenerList listeners = new ListenerList(1);

	static
	{
		START_PAGE = ServerUIPlugin.getImageDescriptor("icons/start_page_ovr.gif"); //$NON-NLS-1$
	}

	/**
	 * StartPageDecorator
	 */
	public StartPageDecorator()
	{
		decoratedResource = StartPage.getInstance().getStartPageResource();
		StartPage.getInstance().addObserver(this);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration)
	{
		if (element instanceof IResource == false)
		{
			return;
		}
		IResource resource = (IResource) element;
		IResource startPage = decoratedResource;
		if (startPage != null && startPage.equals(resource))
		{
			decoration.addOverlay(START_PAGE);
		}

		// decoration.setFont(new Font(decoration.setFont(new Font(JFaceResources.getDefaultFont().getFontData());
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * fireLabelEvent
	 * 
	 * @param event
	 */
	private void fireLabelEvent(final LabelProviderChangedEvent event)
	{
		// Decorate using current UI thread
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				// Fire a LabelProviderChangedEvent to notify eclipse views
				// that label provider has been changed for the resources
				fireLabelProviderChanged(event);
			}
		});
	}

	/**
	 * Fires a label provider changed event to all registered listeners Only listeners registered at the time this
	 * method is called are notified.
	 * 
	 * @param event
	 *            a label provider changed event
	 * @see ILabelProviderListener#labelProviderChanged
	 */
	protected void fireLabelProviderChanged(final LabelProviderChangedEvent event)
	{
		Object[] listeners = this.listeners.getListeners();
		for (int i = 0; i < listeners.length; ++i)
		{
			final ILabelProviderListener l = (ILabelProviderListener) listeners[i];
			SafeRunnable.run(new SafeRunnable()
			{
				public void run()
				{
					l.labelProviderChanged(event);
				}
			});

		}
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1)
	{
		// Fire a label provider changed event to decorate the
		// resources whose image needs to be updated
		IResource oldDecoratedResource = decoratedResource;
		decoratedResource = StartPage.getInstance().getStartPageResource();
		IResource[] updatedResources = null;
		if (oldDecoratedResource != null)
		{
			if (decoratedResource != null)
			{
				updatedResources = new IResource[] { oldDecoratedResource, decoratedResource };
			}
			else
			{
				updatedResources = new IResource[] { oldDecoratedResource };
			}
		}
		else if (decoratedResource != null)
		{
			updatedResources = new IResource[] { decoratedResource };
		}
		if (updatedResources != null)
		{
			fireLabelEvent(new LabelProviderChangedEvent(this, updatedResources));	
		}
	}
}
