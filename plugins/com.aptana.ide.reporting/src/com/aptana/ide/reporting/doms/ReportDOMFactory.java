package com.aptana.ide.reporting.doms;

import org.eclipse.eclipsemonkey.dom.IMonkeyDOMFactory;

public class ReportDOMFactory implements IMonkeyDOMFactory {

	public Object getDOMroot() {
		return new Report();
	}
}
