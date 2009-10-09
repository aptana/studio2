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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.Trace;
import com.aptana.ide.io.FileUtilities;
import com.aptana.ide.tests.TestsPlugin;

/**
 * @author Kevin Lindsey
 */
public final class UTFvsString
{
	private static final int COUNT = 10000;
	private static final String FILENAME = "c:\\test.bin"; //$NON-NLS-1$
	private static final String TEXT = "This is a test"; //$NON-NLS-1$

	/**
	 * UTFvsString
	 */
	private UTFvsString()
	{
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Trace.info("Testing UTF"); //$NON-NLS-1$
		testUTF();
		
		Trace.info("Testing String"); //$NON-NLS-1$
		testString();
	}

	private static void testString() throws IOException
	{
		Trace.info("Creating file"); //$NON-NLS-1$
		writeUsingString();

		Trace.info("Reading file"); //$NON-NLS-1$
		long start = System.currentTimeMillis();
		DataInputStream dis = createInputStream();
		
		for (int i = 0; i < COUNT; i++)
		{
			FileUtilities.readString(dis);
		}
		
		long diff = System.currentTimeMillis() - start;

		Trace.info("FileUtilities.readString: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
		
		dis.close();
		deleteTestFile();
	}

	private static void testUTF() throws IOException
	{
		Trace.info("Creating file"); //$NON-NLS-1$
		writeUsingUTF();
		
		Trace.info("Reading file"); //$NON-NLS-1$
		long start = System.currentTimeMillis();
		DataInputStream dis = createInputStream();
		
		for (int i = 0; i < COUNT; i++)
		{
			dis.readUTF();
		}
		
		long diff = System.currentTimeMillis() - start;

		Trace.info("DataInputStream.readUTF: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
		
		dis.close();
		deleteTestFile();
	}

	private static void writeUsingString() throws IOException
	{
		DataOutputStream dos = createOutputStream();

		for (int i = 0; i < COUNT; i++)
		{
			FileUtilities.writeString(dos, TEXT);
		}

		dos.close();
	}

	private static void writeUsingUTF() throws IOException
	{
		DataOutputStream dos = createOutputStream();

		for (int i = 0; i < COUNT; i++)
		{
			dos.writeUTF(TEXT);
		}

		dos.close();
	}

	private static DataInputStream createInputStream()
	{
		DataInputStream result = null;

		try
		{
			File testFile = new File(FILENAME);
			FileInputStream fin = new FileInputStream(testFile);
			result = new DataInputStream(fin);
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(TestsPlugin.getDefault(), CoreStrings.ERROR, e);
		}

		return result;
	}

	private static DataOutputStream createOutputStream()
	{
		DataOutputStream result = null;

		try
		{
			// create file
			File testFile = new File(FILENAME);
			FileOutputStream fos = new FileOutputStream(testFile);
			result = new DataOutputStream(fos);
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(TestsPlugin.getDefault(), CoreStrings.ERROR, e);
		}

		return result;
	}

	private static void deleteTestFile()
	{
		new File(FILENAME).delete();
	}
}
