/**
 * bootstrap.js
 *
 * @author Kevin Lindsey
 * @version 1.0
 *
 **/

// run main
init();

/**
 * init
 */
function init() {
	processRootDir(getProperty("scripts.dir"));
	processRootDir(getProperty("user.scripts.dir"));
}

/**
 * Process the root directory of a script folder
 *
 * @param {String} dir
 */
function processRootDir(path) {
	var dir = new File(path);
	
	if (dir.exists && dir.isDirectory) {
		processDir(dir);
	}
}

/**
 * Recursively descend the given directory
 *
 * @param {File} dir
 *		The directory to process
 * @param {Number} [level]
 * 		The current depth of the recursion. Defaults to zero if not defined
 */
function processDir(dir, level) {
	// init level
	if (level === undefined) {
		level = 0;
	}
	
	// get all files and directories in this directory
	var files = dir.list;
	var length = files.length;
	
	// process files first
	if (level == 0) {
		for (var i = 0; i < length; i++) {
			var file = files[i];
			
			if (file.isFile && file.canRead && file.extension == ".js") {
				try {
					loadLibrary(file.absolutePath);
				} catch (e) {
					out.println(e.message);
				}
			}
		}
	} else {
		var manifest = new File(dir.absolutePath + "/MANIFEST");
		
		if (manifest.exists && manifest.isFile && manifest.canRead) {
			var lines = manifest.readLines();
			var properties = parseManifest(lines);
			
			//err.println(manifest.absolutePath);
			//for (var p in properties) {
			//	err.println(p + " = " + properties[p]);
			//}
			
			if (properties.hasOwnProperty("startupFile"))
			{
				var main = new File(dir.absolutePath + "/" + properties.startupFile);
				
				if (main.exists && main.isFile && main.canRead) {
					var onLoad = (properties.hasOwnProperty("startupFunction")) ? properties.startupFunction : "onload"
					
					try {
						var libGlobal = loadLibrary(main.absolutePath, onLoad);
						
						libGlobal.MANIFEST = properties;
					} catch (e) {
						out.println(e.message);
					}
				}
			}
		}
	}
	
	// process directories
	for (var i = 0; i < length; i++) {
		var file = files[i];
		
		if (file.isDirectory) {
			processDir(file, level + 1);
		}
	}
}

/**
 * Make an string used for indentation at the beginning of a line
 *
 * @param {Number} count
 * 		The number of times to repeat the indentation text
 */
function makeIndent(count) {
	var result = "";
	
	if (count !== undefined) {
		result = new Array(count + 1).join("  ");
	}
	
	return result;
}

function parseManifest(lines) {
	var emptyLine = /^\s*$/;
	var result = {};
	
	for (var i = 0; i < lines.length; i++) {
		var line = lines[i];
		
		if (line.length > 0 && line.charAt(0) != "#" && emptyLine.test(line) == false) {
			var values = line.split("=");
			
			if (values.length == 2) {
				var key = values[0];
				var value = values[1];
				
				result[key] = value;
			} else {
				err.println("Improperly formatted MANIFEST line: " + line);
			}
		}
	}
	
	return result;
}