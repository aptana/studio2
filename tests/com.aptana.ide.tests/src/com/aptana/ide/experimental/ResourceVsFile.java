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
package com.aptana.ide.experimental;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.aptana.ide.core.Trace;
import com.aptana.ide.metadata.MetadataEnvironment;
import com.aptana.ide.metadata.reader.MetadataObjectsReader;

/**
 * @author Kevin Lindsey
 */
public final class ResourceVsFile
{
	/**
	 * ResourceVsFile
	 */
	private ResourceVsFile()
	{
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		readUsingResource();
		readUsingFile();
		readUsingZipFile();
	}

	private static void readUsingFile() throws Exception
	{
		Trace.info("Reading using a file"); //$NON-NLS-1$

		long start = System.currentTimeMillis();
		createReader(new FileInputStream("e:\\CSSMetadata.bin")); //$NON-NLS-1$
		long diff = System.currentTimeMillis() - start;

		Trace.info("File: " + diff + "ms");  //$NON-NLS-1$//$NON-NLS-2$
	}

	private static void readUsingZipFile() throws Exception
	{
		Trace.info("Reading using a zip file"); //$NON-NLS-1$

		long start = System.currentTimeMillis();
		ZipFile zf = new ZipFile("e:\\CSSMetadata.zip"); //$NON-NLS-1$
		ZipEntry ze = zf.getEntry("CSSMetadata.bin"); //$NON-NLS-1$
		createReader(zf.getInputStream(ze));
		long diff = System.currentTimeMillis() - start;

		Trace.info("Zip File: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void readUsingResource() throws Exception
	{
		Trace.info("Reading using a resource"); //$NON-NLS-1$

		long start = System.currentTimeMillis();
		createReader(MetadataObjectsReader.class.getResourceAsStream("/com/aptana/ide/css/CSSMetadata.bin")); //$NON-NLS-1$
		long diff = System.currentTimeMillis() - start;

		Trace.info("Resource: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void createReader(InputStream input) throws Exception
	{
		MetadataEnvironment environment = new MetadataEnvironment();
		MetadataObjectsReader reader = new MetadataObjectsReader(environment);

		reader.load(input);
		input.close();
	}
}
