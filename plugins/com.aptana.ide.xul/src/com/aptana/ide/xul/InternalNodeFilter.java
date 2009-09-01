package com.aptana.ide.xul;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

import com.aptana.ide.xul.browser.Activator;

/**
 * Filter for internal nodes
 * @author Kevin Sawicki (ksawicki@aptana.com)
 *
 */
public class InternalNodeFilter extends ViewerFilter
{

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return !(isInternal(parentElement) || isInternal(element));
	}

	private boolean isInternal(Object element)
	{
		if (element instanceof nsIDOMNode && ((nsIDOMNode) element).getNodeType() == nsIDOMNode.ELEMENT_NODE)
		{
			nsIDOMElement domElement = (nsIDOMElement) (((nsIDOMNode) element)
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));

			return Activator.INTERNAL_ID.equals(domElement.getAttribute("class")); //$NON-NLS-1$
		}

		return false;

	}

}
