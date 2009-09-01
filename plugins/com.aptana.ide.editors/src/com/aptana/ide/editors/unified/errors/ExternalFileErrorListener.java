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
package com.aptana.ide.editors.unified.errors;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.internal.resources.MarkerInfo;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * A listener for files external to the workspace and projects
 * 
 * @author Ingo Muschenetz
 */
public class ExternalFileErrorListener extends FileErrorListener
{
	private IAnnotationModel annotationModel;
	private IDocument document;

	/**
	 * Creates a new instance of FileErrorListener
	 * 
	 * @param annotationModel
	 * @param document
	 */
	public ExternalFileErrorListener(IAnnotationModel annotationModel, IDocument document)
	{
		this.annotationModel = annotationModel;
		this.document = document;
	}

	/**
	 * Activated when errors change
	 * 
	 * @param errors
	 *            The list of errors
	 */
	public void onErrorsChanged(final IFileError[] errors)
	{

		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor)
			{
				// notifyProblemsView(errors);
				updateErrors(errors);
			}

		};

		try
		{
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ExternalFileErrorListener_Error, e);
		}
	}

	/**
	 * Create a new ErrorMarker
	 * 
	 * @param error
	 * @return A newly created error marker
	 */
	public ErrorMarker createMarker(IFileError error)
	{
		MarkerInfo info = new MarkerInfo();
		info.setType(IMarker.PROBLEM);
		info.setCreationTime(System.currentTimeMillis());
		ErrorMarker marker = new ErrorMarker(info, error.getOffset(), error.getLength());
		setMarkerAttributes(marker, error, document);
		return marker;
	}

	/**
	 * Creates a new "key" from an annotation
	 * 
	 * @param annotation
	 * @param offset
	 * @param length
	 * @return Returns a hashtable key for the annotation
	 */
	private String createAnnotationKey(Annotation annotation, int offset, int length)
	{
		return offset + ":" + length + ":" + annotation.getText() + ":" + annotation.getType(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Updates a list of annotations
	 * 
	 * @param errors
	 *            The list of annotations to run through
	 */
	public void updateErrors(IFileError[] errors)
	{
		Hashtable h = new Hashtable();

		// remove the old annotations
		// TODO: may not want to delete ALL annotations like Bookmarks, Tasks, etc.
		for (Iterator it = annotationModel.getAnnotationIterator(); it.hasNext();)
		{
			Annotation element = (Annotation) it.next();
			Position p = annotationModel.getPosition(element);
			if(p != null)
			{
				String key = createAnnotationKey(element, p.offset, p.length);
				if (!h.containsKey(key))
				{
					h.put(key, element);
				}
			}
		}

		// add new annotations
		for (int i = 0; i < errors.length; i++)
		{
			Annotation a = new SimpleMarkerAnnotation(createMarker(errors[i]));

			String key = createAnnotationKey(a, errors[i].getOffset(), errors[i].getLength());
			if (!h.containsKey(key))
			{
				annotationModel.addAnnotation(a, new Position(errors[i].getOffset(), errors[i].getLength()));
			}
			else
			{
				h.remove(key);
			}
		}

		// remove the old annotations
		// TODO: may not want to delete ALL annotations like Bookmarks, Tasks, etc.
		Collection vals = h.values();
		Iterator iter = vals.iterator();
		while (iter.hasNext())
		{
			Annotation element = (Annotation) iter.next();
			//annotationModel.removeAnnotation(element);

			if(element instanceof SimpleMarkerAnnotation)
			{
				SimpleMarkerAnnotation sma = (SimpleMarkerAnnotation)element;
				if(sma.getMarker() instanceof ErrorMarker)
				{
					annotationModel.removeAnnotation(element);
				}
			}
		}
	}
}
