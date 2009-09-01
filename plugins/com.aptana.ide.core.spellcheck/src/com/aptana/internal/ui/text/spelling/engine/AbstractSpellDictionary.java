/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.internal.ui.text.spelling.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aptana.semantic.ui.text.spelling.Activator;

/**
 * Partial implementation of a spell dictionary.
 * 
 * @since 3.0
 */
public abstract class AbstractSpellDictionary implements ISpellDictionary {

	private final ArrayList<String> allWords = new ArrayList<String>();

	public ArrayList<String> getAllWordsStartingWith(String text,
			boolean sentence) {
		final ArrayList<String> result = new ArrayList<String>();
		if (sentence) {
			text = text.toLowerCase();
		}
		for (final String s : this.allWords) {
			if (sentence) {
				if (s.toLowerCase().startsWith(text)) {
					result.add(s);
				}
			} else {
				if (s.startsWith(text)) {
					result.add(s);
				}
			}
		}
		return result;
	}

	/** The bucket capacity */
	protected static final int BUCKET_CAPACITY = 4;

	/** The word buffer capacity */
	protected static final int BUFFER_CAPACITY = 32;

	/** The distance threshold */
	protected static final int DISTANCE_THRESHOLD = 160;

	/** The hash capacity */
	protected static final int HASH_CAPACITY = 22 * 1024;

	/** The phonetic distance algorithm */
	private IPhoneticDistanceAlgorithm fDistanceAlgorithm = new DefaultPhoneticDistanceAlgorithm();

	/** The mapping from phonetic hashes to word lists */
	private final Map fHashBuckets = new HashMap(HASH_CAPACITY);

	/** The phonetic hash provider */
	private IPhoneticHashProvider fHashProvider = new DefaultPhoneticHashProvider();

	/** Is the dictionary already loaded? */
	private boolean fLoaded = false;
	/**
	 * Must the dictionary be loaded?
	 * 
	 * @since 3.2
	 */
	private boolean fMustLoad = true;

	/**
	 * Tells whether to strip non-letters at word boundaries.
	 * 
	 * @since 3.3
	 */
	boolean fIsStrippingNonLetters = true;

	/**
	 * Returns all candidates with the same phonetic hash.
	 * 
	 * @param hash
	 *            The hash to retrieve the candidates of
	 * @return Array of candidates for the phonetic hash
	 */
	protected final Object getCandidates(final String hash) {
		return this.fHashBuckets.get(hash);
	}

	/**
	 * Returns all candidates that have a phonetic hash within a bounded
	 * distance to the specified word.
	 * 
	 * @param word
	 *            The word to find the nearest matches for
	 * @param sentence
	 *            <code>true</code> iff the proposals start a new sentence,
	 *            <code>false</code> otherwise
	 * @param hashs
	 *            Array of close hashes to find the matches
	 * @return Set of ranked words with bounded distance to the specified word
	 */
	protected final Set getCandidates(final String word,
			final boolean sentence, final ArrayList hashs) {

		int distance = 0;
		String hash = null;

		final StringBuffer buffer = new StringBuffer(BUFFER_CAPACITY);
		final HashSet result = new HashSet(BUCKET_CAPACITY * hashs.size());

		for (int index = 0; index < hashs.size(); index++) {

			hash = (String) hashs.get(index);

			final Object candidates = this.getCandidates(hash);
			if (candidates == null) {
				continue;
			} else if (candidates instanceof String) {
				final String candidate = (String) candidates;
				distance = this.fDistanceAlgorithm.getDistance(word, candidate);
				if (distance < DISTANCE_THRESHOLD) {
					buffer.setLength(0);
					buffer.append(candidate);
					if (sentence) {
						buffer.setCharAt(0, Character.toUpperCase(buffer
								.charAt(0)));
					}
					result.add(new RankedWordProposal(buffer.toString(),
							-distance));
				}
				continue;
			}

			final ArrayList candidateList = (ArrayList) candidates;
			final int candidateSize = Math.min(500, candidateList.size()); // see:
			// https
			// :
			// //bugs
			// .
			// eclipse
			// .org/
			// bugs/
			// show_bug
			// .
			// cgi?id
			// =
			// 195357
			for (int offset = 0; offset < candidateSize; offset++) {

				final String candidate = (String) candidateList.get(offset);
				distance = this.fDistanceAlgorithm.getDistance(word, candidate);

				if (distance < DISTANCE_THRESHOLD) {

					buffer.setLength(0);
					buffer.append(candidate);

					if (sentence) {
						buffer.setCharAt(0, Character.toUpperCase(buffer
								.charAt(0)));
					}

					result.add(new RankedWordProposal(buffer.toString(),
							-distance));
				}
			}
		}
		return result;
	}

	/**
	 * Returns all approximations that have a phonetic hash with smallest
	 * possible distance to the specified word.
	 * 
	 * @param word
	 *            The word to find the nearest matches for
	 * @param sentence
	 *            <code>true</code> iff the proposals start a new sentence,
	 *            <code>false</code> otherwise
	 * @param result
	 *            Set of ranked words with smallest possible distance to the
	 *            specified word
	 */
	protected final void getCandidates(final String word,
			final boolean sentence, final Set result) {

		int distance = 0;
		int minimum = Integer.MAX_VALUE;

		final StringBuffer buffer = new StringBuffer(BUFFER_CAPACITY);

		final Object candidates = this.getCandidates(this.fHashProvider.getHash(word));
		if (candidates == null) {
			return;
		} else if (candidates instanceof String) {
			final String candidate = (String) candidates;
			distance = this.fDistanceAlgorithm.getDistance(word, candidate);
			buffer.append(candidate);
			if (sentence) {
				buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
			}
			result.add(new RankedWordProposal(buffer.toString(), -distance));
			return;
		}

		final ArrayList candidateList = (ArrayList) candidates;
		final ArrayList matches = new ArrayList(candidateList.size());

		for (int index = 0; index < candidateList.size(); index++) {
			final String candidate = (String) candidateList.get(index);
			distance = this.fDistanceAlgorithm.getDistance(word, candidate);

			if (distance <= minimum) {

				if (distance < minimum) {
					matches.clear();
				}

				buffer.setLength(0);
				buffer.append(candidate);

				if (sentence) {
					buffer
							.setCharAt(0, Character.toUpperCase(buffer
									.charAt(0)));
				}

				matches
						.add(new RankedWordProposal(buffer.toString(),
								-distance));
				minimum = distance;
			}
		}

		result.addAll(matches);
	}

	/**
	 * Tells whether this dictionary is empty.
	 * 
	 * @return <code>true</code> if this dictionary is empty
	 * @since 3.3
	 */
	protected boolean isEmpty() {
		return this.fHashBuckets.size() == 0;
	}

	/**
	 * Returns the used phonetic distance algorithm.
	 * 
	 * @return The phonetic distance algorithm
	 */
	protected final IPhoneticDistanceAlgorithm getDistanceAlgorithm() {
		return this.fDistanceAlgorithm;
	}

	/**
	 * Returns the used phonetic hash provider.
	 * 
	 * @return The phonetic hash provider
	 */
	protected final IPhoneticHashProvider getHashProvider() {
		return this.fHashProvider;
	}

	/*
	 * @see
	 * com.onpositive.internal.ui.text.spelling.engine.ISpellDictionary#getProposals
	 * (java.lang.String,boolean)
	 */
	public Set getProposals(final String word, final boolean sentence) {

		try {

			if (!this.fLoaded) {
				synchronized (this) {
					this.fLoaded = this.load(this.getURL());
					if (this.fLoaded) {
						this.compact();
					}
				}
			}

		} catch (final MalformedURLException exception) {
			// Do nothing
		}

		final String hash = this.fHashProvider.getHash(word);
		final char[] mutators = this.fHashProvider.getMutators();

		final ArrayList neighborhood = new ArrayList((word.length() + 1)
				* (mutators.length + 2));
		neighborhood.add(hash);

		final Set candidates = this.getCandidates(word, sentence, neighborhood);
		neighborhood.clear();

		char previous = 0;
		char next = 0;

		char[] characters = word.toCharArray();
		for (int index = 0; index < word.length() - 1; index++) {

			next = characters[index];
			previous = characters[index + 1];

			characters[index] = previous;
			characters[index + 1] = next;

			neighborhood.add(this.fHashProvider.getHash(new String(characters)));

			characters[index] = next;
			characters[index + 1] = previous;
		}

		final String sentinel = word + " "; //$NON-NLS-1$

		characters = sentinel.toCharArray();
		int offset = characters.length - 1;

		while (true) {

			for (int index = 0; index < mutators.length; index++) {

				characters[offset] = mutators[index];
				neighborhood.add(this.fHashProvider.getHash(new String(characters)));
			}

			if (offset == 0) {
				break;
			}

			characters[offset] = characters[offset - 1];
			--offset;
		}

		char mutated = 0;
		characters = word.toCharArray();

		for (int index = 0; index < word.length(); index++) {

			mutated = characters[index];
			for (int mutator = 0; mutator < mutators.length; mutator++) {

				characters[index] = mutators[mutator];
				neighborhood.add(this.fHashProvider.getHash(new String(characters)));
			}
			characters[index] = mutated;
		}

		characters = word.toCharArray();
		final char[] deleted = new char[characters.length - 1];

		for (int index = 0; index < deleted.length; index++) {
			deleted[index] = characters[index];
		}

		next = characters[characters.length - 1];
		offset = deleted.length;

		while (true) {

			neighborhood.add(this.fHashProvider.getHash(new String(characters)));
			if (offset == 0) {
				break;
			}

			previous = next;
			next = deleted[offset - 1];

			deleted[offset - 1] = previous;
			--offset;
		}

		neighborhood.remove(hash);
		final Set matches = this.getCandidates(word, sentence, neighborhood);

		if ((matches.size() == 0) && (candidates.size() == 0)) {
			this.getCandidates(word, sentence, candidates);
		}

		candidates.addAll(matches);

		return candidates;
	}

	/**
	 * Returns the URL of the dictionary word list.
	 * 
	 * @throws MalformedURLException
	 *             if the URL could not be retrieved
	 * @return The URL of the dictionary word list
	 */
	protected abstract URL getURL() throws MalformedURLException;

	/**
	 * Hashes the word into the dictionary.
	 * 
	 * @param word
	 *            The word to hash in the dictionary
	 */
	protected final void hashWord(final String word) {

		final String hash = this.fHashProvider.getHash(word);
		final Object bucket = this.fHashBuckets.get(hash);
		this.allWords.add(word);
		if (bucket == null) {
			this.fHashBuckets.put(hash, word);
		} else if (bucket instanceof ArrayList) {
			((ArrayList) bucket).add(word);
		} else {
			final ArrayList list = new ArrayList(BUCKET_CAPACITY);
			list.add(bucket);
			list.add(word);
			this.fHashBuckets.put(hash, list);
		}
	}

	/*
	 * @see
	 * com.onpositive.internal.ui.text.spelling.engine.ISpellDictionary#isCorrect
	 * (java.lang.String)
	 */
	public boolean isCorrect(String word) {
		word = this.stripNonLetters(word);
		try {

			if (!this.fLoaded) {
				synchronized (this) {
					this.fLoaded = this.load(this.getURL());
					if (this.fLoaded) {
						this.compact();
					}
				}
			}

		} catch (final MalformedURLException exception) {
			// Do nothing
		}

		final Object candidates = this.getCandidates(this.fHashProvider.getHash(word));
		if (candidates == null) {
			return false;
		} else if (candidates instanceof String) {
			final String candidate = (String) candidates;
			if (candidate.equals(word) || candidate.equals(word.toLowerCase())) {
				return true;
			}
			return false;
		}
		final ArrayList candidateList = (ArrayList) candidates;
		if (candidateList.contains(word)
				|| candidateList.contains(word.toLowerCase())) {
			return true;
		}

		return false;
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellDictionary#
	 * setStripNonLetters(boolean)
	 * 
	 * @since 3.3
	 */
	public void setStripNonLetters(boolean state) {
		this.fIsStrippingNonLetters = state;
	}

	/**
	 * Strips non-letter characters from the given word.
	 * <p>
	 * This will only happen if the corresponding preference is enabled.
	 * </p>
	 * 
	 * @param word
	 *            the word to strip
	 * @return the stripped word
	 * @since 3.3
	 */
	protected String stripNonLetters(String word) {
		if (!this.fIsStrippingNonLetters) {
			return word;
		}

		int i = 0;
		int j = word.length() - 1;
		while ((i <= j) && !Character.isLetter(word.charAt(i))) {
			i++;
		}
		if (i > j) {
			return ""; //$NON-NLS-1$
		}

		while ((j > i) && !Character.isLetter(word.charAt(j))) {
			j--;
		}

		return word.substring(i, j + 1);
	}

	/*
	 * @see org.eclipse.jdt.ui.text.spelling.engine.ISpellDictionary#isLoaded()
	 */
	public final synchronized boolean isLoaded() {
		return this.fLoaded || (this.fHashBuckets.size() > 0);
	}

	/**
	 * Loads a dictionary word list from disk.
	 * 
	 * @param url
	 *            The URL of the word list to load
	 * @return <code>true</code> iff the word list could be loaded,
	 *         <code>false</code> otherwise
	 */
	protected synchronized boolean load(final URL url) {
		if (!this.fMustLoad) {
			return this.fLoaded;
		}

		if (url != null) {
			InputStream stream = null;
			final int line = 0;
			try {
				stream = url.openStream();
				if (stream != null) {
					String word = null;

					// Setup a reader with a decoder in order to read over
					// malformed input if needed.
					final CharsetDecoder decoder = Charset.forName(this.getEncoding())
							.newDecoder();
					decoder.onMalformedInput(CodingErrorAction.REPORT);
					decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
					final BufferedReader reader = new BufferedReader(
							new InputStreamReader(stream, decoder));

					boolean doRead = true;
					while (doRead) {
						try {
							word = reader.readLine();
						} catch (final MalformedInputException ex) {
							// Tell the decoder to replace malformed input in
							// order to read the line.
							decoder.onMalformedInput(CodingErrorAction.REPLACE);
							decoder.reset();
							word = reader.readLine();
							decoder.onMalformedInput(CodingErrorAction.REPORT);

							// String message= Messages.format(JavaUIMessages.
							// AbstractSpellingDictionary_encodingError, new
							// String[] { word, decoder.replacement(),
							// BasicElementLabels.getURLPart(url.toString()) });
							// IStatus status= new Status(IStatus.ERROR,
							// JavaUI.ID_PLUGIN, IStatus.OK, message, ex);
							Activator.log(ex);

							doRead = word != null;
							continue;
						}
						doRead = word != null;
						if (doRead) {
							this.hashWord(word);
						}
					}
					return true;
				}
			} catch (final FileNotFoundException ex) {
				final String urlString = url.toString();
				final String lowercaseUrlString = urlString.toLowerCase();
				if (urlString.equals(lowercaseUrlString)) {
					Activator.log(ex);
				} else {
					try {
						return this.load(new URL(lowercaseUrlString));
					} catch (final MalformedURLException e) {
						Activator.log(e);
					}
				}
			} catch (final IOException exception) {
				if (line > 0) {

					Activator.log(exception);
				} else {
					Activator.log(exception);
				}
			} finally {
				this.fMustLoad = false;
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (final IOException x) {
				}
			}
		}
		return false;
	}

	/**
	 * Compacts the dictionary.
	 * 
	 * @since 3.3.
	 */
	private void compact() {
		final Iterator iter = this.fHashBuckets.values().iterator();
		while (iter.hasNext()) {
			final Object element = iter.next();
			if (element instanceof ArrayList) {
				((ArrayList) element).trimToSize();
			}
		}
	}

	/**
	 * Sets the phonetic distance algorithm to use.
	 * 
	 * @param algorithm
	 *            The phonetic distance algorithm
	 */
	protected final void setDistanceAlgorithm(
			final IPhoneticDistanceAlgorithm algorithm) {
		this.fDistanceAlgorithm = algorithm;
	}

	/**
	 * Sets the phonetic hash provider to use.
	 * 
	 * @param provider
	 *            The phonetic hash provider
	 */
	protected final void setHashProvider(final IPhoneticHashProvider provider) {
		this.fHashProvider = provider;
	}

	/*
	 * @see org.eclipse.jdt.ui.text.spelling.engine.ISpellDictionary#unload()
	 */
	public synchronized void unload() {
		this.fLoaded = false;
		this.fMustLoad = true;
		this.fHashBuckets.clear();
	}

	/*
	 * @see
	 * org.eclipse.jdt.ui.text.spelling.engine.ISpellDictionary#acceptsWords()
	 */
	public boolean acceptsWords() {
		return false;
	}

	/*
	 * @see
	 * com.onpositive.internal.ui.text.spelling.engine.ISpellDictionary#addWord
	 * (java.lang.String)
	 */
	public void addWord(final String word) {
		// Do nothing
	}

	/**
	 * Returns the encoding of this dictionary.
	 * 
	 * @return the encoding of this dictionary
	 * @since 3.3
	 */
	protected String getEncoding() {
		// String encoding=
		// JavaPlugin.getDefault().getPreferenceStore().getString
		// (PreferenceConstants.SPELLING_USER_DICTIONARY_ENCODING);
		// if (encoding == null || encoding.length() == 0)
		// encoding= ResourcesPlugin.getEncoding();
		return Charset.defaultCharset().name();
		// return encoding;
	}

	public Collection getCompletionProposals(String word, boolean sentence) {
		final Set proposals = this.getProposals(word, sentence);
		for (String s : this.getAllWordsStartingWith(word, sentence)) {
			if (sentence) {
				s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
			}
			proposals.add(new RankedWordProposal(s, -100));
		}
		return proposals;
	}

}
