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
package com.aptana.ide.editor.html.parsing;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.ide.editor.html.parsing.nodes.HTMLParseNodeFactory;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParseStateChild;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class HTMLParseState extends ParseStateChild
{
	/*
	 * Fields
	 */
	private static final String HTML_2_0 = "-//IETF//DTD HTML//EN"; //$NON-NLS-1$
	private static final String HTML_3_2 = "-//W3C//DTD HTML 3.2 Final//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_STRICT = "-//W3C//DTD HTML 4.01//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_TRANSITIONAL = "-//W3C//DTD HTML 4.01 Transitional//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_FRAMESET = "-//W3C//DTD HTML 4.01 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_STRICT = "-//W3C//DTD XHTML 1.0 Strict//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_TRANSITIONAL = "-//W3C//DTD XHTML 1.0 Transitional//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_FRAMESET = "-//W3C//DTD XHTML 1.0 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_1_STRICT = "-//W3C//DTD XHTML 1.1//EN"; //$NON-NLS-1$

	private static Pattern _docTypeSniffer;
	private static HashMap _docTypeIndex;
	private static HashMap _endTagInfo;

	private String _rootElement;
	private String _pubId;
	private String _system;
	private int _documentType;

	/*
	 * Properties
	 */

	/**
	 * getDocumentType
	 * 
	 * @return int;
	 */
	public int getDocumentType()
	{
		return this._documentType;
	}

	/**
	 * getRootElement
	 * 
	 * @return String
	 */
	public String getRootElement()
	{
		return this._rootElement;
	}

	/**
	 * getPubId
	 * 
	 * @return String
	 */
	public String getPubId()
	{
		return this._pubId;
	}

	/**
	 * getSystem
	 * 
	 * @return String
	 */
	public String getSystem()
	{
		return this._system;
	}

	/*
	 * Constructors
	 */

	/**
	 * static constructor
	 */
	static
	{
		_docTypeSniffer = Pattern
				.compile("<!DOCTYPE\\s+(\\S+)\\s+PUBLIC\\s+((?:'[^']+')|(?:\"[^\"]+\"))(?:\\s+((?:'[^']+')|(?:\"[^\"]+\")))?"); //$NON-NLS-1$

		_docTypeIndex = new HashMap();
		_docTypeIndex.put(HTML_2_0, new Integer(HTMLDocumentType.HTML_2_0));
		_docTypeIndex.put(HTML_3_2, new Integer(HTMLDocumentType.HTML_3_2));
		_docTypeIndex.put(HTML_4_0_1_STRICT, new Integer(HTMLDocumentType.HTML_4_0_1_STRICT));
		_docTypeIndex.put(HTML_4_0_1_TRANSITIONAL, new Integer(HTMLDocumentType.HTML_4_0_1_TRANSITIONAL));
		_docTypeIndex.put(HTML_4_0_1_FRAMESET, new Integer(HTMLDocumentType.HTML_4_0_1_FRAMESET));
		_docTypeIndex.put(XHTML_1_0_STRICT, new Integer(HTMLDocumentType.XHTML_1_0_STRICT));
		_docTypeIndex.put(XHTML_1_0_TRANSITIONAL, new Integer(HTMLDocumentType.XHTML_1_0_TRANSITIONAL));
		_docTypeIndex.put(XHTML_1_0_FRAMESET, new Integer(HTMLDocumentType.XHTML_1_0_FRAMESET));
		_docTypeIndex.put(XHTML_1_1_STRICT, new Integer(HTMLDocumentType.XHTML_1_1_STRICT));

		_endTagInfo = new HashMap();
		_endTagInfo.put("area", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("base", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("basefont", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("body", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("br", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("col", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("colgroup", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("dd", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("dt", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("frame", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("area", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("hr", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("html", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("img", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("input", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("isindex", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("li", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("link", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("meta", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("option", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("p", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("param", new Integer(HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY)); //$NON-NLS-1$
		_endTagInfo.put("tbody", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("td", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("tfoot", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("th", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("thead", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
		_endTagInfo.put("tr", new Integer(HTMLTagInfo.END_OPTIONAL)); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of HTMLParseState
	 */
	public HTMLParseState()
	{
		super(HTMLMimeType.MimeType);
	}

	/**
	 * Create a new instance of HTMLParseState
	 * 
	 * @param parent
	 *            The parent IParseState
	 */
	public HTMLParseState(IParseState parent)
	{
		super(HTMLMimeType.MimeType, parent);
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.parsing.ParseStateChild#createParseNodeFactory()
	 */
	protected IParseNodeFactory createParseNodeFactory()
	{
		return new HTMLParseNodeFactory(this);
	}

	/**
	 * getCloseTagType
	 * 
	 * @param tagName
	 * @return close tag type
	 */
	public int getCloseTagType(String tagName)
	{
		int result = HTMLTagInfo.END_REQUIRED;

		if (this._documentType < HTMLDocumentType.XHTML_1_0_STRICT)
		{
			String key = tagName.toLowerCase();

			if (_endTagInfo.containsKey(key))
			{
				result = ((Integer) _endTagInfo.get(key)).intValue();

				result = (result & HTMLTagInfo.END_MASK);
			}
		}

		return result;
	}

	/**
	 * isEmptyTagType
	 * 
	 * @param tagName
	 * @return empty tag type
	 */
	public boolean isEmptyTagType(String tagName)
	{
		boolean result = false;

		String key = tagName.toLowerCase();

		if (_endTagInfo.containsKey(key))
		{
			int flags = ((Integer) _endTagInfo.get(key)).intValue();
			result = (flags & HTMLTagInfo.EMPTY) == HTMLTagInfo.EMPTY;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.ParseStateChild#setEditState(java.lang.String, java.lang.String, int, int)
	 */
	public void setEditState(String source, String insertedSource, int offset, int removeLength)
	{
		super.setEditState(source, insertedSource, offset, removeLength);

		// assume we don't know the document type
		int documentType = HTMLDocumentType.UNKNOWN;		
		int indexOf = source.indexOf("<!DOCTYPE");//$NON-NLS-1$
		if (indexOf != -1)
		{
			Matcher match = _docTypeSniffer.matcher(source.substring(indexOf));

			if (match.find())
			{
				// grab doctype pieces
				this._rootElement = match.group(1);
				this._pubId = match.group(2);
				this._system = match.group(3);

				// strip opening and closing quotes
				this._pubId = this._pubId.substring(1, this._pubId.length() - 1);

				if (this._system != null && this._system.length() > 0)
				{
					this._system = this._system.substring(1, this._system.length() - 1);
				}

				// see if see can determine the document type
				if (this._rootElement.equals("html") || this._rootElement.equals("HTML")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if (_docTypeIndex.containsKey(this._pubId))
					{
						documentType = ((Integer) _docTypeIndex.get(this._pubId)).intValue();
					}
				}
			}
		}
		// set document type
		this._documentType = documentType;
	}
}
