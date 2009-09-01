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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.intro.messaging.Message;
import com.aptana.ide.messagecenter.MessageCenterPlugin;
import com.aptana.ide.messagecenter.preferences.FeedDescriptor;

/**
 * Message reader from remote sites. Reads messages from the Aptana blog plus
 * mesages found on other update sites
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MessagesReader extends DefaultHandler
{

    private static final int TIMEOUT = 10000;

	private List<Message> messages;
	private Message currentMessage;
	private String currentSiteURL;
	private boolean inTitle;
	private boolean inLink;
	private boolean inDescription;
	private boolean inContent;
	private boolean inDate;
	private StringBuilder buffer;

	private String channelTitle;

	/**
	 * Creates a new messages reader
	 */
	public MessagesReader()
	{
		this.messages = new ArrayList<Message>();
		this.buffer = new StringBuilder();
	}

	/**
	 * Gets the current list of messages
	 * 
	 * @return - an array of Message objects
	 */
	public Message[] getMessages()
	{
		return this.messages.toArray(new Message[this.messages.size()]);
	}

	/**
	 * Clears the messages
	 */
	private void clearMessages()
	{
		this.messages.clear();
	}

	/**
	 * Loads the messages
	 */
	public void loadMessages(FeedDescriptor[] feeds, IProgressMonitor monitor)
	{
		if (monitor != null)
		{
			monitor.beginTask(Messages.MessagesReader_Task_LoadingFeeds, feeds.length);
		}

		clearMessages();

		String siteURL;
		URL url;
		InputStream stream;
		for (FeedDescriptor feedDescriptor : feeds)
		{
			siteURL = feedDescriptor.getUrl();
			if (monitor != null)
			{
				monitor.subTask(StringUtils.format(Messages.MessagesReader_SubTask_LoadingMsgs, siteURL));
			}

			this.currentSiteURL = siteURL;
			stream = null;
			try
			{
				url = new URL(siteURL);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				stream = conn.getInputStream();
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				SAXParser saxParser = null;
				saxParser = factory.newSAXParser();
				saxParser.parse(stream, this);
				stream.close();
			}
			catch (FileNotFoundException e)
			{
				// Don't need to log for now since many sites will
				// probably aren't providing messages, and thus an exception is
				// not an issue as it just means no messages exist; uncomment
				// the following line to log error
				// IdeLog.logInfo(IntroPlugin.getDefault(),
				// "Error loading message XML", e);
			}
			catch (SocketTimeoutException ste) 
			{
			    if (monitor != null) {
			        monitor.subTask(StringUtils.format(Messages.MessagesReader_Task_ErrorLoading, siteURL) );
			        IdeLog.logError(MessageCenterPlugin.getDefault(), Messages.MessagesReader_ERR_Loading, ste);
			    }
			}
			catch (Exception e)
			{
				IdeLog.logInfo(MessageCenterPlugin.getDefault(), Messages.MessagesReader_INF_ErrorLoading, e);
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

			if (monitor != null)
			{
				monitor.worked(1);
			}

			feedDescriptor.setLastPoll(new Date());
		}

	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (qName.equals("item")) //$NON-NLS-1$
		{
			this.currentMessage = new Message();
			this.currentMessage.setSite(this.currentSiteURL);

			String read = attributes.getValue("aptana:read"); //$NON-NLS-1$
			if (read != null && !"".equals(read)) //$NON-NLS-1$
			{
				this.currentMessage.setRead(Boolean.parseBoolean(read));
			}
		}
		else if (this.currentMessage == null && qName.equals("title")) //$NON-NLS-1$
		{
			buffer = new StringBuilder();
		}
		else if (this.currentMessage != null)
		{
			if (qName.equals("title")) //$NON-NLS-1$
			{
				this.inTitle = true;
				buffer = new StringBuilder();
			}
			else if (qName.equals("link")) //$NON-NLS-1$
			{
				this.inLink = true;
				buffer = new StringBuilder();
			}
			else if (qName.equals("description")) //$NON-NLS-1$
			{
				this.inDescription = true;
				buffer = new StringBuilder();
			}
			else if (qName.equals("content:encoded")) //$NON-NLS-1$
			{
				this.inContent = true;
				buffer = new StringBuilder();
			}
			else if (qName.equals("pubDate") || qName.equals("dc:date")) //$NON-NLS-1$//$NON-NLS-2$
			{
				this.inDate = true;
				buffer = new StringBuilder();
			}
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("item") && this.currentMessage != null) //$NON-NLS-1$
		{
			if (this.currentMessage.getContent() == null && this.currentMessage.getPreview() != null)
			{
				this.currentMessage.setContent(this.currentMessage.getPreview());
			}
			if (this.currentMessage.getContent() != null && this.currentMessage.getTitle() != null
					&& this.currentMessage.getId() != null && this.currentMessage.getAddress() != null
					&& this.currentMessage.getPreview() != null && this.currentMessage.getDate() != null)
			{
				this.messages.add(this.currentMessage);
			}
			else
			{
				IdeLog.logInfo(MessageCenterPlugin.getDefault(), StringUtils.format(Messages.MessagesReader_INF_AddFailed,
						this.currentMessage.getId()));
			}
			this.currentMessage = null;
		}
		if (qName.equals("title") && this.currentMessage == null) //$NON-NLS-1$
		{
			this.channelTitle = buffer.toString();
		}
		else if (this.currentMessage != null)
		{
			if (qName.equals("title")) //$NON-NLS-1$
			{
				if (this.inTitle)
				{
					String messageTitle = extractTitle(buffer.toString());
					this.currentMessage.setTitle(messageTitle);
					if (messageTitle.toLowerCase().startsWith("urgent:")) //$NON-NLS-1$
					{
						this.currentMessage.setUrgent(true);
					}
				}
				this.inTitle = false;
				this.currentMessage.setChannelTitle(channelTitle);
			}
			else if (qName.equals("link")) //$NON-NLS-1$
			{
				if (this.inLink)
				{
					this.currentMessage.setId(buffer.toString());
					this.currentMessage.setAddress(buffer.toString());
				}
				this.inLink = false;
			}
			else if (qName.equals("description")) //$NON-NLS-1$
			{
				if (this.inDescription)
				{
					this.currentMessage.setPreview(buffer.toString());
				}
				this.inDescription = false;
			}
			else if (qName.equals("content:encoded")) //$NON-NLS-1$
			{
				if (this.inContent)
				{
					this.currentMessage.setContent(buffer.toString());
				}
				this.inContent = false;
			}
			else if (qName.equals("pubDate") || qName.equals("dc:date")) //$NON-NLS-1$//$NON-NLS-2$
			{
				if (this.inDate)
				{
					SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US); //$NON-NLS-1$
					Date date;
					try
					{
						date = df.parse(buffer.toString());
					}
					catch (ParseException e)
					{
						try
						{
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
							date = sdf.parse(buffer.toString());
						}
						catch (ParseException ex)
						{
							date = new Date();
						}
					}
					this.currentMessage.setDate(date);
				}
			}
			buffer = new StringBuilder();
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		buffer.append(ch, start, length);
	}

	private static String extractTitle(String message)
	{
		int index = message.indexOf('\n');
		return index < 0 ? message : message.substring(0, index);
	}

}
