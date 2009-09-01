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
package com.aptana.ide.editor.scriptdoc;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.Messages;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.BaseFileServiceFactory;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public final class ScriptDocFileServiceFactory extends BaseFileServiceFactory
{
	private static ScriptDocFileServiceFactory instance;

	/**
	 * ScriptDocFileServiceFactory
	 */
	private ScriptDocFileServiceFactory()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return ScriptDocFileServiceFactory
	 */
	public static ScriptDocFileServiceFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptDocFileServiceFactory();
		}

		return instance;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileServiceFactory#createFileService(com.aptana.ide.editors.unified.IFileSourceProvider,
	 *      boolean)
	 */
	public FileService createFileService(IFileSourceProvider sourceProvider, boolean parse)
	{
		IParser parser = this.createParser();
		IParser scanner = null; //this.createScanner();
		
		IParseState parseState = parser.createParseState(null);
		FileService fileService = new FileService(parser, parseState, sourceProvider, ScriptDocMimeType.MimeType);
		fileService.setScanner(scanner);
		// fileService.setErrorManager(new ScriptDocErrorManager(fileService));

		IParentOffsetMapper mapper = new ParentOffsetMapper(fileService);

		ScriptDocFileLanguageService languageService = new ScriptDocFileLanguageService(fileService, parseState,
				parser, mapper);
		fileService.addLanguageService(ScriptDocMimeType.MimeType, languageService);

		if (parse)
		{
			fileService.doFullParse();
		}
		return fileService;
	}
	
	/**
	 * createParser
	 * 
	 * @return IParser
	 */
	protected IParser createParser()
	{
		IParser parser = LanguageRegistry.getParser(ScriptDocMimeType.MimeType);

		if (parser == null)
		{
			IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(
				Messages.JSFileServiceFactory_DBG_Parser_for_mime_type_not_registered,
				ScriptDocMimeType.MimeType)
			);
		}

		return parser;
	}
	
	/**
	 * createScanner
	 * 
	 * @return - parser
	 */
	protected IParser createScanner()
	{
		IParser result = null;

		if (UnifiedEditorsPlugin.getDefault().useFastScan())
		{
			result = LanguageRegistry.getScanner(ScriptDocMimeType.MimeType);
		}

		return result;
	}
}
