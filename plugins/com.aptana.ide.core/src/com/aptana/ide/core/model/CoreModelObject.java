/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import com.aptana.ide.core.ILoggable;
import com.aptana.ide.core.ILogger;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.xpath.XPathUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class CoreModelObject extends BaseModelObject implements ILocationObject, ISynchronizableObject,
		ILoggable, ITransformObject
{

	/**
	 * XPATH
	 */
	protected static final XPath XPATH = XPathFactory.newInstance().newXPath();

	/**
	 * ID_ELEMENT
	 */
	public static final String ID_ELEMENT = "id"; //$NON-NLS-1$

	/**
	 * URLS
	 */
	public static final String URLS = "urls"; //$NON-NLS-1$

	/**
	 * URL
	 */
	public static final String URL = "url"; //$NON-NLS-1$

	/**
	 * PATHS
	 */
	public static final String PATHS = "paths"; //$NON-NLS-1$

	/**
	 * PATH
	 */
	public static final String PATH = "path"; //$NON-NLS-1$

	/**
	 * NAME
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * VALUE
	 */
	public static final String VALUE = "value"; //$NON-NLS-1$

	/**
	 * Id of this object
	 */
	protected String id;

	/**
	 * Service provider
	 */
	protected IServiceProvider provider;

	/**
	 * Service request builder
	 */
	protected IServiceRequestBuilder builder;

	/**
	 * Location
	 */
	protected URL location = null;

	/**
	 * Default location
	 */
	protected URL defaultLocation = null;

	/**
	 * Logger
	 */
	protected ILogger logger;

	private int lastUpdateStatus = IServiceResponse.STATUS_UNSET;

	/**
	 * Last service errors
	 */
	protected ServiceErrors lastErrors;

	/**
	 * Creates a new core model object with a null id
	 */
	public CoreModelObject()
	{
		this.id = null;
	}

	/**
	 * Gets the item string to use for the XPath
	 * 
	 * @return - item element string
	 */
	protected abstract String getItemString();

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#getDefaultLocation()
	 */
	public URL getDefaultLocation()
	{
		return this.defaultLocation;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#getLocation()
	 */
	public URL getLocation()
	{
		if (this.location == null && this.defaultLocation != null)
		{
			return defaultLocation;
		}
		return this.location;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#getRequestBuilder()
	 */
	public IServiceRequestBuilder getRequestBuilder()
	{
		return this.builder;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#getServiceProvider()
	 */
	public IServiceProvider getServiceProvider()
	{
		return this.provider;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#hasLocation()
	 */
	public boolean hasLocation()
	{
		return this.location != null && !this.location.equals(this.defaultLocation);
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#setDefaultLocation(java.net.URL)
	 */
	public void setDefaultLocation(URL defaultLocation)
	{
		this.defaultLocation = defaultLocation;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#setLocation(java.net.URL)
	 */
	public void setLocation(URL location)
	{
		if (isModelChanged(this.location, location))
		{
			this.location = location;
			fireChange();
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ITransformObject#fromXML(java.lang.String)
	 */
	public synchronized void fromXML(String xml)
	{
		try
		{
			XPath xpath = XPathUtils.getNewXPath();
			Node site = (Node) xpath.evaluate("/" + getItemString(), XPathUtils.createSource(xml), XPathConstants.NODE); //$NON-NLS-1$
			fromNode(site);
		}
		catch (XPathExpressionException e1)
		{
			String message = MessageFormat.format(
				Messages.getString("CoreModelObject.Error_Building_XML"), //$NON-NLS-1$
				new Object[] {
					getLoggingPrefix(),
					e1.getMessage()
				}
			);
			logError(message);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#setRequestBuilder(com.aptana.ide.core.model.IServiceRequestBuilder)
	 */
	public void setRequestBuilder(IServiceRequestBuilder builder)
	{
		this.builder = builder;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#setServiceProvider(com.aptana.ide.core.model.IServiceProvider)
	 */
	public void setServiceProvider(IServiceProvider provider)
	{
		this.provider = provider;
	}

	/**
	 * Gets the prefix to put before logging statements
	 * 
	 * @return - prefix
	 */
	public abstract String getLoggingPrefix();

	/**
	 * Calls perform action with IServiceRequest.UPLOAD as the type
	 * 
	 * @param nameValuePairs
	 */
	public void performUploadAction(Map<String, String> nameValuePairs)
	{
		this.performAction(nameValuePairs, IServiceRequest.UPLOAD);
	}

	/**
	 * Calls perform action with IServiceRequest.UPLOAD as the type
	 * 
	 * @param name
	 * @param value
	 */
	public void performUploadAction(String name, String value)
	{
		this.performAction(name, value, IServiceRequest.UPLOAD);
	}

	/**
	 * Calls perform action with IServiceRequest.UPLOAD as the type
	 * 
	 * @param nameValuePairs
	 */
	public void performUploadAction(String nameValuePairs)
	{
		this.performAction(nameValuePairs, IServiceRequest.UPLOAD);
	}

	/**
	 * Calls perform action with IServiceRequest.UPDATE as the type
	 * 
	 * @param nameValuePairs
	 */
	public void performUpdateAction(Map<String, String> nameValuePairs)
	{
		this.performAction(nameValuePairs, IServiceRequest.UPDATE);
	}

	/**
	 * Calls perform action with IServiceRequest.UPDATE as the type
	 * 
	 * @param name
	 * @param value
	 */
	public void performUpdateAction(String name, String value)
	{
		this.performAction(name, value, IServiceRequest.UPDATE);
	}

	/**
	 * Calls perform action with IServiceRequest.UPDATE as the type
	 * 
	 * @param nameValuePairs
	 */
	public void performUpdateAction(String nameValuePairs)
	{
		this.performAction(nameValuePairs, IServiceRequest.UPDATE);
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#performAction(java.util.Map, java.lang.String)
	 */
	public IServiceErrors performAction(Map<String, String> nameValuePairs, String type)
	{
		StringBuffer query = new StringBuffer("?"); //$NON-NLS-1$
		if (nameValuePairs != null)
		{
			for (String name : nameValuePairs.keySet())
			{
				String value = nameValuePairs.get(name);
				query.append(name);
				query.append('=');
				query.append(value);
				query.append('&');
			}
		}
		return performAction(query.toString(), type);
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#performAction(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public IServiceErrors performAction(String name, String value, String type)
	{
		return performAction(name + "=" + value, type); //$NON-NLS-1$
	}

	/**
	 * Calls with the response from perform action
	 * 
	 * @param response
	 */
	protected void handleActionResponse(IServiceResponse response)
	{
		// Does nothing by default, subclasses should override
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#performAction(java.lang.String, java.lang.String)
	 */
	public IServiceErrors performAction(String nameValuePairs, String type)
	{
		ServiceErrors errors = null;
		if (hasLocation())
		{
			String query = ""; //$NON-NLS-1$
			String baseURL = getLocation().toExternalForm();
			if (baseURL.indexOf('?') == -1)
			{
				if (!nameValuePairs.startsWith("?")) //$NON-NLS-1$
				{
					query += "?"; //$NON-NLS-1$
				}
			}
			else
			{
				query += "&"; //$NON-NLS-1$
			}
			query += nameValuePairs;
			try
			{
				URL actionURL = new URL(baseURL + query);
				if (this.provider != null && this.builder != null)
				{
					IServiceResponse response = null;
					IServiceRequest request = this.builder.generateRequest(this, type);
					if (request != null)
					{
						String message = MessageFormat.format(
							Messages.getString("CoreModelObject.Perform_Action_Content"), //$NON-NLS-1$
							new Object[] {
								getLoggingPrefix(),
								request.getContents()
							}
						);
						logInfo(message);
					}
					else
					{
						String message = MessageFormat.format(
							Messages.getString("CoreModelObject.Null_Request_On_Action"), //$NON-NLS-1$
							new Object[] {
								getLoggingPrefix()
							}
						);
						logInfo(message);
					}
					response = this.provider.callService(actionURL, request);
					if (response != null)
					{
						String message1 = MessageFormat.format(
							Messages.getString("CoreModelObject.Perform_Action_Response"), //$NON-NLS-1$
							new Object[] {
								getLoggingPrefix(),
								response.getContents()
							}
						);
						String message2 = MessageFormat.format(
							Messages.getString("CoreModelObject.Perform_Action_Status"), //$NON-NLS-1$
							new Object[] {
								getLoggingPrefix(),
								response.getStatus()
							}
						);
						logInfo(message1);
						logInfo(message2);
						errors = new ServiceErrors();
						errors.setStatus(response.getStatus());
						if (response.getStatus() != IServiceResponse.STATUS_OK
								&& response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
						{
							errors.fromXML(response.getContents());
						}
						handleActionResponse(response);
					}
				}
			}
			catch (MalformedURLException e)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Error_Creating_URL"), //$NON-NLS-1$
					new Object [] {
						e.getMessage()
					}
				);
				logError(message);
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}

	/**
	 * Logs an info message
	 * 
	 * @param message
	 */
	protected void logInfo(String message)
	{
		if (this.logger != null)
		{
			this.logger.logInfo(message);
		}
	}

	/**
	 * Logs an error message
	 * 
	 * @param message
	 */
	protected void logError(String message)
	{
		if (this.logger != null)
		{
			this.logger.logError(message);
		}
	}

	/**
	 * Logs an error message
	 * 
	 * @param message
	 * @param th
	 */
	protected void logError(String message, Throwable th)
	{
		if (this.logger != null)
		{
			this.logger.logError(message, th);
		}
	}

	/**
	 * Logs a warning message
	 * 
	 * @param message
	 */
	protected void logWarning(String message)
	{
		if (this.logger != null)
		{
			this.logger.logWarning(message);
		}
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#commit()
	 */
	public IServiceErrors commit()
	{
		ServiceErrors errors = null;
		if (this.provider != null && this.builder != null)
		{
			IServiceResponse response = null;
			IServiceRequest request = this.builder.generateRequest(this, IServiceRequest.COMMIT);
			if (request != null)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Commit_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						StringUtils.getPublishableMessage(request.getContents())
					}
				);
				logInfo(message);
			}
			else
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Null_Request_On_Commit"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
					}
				);
				logInfo(message);
			}
			if (hasLocation())
			{
				response = this.provider.callService(getLocation(), request);
			}
			else if (getDefaultLocation() != null)
			{
				response = this.provider.callService(getDefaultLocation(), request);
			}
			if (response != null)
			{
				String message1 = MessageFormat.format(
					Messages.getString("CoreModelObject.Commit_Response"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				String message2 = MessageFormat.format(
					Messages.getString("CoreModelObject.Commit_Reponse_Status"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getStatus()
					}
				);
				logInfo(message1);
				logInfo(message2);
				if (response.getStatus() == IServiceResponse.STATUS_CREATED && response.getData() instanceof URL)
				{
					setLocation((URL) response.getData());
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.setContents(response.getContents());
				}
				else if (response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
				{
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.fromXML(response.getContents());
					if (response.getStatus() == IServiceResponse.STATUS_OK)
					{
						fireChange();
					}
				}
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}
	
	// This is a really ugly implementation that I just glued together using some of the other methods in this class
	// to unblock Ian and get the job done for posts. It has not been tested for other methods. (kris)
	public IServiceErrors xmlRest(String method)
	{
		ServiceErrors errors = null;
		if (this.provider != null && this.builder != null)
		{
			IServiceResponse response = null;
			IServiceRequest request = this.builder.generateRequest(this, method);
			if (request != null)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.REST_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						request.getContents()
					}
				);
				logInfo(message);
			}
			else
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Null_Request_On_XML_REST"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix()
					}
				);
				logInfo(message);
			}
			if (hasLocation())
			{
				response = this.provider.callService(getLocation(), request);
			}
			else if (getDefaultLocation() != null)
			{
				response = this.provider.callService(getDefaultLocation(), request);
			}
			
			if (response != null)
			{
				String message1 = MessageFormat.format(
					Messages.getString("CoreModelObject.XML_REST_Response"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				String message2 = MessageFormat.format(
					Messages.getString("CoreModelObject.XML_REST_Response_Status"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getStatus()
					}
				);
				logInfo(message1);
				logInfo(message2);
				
				
				if (response.getStatus() == IServiceResponse.STATUS_CREATED  || 
					response.getStatus() == IServiceResponse.STATUS_OK ||
					response.getStatus() == IServiceResponse.STATUS_DELETED)
				{
					if (response.getData() instanceof String)
					{
						fromXML((String) response.getData());
					}
					else if (response.getData() instanceof URL)
					{
						setLocation((URL) response.getData());
					}
					
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
				}
				else if (response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
				{
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.fromXML(response.getContents());
				}
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}
	
	public IServiceErrors upload()
	{
		ServiceErrors errors = null;
		if (this.provider != null && this.builder != null)
		{
			IServiceResponse response = null;
			IServiceRequest request = this.builder.generateRequest(this, IServiceRequest.UPLOAD);
			if (request != null)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Upload_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						request.getContents()
					}
				);
				logInfo(message);
			}
			else
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Null_Request_On_Upload"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix()
					}
				);
				logInfo(message);
			}
			if (hasLocation())
			{
				response = this.provider.callService(getLocation(), request);
			}
			else if (getDefaultLocation() != null)
			{
				response = this.provider.callService(getDefaultLocation(), request);
			}
			if (response != null)
			{
				String message1 = MessageFormat.format(
					Messages.getString("CoreModelObject.Upload_Response"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				String message2 = MessageFormat.format(
					Messages.getString("CoreModelObject.Upload_Response_Status"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				logInfo(message1);
				logInfo(message2);
				if (response.getStatus() == IServiceResponse.STATUS_CREATED && response.getData() instanceof URL)
				{
					setLocation((URL) response.getData());
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
				}
				else if (response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
				{
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.fromXML(response.getContents());
					if (response.getStatus() == IServiceResponse.STATUS_OK)
					{
						fireChange();
					}
				}
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#delete()
	 */
	public IServiceErrors delete()
	{
		ServiceErrors errors = null;
		if (this.provider != null && this.builder != null)
		{
			IServiceResponse response = null;
			IServiceRequest request = this.builder.generateRequest(this, IServiceRequest.DELETE);
			if (request != null)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Delete_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						request.getContents()
					}
				);
				logInfo(message);
			}
			else
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Null_Request_On_Delete_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix()
					}
				);
				logInfo(message);
			}
			if (hasLocation())
			{
				response = this.provider.callService(getLocation(), request);
			}
			else if (getDefaultLocation() != null)
			{
				response = this.provider.callService(getDefaultLocation(), request);
			}
			if (response != null)
			{
				String message1 = MessageFormat.format(
					Messages.getString("CoreModelObject.Delete_Reponse_Status"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				String message2 = MessageFormat.format(
					Messages.getString("CoreModelObject.Delete_Response"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getContents()
					}
				);
				logInfo(message1);
				logInfo(message2);
				if (response.getStatus() == IServiceResponse.STATUS_DELETED)
				{
					setLocation(null);
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
				}
				else if (response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
				{
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.fromXML(response.getContents());
				}
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}

	/**
	 * @see com.aptana.ide.core.model.ISynchronizableObject#update()
	 */
	public IServiceErrors update()
	{
		ServiceErrors errors = null;
		if (this.provider != null && this.builder != null)
		{
			IServiceResponse response = null;
			IServiceRequest request = this.builder.generateRequest(this, IServiceRequest.UPDATE);
			if (request != null)
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Update_Content"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						request.getContents()
					}
				);
				logInfo(message);
			}
			else
			{
				String message = MessageFormat.format(
					Messages.getString("CoreModelObject.Null_Request_On_Update"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix()
					}
				);
				logInfo(message);
			}
			if (hasLocation())
			{
				response = this.provider.callService(getLocation(), request);
			}
			else if (getDefaultLocation() != null)
			{
				response = this.provider.callService(getDefaultLocation(), request);
			}
			if (response != null)
			{
				lastUpdateStatus = response.getStatus();
				String message1 = MessageFormat.format(
					Messages.getString("CoreModelObject.Update_Response_Status"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						response.getStatus()
					}
				);
				String message2 = MessageFormat.format(
					Messages.getString("CoreModelObject.Update_Reponse"), //$NON-NLS-1$
					new Object[] {
						getLoggingPrefix(),
						StringUtils.getPublishableMessage(response.getData())
					}
				);
				logInfo(message1);
				logInfo(message2);
				if (response.getStatus() == IServiceResponse.STATUS_OK)
				{
					if (response.getData() instanceof String)
					{
						fromXML((String) response.getData());
					}
					else if (response.getData() instanceof URL)
					{
						setLocation((URL) response.getData());
					}
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
				}
				else if (response.getStatus() != IServiceResponse.STATUS_UNKNOWN_HOST)
				{
					errors = new ServiceErrors();
					errors.setStatus(response.getStatus());
					errors.fromXML(response.getContents());
				}
			}
		}
		setLastServiceErrors(errors);
		return errors;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#getLogger()
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#setLogger(com.aptana.ide.core.ILogger)
	 */
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Clears the model this object holds
	 */
	public synchronized void clear()
	{
		// Does nothing by default
	}

	/**
	 * Get last update status
	 * 
	 * @return - last status
	 */
	public int getLastUpdateStatus()
	{
		return lastUpdateStatus;
	}

	/**
	 * Gets the text content
	 * 
	 * @param xpath
	 * @param node
	 * @return - text content
	 */
	protected String getTextContent(String xpath, Node node)
	{
		String text = null;
		try
		{
			Node subNode = (Node) XPATH.evaluate(xpath, node, XPathConstants.NODE);
			if (subNode != null)
			{
				text = subNode.getTextContent();
			}
		}
		catch (XPathExpressionException e)
		{
			String message = MessageFormat.format(
				Messages.getString("CoreModelObject.XPath_Error_While_Parsing"), //$NON-NLS-1$
				new Object[] {
					getLoggingPrefix(),
					e.getMessage()
				}
			);
			logError(message);
		}
		return text;
	}

	/**
	 * @see com.aptana.ide.core.model.ILocationObject#getLastServiceErrors()
	 */
	public ServiceErrors getLastServiceErrors()
	{
		return this.lastErrors;
	}

	/**
	 * Sets the last service errors
	 * 
	 * @param lastErrors
	 */
	public void setLastServiceErrors(ServiceErrors lastErrors)
	{
		this.lastErrors = lastErrors;
	}

}