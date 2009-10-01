/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.ide.filesystem.s3;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.epl.IMemento;
import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 * @author cwilliams
 */
public class S3ConnectionPoint extends ConnectionPoint
{

	public static final String TYPE = "s3"; //$NON-NLS-1$

	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$
	private static final String ELEMENT_ACCESS_KEY = "accessKey"; //$NON-NLS-1$

	private IPath path = Path.ROOT;
	private String accessKey = StringUtils.EMPTY;
	private char[] password;

	/**
	 * Default constructor
	 */
	public S3ConnectionPoint()
	{
		super(TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.io.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento)
	{
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_PATH);
		if (child != null)
		{
			if (child.getTextData() == null)
				path = Path.ROOT;
			else
				path = Path.fromPortableString(child.getTextData());
		}
		child = memento.getChild(ELEMENT_ACCESS_KEY);
		if (child != null)
		{
			accessKey = child.getTextData();
		}
		if (CoreIOPlugin.getAuthenticationManager().hasPersistent(getAccessKey()))
			password = CoreIOPlugin.getAuthenticationManager().getPassword(getAccessKey());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.io.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento)
	{
		super.saveState(memento);
		if (!Path.ROOT.equals(path))
		{
			memento.createChild(ELEMENT_PATH).putTextData(path.toPortableString());
		}
		if (accessKey.length() != 0)
		{
			memento.createChild(ELEMENT_ACCESS_KEY).putTextData(accessKey);
		}
		CoreIOPlugin.getAuthenticationManager().setPassword(getAccessKey(), password, true);
	}

	public String getAccessKey()
	{
		return accessKey;
	}

	public void setAccessKey(String accessKey)
	{
		this.accessKey = accessKey;
		notifyChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getPath()
	 */
	public IPath getPath()
	{
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setPath(org.eclipse.core.runtime.IPath)
	 */
	public void setPath(IPath path)
	{
		this.path = path;
		notifyChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getPassword()
	 */
	public char[] getPassword()
	{
		return password;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setPassword(char[])
	 */
	public void setPassword(char[] password)
	{
		this.password = password;
		notifyChanged();
	}

	@Override
	public URI getRootURI()
	{
		try
		{
			return new URI("s3", (getAccessKey() + ":" + new String(getOrPromptForPassword())), "s3.amazonaws.com", -1,
					getPath().toString(), (String) null, (String) null);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private char[] getOrPromptForPassword()
	{
		if (getPassword() != null)
			return getPassword();
		setPassword(CoreIOPlugin.getAuthenticationManager().promptPassword(getAccessKey(), getAccessKey(),
				"Gimme yer Secret Access Key!", "I wants it now!"));
		return getPassword();
	}

}
