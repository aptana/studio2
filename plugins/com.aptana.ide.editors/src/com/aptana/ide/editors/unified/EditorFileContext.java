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

import org.eclipse.jface.text.ITypedRegion;

import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.IFileErrorListener;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * Delegating IFileContext implementation for editors so that the real file context can be swapped out if the editor's
 * input is switched to a different file.
 * 
 * @author Spike Washburn
 */
public class EditorFileContext implements IFileService
{
	private IFileService _fileService;

	/**
	 * EditorFileContext
	 */
	public EditorFileContext()
	{
		this._fileService = null;
	}

	/**
	 * EditorFileContext
	 * 
	 * @param fileContext
	 */
	public EditorFileContext(IFileService fileContext)
	{
		this._fileService = fileContext;
	}

	/**
	 * getFileContext
	 * 
	 * @return IFileContext
	 */
	public IFileService getFileContext()
	{
		return this._fileService;
	}

	/**
	 * setFileContext
	 * 
	 * @param fileContext
	 */
	public void setFileContext(IFileService fileContext)
	{
		this._fileService = fileContext;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#activateForEditing()
	 */
	public void activateForEditing()
	{
		if (this._fileService != null)
		{
			this._fileService.activateForEditing();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addDelayedFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.addDelayedFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addErrorListener(com.aptana.ide.editors.unified.errors.IFileErrorListener)
	 */
	public void addErrorListener(IFileErrorListener listener)
	{
		if (this._fileService != null)
		{
			this._fileService.addErrorListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.addFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#connectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void connectSourceProvider(IFileSourceProvider sourceProvider)
	{
		if (this._fileService != null)
		{
			this._fileService.connectSourceProvider(sourceProvider);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#deactivateForEditing()
	 */
	public void deactivateForEditing()
	{
		if (this._fileService != null)
		{
			this._fileService.deactivateForEditing();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#disconnectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void disconnectSourceProvider(IFileSourceProvider sourceProvider)
	{
		if (this._fileService != null)
		{
			this._fileService.disconnectSourceProvider(sourceProvider);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getFileErrors()
	 */
	public IFileError[] getFileErrors()
	{
		IFileError[] result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getFileErrors();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLanguageService(java.lang.String)
	 */
	public IFileLanguageService getLanguageService(String mimeType)
	{
		IFileLanguageService result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getLanguageService(mimeType);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLexemeList()
	 */
	public LexemeList getLexemeList()
	{
		LexemeList result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getLexemeList();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getParseState()
	 */
	public IParseState getParseState()
	{
		IParseState result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getParseState();
		}
		
		return result; 
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getPartitions()
	 */
	public ITypedRegion[] getPartitions()
	{
		ITypedRegion[] result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getPartitions();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getPartitionAtOffset(int)
	 */
	public ITypedRegion getPartitionAtOffset(int offset)
	{
		ITypedRegion result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getPartitionAtOffset(offset);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getSource()
	 */
	public String getSource()
	{
		String result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getSource();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#isConnected()
	 */
	public boolean isConnected()
	{
		boolean result = false;
		
		if (this._fileService != null)
		{
			result = this._fileService.isConnected();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeDelayedFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.removeDelayedFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeErrorListener(com.aptana.ide.editors.unified.errors.IFileErrorListener)
	 */
	public void removeErrorListener(IFileErrorListener listener)
	{
		if (this._fileService != null)
		{
			this._fileService.removeErrorListener(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.removeFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#setFileErrors(com.aptana.ide.editors.unified.errors.IFileError[])
	 */
	public void setFileErrors(IFileError[] markers)
	{
		if (this._fileService != null)
		{
			this._fileService.setFileErrors(markers);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#updateContent(java.lang.String, int, int)
	 */
	public void updateContent(String insertedSource, int offset, int removeLength)
	{
		if (this._fileService != null)
		{
			this._fileService.updateContent(insertedSource, offset, removeLength);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getDefaultLanguage()
	 */
	public String getDefaultLanguage()
	{
		String result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getDefaultLanguage();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#fireContentChangedEvent(java.lang.String, int, int)
	 */
	public void fireContentChangedEvent(String insertedSource, int offset, int removeLength)
	{
		if (this._fileService != null)
		{
			this._fileService.fireContentChangedEvent(insertedSource, offset, removeLength);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#forceContentChangedEvent()
	 */
	public void forceContentChangedEvent()
	{
		if (this._fileService != null)
		{
			this._fileService.forceContentChangedEvent();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addLongDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addLongDelayedFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.addLongDelayedFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeLongDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeLongDelayedFileListener(IFileContextListener fileListener)
	{
		if (this._fileService != null)
		{
			this._fileService.removeLongDelayedFileListener(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getFileListeners()
	 */
	public IFileContextListener[] getFileListeners()
	{
		IFileContextListener[] result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getFileListeners();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getDelayedFileListeners()
	 */
	public IFileContextListener[] getDelayedFileListeners()
	{
		IFileContextListener[] result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getDelayedFileListeners();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLongDelayedFileListeners()
	 */
	public IFileContextListener[] getLongDelayedFileListeners()
	{
		IFileContextListener[] result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getLongDelayedFileListeners();
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasFileListenerAdded(IFileContextListener listener)
	{
		boolean result = false;
		
		if (this._fileService != null)
		{
			result = this._fileService.hasFileListenerAdded(listener);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasDelayedFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasDelayedFileListenerAdded(IFileContextListener listener)
	{
		boolean result = false;
		
		if (this._fileService != null)
		{
			result = this._fileService.hasDelayedFileListenerAdded(listener);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasLongDelayedFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasLongDelayedFileListenerAdded(IFileContextListener listener)
	{
		boolean result = false;
		
		if (this._fileService != null)
		{
			result = this._fileService.hasLongDelayedFileListenerAdded(listener);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getSourceProvider()
	 */
	public IFileSourceProvider getSourceProvider()
	{
		IFileSourceProvider result = null;
		
		if (this._fileService != null)
		{
			result = this._fileService.getSourceProvider();
		}
		
		return result;
	}
}
