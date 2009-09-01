/*
 * Menu: Reporting > Assets Report
 * Kudos: Paul Colton
 * License: Aptana Commercial License v1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.reporting
 */
 
include("../reporting/engine.js");

function main()
{
	var reportTemplate = report.getTemplate("/reporting/assets.html");
	var finalReport = generateReport(reportTemplate);
	var project = report.getSelectedProject();
	if( project != null ) 
	{
		report.createReportView(finalReport, "Asset", project);
	}
}
