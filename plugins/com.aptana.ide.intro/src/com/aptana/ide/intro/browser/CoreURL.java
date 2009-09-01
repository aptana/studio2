/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.browser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.internal.intro.impl.model.AbstractIntroElement;
import org.eclipse.ui.internal.intro.impl.model.AbstractIntroPage;
import org.eclipse.ui.internal.intro.impl.model.IntroLaunchBarElement;
import org.eclipse.ui.internal.intro.impl.model.IntroModelRoot;
import org.eclipse.ui.internal.intro.impl.model.IntroPartPresentation;
import org.eclipse.ui.internal.intro.impl.model.IntroTheme;
import org.eclipse.ui.internal.intro.impl.model.loader.ExtensionPointManager;
import org.eclipse.ui.internal.intro.impl.model.loader.ModelLoaderUtil;
import org.eclipse.ui.internal.intro.impl.model.util.ModelUtil;
import org.eclipse.ui.internal.intro.impl.presentations.BrowserIntroPartImplementation;
import org.eclipse.ui.internal.intro.impl.presentations.IntroLaunchBar;
import org.eclipse.ui.internal.intro.impl.util.DialogUtil;
import org.eclipse.ui.internal.intro.impl.util.StringUtil;
import org.eclipse.ui.internal.intro.impl.util.Util;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.config.CustomizableIntroPart;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.intro.config.IIntroURL;

import com.aptana.ide.core.ui.RectangleAnimation;

/**
 * Core URL for internal Eclipse URLs
 * 
 * @author Kevin Sawicki
 */
public class CoreURL implements IIntroURL
{

	/**
	 * INTRO_PROTOCOL
	 */
	public static final String INTRO_PROTOCOL = "http"; //$NON-NLS-1$

	/**
	 * INTRO_HOST_ID
	 */
	public static final String INTRO_HOST_ID = "org.eclipse.ui.intro"; //$NON-NLS-1$

	/**
	 * Constants that represent Intro URL actions.
	 */
	public static final String SET_STANDBY_MODE = "setStandbyMode"; //$NON-NLS-1$

	/**
	 * SHOW_STANDBY
	 */
	public static final String SHOW_STANDBY = "showStandby"; //$NON-NLS-1$

	/**
	 * CLOSE
	 */
	public static final String CLOSE = "close"; //$NON-NLS-1$

	/**
	 * SHOW_HELP_TOPIC
	 */
	public static final String SHOW_HELP_TOPIC = "showHelpTopic"; //$NON-NLS-1$

	/**
	 * SHOW_HELP
	 */
	public static final String SHOW_HELP = "showHelp"; //$NON-NLS-1$

	/**
	 * OPEN_BROWSER
	 */
	public static final String OPEN_BROWSER = "openBrowser"; //$NON-NLS-1$

	/**
	 * OPEN_URL
	 */
	public static final String OPEN_URL = "openURL"; //$NON-NLS-1$

	/**
	 * RUN_ACTION
	 */
	public static final String RUN_ACTION = "runAction"; //$NON-NLS-1$

	/**
	 * SHOW_PAGE
	 */
	public static final String SHOW_PAGE = "showPage"; //$NON-NLS-1$

	/**
	 * SHOW_MESSAGE
	 */
	public static final String SHOW_MESSAGE = "showMessage"; //$NON-NLS-1$

	/**
	 * NAVIGATE
	 */
	public static final String NAVIGATE = "navigate"; //$NON-NLS-1$

	/**
	 * SWITCH_TO_LAUNCH_BAR
	 */
	public static final String SWITCH_TO_LAUNCH_BAR = "switchToLaunchBar"; //$NON-NLS-1$

	/**
	 * EXECUTE
	 */
	public static final String EXECUTE = "execute"; //$NON-NLS-1$

	/**
	 * KEY_ID
	 */
	public static final String KEY_ID = "id"; //$NON-NLS-1$

	/**
	 * KEY_PLUGIN_ID
	 */
	public static final String KEY_PLUGIN_ID = "pluginId"; //$NON-NLS-1$

	/**
	 * KEY_CLASS
	 */
	public static final String KEY_CLASS = "class"; //$NON-NLS-1$

	/**
	 * KEY_STANDBY
	 */
	public static final String KEY_STANDBY = "standby"; //$NON-NLS-1$

	/**
	 * KEY_PART_ID
	 */
	public static final String KEY_PART_ID = "partId"; //$NON-NLS-1$

	/**
	 * KEY_INPUT
	 */
	public static final String KEY_INPUT = "input"; //$NON-NLS-1$

	/**
	 * KEY_MESSAGE
	 */
	public static final String KEY_MESSAGE = "message"; //$NON-NLS-1$

	/**
	 * KEY_URL
	 */
	public static final String KEY_URL = "url"; //$NON-NLS-1$

	/**
	 * KEY_DIRECTION
	 */
	public static final String KEY_DIRECTION = "direction"; //$NON-NLS-1$

	/**
	 * KEY_EMBED
	 */
	public static final String KEY_EMBED = "embed"; //$NON-NLS-1$

	/**
	 * KEY_EMBED_TARGET
	 */
	public static final String KEY_EMBED_TARGET = "embedTarget"; //$NON-NLS-1$

	/**
	 * KEY_DECODE
	 */
	public static final String KEY_DECODE = "decode"; //$NON-NLS-1$

	/**
	 * KEY_COMAND
	 */
	public static final String KEY_COMAND = "command"; //$NON-NLS-1$

	/**
	 * VALUE_BACKWARD
	 */
	public static final String VALUE_BACKWARD = "backward"; //$NON-NLS-1$

	/**
	 * VALUE_FORWARD
	 */
	public static final String VALUE_FORWARD = "forward"; //$NON-NLS-1$

	/**
	 * VALUE_HOME
	 */
	public static final String VALUE_HOME = "home"; //$NON-NLS-1$

	/**
	 * VALUE_TRUE
	 */
	public static final String VALUE_TRUE = "true"; //$NON-NLS-1$

	/**
	 * VALUE_FALSE
	 */
	public static final String VALUE_FALSE = "false"; //$NON-NLS-1$

	private String action = null;
	private Properties parameters = null;
	private Browser browser;

	/**
	 * Prevent creation. Must be created through an IntroURLParser. This constructor assumed we have a valid intro url.
	 * 
	 * @param url
	 */
	CoreURL(String action, Properties parameters)
	{
		this.action = action;
		this.parameters = parameters;
	}

	/**
	 * Sets the browser to open urls in
	 * 
	 * @param browser
	 */
	public void setBrowser(Browser browser)
	{
		this.browser = browser;
	}

	/**
	 * Executes whatever valid Intro action is embedded in this Intro URL.
	 * 
	 * @return - true if executed
	 */
	public boolean execute()
	{
		final boolean[] result = new boolean[1];
		Display display = Display.getCurrent();

		BusyIndicator.showWhile(display, new Runnable()
		{

			public void run()
			{
				result[0] = doExecute();
			}
		});
		return result[0];
	}

	/**
	 * Do execute
	 * 
	 * @return - true if executed
	 */
	protected boolean doExecute()
	{

		if (action.equals(SHOW_HELP))
		{
			// display the full Help System.
			return showHelp();
		}
		else if (action.equals(SHOW_HELP_TOPIC))
		{
			// display a Help System Topic. It can be displayed in the Help
			// system window, or embedded as an intro page.
			// return showHelpTopic(getParameter(KEY_ID));
			return showHelpTopic(getParameter(KEY_ID), getParameter(KEY_EMBED), getParameter(KEY_EMBED_TARGET));
		}
		else if (action.equals(OPEN_BROWSER))
		{
			// display url in external browser
			return openBrowser(getParameter(KEY_URL), getParameter(KEY_PLUGIN_ID));
		}

		if (action.equals(OPEN_URL))
		{
			// display url embedded in intro browser.
			return openURL(getParameter(KEY_URL), getParameter(KEY_PLUGIN_ID));
		}
		else if (action.equals(RUN_ACTION))
		{
			// run an Intro action. Get the pluginId and the class keys. Pass
			// the parameters and the standby state.
			return runAction(getParameter(KEY_PLUGIN_ID), getParameter(KEY_CLASS), parameters,
					getParameter(KEY_STANDBY));
		}
		else if (action.equals(EXECUTE))
		{
			// execute a serialized command
			return executeCommand(getParameter(KEY_COMAND), getParameter(KEY_STANDBY));
		}
		else if (action.equals(SHOW_PAGE))
		{
			// display an Intro Page.
			return showPage(getParameter(KEY_ID), getParameter(KEY_STANDBY));
		}
		else if (action.equals(SHOW_MESSAGE))
		{
			return showMessage(getParameter(KEY_MESSAGE));
		}
		else if (action.equals(NAVIGATE))
		{
			return navigate(getParameter(KEY_DIRECTION));
		}
		else if (action.equals(SWITCH_TO_LAUNCH_BAR))
		{
			return switchToLaunchBar();
		}
		else
		{
			return handleCustomAction();
		}
	}

	/**
	 * Run an action
	 */
	private boolean runAction(String pluginId, String className, Properties parameters, String standbyState)
	{

		Object actionObject = ModelLoaderUtil.createClassInstance(pluginId, className);
		try
		{
			if (actionObject instanceof IIntroAction)
			{
				IIntroAction introAction = (IIntroAction) actionObject;
				introAction.run(null, parameters);
			}
			else if (actionObject instanceof IAction)
			{
				IAction action = (IAction) actionObject;
				action.run();

			}
			else if (actionObject instanceof IActionDelegate)
			{
				final IActionDelegate delegate = (IActionDelegate) actionObject;
				if (delegate instanceof IWorkbenchWindowActionDelegate)
				{
					((IWorkbenchWindowActionDelegate) delegate).init(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
				}
				Action proxy = new Action(this.action)
				{

					public void run()
					{
						delegate.run(this);
					}
				};
				proxy.run();
			}
			else
			{
				// we could not create the class.
				return false;
			}

		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Executes a serialized <code>ParameterizedCommand</code>. Uses {@link ICommandService#deserialize(String)} to
	 * convert the <code>command</code> argument into the parameterized command.
	 */
	private boolean executeCommand(String command, String standbyState)
	{
		ICommandService commandService = getCommandService();
		if (commandService == null)
		{
			return false;
		}

		try
		{
			ParameterizedCommand pCommand = commandService.deserialize(command);
			pCommand.executeWithChecks(null, null);

		}
		catch (CommandException ex)
		{
			return false;
		}
		return true;
	}

	private ICommandService getCommandService()
	{
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null)
		{
			Object serviceObject = wb.getAdapter(ICommandService.class);
			if (serviceObject != null)
			{
				ICommandService service = (ICommandService) serviceObject;
				return service;
			}
		}
		return null;
	}

	/**
	 * Open a help topic. If embed="true", open the help href as an intro page. If false, open the href in the Help
	 * system window. If embedTarget is set, then the Help System topic is embedded instead of the content of the
	 * specified div.<br>
	 * In the case of SWT presentation, embedd flag is ignored and the topic is opened in the Help system window.
	 */
	private boolean showHelpTopic(String href, String embed, String embedTarget)
	{
		if (href == null)
		{
			return false;
		}

		boolean isEmbedded = (embed != null && embed.equals(VALUE_TRUE)) ? true : false;
		if (isEmbedded == false)
		{
			// still false, check the embedTarget. If embedTarget is set, then
			// we
			// have embedded by default.
			isEmbedded = (embedTarget != null) ? true : false;
		}

		if (isEmbedded)
		{

			href = PlatformUI.getWorkbench().getHelpSystem().resolve(href, true).toExternalForm();

			if (embedTarget == null && browser != null)
			{
				return browser.setUrl(href);
			}

			// embedded in Div case.
			IntroModelRoot model = IntroPlugin.getDefault().getIntroModelRoot();
			return handleEmbedURLInDiv(href, embedTarget, model.getCurrentPage());
		}

		// show href in Help window. SWT presentation is handled here.
		// WorkbenchHelp takes care of error handling.
		PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(href);
		return true;
	}

	private boolean handleEmbedURLInDiv(String href, String embedTarget, AbstractIntroPage currentPage)
	{

		// re-use a runtime generated page, if found. Create the mangled id for
		// the page and check if page exists first. If not, create one.
		IntroModelRoot model = (IntroModelRoot) currentPage.getParentPage().getParent();
		String currentPageId = null;
		if (currentPage.isIFramePage())
		{
			currentPageId = currentPage.getUnmangledId();
		}
		else
		{
			currentPageId = currentPage.getId();
		}
		String mangledPageId = currentPageId + "_" + "WITH_IFRAME"; //$NON-NLS-1$ //$NON-NLS-2$

		// get current standby state.
		boolean standby = IntroPlugin.isIntroStandby();
		String standbyAsString = standby ? CoreURL.VALUE_TRUE : "false"; //$NON-NLS-1$

		AbstractIntroPage pageWithIFrame = (AbstractIntroPage) model.findChild(mangledPageId,
				AbstractIntroElement.ABSTRACT_PAGE);
		if (pageWithIFrame != null)
		{
			pageWithIFrame.setIFrameURL(href);
			return this.showPage(mangledPageId, standbyAsString);
		}

		// Page never generated, clone and create.
		AbstractIntroPage clonedPage = null;
		try
		{
			clonedPage = (AbstractIntroPage) currentPage.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			// should never be here.
			return false;
		}

		// embed url as IFrame in target div. We need to find target div in
		// cloned page not in the original page.
		boolean canInjectFrame = clonedPage.injectIFrame(href, embedTarget);
		if (!canInjectFrame)
		{
			// Called method handles error.
			return false;
		}

		clonedPage.setId(mangledPageId);
		model.addChild(clonedPage);
		return this.showPage(clonedPage.getId(), standbyAsString);
	}

	/**
	 * Open the help system.
	 */
	private boolean showHelp()
	{
		PlatformUI.getWorkbench().getHelpSystem().displayHelp();
		return true;
	}

	/**
	 * Launch external browser.
	 */
	private boolean openBrowser(String url, String pluginId)
	{
		// no need to decode url because we will create another url from this
		// url anyway. Resolve the url just in case we are trying to load a
		// plugin relative file.
		url = ModelUtil.resolveURL(url, pluginId);
		return Util.openBrowser(url);
	}

	/**
	 * Show a URL in an intro page. This is the embedded version of the intro action openBrowser(). It is useful when
	 * trying to show an html file relative to another plugin. When the presentation is UI forms presentation, this call
	 * behaves exactly as the openBrowser intro action.
	 */

	private boolean openURL(String url, String pluginId)
	{
		IntroModelRoot model = IntroPlugin.getDefault().getIntroModelRoot();
		String presentationStyle = model.getPresentation().getImplementationKind();

		if (presentationStyle.equals(IntroPartPresentation.BROWSER_IMPL_KIND))
		{
			// HTML presentation
			url = ModelUtil.resolveURL(url, pluginId);
			BrowserIntroPartImplementation impl = (BrowserIntroPartImplementation) IntroPlugin.getDefault()
					.getIntroModelRoot().getPresentation().getIntroPartImplementation();
			Browser browser = impl.getBrowser();
			return browser.setUrl(url);
		}
		{
			// SWT presentation.
			return openBrowser(url, pluginId);
		}
	}

	private boolean showMessage(String message)
	{
		if (message == null)
		{
			return false;
		}
		DialogUtil.displayInfoMessage(null, message);
		return true;
	}

	/**
	 * Display an Intro Page.
	 * <p>
	 * INTRO: revisit picking first page.
	 */
	boolean showPage(String pageId, String standbyState)
	{
		// set the current page id in the model. This will triger appropriate
		// listener event to the UI. If setting the page in the model fails (ie:
		// the page was not found in the current model, look for it in loaded
		// models. return false if failed.
		// avoid flicker.
		CustomizableIntroPart currentIntroPart = (CustomizableIntroPart) IntroPlugin.getIntro();
		currentIntroPart.getControl().setRedraw(false);

		IntroModelRoot modelRoot = IntroPlugin.getDefault().getIntroModelRoot();
		boolean success = modelRoot.setCurrentPageId(pageId);
		if (!success)
		{
			success = includePageToShow(modelRoot, pageId);
		}

		// we turned drawing off. Turn it on again.
		currentIntroPart.getControl().setRedraw(true);

		if (success)
		{
			// found page. Set the history
			modelRoot.getPresentation().updateHistory(modelRoot.getCurrentPage());

		}
		// could not find referenced page.
		return false;
	}

	/**
	 * Finds the target page and includes it in passed model.
	 * 
	 * @param pageId
	 * @return - boolean
	 */
	private boolean includePageToShow(IntroModelRoot model, String pageId)
	{
		AbstractIntroPage page = findPageToShow(pageId);
		if (page == null)
		{
			return false;
		}
		// now clone the target page because original model should be kept
		// intact. Resolve target page first to resolve its includes
		// properly. Insert presentation shared style at the top of the shared
		// styles list because once reparented, the shared style is lost.
		// Finally, add clone page to current model.
		page.getChildren();
		// current kind.
		String currentPresentationKind = model.getPresentation().getImplementationKind();
		// load shared style corresponding to same presentation kind from target
		// model.
		IntroPartPresentation targetPresentation = ((IntroModelRoot) page.getParent()).getPresentation();
		String targetSharedStyle = targetPresentation.getSharedStyle(currentPresentationKind);
		// clone.
		AbstractIntroPage clonedPage = null;
		try
		{
			clonedPage = (AbstractIntroPage) page.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			// should never be here.
			return false;
		}
		// reparent cloned target to current model.
		clonedPage.setParent(model);
		// REVISIT: SWT presentation does not support multiple shared
		// styles.
		if (targetSharedStyle != null)
		{
			// add target model shared style.
			clonedPage.insertStyle(targetSharedStyle, 0);
		}
		model.addChild(clonedPage);
		return model.setCurrentPageId(clonedPage.getId());
	}

	/**
	 * Searches all loaded models for the first page with the given id.
	 * 
	 * @param pageId
	 * @return
	 */
	private AbstractIntroPage findPageToShow(String pageId)
	{
		// get all cached models.
		Hashtable models = ExtensionPointManager.getInst().getIntroModels();
		Enumeration values = models.elements();
		while (values.hasMoreElements())
		{
			IntroModelRoot model = (IntroModelRoot) values.nextElement();
			AbstractIntroPage page = (AbstractIntroPage) model.findChild(pageId, AbstractIntroElement.ABSTRACT_PAGE);
			if (page != null)
			{
				return page;
			}
		}
		// could not find page in any model.
		return null;
	}

	/**
	 * Navigate foward in the presentation, whichever one it is.
	 * 
	 * @return
	 */
	private boolean navigate(String direction)
	{
		// set intro to standby mode. we know we have a customizable part.
		CustomizableIntroPart introPart = (CustomizableIntroPart) IntroPlugin.getIntro();
		if (introPart == null)
		{
			// intro is closed. Do nothing.
			return false;
		}

		IntroPartPresentation presentation = (IntroPartPresentation) introPart.getAdapter(IntroPartPresentation.class);

		if (direction.equalsIgnoreCase(VALUE_BACKWARD))
		{
			return presentation.navigateBackward();
		}
		else if (direction.equalsIgnoreCase(VALUE_FORWARD))
		{
			return presentation.navigateForward();
		}
		else if (direction.equalsIgnoreCase(VALUE_HOME))
		{
			return presentation.navigateHome();
		}
		return false;
	}

	/**
	 * @return Returns the action imbedded in this URL.
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Return a parameter defined in the Intro URL. Returns null if the parameter is not defined. If this intro url has
	 * a decode=true parameter, then all parameters are returned decoded using UTF-8.
	 * 
	 * @param parameterId
	 * @return
	 */
	public String getParameter(String parameterId)
	{
		// make sure to decode only on return, since we may need to recreate the
		// url when handling custom urls.
		String value = parameters.getProperty(parameterId);
		String decode = parameters.getProperty(KEY_DECODE);

		if (value != null)
		{
			try
			{
				if (decode != null && decode.equalsIgnoreCase(VALUE_TRUE))
				{
					// we are told to decode the parameters of the url through
					// the decode parameter. Assume that parameters are
					// UTF-8 encoded.
					return StringUtil.decode(value, "UTF-8"); //$NON-NLS-1$
				}
				return value;
			}
			catch (Exception e)
			{
				// should never be here.
			}
		}
		return value;
	}

	private boolean handleCustomAction()
	{
		String replaces = null;
		if (action.equals("installPlugin")) //$NON-NLS-1$
		{
			replaces = "runAction?pluginId=com.aptana.ide.extras&class=com.aptana.ide.extras.plugins.InstallPlugin"; //$NON-NLS-1$
		}
		else if (action.equals("disablePlugin")) //$NON-NLS-1$
		{
			replaces = "runAction?pluginId=com.aptana.ide.extras&class=com.aptana.ide.extras.plugins.DisablePlugin"; //$NON-NLS-1$
		}
		if (replaces != null)
		{

			// custom command. execute it.
			StringBuffer url = new StringBuffer();
			url.append("http://org.eclipse.ui.intro/"); //$NON-NLS-1$
			url.append(replaces);
			// command already has parameters.
			url.append("&"); //$NON-NLS-1$
			url.append(retrieveInitialQuery());
			URLParser parser = new URLParser(url.toString());
			parser.getIntroURL().doExecute();
		}
		return false;
	}

	/**
	 * Recreate the initial query passed to this URL.
	 * 
	 * @return
	 */
	private String retrieveInitialQuery()
	{
		StringBuffer query = new StringBuffer();
		Enumeration keys = parameters.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			query.append(key);
			query.append("="); //$NON-NLS-1$
			query.append(parameters.get(key));
			if (keys.hasMoreElements())
			{
				query.append("&"); //$NON-NLS-1$
			}
		}
		return query.toString();
	}

	private boolean switchToLaunchBar()
	{
		IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
		if (intro == null)
		{
			return false;
		}

		CustomizableIntroPart cpart = (CustomizableIntroPart) intro;
		IntroModelRoot modelRoot = IntroPlugin.getDefault().getIntroModelRoot();
		String pageId = modelRoot.getCurrentPageId();
		IntroTheme theme = modelRoot.getTheme();
		Rectangle bounds = cpart.getControl().getBounds();
		Rectangle startBounds = Geometry.toDisplay(cpart.getControl().getParent(), bounds);

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IntroLaunchBarElement launchBarElement = modelRoot.getPresentation().getLaunchBarElement();
		if (launchBarElement == null)
		{
			return true;
		}
		IntroLaunchBar launchBar = new IntroLaunchBar(launchBarElement.getOrientation(), pageId, launchBarElement,
				theme);
		launchBar.createInActiveWindow();
		Rectangle endBounds = Geometry
				.toDisplay(launchBar.getControl().getParent(), launchBar.getControl().getBounds());

		RectangleAnimation animation = new RectangleAnimation(window.getShell(), startBounds, endBounds);
		animation.schedule();
		return true;
	}
}
