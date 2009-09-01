/**
 * Editors.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * Editors
 *
 * @constructor
 */
function Editors(){}

/*
 * Properties
 */

/**
 * Get all editors currently being displayed
 *
 * @type {Array} Returns an array of Editor instances, one for each open editor
 */
Editors.prototype.all = [];

/**
 * Get the currently active editor
 *
 * @type {Editor} Returns the currently active editor or undefined if no editor is open
 */
Editors.prototype.activeEditor = {};

/*
 * Methods
 */

/**
 * Get the object that represents all editors of a given type. This is
 * typically used to register event handlers for a given event type for
 * all editors of a specified language.
 *
 * @param {String} type
 * 		The editor type to retrieve. Currently, this is the MIME type for the
 *		language the editor supports.
 * @return {EditorType} Returns the editor type for the given MIME type or
 *		undefined if no editor type exists for the given MIME type
 */
Editors.prototype.getEditorType = function(type) {};

/**
 * Open the specified filename in a new editor
 *
 * @param {String} filename
 *		The name of the file to open in the file system
 * @return {Editor} Returns a new Editor object for the newly opened editor
 */
Editors.prototype.open = function(filename) {};

