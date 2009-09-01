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
package com.aptana.ide.editor.xml.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editor.xml.parsing.nodes.XMLElementNode;
import com.aptana.ide.editor.xml.preferences.IPreferenceConstants;
import com.aptana.ide.views.outline.UnifiedOutlineProvider;

/**
 * @author Kevin Lindsey
 */
public class XMLLabelProvider extends LabelProvider implements IPropertyChangeListener
{
	private static final Image ELEMENT_ICON = XMLPlugin.getImage("icons/element_icon.gif"); //$NON-NLS-1$
	private String[] _attributeNames;

	/**
	 * XMLLabelProvider
	 */
	public XMLLabelProvider()
	{
		// get XML preference store
		IPreferenceStore prefStore = XMLPlugin.getDefault().getPreferenceStore();

		// add this object as a listener
		prefStore.addPropertyChangeListener(this);

		// get attribute name list from preferences
		this.getAttributeNameList();
	}

	/**
	 * getAttributeNameList
	 */
	private void getAttributeNameList()
	{
		// get XML preference store
		IPreferenceStore prefStore = XMLPlugin.getDefault().getPreferenceStore();

		// grab list of attribute names
		String result = prefStore.getString(IPreferenceConstants.XMLEDITOR_OUTLINER_ATTRIBUTE_LIST);

		this._attributeNames = result.split("\\s+,\\s*|\\s*,\\s+|,|\\s+"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result;

		if (element instanceof XMLElementNode)
		{
			result = ELEMENT_ICON;
		}
		else
		{
			result = super.getImage(element);
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result;

		if (element instanceof XMLElementNode)
		{
			XMLElementNode node = (XMLElementNode) element;
			result = node.getName();

			if (this._attributeNames.length > 0)
			{
				List<String> values = new ArrayList<String>();

				for (int i = 0; i < this._attributeNames.length; i++)
				{
					String attributeName = this._attributeNames[i];

					if (node.hasAttribute(attributeName))
					{
						values.add(node.getAttribute(attributeName));
					}
				}

				if (values.size() > 0)
				{
					String[] valueStrings = (String[]) values.toArray(new String[values.size()]);

					result += " : " + StringUtils.join(" | ", valueStrings); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		else
		{
			result = super.getText(element);
		}
		if (result != null)
		{
			try
			{
				result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			catch (Exception e)
			{
				// Don't let any regex errors bubble up to user since this is on UI-thread
			}
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IPreferenceConstants.XMLEDITOR_OUTLINER_ATTRIBUTE_LIST))
		{
			// get attribute name list from preferences
			this.getAttributeNameList();

			// refresh tree
			UnifiedOutlineProvider.getInstance().refresh();
		}
	}
}
