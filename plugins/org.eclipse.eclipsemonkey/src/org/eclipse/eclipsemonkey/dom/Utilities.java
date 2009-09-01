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

package org.eclipse.eclipsemonkey.dom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.eclipsemonkey.internal.DynamicState;

/**
 * Utilities
 *
 */
public class Utilities {

	/**
	 * @param path
	 * @return String
	 * @throws CoreException
	 * @throws IOException
	 */
	public static String getFileContents(IPath path) throws CoreException,
			IOException {
		return getFileContents(path.toFile());
	}
	
	/**
	 * @param file
	 * @return String
	 * @throws CoreException
	 * @throws IOException
	 */
	public static String getFileContents(File file) throws CoreException,
		IOException {
		final int BUF_SIZE = 100000;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if(in != null)
			{
				StringBuffer result = new StringBuffer();
				while (true) {
					byte[] buf = new byte[BUF_SIZE];
					int count = in.read(buf);
					if (count <= 0)
						return result.toString();
					byte[] buf2 = new byte[count];
					for (int k = 0; k < count; k++) {
						buf2[k] = buf[k];
					}
					result.append(new String(buf2));
				}
			}
			else
			{
				return ""; //$NON-NLS-1$
			}
		} finally {
			if (in != null)
				in.close();
		}
	}


	private static IDynamicState _state = new DynamicState();

	/**
	 * @return IDynamicState
	 */
	public static IDynamicState state() {
		return _state;
	}

	/**
	 * SCRIPT_NAME
	 */
	public static final String SCRIPT_NAME = "scriptName"; //$NON-NLS-1$
}
