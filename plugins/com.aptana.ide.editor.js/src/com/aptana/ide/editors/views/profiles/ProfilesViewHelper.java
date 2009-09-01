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
package com.aptana.ide.editors.views.profiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * 
 * @author Ingo Muschenetz
 * 
 */
public class ProfilesViewHelper {

	/**
	 * 
	 * @param base
	 * @param text
	 * @return
	 */
	public static String[] addSDocFromJavaScriptSource(File base, String text) {		
		URL baseFile = FileUtils.uriToURL(CoreUIUtils.getURI(base));
		return addSDocFromJavaScriptSource(baseFile, text);
	
	}
	
	/**
	 * 
	 * @param base
	 * @param text
	 * @return
	 */
	public static String[] addSDocFromJavaScriptSource(URL base, String text) {

		List<String> fileList = new ArrayList<String>();
		int commentStart = text.indexOf("/*"); //$NON-NLS-1$

		while (commentStart != -1) {
			int commentEnd = text.indexOf("*/", commentStart + 2); //$NON-NLS-1$

			if (commentEnd != -1) {
				// String comment = text.substring(commentStart, commentEnd +
				// 2);
				Pattern p = Pattern.compile("@sdoc\\s+([^\r\n]+)"); //$NON-NLS-1$
				Matcher matcher = p.matcher(text);

				String matchedString = ""; //$NON-NLS-1$
				while (matcher.find()) {
					matchedString = matcher.group(1);
				}

				if (matchedString != "") { //$NON-NLS-1$

					// String filename = matchedString.replace("\\s+$", "");
					String filename = matchedString.trim();

					if (filename.charAt(0) != '/'
							|| filename.indexOf(":") == -1) { //$NON-NLS-1$
						filename = CoreUIUtils.joinURI(base, filename);
					}

					// save original source up to but not including the comment
					fileList.add(CoreUIUtils.getURI(filename));
				}

				commentStart = text.indexOf("/*", commentEnd + 2); //$NON-NLS-1$
			} else {
				break;
			}
		}

		return fileList.toArray(new String[fileList.size()]);
	}

	/**
	 * 
	 * @param base
	 * @param text
	 * @param parseState
	 * @return
	 */
	public static String[] addScriptTagsFromHTMLSource(File base,
			String text, IParseState parseState) {
		
		URL baseFile = FileUtils.uriToURL(CoreUIUtils.getURI(base));
		return addScriptTagsFromHTMLSource(baseFile, text, parseState);
	
	}
	
	/**
	 * 
	 * @param base
	 * @param text
	 * @param parseState
	 * @return
	 */
	public static String[] addScriptTagsFromHTMLSource(URL base,
			String text, IParseState parseState) {

		List<String> fileList = new ArrayList<String>();

		IParseNode results = parseState.getParseResults();
		IParseNode[] current = results.getChildren();

		IQueue queue = new ArrayQueue();

		List<String> srcs = new ArrayList<String>();

		for (int i = 0; i < current.length; i++) {
			queue.enqueue(current[i]);
		}

		while (queue.size() > 0) {
			IParseNode node = (IParseNode) queue.dequeue();
			IParseNode[] children = node.getChildren();

			for (int i = 0; i < children.length; i++) {
				IParseNode child = children[i];

				if ("script".equals(child.getText())) { //$NON-NLS-1$
					boolean hasFile = false;
					String filename = child.getAttribute("src"); //$NON-NLS-1$
					filename = makeHttpUrl(filename); // [IM] added for bug #4735
					String aSrc = ""; //$NON-NLS-1$
					for (int j = 0; j < srcs.size(); j++) {
						aSrc = srcs.get(j);
						if (aSrc == filename) {
							hasFile = true;
							break;
						}
					}

					if (hasFile == false) {
						srcs.add(filename);
					}
				} else {
					queue.enqueue(child);
				}
			}
		}

		for (int i = 0; i < srcs.size(); i++) {
			String src = srcs.get(i);

			if (src != null && src.length() > 0) {
				String source = StringUtils.trimStringQuotes(src);
				source = stripQuerystring(source);

				// see if the source ref is absolute or relative
				if (!CoreUIUtils.isURI(source) && source.charAt(0) != '/') {
					source = CoreUIUtils.joinURI(base, source);
				}

				String uri = CoreUIUtils.getURI(source);
				fileList.add(uri);

				String sdocFile = findScriptDocFile(uri);

				if (sdocFile != null) {
					fileList.add(CoreUIUtils.getURI(sdocFile));
				}

				URL url = FileUtils.uriToURL(uri);
				if(url != null)
				{
					String[] subFiles = addScriptFromJavaScriptSource(url);
					fileList.addAll(Arrays.asList(subFiles));
				}
			}
		}
		
		// Remove duplicate entries
		return fileList.toArray(new String[fileList.size()]);
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String[] addSDocFromJavaScriptSource(URL url) {
		
		URL basePath = getBasePath(url);
		try
		{
			String text = FileUtils.readContent(url);
			if(text != null)
			{
				return addSDocFromJavaScriptSource(
						basePath, text);
			}
			else
			{
				return new String[0];
			}			
		}
		catch(IOException ex)
		{
			return new String[0];			
		}
	}

	public static URL getBasePath(URL url) {
		return CoreUIUtils.trimURLSegments(url, 1);
	}
	
	public static URL getBasePath(String uri) {
		URI uri2;
		try {
			uri2 = new URI(uri);
			URL url = uri2.toURL();
			return getBasePath(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String[] addScriptFromJavaScriptSource(URL url) {
		
		URL basePath = getBasePath(url);
		String text;
		try {
			text = FileUtils.readContent(url);
			if (text != null) {
				return addScriptFromJavaScriptSource(basePath, text);
			}
		} catch (IOException e) {
			return new String[0];
		}

		return new String[0];
	}
	
	/**
	 * 
	 * @param base
	 * @param text
	 * @return
	 */
	public static String[] addScriptFromJavaScriptSource(File base, String text) {		
		URL baseFile = FileUtils.uriToURL(CoreUIUtils.getURI(base));
		return addScriptFromJavaScriptSource(baseFile, text);
	}
	
	/**
	 * 
	 * @param base
	 * @param text
	 * @return
	 */
	public static String[] addScriptFromJavaScriptSource(URL base,
			String text) {
		
		List<String> fileList = new ArrayList<String>();

		Pattern pattern = Pattern
				.compile(
						"^MochiKit\\.MochiKit\\.SUBMODULES\\s*=\\s*\\[[\\r\\n]+([^\\]]+)\\]", //$NON-NLS-1$
						Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(text);

		String names = ""; //$NON-NLS-1$
		while (matcher.find()) {
			names = matcher.group(1);
		}

		pattern = Pattern.compile("\"([^\"]+)\""); //$NON-NLS-1$

		matcher = pattern.matcher(names);

		List<String> mochikitNames = new ArrayList<String>();

		while (matcher.find()) {
			mochikitNames.add(matcher.group(1));
		}

		// see if they referenced any sdoc files in the code via @sdoc tag
		String[] sdocs = ProfilesViewHelper.addSDocFromJavaScriptSource(base, text);
		fileList.addAll(Arrays.asList(sdocs));

		for (int i = 0; i < mochikitNames.size(); i++) {

			String source = CoreUIUtils.joinURI(base, mochikitNames.get(i)
					+ ".js"); //$NON-NLS-1$

			fileList.add(CoreUIUtils.getURI(source));

			// see if they referenced any sdoc files in the code via @sdoc tag
			URL u = FileUtils.uriToURL(source);
			if(u != null)
			{
				String[] subFiles = addSDocFromJavaScriptSource(u);
				fileList.addAll(Arrays.asList(subFiles));
			}
			
			// see if there are any sdoc files along side this file
			String sdocFile = findScriptDocFile(source);
			if (sdocFile != null) {
				fileList.add(CoreUIUtils.getURI(sdocFile));
			}
		}
		
		return fileList.toArray(new String[fileList.size()]);

	}


	/**
	 * 
	 * @param source
	 * @return
	 */
	public static String findScriptDocFile(String fileName) {
		
		String baseName = CoreUIUtils.getURI(FileUtils.stripExtension(fileName));
		String sdocName = baseName + ".sdoc"; //$NON-NLS-1$
		
		URL sdocURL = FileUtils.uriToURL(sdocName);
		
		if (isValidURL(sdocURL)) {
			return sdocURL.toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	public static String stripQuerystring(String source) {
		int tmp = source.indexOf("?"); //$NON-NLS-1$
		if (tmp > -1) {
			source = source.substring(0, tmp);
		}

		return source;
	}
	
	/**
	 * Is the URL a valid URL?
	 * @param f
	 * @return
	 */
	public static boolean isValidURL(URL url) {
		
		if(url == null)
		{
			return false;
		}
		
		try {
			if(FileUtils.isFileURL(url))
			{
				File file = FileUtils.urlToFile(url);
				return file.exists() && file.isFile() && file.canRead();
			}
			else
			{
				InputStream is = url.openStream();
				is.close();
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Treat urls beginning with "//" as http://
	 * @param url
	 * @return
	 */
	public static String makeHttpUrl(String url)
	{
		if(url != null && url.startsWith("//")) //$NON-NLS-1$
		{
			return "http:" + url; //$NON-NLS-1$
		}
		else
		{
			return url;
		}
	}
}
