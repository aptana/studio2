/*
 * Use this as a template to assign a key command to a snippet. Create a scripts directory in your
 * project, copy this intoa  new JavaScript file, change the menu name to what you like, and assign
 * a key command, using the guidance shown here:
 * http://www.aptana.com/docs/index.php/Adding_metadata_to_an_Eclipse_Monkey_script#Key_metadata
 * 
 * Menu: Experimental > Toggle Model View
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

var state = true;

function main()
{
	Packages.java.lang.System.setProperty("environment.outline", state.toString());
	
	out.println("environment.outline = " + state);
	
	state = !state;
}
