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
package com.aptana.ide.parsing.xpath;

import java.util.ArrayList;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;

/**
 * @author Kevin Lindsey
 */
public class SubstringFunction implements Function
{
	/**
	 * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
	 */
	public Object call(Context context, List args) throws FunctionCallException
	{
		List<String> result = null;

		if (args.size() == 2 && args.get(0) instanceof List)
		{
			// get context's navigator
			Navigator navigator = context.getNavigator();
			
			// get parameters
			Object arg = args.get(0);
			
			if (arg instanceof List)
			{
				List nodeSet = (List) arg;
				int startingOffset = NumberFunction.evaluate(args.get(1), navigator).intValue();
	
				// create container for resulting node set 
				result = new ArrayList<String>(nodeSet.size());
	
				// process the passed-in node set
				for (int i = 0; i < nodeSet.size(); i++)
				{
					String value = StringFunction.evaluate(nodeSet.get(i), navigator);
					String newValue;
					
					if (startingOffset >= 0)
					{
						newValue = value.substring(startingOffset);
					}
					else
					{
						// TODO: not tested
						int start = value.length() + startingOffset - 1;
						
						if (start >= 0)
						{
							newValue = value.substring(start, value.length());
						}
						else
						{
							newValue = ""; //$NON-NLS-1$
						}
					}
	
					result.add(newValue);
				}
			}
		}

		return result;
	}
}
