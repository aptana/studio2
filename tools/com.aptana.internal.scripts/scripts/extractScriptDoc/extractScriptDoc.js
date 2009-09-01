/**
 * extractScriptDoc.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */
 
/*
 * Globals
 */
var actionSetName = "Internal Testing";
var actionName = "Extract JavaScript SDoc";
var actionPath;

/**
 * Initialize this script.
 */
function onload() {
	if (getProperty("aptana.testing") == "on")
	{
		var actionsView = views.actionsView;
		var actionSet = actionsView.createActionSet(actionSetName);
		var action = actionSet.addAction(actionName, location);
		
		action.setToolTipText("Extract comments to a file");
		actionPath = action.getPath() + "";
		
		actionsView.addEventListener("ActionsExecuteEvent", onExecute);
	}
}

/**
 * unload this script
 */
function onunload() {
	if (getProperty("aptana.testing") == "on")
	{
		var actionsView = views.actionsView;
		var actionSet = actionsView.getActionSet(actionSetName);
		
		if (actionSet != null) {
			actionSet.removeAction(actionName);
			
			if (actionSet.actionCount == 0) {
				actionsView.removeActionSet(actionSetName);
			}
		}
		
		actionsView.removeEventListener("ActionsExecuteEvent", onExecute);
	}
}

/**
 * onExecute
 *
 * @param {ActionsExecuteEvent} evt
 */
function onExecute(evt) {
	if (actionPath != evt.actions[0].path) {
		return;
	}
	
	var language = "text/javascript";
	var editor = editors.activeEditor;
	var valid = true;
	var newFile;
	
	// make sure there's an active editor
	if (editor === undefined) {
		valid = false;
		showError("no active editor");
	}
	
	// make sure we're in the right language
	if (valid && editor.language != language) {
		valid = false;
		showError("Can only extract comments from JavaScript files");
	}
	
	if (valid) {
		var lastPosition = 0;
		var newSource = [];
		var comments = [];
		
		// grab source
		source = editor.source;
		
		var commentRegex = /\/\*\*(?:[^*]|\*[^\/]|[\r\n])*\*\//gm;
		var commentMatch = commentRegex.exec(source);
		
		while (commentMatch != null)
		{
			var comment = commentMatch[0];
			var commentEnd = commentRegex.lastIndex;
			var commentStart = commentEnd - comment.length;
			
			var idRegex = /@id\s+\S+/;
			var idMatch = idRegex.exec(comment);
			
			if (idMatch != null) {
				// save comment for sdoc file
				comments.push(comment);
				
				// save original source up to but not including the comment
				newSource.push(source.substring(lastPosition, commentStart));
				newSource.push("/** " + idMatch[0] + " */");
				lastPosition = commentEnd;
			}
			
			commentMatch = commentRegex.exec(source);
		}
		
		// make sure we have all the text to the end of the file
		if (lastPosition != source.length)
		{
			newSource.push(source.substring(lastPosition, source.length));
		}
		
		// output sdoc and update original source
		if (comments.length > 0) {
			// get the underlying File object for the current editor
			var file = editor.file;
			
			// calculate the new file name
			var parentFile = file.parentFile.absolutePath;
			var newName = parentFile + File.separator + file.baseName + ".sdoc";
			var newFile = new File(newName);
		
			// create new file, if needed
			if (newFile.exists == false && newFile.createNewFile() == false) {
				showError("Unable to create: " + newFile.absolutePath);
				valid = false;
			}
			
			// make sure we have a writable file
			if (valid && newFile.exists && newFile.canWrite == false) {
				showError("File is not writable: " + newFile.absolutePath);
				valid = false;
			}
			
			if (valid) {
				// open target file
				var targetEditor = editors.open(newFile.absolutePath);
				
				// clear and write text
				targetEditor.applyEdit(0, 0, comments.join("\r\n\r\n"));
			}
			
			editor.applyEdit(0, editor.sourceLength, newSource.join(""));
		}
	}
}

/**
 * Display an error message
 */
function showError(message) {
	err.println("extractScriptDoc: " + message);
}