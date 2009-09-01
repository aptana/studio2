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
package com.aptana.ide.editors.unified.folding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedEditor;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class FoldingExtensionPointLoader
{

	/**
	 * Foldable types for a language
	 */
	public class FoldingStructure
	{

		String language;
		String label;
		IFoldingContextHandler handler;
		Map types = new HashMap();
		boolean foldAllParents = true;

		/**
		 * Returns whether all nodes with children will be foldable
		 * 
		 * @return - true if making parents foldable, false otherwise
		 */
		public boolean foldAllParents()
		{
			return foldAllParents;
		}

		/**
		 * Sets whether parents should be foldable or not
		 * 
		 * @param foldAllParents -
		 *            true for parents foldable, false otherwise
		 */
		public void setFoldAllParents(boolean foldAllParents)
		{
			this.foldAllParents = foldAllParents;
		}

		/**
		 * Gets the label for this folding structure
		 * 
		 * @return - label for language
		 */
		public String getLabel()
		{
			return label;
		}

		/**
		 * Sets the label for this folding structure
		 * 
		 * @param label -
		 *            language label
		 */
		public void setLabel(String label)
		{
			this.label = label;
		}

		/**
		 * Gets the language
		 * 
		 * @return - language mime type
		 */
		public String getLanguage()
		{
			return language;
		}

		/**
		 * Sets the language
		 * 
		 * @param language -
		 *            mime type
		 */
		public void setLanguage(String language)
		{
			this.language = language;
		}

		/**
		 * Gets the foldable types for this folding structure
		 * 
		 * @return - list of strings of foldable node types
		 */
		public Map getTypes()
		{
			return types;
		}

		/**
		 * Sets the foldable node type list
		 * 
		 * @param types
		 */
		public void setTypes(Map types)
		{
			this.types = types;
		}

		/**
		 * @return the foldAllParents
		 */
		public boolean isFoldAllParents()
		{
			return foldAllParents;
		}

		/**
		 * @return the handler
		 */
		public IFoldingContextHandler getHandler()
		{
			return handler;
		}

		/**
		 * @param handler
		 *            the handler to set
		 */
		public void setHandler(IFoldingContextHandler handler)
		{
			this.handler = handler;
		}

	}

	/**
	 * Folding action that is updateable
	 */
	private abstract static class FoldingAction extends Action
	{

		/**
		 * Creates a new folding action
		 * 
		 * @param name
		 * @param style
		 */
		public FoldingAction(String name, int style)
		{
			super(name, style);
		}

	}

	
	private static class AnnotationPosition implements Comparable<AnnotationPosition>
	{
	    /**
	     * Position;
	     */
	    private Position position;
	    
	    /**
	     * Annotation.
	     */
	    private Annotation annotation;
	    
        /**
         * AnnotationPosition constructor.
         * @param position - position.
         * @param annotation - annotation.
         */
        public AnnotationPosition(Position position, Annotation annotation)
        {
            super();
            this.position = position;
            this.annotation = annotation;
        }

        /**
         * Gets position.
         * @return the position.
         */
        public Position getPosition()
        {
            return position;
        }

        /**
         * Gets annotation. 
         * @return the annotation.
         */
        public Annotation getAnnotation()
        {
            return annotation;
        }

        /**
          * {@inheritDoc}
          */
        public int compareTo(AnnotationPosition o)
        {
            return o.getPosition().getLength() - this.getPosition().getLength();
        }
	}
	
	/**
	 * "Expand current" action ID.
	 */
	public static final String EXPAND_CURRENT_ACTION_ID = "org.eclipse.ui.edit.text.folding.expand"; //$NON-NLS-1$
	
	/**
     * "Collapse current" action ID.
     */
    public static final String COLLAPSE_CURRENT_ACTION_ID = "org.eclipse.ui.edit.text.folding.collapse"; //$NON-NLS-1$
	
	private Map structs = new HashMap();

	private static FoldingExtensionPointLoader instance;

	private FoldingExtensionPointLoader()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.FOLDING_EXTENSION_POINT);
		if (ep != null && ep.getExtensions() != null)
		{
			IExtension[] extensions = ep.getExtensions();
			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ce = extensions[i].getConfigurationElements();
				for (int j = 0; j < ce.length; j++)
				{
					String language = ce[j].getAttribute("language"); //$NON-NLS-1$
					String label = ce[j].getAttribute("label"); //$NON-NLS-1$
					String foldParents = ce[j].getAttribute("foldAllParents"); //$NON-NLS-1$

					if (language != null && label != null)
					{
						FoldingStructure fs = null;
						if (structs.containsKey(language))
						{
							fs = (FoldingStructure) structs.get(language);
						}
						else
						{
							fs = new FoldingStructure();
							structs.put(language, fs);
						}
						fs.language = language;
						fs.label = label;
						fs.foldAllParents = Boolean.valueOf(foldParents).booleanValue();

						String handlerClass = ce[j].getAttribute("contextHandler"); //$NON-NLS-1$
						if (handlerClass != null)
						{
							try
							{
								Object obj = ce[j].createExecutableExtension("contextHandler"); //$NON-NLS-1$
								if (obj instanceof IFoldingContextHandler)
								{
									fs.handler = (IFoldingContextHandler) obj;
								}
							}
							catch (CoreException e)
							{
							}
						}

						IConfigurationElement[] types = ce[j].getChildren("type"); //$NON-NLS-1$
						for (int k = 0; k < types.length; k++)
						{
							String name = types[k].getAttribute("name"); //$NON-NLS-1$
							String typeLabel = types[k].getAttribute("label"); //$NON-NLS-1$
							if (name != null)
							{
								fs.types.put(name, typeLabel);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the instance
	 * 
	 * @return - the instance
	 */
	public static FoldingExtensionPointLoader getInstance()
	{
		if (instance == null)
		{
			instance = new FoldingExtensionPointLoader();
		}
		return instance;
	}

	/**
	 * Creates the folding actions for an editor
	 * 
	 * @param editor
	 */
	public static void createFoldingActions(final UnifiedEditor editor)
	{
		final FoldingExtensionPointLoader loader = getInstance();
		final String[] supportedTypes = editor.getBaseContributor().getContentTypes();

		final FoldingAction fAllCollapseAll = new FoldingAction(Messages.FoldingExtensionPointLoader_10, IAction.AS_PUSH_BUTTON)
		{
			public void run()
			{
				if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
				{
					ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
					if (viewer.canDoOperation(ProjectionViewer.COLLAPSE_ALL))
					{
						viewer.doOperation(ProjectionViewer.COLLAPSE_ALL);
					}
				}
			}

		};
		editor.setAction(createCollapseAllActionId(), fAllCollapseAll);

		final FoldingAction fAllExpandAll = new FoldingAction(Messages.FoldingExtensionPointLoader_11, IAction.AS_PUSH_BUTTON)
		{
			public void run()
			{
				if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
				{
					ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
					if (viewer.canDoOperation(ProjectionViewer.EXPAND_ALL))
					{
						viewer.doOperation(ProjectionViewer.EXPAND_ALL);
					}
				}
			}
		};
		editor.setAction(createExpandAllActionId(), fAllExpandAll);

		final FoldingAction fExpandCurrent = new FoldingAction(Messages.FoldingExtensionPointLoader_12, IAction.AS_PUSH_BUTTON)
		{

            /**
              * {@inheritDoc}
              */
            @Override
            public void run()
            {
                if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
                {
                    ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
                    if (viewer.getProjectionAnnotationModel() != null)
                    {
                        ISelection selection = viewer.getSelection();
                        if (selection != null && selection instanceof TextSelection)
                        {
                            TextSelection textSelection = (TextSelection) selection;
                            ProjectionAnnotationModel model = viewer.getProjectionAnnotationModel();
                            model.expandAll(textSelection.getOffset(), textSelection.getLength());
                        }
                    }
                }
            }
		};
		fExpandCurrent.setActionDefinitionId(EXPAND_CURRENT_ACTION_ID);
		editor.setAction(createExpandCurrentActionId(), fExpandCurrent);
		
		final FoldingAction fCollapseCurrent = new FoldingAction(Messages.FoldingExtensionPointLoader_13, IAction.AS_PUSH_BUTTON)
        {

            /**
              * {@inheritDoc}
              */
            @Override
            public void run()
            {
                if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
                {
                    ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
                    if (viewer.getProjectionAnnotationModel() != null)
                    {
                        ISelection selection = viewer.getSelection();
                        if (selection != null && selection instanceof TextSelection)
                        {
                            TextSelection textSelection = (TextSelection) selection;
                            ProjectionAnnotationModel model = viewer.getProjectionAnnotationModel();
                            Iterator iterator= model.getAnnotationIterator();
                            List<AnnotationPosition> annotations = new ArrayList<AnnotationPosition>();
                            while (iterator.hasNext()) 
                            {
                                ProjectionAnnotation annotation= (ProjectionAnnotation) iterator.next();
                                
                                if (!annotation.isCollapsed()) 
                                {
                                    Position position= model.getPosition(annotation);
                                    if (position != null && position.overlapsWith(position.getOffset(), position.getLength()))
                                    {
                                        annotations.add(new AnnotationPosition(position, annotation));
                                    }
                                }
                            }
                            if (annotations.size() != 0)
                            {
                                model.collapse(annotations.get(annotations.size() - 1).getAnnotation());
                            }
                        }
                    }
                }
            }
        };
        fCollapseCurrent.setActionDefinitionId(COLLAPSE_CURRENT_ACTION_ID);
        editor.setAction(createCollapseCurrentActionId(), fCollapseCurrent);
		
		for (int i = 0; i < supportedTypes.length; i++)
		{
			if (loader.structs.containsKey(supportedTypes[i]))
			{
				final FoldingStructure fs = (FoldingStructure) loader.structs.get(supportedTypes[i]);
				final String prefID = createEnablePreferenceId(fs.language);

				final FoldingAction fExpandAll = new FoldingAction(Messages.FoldingExtensionPointLoader_14, IAction.AS_PUSH_BUTTON)
				{

					public void run()
					{
						if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
						{
							ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
							if (viewer.getProjectionAnnotationModel() != null)
							{
								List mods = new ArrayList();
								Iterator annotationIterator = viewer.getProjectionAnnotationModel()
										.getAnnotationIterator();
								if (annotationIterator != null)
								{
									while (annotationIterator.hasNext())
									{
										Annotation annotation = (Annotation) annotationIterator.next();
										if (annotation instanceof LanguageProjectAnnotation)
										{
											LanguageProjectAnnotation lpa = (LanguageProjectAnnotation) annotation;
											if (fs.language.equals(lpa.getLanguage()) && lpa.isCollapsed())
											{
												lpa.markExpanded();
												mods.add(lpa);
											}
										}
									}
									viewer.getProjectionAnnotationModel().modifyAnnotations(null, null,
											(Annotation[]) mods.toArray(new Annotation[mods.size()]));
								}
							}
						}
					}

				};
				editor.setAction(createExpandAllActionId(fs.language), fExpandAll); //$NON-NLS-1$

				final FoldingAction fCollapseAll = new FoldingAction(Messages.FoldingExtensionPointLoader_15, IAction.AS_PUSH_BUTTON)
				{

					public void run()
					{
						if (editor.getViewer() != null && editor.getViewer() instanceof ProjectionViewer)
						{
							ProjectionViewer viewer = (ProjectionViewer) editor.getViewer();
							if (viewer.getProjectionAnnotationModel() != null)
							{
								List mods = new ArrayList();
								Iterator annotationIterator = viewer.getProjectionAnnotationModel()
										.getAnnotationIterator();
								if (annotationIterator != null)
								{
									while (annotationIterator.hasNext())
									{
										Annotation annotation = (Annotation) annotationIterator.next();
										if (annotation instanceof LanguageProjectAnnotation)
										{
											LanguageProjectAnnotation lpa = (LanguageProjectAnnotation) annotation;
											if (fs.language.equals(lpa.getLanguage()) && !lpa.isCollapsed())
											{
												lpa.markCollapsed();
												mods.add(lpa);
											}
										}
									}
									viewer.getProjectionAnnotationModel().modifyAnnotations(null, null,
											(Annotation[]) mods.toArray(new Annotation[mods.size()]));
								}
							}
						}
					}

				};
				editor.setAction(createCollapseAllActionId(fs.language), fCollapseAll); //$NON-NLS-1$

				final FoldingAction fToggle = new FoldingAction(Messages.FoldingExtensionPointLoader_LBL_EnableFolding, IAction.AS_CHECK_BOX) {
					public void run()
					{
						UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(prefID, this.isChecked());
						fCollapseAll.setEnabled(this.isChecked());
						fExpandAll.setEnabled(this.isChecked());
					}

				};
				fToggle.setEnabled(true);
				fToggle.setChecked(true);
				editor.setAction(createToggleActionId(fs.language), fToggle);

				Iterator iterator = fs.types.keySet().iterator();
				while (iterator.hasNext())
				{
					String name = (String) iterator.next();
					String label = (String) fs.types.get(name);
					if (label != null)
					{
						final FoldingAction nameExpandAction = new FoldingAction(Messages.FoldingExtensionPointLoader_16 + label,
								IAction.AS_PUSH_BUTTON)
						{

							public void run()
							{
								super.run();
							}

						};
						editor.setAction(createExpandActionId(fs.language, name), nameExpandAction);

						final FoldingAction nameCollapseAction = new FoldingAction(Messages.FoldingExtensionPointLoader_17 + label,
								IAction.AS_PUSH_BUTTON)
						{

							public void run()
							{
								super.run();
							}

						};
						editor.setAction(createCollapseActionId(fs.language, name), nameCollapseAction);
					}
				}
				
				
			}
		}
	}

	/**
	 * Loads the reconciling strategy functionality for an editor from the folding extension point. The map returned
	 * from this method is used to determine what folding positions will be emitted from the UnifiedReconcilingStrategy.
	 * 
	 * @param editor -
	 *            editor to add folding
	 * @return - map of languages to folding structure objects
	 */
	public static Map loadChildTypes(UnifiedEditor editor)
	{
		Map childTypes = new HashMap();
		final FoldingExtensionPointLoader loader = getInstance();
		Iterator iter = loader.structs.keySet().iterator();
		while (iter.hasNext())
		{
			final FoldingStructure fs = (FoldingStructure) loader.structs.get(iter.next());

			// Check to see if language is in this editor before adding folding options
			boolean containsLanguage = false;
			String[] supportedTypes = editor.getBaseContributor().getContentTypes();
			for (int i = 0; i < supportedTypes.length; i++)
			{
				if (fs.language.equals(supportedTypes[i]))
				{
					containsLanguage = true;
					break;
				}
			}

			if (containsLanguage)
			{
				childTypes.put(fs.language, fs);
			}
		}
		return childTypes;
	}
	
	/**
	 * Registers key bindings.
	 * @param service - key binding service.
	 */
	public static void registerKeyBindings(ITextEditor editor, IKeyBindingService service)
	{
	    IAction fExpandCurrentAction = editor.getAction(createExpandCurrentActionId());
	    if (fExpandCurrentAction != null)
	    {
	        service.registerAction(fExpandCurrentAction);
	    }
	}

	private static String createCollapseAllActionId()
	{
		return "FoldingCollapseAllLanguages"; //$NON-NLS-1$
	}

	private static String createExpandAllActionId()
	{
		return "FoldingExpandAllLanguages"; //$NON-NLS-1$
	}
	
	private static String createExpandCurrentActionId()
    {
        return "FoldingExpandCurrent"; //$NON-NLS-1$
    }
	
	private static String createCollapseCurrentActionId()
    {
        return "FoldingECollapseCurrent"; //$NON-NLS-1$
    }

	private static String createCollapseActionId(String language, String name)
	{
		return "FoldingCollapse" + language + name; //$NON-NLS-1$
	}

	private static String createExpandActionId(String language, String name)
	{
		return "FoldingExpand" + language + name; //$NON-NLS-1$
	}

	private static String createToggleActionId(String language)
	{
		return "FoldingToggle" + language; //$NON-NLS-1$
	}

	private static String createCollapseAllActionId(String language)
	{
		return "FoldingCollapseAll" + language; //$NON-NLS-1$
	}

	private static String createExpandAllActionId(String language)
	{
		return "FoldingExpandAll" + language; //$NON-NLS-1$
	}

	/**
	 * Creates a preference id for a language to store folding preferences
	 * 
	 * @param language
	 * @return - preference string
	 */
	public static String createEnablePreferenceId(String language)
	{
		return IPreferenceConstants.EDITOR_FOLDING_ENABLED + "." + language; //$NON-NLS-1$
	}

	/**
	 * Creates a preference id for an initial folding of name and language
	 * 
	 * @param language
	 * @param name
	 * @return - preference string
	 */
	public static String createInitialFoldingPreferenceId(String language, String name)
	{
		return IPreferenceConstants.INITIAL_FOLDING_ENABLED + "." + language + "." + name; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void updateLanguageActions(ITextEditor editor, FoldingStructure fs)
	{
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		// Get preference for type
		String prefId = createEnablePreferenceId(fs.language);
		boolean fold = store.getBoolean(prefId);
		// Toggle expand/collapse
		IAction tAction = editor.getAction(createToggleActionId(fs.language));
		if (tAction != null)
		{
			tAction.setChecked(fold);
			tAction.setEnabled(true);
		}
		IAction eAction = editor.getAction(createExpandAllActionId(fs.language));
		if (eAction != null)
		{
			eAction.setEnabled(fold);
		}
		IAction cAction = editor.getAction(createCollapseAllActionId(fs.language));
		if (cAction != null)
		{
			cAction.setEnabled(fold);
		}
		Iterator iter = fs.types.keySet().iterator();
		while (iter.hasNext())
		{
			String name = (String) iter.next();
			IAction enAction = editor.getAction(createExpandActionId(fs.language, name));
			if (enAction != null)
			{
				enAction.setEnabled(fold);
			}
			IAction cnAction = editor.getAction(createCollapseActionId(fs.language, name));
			if (cnAction != null)
			{
				cnAction.setEnabled(fold);
			}
		}
	}

	/**
	 * Updates the action state (enabled, disabled, checked) for a given editor. The IUnifiedEditor passed in must also
	 * implement ITextEditor so the getAction method can be called.
	 * 
	 * @param editor -
	 *            IUnifiedEditor and ITextEditor implementing editor
	 */
	public static void updateActions(IUnifiedEditor editor)
	{
		final FoldingExtensionPointLoader loader = getInstance();
		final String[] supportedTypes = editor.getBaseContributor().getContentTypes();
		if (editor instanceof ITextEditor)
		{
			ITextEditor tEditor = (ITextEditor) editor;
			if (supportedTypes.length == 1 && loader.structs.containsKey(supportedTypes[0]))
			{
				FoldingStructure fs = (FoldingStructure) loader.structs.get(supportedTypes[0]);
				updateLanguageActions(tEditor, fs);
			}
			else
			{
				// Update language actions
				for (int i = 0; i < supportedTypes.length; i++)
				{
					if (loader.structs.containsKey(supportedTypes[i]))
					{
						FoldingStructure fs = (FoldingStructure) loader.structs.get(supportedTypes[i]);
						updateLanguageActions(tEditor, fs);
					}
				}
			}
		}
	}

	/**
	 * Fills a context menu for an editor with the folding options
	 * 
	 * @param editor
	 * @param menu
	 */
	public static void fillRulerContextMenu(UnifiedEditor editor, IMenuManager menu)
	{
		final FoldingExtensionPointLoader loader = getInstance();
		IMenuManager foldingMenu = new MenuManager("Folding", "projection"); //$NON-NLS-1$ //$NON-NLS-2$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		String[] supportedTypes = editor.getBaseContributor().getContentTypes();
		updateActions(editor);
		if (supportedTypes.length == 1)
		{
			FoldingStructure fs = (FoldingStructure) loader.structs.get(supportedTypes[0]);
			if (fs != null)
			{
				addFoldingStructureAction(foldingMenu, fs, editor);
			}
		}
		else
		{
			IAction action = editor.getAction(createExpandAllActionId());
			if (action != null)
			{
				foldingMenu.add(action);
			}
			action = editor.getAction(createCollapseAllActionId());
			if (action != null)
			{
				foldingMenu.add(action);
			}
			Iterator iter = loader.structs.keySet().iterator();
			while (iter.hasNext())
			{
				final FoldingStructure fs = (FoldingStructure) loader.structs.get(iter.next());

				// Check to see if language is in this editor before adding folding options
				boolean containsLanguage = false;

				for (int i = 0; i < supportedTypes.length; i++)
				{
					if (fs.language.equals(supportedTypes[i]))
					{
						containsLanguage = true;
						break;
					}
				}

				if (containsLanguage)
				{
					IMenuManager languageMenu = new MenuManager(fs.label, fs.language);
					foldingMenu.add(languageMenu);
					addFoldingStructureAction(languageMenu, fs, editor);
				}
			}
		}

	}

	private static void addFoldingStructureAction(IMenuManager parent, FoldingStructure fs, UnifiedEditor editor)
	{
		IAction action = editor.getAction(createToggleActionId(fs.language));
		if (action != null)
		{
			parent.add(action);
		}
		action = editor.getAction(createExpandAllActionId(fs.language));
		if (action != null)
		{
			parent.add(action);
		}
		action = editor.getAction(createCollapseAllActionId(fs.language));
		if (action != null)
		{
			parent.add(action);
		}
		Iterator iter = fs.types.keySet().iterator();
		while (iter.hasNext())
		{
			String name = (String) iter.next();
			action = editor.getAction(createExpandActionId(fs.language, name));
			if (action != null)
			{
				parent.add(action);
			}
			action = editor.getAction(createCollapseActionId(fs.language, name));
			if (action != null)
			{
				parent.add(action);
			}
		}
	}

	/**
	 * @param model
	 * @param lang
	 * @param node
	 */
	public static void collapseAll(ProjectionAnnotationModel model, String lang, String node)
	{
		Iterator annotationIterator = model.getAnnotationIterator();
		while (annotationIterator.hasNext())
		{
			Annotation annotation = (Annotation) annotationIterator.next();
			if (annotation instanceof LanguageProjectAnnotation)
			{
				LanguageProjectAnnotation projectionAnnotation = (LanguageProjectAnnotation) annotation;
				if (projectionAnnotation.getLanguage().equals(lang))
				{
					if (projectionAnnotation.getNodeType().equals(node))
					{
						model.collapse(annotation);
					}
				}
			}
		}
	}

	/**
	 * @param model
	 * @param lang
	 * @param node
	 */
	public static void expandAll(ProjectionAnnotationModel model, String lang, String node)
	{
		Iterator annotationIterator = model.getAnnotationIterator();
		while (annotationIterator.hasNext())
		{
			Annotation annotation = (Annotation) annotationIterator.next();
			if (annotation instanceof LanguageProjectAnnotation)
			{
				LanguageProjectAnnotation projectionAnnotation = (LanguageProjectAnnotation) annotation;
				if (projectionAnnotation.getLanguage().equals(lang))
				{
					if (projectionAnnotation.getNodeType().equals(node))
					{
						model.expand(annotation);
					}
				}
			}
		}
	}

}
