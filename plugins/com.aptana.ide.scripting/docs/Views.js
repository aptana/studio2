/**
 * Views.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * This object is used to access various views in the user interface
 *
 * @constructor
 */
function Views() {}

/*
 * Properties
 */

/**
 * Retrieve all views in the user interface
 *
 * @type {Array} Returns an array of View objects, one for each view.
 */
Views.prototype.all = [];

/**
 * Retrieve the Active Libraries view
 *
 * @type {ProfilesView} Returns a View object for the Active Libraries
 *		view
 */
Views.prototype.profilesView = {};

/**
 * Retrieve the Navigator view in the user interface
 * 
 * @type {ResourceNavigator} Returns the navigator object
 */
Views.prototype.navigatorView = {};

/**
 * Retrieve the Problems View view
 *
 * @type {ProblemsView} Returns a View object for the Problems View
 */
Views.prototype.problemsView = {};

/*
 * Methods
 */

/**
 * Retrieve a view for the given id
 *
 * @param {String} id
 *		The Eclipse id for the view to retrieve
 * @return {View} Returns a View object for the view with the given id
 */
Views.prototype.getView = function(id) {};

