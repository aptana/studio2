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
package com.aptana.ide.editors.junit.formatting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import com.aptana.ide.editor.css.CSSEditor;
import com.aptana.ide.editor.css.MultiPageCSSEditor;
import com.aptana.ide.editors.junit.ProjectTestUtils;
import com.aptana.ide.editors.unified.actions.CodeFormatAction;

/**
 * SelectionFormattingTests
 * 
 * @author Pavel Petrochenko
 */
public class SelectionFormattingTests extends TestCase
{

	public void testSTU4416() throws Exception
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		IProject project = ProjectTestUtils.createProject("css_formatting_test");
		String src = "body.* {\r\n\r\n  display: inline-table;\r\n}\r\n";
		InputStream source = new ByteArrayInputStream(src.getBytes());
		IFile file = project.getFile("file.css");
		file.create(source, true, new NullProgressMonitor());
		IEditorPart targetEditor = ProjectTestUtils.openInEditor(file, ProjectTestUtils.CSS_EDITOR_ID);
		MultiPageCSSEditor editor = (MultiPageCSSEditor) targetEditor;
		final StyledText text = editor.getViewer().getTextWidget();
		text.setCaretOffset(10);
		codeFormatAction.setActiveEditor(null, targetEditor);		
		codeFormatAction.run();
		assertEquals(10, text.getCaretOffset());
	}

	public void test1()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		String original = "tet"; //$NON-NLS-1$
		String removeTrailingReturnsIfNeeded = codeFormatAction.removeTrailingReturnsIfNeeded(original, "tet"); //$NON-NLS-1$ 
		assertEquals(original, removeTrailingReturnsIfNeeded);
	}

	public void test2()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		String original = "tet"; //$NON-NLS-1$
		String removeTrailingReturnsIfNeeded = codeFormatAction.removeTrailingReturnsIfNeeded(original, "tet\n\r"); //$NON-NLS-1$ 
		assertEquals(original, removeTrailingReturnsIfNeeded);
	}

	public void test3()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		String original = "tet\r\n"; //$NON-NLS-1$
		String removeTrailingReturnsIfNeeded = codeFormatAction.removeTrailingReturnsIfNeeded(original, original);
		assertEquals(original, removeTrailingReturnsIfNeeded);
	}

	public void test4()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document(""); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 0, false);
			assertEquals(iLevel, 0);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test5()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document(" b();\r\na();"); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 1, false);
			assertEquals(iLevel, 0);
			iLevel = codeFormatAction.calculateIndentation(dc, 0, false);
			assertEquals(iLevel, 1);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test6()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\tb();\r\na(){;"); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 0, false);
			assertEquals(iLevel, 4);
			iLevel = codeFormatAction.calculateIndentation(dc, 1, false);
			assertEquals(iLevel, 4);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test7()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\tb();\r\na(){;"); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 0, true);
			assertEquals(iLevel, 4);
			iLevel = codeFormatAction.calculateIndentation(dc, 1, true);
			assertEquals(iLevel, 0);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test8()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){};"); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 0, true);
			assertEquals(iLevel, 5);
			iLevel = codeFormatAction.calculateIndentation(dc, 1, false);
			assertEquals(iLevel, 0);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test9()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){}};"); //$NON-NLS-1$
		int iLevel;
		try
		{
			iLevel = codeFormatAction.calculateIndentation(dc, 1, false);
			assertEquals(iLevel, 0);
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test10()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){}};"); //$NON-NLS-1$
		String str = ""; //$NON-NLS-1$
		try
		{
			str = codeFormatAction.determineCorrectString(dc, 1);
			assertEquals(str, "a(){}};"); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test11()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){}}\r\n\r\n"); //$NON-NLS-1$
		String str = ""; //$NON-NLS-1$
		try
		{
			str = codeFormatAction.determineCorrectString(dc, 3);
			assertEquals(str, "a(){}}"); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test12()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){}\r\n/**\r\n**/}\r\n\r\n"); //$NON-NLS-1$
		String str = ""; //$NON-NLS-1$
		try
		{
			str = codeFormatAction.determineCorrectString(dc, 5);
			assertEquals(str, "**/}"); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}

	public void test13()
	{
		CodeFormatAction codeFormatAction = new CodeFormatAction();
		Document dc = new Document("\t b();\r\na(){}\r\n/**\r\n**/\r\n\r\n"); //$NON-NLS-1$
		String str = ""; //$NON-NLS-1$
		try
		{
			str = codeFormatAction.determineCorrectString(dc, 5);
			assertEquals(str, "a(){}"); //$NON-NLS-1$
		}
		catch (BadLocationException e)
		{
			assertTrue(false);
		}
	}
}
