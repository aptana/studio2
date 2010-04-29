package com.aptana.ide.core.io.ingo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filesystem.EFS;
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
	public IVirtualFileManager getFileManager() {
		return _manager;
	}

//	@Override
//	public String getRelativePath() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public InputStream getStream(Client client) throws ConnectionException,
//			VirtualFileManagerException, IOException, CoreException {
//		InputStream input = super.openInputStream(EFS.NONE, null);
//		client.streamGot(input);
//		return input;
//	}


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


//	public File getFile() {
//		return super.file;
//	}

	public void setInternalFile(File newFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
