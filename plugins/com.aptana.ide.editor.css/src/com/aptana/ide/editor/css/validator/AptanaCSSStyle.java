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
package com.aptana.ide.editor.css.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.properties.css3.Css3Style;
import org.w3c.css.util.Utf8Properties;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSPlugin;

/**
 * CSS Style.
 * 
 * @author Denis Denisenko
 */
public class AptanaCSSStyle extends Css3Style {
    
    /**
     * Properties file name.
     */
    private static final String PROPERITES_FILE_NAME = "AptanaCSSProperties.properties"; //$NON-NLS-1$
    
    private static final Utf8Properties CSS_PROPERTIES = new Utf8Properties();
    
    static {
        InputStream configStream = 
            StylesheetValidator.class.getResourceAsStream(PROPERITES_FILE_NAME);
        try
        {
            CSS_PROPERTIES.load(configStream);
        } catch (IOException e)
        {
            IdeLog.logError(CSSPlugin.getDefault(), "Unable loading CSS properties", e); //$NON-NLS-1$
        }
    }
	
	/**
	 * Properties map.
	 */
	private final Map<String, CssProperty> properties = new HashMap<String, CssProperty>();


	/**
	 * AptanaCSSStyle constructor.
	 */
	public AptanaCSSStyle() {
	}
	
	/**
	 * Gest property by name.
	 * @param propertyName - property name.
	 * @return property, or null if not found.
	 */
	public CssProperty getProperty(String propertyName) {
		return properties.get(propertyName);
	}
	
	/**
	 * Gets property by name. Is aware of CSS cascading. 
	 * @param propertyName - property name.
	 * @return property by name.
	 */
	public CssProperty getPropertyCascadingOrder(String propertyName) {
		CssProperty toReturn = properties.get(propertyName);
		if (toReturn == null) 
		{
			toReturn = style.CascadingOrder(
					createNewDefaultInstance(propertyName), style, selector);
		}
		return toReturn;
	}
	
	/**
	 * Sets property value.
	 * @param propertyName - property name.
	 * @param propertyValue - property value.
	 */
	public void setProperty(String propertyName, CssProperty propertyValue) {
		properties.put(propertyName, propertyValue);
	}
	
	/**
	 * Creates new default property instance.
	 * @param propertyName - property name.
	 * @return new property instance.
	 */
	protected CssProperty createNewDefaultInstance(String propertyName) {
		try
        {
            String nameToSearch = propertyName;
            if (propertyName.startsWith("-")) //$NON-NLS-1$
            {
                nameToSearch = propertyName.substring(1);
            }

            String propertyClassName = CSS_PROPERTIES.getProperty(nameToSearch);
            if (propertyClassName == null)
            {
                return null;
            }
            return (CssProperty) Class.forName(propertyClassName).newInstance();
        } 
		catch (InstantiationException e)
		{
			throw new RuntimeException("Error while creating new instance of " //$NON-NLS-1$
					+ this.getClass().getName() + " class", e); //$NON-NLS-1$
		} 
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("Error while creating new instance of " //$NON-NLS-1$
					+ this.getClass().getName() + " class", e); //$NON-NLS-1$
		} 
		catch (ClassNotFoundException e)
        {
		    throw new RuntimeException("Error while creating new instance of " //$NON-NLS-1$
                    + this.getClass().getName() + " class", e); //$NON-NLS-1$
        }
	}
}
