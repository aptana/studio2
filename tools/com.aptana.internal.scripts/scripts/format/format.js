/**
 * format.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/*
 * Globals
 */
var actionSetName = "Internal Testing";
var actionName = "Format";
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
		
		action.setToolTipText("Format entire JS file");
		actionPath = action.getPath() + "";
		
		actionsView.addEventListener("ActionsExecuteEvent", onExecute);
	}
}

/**
 * unload this script
 */
function onunload() {
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

/**
 * onExecute
 *
 * @param {ActionsExecuteEvent} evt
 */
function onExecute(evt) {
	if (actionPath != evt.actions[0].path) {
		return;
	}
	
	var editor = editors.activeEditor;
	var valid = true;
	
	if (editor === undefined) {
		valid = false;
		showError("No current editor");
	}
	
	if (valid && editor.language != "text/javascript") {
		valid = false;
		showError("Format currently only works with JavaScript");
	}
	
	if (valid) {
		var parseTree = editor.parseResults;
		var formattedSource = parseTree.source;
		
		editor.applyEdit(0, editor.sourceLength, formattedSource);
	}
}

/**
 * Display an error message
 */
function showError(message) {
	alert("Format: " + message);
}
