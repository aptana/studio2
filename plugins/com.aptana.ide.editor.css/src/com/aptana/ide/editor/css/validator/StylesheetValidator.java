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
package com.aptana.ide.editor.css.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.css.css.StyleReport;
import org.w3c.css.css.StyleReportFactory;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.css.StyleSheetParser;
import org.w3c.css.properties.PropertiesLoader;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.Utf8Properties;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.Messages;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSParseState;
import com.aptana.ide.editor.css.parsing.CSSParser;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.editors.unified.utils.Entities;
import com.aptana.ide.editors.validator.ValidatorBase;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * @author Robin Debreuil
 * @author Kevin Lindsey 
 * @author Samir Joshi (Refactored out of CSSErrorManager)
 *
 */
public class StylesheetValidator extends ValidatorBase
{
	/**
	 * APTANA PROFILE name.
	 */
	private static final String APTANA_PROFILE = "AptanaProfile"; //$NON-NLS-1$
	
	/**
	 * Configuration file.
	 */
	private static final String CONFIG_FILE = "AptanaCSSConfig.properties"; //$NON-NLS-1$
	
	/**
	 * Profiles configuration file.
	 */
	private static final String PROFILES_CONFIG_FILE = "AptanaCSSProfiles.properties"; //$NON-NLS-1$
	
	/**
	 * Custom CSS properties
	 */
	private static final String[] CUSTOM_PROPERTIES = 
		new String[] {
		"-moz-binding", //$NON-NLS-1$
		"-moz-border-radius", //$NON-NLS-1$
		"-moz-border-radius-bottomleft", //$NON-NLS-1$
		"-moz-border-radius-bottomright", //$NON-NLS-1$
		"-moz-border-radius-topleft", //$NON-NLS-1$
		"-moz-border-radius-topright", //$NON-NLS-1$
		"-moz-border-top-colors", //$NON-NLS-1$
		"-moz-border-right-colors", //$NON-NLS-1$
		"-moz-border-bottom-colors", //$NON-NLS-1$
		"-moz-border-left-colors", //$NON-NLS-1$
		"-moz-opacity", //$NON-NLS-1$
		"-moz-outline", //$NON-NLS-1$
		"-moz-outline-color", //$NON-NLS-1$
		"-moz-outline-style", //$NON-NLS-1$
		"-moz-outline-width", //$NON-NLS-1$
		"-moz-user-focus", //$NON-NLS-1$
		"-moz-user-input", //$NON-NLS-1$
		"-moz-user-modify", //$NON-NLS-1$
		"-moz-user-select", //$NON-NLS-1$
		"-o-link", //$NON-NLS-1$
		"-o-link-source" //$NON-NLS-1$
	};
	
	/**
	 * Errors that get reported that we want to explicitly ignore
	 */
	private static final Set<String> errorsToIgnore = new HashSet<String>();
	static {
		errorsToIgnore.add("inline-table is not a display value"); //$NON-NLS-1$
	}
	
	/**
	 * Error pattern.
	 */
	private static Pattern errorPattern = Pattern.compile("<(error)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$
	
	/**
	 * Properties pattern.
	 */
	private static Pattern propertiesPattern = Pattern.compile("<([-A-Za-z0-9_:]+)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$
	
	/**
	 * Warning pattern.
	 */
	private static Pattern warningPattern = Pattern.compile("<(warning)>(.*?)</\\1>", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$
	
	/**
	 * Set of the custom properties that start with the "-" sign. Needed for speedup purpose.
	 */
	private static final Set<String> NO_MINUS_CUSTOM_PROPERTIES_SET;
	static
	{
	    //initializes custom properties set
        NO_MINUS_CUSTOM_PROPERTIES_SET = new HashSet<String>();
        for (int i = 0; i < CUSTOM_PROPERTIES.length; i++)
        {
            if (CUSTOM_PROPERTIES[i].startsWith("-")) //$NON-NLS-1$
            {
                NO_MINUS_CUSTOM_PROPERTIES_SET.add(
                        CUSTOM_PROPERTIES[i].substring(1, CUSTOM_PROPERTIES[i].length()));
            }
        }
	}
	
	/**
	 * StylesheetValidator constructor.
	 * @throws IOException IF Aptana CSS profile can not be read.
	 */
	public StylesheetValidator() throws IOException
	{
		loadAptanaCSSProfile();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider, 
									boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{
		
		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);
		
		NewLineUtils newLineUtils = new NewLineUtils(source);
		
		String patchedSource = patchCSSProperties(source, reporter, newLineUtils);

		if (patchedSource == null || patchedSource.trim().length() == 0)
		{
			return new IFileError[0];
		}
		
		String report = getReport(path, patchedSource);

		String uri = CoreUIUtils.getURI(path);
		
		if (collectErrors)
		{
//			String[] errors = getContent(errorPattern, report);
//			this.parseCSSErrors(reporter, errors, path);
			this.processErrorsInReport(report, reporter, uri, false, newLineUtils);
		}

		if (collectWarnings)
		{
//			String[] warnings = getContent(warningPattern, report);
//			this.parseCSSWarnings(reporter, warnings, path);
			this.processWarningsInReport(report, reporter, uri, false);
		}

		return reporter.getErrors();
	}
	
	/**
	 * Gets validation report.
	 * 
	 * @param path - resource path.
	 * @param sourceString - source string.
	 * @return report as string
	 */
	private String getReport(String path, String sourceString)
	{
		StyleSheetParser parser = new StyleSheetParser();
		
		ApplContext ac = new ApplContext("en"); //$NON-NLS-1$
		ac.setCssVersion(APTANA_PROFILE);

		try
		{
			ByteArrayInputStream stream = new ByteArrayInputStream(sourceString.getBytes());
			URL url = new URL("file://" + path); //$NON-NLS-1$
			
			parser.parseStyleElement(ac, stream, null, null, url, 0);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), StringUtils.format(Messages.CSSErrorManager_InvalidUL, path), e);
		}

		int warningLevel = 2;

		StyleSheet styleSheet = parser.getStyleSheet();

		styleSheet.findConflicts(ac);

		StyleReport style = StyleReportFactory.getStyleReport(ac, "Title", styleSheet, "soap12", warningLevel); //$NON-NLS-1$ //$NON-NLS-2$

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		style.print(new PrintWriter(bout));
		String report = bout.toString().replaceAll("m:", ""); //$NON-NLS-1$ //$NON-NLS-2$

		return report;
	}
	/**
	 * Process report errors.
	 *
	 * @param report - error report
	 * @param reporter - error reporter to report errors to.
	 * @param baseUri - base URI.
	 * @param includeExternal - ?
	 * @param utils - new line utils
	 */
	private void processErrorsInReport(String report, UnifiedErrorReporter reporter, 
	        String baseUri, boolean includeExternal, NewLineUtils utils)
	{
		int offset = 0;
		String elementName = "errorlist"; //$NON-NLS-1$
		String startTag = "<" + elementName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		String endTag = "</" + elementName + ">"; //$NON-NLS-1$ //$NON-NLS-2$

		while (offset < report.length())
		{
			int errorListStart = report.indexOf(startTag, offset);
			
			if (errorListStart != -1)
			{
				// advance past start tag
				errorListStart += startTag.length();
				
				// get uri
				int uriStart = report.indexOf("<uri>", errorListStart) + "<uri>".length(); //$NON-NLS-1$ //$NON-NLS-2$
				int uriEnd = report.indexOf("</uri>", uriStart); //$NON-NLS-1$
				String uri = report.substring(uriStart, uriEnd);

				// find end of this list
				int errorListEnd = report.indexOf(endTag, errorListStart);
				
				String tempPath = CoreUIUtils.getURI(uri);
				tempPath = StringUtils.urlEncodeFilename(tempPath.toCharArray());
				if(tempPath.equals(baseUri) || includeExternal)
				{
					// extract list
					String listString = report.substring(errorListStart, errorListEnd);
					
					// find errors
					String[] errors = getContent(errorPattern, listString);
			
					// add errors
					this.parseCSSErrors(reporter, errors, uri, utils);					
				}
				
				// advance past this error list
				offset = errorListEnd + endTag.length();
			}
			else
			{
				// go to end
				offset = report.length();
			}
		}
	}
	
	/**
	 * Processes report warnings.
	 *
	 * @param report - report to process.
	 * @param reporter - reporter to report warnings with.
	 */
	private void processWarningsInReport(String report, UnifiedErrorReporter reporter, String baseUri, boolean includeExternal)
	{
		int offset = 0;
		String elementName = "warninglist"; //$NON-NLS-1$
		String startTag = "<" + elementName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		String endTag = "</" + elementName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		
		while (offset < report.length())
		{
			int errorListStart = report.indexOf(startTag, offset);
			
			if (errorListStart != -1)
			{
				// advance past start tag
				errorListStart += startTag.length();
				
				// get uri
				int uriStart = report.indexOf("<uri>", errorListStart) + "<uri>".length(); //$NON-NLS-1$ //$NON-NLS-2$
				int uriEnd = report.indexOf("</uri>", uriStart); //$NON-NLS-1$
				String uri = report.substring(uriStart, uriEnd);
				
				// find end of this list
				int errorListEnd = report.indexOf(endTag, errorListStart);

				String tempPath = CoreUIUtils.getURI(uri);
				tempPath = StringUtils.urlEncodeFilename(tempPath.toCharArray());
				if(tempPath.equals(baseUri) || includeExternal)
				{
					// extract list
					String listString = report.substring(errorListStart, errorListEnd);
					
					// find errors
					String[] warnings = getContent(warningPattern, listString);
			
					// add errors
					this.parseCSSWarnings(reporter, warnings, uri);
				}
				
				// advance past this error list
				offset = errorListEnd + endTag.length();
			}
			else
			{
				// go to end
				offset = report.length();
			}
		}
	}
	/**
	 * Parses CSS errors.
	 * 
	 * @param err - error reporter.
	 * @param errors - errors.
	 * @param filename - file name.
	 * @param utils - new line utils.
	 */
	private void parseCSSErrors(UnifiedErrorReporter err, String[] errors, String filename,
	        NewLineUtils utils)
	{
		Map<String, String> map;
		for (int i = 0; i < errors.length; i++)
		{
			map = getProperties(errors[i]);
			int line = Integer.parseInt(map.get("line")); //$NON-NLS-1$
			// String type = map.get("errortype");
			String msg = map.get("message"); //$NON-NLS-1$
			String context = map.get("context"); //$NON-NLS-1$
			String property = map.get("property"); //$NON-NLS-1$
			String skippedstring = map.get("skippedstring"); //$NON-NLS-1$
			String errorsubtype = map.get("errorsubtype"); //$NON-NLS-1$

			if (msg == null)
			{
				if (property == null)
				{
					property = context;
				}
				
				if (skippedstring.equals("[empty string]")) //$NON-NLS-1$
				{
					skippedstring = "no properties defined"; //$NON-NLS-1$
				}
				
				msg = StringUtils.format(Messages.CSSErrorManager_ErrorParseMessage, new String[] { errorsubtype, skippedstring, property });
			}

			Entities e = Entities.HTML40;
			
			msg = e.unescape(msg);
			//msg = filterMessage(msg);

			if (msg != null)
			{
			    int errorOffset = utils.getFirstNonWhitespaceCharacterOffset(line - 1);
			    if (errorOffset == -1)
			    {
			        errorOffset = 0;
			    }
			    if (!errorsToIgnore.contains(msg))
			    	err.error(msg, filename, line, context, errorOffset);
			}
		}
	}

	/**
	 * Parses CSS warnings.
	 * 
	 * @param err - error reporter.
	 * @param warnings - warnings to parse.
	 * @param filename - file name.
	 */
	private void parseCSSWarnings(UnifiedErrorReporter err, String[] warnings, String filename)
	{
		String last = StringUtils.EMPTY;

		Map<String, String> map;
		for (int i = 0; i < warnings.length; i++)
		{
			map = getProperties(warnings[i]);
			int line = Integer.parseInt(map.get("line")); //$NON-NLS-1$
			int level = Integer.parseInt(map.get("level")); //$NON-NLS-1$
			String msg = StringUtils.format(Messages.CSSErrorManager_LevelWarningMessage, new String[] { map.get("message"), String.valueOf(level) }); //$NON-NLS-1$
			String context = map.get("context"); //$NON-NLS-1$

			String hash = line + ":" + level + ":" + msg + ":" + context; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			if (last.equals(hash) == false)
			{
				//msg = filterMessage(msg);
				
				if (msg != null)
				{
					err.warning(msg, filename, line, context, 1);
				}
			}

			last = hash;
		}
	}
	
	/**
	 * Gets properties table. 
	 * 
	 * @param source - source.
	 * @return properties table.
	 */
	private static Map<String, String> getProperties(String source)
	{
		Pattern pattern = propertiesPattern;
		Matcher matcher = pattern.matcher(source);
		Map<String, String> result = new HashMap<String, String>();

		while (matcher.find())
		{
			result.put(matcher.group(1), matcher.group(2));
		}

		return result;
	}
	
	/**
	 * Gets matching content.
	 * 
	 * @param pattern - pattern to match.
	 * @param source - source.
	 * @return matching content
	 */
	private static String[] getContent(Pattern pattern, String source)
	{
		Matcher matcher = pattern.matcher(source);
		List<String> result = new ArrayList<String>();

		while (matcher.find())
		{
			// String name = matcher.group(1);
			result.add(matcher.group(2));
		}

		return result.toArray(new String[0]);
	}
	
	/**
	 * Loads Aptana CSS profile.
	 * @throws IOException if profile loading fails
	 */
	private void loadAptanaCSSProfile() throws IOException {
		InputStream configStream = 
			StylesheetValidator.class.getResourceAsStream(CONFIG_FILE);
		InputStream profilesStream = 
			StylesheetValidator.class.getResourceAsStream(PROFILES_CONFIG_FILE);
		
		try
		{
			Utf8Properties configProperties = new Utf8Properties();
			configProperties.load(configStream);
			
			Utf8Properties profiles = new Utf8Properties();
			profiles.load(profilesStream);
			
			//not a very good way, but the only I found. 
			PropertiesLoader.config = configProperties;
			
			//pure hack, but again, no other way
			Field field = PropertiesLoader.class.getDeclaredField("profiles"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(null, profiles);
		} catch (Throwable th) {
			IdeLog.logError(CSSPlugin.getDefault(), "Failed to load Aptana CSS profile", th); //$NON-NLS-1$
		}
		finally
		{
			configStream.close();
			profilesStream.close();
		}
	}
	
	/**
	 * Patches CSS properties.
	 * 
	 * @param source - original source.
	 * @param reporter - error reporter.
	 * @param utils - new line utils.
	 * 
	 * @return patched source
	 */
	private String patchCSSProperties(String source, UnifiedErrorReporter reporter,
	        NewLineUtils utils)
	{
		try {
		    
		    //parsing source
			CSSParser parser;
			parser = new CSSParser();
			
			CSSParseState state = new CSSParseState();
			state.setEditState(source, source, 0, 0);
			
			parser.parse(state);
			
			//getting lexemes
			LexemeList lexemes = state.getLexemeList();
			
			//List of lexemes to replace
			LexemeList lexemesToReplace = new LexemeList();
			
			for (int i = 0; i < lexemes.size(); i++)
			{
				Lexeme currentLexeme = lexemes.get(i);
				String currentLexemeText = currentLexeme.getText();
				
				//checking for the error
				if (isCustomPropertyWithoutMinus(currentLexemeText))
				{
				    int errorLineNumber = utils.getLineOfOffset(currentLexeme.getStartingOffset());
                    int errorOffset = utils.getFirstNonWhitespaceCharacterOffset(errorLineNumber);
                    if (errorLineNumber == -1)
                    {
                        errorLineNumber = 0;
                    }
                    if (errorOffset == -1)
                    {
                        errorOffset = 0;
                    }
                    reporter.error(
                            Messages.StylesheetValidator_ILLEGAL_PROPERTY_MESSAGE
                            + currentLexemeText, 
                            "", errorLineNumber + 1, "", errorOffset); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				//removing "-" sign from property names start
				//and saving text to replace
				if (currentLexeme.typeIndex == CSSTokenTypes.PROPERTY
						&& currentLexemeText.startsWith("-")) //$NON-NLS-1$
				{
				    
				    lexemesToReplace.add(currentLexeme);
				}
			}
			
			//printing result
			//printing source, removing "-" sign from each property name start.
            //this is required as W3C parser does not accept properties starting with "-".
            StringBuffer result = new StringBuffer();       
            if (lexemesToReplace.size() != 0)
            {
                int offset = 0;
    			for (int i = 0; i < lexemesToReplace.size(); i++)
    			{
    			    //printing text before lexeme
    			    Lexeme currentLexeme = lexemesToReplace.get(i);
    			    result.append(source.substring(offset, currentLexeme.getStartingOffset()));
    			    offset = currentLexeme.getEndingOffset();
    			    
    			    //printing patched lexeme
    			    String currentLexemeText = currentLexeme.getText(); 
    			    String patchedText = currentLexemeText.substring(1,
    			            currentLexemeText.length());
    			    result.append(patchedText);
    			}
    			
    			Lexeme lastLexeme = lexemesToReplace.get(lexemesToReplace.size() - 1);
    			result.append(source.substring(lastLexeme.getEndingOffset()));
            }
            else
            {
                result.append(source);
            }
            
			return result.toString();
		} catch (ParserInitializationException e) {
			IdeLog.logError(CSSPlugin.getDefault(), "Failed to patch CSS properties", e); //$NON-NLS-1$
			return source;
		} catch (LexerException e) {
			IdeLog.logError(CSSPlugin.getDefault(), "Failed to patch CSS properties", e); //$NON-NLS-1$
			return source;
		}
	}
	
	/**
	 * Checks whether current string is custom property name without the first minus.
	 * @param propertyName - property name.
	 * @return true if  current string is custom property name without the first minus,
	 * false otherwise.
	 */
	private boolean isCustomPropertyWithoutMinus(String propertyName)
	{
	    return NO_MINUS_CUSTOM_PROPERTIES_SET.contains(propertyName);
	}
}
