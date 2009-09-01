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
package com.aptana.ide.editor.js.environment;

import java.util.ArrayList;
import java.util.Stack;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.FunctionBase;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.TypedDescription;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Spike Washburn
 */
public class LexemeConsumerHelper
{
	Environment env;
	LexemeBasedEnvironmentLoader envLoader;
	LexemeList lexemeList;
	int llSize;
	private IParseState parseState;

	/**
	 * LexemeConsumerHelper
	 * 
	 * @param env
	 * @param envLoader
	 * @param parseState
	 */
	LexemeConsumerHelper(Environment env, LexemeBasedEnvironmentLoader envLoader, IParseState parseState)
	{
		this.env = env;
		this.envLoader = envLoader;
		this.parseState = parseState;
		this.lexemeList = parseState.getLexemeList();
		this.llSize = this.lexemeList.size();
	}

	/**
	 * consumeIdentifier
	 * 
	 * @param startIndex
	 * @param scope
	 * @param isVar
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeIdentifier(int startIndex, IScope scope, boolean isVar)
			throws EndOfFileException
	{
		JSIdentifierConsumer indentifier = new JSIdentifierConsumer(scope, isVar);

		return indentifier.consume(startIndex);
	}

	/**
	 * consumeIdentifier
	 * 
	 * @param startIndex
	 * @param scope
	 * @param parent
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeIdentifier(int startIndex, IScope scope, IObject parent)
			throws EndOfFileException
	{
		JSIdentifierConsumer indentifier = new JSIdentifierConsumer(scope, parent);

		return indentifier.consume(startIndex);
	}

	/**
	 * consumeStatements
	 * 
	 * @param startIndex
	 * @param scope
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeStatements(int startIndex, IScope scope) throws EndOfFileException
	{
		JSStatementConsumer statements = new JSStatementConsumer(scope);

		return statements.consume(startIndex);
	}

	/**
	 * consumeObjectLiteral
	 * 
	 * @param startIndex
	 * @param scope
	 * @param objectLiteral
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeObjectLiteral(int startIndex, IScope scope, IObject objectLiteral)
			throws EndOfFileException
	{
		JSObjectLiteralConsumer objectLiteralConsumer = new JSObjectLiteralConsumer(scope, objectLiteral);

		return objectLiteralConsumer.consume(startIndex);
	}

	/**
	 * Adds any alias tags on an object to the environment
	 * 
	 * @param alias
	 */
	private void addPotentialAliases(IObject obj, TypedDescription alias)
	{
		if (obj != null && alias != null && alias.getTypes().length > 0)
		{
			String[] aliasTypes = alias.getTypes();
			for (int i = 0; i < aliasTypes.length; i++)
			{
				String type = aliasTypes[i];
				if (type != null && type != "") //$NON-NLS-1$
				{
					String name = type;
					IObject root = env.getGlobal();
					if (type.indexOf(".") > -1) //$NON-NLS-1$
					{
						int dotLoc = type.lastIndexOf("."); //$NON-NLS-1$
						String basename = type.substring(0, dotLoc);
						name = type.substring(dotLoc + 1);
						root = lookupOrCreateObject(basename, env);
					}
					if (!name.equals("")) //$NON-NLS-1$
					{
						JSReference aliasRef = new JSReference(root, name, false);
						envLoader.putPropertyValue(aliasRef, obj);
						// IObject newObj = envLoader.getPropertyValue(aliasRef, Integer.MAX_VALUE);
						// newObj.setDocumentation(func.getDocumentation());
					}
				}
			}
		}
	}

	/**
	 * consumeArrayLiteral
	 * 
	 * @param startIndex
	 * @param scope
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeArrayLiteral(int startIndex, IScope scope) throws EndOfFileException
	{
		JSArrayLiteralConsumer arrayLiteral = new JSArrayLiteralConsumer(scope);

		return arrayLiteral.consume(startIndex);
	}

	/**
	 * consumeFunction
	 * 
	 * @param startIndex
	 * @param scope
	 * @param ref
	 * @param functionDoc
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeFunction(int startIndex, IScope scope, JSReference ref,
			FunctionDocumentation functionDoc) throws EndOfFileException
	{
		JSFunctionConsumer function = new JSFunctionConsumer(scope, ref, functionDoc);

		return function.consume(startIndex);
	}

	/**
	 * consumeAssignment
	 * 
	 * @param startIndex
	 * @param scope
	 * @param reference
	 * @param referenceStartIndex
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeAssignment(int startIndex, IScope scope, JSReference reference,
			int referenceStartIndex) throws EndOfFileException
	{
		JSAssignmentConsumer assignment = new JSAssignmentConsumer(scope, reference, referenceStartIndex);

		return assignment.consume(startIndex);
	}

	/**
	 * consumeNewStatement
	 * 
	 * @param startIndex
	 * @param scope
	 * @return LexemeConsumerResult
	 * @throws EndOfFileException
	 */
	protected LexemeConsumerResult consumeNewStatement(int startIndex, IScope scope) throws EndOfFileException
	{
		JSNewStatementConsumer newStatement = new JSNewStatementConsumer(scope);

		return newStatement.consume(startIndex);
	}

	/**
	 * @author Spike Washburn
	 */
	class LexemeConsumerResult
	{
		int endIndex;
		Object value;

		/**
		 * LexemeConsumerResult
		 * 
		 * @param endIndex
		 * @param value
		 */
		LexemeConsumerResult(int endIndex, Object value)
		{
			this.endIndex = endIndex;
			this.value = value;
		}
	}

	/**
	 * @author Spike Washburn
	 */
	abstract class LexemeConsumer
	{
		/**
		 * consume
		 * 
		 * @param startIndex
		 * @return LexemeConsumerResult
		 * @throws EndOfFileException
		 */
		public abstract LexemeConsumerResult consume(int startIndex) throws EndOfFileException;

		/**
		 * Returns the index of the next lexeme that is not a comment or other documentation
		 * 
		 * @param startIndex
		 *            The index at which to begin searching
		 * @return The index of the next lexeme in the lexeme list
		 * @throws EndOfFileException
		 *             Thrown if there is no next lexeme after any EOF whitespace
		 */
		protected int skipWhitespace(int startIndex) throws EndOfFileException
		{
			int index = startIndex;
			while (index < llSize)
			{
				index = getJSLexeme(index);
				Lexeme l = lastLexeme;
				if (l.typeIndex != JSTokenTypes.COMMENT && l.typeIndex != JSTokenTypes.DOCUMENTATION)
				{
					return index;
				}
				index++;
			}

			throw new EndOfFileException();
		}

		/**
		 * Retrieves the function documentation from the documentation store.
		 * 
		 * @param lexeme
		 *            The lexeme for which to retrieve documentation
		 * @return A new FunctionDocumentation object, or null if not found
		 */
		protected FunctionDocumentation getFunctionDocumentation(Lexeme lexeme)
		{
			if (lexeme == null)
			{
				return null;
			}
			JSParseState jsps = (JSParseState) parseState.getParseState(JSMimeType.MimeType);
			if (jsps != null)
			{
				IDocumentationStore store = jsps.getDocumentationStore();
				IDocumentation doc = store.getDocumentationFromOffset(lexeme.getEndingOffset());
				if (doc instanceof FunctionDocumentation)
				{
					return (FunctionDocumentation) doc;
				}
			}
			return null;
		}

		/**
		 * Retrieves the property documentation from the documentation store.
		 * 
		 * @param lexeme
		 *            The lexeme for which to retrieve documentation
		 * @return A new PropertyDocumentation object, or null if not found
		 */
		protected IDocumentation getPropertyDocumentation(Lexeme lexeme)
		{
			if (lexeme == null)
			{
				return null;
			}
			JSParseState jsps = (JSParseState) parseState.getParseState(JSMimeType.MimeType);
			if (jsps != null)
			{
				IDocumentationStore store = jsps.getDocumentationStore();
				IDocumentation doc = store.getDocumentationFromOffset(lexeme.offset + lexeme.getLength());
				return doc;
			}
			return null;
		}

		/**
		 * findDocumentationLexemeAboveIndex
		 * 
		 * @param index
		 * @return Lexeme
		 */
		protected Lexeme findDocumentationLexemeAboveIndex(int index)
		{
			int i = index - 1;
			int maxLookback = 10;
			int minIndex = i > maxLookback ? i - maxLookback : 0;
			while (i >= minIndex) // need to limit this
			{
				Lexeme lexeme = lexemeList.get(i); // needs to get all lang lexemes
				if (lexeme.typeIndex == ScriptDocTokenTypes.END_DOCUMENTATION
						&& lexeme.getToken().getLanguage().equals(ScriptDocMimeType.MimeType))
				{
					return lexeme;
				}
				else
				{
					switch (lexeme.typeIndex)
					{
						case JSTokenTypes.COMMENT:
							break;
						case JSTokenTypes.VAR:
							break;
						case JSTokenTypes.IDENTIFIER: // for obj literal assignments {
														// x:function(){} }
							break;
						case JSTokenTypes.COLON:
							break;
						default:
							return null;
					}
				}
				i--;
			}
			return null;
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSAssignmentConsumer extends LexemeConsumer
	{
		IScope scope;
		boolean isVar;
		JSReference reference;
		int referenceStartIndex;

		/**
		 * JSAssignmentConsumer
		 * 
		 * @param scope
		 * @param reference
		 * @param referenceStartIndex
		 */
		JSAssignmentConsumer(IScope scope, JSReference reference, int referenceStartIndex)
		{
			this.scope = scope;
			this.reference = reference;
			this.referenceStartIndex = referenceStartIndex;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			int index = startIndex;
			// Lexeme lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme lexeme = lastLexeme;

			if (lexeme.typeIndex != JSTokenTypes.EQUAL && lexeme.typeIndex != JSTokenTypes.COLON)
			{
				throw new IllegalStateException();
			}

			LexemeConsumerResult result;
			index = skipWhitespace(index + 1);

			// lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			lexeme = lastLexeme;

			IObject assignValue = null;
			switch (lexeme.typeIndex)
			{
				case JSTokenTypes.FUNCTION:
					if (lastObjectKind.size() > 0)
					{
						lastObjectKind.pop();
						lastObjectKind.push(FUNCTION);
					}
					FunctionDocumentation functionDoc = getFunctionDocumentation(findDocumentationLexemeAboveIndex(referenceStartIndex));
					JSFunctionConsumerResult fResult = (JSFunctionConsumerResult) consumeFunction(index, scope,
							reference, functionDoc);
					if (fResult.function != null)
					{
						envLoader.registerScope(fResult.function.getBodyScope(), fResult.scopeStartingOffset,
								fResult.scopeEndingOffset);
						if (reference.getObjectBase() instanceof JSObject)
						{
							fResult.function.setGuessedMemberObject((JSObject) reference.getObjectBase());
						}
					}
					index = fResult.endIndex;
					break;
				case JSTokenTypes.NEW:
					result = consumeNewStatement(index, scope);
					index = result.endIndex;

					// assign the new'd object to the property reference
					assignValue = (IObject) result.value;
					if (assignValue != null)
					{
						envLoader.putPropertyValue(reference, assignValue);
					}
					break;
				case JSTokenTypes.STRING:
					index++;
					assignValue = envLoader.createNewInstance("String", lexeme.getStartingOffset(), false); //$NON-NLS-1$
					envLoader.putPropertyValue(reference, assignValue);
					break;

				case JSTokenTypes.NUMBER:
					index++;
					assignValue = envLoader.createNewInstance("Number", lexeme.getStartingOffset(), false); //$NON-NLS-1$
					envLoader.putPropertyValue(reference, assignValue);
					break;

				case JSTokenTypes.NULL:
					index++;
					assignValue = ObjectBase.NULL;
					envLoader.putPropertyValue(reference, assignValue);
					break;

				case JSTokenTypes.TRUE:
				case JSTokenTypes.FALSE:
					index++;
					assignValue = envLoader.createNewInstance("Boolean", lexeme.getStartingOffset(), false); //$NON-NLS-1$
					envLoader.putPropertyValue(reference, assignValue);
					break;

				case JSTokenTypes.REGEX:
					index++;
					assignValue = envLoader.createNewInstance("RegExp", lexeme.getStartingOffset(), false); //$NON-NLS-1$
					envLoader.putPropertyValue(reference, assignValue);
					break;

				case JSTokenTypes.LCURLY:
					// an object literal has been encountered, so let the object literal consumer
					// process it

					if (lastObjectKind.size() > 0)
					{
						lastObjectKind.pop();
						lastObjectKind.push(OBJECT_LITERAL);
					}
					// create an object to hold the properties defined by the object literal
					IObject objectLiteral = new JSObject(
							new Range(lexeme.getStartingOffset(), lexeme.getEndingOffset()));

					if (reference != null)
					{
						IObject o = null;
						Property p = reference.getObjectBase().getProperty(reference.getPropertyName());
						if (p != null)
						{
							o = p.getValue(Integer.MAX_VALUE, Integer.MAX_VALUE);
						}

						if (o != null)
						{
							objectLiteral = o;
						}

						envLoader.putPropertyValue(reference, objectLiteral);
					}

					result = consumeObjectLiteral(index, scope, objectLiteral);
					index = result.endIndex;

					// assign the object literal value to the property reference
					assignValue = (IObject) result.value;
					if (assignValue != null)
					{
						envLoader.putPropertyValue(reference, assignValue);
					}
					break;

				case JSTokenTypes.LBRACKET:
					// an array literal has been encountered, so let the array literal consumer
					// process it
					result = consumeArrayLiteral(index, scope);
					index = result.endIndex;

					// assign the object literal value to the property reference
					assignValue = (IObject) result.value;
					if (assignValue != null)
					{
						envLoader.putPropertyValue(reference, assignValue);
					}
					break;

				case JSTokenTypes.IDENTIFIER:
					String hashName = LexemeConsumerHelper.getNameHash(index, lexemeList);
					boolean isInvoking = hashName.endsWith(")"); //$NON-NLS-1$

					// checking docs first
					String retName = LexemeConsumerHelper.lookupReturnStringFromHash(hashName, scope, env);
					if (retName != null && !retName.equals("")) //$NON-NLS-1$
					{
						assignValue = envLoader.createNewInstance(retName, lexeme.getStartingOffset(), isInvoking);
						envLoader.putPropertyValue(reference, assignValue);
					}
					else
					// else checking assignment chain
					{
						IObject obj = LexemeConsumerHelper.lookupReturnTypeFromNameHash(hashName, scope, env);
						int offset = lexeme.getStartingOffset();
						int fileIndex = envLoader.getFileIndex();
						if (obj instanceof IFunction) // functions can be used as ctors
						{
							IFunction f = (IFunction) obj;
							assignValue = f.construct(env, FunctionBase.EmptyArgs, fileIndex, new Range(offset,
									offset + 1));
							envLoader.putPropertyValue(reference, assignValue);
						}
						else if (obj != null) // else make a dup of the result type
						{
							IObject fobj = obj.getPropertyValue("constructor", fileIndex, offset); //$NON-NLS-1$
							if (fobj != null && fobj instanceof IFunction)
							{
								IFunction f = (IFunction) fobj;
								assignValue = f.construct(env, FunctionBase.EmptyArgs, fileIndex, new Range(offset,
										offset + 1));
								envLoader.putPropertyValue(reference, assignValue);
							}
						}
					}
					// if(!hashName.endsWith(")") && obj != null && obj.getDocumentation()!= null &&
					// obj.getDocumentation() instanceof PropertyDocumentation)
					// {
					// PropertyDocumentation pdoc = (PropertyDocumentation)obj.getDocumentation();
					// if(pdoc.getReturn() != null && pdoc.getReturn().getTypes()!= null )
					// {
					// String[] types = pdoc.getReturn().getTypes();
					// if(types.length > 0)
					// {
					// assignValue = envLoader.createNewInstance(types[0],
					// lexeme.getStartingOffset());
					// envLoader.putPropertyValue(reference, assignValue);
					// }
					// }
					// }
					break;
				default:
					break;
			}

			return new LexemeConsumerResult(index, assignValue);
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSStatementConsumer extends LexemeConsumer
	{
		boolean isVar;
		IScope parentScope;
		int curlyDepth;
		JSReference assignToReference; // an environment reference for an identifier that is being
										// assigned to a value
		int assignToLexemeStartIndex; // the startIndex of an identifier that is being assigned to
										// a value

		/**
		 * JSStatementConsumer
		 * 
		 * @param parentScope
		 */
		JSStatementConsumer(IScope parentScope)
		{
			this.parentScope = parentScope;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			int index = startIndex;
			while (index < llSize)
			{
				index = skipWhitespace(index);

				// Lexeme lexeme = getJSLexeme(index);
				index = getJSLexeme(index);
				Lexeme lexeme = lastLexeme;

				switch (lexeme.typeIndex)
				{
					case JSTokenTypes.FUNCTION:
						JSFunctionConsumerResult fResult = (JSFunctionConsumerResult) consumeFunction(index,
								parentScope, null, null);
						if (fResult.function != null) // && fResult.name != null)
						{
							// envLoader.putPropertyValue(parentScope, fResult.name,
							// fResult.function, true);
							envLoader.registerScope(fResult.function.getBodyScope(), fResult.scopeStartingOffset,
									fResult.scopeEndingOffset);
						}
						index = fResult.endIndex;
						break;
					case JSTokenTypes.IDENTIFIER:
					case JSTokenTypes.THIS:
						int identifierStartIndex = index;
						LexemeConsumerHelper.LexemeConsumerResult result = consumeIdentifier(index, parentScope, isVar);
						isVar = false;
						index = result.endIndex;

						// scan ahead to see if this variable is about6 to get an assignment of a
						// function, or a new
						// object
						// if so, consume the right side and save the result into the value of the
						// identifier's property
						int nextIndex = skipWhitespace(index + 1);

						// Lexeme nextLexeme = getJSLexeme(nextIndex);
						nextIndex = getJSLexeme(nextIndex);
						Lexeme nextLexeme = lastLexeme;

						if (nextLexeme.typeIndex == JSTokenTypes.EQUAL)
						{
							assignToLexemeStartIndex = identifierStartIndex;
							assignToReference = (JSReference) result.value;
						}
						break;
					case JSTokenTypes.VAR:
						isVar = true;
						break;
					case JSTokenTypes.LCURLY:
						curlyDepth++;
						break;
					case JSTokenTypes.RCURLY:
						curlyDepth--;
						if (curlyDepth < 0)
						{
							return new LexemeConsumerResult(index, null);
						}
						break;
					case JSTokenTypes.EQUAL:
					{
						IObject assignedValue = null;
						if (assignToReference != null)
						{
							nextIndex = skipWhitespace(index + 1);

							// nextLexeme = getJSLexeme(nextIndex);
							nextIndex = getJSLexeme(nextIndex);
							nextLexeme = lastLexeme;

							if (nextLexeme.typeIndex == JSTokenTypes.FUNCTION // function value
																				// assignment
									|| nextLexeme.typeIndex == JSTokenTypes.NEW // 'new' object
																				// value assignment
									|| nextLexeme.typeIndex == JSTokenTypes.LCURLY // object
																					// literal value
																					// assignment
									|| nextLexeme.typeIndex == JSTokenTypes.LBRACKET // object
																						// literal
																						// value
							// assignment
							)
							{
								result = consumeAssignment(index, parentScope, assignToReference,
										assignToLexemeStartIndex);
								index = result.endIndex;
								assignedValue = (IObject) result.value;

							}
							else if (nextLexeme.typeIndex == JSTokenTypes.STRING
									|| nextLexeme.typeIndex == JSTokenTypes.NUMBER
									|| nextLexeme.typeIndex == JSTokenTypes.REGEX
									|| nextLexeme.typeIndex == JSTokenTypes.TRUE
									|| nextLexeme.typeIndex == JSTokenTypes.FALSE)
							{
								// skip ahead one more lexeme to see if its a semicolon
								nextIndex = skipWhitespace(nextIndex + 1);

								// nextLexeme = getJSLexeme(nextIndex);
								nextIndex = getJSLexeme(nextIndex);
								nextLexeme = lastLexeme;

								if (nextLexeme.typeIndex == JSTokenTypes.SEMICOLON)
								{
									result = consumeAssignment(index, parentScope, assignToReference,
											assignToLexemeStartIndex);
									index = result.endIndex;
									assignedValue = (IObject) result.value;
								}
							}
							else if (nextLexeme.typeIndex == JSTokenTypes.IDENTIFIER)
							{
								result = consumeAssignment(index, parentScope, assignToReference,
										assignToLexemeStartIndex);
								index = result.endIndex;
								assignedValue = (IObject) result.value;
							}
							// this is an unsupported assignment
							// if there was a documentation block above this assignment, then bind
							// it to the property
							IDocumentation propertyDoc = getPropertyDocumentation(findDocumentationLexemeAboveIndex(assignToLexemeStartIndex));
							if (propertyDoc != null)
							{
								// TODO: Check with Kevin...should the documentation be attached to
								// the property and/or
								// the value?
								// assignToReference.getProperty().setDocumentation(propertyDoc);
								IObject propValue = assignedValue;
								if (propValue == null || propValue == ObjectBase.UNDEFINED)
								{
									propValue = envLoader.getPropertyValue(assignToReference, lexeme
											.getStartingOffset());
								}

								if (propValue != ObjectBase.UNDEFINED)
								{
									try
									{
										if (propertyDoc instanceof PropertyDocumentation)
										{
											PropertyDocumentation pDoc = (PropertyDocumentation) propertyDoc;
											IObject retValue = null;

											TypedDescription typeDesc = pDoc.getReturn();
											if (typeDesc != null && typeDesc.getTypes().length > 0)
											{
												String type = typeDesc.getTypes()[0];
												if (type != null)
												{
													retValue = envLoader.createNewInstance(type, lexeme
															.getStartingOffset(), false);
													envLoader.putPropertyValue(assignToReference, retValue);
													propValue.setPrototype(retValue);
												}
											}
											addPotentialAliases(propValue, pDoc.getAliases());

											// TypedDescription alias = pDoc.getAlias();
											// String[] aliasTypes = alias.getTypes();
											// if(retValue != null && alias != null &&
											// aliasTypes.length > 0)
											// {
											// for (int i = 0; i < aliasTypes.length; i++)
											// {
											// String type = aliasTypes[i];
											// if(type != null && type != "")
											// {
											// String name = type;
											// IObject root = env.getGlobal();
											// if(type.indexOf(".") > -1)
											// {
											// int dotLoc = type.lastIndexOf(".");
											// String basename = type.substring(0, dotLoc);
											// name = type.substring(dotLoc);
											// root = lookupOrCreateObject(basename, env);
											// }
											// JSReference aliasRef = new JSReference(root, name,
											// true);
											// envLoader.putPropertyValue(aliasRef, retValue);
											// }
											// }
											// }
										}
									}
									catch (Exception e)
									{
										IdeLog.logError(JSPlugin.getDefault(), Messages.LexemeConsumerHelper_ErrorSettingPrototype, e);
									}

									if (propValue != null)
									{
										propValue.setDocumentation(propertyDoc);
									}
								}
							}

							// remove the assignToReference now that the assignment is complete.
							assignToReference = null;
							assignToLexemeStartIndex = -1;
						}
						break;
					}
					default:
						break;
				}
				index++;
			}
			throw new EndOfFileException();
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSObjectLiteralConsumer extends LexemeConsumer
	{
		IScope parentScope;
		IObject objectLiteral;

		/**
		 * JSObjectLiteralConsumer
		 * 
		 * @param parentScope
		 * @param objectLiteral 
		 */
		JSObjectLiteralConsumer(IScope parentScope, IObject objectLiteral)
		{
			this.parentScope = parentScope;
			this.objectLiteral = objectLiteral;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			// Lexeme lexeme = getJSLexeme(startIndex);
			startIndex = getJSLexeme(startIndex);
			Lexeme lexeme = lastLexeme;

			if (lexeme.typeIndex != JSTokenTypes.LCURLY)
			{
				throw new IllegalStateException();
			}

			// consumes the LCurly, and process until the rCurly is hit
			int curlyDepth = 1;
			int index = startIndex;
			JSReference currentReference = null; // the reference to the property associated
													// with the named identifiers
			int currentReferenceStartIndex = -1;
			while (index < llSize && curlyDepth > 0)
			{
				if (index + 1 < llSize)
				{
					index = skipWhitespace(index + 1);
				}

				index = getJSLexeme(index);
				lexeme = lastLexeme;
				LexemeConsumerResult result;
				switch (lexeme.typeIndex)
				{
					case JSTokenTypes.RCURLY:
						curlyDepth--;
						break;
					case JSTokenTypes.COMMA:
						// NOTE: If we don't advance here, last lexemes that are commas cause an infinite loop
						if (index + 1 == llSize)
						{
							index++;
						}
						// skip commas
						break;
					case JSTokenTypes.IDENTIFIER:
						currentReferenceStartIndex = index;
						result = consumeIdentifier(index, parentScope, objectLiteral);
						// save the identifier's property reference so it can be assigned a value.
						currentReference = (JSReference) result.value;

						index = result.endIndex;
						break;
					case JSTokenTypes.COLON:
						// int firstIndex = startIndex;
						// int curAssignmentStart = index + 1;
						lastObjectKind.push("unknown"); //$NON-NLS-1$
						if (currentReference != null)
						{
							// let the assignment processor consume the r-side value of the
							// assignment
							result = consumeAssignment(index, parentScope, currentReference, currentReferenceStartIndex);
							index = result.endIndex;
						}
						String lastObj = (String) lastObjectKind.pop();
						boolean wasObjLit = lastObj.equals(OBJECT_LITERAL);
						boolean wasFn = lastObj.equals(FUNCTION);
						// int lastIndex = wasObjectLiteral ? curAssignmentStart : index;

						// we need to check if that was an object literal,
						// however we have no context...
						// a) if we are on a rcurly and this wasn't an object literal or fn, that is
						// a close brace
						if (!wasObjLit && !wasFn && index < llSize)
						{
							Lexeme curLexeme = lexemeList.get(index);
							if (curLexeme.typeIndex == JSTokenTypes.RCURLY)
							{
								curlyDepth--;
							}
						}
						// b) Don't need this empty obj literal test as the above will catch it
						// if(wasObjLit && index < llSize && index > 0)// && firstIndex < llSize &&
						// lastIndex < llSize)
						// {
						// Lexeme lastLexeme = lexemeList.get(index - 1);
						// if(lastLexeme.typeIndex == JSTokenTypes.LCURLY)
						// {
						// curlyDepth--;
						// }
						// }
						break;
					default:
						curlyDepth = 0;
						break;
				}
			}
			return new LexemeConsumerResult(index, objectLiteral);
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSArrayLiteralConsumer extends LexemeConsumer
	{
		IScope parentScope;

		/**
		 * JSArrayLiteralConsumer
		 * 
		 * @param parentScope
		 */
		JSArrayLiteralConsumer(IScope parentScope)
		{
			this.parentScope = parentScope;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			startIndex = getJSLexeme(startIndex);
			Lexeme lexeme = lastLexeme;

			if (lexeme.typeIndex != JSTokenTypes.LBRACKET)
			{
				throw new IllegalStateException();
			}

			int startingOffset = lexeme.getStartingOffset();

			// consumes the LCurly, and process until the rCurly is hit
			int bracketDepth = 1;
			int index = startIndex;

			if (index + 1 < llSize)
			{
				index = skipWhitespace(index + 1);
			}

			// get the first object to guess the type of array, if possible
			String type = "Object"; //$NON-NLS-1$

			// lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			lexeme = lastLexeme;

			switch (lexeme.typeIndex)
			{
				case JSTokenTypes.RBRACKET:
					bracketDepth--;
					break;
				case JSTokenTypes.STRING:
					type = "String"; //$NON-NLS-1$
					break;
				case JSTokenTypes.NUMBER:
					type = "Number"; //$NON-NLS-1$
					break;
				case JSTokenTypes.TRUE:
				case JSTokenTypes.FALSE:
					type = "Boolean"; //$NON-NLS-1$
					break;
				case JSTokenTypes.REGEX:
					type = "RegExp"; //$NON-NLS-1$
					break;
				case JSTokenTypes.LBRACKET:
					type = "Array"; //$NON-NLS-1$
					break;
				case JSTokenTypes.FUNCTION:
					type = "Function"; //$NON-NLS-1$
					break;
				case JSTokenTypes.NEW:
					if (index + 1 < llSize)
					{
						index = skipWhitespace(index + 1);
					}
					index = getJSLexeme(index);
					lexeme = lastLexeme;

					if (lexeme.typeIndex == JSTokenTypes.RBRACKET)
					{
						bracketDepth = 0;
					}
					else if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER)
					{
						// todo: maybe consume ns ident
						type = lexeme.getText();
					}
					break;
				default:
					break;
			}

			while (index < llSize && bracketDepth > 0)
			{
				if (index + 1 < llSize)
				{
					index = skipWhitespace(index + 1);
				}
				else
				{
					bracketDepth = 0;
					break;
				}

				index = getJSLexeme(index);
				lexeme = lastLexeme;

				switch (lexeme.typeIndex)
				{
					case JSTokenTypes.RBRACKET:
						bracketDepth--;
						break;
					default:
						break;
				}
				if (lexeme.isAfterEOL())
				{
					bracketDepth = 0;
				}
			}
			// add return type to doc
			PropertyDocumentation doc = new PropertyDocumentation();
			doc.getReturn().addType("Array"); //$NON-NLS-1$
			doc.getReturn().addType(type);

			// create an object to hold the properties defined by the object literal
			IObject array = envLoader.createNewInstance("Array", startingOffset, false); //$NON-NLS-1$
			array.setDocumentation(doc);

			return new LexemeConsumerResult(index, array);
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSNewStatementConsumer extends LexemeConsumer
	{
		IScope scope;

		/**
		 * JSNewStatementConsumer
		 * 
		 * @param scope
		 */
		JSNewStatementConsumer(IScope scope)
		{
			this.scope = scope;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			int index = startIndex;

			// Lexeme lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme lexeme = lastLexeme;

			if (lexeme.typeIndex != JSTokenTypes.NEW)
			{
				throw new IllegalStateException();
			}

			Range range = new Range(lexeme.getStartingOffset(), lexeme.getEndingOffset());
			index = skipWhitespace(index + 1);

			// lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			lexeme = lastLexeme;

			IObject newValue = null;
			if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				LexemeConsumerResult result = consumeIdentifier(index, scope, false);
				index = result.endIndex;

				JSReference propReference = (JSReference) result.value;

				IObject value;
				if (propReference.getObjectBase() instanceof IScope)
				{
					value = envLoader.getVariableValue((IScope) propReference.getObjectBase(), propReference
							.getPropertyName(), range.getStartingOffset());
				}
				else
				{
					value = envLoader.getPropertyValue(propReference, range.getStartingOffset());
				}

				// todo: need to check here for more elements, eg var x = new Date().now;

				if (value instanceof IFunction)
				{
					// create a new instance of the function
					// Note: createNewInstance hooks the function's prototype values up to the new
					// instance
					newValue = envLoader.createNewInstance(range.getStartingOffset(), (IFunction) value);
				}
			}
			else
			{
				// this new operator is invalid, so roll our consumption back to the new lexeme
				index = startIndex;
			}

			if (newValue == null)
			{
				// there was a problem creating an instance of a function, so just return an empty
				// object
				newValue = new JSObject(range);
			}

			return new LexemeConsumerResult(index, newValue);
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSFunctionConsumer extends LexemeConsumer
	{
		private IScope parentScope;
		private String functionName;
		private ArrayList functionArgs = new ArrayList();
		private JSReference reference; // the reference to attach the function to
		private FunctionDocumentation functionDocs;

		/**
		 * JSFunctionConsumer
		 * 
		 * @param parentScope
		 * @param ref
		 * @param functionDocs
		 */
		JSFunctionConsumer(IScope parentScope, JSReference ref, FunctionDocumentation functionDocs)
		{
			this.parentScope = parentScope;
			this.reference = ref;
			this.functionDocs = functionDocs;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			int index = startIndex;

			// Lexeme lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme lexeme = lastLexeme;

			if (lexeme.typeIndex != JSTokenTypes.FUNCTION)
			{
				throw new IllegalStateException();
			}

			int functionStart = lexeme.getEndingOffset();

			FunctionDocumentation functionDoc = functionDocs;
			if (functionDoc == null)
			{
				functionDoc = getFunctionDocumentation(findDocumentationLexemeAboveIndex(index));
			}
			index = advanceToLCurly(index);

			// Lexeme lCurly = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme lCurly = lastLexeme;

			if (lCurly.typeIndex != JSTokenTypes.LCURLY)
			{
				return new JSFunctionConsumerResult(index, null, functionName, 0, 0);
			}

			// setup the function arguments
			String[] funcArgs = new String[this.functionArgs.size()];
			for (int i = 0; i < funcArgs.length; i++)
			{
				Lexeme l = (Lexeme) functionArgs.get(i);
				funcArgs[i] = l.getText();
			}

			// create the function
			int functionOffset = lexeme.getStartingOffset();
			if (functionName != null)
			{
				// HACK: we need function definitions to be declared before all other objects in the
				// file
				// since global declarations are theoretically interpreted first. To create this
				// appearance,
				// we set the function offset to a negative value so that all non-Function
				// assignments
				functionOffset = Integer.MIN_VALUE + 1 + lexeme.getStartingOffset();
			}
			JSFunction func = envLoader.createFunctionInstance(functionOffset, false);
			// JSFunction func = envLoader.createNewInstance(functionName, functionOffset);

			// [RD] adding probable assignement ref for prototype mapping
			if (this.reference != null && this.reference.getObjectBase() instanceof JSObject)
			{
				func.setGuessedMemberObject((JSObject) this.reference.getObjectBase());
			}
			else
			{
				func.setGuessedMemberObject((JSObject) func.getLocalProperty("prototype").getValue( //$NON-NLS-1$
						FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE));
			}

			IScope bodyScope = new JSScope();
			// set body's parent scope
			bodyScope.setParentScope(parentScope);
			bodyScope.setEnclosingFunction(func);
			func.setBodyScope(bodyScope);
			// func.setGuessedMemberObject((JSObject)func.getLocalProperty("prototype").getValue(Integer.MAX_VALUE,
			// Integer.MAX_VALUE));
			if (functionDoc != null)
			{
				func.setDocumentation(functionDoc);
			}

			// set the function parameter names, and pre-add the args to the function's scope
			func.setParameters(funcArgs);
			TypedDescription[] argTypes = functionDoc != null ? functionDoc.getParams() : new TypedDescription[0];
			int size = functionArgs.size();
			for (int i = 0; i < size; i++)
			{
				String type = null;
				TypedDescription argDesc = null;
				if (argTypes.length > i)
				{
					argDesc = argTypes[i];
					String[] types = argDesc.getTypes();
					type = types.length > 0 ? types[0] : type;
				}
				Lexeme l = (Lexeme) functionArgs.get(i);
				if (type == null)
				{
					envLoader.addVariable(bodyScope, l.getText(), l.getStartingOffset(), true);
				}
				else
				{
					// here we want to create an instance of the arg type
					// this will only happen if the type is an IFunction
					// otherwise it will just use the passed type (assuming a passed object with
					// props directly on it)
					IObject argVal = null;
					IObject classType = lookupReturnTypeFromNameHash(type, env.getGlobal(), env);
					boolean isFunction = true;
					if (classType instanceof IFunction)
					{
						argVal = envLoader.createNewInstance(functionOffset, (IFunction) classType);
					}
					else
					{
						isFunction = false;
						argVal = classType;// envLoader.createNewInstance(type,
											// l.getStartingOffset(), false);
					}

					if (argVal != null)
					{
						// [RD] set docs using newly created doc based on the param arg
						envLoader.putVariableValue(bodyScope, l.getText(), argVal, true);
						PropertyDocumentation argDoc = new PropertyDocumentation();
						String desc = argDesc.getDescription() == null ? "" : argDesc.getDescription(); //$NON-NLS-1$
						argDoc.setDescription(desc);
						// only doc return types on functions
						if (isFunction)
						{
							argDoc.getReturn().addType(type);
						}
						argVal.setDocumentation(argDoc);
					}
				}
			}
			// add arguments object
			IObject argsArray = envLoader.addVariable(bodyScope, "arguments", lCurly.getStartingOffset(), true); //$NON-NLS-1$
			int fileIndex = FileContextManager.BUILT_IN_FILE_INDEX;
			IObject gFn = env.getGlobal().getPropertyValue("Function", fileIndex, 0); //$NON-NLS-1$
			IObject fnprot = gFn.getPropertyValue("prototype", fileIndex, 0); //$NON-NLS-1$
			IObject args = fnprot.getPropertyValue("arguments", fileIndex, 0); //$NON-NLS-1$
			argsArray.setDocumentation(args.getDocumentation());

			if (functionDoc != null && functionDoc.getExtends() != null)
			{
				String[] types = functionDoc.getExtends().getTypes();
				IObject fnObj = null;
				if (types.length > 0)
				{
					String type = types[0];
					if (!type.equals("Object")) //$NON-NLS-1$
					{
						// maybe this just needs to be global lookup? (not return type?)
						fnObj = lookupReturnTypeFromNameHash(type, env.getGlobal(), env);
						if (fnObj != null)
						{
							Property prot = fnObj.getProperty("prototype"); //$NON-NLS-1$
							if (prot != null)
							{
								// this should only work on local properties, so safe
								func.deletePropertyName("prototype"); //$NON-NLS-1$

								// probably should be adding via putPropertyValue() here to be safe
								// with refs
								func.putPropertyValue("prototype", prot.getAssignment(0), envLoader.getFileIndex()); //$NON-NLS-1$
								// func.putLocalProperty("prototype", prot);
							}
						}
					}
				}

				addPotentialAliases(func, functionDoc.getAliases());
			}

			// Register the function with the environment to guarantee all references made to the
			// function inside the body augment this function instance instead of creating an
			// implicit JSObject.
			// Note: if the function is not assigned to the environment before the statements in the
			// body are
			// processed, then any references to the function inside the body will cause a JSObject
			// to be created
			// implicitly. Since those assignments have a higher starting offset than the function's
			// starting offset,
			// the function's real declaration would no longer be visible.
			if (reference != null)
			{
				envLoader.putPropertyValue(reference, func);
			}
			else if (functionName != null)
			{
				envLoader.replaceFunctionDeclaration(lexeme.getStartingOffset(), parentScope, functionName, func);
			}

			// save the scope's starting offset
			// int startScopeOffset = lCurly.getStartingOffset();

			// Consume the lCurly, and then let the statement consumer have it (it will stop when it
			// hits the first
			// unbalanced rCurly)
			index = skipWhitespace(index + 1);
			LexemeConsumerHelper.LexemeConsumerResult result = consumeStatements(index, bodyScope);

			// leave the final index at the function's rCurly (which was the stopping point of the
			// statement consumer
			index = result.endIndex;

			// save the scope's ending offset
			index = getJSLexeme(index);
			Lexeme offsetLx = lastLexeme;
			int endScopeOffset = offsetLx.getEndingOffset();

			// return the function result
			// [RD]changed this to start after the word function so the name and args are in the
			// function scope
			return new JSFunctionConsumerResult(result.endIndex, func, functionName, functionStart, endScopeOffset);// startScopeOffset,
			// endScopeOffset);
		}

		private int advanceToLCurly(int functionIndex) throws EndOfFileException
		{
			int index = skipWhitespace(functionIndex + 1);

			// consume the function's declared name if there is one
			// Lexeme nextLexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme nextLexeme = lastLexeme;

			if (nextLexeme.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				functionName = nextLexeme.getText();
				index = this.skipWhitespace(index + 1);
			}

			// consume the functions arguments
			// nextLexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			nextLexeme = lastLexeme;

			if (nextLexeme.typeIndex == JSTokenTypes.LPAREN)
			{
				index = skipWhitespace(index + 1);

				// nextLexeme = getJSLexeme(index);
				index = getJSLexeme(index);
				nextLexeme = lastLexeme;

				while (nextLexeme.typeIndex != JSTokenTypes.RPAREN)
				{
					if (nextLexeme.typeIndex == JSTokenTypes.IDENTIFIER)
					{
						functionArgs.add(nextLexeme);
					}
					else if (nextLexeme.typeIndex != JSTokenTypes.COMMA)
					{
						return index; // an invalid argument token was hit, so abort
					}
					index = skipWhitespace(index + 1);

					// nextLexeme = getJSLexeme(index);
					index = getJSLexeme(index);
					nextLexeme = lastLexeme;
				}
				// advance to the next lexeme after the rParen (should be the lcurly...)
				index = skipWhitespace(index + 1);

				// nextLexeme = getJSLexeme(index);
				index = getJSLexeme(index);
				nextLexeme = lastLexeme;
			}

			// the index should now be at the LCURLY, if its not this is still the correct place to
			// stop
			if (nextLexeme.typeIndex == JSTokenTypes.LCURLY)
			{
				return index;
			}
			return index;
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSFunctionConsumerResult extends LexemeConsumerResult
	{
		String name;
		JSFunction function;
		int scopeStartingOffset;
		int scopeEndingOffset;

		/**
		 * JSFunctionConsumerResult
		 * 
		 * @param endIndex
		 * @param function
		 * @param name
		 * @param scopeStartingOffset
		 * @param scopeEndingOffset
		 */
		JSFunctionConsumerResult(int endIndex, JSFunction function, String name, int scopeStartingOffset,
				int scopeEndingOffset)
		{
			super(endIndex, function);
			this.name = name;
			this.function = function;
			this.scopeStartingOffset = scopeStartingOffset;
			this.scopeEndingOffset = scopeEndingOffset;
		}
	}

	/**
	 * @author Spike Washburn
	 */
	class JSIdentifierConsumer extends LexemeConsumer
	{
		private IScope scope;
		private boolean isVar;
		private JSReference reference;
		IObject parentObject;

		/**
		 * JSIdentifierConsumer
		 * 
		 * @param scope
		 * @param isVar
		 */
		JSIdentifierConsumer(IScope scope, boolean isVar)
		{
			this.scope = scope;
			this.isVar = isVar;
			parentObject = scope;
		}

		JSIdentifierConsumer(IScope scope, IObject parentObject)
		{
			this.scope = scope;
			this.isVar = false;
			this.parentObject = parentObject;
		}

		/**
		 * @see com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumer#consume(int)
		 */
		public LexemeConsumerResult consume(int startIndex) throws EndOfFileException
		{
			// int index = skipWhitespace(startIndex);
			int index = startIndex;
			// Lexeme lexeme = getJSLexeme(index);
			index = getJSLexeme(index);
			Lexeme lexeme = lastLexeme;

			boolean isLocalVar = isVar || lexeme.typeIndex == JSTokenTypes.THIS;
			while (lexeme != null && index != -1)
			{
				if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER || lexeme.typeIndex == JSTokenTypes.THIS)
				{
					String propertyName = lexeme.getText();

					// if there are brackets in name, skip over them
					// TODO: support nexting
					int nextIndex = skipWhitespace(index + 1);

					// Lexeme nextLexeme = getJSLexeme(nextIndex);
					nextIndex = getJSLexeme(nextIndex);
					Lexeme nextLexeme = lastLexeme;

					if (nextLexeme.typeIndex == JSTokenTypes.LBRACKET)
					{
						int bracketDepth = 1;
						while (bracketDepth > 0 && nextLexeme != null)
						{
							nextIndex = skipWhitespace(nextIndex + 1);

							// nextLexeme = getJSLexeme(nextIndex);
							nextIndex = getJSLexeme(nextIndex);
							nextLexeme = lastLexeme;

							if (nextLexeme.typeIndex == JSTokenTypes.LBRACKET)
							{
								bracketDepth++;
							}
							else if (nextLexeme.typeIndex == JSTokenTypes.RBRACKET)
							{
								bracketDepth--;
							}
						}
						index = nextIndex;

						// update the name to include [] so that there's a place to hang properties
						// referenced via the
						// brackets.
						propertyName = propertyName + "[]"; //$NON-NLS-1$
					}

					reference = new JSReference(parentObject, propertyName, isLocalVar);
					IObject parentTemp = parentObject;
					if (parentObject instanceof IScope)
					{
						parentObject = envLoader.addVariable(scope, propertyName, lexeme.getStartingOffset(),
								isLocalVar);
						isLocalVar = false; // the variable is no longer a var since the next parent
											// is an object
					}
					else
					{
						parentObject = envLoader.addProperty(scope, parentObject, propertyName, lexeme
								.getStartingOffset());
					}

					// look for special 'this' case and reset parent obj if found
					if (propertyName.equals("this") && parentTemp instanceof IScope) //$NON-NLS-1$
					{
						IFunction fn = ((IScope) parentTemp).getEnclosingFunction();
						if (fn instanceof JSFunction)
						{
							JSObject guessedPrototype = ((JSFunction) fn).getGuessedMemberObject();
							if (guessedPrototype != null)
							{
								parentObject = guessedPrototype;// envLoader.addProperty(fn.getBodyScope(),
								// guessedPrototype, propertyName,
								// lexeme.getStartingOffset());
							}
						}
					}
				}
				else if (lexeme.typeIndex != JSTokenTypes.DOT)
				{
					// the current token is not part of the property name, so go back one and return
					index = index - 1;
					break;
				}
				index++;
				if (llSize > index)
				{
					// lexeme = getJSLexeme(index);
					index = getJSLexeme(index);
					lexeme = lastLexeme;
				}
				else
				{
					lexeme = null;
				}
			}
			return new LexemeConsumerResult(index, reference);
		}
	}

	private int lastIndex = -1;
	private Lexeme lastLexeme = null;
	private Stack lastObjectKind = new Stack();
	private final String OBJECT_LITERAL = "__objectLiteral__"; //$NON-NLS-1$
	private final String FUNCTION = "__function__"; //$NON-NLS-1$

	private int getJSLexeme(int index)
	{
		if (index == lastIndex)
		{
			return index;
		}
		lastLexeme = lexemeList.get(index);
		if (!lastLexeme.getLanguage().equals(JSMimeType.MimeType))
		{
			// lx = null;
			while (++index < llSize)
			{
				lastLexeme = lexemeList.get(index);
				if (lastLexeme.getLanguage().equals(JSMimeType.MimeType))
				{
					break;
				}
			}
		}
		lastIndex = index;
		return lastIndex;
		// return lastLexeme;
	}

	// ******************************
	// * helper methods
	// ******************************
	private static String NOT_AN_IDENTIFIER = "NOT_AN_IDENTIFIER"; //$NON-NLS-1$

	private static String getNameHash(int position, LexemeList lexemes)
	{
		String name = ""; //$NON-NLS-1$
		int parenCount = 0;
		int lexLen = lexemes.size();

		// backtrack over lexemes to find name - this can include parens and
		// dots and commas, but no args - eg - "Math.fn(,,).tostring()"
		while (position < lexLen)
		{
			Lexeme curLexeme = lexemes.get(position);

			// reset name unless in a new stmt.
			if (name.equals(NOT_AN_IDENTIFIER))
			{
				name = ""; //$NON-NLS-1$
			}

			// now continue to add to string
			switch (curLexeme.typeIndex)
			{
				case JSTokenTypes.LPAREN:

					int startParenCount = parenCount;
					name += "("; //$NON-NLS-1$
					parenCount++;
					while (++position < lexLen)
					{
						Lexeme lx = lexemes.get(position);
						if (lx.typeIndex == JSTokenTypes.LPAREN)
						{
							parenCount++;
						}
						else if (lx.typeIndex == JSTokenTypes.RPAREN)
						{
							parenCount--;
						}
						if (startParenCount == parenCount)
						{
							name += ")"; //$NON-NLS-1$
							break;
						}
					}
					break;

				case JSTokenTypes.RPAREN:
					name += ")"; //$NON-NLS-1$
					parenCount--;
					// if(parenCount == 0) // inside parens ( like "if(" )
					// {
					// if(position < lexLen)
					// {
					// Lexeme nextLex =lexemes.get(position + 1);
					// if(nextLex.getCategoryIndex() == JSTokenCategories.KEYWORD &&
					// nextLex.typeIndex !=
					// JSTokenTypes.TYPEOF)
					// {
					// return name;
					// }
					// else
					// {
					// position = 0;
					// break;
					// }
					// }
					// }
					break;

				case JSTokenTypes.IDENTIFIER:
					name += curLexeme.getText();
					break;

				// case JSTokenTypes.COMMA:
				case JSTokenTypes.DOT:
					name += curLexeme.getText();
					break;

				case JSTokenTypes.WHITESPACE:
					break;

				default:
					position = lexLen; // end backtrack loop
					break;

			}

			if (curLexeme.isAfterEOL())
			{
				break;
			}
			position++;
		}
		return name;
	}

	private static String lookupReturnStringFromHash(String fullname, IScope scope, Environment environment)
	{
		if (fullname.equals(NOT_AN_IDENTIFIER))
		{
			return null;
		}
		if (fullname.length() == 0)
		{
			return null;
		}
		String result = ""; //$NON-NLS-1$

		// look up object of base part
		int lastDot = fullname.lastIndexOf("."); //$NON-NLS-1$
		String baseName = ""; //$NON-NLS-1$
		String name = fullname;
		if (lastDot > -1)
		{
			baseName = fullname.substring(0, lastDot);
			name = fullname.substring(lastDot + 1);
		}
		IObject obj = environment.getGlobal();
		if (baseName != "") //$NON-NLS-1$
		{
			obj = lookupReturnTypeFromNameHash(baseName, scope, environment);
		}

		if (obj == null)
		{
			return ""; //$NON-NLS-1$
		}

		boolean isInvoking = false;
		int firstParen = name.indexOf("("); //$NON-NLS-1$
		if (name.endsWith(")") && firstParen > -1) //$NON-NLS-1$
		{
			isInvoking = true;
			name = name.substring(0, firstParen);
		}

		IObject robj = obj.getPropertyValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE)
				.getInstance(environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);

		if (!isInvoking && robj instanceof IFunction)
		{
			// result = "Function";
			result = fullname;// name;
		}
		else if (robj.getDocumentation() != null && robj.getDocumentation() instanceof PropertyDocumentation)
		{
			PropertyDocumentation pdoc = ((PropertyDocumentation) robj.getDocumentation());

			if (pdoc.getReturn() != null && pdoc.getReturn().getTypes() != null)
			{
				String[] types = pdoc.getReturn().getTypes();

				if (types.length > 0)
				{
					if (pdoc.getIsInstance())
					{
						// FIXME: This is a nice hack to indicate that we need to create an instance
						// in LexemeBasedEnvironmentLoader.createInstance
						result = "+" + types[0]; //$NON-NLS-1$
					}
					else
					{
						result = types[0];
					}
				}
			}
		}
		else
		{
			name = name.toString();
		}
		return result;

	}

	private static IObject lookupReturnTypeFromNameHash(String fullname, IScope scope, Environment environment)
	{
		if (fullname.equals(NOT_AN_IDENTIFIER))
		{
			return null;
		}

		if (fullname.length() == 0)
		{
			return null;
		}
		// split the full name, then look up each segment, find return types if
		// needed as we parse the name.
		String[] names = fullname.split("\\."); //$NON-NLS-1$

		IObject obj = scope;
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			boolean isMethodCall = false;

			if (name.endsWith("()")) //$NON-NLS-1$
			{
				isMethodCall = true;
				name = name.substring(0, name.length() - 2);
			}
			if (i == 0)
			{
				if (name.equals("this") && obj instanceof IScope) //$NON-NLS-1$
				{
					IFunction enclFn = ((IScope) obj).getEnclosingFunction();
					if (enclFn != null && enclFn instanceof JSFunction)
					{
						JSFunction fn = (JSFunction) enclFn;
						IDocumentation doc = fn.getDocumentation();
						if (doc instanceof PropertyDocumentation)
						{
							PropertyDocumentation pdoc = (PropertyDocumentation) doc;
							if (pdoc instanceof FunctionDocumentation
									&& ((FunctionDocumentation) pdoc).getIsConstructor())
							{
								obj = fn.getPropertyValue("prototype", FileContextManager.CURRENT_FILE_INDEX, //$NON-NLS-1$
										Integer.MAX_VALUE);
							}
							else
							{
								String rettype = fn.getMemberOf();
								if (rettype != null && !rettype.equals("")) //$NON-NLS-1$
								{
									if (rettype.indexOf(".") > -1) //$NON-NLS-1$
									{
										obj = lookupNamespaceFromNameHash(rettype, environment);
									}
									else
									{
										obj = lookupReturnTypeFromNameHash(rettype, environment.getGlobal(),
												environment);
									}

									if (obj != null) // new fix
									{
										obj = obj.getPropertyValue("prototype", FileContextManager.CURRENT_FILE_INDEX, //$NON-NLS-1$
												Integer.MAX_VALUE);
									}
								}
							}
						}
						else
						{
							obj = fn.getGuessedMemberObject();
						}
					}
				}
				else
				{
					obj = scope.getVariableValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE)
							.getInstance(environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
				}
			}
			else
			{
				obj = obj.getPropertyValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE).getInstance(
						environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}

			if (obj == null || obj == ObjectBase.UNDEFINED)
			{
				return null;
			}

			// now we have the new object, check if we need to use the return
			// type or the declared type
			if (isMethodCall || (i == names.length - 1 && fullname.endsWith("."))) //$NON-NLS-1$
			{
				IDocumentation doc = obj.getDocumentation();
				if (doc instanceof PropertyDocumentation)
				{
					PropertyDocumentation pdoc = (PropertyDocumentation) doc;

					String[] rettypes = pdoc.getReturn().getTypes();

					if (rettypes.length > 0)
					{
						// todo: handle multiple return types in the future
						// todo: handle [optional] and ... (params) return types
						String rettype = rettypes[0];
						// method calls always return an instance, so we look on prototype
						if (rettype.indexOf(".") > -1) //$NON-NLS-1$
						{
							obj = lookupNamespaceFromNameHash(rettype, environment);
						}
						else
						{
							obj = lookupReturnTypeFromNameHash(rettype, environment.getGlobal(), environment);
						}

						if (obj != null) // new fix
						{
							obj = obj.getPropertyValue("prototype", FileContextManager.CURRENT_FILE_INDEX, //$NON-NLS-1$
									Integer.MAX_VALUE);
						}
					}
				}
				else
				{
					// no docs, eventually look at return statements here
					// for now return 'Object'
					if (obj instanceof JSFunction)
					{
						obj = ((JSFunction) obj).getGuessedMemberObject();
					}
					else
					{
						obj = environment.getGlobal().getPropertyValue("Object", FileContextManager.CURRENT_FILE_INDEX, //$NON-NLS-1$
								Integer.MAX_VALUE);

					}
				}
			}
		}
		return obj;
	}

	private static IObject lookupNamespaceFromNameHash(String fullname, Environment environment)
	{
		if (fullname.length() == 0)
		{
			return null;
		}

		// split the full name, then look up each segment, find return types if
		// needed as we parse the name.
		String[] names = fullname.split("\\."); //$NON-NLS-1$

		IScope scope = environment.getGlobal();
		IObject obj = scope;

		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			if (i == 0)
			{
				obj = scope.getVariableValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE)
						.getInstance(environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}
			else
			{
				obj = obj.getPropertyValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE).getInstance(
						environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}

			if (obj == ObjectBase.UNDEFINED)
			{
				return null;
			}
		}
		return obj;
	}

	private static IObject lookupOrCreateObject(String fullname, Environment environment)
	{
		if (fullname.length() == 0)
		{
			return environment.getGlobal();
		}
		// split the full name, then look up each segment, find return types if
		// needed as we parse the name.
		String[] names = fullname.split("\\."); //$NON-NLS-1$

		IScope scope = environment.getGlobal();
		IObject obj = scope;
		IObject prevObj = scope;

		String path = ""; //$NON-NLS-1$

		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			if (i == 0)
			{
				path = name;
				obj = scope.getVariableValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE)
						.getInstance(environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}
			else
			{
				path += "." + name; //$NON-NLS-1$
				obj = obj.getPropertyValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE).getInstance(
						environment, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}
			if (obj == ObjectBase.UNDEFINED)
			{
				Range r = new Range(0, 0);
				IObject guessedObj = new JSObject(r);
				Property p = new Property(guessedObj, FileContextManager.BUILT_IN_FILE_INDEX, 0);
				prevObj.putLocalProperty(name, p);
				obj = guessedObj;
				prevObj = guessedObj;
			}
		}
		return obj;
	}

	/**
	 * addDocHolderToEnvironment
	 * 
	 * @param id
	 * @param doc
	 * @param parseState
	 * @return Reference
	 */
	public static Reference addDocHolderToEnvironment(String id, IDocumentation doc, IParseState parseState)
	{
		Reference result = null;
		Environment environment = (Environment) JSLanguageEnvironment.getInstance().getRuntimeEnvironment();

		String[] path = (id.indexOf(".") > -1) ? id.split("\\.") : new String[] { id }; //$NON-NLS-1$ //$NON-NLS-2$

		IObject scope = environment.getGlobal();
		boolean hasDef = true;
		for (int i = 0; i < path.length; i++)
		{
			String name = path[i];

			if (scope.hasLocalProperty(name))
			{
				scope = scope.getPropertyValue(name, FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
			}
			else
			{
				hasDef = false;
				break;
			}
		}
		if (!hasDef)
		{
			IObject objBase = environment.getGlobal();
			String name = id;
			int dotLoc = id.lastIndexOf("."); //$NON-NLS-1$
			if (dotLoc > -1)
			{
				name = id.substring(dotLoc + 1);
				String basename = id.substring(0, dotLoc);
				objBase = lookupOrCreateObject(basename, environment);
			}

			if ("".equals(name)) //$NON-NLS-1$
			{
				IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.LexemeConsumerHelper_MalformedIdTag, id)); 
			}

			// [IM] Guard in case where id ends with a ".". This cause all sorts of problems to
			// break loose.
			if (objBase != null && !"".equals(name)) //$NON-NLS-1$
			{
				Range r = new Range(0, 0);
				IObject obj = new JSObject(r);
				Property p = new Property(obj, parseState.getFileIndex(), 0);
				objBase.putLocalProperty(name, p);

				// don't add these to undo list as parse
				result = new JSReference(objBase, name, false);
				// parseState.getUpdatedProperties().put(reference.getProperty(), reference);

				if (obj.getDocumentation() == null)
				{
					obj.setDocumentation(doc);
				}
			}
		}
		return result;
	}
}
