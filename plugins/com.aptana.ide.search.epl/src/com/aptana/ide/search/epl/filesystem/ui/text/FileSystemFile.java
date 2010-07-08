/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * Wraps a java.io.File object in IFile.
 */
class FileSystemFile extends org.eclipse.core.internal.resources.File {

    public FileSystemFile(File file) {
        super(new Path(file.getAbsolutePath()), (Workspace) ResourcesPlugin
                .getWorkspace());
    }
}
