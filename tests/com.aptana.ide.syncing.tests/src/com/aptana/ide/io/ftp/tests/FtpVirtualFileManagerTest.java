/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.io.ftp.tests;

import junit.framework.TestCase;

/**
 * FtpVirtualFileManagerTest
 * 
 * @author Ingo Muschenetz
 */
public class FtpVirtualFileManagerTest extends TestCase
{
//	
//	/**
//	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.getHashString()'
//	 */
//	public void testGetHashString()
//	{
//		// see below
//	}
//
//	/**
//	 * Test method for 'com.aptana.ide.io.ftp.FtpVirtualFileManager.fromSerializableString(String)'
//	 * 
//	 * @throws ConnectionException
//	 */
//	public void testFromSerializableString() throws ConnectionException
//	{
//		IVirtualFileManager fileManager = FtpProtocolManager.getInstance().createFileManager();
//		FtpVirtualFileManager ftp = (FtpVirtualFileManager) fileManager;
//		ftp.setNickName("nickname"); //$NON-NLS-1$
//		ftp.setServer("server.host.name"); //$NON-NLS-1$
//		ftp.setBasePath("/base/path"); //$NON-NLS-1$
//		ftp.setUser("user"); //$NON-NLS-1$
//		ftp.setPassword("password$$!!@@~~"); //$NON-NLS-1$
//		ftp.setPassiveMode(!ftp.getPassiveMode());
//		// ftp.setId(0); // Id is auto-calculated
//		ftp.setAutoCalculateServerTimeOffset(!ftp.isAutoCalculateServerTimeOffset());
//		ftp.setTimeOffset(100000); // requires a valid server, so we have to fake a value
//		ftp.setPort(ftp.getPort() + 1);
//
//		IVirtualFile f1 = ftp.createVirtualFile("/test.txt"); //$NON-NLS-1$
//		IVirtualFile f2 = ftp.createVirtualDirectory("/test_directory"); //$NON-NLS-1$
//		ftp.addCloakedFile(f1);
//		ftp.addCloakedFile(f2);
//
//		ftp.addCloakExpression(".*\\.js"); //$NON-NLS-1$
//		ftp.addCloakExpression("\\.svn"); //$NON-NLS-1$
//
//		String serialized = ftp.getHashString();
//		IVirtualFileManager fileManager2 = FtpProtocolManager.getInstance().createFileManager();
//		FtpVirtualFileManager ftp2 = (FtpVirtualFileManager) fileManager2;
//		ftp2.fromSerializableString(serialized);
//
//		String[] strings = serialized.split(FtpVirtualFileManager.DELIMITER);
//		assertEquals(13, strings.length);
//
//		assertEquals(ftp.getNickName(), ftp2.getNickName());
//		assertEquals(ftp.getServer(), ftp2.getServer());
//		assertEquals(ftp.getBasePath(), ftp2.getBasePath());
//		assertEquals(ftp.getUser(), ftp2.getUser());
//		assertEquals(ftp.getPassword(), ftp2.getPassword());
//		assertEquals(ftp.isAutoCalculateServerTimeOffset(), ftp2.isAutoCalculateServerTimeOffset());
//		assertEquals(ftp.getId(), ftp2.getId());
//		assertEquals(ftp.getTimeOffset(), ftp2.getTimeOffset());
//		assertEquals(ftp.getPort(), ftp2.getPort());
//		assertEquals(ftp.getPassiveMode(), ftp2.getPassiveMode());
//		assertEquals(ftp.getSavePassword(), ftp2.getSavePassword());
//
//		assertEquals(ftp.getCloakedFiles().length, ftp2.getCloakedFiles().length);
//		
//		List<String> al = Arrays.asList(ftp.getCloakedFileExpressions());
//		assertTrue(al.contains(".*\\.js"));
//		assertTrue(al.contains("\\.svn"));
//	}

}
