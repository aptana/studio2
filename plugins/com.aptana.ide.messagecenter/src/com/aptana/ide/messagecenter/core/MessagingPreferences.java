/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.messagecenter.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.intro.messaging.Message;
import com.aptana.ide.messagecenter.MessageCenterPlugin;

/**
 * Message reader from file store
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class MessagingPreferences
{
	private static final String SETTING_FILE = ".aptana-messaging"; //$NON-NLS-1$

	private static MessagingPreferences instance;

	private List<Message> messages;
	private boolean _hasLoaded;

	/**
	 * MessagingPreferences
	 */
	private MessagingPreferences()
	{
		this.messages = new ArrayList<Message>();
	}

	/**
	 * getInstance
	 * 
	 * @return preference singleton
	 */
	public static MessagingPreferences getInstance()
	{
		if (instance == null)
		{
			instance = new MessagingPreferences();
		}
		return instance;
	}

	/**
	 * LoadPreferences
	 * 
	 * @return - list of loaded messages
	 */
	@SuppressWarnings("unchecked")
	public List<Message> loadPreferences()
	{
		File settings = getSettingsFile();
		if (this._hasLoaded || settings.exists() == false)
		{
			return this.messages;
		}

		ObjectInputStream ois = null;

		try
		{
			ois = new ObjectInputStream(new FileInputStream(settings));
			Object obj = ois.readObject();

			if (obj instanceof List)
			{
				this.messages = (List<Message>) obj;
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(MessageCenterPlugin.getDefault(), Messages.MessagingPreferences_ERR_ReadStore, e);
		}
		finally
		{
			if (ois != null)
			{
				try
				{
					ois.close();
				}
				catch (IOException e)
				{
					IdeLog.logError(MessageCenterPlugin.getDefault(), Messages.MessagingPreferences_ERR_CloseStore, e);
				}
			}

			this._hasLoaded = true;
		}
		return this.messages;
	}

	/**
	 * SavePreferences
	 * 
	 * @param savedMessages
	 */
	public void savePreferences(List<Message> savedMessages)
	{
		ObjectOutputStream oos = null;
		try
		{
			File settings = getSettingsFile();
			oos = new ObjectOutputStream(new FileOutputStream(settings));
			oos.writeObject(savedMessages);
			oos.flush();
		}
		catch (Exception e)
		{
			IdeLog.logError(MessageCenterPlugin.getDefault(), Messages.MessagingPreferences_ERR_WriteStore, e);
		}
		finally
		{
			if (oos != null)
			{
				try
				{
					oos.close();
				}
				catch (IOException e)
				{
					IdeLog.logError(MessageCenterPlugin.getDefault(), Messages.MessagingPreferences_ERR_CloseStore, e);
				}
			}
		}
	}

    private File getSettingsFile()
    {
        String homeDir = System.getProperty("osgi.configuration.area"); //$NON-NLS-1$
        URL fileURL = FileUtils.uriToURL(homeDir);
        File f = FileUtils.urlToFile(fileURL);
        f.mkdirs();
        return new File(f, SETTING_FILE);
    }

}
