/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FeatureChange
{

	private String oldVersion;
	private String newVersion;
	private String label;
	private String id;
	private String provider;

	/**
	 * Creates a new feature change
	 * 
	 * @param id
	 * @param label
	 * @param oldVersion -
	 *            should be null if this is a feature addition
	 * @param newVersion
	 * @param provider
	 */
	public FeatureChange(String id, String label, String oldVersion, String newVersion, String provider)
	{
		this.id = id;
		this.label = label;
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		this.provider = provider;
	}

	/**
	 * Gets the feature id
	 * 
	 * @return feature id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the feature id
	 * 
	 * @param id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the feature label
	 * 
	 * @return - feature label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the feature label
	 * 
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the new feature version
	 * 
	 * @return - new feature version
	 */
	public String getNewVersion()
	{
		return newVersion;
	}

	/**
	 * Sets the new feature version
	 * 
	 * @param newVersion
	 */
	public void setNewVersion(String newVersion)
	{
		this.newVersion = newVersion;
	}

	/**
	 * Gets the old feature version
	 * 
	 * @return - null if this feature is new
	 */
	public String getOldVersion()
	{
		return oldVersion;
	}

	/**
	 * Sets the old feature version
	 * 
	 * @param oldVersion -
	 *            null if new feature
	 */
	public void setOldVersion(String oldVersion)
	{
		this.oldVersion = oldVersion;
	}

	/**
	 * @return the provider
	 */
	public String getProvider()
	{
		return provider;
	}

	/**
	 * @param provider
	 *            the provider to set
	 */
	public void setProvider(String provider)
	{
		this.provider = provider;
	}

}
