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

/**
 * EditableBooleanProperty
 * 
 * @author Ingo Muschenetz
 */
public class EditableBooleanProperty extends EditableComboProperty
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * EditableBooleanProperty
	 * 
	 * @param ID
	 * @param name
	 */
	public EditableBooleanProperty(String ID, String name)
	{
		super(ID, name);
		super.addComboItem(Messages.EditableBooleanProperty_True, Boolean.TRUE);
		super.addComboItem(Messages.EditableBooleanProperty_False, Boolean.FALSE);
	}

	/**
	 * @see com.aptana.ide.editors.views.outline.propertyManager.EditableComboProperty#addComboItem(java.lang.String,
	 *      java.lang.Object)
	 */
	public void addComboItem(String text, Object value)
	{
		// do nothing, the user should not try to
		// add additional values, since this is a true/false field
	}

	/**
	 * Sets the default value of the boolean property
	 * 
	 * @param b
	 *            The new default value
	 */
	public void setDefaultValue(boolean b)
	{
		super.setDefaultValue(Boolean.valueOf(b));
	}

	/**
	 * Sets the current value of the boolean property
	 * 
	 * @param b
	 *            The new value
	 */
	public void setValue(boolean b)
	{
		super.setValue(Boolean.valueOf(b));
	}

	/**
	 * @return The boolean value of the property
	 */
	public Boolean getBooleanValue()
	{
		return (Boolean) getValue();
	}

	/**
	 * This is a convenience method that
	 * 
	 * @return the primative value of the property
	 */
	public boolean isSelected()
	{
		return getBooleanValue().booleanValue();
	}
}
