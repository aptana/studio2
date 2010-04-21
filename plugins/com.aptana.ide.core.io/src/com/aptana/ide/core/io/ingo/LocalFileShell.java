package com.aptana.ide.core.io.ingo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.efs.LocalFile;


public class LocalFileShell extends LocalFile implements IVirtualFile {

	IVirtualFileManager _manager;
	
	public LocalFileShell(File file) {
		super(file);
		// TODO Auto-generated constructor stub
	}

	public LocalFileShell(LocalFileManager manager, File file) {
		super(file);
		_manager = manager;
	}

	public LocalFileShell(ProjectFileManager manager, File file) {
		super(file);
		_manager = manager;
	}

	@Override
	public boolean canRead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWrite() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete() throws ConnectionException,
			VirtualFileManagerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists() throws ConnectionException {
		return super.file.exists();
	}

	@Override
	public String getAbsolutePath() {
		// TODO Auto-generated method stub
		return super.filePath;
	}

	@Override
	public long getCreationMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFileManager getFileManager() {
		return _manager;
	}

	@Override
	public IVirtualFile[] getFiles() throws ConnectionException, IOException {
		return _manager.getFiles(this, false, true);
	}

	@Override
	public IVirtualFile[] getFiles(boolean recurse, boolean includeCloakedFiles)
			throws ConnectionException, IOException {
		// TODO Auto-generated method stub
		return _manager.getFiles(this, recurse, includeCloakedFiles);
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getModificationMillis() {
		return this.file.lastModified();
	}

	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVirtualFile getParentFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPermissions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream getStream() throws ConnectionException,
			VirtualFileManagerException, IOException {
		try {
			return super.openInputStream(0, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InputStream getStream(Client client) throws ConnectionException,
			VirtualFileManagerException, IOException {
		InputStream input = getStream();
		client.streamGot(input);
		return input;
	}

	@Override
	public String getTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasFiles() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCloaked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectory() {
		return this.fetchInfo().isDirectory();
	}

	@Override
	public boolean isFile() {
		return !this.fetchInfo().isDirectory();
	}

	@Override
	public boolean isLink() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putStream(InputStream input) throws ConnectionException,
			VirtualFileManagerException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putStream(InputStream input, IFileProgressMonitor monitor)
			throws ConnectionException, VirtualFileManagerException,
			IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean rename(String newName) throws ConnectionException,
			VirtualFileManagerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCloaked(boolean cloak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroup(String group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModificationMillis(long modificationTime)
			throws IOException, ConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOwner(String owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPermissions(long permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeStamp(String timeStamp) {
		// TODO Auto-generated method stub
		
	}

	public File getFile() {
		return super.file;
	}

	public void setInternalFile(File newFile) {
		// TODO Auto-generated method stub
		
	}

}
