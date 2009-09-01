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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.io.file.LocalFileManager;
import com.aptana.ide.io.ftp.FtpVirtualFileManager;
import com.aptana.ide.syncing.importing.Dreamweaver8SiteImporter;

/**
 * DreamweaverImporterTest
 */
public class DreamweaverImporterTest extends TestCase
{
	private Dreamweaver8SiteImporter dwi;

	// this file is local so I don't check my password etc into a public perforce repository : )
	// it is the exported ste file from dreamweaver 8
	private String fileName = "/com/aptana/ide/syncing/tests/DreamweaverImporterTest.ste"; //$NON-NLS-1$

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		dwi = new Dreamweaver8SiteImporter();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
	}

	/**
	 * testImport
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	public void testImport() throws ParserConfigurationException, SAXException, IOException, Exception
	{
		InputStream input = this.getClass().getResourceAsStream(fileName);
		dwi.read(input);

		LocalFileManager lm = (LocalFileManager) dwi.getLocalManager();
		FtpVirtualFileManager rm = (FtpVirtualFileManager) dwi.getRemoteManager();

		assertFalse(rm.getPassiveMode());
		assertEquals(StringUtils.EMPTY, rm.getBaseFile().getPath());
		assertEquals("ultraleap.com", rm.getServer()); //$NON-NLS-1$
		assertEquals("pcolton", rm.getUser()); //$NON-NLS-1$
		assertEquals(rm.getPort(), 21);

		assertEquals("Photoleap", lm.getNickName()); //$NON-NLS-1$
		assertEquals(
				"C:\\Documents and Settings\\Robin\\Application Data\\Macromedia\\Dreamweaver 8\\Configuration\\ServerConnections\\Photoleap", //$NON-NLS-1$
				lm.getBaseFile().getPath());

		String pw1a = "61636567696B"; //$NON-NLS-1$
		String pw2a = "4D317A59282937"; //$NON-NLS-1$
		String pw3a = "31333537393B"; //$NON-NLS-1$
		String pw4a = "40252626255F80"; //$NON-NLS-1$

		String pw1b = "abcdef"; //$NON-NLS-1$
		String pw2b = "M0xV$$1"; //$NON-NLS-1$
		String pw3b = "123456"; //$NON-NLS-1$
		String pw4b = "@$$#!Zz"; //$NON-NLS-1$

		// oop, intl chars don't work
		// but apparently they don't work in dreamweaver either?!
		// note the last 2
		// String intl0 = "йфи0DD"; // FFFFFF334849
		// String intl1 = "ииии"; // FFFFFFFFFFFFEB
		// String intl2 = "ииий"; // FFFFFFFFFFFFEC
		// String intl3 = "ииик"; // FFFFFFFFFFFFED
		// String intl4 = "кккк"; // FFFFFFFFFFFFED

		assertEquals(pw1b, Dreamweaver8SiteImporter.decryptDWPassword(pw1a));
		assertEquals(pw2b, Dreamweaver8SiteImporter.decryptDWPassword(pw2a));
		assertEquals(pw3b, Dreamweaver8SiteImporter.decryptDWPassword(pw3a));
		assertEquals(pw4b, Dreamweaver8SiteImporter.decryptDWPassword(pw4a));

		// assertTrue(pw4b.equals(Dreamweaver8SiteImporter.decryptDWPassword(pw4a)));

	}
}
