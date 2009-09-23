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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.ui.UIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class SiteConnectionPropertiesWidget extends Composite {

	private ISiteConnection source;
	private Text nameText;
	
	/**
	 * @param parent
	 * @param style
	 */
	public SiteConnectionPropertiesWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		
		Label label = new Label(this, SWT.NONE);
		label.setText(StringUtils.makeFormLabel("Name"));		
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		
		nameText = new Text(this, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setText("Source");
		
		createTargets(group, false);

		group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setText("Destination");
		
		createTargets(group, true);
	}
	
	private void createTargets(Composite parent, boolean showRemote) {
		parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		
		/* row 1 */
		final Button remoteRadio = new Button(parent, SWT.RADIO);
		remoteRadio.setText("Remote");
		remoteRadio.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
	
		ComboViewer remotesViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		remotesViewer.getControl().setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote)
				.align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).create());
		remotesViewer.setContentProvider(new ArrayContentProvider());
		remotesViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		remotesViewer.setInput(ConnectionPointUtils.getRemoteConnectionPoints());
		
		Button newRemoteButton = new Button(parent, SWT.PUSH);
		newRemoteButton.setText(StringUtils.ellipsify(CoreStrings.NEW));
		newRemoteButton.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
				
		/* row 2 */
		final Button projectRadio = new Button(parent, SWT.RADIO);
		projectRadio.setText("Project");
		projectRadio.setLayoutData(GridDataFactory.swtDefaults().create());
	
		final ComboViewer projectViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
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
		
		final Text projectFolderText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		projectFolderText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		
		Button projectBrowseButton = new Button(parent, SWT.PUSH);
		projectBrowseButton.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
		projectBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());

		/* row 4 */
		final Button filesystemRadio = new Button(parent, SWT.RADIO);
		filesystemRadio.setText("Filesystem");
		filesystemRadio.setLayoutData(GridDataFactory.swtDefaults().create());

		final Text filesystemFolderText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		filesystemFolderText.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.CENTER).span(2, 1).create());

		Button filesystemBrowseButton = new Button(parent, SWT.PUSH);
		filesystemBrowseButton.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
		filesystemBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());

		
		/* -- */
		remotesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				remoteRadio.setSelection(true);
				projectRadio.setSelection(false);
				filesystemRadio.setSelection(false);
			}
		});
		newRemoteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				remoteRadio.setSelection(true);
				projectRadio.setSelection(false);
				filesystemRadio.setSelection(false);
			}
		});

		projectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				projectRadio.setSelection(true);
				remoteRadio.setSelection(false);
				filesystemRadio.setSelection(false);
				projectFolderText.setText(Path.ROOT.toPortableString());
			}
		});
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				projectRadio.setSelection(true);
				remoteRadio.setSelection(false);
				filesystemRadio.setSelection(false);
			}
		};
		projectFolderText.addListener(SWT.Activate, listener);
		projectBrowseButton.addListener(SWT.Selection, listener);

		listener = new Listener() {
			public void handleEvent(Event event) {
				filesystemRadio.setSelection(true);
				remoteRadio.setSelection(false);
				projectRadio.setSelection(false);
			}
		};
		filesystemFolderText.addListener(SWT.Activate, listener);
		filesystemBrowseButton.addListener(SWT.Selection, listener);

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
					IPath path = result.getFullPath().removeFirstSegments(result.getProject().getFullPath().segmentCount()).makeAbsolute();
					projectFolderText.setText(path.toPortableString());
				}
			}
		});
		
		filesystemBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPath path = browseFilesystem(Path.fromPortableString(filesystemFolderText.getText()));
				if (path != null) {
					filesystemFolderText.setText(path.toPortableString());
				}
			}
		});
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
	
	public void setSource(ISiteConnection source) {
		this.source = source;
	}

}
