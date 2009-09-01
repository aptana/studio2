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
package com.aptana.ide.editors.unified.errors;

import java.util.ArrayList;

import com.aptana.ide.core.StringUtils;

/**
 * FileError
 * 
 * @author Ingo Muschenetz
 */
public class ErrorDescriptor
{
	private String _message;
	private String _fileName;
	private String _folderPath;
	private int _severity = -1;

	/**
	 * Separates the fields
	 */
	public static String FIELD_SEPARATOR = "~~~~"; //$NON-NLS-1$

	/**
	 * Separates the descriptors
	 */
	public static String DESCRIPTOR_SEPARATOR = "####"; //$NON-NLS-1$

	/**
	 * A value indicating we don't care about the severity
	 */
	public static int NO_SEVERITY = -1;

	/**
	 * Creates a new error
	 */
	public ErrorDescriptor()
	{
	}

	/**
	 * gets the message of the error
	 * 
	 * @return String
	 */
	public String getMessage()
	{
		return _message;
	}

	/**
	 * Gets the file name of the error
	 * 
	 * @return String
	 */
	public String getFileName()
	{
		return _fileName;
	}

	/**
	 * Gets the folder path of the error
	 * 
	 * @return String
	 */
	public String getFolderPath()
	{
		return _folderPath;
	}

	/**
	 * Gets the severity of the error
	 * 
	 * @return int
	 */
	public int getSeverity()
	{
		return _severity;
	}

	/**
	 * Sets the file name of the error
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName)
	{
		this._fileName = fileName;
	}

	/**
	 * Sets the folder path of the error
	 * 
	 * @param folderPath
	 */
	public void setFolderPath(String folderPath)
	{
		this._folderPath = folderPath;
	}

	/**
	 * Sets the error message
	 * 
	 * @param message
	 */
	public void setMessage(String message)
	{
		this._message = message;
	}

	/**
	 * Sets the error severity
	 * 
	 * @param severity
	 */
	public void setSeverity(int severity)
	{
		this._severity = severity;
	}

	/**
	 * Returns this descriptor as a string
	 * 
	 * @return String
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		if (getFileName() != null)
		{
			sb.append(getFileName());
		}
		sb.append(FIELD_SEPARATOR);
		if (getFolderPath() != null)
		{
			sb.append(getFolderPath());
		}
		sb.append(FIELD_SEPARATOR);

		sb.append(getSeverity());
		sb.append(FIELD_SEPARATOR);

		if (getMessage() != null)
		{
			sb.append(getMessage());
		}

		return sb.toString();
	}

	/**
	 * Converts the string into an error descriptor
	 * 
	 * @param values
	 */
	public void fromString(String values)
	{
		String[] vals = values.split(FIELD_SEPARATOR, 4);
		if (vals.length != 4)
		{
			throw new IllegalArgumentException(Messages.ErrorDescriptor_MustHaveFourValues);
		}
		if (!StringUtils.EMPTY.equals(vals[0]))
		{
			_fileName = vals[0];
		}

		if (!StringUtils.EMPTY.equals(vals[1]))
		{
			_folderPath = vals[1];
		}

		_severity = Integer.parseInt(vals[2]);

		if (!StringUtils.EMPTY.equals(vals[3]))
		{
			_message = vals[3];
		}
	}

	/**
	 * Returns a string consisting of all error descriptors
	 * 
	 * @param errors
	 * @return String
	 */
	public static String serializeErrorDescriptors(ErrorDescriptor[] errors)
	{
		ArrayList al = new ArrayList();
		for (int i = 0; i < errors.length; i++)
		{
			ErrorDescriptor descriptor = errors[i];
			al.add(descriptor.toString());
		}

		return StringUtils.join(DESCRIPTOR_SEPARATOR, (String[]) al.toArray(new String[0]));
	}

	/**
	 * Returns an array consisting of all error descriptors
	 * 
	 * @param errors
	 * @return ErrorDescriptor[]
	 */
	public static ErrorDescriptor[] deserializeErrorDescriptors(String errors)
	{
		if (StringUtils.EMPTY.equals(errors))
		{
			return new ErrorDescriptor[0];
		}

		ArrayList al = new ArrayList();
		String[] errorDescriptors = errors.split(DESCRIPTOR_SEPARATOR);
		for (int i = 0; i < errorDescriptors.length; i++)
		{
			String descriptor = errorDescriptors[i];
			ErrorDescriptor ed = new ErrorDescriptor();
			ed.fromString(descriptor);
			al.add(ed);
		}

		return (ErrorDescriptor[]) al.toArray(new ErrorDescriptor[0]);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0)
	{
		boolean result = false;

		if (arg0 instanceof ErrorDescriptor)
		{
			ErrorDescriptor s = ((ErrorDescriptor) arg0);

			result = toString().equals(s.toString());
		}
		return result;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	/**
	 * Does this descriptor "match" the specified file error
	 * 
	 * @param error
	 * @return boolean
	 */
	public boolean matchesError(FileError error)
	{
		if (getFileName() != null && !StringUtils.EMPTY.equals(getFileName()))
		{
			if (!getFileName().equals(error.getFileName()))
			{
				return false;
			}
		}

		if (getFolderPath() != null && !StringUtils.EMPTY.equals(getFolderPath()))
		{
			if (!getFolderPath().equals(error.getFolderPath()))
			{
				return false;
			}
		}

		if (getSeverity() != NO_SEVERITY)
		{
			if (getSeverity() != error.getSeverity())
			{
				return false;
			}
		}

		if (getMessage() != null && !StringUtils.EMPTY.equals(getMessage()))
		{
			return error.getMessage().matches(getMessage());
		}

		return false;
	}

}
