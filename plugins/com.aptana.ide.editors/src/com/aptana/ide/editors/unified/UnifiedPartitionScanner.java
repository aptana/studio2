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
import java.util.List;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Robin Debreuil
 */
public class UnifiedPartitionScanner implements IPartitionScanner
{
	private FileService _fileService;
	private List<TypedRegion> _partitions;
	private int _currentOffset;
	private String _defaultLanguage;
	private String _currentLanguage;

	/**
	 * UnifiedPartitionScanner
	 * 
	 * @param service
	 * @param defaultLanguage
	 */
	public UnifiedPartitionScanner(FileService service, String defaultLanguage)
	{
		if (service == null)
		{
			throw new IllegalArgumentException(Messages.UnifiedPartitionScanner_ServiceNotNull);
		}
		if (defaultLanguage == null || defaultLanguage.length() == 0)
		{
			throw new IllegalArgumentException(Messages.UnifiedPartitionScanner_DefaultLanguageMustBeDefined);
		}
		
		this._fileService = service;
		this._partitions = new ArrayList<TypedRegion>();
		this._defaultLanguage = defaultLanguage;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPartitionScanner#startPartitionScan()
	 */
	public void startPartitionScan()
	{
		this._partitions.clear();
		this._currentOffset = 0;
		this._currentLanguage = this._defaultLanguage;
		this._fileService.setLanguageChangeListener(this);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPartitionScanner#endPartitionScan()
	 */
	public ITypedRegion[] endPartitionScan()
	{
		ITypedRegion[] result;
		
		// remove change listener
		this._fileService.setLanguageChangeListener(null);
		
		// get the document's text length
		int sourceLength = this._fileService.getSourceProvider().getSourceLength();
		
		// grab the lexeme list
		LexemeList lexemes = this._fileService.getLexemeList();
		
		if (lexemes != null && lexemes.size() > 0)
		{
			// grab the last lexeme and its ending offset
			Lexeme lastLexeme = lexemes.get(lexemes.size() - 1);
			int endingOffset = lastLexeme.offset + lastLexeme.length;
	
			// go right to end of doc - use default language for last whitespace,
			// unless we are currently in the default lang, in which case just grow it to the end
			if (this._currentLanguage.equals(this._defaultLanguage))
			{
				int length = sourceLength - this._currentOffset;
				
				this._partitions.add(new TypedRegion(this._currentOffset, length, this._defaultLanguage));
			}
			else
			{
				int length = endingOffset - this._currentOffset;
				int whitespaceLength = sourceLength - endingOffset;
				
				this._partitions.add(new TypedRegion(this._currentOffset, length, this._currentLanguage));
				this._partitions.add(new TypedRegion(endingOffset, whitespaceLength, this._defaultLanguage));
			}
			
			result = this._partitions.toArray(new ITypedRegion[this._partitions.size()]);
		}
		else
		{
			result = new ITypedRegion[] { new TypedRegion(0, sourceLength, this._defaultLanguage) };
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IPartitionScanner#startNewLanguage(java.lang.String, int)
	 */
	public void startNewLanguage(String type, int offset)
	{
		if (this._currentLanguage.equals(type) == false)
		{
			int length = offset - this._currentOffset;
			TypedRegion typedRegion = new TypedRegion(this._currentOffset, length, this._currentLanguage);
			
			this._partitions.add(typedRegion);
			this._currentOffset = offset;
			this._currentLanguage = type;
		}
	}
}
