/*
 * Menu: Editors > Comment Lines
 * Kudos: Paul Colton (Aptana, Inc.)
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
 
var comment = "//";
var commentLength = comment.length;

function main() {
 
	var editor = editors.activeEditor;
 
	// get range of lines in the selection (or at the cursor position)
	var range = editor.selectionRange;
	var startLine = editor.getLineAtOffset(range.startingOffset);
	var endLine = editor.getLineAtOffset(range.endingOffset);
		
	// determine if we're adding or removing comments
	var source = editor.source;
	var offset = editor.getOffsetAtLine(startLine);
	var addComment = (source.substring(offset, offset + commentLength) != comment);
	var adjust = 0;
		
	editor.beginCompoundChange();
		
	if (addComment) {
		for (var i = startLine; i <= endLine; i++) {
			var offset = editor.getOffsetAtLine(i);
			editor.applyEdit(offset, 0, comment);
		}
	} else {
		for (var i = startLine; i <= endLine; i++) {
			var offset = editor.getOffsetAtLine(i);

			if (source.substring(offset + adjust, offset + adjust + commentLength) == comment) {
				editor.applyEdit(offset, commentLength, "");
				adjust += commentLength;
			}
		}
	}
		
	editor.endCompoundChange();
}
