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

import java.io.IOException;
import java.io.InputStream;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.colorizer.Messages;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.coloring.TokenTypes;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;

/**
 * Logging colorization reader.
 * @author Denis Denisenko
 */
public class LoggingColorizerReader extends ColorizerReader
{
    /**
     * Schema resource.
     */
    private static final String SCHEMA = "/com/aptana/ide/logging/resources/ColorizationSchema.xml"; //$NON-NLS-1$
    
    /**
     * LoggingColorizerReader constructor.
     * @param tokenList- token list.
     * @param registerColorizer - whether to register colorizer.
     */
    public LoggingColorizerReader(ITokenList tokenList, boolean registerColorizer)
    {
        super(tokenList);
        this.setPreferenceId(TokenTypes.LANGUAGE, LoggingPreferences.COLORIZER_ID);
        this.registerColorizer = true;
    }

    /**
     * Creates schema.
     */
    protected Schema createSchema()
    {
        InputStream schemaStream = LoggingColorizerReader.class
                .getResourceAsStream(SCHEMA);
        try
        {
            // create the schema
            Schema schema = SchemaBuilder.fromXML(schemaStream, this);
            return schema;
        }
        catch (SchemaInitializationException e)
        {
            IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.ColorizerReader_ERROR_LOADING_SCHEMA);
        }
        finally
        {
            // close the input stream
            try
            {
                schemaStream.close();
            }
            catch (IOException e)
            {
            }
        }
        
        return null;
    }
}
