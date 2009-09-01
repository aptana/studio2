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
package com.aptana.ide.editors.unified.colorizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ColorizerWriter
{

	/**
	 * XML elements and attributes
	 */
	public static final String XMLNS_ATTR = "xmlns"; //$NON-NLS-1$

	/**
	 * NAMESPACE_ATTR
	 */
	public static final String NAMESPACE_ATTR = "http://www.aptana.com/2007/colorizer/1.0"; //$NON-NLS-1$

	/**
	 * Contructor for colorizer writer
	 */
	public ColorizerWriter()
	{
		// Does nothing
	}

	/**
	 * Builds a colorization preference.
	 * 
	 * @param lc -
	 *            language colorizer.
	 * @param language -
	 *            language.
	 * @param prefID -
	 *            preference ID.
	 * @throws LexerException
	 *             IF lexer error occurs.
	 */
	public void buildColorizationPreference(LanguageColorizer lc, String language, String prefID) throws LexerException
	{
		try
		{
			Document document = buildDom(lc, language);
			// Save file
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			DOMSource domSource = new DOMSource(document);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(domSource, result);
			IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			store.setValue(prefID, writer.toString());
		}
		catch (Exception e)
		{
			throw new LexerException(Messages.ColorizerWriter_ERROR_SAVING, e);
		}
	}

	/**
	 * Builds colorization file.
	 * 
	 * @param lc -
	 *            language colorizer.
	 * @param language -
	 *            language.
	 * @param file -
	 *            file to write to.
	 * @throws LexerException
	 *             IF lexer error occurs.
	 */
	public void buildColorizationFile(LanguageColorizer lc, String language, File file) throws LexerException
	{
		try
		{
			Document document = buildDom(lc, language);
			// Save file
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			DOMSource domSource = new DOMSource(document);
			File output = file;
			if (output != null)
			{
				try
				{
					FileOutputStream stream = new FileOutputStream(output);
					StreamResult result = new StreamResult(stream);
					transformer.transform(domSource, result);
					stream.close();
				}
				catch (TransformerException e)
				{
					throw new LexerException(Messages.ColorizerWriter_ERROR_SAVING, e);
				}
			}
		}
		catch (Exception e)
		{
			throw new LexerException(Messages.ColorizerWriter_ERROR_SAVING, e);
		}
	}

	/**
	 * Builds a dom from a colorizer
	 * 
	 * @param lc
	 * @param language
	 * @return - DOM document
	 * @throws ParserConfigurationException
	 */
	protected Document buildDom(LanguageColorizer lc, String language) throws ParserConfigurationException
	{
		Collection tokenColorizers = lc.getTokenColorizers();
		Collection categoryColorizers = lc.getCategoryColorizers();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		// Create document
		Element root = document.createElement(ColorizationConstants.COLORIZER); //$NON-NLS-1$
		document.appendChild(root);
		root.setAttribute(ColorizerWriter.XMLNS_ATTR, ColorizerWriter.NAMESPACE_ATTR);
		root.setAttribute(ColorizationConstants.LANGUAGE_ATTR, language);

		// Set editor properties
		if (lc.getBackground() != null)
		{
			String editorBG = createRGBString(lc.getBackground());
			root.setAttribute(ColorizationConstants.BACKGROUND_ATTR, editorBG);
		}
		if (lc.getSelectionBackground() != null)
		{
			String selectionBG = createRGBString(lc.getSelectionBackground());
			root.setAttribute(ColorizationConstants.SELECTIONBACKGROUND_ATTR, selectionBG);
		}
		if (lc.getSelectionForeground() != null)
		{
			String selectionFG = createRGBString(lc.getSelectionForeground());
			root.setAttribute(ColorizationConstants.SELECTIONFOREGROUND_ATTR, selectionFG);
		}
		if (lc.getCaretColor() != null)
		{
			String caretColor = createRGBString(lc.getCaretColor());
			root.setAttribute(ColorizationConstants.CARETCOLOR_ATTR, caretColor);
		}
		if (lc.getLineHighlightColor() != null)
		{
			String lineHighlight = createRGBString(lc.getLineHighlightColor());
			root.setAttribute(ColorizationConstants.LINEHIGHLIGHT_ATTR, lineHighlight);
		}
		if (lc.getFoldingBg() != null)
		{
			String foldingBg = createRGBString(lc.getFoldingBg());
			root.setAttribute(ColorizationConstants.FOLDING_BACKGROUND_ATTR, foldingBg);
		}
		if (lc.getFoldingFg() != null)
		{
			String foldingFg = createRGBString(lc.getFoldingFg());
			root.setAttribute(ColorizationConstants.FOLDING_FOREGROUND_ATTR, foldingFg);
		}

		Iterator catIter = categoryColorizers.iterator();
		while (catIter.hasNext())
		{
			CategoryColorizer cColorizer = (CategoryColorizer) catIter.next();
			String cName = cColorizer.getName();
			String cStyle = cColorizer.getStyle().getName();
			Element catElement = document.createElement(ColorizationConstants.CATEGORY_ATTR);
			catElement.setAttribute(ColorizationConstants.NAME_ATTR, cName);
			catElement.setAttribute(ColorizationConstants.STYLE_ATTR, cStyle);
			root.appendChild(catElement);
			Iterator tokens = tokenColorizers.iterator();
			addColorization(root, cColorizer.getStyle(), document);
			while (tokens.hasNext())
			{
				TokenColorizer tColorizer = (TokenColorizer) tokens.next();
				IToken token = tColorizer.getToken();
				if (cName.equals(token.getCategory()))
				{
					String type = token.getType();
					String token_style = tColorizer.getBaseColorization().getName();
					Element tokenElement = document.createElement(ColorizationConstants.TOKEN_ELEMENT);
					tokenElement.setAttribute(ColorizationConstants.TYPE_ATTR, type);
					tokenElement.setAttribute(ColorizationConstants.STYLE_ATTR, token_style);
					catElement.appendChild(tokenElement);
					addRegions(root, tokenElement, tColorizer, document);
					addColorization(root, tColorizer.getBaseColorization(), document);
				}
			}
		}
		return document;
	}

	private static String createRGBString(Color color)
	{
		return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	private static void addColorization(Element parent, ColorizationStyle style, Document document)
	{
		Color bg = style.getBackgroundColor();
		Element styleElement = document.createElement(ColorizationConstants.STYLE_ELEMENT);
		if (bg != null)
		{
			String bgColor = createRGBString(bg);
			styleElement.setAttribute(ColorizationConstants.BACKGROUND_ATTR, bgColor);
		}
		Color fg = style.getForegroundColor();
		if (fg != null)
		{
			String fgColor = createRGBString(fg);
			styleElement.setAttribute(ColorizationConstants.FOREGROUND_ATTR, fgColor);
		}
		if (style.isBold())
		{
			styleElement.setAttribute(ColorizationConstants.FONTWEIGHT_ATTR, "bold"); //$NON-NLS-1$
		}
		if (style.isItalic())
		{
			styleElement.setAttribute(ColorizationConstants.FONTSTYLE_ATTR, "italic"); //$NON-NLS-1$
		}
		if (style.isUnderline())
		{
			styleElement.setAttribute(ColorizationConstants.TEXTDECORATION_ATTR, "underline"); //$NON-NLS-1$
		}
		if (style.getName() != null)
		{
			styleElement.setAttribute(ColorizationConstants.ID_ATTR, style.getName());
			parent.appendChild(styleElement);
		}
	}

	private static void addRegions(Element root, Element parent, TokenColorizer tokenColorizer, Document document)
	{
		Iterator regions = tokenColorizer.getRegions().iterator();
		while (regions.hasNext())
		{
			Region region = (Region) regions.next();
			Element regionElement = document.createElement(ColorizationConstants.REGION_ELEMENT);
			regionElement.setAttribute(ColorizationConstants.NAME_ATTR, region.getName());
			regionElement.setAttribute(ColorizationConstants.STYLE_ATTR, region.getStyle().getName());
			regionElement.setAttribute(ColorizationConstants.OFFSET_ATTR, region.getOffsetString());
			regionElement.setAttribute(ColorizationConstants.LENGTH_ATTR, region.getLengthString());
			addColorization(root, region.getStyle(), document);
			parent.appendChild(regionElement);
		}
	}

}
