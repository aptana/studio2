///** 
// * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
// * dual-licensed under both the Aptana Public License and the GNU General
// * Public license. You may elect to use one or the other of these licenses.
// * 
// * This program is distributed in the hope that it will be useful, but
// * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
// * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
// * the GPL or APL you select, is prohibited.
// *
// * 1. For the GPL license (GPL), you can redistribute and/or modify this
// * program under the terms of the GNU General Public License,
// * Version 3, as published by the Free Software Foundation.  You should
// * have received a copy of the GNU General Public License, Version 3 along
// * with this program; if not, write to the Free Software Foundation, Inc., 51
// * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
// * 
// * Aptana provides a special exception to allow redistribution of this file
// * with certain Eclipse Public Licensed code and certain additional terms
// * pursuant to Section 7 of the GPL. You may view the exception and these
// * terms on the web at http://www.aptana.com/legal/gpl/.
// * 
// * 2. For the Aptana Public License (APL), this program and the
// * accompanying materials are made available under the terms of the APL
// * v1.0 which accompanies this distribution, and is available at
// * http://www.aptana.com/legal/apl/.
// * 
// * You may view the GPL, Aptana's exception and additional terms, and the
// * APL in the file titled license.html at the root of the corresponding
// * plugin containing this source file.
// * 
// * Any modifications to this file must keep this entire header intact.
// */
//package com.aptana.ide.logging.impl.test;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.nio.CharBuffer;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import junit.framework.TestCase;
//
//import com.aptana.ide.lexer.ILexer;
//import com.aptana.ide.lexer.Lexeme;
//import com.aptana.ide.lexer.LexemeList;
//import com.aptana.ide.lexer.LexerException;
//import com.aptana.ide.logging.LoggingPlugin;
//import com.aptana.ide.logging.LoggingPreferences;
//import com.aptana.ide.logging.coloring.TokenTypes;
//
//
///**
// * Performance test.
// * @author Denis Denisenko
// */
//public class PerformanceTest extends TestCase
//{
//    public void test01() throws IOException, LexerException
//    {
//        final int times = 5;
//        final String resourceName = "/com/aptana/ide/logging/impl/test/1000lines.txt";
//        final String[] searchStrings = new String[]{"Thing1.", "<.rou.>", "0", "8.0*<Thing.>.</Thing.>", "Custom Query*Ticket"};
//        
//        
//        InputStreamReader streamReader = new InputStreamReader(PerformanceTest.class.getResourceAsStream(resourceName), "UTF-8");
//        CharBuffer target = CharBuffer.allocate(1024*1024*2);
//        streamReader.read(target);
//        
//        String toParse = target.toString();
//        
//        //initializing lexer test
//        ILexer lexer = initLexerTest(toParse, searchStrings);
//        lexer.seal();
//        
//        
//        //running lexer test
//        final long lexerTestStart = System.nanoTime(); 
//        testLexer(lexer, toParse, times);
//        final long lexerTestEnd = System.nanoTime();
//            
//        
//        //initializing Java regexp test
//        Pattern[] patterns = initJavaRegexpTest(searchStrings);
//            
//        //running java regexp test
//        final long javaTestStart = System.nanoTime(); 
//        testJavaRegexps(toParse, searchStrings, patterns, times);
//        final long javaTestEnd = System.nanoTime();
//            
//        System.out.println("Lexing with Java regexps (nanosecs):\t" + (javaTestEnd - javaTestStart)/times);
//        System.out.println("Lexing with lexer (nanosecs):\t\t" + (lexerTestEnd - lexerTestStart)/times);
//    }
//    
//    
//    private ILexer initLexerTest(String toParse, String[] searchStrings) throws IOException, LexerException
//    {
//        LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
//        List<LoggingPreferences.Rule> rules = preferences.getRules();
//        rules.clear();
//        
//        for (int i = 0; i < searchStrings.length; i++)
//        {
//            rules.add(new LoggingPreferences.Rule(searchStrings[i], searchStrings[i], true, false));
//        }
//        
//     
//        ILexer lexer = TokenTypes.getLexerFactory().getLexer();
//        lexer.setLanguage(TokenTypes.LANGUAGE);
//        lexer.setSource(toParse);
//        return lexer;
//    }
//    
//    private Pattern[] initJavaRegexpTest(String[] searchStrings)
//    {
//        Pattern[] patterns = new Pattern[searchStrings.length];
//        for (int i = 0; i < searchStrings.length; i++)
//        {
//            patterns[i] = Pattern.compile(searchStrings[i]);
//        }
//        
//        return patterns;
//    }
//    
//    private void testLexer(ILexer lexer, String source, int times)
//    {
//        for (int i = 0; i < times; i++)
//        {
//            lexer.setSource(source);
//            LexemeList currentLexemes = new LexemeList();
//            while(true)
//            {
//                Lexeme lexeme = lexer.getNextLexeme();
//                if (lexeme == null)
//                {
//                    break;
//                }
//                
//                currentLexemes.add(lexeme);
//            }
//        }
//    }
//    
//    private void testJavaRegexps(String toParse, String[] searchStrings, Pattern[] patterns, int times) throws IOException
//    {
//        //abstract list and hash map to put lexemes into
//        ArrayList<String> lexemes = new ArrayList<String>();
//        HashMap<Integer, String> lexemesMap = new HashMap<Integer, String>();
//        for (int i = 0; i < times; i++)
//        {
//            StringReader sReader = new StringReader(toParse);
//            BufferedReader reader = new BufferedReader(sReader);
//            String line = null;
//            
//            while((line = reader.readLine()) != null)
//            {
//                for(int j = 0; j < searchStrings.length; j++)
//                {
//                    String currentStr = searchStrings[j];
//                    Matcher matcher = patterns[j].matcher(line);
//                    if(matcher.find())
//                    {
//                        //emulating putting lexeme to some list and Hash Map
//                        lexemes.add(currentStr);
//                        lexemesMap.put(matcher.start(), currentStr);
//                        break;
//                    }
//                }
//            }
//
//            lexemes.clear();
//            lexemesMap.clear();
//        }
//    }
//}
