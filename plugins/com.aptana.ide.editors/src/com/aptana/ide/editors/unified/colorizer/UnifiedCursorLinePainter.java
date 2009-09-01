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
package com.aptana.ide.editors.unified.colorizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UnifiedCursorLinePainter implements IPainter, LineBackgroundListener
{

	/** The viewer the painter works on */
	private final ITextViewer fViewer;
	/** The cursor line back ground color */
	private Color fHighlightColor;
	/** The paint position manager for managing the line coordinates */
	private IPaintPositionManager fPositionManager;

	/** Keeps track of the line to be painted */
	private Position fCurrentLine = new Position(0, 0);
	/** Keeps track of the line to be cleared */
	private Position fLastLine = new Position(0, 0);
	/** Keeps track of the line number of the last painted line */
	private int fLastLineNumber = -1;
	/** Indicates whether this painter is active */
	private boolean fIsActive;

	private IUnifiedEditor editor;
	private IPreferenceStore store;
	
	/**
	 * Map of language -> set of language parents.
	 */
	private Map<String, Set<String>> lanaguageParents;

	/**
	 * Creates a new painter for the given source viewer.
	 * 
	 * @param editor
	 * @param textViewer
	 *            the source viewer for which to create a painter
	 */
	public UnifiedCursorLinePainter(IUnifiedEditor editor, ITextViewer textViewer)
	{
		fViewer = textViewer;
		this.editor = editor;
		store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Sets the color in which to draw the background of the cursor line.
	 * 
	 * @param highlightColor
	 *            the color in which to draw the background of the cursor line
	 */
	public void setHighlightColor(Color highlightColor)
	{
		fHighlightColor = highlightColor;
	}

	/**
	 * @see org.eclipse.swt.custom.LineBackgroundListener#lineGetBackground(org.eclipse.swt.custom.LineBackgroundEvent)
	 */
	public void lineGetBackground(LineBackgroundEvent event)
	{
		// don't use cached line information because of asynchronous painting

		StyledText textWidget = fViewer.getTextWidget();
		if (textWidget != null)
		{

			int caret = textWidget.getCaretOffset();
			int length = event.lineText.length();

			if (event.lineOffset <= caret && caret <= event.lineOffset + length)
			{
				event.lineBackground = fHighlightColor;
			}
			else
			{
				event.lineBackground = textWidget.getBackground();
			}
			try
			{
				int lineOffset = event.lineOffset;
				// Account for folding
				if (fViewer instanceof ITextViewerExtension5)
				{
					lineOffset = ((ITextViewerExtension5) fViewer).widgetOffset2ModelOffset(lineOffset);
				}
				EditorFileContext fileContext = editor.getFileContext();

				// wrapper could be non-null, but interior is.
				if (fileContext != null && fileContext.getFileContext() != null)
				{

					IParseState parseState = fileContext.getParseState();

					if (parseState != null)
					{
						LexemeList lexemeList = parseState.getLexemeList();
						if (lexemeList != null)
						{
							//counting the reference lexeme to determine the background
							Lexeme finalLexeme = countBackgroundReferenceLexemeByOffset(
									lineOffset, length, lexemeList, parseState);
							
							if (finalLexeme != null)
							{
								
								if (finalLexeme != null)
								{
									//trying to get the line highlight color taking into account 
									//current document offset
									if (event.lineOffset <= caret && caret <= event.lineOffset + length)
									{
										event.lineBackground = getLineHighlightBackgroundByLexeme(finalLexeme);
									}
									else
									{
										handleBackground(finalLexeme, event, textWidget,
												lineOffset, parseState, lexemeList);
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				// Do nothing, just use default background color for editor
			}
			catch (Error e)
			{
				// Do nothing, just use default background color for editor
			}
		}
	}

	private Color getBackgroundColor(IParseState state, Lexeme lexeme, int offset)
	{
		Color bg = null;
		if (lexeme != null)
		{
			String language = lexeme.getToken().getLanguage();
			// This is a temp fix to map the jscomment mime type to js so that we can do bgs
			if ("text/jscomment".equals(language)) //$NON-NLS-1$
			{
				language = "text/javascript"; //$NON-NLS-1$
			}
			LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(language);
			if (isEmptyLine(state.getLexemeList(), offset))
			{
				bg = colorizer.getEmptyLineBackground(state, offset);
			}
			else
			{
				bg = colorizer.getBackground(state, lexeme);
			}
		}
		return bg;
	}

	private boolean isEmptyLine(LexemeList list, int offset)
	{
		int index = -1;
		index = list.getLexemeCeilingIndex(offset);
		if (index == -1)
			return false;
		Lexeme candidate = list.get(index);
		if (fViewer instanceof ITextViewerExtension5)
		{
			int currentWidgetOffset = ((ITextViewerExtension5) fViewer).modelOffset2WidgetOffset(offset);
			int currentLineOffset = fViewer.getTextWidget().getLineAtOffset(currentWidgetOffset);
			int newWidgetOffset = ((ITextViewerExtension5) fViewer).modelOffset2WidgetOffset(candidate.offset
					+ candidate.length);
			int newLineOffset = fViewer.getTextWidget().getLineAtOffset(newWidgetOffset);
			if (newLineOffset > currentLineOffset)
			{
				// This is the case where we are trying to color a line but the next lexeme is on the next line and may
				// be a different language or context
				return true;
			}
		}
		return false;
	}

	/**
	 * Paints the lines from start to start+lineCount with the background obtained from the language registry
	 * 
	 * @param start
	 * @param lineCount
	 */
	public void paintLines(int start, int lineCount)
	{
		StyledText textWidget = fViewer.getTextWidget();
		if (textWidget != null)
		{
			try
			{
				EditorFileContext fileContext = editor.getFileContext();

				// wrapper could be non-null, but interior is.
				if (fileContext != null && fileContext.getFileContext() != null)
				{

					IParseState parseState = fileContext.getParseState();

					if (parseState != null)
					{

						LexemeList lexemeList = parseState.getLexemeList();
						if (lexemeList != null)
						{
							for (int i = 0; i < lineCount; i++)
							{
								int offset = textWidget.getOffsetAtLine(start + i);
								// Account for folding
								if (fViewer instanceof ITextViewerExtension5)
								{
									offset = ((ITextViewerExtension5) fViewer).widgetOffset2ModelOffset(offset);
								}
								int index = lexemeList.getLexemeCeilingIndex(offset);
								if (index > -1)
								{
									Color bg = getBackgroundColor(parseState, lexemeList.get(index), offset);
									Color pianoed = createPianoedColor(bg);
									if (start + i % 2 != 0 && pianoed != null
											&& store.getBoolean(IPreferenceConstants.SHOW_PIANO_KEYS))
									{
										textWidget.setLineBackground(start + i, 1, pianoed);
									}
									else
									{
										textWidget.setLineBackground(start + i, 1, bg);
									}
								}
								else
								{
									index = lexemeList.getLexemeFloorIndex(offset);
									if (index > -1)
									{
										Color bg = getBackgroundColor(parseState, lexemeList.get(index), offset);
										Color pianoed = createPianoedColor(bg);
										if (start + i % 2 != 0 && pianoed != null
												&& store.getBoolean(IPreferenceConstants.SHOW_PIANO_KEYS))
										{
											textWidget.setLineBackground(start + i, 1, pianoed);
										}
										else
										{
											textWidget.setLineBackground(start + i, 1, bg);
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
				// Do nothing, just use default background color for editor
			}
			catch (Error e)
			{
				// Do nothing, just use default background color for editor
			}
		}
	}

	private Color createPianoedColor(Color bg)
	{
		if (bg == null)
		{
			return null;
		}
		int factor = store.getInt(IPreferenceConstants.PIANO_KEY_DIFFERENCE);
		boolean canGoDarker = bg.getRed() - factor > 0 || bg.getGreen() - factor > 0 || bg.getBlue() - factor > 0;
		boolean canGoLighter = bg.getRed() + factor > 0 || bg.getGreen() + factor > 0 || bg.getBlue() + factor > 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		if (canGoDarker)
		{
			red = bg.getRed() - factor > 0 ? (int) (bg.getRed() - factor) : bg.getRed();
			green = bg.getGreen() - factor > 0 ? (int) (bg.getGreen() - factor) : bg.getGreen();
			blue = bg.getBlue() - factor > 0 ? (int) (bg.getBlue() - factor) : bg.getBlue();
		}
		else if (canGoLighter)
		{
			red = bg.getRed() + factor > 0 ? (int) (bg.getRed() + factor) : bg.getRed();
			green = bg.getGreen() + factor > 0 ? (int) (bg.getGreen() + factor) : bg.getGreen();
			blue = bg.getBlue() + factor > 0 ? (int) (bg.getBlue() + factor) : bg.getBlue();
		}
		if (canGoDarker || canGoLighter)
		{
			Color deadSpace = UnifiedColorManager.getInstance().getColor(new RGB(red, green, blue));
			return deadSpace;
		}
		return null;
	}

	/**
	 * Updates all the cached information about the lines to be painted and to be cleared. Returns <code>true</code>
	 * if the line number of the cursor line has changed.
	 * 
	 * @return <code>true</code> if cursor line changed
	 */
	private boolean updateHighlightLine()
	{
		try
		{

			IDocument document = fViewer.getDocument();
			int modelCaret = getModelCaret();
			int lineNumber = document.getLineOfOffset(modelCaret);

			// redraw if the current line number is different from the last line number we painted
			// initially fLastLineNumber is -1
			if (lineNumber != fLastLineNumber || !fCurrentLine.overlapsWith(modelCaret, 0))
			{

				fLastLine.offset = fCurrentLine.offset;
				fLastLine.length = fCurrentLine.length;
				fLastLine.isDeleted = fCurrentLine.isDeleted;

				if (fCurrentLine.isDeleted)
				{
					fCurrentLine.isDeleted = false;
					fPositionManager.managePosition(fCurrentLine);
				}

				fCurrentLine.offset = document.getLineOffset(lineNumber);
				if (lineNumber == document.getNumberOfLines() - 1)
				{
					fCurrentLine.length = document.getLength() - fCurrentLine.offset;
				}
				else
				{
					fCurrentLine.length = document.getLineOffset(lineNumber + 1) - fCurrentLine.offset;
				}

				fLastLineNumber = lineNumber;
				return true;

			}

		}
		catch (BadLocationException e)
		{
		}

		return false;
	}

	/**
	 * Returns the location of the caret as offset in the source viewer's input document.
	 * 
	 * @return the caret location
	 */
	private int getModelCaret()
	{
		int widgetCaret = fViewer.getTextWidget().getCaretOffset();
		if (fViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) fViewer;
			return extension.widgetOffset2ModelOffset(widgetCaret);
		}
		IRegion visible = fViewer.getVisibleRegion();
		return widgetCaret + visible.getOffset();
	}

	/**
	 * Assumes the given position to specify offset and length of a line to be painted.
	 * 
	 * @param position
	 *            the specification of the line to be painted
	 */
	private void drawHighlightLine(Position position)
	{

		// if the position that is about to be drawn was deleted then we can't
		if (position.isDeleted())
		{
			return;
		}

		int widgetOffset = 0;
		if (fViewer instanceof ITextViewerExtension5)
		{

			ITextViewerExtension5 extension = (ITextViewerExtension5) fViewer;
			widgetOffset = extension.modelOffset2WidgetOffset(position.getOffset());
			if (widgetOffset == -1)
			{
				return;
			}

		}
		else
		{

			IRegion visible = fViewer.getVisibleRegion();
			widgetOffset = position.getOffset() - visible.getOffset();
			if (widgetOffset < 0 || visible.getLength() < widgetOffset)
			{
				return;
			}
		}

		StyledText textWidget = fViewer.getTextWidget();
		// check for https://bugs.eclipse.org/bugs/show_bug.cgi?id=64898
		// this is a guard against the symptoms but not the actual solution
		if (0 <= widgetOffset && widgetOffset <= textWidget.getCharCount())
		{
			Point upperLeft = textWidget.getLocationAtOffset(widgetOffset);
			int width = textWidget.getClientArea().width + textWidget.getHorizontalPixel();
			int height = textWidget.getLineHeight(widgetOffset);
			textWidget.redraw(0, upperLeft.y, width, height, false);
		}
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
	 */
	public void deactivate(boolean redraw)
	{
		if (fIsActive)
		{
			fIsActive = false;

			/*
			 * on turning off the feature one has to paint the currently highlighted line with the standard background
			 * color
			 */
			if (redraw)
			{
				drawHighlightLine(fCurrentLine);
			}

			fViewer.getTextWidget().removeLineBackgroundListener(this);

			if (fPositionManager != null)
			{
				fPositionManager.unmanagePosition(fCurrentLine);
			}

			fLastLineNumber = -1;
			fCurrentLine.offset = 0;
			fCurrentLine.length = 0;
		}
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#paint(int)
	 */
	public void paint(int reason)
	{
		if (fViewer.getDocument() == null)
		{
			deactivate(false);
			return;
		}

		StyledText textWidget = fViewer.getTextWidget();

		// check selection
		Point selection = textWidget.getSelection();
		int startLine = textWidget.getLineAtOffset(selection.x);
		int endLine = textWidget.getLineAtOffset(selection.y);
		if (startLine != endLine)
		{
			deactivate(true);
			return;
		}

		// initialization
		if (!fIsActive)
		{
			textWidget.addLineBackgroundListener(this);
			fPositionManager.managePosition(fCurrentLine);
			fIsActive = true;
		}

		// redraw line highlight only if it hasn't been drawn yet on the respective line
		if (updateHighlightLine())
		{
			// clear last line
			drawHighlightLine(fLastLine);
			// draw new line
			drawHighlightLine(fCurrentLine);
		}
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.text.IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager)
	{
		fPositionManager = manager;
	}
	
	/**
	 * Gets line highlight background color by lexeme.
	 * @param lexeme - lexeme.
	 * @return line highlight color or null if not found.
	 */
	private Color getLineHighlightBackgroundByLexeme(Lexeme lexeme)
	{
		String language = lexeme.getToken().getLanguage();
		if (language == null)
		{
			return null;
		}
			
		LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(language);
		if (colorizer == null)
		{
			return null;
		}
		return colorizer.getLineHighlightColor();
	}
	
	/**
	 * Handles background color setting.
	 * @param lexeme - reference lexeme.
	 * @param event - line colorization event.
	 * @param textWidget - text widget.
	 * @param lineOffset - line offset.
	 * @param parseState - parse state.
	 * @param lexemeList - lexeme list.
	 */
	private void handleBackground(Lexeme lexeme, LineBackgroundEvent event,
			StyledText textWidget, int lineOffset, IParseState parseState,
			LexemeList lexemeList) 
	{
		int lineNumber = textWidget.getLineAtOffset(event.lineOffset);
		Color bg = getBackgroundColor(parseState, lexeme, lineOffset);
		Color pianoed = createPianoedColor(bg);
		if (lineNumber % 2 != 0 && pianoed != null
				&& store.getBoolean(IPreferenceConstants.SHOW_PIANO_KEYS))
		{
			event.lineBackground = pianoed;
		}
		else
		{
			event.lineBackground = bg;
		}
	}
	
	/**
	 * Counts background reference lexeme by offset.
	 * @param lineOffset - line offset.
	 * @param lineLength - line length.
	 * @param lexemeList - lexeme list.
	 * @param state - parse state.
	 * 
	 * @return reference lexeme or null.
	 */
	private Lexeme countBackgroundReferenceLexemeByOffset(int lineOffset, int lineLength,
			LexemeList lexemeList, IParseState state) 
	{
		Lexeme ceilingLexeme = null;
		Lexeme floorLexeme = null;
		int ceilingIndex = lexemeList.getLexemeCeilingIndex(lineOffset);
		int floorIndex = lexemeList.getLexemeFloorIndex(lineOffset);
		if (ceilingIndex > -1)
		{
			ceilingLexeme = lexemeList.get(ceilingIndex);
		}
		if (floorIndex > -1)
		{
			floorLexeme = lexemeList.get(floorIndex);
		}
		
		Lexeme finalLexeme = null;
		
		if (ceilingIndex != -1 && floorIndex != -1 && ceilingIndex != floorIndex)
		{
			//if we have two lexemes for position, we need to pick a better one
			finalLexeme = chooseBetterBackgroundLexeme(ceilingLexeme, floorLexeme, state, 
					lineOffset, lineLength);
		}
		else
		{
			finalLexeme = ceilingLexeme != null ? ceilingLexeme : floorLexeme;
		}
		return finalLexeme;
	}

	/**
	 * Chooses the best background reference lexeme.
	 * @param ceilingLexeme - ceiling lexeme.
	 * @param floorLexeme - floor lexeme.
	 * @param parseState - parse state.
	 * @param lineOffset - line offset.
	 * @param lineLength - line length.
	 * 
	 * @return better lexeme.
	 */
	private Lexeme chooseBetterBackgroundLexeme(Lexeme ceilingLexeme,
			Lexeme floorLexeme, IParseState parseState, int lineOffset, int lineLength) 
	{
		String ceilingLanguage = ceilingLexeme.getToken().getLanguage();
		String floorLanguage = floorLexeme.getToken().getLanguage();
		if (ceilingLanguage == floorLanguage)
		{
			return ceilingLexeme;
		}
		
		if (lanaguageParents == null)
		{
			makeLaguageRelationsCache(parseState);
		}
		
		Set<String> parents = lanaguageParents.get(ceilingLanguage);
		
		boolean ceilingLanguageIsChild = false; 
		if (parents == null)
		{
			makeLaguageRelationsCache(parseState);
			parents = lanaguageParents.get(ceilingLanguage);
			if (parents == null)
			{
				ceilingLanguageIsChild = false;
			}
		}
		
		if (parents != null)
		{
			ceilingLanguageIsChild = parents.contains(floorLanguage);
		}
		
		if (ceilingLanguageIsChild)
		{
			return ceilingLexeme;
		}
		else
		{
			if (ceilingLexeme.getStartingOffset() >= lineOffset
					&& ceilingLexeme.getStartingOffset() < lineOffset + lineLength)
			{
				return ceilingLexeme;
			}
			else 
			{
				return floorLexeme;
			}
		}
	}

	/**
	 * Makes language parents cache.
	 * @param parseState - parse state.
	 */
	private void makeLaguageRelationsCache(IParseState parseState) 
	{
		lanaguageParents = new HashMap<String, Set<String>>();
		
		//counting root parse state.
		IParseState rootState = parseState;
		while(rootState.getParent() != null)
		{
			rootState = rootState.getParent();
		}

		cacheLanguageChildrenLerations(parseState);
	}

	/**
	 * Adds the information about relations between argument language
	 * and it's children to the relations cache.  
	 * @param parseState - language parse state.
	 */
	private void cacheLanguageChildrenLerations(IParseState parseState) 
	{
		String language = parseState.getLanguage();
		if (language == null)
		{
			return;
		}
		
		IParseState[] children = parseState.getChildren();
		if (children == null)
		{
			return;
		}
		
		for (IParseState childState : children)
		{
			String childLanguage = childState.getLanguage();
			if (childLanguage != null)
			{
				Set<String> currentParents = lanaguageParents.get(childLanguage);
				if (currentParents == null)
				{
					currentParents = new HashSet<String>();
					lanaguageParents.put(childLanguage, currentParents);
				}
				
				currentParents.add(language);
				
				cacheLanguageChildrenLerations(childState);
			}
		}
	}
}
