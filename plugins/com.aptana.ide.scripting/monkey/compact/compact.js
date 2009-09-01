/*
 * Menu: Javascript > Compact
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

include("lib/Compactor.js");

/**
 * main
 */
function main()
{
	var sourceEditor = editors.activeEditor;
	
	// make sure we have an editor
	if (sourceEditor === undefined)
	{
		showError("No active editor");
	}
	// make sure we have a JS editor
	else if (getLanguage() != "text/javascript")
	{
		showError("Can only compact JavaScript files");
	}
	// compact
	else
	{
		var lexemes = getLexemes();
	
		// make sure we have content
		if (lexemes !== null && lexemes !== undefined && lexemes.size() > 0) {
			// compact
			var compactor = new Compactor(lexemes, "text/javascript", sourceEditor.lineDelimiter);
			
			compactor.compact();
			
			// write text
			sourceEditor.applyEdit(0, sourceEditor.sourceLength, compactor.toString());
		}
	}
}

/**
 * getLanguage
 * 
 * @return {String}
 */
function getLanguage()
{
	var result = "";
	
	try
	{
		result = editors.activeEditor.textEditor.getFileContext().getDefaultLanguage();
	}
	catch(e)
	{
	}
	
	return result;
}

/**
 * getLexemes
 *
 * @return {LexemeList}
 */
function getLexemes()
{
	var result = null;
	
	try {
		var fileContext = editors.activeEditor.textEditor.getFileContext();
		
		if (fileContext !== null && fileContext !== undefined) {
			result = fileContext.getLexemeList();
		}
	} catch(e) {
	}
	
	return result;
}

/**
 * Display an error message
 */
function showError(message) {
	alert("compact: " + message);
}
