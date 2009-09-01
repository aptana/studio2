/*
 * Menu: Aptana > Colorize as HTML
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

// includes
include("lib/IDE_Utils.js");
include("SpanManager.js");

// globals
var languageMap = {
	"text/jscomment" : "text/javascript"
};
var colorizers = {};

/**
 * main
 */
function main()
{
	var filename = getFilename();
	
	if (filename !== null)
	{
		this.processFile(new File(filename));
	}
	else
	{
		err.println("Unable to retrieve filename from active editor.");
	}
}

/**
 * processFile
 * 
 * @param {File} file
 */
function processFile(file)
{
	var lexemes = getLexemeList();
	
	if (lexemes)
	{
		// grab the source so we can find text between lexemes
		var source = this.getSourceProvider().getSource();
		
		// create a new span manager to create our final HTML
		var spanManager = new SpanManager(source);
		
		// process each lexeme
		for (var i = 0; i < lexemes.size(); i++)
		{
			var lexeme = lexemes.get(i);
			var style = getStyle(lexeme);
			
			if (style)
			{
				spanManager.addLexeme(lexeme, style);
			}
			else
			{
				style = {
					getForegroundColor: function () {
						return {
							getRed: function () { return 0; },
							getBlue: function () { return 0; },
							getGreen: function () { return 0; }
						}
					},
					isBold: function() { return false; }
				};
				spanManager.addLexeme(lexeme, style);
			}
		}
		
		// create HTML
		var html = spanManager.getHTML();
		
		// write HTML to file
		var parentPath = file.parentFile.absolutePath;
		var targetFileName = parentPath + File.separator + file.name + ".html";
		
		writeAndShow(targetFileName, html);
	}
	else
	{
		err.println("No lexeme list for current editor");
	}
}

/**
 * getStyle
 * 
 * @param {Lexeme} lexeme
 * @param {ColorizationStyle}
 */
function getStyle(lexeme)
{
	var language = lexeme.getLanguage();
	var result = null;
	
	// map jscomment to javascript
	if (languageMap.hasOwnProperty(language))
	{
		language = languageMap[language];
	}
	
	// make sure we have a colorizer for this lexeme's language
	if (colorizers.hasOwnProperty(language) == false)
	{
		colorizers[language] = getLanguageColorizer(language);
	}

	// get this lexeme's colorizer
	var colorizer = colorizers[language];
	
	if (colorizer)
	{
		var token = lexeme.getToken();
		
		// see if we have a colorizer for this specific token type
		var tc = colorizer.getTokenColorizer(token);
		var style = null;
		
		if (tc)
		{
			result = tc.getBaseColorization();
		}
		else
		{
			// we didn't have a token colorizer, so try getting a
			// colorizer for this token's group
			var category = lexeme.getCategory();
			var cc = colorizer.getCategoryColorizer(category);
			
			if (cc != null)
			{
				result = cc.getStyle();
			}
		}
	}
	else
	{
		err.println("No colorizer for " + lexeme.toString());
	}
	
	return result;
}
