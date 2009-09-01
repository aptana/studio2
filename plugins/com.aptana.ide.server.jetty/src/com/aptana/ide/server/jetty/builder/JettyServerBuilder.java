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
package com.aptana.ide.server.jetty.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.server.jetty.server.IDELoggingHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.IDocumentRootFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IDocumentRootResolver;
import com.aptana.jaxer.connectors.servlet.interfaces.IErrorPageFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IErrorPageHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.IHostnameFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IIgnoreFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IIgnoreHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.ILoggingFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IRestartManager;
import com.aptana.jaxer.connectors.servlet.interfaces.IRestartableFilter;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class JettyServerBuilder
{

	private static JettyServerBuilder builder;

	/**
	 * CONTEXT_EXTENSION_POINT
	 */
	public static final String CONTEXT_EXTENSION_POINT = JettyPlugin.PLUGIN_ID + ".context"; //$NON-NLS-1$

	/**
	 * FILTER_ELEMENT
	 */
	public static final String FILTER_ELEMENT = "filter"; //$NON-NLS-1$

	/**
	 * SERVLET_ELEMENT
	 */
	public static final String SERVLET_ELEMENT = "servlet"; //$NON-NLS-1$

	/**
	 * Configurator attribute name.
	 */
	public static final String CONFIGURATOR_ATTR = "configurator"; //$NON-NLS-1$

	/**
	 * HANDLER_ATTR
	 */
	public static final String HANDLER_ATTR = "handler"; //$NON-NLS-1$

	/**
	 * STATISTICS_ATTR
	 */
	public static final String STATISTICS_ATTR = "statistics"; //$NON-NLS-1$

	/**
	 * RESTART_ATTR
	 */
	public static final String RESTART_ATTR = "restart"; //$NON-NLS-1$

	/**
	 * IGNORE_ATTR
	 */
	public static final String IGNORE_ATTR = "ignore"; //$NON-NLS-1$

	/**
	 * ERROR_ATTR
	 */
	public static final String ERROR_ATTR = "error";//$NON-NLS-1$

	/**
	 * CLASS_ATTR
	 */
	public static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	/**
	 * PATHSPEC_ATTR
	 */
	public static final String PATHSPEC_ATTR = "pathSpec"; //$NON-NLS-1$

	/**
	 * DISPATCHES_ATTR
	 */
	public static final String DISPATCHES_ATTR = "dispatches"; //$NON-NLS-1$

	/**
	 * SERVERID_ATTR
	 */
	public static final String SERVERID_ATTR = "serverID"; //$NON-NLS-1$

	private List<IConfigurationElement> servlets;
	private List<IConfigurationElement> filters;
	private IDELoggingHandler loggingHandler = new IDELoggingHandler();

	private JettyServerBuilder()
	{
		servlets = new ArrayList<IConfigurationElement>();
		filters = new ArrayList<IConfigurationElement>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(CONTEXT_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				String type = ce[j].getName();
				if (FILTER_ELEMENT.equals(type))
				{
					this.filters.add(ce[j]);
				}
				else if (SERVLET_ELEMENT.equals(type))
				{
					this.servlets.add(ce[j]);
				}
			}
		}
	}

	/**
	 * Builds a server context with the extension points for the server id
	 * 
	 * @param context
	 * @param extensionPointID
	 * @param serverID
	 * @param hostname
	 * @param port
	 * @param resolver
	 */
	public void buildServer(Context context, String extensionPointID, String serverID, String hostname, int port,
			IDocumentRootResolver resolver)
	{
		if (context != null && extensionPointID != null)
		{
			IConfigurationElement curr = null;
			for (int i = 0; i < servlets.size(); i++)
			{
				curr = (IConfigurationElement) servlets.get(i);
				String id = curr.getAttribute(SERVERID_ATTR);
				if (id != null && extensionPointID.equals(id))
				{
					String pathSpec = curr.getAttribute(PATHSPEC_ATTR);
					String className = curr.getAttribute(CLASS_ATTR);
					if (className != null)
					{
						try
						{
							Object obj = curr.createExecutableExtension(CLASS_ATTR);
							if (obj instanceof Servlet)
							{
								Servlet servlet = (Servlet) obj;
								if (servlet instanceof IStatisticsProvider && serverID != null)
								{
									IStatisticsHandler statsHandler = getStatisticsHandler(curr);
									if (statsHandler != null)
									{
										statsHandler.setID(serverID);
										((IStatisticsProvider) servlet).setStatisticsHandler(statsHandler);
									}
								}
								IContextHandler handler = getContextHandler(curr);
								if (handler == null || handler.shouldAddServlet())
								{
									ServletHolder holder = new ServletHolder(servlet);

									if (curr.getAttribute(CONFIGURATOR_ATTR) != null)
									{

										try
										{
											IServletConfigurator configurator = (IServletConfigurator) curr
													.createExecutableExtension(CONFIGURATOR_ATTR);
											try
											{
												configurator.configure(holder);
											}
											catch (ServletConfigurationException ex)
											{
												IdeLog.logError(JettyPlugin.getDefault(),
														Messages.JettyServerBuilder_ERR_ConfigureServlet + holder.getClassName(),
														ex);
											}
										}
										catch (Throwable th)
										{
											IdeLog.logError(JettyPlugin.getDefault(),
													Messages.JettyServerBuilder_ERR_CreateServlet, th);
										}
									}
									if (pathSpec != null)
									{
										// Support comma separated path list of path specs
										String[] pathSpecsArray = pathSpec.trim().split(Pattern.quote(",")); //$NON-NLS-1$
										for (String aPathSpec : pathSpecsArray)
										{
											context.addServlet(holder, aPathSpec.trim());
										}
									}
									else
									{
										holder.setName(servlet.getClass().getName());
										context.getServletHandler().addServlet(holder);
									}
								}
							}
						}
						catch (CoreException e)
						{
							IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadExtension, e);
						}
					}
				}
			}
			for (int i = 0; i < filters.size(); i++)
			{
				curr = (IConfigurationElement) filters.get(i);
				String id = curr.getAttribute(SERVERID_ATTR);
				if (id != null && extensionPointID.equals(id))
				{
					String pathSpec = curr.getAttribute(PATHSPEC_ATTR);
					String dispatches = curr.getAttribute(DISPATCHES_ATTR);
					String className = curr.getAttribute(CLASS_ATTR);
					if (pathSpec != null && className != null && dispatches != null)
					{
						String[] allPathSpecs = new String[] { pathSpec };
						try
						{
							allPathSpecs = pathSpec.split(","); //$NON-NLS-1$
						}
						catch (Exception e)
						{
							IdeLog.logError(JettyPlugin.getDefault(), Messages.JettyServerBuilder_ERR_SplitPath, e);
						}

						try
						{
							int intDispatches = Integer.parseInt(dispatches);
							Object obj = curr.createExecutableExtension(CLASS_ATTR);
							if (obj instanceof Filter)
							{
								Filter filter = (Filter) obj;
								IContextHandler handler = getContextHandler(curr);
								IIgnoreHandler ignoreHandler = getIgnoreHandler(curr);
								IErrorPageHandler errorHandler = getErrorHandler(curr);
								IStatisticsHandler statsHandler = getStatisticsHandler(curr);
								IRestartManager restartHandler = getRestartHandler(curr);
								if (filter instanceof IStatisticsProvider && serverID != null)
								{
									if (statsHandler != null)
									{
										statsHandler.setID(serverID);
										((IStatisticsProvider) filter).setStatisticsHandler(statsHandler);
									}
								}
								if (filter instanceof IRestartableFilter && serverID != null)
								{
									if (restartHandler != null)
									{
										restartHandler.setID(serverID);
										restartHandler.setFilter((IRestartableFilter) filter);
									}
								}
								if (filter instanceof IHostnameFilter)
								{
									((IHostnameFilter) filter).setHostname(hostname);
									((IHostnameFilter) filter).setPort(port);
								}
								if (filter instanceof IDocumentRootFilter)
								{
									((IDocumentRootFilter) filter).setDocumentRootResolver(resolver);
								}
								if (filter instanceof IIgnoreFilter)
								{
									((IIgnoreFilter) filter).setIgnoreHandler(ignoreHandler);
								}
								if (filter instanceof IErrorPageFilter)
								{
									((IErrorPageFilter) filter).setErrorPageHandler(errorHandler);
								}
								if (filter instanceof ILoggingFilter)
								{
									((ILoggingFilter) filter).setLoggingHandler(loggingHandler);
								}
								if (handler == null || handler.shouldaddFilter())
								{
									for (int paths = 0; paths < allPathSpecs.length; paths++)
									{
										context.addFilter(new FilterHolder(filter), allPathSpecs[paths].trim(),
												intDispatches);
									}
								}
							}
						}
						catch (CoreException e)
						{
							IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadExtension, e);
						}
					}
				}
			}
		}
	}

	private IContextHandler getContextHandler(IConfigurationElement element)
	{
		IContextHandler handler = null;
		if (element.getAttribute(HANDLER_ATTR) != null)
		{
			Object oHandler;
			try
			{
				oHandler = element.createExecutableExtension(HANDLER_ATTR);
				if (oHandler instanceof IContextHandler)
				{
					handler = (IContextHandler) oHandler;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadContext, e);
			}

		}
		return handler;
	}

	private IStatisticsHandler getStatisticsHandler(IConfigurationElement element)
	{
		IStatisticsHandler handler = null;
		if (element.getAttribute(STATISTICS_ATTR) != null)
		{
			Object oHandler;
			try
			{
				oHandler = element.createExecutableExtension(STATISTICS_ATTR);
				if (oHandler instanceof IStatisticsHandler)
				{
					handler = (IStatisticsHandler) oHandler;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadStats, e);
			}

		}
		return handler;
	}

	private IRestartManager getRestartHandler(IConfigurationElement element)
	{
		IRestartManager handler = null;
		if (element.getAttribute(RESTART_ATTR) != null)
		{
			Object oHandler;
			try
			{
				oHandler = element.createExecutableExtension(RESTART_ATTR);
				if (oHandler instanceof IRestartManager)
				{
					handler = (IRestartManager) oHandler;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadRestart, e);
			}

		}
		return handler;
	}

	private IIgnoreHandler getIgnoreHandler(IConfigurationElement element)
	{
		IIgnoreHandler handler = null;
		if (element.getAttribute(IGNORE_ATTR) != null)
		{
			Object oHandler;
			try
			{
				oHandler = element.createExecutableExtension(IGNORE_ATTR);
				if (oHandler instanceof IIgnoreHandler)
				{
					handler = (IIgnoreHandler) oHandler;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadIgnore, e);
			}

		}
		return handler;
	}

	private IErrorPageHandler getErrorHandler(IConfigurationElement element)
	{
		IErrorPageHandler handler = null;
		if (element.getAttribute(ERROR_ATTR) != null)
		{
			Object oHandler;
			try
			{
				oHandler = element.createExecutableExtension(ERROR_ATTR);
				if (oHandler instanceof IErrorPageHandler)
				{
					handler = (IErrorPageHandler) oHandler;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logInfo(JettyPlugin.getDefault(), Messages.JettyServerBuilder_INF_LoadError, e);
			}

		}
		return handler;
	}

	/**
	 * Gets the instance of the builder
	 * 
	 * @return - builder
	 */
	public static JettyServerBuilder getInstance()
	{
		if (builder == null)
		{
			builder = new JettyServerBuilder();
		}
		return builder;
	}
}
