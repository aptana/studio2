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
package com.aptana.ide.editor.html;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.unified.AbstractLanguageEnvironment;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.metadata.MetadataEnvironment;
import com.aptana.ide.metadata.MetadataRuntimeEnvironment;
import com.aptana.ide.metadata.reader.MetadataObjectsReader;
import com.aptana.ide.parsing.IRuntimeEnvironment;

// TODO: rename to HTMLLanguageEnvironment
/**
 * @author Robin Debreuil
 */
public class HTMLLanguageEnvironment extends AbstractLanguageEnvironment
{
	/*
	 * Fields
	 */

	/**
	 * SLEEP_DELAY
	 */
	public static int SLEEP_DELAY = 6000;
	private static HTMLLanguageEnvironment instance;

	private MetadataRuntimeEnvironment environment;

	/*
	 * Constructors
	 */

	HTMLLanguageEnvironment()
	{
		// environment = MetadataEnvironment.getMetadataFromResource("/com/aptana/ide/resources/HTML.xml");
		// InputStream input =
		// this.getClass().getResourceAsStream("/com/aptana/ide/editor/html/parsing/HTMLMetadata.bin");
		// environment = MetadataEnvironment.getMetadataFromResource(input);
		// EnvironmentManager.addEnvironmentMapping(HTMLMimeType.MimeType, environment);

		environment = new MetadataRuntimeEnvironment();
		EnvironmentManager.addEnvironmentMapping(HTMLMimeType.MimeType, environment);

		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(SLEEP_DELAY);
					loadEnvironment();
				}
				catch (Exception e1)
				{
					IdeLog.logInfo(HTMLPlugin.getDefault(),
							Messages.HTMLLanguageEnvironment_EnvironmentInitializationAborted, e1);
				}
			}
		}, Messages.HTMLLanguageEnvironment_HTMLLanguageThreadName);

		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(true);
		t.start();
	}

	/*
	 * Methods
	 */

	/**
	 * getInstance
	 * 
	 * @return HTMLLanguageEnvironment
	 */
	public static HTMLLanguageEnvironment getInstance()
	{
		if (instance == null)
		{
			instance = new HTMLLanguageEnvironment();
		}
		return instance;
	}

	/**
	 * Return the current object environment
	 * 
	 * @return an IEnvironment object
	 */
	public IRuntimeEnvironment getRuntimeEnvironment()
	{
		return environment;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ILanguageEnvironment#loadEnvironment()
	 */
	public void loadEnvironment()
	{
		InputStream input = this.getClass().getResourceAsStream("/com/aptana/ide/editor/html/parsing/HTMLMetadata.bin"); //$NON-NLS-1$
		MetadataEnvironment env = new MetadataEnvironment();
		env = MetadataEnvironment.getMetadataFromResource(input, env);
		environment.addEnvironment(env);

		Hashtable<URL, String> xmlFiles = new Hashtable<URL, String>();
		addFromExtension(xmlFiles, TAG_XML_FILE, HTMLMimeType.MimeType);

		Enumeration<URL> keys = xmlFiles.keys();
		while (keys.hasMoreElements())
		{
			env = new MetadataEnvironment();
			MetadataObjectsReader reader = new MetadataObjectsReader(env);

			URL key = keys.nextElement();
			String userAgent = xmlFiles.get(key);
			if (userAgent != null)
			{
				// reader.setUserAgent(userAgent);
			}
			try
			{
				reader.loadXML(key.openStream());
			}
			catch (Exception e)
			{
				IdeLog.logInfo(HTMLPlugin.getDefault(), Messages.HTMLLanguageEnvironment_ErrorLoadingEnvironment, e);
			}

			environment.addEnvironment(env);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ILanguageEnvironment#cleanEnvironment()
	 */
	public void cleanEnvironment()
	{
	}

	/**
	 * setProfileMemberOnAll
	 * 
	 * @param value
	 */
	public void setProfileMemberOnAll(boolean value)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * attachFile
	 * 
	 * @param fileService
	 */
	public void attachFile(FileService fileService)
	{
	}

	/**
	 * removeFile
	 * 
	 * @param fileService
	 */
	public void removeFile(FileService fileService)
	{
	}
}
