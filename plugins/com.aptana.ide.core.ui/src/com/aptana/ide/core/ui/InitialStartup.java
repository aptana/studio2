/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.db.EventInfo;
import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * @author Ingo Muschenetz
 */
@SuppressWarnings("restriction")
public class InitialStartup
{
	private static final String EDITORAREA_CUSTOM_PAINT = "editorarea.custom_paint"; //$NON-NLS-1$

	/**
	 * perpListener
	 */
	private static IPerspectiveListener perpListener;

	private static Image editorAreaImage;
	private static Image editorAreaImage_studio;
	private static Image editorAreaImage_radrails;

	private static PaintListener paintListener = new PaintListener()
	{
		public void paintControl(PaintEvent e)
		{
			Rectangle rect = ((Composite) e.widget).getClientArea();
			Rectangle imageSize = editorAreaImage.getBounds();
			int drawWidth;
			int drawHeight;
			if (rect.width > imageSize.width && rect.height > imageSize.height)
			{
				drawWidth = imageSize.width;
				drawHeight = imageSize.height;
			}
			else if (rect.width * imageSize.height > rect.height * imageSize.width)
			{
				drawHeight = rect.height;
				drawWidth = (drawHeight * imageSize.width) / imageSize.height;
			}
			else
			{
				drawWidth = rect.width;
				drawHeight = (drawWidth * imageSize.height) / imageSize.width;
			}
			e.gc.drawImage(editorAreaImage, 0, 0, imageSize.width, imageSize.height, (rect.width - drawWidth) / 2,
					(rect.height - drawHeight) / 2, drawWidth, drawHeight);
		}
	};

	/**
	 * windows
	 */
	private static Map<IWorkbenchWindow, IPerspectiveListener> windows = new HashMap<IWorkbenchWindow, IPerspectiveListener>();

	public static void start()
	{
		ImageDescriptor imageDescriptor = CoreUIPlugin.getImageDescriptor("icons/editorarea.gif"); //$NON-NLS-1$
		if (imageDescriptor != null)
		{
			editorAreaImage_studio = imageDescriptor.createImage();
			editorAreaImage = editorAreaImage_studio;
		}

		ImageDescriptor imageDescriptor_radrails = CoreUIPlugin.getImageDescriptor("icons/editorarea_radrails.gif"); //$NON-NLS-1$
		if (imageDescriptor_radrails != null)
		{
			editorAreaImage_radrails = imageDescriptor_radrails.createImage();
		}

		IPreferenceStore prefs = CoreUIPlugin.getDefault().getPreferenceStore();
		boolean hasRunFirstStartup = prefs.getBoolean(IPreferenceConstants.PREF_KEY_FIRST_STARTUP);
		if (!hasRunFirstStartup)
		{
			initForFirstTimeStartup();
			prefs.setValue(IPreferenceConstants.PREF_KEY_FIRST_STARTUP, true);
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();

		perpListener = new IPerspectiveListener()
		{
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
			{
				recordPerspectiveActivation(perspective);
				checkPerspective(page, perspective);
				setEditorAreaPaintListener(page);
			}

			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
			{
			}
		};

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow w = workbench.getActiveWorkbenchWindow();
				addListenerToWindow(w);
			}
		});

		addWindowListener(workbench);

		final IPartListener _partListener = createPartActivationListener();

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
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

	protected static void recordPerspectiveActivation(IPerspectiveDescriptor perspective)
	{
		String keyName = "perspective.activated";
		EventInfo[] events = EventLogger.getInstance().getEvents(keyName);
		String perspectiveId = perspective.getId();
		if (events != null && events.length > 0)
		{
			for (EventInfo event : events)
			{
				String msg = event.getMessage();
				if (msg != null && msg.equals(perspectiveId))
					return;
			}
		}
		EventLogger.getInstance().logEvent(keyName, perspectiveId);
	}

	/**
	 * Creates a new window listener to the workbench, and adds a new perspective listener on window activation (if not
	 * already added, and removes it on deactivation
	 * 
	 * @param workbench
	 */
	private static void addWindowListener(final IWorkbench workbench)
	{
		workbench.addWindowListener(new IWindowListener()
		{
			public void windowActivated(IWorkbenchWindow window)
			{
			}

			public void windowDeactivated(IWorkbenchWindow window)
			{
			}

			public void windowClosed(IWorkbenchWindow window)
			{
				window.removePerspectiveListener(perpListener);

				if (windows.containsKey(window))
				{
					windows.remove(window);
				}
			}

			public void windowOpened(IWorkbenchWindow window)
			{
				if (!windows.containsKey(window))
				{
					addListenerToWindow(window);
					windows.put(window, perpListener);
				}
			}
		});
	}

	/**
	 * createPartActivationListener
	 * 
	 * @return IPartListener
	 */
	private static IPartListener createPartActivationListener()
	{
		final IPartListener _partListener = new IPartListener()
		{
			public void partActivated(IWorkbenchPart part)
			{
				try
				{
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (part.getSite().getPart() instanceof CommonNavigator)
					{
						if (page instanceof WorkbenchPage)
						{
							WorkbenchPage wp = (WorkbenchPage) page;
							Perspective persp = wp.getActivePerspective();

							if (WebPerspectiveFactory.isSameOrDescendantPerspective(persp.getDesc()))
							{
								// Remove the Untitled file wizards, add the project-based new file wizards
								String[] existing = persp.getNewWizardShortcuts();
								List<String> fileWizards = WebPerspectiveFactory.getFileWizardShortcuts();
								ArrayList<String> ids = new ArrayList<String>();
								for (int i = 0; i < existing.length; i++)
								{
									if (fileWizards.contains(existing[i]))
										continue;
									ids.add(existing[i]);
								}
								List<String> projectWizards = WebPerspectiveFactory.getProjectWizardShortcuts();
								for (String id : projectWizards)
								{
									if (!ids.contains(id))
										ids.add(id);
								}
								persp.setNewWizardActionIds(ids);
							}
						}
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(),
							Messages.InitialStartup_UnableToSwitchNewFileWizardListing, ex);
				}
			}

			public void partDeactivated(IWorkbenchPart part)
			{
				try
				{
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (part.getSite().getPart() instanceof CommonNavigator)
					{
						WorkbenchPage wp = (WorkbenchPage) page;
						Perspective persp = wp.getActivePerspective();
						if (WebPerspectiveFactory.isSameOrDescendantPerspective(persp.getDesc()))
						{
							// Remove the project-based new file wizards, add Untitled file wizards
							String[] existing = persp.getNewWizardShortcuts();
							List<String> projectWizards = WebPerspectiveFactory.getProjectWizardShortcuts();
							ArrayList<String> ids = new ArrayList<String>();
							for (int i = 0; i < existing.length; i++)
							{
								if (projectWizards.contains(existing[i]))
									continue;
								ids.add(existing[i]);
							}
							List<String> fileWizards = WebPerspectiveFactory.getFileWizardShortcuts();
							for (String id : fileWizards)
							{
								if (!ids.contains(id))
									ids.add(id);
							}

							persp.setNewWizardActionIds(ids);
						}
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(),
							Messages.InitialStartup_UnableToSwitchNewFileWizardListing, ex);
				}
			}

			public void partBroughtToTop(IWorkbenchPart part)
			{
			}

			public void partOpened(IWorkbenchPart part)
			{
			}

			public void partClosed(IWorkbenchPart part)
			{
			}
		};
		return _partListener;
	}

	/**
	 * Adds the perspective listener to the current window
	 * 
	 * @param w
	 */
	private static void addListenerToWindow(final IWorkbenchWindow w)
	{
		if (w == null)
		{
			return;
		}

		IWorkbenchPage page = w.getActivePage();

		if (page != null)
		{
			checkPerspective(page, page.getPerspective());
			w.addPerspectiveListener(perpListener);
			w.getPartService().addPartListener(new IPartListener()
			{
				public void partActivated(IWorkbenchPart part)
				{
				}

				public void partBroughtToTop(IWorkbenchPart part)
				{
				}

				public void partClosed(IWorkbenchPart part)
				{
					IWorkbenchPage page = w.getActivePage();
					if (part instanceof IEditorPart && page != null && page.getEditorReferences().length == 0)
					{
						setEditorAreaPaintListener(page);
					}
				}

				public void partDeactivated(IWorkbenchPart part)
				{
				}

				public void partOpened(IWorkbenchPart part)
				{
				}

			});
			setEditorAreaPaintListener(page);
		}
	}

	/**
	 * Checks to see if we are in the web perspective
	 * 
	 * @param page
	 * @param perspective
	 */
	private static boolean checkPerspective(IWorkbenchPage page, IPerspectiveDescriptor perspective)
	{
		if (WebPerspectiveFactory.isSameOrDescendantPerspective(perspective))
		{
			onWebPerspectiveActivated(page);
			return true;
		}

		return false;
	}

	/**
	 * Run whenever the web perspective is activated
	 * 
	 * @param page
	 * @param listener
	 */
	private static void onWebPerspectiveActivated(IWorkbenchPage page)
	{
		IPreferenceStore prefs = CoreUIPlugin.getDefault().getPreferenceStore();
		int lastWorkspace = prefs.getInt(IPreferenceConstants.WEB_PERSPECTIVE_LAST_VERSION);

		if (WebPerspectiveFactory.VERSION > lastWorkspace)
		{
			prefs.setValue(IPreferenceConstants.WEB_PERSPECTIVE_LAST_VERSION, WebPerspectiveFactory.VERSION);

			prefs.setValue(IPreferenceConstants.WEB_PERSPECTIVE_RESET_PERSPECTIVE, false);
			WebPerspectiveFactory.resetPerspective(page);
		}
	}

	/**
	 * Runs the first time startup logic for this workspace.
	 */
	private static void initForFirstTimeStartup()
	{
		IPreferenceStore prefs = CoreUIPlugin.getDefault().getPreferenceStore();

		// Set here so that a new user does not immediately get a "perspective has changed" warning
		prefs.setValue(IPreferenceConstants.WEB_PERSPECTIVE_LAST_VERSION, WebPerspectiveFactory.VERSION);
	}

	private static void setEditorAreaPaintListener(IWorkbenchPage page)
	{
		if (editorAreaImage_studio == null && editorAreaImage_radrails == null)
		{
			return;
		}
		ViewForm control = null;
		EditorAreaHelper editorAreaHelper = ((WorkbenchPage) page).getEditorPresentation();
		if (editorAreaHelper == null)
		{
			return;
		}
		LayoutPart layoutPart = editorAreaHelper.getWorkbookFromID("DefaultEditorWorkbook");
		if (layoutPart == null)
		{
			return;
		}
		Control[] list = ((Composite) layoutPart.getControl()).getChildren(); //$NON-NLS-1$
		for (int i = 0; i < list.length; ++i)
		{
			if (list[i] instanceof ViewForm)
			{
				control = (ViewForm) list[i];
				break;
			}
		}
		if (control == null)
		{
			return;
		}
		IPerspectiveDescriptor perspective = page.getPerspective();
		if (WebPerspectiveFactory.isSameOrDescendantPerspective(perspective)
				|| perspective.getId().equals(WebPerspectiveFactory.RAILS_PERSPECTIVE_ID)
				|| perspective.getId().equals(WebPerspectiveFactory.RUBY_PERSPECTIVE_ID))
		{
			if (perspective.getId().equals(WebPerspectiveFactory.RAILS_PERSPECTIVE_ID)
					|| perspective.getId().equals(WebPerspectiveFactory.RUBY_PERSPECTIVE_ID))
			{
				editorAreaImage = editorAreaImage_radrails;
			}
			else if (WebPerspectiveFactory.isSameOrDescendantPerspective(perspective))
			{
				editorAreaImage = editorAreaImage_studio;
			}
			if (control.getData(EDITORAREA_CUSTOM_PAINT) != null)
			{
				return;
			}
			control.addPaintListener(paintListener);
			control.setData(EDITORAREA_CUSTOM_PAINT, Boolean.TRUE);
			control.redraw();
		}
		else
		{
			if (control.getData(EDITORAREA_CUSTOM_PAINT) == null)
			{
				return;
			}
			control.removePaintListener(paintListener);
			control.setData(EDITORAREA_CUSTOM_PAINT, null);
			control.redraw();
		}
	}

}
