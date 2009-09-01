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
package com.aptana.ide.core;

import java.io.File;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Robin
 *
 */
public class WorkspaceSaveParticipant implements ISaveParticipant 
{

	/**
	 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
	 */
	public void doneSaving(ISaveContext context) 
	{
        AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();

        // delete the old saved state since it is not necessary anymore
        int previousSaveNumber = context.getPreviousSaveNumber();
        String oldFileName = "save-" + Integer.toString(previousSaveNumber); //$NON-NLS-1$
        File f = plugin.getStateLocation().append(oldFileName).toFile();
        if(f != null)
        {
            f.delete();
        }
	}

	/**
	 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
	 */
	public void prepareToSave(ISaveContext context) throws CoreException 
	{
	}

	/**
	 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
	 */
	public void rollback(ISaveContext context) 
	{        
		AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();

	    // since the save operation has failed, delete the saved state we have just written
	    int saveNumber = context.getSaveNumber();
	    String saveFileName = "save-" + Integer.toString(saveNumber); //$NON-NLS-1$
	    File f = plugin.getStateLocation().append(saveFileName).toFile();
	    f.delete();
	}

	/**
	 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
	 */
	public void saving(ISaveContext context) throws CoreException 
	{
        AptanaCorePlugin plugin = AptanaCorePlugin.getDefault();
        // save the plug-in state
        int saveNumber = context.getSaveNumber();
        String saveFileName = "save-" + Integer.toString(saveNumber); //$NON-NLS-1$
        try {
            File f = plugin.getStateLocation().append(saveFileName).toFile();
            // if we fail to write, an exception is thrown and we do not update
            // the path
            plugin.writeState(f);
            context.map(new Path("save"), new Path(saveFileName)); //$NON-NLS-1$
            context.needSaveNumber();
        } catch (NullPointerException e) {
            IdeLog.logError(plugin, Messages.WorkspaceSaveParticipant_ERR_FailedToSave);
        }
	}
}
