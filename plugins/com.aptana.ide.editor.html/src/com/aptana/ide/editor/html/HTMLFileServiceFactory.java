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
package com.aptana.ide.editor.html;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.CSSFileLanguageService;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLScanner;
import com.aptana.ide.editor.js.JSFileLanguageService;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.JSCommentFileLanguageService;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.ScriptDocFileLanguageService;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.unified.BaseFileServiceFactory;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class HTMLFileServiceFactory extends BaseFileServiceFactory
{
	private static HTMLFileServiceFactory instance;

	/**
	 * HTMLFileServiceFactory
	 */
	public HTMLFileServiceFactory()
	{
		// add HTML and JS to the profile manager
		// NOTE: must load JS in case no JS files are open
		ProfileManager profileManager = UnifiedEditorsPlugin.getDefault().getProfileManager();

		profileManager.addLanguageSupport(HTMLMimeType.MimeType, HTMLLanguageEnvironment.getInstance(), this);
		profileManager.addLanguageSupport(JSMimeType.MimeType, JSLanguageEnvironment.getInstance(),
				JSFileServiceFactory.getInstance());
	}

	/**
	 * createChildFileServices
	 * 
	 * @param parser
	 * @param fileService
	 * @param parseState
	 * @param mapper
	 */
	protected void createChildFileServices(IParser parser, FileService fileService, IParseState parseState,
			ParentOffsetMapper mapper)
	{
		this.createCSSFileService(parser, fileService, parseState, mapper);
		this.createJSFileService(parser, fileService, parseState, mapper);
	}

	/**
	 * createCSSFileService
	 * 
	 * @param parser
	 * @param fileService
	 * @param parseState
	 * @param mapper
	 */
	private void createCSSFileService(IParser parser, FileService fileService, IParseState parseState,
			ParentOffsetMapper mapper)
	{
		IParser cssParser = parser.getParserForMimeType(CSSMimeType.MimeType);
		CSSFileLanguageService cssService = new CSSFileLanguageService(fileService, parseState, cssParser, mapper);
		fileService.addLanguageService(CSSMimeType.MimeType, cssService);
	}

	/**
	 * createJSFileService
	 * 
	 * @param parser
	 * @param fileService
	 * @param parseState
	 * @param mapper
	 */
	private void createJSFileService(IParser parser, FileService fileService, IParseState parseState,
			ParentOffsetMapper mapper)
	{
		IParser jsParser = parser.getParserForMimeType(JSMimeType.MimeType);
		JSFileLanguageService jsfs = new JSFileLanguageService(fileService, parseState, jsParser, mapper);
		fileService.addLanguageService(JSMimeType.MimeType, jsfs);

		FileService jsContext = (FileService) jsfs.getFileContext();

		if (jsParser != null)
		{
			IParser scriptDocParser = jsParser.getParserForMimeType(ScriptDocMimeType.MimeType);
			ScriptDocFileLanguageService scriptDocService = new ScriptDocFileLanguageService(fileService, parseState,
					scriptDocParser, mapper);
			jsContext.addLanguageService(ScriptDocMimeType.MimeType, scriptDocService);

			IParser jsCommentParser = jsParser.getParserForMimeType(JSCommentMimeType.MimeType);
			JSCommentFileLanguageService jsCommentService = new JSCommentFileLanguageService(fileService, parseState,
					jsCommentParser, mapper);
			jsContext.addLanguageService(JSCommentMimeType.MimeType, jsCommentService);
		}
	}

	/**
	 * createParser
	 * 
	 * @return IParser
	 */
	protected IParser createParser()
	{
		IParser parser = LanguageRegistry.getParser(HTMLMimeType.MimeType);
		
		if (parser == null)
		{
			IdeLog.logError(
				HTMLPlugin.getDefault(),
				StringUtils.format(
					Messages.HTMLFileServiceFactory_ERR_CreateParser,
					HTMLMimeType.MimeType
				)
			);
		}
		
		return parser;
	}

	/**
	 * getInstance
	 * 
	 * @return HTMLFileServiceFactory
	 */
	public static HTMLFileServiceFactory getInstance()
	{
		if (instance == null)
		{
			instance = new HTMLFileServiceFactory();
		}

		return instance;
	}

	/**
	 * createFastParser
	 * 
	 * @return - parser
	 */
	protected IParser createScanner()
	{
		IParser result = null;

		if (UnifiedEditorsPlugin.getDefault().useFastScan())
		{
			result = LanguageRegistry.getScanner(HTMLMimeType.MimeType);
		}

		return result;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileServiceFactory#createFileService(com.aptana.ide.editors.unified.IFileSourceProvider,
	 *      boolean)
	 */
	public FileService createFileService(IFileSourceProvider sourceProvider, boolean parse)
	{
		IParser parser = this.createParser();
		IParser scanner = null; //this.createScanner();
		FileService fileService = null;
		
		if (parser != null)
		{
			// Temporarily(?) let scanner know who the parser is in case we don't
			// have a nested scanner for a given language
			if (scanner != null)
			{
				((HTMLScanner) scanner).setParser(parser);
			}
			
			IParseState parseState = parser.createParseState(null);

			fileService = new FileService(parser, parseState, sourceProvider, HTMLMimeType.MimeType);

			ParentOffsetMapper mapper = new ParentOffsetMapper(fileService);

			fileService.setScanner(scanner);
			fileService.setErrorManager(new HTMLErrorManager(fileService));

			HTMLFileLanguageService htmlService = new HTMLFileLanguageService(fileService, parseState, parser, mapper);
			fileService.addLanguageService(HTMLMimeType.MimeType, htmlService);

			this.createChildFileServices(parser, fileService, parseState, mapper);

			// fileService.addParser(new PHP5Parser());
			if (parse)
			{
				fileService.doFullParse();
			}
		}
		else
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLFileServiceFactory_ERR_NullParser);
		}
		
		return fileService;
	}
}
