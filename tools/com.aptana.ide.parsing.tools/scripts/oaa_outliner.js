/*
 * Menu: OAA > OAA Outline
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
var language = "text/oaa";

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
		trace("OAA outliner deactivated");
		
		// unregister providers here
		// NOTE: using try/catch until everyone has upgraded to build 15379+
		try
		{
			op.removeProviders(language);
		}
		catch (e)
		{
			// fail silently
		}
		
		// clear references
		labelProvider = null;
		treeContentProvider = null;
	}
	else
	{
		trace("OAA outliner activated");
		
		// create interfaces
		labelProvider = new Packages.org.eclipse.jface.viewers.ILabelProvider(
			new LabelProvider()
		);
		treeContentProvider = new Packages.org.eclipse.jface.viewers.ITreeContentProvider(
			new TreeContentProvider()
		);
		
		// register providers
		op.setProviders(language, labelProvider, treeContentProvider, true);
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
		var name = "elements.png";
		
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
		result = element.getText() + "";
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
		
		if (type == "IDENTIFIER")
		{
			result.push(lexeme);
			index = TreeContentProvider.findEnd(lexemes, index);
		}
		else
		{
			index++;
		}
	}
	
	result.sort(TreeContentProvider.lexemeSorter);
	
	return result;
};

/**
 * lexemeSorter
 * 
 * @param {Object} a
 * @param {Object} b
 */
TreeContentProvider.lexemeSorter = function(a, b)
{
	result = 0;
		
	if (a.getText() < b.getText())
	{
		result = -1;
	}
	else
	{
		result = 1;
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
	var result = index + 1;
	var lookFor = null;
	
	// find lparen or lcurly
	while (result < lexemes.size() && lookFor == null)
	{
		var lexeme = lexemes.get(result);
		var type = lexeme.getType() + "";
		
		switch (type)
		{
			case "LPAREN":
				lookFor = "RPAREN";
				break;
				
			case "LCURLY":
				lookFor = "RCURLY";
				break;
		}
		
		result++;
	}
	
	if (lookFor != null)
	{
		// look for match
		while (result < lexemes.size())
		{
			var lexeme = lexemes.get(result);
			var type = lexeme.getType() + "";
			
			if (type == lookFor)
			{
				break;
			}
			
			result++;
		}
	}
	
	if (result < lexemes.size())
	{
		// advance
		result++;
	}
	
	return result;
};

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
	
	if (parentElement !== null)
	{
		var lexemes = getLexemeList();
		var index = lexemes.getLexemeIndex(parentElement) + 1;
		var lookFor = null;
		
		// find lparen or lcurly
		while (index < lexemes.size() && lookFor == null)
		{
			var lexeme = lexemes.get(index);
			var type = lexeme.getType() + "";
			
			switch (type)
			{
				case "LPAREN":
					lookFor = "RPAREN";
					break;
					
				case "LCURLY":
					lookFor = "RCURLY";
					break;
			}
			
			index++;
		}
		
		if (lookFor != null)
		{
			// look for match
			while (index < lexemes.size())
			{
				var lexeme = lexemes.get(index);
				var type = lexeme.getType() + "";
				
				if (type == "IDENTIFIER")
				{
					result.push(lexeme);
				}
				else if (type == lookFor)
				{
					break;
				}
				
				index++;
			}
		}
	}
	
	result.sort(TreeContentProvider.lexemeSorter);
	
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
	return null;
};

/**
 * hasChildren
 * 
 * @param {Object} element
 * @reulst {Boolean}
 */
TreeContentProvider.prototype.hasChildren = function(element)
{
	return true;
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