/*
 * Menu: Experimental > Load Metadata
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

// includes
include("lib/IDE_Utils.js");

// globals
var USER_AGENT = "Aptana Studio";

/**
 * main
 */
function main()
{
	// get source
	var source = getSource();
	
	if (source)
	{
		// make sure we have access to the JS editor bundle
		loadBundle("com.aptana.ide.editor.js");
		
		// get reference to JS environment
		var env = Packages.com.aptana.ide.editor.js.JSLanguageEnvironment.getInstance().getJSEnvironment();
		
	    // create native objects reader
		var reader = new Packages.com.aptana.ide.editor.scriptdoc.parsing.reader.NativeObjectsReader2(env);
		
		// set user agent
		reader.setUserAgent(USER_AGENT);
		
		// create input stream
		var input = new Packages.java.io.ByteArrayInputStream(source.getBytes());
		
		// load stream 
		reader.loadXML(input);
		
		// close stream
		input.close();
		
		out.println("Metadata loaded");
	}
}

/**
 * getSource
 * 
 * @return {String}
 */
function getSource()
{
	var filename = getFilename();
	var source = null;
	
	if (filename)
	{
		if (filename.match(/\.xml$/i))
		{
			var sourceProvider = getSourceProvider();
			
			if (sourceProvider)
			{
				source = sourceProvider.getSource();
			}
			else
			{
				out.println("No source provider for " + filename);
			}
		}
		else
		{
			out.println("Metadata file must be an XML file");
		}
	}
	else
	{
		out.println("Unable to get active editor's file name");
	}
	
	return source;
}
