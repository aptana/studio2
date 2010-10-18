/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Aptana, Inc. - modifications
 *******************************************************************************/
package com.aptana.commons.spelling.engine;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.osgi.framework.Bundle;

import com.aptana.internal.ui.text.spelling.PreferenceConstants;
import com.aptana.semantic.ui.text.spelling.Activator;

public class SpellingPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public SpellingPreferenceInitializer() {
	}

	
	public void initializeDefaultPreferences() {
		final Bundle bundle = Platform.getBundle("org.eclipse.jdt.ui"); //$NON-NLS-1$
		final IPreferenceStore preferenceStore = Activator.getSpellingPreferenceStore();
		if ((bundle != null) && false) {
			preferenceStore
					.setDefault(SpellingService.PREFERENCE_SPELLING_ENGINE,
							"org.eclipse.jdt.internal.ui.text.spelling.DefaultSpellingEngine"); //$NON-NLS-1$
		} else {
			preferenceStore.setDefault(
					SpellingService.PREFERENCE_SPELLING_ENGINE,
					"com.onpositive.text.spelling.DefaultSpellingEngine"); //$NON-NLS-1$
		}
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_DIGITS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_MIXED,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_NON_LETTERS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_SENTENCE,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_UPPER,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_URLS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_PROBLEMS_THRESHOLD,100);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_SINGLE_LETTERS,true);
		preferenceStore.setDefault(SpellingService.PREFERENCE_SPELLING_ENABLED,
				true);

	}

}
