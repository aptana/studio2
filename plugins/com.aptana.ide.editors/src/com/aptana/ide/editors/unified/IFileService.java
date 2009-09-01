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
 * Tools to work with files generically
 */
public interface IFileService
{
	/**
	 * Retrieves tools specific to the language of this file
	 * 
	 * @param mimeType
	 * @return IFileLanguageService
	 */
	IFileLanguageService getLanguageService(String mimeType);

	/**
	 * Called whenever source content is updated
	 * 
	 * @param insertedSource
	 * @param offset
	 * @param removeLength
	 */
	void updateContent(String insertedSource, int offset, int removeLength);

	/**
	 * getLexemeList
	 * 
	 * @return LexemeList
	 */
	LexemeList getLexemeList();

	/**
	 * getParseState
	 * 
	 * @return IParseState
	 */
	IParseState getParseState();

	/**
	 * Supports language changes within a file
	 * 
	 * @return Partitions
	 */
	ITypedRegion[] getPartitions();

	/**
	 * Returns the partition at the given offset
	 * 
	 * @param offset
	 * @return Returns the partition at the given offset
	 */
	ITypedRegion getPartitionAtOffset(int offset);

	/**
	 * Adds file listener
	 * 
	 * @param fileListener
	 */
	void addFileListener(IFileContextListener fileListener);

	/**
	 * Removes file listener
	 * 
	 * @param fileListener
	 */
	void removeFileListener(IFileContextListener fileListener);

	/**
	 * Adds file listener that is not time sensitive. These can happen after the edit is complete, and the UI is
	 * updated, on a separate thread. eg. error marking
	 * 
	 * @param fileListener
	 */
	void addDelayedFileListener(IFileContextListener fileListener);

	/**
	 * Removes file listener that is not time sensitive. These can happen after the edit is complete, and the UI is
	 * updated, on a separate thread. eg. error marking
	 * 
	 * @param fileListener
	 */
	void removeDelayedFileListener(IFileContextListener fileListener);

	/**
	 * This file listener waits even longer than delayed, usesful for things that just don't need to happen in 'real
	 * time' or 'near real time', like problems and outliner
	 * 
	 * @param fileListener
	 */
	void addLongDelayedFileListener(IFileContextListener fileListener);

	/**
	 * removeLongDelayedFileListener
	 * 
	 * @param fileListener
	 */
	void removeLongDelayedFileListener(IFileContextListener fileListener);

	/**
	 * getFileListeners
	 * 
	 * @return IFileContextListener[]
	 */
	IFileContextListener[] getFileListeners();

	/**
	 * hasFileListenerAdded
	 * 
	 * @param listener
	 * @return boolean
	 */
	boolean hasFileListenerAdded(IFileContextListener listener);

	/**
	 * getDelayedFileListeners
	 * 
	 * @return IFileContextListener[]
	 */
	IFileContextListener[] getDelayedFileListeners();

	/**
	 * hasDelayedFileListenerAdded
	 * 
	 * @param listener
	 * @return boolean
	 */
	boolean hasDelayedFileListenerAdded(IFileContextListener listener);

	/**
	 * getLongDelayedFileListeners
	 * 
	 * @return IFileContextListener[]
	 */
	IFileContextListener[] getLongDelayedFileListeners();

	/**
	 * hasLongDelayedFileListenerAdded
	 * 
	 * @param listener
	 * @return boolean
	 */
	boolean hasLongDelayedFileListenerAdded(IFileContextListener listener);

	/**
	 * setFileErrors
	 * 
	 * @param markers
	 */
	void setFileErrors(IFileError[] markers);

	/**
	 * getFileErrors
	 * 
	 * @return IFileError[]
	 */
	IFileError[] getFileErrors();

	/**
	 * addErrorListener
	 * 
	 * @param listener
	 */
	void addErrorListener(IFileErrorListener listener);

	/**
	 * removeErrorListener
	 * 
	 * @param listener
	 */
	void removeErrorListener(IFileErrorListener listener);

	/**
	 * activateForEditing
	 */
	void activateForEditing();

	/**
	 * deactivateForEditing
	 */
	void deactivateForEditing();

	/**
	 * getSource
	 * 
	 * @return String
	 */
	String getSource();

	/**
	 * connectSourceProvider
	 * 
	 * @param sourceProvider
	 */
	void connectSourceProvider(IFileSourceProvider sourceProvider);

	/**
	 * disconnectSourceProvider
	 * 
	 * @param sourceProvider
	 */
	void disconnectSourceProvider(IFileSourceProvider sourceProvider);

	/**
	 * isConnected
	 * 
	 * @return boolean
	 */
	boolean isConnected();

	/**
	 * getDefaultLanguage
	 * 
	 * @return String
	 */
	String getDefaultLanguage();

	/**
	 * fireContentChangedEvent
	 * 
	 * @param insertedSource
	 * @param offset
	 * @param removeLength
	 */
	void fireContentChangedEvent(String insertedSource, int offset, int removeLength);

	/**
	 * forceContentChangedEvent
	 */
	void forceContentChangedEvent();

	/**
	 * getSourceProvider
	 * 
	 * @return IFileSourceProvider
	 */
	IFileSourceProvider getSourceProvider();
}
