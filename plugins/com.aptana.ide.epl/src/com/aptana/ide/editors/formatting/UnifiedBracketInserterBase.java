/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.editors.formatting;

import java.util.Arrays;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3.InsertMode;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.epl.Activator;

/**
 * @author Paul Colton
 */
public abstract class UnifiedBracketInserterBase implements VerifyKeyListener, ILinkedModeListener
{
	/**
	 * sourceViewer
	 */
	protected ISourceViewer sourceViewer;

	/**
	 * UnifiedBracketInserter
	 * 
	 * @param sourceViewer
	 */
	public UnifiedBracketInserterBase(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
	}

	/**
	 * hasPeerCharacter
	 * 
	 * @param character
	 * @return boolean
	 */
	public static boolean hasPeerCharacter(char character)
	{
		switch (character)
		{
			case '(':
			case ')':
			case '<':
			case '>':
			case '[':
			case ']':
			case '"':
			case '\'':
			case '{':
			case '}':
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	/**
	 * getPeerCharacter
	 * 
	 * @param character
	 * @return char
	 */
	public static char getPeerCharacter(char character)
	{
		switch (character)
		{
			case '(':
				return ')';

			case ')':
				return '(';

			case '<':
				return '>';

			case '>':
				return '<';

			case '[':
				return ']';

			case ']':
				return '[';

			case '"':
				return character;

			case '\'':
				return character;

			case '{':
				return '}';

			case '}':
				return '{';

			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * This checks if a string has a balanced amount of the given char eg:() [] "" '' This is true if the string is
	 * balanced after the character typed, but before the potential auto-insert.
	 * 
	 * @param source
	 * @param charToCheck
	 * @param isInserting
	 * @return Returns true if the string is balanced after the character typed, but before the potential auto-insert.
	 */
	public static boolean isStringBalanced(String source, char charToCheck, boolean isInserting)
	{
		if (!hasPeerCharacter(charToCheck))
		{
			return true;
		}

		char peer = getPeerCharacter(charToCheck);
		char[] sourceChars = source.toCharArray();
		int charCount = 0;
		int peerCount = 0;
		for (int i = 0; i < sourceChars.length; i++)
		{
			if (sourceChars[i] == charToCheck)
			{
				charCount++;
			}
			else if (sourceChars[i] == peer)
			{
				peerCount++;
			}
		}

		// for strings and quotes just check for even and odd
		// we don't care about insert vs delete in this case as
		// we just want to know if one char will make it even
		if (charToCheck == '"' || charToCheck == '\'')
		{
			double firstChangeOnly = charCount + 1;
			return (firstChangeOnly % 2) == 0;
		}

		int insertCount = isInserting ? 1 : -1;
		return charCount + insertCount == peerCount;
	}

	/**
	 * fCloseBrackets
	 */
	protected boolean fCloseBrackets = true;

	/**
	 * fCloseStrings
	 */
	protected boolean fCloseStrings = true;

	/**
	 * fCloseAngularBrackets
	 */
	protected boolean fCloseAngularBrackets = false;

	private final String CATEGORY = toString();

	private IPositionUpdater fUpdater = new ExclusivePositionUpdater(CATEGORY);

	private Stack fBracketLevelStack = new Stack();

	/**
	 * Set close bracket enabled
	 * 
	 * @param enabled
	 */
	public void setCloseBracketsEnabled(boolean enabled)
	{
		fCloseBrackets = enabled;
	}

	/**
	 * Set close strings enabled
	 * 
	 * @param enabled
	 */
	public void setCloseStringsEnabled(boolean enabled)
	{
		fCloseStrings = enabled;
	}

	/**
	 * Set close angular brackets enabled
	 * 
	 * @param enabled
	 */
	public void setCloseAngularBracketsEnabled(boolean enabled)
	{
		fCloseAngularBrackets = enabled;
	}

	/**
	 * Is angualar introducer
	 * 
	 * @param identifier
	 * @return - true if angular introducer
	 */
	protected boolean isAngularIntroducer(String identifier)
	{
		return identifier.length() > 0
				&& (Character.isUpperCase(identifier.charAt(0)) || identifier.startsWith("final") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("protected") //$NON-NLS-1$
				|| identifier.startsWith("private")); //$NON-NLS-1$
	}

	/**
	 * Gets the insert mode
	 * 
	 * @return - insert mode
	 */
	public InsertMode getInsertMode()
	{
		return AbstractDecoratedTextEditor.SMART_INSERT;
	}

	/**
	 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
	 */
	public void verifyKey(VerifyEvent event)
	{

		// early pruning to slow down normal typing as little as possible
		if (!event.doit || getInsertMode() != AbstractDecoratedTextEditor.SMART_INSERT)
		{
			return;
		}

		// Don't insert if it's not the proper character
		if (!isAutoInsertCharacter(event.character))
		{
			return;
		}

		// Don't insert if auto-insert is not enabled
		if (!isAutoInsertEnabled())
		{
			return;
		}

		IDocument document = sourceViewer.getDocument();

		final Point selection = sourceViewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;

		try
		{
			if (!isValidAutoInsertLocation(event.character, offset, length))
			{
				return;
			}

			final char character = event.character;
			final char closingCharacter = getPeerCharacter(character);
			final StringBuffer buffer = new StringBuffer();
			buffer.append(character);
			buffer.append(closingCharacter);

			// We can only draw the green line if a character is after the inserted characters
			// This code inserts a new line if the inserted text is at the end of the document and without this no green
			// bar would appear
			if (offset == document.getLength())
			{
				String delim = null;
				if (document instanceof IDocumentExtension4)
				{
					delim = ((IDocumentExtension4) document).getDefaultLineDelimiter();
				}
				if (delim == null)
				{
					delim = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append(delim);
			}

			document.replace(offset, length, buffer.toString());

			BracketLevel level = new BracketLevel();
			fBracketLevelStack.push(level);

			LinkedPositionGroup group = new LinkedPositionGroup();
			group.addPosition(new LinkedPosition(document, offset + 1, 0, LinkedPositionGroup.NO_STOP));

			LinkedModeModel model = new LinkedModeModel();
			model.addLinkingListener(this);
			model.addGroup(group);
			model.forceInstall();

			level.fOffset = offset;
			level.fLength = 2;

			// set up position tracking for our magic peers
			if (fBracketLevelStack.size() == 1)
			{
				document.addPositionCategory(CATEGORY);
				document.addPositionUpdater(fUpdater);
			}
			level.fFirstPosition = new Position(offset, 1);
			level.fSecondPosition = new Position(offset + 1, 1);
			document.addPosition(CATEGORY, level.fFirstPosition);
			document.addPosition(CATEGORY, level.fSecondPosition);

			level.fUI = new EditorLinkedModeUI(model, sourceViewer);
			level.fUI.setSimpleMode(true);
			level.fUI.setExitPolicy(new ExitPolicy(closingCharacter, getEscapeCharacter(closingCharacter),
					fBracketLevelStack));
			level.fUI.setExitPosition(sourceViewer, offset + 2, 0, Integer.MAX_VALUE);
			level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
			level.fUI.enter();

			IRegion newSelection = level.fUI.getSelectedRegion();
			sourceViewer.setSelectedRange(newSelection.getOffset(), newSelection.getLength());

			event.doit = doEvent(event);

			triggerAssistPopup(event);

		}
		catch (BadLocationException e)
		{
			IdeLog.logError(Activator.getDefault(), Messages.UnifiedBracketInserterBase_ERR_BadLocation, e);
		}
		catch (BadPositionCategoryException e)
		{
			IdeLog.logError(Activator.getDefault(), Messages.UnifiedBracketInserterBase_ERR_BadLocation, e);
		}
	}

	/**
	 * Alter the event before insertion
	 * 
	 * @param event
	 * @return - returns false by default
	 */
	protected boolean doEvent(VerifyEvent event)
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
	 */
	public void left(LinkedModeModel environment, int flags)
	{

		final BracketLevel level = (BracketLevel) fBracketLevelStack.pop();

		if (flags != ILinkedModeListener.EXTERNAL_MODIFICATION)
		{
			return;
		}

		// remove brackets
		final IDocument document = sourceViewer.getDocument();
		if (document instanceof IDocumentExtension)
		{
			IDocumentExtension extension = (IDocumentExtension) document;
			extension.registerPostNotificationReplace(null, new IDocumentExtension.IReplace()
			{

				public void perform(IDocument document, IDocumentListener owner)
				{
					if ((level.fFirstPosition.isDeleted || level.fFirstPosition.length == 0)
							&& !level.fSecondPosition.isDeleted
							&& level.fSecondPosition.offset == level.fFirstPosition.offset)
					{
						try
						{
							document.replace(level.fSecondPosition.offset, level.fSecondPosition.length, null);
						}
						catch (BadLocationException e)
						{
							IdeLog.logError(Activator.getDefault(), Messages.UnifiedBracketInserterBase_ERR_BadLocation, e);
						}
					}

					if (fBracketLevelStack.size() == 0)
					{
						document.removePositionUpdater(fUpdater);
						try
						{
							document.removePositionCategory(CATEGORY);
						}
						catch (BadPositionCategoryException e)
						{
							IdeLog.logError(Activator.getDefault(), Messages.UnifiedBracketInserterBase_ERR_BadLocation, e);
						}
					}
				}
			});
		}

	}

	/**
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#suspend(org.eclipse.jface.text.link.LinkedModeModel)
	 */
	public void suspend(LinkedModeModel environment)
	{
	}

	/**
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#resume(org.eclipse.jface.text.link.LinkedModeModel, int)
	 */
	public void resume(LinkedModeModel environment, int flags)
	{
	}

	/**
	 * Position updater that takes any changes at the borders of a position to not belong to the position.
	 * 
	 * @since 3.0
	 */
	private static class ExclusivePositionUpdater implements IPositionUpdater
	{

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 * 
		 * @param category
		 *            the new category.
		 */
		public ExclusivePositionUpdater(String category)
		{
			fCategory = category;
		}

		/**
		 * @see org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text.DocumentEvent)
		 */
		public void update(DocumentEvent event)
		{

			int eventOffset = event.getOffset();
			int eventOldLength = event.getLength();
			int eventNewLength = event.getText() == null ? 0 : event.getText().length();
			int deltaLength = eventNewLength - eventOldLength;

			try
			{
				Position[] positions = event.getDocument().getPositions(fCategory);

				for (int i = 0; i != positions.length; i++)
				{

					Position position = positions[i];

					if (position.isDeleted())
					{
						continue;
					}

					int offset = position.getOffset();
					int length = position.getLength();
					int end = offset + length;

					if (offset >= eventOffset + eventOldLength)
					{
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					}
					else if (end <= eventOffset)
					{
						// position comes way before change -
						// leave alone
					}
					else if (offset <= eventOffset && end >= eventOffset + eventOldLength)
					{
						// event completely internal to the position - adjust
						// length
						position.setLength(length + deltaLength);
					}
					else if (offset < eventOffset)
					{
						// event extends over end of position - adjust length
						int newEnd = eventOffset;
						position.setLength(newEnd - offset);
					}
					else if (end > eventOffset + eventOldLength)
					{
						// event extends from before position into it - adjust
						// offset
						// and length
						// offset becomes end of event, length adjusted
						// accordingly
						int newOffset = eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					}
					else
					{
						// event consumes the position - delete it
						position.delete();
					}
				}
			}
			catch (BadPositionCategoryException e)
			{
				// ignore and return
			}
		}

		/**
		 * Returns the position category.
		 * 
		 * @return the position category
		 */
		public String getCategory()
		{
			return fCategory;
		}

	}

	/**
	 * @author Ingo Muschenetz
	 */
	private static class BracketLevel
	{
		int fOffset;

		int fLength;

		LinkedModeUI fUI;

		Position fFirstPosition;

		Position fSecondPosition;
	}

	private static char getEscapeCharacter(char character)
	{
		switch (character)
		{
			case '"':
			case '\'':
				return '\\';
			default:
				return 0;
		}
	}

	/**
	 * @author Ingo Muschenetz
	 */
	private class ExitPolicy implements IExitPolicy
	{

		final char fExitCharacter;

		final char fEscapeCharacter;

		final Stack fStack;

		final int fSize;

		/**
		 * Creates a new exit policy
		 * 
		 * @param exitCharacter
		 * @param escapeCharacter
		 * @param stack
		 */
		public ExitPolicy(char exitCharacter, char escapeCharacter, Stack stack)
		{
			fExitCharacter = exitCharacter;
			fEscapeCharacter = escapeCharacter;
			fStack = stack;
			fSize = fStack.size();
		}

		/**
		 * Do exit
		 * 
		 * @param model
		 * @param event
		 * @param offset
		 * @param length
		 * @return the exit flags
		 */
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length)
		{

			if (fSize == fStack.size() && !isMasked(offset))
			{
				if (event.character == fExitCharacter)
				{
					BracketLevel level = (BracketLevel) fStack.peek();
					if (level.fFirstPosition.offset > offset || level.fSecondPosition.offset < offset)
					{
						return null;
					}
					if (level.fSecondPosition.offset == offset && length == 0)
					{
						// don't enter the character if if its the closing peer
						triggerAssistClose(event);
						return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
					}
				}
				// when entering an anonymous class between the parenthesis', we
				// don't want,
				// to jump after the closing parenthesis when return is pressed
				if (event.character == SWT.CR && offset > 0)
				{
					IDocument document = sourceViewer.getDocument();
					try
					{
						if (document.getChar(offset - 1) == '{')
						{
							return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
						}
					}
					catch (BadLocationException e)
					{
					}
				}
			}
			return null;
		}

		private boolean isMasked(int offset)
		{
			IDocument document = sourceViewer.getDocument();
			try
			{
				return fEscapeCharacter == document.getChar(offset - 1);
			}
			catch (BadLocationException e)
			{
			}
			return false;
		}
	}

	/**
	 * Is this location a valid place to insert the specified character?
	 * 
	 * @param character
	 *            The character inserted
	 * @param offset
	 *            The offset of the insert
	 * @param length
	 *            The length of the insertion
	 * @return - true by default
	 */
	protected boolean isValidAutoInsertLocation(char character, int offset, int length)
	{
		return true;
	}

	/**
	 * @param c
	 * @return - true if auto insert character
	 */
	private boolean isAutoInsertCharacter(char c)
	{
		char[] arr = getAutoInsertCharacters();
		Arrays.sort(arr);
		int val = Arrays.binarySearch(arr, c);
		return val >= 0;
	}

	/**
	 * getAutoInsertCharacters
	 * 
	 * @return char[]
	 */
	protected char[] getAutoInsertCharacters()
	{
		return new char[] { '"', '\'', '(', '[', '{' };
		// return new char[] { '"', '\'', '<', '(', '[', '{' };
	}

	/**
	 * isAutoInsertEnabled
	 * 
	 * @return boolean
	 */
	protected boolean isAutoInsertEnabled()
	{
		return true;
	}

	/**
	 * Forces code assist to appear assuming it can be shown
	 * 
	 * @param event
	 */
	protected void triggerAssistPopup(VerifyEvent event)
	{

	}

	/**
	 * Forces code assist to close
	 * 
	 * @param event
	 */
	protected void triggerAssistClose(VerifyEvent event)
	{

	}

}
