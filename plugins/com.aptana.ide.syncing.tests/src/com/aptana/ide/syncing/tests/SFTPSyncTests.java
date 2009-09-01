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
package com.aptana.ide.syncing.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.NotConnectedException;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.core.io.sync.VirtualFileSyncPair;
import com.aptana.ide.core.ui.io.file.LocalProtocolManager;
import com.aptana.ide.io.sftp.SftpProtocolManager;
import com.aptana.ide.io.sftp.SftpVirtualFileManager;
import com.aptana.ide.syncing.Synchronizer;

/**
 * @author Kevin Lindsey
 */
public class SFTPSyncTests extends TestCase
{
	private static final String server = "build.aptana.com"; //$NON-NLS-1$
	private static final String user = "aptana_unit_tests"; //$NON-NLS-1$
	private static final String pass = System.getProperty("sftp.password"); //$NON-NLS-1$
	private static final String home = "/home/aptana_unit_tests/SFTPSyncTests_do_not_delete"; //$NON-NLS-1$

	/**
	 * testSync
	 * 
	 * @throws IOException
	 * @throws NotConnectedException
	 * @throws VirtualFileManagerException
	 * @throws ConnectionException
	 */
	public void testFilteredSync() throws IOException, NotConnectedException, ConnectionException, VirtualFileManagerException
	{
		syncFromRemote(new String[] {".*\\.ignoreme"}); //$NON-NLS-1$
	}

	/**
	 * testSync
	 * 
	 * @throws IOException
	 * @throws NotConnectedException
	 * @throws VirtualFileManagerException
	 * @throws ConnectionException
	 */
	public void testStraightSync() throws IOException, NotConnectedException, ConnectionException, VirtualFileManagerException
	{
		syncFromRemote(null);
	}

	/**
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	private void syncFromRemote(String[] filterExpression) throws IOException, ConnectionException,
			VirtualFileManagerException
	{
		// we'll use the local file system to make the testing a bit easier
		LocalProtocolManager protocolManager = new LocalProtocolManager();

		// setup the client file manager
		IVirtualFileManager clientManager = protocolManager.createFileManager();
		if (filterExpression != null)
		{
			for (int i = 0; i < filterExpression.length; i++)
			{
				String string = filterExpression[i];
				clientManager.addCloakExpression(string);				
			}
		}

		File baseTempFile = File.createTempFile("_test", null); //$NON-NLS-1$ 
		File baseClientDirectory = new File(baseTempFile.getParentFile().getCanonicalPath()
				+ "/" + baseTempFile.getName() + ".directory"); //$NON-NLS-1$ //$NON-NLS-2$
		baseClientDirectory.mkdirs();

		clientManager.setBasePath(baseClientDirectory.getAbsolutePath());
		IVirtualFile clientFile = clientManager.getBaseFile();

		SftpProtocolManager serverProtocolManager = new SftpProtocolManager();

		SftpVirtualFileManager serverManager = (SftpVirtualFileManager) serverProtocolManager.createFileManager();
		serverManager.setServer(server);
		serverManager.setUser(user);
		serverManager.setPassword(pass);
		serverManager.setBasePath(home);
		if (filterExpression != null)
		{
			for (int i = 0; i < filterExpression.length; i++)
			{
				String string = filterExpression[i];
				serverManager.addCloakExpression(string);				
			}
		}
		IVirtualFile serverFile = serverManager.getBaseFile();

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientFile, serverFile);
		Trace.info(StringUtils.format("{0} items to sync", items.length)); //$NON-NLS-1$

		syncManager.download(items);

		if (filterExpression != null)
		{
			File[] files = baseClientDirectory.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];
				for (int j = 0; j < filterExpression.length; j++)
				{
					String string = filterExpression[j];
					if (file.getAbsolutePath().matches(string))
					{
						fail(StringUtils.format("File {0} was download, but should have been filtered by expression {1}", //$NON-NLS-1$
								new String[] { file.getAbsolutePath(), string }));
					}
				}

			}
		}

		FileUtils.deleteDirectory(baseClientDirectory);
		baseTempFile.delete();
	}
}
