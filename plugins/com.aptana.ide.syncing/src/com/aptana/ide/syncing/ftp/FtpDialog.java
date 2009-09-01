package com.aptana.ide.syncing.ftp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.ExceptionUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.ui.PixelConverter;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.core.ui.io.file.Messages;
import com.aptana.ide.io.file.FilePlugin;
import com.aptana.ide.io.ftp.IFtpVirtualFileManager;
import com.aptana.ide.syncing.SyncingPlugin;

/**
 * A generic dialog for FTP connections.
 * 
 * @author Shalom Gibly
 * @since Aptana Studio 1.2.4
 */
public class FtpDialog extends TitleAreaDialog
{
	private static final int ProgressInitalSleepDelay = 250;
	private IFtpVirtualFileManager item;
	private boolean newItem;
	protected ProgressMonitorPart progressMonitorPart;
	protected Map<String, ProtocolManager> managers;
	protected Text nickName;
	protected Combo managersCombo;
	protected Text server;
	protected Text remotePath;
	protected Label privateKeyLocation;
	protected Button usePublicKeyAuthCheckbox;
	protected Combo userName;
	protected Text password;
	protected Button saveButton;
	protected Composite progressPartContainer;
	private SelectionListener typeSwitchListener;
	private HashSet<String> names;
	private ModifyListener validationListener;
	private boolean hasError; // indicate that an error message is displayed
	private Button browseButton;
	private Button testButton;
	private IFtpVirtualFileManager originalItem;
	private boolean lockUI = false;
	private Label passwordLabel;

	/**
	 * Constructs a new GenericFTPDialog
	 * 
	 * @param initialFTPDialog
	 *            Provide the initial IVirtualFileManagerDialog2 that will generate the initial content in the dialog.
	 */
	public FtpDialog(Shell parent)
	{
		super(parent);
		loadProtocolManagers();
		setHelpAvailable(false);
	}

	/**
	 * Loads all the protocol managers that manage FTP connections.
	 */
	protected void loadProtocolManagers()
	{
		ProtocolManager[] prototcolManagers = ProtocolManager.getPrototcolManagers();
		managers = new HashMap<String, ProtocolManager>();
		for (ProtocolManager manager : prototcolManagers)
		{
			String displayName = manager.getDisplayName();
			if (displayName != null && displayName.toUpperCase().indexOf("FTP") > -1) // $NON-NLS-1$
			{
				managers.put(displayName, manager);
			}
		}
	}

	/**
	 * Returns the registered IVirtualFileManager
	 * 
	 * @return IVirtualFileManager
	 */
	public IFtpVirtualFileManager getItem()
	{
		return this.item;
	}

	/**
	 * Set an IFtpVirtualFileManager to this wizard
	 * 
	 * @param vfm
	 * @param newItem
	 */
	public void setItem(IFtpVirtualFileManager vfm, boolean newItem)
	{
		this.item = vfm;
		this.newItem = newItem;

		if (!newItem && originalItem == null)
		{
			originalItem = (IFtpVirtualFileManager) item.cloneManager();
			originalItem.setNickName(item.getNickName());
		}

		// Load the names of the previously defined items for the same type to avoid any conflicts.
		// The names hash will be checked in the validateFields
		IVirtualFileManager[] managers = (IVirtualFileManager[]) SyncManager.getSyncManager().getItems(vfm.getClass());
		names = new HashSet<String>();
		// Set the original name in case that the given item was opened for editing.
		// In that case, we don't want to have its name in the names list to avoid mis-validation.
		String originalName = !newItem ? vfm.getNickName() : null;
		for (int i = 0; i < managers.length; i++)
		{
			String name = managers[i].getNickName();
			if (originalName != null && name.equals(originalName))
				continue; // Skip that item
			names.add(name);
		}
		// This part should be invoked when an FTP type switch happens
		if (getContents() != null)
		{
			usePublicKeyAuthCheckbox.setEnabled(item.supportsPublicKeyAuthentication());
			updateTitle();
			validateFields();
			updateButtons();
		}
	}

	/**
	 * isNewItem
	 * 
	 * @return boolean
	 */
	public boolean isNewItem()
	{
		return this.newItem;
	}

	/**
	 * attempConnection
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	protected boolean attemptConnection() throws InvocationTargetException, InterruptedException
	{
		if (progressMonitorPart == null)
		{
			throw new IllegalArgumentException(Messages.VirtualFileManagerLocationDialog_ProgressMonitorNullError);
		}

		try
		{
			lockUI(true);
			ModalContext.run(new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask(Messages.VirtualFileManagerLocationDialog_TryingToConnect,
							IProgressMonitor.UNKNOWN);

					Thread.sleep(ProgressInitalSleepDelay);

					try
					{
						// disconnect so new settings will be used on next connect
						item.disconnect();

						// this call connects as a side-effect
						item.resolveBasePath();
					}
					catch (Exception e)
					{
						throw new InvocationTargetException(e);
					}
					finally
					{
						monitor.done();
					}
				}
			}, true, progressMonitorPart, getShell().getDisplay());
		}
		finally
		{
			lockUI(false);
		}

		return true;
	}

	private void lockUI(boolean lock)
	{
		lockUI = lock;
		getButton(OK).setEnabled(!lock);
		getButton(CANCEL).setEnabled(!lock);
		testButton.setEnabled(!lock);
		browseButton.setEnabled(!lock);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#canHandleShellCloseEvent()
	 */
	@Override
	protected boolean canHandleShellCloseEvent()
	{
		return !lockUI && super.canHandleShellCloseEvent();
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Control control = super.createContents(parent);
		updateButtons();
		return control;
	}

	/**
	 * Creates to dialog area according to the selected FTP
	 */
	protected Control createDialogArea(Composite parent)
	{
		validationListener = new ValidationListener();
		if (isNewItem())
		{
			getShell().setText(Messages.FtpDialog_LBL_NewConnection);
		}
		else
		{
			getShell().setText(Messages.FtpDialog_LBL_EditConnection);
		}
		ImageDescriptor imageDescriptor = SyncingPlugin.getImageDescriptor("icons/ftpconnect.gif"); //$NON-NLS-1$
		if (imageDescriptor != null)
		{
			final Image titleImage = imageDescriptor.createImage();
			setTitleImage(titleImage);
			parent.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					titleImage.dispose();
				}
			});
		}
		updateTitle();
		Composite p = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(p, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(layoutData);
		composite.setFont(p.getFont());

		// Add the default FTP widgets
		Label nickNameLabel = new Label(composite, SWT.NONE);
		nickNameLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_SiteName));
		nickName = createText(composite, 1, 0);
		if (managers.size() > 1)
		{
			Label connectionTypeLabel = new Label(composite, SWT.NONE);
			connectionTypeLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_ConnectionType));
			managersCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
			layoutData = new GridData();
			managersCombo.setLayoutData(layoutData);
			Object[] names = managers.keySet().toArray();
			String[] ftps = new String[names.length];
			for (int i = 0; i < names.length; i++)
			{
				ftps[i] = names[i].toString();
			}
			managersCombo.setItems(ftps);
			managersCombo.select(0);
			// We decide which item is the default and if to disable the combo at the init
		}

		// Add the remote info group
		Group remoteInfo = new Group(composite, SWT.NONE);
		remoteInfo.setText(Messages.FtpDialog_LBL_RemoteInfo);
		layout = new GridLayout(3, false);
		remoteInfo.setLayout(layout);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		remoteInfo.setLayoutData(layoutData);

		PixelConverter pixelConverter = new PixelConverter(parent);
		// Indent in a size of 3 characters, minus the group horizontal spacing.
		// This will align the text, password and combo widgets with the widgets on the top part.
		int indent = pixelConverter.convertWidthInCharsToPixels(3) - layout.horizontalSpacing;

		// Server
		Label serverLabel = new Label(remoteInfo, SWT.NONE);
		serverLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_Server));
		server = createText(remoteInfo, 2, indent);
		// Server example
		new Label(remoteInfo, SWT.NONE); // dummy label
		createExampleText(remoteInfo, Messages.FtpDialog_LBL_Example, 2, indent);
		// User Name
		Label userNameLabel = new Label(remoteInfo, SWT.NONE);
		userNameLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_UserName));
		userName = new Combo(remoteInfo, SWT.DROP_DOWN);
		userName.add("anonymous"); // $NON_NLS-1$
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalIndent = indent;
		userName.setLayoutData(layoutData);
		testButton = new Button(remoteInfo, SWT.PUSH);
		testButton.setText(Messages.FtpDialog_LBL_Test);
		testButton.addSelectionListener(new TestButtonSelectionAdapter());
		
		// Public Key Auth
		usePublicKeyAuthCheckbox = new Button(remoteInfo, SWT.CHECK);
		usePublicKeyAuthCheckbox.setText(Messages.FtpDialog_LBL_UsePublicKeyAuthentication);
		privateKeyLocation = new Label(remoteInfo, SWT.NONE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		layoutData.horizontalIndent = indent;
		privateKeyLocation.setLayoutData(layoutData);
		setFont(privateKeyLocation, 10);
		privateKeyLocation.setText(Messages.FtpDialog_LBL_NoPrivateKeySelected);
		usePublicKeyAuthCheckbox.setEnabled(item.supportsPublicKeyAuthentication());
		usePublicKeyAuthCheckbox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				boolean isSelected = usePublicKeyAuthCheckbox.getSelection();
				if (!isSelected)
				{
					passwordLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_Password));
					privateKeyLocation.setText(Messages.FtpDialog_LBL_NoPrivateKeySelected);
					return;
				}
				FileDialog dialog = new FileDialog(getShell(), SWT.APPLICATION_MODAL | SWT.OPEN);
				String userHome = System.getProperty("user.home"); //$NON-NLS-1$
				if (userHome != null)
				{
					dialog.setFilterPath(userHome + File.separator + ".ssh"); //$NON-NLS-1$
				}
				String selectedFile = dialog.open();
				if (selectedFile != null)
				{
					privateKeyLocation.setText(selectedFile);
				}
				passwordLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_KeyPass));
				validateFields();
			}
		});
		
		// Password
		passwordLabel = new Label(remoteInfo, SWT.NONE);
		passwordLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_Password));
		password = new Text(remoteInfo, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalIndent = indent;
		password.setLayoutData(layoutData);
		saveButton = new Button(remoteInfo, SWT.CHECK);
		saveButton.setText(Messages.FtpDialog_LBL_Save);
		saveButton.setSelection(true);

		// Remote path
		Label remotePathLabel = new Label(remoteInfo, SWT.NONE);
		remotePathLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_RemotePath));
		remotePath = createText(remoteInfo, 1, indent);
		browseButton = new Button(remoteInfo, SWT.PUSH);
		browseButton.setText(Messages.FtpDialog_LBL_Browse);
		browseButton.addSelectionListener(new RemoteBrowseSelectionAdapter());
		// Advanced Options
		new Label(remoteInfo, SWT.NONE);// dummy label
		Button advanced = new Button(remoteInfo, SWT.PUSH);
		advanced.setText(Messages.FtpDialog_LBL_AdvancedOptions);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.horizontalIndent = indent;
		advanced.setLayoutData(layoutData);
		advanced.addSelectionListener(new AdvancedOptionsSelectionAdapter());

		// Set the tabbing order
		Control[] tabList = new Control[] { server, userName, password, usePublicKeyAuthCheckbox, remotePath, advanced,
				browseButton, testButton, saveButton };
		remoteInfo.setTabList(tabList);

		// Progress monitor
		progressPartContainer = new Composite(composite, SWT.NONE);
		this.progressPartContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 1;
		gridLayout_1.horizontalSpacing = 1;
		gridLayout_1.numColumns = 2;
		this.progressPartContainer.setLayout(gridLayout_1);
		layout = new GridLayout();
		progressMonitorPart = new ProgressMonitorPart(progressPartContainer, layout);
		progressMonitorPart.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		initializeDialogFields();

		// Add the modification listeners
		nickName.addModifyListener(validationListener);
		server.addModifyListener(validationListener);
		userName.addModifyListener(validationListener);

		return composite;
	}

	/**
	 * Updates the dialog's title.
	 */
	protected void updateTitle()
	{
		if (isNewItem())
		{
			setTitle(Messages.FtpDialog_TTL_CreateANew + item.getProtocolManager().getDisplayName()
					+ Messages.FtpDialog_TTL_connection);
		}
		else
		{
			setTitle(Messages.FtpDialog_TTL_EditThe + item.getProtocolManager().getDisplayName()
					+ Messages.FtpDialog_TTL_connection);
		}
	}

	/**
	 * Initialize the dialog with the values that were passed with the {@link IFtpVirtualFileManager}.
	 */
	protected void initializeDialogFields()
	{
		if (item == null)
		{
			throw new IllegalStateException("The IVirtualFileManager was not set yet"); // $NON-NLS-1$ //$NON-NLS-1$
		}
		SWTUtils.setTextWidgetValue(this.nickName, item.getNickName());
		SWTUtils.setTextWidgetValue(this.password, item.getPassword());
		if (item.getPrivateKeyFile() != null && item.getPrivateKeyFile().trim().length() > 0)
		{
			this.usePublicKeyAuthCheckbox.setSelection(true);
			this.privateKeyLocation.setText(item.getPrivateKeyFile());
			this.passwordLabel.setText(StringUtils.makeFormLabel(Messages.FtpDialog_LBL_KeyPass));
		}
		else
		{
			this.privateKeyLocation.setText(Messages.FtpDialog_LBL_NoPrivateKeySelected);
		}
		if (item.getUser() != null)
		{
			this.userName.setText(item.getUser());
		}
		SWTUtils.setTextWidgetValue(this.server, item.getServer());
		SWTUtils.setTextWidgetValue(this.remotePath, StringUtils.EMPTY);
		SWTUtils.setTextWidgetValue(this.remotePath, item.getBasePath(), "/"); //$NON-NLS-1$
		this.saveButton.setSelection(item.getSavePassword());

		this.nickName.selectAll();
		this.nickName.setFocus();

		// Select the FTP connection type
		// The combo can be null in case we are not in the pro version and we have only one FTP option
		if (managersCombo != null)
		{
			managersCombo.select(managersCombo.indexOf(item.getProtocolManager().getDisplayName()));
			// Add the combo listener here
			if (typeSwitchListener == null)
			{
				typeSwitchListener = new TypeSwitchListener();
				managersCombo.addSelectionListener(typeSwitchListener);
			}
		}
		validateFields();
	}

	/**
	 * Save the values into the IFtpVirtualFileManager item
	 */
	public void saveValues()
	{
		item.setNickName(this.nickName.getText().trim());
		item.setSavePassword(this.saveButton.getSelection());
		item.setPassword(this.password.getText());		
		item.setUser(this.userName.getText());
		if (item.supportsPublicKeyAuthentication() && usePublicKeyAuthCheckbox.getSelection()
				&& !privateKeyLocation.getText().equals(Messages.FtpDialog_LBL_NoPrivateKeySelected))
		{
			item.setPrivateKeyFile(this.privateKeyLocation.getText());
		}
		item.setServer(this.server.getText());
		if (this.remotePath.getText() != null && this.remotePath.getText().equals(StringUtils.EMPTY) == false)
		{
			item.setBasePath(this.remotePath.getText());
		}
	}

	/**
	 * Creates a Text widget.
	 * 
	 * @param parent
	 * @param span
	 * @return A new {@link Text} widget.
	 */
	public static Text createText(Composite parent, int span, int indent)
	{
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		gd.horizontalIndent = indent;
		t.setLayoutData(gd);
		return t;
	}

	/**
	 * Creates an example text label (e.g. Small font text that should appear under a widget).
	 */
	protected void createExampleText(Composite parent, String text, int span, int indent)
	{
		Label banner = new Label(parent, SWT.NONE);
		banner.setText(text);
		setFont(banner, 8);
		GridData gds = new GridData(GridData.FILL_HORIZONTAL);
		gds.horizontalSpan = 2;
		gds.verticalIndent = -2;
		gds.verticalAlignment = SWT.TOP;
		gds.horizontalSpan = span;
		gds.horizontalIndent = indent;
		banner.setLayoutData(gds);
	}

	private void setFont(Label label, int size)
	{
		Font defaultFont = label.getParent().getFont();
		if (defaultFont == null)
		{
			defaultFont = JFaceResources.getTextFont();
		}
		final Font smallFont = new Font(label.getDisplay(), defaultFont.getFontData()[0].getName(), size, SWT.NONE);
		label.setFont(smallFont);
		label.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				smallFont.dispose();
			}

		});
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed()
	{
		if (newItem && item != null)
		{
			item.getProtocolManager().removeFileManager(item);
		}
		else if (!newItem && originalItem != null)
		{
			// Revert the change to the original item
			item.getProtocolManager().removeFileManager(item);
			ProtocolManager pmInItem = originalItem.getProtocolManager();
			pmInItem.addFileManager(originalItem);
		}
		this.item = null;
		super.cancelPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		if (validateFields())
		{
			saveValues();

			try
			{
				attemptConnection();
				super.okPressed();
			}
			catch (InvocationTargetException e1)
			{
				String msg = null;
				Throwable t = ExceptionUtils.getRootException(e1);

				if (t != null)
				{
					msg = t.getMessage();
				}
				if (msg == null)
				{
					msg = Messages.VirtualFileManagerLocationDialog_UnknownError;
				}

				String message = StringUtils.format(Messages.VirtualFileManagerLocationDialog_ConnectionErrorMessage,
						msg);
				IdeLog.logError(FilePlugin.getDefault(), message, t);

				MessageDialog md = new MessageDialog(getShell(),
						Messages.VirtualFileManagerLocationDialog_ConnectionErrorTitle, null, message,
						MessageDialog.WARNING, new String[] { CoreStrings.CONTINUE, CoreStrings.CANCEL }, 1);

				if (md.open() == Window.OK)
				{
					super.okPressed();
				}
			}
			catch (Exception e1)
			{
				String message = StringUtils.format(Messages.VirtualFileManagerLocationDialog_ConnectionErrorMessage,
						e1.getMessage());
				IdeLog.logError(FilePlugin.getDefault(), message, e1);

				MessageDialog md = new MessageDialog(getShell(),
						Messages.VirtualFileManagerLocationDialog_ConnectionErrorTitle, null, message,
						MessageDialog.WARNING, new String[] { CoreStrings.CONTINUE, CoreStrings.CANCEL }, 1);

				if (md.open() == Window.OK)
				{
					super.okPressed();
				}
			}
		}
		else
		{
			super.okPressed();
		}
	}

	/**
	 * Validates the fields to see if they are complete
	 * 
	 * @return boolean
	 */
	protected boolean validateFields()
	{
		// site-name check
		if (nickName.getText().trim().length() == 0)
		{
			setErrorMessage(Messages.FtpDialog_ERR_PleaseSetAConnectionName);
			return false;
		}
		else
		{
			if (names.contains(nickName.getText()))
			{
				setErrorMessage(Messages.FtpDialog_ERR_SiteWithSimilarNameExistsForThisTypeOfFTP);
				return false;
			}
		}
		if (server.getText().trim().length() == 0)
		{
			setErrorMessage(Messages.FtpDialog_ERR_PleaseProvideAServerHost);
			return false;
		}
		if (userName.getText().trim().length() == 0)
		{
			setErrorMessage(Messages.FtpDialog_ERR_PleaseProvideAUserName);
			return false;
		}
		if (privateKeyLocation.getText().trim().length() > 0 && !privateKeyLocation.getText().equals(Messages.FtpDialog_LBL_NoPrivateKeySelected))
		{
			File file = new File(privateKeyLocation.getText());
			if (!file.exists())
			{
				setErrorMessage("Private Key file does not exist");
				return false;
			}
			if (file.isDirectory())
			{
				setErrorMessage("Private key file is a directory");
				return false;
			}
		}
		setErrorMessage(null);
		return true;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setErrorMessage(java.lang.String)
	 */
	public void setErrorMessage(String newErrorMessage)
	{
		hasError = newErrorMessage != null;
		super.setErrorMessage(newErrorMessage);
	}

	/**
	 * Updates the dialog's buttons state. The default implementation disables the OK button when an error message is
	 * displayed.
	 */
	protected void updateButtons()
	{
		Button button = getButton(Dialog.OK);
		if (button != null)
		{
			button.setEnabled(!hasError);
			browseButton.setEnabled(!hasError);
			testButton.setEnabled(!hasError);
		}
	}

	/**
	 * TestButtonSelectionAdapter
	 */
	protected class TestButtonSelectionAdapter extends SelectionAdapter
	{
		/**
		 * TestButtonSelectionAdapter
		 */
		public TestButtonSelectionAdapter()
		{
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(final SelectionEvent e)
		{
			if (!validateFields())
			{
				return;
			}
			if (!e.widget.isDisposed())
			{
				saveValues();

				try
				{
					attemptConnection();
					MessageDialog.openInformation(getShell(),
							Messages.VirtualFileManagerLocationDialog_ConnectionSuccessful, StringUtils
									.format(Messages.VirtualFileManagerLocationDialog_ConnectionToSucceeded, item
											.getNickName()));
				}
				catch (InvocationTargetException e1)
				{
					String msg = null;

					Throwable t = ExceptionUtils.getRootException(e1);

					if (t != null)
					{
						msg = t.getMessage();
					}

					if (msg == null)
					{
						msg = Messages.VirtualFileManagerLocationDialog_UnknownError;
					}

					MessageDialog.openWarning(getShell(),
							Messages.VirtualFileManagerLocationDialog_ConnectionErrorTitle, StringUtils.format(
									Messages.VirtualFileManagerLocationDialog_ConnectionErrorMessage, msg));
				}
				catch (Exception e1)
				{
					MessageDialog.openWarning(getShell(),
							Messages.VirtualFileManagerLocationDialog_ConnectionErrorTitle, StringUtils.format(
									Messages.VirtualFileManagerLocationDialog_ConnectionErrorMessage, e1
											.getLocalizedMessage()));
				}
			}
		}
	}

	/**
	 * Triggered when the 'Advanced Option' button is clicked, and displays the Advanced Options dialog for the selected
	 * item.
	 */
	protected class AdvancedOptionsSelectionAdapter extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			item.getDialogFactory().createAdvancedFtpDialog(FtpDialog.this).open();
		}
	}

	/**
	 * Triggered when the 'Remote Path' button is clicked, and displays the remote FTP in a tree structure for a path
	 * selection.
	 */
	protected class RemoteBrowseSelectionAdapter extends SelectionAdapter
	{

		public void widgetSelected(SelectionEvent e)
		{
			if (!validateFields())
				return;
			saveValues();
			try
			{
				attemptConnection();
			}
			catch (Exception ex)
			{
				String msg = null;

				Throwable t = ExceptionUtils.getRootException(ex);

				if (t != null)
				{
					msg = t.getMessage();
				}

				if (msg == null)
				{
					msg = Messages.VirtualFileManagerLocationDialog_UnknownError;
				}
				if (!getContents().isDisposed())
				{
					MessageDialog.openWarning(getShell(), Messages.FtpDialog_TTL_ConnectionError,
							Messages.FtpDialog_WRN_CouldNotConnect + msg);
				}
				return;
			}
			// Set the base path to the '/', so that the dialog will display the FTP directories from the top.
			getItem().setBasePath("/"); //$NON-NLS-1$
			FtpBrowseDialog dialog = new FtpBrowseDialog(getShell());
			dialog.setTitle(getItem().getProtocolManager().getDisplayName() + Messages.FtpDialog_TTL_PathSelection);
			dialog.setInput(new Object[] { getItem() });
			if (dialog.open() == Window.OK)
			{
				Object[] result = dialog.getResult();
				if (result != null && result.length > 0)
				{
					// take the first item
					if (result[0] instanceof IVirtualFile)
					{
						remotePath.setText(((IVirtualFile) result[0]).getAbsolutePath());
					}
					else
					{
						remotePath.setText("/"); //$NON-NLS-1$
					}
				}
			}
			else
			{
				getItem().setBasePath(remotePath.getText());
			}
		}
	}

	/*
	 * A modify listener that calls validate fields on widget modifications.
	 */
	private class ValidationListener implements ModifyListener
	{
		public void modifyText(ModifyEvent e)
		{
			validateFields();
			updateButtons();
		}
	}

	/*
	 * Handle a switch in the FTP connections types combo.
	 */
	private class TypeSwitchListener extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			// Remove the previous
			if (item != null)
			{
				item.getProtocolManager().removeFileManager(item);
			}
			// Switch to the new FTP type
			String selectedFTP = managersCombo.getText();
			ProtocolManager pm = managers.get(selectedFTP);
			// create virtual file manager
			IVirtualFileManager vfm = pm.createFileManager(false);
			setItem((IFtpVirtualFileManager) vfm, isNewItem());
			if (!isNewItem())
			{
				// we need to add this item again into its right location
				saveValues();
				ProtocolManager pmInItem = item.getProtocolManager();
				pmInItem.addFileManager(item);
			}
		}
	}
}
