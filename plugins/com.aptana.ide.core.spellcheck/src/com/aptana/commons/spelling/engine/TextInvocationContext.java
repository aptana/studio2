package com.aptana.commons.spelling.engine;

import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;


/**
 * Text quick assist invocation context.
 * <p>
 * Clients may extend this class to add additional context information.
 * </p>
 * 
 * @since 3.3
 */
public class TextInvocationContext implements IQuickAssistInvocationContext {

	private ISourceViewer fSourceViewer;
	private int fOffset;
	private int fLength;
	
	public TextInvocationContext(ISourceViewer sourceViewer, int offset, int length) {
		fSourceViewer= sourceViewer;
		fOffset= offset;
		fLength= length;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext#getOffset()
	 */
	public int getOffset() {
		return fOffset;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext#getLength()
	 */
	public int getLength() {
		return fLength;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext#getSourceViewer()
	 */
	public ISourceViewer getSourceViewer() {
		return fSourceViewer;
	}
	
}
