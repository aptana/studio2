package com.aptana.ide.core.xpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.xpath.XPath;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

public class XPathUtilsTest extends TestCase
{

	public void testNewXPath()
	{
		XPath first = XPathUtils.getNewXPath();
		assertNotNull("Never returns null", first);
		assertNotSame("Creates new XPath object each call", first, XPathUtils.getNewXPath());
	}

	public void testCreateSource() throws IOException
	{
		assertNull("Null argument returns null", XPathUtils.createSource(null));

		final String input = "Hello world!";
		InputSource source = XPathUtils.createSource(input);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(source.getByteStream()));
			String read = reader.readLine();
			assertEquals(input, read);
		}
		finally
		{
			if (reader != null)
				reader.close();
		}
	}
}
