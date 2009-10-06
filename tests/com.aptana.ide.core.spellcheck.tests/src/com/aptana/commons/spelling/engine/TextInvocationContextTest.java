package com.aptana.commons.spelling.engine;

import junit.framework.TestCase;

import org.eclipse.jface.text.source.ISourceViewer;

public class TextInvocationContextTest extends TestCase
{

	public void testConstructor()
	{
		int offset = 0;
		int length = 10;
		ISourceViewer sourceViewer = null;
		TextInvocationContext context = new TextInvocationContext(sourceViewer, offset, length);
		assertEquals(offset, context.getOffset());
		assertEquals(length, context.getLength());
		assertEquals(sourceViewer, context.getSourceViewer());
	}

}
