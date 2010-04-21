package com.aptana.ide.core.io.ingo;

import java.io.File;

import org.eclipse.core.internal.resources.SaveManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.core.AptanaCorePlugin;

/**
 * The SyncDataWriter is a class that imitate {@link WorkspaceSaveParticipant}, however, it maintains the version of the saved file
 * and make sure a rollback is possible if needed.
 * @author Shalom Gibly
 */
public class SyncDataWriter
{
	private static final SaveManager SAVE_MANAGER = ((Workspace) ResourcesPlugin.getWorkspace()).getSaveManager();

	/**
	 * Tells this writer that the save operation is now
	 * complete and it is free to go about its normal business.
	 * Exceptions are not expected to be thrown at this point, so they
	 * should be handled internally.
	 */
	public void doneSaving() 
	{
		AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();
		// Since there is a chance that the save number will change during the workspace life-cycle, we do not
		// cache this number.
		int saveNumber = SAVE_MANAGER.getSaveNumber(plugin.ID);
        // delete the old saved state since it is not necessary anymore
        String fileName = "save-" + Integer.toString(saveNumber); //$NON-NLS-1$
        String newFileName = fileName + "_new"; //$NON-NLS-1$
        File newFile = plugin.getStateLocation().append(newFileName).toFile();
        File oldFile = plugin.getStateLocation().append(fileName).toFile();
        if (newFile != null && newFile.exists()) {
        	if(oldFile != null)
            {
        		oldFile.delete();
            }
        	newFile.renameTo(oldFile);
        }
        
	}

	/**
	 * Tells this writer to rollback the written data.
	 */
	public void rollback() 
	{        
		AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();
		int saveNumber = SAVE_MANAGER.getSaveNumber(plugin.ID);
	    // since the save operation has failed, delete the saved state we have just written
	    String saveFileName = "save-" + Integer.toString(saveNumber) + "_new"; //$NON-NLS-1$ //$NON-NLS-2$
	    File f = plugin.getStateLocation().append(saveFileName).toFile();
	    f.delete();
	}

	/**
	 * Tells this writer to save its important state.
	 */
	public void saving() throws CoreException 
	{
        AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();
        int saveNumber = SAVE_MANAGER.getSaveNumber(plugin.ID);
        String saveFileName = "save-" + Integer.toString(saveNumber) + "_new"; //$NON-NLS-1$ //$NON-NLS-2$
        File f = plugin.getStateLocation().append(saveFileName).toFile();
        // if we fail to write, an exception is thrown and we do not update the path
        // TODO: Fix plugin writing
        //plugin.writeState(f);
	}
}
