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
package com.aptana.ide.editors.unified.colorizer;

/**
 * Interface containing constants for serialization of color info.
 * @author Denis Denisenko
 */
public interface ColorizationConstants
{
    /**
     * Language element.
     */
    String LANGUAGE = "language"; //$NON-NLS-1$

    /**
     * Colorizer element.
     */
    String COLORIZER = "colorizer"; //$NON-NLS-1$

    /**
     * LANGUAGE_ATTR
     */
    String LANGUAGE_ATTR = LANGUAGE; //$NON-NLS-1$

    /**
     * COLORIZATION_ELEMENT
     */
    String COLORIZATION_ELEMENT = "colorization"; //$NON-NLS-1$

    /**
     * HANDLER_ELEMENT
     */
    String HANDLER_ELEMENT = "handler"; //$NON-NLS-1$

    /**
     * CLASS_ATTR
     */
    String CLASS_ATTR = "class"; //$NON-NLS-1$

    /**
     * LENGTH_ATTR
     */
    String LENGTH_ATTR = "length"; //$NON-NLS-1$

    /**
     * OFFSET_ATTR
     */
    String OFFSET_ATTR = "offset"; //$NON-NLS-1$

    /**
     * TYPE_ATTR
     */
    String TYPE_ATTR = "type"; //$NON-NLS-1$

    /**
     * CATEGORY_ATTR
     */
    String CATEGORY_ATTR = "category"; //$NON-NLS-1$

    /**
     * STYLE_ATTR
     */
    String STYLE_ATTR = "style"; //$NON-NLS-1$

    /**
     * NAME_ATTR
     */
    String NAME_ATTR = "name"; //$NON-NLS-1$

    /**
     * ID_ATTR
     */
    String ID_ATTR = "id"; //$NON-NLS-1$

    /**
     * File attribute for colorization extension point
     */
    String FILE_ATTR = "file"; //$NON-NLS-1$

    /**
     * DIRECTOR_ATTR
     */
    String DIRECTOR_ATTR = "direction"; //$NON-NLS-1$

    /**
     * FOREGROUND_ATTR
     */
    String FOREGROUND_ATTR = "foreground"; //$NON-NLS-1$

    /**
     * BACKGROUND_ATTR
     */
    String BACKGROUND_ATTR = "background"; //$NON-NLS-1$

    /**
     * FONTWEIGHT_ATTR
     */
    String FONTWEIGHT_ATTR = "font-weight"; //$NON-NLS-1$

    /**
     * FONTSTYLE_ATTR
     */
    String FONTSTYLE_ATTR = "font-style"; //$NON-NLS-1$

    /**
     * TEXTDECORATION_ATTR
     */
    String TEXTDECORATION_ATTR = "text-decoration"; //$NON-NLS-1$

    /**
     * Length keyword
     */
    String LENGTH_KEYWORD = "LENGTH"; //$NON-NLS-1$

    /**
     * LINEHIGHLIGHT_ATTR
     */
    String LINEHIGHLIGHT_ATTR = "line-highlight"; //$NON-NLS-1$

    /**
     * SELECTIONFOREGROUND_ATTR
     */
    String SELECTIONFOREGROUND_ATTR = "selection-foreground"; //$NON-NLS-1$

    /**
     * SELECTIONBACKGROUND_ATTR
     */
    String SELECTIONBACKGROUND_ATTR = "selection-background"; //$NON-NLS-1$

    /**
     * CARETCOLOR_ATTR
     */
    String CARETCOLOR_ATTR = "caret-color"; //$NON-NLS-1$

    /**
     * FOLDING_BACKGROUND_ATTR
     */
    String FOLDING_BACKGROUND_ATTR = "folding-background"; //$NON-NLS-1$

    /**
     * FOLDING_BACKGROUND_ATTR
     */
    String FOLDING_FOREGROUND_ATTR = "folding-foreground"; //$NON-NLS-1$

    /**
     * TOKEN_ELEMENT
     */
    String TOKEN_ELEMENT = "token"; //$NON-NLS-1$

    /**
     * STYLE_ELEMENT
     */
    String STYLE_ELEMENT = "style"; //$NON-NLS-1$

    /**
     * REGION_ELEMENT
     */
    String REGION_ELEMENT = "region"; //$NON-NLS-1$
}
