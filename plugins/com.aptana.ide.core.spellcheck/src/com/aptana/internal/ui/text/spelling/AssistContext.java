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

import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;

public class AssistContext implements IInvocationContext,
		IQuickAssistInvocationContext {

	Object object;
	private final int offset;
	private final int length;
	private final ISourceViewer viewer;

	public AssistContext(Object object, ISourceViewer sourceViewer, int offset,
			int length) {
		this.viewer = sourceViewer;
		this.object = object;
		this.offset = offset;
		this.length = length;
	}

	public ISourceViewer getViewer() {
		return this.viewer;
	}

	
	public int getSelectionLength() {
		return this.length;
	}

	
	public int getSelectionOffset() {
		return this.offset;
	}

	
	public int getLength() {
		return this.length;
	}

	
	public int getOffset() {
		return this.offset;
	}

	
	public ISourceViewer getSourceViewer() {
		return this.viewer;
	}

}
