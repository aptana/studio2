/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Aptana, Inc. - modifications
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.aptana.internal.ui.text.spelling.messages"; //$NON-NLS-1$
    public static String JavaUIMessages_INF_Add;
    public static String JavaUIMessages_INF_Disable;
    public static String JavaUIMessages_INF_Ignore;
    public static String JavaUIMessages_LBL_Add;
    public static String JavaUIMessages_LBL_Disable;
    public static String JavaUIMessages_LBL_Ignore;
    public static String JavaUIMessages_LBL_Spellcase;
    public static String JavaUIMessages_MSG_Ignore;
    public static String JavaUIMessages_Ques_Configure;
    public static String JavaUIMessages_Title_Configure;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
