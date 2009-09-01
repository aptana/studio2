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
package com.aptana.ide.editors.views.profiles;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.WorkbenchHelper;
import com.aptana.ide.core.ui.widgets.Accordion;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.profiles.IProfileAppliedListener;
import com.aptana.ide.editors.profiles.IProfileChangeListener;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.profiles.ProfileURI;
import com.aptana.ide.editors.profiles.TransientProfileURI;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileContextContentEvent;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileContextListener;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedInformationControl;
import com.aptana.ide.editors.unified.utils.HTMLTextPresenter;

/**
 * ProfilesView is the view in charge of Code Assist Profiles. It contains a collection of JavaScript files, and allows
 * the user to add and remove files from the list. It will also automatically create a list of files based on the files
 * in the current HTML document by reading the &lt;script&gt; tags at the top.
 */
public class ProfilesView extends ViewPart implements IFileContextListener, IPropertyChangeListener
{
	static final String INFO_MESSAGE = Messages.ProfilesView_OpenOrDropFile;

	static final String[] FILTER_EXTENSIONS = new String[] { Messages.ProfilesView_JsExtension,
			Messages.ProfilesView_HtmlExtension, Messages.ProfilesView_AllExtension };

	static final String[] FILTER_NAMES = new String[] {
			StringUtils.format(Messages.ProfilesView_JavaScriptFiles, FILTER_EXTENSIONS[0]),
			StringUtils.format(Messages.ProfilesView_HTMLFiles, FILTER_EXTENSIONS[1]),
			StringUtils.format(Messages.ProfilesView_AllFiles, FILTER_EXTENSIONS[2]) };

	static final String defaultTitle = "Default Profile"; //$NON-NLS-1$

	static final String staticProtocol = "static://"; //$NON-NLS-1$

	static final String defaultPath = staticProtocol + defaultTitle;

	static final String titleLabel = " (Auto-created)"; //$NON-NLS-1$

	/**
	 * JS editor class
	 */
	protected static final String JS_EDITOR_CLASS = "com.aptana.ide.editor.js.JSEditor"; //$NON-NLS-1$	

	/**
	 * HTML editor class
	 */
	protected static final String HTML_EDITOR_CLASS = "com.aptana.ide.editor.html.HTMLEditor"; //$NON-NLS-1$

	/**
	 * Generic editor class
	 */
	protected static final String GENERIC_EDITOR_CLASS = "com.aptana.ide.editors"; //$NON-NLS-1$

	/**
	 * JS editor id
	 */
	protected static final String JS_EDITOR_ID = "com.aptana.ide.editors.JSEditor"; //$NON-NLS-1$

	/**
	 * HTML editor id
	 */
	protected static final String HTML_EDITOR_ID = "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$

	private boolean isLinked = true;

	private IPartListener _partListener;

	private static ImageDescriptor fUpIconDescriptor = getImageDescriptor("icons/upward_nav_on.gif"); //$NON-NLS-1$
	private static ImageDescriptor fDownIconDescriptor = getImageDescriptor("icons/downward_nav_on.gif"); //$NON-NLS-1$
	private static ImageDescriptor fAddFileIconDescriptor = getImageDescriptor("icons/js_file_new.gif"); //$NON-NLS-1$
	private static ImageDescriptor fAddProfileIconDescriptor = getImageDescriptor("icons/folder_new.gif"); //$NON-NLS-1$
	private static ImageDescriptor fRefreshIconDescriptor = getImageDescriptor("icons/refresh.gif"); //$NON-NLS-1$
	private static ImageDescriptor fLinkWithEditorIconDescriptor = getImageDescriptor("icons/sync.gif"); //$NON-NLS-1$
	private static ImageDescriptor fMakeCurrentProfileIconDescriptor = getImageDescriptor("icons/profile-current.gif"); //$NON-NLS-1$
	private static Image fErrIcon = getImageDescriptor("icons/error.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileIcon = getImageDescriptor("icons/profile.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileDynamicIcon = getImageDescriptor("icons/profile-dynamic.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileCurrentIcon = getImageDescriptor("icons/profile-current.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileCurrentDynamicIcon = getImageDescriptor("icons/profile-dynamic-current.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileFileIcon = getImageDescriptor("icons/js_file.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileFileDynamicIcon = getImageDescriptor("icons/js_file_new.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileFileIconGrey = getImageDescriptor("icons/js_file_grey.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileFileDynamicIconGrey = getImageDescriptor("icons/js_file_new_grey.gif").createImage(); //$NON-NLS-1$

	private Composite displayArea;
	private TreeViewer viewer;
	private Accordion accordion;
	private Composite fileDrawer;
	private Composite globalDrawer;
	private Composite top;
	private ToolBar globalToolbar;
	private ToolItem addGlobal;
	private ToolItem removeGlobal;
	private Table globalEnvironments;
	private ProgressBar bar;
	private StackLayout topLayout;
	private Label infoLabel;
	private Font infoLabelFont;
	private HashMap<File, Image> images = new HashMap<File, Image>();

	private Action actionAddProfile;
	private Action actionAdd;
	private Action actionDelete;
	private Action actionMoveUp;
	private Action actionMoveDown;
	private Action actionRefresh;
	private Action actionDoubleClick;
	private Action actionAddCurrentFile;
	private Action actionMakeStatic;
	private Action actionLinkWithEditor;
	private Action actionMakeCurrent;

	private ArrayList<IProfilesViewEventListener> listeners = new ArrayList<IProfilesViewEventListener>();

	/**
	 * fireProfilesViewEvent
	 * 
	 * @param e
	 */
	public void fireProfilesViewEvent(ProfilesViewEvent e)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			IProfilesViewEventListener listener = listeners.get(i);
			listener.onProfilesViewEvent(e);
		}
	}

	/**
	 * addProfilesViewEventListener
	 * 
	 * @param l
	 */
	public void addProfilesViewEventListener(IProfilesViewEventListener l)
	{
		listeners.add(l);
	}

	/**
	 * removeProfilesViewEventListener
	 * 
	 * @param l
	 */
	public void removeProfilesViewEventListener(IProfilesViewEventListener l)
	{
		listeners.remove(l);
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
	 * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */
	class ViewContentProvider implements ITreeContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent)
		{
			if (parent instanceof IPath == false)
			{
				boolean showProfiles = false;
				Profile[] profiles = profileManager.getProfiles();

				if (profiles != null && profiles.length > 0)
				{
					showProfiles = true;
				}

				if (showProfiles && profiles.length == 1
						&& profiles[0].getName().equals(ProfileManager.DEFAULT_PROFILE_NAME)
						&& profiles[0].getURIs().length == 0)
				{
					showProfiles = false;
				}

				if (showProfiles)
				{
					if (topLayout != null && infoLabel != null && top != null && topLayout.topControl == infoLabel)
					{
						topLayout.topControl = viewer.getControl();
						top.layout();
					}

					return profiles;
				}
			}

			if (topLayout != null && infoLabel != null && top != null)
			{
				topLayout.topControl = infoLabel;
				top.layout();
			}

			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof Profile)
			{
				Profile profile = (Profile) parentElement;

				ProfileURI[] uris = profile.getURIs();

				return uris;
			}
			else if (parentElement instanceof ProfileURI)
			{
				ProfileURI path = (ProfileURI) parentElement;

				return path.getChildren();
			}
			else
			{
				return new Object[0];
			}

		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if (element instanceof ProfileURI)
			{
				return ((ProfileURI) element).getParent();
			}

			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			if (element instanceof Profile)
			{
				Profile profile = (Profile) element;
				boolean children = profile.getURIs().length > 0;
				return children;

			}
			else if (element instanceof ProfileURI)
			{
				ProfileURI path = (ProfileURI) element;
				return path.getChildren().length > 0;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * ViewerSorterProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ViewerSorterProvider extends ViewerSorter
	{
		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element)
		{
			if (element instanceof Profile)
			{
				if (((Profile) element).getName().equals(ProfileManager.DEFAULT_PROFILE_NAME))
				{
					return 0;
				}
			}

			return 1;
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{

			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 != cat2)
			{
				return cat1 - cat2;
			}

			return 0;
		}
	}

	/**
	 * ViewLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ViewLabelProvider extends LabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			if (element instanceof Profile)
			{
				Profile profile = (Profile) element;
				return profile.getName();
			}
			else if (element instanceof ProfileURI)
			{
				ProfileURI profileURI = (ProfileURI) element;
				String uri = StringUtils.urlDecodeFilename(profileURI.getURI().toCharArray());

				int lastSlashIndex = uri.lastIndexOf("/"); //$NON-NLS-1$

				if (lastSlashIndex != -1)
				{
					return uri.substring(lastSlashIndex + 1);
				}
				else
				{
					return uri;
				}
			}
			else
			{
				return null;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj)
		{
			if (obj instanceof Profile)
			{
				Profile p = (Profile) obj;

				if (profileManager.isCurrentProfile(p))
				{
					if (p.isDynamic())
					{
						return fProfileCurrentDynamicIcon;
					}
					else
					{
						return fProfileCurrentIcon;
					}
				}
				else
				{
					if (p.isDynamic())
					{
						return fProfileDynamicIcon;
					}
					else
					{
						return fProfileIcon;
					}

				}
			}
			else if (obj instanceof ProfileURI)
			{
				ProfileURI uri = (ProfileURI) obj;
				URL url = FileUtils.uriToURL(uri.getURI());

				if (url != null && ProfilesViewHelper.isValidURL(url))
				{
					if (obj instanceof TransientProfileURI)
					{
						return fProfileFileDynamicIcon;
					}
					else
					{
						return fProfileFileIcon;
					}
				}
				else
				{
					return fErrIcon;
				}
			}
			else
			{
				return null;
			}
		}
	}

	/**
	 * profileManager
	 */
	public ProfileManager profileManager;

	/**
	 * The constructor.
	 */
	public ProfilesView()
	{
		profileManager = UnifiedEditorsPlugin.getDefault().getProfileManager();

		profileManager.addProfileChangeListener(new IProfileChangeListener()
		{

			public void onProfileChanged(Profile p)
			{

				if (viewer.getTree().isDisposed())
				{
					return;
				}

				viewer.getTree().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{

						if (viewer.getTree().isDisposed())
						{
							return;
						}

						Object[] expanded = viewer.getExpandedElements();
						viewer.refresh();
						viewer.setExpandedElements(expanded);
					}
				});
			}
		});

		profileManager.addProfileAppliedListener(new IProfileAppliedListener()
		{

			public void onProfileApplied(final ProfileURI profileURI, final boolean state)
			{

				if (viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed())
				{
					return;
				}

				viewer.getTree().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{

						if (viewer.getTree().isDisposed())
						{
							return;
						}

						TreeItem[] treeItems = viewer.getTree().getItems();

						boolean toggleState = state;

						for (int i = 0; i < treeItems.length; i++)
						{
							Object o = treeItems[i].getData();

							if (o instanceof Profile)
							{
								Profile profile = (Profile) o;

								if (profile.getURI().equals(profileURI.getParent().getURI()))
								{
									TreeItem[] children = treeItems[i].getItems();

									for (int j = 0; j < children.length; j++)
									{
										TreeItem treeItem = children[j];

										ProfileURI profileURIItem = (ProfileURI) treeItem.getData();

										String uri = profileURI.getURI();

										if (profileURIItem != null && profileURIItem.getURI().equals(uri))
										{
											if (treeItem.getImage() == fErrIcon)
											{
												return;
											}

											if (profileURIItem instanceof TransientProfileURI)
											{
												if (toggleState)
												{
													treeItem.setImage(fProfileFileDynamicIcon);
												}
												else
												{
													treeItem.setImage(fProfileFileDynamicIconGrey);
												}
											}
											else
											{
												if (toggleState)
												{
													treeItem.setImage(fProfileFileIcon);
												}
												else
												{
													treeItem.setImage(fProfileFileIconGrey);
												}
											}

											return;
										}
									}
								}
							}
						}
					}
				});
			}

		});

		JSPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	private void bindToWorkbench()
	{
		final ProfilesView me = this;
		me._partListener = new IPartListener()
		{
			public void partBroughtToTop(IWorkbenchPart part)
			{
			}

			public void partClosed(IWorkbenchPart part)
			{

				if (part instanceof IEditorPart)
				{

					String uri = CoreUIUtils.getURI((IEditorPart) part);
					if (part.getClass().getName().equals(HTML_EDITOR_CLASS))
					{
						Profile profile = profileManager.getProfile(uri);
						if (getCurrentProfile() != null && getCurrentProfile().equals(profile))
						{
							setCurrentProfile(defaultPath);
						}
						if (profile != null)
						{
							deleteProfile(profile.getURI());

						}
					}
					// else if(part.getClass().getName().equals(JS_EDITOR_CLASS))
					// {
					// }
				}
			}

			public void partDeactivated(IWorkbenchPart part)
			{
				if (part instanceof IEditorPart)
				{
					IEditorPart activeEditor = CoreUIUtils.getActiveEditor();

					if (activeEditor != null && activeEditor instanceof IUnifiedEditor)
					{
						IUnifiedEditor editor = (IUnifiedEditor) activeEditor;
						editor.getFileContext().removeDelayedFileListener(me);
					}
				}
			}

			public void partOpened(IWorkbenchPart part)
			{

			}

			public void partActivated(IWorkbenchPart part)
			{
				if (part instanceof IEditorPart)
				{

					// This check is done to see if the current editor input is valid and if not do not add any profile
					// I.E when the file doesn't exist
					if (part instanceof ITextEditor)
					{
						ITextEditor te = (ITextEditor) part;
						if (te.getDocumentProvider() instanceof IDocumentProviderExtension)
						{
							IDocumentProviderExtension extension = (IDocumentProviderExtension) te
									.getDocumentProvider();
							IStatus status = extension.getStatus(te.getEditorInput());
							if (status != null && !status.isOK())
							{
								return;
							}
						}
					}

					String uri = CoreUIUtils.getURI((IEditorPart) part);
					IEditorPart activeEditor = CoreUIUtils.getActiveEditor();

					if (activeEditor != null && activeEditor instanceof IUnifiedEditor)
					{
						IUnifiedEditor editor = (IUnifiedEditor) activeEditor;
						editor.getFileContext().addDelayedFileListener(me);
					}
					boolean needsRefresh = false;

					// Don't load up code assist on files not on disk.
					if (uri == null || uri.equals("")) //$NON-NLS-1$
					{
						return;
					}

					if (activeEditor != null && part.getClass().getName().equals(HTML_EDITOR_CLASS))
					{
						needsRefresh = true;
						if (profileManager.getProfile(uri) == null
								&& profileManager.getProfile(staticProtocol + uri) == null)
						{

							String title = activeEditor.getTitle();
							Profile profile = createDynamicProfile(title + titleLabel, uri);

							URL url = FileUtils.uriToURL(uri);
							if (url != null)
							{
								String[] scripts = addScriptTags(url);
								if (scripts.length > 0)
								{
									profile.addTransientURIs(scripts);
								}
							}
						}

						if (isLinked)
						{
							if (profileManager.getProfile(staticProtocol + uri) != null)
							{
								setCurrentProfile(staticProtocol + uri);
							}
							else
							{
								setCurrentProfile(uri);
							}

							expandProfile(getCurrentProfile().getURI());
						}

					}
					else if (activeEditor != null && part.getClass().getName().equals(JS_EDITOR_CLASS))
					{
						needsRefresh = true;
						if (isLinked)
						{
							Profile[] profiles = profileManager.getProfiles();
							String[] uris = null;
							String profileUri = ""; //$NON-NLS-1$

							for (int i = 0; i < profiles.length; i++)
							{
								uris = profiles[i].getURIsAsStrings();
								for (int j = 0; j < uris.length; j++)
								{
									if (uris[j].equals(uri))
									{
										profileUri = profiles[i].getURI();
									}
								}
							}

							if (profileUri.equals("") != true) { //$NON-NLS-1$
								setCurrentProfile(profileUri);
							}
							else
							{
								setCurrentProfile(defaultPath);
							}

							Profile currentProfile = getCurrentProfile();
							if (currentProfile != null)
							{
								expandProfile(currentProfile.getURI());
							}
						}
					}
					else if (part.getClass().getName().indexOf(GENERIC_EDITOR_CLASS) != -1)
					{
						if (isLinked)
						{
							setCurrentProfile(defaultPath);
						}
					}
					if (needsRefresh)
					{
						Job updateJob = new Job(Messages.ProfilesView_UpdatingProfileEnvironment)
						{

							protected IStatus run(IProgressMonitor monitor)
							{
								UnifiedEditorsPlugin.getDefault().getProfileManager().onUpdaterThreadUpdate();
								return Status.OK_STATUS;
							}

						};
						updateJob.setSystem(true);
						updateJob.schedule();
					}
				}

			}
		};
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

				if (window != null)
				{
					window.getPartService().addPartListener(_partListener);
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (infoLabelFont != null)
		{
			infoLabelFont.dispose();
		}

		if (fErrIcon != null)
		{
			fErrIcon.dispose();
		}
		JSPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.aptana.ide.js.ui.ProfilesView"); //$NON-NLS-1$

		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		accordion = new Accordion();
		accordion.createAccordion(displayArea);
		fileDrawer = accordion.addDrawer(Messages.ProfilesView_File_references);
		Composite fda = accordion.getDrawerArea(fileDrawer);
		GridLayout fdaLayout = new GridLayout(1, true);
		fdaLayout.marginHeight = 0;
		fdaLayout.marginWidth = 0;
		fda.setBackground(fda.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		fda.setLayout(fdaLayout);
		globalDrawer = accordion.addDrawer(Messages.ProfilesView_Global_references);
		Composite gda = accordion.getDrawerArea(globalDrawer);
		GridLayout gdaLayout = new GridLayout(1, true);
		gdaLayout.marginHeight = 0;
		gdaLayout.marginWidth = 0;
		gda.setBackground(gda.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		gda.setLayout(gdaLayout);

		top = new Composite(fda, SWT.NONE);
		top.setBackground(top.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		topLayout = new StackLayout();
		top.setLayout(topLayout);
		GridData topData = new GridData(SWT.FILL, SWT.FILL, true, true);
		topData.horizontalIndent = 10;
		top.setLayoutData(topData);

		viewer = createTreeViewer(top);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ViewerSorterProvider());
		viewer.setAutoExpandLevel(2);
		viewer.setInput(getViewSite());

		// Scroll to top left if not shown and not empty
		if (viewer.getTree().getItemCount() > 0)
		{
			viewer.getTree().showItem(viewer.getTree().getItem(0));
		}
		if (viewer.getTree().getColumnCount() > 0)
		{
			viewer.getTree().showColumn(viewer.getTree().getColumn(0));
		}

		infoLabel = new Label(top, SWT.CENTER);
		infoLabelFont = new Font(parent.getDisplay(), Messages.ProfilesView_Font_name, 12, SWT.NONE);
		infoLabel.setFont(infoLabelFont);
		infoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		PreferenceUtils.ignoreForegroundColorPreference(infoLabel);
		infoLabel.setData(PreferenceUtils.IGNORE_COLOR_KEY, Boolean.TRUE);
		infoLabel.setText(INFO_MESSAGE);

		topLayout.topControl = infoLabel;
		top.layout();

		createEnvironmentTable();

		bar = new ProgressBar(gda, SWT.SMOOTH | SWT.HORIZONTAL);
		GridData barData = new GridData(SWT.FILL, SWT.FILL, true, false);
		barData.exclude = true;
		barData.heightHint = 10;
		bar.setLayoutData(barData);
		bar.setVisible(false);
		bar.setMaximum(5);
		bar.setMinimum(0);
		bar.setSelection(3);

		fillEnvironmentTable();

		bindToWorkbench();
		initStaticProfiles();
		checkForOpenEditor();

		addDragDrop();

		makeActions();
		hookKeyActions(viewer.getControl());
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		PreferenceUtils.registerBackgroundColorPreference(parent,
				"com.aptana.ide.core.ui.background.color.profilesView"); //$NON-NLS-1$		
		PreferenceUtils.registerForegroundColorPreference(parent,
				"com.aptana.ide.core.ui.foreground.color.profilesView"); //$NON-NLS-1$
		viewer.refresh();

		accordion.setInitialDrawerOpen(fileDrawer);
	}

	private void addDragDrop()
	{
		final DropTarget labeldt = new DropTarget(infoLabel, DND.DROP_MOVE);

		labeldt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		labeldt.addDropListener(new DropTargetAdapter()
		{
			public void drop(DropTargetEvent event)
			{
				handleDrop(event);
			}
		});

		DragSource ds = new DragSource(viewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		ds.addDragListener(new DragSourceAdapter()
		{
			@SuppressWarnings("unchecked")
			public void dragStart(DragSourceEvent event)
			{

				super.dragStart(event);

				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				for (Iterator iter = selection.iterator(); iter.hasNext();)
				{
					Object element = iter.next();
					if (element instanceof Profile)
					{
						Profile p = (Profile) element;
						if (p.getURIs().length == 0)
						{
							event.doit = false;
							return;
						}
					}
				}
			}

			public void dragSetData(DragSourceEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				if (FileTransfer.getInstance().isSupportedType(event.dataType))
				{
					Object[] items = selection.toArray();
					ArrayList<String> al = new ArrayList<String>();

					for (int i = 0; i < items.length; i++)
					{
						if (items[i] instanceof ProfileURI)
						{
							al.add(CoreUIUtils.getPathFromURI(((ProfileURI) items[i]).getURI()));
						}
						else if (items[i] instanceof Profile)
						{
							Profile p = (Profile) items[i];
							for (int j = 0; j < p.getURIs().length; j++)
							{
								ProfileURI object = p.getURIs()[j];
								al.add(CoreUIUtils.getPathFromURI(object.getURI()));
							}
						}
					}

					event.data = al.toArray(new String[0]);
				}
			}
		});

		DropTarget dt = new DropTarget(viewer.getControl(), DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter()
		{
			public void drop(DropTargetEvent event)
			{
				handleDrop(event);
			}
		});
	}

	private void changeEnvironment()
	{
		UIJob job = new UIJob(Messages.ProfilesView_Refreshing_environment_job_title)
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				bar.setSelection(4);
				TableItem[] items = globalEnvironments.getItems();
				List<String> environments = new ArrayList<String>();
				List<String> disabledEnvironments = new ArrayList<String>();
				for (int i = 0; i < items.length; i++)
				{
					if (items[i].getChecked())
					{
						environments.add(items[i].getText().trim());
					}
					else
					{
						disabledEnvironments.add(items[i].getText().trim());
					}
				}
				JSLanguageEnvironment.setEnabledEnvironments(environments.toArray(new String[0]));
				JSLanguageEnvironment.setDisabledEnvironments(disabledEnvironments.toArray(new String[0]));

				try
				{
					JSLanguageEnvironment.resetEnvironment();
				}
				catch (Exception e)
				{
					MessageDialog.openError(getDisplay().getActiveShell(), Messages.ProfilesView_ERR_Setting_global_refs,
							Messages.ProfilesView_ERR_MSG_error_occurred_setting_global_refs);
					IdeLog.logError(JSPlugin.getDefault(), Messages.ProfilesView_ERR_Setting_global_refs, e);
				}

				bar.setSelection(5);
				GridData data = (GridData) bar.getLayoutData();
				data.exclude = true;
				bar.setVisible(false);
				accordion.getDrawerArea(globalDrawer).layout(true, true);
				globalEnvironments.setEnabled(true);
				return Status.OK_STATUS;
			}

		};
		job.schedule();
	}

	private void createEnvironmentTable()
	{
		globalToolbar = new ToolBar(accordion.getDrawerArea(globalDrawer), SWT.FLAT | SWT.WRAP);
		globalToolbar.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
		addGlobal = new ToolItem(globalToolbar, SWT.PUSH);
		addGlobal.setToolTipText(Messages.ProfilesView_Add_reference);
		addGlobal.setImage(JSPlugin.getImage("icons/add_obj.gif")); //$NON-NLS-1$
		addGlobal.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog newGlobal = new FileDialog(globalToolbar.getShell(), SWT.OPEN);
				newGlobal.setFilterExtensions(new String[] { "*.xml;*.js;*.sdoc;*.bin" }); //$NON-NLS-1$
				newGlobal.setText(Messages.ProfilesView_Select_new_global_ref);
				String rc = newGlobal.open();
				if (rc != null)
				{
					File file = new File(rc);
					String name = file.getName();
					TableItem[] items = globalEnvironments.getItems();
					for (int i = 0; i < items.length; i++)
					{
						if (items[i].getText().equals(name))
						{
							CoreUIUtils
									.showMessage(Messages.ProfilesView_ERR_MSG_coudlnt_add_global_ref);
							return;
						}
					}
					JSLanguageEnvironment.addUserEnvironment(file.getAbsolutePath());
					TableItem item = new TableItem(globalEnvironments, SWT.NONE);
					item.setText(file.getName());
					item.setData(file);
					item.setChecked(true);
					item.setImage(JSPlugin.getImage("icons/js_file.gif")); //$NON-NLS-1$
					changeEnvironment();
				}

			}

		});
		removeGlobal = new ToolItem(globalToolbar, SWT.PUSH);
		removeGlobal.setToolTipText(Messages.ProfilesView_Remove_reference);
		removeGlobal.setEnabled(false);
		removeGlobal.setImage(JSPlugin.getImage("icons/delete_obj.gif")); //$NON-NLS-1$
		removeGlobal.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (globalEnvironments.getSelectionCount() == 1)
				{
					TableItem item = globalEnvironments.getSelection()[0];
					if (item.getData() instanceof File)
					{
						JSLanguageEnvironment.removeUserEnvironment(((File) item.getData()).getAbsolutePath());
						item.dispose();
						changeEnvironment();
					}
				}
				removeGlobal.setEnabled(false);
			}

		});
		globalEnvironments = new Table(accordion.getDrawerArea(globalDrawer), SWT.CHECK | SWT.SINGLE | SWT.V_SCROLL
				| SWT.H_SCROLL);
		globalEnvironments.setBackground(globalEnvironments.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		globalEnvironments.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.CHECK)
				{
					globalEnvironments.setEnabled(false);
					GridData data = (GridData) bar.getLayoutData();
					data.exclude = false;
					bar.setSelection(3);
					bar.setVisible(true);
					accordion.getDrawerArea(globalDrawer).layout(true, true);
					changeEnvironment();
				}
				else if (globalEnvironments.getSelectionCount() == 1)
				{
					TableItem item = globalEnvironments.getSelection()[0];
					removeGlobal.setEnabled(item.getData() instanceof File);
				}
			}

		});
		GridData geData = new GridData(SWT.FILL, SWT.FILL, true, true);
		geData.horizontalIndent = 10;
		globalEnvironments.setLayoutData(geData);
	}

	private void fillEnvironmentTable()
	{
		List<IConfigurationElement> elementList = new ArrayList<IConfigurationElement>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(JSPlugin.ID, JSLanguageEnvironment.SCRIPTDOC_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						String agent = element.getAttribute(JSLanguageEnvironment.ATTR_USER_AGENT);
						if (agent != null)
						{
							elementList.add(element);
						}
					}
				}
			}
		}
		Collections.sort(elementList, new Comparator<IConfigurationElement>()
		{

			public int compare(IConfigurationElement arg0, IConfigurationElement arg1)
			{
				String agent0 = arg0.getAttribute(JSLanguageEnvironment.ATTR_USER_AGENT);
				String agent1 = arg1.getAttribute(JSLanguageEnvironment.ATTR_USER_AGENT);
				return agent0.compareTo(agent1);
			}

		});
		for (int i = 0; i < elementList.size(); i++)
		{
			IConfigurationElement element = elementList.get(i);
			IExtension declaring = element.getDeclaringExtension();
			String declaringPluginID = declaring.getNamespaceIdentifier();
			Bundle bundle = Platform.getBundle(declaringPluginID);
			String agent = element.getAttribute(JSLanguageEnvironment.ATTR_USER_AGENT);
			String icon = element.getAttribute(JSLanguageEnvironment.ATTR_ICON);

			if (agent != null)
			{
				TableItem item = new TableItem(globalEnvironments, SWT.NONE);
				item.setText(agent);
				item.setData(element);
				if (icon != null)
				{
					String iconFile = getResolvedFilename(bundle, icon);
					if (iconFile != null)
					{
						File file = new File(iconFile);
						if (file.exists())
						{
							Image result = null;
							if (images.containsKey(file.getAbsolutePath()))
							{
								result = images.get(file.getAbsolutePath());
							}
							else
							{
								result = new Image(Display.getDefault(), file.getAbsolutePath());
							}
							images.put(file.getAbsoluteFile(), result);
							item.setImage(result);
						}
					}
				}
			}
		}
		String[] userAdded = JSLanguageEnvironment.getUserAddedJSEnvironments();
		for (String path : userAdded)
		{
			File file = new File(path);

			if (file.exists())
			{
				TableItem item = new TableItem(globalEnvironments, SWT.NONE);
				item.setText(file.getName());
				item.setData(file);
				item.setImage(JSPlugin.getImage("icons/js_file.gif")); //$NON-NLS-1$
			}
		}
		checkLoadedEnvironments();
	}

	/**
	 * checkLoadedEnvironments
	 * 
	 * @param environments
	 */
	private void checkLoadedEnvironments()
	{
		String[] environments = JSLanguageEnvironment.getLoadedEnvironments();

		if (globalEnvironments != null && !globalEnvironments.isDisposed())
		{
			TableItem[] items = globalEnvironments.getItems();
			for (int i = 0; i < items.length; i++)
			{
				items[i].setChecked(false);
			}

			for (int i = 0; i < items.length; i++)
			{
				String label = items[i].getText();
				for (int j = 0; j < environments.length; j++)
				{
					if (label.equals(environments[j]))
					{
						items[i].setChecked(true);
						break;
					}
				}
			}
		}
	}

	private static URL getResolvedURL(Bundle b, String fullPath)
	{
		URL url = FileLocator.find(b, new Path(fullPath), null);

		if (url != null)
		{
			try
			{

				URL localUrl = FileLocator.toFileURL(url);
				if (localUrl != null)
				{
					return localUrl;
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(JSPlugin.getDefault(), e.getMessage());
			}
		}
		return null;
	}

	private static String getResolvedFilename(Bundle b, String fullPath)
	{
		URL url = getResolvedURL(b, fullPath);
		if (url != null)
		{
			return url.getFile();
		}

		return null;
	}

	private String getEditorSource(IEditorPart editorPart)
	{
		String retVal = ""; //$NON-NLS-1$

		if (editorPart instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor) editorPart;

			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());

			retVal = doc.get();
		}

		return retVal;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
	 */
	public void onContentChanged(FileContextContentEvent evt)
	{

		IEditorPart sourceEditor = CoreUIUtils.getActiveEditor();

		if (sourceEditor != null && sourceEditor.getClass().getName().equals(HTML_EDITOR_CLASS))
		{

			String uri = CoreUIUtils.getURI(sourceEditor);
			String path = CoreUIUtils.getPathFromURI(uri);

			Profile profile = profileManager.getProfile(uri);
			if (profile == null)
			{
				profile = profileManager.getProfile(staticProtocol + path);
			}

			String[] set = null;
			if (profile != null)
			{

				set = profile.getURIsIncludingChildrenAsStrings();
			}
			else
			{
				set = new String[] {};
			}

			URL basePath = ProfilesViewHelper.getBasePath(uri);

			if (basePath != null)
			{

				String[] newSet = ProfilesViewHelper.addScriptTagsFromHTMLSource(basePath,
						getEditorSource(sourceEditor), this.getFileService().getParseState());

				boolean diff = false;

				if (set.length != newSet.length)
				{
					diff = true;
				}
				else
				{
					for (int i = 0; i < set.length; i++)
					{
						if (!set[i].equals(newSet[i]))
						{
							diff = true;
						}
					}
				}

				if (diff)
				{
					if (profile != null)
					{
						profile.replaceAllURIs(set, newSet);
					}
				}
			}
		}

	}

	/**
	 * handleDrop
	 * 
	 * @param event
	 */
	protected void handleDrop(DropTargetEvent event)
	{
		String[] files = (String[]) event.data;

		ArrayList<ProfileURI> paths = new ArrayList<ProfileURI>();

		for (int i = 0; i < files.length; i++)
		{
			paths.add(new ProfileURI(CoreUIUtils.getURI(files[i])));
		}

		if (paths.size() > 0)
		{
			Profile profile = null;
			ProfileURI[] uris = paths.toArray(new ProfileURI[0]);

			Widget w = event.item;

			if (w != null)
			{
				TreeItem item = (TreeItem) w;
				Object element = item.getData();

				if (element instanceof Profile)
				{
					profile = (Profile) element;
				}
				else if (element instanceof ProfileURI)
				{
					profile = ((ProfileURI) element).getParent();
				}

			}

			this.onProfilesAddDropEvent(uris, profile);
		}
	}

	private String[] addScriptTags(URL file)
	{
		URL basePath = ProfilesViewHelper.getBasePath(file);
		if (basePath != null)
		{
			String text;
			try
			{
				text = FileUtils.readContent(file);
				return ProfilesViewHelper.addScriptTagsFromHTMLSource(basePath, text, this.getFileService()
						.getParseState());
			}
			catch (IOException e)
			{
				return new String[0];
			}
		}
		else
		{
			return new String[0];
		}
	}

	private IUnifiedEditor getUnifiedEditor()
	{
		IEditorPart part = CoreUIUtils.getActiveEditor();
		IUnifiedEditor result = null;

		if (part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor) part;

			if (editor instanceof IUnifiedEditor)
			{
				result = (IUnifiedEditor) editor;
			}
		}

		return result;
	}

	private FileService getFileService()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();
		FileService result = null;

		if (uniEditor != null)
		{
			IFileService service = uniEditor.getFileContext();

			if (service instanceof EditorFileContext)
			{
				service = ((EditorFileContext) service).getFileContext();
			}

			if (service instanceof FileService)
			{
				result = (FileService) service;
			}
		}

		return result;
	}

	private void onProfilesAddDropEvent(ProfileURI[] uris, Profile profile)
	{

		String string = ""; //$NON-NLS-1$
		String fileSetName = string;
		ArrayList<String> fileList = new ArrayList<String>();

		// Check to see if files were added to a selection
		if (profile == null)
		{
			profile = profileManager.getProfile(defaultPath);

			if (profile == null)
			{
				profile = createProfile(defaultTitle, defaultPath);
			}
		}

		for (int i = 0; i < uris.length; i++)
		{
			String filename = uris[i].getURI();

			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(filename);
			if (desc != null && desc.getId().equals(HTML_EDITOR_ID))
			{
				// Get just the last segment
				int lastSlashIndex = filename.lastIndexOf("/"); //$NON-NLS-1$
				String name = (lastSlashIndex == -1) ? filename : filename.substring(lastSlashIndex + 1);

				// Just in case a dynamic profile is present, remove it
				profile.removeURIs(new ProfileURI[] { new ProfileURI(filename) });

				fileSetName = staticProtocol + filename;

				// Now create the static profile
				profile = createProfile(name, fileSetName);

				URI uri;
				String[] scripts = new String[0];

				try
				{
					uri = new URI(filename);
					scripts = addScriptTags(uri.toURL());
				}
				catch (URISyntaxException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProfilesView_URISyntaxException, e);
				}
				catch (MalformedURLException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ProfilesView_MalformedURLException, e);
				}
				if (scripts.length == 0)
				{
					showMessage(Messages.ProfilesView_NoScriptTagFound);
				}
				else
				{
					fileList.addAll(Arrays.asList(scripts));
				}
			}
			else if (desc != null && desc.getId().equals(JS_EDITOR_ID))
			{
				fileSetName = profile.getURI();

				if (fileSetName == null || fileSetName == string)
				{
					fileSetName = defaultPath;
				}

				fileList.add(filename);

				URL url = FileUtils.uriToURL(filename);
				if (url != null)
				{
					String[] sdocs = ProfilesViewHelper.addScriptFromJavaScriptSource(url);
					fileList.addAll(Arrays.asList(sdocs));
				}
			}
			else
			{
				showMessage(Messages.ProfilesView_UnsupportedFileType);
			}
		}

		if (fileList.size() > 0)
		{
			profile.addURIs(fileList.toArray(new String[fileList.size()]));

			if (fileSetName.indexOf(staticProtocol) != 0)
			{
				boolean result = showConfirmMessage(Messages.ProfilesView_ConfirmProfilePersistence);

				String[] urisStrings = profile.getURIsAsStrings();
				if (result)
				{
					onProfilesMakeStaticEvent(profile, urisStrings);
				}
			}

		}
	}

	private void onProfilesMakeStaticEvent(Profile profile, String[] fileListArray)
	{

		String profileName = profile.getName();
		String path = profile.getURI();
		boolean wasSelected = false;

		if (path == this.getCurrentProfile().getURI())
		{
			wasSelected = true;
		}

		String newPath = staticProtocol + path;

		deleteProfile(path);

		if (profileName.indexOf(titleLabel) != -1)
		{
			profileName = profileName.substring(0, profileName.length() - titleLabel.length());
		}

		Profile newProfile = createProfile(profileName, newPath);

		newProfile.addURIs(fileListArray);

		if (wasSelected)
		{
			setCurrentProfile(newProfile.getURI());
		}
	}

	/*
	 * private void addToSet(set, item) { var newSet = []; for(var i=0;i<set.length;i++) { newSet.push(set[i]); }
	 * newSet.push(item); return newSet; }
	 */

	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	protected TreeViewer createTreeViewer(Composite parent)
	{
		final TreeViewer treeViewer = new TreeViewer(new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
		GridData tvData = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeViewer.getTree().setLayoutData(tvData);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				Object[] paths = ((ITreeSelection) treeViewer.getSelection()).toArray();
				actionMakeCurrent.setEnabled(paths.length == 1 && paths[0] instanceof Profile);
				for (int i = 0; i < paths.length; i++)
				{
					if (paths[i] instanceof Profile)
					{
						actionMoveDown.setEnabled(false);
						actionMoveUp.setEnabled(false);
						return;
					}
				}
				actionMoveDown.setEnabled(true);
				actionMoveUp.setEnabled(true);
			}

		});

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				StyledText label = (StyledText) event.widget;
				Shell shell = (Shell) label.getData("_SHELL"); //$NON-NLS-1$
				switch (event.type)
				{
					case SWT.MouseDown:
						Event e = new Event();
						e.item = (TreeItem) label.getData("_TREEITEM"); //$NON-NLS-1$
						// Assuming table is single select, set the selection as if
						// the mouse down event went through to the table
						treeViewer.getTree().setSelection(new TreeItem[] { (TreeItem) e.item });
						treeViewer.getTree().notifyListeners(SWT.Selection, e);
						// fall through
					case SWT.MouseExit:
						shell.dispose();
						break;
					default:
						break;
				}
			}
		};

		final Shell shell = getSite().getShell();

		Listener tableListener = new Listener()
		{
			UnifiedInformationControl info = null;

			public void handleEvent(Event event)
			{
				switch (event.type)
				{
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove:
					{
						if (info == null || info.getShell() == null)
						{
							break;
						}
						info.getShell().dispose();
						info = null;
						break;
					}
					case SWT.MouseHover:
					{
						TreeItem item = treeViewer.getTree().getItem(new Point(event.x, event.y));
						if (item != null)
						{
							if (info != null && info.getShell() != null && !info.getShell().isDisposed())
							{
								info.getShell().dispose();
							}

							info = new UnifiedInformationControl(shell, SWT.NONE, new HTMLTextPresenter(false));

							info.getStyledTextWidget().setData("_TREEITEM", item); //$NON-NLS-1$
							info.getStyledTextWidget().setData("_SHELL", info.getShell()); //$NON-NLS-1$
							info.getStyledTextWidget().addListener(SWT.MouseExit, labelListener);
							info.getStyledTextWidget().addListener(SWT.MouseDown, labelListener);

							Object data = item.getData();
							String txt = null;

							if (data instanceof Profile)
							{
								Profile profile = (Profile) data;
								txt = StringUtils.format(Messages.ProfilesView_ProfileItems, new Object[] {
										profile.getName(), Integer.toString(profile.getURIs().length) });
							}
							else if (data instanceof ProfileURI)
							{
								ProfileURI uri = (ProfileURI) data;
								txt = StringUtils.urlDecodeFilename(uri.getURI().toCharArray());
							}

							if (txt != null)
							{
								info.setSizeConstraints(300, 500);
								info.setInformation(txt);

								StyledText styledText = info.getStyledTextWidget();
								GC gc = new GC(styledText);
								int width = gc.getFontMetrics().getAverageCharWidth();

								width = ((txt.length() + 2) * width);

								Rectangle rect = item.getBounds(0);
								Point pt = treeViewer.getTree().toDisplay(20 + rect.x, rect.y);

								info.setSize(width, 0);
								info.setLocation(pt);
								info.setVisible(true);
							}
						}
					}
					default:
						break;
				}
			}
		};

		treeViewer.getTree().addListener(SWT.Dispose, tableListener);
		treeViewer.getTree().addListener(SWT.KeyDown, tableListener);
		treeViewer.getTree().addListener(SWT.MouseMove, tableListener);
		treeViewer.getTree().addListener(SWT.MouseHover, tableListener);

		return treeViewer;
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				ProfilesView.this.fillContextMenu(manager, firstElement);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager, Object element)
	{
		if (element instanceof Profile)
		{
			Profile p = ((Profile) element);

			manager.add(actionMakeCurrent);
			manager.add(new Separator());

			manager.add(actionAdd);
			manager.add(actionAddCurrentFile);

			if (p.isDynamic() == false)
			{
				manager.add(actionDelete);
			}
			else
			{
				manager.add(actionMakeStatic);
			}
		}
		else if (element instanceof ProfileURI && element instanceof TransientProfileURI == false)
		{
			ProfileURI path = ((ProfileURI) element);

			manager.add(actionAdd);

			if (path.getParent().isDynamic() == false)
			{
				manager.add(actionDelete);
			}

			manager.add(actionMoveUp);
			manager.add(actionMoveDown);
		}

		manager.add(new Separator());
		manager.add(actionRefresh);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(actionRefresh);
		manager.add(actionMakeCurrent);
		manager.add(actionAdd);
		manager.add(actionAddProfile);
		manager.add(actionDelete);
		manager.add(actionMoveUp);
		manager.add(actionMoveDown);
		manager.add(actionLinkWithEditor);
	}

	private void makeActions()
	{
		createLinkWithEditorAction();
		createMakeCurrentAction();
		createRefreshAction();
		createMakeStaticAction();
		createAddCurrentFileAction();
		createAddProfileAction();
		createAddAction();
		createDeleteAction();
		createMoveUpAction();
		createMoveDownAction();
		createDoubleClickAction();
	}

	private void createDoubleClickAction()
	{
		actionDoubleClick = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof Profile)
				{
					String data = ((Profile) firstElement).getURI();

					if (data == null || data.equals(ProfileManager.DEFAULT_PROFILE_PATH)
							|| (data.startsWith(staticProtocol) && data.indexOf(".") == -1)) //$NON-NLS-1$
					{
						toggleElementState(firstElement);
						return;
					}
					else
					{
						if (data.startsWith(staticProtocol))
						{
							data = data.substring(staticProtocol.length());
						}

						File f = FileUtils.openURL(data);

						if (f != null && f.exists())
						{
							WorkbenchHelper.openFile(f, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						}
						else
						{
							showMessage(Messages.ProfilesView_FileNoLongerAvailable);
						}

						return;
					}
				}
				else if (firstElement instanceof ProfileURI)
				{
					ProfileURI uri = (ProfileURI) firstElement;
					File f = FileUtils.openURL(uri.getURI());

					if (f != null && f.exists())
					{
						WorkbenchHelper.openFile(f, PlatformUI.getWorkbench().getActiveWorkbenchWindow());

						ProfilesViewEvent e = new ProfilesViewEvent(ProfilesViewEventTypes.OPEN);
						e.setProfile(uri.getParent());

						if (isLinked)
						{
							setCurrentProfile(uri.getParent().getURI());
						}
						// fireProfilesViewEvent(e);

					}
					else
					{
						showMessage(Messages.ProfilesView_FileNoLongerAvailable);
					}
				}
			}

		};
	}

	private void createMoveDownAction()
	{
		actionMoveDown = new Action()
		{
			public void run()
			{
				moveFilesDown(viewer.getSelection());
			}
		};
		actionMoveDown.setText(Messages.ProfilesView_MoveDown);
		actionMoveDown.setToolTipText(Messages.ProfilesView_MoveDown);
		actionMoveDown.setImageDescriptor(fDownIconDescriptor);
		actionMoveDown.setEnabled(false);
	}

	private void createMoveUpAction()
	{
		actionMoveUp = new Action()
		{
			public void run()
			{
				moveFilesUp(viewer.getSelection());
			}
		};
		actionMoveUp.setText(Messages.ProfilesView_MoveUp);
		actionMoveUp.setToolTipText(Messages.ProfilesView_MoveUp);
		actionMoveUp.setImageDescriptor(fUpIconDescriptor);
		actionMoveUp.setEnabled(false);
	}

	private void createDeleteAction()
	{
		actionDelete = new Action()
		{
			public void run()
			{
				removeFiles(viewer.getSelection());
			}
		};
		actionDelete.setText(Messages.ProfilesView_RemoveFile);
		actionDelete.setToolTipText(Messages.ProfilesView_RemoveFile);
		actionDelete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
	}

	private void createAddAction()
	{
		actionAdd = new Action()
		{
			public void run()
			{

				ISelection selection = viewer.getSelection();

				if (selection != null)
				{
					Object element = ((IStructuredSelection) selection).getFirstElement();

					if (element != null)
					{
						Profile profile = null;

						if (element instanceof Profile)
						{
							profile = (Profile) element;
						}
						else if (element instanceof ProfileURI)
						{
							profile = ((ProfileURI) element).getParent();
						}
						else
						{
							return;
						}

						FileDialog fileDialog = new FileDialog(viewer.getControl().getShell(), SWT.MULTI);
						fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
						fileDialog.setFilterNames(FILTER_NAMES);
						String text = fileDialog.open();
						if (text != null)
						{
							IPath basePath = new Path(fileDialog.getFilterPath());
							String[] fileNames = fileDialog.getFileNames();
							String[] uris = new String[fileNames.length];

							for (int i = 0; i < uris.length; i++)
							{
								uris[i] = CoreUIUtils.getURI(basePath.append(fileNames[i]));
							}
							profile.addURIs(uris);
						}
					}
				}

			}
		};

		actionAdd.setText(Messages.ProfilesView_AddFilesDotDotDot);
		actionAdd.setToolTipText(Messages.ProfilesView_AddFilesToProfile);
		actionAdd.setImageDescriptor(fAddFileIconDescriptor);
	}

	private void createAddProfileAction()
	{
		actionAddProfile = new Action()
		{
			public void run()
			{
				InputDialog input = new InputDialog(getSite().getShell(), Messages.ProfilesView_NewProfileName,
						Messages.ProfilesView_EnterNewProfileName, StringUtils.EMPTY, null);

				if (input.open() == Window.OK && input.getValue().length() > 0)
				{
					String name = input.getValue();
					if (name.trim().length() > 0)
					{
						addProfile(new Profile(name, staticProtocol + name));
						// ProfilesViewEvent e = new
						// ProfilesViewEvent(ProfilesViewEventTypes.ADD_PROFILE);
						// e.setProfile(new Profile(name, staticProtocol +
						// name)); //$NON-NLS-1$
						// fireProfilesViewEvent(e);
					}
				}
			}
		};
		actionAddProfile.setText(Messages.ProfilesView_AddProfileDotDotDot);
		actionAddProfile.setToolTipText(Messages.ProfilesView_AddProfile);
		actionAddProfile.setImageDescriptor(fAddProfileIconDescriptor);
	}

	private void createAddCurrentFileAction()
	{
		actionAddCurrentFile = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof Profile)
				{
					handleAddCurrentFileAction((Profile) firstElement);
				}
			}
		};
		actionAddCurrentFile.setText(Messages.ProfilesView_AddCurrentFile);
		actionAddCurrentFile.setToolTipText(Messages.ProfilesView_AddFileToProfile);
	}

	private void handleAddCurrentFileAction(Profile profile)
	{

		String uri = CoreUIUtils.getActiveEditorURI();
		String path = CoreUIUtils.getPathFromURI(uri);

		if (uri == null)
		{
			showMessage(Messages.ProfilesView_NoOpenEditor);
			return;
		}

		if (new File(path).exists() == false)
		{
			showMessage(Messages.ProfilesView_SaveBeforeAdd);
			return;
		}

		if (uri.toLowerCase().indexOf(Messages.ProfilesView_JsExtension) == -1)
		{
			showMessage(Messages.ProfilesView_UnsupportedFileType);
			return;
		}

		profile.addURIs(new String[] { uri });
	}

	private void createMakeStaticAction()
	{
		actionMakeStatic = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof Profile)
				{
					Profile profile = (Profile) firstElement;
					String profileName = profile.getName();
					String path = profile.getURI();

					boolean wasSelected = false;

					if (path == getCurrentProfile().getURI())
					{
						wasSelected = true;
					}

					String newPath = staticProtocol + path;

					String[] uris = profile.getURIsAsStrings();

					deleteProfile(path);

					if (profileName.indexOf(titleLabel) != -1)
					{
						profileName = profileName.substring(0, profileName.length() - titleLabel.length());
					}

					Profile newProfile = createProfile(profileName, newPath);

					newProfile.addURIs(uris);

					if (wasSelected)
					{
						setCurrentProfile(newPath);
					}
				}
			}
		};
		actionMakeStatic.setText(Messages.ProfilesView_MakeProfilePermanent);
		actionMakeStatic.setToolTipText(Messages.ProfilesView_MakeThisProfilePermanent);
	}

	private void createRefreshAction()
	{
		actionRefresh = new Action()
		{
			public void run()
			{
				profileManager.refreshEnvironment();
			}
		};
		actionRefresh.setText(Messages.ProfilesView_RefreshEnvironment);
		actionRefresh.setToolTipText(Messages.ProfilesView_RefreshEnvironment);
		actionRefresh.setImageDescriptor(fRefreshIconDescriptor);
	}

	private void createLinkWithEditorAction()
	{
		actionLinkWithEditor = new CheckboxAction()
		{
			public void run()
			{
				if (isChecked())
				{
					isLinked = true;
					// Set current profile to default regardless of open editor
					setCurrentProfile(defaultPath);
					checkForOpenEditor();
				}
				else
				{
					isLinked = false;
					checkForOpenEditor();
				}
			}
		};
		actionLinkWithEditor.setText(Messages.ProfilesView_LinkWithEditor);
		actionLinkWithEditor.setToolTipText(Messages.ProfilesView_LinkWithEditor);
		actionLinkWithEditor.setImageDescriptor(fLinkWithEditorIconDescriptor);
		actionLinkWithEditor.setChecked(true); // TODO: make this a pref
	}

	private void initStaticProfiles()
	{
		Profile[] profiles = getProfiles();

		for (int i = 0; i < profiles.length; i++)
		{
			String path = profiles[i].getURI();

			if (path.indexOf(staticProtocol) == 0)
			{
				setCurrentProfile(path);
				expandProfile(path);
			}
		}
	}

	private void checkForOpenEditor()
	{
		IEditorPart activeEditor = CoreUIUtils.getActiveEditor();
		String uri = CoreUIUtils.getURI(activeEditor);
		if ("".equals(uri)) //$NON-NLS-1$
		{
			return;
		}

		String path = CoreUIUtils.getPathFromURI(uri);

		if (path == null)
		{
			return;
		}
		if (new File(path).exists() == false)
		{
			return;
		}

		_partListener.partActivated(activeEditor);
	}

	private void createMakeCurrentAction()
	{
		actionMakeCurrent = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof Profile)
				{
					actionLinkWithEditor.setChecked(false);
					actionLinkWithEditor.run();

					setCurrentProfile(((Profile) firstElement).getURI());

				}
			}
		};
		actionMakeCurrent.setText(Messages.ProfilesView_MakeCurrentProfile);
		actionMakeCurrent.setImageDescriptor(fMakeCurrentProfileIconDescriptor);
		actionMakeCurrent.setToolTipText(Messages.ProfilesView_MakeProfileCurrentProfile);
		actionMakeCurrent.setEnabled(false);
	}

	private void toggleElementState(Object element)
	{
		boolean state = viewer.getExpandedState(element);
		if (state)
		{
			viewer.setExpandedState(element, false);
		}
		else
		{
			viewer.setExpandedState(element, true);
		}
	}

	/**
	 * Removes one or more files
	 * 
	 * @param selection
	 *            The currently selected files
	 */
	private void removeFiles(ISelection selection)
	{
		if (!(selection instanceof StructuredSelection))
		{
			return;
		}

		Object o = ((StructuredSelection) selection).getFirstElement();

		if (o instanceof TransientProfileURI)
		{
			// if(o instanceof TransientProfilePath)
			showMessage(Messages.ProfilesView_CannotDeleteAutomaticFile);
			// else
			// showMessage("Cannot delete an automatic-mode file, close the file
			// instead.");
			return;
		}

		if (o instanceof Profile && ((Profile) o).getURI().equals(ProfileManager.DEFAULT_PROFILE_PATH))
		{
			showMessage(Messages.ProfilesView_CannotDeleteDefaultProfile);
			return;
		}

		if (o instanceof Profile) // removes the profile from code assist
		{
			Profile p = (Profile) o;

			String profilePath = p.getURI();

			if (profilePath != null)
			{
				// this is probably the profile being passed in... will check
				// after refactoring
				Profile profile = UnifiedEditorsPlugin.getDefault().getProfileManager().getProfile(profilePath);
				Profile currentProfile = UnifiedEditorsPlugin.getDefault().getProfileManager().getCurrentProfile();

				if (profile != null)
				{
					// if we're deleting the profile we're on, reset to default
					if (currentProfile.getURI() == profile.getURI())
					{
						UnifiedEditorsPlugin.getDefault().getProfileManager().setCurrentProfile(defaultPath);
					}

					profile.removeTransientURIs(new String[] { profilePath });

					deleteProfile(profilePath);

				}
			}

			// ProfilesViewEvent e = new
			// ProfilesViewEvent(ProfilesViewEventTypes.DELETE_PROFILE);
			// e.setProfile((Profile) o);
			// fireProfilesViewEvent(e);
		}
		else if (o instanceof ProfileURI) // removes a file from code assist
		{
			ProfileURI[] uris = convertSelectionToProfileURIs((StructuredSelection) selection);

			if (uris.length > 0)
			{
				for (int i = 0; i < uris.length; i++)
				{
					Profile profile = uris[i].getParent();

					if (profile != null)
					{
						ProfileURI[] files = new ProfileURI[] { uris[i] };
						profile.removeURIs(files);
					}
				}
				// ProfilesViewEvent e = new
				// ProfilesViewEvent(ProfilesViewEventTypes.DELETE);
				// e.setURIs(uris);
				// fireProfilesViewEvent(e);
			}
		}
	}

	private void deleteProfile(String profilePath)
	{
		UnifiedEditorsPlugin.getDefault().getProfileManager().removeProfile(profilePath);
	}

	/**
	 * Moves one or more files up in the list
	 * 
	 * @param selection
	 *            The currently selected files
	 */
	private void moveFilesUp(ISelection selection)
	{
		if (!(selection instanceof StructuredSelection))
		{
			return;
		}

		Object o = ((StructuredSelection) selection).getFirstElement();

		if (o instanceof TransientProfileURI)
		{
			showMessage(Messages.ProfilesView_CannotMoveAutoFile);
			return;
		}

		ProfileURI[] uris = convertSelectionToProfileURIs((StructuredSelection) selection);

		if (uris.length > 0)
		{
			for (int i = 0; i < uris.length; i++)
			{
				Profile profile = uris[i].getParent();
				if (profile != null)
				{
					ProfileURI[] files = new ProfileURI[] { uris[i] };
					profile.moveURIsUp(files);
				}
			}

			// ProfilesViewEvent e = new
			// ProfilesViewEvent(ProfilesViewEventTypes.MOVE_UP);
			// e.setURIs(uris);
			// fireProfilesViewEvent(e);
		}
	}

	/**
	 * Moves one or more files down in the list
	 * 
	 * @param selection
	 *            The currently selected files
	 */
	private void moveFilesDown(ISelection selection)
	{
		if (!(selection instanceof StructuredSelection))
		{
			return;
		}

		Object o = ((StructuredSelection) selection).getFirstElement();

		if (o instanceof TransientProfileURI)
		{
			showMessage(Messages.ProfilesView_CannotMoveAutoFile);
			return;
		}

		ProfileURI[] uris = convertSelectionToProfileURIs((StructuredSelection) selection);

		if (uris.length > 0)
		{
			Collections.reverse(Arrays.asList(uris));

			for (int i = 0; i < uris.length; i++)
			{
				Profile profile = uris[i].getParent();
				if (profile != null)
				{
					ProfileURI[] files = new ProfileURI[] { uris[i] };
					profile.moveURIsDown(files);
				}
			}
		}
	}

	/**
	 * Converts a selection into a lists of paths
	 * 
	 * @param selection
	 *            The paths of the selection
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	public ProfileURI[] convertSelectionToProfileURIs(StructuredSelection selection)
	{
		ArrayList<ProfileURI> paths = new ArrayList<ProfileURI>();

		for (Iterator iter = selection.iterator(); iter.hasNext();)
		{
			Object o = iter.next();

			if (o instanceof ProfileURI)
			{
				ProfileURI uri = (ProfileURI) o;
				paths.add(uri);
			}
			else
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
						Messages.ProfilesView_PathNotProfileURI, o.getClass().toString()));
			}
		}
		return paths.toArray(new ProfileURI[0]);
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(viewer.getControl().getShell(), Messages.ProfilesView_FileExplorer, message);
	}

	private boolean showConfirmMessage(String message)
	{
		return MessageDialog.openConfirm(viewer.getControl().getShell(), Messages.ProfilesView_FileExplorer, message);
	}

	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				actionDoubleClick.run();
			}
		});
	}

	private void hookKeyActions(Control control)
	{
		control.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.character == SWT.DEL)
				{
					removeFiles(viewer.getSelection());
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image descriptor registry. If the image
	 * descriptor cannot be retrieved, attempt to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor assocated with resource or the default "missing" image descriptor if one could not
	 *         be found
	 */
	private static ImageDescriptor getImageDescriptor(String imageFilePath)
	{
		ImageDescriptor imageDescriptor = UnifiedEditorsPlugin.getImageDescriptor(imageFilePath);

		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * 
	 * 
	 */
	public void expandAll()
	{
		this.viewer.expandAll();
	}

	/**
	 * @param profilePath
	 */
	public void expandProfile(String profilePath)
	{

		if (viewer == null || viewer.getTree().isDisposed())
		{
			return;
		}

		TreeItem[] treeItems = viewer.getTree().getItems();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object o = treeItems[i].getData();

			if (o instanceof Profile)
			{
				Profile p = (Profile) o;
				String path = p.getURI();
				if (path.equals(profilePath))
				{
					viewer.setExpandedState(o, true);
					return;
				}
			}
		}

	}

	/**
	 * getLinkWithEditorState
	 * 
	 * @return boolean
	 */
	public boolean getLinkWithEditorState()
	{
		return this.actionLinkWithEditor.isChecked();
	}

	/**
	 * CheckboxAction
	 * 
	 * @author Paul Colton
	 */
	protected class CheckboxAction extends Action
	{
		/**
		 * CheckboxAction
		 */
		public CheckboxAction()
		{
			super(Messages.ProfilesView_Sort, Action.AS_CHECK_BOX);
		}
	}

	/* Below copied from com.aptana.ide.scripting.views.ProfilesView */

	/**
	 * getIsLinked
	 * 
	 * @return boolean
	 */
	public boolean getIsLinked()
	{
		return getLinkWithEditorState();
	}

	/*
	 * duplicated from com.aptana.ide.scripting.views.ProfilesView public void expandAll() { IWorkbenchPart part =
	 * this.getView(); if (part != null) { com.aptana.ide.editors.views.profiles.ProfilesView profileView =
	 * (com.aptana.ide.editors.views.profiles.ProfilesView) part; profileView.expandAll(); } } public void
	 * expandProfile(String profilePath) { IWorkbenchPart part = this.getView(); if (part != null) {
	 * com.aptana.ide.editors.views.profiles.ProfilesView profileView =
	 * (com.aptana.ide.editors.views.profiles.ProfilesView) part; profileView.expandProfile(profilePath); } } end
	 * duplicated
	 */

	/**
	 * addProfile
	 * 
	 * @param profile
	 */
	public void addProfile(Object profile)
	{
		if (profile instanceof Profile)
		{
			UnifiedEditorsPlugin.getDefault().getProfileManager().addProfile((Profile) profile);
		}
	}

	/**
	 * createProfile
	 * 
	 * @param name
	 * @param path
	 * @return Profile
	 */
	public Profile createProfile(String name, String path)
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().createProfile(name, path, false);
	}

	/**
	 * createDynamicProfile
	 * 
	 * @param name
	 * @param path
	 * @return Profile
	 */
	public Profile createDynamicProfile(String name, String path)
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().createProfile(name, path, true);
	}

	/**
	 * removeProfile
	 * 
	 * @param name
	 */
	public void removeProfile(String name)
	{
		UnifiedEditorsPlugin.getDefault().getProfileManager().removeProfile(name);
	}

	/**
	 * setCurrentProfile
	 * 
	 * @param name
	 */
	public void setCurrentProfile(String name)
	{
		UnifiedEditorsPlugin.getDefault().getProfileManager().setCurrentProfile(name);
	}

	/**
	 * getCurrentProfile
	 * 
	 * @return String
	 */
	public Profile getCurrentProfile()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getCurrentProfile();
	}

	/**
	 * getProfiles
	 * 
	 * @return String[]
	 */
	public String[] getProfilePaths()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getProfilePaths();
	}

	/**
	 * getProfiles Returns all profiles
	 * 
	 * @return Profile[]
	 */
	public Profile[] getProfiles()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getProfiles();
	}

	/**
	 * Update the view based on some external preferences
	 * 
	 * @param event
	 */
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event)
	{
		// if we've selected a new set of environments in the preference page, update the checked list as well
		if (event != null
				&& (event.getProperty().equals(IPreferenceConstants.LOADED_ENVIRONMENTS) || event.getProperty().equals(
						IPreferenceConstants.DISABLED_ENVIRONMENTS)))
		{
			checkLoadedEnvironments();
		}
	}

	/* END COPY */
}
