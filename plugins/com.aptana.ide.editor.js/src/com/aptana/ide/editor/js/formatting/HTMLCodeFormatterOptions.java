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
package com.aptana.ide.editor.js.formatting;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 * 
 */
public class HTMLCodeFormatterOptions {

		/**
	 * formatterTabChar
	 */
	public String formatterTabChar;
	/**
	 * tabSize
	 */
	public int tabSize;
	
	public boolean doFormatting=true;
	
	/**
	 * 
	 */
	public HTMLCodeFormatterOptions() {
		initFromPreferences();
	}

	private void initFromPreferences() {
		AbstractUIPlugin abstractUIPlugin = ((AbstractUIPlugin)Platform.getPlugin("com.aptana.ide.editor.html")); //$NON-NLS-1$
		if ((abstractUIPlugin!=null)){
		IPreferenceStore preferenceStore = abstractUIPlugin.getPreferenceStore(); 
		
		formatterTabChar = preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		tabSize = preferenceStore
				.getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);				
		}
		else {
			initDefaults();
		}
		
	}

	private void initDefaults() {
		formatterTabChar=" "; //$NON-NLS-1$
		tabSize=4;
	}

	

	/**
	 * @param map
	 * @param project
	 * 
	 */
	public HTMLCodeFormatterOptions(Map map, IProject project) {
		if (project != null) {
			IEclipsePreferences preferences = new ProjectScope(project)
					.getNode("com.aptana.ide.editor.html"); //$NON-NLS-1$
			if (preferences!=null){
			String string = preferences.get(
					DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, null);
			if (string==null){
				initFromPreferences();
				return;
			}
			
			formatterTabChar = preferences.get(
					DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, ""); //$NON-NLS-1$
			
			tabSize = Integer.parseInt(string);
			}else{
				initDefaults();
			}
			
		} else if (map == null) {
			initFromPreferences();
		} else {
			
			formatterTabChar = (String) map
					.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
			if (formatterTabChar == null) {
				formatterTabChar = " "; //$NON-NLS-1$
			}
			Object object10 = map
					.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			if (object10 != null) {
				tabSize = Integer.parseInt(object10.toString());
			} else {
				tabSize = 4;
			}			
		}

	}

	
}
