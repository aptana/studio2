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
public final class AllScriptDocsToBinary
{
	private static final String pluginDirectory = "C:\\dev\\depot\\products\\ide\\plugins\\"; //$NON-NLS-1$
	
	private static final String[] inputFilenames = new String[] {
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\resources\\dom_0.xml", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\resources\\dom_2.xml", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\resources\\js_core.xml" //$NON-NLS-1$
	};
	
	private static final String[] outputFilenames = new String[] {
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\parsing\\dom_0.bin", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\parsing\\dom_2.bin", //$NON-NLS-1$
		pluginDirectory + "com.aptana.ide.editor.js\\src\\com\\aptana\\ide\\editor\\js\\parsing\\js_core.bin" //$NON-NLS-1$
	};
	
	/**
	 * AllScriptDocsToBinary
	 */
	private AllScriptDocsToBinary()
	{
	}
	
	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		for (int i = 0; i < inputFilenames.length; i++)
		{
			String filename = inputFilenames[i];
			String directory = outputFilenames[i];
			
			ScriptDocToBinary.main(new String[] { filename, directory} );
		}
	}
}
