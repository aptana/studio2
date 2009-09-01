package com.aptana.ide.core.ui.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.preferences.IPreferenceConstants;
import com.aptana.ide.core.ui.AptanaAuthenticator;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.dialogs.BooleanFieldEditorPublic;

/**
 * PasswordsPreferencePage
 */
public class PasswordsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private TableViewer credentialList;
	private Button removeButton;
	private Button removeAllButton;
	private List<HostCredentials> credentials;
	private BooleanFieldEditorPublic enableCaching;
	private Composite composite;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		composite = createPageRoot(parent);

		Label tableLabel = new Label(composite, SWT.NONE);
		tableLabel
				.setText(Messages.PasswordsPreferencePage_MSG_SecurePasswordStorage);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		tableLabel.setLayoutData(data);
		Font font = parent.getFont();
		composite.setFont(font);
		tableLabel.setFont(font);

		enableCaching = new BooleanFieldEditorPublic(IPreferenceConstants.PREF_ENABLE_PASSWORD_CACHING,
				Messages.PasswordsPreferencePage_LBL_EnablePasswordCaching, composite);

        boolean enabled = Platform.getPreferencesService().getBoolean(
                AptanaCorePlugin.ID,
                IPreferenceConstants.PREF_ENABLE_PASSWORD_CACHING, true, null);
		Button checkbox = enableCaching.getChangeControl(composite);
		checkbox.setSelection(enabled);

		Table table = createCredentialsTable(composite);
		createCredentialsTableViewer(table);
		createButtonGroup(composite);

		fillWithCredentials();

		enableButtons();
		return composite;
	}

	private void fillWithCredentials()
	{
		credentials = loadCredentials();
		credentialList.setInput(credentials);
		credentialList.refresh();
	}

	private List<HostCredentials> loadCredentials()
	{
		Set<String> hosts = AptanaAuthenticator.getSavedHosts();
		List<HostCredentials> credentials = new ArrayList<HostCredentials>();
		for (Iterator<String> iter = hosts.iterator(); iter.hasNext();)
		{
			String host = iter.next();
			if (host.trim().length() == 0)
			{
				continue;
			}
			String username = AptanaAuthenticator.getUserName(host);
			HostCredentials cred = new HostCredentials(host, username);
			credentials.add(cred);
		}
		return credentials;
	}

	protected Composite createPageRoot(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		return composite;
	}

	protected Table createCredentialsTable(Composite composite)
	{
		Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText(Messages.PasswordsPreferencePage_LBL_Host);
		column.setWidth(250);

		column = new TableColumn(table, SWT.NULL);
		column.setText(Messages.PasswordsPreferencePage_LBL_Username);
		column.setWidth(125);

		return table;
	}

	protected void createCredentialsTableViewer(Table table)
	{
		credentialList = new TableViewer(table);

		credentialList.setLabelProvider(new CredentialsTableLabelProvider());
		credentialList.setContentProvider(new IStructuredContentProvider()
		{

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}

			public void dispose()
			{
			}

			public Object[] getElements(Object inputElement)
			{
				List creds = (List) inputElement;
				return creds.toArray();
			}

		});

		credentialList.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent evt)
			{
				enableButtons();
			}
		});
	}

	protected HostCredentials getSelectedCredentials()
	{
		IStructuredSelection selection = (IStructuredSelection) credentialList.getSelection();
		return (HostCredentials) selection.getFirstElement();
	}

	protected void enableButtons()
	{
		if (getSelectedCredentials() != null)
		{
			removeButton.setEnabled(true);
		}
		else
		{
			removeButton.setEnabled(false);
		}
	}

	protected void createButtonGroup(Composite composite)
	{
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText(Messages.PasswordsPreferencePage_LBL_Remove);
		removeButton.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event evt)
			{
				removeCredentials();
			}
		});

		removeAllButton = new Button(buttons, SWT.PUSH);
		removeAllButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeAllButton.setText(Messages.PasswordsPreferencePage_LBL_RemoveAll);
		removeAllButton.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event evt)
			{
				removeAllCredentials();
			}
		});
	}

	protected void removeAllCredentials()
	{
		for (Iterator<HostCredentials> iter = credentials.iterator(); iter.hasNext();)
		{
			HostCredentials credential = iter.next();
			credential.remove();
		}
		credentials = new ArrayList<HostCredentials>();
		credentialList.setInput(credentials);
		credentialList.refresh();
	}

	protected void removeCredentials()
	{
		HostCredentials creds = getSelectedCredentials();
		creds.remove();
		credentials.remove(creds);
		credentialList.setInput(credentials);
		credentialList.refresh();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// Initialize the preference store we wish to use
		setPreferenceStore(CoreUIPlugin.getDefault().getPreferenceStore());
	}

	public boolean performOk()
	{
		boolean cachingEnabled = enableCaching.getBooleanValue();
		IEclipsePreferences prefs = (new InstanceScope()).getNode(AptanaCorePlugin.ID);
		prefs.putBoolean(IPreferenceConstants.PREF_ENABLE_PASSWORD_CACHING, cachingEnabled);
		IEclipsePreferences defaultPrefs = (new DefaultScope()).getNode(AptanaCorePlugin.ID);
		prefs.put(IPreferenceConstants.CACHED_KEY, defaultPrefs.get(IPreferenceConstants.CACHED_KEY, ""));
		try {
            prefs.flush();
        } catch (BackingStoreException e) {
        }

		return super.performOk();
	}

	protected void performDefaults()
	{
	    IEclipsePreferences defaultPrefs = (new DefaultScope()).getNode(AptanaCorePlugin.ID);
		boolean enabled = defaultPrefs.getBoolean(
				IPreferenceConstants.PREF_ENABLE_PASSWORD_CACHING, true);
		Button checkbox = enableCaching.getChangeControl(composite);
		checkbox.setSelection(enabled);
		super.performDefaults();
	}

	private class HostCredentials
	{
		private String host;
		private String username;

		HostCredentials(String host, String username)
		{
			this.host = host;
			this.username = username;
		}

		public String getHost()
		{
			return host;
		}

		public String getUsername()
		{
			return username;
		}

		public void remove()
		{
			AptanaAuthenticator.removeCachedAuthentication(host);
		}
	}

	private class CredentialsTableLabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			HostCredentials cred = (HostCredentials) element;
			switch (columnIndex)
			{
				case 0:
					return cred.getHost();
				case 1:
					return cred.getUsername();
				default:
					return Messages.PasswordsPreferencePage_LBL_UnknownColumn;
			}
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void dispose()
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}

	}

}
