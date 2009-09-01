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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.ITypedRegion;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.errors.IErrorManager;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.IFileErrorListener;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ILanguageChangeListener;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public class FileService implements IFileService
{
	private Object _parserLock;

	private Map<String, IFileLanguageService> _languageServices;
	private IParseState _parseState;
	private IFileSourceProvider _connectedSourceProvider;
	private IFileSourceProvider _defaultSourceProvider;
	private IParser _parser;
	private IParser _scanner;

	private IPartitionScanner _partitionScanner;
	private List<IFileContextListener> _listeners;
	private List<IFileContextListener> _delayedListeners;
	private List<IFileContextListener> _longDelayedListeners;
	private String _defaultLanguage;
	private ILanguageChangeListener _languageChangeListener;
	private IErrorManager _errorManager;
	private IRedrawRangeListener _redrawListener;

	private IFileError[] _errorMarkers;
	private List<IFileErrorListener> _errorListeners;

	private ITypedRegion[] _partitions;

	/**
	 * FileService
	 * 
	 * @param parser
	 * @param parseState
	 * @param defaultSourceProvider
	 * @param defaultLanguage
	 */
	public FileService(IParser parser, IParseState parseState, IFileSourceProvider defaultSourceProvider,
			String defaultLanguage)
	{
		this._parseState = parseState;
		this._defaultSourceProvider = defaultSourceProvider;
		this._defaultLanguage = defaultLanguage;

		this._parserLock = new Object();
		this._languageServices = new HashMap<String, IFileLanguageService>();
		this._listeners = new ArrayList<IFileContextListener>();
		this._delayedListeners = new ArrayList<IFileContextListener>();
		this._longDelayedListeners = new ArrayList<IFileContextListener>();
		this._errorMarkers = new IFileError[0];
		this._errorListeners = new ArrayList<IFileErrorListener>();

		this.setParser(parser);

		// add delay listener for long parse
		this.addDelayedFileListener(new IFileContextListener()
		{
			/**
			 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
			 */
			public void onContentChanged(FileContextContentEvent evt)
			{
				if (FileService.this._scanner != null && FileService.this._parser != null)
				{
					synchronized (FileService.this)
					{
						synchronized (FileService.this._parserLock)
						{
							IParseState parseState = getParseState();

							if (parseState != null)
							{
								doParse(parseState);
								
								// update any changed lexemes
								for (IRange range : parseState.getUpdateRegions())
								{
									FileService.this._redrawListener.redrawRange(range);
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * addLanguageService
	 * 
	 * @param mimeType
	 * @param service
	 */
	public void addLanguageService(String mimeType, IFileLanguageService service)
	{
		if (this._languageServices.containsKey(mimeType))
		{
			throw new IllegalStateException(Messages.FileService_LanguageServiceRegistered);
		}

		this._languageServices.put(mimeType, service);
	}

	/**
	 * setRedrawRangeListener
	 * 
	 * @param listener
	 */
	public void setRedrawRangeListener(IRedrawRangeListener listener)
	{
		this._redrawListener = listener;
	}
	
	/**
	 * setScanner
	 * 
	 * @param parser
	 */
	public void setScanner(IParser parser)
	{
		this._scanner = parser;
	}

	/**
	 * setParser
	 * 
	 * @param parser
	 */
	public void setParser(IParser parser)
	{
		this._parser = parser;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLanguageService(java.lang.String)
	 */
	public IFileLanguageService getLanguageService(String mimeType)
	{
		return this._languageServices.get(mimeType);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#connectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void connectSourceProvider(final IFileSourceProvider sourceProvider)
	{
		if (this._connectedSourceProvider != null)
		{
			throw new IllegalStateException(StringUtils.format(Messages.FileService_CantConnetSourceProvider,
					new String[] { sourceProvider.getSourceURI(), _connectedSourceProvider.getSourceURI() }));
		}

		this._connectedSourceProvider = sourceProvider;

		visitLanguageServices(new IFileLanguageServiceVisitor()
		{
			public void visit(IFileLanguageService service)
			{
				service.connectSourceProvider(sourceProvider);
			}
		});

		doFullParse();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#disconnectSourceProvider(com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public void disconnectSourceProvider(final IFileSourceProvider sourceProvider)
	{
		if (sourceProvider != null)
		{
			if (this._connectedSourceProvider != sourceProvider)
			{
				if (this._connectedSourceProvider != null && sourceProvider != null)
				{
					throw new IllegalStateException(StringUtils.format(
							Messages.FileService_SourceProviderAlreadyConnected, new String[] {
									sourceProvider.getSourceURI(), _connectedSourceProvider.getSourceURI() }));
				}
				else
				{
					throw new IllegalStateException(Messages.FileService_SourceProviderNotConnected);
				}
			}
		}

		if (getSourceProvider() != null)
		{
			FileContextManager.remove(getSourceProvider().getSourceURI());
		}

		IdleFileChangedNotifier.instance().removeContentChangedEvent(this);
		LongIdleFileChangedNotifier.instance().removeContentChangedEvent(this);

		this._listeners.clear();
		this._delayedListeners.clear();
		this._longDelayedListeners.clear();
		this._errorListeners.clear();

		Object[] vals = this._languageServices.values().toArray();

		for (int i = 0; i < vals.length; i++)
		{
			IFileLanguageService element = (IFileLanguageService) vals[i];

			element.disconnectSourceProvider(sourceProvider);
		}

		this._languageServices.clear();

		visitLanguageServices(new IFileLanguageServiceVisitor()
		{
			public void visit(IFileLanguageService service)
			{
				service.disconnectSourceProvider(sourceProvider);
			}
		});

		if (this._errorManager != null)
		{
			removeLongDelayedFileListener(this._errorManager);
			this._errorManager = null;
		}

		this._partitionScanner = null;
		this._defaultSourceProvider = null;
		this._connectedSourceProvider = null;

		this._parseState = null;
		this._parser = null;
		this._parserLock = null;
		this._partitions = null;

		this._errorMarkers = null;

	}

	/**
	 * @return FileSourceProvider
	 */
	public IFileSourceProvider getSourceProvider()
	{
		return this._connectedSourceProvider != null ? this._connectedSourceProvider : this._defaultSourceProvider;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#isConnected()
	 */
	public boolean isConnected()
	{
		return this._connectedSourceProvider != null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#updateContent(java.lang.String, int, int)
	 */
	public void updateContent(String insertedSource, int offset, int removeLength)
	{
		IFileSourceProvider sourceProvider = getSourceProvider();
		String fullSource;

		try
		{
			IParseState parseState = this.getParseState();

			if (parseState != null)
			{
				fullSource = sourceProvider.getSource();

				parseState.setEditState(fullSource, insertedSource, offset, removeLength);
			}

			synchronized (this)
			{
				synchronized (this._parserLock)
				{
					IPartitionScanner partitionScanner = this.getPartitioner();

					if (partitionScanner != null)
					{
						partitionScanner.startPartitionScan();
					}

					if (parseState != null)
					{
						if (this._scanner != null)
						{
							this.doFastParse(parseState);
						}
						else
						{
							this.doParse(parseState);
						}
					}

					if (partitionScanner != null)
					{
						this._partitions = partitionScanner.endPartitionScan();
					}
				}
			}

			fireContentChangedEvent(insertedSource, offset, removeLength);
		}
		catch (IOException e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.FileService_UpdateContentFailed, e);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLexemeList()
	 */
	public LexemeList getLexemeList()
	{
		if (this._parseState == null)
		{
			return null;
		}

		return this._parseState.getLexemeList();
	}

	/**
	 * Gets the partitioner for this language
	 * 
	 * @return Gets the partitioner for this language
	 */
	protected IPartitionScanner getPartitioner()
	{
		if (this._partitionScanner == null)
		{
			this._partitionScanner = new UnifiedPartitionScanner(this, getDefaultLanguage());
		}

		return this._partitionScanner;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getPartitions()
	 */
	public ITypedRegion[] getPartitions()
	{
		return this._partitions;
	}

	/**
	 * setPartitions
	 * 
	 * @param partitions
	 */
	public void setPartitions(ITypedRegion[] partitions)
	{
		this._partitions = partitions;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getPartitionAtOffset(int)
	 */
	public ITypedRegion getPartitionAtOffset(int offset)
	{
		ITypedRegion result = null;

		for (int i = 0; i < this._partitions.length; i++)
		{
			ITypedRegion p = this._partitions[i];

			if (p.getOffset() <= offset && p.getOffset() + p.getLength() >= offset)
			{
				result = p;

				break;
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getParseState()
	 */
	public IParseState getParseState()
	{
		return this._parseState;
	}

	/**
	 * Parses the file when first connecting to the document
	 */
	public void doFullParse()
	{
		IFileSourceProvider sourceProvider = getSourceProvider();
		String fullSource;

		try
		{
			fullSource = sourceProvider.getSource();

			synchronized (this)
			{
				synchronized (this._parserLock)
				{
					IPartitionScanner partitionScanner = this.getPartitioner();
					IParseState parseState = this.getParseState();

					if (partitionScanner != null)
					{
						partitionScanner.startPartitionScan();
					}

					if (parseState != null)
					{
						LexemeList lexemes = parseState.getLexemeList();

						synchronized (lexemes)
						{
							lexemes.clear();

							parseState.setEditState(fullSource, fullSource, 0, 0);

							this.doParse(parseState);
							
							parseState.clearEditState();
						}
					}

					if (partitionScanner != null)
					{
						this._partitions = partitionScanner.endPartitionScan();
					}
				}
			}

			forceContentChangedEvent(); // always start with cursor at 0
		}
		catch (IOException e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.FileService_DoFullParseFailed, e);
		}
	}

	/**
	 * doParse
	 * 
	 * @param parseState
	 */
	private void doParse(IParseState parseState)
	{
		LexemeList lexemeList = parseState.getLexemeList();

		synchronized (lexemeList)
		{
			if (this._parser != null)
			{
				try
				{
					// save old language change listener
					ILanguageChangeListener oldEventHandler = this._parser.getLanguageChangeListener();

					// attach language change listener for partitioning
					this._parser.setLanguageChangeListener(this._languageChangeListener);

					// perform parse
					this._parser.parse(parseState);

					// restore old language change listener
					this._parser.setLanguageChangeListener(oldEventHandler);
				}
				catch (LexerException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.FileService_DoParseFailedLexer, e);
				}
				catch (ParseException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.FileService_DoParseFailedParse, e);
				}
			}
		}
	}

	/**
	 * doFastParse
	 * 
	 * @param parseState
	 */
	private void doFastParse(IParseState parseState)
	{
		LexemeList lexemeList = parseState.getLexemeList();

		synchronized (lexemeList)
		{
			if (this._scanner != null)
			{
				try
				{
					// save old language change listener
					ILanguageChangeListener oldEventHandler = this._scanner.getLanguageChangeListener();

					// attach language change listener for partitioning
					this._scanner.setLanguageChangeListener(this._languageChangeListener);

					// perform parse
					this._scanner.parse(parseState);

					// restore old language change listener
					this._scanner.setLanguageChangeListener(oldEventHandler);
				}
				catch (LexerException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.FileService_DoParseFailedLexer, e);
				}
				catch (ParseException e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.FileService_DoParseFailedParse, e);
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addFileListener(IFileContextListener fileListener)
	{
		synchronized (this._listeners)
		{
			this._listeners.add(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeFileListener(IFileContextListener fileListener)
	{
		synchronized (this._listeners)
		{
			this._listeners.remove(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addDelayedFileListener(IFileContextListener fileListener)
	{
		synchronized (this._delayedListeners)
		{
			this._delayedListeners.add(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeDelayedFileListener(IFileContextListener fileListener)
	{
		synchronized (this._delayedListeners)
		{
			this._delayedListeners.remove(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addLongDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void addLongDelayedFileListener(IFileContextListener fileListener)
	{
		synchronized (this._longDelayedListeners)
		{
			this._longDelayedListeners.add(fileListener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeLongDelayedFileListener(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public void removeLongDelayedFileListener(IFileContextListener fileListener)
	{
		synchronized (this._longDelayedListeners)
		{
			this._longDelayedListeners.remove(fileListener);
		}
	}

	/**
	 * Event fired every time content is updates
	 * 
	 * @param insertedSource
	 * @param offset
	 * @param removeLength
	 */
	public void fireContentChangedEvent(String insertedSource, int offset, int removeLength)
	{
		FileContextContentEvent evt = createContentChangedEventAndFire(insertedSource, offset, removeLength);

		// Now fire the event to idle listeners
		IdleFileChangedNotifier.instance().queueContentChangedEvent(this, evt);

		// Now fire the event to long idle listeners
		LongIdleFileChangedNotifier.instance().queueContentChangedEvent(this, evt);
	}

	/**
	 * Event fired to "refresh" the current editor. This might be a candidate for removal.
	 */
	public void forceContentChangedEvent()
	{
		fireContentChangedEvent(StringUtils.EMPTY, 0, 0);
	}

	/**
	 * createContentChangedEventAndFire
	 * 
	 * @param insertedSource
	 * @param offset
	 * @param removeLength
	 * @return FileContextContentEvent
	 */
	private FileContextContentEvent createContentChangedEventAndFire(String insertedSource, int offset, int removeLength)
	{
		IFileContextListener[] listenerArray;

		synchronized (this._listeners)
		{
			listenerArray = this._listeners.toArray(new IFileContextListener[this._listeners.size()]);
		}

		int srcLen = insertedSource == null ? 0 : insertedSource.length();
		int finalOffset = offset + srcLen - removeLength;
		FileContextContentEvent evt = new FileContextContentEvent(this, finalOffset);

		// Fire event to listeners
		fireContentChangedEvent(listenerArray, evt);

		return evt;
	}

	/**
	 * fires the ContentChangedEvent to delayed listeners
	 * 
	 * @param evt
	 */
	protected void fireDelayedContentChangedEvent(FileContextContentEvent evt)
	{
		IFileContextListener[] listenerArray;

		synchronized (this._delayedListeners)
		{
			listenerArray = this._delayedListeners.toArray(new IFileContextListener[this._delayedListeners.size()]);
		}

		this.fireContentChangedEvent(listenerArray, evt);
	}

	/**
	 * fires the ContentChangedEvent to delayed listeners
	 * 
	 * @param evt
	 */
	protected void fireLongDelayedContentChangedEvent(FileContextContentEvent evt)
	{
		IFileContextListener[] listenerArray;

		synchronized (this._longDelayedListeners)
		{
			listenerArray = this._longDelayedListeners.toArray(new IFileContextListener[this._longDelayedListeners
					.size()]);
		}

		this.fireContentChangedEvent(listenerArray, evt);
	}

	/**
	 * fireContentChangedEvent
	 * 
	 * @param listenerArray
	 * @param evt
	 */
	private void fireContentChangedEvent(IFileContextListener[] listenerArray, FileContextContentEvent evt)
	{
		for (IFileContextListener listener : listenerArray)
		{
			try
			{
				listener.onContentChanged(evt);
			}
			catch (Exception e)
			{
				IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.FileService_ErrorOnFireChangedEvent, e);
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getSource()
	 */
	public String getSource()
	{
		try
		{
			return getSourceProvider().getSource();
		}
		catch (IOException e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.FileService_GetSourceFailed, e);

			return StringUtils.EMPTY;
		}
	}

	/**
	 * Returns the dominate language for this language type (eg "text/html" for html, rather than "text/javascript" etc)
	 * 
	 * @return Returns the dominate language for this language type
	 */
	public String getDefaultLanguage()
	{
		return this._defaultLanguage;
	}

	/**
	 * setLanguageChangeListener
	 * 
	 * @param languageChangeListener
	 */
	public void setLanguageChangeListener(IPartitionScanner languageChangeListener)
	{
		this._languageChangeListener = languageChangeListener;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#setFileErrors(com.aptana.ide.editors.unified.errors.IFileError[])
	 */
	public void setFileErrors(IFileError[] markers)
	{
		this._errorMarkers = markers;
		fireErrorsChanged();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getFileErrors()
	 */
	public IFileError[] getFileErrors()
	{
		return this._errorMarkers;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#addErrorListener(com.aptana.ide.editors.unified.errors.IFileErrorListener)
	 */
	public void addErrorListener(IFileErrorListener listener)
	{
		synchronized (this._errorListeners)
		{
			this._errorListeners.add(listener);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#removeErrorListener(com.aptana.ide.editors.unified.errors.IFileErrorListener)
	 */
	public void removeErrorListener(IFileErrorListener listener)
	{
		synchronized (this._errorListeners)
		{
			this._errorListeners.remove(listener);
		}
	}

	/**
	 * fireErrorsChanged
	 */
	public void fireErrorsChanged()
	{
		IFileErrorListener[] listeners = null;

		synchronized (this._errorListeners)
		{
			listeners = this._errorListeners.toArray(new IFileErrorListener[this._errorListeners.size()]);
		}

		for (int i = 0; i < listeners.length; i++)
		{
			IFileErrorListener element = listeners[i];

			element.onErrorsChanged(this._errorMarkers);
		}
	}

	/**
	 * setErrorManager
	 * 
	 * @param errorManager
	 */
	public void setErrorManager(IErrorManager errorManager)
	{
		if (this._errorManager != null)
		{
			removeLongDelayedFileListener(this._errorManager);
		}

		this._errorManager = errorManager;

		if (this._errorManager != null)
		{
			addLongDelayedFileListener(this._errorManager);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#activateForEditing()
	 */
	public void activateForEditing()
	{
		visitLanguageServices(new IFileLanguageServiceVisitor()
		{
			public void visit(IFileLanguageService service)
			{
				service.activateForEditing();
			}
		});
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#deactivateForEditing()
	 */
	public void deactivateForEditing()
	{
		visitLanguageServices(new IFileLanguageServiceVisitor()
		{
			public void visit(IFileLanguageService service)
			{
				service.deactivateForEditing();
			}
		});
	}

	private void visitLanguageServices(IFileLanguageServiceVisitor v)
	{
		Iterator<IFileLanguageService> it = this._languageServices.values().iterator();

		while (it.hasNext())
		{
			IFileLanguageService fls = it.next();

			v.visit(fls);
		}
	}

	/**
	 * @author Robin Debreuil
	 */
	interface IFileLanguageServiceVisitor
	{
		/**
		 * visit
		 * 
		 * @param service
		 */
		void visit(IFileLanguageService service);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getFileListeners()
	 */
	public IFileContextListener[] getFileListeners()
	{
		return this._listeners.toArray(new IFileContextListener[this._listeners.size()]);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getDelayedFileListeners()
	 */
	public IFileContextListener[] getDelayedFileListeners()
	{
		return this._delayedListeners.toArray(new IFileContextListener[this._delayedListeners.size()]);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#getLongDelayedFileListeners()
	 */
	public IFileContextListener[] getLongDelayedFileListeners()
	{
		return this._longDelayedListeners.toArray(new IFileContextListener[this._longDelayedListeners.size()]);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasFileListenerAdded(IFileContextListener listener)
	{
		return (this._listeners.contains(listener));
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasDelayedFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasDelayedFileListenerAdded(IFileContextListener listener)
	{
		return (this._delayedListeners.contains(listener));
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileService#hasLongDelayedFileListenerAdded(com.aptana.ide.editors.unified.IFileContextListener)
	 */
	public boolean hasLongDelayedFileListenerAdded(IFileContextListener listener)
	{
		return (this._longDelayedListeners.contains(listener));
	}
}
