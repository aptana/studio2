/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editor.css.preview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class CSSPreviewPropertyPage extends PropertyPage
{

	/**
	 * PREVIEW_PREFIX
	 */
	public static final String CSS_PREVIEW_PATH = "CSS_PREVIEW_PATH"; //$NON-NLS-1$

	/**
	 * CSS_PREVIEW_TYPE
	 */
	public static final String CSS_PREVIEW_TYPE = "CSS_PREVIEW_TYPE"; //$NON-NLS-1$

	/**
	 * URL_TYPE
	 */
	public static final String URL_TYPE = "URL_TYPE"; //$NON-NLS-1$

	/**
	 * PROJECT_TYPE
	 */
	public static final String PROJECT_TYPE = "PROJECT_TYPE"; //$NON-NLS-1$

	/**
	 * CSS_PREVIEW_OVERRIDE
	 */
	public static final String CSS_PREVIEW_OVERRIDE = "CSS_PREVIEW_OVERRIDE"; //$NON-NLS-1$

	/**
	 * TRUE
	 */
	public static final String TRUE = "true"; //$NON-NLS-1$

	/**
	 * FALSE
	 */
	public static final String FALSE = "false"; //$NON-NLS-1$

	private IEditorRegistry registry = WorkbenchPlugin.getDefault().getEditorRegistry();
	private Map<IEditorDescriptor, Image> images = new HashMap<IEditorDescriptor, Image>();
	private Table projectFiles;
	private Button override;
	private Group preview;
	private Button useProjectFile;
	private Button useURL;
	private Label urlLabel;
	private Text urlText;
	private Button browseButton;
	private IResource resource;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public CSSPreviewPropertyPage()
	{
		super();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		composite.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(data);

		Composite top = new Composite(composite, SWT.NONE);
		GridLayout topLayout = new GridLayout(2, false);
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		top.setLayout(topLayout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		override = new Button(top, SWT.CHECK);
		override.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		override.setSelection(false);
		override.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				boolean ov = override.getSelection();
				useProjectFile.setEnabled(ov);
				useURL.setEnabled(ov);
				if (ov)
				{
					if (useProjectFile.getSelection())
					{
						projectFiles.setEnabled(true);
					}
					else if (useURL.getSelection())
					{
						urlText.setEnabled(true);
						browseButton.setEnabled(true);
					}
				}
				else
				{
					urlText.setEnabled(false);
					browseButton.setEnabled(false);
					projectFiles.setEnabled(false);
				}
			}

		});

		Link goToParent = new Link(top, SWT.NONE);
		goToParent.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (resource != null)
				{
					if (resource instanceof IProject)
					{
						PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
								.getActiveShell(), "com.aptana.ide.editor.css.preferences.PreviewPreferencePage", //$NON-NLS-1$
								new String[] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
						dialog.open();
					}
					else if (resource instanceof IFile)
					{
						PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(Display.getDefault()
								.getActiveShell(), resource.getProject(),
								"com.aptana.ide.editor.css.preview.cssPreviewPropertyPage", //$NON-NLS-1$
								new String[] { "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" }, null); //$NON-NLS-1$
						dialog.open();
					}
				}
			}

		});
		goToParent.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

		preview = new Group(composite, SWT.NONE);
		preview.setText(Messages.CSSPreviewPropertyPage_PreviewText);
		GridLayout pLayout = new GridLayout(1, true);
		pLayout.marginHeight = 10;
		preview.setLayout(pLayout);
		preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		useProjectFile = new Button(preview, SWT.RADIO);
		useProjectFile.setText(Messages.CSSPreviewPropertyPage_UseProjectFileText);
		useProjectFile.setSelection(true);
		useProjectFile.setEnabled(false);
		useProjectFile.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				projectFiles.setEnabled(true);
				urlText.setEnabled(false);
				browseButton.setEnabled(false);
			}

		});

		Group projectGroup = new Group(preview, SWT.NONE);
		projectGroup.setText(Messages.CSSPreviewPropertyPage_ProjectFilesText);
		GridLayout pgLayout = new GridLayout(1, true);
		pgLayout.marginHeight = 0;
		pgLayout.marginWidth = 0;
		projectGroup.setLayout(pgLayout);
		GridData pgData = new GridData(SWT.FILL, SWT.FILL, true, true);
		pgData.horizontalIndent = 10;
		projectGroup.setLayoutData(pgData);
		projectFiles = new Table(projectGroup, SWT.CHECK | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		projectFiles.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.CHECK)
				{
					TableItem[] items = projectFiles.getItems();
					for (int i = 0; i < items.length; i++)
					{
						TableItem item = items[i];
						if (item != e.item)
						{
							item.setChecked(false);
						}
					}
				}
			}

		});
		projectFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		projectFiles.setLinesVisible(true);
		new TableColumn(projectFiles, SWT.LEFT);
		projectFiles.addControlListener(new ControlAdapter()
		{

			public void controlResized(ControlEvent e)
			{
				TableColumn c = projectFiles.getColumn(0);
				Point size = projectFiles.getSize();

				// Mac fix for always having a vertical scrollbar and not calculating it affects the horizontal scroll
				// bar
				if (Platform.getOS().equals(Platform.OS_MACOSX))
				{
					ScrollBar vScrolls = projectFiles.getVerticalBar();
					if (vScrolls != null)
					{
						size.x = size.x - vScrolls.getSize().x - 5;
					}
				}
				c.setWidth(size.x - 6);
			}

		});
		projectFiles.setEnabled(false);

		useURL = new Button(preview, SWT.RADIO);
		useURL.setText(Messages.CSSPreviewPropertyPage_UseURLText);
		useURL.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				projectFiles.setEnabled(false);
				urlText.setEnabled(true);
				browseButton.setEnabled(true);
			}

		});
		useURL.setSelection(false);
		useURL.setEnabled(false);

		Composite url = new Composite(preview, SWT.NONE);
		GridLayout urlLayout = new GridLayout(3, false);
		urlLayout.marginHeight = 0;
		urlLayout.marginWidth = 0;
		url.setLayout(urlLayout);
		GridData uData = new GridData(SWT.FILL, SWT.FILL, true, false);
		uData.horizontalIndent = 17;
		url.setLayoutData(uData);
		urlLabel = new Label(url, SWT.LEFT);
		urlLabel.setText(Messages.CSSPreviewPropertyPage_URLText);
		urlText = new Text(url, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		urlText.setEnabled(false);
		browseButton = new Button(url, SWT.PUSH);
		browseButton.setText(Messages.CSSPreviewPropertyPage_BrowseText);
		browseButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(browseButton.getShell(), SWT.OPEN);
				String file = dialog.open();
				if (file != null)
				{
					urlText.setText(file);
				}
			}

		});
		browseButton.setEnabled(false);

		try
		{
			resource = (IResource) getElement();
			String override = resource.getPersistentProperty(new QualifiedName("", CSS_PREVIEW_OVERRIDE)); //$NON-NLS-1$
			String previewPath = resource.getPersistentProperty(new QualifiedName("", CSS_PREVIEW_PATH)); //$NON-NLS-1$
			String previewType = resource.getPersistentProperty(new QualifiedName("", CSS_PREVIEW_TYPE)); //$NON-NLS-1$
			IProject project = null;
			if (resource instanceof IProject)
			{
				project = (IProject) resource;
				this.override.setText(Messages.CSSPreviewPropertyPage_OverrideWorkspaceText);
				goToParent.setText("<a>" + Messages.CSSPreviewPropertyPage_OverrideWorkspaceLinkText + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (resource instanceof IFile)
			{
				project = resource.getProject();
				this.override.setText(Messages.CSSPreviewPropertyPage_OverrideProjectText);
				goToParent.setText("<a>" + Messages.CSSPreviewPropertyPage_OverrideProjectLinkText + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (project != null)
			{
				buildTable(project);
			}
			if (TRUE.equals(override))
			{
				this.override.setSelection(true);
				useURL.setEnabled(true);
				useProjectFile.setEnabled(true);
				if (URL_TYPE.equals(previewType))
				{
					useURL.setSelection(true);
					useProjectFile.setSelection(false);
					urlText.setEnabled(true);
					browseButton.setEnabled(true);
					urlText.setText(previewPath);
				}
				else
				{
					useURL.setSelection(false);
					useProjectFile.setSelection(true);
					projectFiles.setEnabled(true);
					if (project != null && previewPath != null)
					{
						IFile[] candidates = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
								new Path(previewPath));
						IResource resource = null;
						for (int i = 0; i < candidates.length; i++)
						{
							if (project.equals(candidates[i].getProject()))
							{
								resource = candidates[i];
								break;
							}
						}
						if (resource != null && resource.exists())
						{
							TableItem[] items = projectFiles.getItems();
							for (int i = 0; i < items.length; i++)
							{
								IResource file = (IResource) items[i].getData();
								if (resource.equals(file))
								{
									items[i].setChecked(true);
									break;
								}
							}
						}
					}
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), "Error getting element", e); //$NON-NLS-1$
		}
		return composite;
	}

	private void buildTable(IResource root)
	{
		if (root instanceof IContainer)
		{
			IResource[] members;
			try
			{
				members = ((IContainer) root).members();
				for (int i = 0; i < members.length; i++)
				{
					if (members[i] instanceof IFile)
					{
						TableItem item = new TableItem(projectFiles, SWT.NONE);
						item.setData(members[i]);
						item.setText(members[i].getProjectRelativePath().toString());
						IEditorDescriptor desc = registry.getDefaultEditor(members[i].getName());
						if (desc == null || desc.getImageDescriptor() == null)
						{
							IWorkbench workbench = PlatformUI.getWorkbench();
							ISharedImages sharedImages = workbench.getSharedImages();
							sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
						}
						else
						{
							Image image = null;
							if (images.containsKey(desc))
							{
								image = images.get(desc);
							}
							else
							{
								image = desc.getImageDescriptor().createImage();
								images.put(desc, image);
							}
							item.setImage(image);
						}
					}
					else
					{
						buildTable(members[i]);
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CSSPlugin.getDefault(), "Error retrieving project files", e); //$NON-NLS-1$
			}

		}
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{

	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		Iterator<Image> iter = images.values().iterator();
		while (iter.hasNext())
		{
			iter.next().dispose();
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		if (this.resource != null)
		{
			if (override.getSelection())
			{
				try
				{
					this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_OVERRIDE), TRUE); //$NON-NLS-1$
				}
				catch (CoreException e)
				{
					IdeLog.logError(CSSPlugin.getDefault(), "Error saving preview preferences", e); //$NON-NLS-1$
				}
				if (useURL.getSelection())
				{
					try
					{
						this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_PATH), urlText.getText()); //$NON-NLS-1$
						this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_TYPE), URL_TYPE); //$NON-NLS-1$
					}
					catch (CoreException e)
					{
						IdeLog.logError(CSSPlugin.getDefault(), "Error saving preview preferences", e); //$NON-NLS-1$
					}

				}
				else if (useProjectFile.getSelection())
				{
					try
					{
						TableItem[] items = projectFiles.getItems();
						for (int i = 0; i < items.length; i++)
						{
							if (items[i].getChecked())
							{
								IResource file = (IResource) items[i].getData();
								this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_PATH), file //$NON-NLS-1$
										.getLocation().makeAbsolute().toOSString());
								this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_TYPE), //$NON-NLS-1$
										PROJECT_TYPE);
								break;
							}
						}
					}
					catch (CoreException e)
					{
						IdeLog.logError(CSSPlugin.getDefault(), "Error saving preview preferences", e); //$NON-NLS-1$
					}
				}
			}
			else
			{
				try
				{
					this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_PATH), null); //$NON-NLS-1$
					this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_TYPE), null); //$NON-NLS-1$
					this.resource.setPersistentProperty(new QualifiedName("", CSS_PREVIEW_OVERRIDE), FALSE); //$NON-NLS-1$
				}
				catch (CoreException e)
				{
					IdeLog.logError(CSSPlugin.getDefault(), "Error saving preview preferences", e); //$NON-NLS-1$
				}
			}
		}
		return true;
	}

}