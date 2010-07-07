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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * Content provider for a matches in file system
 * 
 * @author Pavel Petrochenko
 */
final class MatchesContentProvider implements IStructuredContentProvider {

    /**
	 * 
	 */
    private final AptanaFileSystemSearchPage aptanaFileSearchPage;
    private final IContentProvider contentProvider;

    /**
     * constructor
     * 
     * @param aptanaFileSearchPage
     * @param contentProvider
     *            base content provider
     */
    MatchesContentProvider(AptanaFileSystemSearchPage aptanaFileSearchPage,
            IContentProvider contentProvider) {
        this.aptanaFileSearchPage = aptanaFileSearchPage;
        this.contentProvider = contentProvider;
    }

    private Map<Object, Match[]> mmap = new HashMap<Object, Match[]>();

    /**
     * @param input
     * @param limit
     * @return matches for a given input (search result)
     */
    protected Object[] getMathes(AbstractTextSearchResult input, int limit) {
        List<Match> result = new ArrayList<Match>();
        Object[] elements = input.getElements();
        l2: for (int b = 0; b < elements.length; b++) {
            Match[] matches = this.aptanaFileSearchPage.getInput().getMatches(
                    elements[b]);
            this.mmap.put(elements[b], matches);
            for (int a = 0; a < matches.length; a++) {
                result.add(matches[a]);
                if (result.size() == limit) {
                    break l2;
                }
            }
        }
        return result.toArray();
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof AbstractTextSearchResult) {
            return getMathes((AbstractTextSearchResult) inputElement,
                    getElementLimit());
        }
        return AptanaFileSystemSearchPage.NO_ELEMENTS;
    }

    private int getElementLimit() {
        return aptanaFileSearchPage.getElementLimit1().intValue();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        this.contentProvider.dispose();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.mmap = new HashMap<Object, Match[]>(1000);
        this.contentProvider.inputChanged(viewer, oldInput, newInput);
    }

    /**
     * handles updates
     * 
     * @param updatedElements
     *            updates elements
     */
    public void elementsChanged(Object[] updatedElements) {
        StructuredViewer viewer2 = this.aptanaFileSearchPage.getViewer();
        TableViewer viewer = (TableViewer) viewer2;
        int elementLimit = getElementLimit();
        boolean tableLimited = getElementLimit() == -1;
        AbstractTextSearchResult input = this.aptanaFileSearchPage.getInput();
        for (Object element : updatedElements) {
            Match[] matchs = this.mmap.get(element);
            if (matchs == null) {
                matchs = AptanaFileSystemSearchPage.NO_MATCH;
            }
            if (input.getMatchCount(element) > 0) {
                Match[] matches = input.getMatches(element);
                Set<Match> set = new HashSet<Match>(Arrays.asList(matches));
                for (Match m : matches) {
                    if (!set.contains(m)) {
                        viewer.remove(m);
                    } else {
                        set.remove(m);
                    }
                }
                this.mmap.put(element, matches);
                for (Match name : set) {
                    if (!tableLimited
                            || (viewer.getTable().getItemCount() < elementLimit)) {
                        viewer.add(name);
                    } else {
                        break;
                    }
                }
            } else {
                viewer.remove(matchs);
            }
        }
    }
}
