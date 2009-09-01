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
package com.aptana.ide.scripting.swt;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author paul
 */
public class Button extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -3906360496177423270L;
	private org.eclipse.swt.widgets.Button _button;

	/*
	 * Constructors
	 */

	/**
	 * Button
	 */
	public Button()
	{
	}

	/*
	 * Methods
	 */

	/**
	 * finishInit
	 * 
	 * @param scope
	 * @param constructor
	 * @param prototype
	 */
	public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype)
	{
		// constructor.defineProperty("separator", java.io.File.separator, READONLY | PERMANENT);
	}

	/**
	 * @see org.mozilla.javascript.Scriptable#getClassName()
	 */
	public String getClassName()
	{
		return "Button"; //$NON-NLS-1$
	}

	/**
	 * Construct a new JS File object
	 * 
	 * @param cx
	 * @param args
	 * @param ctorObj
	 * @param inNewExpr
	 * @return Scriptable
	 */
	public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr)
	{
		Button button = new Button();

		Object parent = args[0];

		int style = (int) Context.toNumber(args[1]);

		if (parent instanceof Shell)
		{
			parent = ((Shell) parent)._shell;
		}

		if (parent instanceof Composite)
		{
			button._button = new org.eclipse.swt.widgets.Button((Composite) parent, style);
		}
		else
		{
			// TODO: error
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Button_Parent_Not_Composite);
		}
		return button;
	}

	/**
	 * jsFunction_setGridLayoutData
	 * 
	 * @param style
	 */
	public void jsFunction_setGridLayoutData(int style)
	{
		_button.setLayoutData(new GridData(style));
	}

	/*
	 * Properties
	 */

	/**
	 * jsGet_text
	 * 
	 * @return String
	 */
	public String jsGet_text()
	{
		return this._button.getText();
	}

	/**
	 * jsSet_text
	 * 
	 * @param text
	 */
	public void jsSet_text(String text)
	{
		this._button.setText(text);
	}
}
