/*
 * Menu: Experimental > Show Lexemes
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
		var lexemeList = getLexemeList();
		
		if (lexemeList != null && lexemeList.size() > 0)
		{
			var parentPath = file.parentFile.absolutePath;
			var targetFileName = parentPath + File.separator + file.name + ".txt";
			var lines = [];
					
			for (var i = 0; i < lexemeList.size(); i++)
			{
				lines.push(lexemeList.get(i).toString());
			}
			
			writeAndShow(targetFileName, lines.join("\n"));
		}
	}
}
