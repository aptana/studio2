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
package com.aptana.ide.editor.html.tests;

/*
 * package com.aptana.ide.editors.junit.html; import java.io.File; import java.io.IOException;
 * import java.io.InputStream; import java.net.URI; import java.net.URISyntaxException; import
 * java.net.URL; import java.net.URLConnection; import java.util.zip.ZipFile; import
 * java.util.zip.ZipInputStream; import org.eclipse.core.runtime.IPath; import
 * org.eclipse.core.runtime.Path; import org.eclipse.jface.text.IDocument; import
 * org.eclipse.jface.text.IDocumentExtension3; import org.eclipse.jface.text.IDocumentPartitioner;
 * import org.eclipse.jface.text.ITypedRegion; import
 * org.eclipse.osgi.framework.adaptor.core.BundleEntry.ZipBundleEntry; import
 * org.eclipse.osgi.framework.internal.core.BundleURLConnection; import org.eclipse.ui.IEditorPart;
 * import org.eclipse.ui.IWorkbenchWindow; import org.eclipse.ui.PartInitException; import
 * org.eclipse.ui.PlatformUI; import com.aptana.ide.core.ui.WorkbenchHelper; import
 * com.aptana.ide.editors.html.HTMLEditor; import com.aptana.ide.editors.html.HTMLEditorPart; import
 * com.aptana.ide.editors.html.HTMLJSPartioner; import com.aptana.ide.editors.html.HTMLSourceEditor;
 * import com.aptana.ide.editors.junit.EditorsJunitPlugin; import
 * com.aptana.ide.js.core.parser.IHTMLPartitions; import
 * com.aptana.ide.js.core.parser.IJSPartitions; import com.aptana.ide.js.ui.editor.EditorHelper;
 * import com.aptana.ide.js.ui.editor.JSEditor; import junit.framework.TestCase; public class
 * HTMLJSTest extends TestCase { private ITypedRegion[] partitions; public HTMLJSTest(String name)
 * throws PartInitException { super(name); // String result = ""; // for(int i = 0; i <
 * partitions.length; i++) // { // ITypedRegion r = partitions[i]; // int end = r.getOffset() +
 * r.getLength(); // result += "\n" + r.getType() + "\t" + // r.getOffset() + " ~ " + // end; // } }
 * protected void setUp() throws Exception { IEditorPart editorPart =
 * EditorsJunitPlugin.getDefault().openProjectFile("project-files", new Path("test1.html"));
 * HTMLEditor editor = (HTMLEditor)editorPart; IDocument doc =
 * editor.getPaletteTarget().getDocumentProvider().getDocument(editor.getEditorInput());
 * IDocumentExtension3 doc3 = (IDocumentExtension3)doc; HTMLJSPartioner partitioner =
 * (HTMLJSPartioner)doc3.getDocumentPartitioner(HTMLSourceEditor.HTML_PARTITIONING); partitions =
 * partitioner.getPartitions(); } protected void tearDown() throws Exception { } public void
 * testTest1_html() { // __html_tag 0 ~ 6 // __dftl_partition_content_type 6 ~ 8 //
 * __html_script_expression 8 ~ 16 // __js_code 16 ~ 30 // __html_script_expression 30 ~ 39 //
 * __dftl_partition_content_type 39 ~ 41 // __html_tag 41 ~ 48 assertEquals(partitions.length, 7);
 * assertPartitionContainsNoGaps(partitions); assertEquals(partitions[0].getType(),
 * IHTMLPartitions.HTML_TAG); assertEquals(partitions[0].getOffset(), 0);
 * assertEquals(partitions[0].getLength(), 6); assertEquals(partitions[1].getType(),
 * IDocument.DEFAULT_CONTENT_TYPE); assertEquals(partitions[1].getOffset(), 6);
 * assertEquals(partitions[1].getLength(), 2); assertEquals(partitions[2].getType(),
 * IHTMLPartitions.HTML_SCRIPT_EXPRESSION); assertEquals(partitions[2].getOffset(), 8);
 * assertEquals(partitions[2].getLength(), 8); assertEquals(partitions[3].getType(),
 * IJSPartitions.JS_CODE); assertEquals(partitions[3].getOffset(), 16);
 * assertEquals(partitions[3].getLength(), 14); assertEquals(partitions[4].getType(),
 * IHTMLPartitions.HTML_SCRIPT_EXPRESSION); assertEquals(partitions[4].getOffset(), 30);
 * assertEquals(partitions[4].getLength(), 9); assertEquals(partitions[5].getType(),
 * IDocument.DEFAULT_CONTENT_TYPE); assertEquals(partitions[5].getOffset(), 39);
 * assertEquals(partitions[5].getLength(), 2); assertEquals(partitions[6].getType(),
 * IHTMLPartitions.HTML_TAG); assertEquals(partitions[6].getOffset(), 41);
 * assertEquals(partitions[6].getLength(), 7); } private void
 * assertPartitionContainsNoGaps(ITypedRegion[] partitions) { for(int i = 0; i < partitions.length -
 * 1; i++) { assertEquals(partitions[i + 1].getOffset(), partitions[i].getOffset() +
 * partitions[i].getLength()); } } private static InputStream getResource(String name) { String
 * fullName = "/com/aptana/ide/editors/testFiles/" + name; InputStream stream =
 * HTMLJSTest.class.getResourceAsStream(fullName); return stream; } }
 */
