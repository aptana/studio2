/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.search.epl;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 */
public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.search.epl.messages"; //$NON-NLS-1$

    /**
     * DIRECTORY
     */
    public static String DIRECTORY;
    /**
     * AptanaFileMatch_IO_ERROR
     */
    public static String AptanaFileMatch_IO_ERROR;
    /**
     * ContentAssistHandler_CONTENT_ASSIST_AVAILABLE
     */
    public static String ContentAssistHandler_CONTENT_ASSIST_AVAILABLE;
    /**
     * OPEN_EDITORS
     */
    public static String OPEN_EDITORS;
    /**
     * CHOOSE
     */
    public static String CHOOSE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
