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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import com.aptana.internal.ui.text.spelling.engine.ISpellCheckEngine;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;
import com.aptana.internal.ui.text.spelling.engine.ISpellEvent;
import com.aptana.internal.ui.text.spelling.engine.ISpellEventListener;

/**
 * Internal abstract spelling engine, subclasses provide a content-type specific
 * implementation.
 * 
 * @since 3.1
 */
public abstract class SpellingEngine implements ISpellingEngine {

	/**
	 * {@link ISpellEvent}listener that forwards events as
	 * {@link org.eclipse.ui.texteditor.spelling.SpellingProblem}.
	 */
	protected static class SpellEventListener implements ISpellEventListener {

		/** Spelling problem collector */
		private final ISpellingProblemCollector fCollector;

		/**
		 * The document.
		 * 
		 * @since 3.3
		 */
		private final IDocument fDocument;

		private final int fProblemsThreshold;
		private int fProblemCount;

		/**
		 * Initialize with the given spelling problem collector.
		 * 
		 * @param collector
		 *            the spelling problem collector
		 * @param document
		 *            the document
		 */
		public SpellEventListener(ISpellingProblemCollector collector,
				IDocument document) {
			this.fCollector = collector;
			this.fDocument = document;
			this.fProblemsThreshold = 100;
		}

		/*
		 * @see
		 * com.onpositive.internal.ui.text.spelling.engine.ISpellEventListener
		 * #handle(com.onpositive.internal.ui.text.spelling.engine.ISpellEvent)
		 */
		public void handle(ISpellEvent event) {
			if (this.isProblemsThresholdReached()) {
				return;
			}
			this.fProblemCount++;
			this.fCollector.accept(new JavaSpellingProblem(event,
					this.fDocument));
		}

		boolean isProblemsThresholdReached() {
			return this.fProblemCount >= this.fProblemsThreshold;
		}
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingEngine#check(org.eclipse.
	 * jface.text.IDocument, org.eclipse.jface.text.IRegion[],
	 * org.eclipse.ui.texteditor.spelling.SpellingContext,
	 * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {
		if (collector != null) {
			final ISpellCheckEngine spellingEngine = SpellCheckEngine
					.getInstance();
			final ISpellChecker checker = spellingEngine.getSpellChecker();
			if (checker != null) {
				this.check(document, regions, checker, collector, monitor);
			}
		}
	}

	/**
	 * Spell checks the given document regions with the given arguments.
	 * 
	 * @param document
	 *            the document
	 * @param regions
	 *            the regions
	 * @param checker
	 *            the spell checker
	 * @param collector
	 *            the spelling problem collector
	 * @param monitor
	 *            the progress monitor, can be <code>null</code>
	 */
	protected abstract void check(IDocument document, IRegion[] regions,
			ISpellChecker checker, ISpellingProblemCollector collector,
			IProgressMonitor monitor);

}
