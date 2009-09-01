/*
 * Menu: HTML > Wrap Selection In Tag
 * Kudos: Ingo Muschenetz (Aptana, Inc.)
 * Key:M3+INSERT
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

function main() {
 
	var sourceEditor = editors.activeEditor;
    var defaultTag = "div" // default value to place in prompt
	
	// make sure we have an editor
	if (sourceEditor === undefined) {
		valid = false;
		showError("No active editor");
	}
    else	
    {
			var range = sourceEditor.selectionRange;
			var offset = range.startingOffset;
			var deleteLength = range.endingOffset - range.startingOffset;
			var source = sourceEditor.source;
			
			var selection = source.substring(range.startingOffset, range.endingOffset);
			var wrap = prompt("Tag to wrap selection with (i.e. 'div'):", defaultTag);
            if(wrap != undefined)
            {
    			selection = "<" + wrap + ">" + selection + "</" + wrap + ">";
    			
    			// apply edit and reveal in editor
    			sourceEditor.applyEdit(offset, deleteLength, selection);
    			sourceEditor.selectAndReveal(offset, selection.length);
            }
	}
}