/**
 * Compact the contents of the specified editor. Note this only currently
 * supports JavaScript
 *
 * @constructor
 * @param {Editor} editor
 *		The editor to compact
 */
function Compactor(lexemes, language, lineDelimiter) {
	this._lexemes = lexemes;
	this._language = language;
	this._eol = lineDelimiter;
	
	this._index = 0;
	this._currentLexeme;
	
	this._buffer = [];
	
	this._lastIsDelim = true;
	this._atLineStart = true;
	this._lastWasAssign = false;
	this._nesting = 0;
}

/**
 * Add the content of the current lexeme to the output buffer. This method
 * determines if the previous and current lexeme require a space between them.
 * If the space is required, that is auto-added.
 *
 * This method will automatically advance to the next lexeme. 
 */
Compactor.prototype.addLexeme = function() {
	if (this._currentLexeme != null) {
		var isDelim = this.isDelimiter();
		
		if (this._atLineStart == false && this._lastIsDelim == false && isDelim == false) {
			this.addText(" ");
		}
		
		this._buffer.push(this._currentLexeme.getText());
		
		this._atLineStart = false;
		this._lastIsDelim = isDelim;
		this._lastWasAssign = (this._currentLexeme.typeIndex == JSTokenTypes.EQUAL);
		
		this.nextLexeme();
	}
};

/**
 * Add the specified text to the output buffer
 */
Compactor.prototype.addText = function(text) {
	this._buffer.push(text);
};

/**
 * Compact the content of the editor associated with this object
 */
Compactor.prototype.compact = function() {
	this._index = 0;
	
	this.nextLexeme();
	
	while (this._currentLexeme != null) {
		this.processLexeme();
	}
};

/**
 * Determines if the current lexeme is self-delimiting. Self-delimiting lexemes
 * do not require space between it and its neighboring lexemes.
 *
 * @return {Boolean} Returns true if the current lexeme is self-delimiting
 */
Compactor.prototype.isDelimiter = function() {
    var index = this._currentLexeme.getCategoryIndex();
    var type = this._currentLexeme.typeIndex;
	var result = true;
	
	if (index == TokenCategories.IDENTIFIER || index == TokenCategories.KEYWORD) {
		result = false;
	} else if (index == TokenCategories.LITERAL && (type != JSTokenTypes.STRING)) {
		result = false;
	}
	
	return result;
};

/*
 * Advance to the next lexeme. If we fall off the end of the lexeme list, then
 * return null
 *
 * @return {Lexeme} Returns the next lexeme or null
 */
Compactor.prototype.nextLexeme = function() {
	var result = null;
	
	if (this._index < this._lexemes.size()) {
		result = this._lexemes.get(this._index++);
	}
	
	return this._currentLexeme = result;
	
};

/**
 * Process a function declaration or literal. This method includes logic to
 * determine if a semicolon needs to be auto-added after a function literal
 */
Compactor.prototype.processFunction = function() {
	if (this._nesting == 0 && this._buffer.length != 0 && this._lastWasAssign == false) {
		this.addText(this._eol);
		this._atLineStart = true;
	}
	
	// advance over 'function'
	this.addLexeme();
	
	// determine if we have a function declaration or function literal
	var isDeclaration = this._currentLexeme.typeIndex == JSTokenTypes.IDENTIFIER;
	
	// advance until '{'
	while (this._index < this._lexemes.length && this._currentLexeme.typeIndex != JSTokenTypes.LCURLY) {
		this.processLexeme();
	}
	
	// process '{'
	if (this._index < this._lexemes.length) {
		this.processLexeme();
	}
	
	// remember current nesting level
	var myNesting = this._nesting;
	
	// advance until '}'
	while (this._index < this._lexemes.length && (this._currentLexeme.typeIndex != JSTokenTypes.RCURLY || this._nesting != myNesting)) {
		this.processLexeme();
	}
	
	// process '}'
	if (this._index < this._lexemes.length) {
		this.processLexeme();
		
		// test for trailing ';'
		if (isDeclaration == false && this.isDelimiter() == false) {
			this.addText(";");
		}
	}
};

/**
 * Process the current lexeme. This method keeps track of the current nesting
 * level and fires processFunction as it encounters FUNCTION lexemes.
 */
Compactor.prototype.processLexeme = function() {
	var lexeme = this._currentLexeme;
	var type = lexeme.typeIndex;
	
	if (lexeme.getLanguage() == this._language && type != JSTokenTypes.COMMENT) {
		if (type == JSTokenTypes.FUNCTION) {
			this.processFunction();
		} else {
			if (type == JSTokenTypes.LCURLY) {
				this._nesting++;
			} else if (type == JSTokenTypes.RCURLY) {
				this._nesting--;
			}
			
			this.addLexeme();
		}
	} else {
		this.nextLexeme();
	}
};

/**
 * Convert the output buffer to a string
 *
 * @return {String} Returns the contents of the output buffer
 */
Compactor.prototype.toString = function() {
	return this._buffer.join("");
};
