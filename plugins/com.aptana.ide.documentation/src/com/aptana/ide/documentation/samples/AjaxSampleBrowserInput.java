/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.documentation.samples;

import java.net.URL;

import com.aptana.ide.intro.browser.CoreBrowserEditorInput;
import com.aptana.ide.samples.model.SamplesEntry;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class AjaxSampleBrowserInput extends CoreBrowserEditorInput
{

	private SamplesEntry entry;

	/**
	 * Creates a new input from a samples entrys
	 * @param entry
	 * @param url
	 */
	public AjaxSampleBrowserInput(SamplesEntry entry, URL url)
	{
		super(url);
		this.entry = entry;
	}

	/**
	 * Tests equality based on samples entry
	 * @param obj 
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Object obj)
	{
		if( obj instanceof AjaxSampleBrowserInput ) {
			return this.entry == ((AjaxSampleBrowserInput)obj).entry;
		}
		return false;
	}

}
