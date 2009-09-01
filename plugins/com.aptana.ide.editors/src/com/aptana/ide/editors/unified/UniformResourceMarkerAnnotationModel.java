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
package com.aptana.ide.editors.unified;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.resources.IUniformResource;
import com.aptana.ide.core.resources.IUniformResourceChangeEvent;
import com.aptana.ide.core.resources.IUniformResourceChangeListener;
import com.aptana.ide.core.resources.IUniformResourceMarker;
import com.aptana.ide.core.resources.MarkerUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * UniformResourceMarkerAnnotationModel
 */
public class UniformResourceMarkerAnnotationModel extends AbstractMarkerAnnotationModel
{
	/**
	 * ResourceChangeListener
	 */
	private class ResourceChangeListener implements IUniformResourceChangeListener
	{
		/**
		 * @see com.aptana.ide.core.resources.IUniformResourceChangeListener#resourceChanged(com.aptana.ide.core.resources.IUniformResourceChangeEvent)
		 */
		public void resourceChanged(IUniformResourceChangeEvent event)
		{
			if (resource.equals(event.getResource()))
			{
				update(event.getMarkerDeltas());
			}
		}

	};

	private IUniformResource resource;
	private IUniformResourceChangeListener resourceChangeListener = new ResourceChangeListener();

	/**
	 * UniformResourceMarkerAnnotationModel
	 * 
	 * @param resource
	 */
	public UniformResourceMarkerAnnotationModel(IUniformResource resource)
	{
		super();
		this.resource = resource;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#retrieveMarkers()
	 */
	protected IMarker[] retrieveMarkers() throws CoreException
	{
		return MarkerUtils.findMarkers(resource, IMarker.MARKER, true);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#deleteMarkers(org.eclipse.core.resources.IMarker[])
	 */
	protected void deleteMarkers(final IMarker[] markers) throws CoreException
	{
		try
		{
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor) throws CoreException
				{
					for (int i = 0; i < markers.length; ++i)
					{
						markers[i].delete();
					}
				}
			}, null, IWorkspace.AVOID_UPDATE, null);
		}
		catch (CoreException e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.UniformResourceMarkerAnnotationModel_ErrorDeletingMarkers, e);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#listenToMarkerChanges(boolean)
	 */
	protected void listenToMarkerChanges(boolean listen)
	{
		if (listen)
		{
			MarkerUtils.addResourceChangeListener(resourceChangeListener);
		}
		else
		{
			MarkerUtils.removeResourceChangeListener(resourceChangeListener);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#isAcceptable(org.eclipse.core.resources.IMarker)
	 */
	protected boolean isAcceptable(IMarker marker)
	{
		return marker instanceof IUniformResourceMarker
				&& resource.equals(((IUniformResourceMarker) marker).getUniformResource());
	}

	/**
	 * Updates this model to the given marker deltas.
	 * 
	 * @param markerDeltas
	 *            the array of marker deltas
	 */
	protected void update(IMarkerDelta[] markerDeltas)
	{

		if (markerDeltas.length == 0)
		{
			return;
		}

		for (int i = 0; i < markerDeltas.length; i++)
		{
			IMarkerDelta delta = markerDeltas[i];
			
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					addMarkerAnnotation(delta.getMarker());
					break;
					
				case IResourceDelta.REMOVED:
					removeMarkerAnnotation(delta.getMarker());
					break;
					
				case IResourceDelta.CHANGED:
					modifyMarkerAnnotation(delta.getMarker());
					break;
					
				default:
					break;
			}
		}

		fireModelChanged();
	}
}
