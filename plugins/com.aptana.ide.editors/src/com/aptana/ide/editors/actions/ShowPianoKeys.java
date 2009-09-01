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
 * with certain Eclipse Public Licensed code and certain additional terms
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

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;

/**
 * Action class to show piano key coloring in the editors
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ShowPianoKeys extends TextEditorAction
{

	/**
	 * ShowPianoKeys
	 */
	public ShowPianoKeys()
	{
		super(Messages.getResourceBundle(), "ShowPianoKeysAction.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$
		update();
	}

	/*
	 * Fields
	 */
	private static boolean state = false;

	/*
	 * Properties
	 */

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
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		// get button state
		state = this.isChecked();

		// save preference
		UnifiedEditorsPlugin plugin = UnifiedEditorsPlugin.getDefault();
		Preferences prefs = plugin.getPluginPreferences();
		prefs.setValue(IPreferenceConstants.SHOW_PIANO_KEYS, state);
		plugin.savePluginPreferences();

		// update editor
		setPianoKeys(state);
	}

	/**
	 * setPianoKeys
	 * 
	 * @param state
	 */
	private void setPianoKeys(boolean state)
	{
		try
		{
			IEditorPart editor = getTextEditor();

			if (editor instanceof IUnifiedEditor)
			{
				IUnifiedEditor uniEditor = (IUnifiedEditor) editor;

				if (uniEditor != null)
				{
					uniEditor.showPianoKeys(state);

					// Set piano key state of other open editors of this same language type
					if (uniEditor.getBaseContributor() != null)
					{
						String language = uniEditor.getBaseContributor().getLocalContentType();
						IWorkbench wb = PlatformUI.getWorkbench();
						if (wb != null && language != null)
						{
							IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
							if (window != null)
							{
								IWorkbenchPage page = window.getActivePage();
								if (page != null)
								{
									IEditorReference[] refs = page.getEditorReferences();
									for (int i = 0; refs != null && i < refs.length; i++)
									{
										IEditorPart openEditor = refs[i].getEditor(false);
										if (openEditor != null && openEditor instanceof IUnifiedEditor)
										{
											IUnifiedEditorContributor contrib = ((IUnifiedEditor) openEditor)
													.getBaseContributor();
											if (contrib != null && language.equals(contrib.getLocalContentType()))
											{
												((IUnifiedEditor) openEditor).showPianoKeys(state);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.ShowPianoKeys_ERR_ErrorSettingPianoKeysPreferences, e);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update()
	{
		UnifiedEditorsPlugin plugin = UnifiedEditorsPlugin.getDefault();
		Preferences prefs = plugin.getPluginPreferences();
		state = prefs.getBoolean(IPreferenceConstants.SHOW_PIANO_KEYS);
		setChecked(state);
	}

}
