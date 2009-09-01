/**
 * Editor.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * Editor
 *
 * @constructor
 * @extends {EventTarget}
 */
function Editor() {}

/*
 * Properties
 */

/**
 * Get/set the position of the cursor in this editor
 * 
 * @type {Number} The current cursor offset position
 */
Editor.prototype.currentOffset = 0;

/**
 * Get the File object that this editor is editing
 *
 * @type {File} Returns a File object or undefined
 */
Editor.prototype.file = {};

/**
 * Get the language MIME type for this editor
 * 
 * @type {String} Returns this editors language type
 */
Editor.prototype.language = "";

/**
 * Get the lexemes associated with this editor
 *
 * @type {Array} Returns an array of Lexemes
 */
Editor.prototype.lexemes = [];

/**
 * Get the line delimiter for this editor
 * 
 * @type {String} Returns the editor's line terminator
 */
Editor.prototype.lineDelimiter = "";

/**
 * Get the source associated with this editor
 *
 * @type {String} Returns the source text in this editor
 */
Editor.prototype.source = "";

/**
 * Get the length of the source in this editor
 * 
 * @type {Number} Returns the number of characters in this editor's document
 */
Editor.prototype.sourceLength = 0;

/**
 * Get the number of columns in a tab
 * 
 * @type {Number} Returns the number of spaces that equal one tab
 */
Editor.prototype.tabWidth = 0;

/**
 * Get the zero-based line number of the line at the top of the editor
 * 
 * @type {Number} The top-most line's index
 */
Editor.prototype.topIndex = 0;

/**
 * Get/set the editor's word wrap setting. Setting this to true turns on word
 * wrapping.
 * 
 * @type {Boolean} The word wrap setting.
 */
Editor.prototype.wordWrap = false;

/*
 * Methods
 */
 
/**
 * Apply an edit to the current document. This function allows you to delete
 * and insert text in one operation, if desired.
 *
 * @param {Number} offset
 *		The offset within the source where this edit is to take place
 * @param {Number} deleteLength
 *		The number of characters to remove before inserting the new text
 * @param {String} insertText
 *		The new text to insert at the given offset
 */
Editor.prototype.applyEdit = function(offset, deleteLength, insertText) {};

/**
 * Get the zero-based line number at the specified character offset
 * 
 * @param {Number} offset
 * 		The character offset within the editor's document
 */
Editor.prototype.getLineAtOffset = function(offset) {};

/**
 * Scroll the editor to bring the current selection or caret position into
 * view.
 */
Editor.prototype.showSelection = function() {};
