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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * UnifiedAnnotationHover
 * 
 * @author Ingo Muschenetz
 */
public class UnifiedAnnotationHover implements IAnnotationHover
{
	/**
	 * UnifiedAnnotationHover
	 */
	public UnifiedAnnotationHover()
	{
	}

	/**
	 * Returns the distance to the ruler line.
	 */
	private int compareRulerLine(Position position, IDocument document, int line)
	{
		if (position.getOffset() > -1 && position.getLength() > -1)
		{
			try
			{
				int xmlAnnotationLine = document.getLineOfOffset(position.getOffset());
				if (line == xmlAnnotationLine)
				{
					return 1;
				}
				if (xmlAnnotationLine <= line
						&& line <= document.getLineOfOffset(position.getOffset() + position.getLength()))
				{
					return 2;
				}
			}
			catch (BadLocationException x)
			{
			}
		}

		return 0;
	}

	/**
	 * Returns one marker which includes the ruler's line of activity.
	 */
	private List getXMLAnnotationsForLine(ISourceViewer viewer, int line)
	{
		IDocument document = viewer.getDocument();
		IAnnotationModel model = viewer.getAnnotationModel();

		if (model == null)
		{
			return null;
		}

		List exact = new ArrayList();

		Iterator e = model.getAnnotationIterator();
		Map messagesAtPosition = new HashMap();
		while (e.hasNext())
		{
			Object o = e.next();
			if (o instanceof Annotation)
			{
				Annotation a = (Annotation) o;
				Position position = model.getPosition(a);
				if (position == null)
				{
					continue;
				}

				if (isDuplicateXMLAnnotation(messagesAtPosition, position, a.getText()))
				{
					continue;
				}

				switch (compareRulerLine(position, document, line))
				{
					case 1:
						exact.add(a);
						break;
					default:
						break;
				}
			}
		}

		return exact;
	}

	/**
	 * isDuplicateXMLAnnotation
	 *
	 * @param messagesAtPosition
	 * @param position
	 * @param message
	 * @return boolean
	 */
	private boolean isDuplicateXMLAnnotation(Map messagesAtPosition, Position position, String message)
	{
		if (messagesAtPosition.containsKey(position))
		{
			Object value = messagesAtPosition.get(position);
			if (message.equals(value))
			{
				return true;
			}

			if (value instanceof List)
			{
				List messages = (List) value;
				if (messages.contains(message))
				{
					return true;
				}
				messages.add(message);
			}
			else
			{
				ArrayList messages = new ArrayList();
				messages.add(value);
				messages.add(message);
				messagesAtPosition.put(position, messages);
			}
		}
		else
		{
			messagesAtPosition.put(position, message);
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber)
	{
		List xmlAnnotations = getXMLAnnotationsForLine(sourceViewer, lineNumber);
		if (xmlAnnotations != null)
		{

			if (xmlAnnotations.size() == 1)
			{

				// optimization
				Annotation xmlAnnotation = (Annotation) xmlAnnotations.get(0);
				String message = xmlAnnotation.getText();
				if (message != null && message.trim().length() > 0)
				{
					return formatSingleMessage(message);
				}

			}
			else
			{

				List messages = new ArrayList(xmlAnnotations.size());
				Iterator e = xmlAnnotations.iterator();
				while (e.hasNext())
				{
					Annotation xmlAnnotation = (Annotation) e.next();
					String message = xmlAnnotation.getText();
					if (message != null && message.trim().length() > 0)
					{
						messages.add(message.trim());
					}
				}

				if (messages.size() == 1)
				{
					return formatSingleMessage((String) messages.get(0));
				}

				if (messages.size() > 1)
				{
					return formatMultipleMessages(messages);
				}
			}
		}

		return null;
	}

	/**
	 * Formats a message as HTML text.
	 */
	private String formatSingleMessage(String message)
	{
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	/**
	 * Formats several message as HTML text.
	 */
	private String formatMultipleMessages(List messages)
	{
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent("Multiple items at this position:")); //$NON-NLS-1$

		HTMLPrinter.startBulletList(buffer);
		Iterator e = messages.iterator();
		while (e.hasNext())
		{
			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
		}
		HTMLPrinter.endBulletList(buffer);

		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	/**
	 * HTMLPrinter
	 * 
	 * @author Ingo Muschenetz
	 */
	static final class HTMLPrinter
	{
		/**
		 * HTMLPrinter
		 */
		private HTMLPrinter()
		{
		}

		/**
		 * replace
		 *
		 * @param text
		 * @param c
		 * @param s
		 * @return String
		 */
		private static String replace(String text, char c, String s)
		{

			int previous = 0;
			int current = text.indexOf(c, previous);

			if (current == -1)
			{
				return text;
			}

			StringBuffer buffer = new StringBuffer();
			while (current > -1)
			{
				buffer.append(text.substring(previous, current));
				buffer.append(s);
				previous = current + 1;
				current = text.indexOf(c, previous);
			}
			buffer.append(text.substring(previous));

			return buffer.toString();
		}

		/**
		 * convertToHTMLContent
		 *
		 * @param content
		 * @return String
		 */
		public static String convertToHTMLContent(String content)
		{
			content = replace(content, '<', "&lt;"); //$NON-NLS-1$
			return replace(content, '>', "&gt;"); //$NON-NLS-1$
		}

		/**
		 * read
		 *
		 * @param rd
		 * @return String
		 */
		public static String read(Reader rd)
		{
			StringBuffer buffer = new StringBuffer();
			char[] readBuffer = new char[2048];

			try
			{
				int n = rd.read(readBuffer);
				while (n > 0)
				{
					buffer.append(readBuffer, 0, n);
					n = rd.read(readBuffer);
				}
				return buffer.toString();
			}
			catch (IOException x)
			{
			}

			return null;
		}

		/**
		 * insertPageProlog
		 *
		 * @param buffer
		 * @param position
		 */
		public static void insertPageProlog(StringBuffer buffer, int position)
		{
			buffer.insert(position, "<html><body text=\"#000000\" bgcolor=\"#FFFF88\"><font size=-1>"); //$NON-NLS-1$
		}

		/**
		 * addPageProlog
		 *
		 * @param buffer
		 */
		public static void addPageProlog(StringBuffer buffer)
		{
			insertPageProlog(buffer, buffer.length());
		}

		/**
		 * addPageEpilog
		 *
		 * @param buffer
		 */
		public static void addPageEpilog(StringBuffer buffer)
		{
			buffer.append("</font></body></html>"); //$NON-NLS-1$
		}

		/**
		 * startBulletList
		 *
		 * @param buffer
		 */
		public static void startBulletList(StringBuffer buffer)
		{
			buffer.append("<ul>"); //$NON-NLS-1$
		}

		/**
		 * endBulletList
		 *
		 * @param buffer
		 */
		public static void endBulletList(StringBuffer buffer)
		{
			buffer.append("</ul>"); //$NON-NLS-1$
		}

		/**
		 * addBullet
		 *
		 * @param buffer
		 * @param bullet
		 */
		public static void addBullet(StringBuffer buffer, String bullet)
		{
			if (bullet != null)
			{
				buffer.append("<li>"); //$NON-NLS-1$
				buffer.append(bullet);
				buffer.append("</li>"); //$NON-NLS-1$
			}
		}

		/**
		 * addSmallHeader
		 *
		 * @param buffer
		 * @param header
		 */
		public static void addSmallHeader(StringBuffer buffer, String header)
		{
			if (header != null)
			{
				buffer.append("<h5>"); //$NON-NLS-1$
				buffer.append(header);
				buffer.append("</h5>"); //$NON-NLS-1$
			}
		}

		/**
		 * addParagraph
		 *
		 * @param buffer
		 * @param paragraph
		 */
		public static void addParagraph(StringBuffer buffer, String paragraph)
		{
			if (paragraph != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append(paragraph);
			}
		}

		/**
		 * addParagraph
		 *
		 * @param buffer
		 * @param paragraphReader
		 */
		public static void addParagraph(StringBuffer buffer, Reader paragraphReader)
		{
			if (paragraphReader != null)
			{
				addParagraph(buffer, read(paragraphReader));
			}
		}
	}
}
