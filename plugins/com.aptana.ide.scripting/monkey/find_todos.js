/*
 * Menu: Editors > Find TODOs
 * Kudos: Ingo Muschenetz
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
  
function main() {
  var files = resources.filesMatching(".*\\.js");
  var match;
   
  for each( file in files ) { 
    file.removeMyTasks(  );
    for each( line in file.lines ) {
      if (match = line.string.match(/\/\/TODO: (.*)/)) {
         line.addMyTask( match[1] );
      }
    }
  }
  window.getActivePage().showView("org.eclipse.ui.views.TaskList");
}