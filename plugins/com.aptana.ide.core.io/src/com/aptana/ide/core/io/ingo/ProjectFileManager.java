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
package com.aptana.ide.core.io.ingo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.resources.IProjectProvider;

/**
 * A project-specific file manager
 * 
 * @author Ingo Muschenetz
 * @author Max Stepanov
 */
public class ProjectFileManager extends LocalConnectionPoint implements IVirtualFileManager, IProjectProvider
{

	public ProjectFileManager(ProtocolManager protocolManager) {
		// TODO Auto-generated constructor stub
	}

//	public ProjectFileManager(ProjectProtocolManager projectProtocolManager) {
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public void addCloakExpression(String fileExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCloakedFile(IFileStore file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IVirtualFileManager cloneManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsFile(IVirtualFile file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createLocalDirectory(IVirtualFile directoryFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IVirtualFile createVirtualDirectory(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFile createVirtualFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public IVirtualFile getBaseFile() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public String getBasePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCloakedFileExpressions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFile[] getCloakedFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescriptiveLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFileManagerEventHandler getEventHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileTimeString(IVirtualFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFile[] getFiles(IVirtualFile file)
			throws  IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFile[] getFiles(IVirtualFile file, boolean recurse,
			boolean includeCloakedFiles) throws 
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroup(IVirtualFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHashString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VirtualFileManagerGroup getManagerGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNickName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOwner(IVirtualFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProtocolManager getProtocolManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getStream(IVirtualFile file) throws  IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasFiles(IVirtualFile file) throws 
			IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAutoCalculateServerTimeOffset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFileCloaked(IVirtualFile file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHidden() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveFile(IFileStore source, IFileStore destination) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void putFile(IVirtualFile clientFile, IVirtualFile targetServerFile,
			IFileProgressMonitor iFileProgressMonitor) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void putFile2(IVirtualFile sourceFile, IVirtualFile targetFile,
//			IFileProgressMonitor monitor) throws 
//			VirtualFileManagerException, IOException {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void putStream(InputStream input, IVirtualFile targetFile)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putStream(InputStream input, IVirtualFile targetFile,
			IFileProgressMonitor monitor) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCloakExpression(String fileExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCloakedFile(IFileStore file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean renameFile(IFileStore file, String newName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetTimeOffsetCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolveBasePath() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutoCalculateServerTimeOffset(boolean calculateOffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBasePath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCloakedFiles(IVirtualFile[] files) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEventHandler(IVirtualFileManagerEventHandler eventHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroup(IVirtualFile file, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHidden(boolean hidden) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setManagerGroup(VirtualFileManagerGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNickName(String nickName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOwner(IVirtualFile file, String ownerName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPoolSizes(int initialSize, int maxSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeOffset(long timeOffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransient(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void fromSerializableString(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toSerializableString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileSeparator() {
		// TODO Auto-generated method stub
		return File.separator;
	}

	public static IVirtualFile convertResourceToFile(Object project) {
		// TODO Auto-generated method stub
		return null;
	}

	public static IVirtualFile[] convertResourcesToFiles(
			Object[] selectedObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFile(IFileStore file) throws 
			CoreException {
		// TODO Auto-generated method stub
		return false;
	}

//	//private static Image fSiteErrorImage = null;
//	private static IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//	private IContainer baseContainer;
//
//	/**
//	 * Static items
//	 */
////	static
////	{
////		ImageDescriptor desc = CoreUIPlugin.getImageDescriptor("icons/error.png"); //$NON-NLS-1$
////		if (desc != null)
////		{
////			fSiteErrorImage = desc.createImage();
////		}
////	}
//
//	/**
//	 * A file manager specific to projects
//	 * 
//	 * @param protocolManager
//	 */
//	public ProjectFileManager(ProtocolManager protocolManager)
//	{
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFileManager#getImage()
//	 */
////	public Image getImage()
////	{
////		if (getBasePath() == null)
////		{
////			return fSiteErrorImage;
////		}
////		else
////		{
////			return super.getImage();
////		}
////	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFileManager#getBasePath()
//	 */
//	public String getBasePath()
//	{
//		if (baseContainer != null && baseContainer.getLocation() != null) {
//			return baseContainer.getLocation().toOSString();
//		}
//		return super.getBasePath();
//	}
//
//	/**
//	 * getRelativePath
//	 * 
//	 * @return String
//	 */
//	public String getRelativePath()
//	{
//		if (baseContainer != null && baseContainer.getLocation() != null) {
//			return baseContainer.getFullPath().toPortableString();
//		}
//		return super.getBasePath();
//	}
//
//	/**
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#setBasePath(java.lang.String)
//	 */
//	public void setBasePath(String pathString)
//	{
//		try
//		{
//			Path path = new Path(pathString);
//			baseContainer = workspaceRoot.getContainerForLocation(path);
//			if (baseContainer == null) {
//				if (path.segmentCount() == 1) {
//					baseContainer = workspaceRoot.getProject(path.segment(0));
//				} else {
//					baseContainer = workspaceRoot.getFolder(path);
//				}				
//			}
//			if (baseContainer != null) {
//				pathString = baseContainer.getFullPath().toOSString();
//			}
//		}
//		catch (IllegalStateException e)
//		{
//			// workspace is closed
//		}
//		//super.setBasePath(pathString);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFileManager#getHashString()
//	 */
//	public String getHashString()
//	{
//		String result = ""; //$NON-NLS-1$
//
//		result += this.getNickName() + ISerializableSyncItem.DELIMITER;
//		result += this.getRelativePath() + ISerializableSyncItem.DELIMITER;
//		result += this.getId() + ISerializableSyncItem.DELIMITER;
//		result += this.isAutoCalculateServerTimeOffset() + ISerializableSyncItem.DELIMITER;
//		try
//		{
//			result += this.getTimeOffset() + ISerializableSyncItem.DELIMITER;
//		}
//		catch (ConnectionException e)
//		{
//			result += 0 + ISerializableSyncItem.DELIMITER;
//		}
//		result += (this.serializeCloakedFiles(getCloakedFiles()) + ISerializableSyncItem.DELIMITER);
//		result += (StringUtils.join(ISerializableSyncItem.FILE_DELIMITER, getCloakedFileExpressions()) + ISerializableSyncItem.DELIMITER);
//
//		return result;
//	}
//
//	/**
//	 * Converts a list of IResources to IVirtualFiles[]
//	 * 
//	 * @param objects
//	 * @return IVirtualFile[]
//	 */
//	public static IVirtualFile[] convertResourcesToFiles(Object[] objects)
//	{
//		List<IVirtualFile> newFiles = new ArrayList<IVirtualFile>();
//
//		for (int i = 0; i < objects.length; i++)
//		{
//			IVirtualFile file = convertResourceToFile(objects[i]);
//
//			if (file != null)
//			{
//				newFiles.add(file);
//			}
//		}
//
//		return newFiles.toArray(new IVirtualFile[newFiles.size()]);
//	}
//
//	/**
//	 * convertResourceToFile
//	 * 
//	 * @param object
//	 * @return - IVirtualFile
//	 */
//	public static IVirtualFile convertResourceToFile(Object object)
//	{
//		IVirtualFile result = null;
//
//		if (object != null && object instanceof IResource)
//		{
//			IResource resource = (IResource) object;
//			ProjectFileManager tempManager = (ProjectFileManager) ProjectProtocolManager.getInstance().createTemporaryFileManager(true);
//			tempManager.setBasePath(resource.getProject().getLocation().toOSString());
//			result = new ProjectFile(tempManager, resource);
//		}
//
//		return result;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFileManager#cloneManager()
//	 */
//	public IVirtualFileManager cloneManager()
//	{
//		ProjectFileManager manager = new ProjectFileManager(this.getProtocolManager());
//		manager.setId(getId());
//		manager.setBasePath(this.getBasePath());
//		manager.setCloakedFiles(this.getCloakedFiles());
//		//manager.setImage(this.getImage());
//		//manager.setDisabledImage(this.getDisabledImage());
//		manager.setTransient(this.isTransient());
//		return manager;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#fromSerializableString(java.lang.String)
//	 */
//	public void fromSerializableString(String s)
//	{
//		super.fromSerializableString(s);
//
//		// If we're an errant file manager, don't add us.
//		if (getBasePath() == null)
//		{
//			this.getProtocolManager().removeFileManager(this);
//		}
//	}
//
//	public IProject getProject()
//	{
//		if (baseContainer != null) {
//			return baseContainer.getProject();
//		}
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#getFiles(com.aptana.ide.core.io.IVirtualFile, boolean, boolean)
//	 */
//	@Override
//	public IVirtualFile[] getFiles(IVirtualFile file, boolean recurse, boolean includeCloakedFiles) {
//		if (file instanceof ProjectFile) {
//			ArrayList<IVirtualFile> list = new ArrayList<IVirtualFile>();
//			IResource resource = ((ProjectFile) file).getResource();
//			try {
//				getFiles(resource, recurse, list, includeCloakedFiles);
//			} catch (CoreException e) {
//				//IdeLog.logImportant(FilePlugin.getDefault(), "getFiles failed", e);
//			}
//			return list.toArray(new IVirtualFile[0]);
//		}
//		return super.getFiles(file, recurse, includeCloakedFiles);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#containsFile(com.aptana.ide.core.io.IVirtualFile)
//	 */
//	@Override
//	public boolean containsFile(IVirtualFile file) {
//		if (file instanceof ProjectFile && baseContainer != null) {
//			return baseContainer.getFullPath().isPrefixOf(((ProjectFile) file).getResource().getFullPath());
//		}
//		//return super.containsFile(file);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#createVirtualDirectory(java.lang.String)
//	 */
//	@Override
//	public IVirtualFile createVirtualDirectory(String pathString) {
//		return createVirtualFile(pathString);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#createVirtualFile(java.lang.String)
//	 */
//	@Override
//	public IVirtualFile createVirtualFile(String pathString) {
//		// XXX What about pre-pending the base path? Should clients really have to set base path and then build all the paths themselves?
//		Path path = new Path(pathString);
//		if (Path.ROOT.equals(path) ) {
//			return new ProjectFile(this, workspaceRoot);
//		}
//		IResource resource = null;
//		File file = path.toFile();
//		if (file.exists()) {
//			try {
//				if (file.isDirectory()) {
//					resource = workspaceRoot.getContainerForLocation(path);
//				} else {
//					resource = workspaceRoot.getFileForLocation(path);
//				}
//			} catch (Exception e) {
//			}
//		}
//		if (resource == null) {
//			try {
//				resource = path.segmentCount() == 1 ?
//						workspaceRoot.getProject(path.segment(0)) : 
//							workspaceRoot.findMember(path);
//			} catch (Exception e) {
//			}
//		}
//		if (resource != null && resource.getLocation() != null) {
//			return new ProjectFile(this, resource);
//		}
//		//IdeLog.logInfo(FilePlugin.getDefault(), "ProjectFileManager: failed to resolve path: "+pathString);
//		return super.createVirtualFile(pathString);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.aptana.ide.core.ui.io.file.LocalFileManager#getBaseFile()
//	 */
//	@Override
//	public IVirtualFile getBaseFile() {
//		if (baseContainer != null && baseContainer.getLocation() != null) {
//			return new ProjectFile(this, baseContainer);
//		}
//		return super.getBaseFile();
//	}
//
//	/**
//	 * getFiles
//	 * 
//	 * @param resource
//	 * @param recurse
//	 * @param list
//	 * @throws CoreException 
//	 */
//	private void getFiles(IResource resource, boolean recurse, List<IVirtualFile> list, boolean includeCloakedFiles) throws CoreException {
//		// fire event
//		this.fireGetFilesEvent(resource.getLocation().toOSString());
//		
//		if (resource instanceof IContainer) {
//			IContainer container = (IContainer) resource;
//			if (!container.isSynchronized(IResource.DEPTH_ONE)) {
//				container.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
//			}
//			IResource[] children = container.members();
//			IVirtualFile localFile;
//			boolean addingFile;
//			for (int i = 0; i < children.length; i++)
//			{
//				IResource child = children[i];
////				if (CoreUIPlugin.getDefault().getPreferenceStore().getBoolean(
////						com.aptana.ide.core.ui.preferences.IPreferenceConstants.PREF_FILE_EXPLORER_SHOW_COMPRESSED)
////						&& (child.getName().endsWith(".zip") || child.getName().endsWith(".jar") || child.getName() //$NON-NLS-1$ //$NON-NLS-2$
////								.endsWith(".gz"))) //$NON-NLS-1$
////				{
////					localFile = new CompressedFile(this, child.getLocation().toFile());
////				}
////				else
////				{
//					if (child.getLocation() == null) {
//						continue;
//					}
//					localFile = new ProjectFile(this, child);
////				}
//
//				addingFile = false;
//				if (includeCloakedFiles || !localFile.isCloaked())
//				{
//					list.add(localFile);
//					addingFile = true;
//				}
//
//				if (recurse && child instanceof IContainer && addingFile)
//				{
//					getFiles(child, recurse, list, includeCloakedFiles);
//				}
//			}
//			
//		}
//		
//	}
//
//	@Override
//	public void addCloakExpression(String fileExpression) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void addCloakedFile(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void cancel() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void connect() throws ConnectionException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean createLocalDirectory(IVirtualFile directoryFile)
//			throws  VirtualFileManagerException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean deleteFile(IVirtualFile file) throws 
//			VirtualFileManagerException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void disconnect() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public String[] getCloakedFileExpressions() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IVirtualFile[] getCloakedFiles() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getDescriptiveLabel() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IVirtualFileManagerEventHandler getEventHandler() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getFileTimeString(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IVirtualFile[] getFiles(IVirtualFile file)
//			throws  IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getGroup(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public VirtualFileManagerGroup getManagerGroup() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getNickName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getOwner(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ProtocolManager getProtocolManager() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public InputStream getStream(IVirtualFile file) throws 
//			VirtualFileManagerException, IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public long getTimeOffset() throws ConnectionException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public boolean hasFiles(IVirtualFile file) throws 
//			IOException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isAutoCalculateServerTimeOffset() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isEditable() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isFileCloaked(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isHidden() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isTransient() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isValid() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean moveFile(IVirtualFile source, IVirtualFile destination) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void putFile(IVirtualFile sourceFile, IVirtualFile targetFile)
//			throws  VirtualFileManagerException,
//			IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void putFile(IVirtualFile clientFile, IVirtualFile targetServerFile,
//			IFileProgressMonitor iFileProgressMonitor) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void putFile2(IVirtualFile sourceFile, IVirtualFile targetFile,
//			IFileProgressMonitor monitor) throws 
//			VirtualFileManagerException, IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void putStream(InputStream input, IVirtualFile targetFile)
//			throws  VirtualFileManagerException,
//			IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void putStream(InputStream input, IVirtualFile targetFile,
//			IFileProgressMonitor monitor) throws 
//			VirtualFileManagerException, IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void putToLocalFile(IVirtualFile file, File tempFile)
//			throws  VirtualFileManagerException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void refresh() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void removeCloakExpression(String fileExpression) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void removeCloakedFile(IVirtualFile file) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean renameFile(IVirtualFile file, String newName)
//			throws  VirtualFileManagerException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void resetTimeOffsetCache() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void resolveBasePath() throws 
//			VirtualFileManagerException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setAutoCalculateServerTimeOffset(boolean calculateOffset) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setCloakedFiles(IVirtualFile[] files) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setEventHandler(IVirtualFileManagerEventHandler eventHandler) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setGroup(IVirtualFile file, String groupName) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setHidden(boolean hidden) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setId(long id) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setManagerGroup(VirtualFileManagerGroup group) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setNickName(String nickName) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setOwner(IVirtualFile file, String ownerName) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setPoolSizes(int initialSize, int maxSize) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setTimeOffset(long timeOffset) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setTransient(boolean value) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int compareTo(Object o) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public String toSerializableString() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
