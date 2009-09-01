/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ide.ui.editors.preferences.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.editors.codeassist.Activator;




/**
 * 
 *
 */
public class SnippetPreview extends Preview {
  
    /**
     * 
     *
     */
    public final static class PreviewSnippet {
        
        /**
         * 
         */
        public String header;
        /**
         * 
         */
        public final String source;
        /**
         * 
         */
        public final int kind;
        
        /**
         * @param kind
         * @param source
         */
        public PreviewSnippet(int kind, String source) {
            this.kind= kind;
            this.source= source;
        }
    }
    
    private ArrayList fSnippets;

    /**
     * @param workingValues
     * @param parent
     * @param editor
     * @param store
     */
    public SnippetPreview(Map workingValues, Composite parent,String editor,IPreferenceStore store) {
        super(workingValues, parent,editor,store);
        fSnippets= new ArrayList();
    }

    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.Preview#doFormatPreview()
     */
    protected void doFormatPreview() {
        if (fSnippets.isEmpty()) { 
            fPreviewDocument.set(""); //$NON-NLS-1$
            return;
        }
        
        //This delimiter looks best for invisible characters
        final String delimiter= "\n"; //$NON-NLS-1$
        
        final StringBuffer buffer= new StringBuffer();
        for (final Iterator iter= fSnippets.iterator(); iter.hasNext();) {
            final PreviewSnippet snippet= (PreviewSnippet) iter.next();
            String formattedSource;
            try {
            	//TODO CORRECT IT
                formattedSource=snippet.source;// CodeFormatterUtil.format(snippet.kind, snippet.source, 0, delimiter, fWorkingValues);
            } catch (Exception e) {
                final IStatus status= new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, 
                    FormatterMessages.JavaPreview_formatter_exception, e); 
                Activator.getDefault().getLog().log(status);
                continue;
            }
            buffer.append(delimiter);            
            buffer.append(formattedSource);
            buffer.append(delimiter);            
            buffer.append(delimiter);
        }
        fPreviewDocument.set(buffer.toString());
    }
    
    
    
    /**
     * @param snippet
     */
    public void add(PreviewSnippet snippet) {
        fSnippets.add(snippet);
    }
    
    /**
     * @param snippet
     */
    public void remove(PreviewSnippet snippet) {
        fSnippets.remove(snippet);
    }
    
    /**
     * @param snippets
     */
    public void addAll(Collection snippets) {
        fSnippets.addAll(snippets);
    }
    
    /**
     * 
     */
    public void clear() {
        fSnippets.clear();
    }

}
