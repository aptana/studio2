package com.aptana.ide.search.epl.filesystem.text;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.search.core.text.TextSearchMatchAccess;

/**
 * A {@link FileTextSearchMatchAccess} gives access to a pattern match found by
 * the {@link FileTextSearchEngine}.
 * <p>
 * Please note that <code>{@link FileTextSearchMatchAccess}</code> objects <b>do
 * not </b> have value semantic. The state of the object might change over time
 * especially since objects are reused for different call backs. Clients shall
 * not keep a reference to a {@link FileTextSearchMatchAccess} element.
 * </p>
 * <p>
 * This class should only be implemented by implementors of a
 * {@link FileTextSearchEngine}.
 * </p>
 */
public abstract class FileTextSearchMatchAccess extends TextSearchMatchAccess {

    /**
     * Returns the file the match was found in.
     * 
     * @return the file the match was found.
     */
    public abstract File getFileSystemFile();

    public IFile getFile() {
        return null;
    }
}
