/**
 * @author kevinl
 */

var entityMap = {
	"<": "&lt;",
	"&": "&amp;",
	">": "&gt;"
};


/**
 * SpanManager
 * 
 * @param {String} source
 */
function SpanManager(source)
{
	this.source = source;
	
	this.styles = {};
	this.styleValues = [];
	this.code = [];
	this.indents = [];
	this.lastPosition = 0;
	
	this.currentColorId = -1;
	this.currentBold = false;
	this.currnetIndent = 0;
	this.currentSpan = [];
}

/**
 * addLexeme
 * 
 * @param {Lexeme} text
 * @param {ColorizationStyle} style
 */
SpanManager.prototype.addLexeme = function(lexeme, style)
{
	// handle text between lexemes (typically whitespace)
	if (this.lastPosition < lexeme.offset)
	{
		var s = this.source.substring(this.lastPosition, lexeme.offset) + "";
		var self = this;
		var indent = 0;
		
		s = s.replace(
			/^\t+/mg,
			function (a)
			{
				var count = a.length;

				self.indents[count] = (count * 2) + "em";
				
				indent = count;
				
				return "";
			}
		);
		
		this.addText(s);
		
		if (indent > 0)
		{
			this.setIndent(indent);
		}
	}
	
	this.lastPosition = lexeme.offset + lexeme.length;
	
	// get lexeme text
	var text = lexeme.getText() + "";

	// entitize
	text = text.replace(
		/[<&>]/g,
		function (a)
		{
			return entityMap[a];
		}
	);
	
	// add lexeme text in new style
	this.addText(text, style);
};

/**
 * addText
 * 
 * @param {String} text
 */
SpanManager.prototype.addText = function(text, style)
{
	this.currentSpan.push(text);
	
	if (style)
	{
		var color = style.getForegroundColor();
		var id = this.getColorClassName(color);
		var bold = style.isBold();
		
		if (this.currentColorId == -1)
		{
			this.currentColorId = id;
			this.currentBold = bold;
		}
		else if (id != this.current || bold != this.currentBold)
		{
			// remove last item from span buffer since it's in a different style
			this.currentSpan.pop();
			
			this.emit();
			
			// reset state
			this.currentColorId = id;
			this.currentBold = bold;
			this.currentIndent = 0;
			this.currentSpan = [text];
		}
	}
};

/**
 * emit
 */
SpanManager.prototype.emit = function()
{
	if (this.currentSpan.length > 0)
	{
		// calculate CSS classes
		var classes = ["c" + this.currentColorId];
		
		if (this.currentIndent > 0)
		{
			classes.push("i" + this.currentIndent);
		}
		
		if (this.currentBold)
		{
			classes.push("b");
		}
		
		// calculate span text
		var buffer = this.currentSpan.join("");
		var span = "<span class='" + classes.join(" ") + "'>" + buffer + "</span>";
		
		// add to code buffer
		this.code.push(span);
		
		this.currentSpan = [];
		this.currentColorId = -1;
	}
};

/**
 * setIndent
 * 
 * @param {Number} indent
 */
SpanManager.prototype.setIndent = function(indent)
{
	this.emit();
	this.currentIndent = indent;
};

/**
 * getColorClassName
 * 
 * @param {Color} color
 * @return {Number}
 */
SpanManager.prototype.getColorClassName = function (color)
{
	var rgb =
		"rgb(" +
		[color.getRed(), color.getGreen(), color.getBlue()].join(",") +
		")";
	
	if (this.styles.hasOwnProperty(rgb) == false)
	{
		this.styles[rgb] = this.styleValues.length;
		this.styleValues.push(rgb);
	}
	
	return this.styles[rgb];
};

/**
 * getCode
 * 
 * @return {String}
 */
SpanManager.prototype.getCode = function()
{
	this.emit();
	
	return this.code.join("");
};

/**
 * getCSS
 * 
 * @return {String}
 */
SpanManager.prototype.getCSS = function()
{
	// open style element (container for our CSS)
	var css = ["<style>"];
	
	// add code class
	css.push(".code{font-size: 8pt;}");
	
	// add color classes
	for (var i = 0; i < this.styleValues.length; i++)
	{
		css.push(".c" + i + "{color: " + this.styleValues[i] + "}");
	}
	
	// add indentation classes
	if (this.indents.length > 1)
	{
		for (var i = 1; i < this.indents.length; i++)
		{
			css.push(".i" + i + "{margin-left: " + this.indents[i] + "}");
		}
	}
	
	// add bold class
	css.push(".b{font-weight: bold}");
	
	// class style element
	css.push("</style>");
	
	return css.join("\r\n");
};

/**
 * getHTML
 * 
 * @return {String}
 */
SpanManager.prototype.getHTML = function()
{
	var css = this.getCSS();
	var code = this.getCode();
	
	return css + "\r\n<pre class='code'>" + code + "</pre>";
};
