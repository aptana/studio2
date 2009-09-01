/**
 * 
 */
package com.aptana.ide.core.io;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * A virtual file manager dialog interface that enhance the previous one with the ability to create a UI content
 * independently.
 * 
 * @author Shalom Gibly
 * @since Aptana Studio 1.2.4
 */
public interface IVirtualFileManagerDialog2 extends IVirtualFileManagerDialog
{
	/**
	 * Creates the UI contents.
	 * 
	 * @param parent
	 */
	public void createContents(Composite parent);
	
	/**
	 * Creates the 'Advanced Properties' contents.
	 * 
	 * @param parent
	 */
	public void createAdvancedProperties(Composite parent);

	/**
	 * Returns the parent Shell for this dialog
	 * 
	 * @return The parent Shell
	 */
	public Shell getParent();

	/**
	 * Returns the IVirtualFileManager item.
	 * 
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager getItem();

	/**
	 * Returns true if the IVirtualFileManager that was set to this dialog is a new one.
	 * 
	 * @return true if the IVirtualFileManager that was set to this dialog is a new one; false, otherwise
	 */
	public boolean isNewItem();
}
