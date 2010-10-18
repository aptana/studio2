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
package com.aptana.internal.ui.text.spelling;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.aptana.semantic.ui.text.spelling.Activator;

/**
 * Default spelling engine.
 * <p>
 * Internally this spelling engine uses a different spelling engine depending on
 * the {@linkplain IContentType content type}. Currently this engine supports
 * the text, Java and Java properties file content types.
 * </p>
 * 
 * @since 3.1
 */
public class DefaultSpellingEngine implements ISpellingEngine {

	/** Text content type */
	private static final IContentType TEXT_CONTENT_TYPE = Platform
			.getContentTypeManager()
			.getContentType(IContentTypeManager.CT_TEXT);

	/** Available spelling engines by content type */
	private final Map fEngines = new HashMap();

	private SpellingService spellingService;

	/**
	 * Initialize concrete engines.
	 */
	public DefaultSpellingEngine() {
		// if (JAVA_CONTENT_TYPE != null)
		// fEngines.put(JAVA_CONTENT_TYPE, new JavaSpellingEngine());
		// if (PROPERTIES_CONTENT_TYPE != null)
		// fEngines.put(PROPERTIES_CONTENT_TYPE, new
		// PropertiesFileSpellingEngine());
		if (TEXT_CONTENT_TYPE != null) {
			this.fEngines.put(TEXT_CONTENT_TYPE, new TextSpellingEngine());
		}
		spellingService = new SpellingService(Activator.getSpellingPreferenceStore());
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
		ISpellingEngine engine = this.getEngine(context.getContentType());
		if (engine == null) {
			engine = this.getEngine(TEXT_CONTENT_TYPE);
		}
		if (engine != null) {
			engine.check(document, regions, context, collector, monitor);
		}

	}

	ISpellingEngine defaultEngine;

	/**
	 * Returns a spelling engine for the given content type or <code>null</code>
	 * if none could be found.
	 * 
	 * @param contentType
	 *            the content type
	 * @return a spelling engine for the given content type or <code>null</code>
	 *         if none could be found
	 */
	private ISpellingEngine getEngine(IContentType contentType) {
		if (contentType == null) {
			return null;
		}

		if (this.fEngines.containsKey(contentType)) {
			return (ISpellingEngine) this.fEngines.get(contentType);
		}
		if (defaultEngine != null) {
			return defaultEngine;
		}
		SpellingEngineDescriptor[] spellingEngineDescriptors = spellingService
				.getSpellingEngineDescriptors();
		for (SpellingEngineDescriptor d : spellingEngineDescriptors) {
			String id = d.getId();
			if (id
					.equals("org.eclipse.jdt.internal.ui.text.spelling.DefaultSpellingEngine")) { //$NON-NLS-1$
				try {
					ISpellingEngine createEngine = d.createEngine();
					defaultEngine = createEngine;
					return createEngine;
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		return null;
	}
}
