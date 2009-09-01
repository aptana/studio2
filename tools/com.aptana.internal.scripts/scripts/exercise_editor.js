/*
 * Menu: Aptana > Exercise Editor
 * Kudos: Kevin Lindsey
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */

// includes
include("lib/IDE_Utils.js");

// globals
var TRACING = true;
var TIMEOUT = 7000;
var BASENAME = "test";
var EXTENSION = ".js";
var SCRIPT_FILE = new File(location);
var SOURCE_FILE = new File(SCRIPT_FILE.parentFile.absolutePath + File.separator + "lib/dojo.js");

var counter = 0;
var file;
var source;
var id;
var firstRun = true;

/**
 * main
 */
function main()
{
	if (id == null)
	{
		trace("Execute this script again to stop processing.");
		
		if (createFile())
		{
			// grab the source we want to use for testing
			source = SOURCE_FILE.readLines().join("\r\n");
			
			// begin open/edit/save/close loop
			exerciseFile();
		}
	}
	else
	{
		clearTimeout(id);
		id = null;
		firstRun = true;
		
		trace("Exercise Editor stopped");
	}
}

/**
 * createFile
 */
function createFile()
{
	// NOTE: file is global
	file = new File(BASENAME + "_" + counter + EXTENSION);
	
	if (file.exists == false)
	{
		// create new file
		if (file.createNewFile() == false)
		{
			trace("Unable to create file: " + file.absolutePath);
		}
	}
	
	if (file.exists)
	{
		// be sure to clean up this file when we exit the IDE
		file.deleteOnExit();
	}
	
	return file.exists;
}

/**
 * exerciseFile
 */
function exerciseFile()
{
	if (firstRun == false)
	{
		var activeEditor = editors.activeEditor;
		
		// close file and delete
		activeEditor.save();
		activeEditor.close(false);
		
		wait(250);
		file.deleteFile();
		
		// update processing count
		trace("File processed " + counter + " times");
		
		// create new file
		if (createFile())
		{
			// continue
			id = setTimeout(execiseFilePart2, TIMEOUT / 2);
		}	
	}
	else
	{
		execiseFilePart2();
	}
}

/**
 * exerciseFilePart2
 */
function execiseFilePart2()
{
	// clear first run flag
	firstRun = false;
	
	// update counter
	counter++;
	
	// open file
	if (file.exists)
	{
		fileUtils.open(file.absolutePath);
		
		waitUntilOpen();
	}
}

/**
 * getSource
 */
function getSource()
{
	var script = new File(location);
	var file = new File(script.parentFile.absolutePath + File.separator + "lib/dojo.js");
	var lines = file.readLines();
	
	return lines.join("\r\n");
}

/**
 * trace
 * 
 * @param {Object} message
 */
function trace(message)
{
	if (TRACING)
	{
		out.println(message);
	}
}

/**
 * Loop for the specified number of milliseconds
 * 
 * @param {Number} ms
 */
function wait(ms)
{
	var startTime = new Date().getTime();
	var endTime = new Date().getTime();
	
	while (endTime - startTime < ms)
	{
		endTime = new Date().getTime();
	}
}

/**
 * waitUntilOpen
 */
function waitUntilOpen()
{
	var activeEditor = editors.activeEditor;
	var isOpen = false;
	
	if (activeEditor)
	{
		var editorFilename = new File(getFilename()).baseName;
		var fileFilename = file.baseName;
		
		if (editorFilename == fileFilename)
		{
			isOpen = true;
			
			// begin timer
			var startTime = new Date().getTime();
		
			// clear existing text and add new text
			activeEditor.applyEdit(0, activeEditor.sourceLength, source);
			
			// move to the end of the file
			activeEditor.selectAndReveal(source.length - 5);
		
			// show elapsed time
			out.print(new Date().getTime() - startTime + "ms - ");
					
			// repeat process
			id = setTimeout(exerciseFile, TIMEOUT / 2);	
		}
	}
	
	// if not open, delay and try again
	if (isOpen == false)
	{
		out.println("File not open yet. Checking again in 500ms");
		id = setTimeout(waitUntilOpen, 500);
	}
}
