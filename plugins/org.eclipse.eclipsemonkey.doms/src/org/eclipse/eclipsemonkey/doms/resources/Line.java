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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Ingo Muschenetz
 *
 */
public class Line {
	private String text;

	private int lineNumber;

	private File file;

	/**
	 * @param text
	 * @param number
	 * @param file
	 */
	public Line(String text, int number, File file) {
		this.text = text;
		this.lineNumber = number;
		this.file = file;
	}

	/**
	 * @return The text of the line
	 */
	public String getString() {
		return text;
	}

	/**
	 * @return The line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param message
	 * @throws CoreException
	 */
	public void addMyTask(String message) throws CoreException {
		IMarker marker = this.file.getEclipseObject()
				.createMarker(IMarker.TASK);
		marker.setAttribute(Resources.standardMarkerName, file.getMarkerKey());
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.LINE_NUMBER, this.lineNumber);
	}
}
