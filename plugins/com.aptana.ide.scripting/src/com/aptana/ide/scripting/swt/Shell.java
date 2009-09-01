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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
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
public class Shell extends ScriptableObject
{
	org.eclipse.swt.widgets.Shell _shell;

	/*
	 * Fields
	 */
	private static final long serialVersionUID = 8520153602658649385L;

	/*
	 * Constructors
	 */

	/**
	 * Shell
	 */
	public Shell()
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
		return "Shell"; //$NON-NLS-1$
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
		String text = null;
		int style = org.eclipse.swt.SWT.SHELL_TRIM;

		Shell shell = new Shell();

		if (args.length > 0)
		{
			text = Context.toString(args[0]);
		}
		else if (args.length > 1)
		{
			style = (int) Context.toNumber(args[1]);
		}

		shell._shell = new org.eclipse.swt.widgets.Shell(style);

		if (text != null)
		{
			shell._shell.setText(text);
		}

		return shell;
	}

	/**
	 * jsFunction_open
	 */
	public void jsFunction_open()
	{
		this._shell.open();
	}

	/**
	 * jsFunction_pack
	 */
	public void jsFunction_pack()
	{
		this._shell.pack();
	}

	/**
	 * jsFunction_center
	 */
	public void jsFunction_center()
	{
		Rectangle parentSize;
		Composite parent = this._shell.getParent();

		if (parent == null)
		{
			parentSize = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
		}
		else
		{
			parentSize = parent.getBounds();
		}

		Rectangle mySize = this._shell.getBounds();

		int locationX, locationY;
		locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
		locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;
		this._shell.setLocation(new Point(locationX, locationY));
	}

	/**
	 * jsFunction_setGridLayout
	 * 
	 * @param columns
	 * @param equalWidth
	 */
	public void jsFunction_setGridLayout(int columns, boolean equalWidth)
	{
		_shell.setLayout(new GridLayout(columns, equalWidth));
	}

	/**
	 * jsFunction_setSize
	 * 
	 * @param width
	 * @param height
	 */
	public void jsFunction_setSize(int width, int height)
	{
		_shell.setSize(width, height);
	}

	/**
	 * jsFunction_eventLoop
	 */
	public void jsFunction_eventLoop()
	{
		try
		{
			while (!_shell.isDisposed())
			{
				if (!_shell.getDisplay().readAndDispatch())
				{
					_shell.getDisplay().sleep();
				}
			}
		}
		catch (RuntimeException e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Shell_Error, e);
		}
	}

	/*
	 * Properties
	 */

	/**
	 * jsGet_isDisposed
	 * 
	 * @return boolean
	 */
	public boolean jsGet_isDisposed()
	{
		return this._shell.isDisposed();
	}

	/**
	 * jsGet_display
	 * 
	 * @return Display
	 */
	public Display jsGet_display()
	{
		Display display = this._shell.getDisplay();
		return display;
	}
}
