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
package com.aptana.ide.server.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Lindsey
 */
public final class HttpContentTypes
{
	/*
	 * Fields
	 */
	private static Map<String, String> contentTypeTable;

	/*
	 * Constructors
	 */

	/**
	 * initialize the content type/file extension association table
	 */
	static
	{
		contentTypeTable = new HashMap<String, String>();

		// application
		contentTypeTable.put(".doc", "application/msword"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".js", "application/x-javascript"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".pdf", "application/pdf"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ai", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".eps", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ps", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".xls", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".csv", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ppt", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ppz", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".pps", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".pot", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".swf", "application/x-shockwave-flash"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".xhtml", "application/xhtml+xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".xml", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".dtd", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".xsl", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ent", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".cat", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".sty", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$

		// audio
		contentTypeTable.put(".mpga", "audio/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".mp2", "audio/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".mp3", "audio/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put("aif", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put("aiff", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put("aifc", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wma", "audio/x-ms-wma"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ram", "audio/x-pn-realaudio"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".rm", "audio/x-pn-realaudio"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".rpm", "audio/x-pn-realaudio-plugin"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ra", "audio/x-realaudio"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wav", "audio/x-wav"); //$NON-NLS-1$ //$NON-NLS-2$

		// image
		contentTypeTable.put(".bmp", "image/bmp"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".gif", "image/gif"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ico", "image/ico"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".ief", "image/ief"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".jpeg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".jpg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".jpe", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".png", "image/png"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".svg", "image/svg+xml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".tiff", "image/tiff"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".tif", "image/tiff"); //$NON-NLS-1$ //$NON-NLS-2$

		// text
		contentTypeTable.put(".css", "text/css"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".htm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".html", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".rtf", "text/rtf"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".sgml", "text/sgml"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".sgm", "text/sgml"); //$NON-NLS-1$ //$NON-NLS-2$

		// video
		contentTypeTable.put(".mpeg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".mpg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".mpe", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".asf", "video/x-ms-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".asx", "video/x-ms-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wm", "video/x-ms-wm"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wmv", "video/x-ms-wmv"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wmx", "video/x-ms-wmx"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".wvx", "video/x-ms-wvx"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".sgm", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".sgm", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".qt", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".mov", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".avi", "video/x-msvideo"); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeTable.put(".movie", "video/x-sgi-movie"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * HttpContentTypes
	 */
	private HttpContentTypes()
	{
	}

	/**
	 * getContentType
	 * 
	 * @param extension
	 * @return String
	 */
	public static String getContentType(String extension)
	{
		return (String) contentTypeTable.get(extension);
	}
}
