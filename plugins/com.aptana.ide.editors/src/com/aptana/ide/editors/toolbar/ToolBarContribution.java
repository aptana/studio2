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
package com.aptana.ide.editors.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import com.aptana.ide.editors.unified.InstanceCreator;

/**
 * Contribution to toolbar registry
 * @author Pavel Petrochenko
 */
public class ToolBarContribution
{
	
	private ImageDescriptor icon;
	
	private String tooltipText;
	
	private String text;

	/**
	 * @param text
	 * @param tooltip
	 * @param desc
	 * @param creator
	 * Constructor allowing lazy creation of IToolBarMembe instance
	 */
	public ToolBarContribution(String text, String tooltip, ImageDescriptor desc, InstanceCreator creator)
	{
		this.text = text;
		this.tooltipText = tooltip;
		this.icon = desc;
		this.instance = creator;
		// if (creator==null){
		// throw new IllegalArgumentException("creator should not be null"); //$NON-NLS-1$
		// }
	}

	/**
	 * @param text
	 * @param tooltip
	 * @param desc
	 * @param member
	 * Simple constructor
	 */
	public ToolBarContribution(String text, String tooltip, ImageDescriptor desc, IToolBarMember member)
	{
		this.text = text;
		this.tooltipText = tooltip;
		this.icon = desc;
		this.member = member;
		if (member == null)
		{
			throw new IllegalArgumentException("member should not be null"); //$NON-NLS-1$
		}
	}

	

	private InstanceCreator instance;
	private IToolBarMember member;

	/**
	 * @return underlying worker
	 */
	public IToolBarMember getInstance()
	{
		if (member != null)
		{
			return member;
		}
		member = (IToolBarMember) instance.createInstance();
		return member;
	}

	/**
	 * @return image descriptor
	 */
	public ImageDescriptor getIcon()
	{
		return icon;
	}

	

	/**
	 * @return tooltip text
	 */
	public String getTooltipText()
	{
		return tooltipText;
	}

	

	/**
	 * @return text
	 */
	public String getText()
	{
		return text;
	}

	

}