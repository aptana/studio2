/*
 * Menu: IO > File Sample
 * Kudos: Paul Colton, Aptana, Inc.
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
  
function main() 
{
	var date = new Date();

	// Write out new file
	var file = new File("myFile.txt");
	file.createNewFile();
	file.write("Date: " + date);
	
	// Read lines from file
	var text = file.readLines();
	
	// Display what we read
	Packages.org.eclipse.jface.dialogs.MessageDialog.openInformation( 	
		window.getShell(), 	
		"File I/O", 
		"Contents: " + text	
	);
	
}
