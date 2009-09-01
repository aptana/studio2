/**
 * LexemeList.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * Create a new instance of LexemeList. Items within this list can be accessed
 * via array indexes. All elements are read-only.
 * 
 * @constructor
 */
function LexemeList() {}

/*
 * Fields
 */

/**
 * Get the number of items in this list
 * 
 * @type {Number} Returns the number of lexemes in this list
 */
LexemeList.prototype.length = 0;

/*
 * Methods
 */

/**
 * Retrieve the lexeme at the specified offset.
 * 
 * @param {Number} offset
 * 		The offset within the source document
 * @return {Lexeme} Returns the lexeme at the specified offset. If no lexeme
 * 		exists at that location, then return null
 */
LexemeList.prototype.getLexeme = function(offset) {};

/**
 * Retrieve the lexeme at the specified offset or the one to the right of the offset.
 * 
 * @param {Number} offset
 * 		The offset within the source document
 * @return {Lexeme} Returns the lexeme at the specified offset. If no lexeme
 * 		exists at that location, then lexeme immediately following the offset
 * 		will be returned. Returns null if no lexeme is at the offset or to the
 * 		right of the offset.
 */
LexemeList.prototype.getCeilingLexeme = function(offset) {};

/**
 * Retrieve the lexeme at the specified offset or the one to the left of the offset.
 * 
 * @param {Number} offset
 * 		The offset within the source document
 * @return {Lexeme} Returns the lexeme at the specified offset. If no lexeme
 * 		exists at that location, then lexeme immediately preceeding the offset
 * 		will be returned. Returns null if no lexeme is at the offset or to the
 * 		left of the offset.
 */
LexemeList.prototype.getFloorLexeme = function(offset) {};

/**
 * Retrieve the index of the specified lexeme.
 * 
 * @param {Number} offset
 * 		The offset within the source document
 * @return {Lexeme} Returns the lexeme at the specified offset. If no lexeme
 * 		exists at that location, then return -1
 */
LexemeList.prototype.getLexemeIndex = function(lexeme) {};

