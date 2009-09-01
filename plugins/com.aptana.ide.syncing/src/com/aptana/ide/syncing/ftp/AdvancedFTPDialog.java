/**
 * 
 */
package com.aptana.ide.syncing.ftp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.DateUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.io.ftp.IFtpVirtualFileManager;

/**
 * An 'Advanced Properties' dialog for FTP connections.
 * 
 * @author Shalom Gibly
 * @since Aptana Studio 1.2.4
 */
public class AdvancedFTPDialog extends StatusDialog
{
	protected Text port;
	protected Button passiveMode;
	protected Button timeOffset;
	protected Label offsetTimeLabel;
	protected IFtpVirtualFileManager item;
	protected FtpDialog ftpDialog;

	/**
	 * Constructs a new AdvancedFTPDialog for the given FTPDialog
	 * 
	 * @param dialog
	 */
	public AdvancedFTPDialog(FtpDialog dialog)
	{
		super(dialog.getShell());
		this.ftpDialog = dialog;
		this.item = ftpDialog.getItem();
		setHelpAvailable(false);
		setTitle(Messages.AdvancedFTPDialog_TTL_AdvancedConnectionOptions);
		setStatusLineAboveButtons(true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.StatusDialog#updateStatus(org.eclipse.core.runtime.IStatus)
	 */
	public void updateStatus(IStatus status)
	{
		super.updateStatus(status);
	}

	/**
	 * Initialize the advanced properties dialog with the values that were passed with the
	 * {@link IFtpVirtualFileManager}.
	 */
	protected void initializeAdvancedFields()
	{
		if (item == null)
		{
			throw new IllegalStateException("The IVirtualFileManager was not set yet"); // $NON-NLS-1$ //$NON-NLS-1$
		}

		if (item.getPort() > 0) {
			SWTUtils.setTextWidgetValue(this.port, String.valueOf(item.getPort()));			
		} else {
			SWTUtils.setTextWidgetValue(this.port, "21"); //$NON-NLS-1$
		}

		if (!ftpDialog.isNewItem())
		{
			this.passiveMode.setSelection(item.getPassiveMode());
		}
		this.timeOffset.setSelection(item.isAutoCalculateServerTimeOffset());
		try
		{
			this.offsetTimeLabel.setText(DateUtils.getTimeOffsetString(item.getTimeOffset()));
		}
		catch (ConnectionException e)
		{
			this.offsetTimeLabel.setText(Messages.AdvancedFTPDialog_LBL_0Seconds);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		saveValues();
		super.okPressed();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createAdvancedContent(composite);
		return composite;
	}

	/**
	 * Creates the advanced content that should be displayed in the 'Advanced Properties' dialog. The default
	 * implementation of this method creates the basic set of widgets that are common to all the FTP connections, and
	 * also calls to the {@link #createExtendAdvancedContent(Composite)} method to add more content to the top group by
	 * any extending FTP dialog.
	 * 
	 * @param parent
	 * @param advancedFTPDialog
	 */
	public void createAdvancedContent(Composite parent)
	{
		Group settings = new Group(parent, SWT.NONE);
		settings.setText(Messages.AdvancedFTPDialog_LBL_Settings);
		GridLayout gridLayout = new GridLayout(2, false);
		settings.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		settings.setLayoutData(gridData);
		// Port
		Composite portComposite = new Composite(settings, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		portComposite.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		portComposite.setLayoutData(gridData);
		Label portLabel = new Label(portComposite, SWT.NONE);
		portLabel.setText(StringUtils.makeFormLabel(Messages.AdvancedFTPDialog_LBL_Port));
		port = FtpDialog.createText(portComposite, 1, 0);
		gridData = new GridData();
		gridData.widthHint = 50;
		port.setLayoutData(gridData);
		port.addModifyListener(new IntegerFieldModifyListener(0, 65535)); // port range check
		// Passive Mode
		passiveMode = new Button(settings, SWT.CHECK);
		passiveMode.setText(Messages.AdvancedFTPDialog_LBL_UsePassiveMode);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		passiveMode.setLayoutData(gridData);

		// Create extended content into this settings group.
		createExtendAdvancedContent(settings);

		// Server Time Settings group
		Group timeSettings = new Group(parent, SWT.NONE);
		timeSettings.setText(Messages.AdvancedFTPDialog_LBL_ServerTime);
		gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		gridLayout.numColumns = 3;
		timeSettings.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		timeSettings.setLayoutData(gridData);
		Label explanationLabel = new Label(timeSettings, SWT.WRAP);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		explanationLabel
				.setText(Messages.AdvancedFTPDialog_LBL_TurningThisOnIsOnlyRecommended);
		explanationLabel.setLayoutData(gridData);

		timeOffset = new Button(timeSettings, SWT.CHECK);
		timeOffset.setLayoutData(new GridData());
		timeOffset.setText(Messages.AdvancedFTPDialog_LBL_CalculateServerClientTimeOffsetAutomatically);
		timeOffset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label offsetLabel = new Label(timeSettings, SWT.NONE);
		offsetLabel.setText(StringUtils.makeFormLabel(Messages.AdvancedFTPDialog_LBL_CurrentOffset));

		offsetTimeLabel = new Label(timeSettings, SWT.NONE);
		offsetTimeLabel.setText(StringUtils.EMPTY);

		Button resetOffsetButton = new Button(timeSettings, SWT.NONE);
		resetOffsetButton.setText(Messages.AdvancedFTPDialog_LBL_ResetOffset);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		resetOffsetButton.setLayoutData(gridData);
		resetOffsetButton.addSelectionListener(new ResetOffsetButtonSelectionAdapter());

		initializeAdvancedFields();
	}

	/**
	 * Create an extended advanced settings in the first group visible at the Advanced Properties dialog. This method
	 * should be overridden by any extending dialog that wish to add more settings.
	 * 
	 * @param parent
	 */
	protected void createExtendAdvancedContent(Composite parent)
	{
		// Do nothing here
	}

	/**
	 * Saves the fields values that were contributed by this dialog.
	 */
	public void saveValues()
	{
		item.setPort(Integer.valueOf(this.port.getText()).intValue());
		item.setPassiveMode(this.passiveMode.getSelection());
		item.setAutoCalculateServerTimeOffset(this.timeOffset.getSelection());
	}

	/**
	 * A modify listener that will indicate an error in case that the input is not an integer in the given range.
	 */
	protected class IntegerFieldModifyListener implements ModifyListener
	{

		private final int min;
		private final int max;

		public IntegerFieldModifyListener(int min, int max)
		{
			this.min = min;
			this.max = max;
		}

		public void modifyText(ModifyEvent e)
		{
			String text = ((Text) e.widget).getText();
			boolean hasError = false;
			try
			{
				int port = Integer.parseInt(text);
				hasError = port < min || port > max;
			}
			catch (NumberFormatException nfe)
			{
				hasError = true;
			}
			if (hasError)
			{
				updateStatus(new Status(IStatus.ERROR, "com.aptana.ide.syncing", 0, Messages.AdvancedFTPDialog_MSG_PleaseSetValidPortBetween //$NON-NLS-1$
						+ min + Messages.AdvancedFTPDialog_MSG_to + max, null));
			}
			else
			{
				updateStatus(Status.OK_STATUS);
			}
		}
	}

	/**
	 * ResetOffsetButtonSelectionAdapter
	 */
	protected class ResetOffsetButtonSelectionAdapter extends SelectionAdapter
	{
		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(final SelectionEvent e)
		{
			item.resetTimeOffsetCache();
			BusyIndicator.showWhile(dialogArea.getDisplay(), new Runnable() {
				public void run() {
					try
					{
						offsetTimeLabel.setText(DateUtils.getTimeOffsetString(item.getTimeOffset()));
					}
					catch (ConnectionException ex)
					{
						offsetTimeLabel.setText(Messages.AdvancedFTPDialog_MSG_0Seconds);
					}
				}				
			});
		}
	}
}
