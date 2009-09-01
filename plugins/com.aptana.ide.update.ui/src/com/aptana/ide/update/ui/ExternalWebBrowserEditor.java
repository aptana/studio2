package com.aptana.ide.update.ui;

import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.WebBrowserEditor;

import com.aptana.ide.core.ui.CoreUIUtils;

public class ExternalWebBrowserEditor extends WebBrowserEditor implements
        LocationListener {

    public static final String ID = "com.aptana.ide.update.ui.browser.editor"; //$NON-NLS-1$

    private boolean startExternal;

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        if (webBrowser.getBrowser() != null) {
            webBrowser.getBrowser().addLocationListener(this);
        }
    }

    public void changed(LocationEvent event) {
    }

    public void changing(LocationEvent event) {
        String location = event.location;
        if (startExternal) {
            // Launch external browser
            CoreUIUtils.openBrowserURL(location);
            event.doit = false;
        } else if (location
                .matches(".*/release-message/[0-9]+\\.[0-9]+/message\\.html")) { //$NON-NLS-1$
            startExternal = true;
        }
    }
}
