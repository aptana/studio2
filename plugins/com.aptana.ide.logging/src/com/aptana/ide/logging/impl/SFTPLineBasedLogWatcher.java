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
package com.aptana.ide.logging.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.io.IDisconnectableConnection;
import com.aptana.ide.logging.LoggingPlugin;

/**
 * Line-based log watcher for SFTP files.
 * 
 * @author Chris Williams
 */
public class SFTPLineBasedLogWatcher extends LineBasedLogWatcher
{

	/**
	 * SFTPLineBasedLogWatcher constructor.
	 * 
	 * @param config
	 * @param resource
	 */
	public SFTPLineBasedLogWatcher(WebLogResource resource, LogWatcherConfiguration config)
	{
		super(config, resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getCurrentLogLength() throws IOException
	{
		URLConnection connection = getUrl().openConnection();
		try
		{
			connection.connect();
			return connection.getContentLength();
		}
		catch (ConnectException ex)
		{
			throw ex;
		}
		finally
		{
			if (connection != null && connection instanceof IDisconnectableConnection)
				((IDisconnectableConnection) connection).disconnect();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readData(int startPos, ByteBuffer buffer, int maxBytesToRead) throws IOException
	{
		// FIXME Re-use one connection and only close it when close() is called on log watcher?
		URLConnection connection = getUrl().openConnection();
		connection.setDoInput(true);
		InputStream stream = null;
		try
		{
			stream = (InputStream) connection.getContent();
			if (startPos > 1)
			{ // we can't ask for a specific byte range like HTTP, so just skip until we reach segment we want.
				stream.skip(startPos - 1);
			}
			int gotBytes = 0;
			byte[] to = buffer.array();

			while (true)
			{
				int got = stream.read(to, gotBytes, maxBytesToRead - gotBytes);
				gotBytes += got;
				if (got == -1 || gotBytes == maxBytesToRead)
				{
					break;
				}
			}
			buffer.limit(gotBytes);
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				throw e;
			}
			finally
			{
				if (connection != null && connection instanceof IDisconnectableConnection)
					((IDisconnectableConnection) connection).disconnect();
			}
		}
	}

	/**
	 * Gets URL.
	 * 
	 * @return URL
	 */
	protected URL getUrl()
	{
		try
		{
			return getResource().getURI().toURL();
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(LoggingPlugin.getDefault(), e.getMessage(), e);
			return null;
		}
	}

}
