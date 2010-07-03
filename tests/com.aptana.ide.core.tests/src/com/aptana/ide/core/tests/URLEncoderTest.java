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

package com.aptana.ide.core.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.URLEncoder;

/**
 * @author Max Stepanov
 *
 */
public class URLEncoderTest extends TestCase
{

	public void testHttp() throws MalformedURLException {
		assertEquals("http://localhost", URLEncoder.encode(new URL("http://localhost")).toExternalForm());
		assertEquals("http://localhost:8080", URLEncoder.encode(new URL("http://localhost:8080")).toExternalForm());
		assertEquals("http://www.aptana.com/", URLEncoder.encode(new URL("http://www.aptana.com/")).toExternalForm());
		assertEquals("http://localhost/path/to/file",
				URLEncoder.encode(new URL("http://localhost/path/to/file")).toExternalForm());
		assertEquals("http://localhost:80/path%20to%20file/file%20with%20spaces.html",
				URLEncoder.encode(new URL("http://localhost:80/path to file/file with spaces.html")).toExternalForm());
		assertEquals("http://localhost/path%20to%20file/file%20with%20spaces.html",
				URLEncoder.encode(new URL("http://localhost/path to file/file with spaces.html")).toExternalForm());
		assertEquals("http://localhost/file0123456789.html",
				URLEncoder.encode(new URL("http://localhost/file0123456789.html")).toExternalForm());
		assertEquals("http://localhost/~path_to-file/file$with!special*+,(chars).html#",
				URLEncoder.encode(new URL("http://localhost/~path_to-file/file$with!special*+,(chars).html#")).toExternalForm());
		assertEquals("http://localhost/path%25to%22file%5B%5D",
				URLEncoder.encode(new URL("http://localhost/path%to\"file[]")).toExternalForm());
	}

	public void testFile() throws MalformedURLException {
		if(Platform.OS_WIN32.equals(Platform.getOS()))
		{
			assertEquals("file:/C:/", new File("C:\\").toURL().toExternalForm());
			assertEquals("file:/C:/", URLEncoder.encode(new File("C:\\").toURL()).toExternalForm());
			assertEquals("file://C:/", URLEncoder.encode(new URL("file://C:/")).toExternalForm());			
			assertEquals("file:/C:/Documents%20and%20settings/", URLEncoder.encode(new File("C:\\Documents and settings\\").toURL()).toExternalForm());
		}
	}
	
	public void testPathEncode() {
		assertEquals("C:/", URLEncoder.encode("C:/", null, null));
		assertEquals("C:/path/to/file", URLEncoder.encode("C:/path/to/file", null, null));
		assertEquals("C:/path%20to%20file", URLEncoder.encode("C:/path to file", null, null));
	}

}
