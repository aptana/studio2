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
package com.aptana.ide.editors.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches a required one.
 */
public class SimpleNewWizardPage extends WizardPage
{
	private Text containerText;

	private Text fileText;

	private ISelection selection;

	private String defaultFileName;

	/**
	 * Constructor for SimpleNewWizardPage.
	 * 
	 * @param selection
	 */
	public SimpleNewWizardPage(ISelection selection)
	{
		super(Messages.SimpleNewWizardPage_WizardPage);
		this.selection = selection;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText(StringUtils.makeFormLabel(Messages.SimpleNewWizardPage_Container));

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
		button.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText(StringUtils.makeFormLabel(Messages.SimpleNewWizardPage_FileName));

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize()
	{
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
			{
				return;
			}
			Object obj = ssel.getFirstElement();
			if (!(obj instanceof IResource) && (obj instanceof IAdaptable))
			{
				IAdaptable adaptable = (IAdaptable) obj;
				Object resource = adaptable.getAdapter(IResource.class);
				if (resource != null) 
				{
					obj = resource;
				}
			}
			if (obj instanceof IResource)
			{
				IContainer container;
				if (obj instanceof IContainer)
				{
					container = (IContainer) obj;
				}
				else
				{
					container = ((IResource) obj).getParent();
				}
				containerText.setText(container.getFullPath().toString());
			}
		}
		if (defaultFileName != null)
		{
			fileText.setText(defaultFileName);
		}
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 */

	private void handleBrowse()
	{
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace()
				.getRoot(), false, Messages.SimpleNewWizardPage_SelectNewFileContainer);
		if (dialog.open() == ContainerSelectionDialog.OK)
		{
			Object[] result = dialog.getResult();
			if (result.length == 1)
			{
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged()
	{
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0)
		{
			updateStatus(Messages.SimpleNewWizardPage_SpecifyFileContainer);
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0)
		{
			updateStatus(Messages.SimpleNewWizardPage_FileContainerMustExist);
			return;
		}
		if (!container.isAccessible())
		{
			updateStatus(Messages.SimpleNewWizardPage_ProjectMustBeWritable);
			return;
		}
		if (fileName.length() == 0)
		{
			updateStatus(Messages.SimpleNewWizardPage_SpecifyFileName);
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0)
		{
			updateStatus(Messages.SimpleNewWizardPage_ValidFileName);
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1)
		{
			String ext = fileName.substring(dotLoc + 1);
			if (requiredFileExtensions.length > 0)
			{
				boolean supportedFileExtension = false;
				for (int i = 0; !supportedFileExtension && i < requiredFileExtensions.length; i++)
				{
					supportedFileExtension = ext.equalsIgnoreCase(requiredFileExtensions[i]);
				}
				if (!supportedFileExtension)
				{
					updateStatus(StringUtils.format(Messages.SimpleNewWizardPage_FileExtensionMustBe, StringUtils.join(",", requiredFileExtensions))); //$NON-NLS-1$
					return;
				}
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * getContainerName
	 * 
	 * @return String
	 */
	public String getContainerName()
	{
		return containerText.getText();
	}

	/**
	 * getFileName
	 * 
	 * @return String
	 */
	public String getFileName()
	{
		return fileText.getText();
	}

	/**
	 * setDefaultFileName
	 * 
	 * @param fileName
	 */
	public void setDefaultFileName(String fileName)
	{
		defaultFileName = fileName;
	}

	/**
	 * setRequiredFileExtensions
	 * 
	 * @param requiredFileExtensions
	 */
	public void setRequiredFileExtensions(String[] requiredFileExtensions)
	{
		this.requiredFileExtensions = requiredFileExtensions;
	}

	private String[] requiredFileExtensions;
}