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
package com.aptana.ide.editors.junit.partitions;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.ide.editor.css.CSSFileServiceFactory;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.html.HTMLFileServiceFactory;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.xml.XMLFileServiceFactory;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editors.junit.ProjectTestUtils;
import com.aptana.ide.editors.junit.TestProject;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileServiceFactory;

/**
 * @author Robin
 */
public class PartitionsTest extends TestCase
{

	public void testHTML() throws Exception
	{
		FileSourceProvider sourceProvider = getFileSourceProvider("partitionTestFiles/partitionTest.html");
		IFileServiceFactory fileServiceFactory = HTMLFileServiceFactory.getInstance();
		IFileService fileService = fileServiceFactory.createFileService(sourceProvider);
		ITypedRegion[] partitions = fileService.getPartitions();

		assertEquals(partitions[0].getOffset(), 0);
		assertEquals(partitions[0].getLength(), 228);
		assertEquals(partitions[0].getType(), HTMLMimeType.MimeType);

		assertEquals(partitions[1].getOffset(), 228);
		assertEquals(partitions[1].getLength(), 4);
		assertEquals(partitions[1].getType(), JSMimeType.MimeType);

		assertEquals(partitions[2].getOffset(), 232);
		assertEquals(partitions[2].getLength(), 20);
		assertEquals(partitions[2].getType(), ScriptDocMimeType.MimeType);

		assertEquals(partitions[3].getOffset(), 252);
		assertEquals(partitions[3].getLength(), 29);
		assertEquals(partitions[3].getType(), JSMimeType.MimeType);

		assertEquals(partitions[4].getOffset(), 281);
		assertEquals(partitions[4].getLength(), 13);
		assertEquals(partitions[4].getType(), JSCommentMimeType.MimeType);

		assertEquals(partitions[5].getOffset(), 294);
		assertEquals(partitions[5].getLength(), 20);
		assertEquals(partitions[5].getType(), JSMimeType.MimeType);

		assertEquals(partitions[6].getOffset(), 314);
		assertEquals(partitions[6].getLength(), 17);
		assertEquals(partitions[6].getType(), JSCommentMimeType.MimeType);

		assertEquals(partitions[7].getOffset(), 331);
		assertEquals(partitions[7].getLength(), 36);
		assertEquals(partitions[7].getType(), JSMimeType.MimeType);

		assertEquals(partitions[8].getOffset(), 367);
		assertEquals(partitions[8].getLength(), 19);
		assertEquals(partitions[8].getType(), HTMLMimeType.MimeType);

		assertEquals(partitions[9].getOffset(), 386);
		assertEquals(partitions[9].getLength(), 481);
		assertEquals(partitions[9].getType(), CSSMimeType.MimeType);

		assertEquals(partitions[10].getOffset(), 867);
		assertEquals(partitions[10].getLength(), 43);
		assertEquals(partitions[10].getType(), HTMLMimeType.MimeType);

		assertEquals(partitions[11].getOffset(), 910);
		assertEquals(partitions[11].getLength(), 24);
		assertEquals(partitions[11].getType(), JSMimeType.MimeType);

		assertEquals(partitions[12].getOffset(), 934);
		assertEquals(partitions[12].getLength(), 27);
		assertEquals(partitions[12].getType(), HTMLMimeType.MimeType);
	}

	public void testXML() throws Exception
	{
		FileSourceProvider sourceProvider = getFileSourceProvider("partitionTestFiles/partitionTest.xml");
		IFileServiceFactory fileServiceFactory = XMLFileServiceFactory.getInstance();
		IFileService fileService = fileServiceFactory.createFileService(sourceProvider);
		ITypedRegion[] partitions = fileService.getPartitions();
		// xml has no sublanguages
		assertEquals(partitions[0].getOffset(), 0);
		assertEquals(partitions[0].getLength(), 2089);
		assertEquals(partitions[0].getType(), XMLMimeType.MimeType);
	}

	public void testCSS() throws Exception
	{
		FileSourceProvider sourceProvider = getFileSourceProvider("partitionTestFiles/partitionTest.css");
		IFileServiceFactory fileServiceFactory = CSSFileServiceFactory.getInstance();
		IFileService fileService = fileServiceFactory.createFileService(sourceProvider);
		ITypedRegion[] partitions = fileService.getPartitions();
		// this will change when css comments are added as a language
		assertEquals(0, partitions[0].getOffset());
		assertEquals(1064, partitions[0].getLength());
		assertEquals(CSSMimeType.MimeType, partitions[0].getType());
	}

	public void testJS() throws Exception
	{
		FileSourceProvider sourceProvider = getFileSourceProvider("partitionTestFiles/partitionTest.js");
		IFileServiceFactory fileServiceFactory = JSFileServiceFactory.getInstance();
		IFileService fileService = fileServiceFactory.createFileService(sourceProvider);
		ITypedRegion[] partitions = fileService.getPartitions();
		assertEquals(partitions[0].getOffset(), 0);
		assertEquals(partitions[0].getLength(), 0);
		assertEquals(partitions[0].getType(), JSMimeType.MimeType);

		assertEquals(partitions[1].getOffset(), 0);
		assertEquals(partitions[1].getLength(), 14);
		assertEquals(partitions[1].getType(), ScriptDocMimeType.MimeType);

		assertEquals(partitions[2].getOffset(), 14);
		assertEquals(partitions[2].getLength(), 20);
		assertEquals(partitions[2].getType(), JSMimeType.MimeType);

		assertEquals(partitions[3].getOffset(), 34);
		assertEquals(partitions[3].getLength(), 13);
		assertEquals(partitions[3].getType(), JSCommentMimeType.MimeType);

		assertEquals(partitions[4].getOffset(), 47);
		assertEquals(partitions[4].getLength(), 14);
		assertEquals(partitions[4].getType(), JSMimeType.MimeType);

		assertEquals(partitions[5].getOffset(), 61);
		assertEquals(partitions[5].getLength(), 17);
		assertEquals(partitions[5].getType(), JSCommentMimeType.MimeType);

		assertEquals(partitions[6].getOffset(), 78);
		assertEquals(partitions[6].getLength(), 26);
		assertEquals(partitions[6].getType(), JSMimeType.MimeType);
	}

	private FileSourceProvider getFileSourceProvider(String pathToFile)
	{
		IPath path = ProjectTestUtils.findFileInPlugin(TestProject.PLUGIN_ID, pathToFile);
		if (path == null)
			fail("Unable to find " + pathToFile);
		return new FileSourceProvider(path.toFile());
	}
}
