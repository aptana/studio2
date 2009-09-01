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

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.views.actions.Action;
import com.aptana.ide.editors.views.actions.ActionSet;
import com.aptana.ide.editors.views.actions.ActionsManager;
import com.aptana.ide.editors.views.actions.ActionsViewEventTypes;
import com.aptana.ide.editors.views.actions.IAction;
import com.aptana.ide.editors.views.actions.IActionsViewEventListener;
import com.aptana.ide.scripting.Global;
import com.aptana.ide.scripting.ScriptInfo;
import com.aptana.ide.scripting.ScriptingEngine;
import com.aptana.ide.scripting.ScriptingPlugin;
import com.aptana.ide.scripting.editors.Editor;
import com.aptana.ide.scripting.events.ActionsAddEvent;
import com.aptana.ide.scripting.events.ActionsCreateSetEvent;
import com.aptana.ide.scripting.events.ActionsDeleteEvent;
import com.aptana.ide.scripting.events.ActionsDeleteSetEvent;
import com.aptana.ide.scripting.events.ActionsExecuteEvent;
import com.aptana.ide.scripting.events.Event;
import com.aptana.ide.scripting.io.File;

/**
 * @author Kevin Lindsey
 */
public class ActionsView extends View implements IActionsViewEventListener
{
	private static final long serialVersionUID = -4383236968215071564L;

	/**
	 * Create a new instance of ActiveLibrariesView
	 * 
	 * @param scope
	 * @param view
	 */
	public ActionsView(Scriptable scope, IWorkbenchPart view)
	{
		super(scope, view);

		// define properties
		this.defineProperty("actions", ActionsView.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("actionSets", ActionsView.class, READONLY | PERMANENT); //$NON-NLS-1$

		// define functions
		String[] names = new String[] { "addAction", "createActionSet", "editAction", "expandActionSet", "expandAll", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"getActionSet", "refresh", "removeActionSet", "selectAndReveal" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		this.defineFunctionProperties(names, ActionsView.class, READONLY | PERMANENT);
	}

	/**
	 * addAction
	 * 
	 * @param name
	 * @param filepath
	 * @return Action
	 */
	public Action addAction(String name, String filepath)
	{
		return this.getActionsManager().addAction(name, filepath);
	}

	/**
	 * getActions
	 * 
	 * @return IAction[]
	 */
	public IAction[] getActions()
	{
		return this.getActionsManager().getAll();
	}

	/**
	 * getActionsManager
	 * 
	 * @return ActionsManager
	 */
	private ActionsManager getActionsManager()
	{
		return UnifiedEditorsPlugin.getDefault().getActionsManager();
	}

	/**
	 * expandAll
	 */
	public void expandAll()
	{
		IWorkbenchPart part = this.getView();

		if (part != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) part;
			actionsView.expandAll();
		}
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		IWorkbenchPart part = this.getView();

		if (part != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) part;
			actionsView.refresh();
		}
	}

	/**
	 * open a file via the actions view (so events bind)
	 * 
	 * @param path
	 */
	public void editAction(String path)
	{
		IWorkbenchPart part = this.getView();

		if (part != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) part;
			actionsView.editAction(path);
		}
	}

	/**
	 * selectAndReveal
	 * 
	 * @param path
	 */
	public void selectAndReveal(String path)
	{
		IWorkbenchPart part = this.getView();

		if (part != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) part;
			actionsView.selectAndReveal(path);
		}
	}

	/**
	 * @see com.aptana.ide.scripting.views.View#setView(org.eclipse.ui.IWorkbenchPart)
	 */
	public void setView(IWorkbenchPart view)
	{
		if (this.getView() != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) this.getView();
			
			actionsView.removeActionsViewEventListener(this);
		}
		
		super.setView(view);
		
		if (view != null && view instanceof com.aptana.ide.editors.views.actions.ActionsView)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) view;

			actionsView.addActionsViewEventListener(this);
		}
	}

	/**
	 * expandProfile
	 * 
	 * @param name
	 */
	public void expandActionSet(String name)
	{
		IWorkbenchPart part = this.getView();

		if (part != null)
		{
			com.aptana.ide.editors.views.actions.ActionsView actionsView = (com.aptana.ide.editors.views.actions.ActionsView) part;
			actionsView.expandActionSet(name);
		}
	}

	/**
	 * addProfile
	 * 
	 * @param name
	 * @return Profile
	 */
	public ActionSet createActionSet(String name)
	{
		return this.getActionsManager().createActionSet(name);
	}

	/**
	 * removeProfile
	 * 
	 * @param name
	 */
	public void removeActionSet(String name)
	{
		this.getActionsManager().removeActionSet(name);
	}

	/**
	 * getProfile return a specific named profile
	 * 
	 * @param name
	 * @return ActionSet
	 */
	public ActionSet getActionSet(String name)
	{
		return this.getActionsManager().getActionSet(name);
	}

	/**
	 * getProfiles Returns all profiles
	 * 
	 * @return ActionSet[]
	 */
	public ActionSet[] getActionSets()
	{
		return this.getActionsManager().getActionSets();
	}

	/**
	 * onActionsViewEvent
	 * 
	 * @param e
	 */
	public void onActionsViewEvent(com.aptana.ide.editors.views.actions.ActionsViewEvent e)
	{
		Event event = null;
		int eventType = e.getEventType();

		switch (eventType)
		{
			case ActionsViewEventTypes.RELOAD:
				Global global = ScriptingEngine.getInstance().getGlobal();
				IAction[] actionsToReload = e.getActions();

				for (int i = 0; i < actionsToReload.length; i++)
				{
					IAction action = actionsToReload[i];
					String filename = action.getScriptPath();
					String key = global.getXrefId(filename);
					ScriptInfo info = global.getScriptInfo(key);
					Scriptable scope = info.getScope();
					Object onreload = scope.get("onreload", scope); //$NON-NLS-1$

					if (onreload instanceof Function)
					{
						Function reloadFunction = (Function) onreload;

						Context cx = Context.enter();

						try
						{
							reloadFunction.call(cx, scope, scope, new Object[] { action.getPath() });
						}
						catch (Exception e1)
						{
							IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ActionsView_Error, e1);
						}

						Context.exit();
					}
					else
					{
						global.reloadLibrary(filename);
					}
				}

				break;

			case ActionsViewEventTypes.ADD:
			case ActionsViewEventTypes.DROP:
				event = new ActionsAddEvent(this.getView(), eventType, e.getPaths());
				break;

			case ActionsViewEventTypes.ADD_CURRENT_FILE:
				Context.enter();
				IEditorPart part = ScriptingEngine.getActiveEditor();
				Editor editor = new Editor(ScriptingEngine.getInstance().getGlobal(), part);
				event = new ActionsAddEvent(this.getView(), eventType, (File) editor.getFile());
				Context.exit();
				break;

			case ActionsViewEventTypes.CREATE_ACTION_SET:
				event = new ActionsCreateSetEvent(this.getView(), eventType, e.getName());
				break;

			case ActionsViewEventTypes.DELETE:
				event = new ActionsDeleteEvent(this.getView(), eventType, e.getActions());
				break;

			case ActionsViewEventTypes.DELETE_ACTION_SET:
				event = new ActionsDeleteSetEvent(this.getView(), eventType, e.getName());
				break;

			case ActionsViewEventTypes.EXECUTE:
				IAction action = e.getActions()[0];

				if (action instanceof ActionSet)
				{
					ActionSet actionSet = (ActionSet) action;

					if (actionSet.isExecutable())
					{
						IAction[] actions = actionSet.getActions();

						Arrays.sort(actions, new Comparator()
						{
							public int compare(Object o1, Object o2)
							{
								IAction action1 = (IAction) o1;
								IAction action2 = (IAction) o2;

								return action1.getName().compareTo(action2.getName());
							}
						});

						for (int i = 0; i < actions.length; i++)
						{
							Event subEvent = new ActionsExecuteEvent(this.getView(), eventType,
									new IAction[] { actions[i] });

							this.fireEventListeners(subEvent);
						}
					}
				}
				else
				{
					event = new ActionsExecuteEvent(this.getView(), eventType, e.getActions());
				}
				break;

			default:
				throw new IllegalArgumentException(Messages.ActionsView_Unrecognized_Event_Type + eventType);
		}

		if (event != null)
		{
			this.fireEventListeners(event);
		}
	}
}
