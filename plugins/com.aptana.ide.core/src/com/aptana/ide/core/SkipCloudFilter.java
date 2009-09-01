package com.aptana.ide.core;

import java.net.URL;

/**
 * SkipCloudFilter
 */
public class SkipCloudFilter implements IAuthentificationULRFilter
{

	/**
	 * @see com.aptana.ide.core.IAuthentificationULRFilter#requiresCheck(java.net.URL)
	 */
	public boolean requiresCheck(URL url)
	{
		String host = url.getHost();
		if (host.endsWith("aptanacloud.com")) //$NON-NLS-1$
			return false;
		return true;
	}

}
