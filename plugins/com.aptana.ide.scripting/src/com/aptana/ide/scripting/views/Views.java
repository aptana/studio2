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
package com.aptana.ide.scripting.views;

import java.util.Hashtable;

import org.eclipse.ui.IWorkbenchPart;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.scripting.events.EventTarget;

/**
 * @author Kevin Lindsey
 */
public class Views extends EventTarget
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -1034231442157154583L;

	private Hashtable _scriptableViews;
	private ProfilesView _profilesView;
	private ActionsView _actionsView;
	//private OutlineView _outlineView;
	private ResourceNavigator _navigatorView;
	private ProblemsView _problemsView;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Views"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Views
	 * 
	 * @param scope
	 */
	public Views(Scriptable scope)
	{
		this.setParentScope(scope);

		this._scriptableViews = new Hashtable();

		String[] names = new String[] { "getView" }; //$NON-NLS-1$
		this.defineFunctionProperties(names, Views.class, READONLY | PERMANENT);

		this.defineProperty("all", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("actionsView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("navigatorView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("profilesView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
		//this.defineProperty("outlineView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("problemsView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

//	/**
//	 * @return Scriptable
//	 */
//	public Scriptable getOutlineView()
//	{
//		if (this._outlineView == null)
//		{
//			IWorkbenchPart part = getViewInternal(IPageLayout.ID_OUTLINE);
//
//			this._outlineView = new com.aptana.ide.scripting.views.OutlineView(this.getParentScope(), part);
//		}
//
//		return this._outlineView;
//	}

	/**
	 * @return Scriptable
	 */
	public Scriptable getProfilesView()
	{
		if (this._profilesView == null)
		{
			IWorkbenchPart part = getViewInternal("com.aptana.ide.js.ui.views.profilesView"); //$NON-NLS-1$

			this._profilesView = new com.aptana.ide.scripting.views.ProfilesView(this.getParentScope(), part);
		}

		return this._profilesView;
	}

	/**
	 * getActionsView
	 * 
	 * @return Scriptable
	 */
	public Scriptable getActionsView()
	{
		if (this._actionsView == null)
		{
			IWorkbenchPart part = getViewInternal("com.aptana.ide.js.ui.views.actionsView"); //$NON-NLS-1$

			this._actionsView = new com.aptana.ide.scripting.views.ActionsView(this.getParentScope(), part);
		}

		return this._actionsView;
	}

	/**
	 * setProfilesView
	 * 
	 * @param part
	 */
	public void setInternalProfilesView(IWorkbenchPart part)
	{
		if (this._profilesView != null)
		{
			IWorkbenchPart oldPart = this._profilesView.getView();

			if (oldPart != part)
			{
				com.aptana.ide.editors.views.profiles.ProfilesView profileView = (com.aptana.ide.editors.views.profiles.ProfilesView) part;

				if (oldPart != null)
				{
					profileView.removeProfilesViewEventListener(this._profilesView);
				}

				this._profilesView.setView(part);

				if (part != null)
				{
					profileView.addProfilesViewEventListener(this._profilesView);
				}
			}
		}
	}

	/**
	 * get all views
	 * 
	 * @param thisObj
	 * @return Scriptable
	 */
	public static Scriptable getAll(ScriptableObject thisObj)
	{
		// TODO: Views.getAll not implemented

		Context cx = Context.getCurrentContext();
		Scriptable scope = ScriptableObject.getTopLevelScope(thisObj);
		Scriptable result = cx.newObject(scope, "Array"); //$NON-NLS-1$

		return result;
	}

	/**
	 * getView Get a generic scritable view
	 * 
	 * @param id
	 * @return ProblemsView
	 */
	public ScriptableView getView(String id)
	{
		ScriptableView _scriptableView = null;

		if (id == null)
		{
			return null;
		}

		if (_scriptableViews.containsKey(id))
		{
			_scriptableView = (ScriptableView) _scriptableViews.get(id);
		}
		else
		{
			IWorkbenchPart part = getViewInternal("com.aptana.ide.js.ui.views.GenericScriptableView", id); //$NON-NLS-1$

			_scriptableView = new ScriptableView(this.getParentScope(), part, id);
			_scriptableViews.put(id, _scriptableView);
		}

		return _scriptableView;
	}

	/**
	 * getProblemsView
	 * 
	 * @return ProblemsView
	 */
	public ScriptableView getProblemsView()
	{
		if (this._problemsView == null)
		{
			IWorkbenchPart part = getViewInternal("com.aptana.ide.js.ui.views.problemsView"); //$NON-NLS-1$

			this._problemsView = new ProblemsView(this.getParentScope(), part);
		}

		return _problemsView;
	}

	//
	/**
	 * getNavigatorView
	 * 
	 * @return Scriptable
	 */
	public Scriptable getNavigatorView()
	{
		if (this._navigatorView == null)
		{
			IWorkbenchPart part = getViewInternal("org.eclipse.ui.views.ResourceNavigator"); //$NON-NLS-1$
			// org.eclipse.ui.views.navigator.ResourceNavigator nav = (org.eclipse.ui.views.navigator.ResourceNavigator)
			// part;
			// nav.

			this._navigatorView = new ResourceNavigator(this.getParentScope(), part);
		}

		return this._navigatorView;
	}

	/**
	 * getView
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Scriptable
	 */
	public static Scriptable getView2(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		String id = Context.toString(args[0]);
		IWorkbenchPart part = getViewInternal(id);
		Scriptable scope = thisObj.getParentScope();
		View result;

		if (part instanceof ScriptableView)
		{
			result = new ScriptableView(scope, part, id);
		}
		else if (part instanceof ProfilesView)
		{
			result = new ProfilesView(scope, part);
		}
		else
		{
			result = new View(scope, part);
		}

		return result;
	}

	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @return Scriptable
	 */
	public static IWorkbenchPart getViewInternal(final String id)
	{
		return getViewInternal(id, null);
	}

	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @param secondaryId
	 * @return IWorkbenchPart
	 */
	public static IWorkbenchPart getViewInternal(String id, String secondaryId)
	{
		return CoreUIUtils.getViewInternal(id, secondaryId);
	}
}
