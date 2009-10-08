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
package com.aptana.ide.logging;

import java.io.File;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.aptana.ide.core.BaseFileEditorInput;
import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.Messages;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.logging.coloring.LoggingColorizer;
import com.aptana.ide.logging.coloring.LoggingLexemeManager;
import com.aptana.ide.logging.coloring.TokenTypes;
import com.aptana.ide.logging.preferences.ILoggingPreferenceListener;
import com.aptana.ide.logging.preferences.LoggingStructureProvider;

/**
 * Log read-only editor.
 * 
 * @author Denis Denisenko
 */
public class LogReadonlyEditor extends TextEditor
{

	class ReadOnlySourceViewer extends SourceViewer
	{

		/**
		 * ReadOnlySourceViewer constructor.
		 * 
		 * @param parent -
		 *            parent.
		 * @param ruler -
		 *            vertical ruler.
		 * @param styles -
		 *            styles.
		 */
		public ReadOnlySourceViewer(Composite parent, IVerticalRuler ruler, int styles)
		{
			super(parent, ruler, styles);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void createControl(Composite parent, int styles)
		{
			super.createControl(parent, styles | SWT.READ_ONLY);
			this.getTextWidget().setEditable(false);
		}
	}

	/**
	 * Line style listener.
	 */
	private LineStyleListener _lineStyleListener;

	/**
	 * Logging colorizer.
	 */
	private LoggingColorizer _colorizer;

	/**
	 * Logging lexeme manager.
	 */
	protected LoggingLexemeManager lexememanager;

	/**
	 * Max colorization columns.
	 */
	protected int _maxColorizingColumns = 512;

	/**
	 * Composite.
	 */
	private Composite composite;

	/**
	 * Helper for handling decoration.
	 */
	protected SourceViewerDecorationSupport decorationSupport;

	/**
	 * Text foreground color.
	 */
	private Color textForeground;

	/**
	 * Viewer.
	 */
	private SourceViewer viewer;

	private ToolBar refreshBar;
	private ToolItem refreshItem;
	private IVirtualFile refreshableFile;

	private IPropertyChangeListener colorizationPreferencesListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEditable()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		if (input instanceof BaseFileEditorInput)
		{
			BaseFileEditorInput bfei = (BaseFileEditorInput) input;
			this.refreshableFile = bfei.getVirtualFile();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		return createViewer(parent, ruler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		createColorizer();

		initializeColors();

		linkColorer();
	}

	/**
	 * @param item
	 */
	private ISourceViewer createViewer(Composite parent, IVerticalRuler ruler)
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

		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayout(layout);
		composite.setLayoutData(data);
		composite.setBackground(UnifiedColorManager.getInstance().getColor(new RGB(220, 220, 220)));

		if (refreshableFile != null)
		{
			refreshBar = new ToolBar(composite, SWT.FLAT);
			refreshBar.setBackground(UnifiedColorManager.getInstance().getColor(new RGB(220, 220, 220)));
			refreshBar.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
			refreshItem = new ToolItem(refreshBar, SWT.PUSH);
			refreshItem.setImage(LoggingPlugin.getImage("icons/active/refresh_active.gif")); //$NON-NLS-1$
			refreshItem.setToolTipText(Messages.LogReadonlyEditor_TTP_Refresh);
			refreshItem.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					refreshItem.setEnabled(false);
					Job job = new Job(Messages.LogReadonlyEditor_Job_Refresh)
					{

						protected IStatus run(IProgressMonitor monitor)
						{
							String newFileName = FileUtils.getRandomFileName("temp", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
							final File temp = new File(FileUtils.systemTempDir + File.separator + newFileName);
							temp.deleteOnExit();
							temp.getParentFile().mkdirs();

							try
							{
								refreshableFile.getFileManager().putToLocalFile(refreshableFile, temp);

							}
							catch (Exception e2)
							{
								CoreUIUtils.showError(StringUtils.format(
										Messages.FileExplorerView_UnableToSaveRemoteFile, new String[] {
												refreshableFile.getAbsolutePath(), temp.getAbsolutePath() }), e2);
							}
							finally
							{
								UIJob update = new UIJob(Messages.LogReadonlyEditor_Job_Updating)
								{

									public IStatus runInUIThread(IProgressMonitor monitor)
									{
										try
										{
											if (viewer != null && viewer.getTextWidget() != null
													&& !viewer.getTextWidget().isDisposed())
											{
												refreshItem.setEnabled(true);
												viewer.getTextWidget().setText(FileUtils.readContent(temp));
											}

										}
										catch (Exception e)
										{
											IdeLog.logError(LoggingPlugin.getDefault(),
													Messages.LogReadonlyEditor_ERR_Update);
										}
										catch (Error e)
										{
											IdeLog.logError(LoggingPlugin.getDefault(),
													Messages.LogReadonlyEditor_ERR_Update);
										}
										return Status.OK_STATUS;
									}

								};
								update.schedule();
							}
							return Status.OK_STATUS;
						}

					};
					job.schedule();
				}

			});
		}

		viewer = new ReadOnlySourceViewer(composite, ruler, viewerStyle);
		viewer.addTextInputListener(new ITextInputListener()
		{

			public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput)
			{
			}

			public void inputDocumentChanged(IDocument oldInput, IDocument newInput)
			{
				lexememanager = new LoggingLexemeManager(newInput, LoggingPlugin.getDefault().getLoggingPreferences());
			}
		});

		Control control = viewer.getControl();
		control.setLayoutData(data);

		viewer.getTextWidget().setFont(LoggingPlugin.getDefault().getLoggingPreferences().getFont());

		viewer.activatePlugins();

		createSourceViewerDecorationSupport(viewer);

		bindToColorizationSave(/* viewer */);

		viewer.getTextWidget().addLineStyleListener(_lineStyleListener);

		return viewer;
	}

	/**
	 * Binds to colorization save.
	 * 
	 * @param viewer -
	 *            viewer.
	 */
	private void bindToColorizationSave(/* final SourceViewer viewer */)
	{
		colorizationPreferencesListener = new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				if (LoggingStructureProvider.COLORIZATION_SAVED.equals(event.getProperty()))
				{
					createColorizer();

					lexememanager.clearCache();
					if (viewer != null)
					{
						viewer.setRedraw(false);
						ISourceViewer sv = getSourceViewer();
						if(sv != null && sv.getTextWidget() != null)
						{
							StyledText st = sv.getTextWidget();
							st.removeLineStyleListener(_lineStyleListener);
							st.addLineStyleListener(_lineStyleListener);
						}
						viewer.setRedraw(true);
					}
				}
			}

		};

		UnifiedEditorsPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
				colorizationPreferencesListener);

		LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
		ILoggingPreferenceListener loggingPreferenceListener = new ILoggingPreferenceListener()
		{

			public void rulesChanged()
			{
			}

			public void wrappingChanged(boolean wrapping)
			{
				// TODO handle this
				// recreateViewer();
			}

			public void fontChanged(Font font)
			{
				if (viewer != null && viewer.getTextWidget() != null && !viewer.getTextWidget().isDisposed())
				{
					LogReadonlyEditor.this.viewer.getTextWidget().setFont(font);
				}
			}

			public void textForegroundColorChanged(Color color)
			{
				textForeground = color;
			}
		};
		preferences.addPreferenceListener(loggingPreferenceListener);
	}

	/**
	 * Creates colorizer.
	 */
	private void createColorizer()
	{
		_colorizer = new LoggingColorizer(getLexer().getTokenList(TokenTypes.LANGUAGE));
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
					if (viewer == null || viewer.getDocument() == null || lexememanager == null)
					{
						return;
					}

					int orgOffset = e.lineOffset;
					int offset = orgOffset;
					int extra = 0;
					int lineLength = e.lineText.length();

					// need to get actual offset values in the doc,
					// as widget offsets do not include potentially folded code
					if (getSourceViewer() instanceof ITextViewerExtension5)
					{
						ITextViewerExtension5 v5 = (ITextViewerExtension5) getSourceViewer();
						offset = v5.widgetOffset2ModelOffset(e.lineOffset);
						extra = offset - e.lineOffset;
					}

					int maxLineLength = lineLength > _maxColorizingColumns ? _maxColorizingColumns : lineLength;
					Lexeme[] lexemes = null;

					int lineNumber;
					try
					{
						lineNumber = viewer.getDocument().getLineOfOffset(e.lineOffset);
						lexemes = lexememanager.getLexemes(lineNumber);
					}
					catch (BadLocationException e1)
					{
						IdeLog.logError(LoggingPlugin.getDefault(), Messages.LogReadonlyEditor_ERR_Exception, e1);
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
						styles[0] = new StyleRange(e.lineOffset, e.lineText.length(), textForeground, null);
						e.styles = styles;
					}
				}
			};
		}
	}

	/**
	 * Initialized tab fonts.
	 */
	private void initializeColors()
	{
		textForeground = LoggingPlugin.getDefault().getLoggingPreferences().getTextColor();
	}

	/**
	 * Gets decoration support.
	 * 
	 * @param viewer -
	 *            viewer.
	 */
	protected void createSourceViewerDecorationSupport(ISourceViewer viewer)
	{
		if (decorationSupport == null)
		{
			ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();

			decorationSupport = new SourceViewerDecorationSupport(viewer, null, null, sharedColors);
			configureSourceViewerDecorationSupport(decorationSupport);
			// decorationSupport.showCursorLine();

			decorationSupport.install(LoggingPlugin.getDefault().getPreferenceStore());
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
	 */
	public boolean isDirty()
	{
		return false;
	}

	/**
	 * Configures decoration support.
	 * 
	 * @param support -
	 *            support to configure.
	 */
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support)
	{
		support.setCursorLinePainterPreferenceKeys(LoggingPreferences.CURSORLINE_KEY,
				LoggingPreferences.CURSORLINE_COLOR_KEY);
		support.setSymbolicFontName(LoggingPreferences.MAIN_TEXT_FONT_KEY);
	}

	/**
	 * Gets lexer.
	 * 
	 * @return - lexer
	 */
	private ILexer getLexer()
	{
		return TokenTypes.getLexerFactory().getLexer();
	}

	/**
	 * Dispose the editor
	 */
	public void dispose()
	{
		super.dispose();
		
		UnifiedEditorsPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(
				colorizationPreferencesListener);
	}
}
