/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Aptana, Inc. - modifications
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import org.eclipse.jface.text.IDocument;

public class ContentAssistInvocationContext {

	IDocument document;
	int offset;

	public ContentAssistInvocationContext(IDocument document, int offset) {
		super();
		this.document = document;
		this.offset = offset;
	}

	public IDocument getDocument() {
		return this.document;
	}

	public int getInvocationOffset() {
		return this.offset;
	}

}
