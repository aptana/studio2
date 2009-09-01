/*******************************************************************
 *
 * Licensed Materials - Property of IBM
 * 
 * AJAX Toolkit Framework 6-28-496-8128
 * 
 * (c) Copyright IBM Corp. 2006 All Rights Reserved.
 * 
 * U.S. Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 *******************************************************************/
package com.aptana.ide.xul;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.xul.DOMContentProvider.MessageNode;

public class DOMLabelProvider extends LabelProvider implements IPropertyChangeListener
{
	
	public Preferences prefs = null;
	public String attrs;
	
	public DOMLabelProvider() {
		prefs = HTMLPlugin.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(this);
		attrs = prefs.getString(IPreferenceConstants.HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST);
	}

	public Image getImage(Object element)
	{

		switch (((nsIDOMNode) element).getNodeType())
		{

			case nsIDOMNode.ELEMENT_NODE:
				return XULPlugin.getDefault().getImage(XULPlugin.ELEMENT_IMG_ID);

			case nsIDOMNode.TEXT_NODE:
				return XULPlugin.getDefault().getImage(XULPlugin.TEXT_IMG_ID);

			default:
				return null;
		}
	}

	public String getText(Object element)
	{

		// MessageNode(s) do not have images
		if (element instanceof MessageNode)
			return ((MessageNode) element).message;

		nsIDOMNode node = (nsIDOMNode) element;

		StringBuffer buf = new StringBuffer();

		buf.append(node.getNodeName());

		// try to get the id if available
		if (node.getNodeType() == nsIDOMNode.ELEMENT_NODE)
		{
			try
			{
				nsIDOMElement e = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				String[] attributes = attrs.split(" "); //$NON-NLS-1$
				boolean addedColon = false;
				boolean firstAlreadyAdded = false;
				for( int i = 0; i < attributes.length; i++ ) {
					String domAttr = e.getAttribute(attributes[i]);
					if (!(domAttr == null || "".equals(domAttr))) //$NON-NLS-1$
					{
						if( !addedColon ) {
							buf.append(" : "); //$NON-NLS-1$
							addedColon = true;
						}
						
						if( firstAlreadyAdded ) {
							buf.append(" | "); //$NON-NLS-1$
						} else {
							firstAlreadyAdded = true;
						}
						buf.append(domAttr);
					}
				}
				
			}
			catch (Exception e)
			{
				// do nothing
			}
		}

		return buf.toString().toLowerCase();
	}

	public void propertyChange(PropertyChangeEvent event)
	{
		if( event.getProperty().equals(IPreferenceConstants.HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST) ) {
			attrs = (String)event.getNewValue();
		}
	}
}
