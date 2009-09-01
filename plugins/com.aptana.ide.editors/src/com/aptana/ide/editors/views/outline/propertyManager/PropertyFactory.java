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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.editors.views.outline.propertyManager;

import java.io.Serializable;

import org.eclipse.jface.viewers.ICellEditorValidator;

import com.aptana.ide.core.StringUtils;

/**
 * PropertyFactory
 * 
 * @author Ingo Muschenetz
 */
public abstract class PropertyFactory implements Serializable
{
	/**
	 * create
	 * 
	 * @return ReadableProperty
	 */
	public abstract ReadableProperty create();

	/**
	 * TOKEN_SIZE_ID
	 */
	public static String TOKEN_SIZE_ID = "TokenSize"; //$NON-NLS-1$

	/**
	 * PHASE_ID
	 */
	public static String PHASE_ID = "Hue"; //$NON-NLS-1$

	/**
	 * TOKEN_SIZE_PROPERTY
	 */
	public static PropertyFactory TOKEN_SIZE_PROPERTY = new PropertyFactory()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ReadableProperty create()
		{
			final EditableIntegerProperty prop = new EditableIntegerProperty(TOKEN_SIZE_ID, Messages.PropertyFactory_TokenSize, 3);

			// token sizes must be odd, so only allow increment of two
			prop.setIncrement(2);
			prop.setMinimum(1);

			prop.setValidator(new TokenSizeValidator(prop));

			return prop;
		}
	};

	/**
	 * PHASE_PROPERTY
	 */
	public static PropertyFactory PHASE_PROPERTY = new PropertyFactory()
	{
		private static final long serialVersionUID = -8716817710115255121L;

		public ReadableProperty create()
		{
			// create properties
			EditableIntegerProperty huePhaseShift = new EditableIntegerProperty(PHASE_ID, Messages.PropertyFactory_PhaseShift, 0);

			huePhaseShift.setMinimum(0);
			huePhaseShift.setMaximum(360);
			huePhaseShift.setIncrement(30);
			return huePhaseShift;
		};
	};

	/**
	 * TokenSizeValidator
	 * 
	 * @author Ingo Muschenetz
	 */
	private final class TokenSizeValidator implements ICellEditorValidator, Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final EditableIntegerProperty prop;

		private TokenSizeValidator(EditableIntegerProperty prop)
		{
			super();
			this.prop = prop;
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
		 */
		public String isValid(Object value)
		{
			int intValue = -1;
			try
			{
				intValue = Integer.parseInt((String) value);
			}
			catch (NumberFormatException exc)
			{
				return Messages.PropertyFactory_TokenSizeMustBeInteger;
			}

			if (intValue < prop.getMinimum())
			{
				return StringUtils.format(Messages.TokenSizeGreaterThan, prop.getMinimum());
			}

			if (intValue > prop.getMaximum())
			{
				return StringUtils.format(Messages.TokenSizeLessThan, prop.getMaximum());
			}

			if (intValue % 2 == 0)
			{
				return Messages.PropertyFactory_TokenMustBeOdd;
			}

			return null;
		}
	}
}
