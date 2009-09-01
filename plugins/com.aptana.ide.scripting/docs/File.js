/**
 * File.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * This object represents a file or a directory in the file system
 *
 * @constructor
 * @param {String} name
 *		The relative or absolute path to a file or directory
 */
function File(name) {}

/*
 * Properties
 *

/**
 * Get the absolute path for this file or directory
 *
 * @type {String} Returns the absolute path to this file or directory
 */
File.prototype.absolutePath = "";

/**
 * Get the file's base name. This is the filename only without the extension.
 * If the file does not have an extension, then this will return the full
 * name
 *
 * @type {String} Returns this file's base name
 */
File.prototype.baseName = "";

/**
 * Determine if this file is readable
 * 
 * @type {Boolean} Returns true if this File is readable
 */
File.prototype.canRead = false;

/**
 * Determine if ths file is writable
 * 
 * @type {Boolean} Returns true if this File is writable
 */
File.prototype.canWrite = false;

/**
 * Determine if this file or directory exists in the file system
 *
 * @type {Boolean} Returns true if this File exists in the file system
 */
File.prototype.exists = false;

/**
 * Returns the file extension of this File
 *
 * @type {String} Returns the last instance of "." and the text after it.
 *		An empty string will be returned if no extension if found. The return
 *		value includes the '.'
 */
File.prototype.extension = "";

/**
 * Determines if thie File is a file in the file system
 *
 * @type {Boolean} Returns true if this File is a file in the file system
 */
File.prototype.isFile = false;

/**
 * Determines if this File is a directory in the file system
 *
 * @type {Boolean} Returns true if this File is a directory in the file
 *		system
 */
File.prototype.isDirectory = false;

/**
 * Returns a list of File objects for all files in the File. This is equivalent
 * to listing out all files in a directory
 *
 * @type {Array} Returns an array of File objects, one for each file and
 *		directory in this File
 */
File.prototype.list = [];

/**
 * Returns the file's name without path information
 *
 * @type {String} Returns the file's name
 */
File.prototype.name = "";

/**
 * Returns a new File object of this object's parent directory
 *
 * @type {File} Returns this file's parent File
 */
File.prototype.parentFile = {};

/**
 * Returns the character used to separate directories on the underlying OS
 *
 * @type {String} Returns the directory separator
 */
File.prototype.separator = "";

/*
 * Methods
 */

/**
 * Create a new file in the file system at the location specified by this
 * object
 *
 * @return {Boolean} Returns true if the file was created successfully.
 */
File.prototype.createNewFile = function() {};

/**
 * Return all lines from this File's text file
 * 
 * @return {Array} Returns an array of strings, one for each line in the file.
 */
File.prototype.readLines = function() {};

