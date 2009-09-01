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
package com.aptana.ide.editor.js;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.JSCommentFileLanguageService;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.ScriptDocFileLanguageService;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParseState;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.unified.BaseFileServiceFactory;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public final class JSFileServiceFactory extends BaseFileServiceFactory
{
	private static JSFileServiceFactory instance;
	
	/**
	 * getInstance
	 * 
	 * @return JSFileServiceFactory
	 */
	public static JSFileServiceFactory getInstance()
	{
		if (instance == null)
		{
			instance = new JSFileServiceFactory();
		}

		return instance;
	}

	/**
	 * JSFileServiceFactory
	 */
	private JSFileServiceFactory()
	{
		// add file service to profiles manager
		UnifiedEditorsPlugin editorsPlugin = UnifiedEditorsPlugin.getDefault();

		if (editorsPlugin != null)
		{
			ProfileManager profileManager = editorsPlugin.getProfileManager();
			ILanguageEnvironment jsEnvironment = JSLanguageEnvironment.getInstance();

			profileManager.addLanguageSupport(JSMimeType.MimeType, jsEnvironment, this);
		}
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
			result = LanguageRegistry.getScanner(JSMimeType.MimeType);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileServiceFactory#createFileService(com.aptana.ide.editors.unified.IFileSourceProvider,
	 *      boolean)
	 */
	public FileService createFileService(IFileSourceProvider sourceProvider, boolean parse)
	{
		// test if this file is already in profiles, if so just use the cached FileService
		if (PluginUtils.isPluginLoaded(UnifiedEditorsPlugin.getDefault()))
		{
			// ProfileManager pm = UnifiedEditorsPlugin.getDefault().getProfileManager();
			String uri = sourceProvider.getSourceURI();
			FileService context = FileContextManager.get(uri);

			// Remove check on instanceof FileService
			if (context != null) // && context instanceof FileService)
			{
				FileService fs = context;
				IParseState ps = fs.getParseState();

				// need source provider to do a full parse
				IFileSourceProvider sp = fs.getSourceProvider();

				if (ps != null && sp != null && sp == sourceProvider) // make sure not disposed/sanity check
				{
					fs.doFullParse();

					return fs;
				}
			}
		}

		IParser parser = this.createParser();
		IParser scanner = this.createScanner();
		
		IParseState parseState = parser.createParseState(null);
		FileService fileService = new FileService(parser, parseState, sourceProvider, JSMimeType.MimeType);
		fileService.setScanner(scanner);

		ParentOffsetMapper parentMapper = new ParentOffsetMapper(fileService);

		fileService.setErrorManager(new JSErrorManager(fileService));

		JSFileLanguageService jsfls = new JSFileLanguageService(fileService, parseState, parser, parentMapper);
		fileService.addLanguageService(JSMimeType.MimeType, jsfls);

		IParser scriptDocParser = parser.getParserForMimeType(ScriptDocMimeType.MimeType);
		ScriptDocFileLanguageService sdfls = new ScriptDocFileLanguageService(fileService, parseState, scriptDocParser,
				parentMapper);
		fileService.addLanguageService(ScriptDocMimeType.MimeType, sdfls);

		IParser jsCommentParser = parser.getParserForMimeType(JSCommentMimeType.MimeType);
		JSCommentFileLanguageService cmfls = new JSCommentFileLanguageService(fileService, parseState, jsCommentParser,
				parentMapper);
		fileService.addLanguageService(JSCommentMimeType.MimeType, cmfls);

		// set the docstore for parsed doc objects (this comes from the ScriptDocParseState)
		ScriptDocParseState sdps = null;
		IParseState[] children = parseState.getChildren();

		if (children != null)
		{
			for (int i = 0; i < children.length; i++)
			{
				IParseState state = children[i];

				if (state instanceof ScriptDocParseState)
				{
					sdps = (ScriptDocParseState) state;
					break;
				}
			}
		}
		if (sdps != null)
		{
			jsfls.setDocumentationStore(sdps.getDocumentationStore());
		}
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
		IParser parser = LanguageRegistry.getParser(JSMimeType.MimeType);

		if (parser == null)
		{
			IdeLog.logError(JSPlugin.getDefault(), StringUtils.format(
				Messages.JSFileServiceFactory_DBG_Parser_for_mime_type_not_registered,
				JSMimeType.MimeType)
			);
		}

		return parser;
	}
}
