/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.parsing.tools;

/**
 * @author Kevin Lindsey
 */
public final class AllMetadataToBinary
{
	private static final String pluginDirectory = "C:\\dev\\depot\\products\\ide\\plugins\\"; //$NON-NLS-1$
	
	private static final String[] bases = new String[] {
		pluginDirectory + "com.aptana.ide.editor.css\\src\\com\\aptana\\ide\\editor\\css\\resources\\CSS.xml", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.html\\src\\com\\aptana\\ide\\editor\\html\\resources\\HTML.xml" //$NON-NLS-1$
	};
	private static final String[] directories = new String[] {
		pluginDirectory + "com.aptana.ide.editor.css\\src\\com\\aptana\\ide\\editor\\css\\parsing\\CSSMetadata.bin", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.html\\src\\com\\aptana\\ide\\editor\\html\\parsing\\HTMLMetadata.bin" //$NON-NLS-1$
	};

	/**
	 * AllMetadataToBinary
	 */
	private AllMetadataToBinary()
	{
	}
	
	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		for (int i = 0; i < bases.length; i++)
		{
			String base = bases[i];
			String directory = directories[i];
			
			MetadataToBinary.main(new String[] { base, directory} );
		}
	}
}
