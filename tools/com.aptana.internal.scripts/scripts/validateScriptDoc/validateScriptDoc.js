/**
 * validateScriptDoc.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/*
 * Globals
 */
var actionSetName = "Internal Testing";
var actionName = "Validate JavaScript SDoc";
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
		
		action.setToolTipText("Validate JS file against SDoc");
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
	var sdocFile;
	var errorFile;
	
	// make sure there's an active editor
	if (editor === undefined) {
		valid = false;
		showError("no active editor");
	}
	
	// make sure we're in the right language
	if (valid && editor.language != language) {
		valid = false;
		showError("Can only check comments from JavaScript files");
	}
	
	if (valid) {
		// get the underlying File object for the current editor
		var file = editor.file;
		
		// calculate the new file name
		var parentFile = file.parentFile.absolutePath;
		var newName = parentFile + File.separator + file.baseName + ".sdoc";
		var errName = parentFile + File.separator + file.baseName + ".err";

		sdocFile = new File(newName);
		errorFile = new File(errName);
		
		if (sdocFile.exists == false) {
			valid = false;
			showError("This JavaScript file does not have an associated sdoc file");
		}
	}
	
	if (valid) {
		ids = {};
		
		// grab source
		source = editor.source;
		
		var commentRegex = /\/\*\*(?:[^*]|\*[^\/]|[\r\n])*\*\//gm;
		var commentMatch = commentRegex.exec(source);
		
		while (commentMatch != null)
		{
			var comment = commentMatch[0];
			var idRegex = /@id\s+(\S+)/;
			var idMatch = idRegex.exec(comment);
			
			if (idMatch != null) {
				ids[idMatch[1]] = "a";
			}
			
			commentMatch = commentRegex.exec(source);
		}
		
		source = sdocFile.readLines().join("\r\n");
		
		commentRegex = /\/\*\*(?:[^*]|\*[^\/]|[\r\n])*\*\//gm;
		commentMatch = commentRegex.exec(source);
		
		while (commentMatch != null) {
			var comment = commentMatch[0];
			var idRegex = /@id\s+(\S+)/;
			var idMatch = idRegex.exec(comment);
			
			if (idMatch != null) {
				var id = idMatch[1];
				
				if (ids.hasOwnProperty(id)) {
					ids[id] += "b";
				} else {
					ids[id] = "b";
				}
			}
			
			commentMatch = commentRegex.exec(source);
		}
		
		// create a sorted list of all ids
		var keys = [];
		
		for (var p in ids) {
			keys.push(p);
		}
		
		keys.sort();
		
		// show missing ids
		var notInJS = [];
		var notInSDoc = [];
		
		for (var i = 0; i < keys.length; i++) {
			var id = keys[i];
			
			switch (ids[id]) {
				case "a":
					notInSDoc.push(id);
					break;
					
				case "b":
					notInJS.push(id);
					break;
					
				case "ab":
					break;
					
				default:
					err.println("unknown state: " + ids[id]);
			}
		}
		
		// create new file, if needed
		if (errorFile.exists == false && errorFile.createNewFile() == false) {
			showError("Unable to create: " + errorFile.absolutePath);
			valid = false;
		}
		
		// make sure we have a writable file
		if (valid && errorFile.exists && errorFile.canWrite == false) {
			showError("File is not writable: " + errorFile.absolutePath);
			valid = false;
		}
		
		if (valid) {
			var allLines = [];
			
			if (notInJS.length > 0) {
				allLines.push("IDs that do not exist in the JavaScript File");
				allLines.push("============================================");
				
				allLines = allLines.concat(notInJS);
				
				if (notInSDoc.length > 0) {
					allLines.push("");
					allLines.push("IDs that do not exist in the SDoc File");
					allLines.push("======================================");
					
					allLines = allLines.concat(notInSDoc);
				}
			} else if (notInSDoc.length > 0) {
				allLines.push("IDs that do not exist in the SDoc File");
				allLines.push("======================================");
				
				allLines = allLines.concat(notInSDoc);
			}
			
			if (allLines.length > 0)
			{
				// open target file
				var targetEditor = editors.open(errorFile.absolutePath);
				
				// clear and write text
				targetEditor.applyEdit(0, 0, allLines.join("\r\n"));
			}
			else
			{
				alert("documents match");
			}
		}
	}
}

/**
 * Display an error message
 */
function showError(message) {
	err.println("extractScriptDoc: " + message);
}
