/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.core.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class WizardPageExtensionLoader
{

	/**
	 * ID_ATTR
	 */
	public static final String ID_ATTR = "id"; //$NON-NLS-1$

	/**
	 * CLASS_ATTR
	 */
	public static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	private static Map<String, List<WizardPageFactory>> pages;

	private WizardPageExtensionLoader()
	{
		// Does nothing
	}

	/**
	 * Creates wizard pages registered for the wizard id
	 * 
	 * @param wizard
	 * @param id -
	 *            wizard id
	 * @return - wizard pages
	 */
	public static IWizardPage[] createWizardPages(BaseWizard wizard, String id)
	{
		if (pages == null)
		{
			loadPages();
		}
		List<WizardPageFactory> elements = pages.get(id);
		if (elements != null)
		{
			List<IWizardPage> wizardPages = new ArrayList<IWizardPage>();
			for (WizardPageFactory element : elements)
			{
				IWizardPage page = element.createWizardPage(wizard);
				if (page != null)
				{
					wizardPages.add(page);
				}
			}
			Collections.sort(wizardPages, new Comparator<IWizardPage>()
			{

				public int compare(IWizardPage w1, IWizardPage w2)
				{
					return w1.getName().compareTo(w2.getName());
				}

			});
			return wizardPages.toArray(new IWizardPage[0]);
		}
		else
		{
			return new IWizardPage[0];
		}

	}

	private static void loadPages()
	{
		pages = new HashMap<String, List<WizardPageFactory>>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(CoreUIPlugin.WIZARD_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				String id = ce[j].getAttribute(ID_ATTR);
				String className = ce[j].getAttribute(CLASS_ATTR);
				if (id != null && className != null)
				{
					List<WizardPageFactory> elements = pages.get(id);
					if (elements == null)
					{
						elements = new ArrayList<WizardPageFactory>();
						pages.put(id, elements);
					}
					try
					{
						Object obj = ce[j].createExecutableExtension(CLASS_ATTR);
						if (obj instanceof WizardPageFactory)
						{
							elements.add((WizardPageFactory) obj);
						}
					}
					catch (CoreException e)
					{
						IdeLog.logInfo(CoreUIPlugin.getDefault(),
						        WizardMessages.WizardPageExtensionLoader_INF_ErrorCreatingWizardPage, e);
					}
				}
			}
		}
	}
}
