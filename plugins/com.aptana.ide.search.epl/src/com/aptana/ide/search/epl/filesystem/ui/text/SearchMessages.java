/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.search.epl.filesystem.ui.text;

import org.eclipse.osgi.util.NLS;

public final class SearchMessages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.search.epl.filesystem.ui.text.SearchMessages";//$NON-NLS-1$

    public static String FileLabelProvider_count_format;
    public static String FileLabelProvider_removed_resource_label;

    public static String FileSearchPage_sort_name_label;
    public static String FileSearchPage_sort_path_label;
    public static String FileSearchPage_sort_by_label;

    public static String ReplaceAction_description_operation;
    public static String ReplaceAction_title_all;
    public static String ReplaceAction_title_selected;

    public static String AptanaFileSearchPage_NOT_ALL_MATCHES;

    static {
        NLS.initializeMessages(BUNDLE_NAME, SearchMessages.class);
    }

    private SearchMessages() {
        // Do not instantiate
    }

}
