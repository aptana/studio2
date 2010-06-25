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
package com.aptana.ide.editor.js.tests;

//package com.aptana.ide.editors.junit.js;
//
//import java.io.File;
//
//import org.eclipse.jface.text.Document;
//import org.eclipse.jface.text.ITypedRegion;
//
//import com.aptana.ide.js.core.parser.IJSPartitions;
//import com.aptana.ide.js.core.parser.JSEnvironment;
//import com.aptana.ide.js.core.parser.JSFileEnvironment;
//import com.aptana.ide.js.core.parser.JSFileEnvironment.PartitionInfo;
//
//import junit.framework.TestCase;
//
//public class testJSPartitions extends TestCase
//{
//	JSEnvironment jse;
//	JSFileEnvironment jsfe;
//	Document doc;
//	String source1 = "/*abc*/\r\nvar x = 5;\r\n/**def*/";
//	String source2 = "/*abc*//**\r\nnewline*/";
//	String source3 = "//abc";
//	String source4 = "/**/\r\n//def\nvar x = 5;//";
//
//	public static void main(String[] args)
//	{
//		junit.textui.TestRunner.run(testJSPartitions.class);
//	}
//
//	public testJSPartitions(String name)
//	{
//		super(name);
//		File f = new File("test.js");
//		JSEnvironment.includeCore = false;
//		JSEnvironment.includeHtml = false;
//		jse = new JSEnvironment("test");
//		jsfe = jse.getJSFileEnvironment(f, null);
//		jsfe.setHandleErrors(false);
//		
//		doc = new Document();
//	}
//
//	protected void setUp() throws Exception
//	{
//		super.setUp();
//	}
//
//	protected void tearDown() throws Exception
//	{
//		super.tearDown();
//	}
//	public void testSource1()
//	{
//		// [__js_multiline_comment	0 ~ 8, __js_code	8 ~ 21, __js_jdoc	21 ~ 30, __js_code	30 ~ 30]
//		doc.set(source1);
//		jsfe.setSourceDocument(doc, 0, source1, 0);
//		PartitionInfo pi = jsfe.getPartitionInfo();
//		ITypedRegion[] partitions = pi.partitions;
//
//		assertEquals(partitions.length, 4);
//		assertPartitionContainsNoGaps(partitions);
//		
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_MULTILINE_COMMENT);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 8);
//		
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[1].getOffset(), 8);
//		assertEquals(partitions[1].getLength(), 13);
//		
//		assertEquals(partitions[2].getType(), IJSPartitions.JS_JDOC);
//		assertEquals(partitions[2].getOffset(), 21);
//		assertEquals(partitions[2].getLength(), 9);
//		
//		assertEquals(partitions[3].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[3].getOffset(), 30);
//		assertEquals(partitions[3].getLength(), 0);
//	}
//	public void testSource2()
//	{
//		// [__js_multiline_comment	0 ~ 8, __js_code	8 ~ 8, __js_jdoc	8 ~ 22, __js_code	22 ~ 22]
//		doc.set(source2);
//		jsfe.setSourceDocument(doc, 0, source2, 0);
//		PartitionInfo pi = jsfe.getPartitionInfo();
//		ITypedRegion[] partitions = pi.partitions;
//
//		assertEquals(partitions.length, 4);
//		assertPartitionContainsNoGaps(partitions);
//		
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_MULTILINE_COMMENT);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 8);
//		
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[1].getOffset(), 8);
//		assertEquals(partitions[1].getLength(), 0);
//		
//		assertEquals(partitions[2].getType(), IJSPartitions.JS_JDOC);
//		assertEquals(partitions[2].getOffset(), 8);
//		assertEquals(partitions[2].getLength(), 14);
//		
//		assertEquals(partitions[3].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[3].getOffset(), 22);
//		assertEquals(partitions[3].getLength(), 0);
//	}
//	public void testSource3()
//	{
//		// [__js_comment	0 ~ 6, __js_code	6 ~ 6]
//		doc.set(source3);
//		jsfe.setSourceDocument(doc, 0, source3, 0);
//		PartitionInfo pi = jsfe.getPartitionInfo();
//		ITypedRegion[] partitions = pi.partitions;
//
//		assertEquals(partitions.length, 2);
//		assertPartitionContainsNoGaps(partitions);
//		
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_COMMENT);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 6);
//		
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[1].getOffset(), 6);
//		assertEquals(partitions[1].getLength(), 0);
//	}
//
//	public void testSource4()
//	{
//		// [__js_multiline_comment	0 ~ 5, __js_comment	5 ~ 12, __js_code	12 ~ 22,
//		// __js_comment	22 ~ 25, __js_code	25 ~ 25]
//		doc.set(source4);
//		
//		jsfe.setSourceDocument(doc, 0, source4, 0);
//		PartitionInfo pi = jsfe.getPartitionInfo();
//		ITypedRegion[] partitions = pi.partitions;
//
//		assertEquals(partitions.length, 5);
//		assertPartitionContainsNoGaps(partitions);
//
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_MULTILINE_COMMENT);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 5);
//
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_COMMENT);
//		assertEquals(partitions[1].getOffset(), 5);
//		assertEquals(partitions[1].getLength(), 7);
//		
//		assertEquals(partitions[2].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[2].getOffset(), 12);
//		assertEquals(partitions[2].getLength(), 10);
//		
//		assertEquals(partitions[3].getType(), IJSPartitions.JS_COMMENT);
//		assertEquals(partitions[3].getOffset(), 22);
//		assertEquals(partitions[3].getLength(), 3);
//		
//		assertEquals(partitions[4].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[4].getOffset(), 25);
//		assertEquals(partitions[4].getLength(), 0);
//	}
//	
//	
//	public void testDeleteAndInsert()
//	{
//		String src = "/*abc*/\r\nvar x = 5;\r\n/**def*/";
//		String edit = "/**abc*/\r\nvar x = 5;\r\n/**def*/";
//		
//		// delete everything
//		// [__js_code	0 ~ 0]
//		doc.set(StringUtils.EMPTY);
//		jsfe.setSourceDocument(doc, 0, StringUtils.EMPTY, doc.getLength());
//		PartitionInfo pi = jsfe.getPartitionInfo();
//		ITypedRegion[] partitions = pi.partitions;
//
//		assertEquals(partitions.length, 1);
//		assertPartitionContainsNoGaps(partitions);
//		
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 0);
//		
//		// add src
//		// [__js_multiline_comment	0 ~ 8, __js_code	8 ~ 21, __js_jdoc	21 ~ 20, __js_code	30 ~ 30]
//		doc.set(src);
//		jsfe.setSourceDocument(doc, 0, src, 0);
//		pi = jsfe.getPartitionInfo();
//		partitions = pi.partitions;
//
//		assertEquals(partitions.length, 4);
//		assertPartitionContainsNoGaps(partitions);
//
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_MULTILINE_COMMENT);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 8);
//		
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[1].getOffset(), 8);
//		assertEquals(partitions[1].getLength(), 13);
//		
//		assertEquals(partitions[2].getType(), IJSPartitions.JS_JDOC);
//		assertEquals(partitions[2].getOffset(), 21);
//		assertEquals(partitions[2].getLength(), 9);
//		
//		assertEquals(partitions[3].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[3].getOffset(), 30);
//		assertEquals(partitions[3].getLength(), 0);
//		
//
//		// apply edit
//		// [__js_jdoc	0 ~ 9, __js_code	9 ~ 22, __js_jdoc	22 ~ 31, __js_code	31 ~ 31]
//		doc.set(edit);
//		jsfe.setSourceDocument(doc, 0, "/**", 2);
//		pi = jsfe.getPartitionInfo();
//		partitions = pi.partitions;
//
//		assertEquals(partitions.length, 4);
//		assertPartitionContainsNoGaps(partitions);
//		
//		assertEquals(partitions[0].getType(), IJSPartitions.JS_JDOC);
//		assertEquals(partitions[0].getOffset(), 0);
//		assertEquals(partitions[0].getLength(), 9);
//		
//		assertEquals(partitions[1].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[1].getOffset(), 9);
//		assertEquals(partitions[1].getLength(), 13);
//		
//		assertEquals(partitions[2].getType(), IJSPartitions.JS_JDOC);
//		assertEquals(partitions[2].getOffset(), 22);
//		assertEquals(partitions[2].getLength(), 9);
//		
//		assertEquals(partitions[3].getType(), IJSPartitions.JS_CODE);
//		assertEquals(partitions[3].getOffset(), 31);
//		assertEquals(partitions[3].getLength(), 0);
//	}
//
//	private void assertPartitionContainsNoGaps(ITypedRegion[] partitions)
//	{
//		for(int i = 0; i < partitions.length - 1; i++)
//		{
//			assertEquals(partitions[i + 1].getOffset(), partitions[i].getOffset() + partitions[i].getLength());
//		}
//	}
//}
