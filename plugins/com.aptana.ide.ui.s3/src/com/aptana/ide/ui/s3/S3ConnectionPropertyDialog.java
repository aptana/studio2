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

package com.aptana.ide.ui.s3;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.filesystem.s3.S3ConnectionPoint;
import com.aptana.ide.ui.IPropertyDialog;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;

/**
 * @author Max Stepanov
 */
public class S3ConnectionPropertyDialog extends TitleAreaDialog implements IPropertyDialog
{

	private static final String DEFAULT_NAME = "New S3 Connection";

	private S3ConnectionPoint genericConnectionPoint;
	private boolean isNew = false;

	private Text nameText;
	private Text accessKeyText;
	private Text remotePathText;
	protected Label passwordLabel;
	protected Text passwordText;

	private ModifyListener modifyListener;

	/**
	 * @param parentShell
	 */
	public S3ConnectionPropertyDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element)
	{
		genericConnectionPoint = null;
		if (element instanceof S3ConnectionPoint)
		{
			genericConnectionPoint = (S3ConnectionPoint) element;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource()
	{
		return genericConnectionPoint;
	}

	private String getConnectionPointType()
	{
		return S3ConnectionPoint.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		if (genericConnectionPoint != null)
		{
			setTitle("Edit the Connection");
			getShell().setText("Edit Connection");
		}
		else
		{
			setTitle("Create a Connection");
			getShell().setText("New Connection");
		}

		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults().margins(
				convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
				convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN)).spacing(
				convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
				convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING)).numColumns(2).create());

		/* row 1 */
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH), SWT.DEFAULT)
				.create());
		label.setText(StringUtils.makeFormLabel("Name"));

		nameText = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults().hint(
				convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).grab(true, false)
				.create());

		/* row 2 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH), SWT.DEFAULT)
				.create());
		label.setText(StringUtils.makeFormLabel("Access Key"));

		accessKeyText = new Text(container, SWT.SINGLE | SWT.BORDER);
		accessKeyText.setLayoutData(GridDataFactory.swtDefaults().hint(
				convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).grab(true, false)
				.create());

		/* row ? */
		createPasswordSection(container);

		/* row 3 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH), SWT.DEFAULT)
				.create());
		label.setText(StringUtils.makeFormLabel("Remote Path"));
		// FIXME Make it a discrete path, not bucket then subpath
		remotePathText = new Text(container, SWT.SINGLE | SWT.BORDER);
		remotePathText.setLayoutData(GridDataFactory.swtDefaults().hint(
				convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).grab(true, false)
				.create());

		/* -- */
		addListeners();

		passwordText.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				e.doit = false;
				testConnection();
			}
		});

		if (genericConnectionPoint == null)
		{
			try
			{
				genericConnectionPoint = (S3ConnectionPoint) CoreIOPlugin.getConnectionPointManager()
						.createConnectionPoint(getConnectionPointType());
				genericConnectionPoint.setName(DEFAULT_NAME);
				isNew = true;
			}
			catch (CoreException e)
			{
				IdeLog.logError(Activator.getDefault(), "Create new connection failed", e);
				close();
			}
		}
		loadPropertiesFrom(genericConnectionPoint);

		return dialogArea;
	}

	protected void createPasswordSection(Composite parent)
	{
		passwordLabel = new Label(parent, SWT.NONE);
		passwordLabel.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(passwordLabel).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		passwordLabel.setText(StringUtils.makeFormLabel("Secret Access Key"));

		passwordText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		passwordText.setLayoutData(GridDataFactory.fillDefaults().hint(
				convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).grab(true, false)
				.create());
	}

	protected void testConnection()
	{
		// TODO Auto-generated method stub

	}

	protected void addListeners()
	{
		if (modifyListener == null)
		{
			modifyListener = new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					validate();
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		accessKeyText.addModifyListener(modifyListener);
		remotePathText.addModifyListener(modifyListener);
	}

	protected void removeListeners()
	{
		if (modifyListener != null)
		{
			nameText.removeModifyListener(modifyListener);
			accessKeyText.removeModifyListener(modifyListener);
			remotePathText.removeModifyListener(modifyListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		if (!isValid())
		{
			return;
		}
		CoreIOPlugin.getAuthenticationManager().setPassword(accessKeyText.getText(),
				passwordText.getText().toCharArray(), true);
		if (savePropertiesTo(genericConnectionPoint))
		{
			/* TODO: notify */
		}
		if (isNew)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(genericConnectionPoint);
		}
		super.okPressed();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		try
		{
			return super.createContents(parent);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		finally
		{
			validate();
		}
	}

	protected void loadPropertiesFrom(S3ConnectionPoint connectionPoint)
	{
		removeListeners();
		try
		{
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			accessKeyText.setText(valueOrEmpty(connectionPoint.getAccessKey()));
			remotePathText.setText(valueOrEmpty(connectionPoint.getPath().toPortableString()));
		}
		finally
		{
			addListeners();
		}
	}

	protected boolean savePropertiesTo(S3ConnectionPoint connectionPoint)
	{
		boolean updated = false;
		String name = nameText.getText();
		if (!name.equals(connectionPoint.getName()))
		{
			connectionPoint.setName(name);
			updated = true;
		}
		String accessKey = accessKeyText.getText();
		if (!accessKey.equals(connectionPoint.getAccessKey()))
		{
			connectionPoint.setAccessKey(accessKey);
			updated = true;
		}
		IPath path = Path.fromPortableString(remotePathText.getText());
		if (!connectionPoint.getPath().equals(path))
		{
			connectionPoint.setPath(path);
			updated = true;
		}
		char[] password = passwordText.getText().toCharArray();
		if (!Arrays.equals(password, connectionPoint.getPassword()))
		{
			connectionPoint.setPassword(password);
			updated = true;
		}
		return updated;
	}

	public void validate()
	{
		boolean valid = isValid();
		getButton(OK).setEnabled(valid);
	}

	public boolean isValid()
	{
		String message = null;
		if (nameText.getText().length() == 0)
		{
			message = "Please specify shortcut name";
		}
		else if (accessKeyText.getText().length() == 0)
		{
			message = "Please specify access key";
		}
		if (message != null)
		{
			setErrorMessage(message);
		}
		else
		{
			setErrorMessage(null);
			setMessage(null);
			return true;
		}
		return false;
	}

	protected static String valueOrEmpty(String value)
	{
		if (value != null)
		{
			return value;
		}
		return StringUtils.EMPTY;
	}

}
