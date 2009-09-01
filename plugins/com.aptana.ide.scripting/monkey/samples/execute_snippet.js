/*
 * Use this as a template to assign a key command to a snippet. Create a scripts directory in your
 * project, copy this intoa  new JavaScript file, change the menu name to what you like, and assign
 * a key command, using the guidance shown here:
 * http://www.aptana.com/docs/index.php/Adding_metadata_to_an_Eclipse_Monkey_script#Key_metadata
 * 
 * Menu: Samples > Execute Snippet
 * Kudos: Ingo Muschenetz (Aptana, Inc.)
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

function main() {

    // Change these two to match the snippet you wish to find/use
    var snippetCategory = "HTML";
    var snippetName = "Insert <form>";
 
	var sourceEditor = editors.activeEditor;
	
	// make sure we have an editor
	if (sourceEditor === undefined) {
		valid = false;
		showError("No active editor");
	}
    else
    {
            loadBundle("com.aptana.ide.snippets");
            var snippetManager = Packages.com.aptana.ide.snippets.SnippetsManager.getInstance();
            var snippets = snippetManager.getSnippetsByCategory(snippetCategory);
            var snippet = null;
            for(i = 0; i < snippets.length; i++)
            {
                snippet = snippets[i];
                if(snippet.getName() == snippetName)
                {
                    break;
                }
            }
            
            if(snippet != null)
            {
    			var range = sourceEditor.selectionRange;
    			var offset = range.startingOffset;
    			var deleteLength = range.endingOffset - range.startingOffset;
    			var source = sourceEditor.source;
    			
    			var selection = source.substring(range.startingOffset, range.endingOffset);
                var content = snippet.getExpandedContent(selection);
    			
    			// apply edit and reveal in editor
    			sourceEditor.applyEdit(offset, deleteLength, content);
    			sourceEditor.selectAndReveal(offset, content.length);
            }
	}
}