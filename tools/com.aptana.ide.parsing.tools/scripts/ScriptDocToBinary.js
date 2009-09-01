/*
 * Menu: Conversion > ScriptDoc to Binary
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

include("IDE_Utils.js");

function main()
{
	try
	{
		var inputFilename = getFilename();
		
		if (inputFilename !== null)
		{
			if (inputFilename.match(/\.xml$/)) {
				var outputFilename = inputFilename.replace(/resources/, "parsing").replace(/xml$/, "bin");
				
				loadBundle("com.aptana.ide.parsing");
				loadBundle("com.aptana.ide.editor.js");
				
				var File = Packages.java.io.File;
				var FileInputStream = Packages.java.io.FileInputStream;
				var FileOutputStream = Packages.java.io.FileOutputStream;
				var Environment = Packages.com.aptana.ide.editor.js.runtime.Environment;
				var NativeObjectsReader = Packages.com.aptana.ide.editor.scriptdoc.parsing.reader.NativeObjectsReader;
				var TabledOutputStream = Packages.com.aptana.ide.io.TabledOutputStream;
				
				var environment = new Environment();
				environment.initBuiltInObjects();
				var reader = new NativeObjectsReader(environment);
				var inputFile = new File(inputFilename);
				var input = new FileInputStream(inputFile);
				
				out.println("Reading documentation: " + inputFilename);
				reader.loadXML(input);
				
				//checkFile(outputFilename);
				
				var binaryFile = new File(outputFilename);
				var outputStream = new FileOutputStream(binaryFile);
				var output = new TabledOutputStream(outputStream);
				
				reader.getScriptDoc().write(output);
				
				output.close();
				
				refresh(outputFilename);
				
				out.println("  Output file updated: " + outputFilename);
			}
			else
			{
				out.println("Unable to convert the active editor because it does not appear to be a scriptdoc metadata xml file.")
				out.println("Please open a scriptdoc metadata xml file and make that the active editor before running this script.");
			}
		}
		else
		{
			out.println("Unable to retrieve the filename from active editor.");
		}
	}
	catch (e)
	{
		out.println("The following error occurred while converting the scriptdoc metadata file");
		out.println(e.toString());
	}
}
