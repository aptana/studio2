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
package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.IElementStateListener;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.html.preview.HTMLPreviewHelper;
import com.aptana.ide.editor.html.preview.HTMLPreviewPropertyPage;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.ServerFileTypeHandlers;
import com.aptana.ide.server.core.ServerFileTypeHandlers.PreviewInfo;

/** The split style HTML editor. */
public class SplitPageHTMLEditor extends EditorPart implements IHTMLEditorPart
{

	/** HTML source editor */
	private HTMLSourceEditor editor;
	/** wrapper */
	private HTMLEditor wrapper;
	/** horizontal split or vertical split */
	private boolean isHorizontal;
	/** EditorSite */
	private SplitEditorSite site;
	private IEditorInput _editorInput;
	private IPropertyListener _propertyListener;
	private boolean isDisposing = false;
	private SashForm sash;
	private SashForm browserSash;
	private String browserWeightPreference;
	private String editorToBrowserWeightPreference;
	
	private IElementStateListener elementListener = new IElementStateListener()
	{

		public void elementMoved(Object originalElement, Object movedElement)
		{

		}

		public void elementDirtyStateChanged(Object element, boolean isDirty)
		{

		}

		public void elementDeleted(Object element)
		{
			if (element.equals(getEditorInput()))
			{
				IWorkbenchPartSite site = SplitPageHTMLEditor.this.getSite();
				if (site == null)
				{
					return;
				}
				IWorkbenchWindow window = site.getWorkbenchWindow();
				if (window == null)
				{
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null)
				{
					return;
				}
				page.closeEditor(SplitPageHTMLEditor.this, true);
			}
		}

		public void elementContentReplaced(Object element)
		{

		}

		public void elementContentAboutToBeReplaced(Object element)
		{

		}

	};

	private IPropertyListener propertyListener = new IPropertyListener()
	{

		public void propertyChanged(Object source, int propId)
		{
			if (propId == IEditorPart.PROP_INPUT && source instanceof HTMLSourceEditor)
			{
				IEditorInput newInput = ((HTMLSourceEditor) source).getEditorInput();
				if (newInput != null)
				{
					setInput(newInput);
					setPartName(newInput.getName());
					setTitleToolTip(newInput.getToolTipText());
				}
			}
		}

	};

	/** List of browsers for this editor */
	private List<ContributedBrowser> browsers;

	/**
	 * SplitPageHTMLEditor
	 * 
	 * @param wrapper
	 * @param isHorizontal
	 * @param editor
	 */
	public SplitPageHTMLEditor(HTMLEditor wrapper, boolean isHorizontal, HTMLSourceEditor editor)
	{
		super();

		if (wrapper == null)
		{
			throw new IllegalArgumentException(Messages.SplitPageHTMLEditor_WrapperCannotBeNull);
		}
		if (editor == null)
		{
			throw new IllegalArgumentException(Messages.SplitPageHTMLEditor_EditorCannotBeNull);
		}
		browsers = new ArrayList<ContributedBrowser>();
		this.wrapper = wrapper;
		this.editor = editor;
		this.isHorizontal = isHorizontal;
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#getBrowser2()
	 */
	public Browser getBrowser2()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#getSourceEditor()
	 */
	public HTMLSourceEditor getSourceEditor()
	{
		return editor;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		editor.doSave(monitor);
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
		wrapper.updatePreview();
		saveWeightPreferences();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs()
	{
		editor.doSaveAs();
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
		wrapper.updatePreview();
		saveWeightPreferences();
	}

	/**
	 * Saves the weight of the editor to the browser sash and the weight of the individual browsers in the sash.
	 */
	private void saveWeightPreferences()
	{
		if (browsers.size() == 2 && browserSash != null && browserWeightPreference != null)
		{
			int[] weights = browserSash.getWeights();
			if (weights != null && weights.length == 2)
			{
				String browserWeight = weights[0] + "," + weights[1]; //$NON-NLS-1$
				HTMLPlugin.getDefault().getPreferenceStore().setValue(browserWeightPreference, browserWeight);
			}
		}
		if (sash != null && editorToBrowserWeightPreference != null)
		{
			int[] weights = sash.getWeights();
			if (weights != null & weights.length == 2)
			{
				String editorToBrowserWeight = weights[0] + "," + weights[1]; //$NON-NLS-1$
				HTMLPlugin.getDefault().getPreferenceStore().setValue(editorToBrowserWeightPreference,
						editorToBrowserWeight);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
	{
		setSite(site);
		_editorInput = editorInput;
		setInput(editorInput);
		setPartName(editorInput.getName());
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty()
	{
		if (editor != null)
		{
			return editor.isDirty();
		}
		return false;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (isDisposing)
		{
			return;
		}
		isDisposing = true;

		if (_propertyListener != null)
		{
			editor.removePropertyListener(_propertyListener);
			_propertyListener = null;
		}
		if (browsers != null && browsers.size() != 0)
		{
			disposeBrowsers();
			browsers = null;
		}
		if (wrapper != null)
		{
			wrapper.dispose();
			wrapper = null;
		}
		if (site != null)
		{
			site.dispose();
			site = null;
		}
		if (_editorInput != null)
		{
			_editorInput = null;
		}
		if (editor != null)
		{
			if (editor.getDocumentProvider() != null)
			{
				editor.getDocumentProvider().removeElementStateListener(elementListener);
			}
			editor.removePropertyListener(propertyListener);
			editor.dispose();
			editor = null;
		}

		// setSite(null);
		// setInput(null);

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		try
		{
			// Don't split when EditorInput isn't IFileEditorInput
			// if(!(getEditorInput() instanceof IFileEditorInput)){
			// editor.init(getEditorSite(), getEditorInput());
			// editor.addPropertyListener(new IPropertyListener() {
			// public void propertyChanged(Object source, int propertyId) {
			// firePropertyChange(propertyId);
			// }
			// });
			// editor.createPartControl(parent);
			// return;
			// }

			sash = null;
			if (isHorizontal)
			{
				sash = new SashForm(parent, SWT.VERTICAL);
				editorToBrowserWeightPreference = IPreferenceConstants.HTMLEDITOR_EDITOR_BROWSER_WEIGHT_HORIZONTAL;
			}
			else
			{
				sash = new SashForm(parent, SWT.HORIZONTAL);
				editorToBrowserWeightPreference = IPreferenceConstants.HTMLEDITOR_EDITOR_BROWSER_WEIGHT_VERTICAL;
			}
			site = new SplitEditorSite(editor, getEditorSite());
			editor.init(site, getEditorInput());
			_propertyListener = new IPropertyListener()
			{
				public void propertyChanged(Object source, int propertyId)
				{
					firePropertyChange(propertyId);
				}
			};

			editor.addPropertyListener(_propertyListener);
			editor.createPartControl(sash);

			editor.addPropertyListener(propertyListener);
			editor.getDocumentProvider().addElementStateListener(elementListener);

			loadBrowsers(sash);

			String editorWeight = HTMLPlugin.getDefault().getPreferenceStore().getString(
					editorToBrowserWeightPreference);
			if (editorWeight != null && !editorWeight.equals("") && browsers.size() > 0) //$NON-NLS-1$
			{
				try
				{
					String[] weights = editorWeight.split(","); //$NON-NLS-1$
					if (weights != null && weights.length == 2)
					{
						int[] intWeights = new int[weights.length];
						intWeights[0] = Integer.parseInt(weights[0]);
						intWeights[1] = Integer.parseInt(weights[1]);
						sash.setWeights(intWeights);
					}
				}
				catch (Exception e)
				{
					// Do nothing
				}
				catch (Error e)
				{
					// Do nothing
				}
			}

			wrapper.updatePreview();
		}
		catch (PartInitException e)
		{
			// HTMLPlugin.logException(e);
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus()); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		editor.setFocus();
	}

	/**
	 * gotoMarker
	 * 
	 * @param marker
	 */
	public void gotoMarker(IMarker marker)
	{
		IDE.gotoMarker(editor, marker);
	}

	/**
	 * setOffset
	 * 
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		editor.selectAndReveal(offset, 0);
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#isFileEditorInput()
	 */
	public boolean isFileEditorInput()
	{
		return editor.isFileEditorInput();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		// Context is just plugin ID + name of class. Matches contexts.xml file
		if (editor != null)
		{
			return editor.getAdapter(adapter);
		}
		else
		{
			IdeLog.logError(HTMLPlugin.getDefault(), StringUtils.format(Messages.SplitPageHTMLEditor_EditorIsNull,
					adapter.toString()));
			return null;
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#firePropertyChange(int)
	 */
	protected void firePropertyChange(int propertyId)
	{
		super.firePropertyChange(propertyId);
		wrapper.firePropertyChange2(propertyId);
	}

	/**
	 * An implementation of IEditorSite for the split editor.
	 */
	private static class SplitEditorSite implements IEditorSite
	{

		private HTMLSourceEditor editor;
		private IEditorSite site;
		private ArrayList menuExtenders;

		/**
		 * SplitEditorSite
		 * 
		 * @param editor
		 * @param site
		 */
		public SplitEditorSite(HTMLSourceEditor editor, IEditorSite site)
		{
			this.editor = editor;
			this.site = site;
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#getActionBarContributor()
		 */
		public IEditorActionBarContributor getActionBarContributor()
		{
			return site.getActionBarContributor();
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#getActionBars()
		 */
		public IActionBars getActionBars()
		{
			return site.getActionBars();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getId()
		 */
		public String getId()
		{
			return site.getId();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getKeyBindingService()
		 */
		public IKeyBindingService getKeyBindingService()
		{
			return site.getKeyBindingService();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getPluginId()
		 */
		public String getPluginId()
		{
			return site.getPluginId();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getRegisteredName()
		 */
		public String getRegisteredName()
		{
			return site.getRegisteredName();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider)
		{
			site.registerContextMenu(menuManager, selectionProvider);
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(java.lang.String,
		 *      org.eclipse.jface.action.MenuManager, org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider)
		{
			if (menuExtenders == null)
			{
				menuExtenders = new ArrayList(1);
			}
			menuExtenders.add(new PopupMenuExtender(menuId, menuManager, selectionProvider, editor));
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchSite#getPage()
		 */
		public IWorkbenchPage getPage()
		{
			return site.getPage();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchSite#getSelectionProvider()
		 */
		public ISelectionProvider getSelectionProvider()
		{
			return site.getSelectionProvider();
		}

		/**
		 * @see org.eclipse.jface.window.IShellProvider#getShell()
		 */
		public Shell getShell()
		{
			return site.getShell();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchSite#getWorkbenchWindow()
		 */
		public IWorkbenchWindow getWorkbenchWindow()
		{
			return site.getWorkbenchWindow();
		}

		/**
		 * @see org.eclipse.ui.IWorkbenchSite#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
		 */
		public void setSelectionProvider(ISelectionProvider provider)
		{
			site.setSelectionProvider(provider);
		}

		/**
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter)
		{
			return site.getAdapter(adapter);
		}

		/**
		 * dispose
		 */
		public void dispose()
		{
			if (menuExtenders != null)
			{
				for (int i = 0; i < menuExtenders.size(); i++)
				{
					((PopupMenuExtender) menuExtenders.get(i)).dispose();
				}
				menuExtenders = null;
			}
		}

		// for Eclipse 3.1

		/**
		 * @see org.eclipse.ui.IWorkbenchPartSite#getPart()
		 */
		public IWorkbenchPart getPart()
		{
			return editor;
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#registerContextMenu(org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider, boolean)
		 */
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider,
				boolean includeEditorInput)
		{
			this.registerContextMenu(menuManager, selectionProvider);
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#registerContextMenu(java.lang.String, org.eclipse.jface.action.MenuManager,
		 *      org.eclipse.jface.viewers.ISelectionProvider, boolean)
		 */
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider,
				boolean includeEditorInput)
		{
			this.registerContextMenu(menuId, menuManager, selectionProvider);
		}

		/**
		 * getService
		 * 
		 * @param api
		 * @return Object
		 */
		public Object getService(Class api)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * hasService
		 * 
		 * @param api
		 * @return boolean
		 */
		public boolean hasService(Class api)
		{
			// TODO Auto-generated method stub
			return false;
		}
	}

	/**
	 * @see com.aptana.ide.editor.html.IHTMLEditorPart#setBrowserURL(java.lang.String)
	 */
	public void setBrowserURL(String url)
	{
		String browserURL = null;
		String value = null;
		String type = null;
		IEditorInput input = this.editor.getEditorInput();
		boolean isProjectFile = false;
		if (input instanceof IFileEditorInput)
		{
			isProjectFile = true;
			IFile file = ((IFileEditorInput) input).getFile();
			try
			{
				String override = file.getProject().getPersistentProperty(
						new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_OVERRIDE)); //$NON-NLS-1$
				if (HTMLPreviewPropertyPage.TRUE.equals(override))
				{
					type = file.getProject().getPersistentProperty(
							new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE)); //$NON-NLS-1$
					value = file.getProject().getPersistentProperty(
							new QualifiedName("", HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE)); //$NON-NLS-1$
				}
			}
			catch (CoreException e)
			{
				type = null;
				value = null;
			}
		}
		if (type == null || value == null)
		{
			type = HTMLPlugin.getDefault().getPreferenceStore().getString(
					HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
			value = HTMLPlugin.getDefault().getPreferenceStore().getString(HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		}
		
		PreviewInfo previewInfo = ServerFileTypeHandlers.getPreviewInfoFromURL(url);
		String alternativeValue = null;
		String serverTypeRestriction = null;
		if (previewInfo != null)
		{
			alternativeValue = previewInfo.serverID;
			serverTypeRestriction = previewInfo.serverTypeID;
		}
		
		if (HTMLPreviewPropertyPage.FILE_BASED_TYPE.equals(type))
		{
			browserURL = url;
		}
		else if (HTMLPreviewPropertyPage.SERVER_BASED_TYPE.equals(type)
				|| HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(type))
		{
			if (isProjectFile)
			{
				IServer[] servers = ServerCore.getServerManager().getServers();
				browserURL = null;
				if (alternativeValue != null && alternativeValue.length() != 0)
				{
					for (int i = 0; i < servers.length; i++)
					{
						final IServer curr = servers[i];
						if (curr.getId().equals(alternativeValue) 
								&& curr.getServerType().getId().equals(serverTypeRestriction))
						{
							browserURL = HTMLPreviewHelper.getServerURL(curr, input,
									HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(type),
									previewInfo.pathHeader);
							break;
						}
					}
				}
				if (browserURL == null)
				{
					for (int i = 0; i < servers.length; i++)
					{
						final IServer curr = servers[i];
						if (curr.getId().equals(value))
						{
							if (alternativeValue != null && alternativeValue.length() == 0
									& curr.getServerType().getId().equals(serverTypeRestriction))
							{
								browserURL = HTMLPreviewHelper.getServerURL(curr, input,
										HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(type),
										previewInfo.pathHeader);
							}
							else
							{
								browserURL = HTMLPreviewHelper.getServerURL(curr, input,
									HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE.equals(type));
							}
							break;
						}
					}
				}
			}
		}
		else if (HTMLPreviewPropertyPage.CONFIG_BASED_TYPE.equals(type))
		{
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchType = launchManager
					.getLaunchConfigurationType("com.aptana.ide.debug.core.jsLaunchConfigurationType"); //$NON-NLS-1$
			try
			{
				ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(launchType);
				for (int i = 0; i < configs.length; i++)
				{
					final ILaunchConfiguration current = configs[i];
					if (current.getName().equals(value))
					{
						browserURL = HTMLPreviewHelper.getConfigURL(current, input);
						break;
					}
				}
			}
			catch (CoreException e)
			{
			}
		}
		if (browserURL == null)
		{
			browserURL = url;
		}
		for (int i = 0; i < browsers.size(); i++)
		{

			((ContributedBrowser) browsers.get(i)).setURL(browserURL);
		}
	}

	private void loadBrowsers(Composite sash)
	{
		List browserList = BrowserExtensionLoader.loadBrowsers();
		if (browserList.size() > 0)
		{
			browserSash = null;
			if (isHorizontal)
			{
				browserSash = new SashForm(sash, SWT.VERTICAL);
				browserWeightPreference = IPreferenceConstants.HTMLEDITOR_TWO_BROWSER_WEIGHT_HORIZONTAL;
			}
			else
			{
				browserSash = new SashForm(sash, SWT.HORIZONTAL);
				browserWeightPreference = IPreferenceConstants.HTMLEDITOR_TWO_BROWSER_WEIGHT_VERTICAL;
			}

			for (int j = 0; j < browserList.size(); j++)
			{
				IConfigurationElement element = (IConfigurationElement) browserList.get(j);
				String name = BrowserExtensionLoader.getBrowserLabel(element);
				String outlineClass = element.getAttribute(UnifiedEditorsPlugin.OUTLINE_ATTR);
				try
				{
					Object obj = element.createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
					if (obj instanceof ContributedBrowser)
					{
						Composite comp = new Composite(browserSash, SWT.NONE);
						GridLayout layout = new GridLayout();
						layout.numColumns = 1;
						layout.makeColumnsEqualWidth = false;
						layout.marginHeight = 0;
						layout.marginWidth = 0;
						layout.verticalSpacing = 0;
						comp.setLayout(layout);
						comp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
						Label label = new Label(comp, SWT.WRAP);
						label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
						label.setText(StringUtils.format(Messages.SplitPageHTMLEditor_Preview, name));
						ContributedBrowser browser = (ContributedBrowser) obj;
						browser.createControl(comp);
						browsers.add(browser);
						if (outlineClass != null)
						{
							Object ol = element.createExecutableExtension(UnifiedEditorsPlugin.OUTLINE_ATTR);
							if (ol instanceof ContributedOutline)
							{
								ContributedOutline outline = (ContributedOutline) ol;
								browser.setOutline(outline);
								outline.setBrowser(browser);
								editor.getOutlinePage().addOutline(outline, name);
							}
						}
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(HTMLPlugin.getDefault(), StringUtils.format(
							Messages.SplitPageHTMLEditor_UnableToCreateBrowserControl, name), e);
				}
				catch (Error e)
				{
					IdeLog.logError(HTMLPlugin.getDefault(), StringUtils.format(
							Messages.SplitPageHTMLEditor_UnableToCreateBrowserControl, name), e);
				}
			}

			// Set weights from preferences
			String weights = HTMLPlugin.getDefault().getPreferenceStore().getString(browserWeightPreference);
			if (browsers.size() == 2 && weights != null && !weights.equals("")) //$NON-NLS-1$
			{
				try
				{
					String[] orderedWeights = weights.split(","); //$NON-NLS-1$
					if (orderedWeights != null && orderedWeights.length == 2)
					{
						int[] intWeights = new int[orderedWeights.length];
						intWeights[0] = Integer.parseInt(orderedWeights[0]);
						intWeights[1] = Integer.parseInt(orderedWeights[1]);
						browserSash.setWeights(intWeights);
					}
				}
				catch (Exception e)
				{
					// Do nothing
				}
				catch (Error e)
				{
					// Do nothing
				}
			}
		}
	}

	private void disposeBrowsers()
	{
		for (int i = 0; i < browsers.size(); i++)
		{
			((ContributedBrowser) browsers.get(i)).dispose();
		}
	}
}
