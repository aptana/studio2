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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.InstanceCreator;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.sax.AttributeSniffer;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;
import com.aptana.xml.IErrorHandler;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ColorizerReader extends ValidatingReader
{

	/**
	 * Schema resource.
	 */
	private static final String SCHEMA = "/com/aptana/ide/editors/resources/ColorizationSchema.xml"; //$NON-NLS-1$

	private static final Pattern PARSE_ERROR_LINE_NUMBER = Pattern.compile(" line (\\d+)"); //$NON-NLS-1$

	private String currentCategory;
	private IToken currentToken;
	private String mimeType;
	private Locator locator;
	private IErrorHandler errorHandler;

	/**
	 * Default token list.
	 */
	private ITokenList defaultTokenList;

	private LanguageColorizer colorizer;

	// String category name -> Map token types to tokens
	private Map<String, Map<String, IToken>> categoriesToToken;

	// String style id -> Colorization style object
	private Map<String, ColorizationStyle> styles;

	// mime types to pref ids
	private Map<String, String> ids;

	// mime type to colorization file urls
	private Map<String, URL> urls;

	// IToken token -> String category || String style id
	private Map<Object, String> waitingElements;

	// Region -> Token
	private Map<Region, IToken> regionsToToken;

	private Map<String, InstanceCreator> instanceCreators = new HashMap<String, InstanceCreator>();

	private List<IColorizerHandler> handlers = new ArrayList<IColorizerHandler>();

	/**
	 * Register colorizer
	 */
	protected boolean registerColorizer;

	/**
	 * Creates a new colorizer reader and parses all colorizations found in the directory.
	 */
	public ColorizerReader()
	{
		this(null);
	}

	/**
	 * Creates a new colorizer reader and parses all colorizations found in the directory.
	 * 
	 * @param tokenList -
	 *            token list to use.
	 */
	public ColorizerReader(ITokenList tokenList)
	{
		categoriesToToken = new HashMap<String, Map<String, IToken>>();
		styles = new HashMap<String, ColorizationStyle>();
		ids = new HashMap<String, String>();
		urls = new HashMap<String, URL>();
		waitingElements = new HashMap<Object, String>();
		regionsToToken = new HashMap<Region, IToken>();
		this.defaultTokenList = tokenList;
		// get schema for our documentation XML format
		this._schema = createSchema();
	}

	/**
	 * Loads and registers all the colorizers contributed via extension point
	 */
	public void loadExtensionPointColorizers()
	{
		registerColorizer = true;
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.COLORIZATION_EXTENSION_POINT);
		if (ep == null)
		{
			return;
		}
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			try
			{
				String pluginID = extensions[i].getContributor().getName();
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (IConfigurationElement element : elements)
				{
					if( !LanguageRegistry.isActivityEnabled(element)){
						//Do nothing because this extension should not work.
						continue;
					}
					else if (ColorizationConstants.COLORIZATION_ELEMENT.equals(element.getName()))
					{
						String colorizationFile = element.getAttribute(ColorizationConstants.FILE_ATTR);
						final String id = element.getAttribute(ColorizationConstants.ID_ATTR);
						if (colorizationFile != null && id != null && id.length() > 0)
						{
							Bundle bundle = Platform.getBundle(pluginID);
							final URL filePath = bundle.getEntry(colorizationFile);
							if (filePath != null)
							{
								InstanceCreator creator = new InstanceCreator(null, null)
								{

									public Object createInstance()
									{
										return loadColorizationFromURL(filePath, id, false);
									}

								};
								AttributeSniffer sniffer = new AttributeSniffer(ColorizationConstants.COLORIZER,
										ColorizationConstants.LANGUAGE);
								sniffer.read(filePath.openStream());
								if (sniffer.getMatchedValue() != null)
								{
									String language = sniffer.getMatchedValue();
									instanceCreators.put(language, creator);
								}
							}
						}
					}
					else if (ColorizationConstants.HANDLER_ELEMENT.equals(element.getName()))
					{
						String handler = element.getAttribute(ColorizationConstants.CLASS_ATTR);
						if (handler != null)
						{
							try
							{
								Object obj = element.createExecutableExtension(ColorizationConstants.CLASS_ATTR);
								if (obj instanceof IColorizerHandler)
								{
									IColorizerHandler colorizerHandler = (IColorizerHandler) obj;
									handlers.add(colorizerHandler);
								}
							}
							catch (CoreException e)
							{
								IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERR_ErrorCreatingColorizerHandler,
										e);
							}
						}
					}
				}
			}
			catch (InvalidRegistryObjectException e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERROR_EXTENSION_POINT);
			}
			catch (Exception e)
			{
				// We want to catch everything so one colorizers errors doesn't affect anothers
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
			}
		}
	}

	/**
	 * @param stream
	 * @param register
	 * @return - language colorizer or null if creation failed
	 */
	public LanguageColorizer loadLanguageColorizer(InputStream stream, boolean register)
	{
		registerColorizer = register;
		colorizer = null;
		try
		{
			this.loadXML(stream);
		}
		catch (Exception e)
		{
			colorizer = null;
		}
		return colorizer;
	}

	/**
	 * Imports a colorization and saves it to preference and global registry.
	 * 
	 * @param file -
	 *            imported colorization
	 * @param mimeType
	 */
	public void importColorization(File file, String mimeType)
	{
		InputStream stream = null;
		try
		{
			String id = ids.get(mimeType);
			if (id != null)
			{
				IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
				String content = store.getString(id);
				stream = file.toURL().openStream();
				this.loadXML(stream);
				if (mimeType != null)
				{
					store.setValue(id, content);
				}
			}
		}
		catch (Exception e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERROR_IMPORTING);
		}
		finally
		{
			try
			{
				if (stream != null)
				{
					stream.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Imports a colorization and returns it as a language colorizer.
	 * 
	 * @param file -
	 *            imported colorization
	 * @return imported language colorizer.
	 */
	public LanguageColorizer importColorization(File file)
	{
		InputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			registerColorizer = false;
			LanguageColorizer colorizer = this.loadXML(stream);
			return colorizer;
		}
		catch (Exception e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERROR_IMPORTING);
		}
		finally
		{
			try
			{
				if (stream != null)
				{
					stream.close();
				}
			}
			catch (IOException e)
			{
			}
		}

		return null;
	}

	/**
	 * Loads a colorization from the preference store
	 * 
	 * @param id
	 * @param register
	 * @return - language colorizer
	 */
	public LanguageColorizer loadColorization(String id, boolean register)
	{
		registerColorizer = register;
		colorizer = null;
		mimeType = null;
		try
		{
			IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			String content = store.getString(id);
			InputStream stream = null;
			if (content.length() > 0)
			{
				stream = new ByteArrayInputStream(content.getBytes());
				this.loadXML(stream);
				if (mimeType != null)
				{
					setPreferenceId(mimeType, id);
					store.setValue(id, content);
				}
			}

		}
		catch (Exception e)
		{
			colorizer = null;
		}
		return colorizer;
	}

	/**
	 * Loads a colorization file into the language registry
	 * 
	 * @param url
	 * @param id
	 * @param useDefault
	 * @return - language colorizer
	 */
	public LanguageColorizer loadColorizationFromURL(URL url, String id, boolean useDefault)
	{
		InputStream stream = null;
		colorizer = null;
		mimeType = null;
		try
		{
			IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
			String content = store.getString(id);
			if (content.length() > 0 && !useDefault)
			{
				stream = new ByteArrayInputStream(content.getBytes());
			}
			else
			{
				stream = url.openStream();
			}
			this.loadXML(stream);
			if (mimeType != null)
			{
				setPreferenceId(mimeType, id);
				urls.put(mimeType, url);
				store.setValue(id, content);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
					Messages.ColorizerReader_ERROR_LOADING, id));
		}
		finally
		{
			try
			{
				if (stream != null)
				{
					stream.close();
				}
			}
			catch (IOException e)
			{
			}
		}
		return colorizer;
	}

	/**
	 * Loads from XML.
	 * 
	 * @param stream -
	 *            stream.
	 * @return loaded colorizer.
	 */
	private LanguageColorizer loadXML(InputStream stream)
	{
		try
		{
			// create a new SAX factory class
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			SAXParser saxParser = null;
			// parse the XML file
			saxParser = factory.newSAXParser();
			saxParser.parse(stream, this);
			return colorizer;
		}
		catch (Exception e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERR_ErrorParsingColorizationFile, e);
		}
		return null;
	}

	/**
	 * Get preference id for mime type
	 * 
	 * @param mimeType
	 * @return id
	 */
	public String getPreferenceId(String mimeType)
	{
		return ids.get(mimeType);
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	/**
	 * sendWarning
	 * 
	 * @param message
	 */
	private void sendWarning(String message)
	{
		if (this.errorHandler != null)
		{
			int line = this.locator.getLineNumber();
			int column = this.locator.getColumnNumber();
			if (line == -1)
			{
				Matcher m = PARSE_ERROR_LINE_NUMBER.matcher(message);

				if (m.find())
				{
					line = Integer.parseInt(m.group(1));
				}
				else
				{
					line = 0;
				}
			}
			this.errorHandler.handleWarning(line, column, message);
		}
	}

	/**
	 * setErrorHandler
	 * 
	 * @param errorHandler
	 */
	public void setErrorHandler(IErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	/**
	 * sendError
	 * 
	 * @param message
	 */
	private void sendError(String message)
	{
		if (this.errorHandler != null)
		{
			int line = this.locator.getLineNumber();
			int column = this.locator.getColumnNumber();
			if (line == -1)
			{
				Matcher m = PARSE_ERROR_LINE_NUMBER.matcher(message);

				if (m.find())
				{
					line = Integer.parseInt(m.group(1));
				}
				else
				{
					line = 0;
				}
			}
			this.errorHandler.handleError(line, column, message);
		}
	}

	/**
	 * Parses the color format of the colorization file
	 * 
	 * @param value -
	 *            String color value
	 * @return - RGB object of parsed color string
	 */
	private RGB parseRGB(String value)
	{
		String original = value;
		RGB rgb = new RGB(0, 0, 0);
		try
		{
			value = value.trim();
			if (value.startsWith("rgb")) //$NON-NLS-1$
			{
				value = value.substring(4, value.length());
				value = value.substring(0, value.length() - 1);
				String[] rgbs = value.split(","); //$NON-NLS-1$
				if (rgbs.length >= 3)
				{
					try
					{
						rgb.red = Integer.parseInt(rgbs[0].trim());
					}
					catch (NumberFormatException e)
					{
						sendError(Messages.ColorizerReader_ERR_InvalidRedValueForRGBColor + original + "\""); //$NON-NLS-1$
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
								Messages.ColorizerReader_ERROR_PARSING_COLOR, e);
					}
					try
					{
						rgb.green = Integer.parseInt(rgbs[1].trim());
					}
					catch (NumberFormatException e)
					{
						sendError(Messages.ColorizerReader_ERR_InvalidGreenValueForRGBColor + original + "\""); //$NON-NLS-1$
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
								Messages.ColorizerReader_ERROR_PARSING_COLOR, e);
					}
					try
					{
						rgb.blue = Integer.parseInt(rgbs[2].trim());
					}
					catch (NumberFormatException e)
					{
						sendError(Messages.ColorizerReader_ERR_InvalidBlueValueForRGBColor + original + "\""); //$NON-NLS-1$
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
								Messages.ColorizerReader_ERROR_PARSING_COLOR, e);
					}
				}
			}
			else if (value.startsWith("COLOR")) //$NON-NLS-1$
			{
				Class<?> c = SWT.class;
				Field f = c.getField(value);
				final int result = f.getInt(c);
				final Display display = Display.getDefault();
				
				if (display != null)
				{
					class ColorResult
					{
						Color color = null;
					}
					final ColorResult colorResult = new ColorResult();
					
					display.syncExec(new Runnable()
					{
						public void run()
						{
							colorResult.color = display.getSystemColor(result);
						}
					});
					
					if (colorResult.color != null)
					{
						rgb = colorResult.color.getRGB();
					}
				}
			}
			else
			{
				sendError(Messages.ColorizerReader_ERR_InvalidRGBValue + original + "\""); //$NON-NLS-1$
			}
		}
		catch (Exception e)
		{
			sendError(Messages.ColorizerReader_ERR_InvalidRGBValue + original + Messages.ColorizerReader_ERR_InvalidRGBValueSuffix);
		}

		return rgb;
	}

	/**
	 * Groups the token of the token list
	 * 
	 * @param tokenList -
	 *            token list
	 */
	private void groupTokens(ITokenList tokenList)
	{
		categoriesToToken = new HashMap<String, Map<String, IToken>>();
		if (tokenList == null)
		{
			return;
		}
		for (int i = 0; i < tokenList.size(); i++)
		{
			IToken curr = tokenList.get(i);
			Map<String, IToken> types = null;
			if (categoriesToToken.containsKey(curr.getCategory()))
			{
				types = categoriesToToken.get(curr.getCategory());
			}
			else
			{
				types = new HashMap<String, IToken>();
				categoriesToToken.put(curr.getCategory(), types);
				ColorizationStyle originalColorStyle = new ColorizationStyle();
				originalColorStyle.setForegroundColor(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 0)));
				originalColorStyle.setName(curr.getCategory());
				CategoryColorizer catColorizer = new CategoryColorizer();
				catColorizer.setName(curr.getCategory());
				catColorizer.setStyle(originalColorStyle);
				colorizer.addCategoryColorizer(catColorizer);
			}
			types.put(curr.getType(), curr);
		}
	}

	/**
	 * Gets the token corresponding to the parameters
	 * 
	 * @param category -
	 *            name of category
	 * @param type -
	 *            name of token
	 * @return - token object
	 */
	private IToken getToken(String category, String type)
	{
		if (categoriesToToken.containsKey(category))
		{
			Map<String, IToken> tokens = categoriesToToken.get(category);
			if (tokens.containsKey(type))
			{
				return tokens.get(type);
			}
		}
		return null;
	}

	private boolean containsCategory(String category)
	{
		return categoriesToToken.containsKey(category);
	}

	/**
	 * Creates the token colorizer for the parameters
	 * 
	 * @param lc -
	 *            language colorizer
	 * @param token -
	 *            token to colorizer
	 * @param style -
	 *            name of style
	 */
	private void styleToken(LanguageColorizer lc, IToken token, String style)
	{
		if (styles.containsKey(style))
		{
			ColorizationStyle originalColorStyle = styles.get(style);
			ColorizationStyle newColorStyle = new ColorizationStyle(originalColorStyle);
			newColorStyle.setName(token.getCategory() + "_" + token.getType()); //$NON-NLS-1$
			TokenColorizer tc = lc.getTokenColorizer(token);
			if (tc == null)
			{
				tc = new TokenColorizer();
				tc.setToken(token);
				lc.addTokenColorizer(tc);
			}
			tc.setBaseColorization(newColorStyle);
		}
		else
		{
			TokenColorizer tc = new TokenColorizer();
			tc.setToken(token);
			ColorizationStyle newColorStyle = new ColorizationStyle();
			newColorStyle.setName(token.getCategory() + "_" + token.getType()); //$NON-NLS-1$
			newColorStyle.setForegroundColor(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 0)));
			tc.setBaseColorization(newColorStyle);
			lc.addTokenColorizer(tc);
			waitingElements.put(token, style);
		}
	}

	private void styleCategory(LanguageColorizer lc, String category, String style)
	{
		if (styles.containsKey(style))
		{
			ColorizationStyle originalColorStyle = styles.get(style);
			ColorizationStyle newColorStyle = new ColorizationStyle(originalColorStyle);
			newColorStyle.setName(category);
			CategoryColorizer catColorizer = new CategoryColorizer();
			catColorizer.setName(category);
			catColorizer.setStyle(newColorStyle);
			lc.addCategoryColorizer(catColorizer);
		}
		else
		{
			waitingElements.put(category, style);
		}
	}

	/**
	 * @param colorizer
	 * @param region
	 * @param style
	 */
	private void styleRegion(LanguageColorizer lc, IToken token, Region region, String style)
	{
		if (styles.containsKey(style))
		{
			ColorizationStyle originalColorStyle = styles.get(style);
			ColorizationStyle newColorStyle = new ColorizationStyle(originalColorStyle);
			newColorStyle.setName(token.getCategory() + "_" + token.getType() + "_" + region.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			region.setStyle(newColorStyle);
			TokenColorizer tc = lc.getTokenColorizer(token);
			tc.addColorization(region);
		}
		else
		{
			waitingElements.put(region, style);
		}
	}

	/**
	 * Enter the colorizer element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterColorizer(String ns, String name, String qname, Attributes attributes)
	{
		styles.clear();
		waitingElements.clear();
		regionsToToken.clear();
		categoriesToToken.clear();
		mimeType = attributes.getValue(ColorizationConstants.LANGUAGE_ATTR);
		colorizer = new LanguageColorizer(mimeType);

		// Add handlers
		for (int i = 0; i < handlers.size(); i++)
		{
			IColorizerHandler handler = handlers.get(i);
			if (handler != null && mimeType.equals(handler.getLanguage()))
			{
				colorizer.addHandler(handler);
			}
		}

		groupTokens(getDefaultTokenList());
		String backgroundColor = attributes.getValue(ColorizationConstants.BACKGROUND_ATTR);
		String selectionFgColor = attributes.getValue(ColorizationConstants.SELECTIONFOREGROUND_ATTR);
		String selectionBgColor = attributes.getValue(ColorizationConstants.SELECTIONBACKGROUND_ATTR);
		String caretColor = attributes.getValue(ColorizationConstants.CARETCOLOR_ATTR);
		String lineHighlightColor = attributes.getValue(ColorizationConstants.LINEHIGHLIGHT_ATTR);
		String foldingBgColor = attributes.getValue(ColorizationConstants.FOLDING_BACKGROUND_ATTR);
		String foldingFgColor = attributes.getValue(ColorizationConstants.FOLDING_FOREGROUND_ATTR);
		if (backgroundColor != null && selectionFgColor != null && selectionBgColor != null && caretColor != null
				&& lineHighlightColor != null)
		{
			RGB bgColor = parseRGB(backgroundColor);
			colorizer.setBackground(UnifiedColorManager.getInstance().getColor(bgColor));
			RGB selectionFg = parseRGB(selectionFgColor);
			colorizer.setSelectionForeground(UnifiedColorManager.getInstance().getColor(selectionFg));
			RGB selectionBg = parseRGB(selectionBgColor);
			colorizer.setSelectionBackground(UnifiedColorManager.getInstance().getColor(selectionBg));
			RGB cColor = parseRGB(caretColor);
			colorizer.setCaretColor(UnifiedColorManager.getInstance().getColor(cColor));
			RGB lineColor = parseRGB(lineHighlightColor);
			colorizer.setLineHighlightColor(UnifiedColorManager.getInstance().getColor(lineColor));
			if (foldingBgColor != null && foldingFgColor != null)
			{
				RGB foldingFg = parseRGB(foldingFgColor);
				colorizer.setFoldingFg(UnifiedColorManager.getInstance().getColor(foldingFg));
				RGB foldingBg = parseRGB(foldingBgColor);
				colorizer.setFoldingBg(UnifiedColorManager.getInstance().getColor(foldingBg));
			}
		}
	}

	/**
	 * Exit the colorizer element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitColorizer(String ns, String name, String qname)
	{
		styles.clear();
		if (waitingElements.isEmpty() && registerColorizer)
		{
			LanguageRegistry.registerLanguageColorizer(mimeType, colorizer);
		}
		waitingElements.clear();
		regionsToToken.clear();
		categoriesToToken.clear();
	}

	/**
	 * Enter the category element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterCategory(String ns, String name, String qname, Attributes attributes)
	{
		currentCategory = attributes.getValue(ColorizationConstants.NAME_ATTR);
		String style = attributes.getValue(ColorizationConstants.STYLE_ATTR);
		if (containsCategory(currentCategory))
		{
			styleCategory(colorizer, currentCategory, style);
		}
		else
		{
			currentCategory = null;
		}
	}

	/**
	 * Exit the category element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitCategory(String ns, String name, String qname)
	{
		currentCategory = null;
	}

	/**
	 * Enter the token element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterToken(String ns, String name, String qname, Attributes attributes)
	{
		String type = attributes.getValue(ColorizationConstants.TYPE_ATTR);
		String category = currentCategory == null ? attributes.getValue(ColorizationConstants.CATEGORY_ATTR)
				: currentCategory;
		if (category != null)
		{
			String style = attributes.getValue(ColorizationConstants.STYLE_ATTR);
			IToken token = getToken(category, type);
			if (token != null)
			{
				currentToken = token;
				styleToken(colorizer, token, style);
			}
		}
	}

	/**
	 * Exit the token element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitToken(String ns, String name, String qname)
	{
		currentToken = null;
	}

	/**
	 * Enter the style element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterStyle(String ns, String name, String qname, Attributes attributes)
	{
		ColorizationStyle style = new ColorizationStyle();
		String id = attributes.getValue(ColorizationConstants.ID_ATTR);
		String fg = attributes.getValue(ColorizationConstants.FOREGROUND_ATTR);
		String bg = attributes.getValue(ColorizationConstants.BACKGROUND_ATTR);
		String bold = attributes.getValue(ColorizationConstants.FONTWEIGHT_ATTR);
		String italic = attributes.getValue(ColorizationConstants.FONTSTYLE_ATTR);
		String ul = attributes.getValue(ColorizationConstants.TEXTDECORATION_ATTR);
		if (styles.containsKey(id))
		{
			sendWarning(Messages.ColorizerReader_WRN_IgnoringDeclarationOfDuplicateStyle + id + "\""); //$NON-NLS-1$
			return;
		}
		style.setName(id);
		RGB rgb = parseRGB(fg);
		style.setForegroundColor(UnifiedColorManager.getInstance().getColor(rgb));
		if (bg != null)
		{
			rgb = parseRGB(bg);
			style.setBackgroundColor(UnifiedColorManager.getInstance().getColor(rgb));
		}
		if ("bold".equalsIgnoreCase(bold)) //$NON-NLS-1$
		{
			style.setBold(true);
		}
		if ("italic".equalsIgnoreCase(italic)) //$NON-NLS-1$
		{
			style.setItalic(true);
		}
		if ("underline".equalsIgnoreCase(ul)) //$NON-NLS-1$
		{
			style.setUnderline(true);
		}
		styles.put(id, style);
		if (waitingElements.containsValue(id))
		{
			Iterator<Object> waitingList = waitingElements.keySet().iterator();
			while (waitingList.hasNext())
			{
				Object curr = waitingList.next();
				if (id.equals(waitingElements.get(curr)))
				{
					if (curr instanceof IToken)
					{
						waitingList.remove();
						styleToken(colorizer, (IToken) curr, id);
					}
					else if (curr instanceof String)
					{
						waitingList.remove();
						styleCategory(colorizer, (String) curr, id);
					}
					else if (curr instanceof Region)
					{
						waitingList.remove();
						IToken token = regionsToToken.get(curr);
						styleRegion(colorizer, token, (Region) curr, id);
					}
				}
			}
		}
	}

	/**
	 * Enter the region element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterRegion(String ns, String name, String qname, Attributes attributes)
	{
		String style = attributes.getValue(ColorizationConstants.STYLE_ATTR);
		String offset = attributes.getValue(ColorizationConstants.OFFSET_ATTR).trim();
		String length = attributes.getValue(ColorizationConstants.LENGTH_ATTR).trim();
		String id = attributes.getValue(ColorizationConstants.NAME_ATTR);
		int offsetInt;
		int lengthInt;
		boolean offsetRelative = false;
		boolean lengthRelative = false;
		if (currentToken != null)
		{
			try
			{
				if (offset.startsWith(ColorizationConstants.LENGTH_KEYWORD))
				{
					offsetInt = Integer.parseInt(offset.substring(ColorizationConstants.LENGTH_KEYWORD.length(), offset
							.length()));
					offsetRelative = true;
				}
				else
				{
					offsetInt = Integer.parseInt(offset);
				}
			}
			catch (NumberFormatException e)
			{
				this.sendError(Messages.ColorizerReader_ERR_NFEInLengthRegion + id);
				return;
			}
			try
			{
				if (length.startsWith(ColorizationConstants.LENGTH_KEYWORD))
				{
					lengthInt = Integer.parseInt(length.substring(ColorizationConstants.LENGTH_KEYWORD.length(), length
							.length()));
					lengthRelative = true;
				}
				else
				{
					lengthInt = Integer.parseInt(length);
				}
				Region region = new Region(offsetInt, offsetRelative, lengthInt, lengthRelative, null);
				region.setLengthString(length);
				region.setOffsetString(offset);
				region.setName(id);
				regionsToToken.put(region, currentToken);
				styleRegion(colorizer, currentToken, region, style);
			}
			catch (NumberFormatException e)
			{
				this.sendError(Messages.ColorizerReader_ERR_NFEInOffsetRegion + id);
				return;
			}
		}
	}

	/**
	 * Exit the region element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitRegion(String ns, String name, String qname)
	{

	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] buffer, int offset, int length)
	{
		// Does nothing, no text nodes in this xml format
	}

	/**
	 * @param language
	 */
	public void restoreDefault(String language)
	{
		URL url = urls.get(language);
		String id = ids.get(language);
		if (url != null && id != null)
		{
			loadColorizationFromURL(url, id, true);
		}
	}

	/**
	 * @param language
	 * @param preferenceId
	 */
	public void setPreferenceId(String language, String preferenceId)
	{
		ids.put(language, preferenceId);
	}

	/**
	 * Gets the colorizer instance creator for a language
	 * 
	 * @param language
	 * @return - instance creator
	 */
	public InstanceCreator getInstanceCreator(String language)
	{
		return instanceCreators.get(language);
	}

	/**
	 * Creates schema.
	 * 
	 * @return - schema
	 */
	protected Schema createSchema()
	{
		InputStream schemaStream = ColorizerReader.class.getResourceAsStream(SCHEMA); //$NON-NLS-1$
		try
		{
			// create the schema
			Schema schema = SchemaBuilder.fromXML(schemaStream, this);
			return schema;
		}
		catch (SchemaInitializationException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERROR_LOADING_SCHEMA);
		}
		finally
		{
			// close the input stream
			try
			{
				schemaStream.close();
			}
			catch (IOException e)
			{
			}
		}

		return null;
	}

	/**
	 * Gets default token list. Searches in Language registry if default list is not defined.
	 * 
	 * @return default token list.
	 */
	private ITokenList getDefaultTokenList()
	{
		if (defaultTokenList != null)
		{
			return defaultTokenList;
		}

		return LanguageRegistry.getTokenList(mimeType);
	}
}
