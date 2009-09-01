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
package com.aptana.ide.logging.preferences;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.ColorCellEditor;
import com.aptana.ide.editors.preferences.ColorizationScrolledComposite;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.colorizer.ColorizationConstants;
import com.aptana.ide.editors.unified.colorizer.ColorizationStyle;
import com.aptana.ide.editors.unified.colorizer.IErrorHandler;
import com.aptana.ide.editors.unified.colorizer.Region;
import com.aptana.ide.editors.unified.colorizer.LanguageStructureProvider.Category;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Denis Denisenko
 */
public class LoggingPreferenceWidget
{

	private ColorizationScrolledComposite scrolls;
	//private ExpandableComposite regionExpandable; @regions
	private ExpandableComposite tokenExpandable;
	private ExpandableComposite editorExpandable;
	private Label description;
	private Composite displayArea;
	private TreeViewer viewer;
	private Tree tree;
	//private Table table; @regions
	//private TableViewer regionViewer; @regions
	private RGB black;
	private LoggingStructureProvider provider;
	private Button importButton;
	private Button exportButton;
	//private Button remove; @regions
	//private Button addRegion; @regions
	private Button override;
	private ColorSelector backgroundSelector;
	private ColorSelector lineHighlightSelector;
	private ColorSelector selectionForegroundSelector;
	private ColorSelector selectionBackgroundSelector;
	private ColorSelector foldingBackgroundSelector;
	private ColorSelector foldingForegroundSelector;
	private ColorSelector caretSelector;
	private Composite coloringComposite;
	private IErrorHandler errorHandler;
	private static final Object INPUT = new Object();

	private static final String[] COLUMNS = { "!", "Token", "R", "FG", "B", "I", "U" };   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private static final String[] REGION_COLUMNS = { "Name", "Offset", "Length", "FG", "B", "I", "U" };   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private static final String[] REGION_TABLE_COLUMNS = { "Name", "Offset", "Length", "", "", "", "" };   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private static final int ICON_COLUMN_WIDTH = 17;
	
	/**
	 * Min read timeout.
	 */
    protected static final int MIN_READ_TIMEOUT = 50;

	private IStructuredContentProvider regionContentProvider = new IStructuredContentProvider()
	{

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{

		}

		public void dispose()
		{

		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Map)
			{
				Map map = (Map) inputElement;
				return map.values().toArray();
			}
			return null;
		}

	};

	private ITableLabelProvider nullLabelProvider = new ITableLabelProvider()
	{

		public void removeListener(ILabelProviderListener listener)
		{

		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void dispose()
		{

		}

		public void addListener(ILabelProviderListener listener)
		{

		}

		public String getColumnText(Object element, int columnIndex)
		{
			return "";  //$NON-NLS-1$
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

	};

	private ITreeContentProvider nullContentProvider = new ITreeContentProvider()
	{

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{

		}

		public void dispose()
		{

		}

		public Object[] getElements(Object inputElement)
		{
			return new Object[0];
		}

		public boolean hasChildren(Object element)
		{
			return false;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public Object[] getChildren(Object parentElement)
		{
			return new Object[0];
		}

	};

	private ITableLabelProvider regionLabelProvider = new ITableLabelProvider()
	{

		private Map imageMap = new HashMap();

		public void removeListener(ILabelProviderListener listener)
		{

		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void dispose()
		{
			Iterator iter = imageMap.values().iterator();
			while (iter.hasNext())
			{
				((Image) iter.next()).dispose();
			}
		}

		public void addListener(ILabelProviderListener listener)
		{

		}

		public String getColumnText(Object element, int columnIndex)
		{
			Region region = (Region) element;
			if (columnIndex == 0)
			{
				return region.getName();
			}
			if (columnIndex == 1)
			{
				return region.getOffsetString();
			}
			if (columnIndex == 2)
			{
				return region.getLengthString();
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			Region region = (Region) element;
			ColorizationStyle style = region.getStyle();
			if (columnIndex == 3)
			{
				if (style != null)
				{
					Color fg = style.getForegroundColor();
					Image img = null;
					if (!imageMap.containsKey(fg.getRGB()))
					{
						img = new Image(Display.getCurrent(), 16, 16);
						GC gc = new GC(img);
						gc.setBackground(fg);
						gc.fillRectangle(1, 1, 13, 13);
						gc.setForeground(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 0)));
						gc.drawRectangle(1, 1, 13, 13);
						gc.dispose();
						imageMap.put(fg.getRGB(), img);
					}
					else
					{
						img = (Image) imageMap.get(fg.getRGB());
					}
					return img;
				}
			}
			if (columnIndex == 4)
			{
				if (style != null && style.isBold())
				{
					return UnifiedEditorsPlugin.getImage("icons/bold_on.gif");  //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/bold_off.gif");  //$NON-NLS-1$
				}
			}
			if (columnIndex == 5)
			{
				if (style != null && style.isItalic())
				{
					return UnifiedEditorsPlugin.getImage("icons/italic_on.gif");  //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/italic_off.gif");  //$NON-NLS-1$
				}
			}
			if (columnIndex == 6)
			{
				if (style != null && style.isUnderline())
				{
					return UnifiedEditorsPlugin.getImage("icons/underline_on.gif");  //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/underline_off.gif");  //$NON-NLS-1$
				}
			}
			return null;
		}

	};

	/**
	 * Disposes this colorization widget
	 */
	public void dispose()
	{
		if (provider != null)
		{
			provider.disposeImages();
		}
		regionLabelProvider.dispose();
	}

	private ICellModifier regionModifier = new ICellModifier()
	{

		public void modify(Object element, String property, Object value)
		{
			if (element instanceof Item)
			{
				element = ((Item) element).getData();
			}
			ColorizationStyle style = null;
			if (element instanceof Region)
			{
				style = ((Region) element).getStyle();
			}
			if (style != null)
			{
				if (value instanceof Boolean)
				{
					Boolean bool = (Boolean) value;
					if (property == REGION_COLUMNS[4])
					{
						style.setBold(bool.booleanValue());
					}
					else if (property == REGION_COLUMNS[5])
					{
						style.setItalic(bool.booleanValue());
					}
					else if (property == REGION_COLUMNS[6])
					{
						style.setUnderline(bool.booleanValue());
					}
				}
				else if (value instanceof RGB)
				{
					RGB rgb = (RGB) value;
					if (property == REGION_COLUMNS[3])
					{
						style.setForegroundColor(UnifiedColorManager.getInstance().getColor(rgb));
					}
				}
				else if (value instanceof String)
				{
					String text = (String) value;
					Region region = (Region) element;
					if (property == REGION_COLUMNS[0])
					{
						IToken token = (IToken) viewer.getTree().getSelection()[0].getData();
						provider.removeRegion(token, region.getName());
						region.setName(text);
						provider.addRegion(token, region);
					}
					else if (property == REGION_COLUMNS[1])
					{
						int offsetInt;
						boolean offsetRelative = false;
						try
						{
							if (text.startsWith(ColorizationConstants.LENGTH_KEYWORD))
							{
								offsetInt = Integer.parseInt(text.substring(ColorizationConstants.LENGTH_KEYWORD.length(),
										text.length()));
								offsetRelative = true;
							}
							else
							{
								offsetInt = Integer.parseInt(text);
							}
							region.setOffset(offsetInt);
							region.setRelativeOffset(offsetRelative);
							region.setOffsetString(text);
						}
						catch (Exception e1)
						{
						}
					}
					else if (property == REGION_COLUMNS[2])
					{
						int lengthInt;
						boolean lengthRelative = false;
						try
						{
							if (text.startsWith(ColorizationConstants.LENGTH_KEYWORD))
							{
								lengthInt = Integer.parseInt(text.substring(ColorizationConstants.LENGTH_KEYWORD.length(),
										text.length()));
								lengthRelative = true;
							}
							else
							{
								lengthInt = Integer.parseInt(text);
							}
							region.setLength(lengthInt);
							region.setRelativeLength(lengthRelative);
							region.setLengthString(text);
						}
						catch (Exception e1)
						{
						}
					}
				}
			}
			//regionViewer.update(element, null); @regions
		}

		public Object getValue(Object element, String property)
		{
			ColorizationStyle style = null;
			if (element instanceof Region)
			{
				style = ((Region) element).getStyle();
			}
			if (style != null)
			{
				if (property == REGION_COLUMNS[0])
				{
					return ((Region) element).getName();
				}
				else if (property == REGION_COLUMNS[1])
				{
					return ((Region) element).getOffsetString();
				}
				else if (property == REGION_COLUMNS[2])
				{
					return ((Region) element).getLengthString();
				}
				else if (property == REGION_COLUMNS[3])
				{
					return style.getForegroundColor().getRGB();
				}
				else if (property == REGION_COLUMNS[4])
				{
					return Boolean.valueOf(style.isBold());
				}
				else if (property == REGION_COLUMNS[5])
				{
					return Boolean.valueOf(style.isItalic());
				}
				else if (property == REGION_COLUMNS[6])
				{
					return Boolean.valueOf(style.isUnderline());
				}
			}
			return null;
		}

		public boolean canModify(Object element, String property)
		{
			return true;
		}

	};
    
    /**
     * Add string-based rule button.
     */
    private Button addStringRuleButton;
    
    /**
     * Remove rule button.
     */
    private Button removeRuleButton;
    
    /**
     * Edit rule button.
     */
    private Button editRuleButton;
    
    /**
     * Add regexp rule button.
     */
    private Button addRegexpRuleButton;
    
    /**
     * Autobolding composite.
     */
    private Composite autoboldingComposite;
    
    /**
     * AUtobolding checkbox.
     */
    private Button autoboldingBox;

    /**
     * Up Rule button.
     */
    private Button upRuleButton;
    
    /**
     * Down Rule button.
     */
    private Button downRuleButton;
    
    /**
     * Wrapping checkbox.
     */
    private Button wrappingBox;
    
    /**
     * Charset combo.
     */
    private Combo charsetCombo;
    
    /**
     * Buffer text.
     */
    private Text bufferText;
    
    /**
     * timeout text.
     */
    private Text timeoutText;
    
    /**
     * Backlog rows text.
     */
    private Text backlogText;
    
    /**
     * Preference tab folder.
     */
    private TabFolder folder;
    
    /**
     * Color selector in appearance group.
     */
    private ColorSelector cursorLineColorSelector;
    
    /**
     * Font style label.
     */
    private Label fontStyleLabel;
    
    /**
     * Text foreground color selector.
     */
    private ColorSelector textForegroundColorSelector;

	/**
	 * Creates this widget witha given parent
	 * 
	 * @param parent - parent.
	 * @param mainCompositeData - layout data.
	 */
	public void createControl(Composite parent, GridData mainCompositeData)
	{
		black = new RGB(0, 0, 0);
		Label l=new Label(parent,SWT.NONE);
		l.setText(Messages.LoggingPreferenceWidget_CUSTOMIZE_LABEL);
		folder = new TabFolder(parent, SWT.NONE);
		folder.setData(mainCompositeData);
		createGeneralTab(folder);
		createColoringTab(folder);
	}
	
	/**
	 * Activates tab.
	 * @param tab
	 */
	public void activateTab(int tab)
	{
	    folder.setSelection(tab);
	}
	
	private void createGeneralTab(TabFolder folder)
	{
	    TabItem generalTab = new TabItem(folder, SWT.NONE);
	    generalTab.setText(Messages.General_Tab_Name);
	    generalTab.setToolTipText(Messages.General_Tab_ToolTip);
	    
	    Composite generalComposite = new Composite(folder, SWT.NONE);
	    generalComposite.setFont(folder.getFont());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 8;
        layout.marginWidth = 10;
        generalComposite.setLayout(layout);
        
        generalTab.setControl(generalComposite);
	    
        createAppearance(generalComposite);
        createTail(generalComposite);
        createAutobolding(generalComposite);
        generalComposite.setFocus();
        createWrapping(generalComposite);
	}
	
	private void createColoringTab(TabFolder folder)
	{
	    TabItem coloringTab = new TabItem(folder, SWT.NONE);
	    coloringTab.setText(Messages.Coloring_Tab_Name);
	    coloringTab.setToolTipText(Messages.Coloring_Tab_ToolTip);
        
	    Color systemWidgetBGColor = getControl().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	    
	    coloringComposite = new Composite(folder, SWT.NONE);
        coloringComposite.setFont(folder.getFont());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 8;
        layout.marginWidth = 10;
        coloringComposite.setLayout(layout);
        coloringComposite.setBackground(systemWidgetBGColor);
        coloringTab.setControl(coloringComposite);
        
        createIntroduction(coloringComposite);
	    
	    scrolls = new ColorizationScrolledComposite(coloringComposite);
        layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 5;
        scrolls.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        scrolls.setLayoutData(gridData);

        displayArea = scrolls.getBody();
        layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 0;
        displayArea.setLayout(layout);
        displayArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//      description = new Label(displayArea, SWT.WRAP);
//      description.setText(Messages.getString("LoggingColorizationWidget.28")); 

        styleImportExport();
        //styleEditorOptions();

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;

        tokenExpandable = new ExpandableComposite(displayArea, SWT.NONE, ExpandableComposite.TWISTIE
                | ExpandableComposite.CLIENT_INDENT);
        tokenExpandable.setText(Messages.LoggingColorizationWidget_29); 
        tokenExpandable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        tokenExpandable.setLayout(gridLayout);
        tokenExpandable.setExpanded(UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(
                IPreferenceConstants.EXPAND_TOKENS));
        tokenExpandable.addExpansionListener(new ExpansionAdapter()
        {
            public void expansionStateChanged(ExpansionEvent e)
            {
                expandedStateChanged((ExpandableComposite) e.getSource());
            }
        });
        makeScrollableCompositeAware(tokenExpandable);
        Composite inner = new Composite(tokenExpandable, SWT.NONE);
        gridLayout = new GridLayout(2, false);
        inner.setLayout(gridLayout);
        tokenExpandable.setClient(inner);


        createAddRuleButtons(inner);
        
        tree = new Tree(inner, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        tree.setHeaderVisible(false);
        tree.setLinesVisible(false);

        GridData treeLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
        treeLayoutData.heightHint = tree.getItemHeight() * 16;
        treeLayoutData.widthHint = 300;
        tree.setLayoutData(treeLayoutData);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        tree.setLayout(gridLayout);
        viewer = new TreeViewer(tree);
        // viewer.setContentProvider(provider);
        // viewer.setLabelProvider(provider);
        viewer.setAutoExpandLevel(2);
        styleCells();

//        tree.addSelectionListener(new SelectionAdapter() @regions
//        {
//
//            private Map empty = new HashMap();
//
//            public void widgetSelected(SelectionEvent e)
//            {
//                TreeItem[] selection = viewer.getTree().getSelection();
//                if (selection == null || selection.length == 0)
//                {
//                    return;
//                }
//                Object data = selection[0].getData();
//                remove.setEnabled(false);
//                if (data instanceof IToken)
//                {
//                    Map regionMap = provider.getRegions((IToken) data);
//                    regionViewer.setInput(regionMap);
//                    addRegion.setEnabled(provider.getStyle((IToken) data) != null);
//                }
//                else
//                {
//                    addRegion.setEnabled(false);
//                    regionViewer.setInput(empty);
//                }
//            }
//
//        });
        
        createRuleButtons(inner);
        
        //loadRegions(); @regions
	}

    /**
     * Creates introduction label.
     * @param parent - parent.
     */
    private void createIntroduction(Composite parent)
    {
        description = new Label(parent, SWT.LEFT | SWT.WRAP);
        GridData dData = new GridData(SWT.FILL, SWT.FILL, false, false);
        dData.widthHint = 400;
        description.setLayoutData(dData);
        description.setText(Messages.LoggingPreferenceWidget_0); 
    }

    /**
     * Creates auto-bolding.
     * @param parent - parent.
     */
    private void createAutobolding(Composite parent)
    {
        autoboldingComposite = new Composite(parent, SWT.NONE);
        autoboldingComposite.setLayout(new GridLayout(2, false));
        autoboldingComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        
        autoboldingBox = new Button(autoboldingComposite, SWT.CHECK);
        autoboldingBox.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                if (provider != null)
                {
                    provider.setAutoBolding(LoggingPreferenceWidget.this.autoboldingBox.getSelection());
                }
            }
        });
        
        Label label = new Label(autoboldingComposite, SWT.NONE);
        label.setText(Messages.LoggingPreferenceWidget_Autobolding_Label); 
    }
    
    /**
     * Creates appearance settings.
     * @param parent - parent.
     */
    private void createAppearance(Composite parent)
    {
        final Group appearanceGroup = new Group(parent, SWT.NONE);
        appearanceGroup.setLayout(new GridLayout(3, false));
        appearanceGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        appearanceGroup.setText(Messages.LoggingPreferenceWidget_1); 
        
        Label label = new Label(appearanceGroup, SWT.NONE);
        label.setText(Messages.LoggingPreferenceWidget_CursorLineColor_Label); 
        
        cursorLineColorSelector = new ColorSelector(appearanceGroup);
        cursorLineColorSelector.getButton().setLayoutData(new GridData(SWT.LEFT, GridData.CENTER, false, false, 2, 1));
        cursorLineColorSelector.setEnabled(true);
        cursorLineColorSelector.addListener(new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {

                RGB newRGB = (RGB) event.getNewValue();
                if (!newRGB.equals(event.getOldValue())) {
                    provider.setCursorLineColor(newRGB);
                }

                cursorLineColorSelector.setColorValue(newRGB);
            }
        });
        
        label = new Label(appearanceGroup, SWT.NONE);
        label.setText(Messages.LoggingPreferenceWidget_TextForegroundColor_Label);
        textForegroundColorSelector = new ColorSelector(appearanceGroup);
        textForegroundColorSelector.getButton().setLayoutData(new GridData(SWT.LEFT, GridData.CENTER, false, false, 2, 1));
        textForegroundColorSelector.setEnabled(true);
        textForegroundColorSelector.addListener(new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {

                RGB newRGB = (RGB) event.getNewValue();
                if (!newRGB.equals(event.getOldValue())) {
                    provider.setTextForegroundColor(newRGB);
                }

                textForegroundColorSelector.setColorValue(newRGB);
            }
        });
        
        label = new Label(appearanceGroup, SWT.LEFT | SWT.CENTER);
        label.setText(Messages.LoggingPreferenceWidget_Font_Label); 
        label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        
        fontStyleLabel = new Label(appearanceGroup, SWT.LEFT | SWT.CENTER);
        fontStyleLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
     
        final Button fontButton = new Button(appearanceGroup, SWT.NONE);
        fontButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
            
            public void widgetSelected(SelectionEvent e)
            {
                if (provider != null)
                {
                    FontDialog dialog = new FontDialog(fontButton.getShell());
                    dialog.setFontList(getPreferences().getFontData());
                    if(dialog.open() != null)
                    {
                        FontData[] font = dialog.getFontList();
                        provider.setFont(font);
                        if (font != null && font.length > 0)
                        {
                            String fontStr = font[0].getName() + "-" + font[0].getHeight();  //$NON-NLS-1$
                            fontStyleLabel.setText(fontStr);
                            appearanceGroup.layout(true, true);
                        }
                    }
                }
            }
        });
        fontButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        fontButton.setText(Messages.LoggingPreferenceWidget_4); 
        
        final Label charsetLabel = new Label(appearanceGroup, SWT.LEFT | SWT.CENTER);
        charsetLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        charsetLabel.setText(Messages.LoggingPreferenceWidget_5); 
        
        charsetCombo = new Combo(appearanceGroup, SWT.LEFT | SWT.CENTER | SWT.READ_ONLY);
        charsetCombo.setLayoutData(new GridData(SWT.LEFT, GridData.CENTER, false, false, 2, 1));
        
        Collection<String> charsets = Charset.availableCharsets().keySet();
        String[] items = new String[charsets.size()];
        charsets.toArray(items);
        charsetCombo.setItems(items);
        
        charsetCombo.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
            
            public void widgetSelected(SelectionEvent e)
            {
                if (provider != null)
                {
                    String selected = charsetCombo.getItem(charsetCombo.getSelectionIndex());
                    provider.setDefaultEncoding(selected);
                }
            }
        });
    }
    
    /**
     * Creates appearance settings.
     * @param parent - parent.
     */
    private void createTail(Composite parent)
    {
        Group tailGroup = new Group(parent, SWT.NONE);
        tailGroup.setLayout(new GridLayout(2, false));
        tailGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        tailGroup.setText(Messages.LoggingPreferenceWidget_6); 
        
        Label label = new Label(tailGroup, SWT.LEFT);
        label.setText(Messages.LoggingPreferenceWidget_7); 
        label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        
        timeoutText = new Text(tailGroup, SWT.LEFT | SWT.BORDER);
        timeoutText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String contentText = timeoutText.getText();
                try
                {
                    int val = Integer.parseInt(contentText);
                    if (val < MIN_READ_TIMEOUT)
                    {
                        if (errorHandler != null)
                        {
                            errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_8 + MIN_READ_TIMEOUT); 
                        }
                    }
                    
                    if (errorHandler != null)
                    {
                        errorHandler.setErrorMessage(null);
                    }
                    provider.setReadTimeout(val);
                }
                catch (NumberFormatException ex)
                {
                    errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_9); 
                    return;
                }
            }
        });
        timeoutText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        label = new Label(tailGroup, SWT.LEFT);
        label.setText(Messages.LoggingPreferenceWidget_10); 
        label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        
        bufferText = new Text(tailGroup, SWT.LEFT | SWT.BORDER);
        bufferText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String contentText = bufferText.getText();
                try
                {
                    int val = Integer.parseInt(contentText);
                    if (val <= 0)
                    {
                        if (errorHandler != null)
                        {
                            errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_11); 
                        }
                    }
                    
                    if (errorHandler != null)
                    {
                        errorHandler.setErrorMessage(null);
                    }
                    provider.setReadBuffer(val);
                }
                catch (NumberFormatException ex)
                {
                    errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_12); 
                    return;
                }
            }
        });
        bufferText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        label = new Label(tailGroup, SWT.LEFT);
        label.setText(Messages.LoggingPreferenceWidget_13); 
        label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        
        backlogText = new Text(tailGroup, SWT.LEFT | SWT.BORDER);
        backlogText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String contentText = backlogText.getText();
                try
                {
                    int val = Integer.parseInt(contentText);
                    if (val <= 0)
                    {
                        if (errorHandler != null)
                        {
                            errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_14); 
                        }
                    }
                    
                    if (errorHandler != null)
                    {
                        errorHandler.setErrorMessage(null);
                    }
                    provider.setBacklogLines(val);
                }
                catch (NumberFormatException ex)
                {
                    errorHandler.setErrorMessage(Messages.LoggingPreferenceWidget_15); 
                    return;
                }
            }
        });
        backlogText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }
    
    /**
     * Creates wrapping composite.
     * @param parent - parent.
     */
    private void createWrapping(Composite parent)
    {
        Composite wrappingComposite = new Composite(parent, SWT.NONE);
        wrappingComposite.setLayout(new GridLayout(2, false));
        wrappingComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        
        wrappingBox = new Button(wrappingComposite, SWT.CHECK);
        wrappingBox.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                if (provider != null)
                {
                    provider.setWrapping(wrappingBox.getSelection());
                }
            }
        });
        
        Label label = new Label(wrappingComposite, SWT.NONE);
        label.setText(Messages.LoggingPreferenceWidget_Wrapping_Label); 
    }

    
    /**
     * Creates "Add rule" buttons.
     * @param parent - parent.
     */
    private void createAddRuleButtons(Composite parent)
    {
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(3, false));
        buttonsComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
        
        addStringRuleButton = new Button(buttonsComposite, SWT.NONE);
        addStringRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false)); 
        addStringRuleButton.setToolTipText(Messages.LoggingColorizationWidget_30); 
        addStringRuleButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$
        addStringRuleButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                addRule();
            }
            
        });
        		
		editRuleButton = new Button(buttonsComposite, SWT.NONE);
		editRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		editRuleButton.setToolTipText(Messages.LoggingColorizationWidget_32); 
		editRuleButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/edit.png")); //$NON-NLS-1$
		editRuleButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                editRule();
            }
            
        });

		removeRuleButton = new Button(buttonsComposite, SWT.NONE);
		removeRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		removeRuleButton.setToolTipText(Messages.LoggingColorizationWidget_31); 
		removeRuleButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$
		removeRuleButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                removeRule();
            }
            
        });

        
//        addRegexpRuleButton = new Button(buttonsComposite, SWT.NONE);
//        addRegexpRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false)); 
//        addRegexpRuleButton.setText(Messages.getString("LoggingColorizationWidget.addRegexpRuleButton.text")); 
//        addRegexpRuleButton.addSelectionListener(new SelectionListener()
//        {
//            public void widgetDefaultSelected(SelectionEvent e)
//            {
//            }
//
//            public void widgetSelected(SelectionEvent e)
//            {
//                addRule(true);
//            }
//            
//        });
    }
    
    /**
     * Creates rule buttons.
     * @param parent - parent.
     */
    private void createRuleButtons(Composite parent)
    {
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        buttonsComposite.setLayout(layout);
        buttonsComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
				
		upRuleButton = new Button(buttonsComposite, SWT.NONE);
        upRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        upRuleButton.setToolTipText(Messages.LoggingColorizationWidget_UpRuleButton); 
        upRuleButton.setImage(SWTUtils.getImage(LoggingPlugin.getDefault(), "/icons/upward_nav_on.gif")); //$NON-NLS-1$
        upRuleButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                moveRuleUp();
            }
        });
		
		downRuleButton = new Button(buttonsComposite, SWT.NONE);
		downRuleButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		downRuleButton.setToolTipText(Messages.LoggingColorizationWidget_DownRuleButton); 
		downRuleButton.setImage(SWTUtils.getImage(LoggingPlugin.getDefault(), "/icons/downward_nav_on.gif"));		 //$NON-NLS-1$
		downRuleButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                moveRuleDown();
            } 
        });
    }


    /**
	 * Gets the control for this widget
	 * 
	 * @return - main widget
	 */
	public Control getControl()
	{
//		return coloringComposite;
	    return folder;
	}

	private void loadEditorOptions()
	{
	}

	private void makeScrollableCompositeAware(Control control)
	{
		ColorizationScrolledComposite parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.adaptChild(control);
		}
	}

	private void expandedStateChanged(ExpandableComposite expandable)
	{
		ColorizationScrolledComposite parentScrolledComposite = getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.reflow(true);
		}
	}

	private ColorizationScrolledComposite getParentScrolledComposite(Control control)
	{
		Control parent = control.getParent();
		while (!(parent instanceof ColorizationScrolledComposite) && parent != null)
		{
			parent = parent.getParent();
		}
		if (parent instanceof ColorizationScrolledComposite)
		{
			return (ColorizationScrolledComposite) parent;
		}
		return null;
	}

	private void styleEditorOptions()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		editorExpandable = new ExpandableComposite(displayArea, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT);
		editorExpandable.setText(Messages.LoggingColorizationWidget_33); 
		editorExpandable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		editorExpandable.setLayout(gridLayout);
		editorExpandable.setExpanded(UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(
				IPreferenceConstants.EXPAND_EDITOR_OPTIONS));
		editorExpandable.addExpansionListener(new ExpansionAdapter()
		{
			public void expansionStateChanged(ExpansionEvent e)
			{
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		makeScrollableCompositeAware(editorExpandable);
		Composite inner = new Composite(editorExpandable, SWT.NONE);
		inner.setLayout(gridLayout);
		editorExpandable.setClient(inner);
		override = new Button(inner, SWT.CHECK);
		override.setEnabled(false);
		override.setText(Messages.LoggingColorizationWidget_34); 
		override.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				boolean isSelected = override.getSelection();

				if (isSelected)
				{
					Color blackColor = UnifiedColorManager.getInstance().getColor(black);
					Color grayColor = UnifiedColorManager.getInstance().getColor(new RGB(139, 139, 139));
					IPreferenceStore store = EditorsPlugin.getDefault().getPreferenceStore();
					RGB background = PreferenceConverter
							.getColor(store, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
					RGB lineHighlight = PreferenceConverter.getColor(store,
							AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
					RGB selectionFg = getControl().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT).getRGB();
					RGB selectionBg = getControl().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION).getRGB();
					Color eclipseBackgroundColor = UnifiedColorManager.getInstance().getColor(background);
					Color eclipseLineHighlightColor = UnifiedColorManager.getInstance().getColor(lineHighlight);
					Color eclipseSelectionFgColor = UnifiedColorManager.getInstance().getColor(selectionFg);
					Color eclipseSelectionBgColor = UnifiedColorManager.getInstance().getColor(selectionBg);
					Color systemWidgetBGColor = getControl().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
					provider.setBackgroundColor(eclipseBackgroundColor);
					provider.setCaretColor(blackColor);
					provider.setLineHighlightColor(eclipseLineHighlightColor);
					provider.setSelectionForegroundColor(eclipseSelectionFgColor);
					provider.setSelectionBackgroundColor(eclipseSelectionBgColor);
					provider.setFoldingBackgroundColor(systemWidgetBGColor);
					provider.setFoldingForegroundColor(grayColor);

					backgroundSelector.setColorValue(provider.getBackgroundColor().getRGB());
					lineHighlightSelector.setColorValue(provider.getLineHighlightColor().getRGB());
					selectionForegroundSelector.setColorValue(provider.getSelectionForegroundColor().getRGB());
					selectionBackgroundSelector.setColorValue(provider.getSelectionBackgroundColor().getRGB());
					foldingBackgroundSelector.setColorValue(provider.getFoldingBackgroundColor().getRGB());
					foldingForegroundSelector.setColorValue(provider.getFoldingForegroundColor().getRGB());
					caretSelector.setColorValue(provider.getCaretColor().getRGB());
				}
				else
				{
					provider.setBackgroundColor(null);
					provider.setCaretColor(null);
					provider.setLineHighlightColor(null);
					provider.setSelectionForegroundColor(null);
					provider.setSelectionBackgroundColor(null);
					provider.setFoldingBackgroundColor(null);
					provider.setFoldingForegroundColor(null);
				}
				backgroundSelector.setEnabled(isSelected);
				lineHighlightSelector.setEnabled(isSelected);
				selectionForegroundSelector.setEnabled(isSelected);
				selectionBackgroundSelector.setEnabled(isSelected);
				foldingBackgroundSelector.setEnabled(isSelected);
				foldingForegroundSelector.setEnabled(isSelected);
				caretSelector.setEnabled(isSelected);
			}

		});
		Composite buttons = new Composite(inner, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginLeft = 0;
		gridLayout.makeColumnsEqualWidth = true;
		buttons.setLayout(gridLayout);
		buttons.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginRight = 0;
		gridLayout.marginLeft = 0;
		gridLayout.horizontalSpacing = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.makeColumnsEqualWidth = false;

		Composite bgComp = new Composite(buttons, SWT.NONE);
		bgComp.setLayout(gridLayout);
		bgComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		Label bgLabel = new Label(bgComp, SWT.RIGHT);
		bgLabel.setText(Messages.LoggingColorizationWidget_35); 
		backgroundSelector = new ColorSelector(bgComp);
		backgroundSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setBackgroundColor(UnifiedColorManager.getInstance().getColor((RGB) event.getNewValue()));
			}

		});

		Composite lComp = new Composite(buttons, SWT.NONE);
		lComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		lComp.setLayout(gridLayout);
		Label lLabel = new Label(lComp, SWT.RIGHT);
		lLabel.setText(Messages.LoggingColorizationWidget_36); 
		lineHighlightSelector = new ColorSelector(lComp);
		lineHighlightSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setLineHighlightColor(UnifiedColorManager.getInstance().getColor((RGB) event.getNewValue()));
			}

		});

		Composite sfComp = new Composite(buttons, SWT.NONE);
		sfComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		sfComp.setLayout(gridLayout);
		Label sfLabel = new Label(sfComp, SWT.RIGHT);
		sfLabel.setText(Messages.LoggingColorizationWidget_37); 
		selectionForegroundSelector = new ColorSelector(sfComp);
		selectionForegroundSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setSelectionForegroundColor(UnifiedColorManager.getInstance().getColor(
						(RGB) event.getNewValue()));
			}

		});

		Composite sbComp = new Composite(buttons, SWT.NONE);
		sbComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		sbComp.setLayout(gridLayout);
		Label sbLabel = new Label(sbComp, SWT.RIGHT);
		sbLabel.setText(Messages.LoggingColorizationWidget_38); 
		selectionBackgroundSelector = new ColorSelector(sbComp);
		selectionBackgroundSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setSelectionBackgroundColor(UnifiedColorManager.getInstance().getColor(
						(RGB) event.getNewValue()));
			}

		});

		Composite fbgComp = new Composite(buttons, SWT.NONE);
		fbgComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		fbgComp.setLayout(gridLayout);
		Label fbgLabel = new Label(fbgComp, SWT.RIGHT);
		fbgLabel.setText(Messages.LoggingColorizationWidget_39); 
		foldingBackgroundSelector = new ColorSelector(fbgComp);
		foldingBackgroundSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setFoldingBackgroundColor(UnifiedColorManager.getInstance()
						.getColor((RGB) event.getNewValue()));
			}

		});

		Composite ffgComp = new Composite(buttons, SWT.NONE);
		ffgComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		ffgComp.setLayout(gridLayout);
		Label ffgLabel = new Label(ffgComp, SWT.RIGHT);
		ffgLabel.setText(Messages.LoggingColorizationWidget_40); 
		foldingForegroundSelector = new ColorSelector(ffgComp);
		foldingForegroundSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setFoldingForegroundColor(UnifiedColorManager.getInstance()
						.getColor((RGB) event.getNewValue()));
			}

		});

		Composite cComp = new Composite(buttons, SWT.NONE);
		cComp.setLayoutData(new GridData(SWT.RIGHT, GridData.FILL, true, true));
		cComp.setLayout(gridLayout);
		Label cLabel = new Label(cComp, SWT.RIGHT);
		cLabel.setText(Messages.LoggingColorizationWidget_41); 
		caretSelector = new ColorSelector(cComp);
		caretSelector.addListener(new IPropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent event)
			{
				provider.setCaretColor(UnifiedColorManager.getInstance().getColor((RGB) event.getNewValue()));
			}

		});

		// Hide caret color option on Mac OS X since it causes cursor lag
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			cComp.setVisible(false);
		}

		loadEditorOptions();
		if (provider != null)
		{
			viewer.setContentProvider(provider);
			viewer.setLabelProvider(provider);
			initializeColorizerView();
		}
	}

	private void styleCells()
	{
		CheckboxCellEditor editor = new CheckboxCellEditor(tree);
		ColorCellEditor colorEditor = new ColorCellEditor(tree);
		CellEditor[] editors = new CellEditor[COLUMNS.length];
		editors[0] = editor;
		editors[1] = null;
		editors[2] = null;
		editors[3] = colorEditor;
		editors[4] = editor;
		editors[5] = editors[4];
		editors[6] = editors[5];
		
		ICellModifier modifier = new ICellModifier()
		{

			public void modify(Object element, String property, Object value)
			{
				if (element instanceof Item)
				{
					element = ((Item) element).getData();
				}
				ColorizationStyle style = null;
				if (element instanceof IToken)
				{
					style = provider.getStyle((IToken) element);
				}
				else if (element instanceof Category)
				{
					style = ((Category) element).getStyle();
				}
				if (style != null)
				{
					if (value instanceof Boolean)
					{
						Boolean bool = (Boolean) value;
						if (element instanceof IToken && property == COLUMNS[0])
						{
							provider.removeStyle((IToken) element);
							//regionViewer.refresh(); @regions
						}
						else if (property == COLUMNS[4])
						{
							style.setBold(bool.booleanValue());
						}
						else if (property == COLUMNS[5])
						{
							style.setItalic(bool.booleanValue());
						}
						else if (property == COLUMNS[6])
						{
							style.setUnderline(bool.booleanValue());
						}
					}
					else if (value instanceof RGB)
					{
						RGB rgb = (RGB) value;
						if (property == COLUMNS[3])
						{
							style.setForegroundColor(UnifiedColorManager.getInstance().getColor(rgb));
						}
					}
				}
				else
				{
//					if (element instanceof IToken && value instanceof Boolean && property == COLUMNS[1])
//					{
//						Boolean bool = (Boolean) value;
//						if (bool.booleanValue())
//						{
//							IToken token = (IToken) element;
//							style = new ColorizationStyle();
//							style.setForegroundColor(UnifiedColorManager.getInstance().getColor(black));
//							style.setName(token.getCategory() + "_" + token.getType());
//							provider.addStyle((IToken) element, style);
//						}
//					}
				}
				viewer.update(element, null);
			}

			public Object getValue(Object element, String property)
			{
				ColorizationStyle style = null;
				if (element instanceof IToken)
				{
					style = provider.getStyle((IToken) element);
				}
				else if (element instanceof Category)
				{
					style = ((Category) element).getStyle();
				}
				if (style != null)
				{
					if (property == COLUMNS[0])
					{
						return Boolean.valueOf(true);
					}
					else if (property == COLUMNS[3])
					{
						return style.getForegroundColor().getRGB();
					}
					else if (property == COLUMNS[4])
					{
						return Boolean.valueOf(style.isBold());
					}
					else if (property == COLUMNS[5])
					{
						return Boolean.valueOf(style.isItalic());
					}
					else if (property == COLUMNS[6])
					{
						return Boolean.valueOf(style.isUnderline());
					}
				}
				else if (property == COLUMNS[0])
				{
					return Boolean.valueOf(false);
				}
				return null;
			}

			public boolean canModify(Object element, String property)
			{
				boolean canModify = false;
				ColorizationStyle style = null;
				if (element instanceof IToken)
				{
					style = provider.getStyle((IToken) element);
					if (property == COLUMNS[0])
					{
						canModify = true;
					}
				}
				else if (element instanceof Category)
				{
					style = ((Category) element).getStyle();
				}
				if (style != null)
				{
					canModify = true;
				}
				return canModify;
			}

		};
//		final TreeColumn column0 = new TreeColumn(tree, SWT.LEFT);
//		column0.setWidth(100);
//		column0.setText(COLUMNS[0]);

		final TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setWidth(ICON_COLUMN_WIDTH);
		column1.setText(COLUMNS[0]);

		final TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setWidth(125);
		column2.setText(COLUMNS[1]);

		final TreeColumn column3 = new TreeColumn(tree, SWT.CENTER);
		column3.setWidth(ICON_COLUMN_WIDTH);
		column3.setText(COLUMNS[2]);
		column3.pack();

		final TreeColumn column4 = new TreeColumn(tree, SWT.CENTER);
		column4.setWidth(ICON_COLUMN_WIDTH);
		column4.setText(COLUMNS[3]);
		column4.pack();

		final TreeColumn column5 = new TreeColumn(tree, SWT.CENTER);
		column5.setWidth(ICON_COLUMN_WIDTH);
		column5.setText(COLUMNS[4]);
		column5.pack();

		final TreeColumn column6 = new TreeColumn(tree, SWT.CENTER);
		column6.setWidth(ICON_COLUMN_WIDTH);
		column6.setText(COLUMNS[5]);
		column6.pack();

		final TreeColumn column7 = new TreeColumn(tree, SWT.CENTER);
		column7.setWidth(ICON_COLUMN_WIDTH);
		column7.setText(COLUMNS[6]);
		column7.pack();

		viewer.setColumnProperties(COLUMNS);
		viewer.setCellEditors(editors);
		viewer.setCellModifier(modifier);
	}

	/**
	 * Sets the error handler for this widget
	 * 
	 * @param handler
	 */
	public void setErrorHandler(IErrorHandler handler)
	{
		this.errorHandler = handler;
	}

//	private TextCellEditor createRegionNameEditor() @regions
//	{
//		TextCellEditor nameEditor = new TextCellEditor(table);
//		nameEditor.setValidator(new ICellEditorValidator()
//		{
//
//			public String isValid(Object value)
//			{
//				IToken token = (IToken) viewer.getTree().getSelection()[0].getData();
//				Region region = (Region) table.getSelection()[0].getData();
//				String text = (String) value;
//				if (!region.getName().equals(text) && regionNameExists(text, token))
//				{
//					String message = Messages.LoggingColorizationWidget_42; 
//					if (errorHandler != null)
//					{
//						errorHandler.setErrorMessage(message);
//					}
//					return message;
//				}
//				else
//				{
//					if (errorHandler != null)
//					{
//						errorHandler.setErrorMessage(null);
//					}
//				}
//				return null;
//			}
//
//		});
//		return nameEditor;
//	}

//	private TextCellEditor createRegionOffsetEditor() @regions
//	{
//		TextCellEditor offsetEditor = new TextCellEditor(table);
//		offsetEditor.setValidator(new ICellEditorValidator()
//		{
//
//			public String isValid(Object value)
//			{
//				String text = (String) value;
//				Region region = (Region) table.getSelection()[0].getData();
//				if (region != null)
//				{
//					try
//					{
//						if (text.startsWith(ColorizationConstants.LENGTH_KEYWORD))
//						{
//							String number = text.substring(ColorizationConstants.LENGTH_KEYWORD.length(), text.length());
//							number = number.trim();
//							if (number.length() > 1 && number.charAt(0) == '-')
//							{
//								Integer.parseInt(number);
//							}
//							else
//							{
//								throw new Exception();
//							}
//						}
//						else
//						{
//							Integer.parseInt(text);
//						}
//						if (errorHandler != null)
//						{
//							errorHandler.setErrorMessage(null);
//						}
//					}
//					catch (Exception e1)
//					{
//						String message = Messages.LoggingColorizationWidget_43 + region.getName() + Messages.LoggingColorizationWidget_44; 
//						if (errorHandler != null)
//						{
//							errorHandler.setErrorMessage(message);
//						}
//						return message;
//					}
//				}
//				return null;
//			}
//
//		});
//		return offsetEditor;
//	}

//	private TextCellEditor createRegionLengthEditor() @regions
//	{
//		TextCellEditor lengthEditor = new TextCellEditor(table);
//		lengthEditor.setValidator(new ICellEditorValidator()
//		{
//
//			public String isValid(Object value)
//			{
//				String text = (String) value;
//				Region region = (Region) table.getSelection()[0].getData();
//				if (region != null)
//				{
//					try
//					{
//						if (text.startsWith(ColorizationConstants.LENGTH_KEYWORD))
//						{
//							String number = text.substring(ColorizationConstants.LENGTH_KEYWORD.length(), text.length());
//							number = number.trim();
//							if (number.length() > 1 && number.charAt(0) == '-')
//							{
//								Integer.parseInt(number);
//							}
//							else
//							{
//								throw new Exception();
//							}
//						}
//						else
//						{
//							Integer.parseInt(text);
//						}
//						if (errorHandler != null)
//						{
//							errorHandler.setErrorMessage(null);
//						}
//					}
//					catch (Exception e1)
//					{
//						String message = Messages.LoggingColorizationWidget_45 + region.getName() + Messages.LoggingColorizationWidget_46; 
//						if (errorHandler != null)
//						{
//							errorHandler.setErrorMessage(message);
//						}
//						return message;
//					}
//				}
//				return null;
//			}
//
//		});
//		return lengthEditor;
//	}

//	private void loadRegions() @regions
//	{
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//
//		regionExpandable = new ExpandableComposite(displayArea, SWT.NONE, ExpandableComposite.TWISTIE
//				| ExpandableComposite.CLIENT_INDENT);
//		regionExpandable.setText(Messages.LoggingColorizationWidget_47); 
//		regionExpandable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//		regionExpandable.setLayout(gridLayout);
//		regionExpandable.setExpanded(UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(
//				IPreferenceConstants.EXPAND_REGIONS));
//		regionExpandable.addExpansionListener(new ExpansionAdapter()
//		{
//			public void expansionStateChanged(ExpansionEvent e)
//			{
//				expandedStateChanged((ExpandableComposite) e.getSource());
//			}
//		});
//		makeScrollableCompositeAware(regionExpandable);
//		Composite inner = new Composite(regionExpandable, SWT.NONE);
//		inner.setLayout(gridLayout);
//		regionExpandable.setClient(inner);
//		Composite addRemove = new Composite(inner, SWT.NONE);
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 2;
//		layout.makeColumnsEqualWidth = false;
//		layout.marginHeight = 5;
//		layout.marginWidth = 0;
//		layout.verticalSpacing = 5;
//		layout.horizontalSpacing = 5;
//		addRemove.setLayout(layout);
//		addRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
//
//		addRegion = new Button(addRemove, SWT.PUSH);
//		addRegion.setText(StringUtils.ellipsify(Messages.LoggingColorizationWidget_48)); 
//		addRegion.setEnabled(false);
//		remove = new Button(addRemove, SWT.PUSH);
//		remove.setText(Messages.LoggingColorizationWidget_49); 
//		remove.setEnabled(false);
//		Label formatLabel = new Label(inner, SWT.WRAP);
//		formatLabel
//				.setText(Messages.LoggingColorizationWidget_50); 
//		formatLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//		table = new Table(inner, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
//		regionViewer = new TableViewer(table);
//		table.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				remove.setEnabled(true);
//			}
//
//		});
//		GridData tableLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
//		tableLayoutData.heightHint = tree.getItemHeight() * 10;
//		tableLayoutData.widthHint = 300;
//		table.setLayoutData(tableLayoutData);
//		CheckboxCellEditor editor = new CheckboxCellEditor(table);
//		TextCellEditor nameEditor = createRegionNameEditor();
//		TextCellEditor offsetEditor = createRegionOffsetEditor();
//		TextCellEditor lengthEditor = createRegionLengthEditor();
//		CellEditor[] editors = new CellEditor[REGION_COLUMNS.length];
//		ColorCellEditor colorEditor = new ColorCellEditor(table);
//		editors[0] = nameEditor;
//		editors[1] = offsetEditor;
//		editors[2] = lengthEditor;
//		editors[3] = colorEditor;
//		editors[4] = editor;
//		editors[5] = editor;
//		editors[6] = editor;
//
//		TableColumn column = new TableColumn(table, SWT.LEFT);
//		column.setWidth(125);
//		column.setText(REGION_TABLE_COLUMNS[0]);
//		column.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				regionViewer.setSorter(new RegionSorter(RegionSorter.NAME));
//			}
//
//		});
//
//		column = new TableColumn(table, SWT.LEFT);
//		column.setWidth(75);
//		column.setText(REGION_TABLE_COLUMNS[1]);
//		column.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				regionViewer.setSorter(new RegionSorter(RegionSorter.OFFSET));
//			}
//
//		});
//
//		column = new TableColumn(table, SWT.LEFT);
//		column.setWidth(75);
//		column.setText(REGION_TABLE_COLUMNS[2]);
//		column.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				regionViewer.setSorter(new RegionSorter(RegionSorter.LENGTH));
//			}
//
//		});
//
//		column = new TableColumn(table, SWT.CENTER);
//		column.setWidth(ICON_COLUMN_WIDTH);
//		column.setText(REGION_TABLE_COLUMNS[3]);
//
//		column = new TableColumn(table, SWT.CENTER);
//		column.setWidth(ICON_COLUMN_WIDTH);
//		column.setText(REGION_TABLE_COLUMNS[4]);
//
//		column = new TableColumn(table, SWT.CENTER);
//		column.setWidth(ICON_COLUMN_WIDTH);
//		column.setText(REGION_TABLE_COLUMNS[5]);
//
//		column = new TableColumn(table, SWT.CENTER);
//		column.setWidth(ICON_COLUMN_WIDTH);
//		column.setText(REGION_TABLE_COLUMNS[6]);
//
//		regionViewer.setSorter(new RegionSorter(RegionSorter.OFFSET));
//		table.setSortColumn(table.getColumn(1));
//		table.setHeaderVisible(true);
//		table.setLinesVisible(false);
//
//		regionViewer.setColumnProperties(REGION_COLUMNS);
//		regionViewer.setCellEditors(editors);
//		regionViewer.setContentProvider(regionContentProvider);
//		regionViewer.setLabelProvider(regionLabelProvider);
//		regionViewer.setCellModifier(regionModifier);
//		PreferenceUtils.persist(UnifiedEditorsPlugin.getDefault().getPreferenceStore(), regionViewer.getTable(), 
//		        Messages.LoggingColorizationWidget_51); 
//		addRegionButtonListeners();
//	}

	private boolean regionNameExists(String newName, IToken token)
	{
		Map regions = provider.getRegions(token);
		Iterator iter = regions.values().iterator();
		while (iter.hasNext())
		{
			String name = ((Region) iter.next()).getName();
			if (name.equals(newName))
			{
				return true;
			}
		}
		return false;
	}

//	private void addRegionButtonListeners() @regions
//	{
//		remove.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				IToken token = (IToken) viewer.getTree().getSelection()[0].getData();
//				Region region = (Region) table.getSelection()[0].getData();
//				provider.removeRegion(token, region.getName());
//				regionViewer.setInput(provider.getRegions(token));
//				remove.setEnabled(false);
//				viewer.update(token, null);
//			}
//
//		});
//		addRegion.addSelectionListener(new SelectionAdapter()
//		{
//
//			public void widgetSelected(SelectionEvent e)
//			{
//				final IToken token = (IToken) viewer.getTree().getSelection()[0].getData();
//				InputDialog name = new InputDialog(addRegion.getShell(), Messages.LoggingColorizationWidget_52,
//				        Messages.LoggingColorizationWidget_53, token 
//						.getType()
//						+ Messages.LoggingColorizationWidget_54, new IInputValidator() 
//				{
//
//					public String isValid(String newText)
//					{
//						if (regionNameExists(newText, token))
//						{
//							return Messages.LoggingColorizationWidget_55; 
//						}
//						else
//						{
//							return null;
//						}
//					}
//
//				});
//				name.setBlockOnOpen(true);
//				int rc = name.open();
//				if (rc == InputDialog.OK)
//				{
//					ColorizationStyle style = new ColorizationStyle();
//					style.setName(token.getCategory() + "_" + token.getType() + "_" + name.getValue());  //$NON-NLS-2$
//					style.setForegroundColor(UnifiedColorManager.getInstance().getColor(black));
//					Region region = new Region(0, false, 1, false, style);
//					region.setName(name.getValue());
//					region.setLengthString("1"); 
//					region.setOffsetString("0"); 
//					Map regionMap = (Map) provider.getRegions(token);
//					regionMap.put(region.getName(), region);
//					regionViewer.setInput(regionMap);
//					viewer.update(token, null);
//				}
//			}
//		});
//	}


//	/** 
//	 * Collapses all expandable regions
//	 */
//	public void collapseAll() @regions
//	{
//		if (regionExpandable != null && tokenExpandable != null && editorExpandable != null)
//		{
//			regionExpandable.setExpanded(false);
//			tokenExpandable.setExpanded(false);
//			editorExpandable.setExpanded(false);
//		}
//	}

	/**
	 * Initializes view after colorization loading.
	 */
	private void initializeColorizerView()
	{
		if (override != null) override.setEnabled(provider != null);
		importButton.setEnabled(provider != null);
		exportButton.setEnabled(provider != null);
		viewer.setInput(INPUT);
		TreeItem[] items = tree.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getData() instanceof Category)
			{
				items[i].setBackground(UnifiedColorManager.getInstance().getColor(new RGB(192, 192, 192)));
			}
		}
		if (tree.getItemCount() > 0)
		{
			tree.showItem(tree.getItem(0));
			tree.showColumn(tree.getColumn(0));
		}
	}

	/**
	 * Styles the import/export section
	 */
	private void styleImportExport()
	{
		Group io = new Group(displayArea, SWT.NONE);
		io.setText(Messages.LoggingColorizationWidget_60); 
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 2;
		io.setLayout(layout);
		io.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		importButton = new Button(io, SWT.PUSH);
		importButton.setEnabled(false);
		importButton.setText(StringUtils.ellipsify(Messages.LoggingColorizationWidget_27)); 
		importButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(displayArea.getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.col" });  //$NON-NLS-1$
				dialog.setText(Messages.LoggingColorizationWidget_63); 
				String path = dialog.open();
				if (path != null)
				{
				    if(!MessageDialog.openQuestion(LoggingPlugin.getActiveWorkbenchShell(), 
                            Messages.LoggingColorizationWidget_ConfirmRewritingTitle, 
                            Messages.LoggingColorizationWidget_ConfirmRewritingCurrentMessage))
                    {
                        return;
                    }
					File file = new File(path);
					provider.importColorization(file);
					loadEditorOptions();
					initializeColorizerView();
					//regionViewer.refresh(); @regions
				}
			}

		});
		exportButton = new Button(io, SWT.PUSH);
		exportButton.setEnabled(false);
		exportButton.setText(StringUtils.ellipsify(Messages.LoggingColorizationWidget_64)); 
		exportButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(displayArea.getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.col" });  //$NON-NLS-1$
				dialog.setText(Messages.LoggingColorizationWidget_66); 
				dialog.setFileName("colorization.col");  //$NON-NLS-1$
				String path = dialog.open();
				if (path != null)
				{
					File file = new File(path);
					if (file.exists())
					{
					    if(!MessageDialog.openQuestion(LoggingPlugin.getActiveWorkbenchShell(), 
					            Messages.LoggingColorizationWidget_ConfirmRewritingTitle, 
					            Messages.LoggingColorizationWidget_ConfirmRewritingMessage))
					    {
					        return;
					    }
					}
					
					try
					{
						provider.buildColorizationFile(file);
					}
					catch (LexerException e1)
					{
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.LoggingColorizationWidget_68); 
					}
				}
			}

		});
	}

	/**
	 * Rebuild the styles
	 */
	public void saveStyles()
	{
//		UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.EXPAND_EDITOR_OPTIONS,
//				editorExpandable.isExpanded());
		UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.EXPAND_TOKENS,
				tokenExpandable.isExpanded());
//		UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.EXPAND_REGIONS,
//				regionExpandable.isExpanded()); @regions3
	}

	private void resetWidget()
	{
	    setProvider(provider);
		initializeColorizerView();
		loadEditorOptions();
//		regionViewer.getTable().deselectAll(); @regions
//		addRegion.setEnabled(false); @regions
//		remove.setEnabled(false); @regions
//		regionViewer.refresh(); @regions
	}

	/**
	 * Resets this UI to the default colorization from the LanguageRegistry
	 */
	public void resetToDefaults()
	{
	    TabItem[] selection = folder.getSelection();
	    if (selection != null && selection.length == 1)
	    {
	        TabItem item = selection[0];
	        int index = folder.indexOf(item);
	        if (index == 0)
	        {
	            provider.restoreGeneralDefaults();
	        }
	        else if (index == 1)
	        {
	            provider.restoreColorizationDefautls();
	        }
	        else
	        {
	            provider.resetToLanguageDefaults();
	        }
	    }
	    else
	    {
	        provider.resetToLanguageDefaults();
	    }
    		
	    resetWidget();
	}

	/**
	 * Gets the provider for this widget
	 * 
	 * @return - language structure provider
	 */
	public LoggingStructureProvider getProvider()
	{
		return provider;
	}

	/**
	 * Sets the provider for this widget
	 * 
	 * @param provider
	 */
	public void setProvider(LoggingStructureProvider provider)
	{
		if (coloringComposite != null)
		{
			if (provider != null)
			{
				viewer.setContentProvider(provider);
				viewer.setLabelProvider(provider);
			}
			else
			{
				viewer.setContentProvider(nullContentProvider);
				viewer.setLabelProvider(nullLabelProvider);
			}
			this.provider = provider;
			initializeColorizerView();
			loadEditorOptions();
			autoboldingBox.setSelection(provider.getAutoBolding());
			wrappingBox.setSelection(provider.getWrapping());
			timeoutText.setText(Integer.toString(provider.getReadTimeot()));
			bufferText.setText(Integer.toString(provider.getReadBuffer()));
			backlogText.setText(Integer.toString(provider.getBacklogLines()));
			cursorLineColorSelector.setColorValue(provider.getCursorLineColor());
			textForegroundColorSelector.setColorValue(provider.getTextForegroundColor());
			
			String[] items = charsetCombo.getItems();
			String currentValue = provider.getDefaultEncoding();
	        for (int i = 0; i < items.length; i++)
	        {
	            if (items[i].equals(currentValue))
	            {
	                charsetCombo.select(i);
	            }
	        }
	        
	        FontData[] font = provider.getFont();
	        if (font != null && font.length > 0)
	        {
	            String fontStr = font[0].getName() + "-" + font[0].getHeight();  //$NON-NLS-1$
	            fontStyleLabel.setText(fontStr);
	        }
		}
	}

	/**
     * Edits rule. 
     */
    protected void editRule()
    {
        TreeItem[] selection = tree.getSelection();
        if (selection != null && selection.length != 0)
        {
            IToken selected = getSelectedToken(selection[0]);
            if (selected != null)
            {
                LoggingPreferences.Rule initialContent = null;
                boolean isRegexp = true;
                for (LoggingPreferences.Rule rule : provider.getCurrentRules())
                {
                    if (rule.getName().equals(selected.getType()))
                    {
                        initialContent = rule;
                    }
                }
                
                
                NewRuleDialog dialog = new NewRuleDialog(coloringComposite.getShell(), initialContent);
                if (dialog.open() == Window.OK)
                {
                    
                    provider.updateRule(selected, dialog.getContent(), dialog.isRegexp(), dialog.isCaseInsensitive());
                    viewer.refresh();
                }
            }
        }
    }

    /**
     * Removes rule.
     */
    protected void removeRule()
    {
        TreeItem[] selection = tree.getSelection();
        if (selection != null && selection.length != 0)
        {
            IToken selectedToken = getSelectedToken(selection[0]);
            if (selectedToken != null)
            {
                provider.removeRule(selectedToken);
            }
            viewer.refresh();
        }
    }
    
    /**
     * Gets currently selected rule.
     * @param selection - selection.
     * @return rule or null.
     */
    private IToken getSelectedToken(TreeItem selection)
    {
        IToken token = (IToken) selection.getData();
        return token;
    }

    /**
     * Adds new rule.
     * @param isRegexp - whether rule is regexp-based.
     */
    protected void addRule()
    {
        NewRuleDialog dialog = new NewRuleDialog(coloringComposite.getShell(), null);
        List<String> forbiddenNames = new ArrayList<String>();
        for (LoggingPreferences.Rule rule : provider.getCurrentRules())
        {
            forbiddenNames.add(rule.getName());
        }
        dialog.setForbiddenNames(forbiddenNames);
        
        if (dialog.open() == Window.OK)
        {
            provider.addRuleToBeginning(dialog.getName(), dialog.getContent(), dialog.isRegexp(), dialog.isCaseInsensitive());
            viewer.refresh();
        }
    }
    
    /**
     * Moves rule down.
     */
    protected void moveRuleDown()
    {
        TreeItem[] selection = tree.getSelection();
        if (selection != null && selection.length != 0)
        {
            IToken selectedToken = getSelectedToken(selection[0]);
            if (selectedToken != null)
            {
                provider.moveRuleDown(selectedToken);
                viewer.refresh();
                TreeItem toSelect = getItemByToken(selectedToken);
                if (toSelect != null)
                {
                    tree.setSelection(toSelect);
                }
                
            }
        }
    }
    
    /**
     * Moves rule up.
     */
    private void moveRuleUp()
    {
        TreeItem[] selection = tree.getSelection();
        if (selection != null && selection.length != 0)
        {
            IToken selectedToken = getSelectedToken(selection[0]);
            if (selectedToken != null)
            {
                provider.moveRuleUp(selectedToken);
                viewer.refresh();
                TreeItem toSelect = getItemByToken(selectedToken);
                if (toSelect != null)
                {
                    tree.setSelection(toSelect);
                }
                
            }
        }
    }
    
    private TreeItem getItemByToken(IToken token)
    {
        TreeItem[] items = tree.getItems();
        for (TreeItem item : items)
        {
            if (provider.tokensEqual((IToken) item.getData(), token))
            {
                return item;
            }
        }
        
        return null;
    }

    /**
     * @return
     */
    private LoggingPreferences getPreferences()
    {
        return LoggingPlugin.getDefault().getLoggingPreferences();
    }
}
