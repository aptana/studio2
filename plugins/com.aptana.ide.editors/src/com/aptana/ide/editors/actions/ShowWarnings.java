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
package com.aptana.ide.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.FileService;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class ShowWarnings extends BaseAction implements IWorkbenchWindowActionDelegate, IViewActionDelegate
{
	private static boolean state = UnifiedEditorsPlugin.getDefault()
	.getPreferenceStore().getBoolean(IPreferenceConstants.SHOW_WARNINGS);
	

	/**
	 * 
	 */
	public ShowWarnings() {
		this.setChecked(state);
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return UnifiedEditorsPlugin.getImageDescriptor("icons/warning.png"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	public String getText() {
		return Messages.ShowWarnings_TEXT;
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#getStyle()
	 */
	public int getStyle() {
		return Action.AS_CHECK_BOX;
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run(){
		run(this);
	}

	/**
	 * isInstanceChecked
	 *
	 * @return boolean
	 */
	public static boolean isInstanceChecked()
	{
		return state;
	}

	/**
	 * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
	 * UI.
	 * 
	 * @param action
	 */
	public void run(IAction action)
	{
		state = action.isChecked();
		IPreferenceStore preferenceStore = UnifiedEditorsPlugin.getDefault()
		.getPreferenceStore();
		preferenceStore.setValue(IPreferenceConstants.SHOW_WARNINGS, state);		
		String[] keys = FileContextManager.getKeySet();
		for( int i = 0; i < keys.length; i++ ) {
			FileService fc = FileContextManager.get(keys[i]);
			if(fc != null)
			{
				fc.forceContentChangedEvent();
			}
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
	 * this can only happen after the delegate has been created.
	 * 
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	/**
	 * We can use this method to dispose of any system resources we previously allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()
	{
	}

	/**
	 * We will cache window object in order to be able to provide parent shell for the message dialog.
	 * 
	 * @param window
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window)
	{
		//state = this.isChecked();
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		
	}

	

}