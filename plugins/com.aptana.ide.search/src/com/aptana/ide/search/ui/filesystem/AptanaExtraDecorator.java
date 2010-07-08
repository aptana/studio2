/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.search.ui.filesystem;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.swt.graphics.Image;

/**
 * adds annotation with match count to folders.
 */
final class AptanaExtraDecorator implements ILabelDecorator {
    /**
	 * 
	 */
    private final AptanaFileSystemSearchPage aptanaFileSearchPage;

    /**
     * @param aptanaFileSearchPage
     */
    AptanaExtraDecorator(AptanaFileSystemSearchPage aptanaFileSearchPage) {
        this.aptanaFileSearchPage = aptanaFileSearchPage;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image,
     *      java.lang.Object)
     */
    public Image decorateImage(Image image, Object element) {
        return null;
    }

    /**
     * decorates folder text by adding count of total matches to it
     * 
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String,
     *      java.lang.Object)
     */
    public String decorateText(String text, Object element) {
        if (aptanaFileSearchPage.turnOff) {
            return text;
        }
        if (element instanceof IResource) {
            if (!(element instanceof IFile)) {
                IResource rs = (IResource) element;
                int matches = this.count(rs);
                String format = SearchMessages.FileLabelProvider_count_format;
                String result = MessageFormat.format(format, new Object[] {
                        text, new Integer(matches) });
                return result;
            }
        }
        return text;
    }

    private int count(IResource rs) {
        Object[] children = aptanaFileSearchPage.provider.getChildren(rs);
        int matches = 0;
        for (Object object : children) {
            if (object instanceof IFile) {
                matches += this.aptanaFileSearchPage.getInput().getMatchCount(
                        object);
            } else {
                matches += this.count((IResource) object);
            }
        }
        return matches;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }
}
