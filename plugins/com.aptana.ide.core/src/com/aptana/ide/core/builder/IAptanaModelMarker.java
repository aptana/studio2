package com.aptana.ide.core.builder;

public interface IAptanaModelMarker
{
	public static final String PROBLEM_MARKER = "com.aptana.ide.core.ui.problem"; //$NON-NLS-1$

	/**
	 * Id marker attribute (value <code>"arguments"</code>). Arguments are concatenated into one String, prefixed with
	 * an argument count (followed with colon separator) and separated with '#' characters. For example: { "foo", "bar"
	 * } is encoded as "2:foo#bar", { } is encoded as "0: "
	 * 
	 * @since 1.5.2
	 */
	public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$

	/**
	 * Id marker attribute (value <code>"id"</code>).
	 * 
	 * @since 1.5.2
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 * ID category marker attribute (value <code>"categoryId"</code>)
	 * 
	 * @since 1.5.2
	 */
	String CATEGORY_ID = "categoryId"; //$NON-NLS-1$
}
