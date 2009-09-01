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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Line-based log watcher for web files.
 * @author Denis Denisenko
 */
public class WebLineBasedLogWatcher extends LineBasedLogWatcher
{

	/**
	 * WebLineBasedLogWatcher constructor.
	 * @param config
	 * @param resource
	 */
	public WebLineBasedLogWatcher(
			WebLogResource resource, LogWatcherConfiguration config)
	{
		super(config, resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getCurrentLogLength() throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) getUrl().openConnection();
		connection.setRequestMethod("HEAD"); //$NON-NLS-1$
		try
		{
			connection.connect();
		}
		catch (ConnectException ex)
		{
			throw ex;
		}
		finally
		{
			connection.disconnect();
		}
		
		int result;
		try
		{
			result = connection.getContentLength();
		}
		finally
		{
			connection.disconnect();
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readData(int startPos, ByteBuffer buffer, int maxBytesToRead) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) getUrl().openConnection();
		connection.setRequestMethod("GET"); //$NON-NLS-1$
		connection.setRequestProperty("Range",  //$NON-NLS-1$
				"bytes=" + startPos + "-" + (startPos + maxBytesToRead)); //$NON-NLS-1$ //$NON-NLS-2$
		connection.setDoInput(true);
		connection.setFollowRedirects(true);
		
		try
		{
			connection.connect();
		}
		catch(ConnectException ex)
		{
			throw ex;
		}
		finally
		{
			connection.disconnect();
		}

		try
		{
			InputStream stream = (InputStream) connection.getContent();
			int gotBytes = 0;
			byte[] to = buffer.array();
			
			while(true)
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
			connection.disconnect();
		}
	}
	
	/**
	 * Gets URL.
	 * @return URL
	 */
	protected URL getUrl()
	{
		try
		{
			return getResource().getURI().toURL();
		} catch (MalformedURLException e)
		{
			return null;
		}
	}

}
