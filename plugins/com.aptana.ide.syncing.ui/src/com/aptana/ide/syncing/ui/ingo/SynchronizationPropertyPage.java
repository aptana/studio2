package com.aptana.ide.syncing.ui.ingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.ProjectFileManager;
import com.aptana.ide.core.io.ingo.SyncManager;
import com.aptana.ide.core.io.ingo.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.SetUtils;

public class SynchronizationPropertyPage extends PreferencePage
		implements IWorkbenchPropertyPage {

	private IProject project;
	
	private Combo lastSyncConnection;
	private Combo lastCloudSyncConnection;
	private Button useSyncCoonnectionsAsDefault;
	private Map<String, String> connectionsMap = new LinkedHashMap<String, String>();
	private Map<String, String> cloudConnectionsMap = new LinkedHashMap<String, String>();

	public SynchronizationPropertyPage() {
		super();
	}
	
	@Override
	protected Control createContents(Composite parent) {		
		// reset maps
		connectionsMap.clear();
		cloudConnectionsMap.clear();
		
		if (project != null) {
			IVirtualFile projectVirtualFile = ProjectFileManager
					.convertResourceToFile(project);
			if (projectVirtualFile != null) {
				VirtualFileManagerSyncPair[] virtualFileManagerSyncPairs = getVirtualFileManagerSyncPairs(new IVirtualFile[] { projectVirtualFile });
				for (VirtualFileManagerSyncPair virtualFileManagerSyncPair : virtualFileManagerSyncPairs) {
					IVirtualFileManager destinationFileManager = virtualFileManagerSyncPair.getDestinationFileManager();
					String serializableString = ProjectSynchronizationUtils.toSerializableString(virtualFileManagerSyncPair);
					String nickName = virtualFileManagerSyncPair.getNickName();
					connectionsMap.put(serializableString, nickName);
					if (ProjectSynchronizationUtils.isCloudConnection(virtualFileManagerSyncPair)) { //$NON-NLS-1$
						cloudConnectionsMap.put(serializableString, nickName);
					}
				}
			}
		}
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridData daData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(daData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		Label lastSyncConnectionLabel = new Label(composite, SWT.BEGINNING);
		lastSyncConnectionLabel.setText(Messages.SynchronizationPropertyPage_lastSyncConnection);
		lastSyncConnection = new Combo(composite, SWT.READ_ONLY);		
		for (String conectionSerializableString : connectionsMap.keySet()) {
			lastSyncConnection.add(connectionsMap.get(conectionSerializableString));
		}
		
		// Start with default selection
		if (lastSyncConnection.getItemCount() > 0) {
			lastSyncConnection.select(0);
		}
		
		String lastSyncTargetConectionSerializableString =
			ProjectSynchronizationUtils.getLastSyncConnection(project);
		if (lastSyncTargetConectionSerializableString != null)
		{
			int i = 0;
			for (String key : connectionsMap.keySet()) {
				if (lastSyncTargetConectionSerializableString.equals(key)) {
					lastSyncConnection.select(i);
					break;
				}
				i++;
			}
		}

		if (cloudConnectionsMap.size() > 0) {
			Label lastCloudSyncConnectionLabel = new Label(composite, SWT.BEGINNING);
			lastCloudSyncConnectionLabel.setText(Messages.SynchronizationPropertyPage_lastCloudSyncConnection);
			lastCloudSyncConnection = new Combo(composite, SWT.READ_ONLY);
			for (String conectionSerializableString : cloudConnectionsMap.keySet()) {
				lastCloudSyncConnection.add(cloudConnectionsMap.get(conectionSerializableString));
			}
			
			// Start with default selection
			if (lastCloudSyncConnection.getItemCount() > 0) {
				lastCloudSyncConnection.select(0);
			}
			
			lastCloudSyncConnection.select(0);
			String lastCloudSyncConectionSerializableString =
				ProjectSynchronizationUtils.getLastCloudSyncConnection(project);
			if (lastCloudSyncConectionSerializableString != null)
			{
				int i = 0;
				for (String key : cloudConnectionsMap.keySet()) {
					if (lastCloudSyncConectionSerializableString.equals(key)) {
						lastCloudSyncConnection.select(i);
						break;
					}
					i++;
				}
			}
		}
		
		useSyncCoonnectionsAsDefault = new Button(composite, SWT.CHECK);
		useSyncCoonnectionsAsDefault.setText(Messages.SynchronizationPropertyPage_useConnectionsAsDefault); // TODO I18N
		useSyncCoonnectionsAsDefault.setSelection(ProjectSynchronizationUtils.isRememberDecision(project));

		return composite;
	}
	
	@Override
	protected void performDefaults() {
		useSyncCoonnectionsAsDefault.setSelection(false);
		lastSyncConnection.select(0);
//		 this should really set it to Public?
//		if (cloudConnectionsMap.size() > 0) {
//			lastCloudSyncConnection.select(0);
//		}
		super.performDefaults();
	}
	
	@Override
	public boolean performOk() {
		ProjectSynchronizationUtils.setRememberDecision(project, useSyncCoonnectionsAsDefault.getSelection());
		
		// Save the last sync connection
		int  lastSyncConnectionIndex = lastSyncConnection.getSelectionIndex();
		ProjectSynchronizationUtils.setLastSyncConnection(project, null);
		if (lastSyncConnectionIndex > 0) {
			Set<String> keySet = connectionsMap.keySet();
			int i = 0;
			for (String key : keySet) {
				if (i == lastSyncConnectionIndex) {
					ProjectSynchronizationUtils.setLastSyncConnection(project, key);
					break;
				}
				i++;
			}
		}
		// Save the last cloud sync connection
		ProjectSynchronizationUtils.setLastCloudSyncConnection(project, null);
		if (cloudConnectionsMap.size() > 0) {
			int  lastCloudSyncConnectionIndex = lastCloudSyncConnection.getSelectionIndex();
			if (lastCloudSyncConnectionIndex > 0) {
				Set<String> keySet = cloudConnectionsMap.keySet();
				int i = 0;
				for (String key : keySet) {
					if (i == lastCloudSyncConnectionIndex) {
						ProjectSynchronizationUtils.setLastCloudSyncConnection(project, key);
						break;
					}
					i++;
				}
			}
		}
		return super.performOk();
	}

	@Override
	public String getDescription() {
		return Messages.SynchronizationPropertyPage_Description;
	}

	public IAdaptable getElement() {
		return project;
	}

	public void setElement(IAdaptable element) {
		project = (IProject) element.getAdapter(IProject.class);
		if (project == null) {
			IResource resource = (IResource) element
					.getAdapter(IResource.class);
			if (resource != null) {
				project = resource.getProject();
			}
		}
	}

	/**
	 * getVirtualFileManagerSyncPair
	 * 
	 * @param files
	 * @return VirtualFileManagerSyncPair
	 */
	@SuppressWarnings("unchecked")
	private static VirtualFileManagerSyncPair[] getVirtualFileManagerSyncPairs(
			IVirtualFile[] files) {
		List<HashSet<VirtualFileManagerSyncPair>> syncSets = new ArrayList<HashSet<VirtualFileManagerSyncPair>>();
		for (IVirtualFile file : files) {
			VirtualFileManagerSyncPair[] confs = SyncManager
					.getContainingSyncPairs(file, true);
			HashSet<VirtualFileManagerSyncPair> newSet = new HashSet<VirtualFileManagerSyncPair>();
			newSet.addAll(Arrays.asList(confs));
			syncSets.add(newSet);
		}

		Set<Object>[] array = syncSets.toArray(new Set[syncSets.size()]);
		Set<Object> intersection = SetUtils.getIntersection(array);
		VirtualFileManagerSyncPair[] confs = (VirtualFileManagerSyncPair[]) intersection
				.toArray(new VirtualFileManagerSyncPair[0]);

		return confs;
	}
}
