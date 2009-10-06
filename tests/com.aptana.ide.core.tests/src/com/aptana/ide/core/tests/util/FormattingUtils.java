package com.aptana.ide.core.tests.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FormattingUtils
{

	private static final int DEFAULT_READING_SIZE = 8192;

	/**
	 * FormattingUtils
	 */
	private FormattingUtils()
	{
	}

	/**
	 * @param expected
	 * @param format
	 * @return is strings equal if does not respect line delimeters
	 */
	public static boolean compareWithoutDelimeters(String expected, String format)
	{
		return changeDelimeters(expected).equals(changeDelimeters(format));
	}

	/**
	 * compareByTokens
	 * 
	 * @param string1
	 * @param string2
	 * @return boolean
	 */
	public static boolean compareByTokens(String string1, String string2)
	{
		boolean equals = removeSpaces(string1).equals(removeSpaces(string2));

		if (!equals)
		{
			// CHECKSTYLE:OFF
			System.err.println("has:\n" + string2); //$NON-NLS-1$
			System.err.println("should be:\n" + string1); //$NON-NLS-1$
			// CHECKSTYLE:ON
		}

		return equals;
	}

	/**
	 * readString
	 * 
	 * @param stream
	 * @return String
	 * @throws IOException 
	 */
	public static String readString(InputStream stream) throws IOException
	{
		try
		{
			return new String(getInputStreamAsCharArray(stream, -1, null));
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	private static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException
	{
		InputStreamReader reader = null;
		reader = encoding == null ? new InputStreamReader(stream) : new InputStreamReader(stream, encoding);
		char[] contents;
		if (length == -1)
		{
			contents = new char[0];
			int contentsLength = 0;
			int amountRead = -1;
			do
			{
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE); // read
				// at
				// least
				// 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length)
				{
					System.arraycopy(contents, 0, contents = new char[contentsLength + amountRequested], 0,
							contentsLength);
				}

				// read as many chars as possible
				amountRead = reader.read(contents, contentsLength, amountRequested);

				if (amountRead > 0)
				{
					// remember length of contents
					contentsLength += amountRead;
				}
			}
			while (amountRead != -1);

			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (contentsLength > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF)
				{ // if BOM char then skip
					contentsLength--;
					start = 1;
				}
			}
			// resize contents if necessary
			if (contentsLength < contents.length)
			{
				System.arraycopy(contents, start, contents = new char[contentsLength], 0, contentsLength);
			}
		}
		else
		{
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length))
			{
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (length > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF)
				{ // if BOM char then skip
					len--;
					start = 1;
				}
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more than
			// one byte for each
			// character
			if (len != length)
				System.arraycopy(contents, start, (contents = new char[len]), 0, len);
		}

		return contents;
	}

	/**
	 * removeSpaces
	 * 
	 * @param src
	 * @return String
	 */
	static String removeSpaces(String src)
	{
		StringBuffer buf0 = new StringBuffer();

		for (int a = 0; a < src.length(); a++)
		{
			char c = src.charAt(a);

			if (Character.isWhitespace(c))
			{
				continue;
			}

			buf0.append(c);
		}

		return buf0.toString();
	}

	public static String changeDelimeters(String arg)
	{
		return changeDelimeters(arg, "!");
	}

	public static String changeDelimeters(String arg, String replacement)
	{
		StringBuffer buf = new StringBuffer();
		for (int a = 0; a < arg.length(); a++)
		{
			char c = arg.charAt(a);
			if (c == '\r')
			{
				if (a != arg.length() - 1)
				{
					if (arg.charAt(a + 1) == '\n')
					{
						continue;
					}
				}
				buf.append(replacement);
				continue;
			}
			if (c == '\n')
			{
				buf.append(replacement);
				continue;
			}
			buf.append(c);
		}
		return buf.toString();
	}
}
