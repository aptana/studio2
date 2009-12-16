/*
 * Menu: Editors > Comment Lines
 * Kudos: Ingo Muschenetz
 * License: EPL 1.0
 * Listener: commandService().addExecutionListener(this); 
 * DOM: http://download.eclipse.org/technology/dash/update/org.eclipse.eclipsemonkey.lang.javascript
 */

/**
 * Returns a reference to the workspace command service
 */
function commandService()
{
	var commandServiceClass = Packages.org.eclipse.ui.commands.ICommandService;
	
	// same as doing ICommandService.class
    var commandService = Packages.org.eclipse.ui.PlatformUI.getWorkbench().getAdapter(commandServiceClass);
    return commandService;
}

/**
 * Called before any/every command is executed, so we must filter on command ID
 */
function preExecute(commandId, event) {
	
	// if we see a save command
	if (commandId == "com.aptana.ide.editors.views.actions.js.actionKeyCommand")
	{
		main();
    }
}

/* Add in all methods required by the interface, even if they are unused */
function postExecuteSuccess(commandId, returnValue) {}

function notHandled(commandId, exception) {}

function postExecuteFailure(commandId, exception) {}

var comment = "//";
var commentLength = comment.length;

function main() {
 
	var editor = editors.activeEditor;
 
	// get range of lines in the selection (or at the cursor position)
	var range = editor.selectionRange;
	var startLine = editor.getLineAtOffset(range.startingOffset);
	var endLine = editor.getLineAtOffset(range.endingOffset);
		
	// determine if we're adding or removing comments
	var source = editor.source;
	var offset = editor.getOffsetAtLine(startLine);
	var addComment = (source.substring(offset, offset + commentLength) != comment);
	var adjust = 0;
		
	var type = getPartition(editor.selectionRange.startingOffset);
	if(type != "text/javascript" && type != "text/jscomment")
	{
		return;	
	}

	editor.beginCompoundChange();
		
	if (addComment) {
        if(endLine - startLine == 0)
        {
			var offset = editor.getOffsetAtLine(startLine);            
			editor.applyEdit(offset, 0, comment);
            
        }
        else
        {
    		var offset = editor.getOffsetAtLine(endLine);
            if(offset == range.endingOffset)
            {
                endLine--;
            }
    		for (var i = startLine; i <= endLine; i++) {
    			offset = editor.getOffsetAtLine(i);
    			editor.applyEdit(offset, 0, comment);
    		}
        }
	} else {
		for (var i = startLine; i <= endLine; i++) {
			var offset = editor.getOffsetAtLine(i);

			if (source.substring(offset + adjust, offset + adjust + commentLength) == comment) {
				editor.applyEdit(offset, commentLength, "");
				adjust += commentLength;
			}
		}
	}
		
	editor.endCompoundChange();
}

/**
 * Get the type of the partition based on the current offset
 * @param {Object} offset
 */
function getPartition(offset)
{
	try {
		
		var fileContext = editors.activeEditor.textEditor.getFileContext();
		
		if (fileContext !== null && fileContext !== undefined) {
			var partition = fileContext.getPartitionAtOffset(offset);
			return partition.getType();
		}
	} catch(e) {
		out.println(e);
	}
	
	return null;
}
