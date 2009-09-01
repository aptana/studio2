package com.aptana.ide.pathtools;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;

/**
 * 
 * This implements the preferences page using the FieldEditor.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class WorkbenchPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public WorkbenchPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	public void init(IWorkbench workbench) {
		// Initialize the preference store we wish to use
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public String getDescription() {
		return "Specify the commands for exploring folders and fies. You can\n"
				+ "use \"\" (quotes) around command arguments with spaces in their value.\n"
				+ "You can use the following parameters in the commands:\n\n"
				+ PathtoolsPreferences.FILE_PATH
				+ "  - path of the selected object with default file separator.\n"
				+ PathtoolsPreferences.FILE_PARENT_PATH
				+ "  - path of the parent of selected object with default file separator.\n"
				+ PathtoolsPreferences.FILE_NAME
				+ "  - name of the selected object.\n"
				+ PathtoolsPreferences.FILE_PARENT_NAME
				+ "  - name of the parent of selected object.\n"
				+ PathtoolsPreferences.FILE_PATH_SLASHES
				+ "  - path of the selected object with / file separator.\n"
				+ PathtoolsPreferences.FILE_PARENT_PATH_SLASHES
				+ "  - path of the parent of selected object with / file separator.\n"
				+ PathtoolsPreferences.FILE_PATH_BACKSLASHES
				+ "  - path of the selected object with \\ File separator.\n"
				+ PathtoolsPreferences.FILE_PARENT_PATH_BACKSLASHES
				+ " - path of the parent of selected object with \\ file separator.\n";
	}

	@Override
	protected void createFieldEditors() {
		// Folder explore command field
		StringFieldEditor folderExploreCommad = new StringFieldEditor(
				PathtoolsPreferences.FOLDER_EXPLORE_COMMAND_KEY, "Explore Folder:",
				getFieldEditorParent());
		addField(folderExploreCommad);

		// File explore command field
		StringFieldEditor fileExploreCommad = new StringFieldEditor(
				PathtoolsPreferences.FILE_EXPLORE_COMMAND_KEY, "Explore File:",
				getFieldEditorParent());
		addField(fileExploreCommad);

		// Folder editor command field
		StringFieldEditor folderEditCommad = new StringFieldEditor(
				PathtoolsPreferences.SHELL_ON_FOLDER_COMMAND_KEY, "Shell on Folder:",
				getFieldEditorParent());
		addField(folderEditCommad);

		// File editor command field
		StringFieldEditor fileEditCommad = new StringFieldEditor(
				PathtoolsPreferences.SHELL_ON_FILE_COMMAND_KEY, "Shell on File:",
				getFieldEditorParent());
		addField(fileEditCommad);
	}

}
