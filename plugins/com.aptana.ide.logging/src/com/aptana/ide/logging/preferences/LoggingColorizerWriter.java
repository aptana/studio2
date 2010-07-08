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
package com.aptana.ide.logging.preferences;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aptana.ide.editors.unified.colorizer.ColorizationConstants;
import com.aptana.ide.editors.unified.colorizer.ColorizerWriter;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.logging.LoggingPreferences;

/**
 * Logging colorization writer.
 * Writes not only pure colorization info, but also colorization rules
 * as "metadata" tag.
 * 
 * @author Denis Denisenko
 */
public class LoggingColorizerWriter extends ColorizerWriter
{
    /**
     * Rules to write.
     */
    private List<LoggingPreferences.Rule> rules;
    
    public LoggingColorizerWriter(List<LoggingPreferences.Rule> rules)
    {
        this.rules = rules;
    }

    /**
      * {@inheritDoc}
      */
    @Override
    protected Document buildDom(LanguageColorizer lc, String language)
            throws ParserConfigurationException
    {
        Document document = super.buildDom(lc, language);
        
        NodeList colorizersList = document.getElementsByTagName(ColorizationConstants.COLORIZER);
        if (colorizersList == null || colorizersList.getLength() != 1)
        {
            throw new IllegalArgumentException("Serialized colorization may only have a single colorizer"); //$NON-NLS-1$
        }
        
        Node colorizerElement = colorizersList.item(0);
        Element metadataElement = document.createElement(LoggingColorizationConstants.METADATA_ELEMENT);
        colorizerElement.appendChild(metadataElement);
        
        for (LoggingPreferences.Rule rule : rules)
        {
            Element ruleElement;
            if (rule.isRegexp())
            {
                ruleElement = document.createElement(LoggingColorizationConstants.REGEXP_ELEMENT);
            }
            else
            {
                ruleElement = document.createElement(LoggingColorizationConstants.STRING_ELEMENT);
            }
            
            metadataElement.appendChild(ruleElement);
            
            ruleElement.setAttribute(LoggingColorizationConstants.NAME_ATTRIBUTE, rule.getName());
            ruleElement.setAttribute(LoggingColorizationConstants.VALUE_ATTRIBUTE, rule.getRule());
            ruleElement.setAttribute(LoggingColorizationConstants.CASE_INSENSITIVE_ATTRIBUTE, 
                    Boolean.toString(rule.isCaseInsensitive()));
        }
        
        return document;
    }
    
}
