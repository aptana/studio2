/*
 * Menu: HTML > Toggle 'Modify pair tag' preference 
 * Kudos: Pavel Petrochenko
 * License: EPL 1.0
 * Key:M3+P
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 * DOM: http://localhost/com.aptana.ide.scripting
 */
// includes
include("lib/IDE_Utils.js");
function main(){
	loadBundle("com.aptana.ide.editor.html");	
	var pstore = Packages.com.aptana.ide.editor.html.HTMLPlugin.getDefault().getPreferenceStore();
	var prefName="com.aptana.ide.editor.html.AUTO_MODIFY_PAIR_TAG";
	var oldValue=pstore.getBoolean(prefName);
	pstore.setValue(prefName,!oldValue);			
}
