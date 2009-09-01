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
package com.aptana.ide.editor.xml;

import org.eclipse.swt.graphics.RGB;

/**
 * @author Robin Debreuil
 */
public interface IXMLColorConstants
{
	/*
	 * Category colors - fallbacks
	 */
	
	/**
	 * Error
	 */
	RGB ERROR = new RGB(0xFF, 0x80, 0x80);
	
	/**
	 * Literal
	 */
	RGB LITERAL = new RGB(0x00, 0x80, 0x00);
	
	/**
	 * Keyword
	 */
	RGB KEYWORD = new RGB(0x00, 0x00, 0x80);
	
	/**
	 * Punctuator
	 */
	RGB PUNCTUATOR = new RGB(0x00, 0x00, 0x00);
	
	/*
	 * Type colors
	 */
	
	/**
	 * PI_OPEN_CLOSE
	 */
	RGB PI_OPEN_CLOSE = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * PI_TEXT
	 */
	RGB PI_TEXT = new RGB(0x80, 0x80, 0x80);
	
	/**
	 * TAG_OPEN_CLOSE
	 */
	RGB TAG_OPEN_CLOSE = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * NAME
	 */
	RGB NAME = new RGB(0xA3, 0x15, 0x15);
	
	/**
	 * ATTRIBUTE
	 */
	RGB ATTRIBUTE = new RGB(0xFF, 0x00, 0x00);
	
	/**
	 * ATTRIBUTE_VALUE
	 */
	RGB ATTRIBUTE_VALUE = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * EQUAL
	 */
	RGB EQUAL = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * TEXT
	 */
	RGB TEXT = new RGB(0x00, 0x00, 0x00);
	
	/**
	 * ENTITY_REF
	 */
	RGB ENTITY_REF = new RGB(0xFF, 0x00, 0x00);
	
	/**
	 * CHAR_REF
	 */
	RGB CHAR_REF = new RGB(0xFF, 0x00, 0x00);
	
	/**
	 * PE_REF
	 */
	RGB PE_REF = new RGB(0xFF, 0x00, 0x00);
	
	/**
	 * CDATA_START_END
	 */
	RGB CDATA_START_END = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * CDATA_TEXT
	 */
	RGB CDATA_TEXT = new RGB(0x80, 0x080, 0x80);
	
	/**
	 * DECL_START_END
	 */
	RGB DECL_START_END = new RGB(0x00, 0x00, 0xFF);
	
	/**
	 * COMMENT
	 */
	RGB COMMENT = new RGB(0x00, 0x80, 0x00);
}
