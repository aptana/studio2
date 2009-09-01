/*
 * Menu: Experimental > Expand Outline
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

function main()
{
    try
	{
        var editor = editors.activeEditor.textEditor;
		
        editor.getOutlinePage().getTreeViewer().expandAll();
    }
	catch (e)
	{
	}
}
