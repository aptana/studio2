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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.editor.xml.XMLDocumentProvider.XMLFileInfo;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.preferences.IPreferenceConstants;
import com.aptana.ide.editors.toolbar.ToolbarWidget;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Robin Debreuil
 */
public class XMLEditor extends UnifiedEditor
{
	private boolean isDisposing = false;
	private boolean _isMarkingBothTags;
	/**
	 * XMLEditor
	 */
	public XMLEditor()
	{
		super();
		addPluginToPreferenceStore(XMLPlugin.getDefault());
		_isMarkingBothTags = getPreferenceStore().getBoolean(IPreferenceConstants.XMLEDITOR_HIGHLIGHT_START_END_TAGS);
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createLocalContributor()
	 */
	protected IUnifiedEditorContributor createLocalContributor()
	{
		return new XMLContributor();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getFileServiceFactory()
	 */
	public IFileServiceFactory getFileServiceFactory()
	{
		return XMLFileServiceFactory.getInstance();
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#getDefaultFileExtension()
	 */
	public String getDefaultFileExtension()
	{
		return "xml"; //$NON-NLS-1$
	}
	
	
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if(isDisposing)
		{
			return;		
		}
		
		isDisposing = true;
		
		super.dispose();
	}
	
	/**
	 * isNewInput
	 *
	 * @param input
	 * @return boolean
	 */
	protected boolean isNewInput( IEditorInput input )
	{
		XMLFileInfo cuInfo = getXMLFileInfo( input );
		if ( cuInfo == null )
		{
			throw new RuntimeException( Messages.XMLEditor_cuInfo_Null_At_DoSetInput );
		}
		XMLDocumentProvider dp = (XMLDocumentProvider) getDocumentProvider( );

		// Get document from input
		IDocument document = dp.getDocument( input );

		DocumentSourceProvider provider = new DocumentSourceProvider( document,
				input );

		if ( provider == null )
		{
			throw new RuntimeException( Messages.XMLEditor_Provider_Null );
		}

		return ( cuInfo.sourceProvider == null || cuInfo.sourceProvider.equals( provider ) == false );
	}

	private XMLFileInfo getXMLFileInfo( IEditorInput input )
	{
		XMLDocumentProvider dp = (XMLDocumentProvider) getDocumentProvider( );
		if ( dp == null )
		{
			throw new RuntimeException( Messages.XMLEditor_Document_Provider_Null );
		}
		return (XMLFileInfo) dp.getFileInfoPublic( input );
	}

	/**
	 * Updates the file information
	 * 
	 * @param input
	 * @param provider
	 * @param document
	 */
	protected void updateFileInfo( IEditorInput input,
			DocumentSourceProvider provider, IDocument document)
	{
		super.updateFileInfo( input, provider, document );
		if ( isNewInput(input))
		{
			// save reference to provider
			getXMLFileInfo( input ).sourceProvider = provider;
		}
	}
	
	private ToolbarWidget toolbar;
	private Composite displayArea;

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		boolean show = getPreferenceStore().getBoolean(IPreferenceConstants.SHOW_XML_TOOLBAR);
		if (show)
		{
			toolbar = new ToolbarWidget(new String[] { XMLMimeType.MimeType }, new String[] { "XML" }, //$NON-NLS-1$
					getPreferenceStore(), IPreferenceConstants.SHOW_XML_TOOLBAR, this);
			toolbar.createControl(displayArea);
		}
		Composite editorArea = new Composite(displayArea, SWT.NONE);
		editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout eaLayout = new GridLayout(1, true);
		eaLayout.marginHeight = 0;
		eaLayout.marginWidth = 0;
		editorArea.setLayout(new FillLayout());
		super.createPartControl(editorArea);
	}

	/**
	 * Can this lexeme be highlighted for matching occurrences ?
	 * 
	 * @param lexeme 
	 * @return true if this token type is eligible for occurrence marking
	 */
	public boolean canMarkOccurrences(Lexeme lexeme)
	{
		IToken token = lexeme.getToken();
		
		int typeIndex = token.getTypeIndex();
		
		if(typeIndex == XMLTokenTypes.WHITESPACE || typeIndex == XMLTokenTypes.COMMENT || typeIndex == XMLTokenTypes.TEXT )
		{
			return false;
		}
			
		return true;
	}
	
	/**
	 * Overridden parent method to handle highlighting of both start and end tag when either is selected
	 * 
	 * If/when we have common based class for tag based languages (html, xml etc.), this method should be moved there.
	 * 
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#markOccurences(com.aptana.ide.lexer.LexemeList, com.aptana.ide.lexer.Lexeme)
	 */
	protected void markOccurences(LexemeList lexemeList, Lexeme selectedLexeme)
	{
		boolean deferToParent = true;
		
		if(_isMarkingBothTags && ( selectedLexeme.typeIndex == XMLTokenTypes.START_TAG || selectedLexeme.typeIndex == XMLTokenTypes.END_TAG ) )
		{
			deferToParent = false;

			String selectedText = selectedLexeme.getText();
			String normalizedSelectedText = selectedText;
			
			// convert e.g. "</table" to <table"
			if(selectedLexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				if(selectedLexeme.length >=3)
				{
					normalizedSelectedText = selectedText.substring(0,1) + selectedText.substring(2);
				}
			}
				
			for (int i = 0; i < lexemeList.size(); i++)
			{
				Lexeme lexeme = lexemeList.get(i);
				if (lexeme != null)
				{
					if (lexeme.isHighlighted())
					{
						lexeme.setHighlighted(false);
					}

					if (lexeme.typeIndex == XMLTokenTypes.START_TAG || lexeme.typeIndex == XMLTokenTypes.END_TAG)
					{
						if( lexeme.typeIndex == selectedLexeme.typeIndex && lexeme.length == selectedLexeme.length)
						{
							if(selectedText.equals(lexeme.getText()))
							{
								lexeme.setHighlighted(true);
							}
						}
						else if( (lexeme.length - selectedLexeme.length) == 1)
						{
							if(lexeme.typeIndex == XMLTokenTypes.END_TAG)
							{
								if(lexeme.length >=3)
								{
									String normalizedText = lexeme.getText().substring(0,1) +  lexeme.getText().substring(2);
									if(normalizedText.equals(selectedText))
									{
										lexeme.setHighlighted(true);
									}
								}
							}
						}
						else if( (selectedLexeme.length - lexeme.length) == 1)
						{
							
							if(normalizedSelectedText.equals(lexeme.getText()))
							{
								lexeme.setHighlighted(true);
							}
						}

					}
					
				}
			}
		}
		
		if(deferToParent)
		{
			super.markOccurences(lexemeList, selectedLexeme);
		}
	}	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#createDocumentProvider()
	 */
	public IDocumentProvider createDocumentProvider()
	{
		return XMLDocumentProvider.getInstance();
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
	 */
	protected String[] collectContextMenuPreferencePages()
	{
		return new String[] {
				"com.aptana.ide.editor.xml.preferences.GeneralPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.ColorizationPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.FoldingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.ProblemsPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.FormattingPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.CodeAssistPreferencePage", //$NON-NLS-1$
				"com.aptana.ide.editor.xml.preferences.TypingPreferencePage", //$NON-NLS-1$
				"org.eclipse.ui.preferencePages.GeneralTextEditor", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Annotations", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.QuickDiff", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Accessibility", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.Spelling", //$NON-NLS-1$
				"org.eclipse.ui.editors.preferencePages.LinkedModePreferencePage", //$NON-NLS-1$
			};
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.UnifiedEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		String property = event.getProperty();

		if (IPreferenceConstants.XMLEDITOR_HIGHLIGHT_START_END_TAGS.equals(property))
		{
			_isMarkingBothTags = getPreferenceStore().getBoolean(IPreferenceConstants.XMLEDITOR_HIGHLIGHT_START_END_TAGS);
		}
		else
		{
			super.handlePreferenceStoreChanged(event);
		}

	}	
}
