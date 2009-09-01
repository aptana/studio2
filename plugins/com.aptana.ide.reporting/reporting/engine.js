
function generateReport(reportTemplate) 
{
	var project = report.getSelectedProject();
	
	if(project == null)
	{
		alert("Please select a project before running a report.");
		return;
	}
	
	var projectPath = report.getSelectedProjectPath();
	
	var items = getFileList(["html?", "rb", "rhtml?", "php", "png", "css", "js", "gif"]);
	
	var jsonData = "var projectName = '" + project + "'; ";
	jsonData += "var projectPath = '" + projectPath + "'; ";
	jsonData += "var reportData = [";
	var first = true;
	
	for each(item in items)
	{
		if(first)
		{
			first = false;
		}
		else
		{
			jsonData += ",";
		}
		
		jsonData += "{ " +
			"'filename': '" + item.filename + "'," +
			"'path': '" + item.path + "'," +
			"'size': " + item.size + "," +
			"'date': " + item.date + 
			" }";
	}
	
	jsonData += "];\n";

	return reportTemplate.replace("$$DATA$$", jsonData);
}

function getFileList(fileTypes)
{
	var pattern = ".*\\.(" + fileTypes.join("|") + ")$";
	
	var project = report.getSelectedProject();
 	var files = resources.filesMatchingForProjectIgnoreCase(project, pattern);
   	var allItems = [];
   	
	for each( file in files ) 
	{
		var ifile = file.getEclipseObject();
	  	
	  	var item = 
	  		{
	  			filename: ifile.getName(),
	  			path: ifile.getFullPath(),
	  			size: file.getSize(),
	  			date: file.getLastModified()
	  		};
	  			
	  	allItems.push(item);
	 }
	 
	 return allItems;
}
