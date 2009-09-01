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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * A listener for files that are part of a project
 * @author Ingo Muschenetz
 *
 */
public class ProjectFileErrorListener extends FileErrorListener {
	IFile file;
	
	/**
	 * Creates a new listener
	 * @param file
	 */
	public ProjectFileErrorListener(IFile file)
	{
		this.file = file;
	}
	
	/**
	 * Activated when errors change in the document
	 * @param errors The list of errors to run through
	 */
	public void onErrorsChanged(final IFileError[] errors) {
		try {
			//Performance fix: schedule the error handling as a single workspace update so that we don't trigger a
			//bunch of resource updated events while problem markers are being added to the file.
			IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
				public void run(IProgressMonitor monitor){
					//notifyProblemsView(errors);
					doHandleErrorsJob(errors);
				}				
			};
			ResourcesPlugin.getWorkspace().run(runnable, getMarkerRule(file), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		}
		catch (CoreException e) {
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProjectFileErrorListener_ErrorUpdatingMarkers, e); //
		}
	}


	private void doHandleErrorsJob(IFileError[] errors)
	{
		synchronized(this) //prevent simultaneous error updates on the same file
		{
			if(ResourcesPlugin.getWorkspace().isTreeLocked())
			{
				//Note from Spike (1/26/2005): if this occurs, we will have problems getting the problem markers updated in the file.
				//Robin had put in a fix to try alleviate this that we no longer think is necessary now that errors are handled as
				//an atomic update via a IWorkspaceRunnable.  If we see this error, we should consider putting that fix back in.
				//If this exception is never seen again, we can remove this check.
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProjectFileErrorListener_ErrorUpdatingErrors, new IllegalStateException(Messages.ProjectFileErrorListener_TreeLocked)); //
			}
			
			if(file == null || file.exists() == false)
			{
				return;
			}
			
			int depth = IResource.DEPTH_INFINITE;
			try 
			{
				Hashtable h = new Hashtable();

				//get the list of existing problem markers 
				IMarker[] problemMarkers = file.findMarkers(IMarker.PROBLEM, true, depth);
				
				// remove the old annotations
				// TODO: may not want to delete ALL annotations like Bookmarks, Tasks, etc.
				for (int i = 0; i < problemMarkers.length; i++) {
					
					IMarker m = problemMarkers[i];
					String key = createAnnotationKey(m);
					if(!h.containsKey(key))
					{
						h.put(key, m);
					}
				}
				
				for(int errorNodeIndex = 0; errorNodeIndex < errors.length; errorNodeIndex++)
				{
					IFileError errorNode = errors[errorNodeIndex];
					errorNode.getMessage();
					
					String key = createAnnotationKey(errorNode.getMessage(), errorNode.getOffset(), errorNode.getLength());		
					if(!h.containsKey(key))
					{
						applyErrorToProblemMarker(
								errorNode, 
								file);
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
					IMarker marker = (IMarker)iter.next();
					marker.delete();
				}
				
				
			}
			catch(Exception e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProjectFileErrorListener_ErrorHere, e);
			}
		}
	}
	
	/**
	 * Creates a new "key" from an annotation
	 * @param annotation
	 * @param offset
	 * @param length
	 * @return Returns a hashtable key for the annotation
	 */
	private String createAnnotationKey(String annotationText, int offset, int length)
	{
		return offset + ":" + length + ":" + annotationText; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Creates a new "key" from an annotation
	 * @param marker
	 * @return Returns a hashtable key for the annotation
	 */
	private String createAnnotationKey(IMarker marker)
	{
		String markerText;
		try {
			markerText = (String)marker.getAttribute(IMarker.MESSAGE);			
			int markerStart = marker.getAttribute(IMarker.CHAR_START, 0);
			int markerEnd = marker.getAttribute(IMarker.CHAR_END, 0);
			return markerStart + ":" + (markerEnd - markerStart) + ":" + markerText; //$NON-NLS-1$ //$NON-NLS-2$
			
		} catch (CoreException e) {
			return StringUtils.EMPTY;
		}

	}
	
	private static void applyErrorToProblemMarker(
			IFileError errorNode, 
			IFile file
	)
	{
		try{
			IMarker problemMarker = file.createMarker(IMarker.PROBLEM);
			problemMarker.setAttribute(IMarker.TRANSIENT, true);
			problemMarker.setAttribute(IMarker.SEVERITY, errorNode.getSeverity());
			problemMarker.setAttribute(IMarker.CHAR_START, errorNode.getOffset());
			problemMarker.setAttribute(IMarker.CHAR_END, errorNode.getOffset() + errorNode.getLength());
			problemMarker.setAttribute(IMarker.MESSAGE, errorNode.getMessage());
			problemMarker.setAttribute(IMarker.LINE_NUMBER, errorNode.getLineNumber());
		}
		catch (CoreException e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProjectFileErrorListener_Error, e1);
		}
	}
	
	private static ISchedulingRule getMarkerRule(IResource resource) {
        ISchedulingRule rule = null;
        if (resource != null) {
            IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
            rule = ruleFactory.markerRule(resource);
        }
        return rule;
    }
}
