/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

public class ScrolledPageContent extends SharedScrolledComposite {

	private final FormToolkit fToolkit;

	public ScrolledPageContent(Composite parent) {
		this(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}

	public ScrolledPageContent(Composite parent, int style) {
		super(parent, style);

		this.setFont(parent.getFont());

		this.fToolkit = new FormToolkit(parent.getDisplay());

		this.setExpandHorizontal(true);
		this.setExpandVertical(true);

		final Composite body = new Composite(this, SWT.NONE);
		body.addDisposeListener(new DisposeListener() {

			
			public void widgetDisposed(DisposeEvent e) {
				ScrolledPageContent.this.fToolkit.dispose();
			}

		});
		body.setFont(parent.getFont());
		this.setContent(body);
	}

	public void adaptChild(Control childControl) {
		this.fToolkit.adapt(childControl, true, true);
	}

	public Composite getBody() {
		return (Composite) this.getContent();
	}

}
