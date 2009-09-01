package com.aptana.ide.core;

import java.net.URL;

/**
 * MultipleAuthenticationFilter
 */
public class MultipleAuthenticationFilter implements IAuthentificationULRFilter
{

	private IAuthentificationULRFilter[] filters;

	/**
	 * MultipleAuthenticationFilter
	 * 
	 * @param filters
	 */
	public MultipleAuthenticationFilter(IAuthentificationULRFilter[] filters)
	{
		this.filters = filters;
	}

	/**
	 * @see com.aptana.ide.core.IAuthentificationULRFilter#requiresCheck(java.net.URL)
	 */
	public boolean requiresCheck(URL url)
	{
		for (int i = 0; i < filters.length; i++)
		{
			if (!filters[i].requiresCheck(url))
				return false;
		}
		return true;
	}

}
