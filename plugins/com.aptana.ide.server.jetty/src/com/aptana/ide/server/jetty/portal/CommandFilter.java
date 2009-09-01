package com.aptana.ide.server.jetty.portal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.model.user.UserRequestBuilder;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.server.jetty.JettyPlugin;

/**
 * Used by the new web based deployment wizard to exceute commands e.g. trackevent, signin, signout.
 * 
 * @author Sandip Chitale
 */
public class CommandFilter implements Filter
{
	private static String PARAMETER_COMMAND = "command"; //$NON-NLS-1$

	private static String VALUE_TRACK_EVENT = "trackevent"; //$NON-NLS-1$

	private static String PARAMETER_CODE = "code"; //$NON-NLS-1$
	private static String PARAMETER_DATA = "data"; //$NON-NLS-1$

	private static String VALUE_SIGNIN = "signin"; //$NON-NLS-1$

	private static String PARAMETER_USERNAME = "username"; //$NON-NLS-1$
	private static String PARAMETER_PASSWORD = "password"; //$NON-NLS-1$

	private static String VALUE_SIGNOUT = "signout"; //$NON-NLS-1$
	
	private static String VALUE_OPENURL = "openurl"; //$NON-NLS-1$
	
	private static String PARAMETER_URL = "url"; //$NON-NLS-1$
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		
		chain.doFilter(request, response);
		
		HttpServletResponse resp = (HttpServletResponse)response;
		
		String command = request.getParameter(PARAMETER_COMMAND);		
		if(command != null) {
			if (VALUE_SIGNIN.equals(command)) {
				String username = request.getParameter(PARAMETER_USERNAME);
				String password = request.getParameter(PARAMETER_PASSWORD);
				if (username != null && password != null) {
					boolean shouldSignIn = true;
					User user = AptanaUser.getSignedInUser();
					if (user != null && user.hasCredentials()) {
					    if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
					        // the specified user is already signed in, no
					        // need to do anything
					        shouldSignIn = false;
					    } else {
					        // signs the current user out first
					        AptanaUser.signOut();
					    }
					}
					if (shouldSignIn) {
					    // signs the new user in
					    final User newUser = new User(username, password, null, null,
					            null, null, null);
					    newUser.setDefaultLocation(new URL(AptanaUser.LOGINS));
					    newUser.setServiceProvider(new RESTServiceProvider());
					    newUser.setRequestBuilder(new UserRequestBuilder());
					    // gets the user location
					    newUser.update();
					    if (newUser.hasLocation()) {
					        // gets the user model
					        newUser.update();
					        // signs in the user in the UI thread to avoid issue
					        // http://support.aptana.com/asap/browse/STU-4438
					        Display.getDefault().syncExec(new Runnable()
					        {

                                public void run()
                                {
                                    AptanaUser.signIn(newUser.getUsername(),
                                            newUser.getPassword(), newUser
                                                    .getLocation(), newUser
                                                    .getId());
                                }

					        });
					    }
					}
				} else {
					IdeLog.logImportant(JettyPlugin.getDefault(),
							VALUE_SIGNIN + " command did not required parameters " + PARAMETER_USERNAME + " and " + PARAMETER_PASSWORD); //$NON-NLS-1$  //$NON-NLS-2$
				}
			} else if (VALUE_SIGNOUT.equals(command)) {
				User user = AptanaUser.getSignedInUser();
				if (user != null && user.hasCredentials()) {
					AptanaUser.signOut();
				}
			} else if (VALUE_OPENURL.equals(command)) {
				String url = request.getParameter(PARAMETER_URL);
				if (url != null) {
					CoreUIUtils.openBrowserURL(url);
				}
			} else if (VALUE_TRACK_EVENT.equals(command)) {
				String eventCode = request.getParameter(PARAMETER_CODE);
				String eventData = request.getParameter(PARAMETER_DATA);
				
				if(eventCode != null && eventCode.length() > 0)
				{
					EventLogger.getInstance().logEvent(eventCode, eventData);
				} 
				else
				{
					IdeLog.logImportant(JettyPlugin.getDefault(),
							"Got null or empty event code from event tracking filter."); //$NON-NLS-1$
				}
			}
		}
		
		resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate, post-check=0, pre-check=0"); //HTTP 1.1 //$NON-NLS-1$ //$NON-NLS-2$
		resp.setHeader("Pragma","no-cache"); //HTTP 1.0 //$NON-NLS-1$  //$NON-NLS-2$
		resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server //$NON-NLS-1$
	}

	public void init(FilterConfig arg0) throws ServletException {}
	public void destroy() {}
}
