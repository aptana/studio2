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
package com.aptana.ide.editors.unified.errors.tests;

import org.eclipse.core.resources.IMarker;

import com.aptana.ide.editors.unified.errors.ErrorDescriptor;
import com.aptana.ide.editors.unified.errors.FileError;

import junit.framework.TestCase;

/**
 * ErrorDescriptorTest
 * 
 * @author Ingo Muschenetz
 */
public class ErrorDescriptorTest extends TestCase
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
	 * Test method for 'com.aptana.ide.editors.unified.errors.ErrorDescriptor.hashCode()'
	 */
	public void testHashCode()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.errors.ErrorDescriptor.toString()'
	 */
	public void testToFromString()
	{
		ErrorDescriptor ed1 = new ErrorDescriptor();
		ErrorDescriptor ed2 = new ErrorDescriptor();

		ed2.fromString(ed1.toString());

		assertEquals(ed1.getFileName(), ed2.getFileName());
		assertEquals(ed1.getFolderPath(), ed2.getFolderPath());
		assertEquals(ed1.getSeverity(), ed2.getSeverity());
		assertEquals(ed1.getMessage(), ed2.getMessage());

		assertEquals(ed1, ed2);

		ed1.setFileName("fileName"); //$NON-NLS-1$
		ed1.setFolderPath("folderPath"); //$NON-NLS-1$
		ed1.setSeverity(IMarker.SEVERITY_INFO);
		ed1.setMessage("message"); //$NON-NLS-1$

		ed2.fromString(ed1.toString());

		assertEquals(ed1.getFileName(), ed2.getFileName());
		assertEquals(ed1.getFolderPath(), ed2.getFolderPath());
		assertEquals(ed1.getSeverity(), ed2.getSeverity());
		assertEquals(ed1.getMessage(), ed2.getMessage());

		assertEquals(ed1, ed2);
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.errors.ErrorDescriptor.serializeErrorDescriptors(ErrorDescriptor[])'
	 */
	public void testSerializeDeserializeErrorDescriptors()
	{

		ErrorDescriptor ed1 = new ErrorDescriptor();
		ErrorDescriptor ed2 = new ErrorDescriptor();

		ed1.setFileName("fileName"); //$NON-NLS-1$
		ed1.setFolderPath("folderPath"); //$NON-NLS-1$
		ed1.setSeverity(IMarker.SEVERITY_INFO);
		ed1.setMessage("message"); //$NON-NLS-1$

		String errors = ErrorDescriptor.serializeErrorDescriptors(new ErrorDescriptor[] { ed1, ed2 });
		ErrorDescriptor[] errorDescriptors = ErrorDescriptor.deserializeErrorDescriptors(errors);
		assertEquals(ed1, errorDescriptors[0]);
		assertEquals(ed2, errorDescriptors[1]);

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.errors.ErrorDescriptor.matchesError(FileError)'
	 */
	public void testMatchesError()
	{
		ErrorDescriptor ed1 = new ErrorDescriptor();
		ed1.setFileName("fileName"); //$NON-NLS-1$
		ed1.setFolderPath("folderPath"); //$NON-NLS-1$
		ed1.setSeverity(IMarker.SEVERITY_INFO);
		ed1.setMessage("message"); //$NON-NLS-1$

		FileError fe1 = new FileError();
		fe1.setFileName("fileName"); //$NON-NLS-1$
		fe1.setFolderPath("folderPath"); //$NON-NLS-1$
		fe1.setSeverity(IMarker.SEVERITY_INFO);
		fe1.setMessage("message"); //$NON-NLS-1$

		assertTrue(ed1.matchesError(fe1));
		ed1.setSeverity(ErrorDescriptor.NO_SEVERITY);
		assertTrue(ed1.matchesError(fe1));
		ed1.setFileName(null);
		assertTrue(ed1.matchesError(fe1));
		ed1.setFolderPath(null);
		assertTrue(ed1.matchesError(fe1));
		ed1.setMessage("message.+"); //$NON-NLS-1$
		assertFalse(ed1.matchesError(fe1));

	}
}
