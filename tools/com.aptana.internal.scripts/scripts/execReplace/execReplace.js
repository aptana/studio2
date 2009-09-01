/**
 * execReplace.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/*
 * Includes
 */
include("Executable.js");

/*
 * Globals
 */
var actionSetName = "Shell";
var executables = [
	new Executable(
		"Perl Reverse Text",
		"perl D:\\reverse.pl",
		"Reverse Text"
	)
];
var actionPaths = {};


/**
 * Initialize this script.
 */
function onload() {
	if (getProperty("aptana.testing") == "on")
	{
		var actionsView = views.actionsView;
		var actionSet = actionsView.createActionSet(actionSetName);
		
		for (var i = 0; i < executables.length; i++) {
			var executable = executables[i];
			var action = actionSet.addAction(executable.name, location);
		
			action.setToolTipText(executable.toolTip);
			actionPaths[action.getPath()] = executable;
		}
		
		// listen to execute events
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
			for (var i = 0; i < executables.length; i++) {
				var executable = executables[i];
				
				actionSet.removeAction(executable.name);
			}
			
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
	var path = evt.actions[0].path;
	
	if (actionPaths.hasOwnProperty(path) == false) {
		return;
	}
	
	var valid = true;
	var language = "text/html";
	var sourceEditor = editors.activeEditor;
	
	// make sure we have an editor
	if (sourceEditor === undefined) {
		valid = false;
		showError("No active editor");
	}
	
	// make sure we're in the right language
	if (valid && sourceEditor.language != language) {
		valid = false;
		showError("Can only inject into HTML files");
	}
	
	// compact
	if (valid) {
		var offset = sourceEditor.currentOffset;
		var executable = actionPaths[path];
		var range = sourceEditor.selectionRange;
		var source = sourceEditor.source;
		var text = source.substring(range.startingOffset, range.endingOffset);
		
		if (typeof(text) == "string") {
			var offset = range.startingOffset;
			var deleteLength = range.endingOffset - range.startingOffset;
			var newText = executable.run(text);
			
			sourceEditor.applyEdit(offset, deleteLength, newText);
		}
	}
}

/**
 * Display an error message
 */
function showError(message) {
	alert("execReplace: " + message);
}
