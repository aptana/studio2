/*
 * Use this as a template to assign a key command to a snippet. Create a scripts directory in your
 * project, copy this intoa  new JavaScript file, change the menu name to what you like, and assign
 * a key command, using the guidance shown here:
 * http://www.aptana.com/docs/index.php/Adding_metadata_to_an_Eclipse_Monkey_script#Key_metadata
 * 
 * Menu: Editors > Increase Font Size
 * Kudos: Ingo Muschenetz (Aptana, Inc.)
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

function main() {
		var store = Packages.org.eclipse.ui.internal.WorkbenchPlugin.getDefault().getPreferenceStore();
		font = Packages.org.eclipse.jface.resource.JFaceResources.getTextFont();
		if (font != null) {
			var data = font.getFontData();
			if (data != null && data.length > 0)
			{
				data[0].setHeight(data[0].getHeight() + 1);
				store.setValue(Packages.org.eclipse.jface.resource.JFaceResources.TEXT_FONT, 
                    Packages.org.eclipse.jface.preference.PreferenceConverter.getStoredRepresentation(data));
			}
		}

}