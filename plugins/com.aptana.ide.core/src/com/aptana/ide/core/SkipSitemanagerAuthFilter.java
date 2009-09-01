package com.aptana.ide.core;

import java.net.URL;

import com.aptana.ide.core.model.RESTServiceProvider;

/**
 * SkipSitemanagerAuthFilter
 */
public class SkipSitemanagerAuthFilter implements IAuthentificationULRFilter
{

	/**
	 * @see com.aptana.ide.core.IAuthentificationULRFilter#requiresCheck(java.net.URL)
	 */
	public boolean requiresCheck(URL url)
	{
		String host = url.getHost();
		if (host.equals("sitemanager.aptana.com")) //$NON-NLS-1$
		{
			return false;
		}
		if (host.equals(RESTServiceProvider.DEBUG_HOST))
		{
			return false;
		}
		return true;
	}

}
