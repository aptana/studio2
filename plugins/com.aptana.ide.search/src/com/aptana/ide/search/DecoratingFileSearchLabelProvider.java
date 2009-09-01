/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.ui.PlatformUI;

/**
 * @author Pavel Petrochenko
 *
 */
public class DecoratingFileSearchLabelProvider extends DecoratingLabelProvider
{

	/**
	 * @param provider
	 */
	public DecoratingFileSearchLabelProvider(FileLabelProvider provider)
	{
			super(provider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

}
