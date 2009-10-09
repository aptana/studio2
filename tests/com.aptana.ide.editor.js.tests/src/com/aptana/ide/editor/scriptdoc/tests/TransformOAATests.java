/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.scriptdoc.tests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

/**
 * @author Kevin Lindsey
 */
public class TransformOAATests extends TestCase
{
	private static final String STYLESHEET_DIRECTORY = concatPath(
		"..",
		new String[] {
			"com.aptana.ide.editor.js",
			"src",
			"com",
			"aptana",
			"ide",
			"editor",
			"scriptdoc",
			"resources"
		}
	);

	/**
	 * concatPath
	 * 
	 * @param path
	 * @param directories
	 * @return
	 */
	private static String concatPath(String path, String[] directories)
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append(path);

		for (String directory : directories)
		{
			buffer.append(File.separatorChar);
			buffer.append(directory);
		}

		return buffer.toString();
	}

	/**
	 * getFiles
	 * 
	 * @return
	 */
	private static File[] getFiles(FileFilter fileFilter)
	{
		File testDirectory = new File("OAA Tests");
		File[] files = testDirectory.listFiles(fileFilter);
		
		return files;
	}

	/**
	 * main
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		// convert OAA to Aptana
		transformFiles(
			getFiles(
				new FileFilter()
				{
					public boolean accept(File pathname)
					{
						boolean result = false;
						
						if (pathname.isFile())
						{
							String name = pathname.getName();
							
							if (name.endsWith(".xml") && name.indexOf('_') == -1)
							{
								result = true;
							}
						}
						
						return result;
					}
				}
			),
			"oaa_to_aptana.xsl",
			".xml",
			"_aptana.xml"
		);
		
		// convert Aptana to OAA
		transformFiles(
			getFiles(
				new FileFilter()
				{
					public boolean accept(File pathname)
					{
						boolean result = false;
						
						if (pathname.isFile())
						{
							String name = pathname.getName();
							
							if (name.endsWith("_aptana.xml"))
							{
								result = true;
							}
						}
						
						return result;
					}
				}
			),
			"aptana_to_oaa.xsl",
			"_aptana.xml",
			"_oaa.xml"
		);
	}

	/**
	 * transformFiles
	 * 
	 * @param stylesheetName
	 * @throws FileNotFoundException
	 */
	private static void transformFiles(File[] files, String stylesheetName, String find, String replace) throws FileNotFoundException
	{
		File stylesheetFile = new File(STYLESHEET_DIRECTORY + File.separator + stylesheetName);
		
		if (stylesheetFile.exists())
		{
			String path = stylesheetFile.getAbsolutePath(); 
			
			for (File file : files)
			{
				String result = transform(
					path,
					file.getAbsolutePath()
				);
				
				System.out.println(file.getAbsolutePath());
				
				String output = file.getAbsolutePath().replace(find, replace);
				FileWriter writer = null;
				
				try
				{
					writer = new FileWriter(output);
					writer.write(result);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (writer != null)
						{
							writer.close();
						}
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		else
		{
			System.err.println("Stylesheet does not exist" + stylesheetFile.getAbsolutePath());
		}
	}

	/**
	 * transform
	 * 
	 * @param transform
	 * @param source
	 * @return
	 */
	private static String transform(InputStream transform, InputStream source)
	{
		String result = null;

		// Create a transform factory instance.
		TransformerFactory factory = TransformerFactory.newInstance();

		// Create input/output streams
		StreamSource stylesheetSource = new StreamSource(transform);
		StreamSource sourceSource = new StreamSource(source);
		StringWriter sourceWriter = new StringWriter();
		StreamResult resultStream = new StreamResult(sourceWriter);

		try
		{
			// Create a transformer for the stylesheet.
			Transformer transformer = factory.newTransformer(stylesheetSource);
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			// Transform the source XML to System.out.
			transformer.transform(sourceSource, resultStream);

			result = sourceWriter.toString();
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sourceWriter.close();
			}
			catch (IOException e)
			{
			}
		}

		return result;
	}

	/**
	 * transform
	 * 
	 * @param transformPath
	 * @param sourcePath
	 * @return
	 * @throws FileNotFoundException
	 */
	private static String transform(String transformPath, String sourcePath) throws FileNotFoundException
	{
		InputStream stylesheetStream = new FileInputStream(transformPath);
		InputStream sourceStream = new FileInputStream(sourcePath);

		String result = transform(stylesheetStream, sourceStream);

		try
		{
			stylesheetStream.close();
		}
		catch (IOException e)
		{
		}

		try
		{
			sourceStream.close();
		}
		catch (IOException e)
		{
		}

		return result;
	}
}
