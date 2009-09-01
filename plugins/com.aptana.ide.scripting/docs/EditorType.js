/**
 * EditorType.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * An object that represents a broad class of editors based on MIME type
 *
 * @constructor
 * @extends {EventTarget}
 */
function EditorType() {}

/*
 * Properties
 */

/**
 * Returns the MIME type for the class of editors this object represents
 *
 * @type {String} The editor MIME type
 */
EditorType.prototype.type = "";

