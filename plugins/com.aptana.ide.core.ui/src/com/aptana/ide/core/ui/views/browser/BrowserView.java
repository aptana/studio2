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
package com.aptana.ide.core.ui.views.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public abstract class BrowserView extends ViewPart
{
	protected BrowserViewer viewer;

	protected ISelectionListener listener;

	public void createPartControl(Composite parent) {
		viewer = new BrowserViewer(parent, 0);
		
		if(this.url == null)
		{
			this.url = getStartUrl();
		}
		setURL(this.url);
		
	}

	public void dispose() {
	}

	public void setFocus() {
		viewer.setFocus();
	}

	public boolean close() {
		try {
			getSite().getPage().hideView(this);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	String url = null;
	
	public String getURL() {
		if (viewer != null)
		{
			return viewer.getURL();
		}
		else
		{
			return url;
		}
	}
	
	public void setURL(String url) {
		if (viewer != null)
		{
			viewer.setURL(url);
		}
		this.url = url;
	}
	
	public void setText(String html) {
		if (viewer != null)
		{
			viewer.getBrowser().setText(html);
		}
	}
	
	/**
	 * execute
	 * 
	 * @param script
	 */
	public void execute(String script)
	{	
		if (viewer != null)
		{
			viewer.getBrowser().execute(script);
		}
	}
	
	public Browser getBrowser()
	{
		if (viewer != null)
		{
			return viewer.getBrowser();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Returns the startup URL (to display when this control initially loads
	 * 
	 * @return String
	 */
	public abstract String getStartUrl();

}
