package com.aptana.ide.core.io.ingo;

import org.eclipse.core.filesystem.IFileStore;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * 
 * @author ingo
 * @remove
 */
public interface IFileStoreSync extends IFileStore {

	public String getAbsolutePath();

	public IConnectionPoint getFileManager();

	public String getRelativePath();
	
	public Boolean isLink();
	
}
