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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.core;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility functions for formatting dates/times
 * 
 * @author Ingo Muschenetz
 * @author Kevin Lindsey
 */
public final class DateUtils
{
	/**
	 * Protected constructor for utility class
	 */
	protected DateUtils()
	{
	}

	/**
	 * Computes a friendlier time offset string
	 * 
	 * @param offset
	 * @return String
	 */
	public static String getTimeOffsetString(long offset)
	{
		long seconds = offset / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		if (Math.abs(seconds) < 60)
		{
			return StringUtils.format(Messages.DateUtils_seconds, seconds);
		}
		else if (Math.abs(minutes) < 60)
		{
			return StringUtils.format(Messages.DateUtils_minutes, minutes);
		}
		else
		{
			return StringUtils.format(Messages.DateUtils_hours, hours);
		}
	}
	
	/**
	 * roundDownToMinuteInterval
	 * 
	 * @param millis
	 * @param minuteInterval
	 * @return
	 */
	public static long roundDownToMinuteInterval(long millis, int minuteInterval)
	{
		long result = millis;
		
		if (minuteInterval > 0)
		{
			Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
			
			// set time
			calendar.setTimeInMillis(millis);
			
			// zero out seconds and milliseconds
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			
			// get current minutes
			int minutes = calendar.get(Calendar.MINUTE);
			
			// find how far we are away from the last interval
			int remainder = minutes % minuteInterval;
			
			// back up to interval start
			if (remainder > 0)
			{
				calendar.add(Calendar.MINUTE, -remainder);
			}
			
			result = calendar.getTimeInMillis();
		}
		
		return result;
	}
	
	/**
	 * addDayInterval
	 * 
	 * @param millis
	 * @param dayInterval
	 * @return
	 */
	public static long addDayInterval(long millis, int dayInterval)
	{
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		
		// set time
		calendar.setTimeInMillis(millis);
		
		// do math
		calendar.add(Calendar.DAY_OF_MONTH, dayInterval);
		
		return calendar.getTimeInMillis();
	}
	
	/**
	 * addMinuteInterval
	 * 
	 * @param millis
	 * @param minuteInterval
	 * @return
	 */
	public static long addMinuteInterval(long millis, int minuteInterval)
	{
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		
		// set time
		calendar.setTimeInMillis(millis);
		
		// do match
		calendar.add(Calendar.MINUTE, minuteInterval);
		
		return calendar.getTimeInMillis();
	}
}
