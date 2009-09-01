package com.aptana.ide.editor.yml;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.editors.unified.actions.UnifiedActionContributor;

public class YMLEditorActionContributor extends UnifiedActionContributor {

    public void setActiveEditor(IEditorPart part) {
        super.setActiveEditor(part);

        ITextEditor textEditor = null;
        if (part instanceof ITextEditor) textEditor = (ITextEditor) part;
 
        IActionBars actionBars = getActionBars();
        actionBars.setGlobalActionHandler("com.aptana.ide.editor.yml.actions.ToggleComment", getAction(textEditor, "ToggleComment")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
