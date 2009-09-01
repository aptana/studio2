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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.logging.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.UniformResourceMarkerAnnotationModel;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.logging.ILogResource;
import com.aptana.ide.logging.ILogResourceListener;
import com.aptana.ide.logging.ILogTailListener;
import com.aptana.ide.logging.ILogWatcher;
import com.aptana.ide.logging.LogResourceFactory;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.Messages;
import com.aptana.ide.logging.coloring.LoggingColorizer;
import com.aptana.ide.logging.coloring.LoggingLexemeManager;
import com.aptana.ide.logging.coloring.TokenTypes;
import com.aptana.ide.logging.impl.DisplayThreadProxy;
import com.aptana.ide.logging.preferences.ILoggingPreferenceListener;
import com.aptana.ide.logging.preferences.LoggingStructureProvider;
import com.aptana.ide.logging.view.LogView.TextListener;

/**
 * Log tab.
 * @author Denis Denisenko
 */
class LogTab
{
	/**
	 * Log source viewer.
	 * @author Denis Denisenko
	 *
	 */
	static class LogSourceViewer extends SourceViewer
	{

		public LogSourceViewer(Composite parent, IVerticalRuler verticalRuler,
				IOverviewRuler overviewRuler, boolean showAnnotationsOverview,
				int styles)
		{
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		}

		/**
		 * Reveals the given range of the visible document.
		 *
		 * @param start the start offset of the range
		 * @param end the end offset of the range
		 */
		protected void revealLastLine() {

			try {

				IDocument doc = getVisibleDocument();

				int endLine= doc.getLineOfOffset(doc.getLength() == 0 ? 0 : doc.getLength() - 1);
				int startLine = endLine;
				
				int top = getTextWidget().getTopIndex();
				if (top > -1) {

					// scroll vertically
					int bottom= getBottomIndex(getTextWidget());
					int lines= bottom - top;
					
					// if the widget is not scrollable as it is displaying the entire content
					// setTopIndex won't have any effect.

					if (startLine >= top && startLine <= bottom	&& endLine >= top && endLine <= bottom ) {

						// do not scroll at all as it is already visible

					} else {

						int delta= Math.max(0, lines - (endLine - startLine));
						getTextWidget().setTopIndex(startLine - delta/3);
						updateViewportListeners(INTERNAL);
					}
				}
			} catch (BadLocationException e) {
				throw new IllegalArgumentException("Illegal text range"); //$NON-NLS-1$
			}
		}
		
		/**
		 * Returns the last fully visible line of the widget. The exact semantics of "last fully visible
		 * line" are:
		 * <ul>
		 * <li>the last line of which the last pixel is visible, if any
		 * <li>otherwise, the only line that is partially visible
		 * </ul>
		 * 
		 * @param widget the widget
		 * @return the last fully visible line
		 */
		public static int getBottomIndex(StyledText widget) {
			int lastPixel= computeLastVisiblePixel(widget);
			
			// bottom is in [0 .. lineCount - 1]
			int bottom= widget.getLineIndex(lastPixel);

			// bottom is the first line - no more checking
			if (bottom == 0)
				return bottom;
			
			int pixel= widget.getLinePixel(bottom);
			// bottom starts on or before the client area start - bottom is the only visible line
			if (pixel <= 0)
				return bottom;
			
			int offset= widget.getOffsetAtLine(bottom);
			int height= widget.getLineHeight(offset);
			
			// bottom is not showing entirely - use the previous line
			if (pixel + height - 1 > lastPixel)
				return bottom - 1;
			
			// bottom is fully visible and its last line is exactly the last pixel
			return bottom;
		}
		
		/**
		 * Returns the last visible pixel in the widget's client area.
		 * 
		 * @param widget the widget
		 * @return the last visible pixel in the widget's client area
		 */
		private static int computeLastVisiblePixel(StyledText widget) {
			int caHeight= widget.getClientArea().height;
			int lastPixel= caHeight - 1;

			return lastPixel;
		}
	}
	
    /**
     * Document writer.
     * @LogTab
     * @author Denis Denisenko
     */
    private static class DocumentWriter extends Writer
    {
        /**
         * Document.
         */
        private IDocument document;
        
        /**
         * DocumentWriter constructor.
         * @param document - document.
         */
        public DocumentWriter(IDocument document)
        {
            this.document = document;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public void close() throws IOException
        {
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public void flush() throws IOException
        {
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            try
            {
                document.replace(document.getLength(), 0, new String(cbuf, off, len));
            } catch (BadLocationException e)
            {
                throw new IOException(e.getMessage());
            }
        }
        
    }
    
    /**
     * DEFAULT_ENCODING
     */
    private static final String DEFAULT_ENCODING = "cp1251";  //$NON-NLS-1$

    /**
     * MAX_NOTIFICATION_SIZE
     */
    private static final int MAX_NOTIFICATION_SIZE = 1024;

    /**
     * Book mark annotation type.
     */
    private static final String BOOKMARK_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.bookmark";  //$NON-NLS-1$
    
    /**
     * Default font constant. 
     */
    private static final String DEFAULT_FONT = "DEFAULT_TAIL_VIEW_FONT"; //$NON-NLS-1$
    
    /**
     * Log tab prefix.
     */
    private static final String PREFIX = "LogTab";  //$NON-NLS-1$
    
    /**
     * Log tab prefix.
     */
    private static final String BOOKMARK_PREFIX = PREFIX + ".BookmarkAction";  //$NON-NLS-1$

    /**
     *  The width of the vertical ruler.
     */
    protected final static int VERTICAL_RULER_WIDTH = 12;
    
    /**
     * Max colorization columns.
     */
    protected int _maxColorizingColumns = 512;
    
    /**
     * logView
     */
    private final LogView logView;

    /**
     * URI.
     */
    private final URI uri;

    /**
     * Tab item.
     */
    private CTabItem item;
    
    /**
     * Ruler.
     */
    private IVerticalRuler ruler;
    
    /**
     * Current global offset.
     */
    private long currentGlobalOffset = 0;
    
    /**
     * Content dependent actions.
     */
    private Set contentDependentActions = new HashSet();

    /**
     * Log watcher.
     */
    private ILogWatcher watcher;

    /**
     * Resource.
     */
    private ILogResource resource = null;

    /**
     * Menu manager.
     */
    private MenuManager menuMgr;
    
    /**
     * Font registry.
     */
    private FontRegistry fontRegistry = new FontRegistry();
    
    /**
     * Color registry.
     */
    private ColorRegistry colorRegistry = new ColorRegistry();
    
    /**
     * Viewer.
     */
    private SourceViewer viewer;

    /**
     * Bold caption font.
     */
    private Font boldFont;

    /**
     * Normal caption font.
     */
    private Font normalFont;

    /**
     * Italic caption font.
     */
    private Font italicFont;
    
    /**
     * Text foreground color.
     */
    private Color textForeground;

    /**
     * Status filed that indicates whether tab has unread data available.
     */
    private volatile boolean unreadDataAvailable;

    /**
     * Whether resource is available.
     */
    private volatile boolean resourceAvailable;

    /**
     * Add Bookmark action.
     */
    private LoggingMarkerRulerAction actionAddBookmark;

    /**
     * Annotation model.
     */
    private UniformResourceMarkerAnnotationModel model;

    /**
     * Overview ruler.
     */
    private IOverviewRuler overviewRuler;

    /**
     * Annotation preferences.
     */
    private MarkerAnnotationPreferences fAnnotationPreferences;

    /**
     * Bookmark color.
     */
    private Color bookmarkColor = new Color(Display.getCurrent(), 0, 0, 255);

    /**
     * Line style listener.
     */
    private LineStyleListener _lineStyleListener;

    /**
     * Logging colorizer.
     */
    private LoggingColorizer _colorizer;

    /**
     * Text change listener.
     */
    private TextChangeListener _textChangeListener;

    /**
     * Lexer.
     */
    private ILexer lexer;
    
    /**
     * Whether to follow document tail.
     */
    private boolean followTail = true;
    
    /**
     * Document.
     */
    private IDocument document;

    /**
     * Colorization preference listener.
     */
    private IPropertyChangeListener colorizationPreferencesListener;

    /**
     * Logging preference listener.
     */
    private ILoggingPreferenceListener loggingPreferenceListener;

    /**
     * Tail listener.
     */
    private ILogTailListener tailListener;
    
    /**
     * Helper for handling decoration.
     */
    protected SourceViewerDecorationSupport decorationSupport;
    
    /**
     * Logging lexeme manager.
     */
    protected LoggingLexemeManager lexememanager;

    /**
     * Composite.
     */
	private Composite composite;

	/**
	 * Whether the tab supports log erasing.
	 */
	private boolean supportsErase;

    /**
     * LogView.LogTab constructor.
     * @param uri - URI.
     * @param logView - log view that contains this tab.
     * @param name - tab name. maybe null.
     * @param supportsErase - whether the tab's log supports log erasing.
     */
    public LogTab(LogView logView, URI uri, String name, boolean supportsErase)
    {
        this.logView = logView;
        this.uri = uri;
        this.supportsErase = supportsErase;
        // creating tab item
        String tabName = name;
        if (tabName == null)
        {
            tabName = getTabName(uri.getPath());
        }
        
        fAnnotationPreferences= EditorsPlugin.getDefault().getMarkerAnnotationPreferences();
        
        item = createLogTabItem(tabName, uri);
        
        createColorizer();
        
        try
        {
            resource = LogResourceFactory.createResource(uri);
            resource.setEncoding(Charset.forName(LoggingPlugin.getDefault().getLoggingPreferences().getDefaultEncoding()));
        } catch (IOException e)
        {
            //e.printStackTrace(new PrintWriter(writer));
            e.printStackTrace(new PrintWriter(new DocumentWriter(getDocument())));
            return;
        }

        initAnnotationModel();
        createActions();
        
        hookRulerContextMenu();

        initializeFonts();
        initializeColors();
        
        getDocument().addDocumentListener(new IDocumentListener()
        {

            public void documentAboutToBeChanged(DocumentEvent event)
            {                            
            }

            public void documentChanged(DocumentEvent event)
            {
            }
        });
            
        LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences(); 
        int timeout = preferences.getReadTimeout();
        int readBuffer = preferences.getReadBuffer();
        
        int maxBytesPerSecond = (int) ((((long)readBuffer) * 1000l) / ((long)timeout));
        watcher = resource
                .getResourceWatcher(new DisplayThreadProxy(item.getDisplay()),
                        maxBytesPerSecond, readBuffer, 
                        timeout,
                        Charset.forName(DEFAULT_ENCODING),
                        LoggingPlugin.getDefault().getLoggingPreferences().getBacklogLines()); 

        //registering tail listener
        registerTailListener();

        //registering resource listener
        registerResourceListener();

        //starting watching
        watcher.startWatching();

        //settign selection to the new tab
        this.logView.tabFolder.setSelection(item);
        
        linkColorer();
    }

    /**
     * Gets tab URI.
     * @return tab URI.
     */
    public URI getURI()
    {
        return uri;
    }

    /**
     * Gets viewer.
     * @return viewer.
     */
    public TextViewer getViewer()
    {
        return viewer;
    }
    
    /**
     * Gets tab name.
     * @return tab name.
     */
    public String getName()
    {
        return item.getText();
    }
    
    /**
     * Gets whether the tab's log supports log erase.
     * @return whether the tab's log supports log erase.
     */
    public boolean supportsLogErase()
    {
    	return this.supportsErase;
    }
    
    /**
     * Sets tab name.
     * @param name - name to set.
     */
    public void setName(String name)
    {
        item.setText(name);
    }

    /**
     * Closes the tab.
     */
    public void close()
    {
        watcher.stopWatching();
        watcher.close();
        //console.destroy();
        try
        {
            resource.close();
        } catch (IOException e)
        {
            IdeLog.logError(LoggingPlugin.getDefault(),
                    com.aptana.ide.logging.view.Messages.LogTab_4, e); 
        }
        
        disposeListeners();

        if (boldFont != null)
        {
            boldFont.dispose();
        }

        if (italicFont != null)
        {
            italicFont.dispose();
        }
        
        bookmarkColor.dispose();

        item.dispose();
        
        if (decorationSupport != null) {
            decorationSupport.dispose();
            decorationSupport= null;
        }
    }

    /**
     * Starts watching.
     */
    public void start()
    {
        watcher.startWatching();
    }

    /**
     * Stops watching.
     */
    public void stop()
    {
        watcher.stopWatching();
    }

    /**
     * Whether watching is on.
     * @return true if watching, false otherwise.
     */
    public boolean isWatching()
    {
        return watcher.watchingStatus();
    }

    /**
     * Reloads the log. 
     */
    public void reload()
    {
        watcher.resetWatching();
        clear();
        watcher.startWatching();
    }

    /**
     * Gets tab item.
     * @return tab item.
     */
    public CTabItem getItem()
    {
        return item;
    }

    /**
     * Clears tab. 
     */
    public void clear()
    {
        if (document != null)
        {
            document.set("");  //$NON-NLS-1$
        }    
        //currentLexemes.clear();
        currentGlobalOffset = 0;
        if (lexememanager != null)
        {
            List<String> empty = Collections.emptyList();
            lexememanager.dataAvailable(empty);
        }
    }

    /**
     * Notifies current tab, it is selected.
     */
    public void selected()
    {
        //removing "unread data state" if any
        unreadDataAvailable = false;
        makeTabBold(false);

        if (!resourceAvailable)
        {
            makeTabShaded(true);
        }
    }

    /**
     * Clears log file.
     */
    public void clearLogFile()
    {
        try
        {
            resource.clear();
        } catch (IOException e)
        {
            MessageDialog.openError(this.getItem().getControl().getShell(), 
                    com.aptana.ide.logging.view.Messages.LogTab_2,
                    com.aptana.ide.logging.view.Messages.LogTab_1   //$NON-NLS-2$
                    + resource.getURI().getPath());
        }
        reload();
    }

    /**
     * Gets whether unread data is available. 
     * @return whether unread data is available.
     */
    public boolean hasUnreadData()
    {
        return unreadDataAvailable;
    }
    
    /**
     * Sets whether viewer follows document tail.
     * @param followTail - whether to follow tail.
     */
    public void setFollowTail(boolean followTail)
    {
        this.followTail = followTail;
    }
    
    /**
     * Inverts whether viewer follows document tail.
     */
    public void invertFollowTail()
    {
        followTail = !followTail;
    }
    
    /**
     * Gets selection.
     * @return selection or null
     */
    String getSelection()
    {
        if (viewer == null)
        {
            return null;
        }
        TextSelection selection = (TextSelection) viewer.getSelection();
        if (selection == null)
        {
            return null;
        }
        
        try
        {
            return getDocument().get(selection.getOffset(), selection.getLength());
        }
        catch(BadLocationException ex)
        {
            IdeLog.logError(LoggingPlugin.getDefault(), com.aptana.ide.logging.view.Messages.LogTab_ERR_Exception, ex); 
            return null;
        }
    }
    
    /**
     * Refreshes tab viewer. 
     */
    void refreshViewer()
    {
        viewer.refresh();
    }

    /**
     * Creates new logger tab item
     * 
     * @param tabName -
     *            tab name.
     * @return tab item
     */
    private CTabItem createLogTabItem(String tabName, URI uri)
    {
        CTabItem item = new CTabItem(this.logView.tabFolder, SWT.DEFAULT);
        item.setText(tabName);
        if (uri != null)
        {
            item.setToolTipText(uri.getPath());
        }

        document = new Document();
        lexememanager = new LoggingLexemeManager(document, LoggingPlugin.getDefault().getLoggingPreferences());

        // creating a view
        IAnnotationAccess access = new DefaultMarkerAnnotationAccess();
        ruler = createVerticalRuler(access);
        overviewRuler = createOverviewRuler(access);
        
        createViewer(item);
        
        return item;
    }
    
    /**
     * Recreates viewer.
     */
    void recreateViewer()
    {
        decorationSupport.dispose();
        decorationSupport = null;
        composite.dispose();
        viewer = null;
        
        LoggingPlugin.getDefault().getLoggingPreferences().
            removePreferenceListener(loggingPreferenceListener);
        createViewer(item);
    }

    /**
     * @param item
     */
    private void createViewer(CTabItem item)
    {
        int viewerStyle = 0;
        if (LoggingPlugin.getDefault().getLoggingPreferences().getWrapping())
        {
            viewerStyle = SWT.V_SCROLL | SWT.WRAP | SWT.BORDER;
        }
        else
        {
            viewerStyle = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
        }
        
        composite = new Composite(this.logView.tabFolder, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight =3;
        layout.marginWidth =3;
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayout(layout);
        composite.setLayoutData(data);
        composite.setBackground(UnifiedColorManager.getInstance().getColor(new RGB(220, 220, 220)));
        
        viewer = new LogSourceViewer(composite,
                ruler, overviewRuler, true, viewerStyle);
        viewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged(SelectionChangedEvent event)
            {
                // TODO Auto-generated method stub
                int i = 0;
                i++;
            }
            
        });
        viewer.setDocument(getDocument());
        
        Control control = viewer.getControl();   
        control.setLayoutData(data);
        
        viewer.getTextWidget().setFont(LoggingPlugin.getDefault().getLoggingPreferences().getFont());

        item.setControl(composite);

        //adding logger menu
        Menu menu = logView.menuMgr.createContextMenu(viewer.getTextWidget());
        viewer.getTextWidget().setMenu(menu);

        TextListener listener = this.logView.new TextListener(viewer);
        viewer.addTextInputListener(listener);
        viewer.addTextListener(listener);

        viewer.activatePlugins();
        
        createSourceViewerDecorationSupport(viewer);
        
        bindToColorizationSave(viewer);

        //PreferenceUtils.registerFontPreference(viewer.getTextWidget(), LoggingPreferences.MAIN_TEXT_FONT_KEY);
    }

    /**
     * Recreates tab item.
     * @param name - tab name.
     * @param newPos - new position.
     */
    void recreateItem(String name, int newPos)
    {
        item = new CTabItem(this.logView.tabFolder, SWT.DEFAULT, newPos);
        item.setText(name);
        item.setControl(composite);
    }

    /**
     * Binds to colorization save.
     * @param viewer - viewer.
     */
    private void bindToColorizationSave(final SourceViewer viewer)
    {
        colorizationPreferencesListener = new IPropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent event)
            {
                if (LoggingStructureProvider.COLORIZATION_SAVED.equals(event.getProperty()))
                {
                    createColorizer();
                    
                    lexememanager.clearCache();
                    if (viewer != null)
                    {
                        viewer.setRedraw(false);
                        ((StyledText) LogTab.this.viewer.getTextWidget()).removeLineStyleListener(_lineStyleListener);
                        ((StyledText) LogTab.this.viewer.getTextWidget()).addLineStyleListener(_lineStyleListener);
                        viewer.setRedraw(true);
                    }
                }
            }
            
        };
        UnifiedEditorsPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(colorizationPreferencesListener);
        
        LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
        loggingPreferenceListener = new ILoggingPreferenceListener(){

            public void rulesChanged()
            {
            }

            public void wrappingChanged(boolean wrapping)
            {
                recreateViewer();
            }

            public void fontChanged(Font font)
            {
                viewer.getTextWidget().setFont(font);
            }

            public void textForegroundColorChanged(Color color)
            {
                textForeground = color;
            }
        };
        preferences.addPreferenceListener(loggingPreferenceListener);
    }

    /**
     * Hooks context menu.
     */
    private void hookRulerContextMenu()
    {
        menuMgr = new MenuManager("#PopupMenu");  //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                logView.setFocus();
                fillRulerContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(ruler.getControl());
        ruler.getControl().setMenu(menu);

        logView.getSite().registerContextMenu(menuMgr, viewer.getSelectionProvider());
    }
    
    /**
     * Fills context menu.
     * @param manager - menu manager.
     */
    private void fillRulerContextMenu(IMenuManager manager)
    {
        manager.add(actionAddBookmark);
//        manager.add(actionAdd);
//        manager.add(actionDelete);
//        
//        manager.add(new Separator());
//        
//        manager.add(actionFindReplace);
//        
//        manager.add(new Separator());
//        // Other plug-ins can contribute there actions here
//        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Creates vertical ruler.
     *
     * @param access - annotation access.
     * 
     * @return the vertical ruler
     */
    protected IVerticalRuler createVerticalRuler(IAnnotationAccess access)
    {
        return new VerticalRuler(VERTICAL_RULER_WIDTH, access);
    }
    
    /**
     * Creates overview ruler.
     * 
     * @param access - annotation access.
     * 
     * @return the overview ruler
     */
    protected IOverviewRuler createOverviewRuler(IAnnotationAccess access) {
        ISharedTextColors sharedColors= EditorsPlugin.getDefault().getSharedTextColors();
        IOverviewRuler ruler= new OverviewRuler(access, VERTICAL_RULER_WIDTH, sharedColors);
        Iterator e= fAnnotationPreferences.getAnnotationPreferences().iterator();
        while (e.hasNext()) {
            AnnotationPreference preference= (AnnotationPreference) e.next();
            if (preference.contributesToHeader())
                ruler.addHeaderAnnotationType(preference.getAnnotationType());
        }
        
        String type = BOOKMARK_ANNOTATION_TYPE;
        
        ruler.addAnnotationType(type);
        ruler.addHeaderAnnotationType(type);
        ruler.setAnnotationTypeLayer(type, 1);
        //TODO find why default bookmark preferences fail to be loaded correctly. 
        ruler.setAnnotationTypeColor(type, bookmarkColor);
        return ruler;
    }

    /**
     * Gets tab name by path.
     * 
     * @param path -
     *            path.
     * @return tab name.
     */
    private String getTabName(String path)
    {
        Path p = new Path(path);
        return p.lastSegment();
    }

    /**
     * Makes tab bold.
     * @param bold - whether to set bold or to remove.
     */
    private void makeTabBold(boolean bold)
    {
        Font currentFont = item.getFont();

        int style = currentFont.getFontData()[0].getStyle();
        if (bold)
        {
            if ((style & SWT.BOLD) != 0)
            {
                return;
            }
            item.setFont(boldFont);
            //item.getControl().setFont(boldFont);
        } else
        {
            if ((style & SWT.BOLD) == 0)
            {
                return;
            }
            item.setFont(normalFont);
            //item.getControl().setFont(normalFont);
        }
    }

    /**
     * Makes tab shaded.
     * @param shaded - true if to turn shaded mode on, false otherwise.
     */
    private void makeTabShaded(boolean shaded)
    {
        if (shaded)
        {
            Font currentFont = item.getFont();
            if (currentFont.equals(italicFont))
            {
                return;
            }

            item.setFont(italicFont);
        } else
        {
            Font currentFont = item.getFont();
            if (!currentFont.equals(italicFont))
            {
                return;
            }

            item.setFont(normalFont);
        }
    }

    
    /**
     * Initialized tab fonts.
     */
    private void initializeColors()
    {
        textForeground = 
            LoggingPlugin.getDefault().getLoggingPreferences().getTextColor();
    }
    
    /**
     * Initialized tab fonts.
     */
    private void initializeFonts()
    {
        Font normalFont = item.getFont();

        //setting normal font
        this.normalFont = normalFont;

        //setting bold font
        FontData data = normalFont.getFontData()[0];
        int originalStyle = data.getStyle();
        int boldStyle = originalStyle | SWT.BOLD;
        data.setStyle(boldStyle);
        this.boldFont = new Font(normalFont.getDevice(), new FontData[]
        { data });

        //setting italic font
        int italicStyle = originalStyle | SWT.ITALIC;
        data = normalFont.getFontData()[0];
        data.setStyle(italicStyle);
        this.italicFont = new Font(normalFont.getDevice(), new FontData[]
        { data });
    }
    
    private void createActions()
    {
        actionAddBookmark = new LoggingMarkerRulerAction(Messages.getResourceBundle(),
                BOOKMARK_PREFIX, resource, getDocument(), 
                model, ruler, true, IMarker.BOOKMARK, 
                item.getControl().getShell());
        actionAddBookmark.update();
    }
    
    /**
     * Initializes annotation model.
     * @return model
     */
    private void initAnnotationModel()
    {
        model = new UniformResourceMarkerAnnotationModel(resource);
        model.connect(getDocument());
        ruler.setModel(model);
        overviewRuler.setModel(model);
    }

    /**
     * Registers resource listener.
     */
    private void registerResourceListener()
    {
        watcher.registerListener(new ILogResourceListener()
        {
            public void resourceAvailable(boolean available)
            {
                resourceAvailable = available;
                //if resource became unavailable and we're not in bold mode,
                //shading tab caption.
                if (!unreadDataAvailable)
                {
                    if (!resourceAvailable)
                    {
                        makeTabShaded(true);
                    } else
                    {
                        makeTabShaded(false);
                    }
                }
            }
        });
    }

    /**
     * Registers tail listener.
     */
    private void registerTailListener()
    {
        tailListener = new ILogTailListener()
        {

            public void dataAvailable(String data, long globalOffset, long globalLength)
            {
                updateData(data, globalOffset, globalLength);
            }

            public void errorHappened(Throwable th)
            {
            	IdeLog.logError(LoggingPlugin.getDefault(), 
            			com.aptana.ide.logging.view.Messages.LogTab_ERR_FetchTail, th);
                //th.printStackTrace(new PrintWriter(new DocumentWriter(getDocument())));
            }

        };
        watcher.registerListener(tailListener);
    }
    
    /**
     * Updates content dependent actions.
     */
    private void updateContentDependentActions()
    {
        Iterator it = contentDependentActions.iterator();
        while (it.hasNext())
        {
            Action action = (Action) it.next();
            if (action instanceof IUpdate)
            {
                ((IUpdate) action).update();
            }
        }
    }
    
    /**
     * Marks action as content dependent.
     * @param action - action to mark.
     */
    void markAsContentDependent(Action action)
    {
        contentDependentActions.add(action);
    }
    
    /**
     * Links colorer.
     */
    private void linkColorer()
    {
        if (_lineStyleListener == null)
        {
            _lineStyleListener = new LineStyleListener()
            {
                /**
                 * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(org.eclipse.swt.custom.LineStyleEvent)
                 */
                public void lineGetStyle(LineStyleEvent e)
                {
                    //LexemeList lexemeList = currentLexemes;
 
                    int orgOffset = e.lineOffset;
                    int offset = orgOffset;
                    int extra = 0;
                    int lineLength = e.lineText.length();

                    // need to get actual offset values in the doc,
                    // as widget offsets do not include potentially folded code
                    if (viewer instanceof ITextViewerExtension5)
                    {
                        ITextViewerExtension5 v5 = (ITextViewerExtension5) viewer;
                        offset = v5.widgetOffset2ModelOffset(e.lineOffset);
                        extra = offset - e.lineOffset;
                    }

                    int maxLineLength = lineLength > _maxColorizingColumns ? _maxColorizingColumns : lineLength;
                    Lexeme[] lexemes = null;

                    int lineNumber;
                    try
                    {
                        lineNumber = document.getLineOfOffset(e.lineOffset);
                        lexemes = lexememanager.getLexemes(lineNumber);
                    } catch (BadLocationException e1)
                    {
                        IdeLog.logError(LoggingPlugin.getDefault(), com.aptana.ide.logging.view.Messages.LogTab_ERR_Exception, e1);
                    }
                    

                    if (lexemes != null && lexemes.length > 0)
                    {
                        Vector styles = new Vector();

                        _colorizer.createStyles(styles, lexemes, false);

                        StyleRange[] styleResults = (StyleRange[]) styles.toArray(new StyleRange[] {});

                        // move styles back to actual widget offsets in case of
                        // folding
                        if (extra > 0)
                        {
                            for (int i = 0; i < styleResults.length; i++)
                            {
                                StyleRange range = styleResults[i];
                                range.start -= extra;
                            }
                        }

                        e.styles = styleResults;
                    }
                    else
                    {
                        StyleRange[] styles = new StyleRange[1];
                        styles[0] = new StyleRange(e.lineOffset, e.lineText.length(),
                                textForeground, null);
                        e.styles = styles;
                    }
                }
            };
        }

        // Repaint lines if the user is making changes
        if (_textChangeListener == null)
        {
            _textChangeListener = new TextChangeListener()
            {

                public void textChanging(TextChangingEvent event)
                {
                }

                public void textChanged(TextChangedEvent event)
                {
                    StyledText text = getViewer().getTextWidget();
                    redrawFrom(text, text.getLineAtOffset(text.getCaretOffset()));
                }

                // Used with complex text change events (tabbing, replacement,
                // etc.)
                public void textSet(TextChangedEvent event)
                {
                    StyledText text = getViewer().getTextWidget();
                    redrawFrom(text, 0);
                }

                private void redrawFrom(StyledText text, int lno)
                {
                    if (lno < 0 || lno >= text.getLineCount())
                    {
                        return;
                    }

                    int height = text.getClientArea().height;
                    int width = text.getClientArea().width + text.getHorizontalPixel();
                    try
                    {
                        text.redraw(0, 0, width, height, true);
                    }
                    catch (Exception e)
                    {
                        // Catch errors in redraw as they may be intermittent and the subsequent redraw will complete
                        // successfully
                    }
                }
            };
        }

        getViewer().getTextWidget().addLineStyleListener(_lineStyleListener);
        getViewer().getTextWidget().getContent().addTextChangeListener(_textChangeListener);
    }

    /**
     * Adds lexeme to the list.
     * @param lexeme - lexeme to add.
     * @param lexemes - lexemes
     */
    private void addLexeme(Lexeme lexeme, LexemeList lexemes)
    {
        lexemes.add(lexeme);
        return;
    }

    /**
     * Gets lexer.
     * @return
     */
    private ILexer getLexer()
    {
        return TokenTypes.getLexerFactory().getLexer();
    }

    /**
     * Finds last recognized regexp lexeme.
     * @param lexemes - lexemes to search in.
     * @return last recognized lexeme.
     */
    private Lexeme findLastRecognizedRegexpLexeme(LexemeList lexemes)
    {
        if (lexemes.size() == 0)
        {
            return null;
        }
        
        for (int i = lexemes.size()-1; i >= 0; i--)
        {
            Lexeme currentLexeme = lexemes.get(i);
            if (TokenTypes.isRegexpType(currentLexeme.getType()))
            {
                return currentLexeme;
            }
        }
        
        return null;
    }
    
    /**
     * Disposes listeners.
     */
    private void disposeListeners()
    {
        UnifiedEditorsPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(colorizationPreferencesListener);
        LoggingPlugin.getDefault().getLoggingPreferences().removePreferenceListener(loggingPreferenceListener);
        watcher.removeListener(tailListener);
    }
    
    /**
     * Creates colorizer.
     */
    private void createColorizer()
    {
        _colorizer = new LoggingColorizer(getLexer().getTokenList(TokenTypes.LANGUAGE));
    }
    
    /**
     * Gets autobolding preference. 
     * @return autobolding preference.
     */
    private boolean getAutobolding()
    {
        return LoggingPlugin.getDefault().getLoggingPreferences().getAutoBolding();
    }
    
    /**
     * Gets decoration support.
     * @param viewer - viewer.
     * @return decoration support.
     */
    protected void createSourceViewerDecorationSupport(ISourceViewer viewer) {
        if (decorationSupport == null)
        {
            ISharedTextColors sharedColors= EditorsPlugin.getDefault().getSharedTextColors();
            
            decorationSupport= new SourceViewerDecorationSupport(viewer, null, null, sharedColors);
            configureSourceViewerDecorationSupport(decorationSupport);
            //decorationSupport.showCursorLine();
            
            decorationSupport.install(LoggingPlugin.getDefault().getPreferenceStore());;
        }
    }
    
    /**
     * Configures decoration support.
     * @param support - support to configure.
     */
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) 
    {
        support.setCursorLinePainterPreferenceKeys(
                LoggingPreferences.CURSORLINE_KEY, LoggingPreferences.CURSORLINE_COLOR_KEY);
        support.setSymbolicFontName(LoggingPreferences.MAIN_TEXT_FONT_KEY);
    }

    /**
     * @return
     */
    private IDocument getDocument()
    {
        return document;
    }

    /**
     * Updates data.
     * @param data - data to add.
     * @param append - whether to append, or to replace.
     */
    private void updateData(String data, long globalOffset, long globalLength)
    {
        //if this tab is not active, we have an unread data
        if (!LogTab.this.equals(logView.getActiveTab()) && getAutobolding())
        {
            //setting "unreadDataAvailable" states
            unreadDataAvailable = true;

            //making tab caption bold
            makeTabBold(true);
        }
        
        Integer topIndex = null;
        Integer bottomIndex = null;
        
        int horizontalScrollPixel = -1;
        
        ISelection selection = null;
        if (viewer != null)
        {
        	//saving horizontal scrolling position
    		horizontalScrollPixel = viewer.getTextWidget().getHorizontalPixel(); 
        	
        	//saving selection
    		viewer.setRedraw(false);
            selection = viewer.getSelection();
            topIndex = viewer.getTopIndex();
            bottomIndex = viewer.getBottomIndex();
        }

        updateDocumentData(data, globalOffset, globalLength);
        
        if (viewer != null)
        {
        	
        	viewer.setRedraw(true);
            //viewer.setSelection(new TextSelection(0,0), false);
//            if (selection != null)
//            {
//                viewer.setSelection(selection, false);
//            }
            

            if (followTail)
            {
            	((LogSourceViewer) viewer).revealLastLine();
            }
            else
            {
                if (topIndex != null)
                {
                    try
                    {
                        int startOffset = document.getLineOffset(topIndex);
                        int endOffset = document.getLineOffset(bottomIndex) + document.getLineLength(bottomIndex);
                        viewer.revealRange(startOffset, endOffset - startOffset);
                        topIndex = viewer.getTopIndex();
                        bottomIndex = viewer.getBottomIndex();
                    }
                    catch (BadLocationException e)
                    {
                        IdeLog.logError(LoggingPlugin.getDefault(), com.aptana.ide.logging.view.Messages.LogTab_ERR_Exception, e);
                    }
                }
            }
            
            final int horizontalScrollPixel1 = horizontalScrollPixel;
            
            //restoring horizontal scrolling position
    		viewer.getTextWidget().setHorizontalPixel(horizontalScrollPixel1);
        }
    }

    /**
     * Updates document data.
     * @param data - data.
     * @param globalOffset - global offset.
     * @param globalLength - global length.
     */
	private void updateDocumentData(String data, long globalOffset,
			long globalLength)
	{
		try
        {
            IDocument document = getDocument();
            
            //applying data
            long lastGlobalPosition = currentGlobalOffset + document.getLength();
            if (globalOffset == 0 && globalLength >= Integer.MAX_VALUE)
            {
            	document.replace(0, document.getLength(), data);
            	currentGlobalOffset = globalOffset;
            }
            else if (globalOffset < currentGlobalOffset)
            {
                int diff = (int) (currentGlobalOffset - globalOffset);
                //checking for part visibility
                if (diff <= data.length())
                {
                    
                    int lengthToReplace = (int) (globalLength - diff);
                    String beginData = data.substring(0, diff);
                    String innrerData = data.substring(diff, data.length());
                    
                    //replacing inner data
                    replaceInnerData(innrerData, currentGlobalOffset, lengthToReplace, document);
                    
                    //adding begin data
                    document.replace(0, diff, beginData);
                    currentGlobalOffset = globalOffset;
                }
                else
                {
                    //in other case, replacing the whole document
                    document.replace(0, document.getLength(), data);
                    currentGlobalOffset = globalOffset;
                }
            }
            else if (globalOffset >= currentGlobalOffset && globalOffset < lastGlobalPosition)
            {
                //replacing inner data
                replaceInnerData(data, globalOffset, globalLength, document);
            }
            else if (globalOffset == lastGlobalPosition)
            {
            	//adding data to the end of the document
            	document.replace(document.getLength(), 0, data);
            }
            else
            {
                //replacing whole data with new data
                document.replace(0, document.getLength(), data);
                currentGlobalOffset = globalOffset;
            }
            
            //fixing number of lines if needed
            int numberOfLines = document.getNumberOfLines();
            int allowedNumberOfLines = LoggingPlugin.getDefault().getLoggingPreferences().getBacklogLines();
            List<String> topLines = new ArrayList<String>();
            if (numberOfLines > allowedNumberOfLines)
            {
                int toRemoveLinesNum = numberOfLines - allowedNumberOfLines;
                int offset = document.getLineOffset(toRemoveLinesNum);
                for (int i = 0; i < toRemoveLinesNum; i++)
                {
                    int lineOffset = document.getLineOffset(i);
                    int lineLength = document.getLineLength(i);
                    topLines.add(document.get(lineOffset, lineLength));
                }
                currentGlobalOffset += offset;
                document.replace(0, offset, "");  //$NON-NLS-1$
            }
            
            if(lexememanager != null)
            {
                lexememanager.dataAvailable(topLines);
            }
        } 
        catch (Throwable e)
        {
            IdeLog.logError(LoggingPlugin.getDefault(),
                    com.aptana.ide.logging.view.Messages.LogTab_8, e); 
        }
	}

    /**
     * Replaces data, when change starts inside the document.
     * @param data - data to replace with.
     * @param globalOffset - global offset.
     * @param globalLength - global length.
     * @param document - codument.
     * @throws BadLocationException
     */
    private void replaceInnerData(String data, long globalOffset,
            long globalLength, IDocument document) throws BadLocationException
    {
        int diff = (int) (globalOffset - currentGlobalOffset);
        int lengthToReplace = (int) globalLength;
        if (lengthToReplace == Integer.MAX_VALUE || diff + lengthToReplace > document.getLength())
        {
            lengthToReplace = document.getLength() - diff;
        }
        document.replace(diff, lengthToReplace, data);
    }
}