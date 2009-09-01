/**
 * Executable.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 */
 
/**
 * This object collects all the information needed to create an action that
 * injects a source file into the current editor. This object is also
 * responsible for loading the content of template file from disk.
 * 
 * @constructor
 * @param {String} name
 * 		The name of the Action as it will appear in the Actions View
 * @param {String} filename
 * 		The name of the file to execute when this action executes.
 * @param {String} toolTip
 * 		The text to display when the mouse hovers over this actions entry
 * 		in the Actions View. Note that short descriptions seem to work
 * 		better than long ones.
 */
function Executable(name, filename, toolTip) {
	this.name = name;
	this.filename = filename;
	this.toolTip = toolTip;
	this.source = "";
	this.actionPath = "";
	
	this.stdout = "";
	this.stderr = "";
	this.retcode = -1;
}

/**
 * run
 * 
 * @param {String} text
 */
Executable.prototype.run = function(text) {
	var result = execute(this.filename, text);
	
	this.stdout = result.stdout;
	this.stderr = result.stderr;
	this.retcode = result.retcode;
	
	return this.stdout;
}