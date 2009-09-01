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
package com.aptana.ide.io.ftps.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.io.ftp.FtpVirtualFileManager;
import com.aptana.ide.io.ftps.FtpsProtocolManager;
import com.aptana.ide.io.ftps.FtpsVirtualFileManager;

/**
 * FtpVirtualFileManagerTest
 * 
 * @author Ingo Muschenetz
 */
public class FtpsVirtualFileManagerTest extends TestCase
{
	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.compareTo(Object)'
	 */
	public void testCompareTo()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.FtpVirtualFileManager(ProtocolManager)'
	 */
	public void testFtpVirtualFileManager()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getBaseFile()'
	 */
	public void testGetBaseFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getBasePath()'
	 */
	public void testGetBasePath()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setBasePath(String)'
	 */
	public void testSetBasePath()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.isConnected()'
	 */
	public void testIsConnected()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getFiles(IVirtualFile, boolean)'
	 */
	public void testGetFilesIVirtualFileBoolean()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.hasFiles(IVirtualFile)'
	 */
	public void testHasFiles()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getFileSeparator()'
	 */
	public void testGetFileSeparator()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getFileTimeString(IVirtualFile)'
	 */
	public void testGetFileTimeString()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getGroup(IVirtualFile)'
	 */
	public void testGetGroup()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setGroup(IVirtualFile, String)'
	 */
	public void testSetGroup()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getHashString()'
	 */
	public void testGetHashString()
	{
		// see below
	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.fromSerializableString(String)'
	 * 
	 * @throws ConnectionException
	 */
	public void testFromSerializableString() throws ConnectionException
	{
		IVirtualFileManager fileManager = FtpsProtocolManager.getInstance().createFileManager();
		FtpsVirtualFileManager ftp = (FtpsVirtualFileManager) fileManager;
		ftp.setNickName("nickname"); //$NON-NLS-1$
		ftp.setServer("server.host.name"); //$NON-NLS-1$
		ftp.setBasePath("/base/path"); //$NON-NLS-1$
		ftp.setUser("user"); //$NON-NLS-1$
		ftp.setPassword("password$$!!@@~~"); //$NON-NLS-1$
		ftp.setPassiveMode(!ftp.getPassiveMode());
		// ftp.setId(0); // Id is auto-calculated
		ftp.setAutoCalculateServerTimeOffset(!ftp.isAutoCalculateServerTimeOffset());
		ftp.setTimeOffset(100000); // requires a valid server, so we have to fake a value
		ftp.setPort(ftp.getPort() + 1);
		ftp.setExplicitMode(true);
		ftp.setSecurityType("SSL");
		IVirtualFile f1 = ftp.createVirtualFile("/test.txt"); //$NON-NLS-1$
		IVirtualFile f2 = ftp.createVirtualDirectory("/test_directory"); //$NON-NLS-1$
		ftp.addCloakedFile(f1);
		ftp.addCloakedFile(f2);

		ftp.addCloakExpression(".*\\.js"); //$NON-NLS-1$
		ftp.addCloakExpression("\\.svn"); //$NON-NLS-1$

		String serialized = ftp.getHashString();
		IVirtualFileManager fileManager2 = FtpsProtocolManager.getInstance().createFileManager();
		FtpsVirtualFileManager ftp2 = (FtpsVirtualFileManager) fileManager2;
		ftp2.fromSerializableString(serialized);

		String[] strings = serialized.split(FtpVirtualFileManager.DELIMITER);
		assertEquals(15, strings.length);

		assertEquals(ftp.getNickName(), ftp2.getNickName());
		assertEquals(ftp.getServer(), ftp2.getServer());
		assertEquals(ftp.getBasePath(), ftp2.getBasePath());
		assertEquals(ftp.getUser(), ftp2.getUser());
		assertEquals(ftp.getPassword(), ftp2.getPassword());
		assertEquals(ftp.isAutoCalculateServerTimeOffset(), ftp2.isAutoCalculateServerTimeOffset());
		assertEquals(ftp.getId(), ftp2.getId());
		assertEquals(ftp.getTimeOffset(), ftp2.getTimeOffset());
		assertEquals(ftp.getPort(), ftp2.getPort());
		assertEquals(ftp.getPassiveMode(), ftp2.getPassiveMode());
		assertEquals(ftp.getSavePassword(), ftp2.getSavePassword());
		assertEquals(ftp.getExplicitMode(), ftp2.getExplicitMode());
		assertEquals(ftp.getSecurityType(), ftp2.getSecurityType());
		assertEquals(2, ftp2.getCloakedFiles().length);
		assertEquals(2, ftp2.getCloakedFileExpressions().length);
	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getImage()'
	 */
	public void testGetImage()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setImage(Image)'
	 */
	public void testSetImage()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getDisabledImage()'
	 */
	public void testGetDisabledImage()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setDisabledImage(Image)'
	 */
	public void testSetDisabledImage()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getOwner(IVirtualFile)'
	 */
	public void testGetOwner()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setOwner(IVirtualFile, String)'
	 */
	public void testSetOwner()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getPassiveMode()'
	 */
	public void testGetPassiveMode()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setPassiveMode(boolean)'
	 */
	public void testSetPassiveMode()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getPassword()'
	 */
	public void testGetPassword()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setPassword(String)'
	 */
	public void testSetPassword()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getPort()'
	 */
	public void testGetPort()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setPort(int)'
	 */
	public void testSetPort()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getServer()'
	 */
	public void testGetServer()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setServer(String)'
	 */
	public void testSetServer()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getType()'
	 */
	public void testGetType()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getUser()'
	 */
	public void testGetUser()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setUser(String)'
	 */
	public void testSetUser()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.connect()'
	 */
	public void testConnect()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.containsFile(IVirtualFile)'
	 */
	public void testContainsFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.createLocalDirectory(IVirtualFile)'
	 */
	public void testCreateLocalDirectory()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.createVirtualDirectory(String)'
	 */
	public void testCreateVirtualDirectory()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.createVirtualFile(String)'
	 */
	public void testCreateVirtualFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.deleteFile(IVirtualFile)'
	 */
	public void testDeleteFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.disconnect()'
	 */
	public void testDisconnect()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getStream(IVirtualFile)'
	 */
	public void testGetStream()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.moveFile(IVirtualFile, IVirtualFile)'
	 */
	public void testMoveFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.putStream(InputStream, IVirtualFile)'
	 */
	public void testPutStream()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.putToLocalFile(IVirtualFile, File)'
	 */
	public void testPutToLocalFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.refresh()'
	 */
	public void testRefresh()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.renameFile(IVirtualFile, String)'
	 */
	public void testRenameFile()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.resolveBasePath()'
	 */
	public void testResolveBasePath()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.toSerializableString()'
	 */
	public void testToSerializableString()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.setModificationMillis(FtpVirtualFile, long)'
	 */
	public void testSetModificationMillis()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.exists(FtpVirtualFile)'
	 */
	public void testExists()
	{

	}
}
