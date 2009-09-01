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
 *******************************************************************************/

package org.eclipse.eclipsemonkey.actions;

import junit.framework.TestCase;

/**
 * UpdateMonkeyActionsResourceChangeListener
 */
public class ActionsTests extends TestCase {

	/**
	 * 
	 */
	public void testCollapseEscapedNewlines() {
		String s1 = "this is a test\nand another test\nand a third line\n"; //$NON-NLS-1$
		String r1 = (new PasteScriptFromClipboardAction()).collapseEscapedNewlines(s1);
		assertEquals( s1, r1 );

		String s2 = "this is a test\\\nand another test\nand a third line\n"; //$NON-NLS-1$
		String e2 = "this is a testand another test\nand a third line\n"; //$NON-NLS-1$
		String r2 = (new PasteScriptFromClipboardAction()).collapseEscapedNewlines(s2);
		assertEquals( e2, r2 );
	}
	
	/**
	 * 
	 *
	 */
	public void testBreakIntoShorterLines() {
		String s1 = "0123456789"; //$NON-NLS-1$
		String r1 = (new PublishScriptForEmail()).breakIntoShorterLines(s1);
		assertEquals( s1, r1 );
		
		String s2 = s1 + s1 + s1 + s1 + s1 + s1;
		String e2 = s1 + s1 + s1 + s1 + s1 + "\\\n" + s1; //$NON-NLS-1$
		String r2 = (new PublishScriptForEmail()).breakIntoShorterLines(s2);
		assertEquals( e2, r2 );
		
		String s3 = s1 + s1 + s1 + s1 + s1 + "\n" + s1; //$NON-NLS-1$
		String e3 = s1 + s1 + s1 + s1 + s1 + "\n" + s1; //$NON-NLS-1$
		String r3 = (new PublishScriptForEmail()).breakIntoShorterLines(s3);
		assertEquals( e3, r3 );
		
		String s4 = s1 + s1 + s1 + s1 + s1 + s1 + s1 + s1 + s1 + s1 + s1;
		String e4 = s1 + s1 + s1 + s1 + s1 + "\\\n" + s1 + s1 + s1 + s1 + s1 + "\\\n" + s1; //$NON-NLS-1$ //$NON-NLS-2$
		String r4 = (new PublishScriptForEmail()).breakIntoShorterLines(s4);
		assertEquals( e4, r4 );
	}
}
