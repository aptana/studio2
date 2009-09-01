/**
 * Lexeme.js
 *
 * Adding this file to your Active Libraries will give you code assist for the
 * Aptana Studio scripting engine.
 *
 * @author Kevin Lindsey
 * @version 1.0
 */

/**
 * Create a new instance of Lexeme. Currently, this Java class is being exposed
 * via LexemeList. Future versions of this object will be re-organized to be
 * more consistent and JavaScript-like
 * 
 * @constructor
 */
function Lexeme() {}

/*
 * Properties
 */

/**
 * Get the type index for this lexeme. Type indexes are defined for each
 * supported language in separate JavaScript objects, for example,
 * JSTokenTypes, HTMLTokenTypes, CSSTokenTypes, and XMLTokenTypes
 * 
 * @type {Number} The token type index for this lexeme
 */
Lexeme.prototype.typeIndex = 0;

/**
 * Get the length of the text in this lexeme.
 * 
 * @type {Number} The length of the text within this lexeme
 */
Lexeme.prototype.length = 0;

/*
 * Methods
 */

/**
 * Get the category index for this lexeme. Category indexes are defined for
 * each supported language in separate JavaScript objects, for example,
 * JSTokenCategories, HTMLTokenCategories, CSSTokenCategories,
 * XMLTokenCategories
 * 
 * @type {Number} The token category for this lexeme
 */
Lexeme.prototype.getCategoryIndex = 0;

/**
 * Get the language MIME type for this lexeme
 * 
 * @type {String} The language MIME type
 */
Lexeme.prototype.getLanguage = "";

/**
 * Get the text in this lexeme
 * 
 * @type {String} The text in this lexeme
 */
Lexeme.prototype.getText = "";

/**
 * Get the offset in the source where this lexeme starts
 * 
 * @type {Number} The beginning offset of this lexeme in the source file
 */
Lexeme.prototype.getStartingOffset = 0;

/**
 * Get the offset in the source immediately following where this lexeme ends
 * 
 * @type {Number} The ending offset of this lexeme in the source file.
 */
Lexeme.prototype.getEndingOffset = 0;

