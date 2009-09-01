/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.parsing.ast.nodes;

/**
 * @author Kevin Lindsey
 */
public class ASTParseNodeTypes
{
	public static final int ROOT = 1;
	public static final int IMPORT = 2;
	public static final int DOT = 3;
	public static final int HANDLER = 4;
	public static final int IDENTIFIER = 5;
	public static final int PARAMETER = 6;
	public static final int APPEND = 7;
	public static final int INSTANTIATION = 8;
	public static final int SWITCH = 9;
	public static final int CASE = 10;
	public static final int INVOCATION = 11;
	public static final int ASSIGN = 12;
	public static final int STRING = 13;
	public static final int NUMBER = 14;
	public static final int TRUE = 15;
	public static final int FALSE = 16;
	public static final int NULL = 17;
	public static final int ARGUMENT = 18;
	public static final int LIST = 19;
}
