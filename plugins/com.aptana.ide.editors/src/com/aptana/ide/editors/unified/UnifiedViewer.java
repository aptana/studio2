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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistant;

/**
 * 
 */
public class UnifiedViewer extends ProjectionViewer implements IUnifiedViewer
{
	private boolean hotkeyActivated = false;
	private TextPresentation presentation;

	/**
	 * The text viewer's text triple click strategies
	 */
	protected Map<String, ITextTripleClickStrategy> fTripleClickStrategies;

	/**
	 * Triple click connector.
	 */
	protected TripleClickConnector fTripleClickStrategyConnector;

	// **** NOTE ****
	// we are not using Projection Viewer in order to get shift left working (to shift until no
	// whitespace)
	// if this is problematic or 3.2 exposes that in a more intelligent way (or fixes that issue) we
	// can get rid
	// of UnifiedViewer, and revert one line of code in UnifiedEditor.createSourceViewer.

	// our only changes are prefixed with "// [APTANA]"

	/**
	 * @param parent
	 * @param ruler
	 * @param overviewRuler
	 * @param showsAnnotationOverview
	 * @param styles
	 */
	public UnifiedViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler,
			boolean showsAnnotationOverview, int styles)
	{
		super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewer#createLayout()
	 */
	protected Layout createLayout()
	{
		return new RulerLayout(0);
	}

	/**
	 * Shifts a text block to the right or left using the specified set of prefix characters. The prefixes must start at
	 * the beginning of the line.
	 * 
	 * @param useDefaultPrefixes
	 *            says whether the configured default or indent prefixes should be used
	 * @param right
	 *            says whether to shift to the right or the left
	 * @deprecated use shift(boolean, boolean, boolean) instead
	 */
	protected void shift(boolean useDefaultPrefixes, boolean right)
	{
		shift(useDefaultPrefixes, right, false);
	}

	/**
	 * @see org.eclipse.jface.text.TextViewer#createTextWidget(org.eclipse.swt.widgets.Composite, int)
	 */
	protected StyledText createTextWidget(Composite parent, int styles)
	{
		return new StyledText(parent, styles)
		{

			boolean swap = false;

			public void setLineBackground(int startLine, int lineCount, Color background)
			{
				swap = true;
				super.setLineBackground(startLine, lineCount, background);
				swap = false;
			}

			public boolean isListening(int eventType)
			{
				if (swap && eventType == 3001)
				{
					return false;
				}
				return super.isListening(eventType);
			}

			public void invokeAction(int action)
			{
				if (getWordWrap() && ST.LINE_DOWN == action)
				{
					int previous = getCaretOffset();
					super.invokeAction(action);
					if (previous == getCaretOffset())
					{
						int line = getLineAtOffset(previous);
						if (line + 1 < getLineCount())
						{
							setCaretOffset(getOffsetAtLine(line + 1));
						}
					}
				}
				else
				{
					super.invokeAction(action);
				}
			}

		};
	}

	/**
	 * Shifts a text block to the right or left using the specified set of prefix characters. If white space should be
	 * ignored the prefix characters must not be at the beginning of the line when shifting to the left. There may be
	 * whitespace in front of the prefixes.
	 * 
	 * @param useDefaultPrefixes
	 *            says whether the configured default or indent prefixes should be used
	 * @param right
	 *            says whether to shift to the right or the left
	 * @param ignoreWhitespace
	 *            says whether whitespace in front of prefixes is allowed
	 * @since 2.0
	 */
	protected void shift(boolean useDefaultPrefixes, boolean right, boolean ignoreWhitespace)
	{

		if (fUndoManager != null)
		{
			fUndoManager.beginCompoundChange();
		}

		setRedraw(false);
		startSequentialRewriteMode(true);

		IDocument d = getDocument();
		Map partitioners = null;

		try
		{

			Point selection = getSelectedRange();
			IRegion block = getTextBlockFromSelection(selection);
			ITypedRegion[] regions = TextUtilities.computePartitioning(d, getDocumentPartitioning(), block.getOffset(),
					block.getLength(), false);

			int lineCount = 0;
			int[] lines = new int[regions.length * 2]; // [start line, end line, start line, end
			// line, ...]
			for (int i = 0, j = 0; i < regions.length; i++, j += 2)
			{
				// start line of region
				lines[j] = getFirstCompleteLineOfRegion(regions[i]);
				// end line of region
				int length = regions[i].getLength();
				int offset = regions[i].getOffset() + length;
				if (length > 0)
				{
					offset--;
				}
				lines[j + 1] = (lines[j] == -1 ? -1 : d.getLineOfOffset(offset));
				lineCount += lines[j + 1] - lines[j] + 1;
			}

			if (lineCount >= 20)
			{
				partitioners = TextUtilities.removeDocumentPartitioners(d);
			}

			// Remember the selection range.
			IPositionUpdater positionUpdater = new ShiftPositionUpdater(SHIFTING);
			Position rememberedSelection = new Position(selection.x, selection.y);
			d.addPositionCategory(SHIFTING);
			d.addPositionUpdater(positionUpdater);
			try
			{
				d.addPosition(SHIFTING, rememberedSelection);
			}
			catch (BadPositionCategoryException ex)
			{
				// should not happen
			}

			// Perform the shift operation.
			Map map = (useDefaultPrefixes ? fDefaultPrefixChars : fIndentChars);
			for (int i = 0, j = 0; i < regions.length; i++, j += 2)
			{
				String[] prefixes = (String[]) selectContentTypePlugin(regions[i].getType(), map);
				if (prefixes != null && prefixes.length > 0 && lines[j] >= 0 && lines[j + 1] >= 0)
				{
					if (right)
					{
						shiftRight(lines[j], lines[j + 1], prefixes[0], d);
					}
					else
					{
						shiftLeft(lines[j], lines[j + 1], prefixes, ignoreWhitespace, d);
					}
				}
			}

			// Restore the selection.
			setSelectedRange(rememberedSelection.getOffset(), rememberedSelection.getLength());

			try
			{
				d.removePositionUpdater(positionUpdater);
				d.removePositionCategory(SHIFTING);
			}
			catch (BadPositionCategoryException ex)
			{
				// should not happen
			}

		}
		catch (BadLocationException x)
		{

		}
		finally
		{

			if (partitioners != null)
			{
				TextUtilities.addDocumentPartitioners(d, partitioners);
			}

			stopSequentialRewriteMode();
			setRedraw(true);

			if (fUndoManager != null)
			{
				fUndoManager.endCompoundChange();
			}
		}
	}

	/**
	 * Shifts the specified lines to the right inserting the given prefix at the beginning of each line
	 * 
	 * @param prefix
	 *            the prefix to be inserted
	 * @param startLine
	 *            the first line to shift
	 * @param endLine
	 *            the last line to shift
	 * @param document
	 * @since 2.0
	 */
	public static void shiftRight(int startLine, int endLine, String prefix, IDocument document)
	{

		try
		{
			while (startLine <= endLine)
			{
				document.replace(document.getLineOffset(startLine++), 0, prefix);
			}

		}
		catch (BadLocationException x)
		{
			if (TRACE_ERRORS)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), "TextViewer.shiftRight: BadLocationException", x); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Shifts the specified lines to the right or to the left. On shifting to the right it insert
	 * <code>prefixes[0]</code> at the beginning of each line. On shifting to the left it tests whether each of the
	 * specified lines starts with one of the specified prefixes and if so, removes the prefix.
	 * 
	 * @param startLine
	 *            the first line to shift
	 * @param endLine
	 *            the last line to shift
	 * @param prefixes
	 *            the prefixes to be used for shifting
	 * @param ignoreWhitespace
	 *            <code>true</code> if whitespace should be ignored, <code>false</code> otherwise
	 * @param document
	 *            The document
	 * @since 2.0
	 */
	public static void shiftLeft(int startLine, int endLine, String[] prefixes, boolean ignoreWhitespace,
			IDocument document)
	{
		try
		{

			IRegion[] occurrences = new IRegion[endLine - startLine + 1];

			// find all the first occurrences of prefix in the given lines
			for (int i = 0; i < occurrences.length; i++)
			{

				IRegion line = document.getLineInformation(startLine + i);
				String text = document.get(line.getOffset(), line.getLength());

				int index = -1;
				int[] found = TextUtilities.indexOf(prefixes, text, 0);
				if (found[0] != -1)
				{
					if (ignoreWhitespace)
					{
						String s = document.get(line.getOffset(), found[0]);
						s = s.trim();
						if (s.length() == 0)
						{
							index = line.getOffset() + found[0];
						}
					}
					else if (found[0] == 0)
					{
						index = line.getOffset();
					}
				}

				if (index > -1)
				{
					// remember where prefix is in line, so that it can be removed
					int length = prefixes[found[1]].length();

					if (length == 0 && !ignoreWhitespace && line.getLength() > 0)
					{
						// found a non-empty line which cannot be shifted
						// return;
						// [APTANA]
						occurrences[i] = new Region(index, 0);
					}
					else
					{
						occurrences[i] = new Region(index, length);
					}
				}
				else
				{
					// found a line which cannot be shifted
					// return;
					// [APTANA]
					occurrences[i] = new Region(index, 0);
				}
			}

			// OK - change the document
			int decrement = 0;
			for (int i = 0; i < occurrences.length; i++)
			{
				IRegion r = occurrences[i];

				// [APTANA]
				if (r.getLength() == 0)
				{
					continue;
				}

				document.replace(r.getOffset() - decrement, r.getLength(), StringUtils.EMPTY);
				decrement += r.getLength();
			}

		}
		catch (BadLocationException x)
		{
			if (TRACE_ERRORS)
			{
				Trace.info("TextViewer.shiftLeft: BadLocationException"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Creates a region describing the text block (something that starts at the beginning of a line) completely
	 * containing the current selection.
	 * 
	 * @param selection
	 *            the selection to use
	 * @return the region describing the text block comprising the given selection
	 * @since 2.0
	 */
	private IRegion getTextBlockFromSelection(Point selection)
	{

		try
		{
			IDocument document = getDocument();
			IRegion line = document.getLineInformationOfOffset(selection.x);
			int length = selection.y == 0 ? line.getLength() : selection.y + (selection.x - line.getOffset());
			return new Region(line.getOffset(), length);

		}
		catch (BadLocationException x)
		{
		}

		return null;
	}

	/**
	 * Returns the index of the first line whose start offset is in the given text range.
	 * 
	 * @param region
	 *            the text range in characters where to find the line
	 * @return the first line whose start index is in the given range, -1 if there is no such line
	 */
	private int getFirstCompleteLineOfRegion(IRegion region)
	{

		try
		{

			IDocument d = getDocument();

			int startLine = d.getLineOfOffset(region.getOffset());

			int offset = d.getLineOffset(startLine);
			if (offset >= region.getOffset())
			{
				return startLine;
			}

			offset = d.getLineOffset(startLine + 1);
			return (offset > region.getOffset() + region.getLength() ? -1 : startLine + 1);

		}
		catch (BadLocationException x)
		{
		}

		return -1;
	}

	/**
	 * Selects from the given <code>plug-ins</code> this one which is registered for the given content
	 * <code>type</code>.
	 * 
	 * @param type
	 *            the type to be used as lookup key
	 * @param plugins
	 *            the table to be searched
	 * @return the plug-in in the map for the given content type
	 */
	private Object selectContentTypePlugin(String type, Map plugins)
	{

		if (plugins == null)
		{
			return null;
		}

		return plugins.get(type);
	}

	/**
	 * This position updater is used to keep the selection during text shift operations.
	 */
	static class ShiftPositionUpdater extends DefaultPositionUpdater
	{

		/**
		 * Creates the position updater for the given category.
		 * 
		 * @param category
		 *            the category this updater takes care of
		 */
		protected ShiftPositionUpdater(String category)
		{
			super(category);
		}

		/**
		 * If an insertion happens at the selection's start offset, the position is extended rather than shifted.
		 */
		protected void adaptToInsert()
		{

			int myStart = fPosition.offset;
			int myEnd = fPosition.offset + fPosition.length - 1;
			myEnd = Math.max(myStart, myEnd);

			int yoursStart = fOffset;
			int yoursEnd = fOffset + fReplaceLength - 1;
			yoursEnd = Math.max(yoursStart, yoursEnd);

			if (myEnd < yoursStart)
			{
				return;
			}

			if (myStart <= yoursStart)
			{
				fPosition.length += fReplaceLength;
				return;
			}

			if (myStart > yoursStart)
			{
				fPosition.offset += fReplaceLength;
			}
		}
	}

	/**
	 * @return isHotkeyActivated
	 */
	public boolean isHotkeyActivated()
	{
		return hotkeyActivated;
	}

	/**
	 * @param value
	 */
	public void setHotkeyActivated(boolean value)
	{
		hotkeyActivated = value;
	}

	/**
	 * Closes the open content assist window
	 */
	public void closeContentAssist()
	{
		if (fContentAssistant != null && fContentAssistant instanceof IUnifiedContentAssistant)
		{
			((IUnifiedContentAssistant) fContentAssistant).hide();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void activatePlugins()
	{
		super.activatePlugins();

	}

	/**
	 * Sets text triple click strategy.
	 * 
	 * @param strategy -
	 *            strategy to set.
	 * @param contentType -
	 *            content type.
	 */
	public void setTextTripleClickStrategy(ITextTripleClickStrategy strategy, String contentType)
	{

		if (strategy != null)
		{
			if (fTripleClickStrategies == null)
			{
				fTripleClickStrategies = new HashMap<String, ITextTripleClickStrategy>();
			}
			fTripleClickStrategies.put(contentType, strategy);
		}
		else if (fTripleClickStrategies != null)
		{
			fTripleClickStrategies.remove(contentType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure(SourceViewerConfiguration configuration)
	{
		super.configure(configuration);

		if (configuration instanceof UnifiedConfiguration)
		{
			UnifiedConfiguration conf = (UnifiedConfiguration) configuration;
			// install content type specific plug-ins
			String[] types = configuration.getConfiguredContentTypes(this);

			for (int i = 0; i < types.length; i++)
			{
				String t = types[i];
				setTextTripleClickStrategy(conf.getTripleClickStrategy(this, t), t);
			}
		}

		activateTripleClickStrategies();
	}

	/**
	 * Activates triple click strategies.
	 */
	private void activateTripleClickStrategies()
	{
		if (fTripleClickStrategies != null && !fTripleClickStrategies.isEmpty()
				&& fTripleClickStrategyConnector == null)
		{
			fTripleClickStrategyConnector = new TripleClickConnector()
			{

				@Override
				public void mouseTripleClick(MouseEvent e)
				{
					ITextTripleClickStrategy s = (ITextTripleClickStrategy) selectContentTypePlugin(
							getSelectedRange().x, fTripleClickStrategies);
					s.tripleClicked(UnifiedViewer.this);
				}

			};

			getTextWidget().addMouseListener(fTripleClickStrategyConnector);
		}
	}

	/*
	 * @see ITextViewer#changeTextPresentation(TextPresentation, boolean)
	 */
	public void changeTextPresentation(TextPresentation presentation, boolean controlRedraw) {
		super.changeTextPresentation(presentation, controlRedraw);
		this.presentation=presentation;
	}
	
	public TextPresentation getTextPresentation() {
		return presentation;		
	}
}
