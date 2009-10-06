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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.aptana.ide.core.Trace;

/**
 * @author Kevin Lindsey
 */
public final class WritingStrings
{
	private static final int COUNT = 100000;
	private static final String FILENAME = "e:\\test.bin"; //$NON-NLS-1$
	private static final String TEXT = "This is a test"; //$NON-NLS-1$

	/**
	 * WritingStrings
	 */
	private WritingStrings()
	{
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		testIndividualStrings();
		testTable();
		testBufferedTable();
	}

	private static void testBufferedTable() throws IOException
	{
		// create file
		writeTable();

		// read file
		long start = System.currentTimeMillis();
		readBufferedTable();
		long diff = System.currentTimeMillis() - start;
		Trace.info("Buffered Table: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$

		// delete file
		deleteFile();
	}

	private static void testTable() throws IOException
	{
		// create file
		writeTable();

		// read file
		long start = System.currentTimeMillis();
		readTable();
		long diff = System.currentTimeMillis() - start;
		Trace.info("Table: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$

		// delete file
		deleteFile();
	}

	private static void testIndividualStrings() throws IOException
	{
		// create file
		writeStrings();

		// read file
		long start = System.currentTimeMillis();
		readStrings();
		long diff = System.currentTimeMillis() - start;
		Trace.info("Individual strings: " + diff + "ms"); //$NON-NLS-1$ //$NON-NLS-2$

		// delete file
		deleteFile();
	}

	private static void readStrings() throws IOException
	{
		DataInputStream input = createInput();

		for (int i = 0; i < COUNT; i++)
		{
			input.readUTF();
		}

		input.close();
	}

	private static void readTable() throws IOException
	{
		DataInputStream input = createInput();

		// read int table
		input.readInt();
		
		for (int i = 0; i < COUNT; i++)
		{
			input.readInt();
		}
		
		// read string table
		int length = input.readInt();
		byte[] data = new byte[length];
		input.read(data);
		String text = new String(data);
		Trace.info("    text(0,14) = " + text.substring(0, 14)); //$NON-NLS-1$

		input.close();
	}
	
	private static void readBufferedTable() throws IOException
	{
		DataInputStream input = createInput();

		// read int table
		int length = input.readInt();
		byte[] lengthBuffer = new byte[4*length];
		input.read(lengthBuffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(lengthBuffer);
		DataInputStream lengths = new DataInputStream(bais);
		
		for (int i = 0; i < COUNT; i++)
		{
			lengths.readInt();
		}
		
		lengths.close();
		
		// read string table
		length = input.readInt();
		byte[] data = new byte[length];
		input.read(data);
		String text = new String(data);
		Trace.info("    text(0,14) = " + text.substring(0, 14)); //$NON-NLS-1$

		input.close();
	}
	
	private static void writeStrings() throws IOException
	{
		DataOutputStream output = createOutput();

		for (int i = 0; i < COUNT; i++)
		{
			output.writeUTF(TEXT);
		}

		output.close();
	}

	private static void writeTable() throws IOException
	{
		DataOutputStream output = createOutput();
		StringBuffer sb = new StringBuffer();

		// write index table
		int index = 0;
		output.writeInt(COUNT);
		
		for (int i = 0; i < COUNT; i++)
		{
			output.writeInt(index);
			index += TEXT.length();
		}
		
		// build string
		for (int i = 0; i < COUNT; i++)
		{
			sb.append(TEXT);
		}
		
		// write string
		String text = sb.toString();
		byte[] data = text.getBytes();
		output.writeInt(data.length);
		output.write(data);

		output.close();
	}
	
	private static DataInputStream createInput() throws FileNotFoundException
	{
		File file = new File(FILENAME);
		FileInputStream in = new FileInputStream(file);

		return new DataInputStream(in);
	}

	private static DataOutputStream createOutput() throws FileNotFoundException
	{
		File file = new File(FILENAME);
		FileOutputStream out = new FileOutputStream(file);

		return new DataOutputStream(out);
	}

	private static void deleteFile()
	{
		new File(FILENAME).delete();
	}
}
