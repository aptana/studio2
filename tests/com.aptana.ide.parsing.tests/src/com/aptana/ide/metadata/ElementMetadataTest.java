package com.aptana.ide.metadata;

import junit.framework.TestCase;

public class ElementMetadataTest extends TestCase {

	public void testMergeElementMetadata() {
		
		ElementMetadata m1 = new ElementMetadata();
		m1.setName("script");
		m1.setDescription("description");

		FieldMetadata fm = new FieldMetadata();
		fm.setName("src");
		m1.addField(fm);
		
		ElementMetadata m2 = new ElementMetadata();
		m2.setName("script");
		m2.setDescription("override description");

		FieldMetadata fm2 = new FieldMetadata();
		fm2.setName("runat");
		m2.addField(fm2);

		EventMetadata em2 = new EventMetadata();
		em2.setName("onRunat");
		m2.addEvent(em2);

		m1.merge(m2);

		assertEquals("script", m1.getName());
		assertEquals("description override description", m1.getDescription());
		assertEquals(2, m1.getFields().size());
		assertEquals(1, m1.getEvents().size());
	}

}
