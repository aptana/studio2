/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.ide.core.PlatformUtils;

/**
 * @author Max Stepanov
 *
 */
public final class LocalRoot extends PlatformObject {
	
	private final String name;
	private final File root;

	/**
	 *
	 */
	private LocalRoot(String name, File root) {
		super();
		this.name = name;
		this.root = root;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRootURI()
	 */
	public URI getRootURI() {
		return EFS.getLocalFileSystem().fromLocalFile(root).toURI();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRoot()
	 */
	public IFileStore getRoot() {
		return EFS.getLocalFileSystem().fromLocalFile(root);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (File.class == adapter) {
			return getFile();
		}
		if (IFileStore.class == adapter) {
			return getRoot();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return root;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LocalRoot)) {
			return false;
		}
		return root.equals(((LocalRoot) obj).root);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return root.hashCode();
	}

	public static LocalRoot[] createRoots() {
		List<LocalRoot> list = new ArrayList<LocalRoot>();
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			for (File root : new File("/Volumes").listFiles()) {
				try {
					if (root.listFiles() != null) {
						LocalRoot localRoot = new LocalRoot(root.getName(), root.getCanonicalFile());
						if ("/".equals(localRoot.getFile().getCanonicalPath())) {
							list.add(0, localRoot);
						} else {
							list.add(localRoot);
						}
					}
				} catch (IOException e) {
				}
			}
		} else {
			for (File root : File.listRoots()) {
				try {
					list.add(new LocalRoot(root.getName(), root.getCanonicalFile()));
				} catch (IOException e) {
				}
			}			
		}
		{	/* Home */
			IPath homePath = new Path(PlatformUtils.expandEnvironmentStrings(PlatformUtils.HOME_DIRECTORY));
			File homeFile = homePath.toFile();
			if (homeFile.exists() && homeFile.isDirectory()) {
				try {
					list.add(new LocalRoot(homePath.lastSegment(), homeFile.getCanonicalFile()));
				} catch (IOException e) {
				}				
			}
		}
		{	/* Desktop */
			IPath desktopPath = new Path(PlatformUtils.expandEnvironmentStrings(PlatformUtils.DESKTOP_DIRECTORY));
			File desktopFile = desktopPath.toFile();
			if (desktopFile.exists() && desktopFile.isDirectory()) {
				try {
					list.add(new LocalRoot(desktopPath.lastSegment(), desktopFile.getCanonicalFile()));
				} catch (IOException e) {
				}				
			}
		}
		{	/* Documents */
			IPath docsPath = new Path(PlatformUtils.expandEnvironmentStrings(PlatformUtils.DOCUMENTS_DIRECTORY));
			File docsFile = docsPath.toFile();
			if (docsFile.exists() && docsFile.isDirectory()) {
				try {
					list.add(new LocalRoot(docsPath.lastSegment(), docsFile.getCanonicalFile()));
				} catch (IOException e) {
				}				
			}
		}

		return list.toArray(new LocalRoot[list.size()]);
	}


}
