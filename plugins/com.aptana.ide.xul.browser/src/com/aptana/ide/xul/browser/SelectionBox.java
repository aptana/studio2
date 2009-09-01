/*******************************************************************
 *
 * Licensed Materials - Property of IBM
 * 
 * AJAX Toolkit Framework 6-28-496-8128
 * 
 * (c) Copyright IBM Corp. 2006 All Rights Reserved.
 * 
 * U.S. Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 *******************************************************************/
package com.aptana.ide.xul.browser;

import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIBoxObject;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMElementCSSInlineStyle;
import org.mozilla.interfaces.nsIDOMNSDocument;

/*
 * This class manages the creation of Jobs that control the flashing of the DIV. It appends the flashing DIV to the
 * current Document.
 */
public class SelectionBox
{

	static final String DIV_NS = "http://www.w3.org/1999/xhtml"; //$NON-NLS-1$

	// number of times that the SelectionBox will flash
	protected static int TOTAL_FLASH_COUNT = 3;

	// color used during flashing
	protected static String FLASH_COLOR = "#FF0000"; //$NON-NLS-1$

	// color use during hover
	protected static String HOVER_COLOR = "#0000FF"; //$NON-NLS-1$

	// width of the selection box
	protected static int BOX_WIDTH = 2;

	// delay between flash on and off
	protected static int DELAY = 250;

	// the Z level where the SelectionBox is rendered
	protected static int BOX_ZINDEX = 200;

	protected nsIDOMElement mainDiv = null;
	protected nsIDOMCSSStyleDeclaration mainDivStyleDecl = null;

	protected nsIDOMElement northDiv, eastDiv, southDiv, westDiv = null;
	protected nsIDOMCSSStyleDeclaration northDivStyleDecl, eastDivStyleDecl, southDivStyleDecl,
			westDivStyleDecl = null;

	protected Object _lock = new Object(); // use to protect the check for isFlashing()
	protected FlasherJob currentFlashJob = null;

	public SelectionBox(nsIDOMDocument document)
	{
		// create the flasher element
		mainDiv = document.createElementNS(DIV_NS, "DIV"); //$NON-NLS-1$
		mainDiv.setAttribute("id", Activator.INTERNAL_ID + "_SelectionBox"); //$NON-NLS-1$ //$NON-NLS-2$
		mainDiv.setAttribute("class", Activator.INTERNAL_ID); // used to filter out elements //$NON-NLS-1$

		// htmlDocument.getBody().appendChild( mainDiv ); //adding to the body did not support framesets
		document.getDocumentElement().appendChild(mainDiv); // adding to the root of the document (Mozilla still renders
															// when outside of body)

		northDiv = document.createElementNS(DIV_NS, "DIV"); //$NON-NLS-1$
		northDiv.setAttribute("class", Activator.INTERNAL_ID); // used to filter out elements //$NON-NLS-1$
		mainDiv.appendChild(northDiv);

		eastDiv = document.createElementNS(DIV_NS, "DIV"); //$NON-NLS-1$
		eastDiv.setAttribute("class", Activator.INTERNAL_ID); // used to filter out elements //$NON-NLS-1$
		mainDiv.appendChild(eastDiv);

		southDiv = document.createElementNS(DIV_NS, "DIV"); //$NON-NLS-1$
		southDiv.setAttribute("class", Activator.INTERNAL_ID); // used to filter out elements //$NON-NLS-1$
		mainDiv.appendChild(southDiv);

		westDiv = document.createElementNS(DIV_NS, "DIV"); //$NON-NLS-1$
		westDiv.setAttribute("class", Activator.INTERNAL_ID); // used to filter out elements //$NON-NLS-1$
		mainDiv.appendChild(westDiv);

		mainDivStyleDecl = ((nsIDOMElementCSSInlineStyle) mainDiv
				.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID)).getStyle();

		northDivStyleDecl = ((nsIDOMElementCSSInlineStyle) northDiv
				.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID)).getStyle();
		eastDivStyleDecl = ((nsIDOMElementCSSInlineStyle) eastDiv
				.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID)).getStyle();
		southDivStyleDecl = ((nsIDOMElementCSSInlineStyle) southDiv
				.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID)).getStyle();
		westDivStyleDecl = ((nsIDOMElementCSSInlineStyle) westDiv
				.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID)).getStyle();

		initDIV();
	}

	protected void initDIV()
	{

		// set the initial CSS (i.e. borders)
		mainDivStyleDecl.setProperty("position", "absolute", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mainDivStyleDecl.setProperty("z-index", String.valueOf(SelectionBox.BOX_ZINDEX), ""); //$NON-NLS-1$ //$NON-NLS-2$
		mainDivStyleDecl.setProperty("visibility", "hidden", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mainDivStyleDecl.setProperty("width", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mainDivStyleDecl.setProperty("height", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mainDivStyleDecl.setProperty("overflow", "visible", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// set absolute position
		northDivStyleDecl.setProperty("position", "absolute", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		eastDivStyleDecl.setProperty("position", "absolute", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		southDivStyleDecl.setProperty("position", "absolute", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		westDivStyleDecl.setProperty("position", "absolute", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// bounds
		northDivStyleDecl.setProperty("left", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		northDivStyleDecl.setProperty("top", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// northDivStyleDecl.setProperty( "width", "0px", "" ); //dynamic
		northDivStyleDecl.setProperty("height", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// eastDivStyleDecl.setProperty( "left", "0px", "" ); //dynamic
		eastDivStyleDecl.setProperty("top", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		eastDivStyleDecl.setProperty("width", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// eastDivStyleDecl.setProperty( "height", "0px", "" ); //dynamic

		southDivStyleDecl.setProperty("left", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// southDivStyleDecl.setProperty( "top", "0px", "" ); //dynamic
		// southDivStyleDecl.setProperty( "width", "0px", "" ); //dynamic
		southDivStyleDecl.setProperty("height", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		westDivStyleDecl.setProperty("left", "0px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		westDivStyleDecl.setProperty("top", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		westDivStyleDecl.setProperty("width", SelectionBox.BOX_WIDTH + "px", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// westDivStyleDecl.setProperty( "height", "0px", "" ); //dynamic

	}

	public void highlight(nsIDOMElement element, String color)
	{
		synchronized (_lock)
		{
			if (!isFlashing())
			{

				Rectangle bounds = getElementBounds(element);
				positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
				colorDiv(color);
				showDiv();
			}
		}
	}

	public void highlight(nsIDOMElement element)
	{
		synchronized (_lock)
		{
			if (!isFlashing())
			{

				Rectangle bounds = getElementBounds(element);
				positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
				colorDiv(SelectionBox.HOVER_COLOR);
				showDiv();
			}
		}
	}

	public void hide()
	{
		synchronized (_lock)
		{
			if (!isFlashing())
			{

				hideDiv();
			}
		}
	}

	public void flash(nsIDOMElement element)
	{

		synchronized (_lock)
		{

			if (isFlashing())
			{
				currentFlashJob.cancel(); // cancel the currently running job
			}

			// create a new job
			currentFlashJob = new FlasherJob("FLASHING DIV", mainDiv); //$NON-NLS-1$

			Rectangle bounds = getElementBounds(element);
			positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
			colorDiv(SelectionBox.FLASH_COLOR);

			currentFlashJob.setRule(mutexRule); // avoid jobs to run concurrently (if there are multiple in the queue)
			currentFlashJob.setPriority(Job.INTERACTIVE);

			currentFlashJob.schedule();

		}
	}

	protected boolean isFlashing()
	{

		// should already have lock
		if (currentFlashJob == null)
		{
			return false;
		}
		else
		{
			return currentFlashJob.getState() == Job.WAITING | currentFlashJob.getState() == Job.SLEEPING
					| currentFlashJob.getState() == Job.RUNNING;

		}
	}

	protected void showDiv()
	{
		mainDivStyleDecl.setProperty("visibility", "visible", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected void hideDiv()
	{
		mainDivStyleDecl.setProperty("visibility", "hidden", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected void positionDiv(int x, int y, int width, int height)
	{

		// adjust for borders (DIVS)
		width -= SelectionBox.BOX_WIDTH;
		height -= SelectionBox.BOX_WIDTH;

		// check for negative values
		/*
		 * if( x<0 ) x=0; if( y<0 ) y=0;
		 */
		if (width < 0)
			width = 0;
		if (height < 0)
			height = 0;

		mainDivStyleDecl.setProperty("left", x + "px", "important"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mainDivStyleDecl.setProperty("top", y + "px", "important"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// bounds
		northDivStyleDecl.setProperty("width", width + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		eastDivStyleDecl.setProperty("left", width + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		eastDivStyleDecl.setProperty("height", height + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		southDivStyleDecl.setProperty("top", height + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		southDivStyleDecl.setProperty("width", width + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		westDivStyleDecl.setProperty("height", height + "px", ""); // dynamic //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	}

	protected void colorDiv(String color)
	{
		// set background
		northDivStyleDecl.setProperty("background-color", color, ""); //$NON-NLS-1$ //$NON-NLS-2$
		eastDivStyleDecl.setProperty("background-color", color, ""); //$NON-NLS-1$ //$NON-NLS-2$
		southDivStyleDecl.setProperty("background-color", color, ""); //$NON-NLS-1$ //$NON-NLS-2$
		westDivStyleDecl.setProperty("background-color", color, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * This method is used to determine the bounds of a DOM element in the rendered area. It used SCREEN based
	 * coordinates to determine the correct X,Y. This seems to be the only way to get correct Coordinates when the
	 * elements are inside a scrollable area. It is actually riding on the assumption that the ROOT tag is at 0,0 of the
	 * document. Calculations do not include MARGINS.
	 */
	protected Rectangle getElementBounds(nsIDOMElement element)
	{
		nsIDOMNSDocument nsdocument = (nsIDOMNSDocument) element.getOwnerDocument().queryInterface(
				nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID);
		nsIBoxObject box = nsdocument.getBoxObjectFor(element);

		/*
		 * Getting the root element of the document (assuming HTML) and using its Screen coordinates as the origin.
		 */
		nsIDOMDocument rootdocument = mainDiv.getOwnerDocument(); // since the SelectionBox DIV is in the root
																	// document
		nsIDOMElement rootElement = rootdocument.getDocumentElement(); // should be the HTML element
		nsIDOMNSDocument rootnsdocument = (nsIDOMNSDocument) rootdocument
				.queryInterface(nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID);
		nsIBoxObject rootBox = rootnsdocument.getBoxObjectFor(rootElement);
		int originX = rootBox.getScreenX();
		int originY = rootBox.getScreenY();

		return new Rectangle((box.getScreenX() - originX), (box.getScreenY() - originY), box.getWidth(), box
				.getHeight());// , getSite().getShell().getDisplay() );
	}

	/*
	 * This is an implementation of a Job used to Flash the DIV. It schedules itself in order to Flash ON and OFF.
	 */
	class FlasherJob extends Job
	{

		protected nsIDOMElement flashingDIV = null;
		protected nsIDOMCSSStyleDeclaration styleDecl = null;

		// used to call XPCOM calls in the UI thread
		protected XPCOMThreadProxyHelper proxyHelper = new XPCOMThreadProxyHelper(Display.getDefault());

		protected int flashCount = SelectionBox.TOTAL_FLASH_COUNT;
		public boolean isOn = false;

		public FlasherJob(String name, nsIDOMElement flashingDIV)
		{
			super(name);

			this.flashingDIV = (nsIDOMElement) XPCOMThreadProxy.createProxy(mainDiv, proxyHelper);
			;

			nsIDOMElementCSSInlineStyle flasherElementStyles = (nsIDOMElementCSSInlineStyle) this.flashingDIV
					.queryInterface(nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID);

			this.styleDecl = flasherElementStyles.getStyle();
		}

		protected IStatus run(IProgressMonitor monitor)
		{

			// System.out.println( flashCount );
			if (isOn)
			{
				flashOff();
				flashCount--; // decrement after turning off
			}
			else
				flashOn();

			if (flashCount > 0)
				schedule(SelectionBox.DELAY);

			return Status.OK_STATUS;
		}

		private void flashOn()
		{

			styleDecl.setProperty("visibility", "visible", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			isOn = true;

		}

		private void flashOff()
		{

			styleDecl.setProperty("visibility", "hidden", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			isOn = false;
		}

	};

	// avoid concurrent runs
	protected ISchedulingRule mutexRule = new ISchedulingRule()
	{

		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule == this;
		}

	};

	/*
	 * Implementation of IXPCOMThreadProxyHelper to make sure XPCOM class happen on the Display thread.
	 */
	class XPCOMThreadProxyHelper implements IXPCOMThreadProxyHelper
	{
		private Display _display;

		public XPCOMThreadProxyHelper(Display display)
		{
			_display = display;
		}

		public Thread getThread()
		{
			return _display.getThread();
		}

		public void syncExec(Runnable runnable)
		{
			_display.syncExec(runnable);
		}
	};
}
