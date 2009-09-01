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
package com.aptana.ide.editor.scriptdoc.parsing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.ErrorMessage;

/**
 * @author Robin Debreuil
 */
public abstract class DocumentationBase implements IDocumentation
{
	private static final String SDOC_LINE_START = " * "; //$NON-NLS-1$
	
	private String fAuthor = ""; //$NON-NLS-1$
	private String fName = ""; //$NON-NLS-1$
	private String fDescription = ""; //$NON-NLS-1$
	private String fVersion = ""; //$NON-NLS-1$
	private List<String> fSees;
	private List<String> fSDocLocations;
	private int fType = IDocumentation.TYPE_FUNCTION; // default type
	private HashMap<String, ArrayList<CodeLocation>> fID;

	private transient List<ErrorMessage> fErrors;
	private List<String> fExamples; //$NON-NLS-1$
	private String fRemarks = ""; //$NON-NLS-1$
	private String userAgent = ""; //$NON-NLS-1$
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getName()
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setName(java.lang.String)
	 */
	public void setName(String value)
	{
		fName = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getDescription()
	 */
	public String getDescription()
	{
		return fDescription;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setDescription(java.lang.String)
	 */
	public void setDescription(String value)
	{
		fDescription = (value == null) ? "" : value; //$NON-NLS-1$
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getExample()
	 * @deprecated Use the array method instead
	 */
	public String getExample()
	{
		String[] examples = getExamples();
		return StringUtils.join(FileUtils.NEW_LINE + FileUtils.NEW_LINE, examples);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getExample()
	 */
	public String[] getExamples()
	{
		if(fExamples != null)
		{
			return fExamples.toArray(new String[0]);
		}
		else
		{
			return new String[0];
		}
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setExample(java.lang.String)
	 */
	public void addExample(String value)
	{
		if (fExamples == null)
		{
			fExamples = new ArrayList<String>();
		}

		fExamples.add(value);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getRemarks()
	 */
	public String getRemarks()
	{
		return fRemarks;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setRemarks(java.lang.String)
	 */
	public void setRemarks(String value)
	{
		fRemarks = (value == null) ? "" : value; //$NON-NLS-1$
	}
	
	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getAuthor()
	 */
	public String getAuthor()
	{
		return fAuthor;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setAuthor(java.lang.String)
	 */
	public void setAuthor(String value)
	{
		fAuthor = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getVersion()
	 */
	public String getVersion()
	{
		return fVersion;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setVersion(java.lang.String)
	 */
	public void setVersion(String value)
	{
		fVersion = (value == null) ? "" : value; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getSees()
	 */
	public String[] getSees()
	{
		if (fSees == null)
		{
			return new String[0];
		}
		return fSees.toArray(new String[0]);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#addSee(java.lang.String)
	 */
	public void addSee(String value)
	{
		value = (value == null) ? "" : value; //$NON-NLS-1$

		if (fSees == null)
		{
			fSees = new ArrayList<String>();
		}

		fSees.add(value);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getErrors()
	 */
	public ErrorMessage[] getErrors()
	{
		if (fErrors == null)
		{
			return new ErrorMessage[0];
		}

		return fErrors.toArray(new ErrorMessage[0]);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#clearErrors()
	 */
	public void clearErrors()
	{
		fErrors.clear();
		fErrors = null;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#addError(com.aptana.ide.parsing.ErrorMessage)
	 */
	public void addError(ErrorMessage e)
	{
		if (fErrors == null)
		{
			fErrors = new ArrayList<ErrorMessage>();
		}

		fErrors.add(e);
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getDocumentType()
	 */
	public int getDocumentType()
	{
		return fType;
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#setDocumentType(int)
	 */
	public void setDocumentType(int type)
	{
		fType = type;
	}

	/**
	 * @return Returns the userAgent.
	 */
	public String getUserAgent()
	{
		return userAgent ;
	}

	/**
	 * @param userAgent The userAgent to set.
	 */
	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}

	/**
	 * @param id 
	 * @return Returns the fID.
	 */
	public CodeLocation[] getID(String id)
	{
		if(fID == null)
		{
			return null;
		}
		
		if(fID.containsKey(id))
		{
			ArrayList<CodeLocation> al = fID.get(id);
			
			return al.toArray(new CodeLocation[0]);
		}
		
		return null;
	}

	/**
	 * @param id The ID to set.
	 * @param location 
	 */
	public void setID(String id, CodeLocation location) 
	{
		if(fID == null)
		{
			fID = new HashMap<String, ArrayList<CodeLocation>>();
		}
		
		if(fID.containsKey(id))
		{
			ArrayList<CodeLocation> al = fID.get(id);
			al.add(location);
		}
		else
		{
			ArrayList<CodeLocation> list = new ArrayList<CodeLocation>();
			list.add(location);
			fID.put(id, list);
		}
		
	}
	/**
	 * Gets the String IDs for all the @id tags in this doc object.
	 * @return Returns the String IDs for all the @id tags in this doc object.
	 */
	public String[] getIDs()
	{
		if(fID == null)
		{
			return new String[0];
		}
		
		return fID.keySet().toArray(new String[0]);
	}
	
	/**
	 * Read in a binary representation of this object
	 * 
	 * @param input
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		this.fAuthor = input.readUTF();
		this.fName = input.readUTF();
		this.fDescription = input.readUTF();
		this.addExample(input.readUTF());
		this.fRemarks = input.readUTF();
		this.fVersion = input.readUTF();

		int size = input.readInt();

		if (size > 0)
		{
			this.fSees = new ArrayList<String>();
			
			for (int i = 0; i < size; i++)
			{
				this.fSees.add(input.readUTF());
			}
		}
	}

	public String toSource()
	{
		SourceWriter writer = new SourceWriter();
		
		// open comment
		writer.println("/**"); //$NON-NLS-1$
		
		// do body
		this.printBody(writer);
		
		// close comment
		writer.println(" */"); //$NON-NLS-1$
		
		// return result
		return writer.toString();
	}
	
	/**
	 * printBody
	 * 
	 * @param writer
	 */
	protected void printBody(SourceWriter writer)
	{
		this.printTag(writer, "@author", this.fAuthor); //$NON-NLS-1$
		this.printTag(writer, "@name", this.fName); //$NON-NLS-1$
		this.printTag(writer, "@description", this.fDescription); //$NON-NLS-1$
		this.printTag(writer, "@version", this.fVersion); //$NON-NLS-1$
		this.printTags(writer, "@see", this.fSees); //$NON-NLS-1$
		this.printTags(writer, "@sdoc", this.fSDocLocations); //$NON-NLS-1$
		this.printTags(writer, "@example", this.fExamples); //$NON-NLS-1$
		this.printTag(writer, "@remarks", this.fRemarks); //$NON-NLS-1$
		this.printTag(writer, "@userAgent", this.userAgent); //$NON-NLS-1$
	}
	
	/**
	 * printTag
	 * 
	 * @param writer
	 * @param tagName
	 * @param flag
	 */
	protected void printTag(SourceWriter writer, String tagName, Boolean flag)
	{
		if (flag)
		{
			writer.print(SDOC_LINE_START).println(tagName);
		}
	}
	
	/**
	 * printTag
	 * 
	 * @param writer
	 * @param tagName
	 * @param content
	 */
	protected void printTag(SourceWriter writer, String tagName, String content)
	{
		if (content != null && content.length() > 0)
		{
			writer.print(SDOC_LINE_START).print(tagName).print(" ").println(content); //$NON-NLS-1$
		}
	}
	
	/**
	 * printTag
	 * 
	 * @param writer
	 * @param tagName
	 * @param typeInfo
	 */
	protected void printTag(SourceWriter writer, String tagName, TypedDescription typeInfo)
	{
		if (typeInfo != null)
		{
			String name = typeInfo.getName();
			String description = typeInfo.getDescription();
			String[] types = typeInfo.getTypes();
			
			if (types != null && types.length > 0)
			{
				writer.print(SDOC_LINE_START).print(tagName).print(" {"); //$NON-NLS-1$
				
				if (types != null && types.length > 0)
				{
					writer.print(types[0]);
					
					for (int i = 1; i < types.length; i++)
					{
						writer.print("|").print(types[i]); //$NON-NLS-1$
					}
				}
				
				writer.print("}"); //$NON-NLS-1$
				
				if (name != null && name.length() > 0)
				{
					writer.print(" ").print(name); //$NON-NLS-1$
				}
				
				if (description != null && description.length() > 0)
				{
					writer.println();
					writer.print(SDOC_LINE_START).print("     ").print(description); //$NON-NLS-1$
				}

				writer.println();
			}
		}
	}
	
	/**
	 * printTags
	 * 
	 * @param writer
	 * @param tagName
	 * @param values
	 */
	protected void printTags(SourceWriter writer, String tagName, List<String> values)
	{
		if (values != null)
		{
			for (String value : values)
			{
				this.printTag(writer, tagName, value);
			}
		}
	}
	
	/**
	 * Write out a binary representation of this object
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		output.writeUTF(this.fAuthor);
		output.writeUTF(this.fName);
		output.writeUTF(this.fDescription);
		output.writeUTF(this.getExample());
		output.writeUTF(this.fRemarks);
		output.writeUTF(this.fVersion);

		if (this.fSees != null)
		{
			output.writeInt(this.fSees.size());

			for (int i = 0; i < this.fSees.size(); i++)
			{
				output.writeUTF(this.fSees.get(i));
			}
		}
		else
		{
			output.writeInt(0);
		}
	}

	/**
	 * @see com.aptana.ide.metadata.IDocumentation#addSDocLocation(java.lang.String)
	 */
	public void addSDocLocation(String value)
	{		
		value = (value == null) ? "" : value; //$NON-NLS-1$

		if (fSDocLocations == null)
		{
			fSDocLocations = new ArrayList<String>();
		}
	
		fSDocLocations.add(value);
	}	
	/**
	 * @see com.aptana.ide.metadata.IDocumentation#getSDocLocations()
	 */
	public String[] getSDocLocations()
	{
		if (fSDocLocations == null)
		{
			return new String[0];
		}
		return fSDocLocations.toArray(new String[0]);
	}

}
