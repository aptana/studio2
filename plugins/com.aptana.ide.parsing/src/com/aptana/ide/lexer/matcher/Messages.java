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
package com.aptana.ide.lexer.matcher;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.lexer.matcher.messages"; //$NON-NLS-1$

	/**
	 * AbstractMatcher_No_Category
	 */
	public static String AbstractMatcher_No_Category;

	/**
	 * AbstractMatcher_No_Type
	 */
	public static String AbstractMatcher_No_Type;

	/**
	 * AbstractMatcher_No_Type_On_Self_Or_Descendants
	 */
	public static String AbstractMatcher_No_Type_On_Self_Or_Descendants;

	public static String BalancedCharacterMatcher_End_Char_Not_Defined;

	public static String BalancedCharacterMatcher_Start_Char_Not_Defined;

	/**
	 * CharacterClassMatcher_No_Text_Content
	 */
	public static String CharacterClassMatcher_No_Text_Content;

	/**
	 * CharacterMatcher_No_Text_Or_Use_Element
	 */
	public static String CharacterMatcher_No_Text_Or_Use_Element;

	/**
	 * CharacterMatcher_Only_Recognize_First_Child
	 */
	public static String CharacterMatcher_Only_Recognize_First_Child;
	
	/**
	 * CommentMatcher_End_Not_Defined
	 */
	public static String CommentMatcher_End_Not_Defined;

	/**
	 * CommentMatcher_Start_Not_Defined
	 */
	public static String CommentMatcher_Start_Not_Defined;

	/**
	 * LookaheadMatcher_No_Children
	 */
	public static String LookaheadMatcher_No_Children;

	/**
	 * LookaheadMatcher_Only_Recognizes_First_Child
	 */
	public static String LookaheadMatcher_Wrapping_Children;

	/**
	 * MatcherLexerBuilder_Cannot_Build_Lexer
	 */
	public static String MatcherLexerBuilder_Cannot_Build_Lexer;

	/**
	 * MatcherLexerBuilder_Error_Message
	 */
	public static String MatcherLexerBuilder_Error_Message;

	/**
	 * MatcherLexerBuilder_Error_Reading_XML_File
	 */
	public static String MatcherLexerBuilder_Error_Reading_XML_File;

	/**
	 * MatcherLexerBuilder_Info_Message
	 */
	public static String MatcherLexerBuilder_Info_Message;

	/**
	 * MatcherLexerBuilder_Warning_Message
	 */
	public static String MatcherLexerBuilder_Warning_Message;

	/**
	 * MatcherMap_Call_SetSeal_Before_GetMatchers
	 */
	public static String MatcherMap_Call_SetSeal_Before_GetMatchers;

	/**
	 * MatcherTokenList_Unrecognzied_Group_Name
	 */
	public static String MatcherTokenList_Unrecognzied_Group_Name;

	public static String MultiwordMatcher_Unsupported_type;

	/**
	 * OneOrMoreMatcher_No_Children
	 */
	public static String OneOrMoreMatcher_No_Children;

	/**
	 * OneOrMoreMatcher_Only_Recognize_First_Child
	 */
	public static String OneOrMoreMatcher_Wrapping_Children;

	/**
	 * OptionalMatcher_No_Children
	 */
	public static String OptionalMatcher_No_Children;

	/**
	 * OptionalMatcher_Only_Recognizes_First_Child
	 */
	public static String OptionalMatcher_Wrapping_Children;

	/**
	 * RepetitionMatcher_No_Children
	 */
	public static String RepetitionMatcher_No_Children;

	/**
	 * RepetitionMatcher_Only_Recognizes_First_Child
	 */
	public static String RepetitionMatcher_Wrapping_Children;

	/**
	 * StringMatcher_No_Text_Or_Use_Element
	 */
	public static String StringMatcher_No_Text_Or_Use_Element;

	/**
	 * StringMatcher_Only_Recognize_First_Child
	 */
	public static String StringMatcher_Only_Recognize_First_Child;

	/**
	 * StringSetMatcher_Index_Out_Of_Bounds
	 */
	public static String StringSetMatcher_Index_Out_Of_Bounds;

	/**
	 * StringSetMatcher_Node_Not_Defined
	 */
	public static String StringSetMatcher_Node_Not_Defined;

	/**
	 * StringSetMatcher_Value_Not_Defined
	 */
	public static String StringSetMatcher_Value_Not_Defined;

	/**
	 * ToDelimiterMatcher_No_Text_Or_Child_Matcher
	 */
	public static String ToDelimiterMatcher_No_Text_Or_Child_Matcher;

	/**
	 * ToDelimiterMatcher_Only_Recognizes_First_Child
	 */
	public static String ToDelimiterMatcher_Wrapping_Children;

	/**
	 * ZeroOrMoreMatcher_No_Children
	 */
	public static String ZeroOrMoreMatcher_No_Children;

	/**
	 * ZeroOrMoreMatcher_Only_Recognize_First_Child
	 */
	public static String ZeroOrMoreMatcher_Wrapping_Children;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
