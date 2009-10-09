package com.aptana.ide.metadata;

import java.util.Hashtable;

import junit.framework.TestCase;

public class MetadataRuntimeEnvironmentTest extends TestCase {

	public void testAddEnvironment() {

		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		MetadataEnvironment e1 = new MetadataEnvironment();
		MetadataEnvironment e2 = new MetadataEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);

		assertEquals(2, mr1.getEnvironments().length);
	}

	public void testRemoveEnvironment() {

		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		MetadataEnvironment e1 = new MetadataEnvironment();
		MetadataEnvironment e2 = new MetadataEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);

		assertEquals(2, mr1.getEnvironments().length);

		mr1.removeEnvironment(e2);

		assertEquals(1, mr1.getEnvironments().length);
	}

	public void testGetAllElements()
	{
		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		MetadataEnvironment e1 = new MetadataEnvironment();
		MetadataEnvironment e2 = new MetadataEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);
		
		ElementMetadata m1 = new ElementMetadata();
		m1.setName("script");
		m1.setDescription("description");
		e1.addElement(m1);
		
		ElementMetadata m2 = new ElementMetadata();
		m2.setName("body");
		m2.setDescription("description");
		e2.addElement(m2);
		
		String[] em1 = mr1.getAllElements();
		assertEquals(2, em1.length);
	}

	public void testGetGlobalFields()
	{
		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		MetadataEnvironment e1 = new MetadataEnvironment();
		MetadataEnvironment e2 = new MetadataEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);
		
		Hashtable<String, FieldMetadata> h1 = new Hashtable<String, FieldMetadata>();
		FieldMetadata m1 = new FieldMetadata();
		m1.setName("src");
		m1.setDescription("description");
		h1.put("src", m1);
		e1.setGlobalFields(h1);
		
		Hashtable<String, FieldMetadata> h2 = new Hashtable<String, FieldMetadata>();
		FieldMetadata m2 = new FieldMetadata();
		m2.setName("src");
		m2.setDescription("description1");
		h2.put("src", m2);

		FieldMetadata m3 = new FieldMetadata();
		m3.setName("id");
		m3.setDescription("description");
		h2.put("id", m3);

		e2.setGlobalFields(h2);
		
		Hashtable em1 = mr1.getGlobalFields();
		assertEquals(2, em1.size());

		FieldMetadata fm = (FieldMetadata)em1.get("src");
		assertEquals("description description1", fm.getDescription());

	}

	public void testGetGlobalEvents() {
		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		MetadataEnvironment e1 = new MetadataEnvironment();
		MetadataEnvironment e2 = new MetadataEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);
		
		Hashtable<String, EventMetadata> h1 = new Hashtable<String, EventMetadata>();
		EventMetadata m1 = new EventMetadata();
		m1.setName("src");
		m1.setDescription("description");
		h1.put("src", m1);
		e1.setGlobalEvents(h1);
		
		Hashtable<String, EventMetadata> h2 = new Hashtable<String, EventMetadata>();
		EventMetadata m2 = new EventMetadata();
		m2.setName("src");
		m2.setDescription("description1");
		h2.put("src", m2);

		EventMetadata m3 = new EventMetadata();
		m3.setName("id");
		m3.setDescription("description");
		h2.put("id", m3);
		e2.setGlobalEvents(h2);
		
		Hashtable em1 = mr1.getGlobalEvents();
		assertEquals(2, em1.size());
		
		EventMetadata fm = (EventMetadata)em1.get("src");
		assertEquals("description description1", fm.getDescription());
	}

	public void testGetElementString() {
		ElementMetadata m1 = new ElementMetadata();
		m1.setName("script");
		m1.setDescription("description");

		FieldMetadata fm = new FieldMetadata();
		fm.setName("src");
		m1.addField(fm);

		MetadataEnvironment e1 = new MetadataEnvironment();
		e1.addElement(m1);
		
		ElementMetadata m2 = new ElementMetadata();
		m2.setName("script");
		m2.setDescription("override description");

		FieldMetadata fm2 = new FieldMetadata();
		fm2.setName("runat");
		m2.addField(fm2);

		EventMetadata em2 = new EventMetadata();
		em2.setName("onRunat");
		m2.addEvent(em2);

		MetadataEnvironment e2 = new MetadataEnvironment();
		e2.addElement(m2);

		MetadataRuntimeEnvironment mr1 = new MetadataRuntimeEnvironment();
		mr1.addEnvironment(e1);
		mr1.addEnvironment(e2);
		
		ElementMetadata script = mr1.getElement("script");
		assertEquals("script", script.getName());
		assertEquals("description override description", script.getDescription());
		assertEquals(2, script.getFields().size());
		assertEquals(1, script.getEvents().size());
	}

//	public void testGetMergedElement() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetElementLexeme() {
//		fail("Not yet implemented");
//	}

}
