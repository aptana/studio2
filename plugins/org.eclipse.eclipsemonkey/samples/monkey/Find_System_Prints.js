/*
 * Menu: Find System Prints
 * Kudos: Bjorn Freeman-Benson & Ward Cunningham
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */
  
function main() {
  var files = resources.filesMatching(".*\\.java");
  var match;
   
  for each( file in files ) { 
    file.removeMyTasks(  );
    for each( line in file.lines ) {
      if (match = line.string.match(/System\.out\.print(ln)? *\(.*\)/)) {
         line.addMyTask( match[0] );
      }
    }
  }
  window.getActivePage().showView("org.eclipse.ui.views.TaskList");
}
