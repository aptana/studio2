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
package com.aptana.ide.editors.unified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.aptana.commons.spelling.engine.IComputeSpellcheckRegions;
import com.aptana.commons.spelling.engine.SpellingAnnotation;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.editors.unified.folding.LanguageProjectAnnotation;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader.FoldingStructure;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * UnifiedReconcilingStrategy
 */
public class UnifiedReconcilingStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension
{
	/*
	 * Fields
	 */
	private UnifiedEditor _editor;
	private IDocument _document;
	private boolean _isDisposing = false;
	private boolean firstReconcile = true;
	private Color foldingFgColor;

	/** Holds the language annotations mapped to their positions */
	protected final Map annotations = new HashMap();

	/** Holds language mapped to folding structure objects */
	protected Map childTypes = new HashMap();

	protected SpellingAnnotation[] spellingAnnotations;
	private SpellingService spellingService;
	private IPreferenceStore editorsPreferenceStore;
	private IPropertyChangeListener editorsListener;

	/**
	 * dispose
	 */
	public void dispose()
	{
		this._isDisposing = true;
		this._editor = null;
		this._document = null;
		editorsPreferenceStore.removePropertyChangeListener(editorsListener);
	}

	/**
	 * @return Returns the editor.
	 */
	public IUnifiedEditor getEditor()
	{
		return this._editor;
	}

	/**
	 * setEditor
	 * 
	 * @param editor
	 */
	public void setEditor(UnifiedEditor editor)
	{
		this._editor = editor;
		childTypes = FoldingExtensionPointLoader.loadChildTypes(editor);

		editorsPreferenceStore = EditorsUI.getPreferenceStore();
		spellingService = new SpellingService(
				editorsPreferenceStore);
		editorsListener = new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				doSpellCheck(spellingService, _editor.getViewer());
			}

		};		
		editorsPreferenceStore.addPropertyChangeListener(editorsListener);
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document)
	{
		this._document = document;

	}

	/**
	 * getDocument
	 * 
	 * @return IDocument
	 */
	public IDocument getDocument()
	{
		return this._document;
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		calculatePositions();
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition)
	{
		calculatePositions();
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile()
	{
		if (this._isDisposing)
		{
			return;
		}

		calculatePositions();
		firstReconcile = false;
	}

	/**
	 * calculatePositions
	 */
	protected void calculatePositions()
	{
		if (this._isDisposing)
		{
			return;
		}

		annotations.clear();
		LexemeList ll = this._editor.getFileContext().getLexemeList();

		if (ll != null)
		{
			IParseState parseState = this._editor.getFileContext()
					.getParseState();

			parseForRegions(parseState);
		}

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_isDisposing)
				{
					_editor.updateFoldingStructure(annotations);
				}
			}

		});
		if (_editor != null)
			doSpellCheck(spellingService, (SourceViewer) _editor.getViewer());
	}

	/**
	 * emitPosition
	 * 
	 * @param startOffset
	 * @param length
	 * @param type
	 * @param language
	 */
	public void emitPosition(int startOffset, int length, String type,
			String language)
	{
		if (this._isDisposing)
		{
			return;
		}

		try
		{
			IDocument doc = this._document;

			if (doc != null)
			{
				int startLine = doc.getLineOfOffset(startOffset);
				int endLine = doc.getLineOfOffset(startOffset + length);

				if (startLine < endLine)
				{
					int start = doc.getLineOffset(startLine);
					int end = doc.getLineOffset(endLine)
							+ doc.getLineLength(endLine);
					Position position = new Position(start, end - start);
					LanguageProjectAnnotation annotation = new LanguageProjectAnnotation(
							language, type);

					LanguageColorizer colorizer = LanguageRegistry
							.getLanguageColorizer(language);
					Color fgColor = null;
					Color bgColor = null;
					if (colorizer != null && colorizer.getFoldingFg() != null)
					{
						fgColor = colorizer.getFoldingFg();
						bgColor = colorizer.getFoldingBg();
						annotation.setColor(colorizer.getFoldingFg());
					} else if (foldingFgColor != null)
					{
						fgColor = foldingFgColor;
						annotation.setColor(foldingFgColor);
					}
					if (fgColor != null)
					{
						annotation.setCollapsedImage(annotation
								.getCollapsedImage(), fgColor, bgColor);
						annotation.setExpandedImage(annotation
								.getExpandedImage(), fgColor, bgColor);
					}

					String pref = FoldingExtensionPointLoader
							.createInitialFoldingPreferenceId(language, type);
					IPreferenceStore store = UnifiedEditorsPlugin.getDefault()
							.getPreferenceStore();

					if (firstReconcile)
					{
						if (store.getBoolean(pref))
						{
							annotation.markCollapsed();
						}
					}
					annotations.put(annotation, position);
				}
			}
		} catch (BadLocationException e)
		{
			// Do not log error here, if a bad location occurs the document must
			// be changing rapidly and this will
			// eventually right itself and correct folding will be restored
		}
	}

	/**
	 * parseForRegions
	 * 
	 * @param parseState
	 */
	public void parseForRegions(IParseState parseState)
	{
		IParseNode node = parseState.getParseResults();

		if (node != null)
		{
			emitChildren(node.getChildren());
			emitChildren(parseState.getCommentRegions());
		}
	}

	private void emitChildren(IParseNode[] nodes)
	{
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault()
				.getPreferenceStore();
		for (int i = 0; i < nodes.length; i++)
		{
			IParseNode node = nodes[i];
			if (node.getStartingOffset() >= 0)
			{
				if (childTypes.containsKey(node.getLanguage()))
				{
					FoldingStructure fs = (FoldingStructure) childTypes
							.get(node.getLanguage());
					if (node.getChildCount() > 0)
					{
						if (fs.getHandler() == null
								|| fs.getHandler().nodeIsFoldable(node))
						{
							if (fs.foldAllParents()
									|| fs.getTypes()
											.containsKey(node.getName()))
							{
								String prefID = FoldingExtensionPointLoader
										.createEnablePreferenceId(fs
												.getLanguage());
								if (store.getBoolean(prefID))
								{
									this.emitPosition(node.getStartingOffset(),
											node.getEndingOffset()
													- node.getStartingOffset(),
											node.getName(), node.getLanguage());
								}
							}
						}
						emitChildren(node.getChildren());
					} else
					{
						String nm = (node instanceof IHasCustomFoldingType) ? ((IHasCustomFoldingType) node)
								.getFoldingTypeName()
								: node.getName();
						if (fs.getTypes().containsKey(nm)
								|| (fs.getHandler() != null && fs.getHandler()
										.nodeIsFoldable(node)))
						{
							String prefID = FoldingExtensionPointLoader
									.createEnablePreferenceId(fs.getLanguage());
							if (store.getBoolean(prefID))
							{
								this.emitPosition(node.getStartingOffset(),
										node.getEndingOffset()
												- node.getStartingOffset(),
										node.getName(), node.getLanguage());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the folding annotation hover color
	 * 
	 * @param color
	 */
	public void setFoldingAnnotationHoverColor(Color color)
	{
		this.foldingFgColor = color;
	}

	private void doSpellCheck(final SpellingService spellingService,
			final ISourceViewer sv)
	{
		final ISpellingProblemCollector collector = new ISpellingProblemCollector()
		{

			ArrayList<SpellingProblem> prblms = new ArrayList<SpellingProblem>();

			public void accept(SpellingProblem problem)
			{
				this.prblms.add(problem);
			}

			public void beginCollecting()
			{
				this.prblms.clear();
			}

			public void endCollecting()
			{
				final AnnotationModel annotationModel = (AnnotationModel) sv
						.getAnnotationModel();
				if (annotationModel!=null){
				final Map<SpellingAnnotation, Position> annotationsToAdd = new HashMap<SpellingAnnotation, Position>();
				for (final SpellingProblem p : this.prblms)
				{
					final SpellingAnnotation annotation = new SpellingAnnotation(
							p);
					annotationsToAdd.put(annotation, new Position(
							p.getOffset(), p.getLength()));
				}

				annotationModel.replaceAnnotations(
						spellingAnnotations == null ? new Annotation[] {}
								: spellingAnnotations, annotationsToAdd);
				spellingAnnotations = new SpellingAnnotation[annotationsToAdd
						.size()];
				annotationsToAdd.keySet().toArray(spellingAnnotations);
				}
			}
		};
		final SpellingContext context = new SpellingContext();
		spellingService.check(sv.getDocument(),computeSpellCheckRegions(), context, collector, null);
	}

	private IRegion[] computeSpellCheckRegions()
	{
		IComputeSpellcheckRegions adapter = (IComputeSpellcheckRegions) this._editor.getAdapter(IComputeSpellcheckRegions.class);
		if (adapter!=null){
			return adapter.spellCheckRegions();
		}
		LexemeList ll = this._editor.getFileContext().getLexemeList();
		
		if (ll != null)
		{
			IParseState parseState = this._editor.getFileContext()
					.getParseState();
			
			IParseNode[] commentRegions = parseState.getCommentRegions();
			IRegion[] regions=new IRegion[commentRegions.length];
			for (int a=0;a<commentRegions.length;a++){
				regions[a]=new Region(commentRegions[a].getStartingOffset(),commentRegions[a].getLength());
			}
			return regions;
		}
		
		return new IRegion[0];
	}
}
