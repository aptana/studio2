/*
 * Menu: Editors > Open in Explorer
 * Key: M2+F4
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
	var os = getProperty("osgi.os");
	
	if (os == "win32")
	{
		var filename = getFilename();
		
		if (filename)
		{
			var file = new File(filename);
			var parentPath = file.parentFile.absolutePath;
			var systemRoot = java.lang.System.getenv("SystemRoot");
			var explorer = systemRoot + File.separator + "explorer";
			
			java.lang.Runtime.getRuntime().exec(explorer + ' "' + parentPath + '"');
		}
	}
}
