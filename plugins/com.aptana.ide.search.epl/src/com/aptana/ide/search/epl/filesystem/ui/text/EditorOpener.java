package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.search.epl.Activator;

public class EditorOpener {

    /**
     * @param file
     * @param activate
     * @return
     */
    public IEditorPart open(File file, boolean activate) {
        IEditorInput input = CoreUIUtils.createJavaFileEditorInput(file);
        try {
            IEditorPart openEditor = IDE.openEditor(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage(), input, IDE
                    .getEditorDescriptor(file.getName()).getId());
            return openEditor;
        } catch (PartInitException e) {
            IdeLog.logError(Activator.getDefault(), e.getMessage());
            return null;
        }
    }

}
