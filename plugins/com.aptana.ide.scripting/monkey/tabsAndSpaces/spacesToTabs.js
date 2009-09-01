/*
 * Menu: Editors > Leading Spaces to Tabs
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

include("lib/leadingTabsAndSpaces.js");

function main() {
	var editor = editors.activeEditor;
	
	if (editor !== undefined) {
		var insertText = "\t";
		
		convert(insertText);
	}
}
