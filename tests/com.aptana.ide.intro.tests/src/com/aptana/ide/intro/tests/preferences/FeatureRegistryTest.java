/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.intro.tests.preferences;

import java.util.List;

import junit.framework.TestCase;

import com.aptana.ide.intro.preferences.FeatureDescriptor;
import com.aptana.ide.intro.preferences.FeatureRegistry;

public class FeatureRegistryTest extends TestCase
{

	/**
	 * testGetRequiredFeatures
	 */
	public void testGetRequiredFeatures()
	{
		List<FeatureDescriptor> features = FeatureRegistry.getRequiredFeatures();
		assertTrue("No required features found", features.size() > 0);

		FeatureDescriptor svn = new FeatureDescriptor("com.aptana.ide.intro.tests.feature1", "label", "url", new String[] {
				"com.aptana.ide.intro.tests.conflictsWith1" });
		assertTrue("Test Required feature not found in required features list", features.contains(svn));
	}

	public void testGatherUninstalledRequiredFeatures()
	{
		List<FeatureDescriptor> features = FeatureRegistry.gatherUninstalledRequiredFeatures();
		assertTrue("No uninstalled required features found", features.size() > 0);

		FeatureDescriptor svn = new FeatureDescriptor("com.aptana.ide.intro.tests.feature1", "label", "url", new String[] {
				"com.aptana.ide.intro.tests.conflictsWith1" });
		assertTrue("Test Required feature not found in required features list", features.contains(svn));
	}

	/**
	 * testIsFeatureIgnored
	 */
	public void testIsFeatureIgnored()
	{
		assertTrue(FeatureRegistry.isFeatureIgnored("featurea", new String[] { "featurea", "featureb" }));
		assertFalse(FeatureRegistry.isFeatureIgnored("featurec", new String[] { "featurea", "featureb" }));
		assertFalse(FeatureRegistry.isFeatureIgnored("featurec", new String[0]));
		assertFalse(FeatureRegistry.isFeatureIgnored("featurec", null));
	}

	/**
	 * testDoesFeatureConflict
	 */
	public void testDoesFeatureConflict()
	{
		FeatureDescriptor noConflicts = new FeatureDescriptor("com.aptana.ide.feature.noconflict",
				"Aptana No Conflict", "url", new String[0]);
		assertFalse(FeatureRegistry.doesFeatureConflict(noConflicts, new String[] { "featurea", "featureb" }));

		FeatureDescriptor svn = new FeatureDescriptor("com.aptana.ide.feature.svn", "Aptana SVN", "url", new String[] {
				"featurea", "featureb" });
		assertTrue(FeatureRegistry.doesFeatureConflict(svn, new String[] { "featurea", "featureb" }));
		assertFalse(FeatureRegistry.doesFeatureConflict(svn, new String[] { "featurec" }));
	}

}
