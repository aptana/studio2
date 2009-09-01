/*
 * Menu: Experimental > Show Parse Tree
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

// includes
include("lib/IDE_Utils.js");

/**
 * main
 */
function main()
{
	var filename = getFilename();
	
	if (filename != null)
	{
		var file = new File(filename);
		var parseResults = getParseResults();
		
		if (parseResults != null)
		{
			var parentPath = file.parentFile.absolutePath;
			var targetFileName = parentPath + File.separator + file.name + ".xml";
			
			writeAndShow(targetFileName, parseResults.getXML());
		}
	}
}
