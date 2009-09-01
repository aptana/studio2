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
package com.aptana.ide.subscription;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.subscription.model.SubscriptionService;

public class SubscriptionContentHandler implements ContentHandler {

    private static final String SERVICE = "service"; //$NON-NLS-1$
    private static final String FEATURE_ID = "feature_id"; //$NON-NLS-1$
    private static final String UPDATE_SITE = "update_site"; //$NON-NLS-1$
    private static final String START_DATE = "created_at"; //$NON-NLS-1$
    private static final String NEXT_BILL_DATE = "next_bill_date"; //$NON-NLS-1$
    private static final String STATUS = "status"; //$NON-NLS-1$
    private static final String ACTIVITY_ID = "activity_id"; //$NON-NLS-1$

    private static final SimpleDateFormat XML_DATETIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$

    private StringBuilder fData;
    private List<SubscriptionService> fServices;

    private String fFeatureId;
    private URL fUpdateSite;
    private long fStartTime;
    private long fNextBillTime;
    private String fStatus;
    private List<String> fActivityIds;

    public SubscriptionContentHandler() {
        fServices = new ArrayList<SubscriptionService>();
        fActivityIds = new ArrayList<String>();
    }

    public SubscriptionService[] getServices() {
        return fServices.toArray(new SubscriptionService[fServices.size()]);
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        for (int i = start; i < start + length; ++i) {
            fData.append(ch[i]);
        }
    }

    public void endDocument() throws SAXException {
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String data = fData.toString().trim();
        if (qName.equals(FEATURE_ID)) {
            fFeatureId = data;
        } else if (qName.equals(UPDATE_SITE)) {
            try {
                fUpdateSite = new URL(data);
            } catch (MalformedURLException e) {
                logError(e);
            }
        } else if (qName.equals(START_DATE)) {
            try {
                fStartTime = XML_DATETIME_FORMAT.parse(data).getTime();
            } catch (ParseException e) {
                logError(e);
            }
        } else if (qName.equals(NEXT_BILL_DATE)) {
            try {
                fNextBillTime = XML_DATETIME_FORMAT.parse(data).getTime();
            } catch (ParseException e) {
                logError(e);
            }
        } else if (qName.equals(STATUS)) {
            fStatus = data;
        } else if (qName.equals(ACTIVITY_ID)) {
            fActivityIds.add(data);
        } else if (qName.equals(SERVICE)) {
            SubscriptionService service = new SubscriptionService(fFeatureId,
                    fUpdateSite, fStartTime, fNextBillTime, fStatus,
                    fActivityIds.toArray(new String[fActivityIds.size()]));
            fServices.add(service);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        fData = new StringBuilder();
        if (qName.equals(SERVICE)) {
            fFeatureId = null;
            fUpdateSite = null;
            fStartTime = 0;
            fNextBillTime = 0;
            fStatus = null;
            fActivityIds.clear();
        }
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    private static void logError(Exception e) {
        IdeLog.logError(SubscriptionPlugin.getDefault(), e.getMessage(), e);
    }
}
