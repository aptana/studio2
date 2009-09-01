package com.aptana.ide.editor.css.validator.properties;

import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssValue;

/**
 * Converter from number to length.
 * @author Denis Denisenko
 */
class NumberToLenghtConverter implements CSSValueTypeConverter
{
    /**
      * {@inheritDoc}
      */
    public CssValue convert(CssValue in)
    {
        if (in instanceof CssNumber)
        {
            try
            {
                return ((CssNumber) in).getLength();
            } catch (InvalidParamException e)
            {
                return null;
            }
        }
        
        return null;
    }
}