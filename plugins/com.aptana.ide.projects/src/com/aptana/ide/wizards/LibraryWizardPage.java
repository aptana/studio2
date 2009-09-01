/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.wizards.BaseWizard;
import com.aptana.ide.core.ui.wizards.IBaseWizardPage;
import com.aptana.ide.projects.ProjectsPlugin;
import com.aptana.ide.installer.Activator;
import com.aptana.ide.librarymanager.LibraryInfo;
import com.aptana.ide.librarymanager.LibraryManager;

/**
 * Standard project reference page for a wizard that creates a project resource.
 * <p>
 * This page may be used by clients as-is; it may be also be subclassed to suit.
 * </p>
 * <p>
 * Example useage:
 * 
 * <pre>
 * referencePage = new WizardNewProjectReferencePage(&quot;basicReferenceProjectPage&quot;);
 * referencePage.setTitle(&quot;Project&quot;);
 * referencePage.setDescription(&quot;Select referenced projects.&quot;);
 * </pre>
 * 
 * </p>
 */
public class LibraryWizardPage extends WizardPage implements IBaseWizardPage
{
	// widgets
	private CheckboxTableViewer referenceProjectsViewer;

	private static final String JS_LIBS_TITLE = Messages.LibraryWizardPage_SelectAJAXLibraries;
	private static final String INSTALL_JS_LIBS_LABEL = Messages.LibraryWizardPage_InstallJavascriptLibraries;

	private static final int LIB_LIST_MULTIPLIER = 15;

	private IStructuredContentProvider _contentProvider;
	private ILabelProvider _labelProvider;

	/**
	 * Creates a new project reference wizard page.
	 * 
	 * @param pageName
	 *            the name of this page
	 * @param contentProvider
	 * @param labelProvider
	 */
	public LibraryWizardPage(String pageName, IStructuredContentProvider contentProvider, ILabelProvider labelProvider)
	{
		super(pageName);
		_contentProvider = contentProvider;
		_labelProvider = labelProvider;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{

		Font font = parent.getFont();

		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(font);

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IIDEHelpContextIds.NEW_PROJECT_REFERENCE_WIZARD_PAGE);
		
		LibraryInfo[] libraryInfoExtensions = LibraryManager.getInstance().getLibraryInfoExtensions();
		if (libraryInfoExtensions.length == 0) {
			Browser browser = new Browser(composite, SWT.BORDER);
	        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        browser.setFont(font);
	        Bundle bundle = Platform.getBundle(ProjectsPlugin.PLUGIN_ID);
	        URL url = null;
	        try {
	            url = FileLocator.toFileURL(bundle.getEntry("html/librarywizardpage.html")); //$NON-NLS-1$
	            browser.setUrl(url.toString());
	        } catch (IOException e) {
	        	IdeLog.logInfo(ProjectsPlugin.getDefault(), e.getMessage(), e);
	        }
		} else {
			Label referenceLabel = new Label(composite, SWT.NONE);
			referenceLabel.setText(JS_LIBS_TITLE);
			referenceLabel.setFont(font);
	
			referenceProjectsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
			referenceProjectsViewer.getTable().setFont(composite.getFont());
			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
	
			data.heightHint = getDefaultFontHeight(referenceProjectsViewer.getTable(), LIB_LIST_MULTIPLIER);
			referenceProjectsViewer.getTable().setLayoutData(data);
			referenceProjectsViewer.setLabelProvider(new LabelProvider());
			referenceProjectsViewer.setContentProvider(_contentProvider);
			referenceProjectsViewer.setLabelProvider(_labelProvider);
			referenceProjectsViewer.setInput(ResourcesPlugin.getWorkspace());
		}
		Button installLibrariesButton = new Button(composite, SWT.PUSH);
		installLibrariesButton.setText(INSTALL_JS_LIBS_LABEL);
		installLibrariesButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		installLibrariesButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				// Hide the wizard
				getWizard().performCancel();
				getWizard().getContainer().getShell().close();
				// Show the installer wizard
				Activator.launchWizard(false, new String[] {"installer.ajax"} ); //$NON-NLS-1$
			}
		});
		setControl(composite);
	}

	/**
	 * Get the defualt widget height for the supplied control.
	 * 
	 * @return int
	 * @param control -
	 *            the control being queried about fonts
	 * @param lines -
	 *            the number of lines to be shown on the table.
	 */
	private static int getDefaultFontHeight(Control control, int lines)
	{
		FontData[] viewerFontData = control.getFont().getFontData();
		int fontHeight = 10;

		// If we have no font data use our guess
		if (viewerFontData.length > 0)
		{
			fontHeight = viewerFontData[0].getHeight();
		}
		return lines * fontHeight;

	}

	/**
	 * Returns the referenced projects selected by the user.
	 * 
	 * @return the referenced projects
	 */
	public String[] getSelectedLibraries()
	{
		List<String> libs = new ArrayList<String>();

		if (referenceProjectsViewer != null) {
			Object[] elements = referenceProjectsViewer.getCheckedElements();
			for (int i = 0; i < elements.length; i++)
			{
				LibraryInfo item = (LibraryInfo) elements[i];
				URL resolved = item.getResolvedURL();
				if (resolved != null)
				{
					libs.add(resolved.getFile());
				}
			}
		}

		return libs.toArray(new String[0]);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			setPageComplete(true);
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.wizards.IBaseWizardPage#performFinish()
	 */
	public void performFinish()
	{
		IProgressMonitor monitor = ((BaseWizard) getWizard()).getFinishProgressMonitor();
		IProject p = ((BaseWizard) getWizard()).getCreatedProject();

		String toOpen = null;
		String[] selection = getSelectedLibraries();
		for (int j = 0; j < selection.length; j++)
		{
			String destinationDir = p.getLocation().toOSString();
			String sourceDir = selection[j];

			try
			{
				File f = new File(sourceDir);
				File[] files = f.listFiles();

				if (monitor != null)
				{
					monitor.beginTask(StringUtils.format(Messages.LibraryProjectWizard_CopyingFiles, selection[j]),
							files.length);
				}

				for (int i = 0; i < files.length; i++)
				{
					String name = files[i].getName();

					if (monitor != null)
					{
						monitor.subTask(name);
					}

					FileUtils.copy(sourceDir, destinationDir, name);
					if (toOpen == null && (name.toLowerCase().endsWith(".htm") || name.toLowerCase().endsWith(".html"))) //$NON-NLS-1$ //$NON-NLS-2$
					{
						toOpen = name;
					}

					if (monitor != null)
					{
						monitor.worked(1);
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(ProjectsPlugin.getDefault(),
						Messages.LibraryProjectWizard_UnableToCopyFileToProject, e);
			}

			try
			{
				p.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException e)
			{
				IdeLog.logError(ProjectsPlugin.getDefault(), Messages.LibraryProjectWizard_Error, e);
			}
		}
	}
}
