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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mozilla.xpcom.XPCOMException;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMWindowInternal;

/*
 * ContentProvider for the nsIDOM elements
 */
public class DOMContentProvider implements ITreeContentProvider
{

	protected nsIDOMDocument input = null;

	protected HashMap documentToFrameMap = new HashMap();

	// this is a special not that is inserted to show a message in the DOMInspector Tree
	protected class MessageNode
	{

		public String message = ""; //$NON-NLS-1$
		public Object parent = null;

		public MessageNode(String message, Object parent)
		{
			this.message = message;
			this.parent = parent;

		}
	}

	public Object[] getChildren(Object parentElement)
	{

		if (parentElement == null)
		{
			return new Object[0];
		}
		else if (parentElement == input)
		{
			return new Object[] { input.getElementsByTagName("HTML").item(0) }; //$NON-NLS-1$
		}

		else if (parentElement instanceof MessageNode)
		{
			return new Object[] {}; // MessageNode objects have no children
		}

		else
		{
			nsIDOMNode node = (nsIDOMNode) parentElement;

			if (node.getNodeType() == nsIDOMNode.DOCUMENT_NODE)
				return new Object[] { ((nsIDOMDocument) node).getDocumentElement() };

			else
			{

				// special case of for IFRAME, FRAME
				if ("FRAME".equalsIgnoreCase(node.getNodeName())) //$NON-NLS-1$
				{

					nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement) node
							.queryInterface(nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID);
					nsIDOMDocument frameDoc = frame.getContentDocument();

					if (frameDoc != null)
					{
						// important to keep this relationship because there is no way to map the document back to the
						// frame through API
						documentToFrameMap.put(frameDoc, frame);

						return new Object[] { frameDoc };
					}
					else
						return new Object[] {};

				}

				else if ("IFRAME".equalsIgnoreCase(node.getNodeName())) //$NON-NLS-1$
				{
					nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement) node
							.queryInterface(nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID);
					nsIDOMDocument iframeDoc = iframe.getContentDocument();

					if (iframeDoc != null)
					{
						// important to keep this relationship because there is no way to map the document back to the
						// frame through API
						documentToFrameMap.put(iframeDoc, iframe);
						return new Object[] { iframeDoc };
					}
					else
						return new Object[] {};
				}
				else
				{

					nsIDOMNodeList childrenList = node.getChildNodes();
					ArrayList children = new ArrayList();
					for (int i = 0; i < (int) childrenList.getLength(); i++)
					{
						if (childrenList.item(i).getNodeType() == nsIDOMNode.ELEMENT_NODE)
						{
							children.add(childrenList.item(i));
						}
					}
					return children.toArray();
				}
			}

		}
	}

	public Object getParent(Object element)
	{

		// return null if at the root
		if (element == input)
		{
			return null;
		}

		// this is the case when a message is showing
		else if (element instanceof MessageNode)
		{
			return ((MessageNode) element).parent;

		}
		else
		{
			Object parent = null;
			nsIDOMNode node = (nsIDOMNode) element;

			if (node.getNodeType() == nsIDOMNode.DOCUMENT_NODE)
			{

				nsIDOMDocument doc = (nsIDOMDocument) node.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);
				// need to check if the document is inside of a FRAME/IFRAME

				// first check to see if it's been mapped
				if (documentToFrameMap.containsKey(doc))
				{
					parent = documentToFrameMap.get(doc);
				}
				else
				{
					// lookup for FRAME/IFRAME
					try
					{
						nsIDOMDocumentView documentView = (nsIDOMDocumentView) doc
								.queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);

						nsIDOMAbstractView defaultDocView = documentView.getDefaultView();

						if (defaultDocView != null)
						{
							nsIDOMWindowInternal iWin = (nsIDOMWindowInternal) documentView.getDefaultView()
									.queryInterface(nsIDOMWindowInternal.NS_IDOMWINDOWINTERNAL_IID);
							parent = iWin.getFrameElement();

							// cache it
							if (parent != null)
							{
								documentToFrameMap.put(doc, parent);
							}
						}
					}
					catch (XPCOMException e)
					{
						// do nothing, at this point assume it is the root document
					}
				}

				if (parent == null)
				{
					parent = input;
				}
			}
			else
			{
				parent = node.getParentNode();
			}

			return parent;
		}
	}

	public boolean hasChildren(Object element)
	{

		if (element instanceof MessageNode)
		{
			return false;
		}
		else
		{
			nsIDOMNode node = (nsIDOMNode) element;

			if ("FRAME".equalsIgnoreCase(node.getNodeName())) //$NON-NLS-1$
			{

				nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement) node
						.queryInterface(nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID);
				nsIDOMDocument frameDoc = frame.getContentDocument();

				// important to keep this relationship because there is no way to map the document back to the frame
				// through API
				documentToFrameMap.put(frameDoc, frame);
				return frameDoc != null;
			}

			else if ("IFRAME".equalsIgnoreCase(node.getNodeName())) //$NON-NLS-1$
			{
				nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement) node
						.queryInterface(nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID);
				nsIDOMDocument iframeDoc = iframe.getContentDocument();

				// important to keep this relationship because there is no way to map the document back to the frame
				// through API
				documentToFrameMap.put(iframeDoc, iframe);
				return iframeDoc != null;
			}
			else
			{
				if (node.hasChildNodes())
				{
					nsIDOMNodeList list = node.getChildNodes();
					for (int i = 0; i < list.getLength(); i++)
					{
						if (list.item(i).getNodeType() == nsIDOMNode.ELEMENT_NODE)
						{
							return true;
						}
					}
				}
				return false;
			}
		}
	}

	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	public void dispose()
	{

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.input = (nsIDOMDocument) newInput;
		documentToFrameMap.clear();
	}
}
