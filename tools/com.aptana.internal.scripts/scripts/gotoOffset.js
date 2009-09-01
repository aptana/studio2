/*
 * Menu: Experimental > Go to offset
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

function main()
{
	var offset = prompt("Offset Please", 0);
	var activeEditor = editors.activeEditor;
	
	if (activeEditor)
	{
		editors.activeEditor.selectAndReveal(offset - 0, 0);
		out.println("Moved cursor to offset: " + offset);
	}
	else
	{
		out.println("There is no active editor");
	}
}
