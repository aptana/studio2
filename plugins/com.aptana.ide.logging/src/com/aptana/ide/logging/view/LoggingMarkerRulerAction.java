/** 
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.logging.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;

import com.aptana.ide.core.resources.IUniformResource;
import com.aptana.ide.core.resources.MarkerUtils;
import com.aptana.ide.logging.ILogResource;

/**
 * Marker ruler action.
 * @author Denis Denisenko
 */
public class LoggingMarkerRulerAction extends ResourceAction implements IUpdate
{
    /** The maximum length of an proposed label. */
    private static final int MAX_LABEL_LENGTH = 80;
    
    /**
     * Resource bundle.
     */
    private final ResourceBundle bundle;
    
    /**
     * Add label.
     */
    private String label;
    
    /**
     * Action prefix.
     */
    private final String prefix;
    
    /**
     * Shell.
     */
    private final Shell shell;
    
    /**
     * Ruler.
     */
    private final IVerticalRuler ruler;
    
    /**
     * Whether to ask for label.
     */
    private final boolean askForLabel;
    
    /**
     * Marker type.
     */
    private final String markerType;
    
    /**
     * Document to watch.
     */
    private final IDocument document;
    
    /**
     * Annotation model.
     */
    private final IAnnotationModel model;
    
    /**
     * Resource.
     */
    private final IUniformResource resource;
    
    /**
     *  Markers for ruler position.
     *  
     */
    private List markers;

    /**
     * LoggingMarkerRulerAction constructor.
     * @param bundle - resource bundle.
     * @param prefix - prefix.
     * @param resource - resource.
     * @param document - document.
     * @param ruler - vertical ruler.
     * @param annotationModel - annotation model.
     * @param askForLabel - whether to ask for level.
     * @param markerType - marker type.
     * @param shell - shell.
     */
    public LoggingMarkerRulerAction(ResourceBundle bundle, String prefix,
            ILogResource resource,
            IDocument document, IAnnotationModel annotationModel,
            IVerticalRuler ruler, boolean askForLabel, String markerType,
            Shell shell)
    {
        super(bundle, prefix);
        this.bundle = bundle;
        this.prefix = prefix;
        this.ruler = ruler;
        this.askForLabel = askForLabel;
        this.markerType = markerType;
        this.document = document;
        this.model = annotationModel;
        this.shell = shell;
        this.resource = resource;
        
        label= getString(bundle, prefix + ".label", prefix + ".label"); //$NON-NLS-2$ //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        update();
        if (markers.isEmpty())
            addMarker();
        else
            removeMarkers(markers);
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        int line= ruler.getLineOfLastMouseButtonActivity() + 1;
        
        if (line > document.getNumberOfLines()) {
            setEnabled(false);
            setText(label);
        } else {
            markers= getMarkers();
            setEnabled(resource != null);
            setText(label);
        }
    }

    
    /**
     * Creates a new marker according to the specification of this action and
     * adds it to the marker resource.
     */
    protected void addMarker() {
        Map attributes= createAttributes();
        if (askForLabel) {
            if (!askForLabel(attributes))
                return;
        }

        try {
            MarkerUtils.createMarkerForExternalResource(resource, attributes, markerType);
        } catch (CoreException x) {
            handleCoreException(x, "Unxepected exception"); //$NON-NLS-1$
        }
        
        ruler.update();       
    }
    
    /**
     * Asks the user for a marker label. Returns <code>true</code> if a label
     * is entered, <code>false</code> if the user cancels the input dialog.
     * Sets the value of the attribute <code>message</code> in the given
     * map of attributes.
     *
     * @param attributes the map of attributes
     * @return <code>true</code> if the map of attributes has successfully been initialized
     */
    protected boolean askForLabel(Map attributes) {

        Object o= attributes.get("message"); //$NON-NLS-1$
        String proposal= (o instanceof String) ? (String) o : ""; //$NON-NLS-1$
        if (proposal == null)
            proposal= ""; //$NON-NLS-1$

        String title= getString(bundle, prefix + "add.dialog.title", prefix + "add.dialog.title"); //$NON-NLS-2$ //$NON-NLS-1$
        String message= getString(bundle, prefix + "add.dialog.message", prefix + "add.dialog.message"); //$NON-NLS-2$ //$NON-NLS-1$
        IInputValidator inputValidator = new IInputValidator() {
            public String isValid(String newText) {
                return (newText == null || newText.trim().length() == 0) ? " " : null; //$NON-NLS-1$
            }
        };
        InputDialog dialog= new InputDialog(shell, title, message, proposal, inputValidator);

        String label= null;
        if (dialog.open() != Window.CANCEL)
            label= dialog.getValue();

        if (label == null)
            return false;

        label= label.trim();
        if (label.length() == 0)
            return false;

        MarkerUtils.setMessage(attributes, label);
        return true;
    }
    
    
    
    /**
     * Removes the  markers.
     *
     * @param markers to remove
     */
    protected void removeMarkers(final List markers) {
        try {
            for (int i= 0; i < markers.size(); ++i) {
                IMarker marker= (IMarker) markers.get(i);
                marker.delete();
            }
        } catch (CoreException x) {
            handleCoreException(x, "Unexpected exception"); //$NON-NLS-1$
        }
    }
    
    /**
     * Creates initial marker attributes.
     */
    private Map createAttributes()
    {
        Map attributes= new HashMap();

        int line= ruler.getLineOfLastMouseButtonActivity();
        int start= -1;
        int end= -1;
        int length= 0;

        try {

            IRegion lineInformation= document.getLineInformation(line);
            start= lineInformation.getOffset();
            length= lineInformation.getLength();

            end= start + length;


        } catch (BadLocationException x) {
        }

        // marker line numbers are 1-based
        MarkerUtils.setMessage(attributes, getLabelProposal(document, start, length));
        MarkerUtils.setLineNumber(attributes, line + 1);
        MarkerUtils.setCharStart(attributes, start);
        MarkerUtils.setCharEnd(attributes, end);

        return attributes;
    }

    /**
     * Returns the initial label for the marker.
     *
     * @param document the document from which to extract a label proposal
     * @param offset the document offset of the range from which to extract the label proposal
     * @param length the length of the range from which to extract the label proposal
     * @return the label proposal
     * @since 3.0
     */
    protected String getLabelProposal(IDocument document, int offset, int length) {
        try {
            String label= document.get(offset, length).trim();
            if (label.length() <= MAX_LABEL_LENGTH)
                return label;
            return label.substring(0, MAX_LABEL_LENGTH);
        } catch (BadLocationException x) {
            // don't propose label then
            return null;
        }
    }
    
    /**
     * Handles core exceptions. This implementation logs the exceptions
     * with the workbench plug-in and shows an error dialog.
     *
     * @param exception the exception to be handled
     * @param message the message to be logged with the given exception
     */
    protected void handleCoreException(CoreException exception, String message) {
        

//        if (message != null)
//            log.log(new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, message, exception));
//        else
//            log.log(exception.getStatus());

        String title= getString(bundle, prefix + "error.dialog.title", prefix + "error.dialog.title"); //$NON-NLS-2$ //$NON-NLS-1$
        String msg= getString(bundle, prefix + "error.dialog.message", prefix + "error.dialog.message"); //$NON-NLS-2$ //$NON-NLS-1$

        ErrorDialog.openError(shell, title, msg, exception.getStatus());
    }
    
    /**
     * Returns all markers which include the ruler's line of activity.
     *
     * @return all a list of markers which include the ruler's line of activity
     */
    protected List getMarkers() {

        List markers= new ArrayList();
        
        if (resource != null && model != null) 
        {
            IMarker[] allMarkers= MarkerUtils.findMarkers(resource, markerType, true);
            if (allMarkers != null) 
            {
                for (int i= 0; i < allMarkers.length; i++) 
                {
                    if (includesRulerLine(
                            ((AbstractMarkerAnnotationModel)model).getMarkerPosition(
                                    allMarkers[i]), document)) 
                    {
                        markers.add(allMarkers[i]);
                    }
                }
            }
        }

        return markers;
    }
    
    /**
     * Checks whether a position includes the ruler's line of activity.
     *
     * @param position the position to be checked
     * @param document the document the position refers to
     * @return <code>true</code> if the line is included by the given position
     */
    protected boolean includesRulerLine(Position position, IDocument document) {

        if (position != null) {
            try {
                int markerLine= document.getLineOfOffset(position.getOffset());
                int line= ruler.getLineOfLastMouseButtonActivity();
                if (line == markerLine)
                    return true;
                // commented because of "1GEUOZ9: ITPJUI:ALL - Confusing UI for multi-line Bookmarks and Tasks"
                // return (markerLine <= line && line <= document.getLineOfOffset(position.getOffset() + position.getLength()));
            } catch (BadLocationException x) {
            }
        }

        return false;
    }
}
