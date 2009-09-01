package com.aptana.ide.editor.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.content.IContentDescription;

public class XMLContentDescriber extends com.aptana.ide.epl.XMLContentDescriber {

    public XMLContentDescriber() {
    }

    public int describe(InputStream input, IContentDescription description)
            throws IOException {
        return VALID;
    }

    public int describe(Reader input, IContentDescription description)
            throws IOException {
        return VALID;
    }
}
