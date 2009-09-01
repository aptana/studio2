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
package com.aptana.ide.editors.unified;

import java.util.ArrayList;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitionerExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;

import com.aptana.ide.editors.managers.FileContextManager;

/**
 * 
 */
public class UnifiedDocumentPartitioner implements IDocumentPartitioner, IDocumentPartitionerExtension,
		IDocumentPartitionerExtension2, IDocumentPartitionerExtension3
{
	private ITypedRegion[] _partitions;
	private String[] _legalContentTypes;
	private String _sourceURI;
	private IDocument _document;

	/**
	 * UnifiedDocumentPartitioner
	 * 
	 * @param sourceURI
	 */
	public UnifiedDocumentPartitioner(String sourceURI)
	{
		this._sourceURI = sourceURI;
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#connect(org.eclipse.jface.text.IDocument)
	 */
	public void connect(IDocument document)
	{
		connect(document, false);
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension3#connect(org.eclipse.jface.text.IDocument, boolean)
	 */
	public void connect(IDocument document, boolean delayInitialization)
	{
		this._document = document;
		
		if (this._partitions != null && this._partitions.length == 0)
		{
			// !! this is called on multiline tabs
			
			// TODO: may need to refill environment here
			FileService fs = FileContextManager.get(this._sourceURI);
			
			if (fs != null)
			{
				fs.doFullParse();
			}
		}

		setPartitions();
	}

	/**
	 * setPartitions
	 */
	public void setPartitions()
	{
		FileService fs = FileContextManager.get(this._sourceURI);
		
		if (fs != null)
		{
			this._partitions = fs.getPartitions();
		}
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	public void disconnect()
	{
		// !! this is called on multiline tabs
		this._partitions = new TypedRegion[0];
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event)
	{
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean documentChanged(DocumentEvent event)
	{
		documentChanged2(event);
		
		return true;
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getLegalContentTypes()
	 */
	public String[] getLegalContentTypes()
	{
		return this._legalContentTypes;
	}

	/**
	 * @param partitions
	 *            The _legalPartitions to set.
	 */
	public void setLegalContentTypes(String[] partitions)
	{
		this._legalContentTypes = partitions;
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getContentType(int)
	 */
	public String getContentType(int offset)
	{
		return getContentType(offset, false);
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#computePartitioning(int, int)
	 */
	public ITypedRegion[] computePartitioning(int offset, int length)
	{
		return computePartitioning(offset, length, false);
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getPartition(int)
	 */
	public ITypedRegion getPartition(int offset)
	{
		return getPartition(offset, false);
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension#documentChanged2(org.eclipse.jface.text.DocumentEvent)
	 */
	public IRegion documentChanged2(DocumentEvent event)
	{
		FileService fs = FileContextManager.get(this._sourceURI);

		if (fs != null)
		{
			fs.updateContent(event.getText(), event.fOffset, event.getLength());
			this._partitions = fs.getPartitions();
		}

		return null;// getPartition(event.fOffset, false); // only return range on partition change
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension2#getManagingPositionCategories()
	 */
	public String[] getManagingPositionCategories()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension2#getContentType(int, boolean)
	 */
	public String getContentType(int offset, boolean preferOpenPartitions)
	{
		String result = IDocument.DEFAULT_CATEGORY;

		if (this._partitions != null)
		{
			for (int i = 0; i < this._partitions.length; i++)
			{
				ITypedRegion p = this._partitions[i];

				if (p.getOffset() + p.getLength() >= offset)
				{
					result = p.getType();
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension2#getPartition(int, boolean)
	 */
	public ITypedRegion getPartition(int offset, boolean preferOpenPartitions)
	{
		if (this._partitions != null)
		{
			for (int i = 0; i < this._partitions.length; i++)
			{
				ITypedRegion p = this._partitions[i];

				if (p.getOffset() + p.getLength() >= offset)
				{
					return p;
				}
			}
		}
		
		int length = 0;
		
		if (this._document != null)
		{
			length = this._document.getLength();
		}
		
		// this should perhaps be the default language (i.e. from file service,
		// but mimicking getContentType from above)
		return new TypedRegion(0, length, IDocument.DEFAULT_CATEGORY);
	}

	/**
	 * Computes the partitions encapsulated by the current selected text
	 * 
	 * @param offset
	 *            The starting offset of the text selection
	 * @param length
	 *            The length of the text selection
	 * @param includeZeroLengthPartitions
	 *            Include partitions of zero length?
	 * @return An array of the partitions
	 */
	public ITypedRegion[] computePartitioning(int offset, int length, boolean includeZeroLengthPartitions)
	{
		ArrayList<ITypedRegion> pts = new ArrayList<ITypedRegion>();
		
		for (int i = 0; i < _partitions.length; i++)
		{
			ITypedRegion p = _partitions[i];
			int poffset = p.getOffset();
			int plen = p.getLength();
			int pend = poffset + plen;

			// default is whole partition
			int start = poffset;
			int len = plen;

			if (pend < offset)
			{
				// offset begins after end of current partition
				continue;
			}
			else if (poffset > offset + length)
			{
				// current partition begins after offset + length
				break;
			}

			if (pend > offset + length)
			{
				// end of current partition is after current selection
				len = offset + length - poffset;
			}
			if (poffset < offset)
			{
				// middle of first good partition
				start = offset;
				len = pend - offset;
			}
			if (poffset < offset && pend > offset + length)
			{
				// the text selection is encapsulated in a single partition
				start = offset;
				len = length;
			}
			if (includeZeroLengthPartitions)
			{
				pts.add(new TypedRegion(start, len, p.getType()));
			}
			else if (len > 0)
			{
				pts.add(new TypedRegion(start, len, p.getType()));
			}

		}
		
		return pts.toArray(new ITypedRegion[pts.size()]);
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension3#startRewriteSession(org.eclipse.jface.text.DocumentRewriteSession)
	 */
	public void startRewriteSession(DocumentRewriteSession session) throws IllegalStateException
	{
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension3#stopRewriteSession(org.eclipse.jface.text.DocumentRewriteSession)
	 */
	public void stopRewriteSession(DocumentRewriteSession session)
	{
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitionerExtension3#getActiveRewriteSession()
	 */
	public DocumentRewriteSession getActiveRewriteSession()
	{
		return null;
	}
}
