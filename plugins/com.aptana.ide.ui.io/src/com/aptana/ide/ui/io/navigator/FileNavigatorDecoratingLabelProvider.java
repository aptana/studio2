package com.aptana.ide.ui.io.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.internal.navigator.NavigatorDecoratingLabelProvider;

/**
 * A custom label provider for file navigator to provide additional features
 * such as tooltip support.
 */
@SuppressWarnings("restriction")
public class FileNavigatorDecoratingLabelProvider extends NavigatorDecoratingLabelProvider {

    public FileNavigatorDecoratingLabelProvider(ILabelProvider commonLabelProvider) {
        super(commonLabelProvider);
    }

    @Override
    public String getToolTipText(Object element) {
        return element.toString();
    }
}
