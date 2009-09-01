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
package com.aptana.ide.editor.scriptdoc.runtime;

import com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.bnf.IReductionContext;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class SDBCHandler extends UnifiedReductionHandler
{
	private ScriptDocVM _vm;
	
	/**
	 * SDBCHandler
	 */
	public SDBCHandler()
	{
		this._vm = new ScriptDocVM();
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler#beforeParse(com.aptana.ide.parsing.IParseState, com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void beforeParse(IParseState parseState, IParseNode parentNode)
	{
		super.beforeParse(parseState, parentNode);
		
		this._vm.clearOpcodes();
	}

	/**
	 * @see com.aptana.ide.editors.unified.parsing.UnifiedReductionHandler#reduce(com.aptana.ide.parsing.bnf.IReductionContext)
	 */
	public void reduce(IReductionContext context)
	{
		String actionName = context.getAction();

		if (actionName != null && actionName.length() > 0)
		{
			ScriptDocVMHandlerAction action = ScriptDocVMHandlerAction.get(actionName);

			if (action == null)
			{
				throw new IllegalArgumentException("Unknown action: " + actionName); //$NON-NLS-1$
			}

			switch (action)
			{
				case ADD_STATEMENT:
					// do nothing
					break;

				case DUPLICATE:
					this._vm.addDuplicate();
					break;

				case FIRST_STATEMENT:
					// do nothing
					break;

				case GET:
					break;

				case GET_GLOBAL:
					break;

				case INSTANTIATE:
					break;

				case INVOKE:
					break;

				case NO_OPERATION:
					break;

				case POP:
					break;

				case PUSH:
					break;

				case PUT:
					break;

				case SWAP:
					break;

				default:
					throw new IllegalArgumentException("Missing handler for action: " + actionName); //$NON-NLS-1$
			}
		}
	}
}
