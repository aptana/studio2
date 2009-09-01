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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.ServerCorePlugin;
import com.aptana.ide.server.logging.IHttpLog;
import com.aptana.ide.server.resolvers.IHttpResourceResolver;
import com.aptana.ide.server.resources.IHttpFolderResource;
import com.aptana.ide.server.resources.IHttpResource;

/**
 * @author Kevin Lindsey
 */
public class ServerThreadRunnable implements Runnable
{
	/*
	 * Fields
	 */
	private boolean _stopped;
	private HttpServer _server;

	private ServerSocket _socketServer;
	private IHttpResourceResolver _resourceResolver;
	private IHttpLog _logger;

	/* Where worker threads stand idle */
	Vector threads = new Vector();

	/* max # worker threads */
	int workers = 5;

	/* timeout on client connections */
	int timeout = 5000;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of ServerThreadRunnable
	 * 
	 * @param server
	 */
	public ServerThreadRunnable(HttpServer server)
	{
		this(server, 5000);
	}
	
	/**
	 * Create a new instance of ServerThreadRunnable
	 * 
	 * @param server
	 * @param timeout
	 */
	public ServerThreadRunnable(HttpServer server, int timeout)
	{
		this._server = server;
		this._socketServer = this._server.getSocketServer();
		this._resourceResolver = this._server.getResourceResolver();
		this._logger = this._server.getLogger();
		this.timeout = timeout;
	}
	

	/*
	 * Methods
	 */

	/**
	 * Runs the server
	 */
	public void run()
	{
		for (int i = 0; i < workers; ++i)
		{
			Worker w = new Worker();
			(new Thread(w, "Aptana: HTTP Worker " + i)).start(); //$NON-NLS-1$
			threads.addElement(w);
		}

		while (this._stopped == false && !this._socketServer.isClosed() )
		{
			try
			{
				Socket s = this._socketServer.accept();

				Worker w = null;
				synchronized (threads)
				{
					if (threads.isEmpty())
					{
						Worker ws = new Worker();
						ws.setSocket(this, s, this._server, this._resourceResolver, this._logger);
						(new Thread(ws, "Aptana: HTTP Worker (Additional)")).start(); //$NON-NLS-1$
					}
					else
					{
						w = (Worker) threads.elementAt(0);
						threads.removeElementAt(0);
						w.setSocket(this, s, this._server, this._resourceResolver, this._logger);
					}
				}
			}
			catch (Exception e)
			{
				if ( this._stopped == false || this._socketServer.isClosed() == false ) {
					/* skip socket closed exception on stop() */
					this._logger.logError(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Stops the server
	 */
	public synchronized void stop()
	{
		if (this._stopped == false)
		{
			this._stopped = true;
			try
			{
				this._socketServer.close();
			}
			catch (Exception e)
			{
				this._logger.logError(e.getMessage(), e);
			}
		}
	}
}

/**
 * Worker
 * @author Ingo Muschenetz
 *
 */
class Worker implements Runnable
{
	static final int BUF_SIZE = 2048;
	static final byte[] EOL = { (byte) '\r', (byte) '\n' };

	private Socket s;
	private ServerThreadRunnable _serverThreadRunnable;
	private HttpServer _server;
	private IHttpResourceResolver _resourceResolver;
	private IHttpLog _logger;

	/* buffer to use for requests */
	byte[] buf;

	Worker()
	{
		buf = new byte[BUF_SIZE];
		s = null;
	}

	synchronized void setSocket(ServerThreadRunnable serverThreadRunnable, Socket s, HttpServer server,
			IHttpResourceResolver resourceResolver, IHttpLog logger)
	{
		this._serverThreadRunnable = serverThreadRunnable;
		this.s = s;
		this._server = server;
		this._resourceResolver = resourceResolver;
		this._logger = logger;

		notify();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run()
	{
		while (true)
		{
			if (s == null)
			{
				/* nothing to do */
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					/* should not happen */
					continue;
				}
			}
			try
			{
				processRequest();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
			/*
			 * go back in wait queue if there's fewer than numHandler connections.
			 */
			s = null;
			Vector pool = _serverThreadRunnable.threads;
			synchronized (pool)
			{
				if (pool.size() >= _serverThreadRunnable.workers)
				{
					/* too many threads, exit this one */
					return;
				}
				else
				{
					pool.addElement(this);
				}
			}
		}
	}

	/**
	 * processRequest
	 * 
	 * @param s
	 * @throws IOException
	 */
	private void processRequest() throws IOException
	{
		String uri = null;

		try
		{
			InputStream is = new BufferedInputStream(s.getInputStream());

			/*
			 * we will only block in read for this many milliseconds before we fail with java.io.InterruptedIOException,
			 * at which point we will abandon the connection.
			 */
			s.setSoTimeout(_serverThreadRunnable.timeout);
			s.setTcpNoDelay(true);

			/* zero out the buffer from last time */
			for (int i = 0; i < BUF_SIZE; i++)
			{
				buf[i] = 0;
			}

			/* 
			 * Read in the full header of the request 
			 */
			int nread = 0, r = 0;

			outerloop: while (nread < BUF_SIZE)
			{
				try
				{
					r = is.read(buf, nread, BUF_SIZE - nread);
				}
				catch(SocketException e)
				{
					return;
				}
				
				if (r == -1)
				{
					/* EOF */
					return;
				}
				int i = nread;
				nread += r;
				for (; i < nread; i++)
				{
					if (buf[i] == (byte) '\n' || buf[i] == (byte) '\r')
					{
						/* read one line */
						break outerloop;
					}
				}
			}

			String reqLine = new String(buf);

			RequestLineParser reqLineParser = new RequestLineParser(reqLine);

			uri = reqLineParser.getUri();

			if (uri != null && uri.length() > 0)
			{
				try
				{
					IHttpResource resource = this._resourceResolver.getResource(reqLineParser);

					if (resource != null)
					{
						if (resource instanceof IHttpFolderResource)
						{
							IHttpFolderResource folderResource = (IHttpFolderResource) resource;

							if (!uri.endsWith("/")) //$NON-NLS-1$
							{
								// send a redirect to tell the browser to connect with a URL that includes
								// the trailing slash (required for relative resources to be properly resolved)
								this.sendRedirect(s, uri + "/"); //$NON-NLS-1$
							}
							else
							{
								String[] fileNames = folderResource.getFileNames();
								String[] folderNames = folderResource.getFolderNames();
								String folderHTML = HttpResponseUtils.createBrowseFolderHTML(new Path(uri), fileNames,
										folderNames);

								if (reqLineParser.getMethod().equals("GET")) //$NON-NLS-1$
								{
									this.sendContent(s, folderHTML, "text/html"); //$NON-NLS-1$
								}
								else if (reqLineParser.getMethod().equals("HEAD")) //$NON-NLS-1$
								{
									this.sendHeaders(s, "text/html"); //$NON-NLS-1$
								}
							}
						}
						else
						{
							InputStream contentInput = resource.getContentInputStream(this._server);
							long length = resource.getContentLength();
							String type = resource.getContentType();

							try
							{
								if (reqLineParser.getMethod().equals("GET")) //$NON-NLS-1$
								{
									this.sendFile(s, contentInput, length, type);
								}
								else if (reqLineParser.getMethod().equals("HEAD")) //$NON-NLS-1$
								{
									this.sendHeaders(s, type);
								}
							}
							finally
							{
								if ( contentInput != null ) {
									contentInput.close();
								}
							}
						}
					}
					else
					{
						throw new HttpServerException(404, "file not found", uri, "file not found: " + uri); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				catch (HttpServerException e)
				{
					this._logger.logTrace(e.getStatusMessage() + "-" + e.getRequestUri()); //$NON-NLS-1$
					sendError(s, e);
				}
			}
			else
			{
				this._logger.logTrace("bad request detected: " + reqLine != null ? reqLine : "request line was empty"); //$NON-NLS-1$ //$NON-NLS-2$
				sendError(s, new HttpServerException(400, "bad request", uri, null)); //$NON-NLS-1$
			}
		}
		catch (Exception e)
		{
			// this._logger.logError("error processing request: " + uri != null ? uri : "uri unavailable", e);
			sendError(s, new HttpServerException(500, "Internal error", uri, null, e)); //$NON-NLS-1$
			IdeLog.logError(ServerCorePlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}
		finally
		{
			if(s != null && !s.isClosed())
			{
				s.close();
			}
		}
	}

	/**
	 * makeHeaders
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, String> makeHeaders()
	{
		Hashtable<String, String> headers = new Hashtable<String, String>();

		headers.put("Server", "Aptana v0.2.7"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// add IE no-cache headers per
		// http://en.wikipedia.org/wiki/XMLHttpRequest#Microsoft_Internet_Explorer_cache_issues
		headers.put("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.put("Cache-Control", "no-store, no-cache, must-revalidate"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.put("Pragma", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zZ"); //$NON-NLS-1$
		headers.put("Last-Modified", format.format(new Date())); //$NON-NLS-1$

		return headers;
	}

	/**
	 * sendContent
	 * 
	 * @param s
	 * @param content
	 * @param contentType
	 * @throws IOException
	 */
	private void sendContent(Socket s, String content, String contentType) throws IOException
	{
		OutputStream output = s.getOutputStream();
		HttpResponse response = new HttpResponse(output);

		response.sendResponseContent(content);
	}

	/**
	 * @param s
	 * @throws IOException
	 */
	private void sendHeaders(Socket s, String contentType) throws IOException
	{
		OutputStream output = s.getOutputStream();
		HttpResponse response = new HttpResponse(output);

		response.sendReponseHeader(200, "OK", 0, contentType, this.makeHeaders()); //$NON-NLS-1$
	}

	/**
	 * sendFile
	 * 
	 * @param s
	 * @param fileInput
	 * @param contentLength
	 * @param contentType
	 * @throws IOException
	 */
	private void sendFile(Socket s, InputStream fileInput, long contentLength, String contentType) throws IOException
	{
		OutputStream output = s.getOutputStream();
		HttpResponse response = new HttpResponse(output);

		response.sendFileContent(fileInput, contentLength, contentType, this.makeHeaders());
	}

	/**
	 * sendError
	 * 
	 * @param s
	 * @param e
	 * @throws IOException
	 */
	private void sendError(Socket s, HttpServerException e) throws IOException
	{
		OutputStream output = s.getOutputStream();
		HttpResponse response = new HttpResponse(output);

		response.sendError(e);
	}

	/**
	 * sendRedirect
	 * 
	 * @param s
	 * @param newURL
	 * @throws IOException
	 */
	private void sendRedirect(Socket s, String newURL) throws IOException
	{
		OutputStream output = s.getOutputStream();
		HttpResponse response = new HttpResponse(output);
		Hashtable<String, String> headers = new Hashtable<String, String>();
		headers.put("location", StringUtils.urlEncodeForSpaces(newURL.toCharArray())); //$NON-NLS-1$
		response.sendReponseHeader(307, "redirect", 1, "text/html", headers); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
