/**
 * Event.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * An object that encapsulates state information for a given event
 *
 * @constructor
 */
function Event() {}

/*
 * Properties
 */

/**
 * Returns the object that fired the event
 *
 * @type {EventTarget} The event firing object
 */
Event.prototype.target = {};

/**
 * Returns the type of event that was fired
 *
 * @type {String} The event type
 */
Event.prototype.type = "";

