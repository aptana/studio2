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
package com.aptana.ide.editors.unified.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.toolbar.IToolBarMember;
import com.aptana.ide.editors.toolbar.IToolbarContributionRegistry;
import com.aptana.ide.editors.toolbar.IToolbarRegistryContributor;
import com.aptana.ide.editors.toolbar.ToolBarContribution;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.InstanceCreator;
import com.aptana.ide.editors.unified.UnifiedConfiguration;

/**
 * real implementation of {@link IToolBarContributionRegistry} lays here
 * @author Pavel Petrochenko
 */
public final class ToolBarContributionRegistryImpl
{

	private static final String TOOLBAR_ID = "com.aptana.ide.editors.toolbarContribution"; //$NON-NLS-1$
	private static final Object TAG_ELEMENT = "element"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	private static final Object TAG_CONTRIBUTOR = "contributor"; //$NON-NLS-1$

	private static Map toolBarContributions = new HashMap();
	private static Map toolBarContributionNames = new HashMap();
	static WeakHashMap map = new WeakHashMap();
	static WeakHashMap mapListener = new WeakHashMap();

	private static IToolbarContributionRegistry toolbarContributionRegistry = new IToolbarContributionRegistry()
	{

		public void addContribution(String language, ToolBarContribution tc)
		{
			ToolBarContributionRegistryImpl.addContribution(language, tc);
		}

		public void removeContribution(String language, ToolBarContribution cont)
		{
			ToolBarContributionRegistryImpl.removeContribution(language, cont);
		}

		public List getContributions(String language)
		{
			return ToolBarContributionRegistryImpl.getContributions(language);
		}

	};

	/**
	 * @return singleton instance of {@link IToolbarContributionRegistry}
	 */
	public static IToolbarContributionRegistry getInstance()
	{
		return toolbarContributionRegistry;
	}

	private static List getContributions(String language)
	{
		List list = (List) toolBarContributions.get(language);
		if (list == null)
		{
			list = new ArrayList();
		}
		return list;
	}

	static
	{
		loadToolBarContributions();
	}

	/**
	 * @author Pavel Petrochenko
	 */
	private static final class ContextListener implements ISelectionChangedListener
	{
		private final IToolBarManager toolBarManager;
		private final TextViewer tv;
		private final IUnifiedEditor activeEditor;

		private ContextListener(IToolBarManager toolBarManager, TextViewer tv, IUnifiedEditor activeEditor)
		{
			this.toolBarManager = toolBarManager;
			this.tv = tv;
			this.activeEditor = activeEditor;
		}

		/**
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event)
		{
			ITextSelection ts = (ITextSelection) event.getSelection();
			int offset = ts.getOffset();

			try
			{
				String type = TextUtilities.getContentType(tv.getDocument(), UnifiedConfiguration.UNIFIED_PARTITIONING,
						offset, true);
				doUpdate(type);
			}
			catch (BadLocationException e)
			{
				return;
			}
		}

		/**
		 * updates toolbar it it is needed
		 * @param chType
		 */
		public void updateIfNeeded(String chType)
		{
			if (tv.getTextWidget().isDisposed())
			{
				return;
			}
			ITextSelection ts = (ITextSelection) tv.getSelection();
			int offset = ts.getOffset();
			try
			{
				String type = TextUtilities.getContentType(tv.getDocument(), UnifiedConfiguration.UNIFIED_PARTITIONING,
						offset, true);
				if (chType.equals(type))
				{
					updateToolbar(toolBarManager, type, activeEditor);
					toolBarManager.update(false);
				}
			}
			catch (BadLocationException e)
			{
				return;
			}
		}

		//real update
		private void doUpdate(String type)
		{
			if (type != null && !type.equals(map.get(activeEditor)))
			{
				map.put(activeEditor, type);
				updateToolbar(toolBarManager, type, activeEditor);
				toolBarManager.update(false);
				WorkbenchWindow activeWorkbenchWindow = (WorkbenchWindow) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				// workarounding eclipse strange behavior
				activeWorkbenchWindow.getCoolBarManager2().update(true);
			}
		}
	}

	private ToolBarContributionRegistryImpl()
	{

	}

	/**
	 * @param activeEditor
	 * @param toolBarManager
	 */
	static void initToolBar(final IUnifiedEditor activeEditor, final IToolBarManager toolBarManager)
	{
		String defaultLanguage = activeEditor.getFileContext().getDefaultLanguage();
		ISourceViewer viewer = activeEditor.getViewer();
		String cLang = (String) map.get(activeEditor);
		if (viewer instanceof TextViewer && cLang == null)
		{
			final TextViewer tv = (TextViewer) viewer;
			map.put(activeEditor, defaultLanguage);
			final ContextListener selectionChangedListener = new ContextListener(toolBarManager, tv, activeEditor);
			tv.addPostSelectionChangedListener(selectionChangedListener);
			mapListener.put(activeEditor, selectionChangedListener);
			tv.getTextWidget().addDisposeListener(new DisposeListener()
			{

				public void widgetDisposed(DisposeEvent e)
				{
					tv.removePostSelectionChangedListener(selectionChangedListener);
					mapListener.remove(activeEditor);
				}
			});
			tv.getTextWidget().addFocusListener(new FocusListener()
			{

				public void focusGained(FocusEvent e)
				{
					map.remove(activeEditor);
					selectionChangedListener.selectionChanged(new SelectionChangedEvent(tv, tv.getSelection()));
				}

				public void focusLost(FocusEvent e)
				{

				}

			});
		}
		if (cLang != null)
		{
			defaultLanguage = cLang;
		}
		// activeEditor.getViewer().getTextWidget().getCaret().addListener(eventType, listener)
		updateToolbar(toolBarManager, defaultLanguage, activeEditor);
	}

	private static void updateToolbar(final IToolBarManager toolBarManager, String defaultLanguage,
			final IUnifiedEditor editor)
	{
		toolBarManager.removeAll();

		ArrayList contributions = (ArrayList) toolBarContributions.get(defaultLanguage);
		if (contributions != null)
		{
			for (Iterator iterator = contributions.iterator(); iterator.hasNext();)
			{
				final ToolBarContribution name = (ToolBarContribution) iterator.next();
				Action action = new Action(name.getText(), Action.AS_PUSH_BUTTON)
				{
					public void run()
					{

						IToolBarMember member = (IToolBarMember) name.getInstance();
						member.execute(editor, name.getText());

					}
				};
				action.setImageDescriptor(name.getIcon());
				action.setToolTipText(name.getTooltipText());
				toolBarManager.add(action);
			}
		}
	}

	private static void loadToolBarContributions()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(TOOLBAR_ID);

		if (ep != null)
		{
			IExtension[] extensions = ep.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] elements = extension.getConfigurationElements();
				// initialize contributors first
				for (int j = 0; j < elements.length; j++)
				{
					try
					{
						IConfigurationElement element = elements[j];
						String elementName = element.getName();
						if (elementName.equals(TAG_CONTRIBUTOR))
						{
							IToolbarRegistryContributor contributor = (IToolbarRegistryContributor) element
									.createExecutableExtension(ATTR_CLASS);
							contributor.contributeToToolbarRegistry(toolbarContributionRegistry);
						}
					}
					catch (Exception e)
					{
						IdeLog.log(UnifiedEditorsPlugin.getDefault(), IStatus.ERROR,
								"Exception while initializing toolbar contribution registry", e); //$NON-NLS-1$
					}
				}
				for (int j = 0; j < elements.length; j++)
				{
					IConfigurationElement element = elements[j];
					String elementName = element.getName();
					try
					{
						if (elementName.equals(TAG_ELEMENT))
						{
							String parserClass = element.getAttribute(ATTR_CLASS);
							String language = element.getAttribute(ATTR_LANGUAGE);
							String icon = element.getAttribute("icon"); //$NON-NLS-1$
							String text = element.getAttribute("name"); //$NON-NLS-1$
							String tooltip = element.getAttribute("tooltip"); //$NON-NLS-1$
							InstanceCreator creator = null;
							if (parserClass != null && language != null && language.length() > 0)
							{
								creator = new InstanceCreator(element, ATTR_CLASS);
							}
							String namespaceIdentifier = extension.getNamespaceIdentifier();
							ImageDescriptor desc = null;
							if (icon != null && icon.length() > 0)
							{
								desc = ImageDescriptor.createFromURL(Platform.getBundle(namespaceIdentifier).getEntry(
										icon));
							}
							ToolBarContribution tc = new ToolBarContribution(text, tooltip, desc, creator);
							addContribution(language, tc);
						}

					}
					catch (Exception e)
					{
						IdeLog.log(UnifiedEditorsPlugin.getDefault(), IStatus.ERROR,
								"Exception while initializing toolbar contribution registry", e); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private static void addContribution(String language, ToolBarContribution tc)
	{
		HashSet set = (HashSet) toolBarContributionNames.get(language);
		if (set == null)
		{
			set = new HashSet();
			toolBarContributionNames.put(language, set);
		}
		if (set.contains(tc.getText()))
		{
			return;
		}
		set.add(tc.getText());
		ArrayList list = (ArrayList) toolBarContributions.get(language);
		if (list == null)
		{
			list = new ArrayList();
			toolBarContributions.put(language, list);
		}

		list.add(tc);
		update(language);
	}
	
	

	/**
	 * @param language
	 * @param cont
	 */
	private static void removeContribution(String language, ToolBarContribution cont)
	{
		ArrayList list = (ArrayList) toolBarContributions.get(language);
		HashSet set = (HashSet) toolBarContributionNames.get(language);
		if (list == null)
		{
			return;
		}
		boolean remove = list.remove(cont);
		if (remove)
		{
			set.remove(cont.getText());
			update(language);
		}
	}

	private static void update(String language)
	{
		for (Iterator iterator = mapListener.values().iterator(); iterator.hasNext();)
		{
			ContextListener name = (ContextListener) iterator.next();
			name.updateIfNeeded(language);
		}
	}

}
