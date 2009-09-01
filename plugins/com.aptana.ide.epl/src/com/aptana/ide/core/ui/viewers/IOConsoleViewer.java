package com.aptana.ide.core.ui.viewers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.TextConsoleViewer;

public class IOConsoleViewer extends TextConsoleViewer {
    /**
     * will always scroll with output if value is true.
     */
    private boolean fAutoScroll = true;

    private IDocumentListener fDocumentListener;
    
    public IOConsoleViewer(Composite parent, TextConsole console) {
        super(parent, console);
    }

    public boolean isAutoScroll() {
        return fAutoScroll;
    }

    public void setAutoScroll(boolean scroll) {
        fAutoScroll = scroll;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.TextViewer#handleVerifyEvent(org.eclipse.swt.events.VerifyEvent)
     */
    protected void handleVerifyEvent(VerifyEvent e) {
        IDocument doc = getDocument();
        String[] legalLineDelimiters = doc.getLegalLineDelimiters();
        String eventString = e.text;
        try {
            IConsoleDocumentPartitioner partitioner = (IConsoleDocumentPartitioner) doc.getDocumentPartitioner();
            if (!partitioner.isReadOnly(e.start)) {
                boolean isCarriageReturn = false;
                for (int i = 0; i < legalLineDelimiters.length; i++) {
                    if (e.text.equals(legalLineDelimiters[i])) {
                        isCarriageReturn = true;
                        break;
                    }
                }

                if (!isCarriageReturn) {
                    super.handleVerifyEvent(e);
                    return;
                }
            }

            int length = doc.getLength();
            if (e.start == length) {
                super.handleVerifyEvent(e);
            } else {
                try {
                    doc.replace(length, 0, eventString);
                } catch (BadLocationException e1) {
                }
                e.doit = false;
            }
        } finally {
            StyledText text = (StyledText) e.widget;
            text.setCaretOffset(text.getCharCount());
        }
    }

    /**
     * makes the associated text widget uneditable.
     */
    public void setReadOnly() {
        ConsolePlugin.getStandardDisplay().asyncExec(new Runnable() {
            public void run() {
                StyledText text = getTextWidget();
                if (text != null) {
                    text.setEditable(false);
                }
            }
        });
    }

    /**
     * @return <code>false</code> if text is editable
     */
    public boolean isReadOnly() {
        return !getTextWidget().getEditable();
    }
   
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextViewer#setDocument(org.eclipse.jface.text.IDocument)
     */
    public void setDocument(IDocument document) {
        IDocument oldDocument= getDocument();
        
        super.setDocument(document);
        
        if (oldDocument != null) {
            oldDocument.removeDocumentListener(getDocumentListener());
        }
        if (document != null) {
            document.addDocumentListener(getDocumentListener());
        }
    }
    
    private IDocumentListener getDocumentListener() {
        if (fDocumentListener == null) {
            fDocumentListener= new IDocumentListener() {
                public void documentAboutToBeChanged(DocumentEvent event) {
                }

                public void documentChanged(DocumentEvent event) {
                    if (fAutoScroll) {
                        revealEndOfDocument();
                    }
                }
            };
        }
        return fDocumentListener;
    }
}
