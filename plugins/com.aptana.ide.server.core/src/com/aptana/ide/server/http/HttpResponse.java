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
package com.aptana.ide.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.ServerCorePlugin;

/**
 * @author Kevin Lindsey
 */
public class HttpResponse
{
	/*
	 * Fields
	 */
	private static final int STATUS_OK = 200;
	private static final String STATUS_MSG_OK = "OK"; //$NON-NLS-1$

	private OutputStream _output;
	private PrintWriter _writer;
	private boolean _headerSent;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of HttpResponse
	 * 
	 * @param output
	 */
	HttpResponse(OutputStream output)
	{
		this._output = output;
		this._writer = new PrintWriter(output);
	}

	/*
	 * Methods
	 */

	/**
	 * Returns the file
	 * 
	 * @param input
	 * @param contentLength
	 * @param contentType
	 * @throws IOException
	 */
	public void sendFileContent(InputStream input, long contentLength, String contentType) throws IOException
	{
		sendReponseHeader(STATUS_OK, STATUS_MSG_OK, contentLength, contentType);
		sendResponseContent(input);
		this._output.close();
	}

	/**
	 * Returns the file
	 * 
	 * @param input
	 * @param contentLength
	 * @param contentType
	 * @param headers
	 * @throws IOException
	 */
	public void sendFileContent(InputStream input, long contentLength, String contentType, Hashtable headers)
			throws IOException
	{
		sendReponseHeader(STATUS_OK, STATUS_MSG_OK, contentLength, contentType, headers);
		sendResponseContent(input);
		this._output.close();
	}

	/**
	 * Sends content back to the browser
	 * 
	 * @param content
	 * @param contentType
	 * @throws IOException
	 */
	public void sendContent(String content, String contentType) throws IOException
	{
		sendReponseHeader(STATUS_OK, STATUS_MSG_OK, -1, contentType);
		sendResponseContent(content);
		this._output.close();
	}

	/**
	 * Sends the error back to the browser
	 * 
	 * @param errorCode
	 * @param message
	 * @throws IOException
	 */
	public void sendError(int errorCode, String message) throws IOException
	{
		sendReponseHeader(errorCode, message, -1, null);
		this._output.close();
	}

	/**
	 * Sends the error back to the browser
	 * 
	 * @param exception
	 * @throws IOException
	 */
	public void sendError(HttpServerException exception) throws IOException
	{
		sendError(exception.getStatusCode(), exception.getStatusMessage(), exception.getContent());
		this._output.close();
	}

	/**
	 * Sends the error back to the browser
	 * 
	 * @param errorCode
	 * @param message
	 * @param content
	 * @throws IOException
	 */
	public void sendError(int errorCode, String message, String content) throws IOException
	{
		int contentLength = content != null ? content.getBytes().length : -1;

		sendReponseHeader(errorCode, message, contentLength, null);

		if (content != null)
		{
			sendResponseContent(content);
		}

		this._output.close();
	}

	/**
	 * sendReponseHeader
	 * 
	 * @param statusCode
	 * @param statusMessage
	 * @param contentLength
	 * @param contentType
	 */
	private void sendReponseHeader(int statusCode, String statusMessage, long contentLength, String contentType)
	{
		sendReponseHeader(statusCode, statusMessage, contentLength, contentType, null);
	}

	/**
	 * sendReponseHeader
	 * 
	 * @param statusCode
	 * @param statusMessage
	 * @param contentLength
	 * @param contentType
	 * @param headers
	 */
	public void sendReponseHeader(int statusCode, String statusMessage, long contentLength, String contentType,
			Hashtable headers)
	{
		if (!_headerSent)
		{
			this._writer.print("HTTP/1.0 "); //$NON-NLS-1$
			this._writer.print(statusCode);
			this._writer.print(" "); //$NON-NLS-1$
			this._writer.println(statusMessage);

			if (contentLength > 0)
			{
				this._writer.println(StringUtils.format("Content-Length: {0}", contentLength)); //$NON-NLS-1$
			}
			if (contentType != null)
			{
				this._writer.println(StringUtils.format("Content-Type: {0}", contentType)); //$NON-NLS-1$
			}
			
			// show current date/time
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zZ"); //$NON-NLS-1$
			this._writer.println("Date: " + format.format(new Date())); //$NON-NLS-1$
			
			// emit other headers
			if (headers != null)
			{
				Iterator it = headers.keySet().iterator();

				while (it.hasNext())
				{
					String hName = (String) it.next();
					String hValue = (String) headers.get(hName);

					this._writer.println(hName + ": " + hValue); //$NON-NLS-1$
				}
				
				if (headers.containsKey("Connection") == false) //$NON-NLS-1$
				{
					this._writer.println("Connection: close"); //$NON-NLS-1$
				}
			}
			else
			{
				this._writer.println("Connection: close"); //$NON-NLS-1$
			}

			this._writer.print("\r\n"); // send the empty line that marks the end of the header //$NON-NLS-1$
			this._writer.flush();

			_headerSent = true;
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	/**
	 * sendResponseContent
	 * 
	 * @param input
	 * @throws IOException
	 */
	private void sendResponseContent(InputStream input) throws IOException
	{
		try
		{
			byte[] bytes = new byte[1024];
			int numRead = input.read(bytes);
	
			while (numRead != -1)
			{
				this._output.write(bytes, 0, numRead);
				numRead = input.read(bytes);
			}
	
			this._output.flush();
		}
		catch(SocketException e)
		{
			String msg = e.getMessage();
			if(msg.startsWith("Connection reset by peer") == false) //$NON-NLS-1$
			{
				IdeLog.logError(ServerCorePlugin.getDefault(), StringUtils.format("sendResponseContent(): {0}", e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * sendResponseContent
	 * 
	 * @param content
	 * @throws IOException
	 */
	public void sendResponseContent(String content) throws IOException
	{
		this._output.write(content.getBytes("utf-8")); //$NON-NLS-1$
		this._output.flush();
	}
}
