/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *     Jeff Mesnil - bug 132601
 *******************************************************************************/

package org.eclipse.eclipsemonkey.tests;

import junit.framework.TestCase;

import org.eclipse.eclipsemonkey.ScriptMetadata;

/**
 * ScriptMetadataTest
 */
public class ScriptMetadataTest extends TestCase {

	/**
	 * testLegalFilenames
	 */
	public void testLegalFilenames() {
		ScriptMetadata data = new ScriptMetadata();
		data.setMenuName("This is a test"); //$NON-NLS-1$
		assertEquals("This_is_a_test.js", data.getReasonableFilename()); //$NON-NLS-1$
		
		data.setMenuName("ABCD@#$%@$#DEFG"); //$NON-NLS-1$
		assertEquals("ABCDDEFG.js", data.getReasonableFilename()); //$NON-NLS-1$
		
		data.setMenuName("!!!+++"); //$NON-NLS-1$
		assertEquals("script.js", data.getReasonableFilename()); //$NON-NLS-1$
		
		data.setMenuName(null);
		assertEquals("script.js", data.getReasonableFilename()); //$NON-NLS-1$
		
	}
}
