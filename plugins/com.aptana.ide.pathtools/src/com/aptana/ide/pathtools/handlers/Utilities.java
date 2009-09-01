package com.aptana.ide.pathtools.handlers;

import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.ide.pathtools.CommandLauncher;
import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;

/**
 * This implements some utility methods.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class Utilities {
	
	public static void launch(String command, File fileObject) {
		// Launch the explore command
		CommandLauncher.launch(formatCommand(command, fileObject, true));
	}
	
	public static String formatCommand(String command, File fileObject) {
		return formatCommand(command, fileObject, false);
	}
	
	private static String formatCommand(String command, File fileObject, boolean escapeDoublebackslashes) {
	    String[] paths;
	    if (fileObject.getParentFile() == null) {
            paths = new String[] {
                    fileObject.getAbsolutePath().replace('/',
                            File.separatorChar).replace('\\',
                            File.separatorChar), "", //$NON-NLS-1$
                    fileObject.getAbsolutePath().replace('\\', '/'), "", //$NON-NLS-1$
                    fileObject.getAbsolutePath().replace('/', '\\'), "", //$NON-NLS-1$
                    fileObject.getName(), "" }; //$NON-NLS-1$
	        
	    } else {
	        paths = new String[] {
				fileObject.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar),
				fileObject.getParentFile().getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar),
				fileObject.getAbsolutePath().replace('\\', '/'),
				fileObject.getParentFile().getAbsolutePath().replace('\\', '/'),
				fileObject.getAbsolutePath().replace('/', '\\'),
				fileObject.getParentFile().getAbsolutePath().replace('/', '\\'),
				fileObject.getName(),
				fileObject.getParentFile().getName()};
	    }
		return MessageFormat.format(Utilities
				.convertParameters(command), (escapeDoublebackslashes ? escapeDoublebackslashes(paths) : paths));
	}
	
	private static Object[] escapeDoublebackslashes(String[] in) {
		String[] out = new String[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = in[i].replaceAll(Pattern.quote("\\\\"), Matcher.quoteReplacement("\\\\\\\\"));  //$NON-NLS-1$//$NON-NLS-2$
		}
		return out;
	}
	
	static String convertParameters(String command) {
		return command.replaceAll(Pattern.quote(PathtoolsPreferences.FILE_PATH), "{0}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PARENT_PATH), "{1}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PATH_SLASHES), "{2}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PARENT_PATH_SLASHES), "{3}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PATH_BACKSLASHES), "{4}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PARENT_PATH_BACKSLASHES), "{5}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_NAME), "{6}").replaceAll( //$NON-NLS-1$
				Pattern.quote(PathtoolsPreferences.FILE_PARENT_NAME), "{7}"); //$NON-NLS-1$
	}

	/**
	 * Parses parameters from a given string in shell-like manner. Users of the
	 * Bourne shell (e.g. on Unix) will already be familiar with the behavior.
	 * For example, when using <code>java.lang.ProcessBuilder</code> (Execution
	 * API) you should be able to:
	 * <ul>
	 * <li>Include command names with embedded spaces, such as
	 * <code>c:\Program Files\jdk\bin\javac</code>.
	 * <li>Include extra command arguments, such as <code>-Dname=value</code>.
	 * <li>Do anything else which might require unusual characters or
	 * processing. For example:
	 * <p>
	 * <code><pre>
	 * "c:\program files\jdk\bin\java" -Dmessage="Hello /\\/\\ there!" -Xmx128m
	 * </pre></code>
	 * <p>
	 * This example would create the following executable name and arguments:
	 * <ol>
	 * <li> <code>c:\program files\jdk\bin\java</code>
	 * <li> <code>-Dmessage=Hello /\/\ there!</code>
	 * <li> <code>-Xmx128m</code>
	 * </ol>
	 * Note that the command string does not escape its backslashes--under the
	 * assumption that Windows users will not think to do this, meaningless
	 * escapes are just left as backslashes plus following character.
	 * </ul>
	 * <em>Caveat</em>: even after parsing, Windows programs (such as the Java
	 * launcher) may not fully honor certain characters, such as quotes, in
	 * command names or arguments. This is because programs under Windows
	 * frequently perform their own parsing and unescaping (since the shell
	 * cannot be relied on to do this). On Unix, this problem should not occur.
	 * 
	 * Copied from org.openide.util.Utilities.
	 * 
	 * @param s
	 *            a string to parse
	 * @return an array of parameters
	 */
	public static String[] parseParameters(String s) {
		int NULL = 0x0; // STICK + whitespace or NULL + non_"
		int INPARAM = 0x1; // NULL + " or STICK + " or INPARAMPENDING + "\ //
		// NOI18N
		int INPARAMPENDING = 0x2; // INPARAM + \
		int STICK = 0x4; // INPARAM + " or STICK + non_" // NOI18N
		int STICKPENDING = 0x8; // STICK + \
		List<String> params = new LinkedList<String>();
		char c;

		int state = NULL;
		StringBuffer buff = new StringBuffer(20);
		int slength = s.length();

		for (int i = 0; i < slength; i++) {
			c = s.charAt(i);

			if (Character.isWhitespace(c)) {
				if (state == NULL) {
					if (buff.length() > 0) {
						params.add(buff.toString());
						buff.setLength(0);
					}
				} else if (state == STICK) {
					params.add(buff.toString());
					buff.setLength(0);
					state = NULL;
				} else if (state == STICKPENDING) {
					buff.append('\\');
					params.add(buff.toString());
					buff.setLength(0);
					state = NULL;
				} else if (state == INPARAMPENDING) {
					state = INPARAM;
					buff.append('\\');
					buff.append(c);
				} else { // INPARAM
					buff.append(c);
				}

				continue;
			}

			if (c == '\\') {
				if (state == NULL) {
					++i;

					if (i < slength) {
						char cc = s.charAt(i);

						if ((cc == '"') || (cc == '\\')) {
							buff.append(cc);
						} else if (Character.isWhitespace(cc)) {
							buff.append(c);
							--i;
						} else {
							buff.append(c);
							buff.append(cc);
						}
					} else {
						buff.append('\\');

						break;
					}

					continue;
				} else if (state == INPARAM) {
					state = INPARAMPENDING;
				} else if (state == INPARAMPENDING) {
					buff.append('\\');
					state = INPARAM;
				} else if (state == STICK) {
					state = STICKPENDING;
				} else if (state == STICKPENDING) {
					buff.append('\\');
					state = STICK;
				}

				continue;
			}

			if (c == '"') {
				if (state == NULL) {
					state = INPARAM;
				} else if (state == INPARAM) {
					state = STICK;
				} else if (state == STICK) {
					state = INPARAM;
				} else if (state == STICKPENDING) {
					buff.append('"');
					state = STICK;
				} else { // INPARAMPENDING
					buff.append('"');
					state = INPARAM;
				}

				continue;
			}

			if (state == INPARAMPENDING) {
				buff.append('\\');
				state = INPARAM;
			} else if (state == STICKPENDING) {
				buff.append('\\');
				state = STICK;
			}

			buff.append(c);
		}

		// collect
		if (state == INPARAM) {
			params.add(buff.toString());
		} else if ((state & (INPARAMPENDING | STICKPENDING)) != 0) {
			buff.append('\\');
			params.add(buff.toString());
		} else { // NULL or STICK

			if (buff.length() != 0) {
				params.add(buff.toString());
			}
		}

		String[] ret = params.toArray(new String[0]);

		return ret;
	}

}
