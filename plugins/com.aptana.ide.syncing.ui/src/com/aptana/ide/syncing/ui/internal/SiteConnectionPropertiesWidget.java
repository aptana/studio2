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

package com.aptana.ide.syncing.ui.internal;

import java.io.File;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.ui.IPropertyDialog;
import com.aptana.ide.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class SiteConnectionPropertiesWidget extends Composite {

	private TitleAreaDialog dialog;
	private ISiteConnection source;
	private Text nameText;
	private TargetEditor sourceEditor;
	private TargetEditor destinationEditor;
	private boolean changed;
	
	/**
	 * @param parent
	 * @param style
	 */
	public SiteConnectionPropertiesWidget(Composite parent, int style, TitleAreaDialog dialog) {
		super(parent, style);
		this.dialog = dialog;
		setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		
		/* row 1 */
		Label label = new Label(this, SWT.NONE);
		label.setText(StringUtils.makeFormLabel("Name"));		
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		
		nameText = new Text(this, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
		
		/* row 2 */
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setText("Source");
		
		sourceEditor = new TargetEditor("Source");
		sourceEditor.createTargets(group, false);

		/* row 2 */
		group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setText("Destination");
	
		destinationEditor = new TargetEditor("Destination");
		destinationEditor.createTargets(group, true);
		
		/* -- */
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				changed = true;
				validateAll();
			}
		});

	}

	public void setSource(ISiteConnection source) {
		if (this.source == source) {
			return;
		}
		this.source = source;
		nameText.setText(source.getName());
		sourceEditor.setTarget(source.getSource());
		destinationEditor.setTarget(source.getDestination());
		validateAll();
		changed = false;
	}
	
	public ISiteConnection getSource() {
		return source;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public boolean applyChanges() {
		if (!validateAll()) {
			return false;
		}
		SiteConnection siteConnection = (SiteConnection) source;
		siteConnection.setName(nameText.getText());
		
		IConnectionPoint connectionPoint = sourceEditor.getTarget();
		if (connectionPoint != null) {
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(connectionPoint);
		}
		siteConnection.setSource(connectionPoint);
		
		connectionPoint = destinationEditor.getTarget();
		if (connectionPoint != null) {
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(connectionPoint);
		}
		siteConnection.setDestination(connectionPoint);
		
		SyncingPlugin.getSiteConnectionManager().addSiteConnection(siteConnection);
		changed = false;
		return true;
	}
	
	private boolean validateAll() {
		String message = null;
		String name = nameText.getText().trim();
		if (name.length() == 0) {
			message = "Please specify site name";
		} else {
	    	for (ISiteConnection i : SyncingPlugin.getSiteConnectionManager().getSiteConnections()) {
	    		if (i != source && name.equals(i.getName())) {
	    			message = StringUtils.format("More than one connections have the name ''{0}''."
	    					+" Please assign an unique name for each connection.", name);
	    		}
	    	}
		}
		if (message == null) {
			message = sourceEditor.validate();
		}
		if (message == null) {
			message = destinationEditor.validate();
		}
		dialog.setErrorMessage(message);
		return (message == null);
	}
	
	private IConnectionPoint createNewRemoteConnection() {
		Dialog dlg = new FTPPropertyDialogProvider().createPropertyDialog(new SameShellProvider(this));
		if (dlg instanceof IPropertyDialog) {
			((IPropertyDialog) dlg).setPropertySource(
					CoreIOPlugin.getConnectionPointManager().getType(FTPConnectionPoint.TYPE));
		}
		if (dlg.open() == Window.OK) {
			Object result = null;
			if (dlg instanceof IPropertyDialog) {
				result = ((IPropertyDialog) dlg).getPropertySource();
			}
			if (result instanceof IConnectionPoint) {
				return (IConnectionPoint) result;
			}
		}
		return null;
	}
	
	private IContainer browseWorkspace(IContainer container, IPath path) {
		FileFolderSelectionDialog dlg = new FileFolderSelectionDialog(getShell(), false, IResource.FOLDER);
		IFileStore input = EFSUtils.getFileStore(container);
		dlg.setInput(input);
		dlg.setInitialSelection(input.getFileStore(path));
		if (dlg.open() == Window.OK) {
			if (dlg.getFirstResult() instanceof IAdaptable) {
				return (IContainer) ((IAdaptable) dlg.getFirstResult()).getAdapter(IResource.class);
			}
		}
		return null;
	}
	
	private IPath browseFilesystem(IPath path) {
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		dlg.setFilterPath(path.toOSString());
		String result = dlg.open();
		if (result != null) {
			return Path.fromOSString(result);
		}
		return null;
	}
		
	private class TargetEditor {
		
		private static final int REMOTE = 1;
		private static final int PROJECT = 2;
		private static final int FILESYSTEM = 3;
		
		private String name;
		private Button remoteRadio;
		private ComboViewer remotesViewer;
		private Button projectRadio;
		private ComboViewer projectViewer;
		private Text projectFolderText;
		private Button filesystemRadio;
		private Text filesystemFolderText;
				
		public TargetEditor(String name) {
			this.name = name;
		}

		private void createTargets(Composite parent, boolean showRemote) {
			parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
			
			/* row 1 */
			remoteRadio = new Button(parent, SWT.RADIO);
			remoteRadio.setText("Remote");
			remoteRadio.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
		
			remotesViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			remotesViewer.getControl().setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote)
					.align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).create());
			remotesViewer.setContentProvider(new ArrayContentProvider());
			remotesViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
			remotesViewer.setInput(ConnectionPointUtils.getRemoteConnectionPoints());
			
			Button newRemoteButton = new Button(parent, SWT.PUSH);
			newRemoteButton.setText(StringUtils.ellipsify(CoreStrings.NEW));
			newRemoteButton.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
					
			/* row 2 */
			projectRadio = new Button(parent, SWT.RADIO);
			projectRadio.setText("Project");
			projectRadio.setLayoutData(GridDataFactory.swtDefaults().create());
		
			projectViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			projectViewer.getControl().setLayoutData(GridDataFactory.swtDefaults()
					.align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).create());
			projectViewer.setContentProvider(new ArrayContentProvider());
			projectViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
			projectViewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());

			/* row 3 */
			new Label(parent, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());
			
			Label label = new Label(parent, SWT.NONE);
			label.setText(StringUtils.makeFormLabel("Folder"));
			label.setLayoutData(GridDataFactory.swtDefaults().create());
			
			projectFolderText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
			projectFolderText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
			
			Button projectBrowseButton = new Button(parent, SWT.PUSH);
			projectBrowseButton.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
			projectBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());

			/* row 4 */
			filesystemRadio = new Button(parent, SWT.RADIO);
			filesystemRadio.setText("Filesystem");
			filesystemRadio.setLayoutData(GridDataFactory.swtDefaults().create());

			filesystemFolderText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
			filesystemFolderText.setLayoutData(GridDataFactory.swtDefaults()
					.align(SWT.FILL, SWT.CENTER).span(2, 1).create());

			Button filesystemBrowseButton = new Button(parent, SWT.PUSH);
			filesystemBrowseButton.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
			filesystemBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());

			
			/* -- */
			SelectionListener selectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					changed = true;
					validateAll();
				}
			};
			remoteRadio.addSelectionListener(selectionListener);
			projectRadio.addSelectionListener(selectionListener);
			filesystemRadio.addSelectionListener(selectionListener);
			
			remotesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					changed = true;
					setType(REMOTE);
					validateAll();
				}
			});
			newRemoteButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					changed = true;
					setType(REMOTE);
					validateAll();
				}
			});

			projectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					changed = true;
					setType(PROJECT);
					projectFolderText.setText(Path.ROOT.toPortableString());
					validateAll();
				}
			});
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					changed = true;
					setType(PROJECT);
					validateAll();
				}
			};
			projectFolderText.addListener(SWT.Activate, listener);
			projectBrowseButton.addListener(SWT.Selection, listener);

			listener = new Listener() {
				public void handleEvent(Event event) {
					changed = true;
					setType(FILESYSTEM);
					validateAll();
				}
			};
			filesystemFolderText.addListener(SWT.Activate, listener);
			filesystemBrowseButton.addListener(SWT.Selection, listener);

			newRemoteButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IConnectionPoint result = createNewRemoteConnection();
					if (result != null) {
						remotesViewer.setInput(ConnectionPointUtils.getRemoteConnectionPoints());
						remotesViewer.setSelection(new StructuredSelection(result), true);
						changed = true;
					}
					validateAll();
				}
			});
			
			projectBrowseButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IContainer container = (IContainer) ((IStructuredSelection) projectViewer.getSelection()).getFirstElement();
					if (container == null) {
						container = ResourcesPlugin.getWorkspace().getRoot();
					}
					IContainer result = browseWorkspace(container, Path.fromPortableString(projectFolderText.getText()));
					if (result != null) {
						projectViewer.setSelection(new StructuredSelection(result.getProject()), true);
						projectFolderText.setText(result.getProjectRelativePath().toPortableString());
						changed = true;
					}
					validateAll();
				}
			});
			
			filesystemBrowseButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IPath path = browseFilesystem(Path.fromPortableString(filesystemFolderText.getText()));
					if (path != null) {
						filesystemFolderText.setText(path.toPortableString());
						changed = true;
					}
					validateAll();
				}
			});
		}
		
		private void setType(int type) {
			remoteRadio.setSelection(false);
			projectRadio.setSelection(false);
			filesystemRadio.setSelection(false);
			switch (type) {
			case REMOTE:
				remoteRadio.setSelection(true);
				break;
			case PROJECT:
				projectRadio.setSelection(true);
				break;
			case FILESYSTEM:
				filesystemRadio.setSelection(true);
				break;
			}
		}
		
		private void setTarget(IConnectionPoint connectionPoint) {
			if (ConnectionPointUtils.isRemote(connectionPoint)) {
				setType(REMOTE);
				remotesViewer.setSelection(new StructuredSelection(connectionPoint), true);
			} else if (ConnectionPointUtils.isWorkspace(connectionPoint)) {
				setType(PROJECT);
				IResource resource = (IResource) connectionPoint.getAdapter(IResource.class);
				projectViewer.setSelection(new StructuredSelection(resource.getProject()), true);
				projectFolderText.setText(resource.getProjectRelativePath().toPortableString());
			} else if (ConnectionPointUtils.isLocal(connectionPoint)) {
				setType(FILESYSTEM);
				File file = (File) connectionPoint.getAdapter(File.class);
				filesystemFolderText.setText(Path.fromOSString(file.getAbsolutePath()).toPortableString());
			} else {
				setType(-1);
			}
		}
		
		private IConnectionPoint getTarget() {
			if (remoteRadio.getSelection()) {
				return (IConnectionPoint) ((IStructuredSelection) remotesViewer.getSelection()).getFirstElement();
			} else if (projectRadio.getSelection()) {
				IProject project = (IProject) ((IStructuredSelection) projectViewer.getSelection()).getFirstElement();
				IPath path = Path.fromPortableString(projectFolderText.getText());
				IContainer container = (IContainer) project.findMember(path);
				IConnectionPoint connectionPoint = ConnectionPointUtils.findConnectionPoint(EFSUtils.getFileStore(container).toURI());
				if (connectionPoint == null) {
					connectionPoint = ConnectionPointUtils.createWorkspaceConnectionPoint(container);
				}
				return connectionPoint;
			} else if (filesystemRadio.getSelection()) {
				IPath path = Path.fromPortableString(filesystemFolderText.getText());
				IConnectionPoint connectionPoint = ConnectionPointUtils.findConnectionPoint(EFSUtils.getFileStore(path.toFile()).toURI());
				if (connectionPoint == null) {
					connectionPoint = ConnectionPointUtils.createLocalConnectionPoint(path);
				}
				return connectionPoint;
			}
			return null;
		}
		
		private String validate() {
			if (remoteRadio.getSelection()) {
				IConnectionPoint connectionPoint = (IConnectionPoint) ((IStructuredSelection) remotesViewer.getSelection()).getFirstElement();
				if (connectionPoint == null) {
					return "Please specify a remote connection";
				}
			} else if (projectRadio.getSelection()) {
				IProject project = (IProject) ((IStructuredSelection) projectViewer.getSelection()).getFirstElement();
				if (project == null) {
					return "Please specify a project";
				}
				IPath path = Path.fromPortableString(projectFolderText.getText());
				if (!(project.findMember(path) instanceof IContainer)) {
					return "Please specify a valid project folder";
				}
			} else if (filesystemRadio.getSelection()) {
				IPath path = Path.fromPortableString(filesystemFolderText.getText());
				if (!path.toFile().isDirectory()) {
					return "Please specify a valid local filesystem location";
				}
			} else {
				return StringUtils.format("Please specify {0} type", name.toLowerCase());
			}
			return null;
		}

	}

}
