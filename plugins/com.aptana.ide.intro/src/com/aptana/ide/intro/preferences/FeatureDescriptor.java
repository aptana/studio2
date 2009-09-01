/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.intro.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import com.aptana.ide.update.manager.IPlugin;

public class FeatureDescriptor implements IPlugin {

    private String featureId;
    private String label;
    private URL url;
    private String[] conflicts = new String[0];

    public FeatureDescriptor(String featureId, String label, String url,
            String[] conflicts) {
        this.featureId = featureId;
        this.label = label;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
        }
        this.conflicts = conflicts;
    }

    public String getId() {
        return featureId;
    }

    public String getName() {
        return label;
    }

    public URL getURL() {
        return url;
    }

    public String getVersion() {
        return null;
    }

    public String[] getConflicts() {
        return conflicts;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeatureDescriptor))
            return false;
        return featureId.equals(((FeatureDescriptor) obj).getId());
    }

    @Override
    public int hashCode() {
        return featureId.hashCode();
    }

    public String toString() {
        return featureId + ", " + label + "(" + url + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
