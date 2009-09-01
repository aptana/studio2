/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey.doms.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.eclipsemonkey.dom.Utilities;

/**
 * File
 */
public class File {
	private IFile file;

	/**
	 * @param resource
	 */
	public File(IResource resource) {
		file = (IFile) resource;
	}

	/**
	 * getEclipseObject
	 * @return The Eclipse IFile object
	 */
	public IFile getEclipseObject() {
		return file;
	}

	/**
	 * getLines
	 * @return The lines in the file
	 */
	public Line[] getLines() {
		try {
			List result = new ArrayList();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					file.getContents()));
			String text;
			int lineNumber = 0;
			while ((text = reader.readLine()) != null) {
				lineNumber++;
				result.add(new Line(text, lineNumber, this));
			}
			reader.close();
			int i = 0;
			Line[] rtrn = new Line[result.size()];
			for (Iterator iter = result.iterator(); iter.hasNext();) {
				Line element = (Line) iter.next();
				rtrn[i++] = element;
			}
			return rtrn;
		} catch (CoreException x) {
			return new Line[0];
		} catch (IOException x) {
			return new Line[0];
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public long getSize()
	{
		long size = -1;
		
		try {
			URI location = file.getLocationURI();
			IFileStore store = EFS.getStore(location);
			IFileInfo info = store.fetchInfo();
			size = info.getLength();
		} catch (CoreException e) {
			e.printStackTrace();
			size = -1;
		}
		
		return size;
	}

	/**
	 * 
	 * @return
	 */
	public long getLastModified()
	{
		long date = -1;
		
		try {
			URI location = file.getLocationURI();
			IFileStore store = EFS.getStore(location);
			IFileInfo info = store.fetchInfo();
			date = info.getLastModified();
		} catch (CoreException e) {
			e.printStackTrace();
			date = -1;
		}
		
		return date;
	}
	
	/**
	 * removeMyTasks
	 * @throws CoreException
	 */
	public void removeMyTasks() throws CoreException {
		IMarker[] markers = file.findMarkers(IMarker.TASK, false, 0);
		String key = this.getMarkerKey();
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			Object value = marker.getAttribute(Resources.standardMarkerName);
			if (key.equals(value)) {
				marker.delete();
			}
		}
	}

	String getMarkerKey() {
		return (String) Utilities.state().get(Utilities.SCRIPT_NAME);
	}
}
