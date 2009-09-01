/**
 * Global.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */
 
/*
 * Properties
 */

/**
 * Retrieve the Editors object to access editors and editor events
 *
 * @type {Editors} Returns the global Editors object
 */
var editors = {};

/**
 * Retrieve the error print stream.
 *
 * @type {PrintStream} Returns the output stream used to display errors
 */
var err = {};

/**
 * This is a reference to the only instance of this object. All scripts run in
 * their own protected scope. However, this Global is accessible from all
 * scripts. Properties placed on "global" will be accessible to all scripts
 *
 * @type {Global} Returns a reference to the global scope
 */
var global = {};

/**
 * Retrieve the Menus object to access menus and menu events
 *
 * @type {Menus} Returns the global Menus object
 */
var menus = {};

/**
 * Retrieve the standard output print stream.
 *
 * @type {PrintStream} Returns the standard output stream
 */
var out = {};

/**
 * Retrieve the View object to access views and view events
 *
 * @type {Views} Returns the global Views object
 */
var views = {};

/*
 * Methods
 */

/**
 * Display an alert dialog with the given message
 *
 * @param {String} message
 *		The message to display in the dialog
 */
var alert = function(message) {};

/**
 * Execute a string in the current shell. This is experimental and may be
 * removed in a future version of the scripting environment
 *
 * @param {String} command
 *		The command to execute in the shell
 * @return {Object} Returns an object with the following properties: code,
 *		stdout, stderr. Code is the return code from the command. Stdout
 *		contains any text that was emitted to standard out while it was
 *		executing. Likewise, stderr contains any errors that were emitted.
 */
var execute = function(command) {};

/**
 * Call Java's System.getProperty.
 *
 * @param {String} property
 *		The name of the property to retrieve
 * @return {String} Returns the specified property value or the string
 *		"undefined" if the property does not exist
 */
var getProperty = function(property) {};

/**
 * Include a JavaScript file into the current script's scope. this is used to 
 * load dependent libraries into the script that invokes this function.
 * 
 * @param {String} filename
 * 		The name of the file to include in the script
 */
var include = function(filename) {};

/**
 * Load a library into the scripting environment. Each script loaded with this
 * function will be assigned a unique ID and, if it exists, the init() function
 * will be invoked. This gives each script the ability to initialize itself and
 * to setup any event listeners it wishes to subscribe to.
 *
 * Each script will exist in its own scope; however, this Global is also
 * included in the scope chain. All variables and functions defined in the
 * script will not collide with any other scripts.
 *
 * Shared properties can be placed on the "global" property. All scripts loaded
 * via this function will then be able to see those properties. This can be
 * used to share data between scripts.
 *
 * @param {String} filename
 *		The file system path to the script to load
 * @return {String} Returns a unique string identifier for the loaded script.
 *		This identifier can be used later to invoke functions within the
 *		script; however, this is more for internal use at this point. If the
 *		script fails to load, this will return undefined.
 */
var loadLibrary = function(filename) {};

