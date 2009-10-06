package com.aptana.ide.metadata;

import junit.framework.TestCase;

public class MetadataItemTest extends TestCase
{
	public void testMerge()
	{
		MetadataItem m1 = new MetadataItem();
		m1.setName("m1Name");
		m1.setDescription("m1Description");
		m1.setDeprecatedDescription("m1DeprecatedDescription");
		m1.setHint("m1Hint");
		UserAgent u1 = new UserAgent("u1platform", "u1version", "u1os", "u1osVersion", "u1description");
		m1.addUserAgent(u1);

		MetadataItem m2 = new MetadataItem();
		m2.setName("m2Name");
		m2.setDescription("m2Description");
		m2.setDeprecatedDescription("m2DeprecatedDescription");
		m2.setHint("m2Hint");
		UserAgent u2 = new UserAgent("u2platform", "u2version", "u2os", "u2osVersion", "u2description");
		m2.addUserAgent(u2);
		
		m1.merge(m2);
		
		assertEquals("m1Name", m1.getName());
		assertEquals("m1Description m2Description", m1.getDescription());
		assertEquals("m1DeprecatedDescription m2DeprecatedDescription", m1.getDeprecatedDescription());
		assertEquals("m1Hint m2Hint", m1.getHint());
		assertEquals(2, m1.getUserAgents().length);
	}
}
