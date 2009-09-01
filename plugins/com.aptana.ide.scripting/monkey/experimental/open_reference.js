/*
 * Menu: Editors > Open Reference
 * Key: M2+F3
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
	
	if (filename)
	{
		var editor = editors.activeEditor;
		var lexemeList = getLexemeList();
		
		if (lexemeList)
		{
			var selectionRange = editor.selectionRange;
			var offset = selectionRange.startingOffset;
			var lexeme = lexemeList.getLexemeFromOffset(offset);
			
			if (lexeme)
			{
				var text = lexeme.getText() + "";
				
				if (text && text.length > 0)
				{
					// removing leading and trailing quotes
					text = text.replace(/^["'].+["']$/, function (a) { return a.substring(1, a.length - 1); });
					
					var file = new File(text);
					
					if (file.exists == false)
					{
						var parent = new File(filename).parentFile.absolutePath;
						
						file = new File(parent + File.separator + text);
					}
					
					if (file.exists)
					{
						fileUtils.open(file.absolutePath);
					}
				}
			}
		}
	}
}
