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
package com.aptana.ide.editor.scriptdoc.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editors.unified.EditorFileContext;

/**
 * @author Paul Colton
 */
public class ScriptDocContentAssistProcessor implements IContentAssistProcessor
{
	boolean initalPopup = false;
	private ISourceViewer sourceViewer;
	private EditorFileContext context;
	private static Image fIcon = getImageDescriptor("icons/at.gif").createImage(); //$NON-NLS-1$

	static String[][] tagDocs = {
			/*
				@alias
				@author
				@classDescription
				@constructor
				@deprecated
				@exception
				@extends
				@id
				@internal
				@link
				@memberof
				@method
				@namespace
				@native
				@param
				@private
				@projectDescription
				@return
				@sdoc
				@see
				@since
				@type 
				@version
				
				@copyright
				@license
			
			{
				"tag name",
				"tag usage",
				"tag desciption"
			},
			*/
		{ 
			"alias", //$NON-NLS-1$
			"alias", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Alias
		},
		{ 
			"author", //$NON-NLS-1$
			"author-name", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Author
		},
		{ 
			"classDescription", //$NON-NLS-1$
			"description", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Description
		},
		{
			"constructor", //$NON-NLS-1$
			"", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Ctor
		},
		{
			"copyright", //$NON-NLS-1$
			"description", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Copyright
		},
		{ 
			"deprecated", //$NON-NLS-1$
			"[description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Deprecated
		},
		{
			"exception", //$NON-NLS-1$
			"[{type-name[,type-name]}] [description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Exception
		},
		{
			"extends", //$NON-NLS-1$
			"{type-name[,type-name]} [description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Extends
		},
		{
			"id", //$NON-NLS-1$
			"type-name", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Id
		},
		{
			"license", //$NON-NLS-1$
			"description", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_License
		},
//		{
//			"internal",
//			"",
//			"For items which are internal to the hosting environment, like the internal HTML classes that create the document objects. Not normally needed in user docs."
//		},
		// @link
		{
			"memberOf", //$NON-NLS-1$
			"{type-name}", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_MemberOf
		},
		{
			"method", //$NON-NLS-1$
			"[method-name]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Method
		},
		{
			"namespace", //$NON-NLS-1$
			"dotted name", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Namespace
		},
//		{
//			"native",
//			"",
//			"For members that were declared natively, thus can't be deleted (like the javascript core objects - e.g. Math)."
//		},
		{ 
			"param", //$NON-NLS-1$
			"param-name [{type-name[,type-name]}] [description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Param 
		},
		{
			"private", //$NON-NLS-1$
			"", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Private_meaning_msg
		},
		{
			"projectDescription", //$NON-NLS-1$
			"", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_ProjectDescription
		},
		{ 
			"property", //$NON-NLS-1$
			"", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Property 
		},
		{ 
			"return",  //$NON-NLS-1$
			"[{type-name[,type-name]}] [description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Return
		},
		{
			"sdoc", //$NON-NLS-1$
			"path", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Sdoc
		},
		{
			"see", //$NON-NLS-1$
			"type-name/method-name#", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_See
		},
		{
			"since", //$NON-NLS-1$
			"version", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Since
		},
        {
			"type", //$NON-NLS-1$
			"[{type-name[,type-name]}] [description]", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Type
		},
		{
			"version", //$NON-NLS-1$
			"version-number", //$NON-NLS-1$
			Messages.ScriptDocContentAssistProcessor_Version
		}
	};
	
	/**
	 * ScriptDocContentAssistProcessor
	 * 
	 * @param context
	 * @param sourceViewer
	 */
	public ScriptDocContentAssistProcessor(EditorFileContext context, SourceViewer sourceViewer)
	{
		this.context = context;
		this.sourceViewer = sourceViewer;
		Arrays.sort(tagDocs, new ScriptDocCompletionProposalComparator());
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		// RD: this has changed now that we use one lexeme per doc
		// no arg assist without @ char on same line
		String source = context.getSource();
		int beginOffset = source.lastIndexOf('@', offset);
		int beginSpace = source.indexOf(' ', beginOffset);
		int cr = source.indexOf('\n', beginOffset);
		beginSpace = Math.min(beginSpace, cr);
		
		if(beginOffset == -1)
		{
			return null;
		}
		if(beginSpace != -1 && beginSpace < offset)
		{
			return null;
		}
		
		int lastNewline = source.lastIndexOf('\n', beginOffset);
		if(lastNewline > beginOffset)
		{
			return null;
		}

		String prefix = ""; //$NON-NLS-1$
		String text = ""; //$NON-NLS-1$
		int start = beginOffset + 1; 
		
		try {
			int length = offset - start;
			if(length < 0) 
			{
				prefix = ""; //$NON-NLS-1$
			}
			else {
				prefix = sourceViewer.getDocument().get(start, length).trim().toLowerCase();
				text = sourceViewer.getDocument().get(start, beginSpace - start).trim().toLowerCase();				
			}
		} catch (BadLocationException e) {
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.ScriptDocContentAssistProcessor_ErrorComputingCompletionProposals, e);
		}
		
		List<ScriptDocCompletionProposal> list = new ArrayList<ScriptDocCompletionProposal>();
		ScriptDocCompletionProposal defaultProp = null;
		for(int i=0;i<tagDocs.length;i++)
		{
			String name = tagDocs[i][0]; 
			String desc = "<b>@" + tagDocs[i][0] + "</b> <i>" + tagDocs[i][1] + "</i><p><p>" + tagDocs[i][2]; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			String insertedName = name;
			//if(name.length() > prefix.length())
			//	insertedName = name.substring(prefix.length());
				
			ScriptDocCompletionProposal sdoc = new ScriptDocCompletionProposal(
					insertedName, 
					start,
					text.length(), 
					insertedName.length(),
					fIcon,
					name,
					null,
					desc,
					0
					);
			list.add(sdoc);

			if(defaultProp == null && name.compareToIgnoreCase(prefix) >= 0)
			{
				defaultProp = sdoc;
			}
		}
		if(defaultProp != null)
		{
			defaultProp.setDefaultSelection(true);
		}

		if(list.size() == 0)
		{
			return null;
		}
		
		ScriptDocCompletionProposal[] sda = list.toArray(new ScriptDocCompletionProposal[list.size()]);
		return sda;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		 return new char[] { '@' };
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	/**
	 * Retrieves the image descriptor associated with resource from the image
	 * descriptor registry. If the image descriptor cannot be retrieved, attempt
	 * to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor associated with resource or the default
	 *         "missing" image descriptor if one could not be found
	 */
	private static ImageDescriptor getImageDescriptor(String imageFilePath)
	{
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin("com.aptana.ide.editors", imageFilePath); //$NON-NLS-1$
	
		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}
	
		return imageDescriptor;
	}
}
