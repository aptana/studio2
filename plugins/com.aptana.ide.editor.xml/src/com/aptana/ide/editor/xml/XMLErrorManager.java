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
package com.aptana.ide.editor.xml;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.validator.ColorizationValidator;
import com.aptana.ide.editor.xml.validator.LexerValidator;
import com.aptana.ide.editor.xml.validator.XMLValidator;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.FileContextContentEvent;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.IErrorManager;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.Messages;
import com.aptana.ide.editors.unified.errors.UnifiedErrorManager;
import com.aptana.ide.editors.validator.IValidator;

/**
 * @author Kevin Lindsey
 */
public class XMLErrorManager extends UnifiedErrorManager implements IErrorManager
{
	private ColorizationValidator _colorizationValidator;
	private LexerValidator _lexerValidator;
	private XMLValidator _xmlValidator;

	
	/**
	 * @param fileService
	 */
	public XMLErrorManager(FileService fileService)
	{
		super(fileService, XMLMimeType.MimeType);
	}
	
	
	/**
	 * getColorizationValidator
	 * 
	 * @return ColorizationValidator
	 */
	public ColorizationValidator getColorizationValidator()
	{
		if (this._colorizationValidator == null)
		{
			this._colorizationValidator = new ColorizationValidator();
		}
		
		return this._colorizationValidator;
	}

	/**
	 * getLexerValidator
	 * 
	 * @return LexerValidator
	 */
	public LexerValidator getLexerValidator()
	{
		if (this._lexerValidator == null)
		{
			this._lexerValidator = new LexerValidator();
		}

		return this._lexerValidator;
	}
	
	/**
	 * getXMLValidator
	 *
	 * @return XMLValidtor
	 */
	public XMLValidator getXMLValidator()
	{
		if (this._xmlValidator == null)
		{
			this._xmlValidator = new XMLValidator();
		}
		
		return this._xmlValidator;
	}

	/**
	 * @see com.aptana.ide.editors.unified.errors.UnifiedErrorManager#getPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPreferenceStore()
	{
		return XMLPlugin.getDefault().getPreferenceStore();
	}


	/**
	 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
	 */
	public void onContentChanged(FileContextContentEvent evt)
	{
		IFileService fileService = evt.getSource();
		IFileSourceProvider sourceProvider = fileService.getSourceProvider();
		String path = CoreUIUtils.getPathFromURI(sourceProvider.getSourceURI());
		String lowercasePath = path.toLowerCase(Locale.getDefault());

		try
		{
			String sourceString = sourceProvider.getSource();
			
			if (sourceProvider instanceof DocumentSourceProvider)
			{
				IValidator validator = null;

				if (lowercasePath.endsWith(".lxr")) //$NON-NLS-1$
				{
					validator = this.getLexerValidator();
				}
				else if (lowercasePath.endsWith(".col")) //$NON-NLS-1$
				{
					validator = this.getColorizationValidator();
				}
				else
				{
					super.onContentChanged(evt);
					return;
				}								
				if (validator != null)
				{
					IFileError[] errors = validator.parseForErrors(path, sourceString, sourceProvider, true, true, false);

					fileService.setFileErrors(errors);
				}
			}
		}
		catch (IOException e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedErrorManager_Error, e1);
		}
	}
}
