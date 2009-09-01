/**
 * externalEditor.js
 * 
 * @author Kevin Lindsey
 * @version 1.0
 */

/*
 * Globals
 */
var actionSetName = "Shell";
var actionName = "Open in External Editor";
var actionPath;

/**
 * Initialize this script and register action and action set
 */
function onload() {
	var os = getProperty("os.name");
		
	if (os.indexOf("Windows") == 0) {
		var actionsView = views.actionsView;
		var actionSet = actionsView.createActionSet(actionSetName);
		var action = actionSet.addAction(actionName, location);
		
		actionPath = action.getPath() + "";
		
		actionsView.addEventListener("ActionsExecuteEvent", onExecute);
	}
}

/**
 * unload this script and remove action and action set
 */
function onunload() {
	var os = getProperty("os.name");
		
	if (os.indexOf("Windows") == 0) {
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
	
	var activeEditor = editors.activeEditor;
	
	if (typeof(activeEditor) != "undefined") {
		var path = activeEditor.file.absolutePath;
		var os = getProperty("os.name");
		
		if (os.indexOf("Windows") == 0) {
			execute("notepad \"" + path + '"');
		} else if (os.indexOf("Mac OS X") == 0) {
			execute("open \"" + path + '"');
		} else {
			
		}
	}
}
