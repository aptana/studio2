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
package com.aptana.ide.views.outline;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.InstanceCreator;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public final class UnifiedOutlineProvider implements ITreeContentProvider, ILabelProvider, IColorProvider,
		IFontProvider, ILabelDecorator
{
	/**
	 * @author Kevin Lindsey
	 */
	private class Providers
	{
		/**
		 * contentProvider
		 */
		public ITreeContentProvider contentProvider;

		/**
		 * labelProvider
		 */
		public ILabelProvider labelProvider;

		/**
		 * colorProvider
		 */
		public IColorProvider colorProvider;

		/**
		 * fontProvider
		 */
		public IFontProvider fontProvider;

		/**
		 * labelDecorator
		 */
		public ILabelDecorator labelDecorator;

		private List<FilterActionInfo> _filters;
		private List<OutlineRefreshHandler> _beforeRefreshHandlers;

		/**
		 * isSortable
		 */
		public boolean isSortable;

		/**
		 * Providers
		 * 
		 * @param contentProvider
		 * @param labelProvider
		 * @param isSortable
		 */
		public Providers(ITreeContentProvider contentProvider, ILabelProvider labelProvider, boolean isSortable)
		{
			this.contentProvider = contentProvider;
			this.labelProvider = labelProvider;
			this.isSortable = isSortable;

			if (labelProvider instanceof IColorProvider)
			{
				this.colorProvider = (IColorProvider) labelProvider;
			}
			if (labelProvider instanceof IFontProvider)
			{
				this.fontProvider = (IFontProvider) labelProvider;
			}
			if (labelProvider instanceof ILabelDecorator)
			{
				this.labelDecorator = (ILabelDecorator) labelProvider;
			}
		}

		/**
		 * addBeforeRefreshHandler
		 * 
		 * @param handler
		 */
		public void addBeforeRefreshHandler(OutlineRefreshHandler handler)
		{
			if (this._beforeRefreshHandlers == null)
			{
				this._beforeRefreshHandlers = new ArrayList<OutlineRefreshHandler>();
			}

			this._beforeRefreshHandlers.add(handler);
		}

		/**
		 * addFilter
		 * 
		 * @param filter
		 * @param icon
		 */
		public void addFilter(String name, String toolTip, InstanceCreator filterCreator,
				ImageDescriptor imageDescriptor)
		{
			if (this._filters == null)
			{
				this._filters = new ArrayList<FilterActionInfo>();
			}

			this._filters.add(new FilterActionInfo(name, toolTip, filterCreator, imageDescriptor));
		}

		/**
		 * fireBeforeRefreshEvent
		 * 
		 * @param fileContext
		 */
		public void fireBeforeRefreshEvent(EditorFileContext fileContext)
		{
			if (this._beforeRefreshHandlers != null)
			{
				for (OutlineRefreshHandler handler : this._beforeRefreshHandlers)
				{
					handler.run(fileContext);
				}
			}
		}

		/**
		 * getFilters
		 * 
		 * @return
		 */
		public FilterActionInfo[] getFilters()
		{
			FilterActionInfo[] result = NO_FILTERS;

			if (this._filters != null)
			{
				result = this._filters.toArray(new FilterActionInfo[this._filters.size()]);
			}

			return result;
		}

		/**
		 * removeBeforeRefreshHandler
		 * 
		 * @param handler
		 */
		public void removeBeforeRefreshHandler(OutlineRefreshHandler handler)
		{
			if (this._beforeRefreshHandlers != null)
			{
				this._beforeRefreshHandlers.remove(handler);
			}
		}
	}

	private static final FilterActionInfo[] NO_FILTERS = new FilterActionInfo[0];

	private static UnifiedOutlineProvider instance;

	private static final Object[] NO_OBJECTS = new Object[0];
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final String OUTLINE_ID = "outline"; //$NON-NLS-1$

	private static final String TAG_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String TAG_CONTRIBUTOR_EXTENSION = "contributor-extension"; //$NON-NLS-1$
	private static final String TAG_FILTER = "filter"; //$NON-NLS-1$

	private static final String ATTR_BEFORE_REFRESH_HANDLER = "before-refresh-handler"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_COLOR_PROVIDER = "color-provider"; //$NON-NLS-1$
	private static final String ATTR_CONTENT_PROVIDER = "content-provider"; //$NON-NLS-1$
	private static final String ATTR_FONT_PROVIDER = "font-provider"; //$NON-NLS-1$
	private static final String ATTR_LABEL_PROVIDER = "label-provider"; //$NON-NLS-1$
	private static final String ATTR_LABEL_DECORATOR = "label-decorator"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGES = "languages"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_IS_SORTABLE = "is-sortable"; //$NON-NLS-1$
	private static final String ATTR_TOOL_TIP = "tool-tip"; //$NON-NLS-1$

	/**
	 * getInstance
	 * 
	 * @return UnifiedOutlineProvider
	 */
	public static UnifiedOutlineProvider getInstance()
	{
		if (instance == null)
		{
			instance = new UnifiedOutlineProvider();
			instance.loadExtensions();
		}

		return instance;
	}

	private IUnifiedOutlinePage _outlinePage;

	private Map<String, String> _privateMemberPrefixes;
	private Map<String, Providers> _providersByLanguage;
	private Providers _currentProviders;

	private String _currentLanguage;

	private IPathResolver _resolver;
	private WeakReference<Object> _oldEditorInput = new WeakReference<Object>(null);

	/**
	 * UnifiedOutlineProvider
	 */
	public UnifiedOutlineProvider()
	{
		this._providersByLanguage = new HashMap<String, Providers>();
	}

	/**
	 * addBeforeRefreshHandler
	 * 
	 * @param language
	 * @param handler
	 */
	public void addBeforeRefreshHandler(String language, OutlineRefreshHandler handler)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.addBeforeRefreshHandler(handler);
		}
	}

	/**
	 * addFilter
	 * 
	 * @param language
	 * @param filter
	 * @param icon
	 */
	public void addFilter(String language, String name, String toolTip, InstanceCreator filterCreator,
			ImageDescriptor imageDescriptor)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.addFilter(name, toolTip, filterCreator, imageDescriptor);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
		if (this._currentProviders != null)
		{
			ILabelProvider labelProvider = this._currentProviders.labelProvider;

			if (labelProvider != null)
			{
				labelProvider.addListener(listener);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element)
	{
		Image result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ILabelDecorator labelDecorator = this._currentProviders.labelDecorator;

			if (labelDecorator != null)
			{
				result = labelDecorator.decorateImage(image, element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element)
	{
		String result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ILabelDecorator labelDecorator = this._currentProviders.labelDecorator;

			if (labelDecorator != null)
			{
				result = labelDecorator.decorateText(text, element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{
		Set<Map.Entry<String, Providers>> entries = this._providersByLanguage.entrySet();
		Iterator<Map.Entry<String, Providers>> iter = entries.iterator();

		while (iter.hasNext())
		{
			Map.Entry<String, Providers> entry = iter.next();
			Providers providers = entry.getValue();
			ILabelProvider labelProvider = providers.labelProvider;

			if (labelProvider != null)
			{
				labelProvider.dispose();
			}
		}
	}

	/**
	 * fireBeforeRefreshEvent
	 * 
	 * @param fileContext
	 */
	public void fireBeforeRefreshEvent(EditorFileContext fileContext)
	{
		if (this._currentProviders != null)
		{
			this._currentProviders.fireBeforeRefreshEvent(fileContext);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element)
	{
		Color result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			IColorProvider colorProvider = this._currentProviders.colorProvider;

			if (colorProvider != null)
			{
				result = colorProvider.getBackground(element);
			}
		}

		return result;
	}

	/**
	 * getChildren
	 * 
	 * @param parentNode
	 * @param language
	 * @return Object[]
	 */
	public Object[] getChildren(IParseNode parentNode, String language)
	{
		List<Object> result = new ArrayList<Object>();

		for (int i = 0; i < parentNode.getChildCount(); i++)
		{
			IParseNode child = parentNode.getChild(i);
			String childLanguage = child.getLanguage();

			if (childLanguage.equals(language))
			{
				result.add(child);
			}
			else
			{
				UnifiedOutlineProvider outlineProvider = UnifiedOutlineProvider.getInstance();

				outlineProvider.setCurrentLanguage(childLanguage);

				Object[] elements = outlineProvider.getElements(child);

				if (elements != null)
				{
					for (int j = 0; j < elements.length; j++)
					{
						result.add(elements[j]);
					}
				}

				outlineProvider.setCurrentLanguage(language);
			}
		}

		return result.toArray(new Object[result.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = NO_OBJECTS;

		switchLanguage(parentElement);

		if (this._currentProviders != null)
		{
			ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

			if (contentProvider != null)
			{
				result = contentProvider.getChildren(parentElement);
			}
		}

		return result;
	}

	/**
	 * getCurrentLanguage
	 * 
	 * @return String or null
	 */
	public String getCurrentLanguage()
	{
		return this._currentLanguage;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] result = NO_OBJECTS;

		if (inputElement instanceof UnifiedEditor)
		{
			UnifiedEditor editor = (UnifiedEditor) inputElement;
			EditorFileContext fileContext = editor.getFileContext();
			IParseState parseState = fileContext.getParseState();
			if (parseState == null)
			{
				return NO_OBJECTS;
			}
			IParseNode node = parseState.getParseResults();

			String defaultLanguage = fileContext.getDefaultLanguage();
			this.setCurrentLanguage(defaultLanguage);

			if (this._currentProviders != null)
			{
				ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

				if (contentProvider != null)
				{
					// initing external entities resolving context
					if (contentProvider instanceof IOutlineContentProviderExtension)
					{
						IOutlineContentProviderExtension resolver = (IOutlineContentProviderExtension) contentProvider;

						resolver.setPathResolver(this._resolver);
					}

					result = this._currentProviders.contentProvider.getElements(node);
				}
			}
		}
		else
		{
			if (this._currentProviders != null)
			{
				switchLanguage(inputElement);

				ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

				if (contentProvider != null)
				{
					// initing external entities resolving context
					if (contentProvider instanceof IOutlineContentProviderExtension)
					{
						IOutlineContentProviderExtension resolver = (IOutlineContentProviderExtension) contentProvider;

						resolver.setPathResolver(this._resolver);
					}

					result = contentProvider.getElements(inputElement);
				}
			}
		}

		return result;
	}

	/**
	 * getFilterActionInfos
	 * 
	 * @return
	 */
	public FilterActionInfo[] getFilterActionInfos(String language)
	{
		FilterActionInfo[] result = NO_FILTERS;

		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			result = providers.getFilters();
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 */
	public Font getFont(Object element)
	{
		Font result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			IFontProvider fontProvider = this._currentProviders.fontProvider;

			if (fontProvider != null)
			{
				result = fontProvider.getFont(element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element)
	{
		Color result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			IColorProvider colorProvider = this._currentProviders.colorProvider;

			if (colorProvider != null)
			{
				result = colorProvider.getForeground(element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ILabelProvider labelProvider = this._currentProviders.labelProvider;

			if (labelProvider != null)
			{
				result = labelProvider.getImage(element);
			}

			Image decorated = this.decorateImage(result, element);

			if (decorated != null)
			{
				result = decorated;
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		Object result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

			if (contentProvider != null)
			{
				result = contentProvider.getParent(element);
			}
		}

		return result;
	}

	/**
	 * getPrivateMemberPrefix
	 * 
	 * @param language
	 * @return String or null
	 */
	public String getPrivateMemberPrefix(String language)
	{
		String result = null;

		if (this._privateMemberPrefixes != null && this._privateMemberPrefixes.containsKey(language))
		{
			result = this._privateMemberPrefixes.get(language);
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result = null;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ILabelProvider labelProvider = this._currentProviders.labelProvider;

			if (labelProvider != null)
			{
				result = labelProvider.getText(element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

			if (contentProvider != null)
			{
				result = contentProvider.hasChildren(element);
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (this._currentProviders != null)
		{
			ITreeContentProvider contentProvider = this._currentProviders.contentProvider;

			if (contentProvider != null)
			{
				contentProvider.inputChanged(viewer, oldInput, newInput);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		boolean result = false;

		switchLanguage(element);

		if (this._currentProviders != null)
		{
			ILabelProvider labelProvider = this._currentProviders.labelProvider;

			if (labelProvider != null)
			{
				result = labelProvider.isLabelProperty(element, property);
			}
		}

		return result;
	}

	/**
	 * isSortable
	 * 
	 * @param language
	 * @return boolean
	 */
	public boolean isSortable(String language)
	{
		boolean result = false;

		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			result = providers.isSortable;
		}

		return result;
	}

	/**
	 * processContributorExtensions
	 * 
	 * @param elements
	 */
	private void loadContributorExtensions(IConfigurationElement[] elements)
	{
		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_CONTRIBUTOR_EXTENSION))
			{
				try
				{
					String language = element.getAttribute(ATTR_LANGUAGE);
					String colorProviderName = element.getAttribute(ATTR_COLOR_PROVIDER);
					String fontProviderName = element.getAttribute(ATTR_FONT_PROVIDER);
					String labelDecoratorName = element.getAttribute(ATTR_LABEL_DECORATOR);
					String beforeRefreshHandler = element.getAttribute(ATTR_BEFORE_REFRESH_HANDLER);

					if (colorProviderName != null)
					{
						this.setColorProvider(language, (IColorProvider) element
								.createExecutableExtension(ATTR_COLOR_PROVIDER));
					}
					if (fontProviderName != null)
					{
						this.setFontProvider(language, (IFontProvider) element
								.createExecutableExtension(ATTR_FONT_PROVIDER));
					}
					if (labelDecoratorName != null)
					{
						this.setLabelDecorator(language, (ILabelDecorator) element
								.createExecutableExtension(ATTR_LABEL_DECORATOR));
					}
					if (beforeRefreshHandler != null)
					{
						this.addBeforeRefreshHandler(language, (OutlineRefreshHandler) element
								.createExecutableExtension(ATTR_BEFORE_REFRESH_HANDLER));
					}
				}
				catch (CoreException e)
				{
					IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), "Unable to create outline extensions"); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * processContributors
	 * 
	 * @param elements
	 */
	private void loadContributors(IConfigurationElement[] elements)
	{
		// process contributers first
		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_CONTRIBUTOR))
			{
				try
				{
					String language = element.getAttribute(ATTR_LANGUAGE);
					ILabelProvider labelProvider = (ILabelProvider) element
							.createExecutableExtension(ATTR_LABEL_PROVIDER);
					ITreeContentProvider contentProvider = (ITreeContentProvider) element
							.createExecutableExtension(ATTR_CONTENT_PROVIDER);
					String sortableValue = element.getAttribute(ATTR_IS_SORTABLE);
					boolean sortable = (sortableValue == null) ? false : sortableValue.equals("true"); //$NON-NLS-1$

					this.setProviders(language, labelProvider, contentProvider, sortable);
				}
				catch (CoreException e)
				{
					IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), "Unable to create outline providers"); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * loadExtensions
	 */
	public void loadExtensions()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(UnifiedEditorsPlugin.ID, OUTLINE_ID);
			IExtension[] extensions = extensionPoint.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] elements = extension.getConfigurationElements();

				loadContributors(elements);
				loadContributorExtensions(elements);
				// loadFilters(language, element.getChildren(TAG_FILTER));
				loadFilters(elements);
			}
		}
	}

	/**
	 * processFilters
	 * 
	 * @param elements
	 */
	private void loadFilters(IConfigurationElement[] elements)
	{
		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_FILTER))
			{
				String languages = element.getAttribute(ATTR_LANGUAGES);
				String name = element.getAttribute(ATTR_NAME);
				String toolTip = element.getAttribute(ATTR_TOOL_TIP);
				InstanceCreator filterCreator = new InstanceCreator(element, ATTR_CLASS);

				IExtension ext = element.getDeclaringExtension();
				String pluginId = ext.getNamespaceIdentifier();
				Bundle bundle = Platform.getBundle(pluginId);
				String resourceName = element.getAttribute(ATTR_ICON);
				URL resource = bundle.getResource(resourceName);

				if (resource == null)
				{
					resource = bundle.getEntry(ATTR_ICON);
				}

				ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(resource);

				for (String language : languages.split("\\s+")) //$NON-NLS-1$
				{
					this.addFilter(language, name, toolTip, filterCreator, imageDescriptor);
				}
			}
		}
	}

	/**
	 * package access method called only from unified outline page
	 * 
	 * @param unifiedOutlinePage
	 */
	void pageActivated(IUnifiedOutlinePage unifiedOutlinePage)
	{
		setOutlinePage(unifiedOutlinePage);

		for (Iterator<Providers> it = _providersByLanguage.values().iterator(); it.hasNext();)
		{
			Providers name = it.next();
			ITreeContentProvider contentProvider = name.contentProvider;

			if (contentProvider instanceof IOutlineContentProviderExtension)
			{
				IOutlineContentProviderExtension resolver = (IOutlineContentProviderExtension) contentProvider;
				resolver.pageActivated(_resolver);
				TreeViewer treeViewer = unifiedOutlinePage.getTreeViewer();

				if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed())
				{
					treeViewer.refresh();
				}
			}
		}
	}

	/**
	 * package access method called only from unified outline page
	 * 
	 * @param unifiedOutlinePage
	 */
	void pageClosed(IUnifiedOutlinePage unifiedOutlinePage)
	{
		for (Iterator<Providers> it = _providersByLanguage.values().iterator(); it.hasNext();)
		{
			Providers name = it.next();
			ITreeContentProvider contentProvider = name.contentProvider;

			if (contentProvider instanceof IOutlineContentProviderExtension)
			{
				IOutlineContentProviderExtension resolver = (IOutlineContentProviderExtension) contentProvider;
				resolver.pageClosed(_resolver);
				TreeViewer treeViewer = unifiedOutlinePage.getTreeViewer();

				if (treeViewer != null && !treeViewer.getTree().isDisposed())
				{
					treeViewer.refresh();
				}
			}
		}
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		if (this._outlinePage != null)
		{
			this._outlinePage.refresh();
		}
	}

	/**
	 * removeBeforeRefreshHandler
	 * 
	 * @param language
	 * @param handler
	 */
	public void removeBeforeRefreshHandler(String language, OutlineRefreshHandler handler)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.removeBeforeRefreshHandler(handler);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
		if (this._currentProviders != null)
		{
			ILabelProvider labelProvider = this._currentProviders.labelProvider;

			if (labelProvider != null)
			{
				labelProvider.removeListener(listener);
			}
		}
	}

	/**
	 * removeProviders
	 * 
	 * @param language
	 */
	public void removeProviders(String language)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			this._providersByLanguage.remove(language);
		}
	}

	/**
	 * setColorProvider
	 * 
	 * @param language
	 * @param colorProvider
	 */
	public void setColorProvider(String language, IColorProvider colorProvider)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.colorProvider = colorProvider;
		}
	}

	/**
	 * activateProviders
	 * 
	 * @param language
	 */
	public void setCurrentLanguage(String language)
	{
		if (language == null || language.length() == 0)
		{
			throw new IllegalArgumentException("language must be defined"); //$NON-NLS-1$
		}

		if (language.equals(this._currentLanguage) == false)
		{
			if (this._providersByLanguage.containsKey(language))
			{
				this._currentProviders = this._providersByLanguage.get(language);
				this._currentLanguage = language;
			}
		}
	}

	/**
	 * setFontProvider
	 * 
	 * @param language
	 * @param fontProvider
	 */
	public void setFontProvider(String language, IFontProvider fontProvider)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.fontProvider = fontProvider;
		}
	}

	/**
	 * setLabelDecorator
	 * 
	 * @param language
	 * @param fontProvider
	 */
	public void setLabelDecorator(String language, ILabelDecorator labelDecorator)
	{
		if (this._providersByLanguage.containsKey(language))
		{
			Providers providers = this._providersByLanguage.get(language);

			providers.labelDecorator = labelDecorator;
		}
	}

	/**
	 * setOutlinePage
	 * 
	 * @param page
	 */
	public void setOutlinePage(IUnifiedOutlinePage page)
	{
		this._outlinePage = page;
		UnifiedEditor editor = page.getUnifiedEditor();
		IEditorInput editorInput = editor.getEditorInput();
		Object object = _oldEditorInput.get();
		if (editorInput.equals(object))
		{
			return;
		}
		_oldEditorInput = new WeakReference<Object>(editorInput);
		this._resolver = PathResolverProvider.getResolver(editorInput);
	}

	/**
	 * setPrivateMemberPrefix
	 * 
	 * @param language
	 * @param prefix
	 */
	public void setPrivateMemberPrefix(String language, String prefix)
	{
		if (this._privateMemberPrefixes == null)
		{
			this._privateMemberPrefixes = new HashMap<String, String>();
		}

		this._privateMemberPrefixes.put(language, prefix);
	}

	/**
	 * addProviders
	 * 
	 * @param language
	 * @param labelProvider
	 * @param contentProvider
	 * @param isSortable
	 */
	public void setProviders(String language, ILabelProvider labelProvider, ITreeContentProvider contentProvider,
			boolean isSortable)
	{
		if (language != null && language.length() > 0)
		{
			this._providersByLanguage.put(language, new Providers(contentProvider, labelProvider, isSortable));
		}
	}

	/**
	 * switchLanguage
	 * 
	 * @param parentElement
	 */
	private boolean switchLanguage(Object parentElement)
	{
		String oldLanguage = this.getCurrentLanguage();

		if (oldLanguage == null)
		{
			oldLanguage = EMPTY_STRING;
		}

		if (parentElement instanceof IParseNode)
		{
			IParseNode parseNode = (IParseNode) parentElement;

			this.setCurrentLanguage(parseNode.getLanguage());
		}
		else if (parentElement instanceof OutlineItem)
		{
			OutlineItem item = (OutlineItem) parentElement;

			this.setCurrentLanguage(item.getLanguage());
		}

		return (oldLanguage.equals(this.getCurrentLanguage()) == false);
	}
}
