/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.internal.ui.text.spelling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;

import com.aptana.internal.ui.text.spelling.engine.DefaultSpellChecker;
import com.aptana.internal.ui.text.spelling.engine.ISpellCheckEngine;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;
import com.aptana.internal.ui.text.spelling.engine.ISpellDictionary;
import com.aptana.internal.ui.text.spelling.engine.LocaleSensitiveSpellDictionary;
import com.aptana.internal.ui.text.spelling.engine.PersistentSpellDictionary;
import com.aptana.semantic.ui.text.spelling.Activator;

/**
 * Spell check engine for Java source spell checking.
 * 
 * @since 3.0
 */
public class SpellCheckEngine implements ISpellCheckEngine,
		IPropertyChangeListener {

	/** The dictionary location */
	public static final String DICTIONARY_LOCATION = "dicts/"; //$NON-NLS-1$

	/** The singleton engine instance */
	private static ISpellCheckEngine fgEngine = null;

	/**
	 * Caches the locales of installed dictionaries.
	 * 
	 * @since 3.3
	 */
	private static Set fgLocalesWithInstalledDictionaries;

	/**
	 * Returns the locales for which this spell check engine has dictionaries in
	 * certain location.
	 * 
	 * @param location
	 *            dictionaries location
	 * @return The available locales for this engine
	 */
	private static Set getLocalesWithInstalledDictionaries(URL location) {
		String[] fileNames;
		try {
			final URL url = FileLocator.toFileURL(location);
			final File file = new File(url.getFile());
			if (!file.isDirectory()) {
				return Collections.EMPTY_SET;
			}
			fileNames = file.list();
			if (fileNames == null) {
				return Collections.EMPTY_SET;
			}
		} catch (final IOException ex) {
			Activator.log(ex);
			return Collections.EMPTY_SET;
		}

		final Set localesWithInstalledDictionaries = new HashSet();
		final int fileNameCount = fileNames.length;
		for (int i = 0; i < fileNameCount; i++) {
			final String fileName = fileNames[i];
			final int localeEnd = fileName.indexOf(".dictionary"); //$NON-NLS-1$ 
			if (localeEnd > 1) {
				final String localeName = fileName.substring(0, localeEnd);
				final int languageEnd = localeName.indexOf('_');
				if (languageEnd == -1) {
					localesWithInstalledDictionaries
							.add(new Locale(localeName));
				} else if ((languageEnd == 2) && (localeName.length() == 5)) {
					localesWithInstalledDictionaries.add(new Locale(localeName
							.substring(0, 2), localeName.substring(3)));
				} else if ((localeName.length() > 6)
						&& (localeName.charAt(5) == '_')) {
					localesWithInstalledDictionaries.add(new Locale(localeName
							.substring(0, 2), localeName.substring(3, 5),
							localeName.substring(6)));
				}
			}
		}

		return localesWithInstalledDictionaries;
	}

	/**
	 * Returns the locales for which this spell check engine has dictionaries.
	 * 
	 * @return The available locales for this engine
	 */
	public static Set getLocalesWithInstalledDictionaries() {
		if (fgLocalesWithInstalledDictionaries != null) {
			return fgLocalesWithInstalledDictionaries;
		}

		Enumeration locations;
		try {
			locations = getDictionaryLocations();
			if (locations == null) {
				return fgLocalesWithInstalledDictionaries = Collections.EMPTY_SET;
			}
		} catch (final IOException ex) {
			Activator.log(ex);
			return fgLocalesWithInstalledDictionaries = Collections.EMPTY_SET;
		}

		fgLocalesWithInstalledDictionaries = new HashSet();

		while (locations.hasMoreElements()) {
			final URL location = (URL) locations.nextElement();
			final Set locales = getLocalesWithInstalledDictionaries(location);
			fgLocalesWithInstalledDictionaries.addAll(locales);
		}

		return fgLocalesWithInstalledDictionaries;
	}

	/**
	 * Returns the default locale for this engine.
	 * 
	 * @return The default locale
	 */
	public static Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	/**
	 * Returns the dictionary closest to the given locale.
	 * 
	 * @param locale
	 *            the locale
	 * @return the dictionary or <code>null</code> if none is suitable
	 * @since 3.3
	 */
	public ISpellDictionary findDictionary(Locale locale) {
		final ISpellDictionary dictionary = (ISpellDictionary) this.fLocaleDictionaries
				.get(locale);
		if (dictionary != null) {
			return dictionary;
		}

		// Try same language
		final String language = locale.getLanguage();
		final Iterator iter = this.fLocaleDictionaries.entrySet().iterator();
		while (iter.hasNext()) {
			final Entry entry = (Entry) iter.next();
			final Locale dictLocale = (Locale) entry.getKey();
			if (dictLocale.getLanguage().equals(language)) {
				return (ISpellDictionary) entry.getValue();
			}
		}

		final ISpellDictionary next = (ISpellDictionary) this.fLocaleDictionaries
				.values().iterator().next();
		return next;
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellCheckEngine#
	 * findDictionary(java.util.Locale)
	 * 
	 * @since 3.3
	 */
	public static Locale findClosestLocale(Locale locale) {
		if ((locale == null) || (locale.toString().length() == 0)) {
			return locale;
		}

		if (getLocalesWithInstalledDictionaries().contains(locale)) {
			return locale;
		}

		// Try same language
		final String language = locale.getLanguage();
		final Iterator iter = getLocalesWithInstalledDictionaries().iterator();
		while (iter.hasNext()) {
			final Locale dictLocale = (Locale) iter.next();
			if (dictLocale.getLanguage().equals(language)) {
				return dictLocale;
			}
		}

		// Try whether American English is present
		final Locale defaultLocale = Locale.US;
		if (getLocalesWithInstalledDictionaries().contains(defaultLocale)) {
			return defaultLocale;
		}

		return null;
	}

	/**
	 * Returns the enumeration of URLs for the dictionary locations where the
	 * Platform dictionaries are located.
	 * <p>
	 * This is in <code>org.eclipse.jdt.ui/dictionaries/</code> which can also
	 * be populated via fragments.
	 * </p>
	 * 
	 * @throws IOException
	 *             if there is an I/O error
	 * @return The dictionary locations, or <code>null</code> iff the locations
	 *         are not known
	 */
	public static Enumeration getDictionaryLocations() throws IOException {
		final Activator plugin = Activator.getDefault();
		if (plugin != null) {
			return plugin.getBundle().getResources("/" + DICTIONARY_LOCATION); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns the singleton instance of the spell check engine.
	 * 
	 * @return The singleton instance of the spell check engine
	 */
	public static final synchronized ISpellCheckEngine getInstance() {

		if (fgEngine == null) {
			fgEngine = new SpellCheckEngine();
		}

		return fgEngine;
	}

	/**
	 * Shuts down the singleton instance of the spell check engine.
	 */
	public static final synchronized void shutdownInstance() {
		if (fgEngine != null) {
			fgEngine.shutdown();
			fgEngine = null;
		}
	}

	/** The registered locale insensitive dictionaries */
	private Set fGlobalDictionaries = new HashSet();

	/** The spell checker for fLocale */
	private ISpellChecker fChecker = null;

	/** The registered locale sensitive dictionaries */
	private Map fLocaleDictionaries = new HashMap();

	/** The user dictionary */
	private ISpellDictionary fUserDictionary = null;

	/**
	 * Creates a new spell check manager.
	 */
	private SpellCheckEngine() {

		// fGlobalDictionaries.add(new TaskTagDictionary());
		// fGlobalDictionaries.add(new HtmlTagDictionary());
		// fGlobalDictionaries.add(new JavaDocTagDictionary());

		try {

			Locale locale = null;
			final Enumeration locations = getDictionaryLocations();

			while ((locations != null) && locations.hasMoreElements()) {
				final URL location = (URL) locations.nextElement();

				for (final Iterator iterator = getLocalesWithInstalledDictionaries(
						location).iterator(); iterator.hasNext();) {

					locale = (Locale) iterator.next();
					this.fLocaleDictionaries
							.put(locale, new LocaleSensitiveSpellDictionary(
									locale, location));
				}
			}

		} catch (final IOException exception) {
			// Do nothing
		}

		Activator.getSpellingPreferenceStore().addPropertyChangeListener(this);
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellCheckEngine#
	 * getSpellChecker()
	 */
	public final synchronized ISpellChecker getSpellChecker()
			throws IllegalStateException {
		if (this.fGlobalDictionaries == null) {
			throw new IllegalStateException("spell checker has been shut down"); //$NON-NLS-1$
		}

		final IPreferenceStore store = Activator.getSpellingPreferenceStore();
		final Locale locale = this.getCurrentLocale(store);
		if ((this.fUserDictionary == null) && "".equals(locale.toString())) { //$NON-NLS-1$
			return null;
		}

		if ((this.fChecker != null) && this.fChecker.getLocale().equals(locale)) {
			return this.fChecker;
		}

		this.resetSpellChecker();

		this.fChecker = new DefaultSpellChecker(store, locale);
		this.resetUserDictionary();

		for (final Iterator iterator = this.fGlobalDictionaries.iterator(); iterator
				.hasNext();) {
			final ISpellDictionary dictionary = (ISpellDictionary) iterator
					.next();
			this.fChecker.addDictionary(dictionary);
		}

		final ISpellDictionary dictionary = this.findDictionary(this.fChecker
				.getLocale());
		if (dictionary != null) {
			this.fChecker.addDictionary(dictionary);
		}

		return this.fChecker;
	}

	/**
	 * Returns the current locale of the spelling preferences.
	 * 
	 * @param store
	 *            the preference store
	 * @return The current locale of the spelling preferences
	 */
	private Locale getCurrentLocale(IPreferenceStore store) {
		return (Locale.getDefault());// store.getString(PreferenceConstants.
										// SPELLING_LOCALE));
	}

	public static Locale convertToLocale(String locale) {
		final Locale defaultLocale = SpellCheckEngine.getDefaultLocale();
		if ((locale == null) || locale.equals(defaultLocale.toString())) {
			return defaultLocale;
		}

		if (locale.length() >= 5) {
			return new Locale(locale.substring(0, 2), locale.substring(3, 5));
		}

		return new Locale(""); //$NON-NLS-1$
	}

	/*
	 * @see
	 * org.eclipse.jdt.ui.text.spelling.engine.ISpellCheckEngine#getLocale()
	 */
	public synchronized final Locale getLocale() {
		if (this.fChecker == null) {
			return null;
		}

		return this.fChecker.getLocale();
	}

	/*
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	public final void propertyChange(final PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.SPELLING_LOCALE)) {
			this.resetSpellChecker();
			return;
		}

		if (event.getProperty().equals(
				PreferenceConstants.SPELLING_USER_DICTIONARY)) {
			this.resetUserDictionary();
			return;
		}

		if (event.getProperty().equals(
				PreferenceConstants.SPELLING_USER_DICTIONARY_ENCODING)) {
			this.resetUserDictionary();
			return;
		}
	}

	/**
	 * Resets the current checker's user dictionary.
	 */
	private synchronized void resetUserDictionary() {
		if (this.fChecker == null) {
			return;
		}

		// Update user dictionary
		if (this.fUserDictionary != null) {
			this.fChecker.removeDictionary(this.fUserDictionary);
			this.fUserDictionary.unload();
			this.fUserDictionary = null;
		}

		final IPreferenceStore store = Activator.getSpellingPreferenceStore();
		// Activator.getSpellingPreferenceStore()();
		final String filePath = store
				.getString(PreferenceConstants.SPELLING_USER_DICTIONARY);
		// IStringVariableManager variableManager=
		// VariablesPlugin.getDefault().getStringVariableManager();
		// try {
		// filePath= variableManager.performStringSubstitution(filePath);
		// } catch (CoreException e) {
		// JavaPlugin.log(e);
		// return;
		// }
		if (filePath.length() > 0) {
			try {
				final File file = new File(filePath);
				if (!file.exists() && !file.createNewFile()) {
					return;
				}

				final URL url = new URL("file", null, filePath); //$NON-NLS-1$
				final InputStream stream = url.openStream();
				if (stream != null) {
					try {
						this.fUserDictionary = new PersistentSpellDictionary(
								url);
						this.fChecker.addDictionary(this.fUserDictionary);
					} finally {
						stream.close();
					}
				}
			} catch (final MalformedURLException exception) {
				// Do nothing
			} catch (final IOException exception) {
				// Do nothing
			}
		}
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellCheckEngine#
	 * registerDictionary
	 * (com.onpositive.internal.ui.text.spelling.engine.ISpellDictionary)
	 */
	public synchronized final void registerGlobalDictionary(
			final ISpellDictionary dictionary) {
		this.fGlobalDictionaries.add(dictionary);
		this.resetSpellChecker();
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellCheckEngine#
	 * registerDictionary(java.util.Locale,
	 * com.onpositive.internal.ui.text.spelling.engine.ISpellDictionary)
	 */
	public synchronized final void registerDictionary(final Locale locale,
			final ISpellDictionary dictionary) {
		this.fLocaleDictionaries.put(locale, dictionary);
		this.resetSpellChecker();
	}

	/*
	 * @see
	 * com.onpositive.internal.ui.text.spelling.engine.ISpellCheckEngine#unload
	 * ()
	 */
	public synchronized final void shutdown() {

		Activator.getSpellingPreferenceStore().removePropertyChangeListener(this);

		ISpellDictionary dictionary = null;
		for (final Iterator iterator = this.fGlobalDictionaries.iterator(); iterator
				.hasNext();) {
			dictionary = (ISpellDictionary) iterator.next();
			dictionary.unload();
		}
		this.fGlobalDictionaries = null;

		for (final Iterator iterator = this.fLocaleDictionaries.values()
				.iterator(); iterator.hasNext();) {
			dictionary = (ISpellDictionary) iterator.next();
			dictionary.unload();
		}
		this.fLocaleDictionaries = null;

		this.fUserDictionary = null;
		this.fChecker = null;
	}

	private synchronized void resetSpellChecker() {
		if (this.fChecker != null) {
			final ISpellDictionary dictionary = (ISpellDictionary) this.fLocaleDictionaries
					.get(this.fChecker.getLocale());
			if (dictionary != null) {
				dictionary.unload();
			}
		}
		this.fChecker = null;
	}

	/*
	 * @seeorg.eclipse.jdt.ui.text.spelling.engine.ISpellCheckEngine#
	 * unregisterDictionary
	 * (org.eclipse.jdt.ui.text.spelling.engine.ISpellDictionary)
	 */
	public synchronized final void unregisterDictionary(
			final ISpellDictionary dictionary) {
		this.fGlobalDictionaries.remove(dictionary);
		this.fLocaleDictionaries.values().remove(dictionary);
		dictionary.unload();
		this.resetSpellChecker();
	}
}
