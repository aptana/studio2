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
package com.aptana.ide.editor.html.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.nodes.HTMLDocumentNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLParseNodeTypes;
import com.aptana.ide.editor.js.outline.JSContentProvider;
import com.aptana.ide.editor.js.outline.JSOutlineItem;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.views.outline.IOutlineContentProviderExtension;
import com.aptana.ide.views.outline.IPathResolver;
import com.aptana.ide.views.outline.IResolvableItem;
import com.aptana.ide.views.outline.UnifiedOutlineProvider;

/**
 * @author Kevin Lindsey
 * @author Pavel Petrochenko
 */
public class HTMLContentProvider implements ITreeContentProvider, IOutlineContentProviderExtension
{
	private static final Object[] NO_OBJECTS = new Object[0];

	private IPathResolver resolver;

	private Map<String, Object[]> cache = new WeakHashMap<String, Object[]>();
	private HashMap<String, IPropertyChangeListener> listeners = new HashMap<String, IPropertyChangeListener>();

	private JSContentProvider provider;

	/**
	 * XMLContentProvider
	 */
	public HTMLContentProvider()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = NO_OBJECTS;

		if (parentElement instanceof HTMLElementNode)
		{
			HTMLElementNode item = (HTMLElementNode) parentElement;

			// addition to allow expansion of script tags with src attribute;
			if (item.getName().equals("script")) { //$NON-NLS-1$
				String attribute = item.getAttribute("src"); //$NON-NLS-1$
				if (attribute.length() > 0)
				{
					return getExternalScriptChildren(attribute,item);
				}
			}
			result = UnifiedOutlineProvider.getInstance().getChildren(item, HTMLMimeType.MimeType);
		}
		return result;
	}

	private Object[] getExternalScriptChildren(final String attribute, IResolvableItem parent)
	{
		Object[] cached = cache.get(attribute);
		if (cached != null)
		{
			//we have a cached result
			return cached;
		}

		if (resolver == null)
		{
			return NO_OBJECTS;
		}
		try
		{
			//registering listener
			IPropertyChangeListener propertyChangeListener = listeners.get(attribute);
			if (propertyChangeListener == null)
			{
				propertyChangeListener = new IPropertyChangeListener()
				{

					public void propertyChange(PropertyChangeEvent event)
					{
						cache.remove(attribute);
					}

				};
				resolver.addChangeListener(attribute, propertyChangeListener);
			}
			listeners.put(attribute, propertyChangeListener);
			
			//resolving source and editor input
			String source = resolver.resolveSource(attribute);
			IEditorInput input = resolver.resolveEditorInput(attribute);
			if (source == null)
			{
				return new Object[] { new WarningItem(StringUtils.format(
						HTMLContentProviderMessages.HTMLContentProvider_NOT_RESOLVABLE, attribute)) };
			}
			JSParser ps;
			try
			{
				//parsing
				ps = new JSParser();
				IParseState pState = ps.createParseState(null);
				pState.setEditState(source, source, 0, 0);
				IParseNode parse = ps.parse(pState);
				provider = new JSContentProvider();
				
				//acquiring items
				Object[] elements = provider.getElements(parse.getChildren());
				for (int a = 0; a < elements.length; a++)
				{
					if (elements[a] instanceof JSOutlineItem)
					{
						JSOutlineItem item = (JSOutlineItem) elements[a];
						item.setResolveInformation(input);
						item.setParent(parent);
					}
				}
				
				//caching result
				cache.put(attribute, elements);
				return elements;
			}
			catch (ParserInitializationException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
				return NO_OBJECTS;
			}
			catch (LexerException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
				return NO_OBJECTS;
			}

		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
			WarningItem warningItem = new WarningItem(e.getMessage());
			warningItem.setError(true);
			return new Object[] { warningItem };
		}

	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		Object result = null;

		if (element instanceof HTMLElementNode)
		{
			HTMLElementNode item = (HTMLElementNode) element;

			result = item.getParent();
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;

		if (element instanceof HTMLElementNode)
		{

			// addition to allow expansion of script tags with src attribute;
			HTMLElementNode elementNode = ((HTMLElementNode) element);
			if (isScriptWithSrc(elementNode))
			{
				if (resolver != null)
				{
					elementNode.setResolveInformation(resolver.resolveEditorInput(elementNode.getAttribute("src"))); //$NON-NLS-1$
					return true;
				}				
			}
			result = elementNode.hasChildren();
		}
		return result;
	}

	private boolean isScriptWithSrc(HTMLElementNode elementNode)
	{
		if (elementNode.getName().equals("script")) { //$NON-NLS-1$
			String attribute = elementNode.getAttribute("src"); //$NON-NLS-1$
			if (attribute.length() > 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List<Object> elements = new ArrayList<Object>();

		if (inputElement instanceof HTMLDocumentNode)
		{
			// process root node
			this.processNode(elements, (IParseNode) inputElement);
		}

		return elements.toArray(new Object[elements.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput == null)
		{
			disconnectListeners();
		}
		cache.clear();
	}

	private void disconnectListeners()
	{
		if (resolver != null)
		{
			for (Iterator<String> iterator = listeners.keySet().iterator(); iterator.hasNext();)
			{
				String key = iterator.next();
				IPropertyChangeListener object = listeners.get(key);
				resolver.removeChangeListener(key, object);
			}
			listeners.clear();
		}
	}

	/**
	 * processNode
	 * 
	 * @param elements
	 * @param node
	 */
	private void processNode(List<Object> elements, IParseNode node)
	{
		switch (node.getTypeIndex())
		{
			case HTMLParseNodeTypes.DOCUMENT:
				for (int i = 0; i < node.getChildCount(); i++)
				{
					this.processNode(elements, node.getChild(i));
				}
				break;

			case HTMLParseNodeTypes.ELEMENT:
				elements.add(node);
				break;

			default:
				if (!node.getLanguage().equals(HTMLMimeType.MimeType))
				{
					Object[] list = UnifiedOutlineProvider.getInstance().getElements(node);
					if (list != null)
					{
						for (int i = 0; i < list.length; i++)
						{
							elements.add(list[i]);
						}
					}
				}
				break;
		}
	}

	/**
	 * @see com.aptana.ide.views.outline.IOutlineContentProviderExtension#setPathResolver(com.aptana.ide.views.outline.IPathResolver)
	 */
	public void setPathResolver(IPathResolver resolver)
	{
		if (this.resolver != null)
		{
			if (!this.resolver.equals(resolver))
			{
				disconnectListeners();
				cache.clear();
			}
		}
		this.resolver = resolver;
	}

	/**
	 * @see com.aptana.ide.views.outline.IOutlineContentProviderExtension#pageActivated(com.aptana.ide.views.outline.IPathResolver)
	 */
	public void pageActivated(IPathResolver _resolver)
	{
		setPathResolver(resolver);
	}

	/**
	 * @see com.aptana.ide.views.outline.IOutlineContentProviderExtension#pageClosed(com.aptana.ide.views.outline.IPathResolver)
	 */
	public void pageClosed(IPathResolver _resolver)
	{
		disconnectListeners();
		cache.clear();
	}
}
