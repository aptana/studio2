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
package com.aptana.ide.metadata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.ParseNodeBase;

/**
 * The "environment" of metadata. This can be queried for documentation of items.
 * 
 * @author Ingo Muschenetz
 */
public class MetadataRuntimeEnvironment implements com.aptana.ide.parsing.IRuntimeEnvironment, IMetadataEnvironment
{

	private static final String EMPTY = ""; //$NON-NLS-1$
	private ArrayList<MetadataEnvironment> environments = new ArrayList<MetadataEnvironment>();
	private Hashtable<Integer, Hashtable<String, ParseNodeBase>> fileObjects = new Hashtable<Integer, Hashtable<String, ParseNodeBase>>();
	private Hashtable<Integer, Hashtable<String, ParseNodeBase>> cssFileObjects = new Hashtable<Integer, Hashtable<String, ParseNodeBase>>();

	/**
	 * returns an hashtable of all "objects" of interest in all files
	 * 
	 * @return An hashtable of every object
	 */
	public Hashtable<Integer, Hashtable<String, ParseNodeBase>> getFileObjects()
	{
		return fileObjects;
	}

	/**
	 * Collects all ids in the environment and returns them.
	 * 
	 * @return String[]
	 */
	public String[] getAllIds()
	{
		ArrayList<String> ids = new ArrayList<String>();
		Hashtable<Integer, Hashtable<String, ParseNodeBase>> objs = getFileObjects();

		Enumeration<Integer> keys = objs.keys();
		while (keys.hasMoreElements())
		{
			Integer fileIndex = (Integer) keys.nextElement();
			if (fileIndex.intValue() >= 0)
			{
				Hashtable<String, ParseNodeBase> a = objs.get(fileIndex);
				ids.addAll(a.keySet());
			}
		}

		return (String[]) ids.toArray(new String[0]);
	}

	/**
	 * Adds the specified ID to the hashtable of ids
	 * 
	 * @param id
	 * @param fileIndex
	 * @param hn
	 */
	public void addId(String id, int fileIndex, ParseNodeBase hn)
	{
		if (!fileObjects.containsKey(new Integer(fileIndex)))
		{
			Hashtable<String, ParseNodeBase> h = new Hashtable<String, ParseNodeBase>();
			h.put(id, hn);
			fileObjects.put(new Integer(fileIndex), h);
		}
		else
		{
			Hashtable<String, ParseNodeBase> h = fileObjects.get(new Integer(fileIndex));
			if (!h.containsKey(id))
			{
				h.put(id, hn);
			}
		}
	}

	/**
	 * Removes the particular ids of the file in question
	 * 
	 * @param fileIndex
	 */
	public void removeFileIds(int fileIndex)
	{
		fileObjects.remove(new Integer(fileIndex));
	}

	/**
	 * Adds an environment to the metadata collection
	 * 
	 * @param element
	 *            The element to add
	 */
	public void addEnvironment(MetadataEnvironment environment)
	{
		environments.add(environment);
	}

	/**
	 * Removes an environment from the metadata collection
	 * 
	 * @param element
	 *            The element to remove
	 */
	public void removeEnvironment(MetadataEnvironment environment)
	{
		environments.remove(environment);
	}

	/**
	 * Return the list of metadata environments
	 * 
	 * @return
	 */
	public MetadataEnvironment[] getEnvironments()
	{
		return environments.toArray(new MetadataEnvironment[0]);
	}

	/**
	 * Returns all the elements
	 */
	public String[] getAllElements()
	{
		List<String> allElements = new ArrayList<String>();
		for (Iterator<MetadataEnvironment> iterator = environments.iterator(); iterator.hasNext();)
		{
			MetadataEnvironment name = iterator.next();
			ElementMetadata[] elements = name.getAllElements();
			for (int i = 0; i < elements.length; i++)
			{
				ElementMetadata elementMetadata = elements[i];
				String elementName = elementMetadata.getName().toLowerCase();
				if (!allElements.contains(elementName))
				{
					allElements.add(elementName);
				}
			}
		}

		return allElements.toArray(new String[0]);
	}

	/**
	 * Return the set of all fields
	 * 
	 * @return the Hashtable of all "global" fields;
	 */
	public Hashtable<String, FieldMetadata> getGlobalFields()
	{
		Hashtable<String, FieldMetadata> globalFields = new Hashtable<String, FieldMetadata>();
		for (Iterator<MetadataEnvironment> iterator = environments.iterator(); iterator.hasNext();)
		{
			MetadataEnvironment name = iterator.next();
			Hashtable<String, FieldMetadata> fields = name.getGlobalFields();
			Enumeration<FieldMetadata> fieldSet = fields.elements();
			while (fieldSet.hasMoreElements())
			{
				FieldMetadata field = (FieldMetadata) fieldSet.nextElement();
				if (!globalFields.containsKey(field.getName()))
				{
					globalFields.put(field.getName(), field);
				}
				else
				{
					globalFields.get(field.getName()).merge(field);
				}
			}
		}

		return globalFields;
	}

	/**
	 * Return the set of all events
	 * 
	 * @return the Hashtable of all "global" events;
	 */
	public Hashtable<String, EventMetadata> getGlobalEvents()
	{
		Hashtable<String, EventMetadata> globalEvents = new Hashtable<String, EventMetadata>();
		for (Iterator<MetadataEnvironment> iterator = environments.iterator(); iterator.hasNext();)
		{
			MetadataEnvironment name = iterator.next();
			Hashtable<String, EventMetadata> events = name.getGlobalEvents();
			Enumeration<EventMetadata> eventsSet = events.elements();
			while (eventsSet.hasMoreElements())
			{
				EventMetadata field = (EventMetadata) eventsSet.nextElement();
				if (!globalEvents.containsKey(field.getName()))
				{
					globalEvents.put(field.getName(), field);
				}
				else
				{
					globalEvents.get(field.getName()).merge(field);
				}
			}
		}

		return globalEvents;
	}

	/**
	 * returns the specified element
	 * 
	 * @param name
	 *            The element name to get
	 * @return The specified element
	 */
	public ElementMetadata getElement(String tagNameLower)
	{
		return getMergedElement(tagNameLower);
	}

	/**
	 * returns the specified element merged from all of the individual environments
	 * 
	 * @param name
	 *            The element name to get
	 * @return The specified element
	 */
	protected ElementMetadata getMergedElement(String tagNameLower)
	{
		ElementMetadata data = new ElementMetadata();

		for (Iterator<MetadataEnvironment> iterator = environments.iterator(); iterator.hasNext();)
		{
			MetadataEnvironment name = iterator.next();
			ElementMetadata d = name.getElement(tagNameLower);
			if (d != null)
			{
				data.merge(d);
			}
		}

		return data;
	}

	/**
	 * Gets an element from the environment based upon a lexeme
	 * 
	 * @param lexeme
	 *            The environment item
	 * @return An element, or null if not found.
	 */
	public ElementMetadata getElement(Lexeme lexeme)
	{
		String lexemeText = lexeme.getText().replaceAll("</", EMPTY); //$NON-NLS-1$
		lexemeText = lexeme.getText().replaceAll("<", EMPTY); //$NON-NLS-1$

		return getElement(lexemeText);
	}

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param elementName
	 * @return A string containing documentation
	 */
	public String getElementDocumentation(String elementName)
	{
		ElementMetadata element = getElement(elementName);
		return getElementDocumentation(element);
	}

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 * @return A string containing documentation
	 */
	public String getElementDocumentation(ElementMetadata element)
	{
		StringBuffer docText = new StringBuffer();

		docText.append("<b>" + element.getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (element.getFullName() != EMPTY)
		{
			docText.append(" (" + element.getFullName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		docText.append("<br>" + element.getDescription()); //$NON-NLS-1$

		UserAgent[] agents = element.getUserAgents();
		if (agents.length > 0)
		{
			docText.append("<br><br><b>").append(Messages.MetadataEnvironment_Supported_Header).append("</b> "); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				docText.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if (i < agents.length - 1)
				{
					docText.append(", "); //$NON-NLS-1$
				}
			}
		}

		return docText.toString();
	}

	/**
	 * Returns a list of all the platforms this item is supported by
	 * 
	 * @return Returns a list of all the platforms this item is supported by
	 */
	public String[] getUserAgentPlatformNames(String elementName)
	{
		ElementMetadata element = getElement(elementName);
		return element.getUserAgentPlatformNames();
	}

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 *            The element to look at
	 * @return A string containing documentation
	 */
	public String getFieldDocumentation(FieldMetadata element)
	{
		StringBuffer docText = new StringBuffer();

		docText.append("<b>" + element.getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$

		if (element.getType() != EMPTY)
		{
			docText.append(": " + element.getType()); //$NON-NLS-1$
		}

		docText.append("<br>" + element.getDescription()); //$NON-NLS-1$

		UserAgent[] agents = element.getUserAgents();
		if (agents.length > 0)
		{
			docText.append("<br><br><b>").append(Messages.MetadataEnvironment_Supported_Header).append("</b> "); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				docText.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if (i < agents.length - 1)
				{
					docText.append(", "); //$NON-NLS-1$
				}
			}
		}

		return docText.toString();
	}

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 * @return A string containing the documentation
	 */
	public String getEventDocumentation(EventMetadata element)
	{
		StringBuffer docText = new StringBuffer();

		docText.append("<b>" + element.getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$

		if (element.getType() != EMPTY)
		{
			docText.append(": " + element.getType()); //$NON-NLS-1$
		}

		docText.append("<br>" + element.getDescription()); //$NON-NLS-1$

		UserAgent[] agents = element.getUserAgents();
		if (agents.length > 0)
		{
			docText.append("<br><br><b>").append(Messages.MetadataEnvironment_Supported_Header).append("</b> "); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < agents.length; i++)
			{
				UserAgent ua = agents[i];
				docText.append(ua.getPlatform() + " " + ua.getVersion()); //$NON-NLS-1$
				if (i < agents.length - 1)
				{
					docText.append(", "); //$NON-NLS-1$
				}
			}
		}

		return docText.toString();
	}

	public void addClass(String cssClass, int fileIndex, ParseNodeBase hn)
	{
		if (!cssFileObjects.containsKey(new Integer(fileIndex)))
		{
			Hashtable<String, ParseNodeBase> h = new Hashtable<String, ParseNodeBase>();
			h.put(cssClass, hn);
			cssFileObjects.put(new Integer(fileIndex), h);
		}
		else
		{
			Hashtable<String, ParseNodeBase> h = cssFileObjects.get(new Integer(fileIndex));
			if (!h.containsKey(hn))
			{
				h.put(cssClass, hn);
			}
		}

	}

	public List<String> getAllClasses()
	{
		List<String> classes = new ArrayList<String>();
		Hashtable<Integer, Hashtable<String, ParseNodeBase>> objs = cssFileObjects;

		Enumeration<Integer> keys = objs.keys();
		while (keys.hasMoreElements())
		{
			Integer fileIndex = (Integer) keys.nextElement();
			if (fileIndex.intValue() >= 0)
			{
				Hashtable<String, ParseNodeBase> a = objs.get(fileIndex);
				classes.addAll(a.keySet());
			}
		}

		return classes;
	}

}
