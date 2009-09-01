/*
 * Menu: Editors > JSON Outliner
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

include("IDE_Utils.js");

var labelProvider;
var treeContentProvider;

var parentPath = new File(location).parentFile.absolutePath;
var iconPath = parentPath + File.separator + "icons";
var images = {};
var language = "application/json";

/**
 * main
 */
function main()
{
	// get outline provider
	loadBundle("com.aptana.ide.editors");
	var op = Packages.com.aptana.ide.views.outline.UnifiedOutlineProvider.getInstance();
	
	if (labelProvider)
	{
		trace("JSON outliner deactivated");
		
		op.removeProviders(language);
		
		// clear references
		labelProvider = null;
		treeContentProvider = null;
	}
	else
	{
		trace("JSON outliner activated");
		
		// create interfaces
		labelProvider = new Packages.org.eclipse.jface.viewers.ILabelProvider(
			new LabelProvider()
		);
		treeContentProvider = new Packages.org.eclipse.jface.viewers.ITreeContentProvider(
			new TreeContentProvider()
		);
		
		// register providers
		op.addProviders(language, labelProvider, treeContentProvider, false);
	}
}

/***
 * createImage
 * 
 * @param {Object} path
 * @return {Image}
 */
function createImage(path)
{
	var result = null;
	
	if (images.hasOwnProperty(path))
	{
		result = images[path];
	}
	else
	{
		var input = null;
		
		try
		{
			input = new java.io.FileInputStream(path);
			
			result = Packages.org.eclipse.swt.graphics.Image(null, input);
			
			images[path] = result;
		}
		finally
		{
			if (input !== null)
			{
				try
				{
					input.close();
				}
				catch(e)
				{
				}
			}
		}
	}
	
	return result;
}


// ILabelProvider

/**
 * LabelProvider
 */
function LabelProvider()
{
}

/**
 * getImage
 * 
 * @param {Object} element
 * @return {Image}
 */
LabelProvider.prototype.getImage = function(element)
{
	var result = null;
	
	if (element !== null)
	{
		var type = element.getType() + "";
		
		if (type == "PROPERTY")
		{
			var lexemes = getLexemeList();
			var index = lexemes.getLexemeIndex(element);
			
			if (index >= 0)
			{
				index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
						
				if (index < lexemes.size())
				{
					index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
					
					if (index < lexemes.size())
					{
						element = lexemes.get(index);
						type = element.getType() + "";
					}
				}
			}
		}
		
		var name = null;
		
		switch (type)
		{
			case "FALSE":
			case "TRUE":
				name = "boolean.png";
				break;
				
			case "LBRACKET":
				name = "array-literal.png";
				break;
				
			case "LCURLY":
				name = "object-literal.png";
				break;
				
			case "NULL":
				name = "null.png";
				break;
				
			case "NUMBER":
				name = "number.png";
				break;
				
			case "REFERENCE":
				name = "reference.png";
				break;
				
			case "STRING":
				name = "string.png";
				
			default:
				break;
		}
		
		if (name !== null)
		{
			result = createImage(iconPath + File.separator + name);
		}
	}
	
	return result;
};

/**
 * getText
 * 
 * @param {Object} element
 * @return {String}
 */
LabelProvider.prototype.getText = function(element)
{
	var result = "";
	
	if (element !== null)
	{
		var type = element.getType() + "";
		
		switch (type)
		{
			case "LBRACKET":
				result = "Array";
				break;
				
			case "LCURLY":
				result = "Object";
				break;
				
			case "PROPERTY":
				result = element.getText() + "";
				result = result.substring(1, result.length - 1);
				var lexemes = getLexemeList();
				var index = lexemes.getLexemeIndex(element);
				
				if (index >= 0)
				{
					index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
					
					if (index < lexemes.size())
					{
						index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
						
						if (index < lexemes.size())
						{
							lexeme = lexemes.get(index);
							type = lexeme.getType() + "";
		
							if (type != "LCURLY" && type != "LBRACKET")
							{
								var text = lexeme.getText() + "";
								
								if (type == "STRING" || type == "REFERENCE")
								{
									text = text.substring(1, text.length - 1);
								}
								
								result = result + " : " + text;
							}
						}
					}
				}
				break;
				
			case "REFERENCE":
			case "STRING":
				result = element.getText() + "";
				result = result.substring(1, result.length - 1);
				break;
				
			default:		
				result = element.getText();
				break;
		}
	}

	return result;
};

// ITreeContentProvider

/**
 * TreeContentProvider
 */
function TreeContentProvider()
{
}

/**
 * dispose
 */
TreeContentProvider.prototype.dispose = function()
{
};

/**
 * inputChanged
 * 
 * @param {Viewer} viewer
 * @param {Object} oldInput
 * @param {Object} newInput
 */
TreeContentProvider.prototype.inputChanged = function(viewer, oldInput, newInput)
{
};

// IStructuredContentProvider

/**
 * getElements
 * 
 * @param {Object} inputElement
 * @result {Object[]}
 */
TreeContentProvider.prototype.getElements = function(inputElement)
{
	var lexemes = getLexemeList();
	var index = 0;
	var result = [];
	
	while (index < lexemes.size())
	{
		var lexeme = lexemes.get(index);
		var type = lexeme.getType() + "";
		
		switch (type)
		{
			case "FALSE":
			case "LBRACKET":
			case "LCURLY":
			case "NULL":
			case "NUMBER":
			case "STRING":
			case "TRUE":
				result.push(lexeme);
				break;
				
			default:
				break;
		}
		
		index = TreeContentProvider.findEnd(lexemes, index);
	}
	
	return result;
};

/**
 * findEnd
 * 
 * @param {LexemeList} lexemes
 * @param {Number} index
 */
TreeContentProvider.findEnd = function(lexemes, index)
{
	var lexeme = lexemes.get(index);
	var type = lexeme.getType() + "";
	var result = index;
	
	function balance(left, right)
	{
		var count = 0;
		
		while (result < lexemes.size())
		{
			lexeme = lexemes.get(result);
			type = lexeme.getType() + "";
			
			if (type == left)
			{
				count++;
			}
			else if (type == right)
			{
				count--;
				
				if (count == 0)
				{
					break;
				}
			}
			
			result++;
		}
	}
	
	switch (type)
	{
		case "LBRACKET":
			balance("LBRACKET", "RBRACKET");
			break;
			
		case "LCURLY":
			balance("LCURLY", "RCURLY");
			break;
			
		default:
			break;
	}
	
	if (result < lexemes.size())
	{
		// advance
		result++;
	}
	
	return result;
};

TreeContentProvider.skipWhitespace = function(lexemes, index)
{
	var result = index;
	
	while (result < lexemes.size())
	{
		var lexeme = lexemes.get(result);
		var type = lexeme.getType() + "";
		
		if (type == "WHITESPACE")
		{
			result++;
		}
		else
		{
			break;
		}
	}
	
	return result;
}

// ITreeContentProvider

/**
 * getChildren
 * 
 * @param {Object} parentElement
 * @return {Object[]}
 */
TreeContentProvider.prototype.getChildren = function(parentElement)
{
	var result = [];
	
	function collectChildren(closingType)
	{
		var lexemes = getLexemeList();
		var index = lexemes.getLexemeIndex(parentElement);
		
		if (index >= 0)
		{
			// advance over opening punctuator
			index++
			
			while (index < lexemes.size())
			{
				var lexeme = lexemes.get(index);
				var t = lexeme.getType();
				
				if (t == "WHITESPACE" || t == "COMMA" || t == "COMMENT")
				{
					index++;
				}
				else if (t == "PROPERTY")
				{
					
					index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
					
					if (index < lexemes.size())
					{
						index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
						
						if (index < lexemes.size())
						{
							lexeme = lexemes.get(index);
							
							result.push(lexeme);
							
							index = TreeContentProvider.findEnd(lexemes, index);
						}
					}
				}
				else if (t == closingType)
				{
					break;
				}
				else
				{
					result.push(lexeme);
					
					index = TreeContentProvider.findEnd(lexemes, index);
				}
			}
		}
	}
	
	if (parentElement !== null)
	{
		var type = parentElement.getType() + "";
		
		if (type == "PROPERTY")
		{
			index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
					
			if (index < lexemes.size())
			{
				index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
				
				if (index < lexemes.size())
				{
					parentElement = lexemes.get(index);
					type = parentElement.getType() + "";
				}
			}
		}
		
		switch (type)
		{
			case "LBRACKET":
				collectChildren("RBRACKET");
				break;
				
			case "LCURLY":
				collectChildren("RCURLY");
				break;
				
			default:
				break;
		}
	}
	
	return result;
};

/**
 * getParent
 * 
 * @param {Object} element
 * @return {Object}
 */
TreeContentProvider.prototype.getParent = function(element)
{
	return null
};

/**
 * hasChildren
 * 
 * @param {Object} element
 * @reulst {Boolean}
 */
TreeContentProvider.prototype.hasChildren = function(element)
{
	var result = false;

	function nextIsType(type)
	{
		var lexemes = getLexemeList();
		var index = lexemes.getLexemeIndex(element);
		var result = false;
		
		if (index >= 0)
		{
			index++;
			
			while (index < lexemes.size())
			{
				var lexeme = lexemes.get(index);
				var t = lexeme.getType() + "";
				
				if (t == "WHITESPACE" || t == "COMMENT")
				{
					index++;
				}
				else if (t == type)
				{
					result = true;
					break;
				}
				else
				{
					break;
				}
			}
		}
		
		return result;
	}
	
	if (element !== null)
	{
		var type = element.getType() + "";
		
		if (type == "PROPERTY")
		{
			var lexemes = getLexemeList();
			var index = lexemes.getLexemeIndex(element);
			
			if (index >= 0)
			{
				index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
				
				if (index < lexemes.size())
				{
					index = TreeContentProvider.skipWhitespace(lexemes, index + 1);
					
					if (index < lexemes.size())
					{
						element = lexemes.get(index);
						type = lexeme.getType() + "";
					}
				}
			}
		}
		
		switch (type)
		{
			case "LBRACKET":
				result = !nextIsType("RBRACKET");
				break;
				
			case "LCURLY":
				result = !nextIsType("RCURLY");
				break;
				
			default:
				break;
		}
	}
	
	return result;
};

/**
 * Entry
 * @param {Lexeme} name
 * @param {Lexeme} value
 */
function Entry(name, value)
{
	this.name = name;
	this.value = value;
}

/**
 * getStartingOffset
 */
Entry.prototype.getStartingOffset = function()
{
	return this.name.offset;
};

/**
 * getText
 */
Entry.prototype.getText = function()
{
	var result = this.name.getText() + "";
	
	result = result.substring(1, result.length - 1);
	
	var type = this.value.getType() + "";
	
	if (type != "LCURLY" && type != "LBRACKET")
	{
		var text = this.value.getText() + "";
		
		if (type == "STRING" || type == "REFERENCE")
		{
			text = text.substring(1, text.length - 1);
		}
		
		result = result + " : " + text;
	}
	
	return result;
};

/**
 * getType
 */
Entry.prototype.getType = function()
{
	return "Entry";
};

// Utility functions

/**
 * trace
 * 
 * @param {String} message
 */
function trace(message)
{
	function pad(num)
	{
		if (num < 10)
		{
			return "0" + num;
		}
		else
		{
			return num + "";
		}
	}
	
	var current = new Date();
	var hours = pad(current.getHours());
	var mins = pad(current.getMinutes());
	var secs = pad(current.getSeconds());
	var timeText = [hours, mins, secs].join(":");
	
	out.println("[" + timeText + "] " + message);
}
