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
package com.aptana.ide.sax.tests;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.xml.sax.Attributes;

import com.aptana.ide.lexer.LexerPlugin;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaElement;

/**
 * @author Kevin Lindsey
 */
public class TestLexerSchemaBuilder extends TestCase
{
	/**
	 * testBuildLexerSchema10
	 */
	public void testBuildLexerSchema10()
	{
		InputStream in = LexerPlugin.class.getResourceAsStream("/com/aptana/ide/resources/LexerSchema_1_0.xml");
		
		try
		{
			Schema schema = SchemaBuilder.fromXML(in, this);
			
			assertNotNull(schema);
			
			this.checkStructure(schema);
		}
		catch (Exception e)
		{
			this.fail(e);
		}
	}
	
	/**
	 * testBuildLexerSchema11
	 */
	public void testBuildLexerSchema11()
	{
		InputStream in = LexerPlugin.class.getResourceAsStream("/com/aptana/ide/resources/LexerSchema_1_1.xml");
		
		try
		{
			Schema schema = SchemaBuilder.fromXML(in, this);
			
			assertNotNull(schema);
			
			this.checkStructure(schema);
		}
		catch (Exception e)
		{
			this.fail(e);
		}
	}
	
	/**
	 * checkStructure
	 *
	 * @param schema
	 */
	private void checkStructure(Schema schema)
	{
		// check root (lexer)
		SchemaElement[] elements = schema.getRootElement().getTransitionElements();
		assertEquals(1, elements.length);
		
		SchemaElement root = elements[0];
		assertEquals("lexer", root.getName());
		
		// check children (group)
		elements = root.getTransitionElements();
		assertEquals(1, elements.length);
		
		SchemaElement group = elements[0];
		assertEquals("group", group.getName());
		
		// check children (token)
		elements = group.getTransitionElements();
		assertEquals(1, elements.length);
		
		SchemaElement token = elements[0];
		assertEquals("token", token.getName());
		
		// check children (regex)
		elements = token.getTransitionElements();
		assertEquals(1, elements.length);
		
		SchemaElement regex = elements[0];
		assertEquals("regex", regex.getName());
	}
	
	/**
	 * fail
	 *
	 * @param e
	 */
	private void fail(Exception e)
	{
		StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			
			// add error message
			writer.println("An exception occurred while building the lexer schema");
			
			// add stack trace
			e.printStackTrace(writer);
			
			// make sure everything is in the string builder
			writer.flush();
			
			// show error
			Assert.fail(sw.toString());
	}
	
	// handlers
	
	/**
	 * Process the start of a new lexer element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterLexer(String ns, String name, String qname, Attributes attributes)
	{
	}
	
	/**
	 * Process the start of a new group element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterGroup(String ns, String name, String qname, Attributes attributes)
	{
	}
	
	/**
	 * Process the start of a new token element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterToken(String ns, String name, String qname, Attributes attributes)
	{
	}
	
	/**
	 * Process the start of a new regex element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterRegex(String ns, String name, String qname, Attributes attributes)
	{
	}
	
	/**
	 * Complete the processing of the regex element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitRegex(String ns, String name, String qname)
	{
	}
}
