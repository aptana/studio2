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
package com.aptana.ide.core.ui.views.fileexplorer;

import java.io.ByteArrayInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * Dialog to create a new remote file or folder
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class NewVirtualFileDialog extends SelectionStatusDialog
{

    public static interface Client
    {
        public void fileCreated(IVirtualFile targetFile);
    }

	private IVirtualFile parentFolder;
	private ProgressMonitorPart progressMonitorPart;
	private Composite displayArea;
	private Label fileLabel;
	private Text fileText;
	private boolean isFile;
	private Client client;

	private static IStatus createStatus(final int severity, final String message)
	{
		return new IStatus()
		{

			public boolean matches(int severityMask)
			{
				return severityMask == severity;
			}

			public boolean isOK()
			{
				return false;
			}

			public boolean isMultiStatus()
			{
				return false;
			}

			public int getSeverity()
			{
				return severity;
			}

			public String getPlugin()
			{
				return null;
			}

			public String getMessage()
			{
				return message;
			}

			public Throwable getException()
			{
				return null;
			}

			public int getCode()
			{
				return 0;
			}

			public IStatus[] getChildren()
			{
				return null;
			}

		};
	}

	/**
	 * NewVirualFileDialog constructor
	 * 
	 * @param parentFolder
	 * @param parentShell
	 * @param view
	 * @param isFile
	 */
	public NewVirtualFileDialog(IVirtualFile parentFolder, Shell parentShell, boolean isFile, Client client)
	{
		super(parentShell);
		setStatusLineAboveButtons(true);
		this.parentFolder = parentFolder;
		this.isFile = isFile;
		this.client = client;
		if (isFile)
		{
			setTitle(Messages.NewVirualFileDialog_CreateRemoteFile);
		}
		else
		{
			setTitle(Messages.NewVirualFileDialog_CreateRemoteFolder);
		}
	}

	private void performFinish(int buttonId)
	{
		super.buttonPressed(buttonId);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(final int buttonId)
	{
		if (buttonId == IDialogConstants.OK_ID)
		{
			getOkButton().setEnabled(false);
			String name = fileText.getText();
			progressMonitorPart.beginTask(Messages.NewVirualFileDialog_Creating + name, IProgressMonitor.UNKNOWN);

			IVirtualFile newFile = null;
			if (isFile)
			{
				newFile = parentFolder.getFileManager().createVirtualFile(parentFolder.getAbsolutePath() + "/" + name); //$NON-NLS-1$
			}
			else
			{
				newFile = parentFolder.getFileManager().createVirtualDirectory(
						parentFolder.getAbsolutePath() + "/" + name); //$NON-NLS-1$
			}
			final IVirtualFile file = newFile;
			Job createJob = new Job(Messages.NewVirualFileDialog_CreatingRemoteFileJob)
			{

				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						if (file.exists())
						{
						    CoreUIUtils.getDisplay().asyncExec(new Runnable()
							{

								public void run()
								{
									if (isFile)
									{
										updateStatus(createStatus(IStatus.ERROR, Messages.NewVirualFileDialog_FileAlreadyExists));
									}
									else
									{
										updateStatus(createStatus(IStatus.ERROR, Messages.NewVirualFileDialog_FolderAlreadyExists));
									}
									progressMonitorPart.done();
								}

							});
						}
						else
						{
							if (isFile)
							{
								file.getFileManager().putStream(new ByteArrayInputStream(new byte[0]), file);
							}
							else
							{
								file.getFileManager().createLocalDirectory(file);
							}
							CoreUIUtils.getDisplay().asyncExec(new Runnable()
							{

								public void run()
								{
									updateStatus(createStatus(IStatus.OK, null));
									progressMonitorPart.done();
									NewVirtualFileDialog.this.performFinish(buttonId);
									client.fileCreated(file);
								}

							});
						}
					}
					catch (Exception e)
					{
					    CoreUIUtils.getDisplay().asyncExec(new Runnable()
						{

							public void run()
							{
								if (isFile)
								{
									updateStatus(createStatus(IStatus.ERROR, Messages.NewVirualFileDialog_ErrorCreatingRemoteFile));
								}
								else
								{
									updateStatus(createStatus(IStatus.ERROR, Messages.NewVirualFileDialog_ErrorCreatingRemoteFolder));
								}
							}

						});
					}
					return Status.OK_STATUS;
				}

			};
			createJob.schedule();
		}
		else
		{
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		displayArea = new Composite(composite, SWT.NONE);
		displayArea.setLayout(new GridLayout(2, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 500;
		displayArea.setLayoutData(data);
		fileLabel = new Label(displayArea, SWT.LEFT);
		if (isFile)
		{
			fileLabel.setText(Messages.NewVirualFileDialog_EnterFileName);
		}
		else
		{
			fileLabel.setText(Messages.NewVirualFileDialog_EnterFolderName);
		}
		fileText = new Text(displayArea, SWT.SINGLE | SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fileText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				updateStatus(createStatus(IStatus.OK, null));
			}

		});
		GridLayout pmLayout = new GridLayout();
		progressMonitorPart = new ProgressMonitorPart(composite, pmLayout);
		progressMonitorPart.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	/**
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */
	protected void computeResult()
	{

	}

}
