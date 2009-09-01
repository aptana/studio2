/*
 * Menu: Editors > Decrease Line Spacing
 * Kudos: Ingo Muschenetz (Aptana, Inc.)
 * License: EPL 1.0
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

function main() {
    
        var editor = editors.activeEditor.textEditor;
        var widget = editor.getViewer().getTextWidget();
        widget.setLineSpacing(widget.getLineSpacing() - 1);
}