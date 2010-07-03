/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ScriptsContentProvider implements ITreeContentProvider
{

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent)
	{

		// Get actions and action sets
		IScriptAction[] actions = ScriptActionsManager.getInstance().getAll();

		if (actions != null && actions.length > 0)
		{
			return actions;
		}

		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ScriptActionSet)
		{
			ScriptActionSet actionSet = (ScriptActionSet) parentElement;
			ScriptAction[] actions = actionSet.getScriptActions();

			return actions;
		}
		else
		{
			return new Object[0];
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof ScriptAction)
		{
			return ((ScriptAction) element).getParent();
		}
		else
		{
			// ActionSets have no parents
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof ScriptActionSet)
		{
			ScriptActionSet actionSet = (ScriptActionSet) element;

			return actionSet.getScriptActions().length > 0;
		}
		else
		{
			return false;
		}
	}

}
