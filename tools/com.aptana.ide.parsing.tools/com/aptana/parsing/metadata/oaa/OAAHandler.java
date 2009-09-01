/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.parsing.metadata.oaa;

import java.util.List;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.bnf.AbstractHandler;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.parsing.nodes.ParseFragment;

/**
 * @author Kevin Lindsey
 */
public class OAAHandler extends AbstractHandler
{
	/**
	 * onAddSimpleAndExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddSimpleAndExpression(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ParseFragment result = (ParseFragment) nodes[0];
		
		return result;
	}
	
	/**
	 * onAddStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onAddStatement(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		ParseFragment result = (ParseFragment) nodes[0];
		
		return result;
	}
	
	/**
	 * onFirstSimpleAndExpression
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstSimpleAndExpression(IReductionContext context)
	{
		ParseFragment result = new ParseFragment();
		
		return result;
	}
	
	/**
	 * onFirstStatement
	 * 
	 * @param context
	 * @return
	 */
	public Object onFirstStatement(IReductionContext context)
	{
		ParseFragment result = new ParseFragment();
		
		return result;
	}
	
	/**
	 * onElementDefinition
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object onElementDefinition(IReductionContext context)
	{
		Object[] nodes = context.getNodes();
		Lexeme var = (Lexeme) nodes[0];
		Lexeme name = (Lexeme) nodes[3];
		List<String> references = (List<String>) nodes[5];
		
		OAAElementDefinition result = new OAAElementDefinition(var.getText(), name.getText());
		
		for (String reference : references)
		{
			result.addReference(reference);
		}
		
		return result;
	}
}
