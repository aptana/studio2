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
import java.util.Arrays;
import java.util.Comparator;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.xml.sax.Attributes;

import com.aptana.ide.parsing.ParsingPlugin;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaElement;

/**
 * @author Kevin Lindsey
 */
public class TestMetadataSchemaBuilder extends TestCase
{
	/**
	 * testBuildMetadataSchema10
	 */
	public void testBuildMetadataSchema10()
	{
		InputStream in = ParsingPlugin.class
				.getResourceAsStream("/com/aptana/ide/metadata/resources/MetadataSchema_1_0.xml");

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
	 * testBuildMetadataSchema11
	 */
	public void testBuildMetadataSchema11()
	{
		InputStream in = ParsingPlugin.class
				.getResourceAsStream("/com/aptana/ide/metadata/resources/MetadataSchema_1_1.xml");

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
		// *** GEN 0 ***

		// check for content-assist
		this.checkChildren(schema.getRootElement(), "content-assist");

		// *** GEN 1 ***

		// check for elements, fields, events
		SchemaElement root = this.getElementFromParent(schema.getRootElement(), "content-assist");
		this.checkChildren(root, new String[] { "elements", "fields", "events" });

		// *** GEN 2 ***

		// check for element
		SchemaElement elements = this.getElementFromParent(root, "elements");
		this.checkChildren(elements, "element");

		// check for field
		SchemaElement fields = this.getElementFromParent(root, "fields");
		this.checkChildren(fields, "field");

		// check for event
		SchemaElement events = this.getElementFromParent(root, "events");
		this.checkChildren(events, "event");

		// *** GEN 3 ***
		
		// check children of element
		SchemaElement element = this.getElementFromParent(elements, "element");
		this.checkChildren(element, new String[] { "availability", "deprecated", "description", "example", "remarks",
				"browsers", "references", "fields", "events", "attributes" });
		
		// *** GEN 4 ***
		// since fields and events are references, I continue testing those under element
		
		// check children of availability
		SchemaElement availability = this.getElementFromParent(element, "availability");
		this.checkChildren(availability, "specification");
		
		// check children of browsers
		SchemaElement browsers = this.getElementFromParent(element, "browsers");
		this.checkChildren(browsers, "browser");
		
		// check children of references
		SchemaElement references = this.getElementFromParent(element, "references");
		this.checkChildren(references, "reference");
		
		// check children of fields
		fields = this.getElementFromParent(element, "fields");
		this.checkChildren(fields, "field");

		// check children of events
		events = this.getElementFromParent(element, "events");
		this.checkChildren(events, "event");
		
		// check children of attributes
		SchemaElement attributes = this.getElementFromParent(element, "attributes");
		this.checkChildren(attributes, "attribute");
		
		// *** GEN 5 ***
		
		// check children of browsers
		SchemaElement browser = this.getElementFromParent(browsers, "browser");
		this.checkChildren(browser, "description");
		
		// check children of field
		SchemaElement field = this.getElementFromParent(fields, "field");
		this.checkChildren(field, new String[] { "hint", "values", "availability", "deprecated", "description", "example", "remarks",
				"browsers", "references" } );
		
		// check children of event
		SchemaElement event = this.getElementFromParent(events, "event");
		this.checkChildren(event, new String[] { "availability", "deprecated", "description", "example", "remarks",
				"browsers", "references" } );
		
		// ** GEN 6 ***
		
		// check children of values
		SchemaElement values = this.getElementFromParent(field, "values");
		this.checkChildren(values, "value");
	}

	/**
	 * checkChildren
	 * 
	 * @param parent
	 * @param childName
	 */
	private void checkChildren(SchemaElement parent, String childName)
	{
		this.checkChildren(parent, new String[] { childName });
	}

	/**
	 * checkChildren
	 * 
	 * @param parent
	 * @param childNames
	 */
	private void checkChildren(SchemaElement parent, String[] childNames)
	{
		SchemaElement[] elements = parent.getTransitionElements();

		// make sure we have the proper number of children
		assertEquals(childNames.length, elements.length);

		// make sure we have each specific child element
		for (int i = 0; i < childNames.length; i++)
		{
			this.parentContainsChild(parent, childNames[i]);
		}
	}

	/**
	 * parentContainsChild
	 * 
	 * @param elementName
	 * @param parentElement
	 */
	private void parentContainsChild(SchemaElement parentElement, String elementName)
	{
		SchemaElement element = this.getElementFromParent(parentElement, elementName);

		assertTrue("Element " + elementName + " does not exist under " + parentElement.getName(), element != null);
	}

	/**
	 * getElementFromParent
	 * 
	 * @param parentElement
	 * @param elementName
	 * @return SchemaElement or null
	 */
	private SchemaElement getElementFromParent(SchemaElement parentElement, String elementName)
	{
		SchemaElement[] elements = parentElement.getTransitionElements();
		SchemaElement result = null;
		
		Arrays.sort(elements, new Comparator() {
			/**
			 * compare
			 *
			 * @param arg0
			 * @param arg1
			 * @return int
			 */
			public int compare(Object arg0, Object arg1)
			{
				SchemaElement e1 = (SchemaElement) arg0;
				SchemaElement e2 = (SchemaElement) arg1;
				
				return e1.getName().compareTo(e2.getName());
			}
			
		});
		
		// make sure elements are in alphabetical order
		int index = Arrays.binarySearch(elements, elementName, new Comparator()
		{
			/**
			 * compare
			 * 
			 * @param arg0
			 * @param arg1
			 * @return int
			 */
			public int compare(Object arg0, Object arg1)
			{
				SchemaElement element = (SchemaElement) arg0;
				String elementName = element.getName();
				String name = (String) arg1;
				
				return elementName.compareTo(name);
			}
		});

		if (index >= 0)
		{
			result = elements[index];
		}

		return result;
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
	 * Process the start of a new browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Process the start of a new element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterElement(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Process the start of a new event element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterEvent(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Process the start of a new field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterField(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Process the start of a new specification element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterSpecification(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Process the start of a new value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterValue(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Complete the processing of the availability element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitAvailability(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the deprecated element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDeprecated(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDescription(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitElement(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the event element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitEvent(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitField(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the hint element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitHint(String ns, String name, String qname)
	{
	}

	/**
	 * Complete the processing of the value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
	}

	/**
	 * start buffering text
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void startTextBuffer(String ns, String name, String qname, Attributes attributes)
	{
	}
}
