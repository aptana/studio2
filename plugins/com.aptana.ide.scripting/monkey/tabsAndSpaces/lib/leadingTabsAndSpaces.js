/**
 * leadingTabsToSpaces.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/*
 * Globals
 */
var eol = new RegExp("\r|\n|\r\n", "mg");
var nonWS = new RegExp("[^ \t]", "g");


/**
 * convert
 *
 * @param {String} insertText
 */
function convert(insertText) {
	// get active editor
	var editor = editors.activeEditor;
	
	// get tab width
	var tabWidth = 4;	//editor.tabWidth;
	
	// grab source
	var source = editor.source;
	
	// clear text buffer
	var buffer = [];
	
	// reset current character index
	var lastIndex = 0;
	
	// processing any leading whitespace at the beginning of the document
	processLeadingWhitespace();
	
	// find each line ending
	while ( eol.exec(source) != null ) {
		// add skipped text to output buffer
		buffer.push(source.substring(lastIndex, eol.lastIndex));
		
		// process leading whitespace
		lastIndex = processLeadingWhitespace(source, insertText, tabWidth, eol.lastIndex, buffer);
	}
	
	// copy any remaining text on the last line
	if (lastIndex != source.length) {
		buffer.push(source.substring(lastIndex, source.length));
	}
	
	// select all, paste
	editor.applyEdit(0, editor.sourceLength, buffer.join(""));
}

/**
 * process leading whitespace
 *
 * @param {String} source
 * @param {String} insertText
 * @param {Number} lastIndex
 * @param {String[]} buffer
 */
function processLeadingWhitespace(source, insertText, tabWidth, lastIndex, buffer) {
	// synchronize regex pattern with current character position
	nonWS.lastIndex = lastIndex;
	
	// find the first non-whitespace character on the current line
	var result = nonWS.exec(source);
	
	if (result != null ) {
		var spaceCount = 0;
		
		// process all leading whitespace characters
		for (var i = lastIndex; i < nonWS.lastIndex - 1; i++) {
			if (source.charAt(i) == '\t') {
				spaceCount = tabWidth;
			} else {
				spaceCount++;
			}
			
			// emit our leading whitespace text, if we've come to a tabwidth boundary
			if (spaceCount >= tabWidth) {
				buffer.push(insertText);
				spaceCount = 0;
			}
		}
		
		// emit trailing spaces if we didn't end on a multiple of tabwidth
		if (spaceCount != 0) {
			buffer.push(new Array(spaceCount + 1).join(" "));
		}
		
		// update character position
		lastIndex = nonWS.lastIndex - 1;
		
		// synchronize eol regex position
		eol.lastIndex = lastIndex;
	}
	
	return lastIndex;
}

/**
 * Display an error message
 */
function showError(message) {
	alert("leadingTabsToSpaces: " + message);
}