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

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.ICodeLocation;

/**
 * 
 */
public final class ParentOffsetMapper implements IParentOffsetMapper, IFileContextListener
{
	private FileService _fileService;
	private int _currentLexemeIndex;
	private Lexeme _currentLexeme;
	private Map<String, IChildOffsetMapper> _children;
	private boolean _isDisposing = false;

	/**
	 * Creates a new OffsetMapper for this type that maps offsets to lexemes
	 * 
	 * @param fileService
	 *            The Language Service
	 */
	public ParentOffsetMapper(FileService fileService)
	{
		if (fileService == null)
		{
			throw new NullPointerException(Messages.ParentOffsetMapper_IFileContentNotNull);
		}

		this._fileService = fileService;
		this._children = new HashMap<String, IChildOffsetMapper>();
		
		fileService.addFileListener(this);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IParentOffsetMapper#getFileService()
	 */
	public FileService getFileService()
	{
		return this._fileService;
	}

	/**
	 * Adds a child mapper for the given language
	 * 
	 * @param mimeType
	 * @param child
	 */
	public void addChildMapper(String mimeType, IChildOffsetMapper child)
	{
		this._children.put(mimeType, child);
	}

	/**
	 * gets a child mapper for a given language
	 * 
	 * @param mimeType
	 * @return IChildOffsetMapper
	 */
	public IChildOffsetMapper getChild(String mimeType)
	{
		if (this._children.containsKey(mimeType))
		{
			return this._children.get(mimeType);
		}
		else
		{
			throw new IllegalStateException(mimeType + Messages.ParentOffsetMapper_LanguageNotSupported);
		}
	}

	/**
	 * @see com.aptana.ide.parsing.IOffsetMapper#getLexemeList()
	 */
	public LexemeList getLexemeList()
	{
		return this._fileService.getLexemeList();
	}

	/**
	 * Calculates the index and lexeme that the given offset is within and caches it. This accounts
	 * for whitespace areas by setting the result to the previous lexeme if available.
	 * 
	 * @param offset
	 */
	public void calculateCurrentLexeme(int offset)
	{
		LexemeList lexemeList = this.getLexemeList();
		
		if (lexemeList != null)
		{
			synchronized (lexemeList)
			{
				// get rid of impossible offsets
				this._currentLexemeIndex = getLexemeIndexFromDocumentOffset(offset);
				
				if (this._currentLexemeIndex > -1)
				{
					this._currentLexeme = getLexemeList().get(this._currentLexemeIndex);
				}
				else
				{
					this._currentLexeme = null;
				}
			}
		}
	}

	/**
	 * Gets the lexeme at the specified index.
	 * 
	 * @param index
	 * @return Returns the current lexeme at that index, or null if not found.
	 */
	public Lexeme getLexemeAtIndex(int index)
	{
		Lexeme result = null;

		if (index >= 0)
		{
			LexemeList ll = this._fileService.getLexemeList();
			
			if (ll != null)
			{
				synchronized (ll)
				{
					if (index < ll.size())
					{
						result = ll.get(index);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Gets the cached current Lexeme based on the current offset of the document.
	 * 
	 * @return Returns the current lexeme.
	 */
	public Lexeme getCurrentLexeme()
	{
		return this._currentLexeme;
	}

	/**
	 * Gets the cached current Lexeme index based on the offset in the current document.
	 * 
	 * @return Returns the current lexeme index.
	 */
	public int getCurrentLexemeIndex()
	{
		return this._currentLexemeIndex;
	}

	/**
	 * Calculates and returns the Lexeme index at the current document offset. Note that document
	 * offsets are one greater that lexeme offsets. Use getCurrentLexemeIndex if querying for the
	 * current caret offset.
	 * 
	 * @param offset
	 *            The offset in the document to check at.
	 * @return Returns the index of the Lexeme at the current offset.
	 */
	public int getLexemeIndexFromDocumentOffset(int offset)
	{
		return getLexemeIndexFromDocumentOffset(offset, getLexemeList());
	}

	/**
	 * Calculates and returns the Lexeme index at the current document offset. Note that document
	 * offsets are one greater that lexeme offsets. Use getCurrentLexemeIndex if querying for the
	 * current caret offset. Basically, if the cursor is at the start of the "current" lexeme, we
	 * consider the current lexeme really the previous one i.e. <html>I<body> ">" is the current
	 * lexeme, not "<body"
	 * 
	 * @param offset
	 *            The offset in the document to check at.
	 * @param ll
	 *            The current list of lexemes.
	 * @return Returns the index of the Lexeme at the current offset.
	 */
	public static int getLexemeIndexFromDocumentOffset(int offset, LexemeList ll)
	{
		int index = -1;
		
		if (offset >= 0 && ll != null)
		{
			synchronized (ll)
			{
				if (ll.size() > 0)
				{
					index = ll.getLexemeIndex(offset - 1); // - 1
					
					if (index < 1) // zero is a special case
					{
						if (index < -1)
						{
							index = -index - 2;
						}
						else
						{
							index = 0;
						}
					}
				}
			}
		}
		
		return index;
	}

	/**
	 * Runs whenever a FileChangedEvent occurs
	 * 
	 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
	 */
	public void onContentChanged(FileContextContentEvent evt)
	{
		this.calculateCurrentLexeme(evt.getOffset());
	}

	/**
	 * @see com.aptana.ide.parsing.IOffsetMapper#findTarget(com.aptana.ide.lexer.Lexeme)
	 */
	public ICodeLocation findTarget(Lexeme lexeme)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IParentOffsetMapper#dispose()
	 */
	public void dispose()
	{
		if (this._isDisposing)
		{
			return;
		}
		
		this._isDisposing = true;

		this._fileService.removeFileListener(this);
		
		if (this._children != null)
		{
			Object[] objs = this._children.values().toArray();
			
			for (int i = 0; i < objs.length; i++)
			{
				IChildOffsetMapper child = (IChildOffsetMapper) objs[i];
				child.dispose();
			}
			
			this._children.clear();
			this._children = null;
		}

		this._fileService = null;
		this._currentLexeme = null;
	}
}
