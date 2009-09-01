/**
 * EventTarget.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * This object is the base implementation for all objects that can fire events
 *
 * @constructor
 */
function EventTarget() {}

/*
 * Methods
 */

/**
 * Add a new event listener for the given event type
 *
 * @param {String} type
 *		The event type
 * @param {Function} handler
 *		The event handler that will fire for the given event type
 *.
EventTarget.prototype.addEventListener = function(type, handler) {};

/**
 * Remove a previously added event listener from this object
 *
 * @param {String} type
 *		The event type
 * @param {Function} handler
 *		The event handler that will fire for the given event type
 */
EventTarget.prototype.removeEventListener = function(type, handler) {};

