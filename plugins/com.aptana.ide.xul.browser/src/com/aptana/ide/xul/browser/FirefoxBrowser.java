package com.aptana.ide.xul.browser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.mozilla.interfaces.nsICache;
import org.mozilla.interfaces.nsICacheService;
import org.mozilla.interfaces.nsIConsoleListener;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLScriptElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMSerializer;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIScriptError;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserSetup;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.browser.WebBrowserEditor;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;

/**
 * Contribute xul-based firefox browser
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FirefoxBrowser extends ContributedBrowser
{

	/**
	 * XULRUNNER_ENV
	 */
	public static final String XULRUNNER_ENV = "org.eclipse.swt.browser.XULRunnerPath"; //$NON-NLS-1$

	/**
	 * XULRUNNER_MAC_PLUGIN
	 */
	public static final String XULRUNNER_MAC_PLUGIN = "org.mozilla.xulrunner.carbon.macosx"; //$NON-NLS-1$

	/**
	 * XULRUNNER_WIN32_PLUGIN
	 */
	public static final String XULRUNNER_WIN32_PLUGIN = "org.mozilla.xulrunner.win32.win32.x86"; //$NON-NLS-1$

	/**
	 * XULRUNNER_PATH
	 */
	public static final String XULRUNNER_PATH = "/xulrunner"; //$NON-NLS-1$

	private static boolean useNativeMozilla = false;
	private static int nativeMozillaField = SWT.NONE;
	private static Class MozillaBrowser = null;

	static
	{
		// In Eclipse 3.4 (3.3 as well?) or higher, we may be able to just set
		// useNativeMozilla to true as it should always be there.
		// For now, we're not making this change in the interest of keeping
		// things working.
		if (true || CoreUIUtils.inEclipse34orHigher == true)
		{
			try
			{
				Field mozillaField = SWT.class.getField("MOZILLA"); //$NON-NLS-1$
				if (mozillaField.getType().equals(Integer.TYPE))
				{
					nativeMozillaField = mozillaField.getInt(SWT.class);
					useNativeMozilla = true;
				}
			}
			catch (Exception e)
			{
				useNativeMozilla = false;
			}
		}

		if (!useNativeMozilla)
		{
			Bundle bundle = Platform.getBundle("org.eclipse.swt"); //$NON-NLS-1$
			try
			{
				MozillaBrowser = bundle.loadClass("org.eclipse.swt.browser.MozillaBrowser"); //$NON-NLS-1$
				if (MozillaBrowser != null)
				{
					useNativeMozilla = false;
				}
			}
			catch (ClassNotFoundException e1)
			{
				IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Browser_Not_Found"), e1); //$NON-NLS-1$
				// falls back to use the SWT browser
				useNativeMozilla = true;
			}
		}
		
		if (useNativeMozilla) {
			FirefoxExtensionsSupport.init();
		}

	}

	private Composite errors;
	private Label errorIcon;
	private Label errorLabel;
	private Cursor hand;
	private int errorCount;
	private nsIConsoleListener errorListener = new nsIConsoleListener()
	{

		public nsISupports queryInterface(String arg0)
		{
			return null;
		}

		public void observe(nsIConsoleMessage message)
		{
			nsIScriptError error = (nsIScriptError) message.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
			if (error == null)
			{
				return;
			}
			if (browser == null || browser.isDisposed())
			{
				return;
			}
			long flag = error.getFlags();
			if ((flag == nsIScriptError.errorFlag || flag == nsIScriptError.exceptionFlag)
					&& error.getSourceName().equals(internalGetUrl()))
			{
				errorCount++;
				if (errorCount == 1)
				{
					String errorMessage = MessageFormat.format(Messages.getString("FirefoxBrowser.Error"), new Object[] { errorCount }); //$NON-NLS-1$
					errorIcon.setImage(Activator.getDefault().getImage(Activator.ERRORS_IMG_ID));
					errorLabel.setText(errorMessage);
					errorLabel.setToolTipText(Messages.getString("FirefoxBrowser.Errors_In_Page")); //$NON-NLS-1$
					errorIcon.setToolTipText(errorLabel.getToolTipText());
				}
				else
				{
					String errorMessage = MessageFormat.format(Messages.getString("FirefoxBrowser.Errors"), new Object[] { errorCount }); //$NON-NLS-1$
					errorLabel.setText(errorMessage);
				}
				errors.layout(true, true);
				errors.getParent().layout(true, true);
			}
		}

	};

	private ProgressListener progressListener = new ProgressListener()
	{

		public void changed(ProgressEvent event)
		{

		}

		public void completed(ProgressEvent event)
		{
			progressCompleted(event);
			handleProgressCompleted(event);
		}
	};
		
	private OpenWindowListener openWindowListener = new OpenWindowListener() {
		public void open(WindowEvent event) {
			if (!event.required || event.browser != null) {
				return;
			}
			WebBrowserEditor webBrowserEditor = WebBrowserEditor.openBlank();
			if (webBrowserEditor != null) {
				event.browser = webBrowserEditor.getBrowser();
			}
		}
	};

	private Composite browser;

	private nsIDOMDocument document;

	private ContributedOutline outline;

	private SelectionBox selectionBox = null;

	private Composite createSWTBrowser(Composite parent)
	{
		try
		{
			if (System.getProperty(XULRUNNER_ENV) == null)
			{
				Bundle bundle = null;
				if (CoreUIUtils.onWindows)
				{
					bundle = Platform.getBundle(XULRUNNER_WIN32_PLUGIN);
				}
				else if (CoreUIUtils.onMacOSX)
				{
					bundle = Platform.getBundle(XULRUNNER_MAC_PLUGIN);

				}
				if (bundle != null)
				{
					URL xulrunner = bundle.getEntry(XULRUNNER_PATH);
					if (xulrunner != null)
					{
						try
						{
							xulrunner = FileLocator.toFileURL(xulrunner);
							if (xulrunner != null)
							{
								File xulrunnerFolder = new File(xulrunner.getFile());
								String message = MessageFormat.format(
									Messages.getString("FirefoxBrowser.Setting_Path_To"), //$NON-NLS-1$
									new Object[] {
										xulrunnerFolder.getAbsolutePath()
									}
								);
								System.setProperty(XULRUNNER_ENV, xulrunnerFolder.getAbsolutePath());
								IdeLog.logInfo(Activator.getDefault(), message);
							}
						}
						catch (IOException e)
						{
							IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Error_Setting_Path"), e); //$NON-NLS-1$
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Error_Setting_Path"), e); //$NON-NLS-1$
		}
		browser = new Browser(parent, nativeMozillaField);
		((Browser) browser).addProgressListener(progressListener);
		((Browser) browser).addOpenWindowListener(openWindowListener);
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			nsIWebBrowserSetup webBrowserSetup = (nsIWebBrowserSetup) internalGetWebBrowser().queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
			if (webBrowserSetup != null) {
				webBrowserSetup.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 0);
			}
		}
		return browser;
	}

	private Composite createMozillaBrowser(Composite parent)
	{
		try
		{
			if (MozillaBrowser != null)
			{
				Constructor mozillaBrowserConstructor = MozillaBrowser.getConstructor(new Class[] { Composite.class,
						Integer.TYPE });
				Object mozillaBrowser = mozillaBrowserConstructor.newInstance(new Object[] { parent,
						Integer.valueOf(SWT.NONE) });
				Method addProgressListener = MozillaBrowser.getMethod("addProgressListener", //$NON-NLS-1$
						new Class[] { ProgressListener.class });
				addProgressListener.invoke(mozillaBrowser, new Object[] { progressListener });
				if (mozillaBrowser instanceof Composite)
				{
					browser = (Composite) mozillaBrowser;
				}
			}
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchMethodException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (InstantiationException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (InvocationTargetException e)
		{
		}
		return browser;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{

		if (useNativeMozilla)
		{
			browser = createSWTBrowser(parent);
		}
		else
		{
			browser = createMozillaBrowser(parent);
		}
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		errors = new Composite(parent, SWT.NONE);
		GridLayout eLayout = new GridLayout(2, false);
		eLayout.marginHeight = 1;
		eLayout.marginWidth = 1;
		eLayout.horizontalSpacing = 2;
		errors.setLayout(eLayout);
		errors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		hand = new Cursor(errors.getDisplay(), SWT.CURSOR_HAND);
		errorIcon = new Label(errors, SWT.LEFT);
		errorIcon.setCursor(hand);
		MouseAdapter showConsole = new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				ConsolePlugin.getDefault().getConsoleManager().showConsoleView(FirefoxConsole.getConsole());
			}

		};
		errorIcon.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		errorIcon.addMouseListener(showConsole);
		errorLabel = new Label(errors, SWT.LEFT);
		errorLabel.setCursor(hand);
		errorLabel.addMouseListener(showConsole);
		errorLabel.setForeground(errorLabel.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
		errorLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, true));
		nsIConsoleService service = (nsIConsoleService) Mozilla.getInstance().getServiceManager()
				.getServiceByContractID("@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID); //$NON-NLS-1$
		service.registerListener(errorListener);
		// Hook console
		FirefoxConsole.getConsole();
	}

	private void internalRefresh()
	{
		if (browser instanceof Browser)
		{
			((Browser) browser).refresh();
		}
		else
		{
			Method refresh;
			try
			{
				refresh = browser.getClass().getMethod("refresh", new Class[0]); //$NON-NLS-1$
				refresh.invoke(browser, new Object[0]);
			}
			catch (SecurityException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#refresh()
	 */
	public void refresh()
	{
		if (browser != null && !browser.isDisposed())
		{
			clearCache();
			clearErrors();
			internalRefresh();
		}
	}

	private void clearCache()
	{
		if (UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.CACHE_BUST_BROWSERS))
		{
			try
			{
				nsICacheService cache = (nsICacheService) Mozilla.getInstance().getServiceManager()
						.getServiceByContractID("@mozilla.org/network/cache-service;1", //$NON-NLS-1$
								nsICacheService.NS_ICACHESERVICE_IID);
				cache.evictEntries(nsICache.STORE_ANYWHERE);
			}
			catch (Exception e)
			{
				if (e instanceof XPCOMException && ((XPCOMException)e).errorcode == Mozilla.NS_ERROR_FILE_NOT_FOUND) {
					/*Not an error since disk cache wasn't created yet  */
					return;
				}
				IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Error_Clearing_Cache"), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#dispose()
	 */
	public void dispose()
	{
		browser.dispose();
		if (hand != null && !hand.isDisposed())
		{
			hand.dispose();
		}
		errors.dispose();
		nsIConsoleService service = (nsIConsoleService) Mozilla.getInstance().getServiceManager()
				.getServiceByContractID("@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID); //$NON-NLS-1$
		service.unregisterListener(errorListener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#getControl()
	 */
	public Control getControl()
	{
		return browser;
	}

	private void clearErrors()
	{
		errorCount = 0;
		errorIcon.setImage(Activator.getDefault().getImage(Activator.NO_ERRORS_IMG_ID));
		errorLabel.setText(""); //$NON-NLS-1$
		errorLabel.setToolTipText(""); //$NON-NLS-1$
		errorIcon.setToolTipText(Messages.getString("FirefoxBrowser.No_Errors_On_Page")); //$NON-NLS-1$
		errors.layout(true, true);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#setURL(java.lang.String)
	 */
	public void setURL(String url)
	{
		clearCache();
		clearErrors();
		internalSetUrl(url);
	}

	private void internalSetUrl(String url)
	{
		if (browser instanceof Browser)
		{
			((Browser) browser).setUrl(url);
		}
		else
		{
			Method setUrl;
			try
			{
				setUrl = browser.getClass().getMethod("setUrl", new Class[] { String.class }); //$NON-NLS-1$
				setUrl.invoke(browser, new Object[] { url });
			}
			catch (SecurityException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
	}

	private String internalGetUrl()
	{
		if (browser instanceof Browser)
		{
			return ((Browser) browser).getUrl();
		}
		else
		{
			Method getUrl;
			try
			{
				getUrl = browser.getClass().getMethod("getUrl", new Class[0]); //$NON-NLS-1$
				Object retVal = getUrl.invoke(browser, new Object[0]);
				if (retVal instanceof String)
				{
					return (String) retVal;
				}
			}
			catch (SecurityException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
		return null;
	}

	/**
	 * Gets the DOM document object
	 * 
	 * @return - dom document
	 */
	public nsIDOMDocument getDocument()
	{
		return this.document;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#setOutline(com.aptana.ide.editors.unified.ContributedOutline)
	 */
	public void setOutline(ContributedOutline outline)
	{
		this.outline = outline;
	}

	private void handleProgressCompleted(ProgressEvent event)
	{
		document = internalGetDocument();
		if (document != null)
		{
			selectionBox = new SelectionBox(document);
		}
		else
		{
			IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Cannot_Get_Document")); //$NON-NLS-1$
		}
		if (outline != null)
		{
			outline.refresh();
		}
	}

	/**
	 * Highlights an element in this browser
	 * 
	 * @param element -
	 *            element to highlight
	 */
	public void highlightElement(nsIDOMNode element)
	{
		if (element.getNodeType() == nsIDOMNode.ELEMENT_NODE)
		{
			selectionBox.highlight((nsIDOMElement) element.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
		}
		else
		{
			selectionBox.hide();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#back()
	 */
	public void back()
	{
		if (browser instanceof Browser)
		{
			((Browser) browser).back();
		}
		else
		{
			Method back;
			try
			{
				back = browser.getClass().getMethod("back", new Class[0]); //$NON-NLS-1$
				back.invoke(browser, new Object[0]);
			}
			catch (SecurityException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#forward()
	 */
	public void forward()
	{
		if (browser instanceof Browser)
		{
			((Browser) browser).forward();
		}
		else
		{
			Method forward;
			try
			{
				forward = browser.getClass().getMethod("forward", new Class[0]); //$NON-NLS-1$
				forward.invoke(browser, new Object[0]);
			}
			catch (SecurityException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#getUnderlyingBrowserObject()
	 */
	public Object getUnderlyingBrowserObject()
	{
		return browser;
	}

	private nsIWebBrowser internalGetWebBrowser()
	{
		try
		{
			Method getWebBrowser = browser.getClass().getMethod("getWebBrowser", new Class[0]); //$NON-NLS-1$
			Object retVal = getWebBrowser.invoke(browser, new Object[0]);
			if (retVal instanceof nsIWebBrowser)
			{
				return (nsIWebBrowser) retVal;
			}
		}
		catch (Exception e)
		{
		}
		return null;
	}

	private nsIDOMDocument internalGetDocument()
	{
		nsIWebBrowser webBrowser = internalGetWebBrowser();
		nsIDOMDocument nsidomdocument = null;
		if (webBrowser != null)
		{
			nsIDOMWindow nsidomwindow = webBrowser.getContentDOMWindow();
			nsidomdocument = nsidomwindow.getDocument();
		}
		return nsidomdocument;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#execute(java.lang.String)
	 */
	public boolean execute(String script)
	{
		nsIDOMDocument document = internalGetDocument();
		if (document != null)
		{
			nsIDOMElement se = document.createElement("script"); //$NON-NLS-1$
			nsIDOMHTMLScriptElement scriptBlock = (nsIDOMHTMLScriptElement) se
					.queryInterface(nsIDOMHTMLScriptElement.NS_IDOMHTMLSCRIPTELEMENT_IID);
			String s2 = "if(" + script + "){" + "document.getElementById('execute').setAttribute('text','success');}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			scriptBlock.setText(s2);
			nsIDOMElement executeBlock = document.getElementById("execute"); //$NON-NLS-1$
			if (executeBlock == null)
			{
				executeBlock = document.createElement("div"); //$NON-NLS-1$
				executeBlock.setAttribute("id", "execute"); //$NON-NLS-1$ //$NON-NLS-2$
				nsIDOMNode body = document.getElementsByTagName("body").item(0); //$NON-NLS-1$
				body.appendChild(executeBlock);
			}
			executeBlock.setAttribute("text", ""); //$NON-NLS-1$ //$NON-NLS-2$
			nsIDOMNode head = document.getElementsByTagName("head").item(0); //$NON-NLS-1$
			head.appendChild(scriptBlock);
			executeBlock = document.getElementById("execute"); //$NON-NLS-1$
			return "success".equals(executeBlock.getAttribute("text")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#displaySource()
	 */
	public void displaySource()
	{
		if (document != null)
		{
			nsIDOMSerializer serializer = (nsIDOMSerializer) Mozilla.getInstance().getComponentManager()
					.createInstanceByContractID("@mozilla.org/xmlextras/xmlserializer;1", null, //$NON-NLS-1$
							nsIDOMSerializer.NS_IDOMSERIALIZER_IID);
			String source = serializer.serializeToString(document.getDocumentElement());
			try
			{
				final String newFileName = FileUtils.getRandomFileName("source", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
				final File temp = new File(FileUtils.systemTempDir + File.separator + newFileName);
				FileUtils.writeStringToFile(source, temp);
				UIJob openJob = new UIJob(Messages.getString("FirefoxBrowser.Open_Source_Editor")) //$NON-NLS-1$
				{

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						IEditorInput input = CoreUIUtils.createJavaFileEditorInput(temp);
						try
						{
							IDE.openEditor(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
									.getActivePage(), input, IDE.getEditorDescriptor(newFileName).getId());
						}
						catch (PartInitException e)
						{
							e.printStackTrace();
						}
						return Status.OK_STATUS;
					}

				};
				openJob.schedule();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}

    /**
     * @see com.aptana.ide.core.ui.browser.IBrowser#addLocationListener(org.eclipse.swt.browser.LocationListener)
     */
    public void addLocationListener(LocationListener listener)
    {
        if (browser instanceof Browser)
        {
            ((Browser) browser).addLocationListener(listener);
        }
        else
        {
            try {
                Method addLocationListener = MozillaBrowser.getMethod(
                        "addLocationListener", //$NON-NLS-1$
                        new Class[] { LocationListener.class });
                addLocationListener.invoke(browser, new Object[] { listener });
            } catch (SecurityException e) {
                error(e);
            } catch (NoSuchMethodException e) {
                error(e);
            } catch (IllegalArgumentException e) {
                error(e);
            } catch (IllegalAccessException e) {
                error(e);
            } catch (InvocationTargetException e) {
                error(e);
            }
        }
    }

    /**
     * @see com.aptana.ide.core.ui.browser.IBrowser#removeLocationListener(org.eclipse.swt.browser.LocationListener)
     */
    public void removeLocationListener(LocationListener listener)
    {
        if (browser instanceof Browser)
        {
            ((Browser) browser).removeLocationListener(listener);
        }
        else
        {
            try {
                Method removeLocationListener = MozillaBrowser.getMethod(
                        "removeLocationListener", //$NON-NLS-1$
                        new Class[] { LocationListener.class });
                removeLocationListener.invoke(browser, new Object[] { listener });
            } catch (SecurityException e) {
                error(e);
            } catch (NoSuchMethodException e) {
                error(e);
            } catch (IllegalArgumentException e) {
                error(e);
            } catch (IllegalAccessException e) {
                error(e);
            } catch (InvocationTargetException e) {
            	// ignores this exception since it could happen on exit
            }
        }
    }

    private void error(Exception e)
    {
        IdeLog.logError(Activator.getDefault(), Messages.getString("FirefoxBrowser.Cannot_Interact_With_Browser"), e); //$NON-NLS-1$
    }
}
