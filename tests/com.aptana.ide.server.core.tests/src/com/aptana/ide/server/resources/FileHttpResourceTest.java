package com.aptana.ide.server.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import com.aptana.ide.core.StreamUtils;

public class FileHttpResourceTest extends TestCase
{

	public void testFileDoesntExist()
	{
		File file = new File("thisshoudlnotexist.txt");
		FileHttpResource resource = new FileHttpResource(file);
		assertEquals(0, resource.getContentLength());
		// FIXME Why do we return null here, but the folder backed one throws an IOException?
		assertNull(resource.getContentInputStream(null));
		assertEquals("text/plain", resource.getContentType());
	}

	public void testFileDoesxist() throws IOException
	{
		final String contents = "testing123";
		File file = File.createTempFile("filehttpresourcetest", ".txt");
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file);
			writer.write(contents);
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
		try
		{
			FileHttpResource resource = new FileHttpResource(file);
			assertEquals(contents.length(), resource.getContentLength());
			String read = StreamUtils.readContent(resource.getContentInputStream(null), null);
			assertEquals(contents, read);
			assertEquals("text/plain", resource.getContentType());
		}
		finally
		{
			if (file != null)
			{
				if (!file.delete())
					file.deleteOnExit();
				file = null;
			}
		}
	}
}
