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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.WhitespaceCharacterPainter;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.UnifiedLineNumberChangeRulerColumn;
import org.eclipse.jface.text.source.UnifiedOverviewRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.source.projection.UnifiedProjectionSupport;
import org.eclipse.jface.text.source.projection.UnifiedRulerColumn;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.BookmarkRulerAction;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TaskRulerAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.editors.ISaveAsEvent;
import com.aptana.ide.core.ui.editors.ISaveEvent;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.actions.OpenDeclarationAction;
import com.aptana.ide.editors.formatting.UnifiedBracketInserterManager;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.actions.CodeFormatAction;
import com.aptana.ide.editors.unified.actions.GotoMatchingBracketAction;
import com.aptana.ide.editors.unified.actions.UnifiedActionContributor;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.colorizer.UnifiedCursorLinePainter;
import com.aptana.ide.editors.unified.contentassist.ContentAssistAction;
import com.aptana.ide.editors.unified.context.ContextItem;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.editors.unified.errors.ExternalFileErrorListener;
import com.aptana.ide.editors.unified.errors.FileErrorListener;
import com.aptana.ide.editors.unified.errors.ProjectFileErrorListener;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.editors.unified.folding.LanguageProjectAnnotation;
import com.aptana.ide.editors.unified.help.LexemeUIHelp;
import com.aptana.ide.editors.unified.messaging.UnifiedMessages;
import com.aptana.ide.editors.untitled.BaseTextEditor;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.views.outline.UnifiedOutlinePage;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;

/**
 * UnifiedEditor
 */
public abstract class UnifiedEditor extends BaseTextEditor implements IUnifiedEditor, IPropertyChangeListener,
		ISelectionChangedListener, IRedrawRangeListener
{
	/**
	 * ADD_BOOKMARK
	 */
	public static final String ADD_BOOKMARK = "AddBookmark"; //$NON-NLS-1$

	/**
	 * ADD_TASK
	 */
	public static final String ADD_TASK = "AddTask"; //$NON-NLS-1$

	/**
	 * ctrlDown
	 */
	public static boolean ctrlDown;

	/**
	 * outlinePage
	 */
	protected UnifiedOutlinePage outlinePage;

	/**
	 * Auto activate for code assist, used for ContentAssistant AutoActivation listener
	 */
	protected boolean autoActivateCodeAssist = true;

	/**
	 * File service change listeners
	 */
	protected ListenerList fileServiceChangeListeners = new ListenerList();

	private UnifiedColorizer _colorizer;
	private IUnifiedEditorContributor _baseContributor;
	private FileErrorListener _errorListener;
	private WhitespaceCharacterPainter _whitespacePainter;
	private EditorFileContext _fileContextWrapper;
	// private UnifiedBracketMatcher bracketMatcher;
	private ArrayList<ISaveAsEvent> _saveAsListeners;
	private ArrayList<ISaveEvent> _saveListeners;
	private CommonNavigator _fileExplorerView;
	// private TextColorer textColorer;
	private IPreferenceStore _prefStore;
	private IPartListener _partListener;
	private UnifiedProjectionSupport _projectionSupport;
	private Listener _keyUpListener;
	private VerifyKeyListener _verifyKeyListener;
	private UnifiedViewer _viewer;
	private IContextAwareness _contextAwareness;
	private boolean _isDisposing = false;
	private boolean _hasKeyBeenPressed = false;
	private LineStyleListener _lineStyleListener;
	private TextChangeListener _textChangeListener;
	private PairMatcher _pairMatcher;
	private Image _caretImage;
	private UnifiedCursorLinePainter _painter;
	private int _maxColorizingColumns;
	private boolean _extendedStylesEnabled;
	private SelectionAdapter _selectionListener;
	private FocusAdapter _focusListener;
	private ITextListener _textListener;
	private IPreferenceStore _runtimeStore = new PreferenceStore();
	private UnifiedBracketInserterManager _bracketInserterManager;
	private CodeFormatAction _formatAction;

	/**
	 * UnifiedEditor
	 */
	public UnifiedEditor()
	{
		super();
		addPluginToPreferenceStore(UnifiedEditorsPlugin.getDefault());

		this._saveListeners = new ArrayList<ISaveEvent>();
		this._saveAsListeners = new ArrayList<ISaveAsEvent>();
		// bracketMatcher = new UnifiedBracketMatcher();

		/**
		 * See UnifiedEditor.init for comments regarding the movement of object creation/abstract method calling from
		 * this constructor to that method.
		 */
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		/**
		 * The following lines of code were moved from the default constructor to allow anonymous UnifiedEditors to be
		 * created from the GenericTextEditor. This code was moved to this specific method since the calling of this
		 * method signifies the beginning of the editors life-cycle and it called following the call to the constructor.
		 * See comments for AbstractTextEditor.init for more details about the timing of this method call.
		 */

		this._baseContributor = createLocalContributor();
		this._colorizer = UnifiedColorizer.getInstance();

		setSourceViewerConfiguration(new UnifiedConfiguration(this, getPreferenceStore()));

		// Keeping in, as it may have side effects
		getFileServiceFactory();

		this._prefStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		this._prefStore = (this._prefStore == null) ? null : this._prefStore; // make compiler happy

		// TODO: Actuate change
		// setDocumentProvider(UnifiedDocumentProvider.getInstance());
		setDocumentProvider(createDocumentProvider());

		this._fileContextWrapper = new EditorFileContext();

		// Update the base contributor with the file context
		this._baseContributor.setFileContext(this._fileContextWrapper);

		/**
		 * This marks the end of the moved lines from the default UnifiedEditor constructor
		 */

		syncFoldingPreferenceStore();

		super.init(site, input);
		setEditorContextMenuId(getSite().getId() + "#UnifiedEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId(getSite().getId() + "#UnifiedRulerContext"); //$NON-NLS-1$

		IPreferenceStore localPreferenceStore = this.getPreferenceStore();
		if (localPreferenceStore != null)
		{
			autoActivateCodeAssist = localPreferenceStore.getBoolean(IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION);
		}

		IUnifiedEditorContributor[] contributors = this._baseContributor.getChildContributors();
		// Process children for auto activate code assist if the parent is not
		// already requesting it
		if (contributors != null && !autoActivateCodeAssist)
		{
			for (int i = 0; i < contributors.length; i++)
			{
				autoActivateCodeAssist = autoActivateCodeAssist || contributors[i].isAutoActivateContentAssist();
				// Once we find a language that wants it we just break and it
				// will be setup to listen
				if (autoActivateCodeAssist)
				{
					break;
				}
			}
		}
	}

	/**
	 * This method circumvents that the line change ruler on AbstractDecoratedTextEditor is private. A placeholder pref
	 * store is used to sync our colorization values with the Eclipse values that this ruler pulls from.
	 */
	private void syncFoldingPreferenceStore()
	{
		if (this._baseContributor != null)
		{
			String language = this._baseContributor.getLocalContentType();
			LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(language);
			if (colorizer != null && colorizer.getFoldingBg() != null)
			{
				this._runtimeStore.setDefault(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
				this._runtimeStore.setValue(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
				PreferenceConverter.setValue(this._runtimeStore, PREFERENCE_COLOR_BACKGROUND, colorizer.getFoldingBg()
						.getRGB());
			}
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		menu.add(getAction(ADD_BOOKMARK));
		menu.add(getAction(ADD_TASK));
		menu.add(new Separator("source"));// TODO: add this constant //$NON-NLS-1$
		menu.add(_formatAction);
		_formatAction.setActiveEditor(null, this);
		menu.add(new Separator());
		super.rulerContextMenuAboutToShow(menu);
		FoldingExtensionPointLoader.fillRulerContextMenu(this, menu);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		menu.add(new Separator("debug")); // TODO: find where this constant is defined //$NON-NLS-1$

		super.editorContextMenuAboutToShow(menu);
		IUnifiedEditorContributor contributor = getBaseContributor();
		if (contributor != null)
		{
			String mimeType = contributor.getLocalContentType();
			if (mimeType != null && LanguageRegistry.getCodeFormatter(mimeType) != null)
			{
				menu.add(new Separator("source")); // TODO: add this constant //$NON-NLS-1$
				menu.add(_formatAction);
				_formatAction.setActiveEditor(null, this);
			}
			OpenDeclarationAction openDelcaration = new OpenDeclarationAction();
			openDelcaration.setActiveEditor(this, this.getFileContext().getLanguageService(mimeType));
			menu.add(openDelcaration);
		}
	}

	/**
	 * Adds the specified plugin to the list of plugin stores to check when searching for preference
	 * 
	 * @param plugin
	 *            The plugin to add
	 */
	protected void addPluginToPreferenceStore(AbstractUIPlugin plugin)
	{

		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] { this._runtimeStore,
				getPreferenceStore(), plugin.getPreferenceStore() }));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

		if (viewer.canDoOperation(ProjectionViewer.TOGGLE))
		{
			viewer.doOperation(ProjectionViewer.TOGGLE);
		}

		// TODO refactor to new colorizer system

		this._maxColorizingColumns = getPreferenceStore().getInt(IPreferenceConstants.COLORIZER_MAXCOLUMNS);
		linkColorer();

		linkPairMatcher();

		// Keyword/variable highlight
		this._extendedStylesEnabled = getPreferenceStore().getBoolean(
				IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED);
		if (this._extendedStylesEnabled)
		{
			installTextOccurrenceHighlightSupport();
		}

		// Overridden here, as this matches what JavaEditor does. Can be done at
		// any point after
		// text widget is created.
		LexemeUIHelp.setHelp(this, this.getViewer().getTextWidget(), getFileContext());

		// Add support for custom colors for background, selection, line
		// highlight, and caret
		setEditorOptions();
	}

	private void setEditorOptions()
	{
		// Check in case init hasn't been called yet or creating the contributor
		// failed
		if (getBaseContributor() == null || this.getViewer() == null || this.getViewer().getTextWidget() == null)
		{
			return;
		}

		String language = getBaseContributor().getLocalContentType();
		LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(language);
		if (colorizer != null && colorizer.getLineHighlightColor() != null && colorizer.getCaretColor() != null
				&& colorizer.getBackground() != null && colorizer.getSelectionForeground() != null
				&& colorizer.getSelectionBackground() != null)
		{
			if (this.getViewer() instanceof ITextViewerExtension2)
			{
				ITextViewerExtension2 highlightEditor = (ITextViewerExtension2) this.getViewer();
				if (this.getViewer().getTextWidget() != null)
				{
					if (this._painter != null)
					{
						highlightEditor.removePainter(this._painter);
					}
					this._painter = new UnifiedCursorLinePainter(this, this.getViewer());
					this._painter.setHighlightColor(colorizer.getLineHighlightColor());
					highlightEditor.addPainter(this._painter);

					if (this._selectionListener != null)
					{
						this.getViewer().getTextWidget().removeSelectionListener(this._selectionListener);
					}
					if (this._focusListener != null)
					{
						this.getViewer().getTextWidget().removeFocusListener(this._focusListener);
					}
					if (this._textListener != null)
					{
						this.getViewer().removeTextListener(this._textListener);
					}

					this._selectionListener = new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							StyledText text = getViewer().getTextWidget();

							if (_painter != null && text != null && !text.isDisposed())
							{
								Point p = text.getSelectionRange();
								if (p.y == 0)
								{
									_painter.paintLines(0, text.getLineCount());
									text.redraw();
									text.update();
								}
							}
						}
					};
					this._focusListener = new FocusAdapter()
					{
						public void focusGained(FocusEvent e)
						{
							StyledText text = getViewer().getTextWidget();

							if (_painter != null && text != null && !text.isDisposed())
							{
								Point p = text.getSelectionRange();
								if (p.y > 0)
								{
									_painter.paintLines(0, text.getLineCount());
								}
							}
						}
					};
					this._textListener = new ITextListener()
					{

						public void textChanged(TextEvent event)
						{
							StyledText text = getViewer().getTextWidget();

							if (_painter != null && text != null && !text.isDisposed())
							{
								Point p = text.getSelectionRange();
								if (p.y > 0)
								{
									_painter.paintLines(0, text.getLineCount());
								}
							}
						}
					};

					this.getViewer().addTextListener(this._textListener);
					this.getViewer().getTextWidget().addFocusListener(this._focusListener);
					this.getViewer().getTextWidget().addSelectionListener(this._selectionListener);

					if (isWordWrapEnabled())
					{
						this.getViewer().getTextWidget().setWordWrap(true);
						this.getViewer().getTextWidget().addControlListener(new ControlAdapter()
						{

							public void controlResized(ControlEvent e)
							{
								if (_projectionSupport != null)
								{
									UnifiedRulerColumn column = _projectionSupport.getRulerColumn();
									if (column != null)
									{
										column.redraw();
									}
								}
								IVerticalRuler ruler = getVerticalRuler();
								if (ruler instanceof CompositeRuler)
								{
									Iterator columnIter = ((CompositeRuler) ruler).getDecoratorIterator();
									while (columnIter.hasNext())
									{
										Object column = columnIter.next();
										if (column instanceof AnnotationRulerColumn)
										{
											((AnnotationRulerColumn) column).redraw();
										}
									}
								}
								if (fLineNumberRulerColumn != null)
								{
									fLineNumberRulerColumn.redraw();
								}
								IOverviewRuler overviewRuler = getOverviewRuler();
								if (overviewRuler != null)
								{
									overviewRuler.update();
								}
							}

						});
					}
				}
			}
			// Apply the folding background and forground colors
			setFoldingColors(colorizer);

			// Removing caret support on Carbon since it causes lag
		    if (!Platform.getWS().equals(Platform.WS_CARBON))
			{
				if (this.getViewer().getTextWidget() != null)
				{
					Caret caret = this.getViewer().getTextWidget().getCaret();

					// Correct cursor color accordining to line highlight color
					RGB converted = new RGB(0, 0, 0);
					converted.red = Math.abs(colorizer.getCaretColor().getRed()
							- colorizer.getLineHighlightColor().getRed());
					converted.blue = Math.abs(colorizer.getCaretColor().getBlue()
							- colorizer.getLineHighlightColor().getBlue());
					converted.green = Math.abs(colorizer.getCaretColor().getGreen()
							- colorizer.getLineHighlightColor().getGreen());

					PaletteData data = new PaletteData(new RGB[] { converted });
					int x = caret.getSize().x;
					int y = caret.getSize().y;
					// Apparently the current caret may have invalid sizings
					// that will cause errors when an attempt to
					// change the color is made. So perform the check and catch
					// errors and exceptions so caret coloring
					// doesn't affect opening the editor.
					if (x > 0 && y > 0)
					{
						try
						{
							ImageData iData = new ImageData(x, y, 1, data);
							caret.setImage(null);
							if (this._caretImage != null)
							{
								this._caretImage.dispose();
								this._caretImage = null;
							}
							this._caretImage = new Image(caret.getDisplay(), iData);
							caret.setImage(this._caretImage);
						}
						catch (Error e)
						{
						}
						catch (Exception e)
						{
						}
					}
				}
			}

			Color bg = colorizer.getBackground();
			Color deadSpace = createDeadSpaceColor(bg);
			if (deadSpace != null && !deadSpace.isDisposed())
			{
				this.getViewer().getTextWidget().setBackground(deadSpace);
			}
			else
			{
				this.getViewer().getTextWidget().setBackground(bg);
			}

			this.getViewer().getTextWidget().setSelectionBackground(colorizer.getSelectionBackground());
			this.getViewer().getTextWidget().setSelectionForeground(colorizer.getSelectionForeground());
		}
		else
		{
			IPreferenceStore store = EditorsPlugin.getDefault().getPreferenceStore();
			RGB background = PreferenceConverter.getColor(store, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			RGB selectionFg = PreferenceConverter.getColor(store,
					AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			RGB selectionBg = PreferenceConverter.getColor(store,
					AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND);
			Color eclipseBackgroundColor = UnifiedColorManager.getInstance().getColor(background);
			if (store.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT))
			{
				eclipseBackgroundColor = null;
			}
			Color eclipseSelectionFgColor = UnifiedColorManager.getInstance().getColor(selectionFg);
			if (store.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT))
			{
				eclipseSelectionFgColor = null;
			}
			Color eclipseSelectionBgColor = UnifiedColorManager.getInstance().getColor(selectionBg);
			if (store.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT))
			{
				eclipseSelectionBgColor = null;
			}

			Color deadSpace = createDeadSpaceColor(eclipseBackgroundColor);
			if (deadSpace != null)
			{
				this.getViewer().getTextWidget().setBackground(deadSpace);
			}
			else
			{
				this.getViewer().getTextWidget().setBackground(eclipseBackgroundColor);
			}

			this.getViewer().getTextWidget().setSelectionBackground(eclipseSelectionBgColor);
			this.getViewer().getTextWidget().setSelectionForeground(eclipseSelectionFgColor);
			Caret caret = this.getViewer().getTextWidget().getCaret();
	        caret.setImage(null);
			if (this._caretImage != null)
			{
				this._caretImage.dispose();
				this._caretImage = null;
			}
			if (this.getViewer() instanceof ITextViewerExtension2)
			{
				ITextViewerExtension2 highlightEditor = (ITextViewerExtension2) this.getViewer();
				if (this._painter != null)
				{
					highlightEditor.removePainter(this._painter);
				}
			}
		}
	}

	/**
	 * Apply the forground and the background colors of the folding markers and bar.
	 * 
	 * @param colorizer
	 */
	protected void setFoldingColors(LanguageColorizer colorizer)
	{
		if (colorizer.getFoldingFg() != null)
		{
			SourceViewerConfiguration config = this.getSourceViewerConfiguration();
			if (config instanceof UnifiedConfiguration)
			{
				UnifiedReconcilingStrategy strategy = ((UnifiedConfiguration) config).getStrategy();
				if (strategy != null)
				{
					strategy.setFoldingAnnotationHoverColor(colorizer.getFoldingFg());
				}
			}
		}
		if (colorizer.getFoldingBg() != null)
		{
			if (this._projectionSupport != null)
			{
				UnifiedRulerColumn column = this._projectionSupport.getRulerColumn();
				if (column != null)
				{
					column.setBackground(colorizer.getFoldingBg());
				}
			}
			if (fLineNumberRulerColumn != null && fLineNumberRulerColumn.getControl() != null
					&& !fLineNumberRulerColumn.getControl().isDisposed())
			{
				if (fLineNumberRulerColumn instanceof UnifiedLineNumberChangeRulerColumn)
				{
					((UnifiedLineNumberChangeRulerColumn) fLineNumberRulerColumn).setTextWidget(getViewer()
							.getTextWidget());
					((UnifiedLineNumberChangeRulerColumn) fLineNumberRulerColumn).setSourceViewer(getViewer());
				}
				fLineNumberRulerColumn.setBackground(colorizer.getFoldingBg());
				fLineNumberRulerColumn.redraw();
			}
		}
	}

	private boolean isWordWrapEnabled()
	{
		return UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.ENABLE_WORD_WRAP);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createLineNumberRulerColumn()
	 */
	protected IVerticalRulerColumn createLineNumberRulerColumn()
	{
		if (isWordWrapEnabled())
		{
			if (isPrefQuickDiffAlwaysOn())
			{
				UnifiedLineNumberChangeRulerColumn column = new UnifiedLineNumberChangeRulerColumn(getSharedColors());
				if (getViewer() != null)
				{
					column.setSourceViewer(getViewer());
					column.setTextWidget(getViewer().getTextWidget());
				}
				column.setHover(createChangeHover());
				// initializeChangeRulerColumn(column);
				fLineNumberRulerColumn = column;
			}
			else
			{
				fLineNumberRulerColumn = new UnifiedLineNumberChangeRulerColumn(getSharedColors());
				if (getViewer() != null)
				{
					((UnifiedLineNumberChangeRulerColumn) fLineNumberRulerColumn).setSourceViewer(getViewer());
					((UnifiedLineNumberChangeRulerColumn) fLineNumberRulerColumn).setTextWidget(getViewer()
							.getTextWidget());
				}
			}
			initializeLineNumberRulerColumn(fLineNumberRulerColumn);
			return fLineNumberRulerColumn;
		}
		else
		{
			return super.createLineNumberRulerColumn();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createOverviewRuler(org.eclipse.jface.text.source.ISharedTextColors)
	 */
	protected IOverviewRuler createOverviewRuler(ISharedTextColors sharedColors)
	{
		if (isWordWrapEnabled())
		{
			IOverviewRuler ruler = new UnifiedOverviewRuler(getAnnotationAccess(), VERTICAL_RULER_WIDTH, sharedColors);
			Iterator e = EditorsPlugin.getDefault().getMarkerAnnotationPreferences().getAnnotationPreferences()
					.iterator();
			while (e.hasNext())
			{
				AnnotationPreference preference = (AnnotationPreference) e.next();
				if (preference.contributesToHeader())
					ruler.addHeaderAnnotationType(preference.getAnnotationType());
			}
			return ruler;
		}
		else
		{
			return super.createOverviewRuler(sharedColors);
		}
	}

	private Color createDeadSpaceColor(Color bg)
	{
		if (bg != null)
		{
			int factor = 4;
			boolean canGoDarker = bg.getRed() - factor > 0 || bg.getGreen() - factor > 0 || bg.getBlue() - factor > 0;
			boolean canGoLighter = bg.getRed() + factor > 0 || bg.getGreen() + factor > 0 || bg.getBlue() + factor > 0;
			int red = 0;
			int green = 0;
			int blue = 0;
			if (canGoDarker)
			{
				red = bg.getRed() - factor > 0 ? (int) (bg.getRed() - factor) : bg.getRed();
				green = bg.getGreen() - factor > 0 ? (int) (bg.getGreen() - factor) : bg.getGreen();
				blue = bg.getBlue() - factor > 0 ? (int) (bg.getBlue() - factor) : bg.getBlue();
			}
			else if (canGoLighter)
			{
				red = bg.getRed() + factor > 0 ? (int) (bg.getRed() + factor) : bg.getRed();
				green = bg.getGreen() + factor > 0 ? (int) (bg.getGreen() + factor) : bg.getGreen();
				blue = bg.getBlue() + factor > 0 ? (int) (bg.getBlue() + factor) : bg.getBlue();
			}
			if (canGoDarker || canGoLighter)
			{
				Color deadSpace = UnifiedColorManager.getInstance().getColor(new RGB(red, green, blue));
				return deadSpace;
			}
		}

		return null;
	}

	private void installTextOccurrenceHighlightSupport()
	{
		ISelectionProvider selectionProvider = getSelectionProvider();
		if (selectionProvider instanceof IPostSelectionProvider)
		{
			IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
			provider.addPostSelectionChangedListener(this);
		}
		else
		{
			selectionProvider.addSelectionChangedListener(this);
		}
		this._extendedStylesEnabled = true;
	}

	private void uninstallTextOccurrenceHighlightSupport()
	{
		ISelectionProvider selectionProvider = getSelectionProvider();
		if (selectionProvider != null)
		{
			if (selectionProvider instanceof IPostSelectionProvider)
			{
				IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
				provider.removePostSelectionChangedListener(this);
			}
			else
			{
				selectionProvider.removeSelectionChangedListener(this);
			}
		}
		removeMarkedOccurrences();
		this._extendedStylesEnabled = false;
	}

	private IRange underLine;

	private Annotation[] fOccurrenceAnnotations;

	/**
	 * removing given range from underlined state
	 * 
	 * @param range
	 */
	void removeUnderlinedRange(IRange range)
	{
		underLine = range;
		StyledText textWidget = getViewer().getTextWidget();
		try
		{
			textWidget.redrawRange(range.getStartingOffset(), range.getLength(), true);
		}
		catch (IllegalArgumentException e)
		{
			// Do nothing if the range being invalid causes an exception
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IRedrawRangeListener#redrawRange(com.aptana.ide.lexer.IRange)
	 */
	public void redrawRange(final IRange range)
	{
		if (range != null)
		{
			final StyledText textWidget = getViewer().getTextWidget();
			Display display = textWidget.getDisplay();

			display.asyncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						textWidget.redrawRange(range.getStartingOffset(), range.getLength(), true);
					}
					catch (IllegalArgumentException e)
					{
						// Do nothing if the range being invalid causes an exception
					}
				}
			});
		}
	}

	/**
	 * sets current range to be underlined
	 * 
	 * @param range
	 */
	void setUnderlinedRange(IRange range)
	{
		if (range != null)
		{
			underLine = null;
			StyledText textWidget = getViewer().getTextWidget();
			try
			{
				textWidget.redrawRange(range.getStartingOffset(), range.getLength(), true);
			}
			catch (IllegalArgumentException e)
			{
				// Do nothing if the range being invalid causes an exception
			}
		}
	}

	private void linkColorer()
	{
		if (this._lineStyleListener == null)
		{
			this._lineStyleListener = new LineStyleListener()
			{
				/**
				 * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(org.eclipse.swt.custom.LineStyleEvent)
				 */
				public void lineGetStyle(LineStyleEvent e)
				{
					EditorFileContext fileContext = getFileContext();

					// wrapper could be non-null, but interior is.
					if (fileContext == null || fileContext.getFileContext() == null)
					{
						return;
					}

					IParseState parseState = fileContext.getParseState();

					if (parseState == null)
					{
						return;
					}

					LexemeList lexemeList = parseState.getLexemeList();

					if (lexemeList == null)
					{
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_LexemeListIsNull);
						return;
					}

					int orgOffset = e.lineOffset;
					int offset = orgOffset;
					int extra = 0;
					int lineLength = e.lineText.length();

					// need to get actual offset values in the doc,
					// as widget offsets do not include potentially folded code
					if (_viewer instanceof ITextViewerExtension5)
					{
						ITextViewerExtension5 v5 = (ITextViewerExtension5) _viewer;
						offset = v5.widgetOffset2ModelOffset(e.lineOffset);
						extra = offset - e.lineOffset;
					}

					int maxLineLength = !isWordWrapEnabled() && lineLength > _maxColorizingColumns ? _maxColorizingColumns
							: lineLength;
					Lexeme[] lexemes = null;

					synchronized (lexemeList)
					{
						int startingIndex = lexemeList.getLexemeCeilingIndex(offset);
						int endingIndex = lexemeList.getLexemeFloorIndex(offset + maxLineLength);

						if (startingIndex == -1 && endingIndex != -1)
						{
							startingIndex = endingIndex;
						}

						if (endingIndex == -1 && startingIndex != -1)
						{
							endingIndex = startingIndex;
						}

						if (startingIndex != -1 && endingIndex != -1)
						{
							lexemes = lexemeList.cloneRange(startingIndex, endingIndex);
						}
					}

					if (lexemes != null)
					{
						Vector<StyleRange> styles = new Vector<StyleRange>();

						_colorizer.createStyles(parseState, styles, lexemes, _extendedStylesEnabled);

						StyleRange[] styleResults = styles.toArray(new StyleRange[] {});

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
						if (underLine != null)
						{
							for (int a = 0; a < styleResults.length; a++)
							{
								StyleRange styleRange = styleResults[a];
								if (styleRange.start == underLine.getStartingOffset()
										&& (styleRange.start + styleRange.length) == underLine.getEndingOffset())
								{
									styleRange.underline = true;
								}

							}
						}
						else
						{
							for (int a = 0; a < styleResults.length; a++)
							{
								StyleRange styleRange = styleResults[a];
								styleRange.underline = false;

							}
						}
						e.styles = styleResults;
					}
					UnifiedViewer rr = (UnifiedViewer) getViewer();
					TextPresentation textPresentation = rr.getTextPresentation();
					if (textPresentation != null)
					{
						TextPresentation ps = new TextPresentation();
						if (e.styles != null)
						{
							ps.replaceStyleRanges(e.styles);
						}
						Iterator<?> nonDefaultStyleRangeIterator = textPresentation.getNonDefaultStyleRangeIterator();
						while (nonDefaultStyleRangeIterator.hasNext())
						{
							// Make sure that the StyleRange is at a visible location in case we have folded blocks in
							// the code.
							StyleRange clone = (StyleRange) ((StyleRange) nonDefaultStyleRangeIterator.next()).clone();
							IRegion region = new Region(clone.start, clone.length);
							region = rr.modelRange2WidgetRange(region);
							if (region != null)
							{
								clone.start = region.getOffset();
								clone.length = region.getLength();
								ps.mergeStyleRange(clone);
							}
						}
						Iterator<?> nonDefaultStyleRangeIterator2 = ps.getNonDefaultStyleRangeIterator();
						ArrayList<StyleRange> rs = new ArrayList<StyleRange>();
						while (nonDefaultStyleRangeIterator2.hasNext())
						{
							StyleRange next = (StyleRange) nonDefaultStyleRangeIterator2.next();
							rs.add(next);
						}
						e.styles = rs.toArray(new StyleRange[rs.size()]);
					}

				}
			};
		}

		// Repaint lines if the user is making changes
		if (this._textChangeListener == null)
		{
			this._textChangeListener = new TextChangeListener()
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
						// Catch errors in redraw as they may be intermittent
						// and the subsequent redraw will complete
						// successfully
					}
				}
			};
		}

		getViewer().getTextWidget().addLineStyleListener(this._lineStyleListener);
		getViewer().getTextWidget().getContent().addTextChangeListener(this._textChangeListener);
	}

	private void linkPairMatcher()
	{
		if (this._pairMatcher == null)
		{
			this._pairMatcher = new PairMatcher();

			StyledText text = getViewer().getTextWidget();

			text.addPaintListener(this._pairMatcher);
			text.addKeyListener(this._pairMatcher);
			text.addTraverseListener(this._pairMatcher);
			text.addMouseListener(this._pairMatcher);
			text.addSelectionListener(this._pairMatcher);
			ScrollBar sb = text.getVerticalBar();
			if (sb != null)
			{
				sb.addSelectionListener(this._pairMatcher);
			}
		}
	}

	private void unlinkPairMatcher()
	{
		if (this._pairMatcher != null)
		{
			if (getViewer() == null)
			{
				return;
			}

			StyledText text = getViewer().getTextWidget();

			if (text == null || text.isDisposed() == true)
			{
				return;
			}

			text.removePaintListener(this._pairMatcher);
			text.removeKeyListener(this._pairMatcher);
			text.removeTraverseListener(this._pairMatcher);
			text.removeMouseListener(this._pairMatcher);
			text.removeSelectionListener(this._pairMatcher);
			ScrollBar sb = text.getVerticalBar();
			if (sb != null)
			{
				sb.removeSelectionListener(this._pairMatcher);
			}
		}

	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getPairMatch(int)
	 */
	public PairMatch getPairMatch(int offset)
	{
		IParseState parseState = this.getFileContext().getParseState();
		if (parseState == null)
		{
			return null;
		}
		LexemeList lexemeList = parseState.getLexemeList();
		Lexeme cursorLexeme = lexemeList.getLexemeFromOffset(offset);

		if (cursorLexeme == null && offset > 0)
		{
			cursorLexeme = lexemeList.getLexemeFromOffset(offset - 1);
		}
		if (cursorLexeme == null)
		{
			return null;
		}

		String language = cursorLexeme.getLanguage();
		IPairFinder finder = LanguageRegistry.getPairFinder(language);
		PairMatch match = null;

		if (finder != null && !finder.doNotDisplay())
		{
			if (finder instanceof AbstractPairFinder)
			{
				// NOTE: [KEL] loop count relates to each pair finder
				// implementation and should not be passed in
				// as an argument
				int loopCount = 2;

				match = ((AbstractPairFinder) finder).findPairMatch(offset, parseState, cursorLexeme, loopCount);
			}
			else
			{
				match = finder.findPairMatch(offset, parseState);
			}
		}

		if (match != null)
		{
			match.setColor(finder.getPairFinderColor());
			match.setDisplayOnlyMatch(finder.displayOnlyMatch());
			match.offset = offset;

			if (match instanceof IPairMatchExt)
			{
				List<PairMatch> subsequentMatches = ((IPairMatchExt) match).getSubsequentMatches();
				for (PairMatch subMatch : subsequentMatches)
				{
					subMatch.setColor(finder.getPairFinderColor());
					subMatch.setDisplayOnlyMatch(finder.displayOnlyMatch());
					subMatch.offset = offset;
				}
			}
			// NOTE: [KEL] The following is subjective and therefore should
			// become a preference. Turning off for now
			// since this breaks the case where the matched pair on the right
			// touches but the matcher pair to the left
			// does not. In that scenario, we show no matching when we should
			// show the pair to the left

			// // This check is for the case when the pair match will border
			// each other which present a cluttered UI
			// with
			// // the match and the cursor
			// if (match.beginEnd == match.endStart || match.endEnd ==
			// match.beginStart)
			// {
			// match = null;
			// }
		}

		return match;
	}

	/**
	 * findBalancingLexeme
	 * 
	 * @param startIndex
	 * @param language
	 * @param startType
	 * @param endType
	 * @param direction
	 * @return Lexeme
	 */
	protected Lexeme findBalancingLexeme(int startIndex, String language, int startType, int endType, int direction)
	{
		LexemeList lexemeList = this.getFileContext().getParseState().getLexemeList();
		return findBalancingLexeme(lexemeList, startIndex, language, startType, endType, direction);
	}

	/**
	 * @param lexemeList
	 * @param startIndex
	 * @param language
	 * @param startType
	 * @param endType
	 * @param direction
	 * @return Lexeme
	 */
	public static Lexeme findBalancingLexeme(LexemeList lexemeList, int startIndex, String language, int startType,
			int endType, int direction)
	{
		Lexeme result = null;

		int count = 0;

		while (0 <= startIndex && startIndex < lexemeList.size())
		{
			result = lexemeList.get(startIndex);

			if (result.getLanguage().equals(language))
			{
				if (result.typeIndex == endType)
				{
					count--;

					if (count == 0)
					{
						break;
					}
				}
				else if (result.typeIndex == startType)
				{
					count++;
				}
			}

			startIndex += direction;
		}

		if (count != 0)
		{
			result = null;
		}

		return result;
	}

	/**
	 * @author Paul Colton
	 */
	class PairMatcher implements SelectionListener, MouseListener, KeyListener, PaintListener, TraverseListener
	{

		private PairMatch _currentPair;

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDoubleClick(MouseEvent e)
		{
		}

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDown(MouseEvent e)
		{
			stateChanged();
		}

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseUp(MouseEvent e)
		{
		}

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		public void keyPressed(KeyEvent e)
		{
			stateChanged();
		}

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
		 */
		public void keyReleased(KeyEvent e)
		{
		}

		/**
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e)
		{
			stateChanged();
			pairsDraw(e.gc, this._currentPair);
		}

		/**
		 * @see org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.events.TraverseEvent)
		 */
		public void keyTraversed(TraverseEvent e)
		{
			stateChanged();
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			stateChanged();
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		void stateChanged()
		{
			StyledText text = getViewer().getTextWidget();
			if (text != null)
			{
				int offset = text.getCaretOffset();
				if (getViewer() instanceof ITextViewerExtension5)
				{
					int resolved = ((ITextViewerExtension5) getViewer()).widgetOffset2ModelOffset(offset);
					if (resolved > -1)
					{
						offset = resolved;
					}
				}
				PairMatch newmatch = getPairMatch(offset);

				if ((newmatch == null && this._currentPair != null)
						|| (newmatch != null && !newmatch.equals(this._currentPair)))
				{
					pairsDraw(null, this._currentPair);
					pairsDraw(null, newmatch);
				}

				this._currentPair = newmatch;
			}
		}

		void pairsDraw(GC gc, PairMatch pm)
		{
			if (pm == null)
			{
				return;
			}
			pairsDrawSingle(gc, pm);
			if (pm instanceof IPairMatchExt)
			{
				List<PairMatch> subsequentMatches = ((IPairMatchExt) pm).getSubsequentMatches();
				if (subsequentMatches != null && subsequentMatches.size() > 0)
				{
					for (PairMatch subsequentMatch : subsequentMatches)
					{
						pairsDrawSingle(gc, subsequentMatch);
					}
				}
			}

		}

		void pairsDrawSingle(GC gc, PairMatch pm)
		{
			if (pm == null)
			{
				return;
			}
			StyledText text = getViewer().getTextWidget();
			if (text != null)
			{
				int cursor = text.getCaretOffset();
				if (pm.displayOnlyMatch() && gc != null)
				{
					if (cursor >= pm.beginStart && cursor <= pm.beginEnd)
					{
						pairDraw(gc, pm.endStart, pm.endEnd, pm.getColor());
					}
					else if (cursor >= pm.endStart && cursor <= pm.endEnd)
					{
						pairDraw(gc, pm.beginStart, pm.beginEnd, pm.getColor());
					}
				}
				else
				{
					pairDraw(gc, pm.beginStart, pm.beginEnd, pm.getColor());
					pairDraw(gc, pm.endStart, pm.endEnd, pm.getColor());
				}
			}

		}

		void pairDraw(GC gc, int start, int end, Color color)
		{
			StyledText text = getViewer().getTextWidget();
			if (getViewer() instanceof ITextViewerExtension5)
			{
				ITextViewerExtension5 v5 = (ITextViewerExtension5) getViewer();
				start = v5.modelOffset2WidgetOffset(start);
				end = v5.modelOffset2WidgetOffset(end);
			}
			if (start < 0 || end < 0 || start > text.getCharCount() || end > text.getCharCount())
			{
				return;
			}

			if (gc != null)
			{
				try
				{
					Point left = text.getLocationAtOffset(start);
					Point right = text.getLocationAtOffset(end);

					gc.setForeground(color);
					gc.setLineWidth(1);
					gc.drawRectangle(left.x + 1, left.y + 1, right.x - left.x - 2, gc.getFontMetrics().getHeight() - 2);
				}
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
							Messages.UnifiedEditor_PairDraw, e.getMessage()));
				}
			}
			else
			{
				text.redrawRange(start, end - start, true);
			}
		}

	}

	/**
	 * getDefaultFileExtension
	 * 
	 * @return String
	 */
	public abstract String getDefaultFileExtension();

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e)
	{
		// if (textColorer == null)
		// return;
		//        
		// if (e == null ||
		// e.getProperty().equals(ColorizerPreferencePage.USE_BACK) ||
		// e.getProperty().equals(ColorizerPreferencePage.HRD_SET))
		// {
		// textColorer.setRegionMapper(
		// prefStore.getString(ColorizerPreferencePage.HRD_SET),
		// prefStore.getBoolean(ColorizerPreferencePage.USE_BACK));
		// }
		// if (e == null ||
		// e.getProperty().equals(ColorizerPreferencePage.FULL_BACK)){
		// textColorer.setFullBackground(prefStore.getBoolean(ColorizerPreferencePage.FULL_BACK));
		// }
		// if (e == null ||
		// e.getProperty().equals(ColorizerPreferencePage.HORZ_CROSS) ||
		// e.getProperty().equals(ColorizerPreferencePage.VERT_CROSS))
		// {
		// textColorer.setCross(
		// prefStore.getBoolean(ColorizerPreferencePage.HORZ_CROSS),
		// prefStore.getBoolean(ColorizerPreferencePage.VERT_CROSS));
		// }
		//
		// if (e == null ||
		// e.getProperty().equals(ColorizerPreferencePage.PAIRS_MATCH)) {
		// String pairs =
		// prefStore.getString(ColorizerPreferencePage.PAIRS_MATCH);
		// int pmode = TextColorer.HLS_XOR;
		// if (pairs.equals("PAIRS_OUTLINE"))
		// pmode = TextColorer.HLS_OUTLINE;
		// if (pairs.equals("PAIRS_OUTLINE2"))
		// pmode = TextColorer.HLS_OUTLINE2;
		// textColorer.setPairsPainter(!pairs.equals("PAIRS_NO"), pmode);
		// }

		// if (e == null ||
		// e.getProperty().equals(ColorizerPreferencePage.TEXT_FONT)){
		// Font textFont =
		// JFaceResources.getFont(ColorizerPreferencePage.TEXT_FONT);
		// getViewer().getTextWidget().setFont(textFont);
		// }
	}

	/**
	 * Are we currently the active editor?
	 * 
	 * @return boolean
	 */
	public boolean isActiveEditor()
	{
		IEditorPart part = getEditorSite().getPage().getActiveEditor();

		if (part == null)
		{
			return false;
		}
		else if (part instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) part;

			return editor.getEditor() == this;
		}
		else
		{
			return part == this;
		}
	}

	/**
	 * onKeyPressed
	 * 
	 * @param event
	 */
	private void onKeyPressed(VerifyEvent event)
	{
		char c = event.character;

		StyledText styledText = (StyledText) event.widget;
		int keyCode = event.keyCode;

		// when undoing a template, the caret is not reset at this point
		ITypedRegion reg = this._fileContextWrapper.getPartitionAtOffset(styledText.getCaretOffset());

		if (reg == null)
		{
			return;
		}

		final String contentType = reg.getType();
		IUnifiedEditorContributor contributor = this._baseContributor.findChildContributor(contentType);

		if (contributor != null && contributor.isAutoActivateContentAssist()
				&& contributor.isValidIdentifier(c, keyCode)
				&& isLeftCharacterWhitespace(contributor, styledText, c, keyCode))
		{
			getViewer().getTextWidget().setData(IContentAssistConstants.ASSIST_FORCE_ACTIVATION, true);
			showContentAssist();
		}
	}

	/**
	 * isLeftCharacterWhitespace
	 * 
	 * @param styledText
	 * @param c
	 * @param keyCode
	 * @return boolean
	 */
	private boolean isLeftCharacterWhitespace(IUnifiedEditorContributor contributor, StyledText styledText, char c,
			int keyCode)
	{
		int offset = styledText.getCaretOffset();

		// Are we at beginning of file?
		if (offset == 0)
		{
			return true;
		}

		String line = styledText.getText(offset - 1, offset - 1);

		if (line.length() > 0)
		{
			return contributor.isValidActivationCharacter(line.charAt(0), keyCode);
		}
		else
		{
			return false;
		}
	}

	/**
	 * showContentAssist
	 */
	private void showContentAssist()
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();

		display.asyncExec(new Runnable()
		{
			public void run()
			{
				try
				{
					final SourceViewer sv = (SourceViewer) getSourceViewer();

					if (sv == null)
					{
						return; // guard against already closed editor
					}

					// In case you switched away
					Control c = display.getFocusControl();
					if (c == null || c != sv.getTextWidget())
					{
						return;
					}

					// Check if source viewer is able to perform operation
					if (sv.canDoOperation(SourceViewer.CONTENTASSIST_PROPOSALS))
					{
						// Perform operation
						sv.doOperation(SourceViewer.CONTENTASSIST_PROPOSALS);
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_ErrorContentProposals, e);
				}
			}
		});
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showWhitespace(boolean)
	 */
	public void showWhitespace(boolean state)
	{
		ISourceViewer fSourceViewer = this.getSourceViewer();

		if (state)
		{
			if (this._whitespacePainter == null)
			{
				if (fSourceViewer instanceof ITextViewerExtension2)
				{
					this._whitespacePainter = new WhitespaceCharacterPainter(fSourceViewer);
					ITextViewerExtension2 extension = (ITextViewerExtension2) fSourceViewer;
					extension.addPainter(this._whitespacePainter);
				}
			}
		}
		else
		{
			if (this._whitespacePainter != null)
			{

				if (fSourceViewer instanceof ITextViewerExtension2)
				{
					ITextViewerExtension2 extension = (ITextViewerExtension2) fSourceViewer;
					extension.removePainter(this._whitespacePainter);
					this._whitespacePainter.deactivate(true);
					this._whitespacePainter.dispose();
					this._whitespacePainter = null;
					// this.getFileContext().removeLongDelayedFileListener(fWhitespacePainter);
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#showPianoKeys(boolean)
	 */
	public void showPianoKeys(boolean state)
	{
		if (this._painter != null)
		{
			StyledText text = getViewer().getTextWidget();
			this._painter.paintLines(0, text.getLineCount());
		}
	}

	/**
	 * Updates the folding structure
	 * 
	 * @param annotations
	 */
	public void updateFoldingStructure(Map annotations)
	{
		List<Annotation> deletions = new ArrayList<Annotation>();
		Collection additions = annotations.values();
		ProjectionAnnotationModel currentModel = getProjectionAnnotationModel();
		if (currentModel == null)
		{
			return;
		}
		for (Iterator iter = currentModel.getAnnotationIterator(); iter.hasNext();)
		{
			Object annotation = iter.next();
			if (annotation instanceof ProjectionAnnotation)
			{
				Position position = currentModel.getPosition((Annotation) annotation);
				if (additions.contains(position))
				{
					additions.remove(position);
				}
				else
				{
					deletions.add((Annotation) annotation);
				}
			}
		}
		if (annotations.size() != 0 || deletions.size() != 0)
		{
			currentModel.modifyAnnotations(deletions.toArray(new Annotation[deletions.size()]), annotations, null);
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		// **** NOTE ****
		// we are not using Projection Viewer in order to get shift left working
		// (to shift until no
		// whitespace)
		// if this is problematic or 3.2 exposes that in a more intelligent way
		// (or fixes that
		// issue)
		// we can get rid of UnifiedViewer, and revert this one line of code
		// here (move back to
		// using
		// projectionViewer).

		// ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		// getOverviewRuler(),
		// isOverviewRulerVisible(),
		// styles);
		this._viewer = new UnifiedViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(this._viewer);

		this._projectionSupport = new UnifiedProjectionSupport(this._viewer, getAnnotationAccess(), getSharedColors());
		this._projectionSupport.install();

		SourceViewer sv = this._viewer;
		Display d = this.getSite().getShell().getDisplay();
		this._keyUpListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				UnifiedEditor.ctrlDown = false;
			}
		};
		d.addFilter(SWT.KeyUp, this._keyUpListener);

		this._verifyKeyListener = new VerifyKeyListener()
		{
			public void verifyKey(VerifyEvent event)
			{
				_hasKeyBeenPressed = true;

				onKeyPressed(event);

				// Check for Ctrl
				// note: state mask doesn't work for some reason
				if (event.keyCode == SWT.CTRL)
				{
					UnifiedEditor.ctrlDown = true;
				}
				else
				{
					UnifiedEditor.ctrlDown = false;
				}
			}
		};

		sv.prependVerifyKeyListener(this._verifyKeyListener);

		SourceViewerConfiguration svc = this.getSourceViewerConfiguration();
		if (svc instanceof UnifiedConfiguration)
		{
			UnifiedConfiguration uc = (UnifiedConfiguration) svc;
			this._bracketInserterManager = uc.getBracketInserterManager(sv);
			sv.prependVerifyKeyListener(this._bracketInserterManager);
		}

		return this._viewer;
	}

	// private IPreferenceStore fPreferenceStore;

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSetInput(org.eclipse.ui.IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException
	{
		// After Save As, we are getting a new IEditorInput each time
		// implicitly enters it into the hash of the document provider, using
		// input as the key
		super.doSetInput(input);

		try
		{
			// Get the document provider (which is a singleton)
			IDocumentProvider dp = getDocumentProvider();
			if (dp == null)
			{
				throw new Exception(Messages.UnifiedEditor_DocumentProviderNull);
			}

			// Get document from input
			IDocument document = dp.getDocument(input);

			// TODO: Actuate change
			// UnifiedFileInfo cuInfo = (UnifiedFileInfo)
			// dp.getFileInfoPublic(input);
			//
			// if (cuInfo == null)
			// {
			// throw new Exception(Messages.UnifiedEditor_CuInfoIsNull);
			// }

			DocumentSourceProvider provider = new DocumentSourceProvider(document, input);
			if (provider == null)
			{
				throw new Exception(Messages.UnifiedEditor_ProviderIsNull);
			}

			// TODO: Actuate change
			// boolean isNewInput = (cuInfo.sourceProvider == null ||
			// cuInfo.sourceProvider.equals(provider) == false);
			boolean isNewInput = isNewInput(input);

			// update various stuffs and more :-)
			// TODO: Actuate changes
			// updateFileInfo(input, provider, cuInfo, document, isNewInput);
			updateFileInfo(input, provider, document);

			// Commented out for now, as this code now lives in
			// UnifiedDocumentProvider
			// updateAnnotationModel(cuInfo);

			// TODO: Actuate changes
			// updatePartitioner(provider, cuInfo, document, isNewInput);
			updatePartitioner(provider, document, isNewInput);

			// Set the actual file context onto the wrapper
			this._fileContextWrapper.setFileContext(FileContextManager.get(provider.getSourceURI()));

			// Set up error listeners based on the the input file type
			setErrorListeners(input);

			fireNewFileServiceEvent();

			// Refresh colorization
			if (this.getViewer() != null && this.getViewer().getTextWidget() != null)
			{
				this.getViewer().getTextWidget().redraw();
				this.getViewer().getTextWidget().update();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), "Unable to set input in UnifiedEditor: " //$NON-NLS-1$
					+ e.getMessage(), e);
			IStatus status = new Status(IStatus.ERROR, UnifiedEditorsPlugin.ID, IStatus.OK,
					"Unable to set input in Unified Editor", e);//$NON-NLS-1$
			throw new CoreException(status);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#addFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void addFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		fileServiceChangeListeners.add(listener);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#removeFileServiceChangeListener(com.aptana.ide.editors.unified.IFileServiceChangeListener)
	 */
	public void removeFileServiceChangeListener(IFileServiceChangeListener listener)
	{
		fileServiceChangeListeners.remove(listener);
	}

	/**
	 * Updates the file service listeners with the new service
	 */
	private void fireNewFileServiceEvent()
	{
		if (this._fileContextWrapper != null && this._fileContextWrapper.getFileContext() != null)
		{
			final IFileService newService = this._fileContextWrapper.getFileContext();
			// Notify file service listeners
			Object[] listeners = fileServiceChangeListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i)
			{
				final IFileServiceChangeListener l = (IFileServiceChangeListener) listeners[i];
				SafeRunnable.run(new SafeRunnable()
				{
					public void run()
					{
						l.fileServiceChanged(newService);
					}
				});
			}
		}
	}

	private void setErrorListeners(IEditorInput input)
	{
		// Project file
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			IFile file = fileInput.getFile();
			this._errorListener = new ProjectFileErrorListener(file);
		}
		// External file or Untitled file (not saved)
		else if (input instanceof IPathEditorInput || input instanceof IURIEditorInput || input instanceof NonExistingFileEditorInput)
		{
			IDocument doc = this.getDocumentProvider().getDocument(input);
			IAnnotationModel ann = this.getDocumentProvider().getAnnotationModel(input);
			this._errorListener = new ExternalFileErrorListener(ann, doc);
		}

		if (this._errorListener != null)
		{
			getFileContext().addErrorListener(this._errorListener);
		}
	}

	/**
	 * updateFileInfo
	 * 
	 * @param input
	 * @param provider
	 * @param document
	 */
	protected void updateFileInfo(IEditorInput input, DocumentSourceProvider provider, IDocument document)
	{
		if (isNewInput(input))
		{
			// TODO: Actuate change
			// save reference to provider
			// cuInfo.sourceProvider = provider;

			IFileServiceFactory fileServiceFactory = this.getFileServiceFactory();

			if (fileServiceFactory != null)
			{
				FileService context = fileServiceFactory.createFileService(provider, false);

				FileContextManager.add(provider.getSourceURI(), context);

				context.setRedrawRangeListener(this);
			}

			FileContextManager.connectSourceProvider(provider.getSourceURI(), provider);
		}
	}

	// private void updateAnnotationModel(UnifiedFileInfo cuInfo)
	// {
	// // Provide a simple annotation model if none has been provided
	// // fixes bug 156 - line numbers are not displayed for external files
	// // PC: not needed? cuInfo.fModel = new AnnotationModel();
	//
	// if (cuInfo.fTextFileBuffer.getAnnotationModel() == null)
	// {
	// // when editing external files, there will not be an annotation model
	// attached, so we
	// // attach a simple one.
	// // This is necessary so that the source viewer's annotation bar (which
	// displays line
	// // numbers, etc) will be displayed
	// IUniformResource uniformResource = null;
	//
	// if (cuInfo.fElement instanceof IAdaptable)
	// {
	// uniformResource = (IUniformResource) ((IAdaptable)
	// cuInfo.fElement).getAdapter(IUniformResource.class);
	// }
	//
	// if (uniformResource != null)
	// {
	// cuInfo.fModel = new
	// UniformResourceMarkerAnnotationModel(uniformResource);
	// }
	// else
	// {
	// cuInfo.fModel = new AnnotationModel();
	// }
	// }
	// else
	// {
	// cuInfo.fModel = cuInfo.fTextFileBuffer.getAnnotationModel();
	// }
	// }

	private void updatePartitioner(DocumentSourceProvider provider, IDocument document, boolean isNewInput)
	{
		if (isNewInput)
		{
			UnifiedDocumentPartitioner partitioner = new UnifiedDocumentPartitioner(provider.getSourceURI());

			// NOTE: The content types (and other stuff) will change when a user
			// does a Save As to another file type (i.e. JS to HTML)
			partitioner.setLegalContentTypes(this._baseContributor.getContentTypes());
			partitioner.setPartitions();

			if (document instanceof IDocumentExtension3)
			{
				// this should always be the once called
				((IDocumentExtension3) document).setDocumentPartitioner(UnifiedConfiguration.UNIFIED_PARTITIONING,
						partitioner);
			}
			else
			{
				// document.setDocumentPartitioner(partitioner);
				throw new IllegalStateException(Messages.UnifiedEditor_DocumentMustBe);
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getFileContext()
	 */
	public EditorFileContext getFileContext()
	{
		return this._fileContextWrapper;
	}

	/**
	 * createLocalContributor
	 * 
	 * @return IUnifiedEditorContributor
	 */
	protected abstract IUnifiedEditorContributor createLocalContributor();

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (this._isDisposing)
		{
			return;
		}

		this._isDisposing = true;

		if (this._lineStyleListener != null)
		{
			if (getViewer().getTextWidget() != null && getViewer().getTextWidget().isDisposed() == false)
			{
				getViewer().getTextWidget().removeLineStyleListener(this._lineStyleListener);
			}
		}

		if (this._textChangeListener != null)
		{
			if (getViewer().getTextWidget() != null && getViewer().getTextWidget().isDisposed() == false)
			{
				getViewer().getTextWidget().getContent().removeTextChangeListener(this._textChangeListener);
			}
		}

		unlinkPairMatcher();

		if (this._errorListener != null)
		{
			getFileContext().removeErrorListener(this._errorListener);
		}

		if (this._partListener != null)
		{
			this.getEditorSite().getPage().removePartListener(this._partListener);
			this._partListener = null;
		}

		if (this._keyUpListener != null)
		{
			Display d = this.getSite().getShell().getDisplay();
			d.removeFilter(SWT.KeyUp, this._keyUpListener);
			this._keyUpListener = null;
		}
		if (_verifyKeyListener != null)
		{
			((SourceViewer) this._viewer).removeVerifyKeyListener(this._verifyKeyListener);
			this._verifyKeyListener = null;
		}
		if (this._bracketInserterManager != null)
		{
			((SourceViewer) this._viewer).removeVerifyKeyListener(this._bracketInserterManager);
			this._bracketInserterManager = null;
		}

		SourceViewerConfiguration svc = this.getConfiguration();
		if (svc instanceof UnifiedConfiguration)
		{
			((UnifiedConfiguration) svc).dispose();
		}

		if (outlinePage != null)
		{
			outlinePage.dispose();
			outlinePage = null;
		}

		showWhitespace(false);
		if (this._whitespacePainter != null)
		{
			this._whitespacePainter.dispose();
		}

		this._saveListeners.clear();
		this._saveAsListeners.clear();

		this._lineStyleListener = null;
		outlinePage = null;

		if (this._baseContributor != null)
		{
			this._baseContributor.setParentConfiguration(null);
			this._baseContributor = null;
		}

		this._errorListener = null;
		this._whitespacePainter = null;
		this._fileExplorerView = null;
		this._prefStore = null;

		this._contextAwareness = null;

		this.disposeDocumentProvider();

		if (this._fileContextWrapper != null)
		{
			this._fileContextWrapper.deactivateForEditing();
			// The following was commented out since:
			// it disconnects regardless if another file is using the file
			// context (two editors around same document)
			// Looking at this.disposeDocumentProvider() shows the proper checks
			// before disconnecting and so the
			// following line was redundant as well since the document provider
			// handles it correctly and so calling it
			// here will just ruin any other open editors using the file context
			// being disposed
			// fileContextWrapper.disconnectSourceProvider(null);
		}

		if (this._extendedStylesEnabled)
		{
			uninstallTextOccurrenceHighlightSupport();
		}

		if (this._caretImage != null)
		{
			this._caretImage.dispose();
			this._caretImage = null;
		}

		super.dispose();
	}

	/**
	 * getFileServiceFactory
	 * 
	 * @return IFileServiceFactory
	 */
	public abstract IFileServiceFactory getFileServiceFactory();

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		// Context is just plugin ID + name of class. Matches contexts.xml file
		if (adapter == IContextProvider.class)
		{
			return LexemeUIHelp.getHelpContextProvider(this, getFileContext());
		}

		// Return our adapter for the content outline page
		if (IContentOutlinePage.class.equals(adapter))
		{
			return getOutlinePage();
		}

		return super.getAdapter(adapter);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getViewer()
	 */
	public ISourceViewer getViewer()
	{
		return this.getSourceViewer();
	}

	/**
	 * // exposing this as public for scripting access
	 * 
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getConfiguration()
	 */
	public SourceViewerConfiguration getConfiguration()
	{
		return this.getSourceViewerConfiguration();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getContextAwareness()
	 */
	public IContextAwareness getContextAwareness()
	{
		// For editors that don't have/need a ContextAwareness service
		if (this._contextAwareness == null)
		{
			this._contextAwareness = new IContextAwareness()
			{
				public void update(IFileService fileService)
				{
				}

				public ContextItem getFileContext()
				{
					return new ContextItem();
				}
			};
		}
		return this._contextAwareness;
	}

	/**
	 * Returns the content outline page adapter
	 * 
	 * @return Returns the content outline page adapter.
	 */
	public UnifiedOutlinePage getOutlinePage()
	{
		if (this.outlinePage == null
				|| (this.outlinePage.getControl() != null && this.outlinePage.getControl().isDisposed()))
		{
			this.outlinePage = new UnifiedOutlinePage(this);
		}

		return outlinePage;
	}

	/**
	 * {@inheritDoc}
	 */
	public UnifiedQuickOutlinePage createQuickOutlinePage()
	{
		return new UnifiedQuickOutlinePage(this);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		try
		{
			ISourceViewer sourceViewer = getSourceViewer();

			if (sourceViewer == null)
			{
				return;
			}

			String property = event.getProperty();

			if (IPreferenceConstants.COLORIZER_MAXCOLUMNS.equals(property))
			{
				this._maxColorizingColumns = getPreferenceStore().getInt(IPreferenceConstants.COLORIZER_MAXCOLUMNS);
			}
			else if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(property)
					|| IPreferenceConstants.INSERT_SPACES_FOR_TABS.equals(property))
			{
				IPreferenceStore store = getPreferenceStore();

				if (store == null)
				{
					throw new Exception(Messages.UnifiedEditor_UnableToRetrievePreferenceStore);
				}

				int prefs = store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
				SourceViewerConfiguration sv = getSourceViewerConfiguration();

				boolean tabsOrSpaces = store.getBoolean(IPreferenceConstants.INSERT_SPACES_FOR_TABS);

				if (sv != null && sv instanceof UnifiedConfiguration)
				{
					UnifiedConfiguration uc = (UnifiedConfiguration) sv;
					uc.setTabWidth(prefs, tabsOrSpaces, sourceViewer);
				}
				else
				{
					IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_ErrorUpdateTabWidth);
				}
			}
			else if (IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED.equals(property))
			{
				boolean textHighlightEnabled = getPreferenceStore().getBoolean(
						IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED);

				if (textHighlightEnabled)
				{
					installTextOccurrenceHighlightSupport();
				}
				else
				{
					uninstallTextOccurrenceHighlightSupport();
					// remove highlights
				}

			}
			else if (property != null && property.startsWith(IPreferenceConstants.EDITOR_FOLDING_ENABLED))
			{
				String language = property.substring(IPreferenceConstants.EDITOR_FOLDING_ENABLED.length() + 1, property
						.length());
				if (this.getViewer() != null && this.getViewer() instanceof ProjectionViewer)
				{
					ProjectionViewer viewer = (ProjectionViewer) this.getViewer();
					if (UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(property))
					{
						if (this.getConfiguration() instanceof UnifiedConfiguration)
						{
							UnifiedReconcilingStrategy reconciler = ((UnifiedConfiguration) this.getConfiguration())
									.getStrategy();
							if (reconciler != null)
							{
								reconciler.initialReconcile();
							}
						}
					}
					else if (viewer.getProjectionAnnotationModel() != null)
					{
						List<Annotation> mods = new ArrayList<Annotation>();
						Iterator annotationIterator = viewer.getProjectionAnnotationModel().getAnnotationIterator();
						if (annotationIterator != null)
						{
							while (annotationIterator.hasNext())
							{
								Annotation annotation = (Annotation) annotationIterator.next();
								if (annotation instanceof LanguageProjectAnnotation)
								{
									LanguageProjectAnnotation lpa = (LanguageProjectAnnotation) annotation;
									if (language.equals(lpa.getLanguage()))
									{
										lpa.markDeleted(true);
										mods.add(lpa);
									}
								}
							}
							viewer.getProjectionAnnotationModel().modifyAnnotations(
									mods.toArray(new Annotation[mods.size()]), null, null);
						}
					}
				}
			}
			// This updates the folding icons when the colorization changes
			else if (property != null && property.startsWith("Colorization")) //$NON-NLS-1$
			{
				syncFoldingPreferenceStore();
				if (this.getConfiguration() instanceof UnifiedConfiguration)
				{
					UnifiedReconcilingStrategy reconciler = ((UnifiedConfiguration) this.getConfiguration())
							.getStrategy();
					ProjectionAnnotationModel currentModel = getProjectionAnnotationModel();
					if (reconciler != null && currentModel != null)
					{
						currentModel.removeAllAnnotations();
						reconciler.initialReconcile();
					}
				}

			}
			// boolean wordWrap =
			// getPreferenceStore().getBoolean(IPreferenceConstants.ENABLE_WORD_WRAP);
			// this.getViewer().getTextWidget().setWordWrap(wordWrap);

			// this can be null (? either when closing or when sourceViewer is
			// UndefinedViewer)
			StyledText viewer = sourceViewer.getTextWidget();
			if (viewer != null)
			{
				sourceViewer.getTextWidget().redraw();
			}
		}
		catch (Exception ex)
		{
			IdeLog
					.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_ErrorHandlingPreferenceChange,
							ex);
		}
		finally
		{
			super.handlePreferenceStoreChanged(event);

			// Editor options must be changed after super is called so that the
			// Eclipse editor prefs don't affect the
			// Aptana color prefs if the Eclipse prefs are overriden by the
			// language editor

			// Refresh editor options in case changed
			// Always run in UI thread!
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			display.asyncExec(new Runnable()
			{

				public void run()
				{
					setEditorOptions();
				}
			});
		}
	}

	/**
	 * Gets the projection annotation model ( folding annotations stored here)
	 * 
	 * @return - model or null if the viewer or model is null
	 */
	private ProjectionAnnotationModel getProjectionAnnotationModel()
	{
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		if (viewer != null)
		{
			return viewer.getProjectionAnnotationModel();
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handleCursorPositionChanged()
	 */
	protected void handleCursorPositionChanged()
	{
		// update current lexeme and index based on new cursor position
		StyledText styledText = getSourceViewer().getTextWidget();
		int offset = styledText.getCaretOffset();

		try
		{
			if (getSourceViewer() instanceof ITextViewerExtension5)
			{
				offset = ((ITextViewerExtension5) getSourceViewer()).widgetOffset2ModelOffset(offset);
			}
		}
		catch (Exception e)
		{
			// If can't convert offset then use caret offset uncoverted
		}
		catch (Error e)
		{
			// If can't convert offset then use caret offset uncoverted
		}

		IFileService fs = this.getFileContext();
		if (fs != null)
		{
			// RD: must always account for whitespace in lexemelist
			LexemeList list = fs.getLexemeList();
			if (list == null)
			{
				return;
			}

			int index = list.getLexemeFloorIndex(offset);
			if (index == -1)
			{
				index = list.getLexemeCeilingIndex(offset);
				if (index == -1)
				{
					return;
				}
			}

			Lexeme l = list.get(index);
			if (l != null)
			{
				IFileLanguageService ls = fs.getLanguageService(l.getLanguage());
				if (ls != null && ls.getOffsetMapper() != null)
				{
					ls.getOffsetMapper().calculateCurrentLexeme(offset);
				}
			}
		}

		super.handleCursorPositionChanged();
	}

	/**
	 * Retrieves the current IEditorPart
	 * 
	 * @return The current EditorPart
	 */
	public IEditorPart getEditor()
	{
		return this;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeKeyBindingScopes()
	 */
	protected void initializeKeyBindingScopes()
	{
		setKeyBindingScopes(new String[] { "com.aptana.ide.editors.UnifiedEditorsScope" }); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#configureSourceViewerDecorationSupport(org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
	 */
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support)
	{
		// support.setCharacterPairMatcher(bracketMatcher);
		// support.setMatchingCharacterPainterPreferenceKeys("matchBrackets",
		// "matchBracketsColor");
		super.configureSourceViewerDecorationSupport(support);
	}

	// public void addBracketMatchListener(IUnifiedBracketMatcherListener obj)
	// {
	// bracketMatcher.addBracketMatchListener(obj);
	// }
	//
	// public void removeBracketMatchListener(IUnifiedBracketMatcherListener
	// obj)
	// {
	// bracketMatcher.removeBracketMatchListener(obj);
	// }

	/**
	 * 
	 */
	protected void onSaveComplete()
	{
		ISaveEvent[] listeners = null;

		synchronized (this._saveListeners)
		{
			listeners = this._saveListeners.toArray(new ISaveEvent[this._saveListeners.size()]);
		}

		for (int i = 0; i < listeners.length; i++)
		{
			ISaveEvent element = listeners[i];
			element.onSave(this.getEditor());
		}
	}

	/**
	 * @see com.aptana.ide.editors.untitled.BaseTextEditor#onSaveAsComplete(java.io.File, java.io.File)
	 */
	protected void onSaveAsComplete(File oldFile, File newFile)
	{
		ISaveAsEvent[] listeners = null;

		synchronized (this._saveAsListeners)
		{
			listeners = this._saveAsListeners.toArray(new ISaveAsEvent[this._saveAsListeners.size()]);
		}

		for (int i = 0; i < listeners.length; i++)
		{
			ISaveAsEvent element = listeners[i];
			element.onSaveAs(this.getEditor(), oldFile, newFile);
		}

		updateFileExplorer();
	}

	private void updateFileExplorer()
	{
		if (this._fileExplorerView == null)
		{
			this._fileExplorerView = (CommonNavigator) CoreUIUtils.getViewInternal(
					"com.aptana.ide.ui.io.fileExplorerView", null); //$NON-NLS-1$
		}

		if (this._fileExplorerView == null)
		{
			return;
		}

		Display.getDefault().asyncExec(new Runnable()
		{

			public void run()
			{
				_fileExplorerView.getCommonViewer().refresh();
			}

		});
	}

	/**
	 * @param listener
	 */
	public void addSaveListener(ISaveEvent listener)
	{
		this._saveListeners.add(listener);
	}

	/**
	 * @param listener
	 */
	public void removeSaveListener(ISaveEvent listener)
	{
		if (this._saveListeners.contains(listener))
		{
			this._saveListeners.remove(listener);
		}
	}

	/**
	 * @param listener
	 */
	public void addSaveAsListener(ISaveAsEvent listener)
	{
		this._saveAsListeners.add(listener);
	}

	/**
	 * @param listener
	 */
	public void removeSaveAsListener(ISaveAsEvent listener)
	{
		if (this._saveAsListeners.contains(listener))
		{
			this._saveAsListeners.remove(listener);
		}
	}

	void showPairError()
	{
		MessageDialog.openInformation(null, Messages.UnifiedEditor_MatchingPairError,
				Messages.UnifiedEditor_MatchingPairErrorMessage);
	}

	/**
	 * isHasKeyBeenPressed
	 * 
	 * @return boolean
	 */
	public boolean isHasKeyBeenPressed()
	{
		return this._hasKeyBeenPressed;
	}

	/**
	 * setHasKeyBeenPressed
	 * 
	 * @param hasKeyBeenPressed
	 */
	public void setHasKeyBeenPressed(boolean hasKeyBeenPressed)
	{
		this._hasKeyBeenPressed = hasKeyBeenPressed;
	}

	/**
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	protected void createActions()
	{
		super.createActions();

		// Add bookmark action
		final BookmarkRulerAction bra = new BookmarkRulerAction();
		Action bookmarkAction = new Action()
		{

			public void run()
			{
				bra.run(this);
			}

		};
		bra.setActiveEditor(bookmarkAction, this);
		setAction(ADD_BOOKMARK, bookmarkAction);

		// Add task action
		final TaskRulerAction tra = new TaskRulerAction();
		Action taskAction = new Action()
		{
			public void run()
			{
				tra.run(this);
			}

		};
		tra.setActiveEditor(taskAction, this);
		setAction(ADD_TASK, taskAction);

		Action action = new ContentAssistAction(UnifiedMessages.getResourceBundle(), "ContentAssistProposal.", this); //$NON-NLS-1$
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$

		action = new GotoMatchingBracketAction(this);
		action.setActionDefinitionId(UnifiedActionContributor.GOTO_MATCHING_BRACKET_ID);
		setAction(GotoMatchingBracketAction.GOTO_MATCHING_BRACKET, action);

		Action actionContext = new TextOperationAction(UnifiedMessages.getResourceBundle(),
				"ContentAssistContextInformation.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION); //$NON-NLS-1$
		actionContext.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistContextInformation", actionContext); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistContextInformation", true); //$NON-NLS-1$
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(action,
		// IJavaHelpContextIds.PARAMETER_HINTS_ACTION);

		FoldingExtensionPointLoader.createFoldingActions(this);

		_formatAction = new CodeFormatAction();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditor#getBaseContributor()
	 */
	public IUnifiedEditorContributor getBaseContributor()
	{
		return this._baseContributor;
	}

	/**
	 * isNewInput
	 * 
	 * @param input
	 * @return boolean
	 */
	protected boolean isNewInput(IEditorInput input)
	{
		return true;
	}

	/**
	 * @param event
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
		{
			return;
		}

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
		{
			return;
		}

		int offset = -1;
		// If there is a selection, we consider selection range instead of caret
		// position.
		Point selectionRange = sourceViewer.getSelectedRange();
		if (selectionRange.x > -1 && selectionRange.y > 0)
		{
			offset = selectionRange.x;
		}

		if (offset < 0)
		{
			if (sourceViewer instanceof ITextViewerExtension5)
			{
				ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
				offset = extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
			}
			else
			{
				offset = sourceViewer.getVisibleRegion().getOffset();
				offset += styledText.getCaretOffset();
			}
		}

		if (offset < 0)
		{
			return;
		}

		LexemeList lexemeList = getLexemeList();

		if (lexemeList == null)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_LexemeListIsNull);
			return;
		}

		Lexeme selectedLexeme = lexemeList.getLexemeFromOffset(offset);

		if (selectedLexeme == null)
		{
			return;
		}

		if (!canMarkOccurrences(selectedLexeme))
		{
			return;
		}

		String selectedText = selectedLexeme.getText();

		if (selectedText == null || selectedText.length() == 0)
		{
			return;
		}

		markOccurences(lexemeList, selectedLexeme);

		Job job = new UIJob("Redraw")
		{
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// TODO: Measure time to see if a precisely calculated redraw area yields significant savings
				getSourceViewer().getTextWidget().redraw();
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Mark matching lexemes in the lexeme list
	 * 
	 * @param lexemeList
	 * @param selectedLexeme
	 */
	protected void markOccurences(LexemeList lexemeList, Lexeme selectedLexeme)
	{
		String selectedText = selectedLexeme.getText();
		IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
		Map<Annotation, Position> toAdd = new HashMap<Annotation, Position>();
		for (int i = 0; i < lexemeList.size(); i++)
		{
			Lexeme lexeme = lexemeList.get(i);
			if (lexeme != null)
			{
				if (lexeme.isHighlighted())
				{
					lexeme.setHighlighted(false);
				}

				if (lexeme.length == selectedLexeme.length && selectedText.equals(lexeme.getText()))
				{
					lexeme.setHighlighted(true);
					if (model != null)
					{
						Position pos = new Position(lexeme.offset, lexeme.length);
						Annotation occurence = new Annotation("com.aptana.ide.annotation.occurence", false, lexeme //$NON-NLS-1$
								.getText());
						toAdd.put(occurence, pos);
					}
				}
			}
		}
		
		synchronized (getLockObject(model)) {
			if (model instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)model).replaceAnnotations(fOccurrenceAnnotations, toAdd);
			} else {
				removeOccurrenceAnnotations();
				Iterator iter= toAdd.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry mapEntry= (Map.Entry)iter.next();
					model.addAnnotation((Annotation)mapEntry.getKey(), (Position)mapEntry.getValue());
				}
			}
			fOccurrenceAnnotations= (Annotation[])toAdd.keySet().toArray(new Annotation[toAdd.keySet().size()]);
		}
	}

	/**
	 * Gets the lexeme list for this editor
	 * 
	 * @return - lexeme list
	 */
	protected LexemeList getLexemeList()
	{
		EditorFileContext fileContext = getFileContext();

		// wrapper could be non-null, but interior is.
		if (fileContext == null || fileContext.getFileContext() == null)
		{
			return null;
		}

		IParseState parseState = fileContext.getParseState();

		if (parseState == null)
		{
			return null;
		}

		LexemeList lexemeList = parseState.getLexemeList();

		return lexemeList;
	}

	/**
	 * Returns language MIME type for offset position
	 * 
	 * @param offset
	 * @return - language
	 */
	public String getLanguageAtOffset(int offset)
	{
		LexemeList lexemeList = getLexemeList();
		if (lexemeList == null)
		{
			return null;
		}

		Lexeme lexemeAtOffset = lexemeList.getCeilingLexeme(offset);
		if (lexemeAtOffset == null)
		{
			return null;
		}
		return lexemeAtOffset.getLanguage();
	}

	private void removeOccurrenceAnnotations()
	{
		
		IDocumentProvider documentProvider= getDocumentProvider();
		if (documentProvider == null)
			return;

		IAnnotationModel annotationModel= documentProvider.getAnnotationModel(getEditorInput());
		if (annotationModel == null || fOccurrenceAnnotations == null)
			return;

		synchronized (getLockObject(annotationModel)) {
			if (annotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)annotationModel).replaceAnnotations(fOccurrenceAnnotations, null);
			} else {
				for (int i= 0, length= fOccurrenceAnnotations.length; i < length; i++)
					annotationModel.removeAnnotation(fOccurrenceAnnotations[i]);
			}
			fOccurrenceAnnotations= null;
		}
	}
	
	/**
	 * Returns the lock object for the given annotation model.
	 *
	 * @param annotationModel the annotation model
	 * @return the annotation model's lock object
	 * @since 3.0
	 */
	private Object getLockObject(IAnnotationModel annotationModel) {
		if (annotationModel instanceof ISynchronizable) {
			Object lock= ((ISynchronizable)annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}

	/**
	 * 
	 * 
	 */
	public void removeMarkedOccurrences()
	{
		removeOccurrenceAnnotations();
		LexemeList lexemeList = getLexemeList();
		if (lexemeList == null)
		{
			return;
		}

		for (int i = 0; i < lexemeList.size(); i++)
		{
			Lexeme lexeme = lexemeList.get(i);
			if (lexeme != null && lexeme.isHighlighted())
			{
				lexeme.setHighlighted(false);
			}
		}
	}

	/**
	 * @param lexeme
	 * @return - true if occurrence should be marked
	 */
	public boolean canMarkOccurrences(Lexeme lexeme)
	{
		if (lexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
		{
			return false;
		}

		if (lexeme.getCategoryIndex() == TokenCategories.PUNCTUATOR)
		{
			return false;
		}

		return true;
	}

	/**
	 * Creates the docment provider
	 * 
	 * @return IDocumentProvider
	 */
	public abstract IDocumentProvider createDocumentProvider();

	/**
	 * Is code assist available for auto-activation
	 * 
	 * @return - true if activating, false otherwise
	 */
	public boolean autoActivateCodeAssist()
	{
		return autoActivateCodeAssist;
	}

	public void gotoMatchingBracket()
	{
		ISourceViewer sourceViewer = getSourceViewer();
		IDocument document = sourceViewer.getDocument();
		if (document == null)
			return;

		Point selectedPoint = sourceViewer.getSelectedRange();
		IRegion selection = new Region(selectedPoint.x, selectedPoint.y);

		int selectionLength = Math.abs(selection.getLength());
		if (selectionLength > 1)
		{
			setStatusLineErrorMessage(com.aptana.ide.editors.unified.actions.Messages.GotoMatchingBracket_error_invalidSelection);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int sourceCaretOffset = selection.getOffset() + selection.getLength();
		PairMatch pair = getPairMatch(sourceCaretOffset);

		if (pair == null)
		{
			setStatusLineErrorMessage(com.aptana.ide.editors.unified.actions.Messages.GotoMatchingBracket_error_noMatchingBracket);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int start = 0;
		int end = 0;
		if (sourceCaretOffset <= pair.beginEnd && sourceCaretOffset >= pair.beginStart)
		{
			start = pair.endStart;
			end = pair.endEnd;
		}
		else
		{
			start = pair.beginStart;
			end = pair.beginEnd;
		}

		int length = end - start;
		sourceViewer.setSelectedRange(start, length);
		sourceViewer.revealRange(start, length);
	}
}
