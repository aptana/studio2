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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

// import com.aptana.ide.scripting.ScriptingEngine;

/**
 * FileErrorListener
 */
public abstract class FileErrorListener implements IFileErrorListener
{
	/**
	 * setMarkerAttributes
	 * 
	 * @param marker
	 * @param error
	 * @param document
	 */
	protected void setMarkerAttributes(IMarker marker, IFileError error, IDocument document)
	{
		try
		{
			marker.setAttribute(IMarker.TRANSIENT, true);

			marker.setAttribute(IMarker.SEVERITY, error.getSeverity());
			marker.setAttribute(IMarker.CHAR_START, error.getOffset());
			marker.setAttribute(IMarker.CHAR_END, error.getOffset() + error.getLength());
			marker.setAttribute(IMarker.MESSAGE, error.getMessage());

			try
			{
				int line = document.getLineOfOffset(error.getOffset());
				marker.setAttribute(IMarker.LINE_NUMBER, line);
			}
			catch (BadLocationException e)
			{
			}
		}
		catch (CoreException e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.FileErrorListener_Error, e1);
		}
	}

	/**
	 * notifyProblemsView
	 * 
	 * @param errors
	 */
	protected void notifyProblemsView(final IFileError[] errors)
	{
		Display display = Display.getDefault();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				/*
				 * ScriptingEngine se = ScriptingEngine.instance; if(se != null) se.onErrorsChanged(errors);
				 * IViewReference[] views = null; try { IWorkbench w = PlatformUI.getWorkbench(); IWorkbenchWindow ww =
				 * w.getActiveWorkbenchWindow(); if(ww != null) { IWorkbenchPage wp = ww.getActivePage(); views =
				 * wp.getViewReferences(); } else return; } catch(Exception e) { return; } ProblemsView problemsView =
				 * null; for(int i=0;i<views.length;i++) {
				 * if("com.aptana.ide.editors.views.problems.ProblemsView".equals(views[i].getId())) { IWorkbenchPart
				 * part = views[i].getPart(false); if(part instanceof ProblemsView) { problemsView = (ProblemsView)
				 * part; if(problemsView != null) problemsView.onErrorsChanged(errors); } return; } }
				 */
			}
		});
	}
}