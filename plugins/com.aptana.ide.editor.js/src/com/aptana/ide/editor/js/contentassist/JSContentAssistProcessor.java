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
package com.aptana.ide.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSOffsetMapper;
import com.aptana.ide.editor.js.environment.JSGuessedObject;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.scriptdoc.ScriptDocHelper;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IUnifiedViewer;
import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.parsing.IOffsetMapper;

/**
 * 
 */
public class JSContentAssistProcessor implements IContentAssistProcessor, IUnifiedContentAssistProcessor
{
	private IContextInformationValidator validator;
	boolean initalPopup = false;
	private static String[] keywords = new String[] { "break", "case", "catch", "continue", "default", "delete", "do", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			"else", "eval", "false", "field", "finally", "for", "function", "if", "in", "instanceof", "new", "null", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
			"return", "super", "switch", "this", "throw", "true", "try", "typeof", "var", "while", "with" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
	// private static String[] futureKeywords = new String[]{"abstract", "boolean", "byte", "char", "class", "const",
	// "debugger", "double", "enum", "export", "extends", "final", "float", "goto", "implements", "import", "int",
	// "interface", "long", "native", "package", "private", "protected", "public", "short", "static", "super",
	// "synchronized", "throws", "transient", "volatile" };
	// icons
	private static Image fIconField = UnifiedEditorsPlugin.getImage("icons/field_public.gif"); //$NON-NLS-1$
	private static Image fIconObject = UnifiedEditorsPlugin.getImage("icons/object.gif"); //$NON-NLS-1$
	private static Image fIconClass = UnifiedEditorsPlugin.getImage("icons/class.gif"); //$NON-NLS-1$
	private static Image fIconFunction = UnifiedEditorsPlugin.getImage("icons/function.gif"); //$NON-NLS-1$
	private static Image fIconFieldGuessed = UnifiedEditorsPlugin.getImage("icons/field_public_guess.gif"); //$NON-NLS-1$
	private static Image fIconObjectGuessed = UnifiedEditorsPlugin.getImage("icons/object_guess.gif"); //$NON-NLS-1$
	private static Image fIconClassGuessed = UnifiedEditorsPlugin.getImage("icons/class_guess.gif"); //$NON-NLS-1$
	private static Image fIconFunctionGuessed = UnifiedEditorsPlugin.getImage("icons/function_guess.gif"); //$NON-NLS-1$

	private static Image fIconError = UnifiedEditorsPlugin.getImage("icons/error.gif"); //$NON-NLS-1$
	private static Image fIconKeyword = UnifiedEditorsPlugin.getImage("icons/keyword.gif"); //$NON-NLS-1$
	private static Image fIconConstant = UnifiedEditorsPlugin.getImage("icons/constant.gif"); //$NON-NLS-1$
	// private Image fIconDefaultValue = getImageDescriptor("icons/defaultValue.gif").createImage();

	private JSCompletionProposalComparator contentAssistComparator;
	private int _offset;

	private static final char[] fContextChars = new char[] { '(', ',' };

	private EditorFileContext context;
	private boolean forceActivated = false;

	// { '(', '.', ' ', '\b' }; // combine fCompletionChars and fContextChars
	// manually for now

	/**
	 * Provides code assist information for javascript.
	 * 
	 * @param context
	 */
	public JSContentAssistProcessor(EditorFileContext context)
	{
		this.context = context;
		validator = new JSContextInformationValidator(this);
		contentAssistComparator = new JSCompletionProposalComparator();
	}

	private Environment getEnvironment()
	{
		return (Environment) JSLanguageEnvironment.getInstance().getRuntimeEnvironment();
	}

	/**
	 * @param viewer
	 * @param offset
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		return computeCompletionProposals(viewer, offset, UnifiedContentAssistProcessor.DEFAULT_CHARACTER);
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int, char)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar)
	{
		return computeCompletionProposals(viewer, offset, activationChar, false);
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int, char, boolean)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		char previousCharacter = getActivationChar(viewer, offset);

		IUnifiedViewer unifiedViewer = null;
		if (viewer instanceof IUnifiedViewer)
		{
			unifiedViewer = (IUnifiedViewer) viewer;
		}

		JSOffsetMapper mapper = (JSOffsetMapper) getOffsetMapper();
		// test for whitespace to left
		int lexemeOffset = mapper.getLexemeIndexFromDocumentOffset(offset);
		Lexeme curLexeme = null;
		if (lexemeOffset != -1)
		{
			curLexeme = mapper.getLexemeAtIndex(lexemeOffset);
		}
		else
		{
			curLexeme = mapper.getCurrentLexeme();
		}
		String fullName = ""; //$NON-NLS-1$
		if (curLexeme != null && curLexeme.getEndingOffset() >= offset)
		{
			// lookup current full name
			fullName = mapper.getNameHash(mapper.getLexemeList().getLexemeIndex(curLexeme));
		}

		// if(activationChar != UnifiedContentAssistProcessor.DEFAULT_CHARACTER && "".equals(fullName))
		// return null;

		// for now just ignore CA inside strings
		// todo: check if this is an invocation with default values, if so adjust lens etc to be correct
		if (fullName.equals(JSOffsetMapper.MODE_STRING))
		{
			return null;
		}

		if (fullName.endsWith("]") || fullName.endsWith(")")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return null;
		}

		// String argAssistFullName = mapper.getArgAssistNameHash();
		_offset = offset;

		// figure out where we want and dont want code assist to appear
		// first filter pass is only name based, we later can filter further
		// based on objects
		if (fullName.equals(JSOffsetMapper.NOT_AN_IDENTIFIER))
		{
			return null;
		}

		// test if we are in an invoke
		// boolean invoking = !argAssistFullName.equals(JSOffsetMapper.NOT_INVOKING);

		/*
		 * if ((activationChar == '\0') && invoking) { // show arg assist instead if (viewer instanceof SourceViewer) {
		 * ((SourceViewer) viewer).doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION); // If we don't want to
		 * allow both code assist and arg assist at once, return here. // return null; } }
		 */

		// test conditions around space activation for 'new'
		boolean isConstructor = fullName.startsWith(JSOffsetMapper.MODE_NEW);
		if (isConstructor)
		{
			fullName = fullName.substring(JSOffsetMapper.MODE_NEW.length());
		}
		if (previousCharacter == ' ' && !isConstructor)
		{
			return null;
		}
		// abort if current character isn't space when in 'new' mode
		// if( isConstructor && jsFileEnvironment.getSource().charAt(offset - 1) != ' ' )
		// return null;

		// now we can continue with regular programming

		// lookup types of object (function calls must be based on return types
		// from docs)
		// until getting a type to find properties from
		IObject obj;
		IScope scope = mapper.getGlobal();

		// LexemeList ll =getLanguageEnvironment().getLexemeList();
		// int lxIndex = ll.getLexemeFloorIndex(offset);
		// if(lxIndex == -1)
		// lxIndex = ll.getLexemeCeilingIndex(offset);
		// Lexeme lex = ll.get(lxIndex);

		lexemeOffset = this.getJSOffsetMapper().getLexemeIndexFromDocumentOffset(offset);
		Lexeme lex = null;
		if (lexemeOffset != -1)
		{
			lex = mapper.getLexemeAtIndex(lexemeOffset);
		}
		else
		{
			lex = mapper.getCurrentLexeme();
		}
		if (lex != null)
		{
			if (lex.getCategoryIndex() == TokenCategories.WHITESPACE)
			{
				int prevLexeme = mapper.getCurrentLexemeIndex() - 1;
				// JSLexeme[] lexemes = jsFileEnvironment.getLexemes();
				LexemeList lexemeList = mapper.getLexemeList();
				while (prevLexeme > 0 && prevLexeme < lexemeList.size())
				{
					lex = lexemeList.get(prevLexeme);
					if (lex.getCategoryIndex() != TokenCategories.WHITESPACE)
					{
						break;
					}
					prevLexeme++;
				}
			}
			synchronized (mapper)
			{
				scope = mapper.getScope(lex, scope);
			}
		}

		// calculate our prefix and base objects
		String prefix = ""; //$NON-NLS-1$
		if (!fullName.equals("") && fullName.indexOf(".") > -1) //$NON-NLS-1$ //$NON-NLS-2$
		{
			// if there are letters after the last dot, that becomes our prefix
			// that filters competion options.
			String lookupName = fullName;
			if (fullName.indexOf(".") > -1) //$NON-NLS-1$
			{
				lookupName = fullName.substring(0, fullName.lastIndexOf(".") + 1); //$NON-NLS-1$
			}

			// find the final return type
			// there is a special ca.se here for things like x = new Date().now
			// in this case we must create the type new Date(), and look up 'now' on that
			obj = mapper.lookupReturnTypeFromNameHash(lookupName, scope);
			if (obj == null)
			{
				return null;
			}

			// set our prefix string to filter with
			prefix = fullName.substring(fullName.lastIndexOf('.') + 1).toUpperCase();
		}
		else
		{
			obj = scope; // jsFileEnvironment.getGlobal();
			prefix = fullName.toUpperCase();
		}
		if (prefix.indexOf('(') > -1)
		{
			prefix = ""; //$NON-NLS-1$
		}

		ICompletionProposal[] result = null;

		if (curLexeme != null && curLexeme.typeIndex == JSTokenTypes.STRING)
		{
			result = getValidCompletionProposals(obj, curLexeme.getText(), curLexeme.getText(), isConstructor, offset);
		}
		else
		{
			// calculate all the completion proposals
			result = getValidCompletionProposals(obj, fullName, prefix, isConstructor, offset);
		}

		UnifiedContentAssistProcessor.resetViewerState(unifiedViewer);

		// we don't need completion proposals when backspacing into a valid object that is
		// the only valid one, and it is completly typed.
		// However if we hit ctrlspace in a valid ident, it is nice to get all
		// the replacement proposals to check things
		if (result.length == 1 && prefix.equals(result[0].getDisplayString().toUpperCase()))
		{
			return getValidCompletionProposals(obj, fullName, "", isConstructor, prefix, offset); //$NON-NLS-1$
		}
		else
		{
			return result;
		}
	}

	/**
	 * These are default values that appear in code assist depending on the function inserted.
	 * 
	 * @param viewer
	 * @param offset
	 */
	private void addDefaultValues(String prefix, Hashtable completionProposals, int beginOffset)
	{
		initalPopup = true;

		// lookup the current full name
		String fullName = getFullName();

		// ...and find the appropriate object based on the return types of the
		// objects in the sequence
		IScope scope = getScope();

		IObject obj = getObject(fullName, scope);

		if (obj == null)
		{
			return;
		}

		IDocumentation doc = obj.getDocumentation();
		if (doc instanceof FunctionDocumentation)
		{
			FunctionDocumentation fDoc = (FunctionDocumentation) doc;
			TypedDescription[] params = fDoc.getParams();
			if (params.length == 1)
			{
				TypedDescription[] defaultValues = params[0].getDefaultValues();
				for (int i = 0; i < defaultValues.length; i++)
				{
					TypedDescription d = defaultValues[i];
					String name = "\"" + d.getName() + "\""; //$NON-NLS-1$ //$NON-NLS-2$

					Image defaultImage = fIconConstant;
					String location = "String Param"; //$NON-NLS-1$

					Image[] images = UnifiedContentAssistProcessor.getUserAgentImages(UnifiedContentAssistProcessor
							.getUserAgents(), fDoc.getUserAgentPlatformNames());
					JSCompletionProposal cp = new JSCompletionProposal(name, beginOffset, prefix.length(), name
							.length(), defaultImage, name, null, d.getDescription(),
							JSCompletionProposalComparator.OBJECT_TYPE_GLOBAL_OBJECT, location, images);

					completionProposals.put(name, cp);
				}
			}
		}
	}

	/**
	 * @param fullName
	 * @param scope
	 * @return IObject
	 */
	private IObject getObject(String fullName, IScope scope)
	{

		IObject obj = getJSOffsetMapper().lookupReturnTypeFromNameHash(fullName, scope);
		return obj;

	}

	/**
	 * @return IScope
	 */
	private IScope getScope()
	{
		JSOffsetMapper mapper = getJSOffsetMapper();
		Lexeme curLex = mapper.getCurrentLexeme();
		IScope scope = null;

		if (curLex != null)
		{
			scope = mapper.getScope(curLex, mapper.getGlobal());
		}

		// temp guard against ws nodes
		if (scope == null)
		{
			scope = getJSOffsetMapper().getGlobal();
		}
		return scope;
	}

	/**
	 * @return String
	 */
	private String getFullName()
	{
		String fullName = getJSOffsetMapper().getArgAssistNameHash();

		if (fullName.indexOf('(') > -1)
		{
			fullName = fullName.substring(0, fullName.lastIndexOf('('));
		}
		return fullName;
	}

	/**
	 * Determines what 'tooltip' style highlighting to show. This will be argument insight for methods. This is
	 * formatted in JSContextInformationValidator (of all places) by implementing the optional
	 * IContextInformationPresenter interface.
	 * 
	 * @param viewer
	 * @param offset
	 * @return Returns an array of relevant context info.
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		initalPopup = true;

		// lookup the current full name
		String fullName = getFullName();

		// ...and find the appropriate object based on the return types of the
		// objects in the sequence
		IScope scope = getScope();

		IObject obj = getObject(fullName, scope);

		IContextInformation[] ici = null;
		ContextInformation ci = computeArgContextInformation(obj, fullName);
		if (ci != null)
		{
			ici = new IContextInformation[] { ci };
		}
		return ici;
	}

	/**
	 * The characters that triggers competion proposals (a combination of all activation chars)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAllActivationCharacters()
	{
		char[] allActivationChars = UnifiedContentAssistProcessor.combine(
				getCompletionProposalAutoActivationCharacters(), getContextInformationAutoActivationCharacters());
		Arrays.sort(allActivationChars);
		return allActivationChars;
	}

	/**
	 * The characters that triggers competion proposals (dot for completion, and space for "new XX" in our case)
	 * 
	 * @return Returns the trigger characters for code completion.
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '.', ' ' };
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		return validator;
	}

	/**
	 * Characters that trigger tooltip popup help
	 * 
	 * @return Returns the trigger characters for auto activation.
	 */
	public char[] getContextInformationAutoActivationCharacters()
	{
		// Make context popup automatically after the following characters
		return fContextChars;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#getCompletionProposalIdleActivationTokens()
	 */
	public int[] getCompletionProposalIdleActivationTokens()
	{
		return new int[] { JSTokenTypes.IDENTIFIER };
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return null;
	}

	/**
	 * Computes argument insight text based on the passed hash name to lookup.
	 * 
	 * @param fullName
	 *            The hash name of the method to lookup
	 * @param identifier
	 *            The "name" of the item
	 * @return A valid ContextInformation object or null.
	 */
	private ContextInformation computeArgContextInformation(IObject obj, String identifier)
	{
		ContextInformation result = null;
		StringBuffer docText = new StringBuffer();

		// if the object exists, find its documentation
		if (obj != null)
		{
			// int argIndex = getJSOffsetMapper().getArgIndexAndCalculateMode();

			IDocumentation idoc = obj.getDocumentation();
			if (idoc != null)
			{
				// look for params and create a string, if it is a method
				if (idoc instanceof FunctionDocumentation)
				{
					FunctionDocumentation md = (FunctionDocumentation) idoc;
					TypedDescription[] params = md.getParams();

					docText.append(ScriptDocHelper.createMethodSignatureString(identifier, md, obj, false));

					if (params.length > 0)
					{
						String paramText = ScriptDocHelper.createParameterDocumentationList(params, obj);
						if (paramText != null && !"".equals(paramText)) //$NON-NLS-1$
						{
							docText.append(ScriptDocHelper.DEFAULT_DELIMITER);
							docText.append(paramText);
						}
					}
				}
				// otherwise just add the description (properties, classes,
				// exceptions)
				else if (idoc instanceof PropertyDocumentation)
				{
					PropertyDocumentation pDoc = (PropertyDocumentation) idoc;
					docText.append(ScriptDocHelper.createPropertyDocumentationHTML(identifier, pDoc, false));
				}
			}
			else if (obj instanceof IFunction)
			{
				// no docs, just pass arg info if this is a function
				String[] params = ((IFunction) obj).getParameterNames();

				if (params != null && params.length > 0)
				{
					docText.append("("); //$NON-NLS-1$
					String comma = ""; //$NON-NLS-1$
					for (int i = 0; i < params.length; i++)
					{
						docText.append(comma + params[i]);
						comma = ", "; //$NON-NLS-1$
					}
					docText.append(")"); //$NON-NLS-1$
				}

			}
			String additionalInfo = ((docText.length() == 0) && (obj instanceof IFunction)) ? Messages.JSContentAssistProcessor_NoArgs
					: docText.toString();
			if (additionalInfo.trim().length() > 0)
			{
				result = new ContextInformation("argInfo", "" + additionalInfo); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return result;
	}

	private char getActivationChar(ITextViewer viewer, int offset)
	{
		if (JSContentAssistant.isHotkeyActivated())
		{
			JSContentAssistant.setHotkeyActivated(false);
			return '\0';
		}

		String source = viewer.getDocument().get();
		if (offset == 0)
		{
			return '\0';
		}

		char activationCharacter = source.charAt(offset - 1);
		// this is a special case as we are using backspace as an activation char
		// this can't come from the document
		// possibly we can store the deletion length from
		// JSPartitionScanner.documentChanged2
		if (Arrays.binarySearch(getCompletionProposalAllActivationCharacters(), activationCharacter) < 0)
		{
			activationCharacter = UnifiedContentAssistProcessor.DEFAULT_CHARACTER;
		}
		return activationCharacter;
	}

	/**
	 * Returns a set of valid completion proposals for a given object and prefix.
	 * 
	 * @param obj
	 *            The object to look for proposals
	 * @param prefix
	 *            The valid starting letters to complete with.
	 * @param isConstructor
	 *            True if constructors are valid (like when using 'new').
	 * @return Returns an array of (JS) completion proposals.
	 */
	private ICompletionProposal[] getValidCompletionProposals(IObject obj, String fullName, String prefix,
			boolean isConstructor, int docOffset)
	{
		return getValidCompletionProposals(obj, fullName, prefix, isConstructor, "", docOffset); //$NON-NLS-1$
	}

	/**
	 * Returns a set of valid completion proposals for a given object and prefix.
	 * 
	 * @param baseObject
	 *            The object to look for proposals
	 * @param fullName
	 *            The hashName of the object.
	 * @param prefix
	 *            The valid starting letters to complete with.
	 * @param isConstructor
	 *            True if constructors are valid (like when using 'new').
	 * @param the
	 *            replace string, this gives us full control over what will be replaced in the case where we aren't
	 *            matching by ident prefix.
	 * @return Returns an array of (JS) completion proposals.
	 */
	private ICompletionProposal[] getValidCompletionProposals(IObject baseObject, String fullName, String prefix,
			boolean isConstructor, String replaceString, int docOffset)
	{
		if (baseObject == null)
		{
			return new ICompletionProposal[0];
		}

		boolean isNamedGlobal = baseObject.equals(getJSOffsetMapper().getGlobal()) && fullName.indexOf('.') != -1;

		int fileIndex = this.getJSOffsetMapper().getFileIndex();

		int offset = 0;
		int lexemeOffset = this.getJSOffsetMapper().getLexemeIndexFromDocumentOffset(docOffset);
		Lexeme curLexeme = null;
		if (lexemeOffset != -1)
		{
			curLexeme = this.getJSOffsetMapper().getLexemeAtIndex(lexemeOffset);
		}
		else
		{
			curLexeme = this.getJSOffsetMapper().getCurrentLexeme();
		}
		if (curLexeme != null)
		{
			offset = curLexeme.offset;
		}

		// boolean isPrefixReplace = false;

		if (replaceString.equals("")) //$NON-NLS-1$
		{
			replaceString = prefix;
			// isPrefixReplace = true;
		}
		int lastDot = fullName.lastIndexOf('.');
		boolean isRootLevelCall = (lastDot > -1) ? false : true;

		// get all members and then filter
		Hashtable completionProposals = new Hashtable();

		int beginOffset = _offset;
		if (!prefix.equals("")) //$NON-NLS-1$
		{
			boolean isText = (curLexeme.getCategoryIndex() == TokenCategories.IDENTIFIER || curLexeme
					.getCategoryIndex() == TokenCategories.KEYWORD);
			beginOffset = isText ? curLexeme.offset : curLexeme.getEndingOffset() - prefix.length();
		}

		// add all keywords
		if (isRootLevelCall)
		{
			addKeywords(prefix, completionProposals, curLexeme);
			addDefaultValues(prefix, completionProposals, beginOffset);
		}

		// boolean isInFunction = (baseObject instanceof IScope) && !(baseObject == environment.getGlobal());

		// compute properties - these are looked up from the proto chain
		String[] props = getAllPropertyNamesInScope(baseObject, true);
		String originalCasePrefix = isRootLevelCall ? fullName : fullName.substring(lastDot + 1, fullName.length());

		for (int i = 0; i < props.length; i++)
		{
			String propName = props[i];
			String upperPropName = propName.toUpperCase();

			if (completionProposals.containsKey(propName) == false || isRootLevelCall == false)
			{
				// note: it seems get instance is not drilling down to the
				// actual instance but rather the commandNode
				// eg: f=function(){}; v=f; => v is an IdentifierNode and
				// getinstance gets a FunctionNode rather than a JSFunction
				IObject instance = baseObject.getInstance(getEnvironment(), fileIndex, offset);

				Property p = JSOffsetMapper.getPropertyInScope(instance, propName);

				if (p == null)
				{
					continue;
				}
				int sourceFileIndex = p.getSourceFileIndex(fileIndex, beginOffset);
				// int sourceFileIndex = p.getSourceFileIndex(FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
				// //fileIndex, beginOffset);

				if (sourceFileIndex == -1)
				{
					sourceFileIndex = p.getSourceFileIndex(FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
				}

				// TODO: if we can't find it, we shouldn't set it to the current file
				// if(sourceFileIndex == -1)
				// sourceFileIndex = fileIndex; // current file

				IObject propObject = p.getValue(fileIndex, offset).getInstance(getEnvironment(), fileIndex, offset);
				// TODO: work this out post beta, the offset here needs to be from the org prop file
				if (propObject == JSUndefined.getSingletonInstance())
				{
					propObject = p.getValue(FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE).getInstance(
							getEnvironment(), FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
				}

				// filter out refs to just typed chars
				int asgnCount = p.getAssignments().size();
				boolean wasJustTyped = prefix.equals(upperPropName) && asgnCount <= 1;
				if (wasJustTyped && asgnCount == 1)
				{
					if (p.getAssignment(0).getStartingOffset() != offset)
					{
						wasJustTyped = false;
					}
				}
				if (!wasJustTyped)
				{
					JSCompletionProposal jscp = computeProposal(p, instance, propObject, propName, prefix,
							isConstructor, replaceString, isNamedGlobal, beginOffset, sourceFileIndex);
					if (jscp != null)
					{
						completionProposals.put(propName, jscp);

						// if (!isPrefixReplace && upperPropName.equals(replaceString))
						// defaultProposal = jscp;
					}
				}
			}
		}
		// testing
		// Template t = new Template("functionTemplate", " create a function", "all", "function", true);
		// TemplateContextType tct = new TemplateContextType("xxx", "myName");
		// UnifiedTemplateContext utc = new UnifiedTemplateContext(tct);
		// Region r = new Region(offset, fullName.length());
		// TemplateProposal tp = new TemplateProposal(t, utc, r, fIconField);
		// completionProposals.put("template1", tp);

		// compute variables - these are looked up from the scope chain
		if ((isRootLevelCall && baseObject instanceof IScope))
		{
			IScope scope = (IScope) baseObject;
			String[] vars;
			if (isRootLevelCall)
			{
				vars = scope.getVariableNames();
			}
			else
			{
				vars = scope.getLocalVariableNames();
			}
			for (int i = 0; i < vars.length; i++)
			{
				String varName = vars[i];
				if (!completionProposals.containsKey(varName))
				{
					Property p = scope.getVariable(varName);
					if (p == null)
					{
						continue;
					}
					int sourceFileIndex = p.getSourceFileIndex(fileIndex, beginOffset);
					if (sourceFileIndex == -1) // cur file or not found
					{
						sourceFileIndex = fileIndex;
					}

					IObject varObject = p.getValue(fileIndex, offset).getInstance(getEnvironment(), fileIndex, offset);

					if (!(prefix.equals(varName.toUpperCase()) && p.getReferenceCount() < 2))
					{
						JSCompletionProposal jscp = computeProposal(p, baseObject, varObject, varName, prefix,
								isConstructor, replaceString, isNamedGlobal, beginOffset, sourceFileIndex);
						if (jscp != null)
						{
							completionProposals.put(varName, jscp);

							// if (!isPrefixReplace && varName.toUpperCase().equals(replaceString))
							// defaultProposal = jscp;
						}
					}
				}
			}
		}
		ICompletionProposal[] result = (ICompletionProposal[]) completionProposals.values().toArray(
				new ICompletionProposal[completionProposals.size()]);
		Arrays.sort(result, contentAssistComparator);

		if (originalCasePrefix.length() > 0 || fullName.endsWith(".")) //$NON-NLS-1$
		{
			if (originalCasePrefix.equals("f")) //$NON-NLS-1$
			{
				originalCasePrefix = "function"; //$NON-NLS-1$
			}
			else if (originalCasePrefix.equals("d")) //$NON-NLS-1$
			{
				originalCasePrefix = "document"; //$NON-NLS-1$
			}
			else if (originalCasePrefix.equals("v")) //$NON-NLS-1$
			{
				originalCasePrefix = "var"; //$NON-NLS-1$
			}

			UnifiedContentAssistProcessor.setSelection(originalCasePrefix, result);
		}

		return result;
	}

	/**
	 * @param prefix
	 * @param completionProposals
	 */
	private void addKeywords(String prefix, Hashtable completionProposals, Lexeme curLexeme)
	{
		boolean isIdent = false;
		int beginOffset = 0;
		if (curLexeme != null)
		{
			isIdent = (curLexeme.typeIndex == JSTokenTypes.IDENTIFIER);
			beginOffset = isIdent ? curLexeme.offset : curLexeme.getEndingOffset();
		}
		for (int i = 0; i < keywords.length; i++)
		{
			String keyName = keywords[i];
			if (!completionProposals.containsKey(keyName))
			{
				int finOffset = beginOffset;
				if (curLexeme != null && !isIdent && keyName.equals(prefix.toLowerCase()))
				{
					finOffset = curLexeme.offset;
				}

				String location = "Keyword"; //$NON-NLS-1$
				JSCompletionProposal cp = new JSCompletionProposal(keyName, finOffset, prefix.length(), keyName
						.length(), fIconKeyword, keyName, new ContextInformation(
						"keywordInfo", Messages.JSContentAssistProcessor_The + keyName //$NON-NLS-1$
								+ Messages.JSContentAssistProcessor_Keyword), Messages.JSContentAssistProcessor_The2
						+ keyName + Messages.JSContentAssistProcessor_Keyword2,
						JSCompletionProposalComparator.OBJECT_TYPE_GLOBAL_OBJECT, location,
						UnifiedContentAssistProcessor.getAllUserAgentImages(UnifiedContentAssistProcessor
								.getUserAgents()));

				completionProposals.put(keyName, cp);
			}
		}

	}

	/**
	 * private void setDefaultProposal(ICompletionProposal[] proposals, String prefix) { if (proposals.length == 0) {
	 * return; } // int x = Arrays.binarySearch(proposals, prefix); // if(x < 0) // x = -x - 1; // if(x ==
	 * proposals.length) // x = proposals.length - 1; // proposals[x].setDefaultSelection(true); // return; if (prefix ==
	 * null || prefix.equals("") ) //$NON-NLS-1$ { for (int i = 0; i < proposals.length; i++) { ICompletionProposal
	 * proposal = proposals[i]; if(proposal instanceof JSCompletionProposal) {
	 * ((JSCompletionProposal)proposal).setDefaultSelection(true); break; } } return; } int defaultIndex = 0; int
	 * defaultIndexNoCase = 0; String pUpper = prefix.toUpperCase(); int prefixIndex = 0; int prefixLen =
	 * prefix.length(); char prefixChar = prefix.charAt(prefixIndex); char prefixUpperChar = pUpper.charAt(prefixIndex);
	 * char nameChar = '\0'; char nameUpperChar = '\0'; // special cases until we get a db for selection history
	 * if(prefix.toLowerCase().equals("f")) //$NON-NLS-1$ { defaultIndex = getProposal(proposals, "function");
	 * //$NON-NLS-1$ } else if(prefix.toLowerCase().equals("d")) //$NON-NLS-1$ { defaultIndex = getProposal(proposals,
	 * "document"); //$NON-NLS-1$ } else if(prefix.toLowerCase().equals("v")) //$NON-NLS-1$ { defaultIndex =
	 * getProposal(proposals, "var"); //$NON-NLS-1$ } else { for (int i = 0; i < proposals.length; i++) { if(!
	 * (proposals[i] instanceof JSCompletionProposal)) { continue; } String name = proposals[i].getDisplayString();
	 * String nameUpper = name.toUpperCase(); if (name.length() > prefixIndex) { nameChar = name.charAt(prefixIndex);
	 * nameUpperChar = nameUpper.charAt(prefixIndex); if (nameUpperChar == prefixUpperChar) { defaultIndex = i; if
	 * (nameChar == prefixChar) { defaultIndexNoCase = i; } if (prefixIndex < prefixLen - 1) { i--; prefixIndex++;
	 * prefixChar = prefix.charAt(prefixIndex); prefixUpperChar = pUpper.charAt(prefixIndex); continue; } else { while
	 * (i < proposals.length) { name = proposals[i].getDisplayString(); if (name.startsWith(prefix)) {
	 * defaultIndexNoCase = i; break; } if (name.toUpperCase().charAt(0) > pUpper.charAt(0)) { break; } i++; } break; } }
	 * else if (nameUpperChar < prefixUpperChar || nameUpperChar == '_') { if (prefixIndex > 0 &&
	 * !nameUpper.startsWith(pUpper.substring(0, prefixIndex))) { break; } defaultIndex = i; } else if (nameUpperChar >
	 * prefixUpperChar) { break; } } } } if (defaultIndexNoCase > defaultIndex) { defaultIndex = defaultIndexNoCase; } //
	 * special case prototype, just for me : ) String curName = proposals[defaultIndex].getDisplayString(); if
	 * (proposals.length > defaultIndex && pUpper.length() < 4 && curName.startsWith("propertyIsEnumerable")
	 * //$NON-NLS-1$ && proposals[defaultIndex + 1].getDisplayString().startsWith("prototype")) //$NON-NLS-1$ {
	 * defaultIndex++; } // [RD] fix - can't assume all proposals will be JSCompletionProposals
	 * if(proposals[defaultIndex] instanceof JSCompletionProposal) {
	 * ((JSCompletionProposal)proposals[defaultIndex]).setDefaultSelection(true); } // JSCompletionProposal dflt = null; //
	 * JSCompletionProposal dfltNoCase = null; // boolean noCaseMatch = true; // // for (int i = 0; i <
	 * proposals.length; i++) // { // JSCompletionProposal proposal = proposals[i]; // String propName =
	 * proposal.getDisplayString(); // // // may want to use Collator here for è etc //
	 * if(propName.compareToIgnoreCase(prefix) >= 0) // { // // check for at least first letter matches in this special
	 * case // if(noCaseMatch && dfltNoCase != null) // { // char curc = propName.toUpperCase().charAt(0); // char dfltc =
	 * dfltNoCase.getDisplayString().toUpperCase().charAt(0); // char prefixc = prefix.toUpperCase().charAt(0); //
	 * if(dfltc != prefixc && curc == prefixc) // { // dfltNoCase = proposal; // } // } // // noCaseMatch = false; // //
	 * if(dfltNoCase == null) // // dfltNoCase = proposal; // if(proposal.getDisplayString().compareTo(prefix) >= 0) // { //
	 * dflt = proposal; // break; // } // } // if(noCaseMatch) // dfltNoCase = proposal; // } // // make sure there is
	 * at least a default // if(dflt == null) // { // dflt = dfltNoCase; // if(dflt == null) // dflt = proposals[0]; // } // //
	 * now need to pick a winner // String prefixUpper = prefix.toUpperCase(); // String dfltUpper =
	 * dflt.getDisplayString().toUpperCase(); // if(dfltNoCase != null && !dfltUpper.startsWith(prefixUpper)) // { //
	 * String dfltNoCaseUpper = dfltNoCase.getDisplayString().toUpperCase(); // char[] prefixChars =
	 * prefixUpper.toCharArray(); // char[] dfltChars = dfltUpper.toCharArray(); // char[] dfltNoCaseChars =
	 * dfltNoCaseUpper.toCharArray(); // for(int i = 0; i < prefixChars.length; i++) // { // if(prefixChars[i] !=
	 * dfltNoCaseChars[i]) // { // break; // }else if(prefixChars[i] != dfltChars[i]) // { // dflt = dfltNoCase; //
	 * break; // } // } // } // dflt.setDefaultSelection(true); }
	 */

	// /**
	// * @param proposals
	// * @param string
	// * @return index of proposal
	// */
	// private int getProposal(ICompletionProposal[] proposals, String matchName)
	// {
	// int result = 0;
	// for (int i = 0; i < proposals.length; i++)
	// {
	// if(! (proposals[i] instanceof JSCompletionProposal))
	// {
	// continue;
	// }
	// String name = proposals[i].getDisplayString();
	// if(name.equals(matchName))
	// {
	// result = i;
	// break;
	// }
	// }
	// return result;
	// }
	/**
	 * Filters most invalid proposals
	 * 
	 * @param propertyObject
	 *            The object to test
	 * @param propertyName
	 *            The name of the object to test.
	 * @param prefix
	 *            The valid prefix for testing
	 * @return Returns true if this is a valid proposal.
	 */
	private boolean isValidProposal(IObject baseObject, IObject propertyObject, String propertyName, String prefix)
	{

		if (baseObject == null || propertyName.equals("")) //$NON-NLS-1$
		{
			return false;
		}

		if (propertyName.indexOf("[") > -1) //$NON-NLS-1$
		{
			return false;
		}

		if (propertyObject == ObjectBase.UNDEFINED || propertyObject == null)
		{
			// only abort if there is no actual property (the value may be undefined if the return type is unknown)
			// note this is a short term fix. Ultimately we need a way to tell the difference between things
			// that have return values of undefined, and things that haven't been defined (yet),
			// and things like params, who's type is undefined. This also turns of position info which needs to be
			// revisited.

			// TODO: decide proper approach for this after the Alpha (do we want to show undefined properties?)
			// In the alpha, we now force the actively edited file to have a higher file index than all other files.
			// if(baseObject.getProperty(propertyName) == null)
			return false;
		}
		// only take members that start with the current prefix
		// remove duplicates - we can do it this way as the array is sorted
		// if (propertyName.toUpperCase().startsWith(prefix) == false)
		// {
		// return false;
		// }
		// we can filter these later if we like, however it is useful to have them for debugging atm.
		// (note: technically it is correct to show them, but may seem noisy)
		// if (propertyName.equals("constructor"))
		// {
		// return false;
		// }

		Property prop = JSOffsetMapper.getPropertyInScope(baseObject, propertyName);

		if ((prop == null) && (baseObject instanceof IScope))
		{
			prop = ((IScope) baseObject).getVariable(propertyName);
		}
		// shouldn't happen
		if (prop == null)
		{
			return false;
		}
		if (prop.isVisible() == false)
		{
			return false;
		}
		// invocation properties like arguments and callee
		IDocumentation docs = propertyObject.getDocumentation();

		if (docs != null && docs instanceof PropertyDocumentation)
		{
			PropertyDocumentation pdocs = (PropertyDocumentation) docs;
			if (pdocs.getIsInvocationOnly())
			{
				if (!(baseObject instanceof JSScope))
				{
					return false;
				}
			}
			else if (pdocs.getIsInternal())
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Computes a single propsal if valid, otherwise returns null.
	 * 
	 * @param obj
	 *            The object to look for proposals
	 * @param propertyName
	 *            The hashName of the object.
	 * @param prefix
	 *            The valid starting letters to complete with.
	 * @param isConstructor
	 *            True if constructors are valid (like when using 'new').
	 * @param the
	 *            replace string, this gives us full control over what will be replaced in the case where we aren't
	 *            matching by ident prefix.
	 * @return Returns a single proposal if valid, otherwise returns null
	 */
	// CHECKSTYLE:OFF
	private JSCompletionProposal computeProposal(Property p, IObject baseObject, IObject propertyObject,
			String propertyName, String prefix, boolean isConstructor, String replaceString, boolean isNamedGlobal,
			int beginOffset, int sourceFileIndex)
	// CHECKSTYLE:ON
	{
		if (!isValidProposal(baseObject, propertyObject, propertyName, prefix))
		{
			return null;
		}

		ObjectBase global = getJSOffsetMapper().getGlobal();
		JSCompletionProposal cp = null;
		int replaceLength = replaceString.length();

		String replacementText = propertyName;

		Image icon = fIconError;
		StringBuffer docText = new StringBuffer();
		int sortingType = JSCompletionProposalComparator.OBJECT_TYPE_OTHER;
		String additionalText = ""; //$NON-NLS-1$
		int extraInsert = 0;
		ContextInformation ci = null;

		int fileIndex = this.getJSOffsetMapper().getFileIndex();
		int offset = 0;
		Lexeme curLexeme = this.getOffsetMapper().getCurrentLexeme();
		if (curLexeme != null)
		{
			offset = curLexeme.offset;
		}
		IObject finalObject = propertyObject.getInstance(getEnvironment(), fileIndex, offset);
		// guard for objects in error state
		if (finalObject == null)
		{
			return cp;
		}

		boolean isGuessedObject = false;

		IDocumentation doc = finalObject.getDocumentation();
		// handle possible reassignments (the docs would have been hidden in this case)
		if (doc == null)
		{
			doc = p.getAnyValidDocumentation(sourceFileIndex, beginOffset);
		}
		if (finalObject instanceof JSGuessedObject && doc == null)
		{
			isGuessedObject = true;
		}

		Image[] images = null;

		// Note: taking this out, because it doesn't allow namespaced CTORs like 'new YAHOO.widget();'
		// // "new XX" only uses constructors - for now things are only treated as
		// // ctors if documented as such
		// if (isConstructor && !(finalObject instanceof IFunction))//(doc instanceof FunctionDocumentation) )
		// {
		// return null;
		// }
		if (doc != null)
		{
			// todo: use the type desc here, and make all docs just "Documentation"
			if (doc instanceof PropertyDocumentation && !(doc instanceof FunctionDocumentation))
			{
				PropertyDocumentation pDoc = (PropertyDocumentation) doc;
				if (pDoc.getUserAgentPlatformNames().length > 0)
				{
					images = UnifiedContentAssistProcessor.getUserAgentImages(UnifiedContentAssistProcessor
							.getUserAgents(), pDoc.getUserAgentPlatformNames());
				}

				// turn off 'window' (or _root for example) from seeing internal classes (but not instnaces)
				// boolean isMember = pDoc.getMemberOf().getTypes().length == 0;
				if (isNamedGlobal && pDoc.getIsInternal())
				{
					return null;
				}

				docText.append(ScriptDocHelper.createPropertyDocumentationHTML(propertyName, pDoc, true));

				if (baseObject == global)
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_GLOBAL_OBJECT;
					if (isGuessedObject)
					{
						icon = fIconObjectGuessed;
					}
					else
					{
						icon = fIconObject;
					}
				}
				else
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_PROPERTY;
					if (isGuessedObject)
					{
						icon = fIconFieldGuessed;
					}
					else
					{
						icon = fIconField;
					}
				}

			}
			else if (doc instanceof FunctionDocumentation)
			{
				// taking these out until they can talk better to the bracket inserter logic
				// if(isConstructor || finalObject.getPropertyNames().length == 0)
				// additionalText = "()";
				// extraInsert = 1;

				// Don't need arg assist if we are not inserting the "()" parens
				// ci = computeArgContextInformation(finalObject, propertyName);
				FunctionDocumentation fDoc = (FunctionDocumentation) doc;
				if (fDoc.getUserAgentPlatformNames().length > 0)
				{
					images = UnifiedContentAssistProcessor.getUserAgentImages(UnifiedContentAssistProcessor
							.getUserAgents(), fDoc.getUserAgentPlatformNames());
				}

				// turn off 'window' (or _root for example) from seeing internal properties
				// boolean isMember = fDoc.getMemberOf().getTypes().length == 0;
				if (isNamedGlobal && fDoc.getIsInternal() && fDoc.getIsConstructor())
				{
					return null;
				}

				docText.append(ScriptDocHelper.createMethodDocumentationHTML(propertyName, fDoc, finalObject));

				boolean isCtor = fDoc.getIsConstructor();
				boolean isMethod = fDoc.getIsMethod();
				// if they haven't set it specifically, guess if it is a class by checking the prototype
				if (!isCtor && !isMethod)
				{
					// find out if there are properties on the prototype
					if (hasPropertiesOnPrototype(finalObject, fileIndex, offset))
					{
						isCtor = true;
					}
				}

				if (isCtor)
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_CLASS;
					if (isGuessedObject)
					{
						icon = fIconClassGuessed;
					}
					else
					{
						icon = fIconClass;
					}
				}
				else
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_METHOD;
					if (isGuessedObject)
					{
						icon = fIconFunctionGuessed;
					}
					else
					{
						icon = fIconFunction;
					}
				}
			}
			else
			{
				docText.append(ScriptDocHelper.createGenericDocumentationHTML(propertyName, doc, false));
				docText.append(doc.getDescription());
			}
		}
		else
		{
			// no docs, so we'll have to guess
			if (propertyObject instanceof IFunction)
			{
				// taking these out until they can talk better to the bracket inserter logic
				// if(isConstructor || finalObject.getPropertyNames().length == 0)
				// additionalText = "()";
				// extraInsert = 1;
				ci = computeArgContextInformation(finalObject, propertyName);
				docText.append("<b>" + propertyName + "</b> " + ci.getInformationDisplayString()); //$NON-NLS-1$ //$NON-NLS-2$
				ci = null;

				// find out if there are properties on the prototype
				if (hasPropertiesOnPrototype(finalObject, fileIndex, offset))
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_CLASS;
					if (isGuessedObject)
					{
						icon = fIconClassGuessed;
					}
					else
					{
						icon = fIconClass;
					}
				}
				else
				{
					sortingType = JSCompletionProposalComparator.OBJECT_TYPE_METHOD;
					if (isGuessedObject)
					{
						icon = fIconFunctionGuessed;
					}
					else
					{
						icon = fIconFunction;
					}
				}
			}
			else if (baseObject == global)
			{
				sortingType = JSCompletionProposalComparator.OBJECT_TYPE_GLOBAL_OBJECT;
				if (isGuessedObject)
				{
					icon = fIconObjectGuessed;
				}
				else
				{
					icon = fIconObject;
				}
				docText.append("<b>" + propertyName + Messages.JSContentAssistProcessor_NoDocs); //$NON-NLS-1$
			}
			else
			{
				sortingType = JSCompletionProposalComparator.OBJECT_TYPE_PROPERTY;
				if (isGuessedObject)
				{
					icon = fIconFieldGuessed;
				}
				else
				{
					icon = fIconField;
				}
				docText.append("<b>" + propertyName + Messages.JSContentAssistProcessor_NoDocs2); //$NON-NLS-1$
			}
		}
		// find source file name
		String name = ""; //$NON-NLS-1$
		if (sourceFileIndex == FileContextManager.BUILT_IN_FILE_INDEX)
		{
			if (doc != null)
			{
				name = doc.getUserAgent();
			}
			else
			{
				name = "JS Core"; //$NON-NLS-1$
			}
		}
		else if (sourceFileIndex == FileContextManager.CURRENT_FILE_INDEX)
		{
			if (doc != null && doc.getUserAgent() != null && !doc.getUserAgent().equals("")) //$NON-NLS-1$
			{
				name = doc.getUserAgent();
			}
			else
			{
				// TODO: safely cache this value at some point
				name = CoreUIUtils.getActiveEditorURI();
				if (name != null && "".equals(name) == false) //$NON-NLS-1$
				{
					name = new Path(name).lastSegment();
				}
			}
		}
		else if (sourceFileIndex > FileContextManager.DEFAULT_FILE_INDEX)
		{
			name = FileContextManager.getURIFromFileIndex(sourceFileIndex);
			
			if (name != null && name.length() > 0)
			{
				name = new Path(name).lastSegment();
			}
			else if (doc != null)
			{
				name = doc.getUserAgent();
			}
			// [IM] now decoding file names to remove %20's
		}
		else
		{
			name = Messages.JSContentAssistProcessor_Inferred; // shouldn't happen - this is the -1 case which doesn't get added to the environment
			// //$NON-NLS-1$
		}

		String location = (name == null) ? "" : name; // files not in profile show up in CA //$NON-NLS-1$
		location = StringUtils.urlDecodeFilename(location.toCharArray());

		cp = new JSCompletionProposal(propertyName + additionalText,
				beginOffset,// offset - prefixLen,
				replaceLength, propertyName.length() + extraInsert, icon, replacementText, ci, docText.toString(),
				sortingType, location, images);
		return cp;
	}

	private boolean hasPropertiesOnPrototype(IObject obj, int fileIndex, int offset)
	{
		// find out if there are properties directly on the prototype
		Property prot = obj.getProperty("prototype"); //$NON-NLS-1$
		if (prot != null)
		{
			String[] protProps = prot.getValue(fileIndex, offset).getLocalPropertyNames();
			// functions always have a 'constructor' property
			return (protProps != null && protProps.length > 1);
		}
		return false;
	}

	/**
	 * getAllPropertyNamesInScope
	 * 
	 * @param object
	 * @param includeHiddenProps
	 * @return String[]
	 */
	public static String[] getAllPropertyNamesInScope(IObject object, boolean includeHiddenProps)
	{
		if (object instanceof IScope)
		{
			IScope scope = (IScope) object;
			if (scope.getParentScope() == null)
			{
				return object.getPropertyNames(includeHiddenProps);
			}

			ArrayList al = new ArrayList();
			String[] curProps = object.getPropertyNames(includeHiddenProps);
			int totalProps = curProps.length;
			al.add(curProps);
			scope = scope.getParentScope();
			while (scope != null)
			{
				curProps = scope.getPropertyNames(includeHiddenProps);
				totalProps += curProps.length;
				al.add(curProps);
				scope = scope.getParentScope();
			}
			String[] result = new String[totalProps];
			int count = 0;
			for (int i = 0; i < al.size(); i++)
			{
				String[] props = (String[]) al.get(i);
				for (int k = 0; k < props.length; k++)
				{
					result[count++] = props[k];
				}
			}
			return result;

		}
		else
		{
			return object.getPropertyNames(includeHiddenProps);
		}
	}

	/**
	 * getOffsetMapper
	 * 
	 * @return IOffsetMapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		IFileLanguageService ls = context.getLanguageService(JSMimeType.MimeType);
		if (ls != null)
		{
			return (JSOffsetMapper) ls.getOffsetMapper();
		}
		else
		{
			return null;
		}
	}

	/**
	 * getJSOffsetMapper
	 * 
	 * @return JSOffsetMapper
	 */
	public JSOffsetMapper getJSOffsetMapper()
	{
		return (JSOffsetMapper) getOffsetMapper();
	}

	/**
	 * setHotkeyActivated
	 * 
	 * @param value
	 */
	public void setHotkeyActivated(boolean value)
	{
		JSContentAssistant.setHotkeyActivated(value);
	}

	/**
	 * setNextIdleActivated
	 * 
	 * @param value
	 */
	public void setNextIdleActivated(boolean value)
	{
		forceActivated = value;
	}

	/**
	 * isNextIdleActivated
	 * 
	 * @return boolean
	 */
	public boolean isNextIdleActivated()
	{
		return forceActivated;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor#isValidIdleActivationLocation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public boolean isValidIdleActivationLocation(ITextViewer viewer, int offset)
	{
		Lexeme currentLexeme = this.getOffsetMapper().getCurrentLexeme();
		return UnifiedContentAssistProcessor.isValidIdleActivationToken(currentLexeme,
				getCompletionProposalIdleActivationTokens());
	}
}
