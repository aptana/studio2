/*
 * Menu: Editors > Leading Tabs to Spaces
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

include("lib/leadingTabsAndSpaces.js");

function main() {
	var editor = editors.activeEditor;
	
	if (editor !== undefined) {
		var tabWidth = 4;	//editor.tabWidth;
		var insertText = new Array(tabWidth + 1).join(" ");
		
		convert(insertText);
	}
}
