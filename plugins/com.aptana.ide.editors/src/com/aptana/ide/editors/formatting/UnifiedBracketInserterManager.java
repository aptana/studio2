/**
 * 
 */package com.aptana.ide.editors.formatting;

import java.util.HashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class UnifiedBracketInserterManager implements VerifyKeyListener {

	private String partitioning;
	private HashMap processors = new HashMap();
	private ISourceViewer sourceViewer;
	
	public UnifiedBracketInserterManager(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
	}

	/**
	 * 
	 * @param docPart
	 */
	public void setDocumentPartitioning(String docPart) {
		this.partitioning = docPart;
	}

	/**
	 * 
	 * @return
	 */
	public String getDocumentPartitioning()
	{
		return partitioning;
	}
	
	/**
	 * 
	 * @param processor
	 * @param contentType
	 */
	public void setBracketInserter(IUnifiedBracketInserter processor, String contentType) {
		if (processors == null)
		{
			processors = new HashMap();
		}

		if (processor == null)
		{
			processors.remove(contentType);
		}
		else
		{
			processors.put(contentType, processor);
		}
	}

	/**
	 * 
	 * @param contentType
	 * @return
	 */
	public IUnifiedBracketInserter getBracketInserter(String contentType)
	{
		if (processors == null)
		{
			return null;
		}

		return (IUnifiedBracketInserter) processors.get(contentType);
	}
	
	/**
	 * 
	 */
	public void verifyKey(VerifyEvent event) {
		final Point selection= sourceViewer.getSelectedRange();
		final int offset= selection.x;

		IUnifiedBracketInserter inserter = getBracketInserterFromOffset(offset);
		if(inserter != null)
		{
			inserter.verifyKey(event);
		}
	}

	/**
	 * 
	 * @param offset
	 * @return
	 */
	private IUnifiedBracketInserter getBracketInserterFromOffset(int offset)
	{
		try
		{

			IDocument document = sourceViewer.getDocument();
			String type;
			if (document != null)
			{
				type = TextUtilities.getContentType(document, getDocumentPartitioning(), offset, true);
			}
			else
			{
				type = IDocument.DEFAULT_CONTENT_TYPE;
			}

			return getBracketInserter(type);

		}
		catch (BadLocationException x)
		{
		}

		return null;
	}

}
