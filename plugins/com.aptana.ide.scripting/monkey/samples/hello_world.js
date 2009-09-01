/*
 * Menu: Samples > Hello World
 * Kudos: Ingo Muschenetz, Aptana, Inc. 
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
 
function main() {
 
text = "Hello World";

Packages.org.eclipse.jface.dialogs.MessageDialog.openInformation( 	
	window.getShell(), 	
	"Aptana Scripting Dialog", 
	text	
	)
	
}