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
package com.aptana.ide.editor.css.validator.properties;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.ApplContext;

import com.aptana.ide.editor.css.validator.AptanaCSSInheritanceProperties;
import com.aptana.ide.editor.css.validator.AptanaCSSStyle;

/**
 * Abstract custom CSS property. 
 * @author Denis Denisenko
 */
public abstract class CustomCSSProperty extends CssProperty {
	
	/**
	 * Property name.
	 */
	private final String propertyName;
	
	/**
	 * AptanaCSSProperty constructor.
	 * @param propertyName - property name.
	 */
	public CustomCSSProperty(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addToStyle(ApplContext ac, CssStyle style) {
		
		//casting
		AptanaCSSStyle aptanaStyle = (AptanaCSSStyle) style;
		
		//if such property is already set, we should add a
		//redefinition warning
	    if (aptanaStyle.getProperty(getPropertyName()) != null)
	    {
	    	aptanaStyle.addRedefinitionWarning(ac, this);
	    }
	    
	    //adding self
	    aptanaStyle.setProperty(getPropertyName(), this);
	}

	/**
	 * {@inheritDoc}
	 */
	public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
		//casting
		AptanaCSSStyle aptanaStyle = (AptanaCSSStyle) style;
		
		if (resolve) {
			//getting property using cascading order
			return aptanaStyle.getPropertyCascadingOrder(getPropertyName());
		} else {
			//getting plain property value
			return aptanaStyle.getProperty(getPropertyName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean Inherited() {
		//checking inheritance properties
		return AptanaCSSInheritanceProperties.getInheritance(this);
	}

	/**
	  * {@inheritDoc}
	  */
	public final String getPropertyName() {
		return propertyName;
	}
	
	/**
     * {@inheritDoc}
     */
    public boolean equals(CssProperty property) {
        //hashCode() is finalized in super class,
        //so there is no way to redefine it here.
        //anyway the worst can happen is a bad hash function for the properties.
        if (get() == null) 
        {
            return false;
        }
        
        return get().equals(property.get());
    }

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if (get() == null)
		{
			return null;
		}
		
		return get().toString();
	}
	
	/**
	 * W3C validator does not accept CSS properties that start from "-" sign.
	 * That makes custom properties implementation in need to bypass such a restriction.
	 * 
	 * This method allows getting property name without the starting minus sign if any.
	 * @return
	 */
	protected String getPropertyNameNoMinus()
	{
	   String propertyName = getPropertyName();
	   if (propertyName == null)
	   {
	       return propertyName;
	   }
	   
	   if (propertyName.startsWith("-")) //$NON-NLS-1$
	   {
	       return propertyName.substring(1);
	   }
	   
	   return propertyName;
	}
}
