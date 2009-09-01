/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.documentation.views;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.documentation.DocumentationPlugin;

public class TutorialView extends ViewPart {
	
	public static final String ID = "com.aptana.ide.documentation.TutorialView"; //$NON-NLS-1$
	
	protected Browser browser;
	
	public TutorialView() {
	}

	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		String url = Platform.getPreferencesService().getString(DocumentationPlugin.PLUGIN_ID, DocumentationPlugin.GETTING_STARTED_CONTENT_URL, null, null);
		if (url != null)
			browser.setUrl(url);
		browser.addLocationListener(new LocationListener() {

			public void changed(LocationEvent event) {
			}

			public void changing(LocationEvent event) {
				String location = event.location;
				// For absolute URLs with http or https protocols 
				if (location.startsWith("http:") || location.startsWith("https:")) { //$NON-NLS-1$ //$NON-NLS-2$
					// Launch external browser
					CoreUIUtils.openBrowserURL(location);
					event.doit = false;
					EventLogger.getInstance().logEvent("c.tv ", location); //$NON-NLS-1$
				}
			}
			
		});
		EventLogger.getInstance().logEvent("c.tv ", "opened"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void dispose() {
		super.dispose();
		EventLogger.getInstance().logEvent("c.tv ", "closed"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setFocus() {
	}
	
	public void setUrl(String url) {
		browser.setUrl(url);
	}

}
