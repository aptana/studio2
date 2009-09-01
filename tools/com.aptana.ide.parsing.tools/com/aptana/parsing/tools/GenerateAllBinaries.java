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
public final class GenerateAllBinaries
{
	/**
	 * GenerateAllBinaries
	 */
	private GenerateAllBinaries()
	{
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		AllMetadataToBinary.main(new String[0]);
		AllScriptDocsToBinary.main(new String[0]);
		
		System.out.println(Messages.getString("GenerateAllBinaries.Generation_Complete")); //$NON-NLS-1$
	}
}
