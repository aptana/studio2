/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.editors.unified.contentassist;

import java.util.ArrayList;

import com.aptana.ide.core.StringUtils;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class CodeAssistExpression {
	private String _expression;
	private String _xPath;
	private String _context;

	/**
	 * Separates the fields
	 */
	public static String FIELD_SEPARATOR = "~~~~"; //$NON-NLS-1$

	/**
	 * Separates the descriptors
	 */
	public static String DESCRIPTOR_SEPARATOR = "####"; //$NON-NLS-1$

	/**
	 * Creates a new error
	 */
	public CodeAssistExpression()
	{
	}

	/**
	 * gets the message of the error
	 * 
	 * @return String
	 */
	public String getExpression()
	{
		return _expression;
	}

	/**
	 * Gets the file name of the error
	 * 
	 * @return String
	 */
	public String getXPath()
	{
		return _xPath;
	}

	/**
	 * Gets the folder path of the error
	 * 
	 * @return String
	 */
	public String getContext()
	{
		return _context;
	}

	/**
	 * Sets the file name of the error
	 * 
	 * @param expression
	 */
	public void setExpression(String expression)
	{
		this._expression = expression;
	}

	/**
	 * Sets the folder path of the error
	 * 
	 * @param xPath
	 */
	public void setXPath(String xPath)
	{
		this._xPath = xPath;
	}

	/**
	 * Sets the error message
	 * 
	 * @param context
	 */
	public void setContext(String context)
	{
		this._context = context;
	}

	/**
	 * Returns this descriptor as a string
	 * 
	 * @return String
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		if (getContext() != null)
		{
			sb.append(getContext());
		}
		sb.append(FIELD_SEPARATOR);
		if (getExpression() != null)
		{
			sb.append(getExpression());
		}
		sb.append(FIELD_SEPARATOR);

		sb.append(getXPath());

		return sb.toString();
	}

	/**
	 * Converts the string into an error descriptor
	 * 
	 * @param values
	 */
	public void fromString(String values)
	{
		String[] vals = values.split(FIELD_SEPARATOR, 3);
		if (vals.length != 3)
		{
			throw new IllegalArgumentException(CodeAssistMessages.CodeAssistExpression_MustHaveThreeValues);
		}
		if (!StringUtils.EMPTY.equals(vals[0]))
		{
			_context = vals[0];
		}

		if (!StringUtils.EMPTY.equals(vals[1]))
		{
			_expression = vals[1];
		}

		if (!StringUtils.EMPTY.equals(vals[2]))
		{
			_xPath = vals[2];
		}
	}

	/**
	 * Returns a string consisting of all error descriptors
	 * 
	 * @param expressions
	 * @return String
	 */
	public static String serializeErrorDescriptors(CodeAssistExpression[] expressions)
	{
		ArrayList al = new ArrayList();
		for (int i = 0; i < expressions.length; i++)
		{
			CodeAssistExpression descriptor = expressions[i];
			al.add(descriptor.toString());
		}

		return StringUtils.join(DESCRIPTOR_SEPARATOR, (String[]) al.toArray(new String[0]));
	}

	/**
	 * Returns an array consisting of all error descriptors
	 * 
	 * @param expressions
	 * @return ErrorDescriptor[]
	 */
	public static CodeAssistExpression[] deserializeErrorDescriptors(String expressions)
	{
		if (expressions == null || StringUtils.EMPTY.equals(expressions))
		{
			return new CodeAssistExpression[0];
		}

		ArrayList al = new ArrayList();
		String[] errorDescriptors = expressions.split(DESCRIPTOR_SEPARATOR);
		for (int i = 0; i < errorDescriptors.length; i++)
		{
			String descriptor = errorDescriptors[i];
			CodeAssistExpression ed = new CodeAssistExpression();
			ed.fromString(descriptor);
			al.add(ed);
		}

		return (CodeAssistExpression[]) al.toArray(new CodeAssistExpression[0]);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0)
	{
		boolean result = false;

		if (arg0 instanceof CodeAssistExpression)
		{
			CodeAssistExpression s = ((CodeAssistExpression) arg0);

			result = toString().equals(s.toString());
		}
		return result;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.toString().hashCode();
	}
}
