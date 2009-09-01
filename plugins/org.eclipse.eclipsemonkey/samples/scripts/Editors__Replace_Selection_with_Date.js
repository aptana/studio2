/*
 * Menu: Editors > Replace Selecton with Date
 * Kudos: Paul Colton (Aptana, Inc.)
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
 
function main() {
 
	var sourceEditor = editors.activeEditor;

	var valid = true;
	
	// make sure we have an editor
	if (sourceEditor === undefined) {
		valid = false;
		showError("No active editor");
	}
	
	// inject
	if (valid) {
	
			var range = sourceEditor.selectionRange;
			var offset = range.startingOffset;
			var deleteLength = range.endingOffset - range.startingOffset;
			var source = sourceEditor.source;
			
			var text = new Date();
			
			// apply edit and reveal in editor
			sourceEditor.applyEdit(offset, deleteLength, text);
			sourceEditor.selectAndReveal(offset, text.length);

	}
}
