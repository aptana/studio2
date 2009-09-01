/*
 * Menu: Hello > Ward
 * Kudos: Ward Cunningham & Bjorn Freeman-Benson
 * License: EPL 1.0
 */
 
function main() {
 
text = "Hello Ward\n\n";
text += "The quick brown fox jumped over the lazy dog's back.";
text += "Now is the time for all good men to come to the aid of their country."

Packages.org.eclipse.jface.dialogs.MessageDialog.openInformation( 	
	window.getShell(), 	
	"Monkey Dialog", 
	text	
	)
	
}