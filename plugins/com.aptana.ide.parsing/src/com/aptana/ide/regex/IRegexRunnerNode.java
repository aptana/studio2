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
package com.aptana.ide.regex;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Kevin Lindsey
 */
public interface IRegexRunnerNode
{
	/**
	 * Get the accept state value associated with this node
	 * 
	 * @return Returns the accept state for this node. A value of -1 means this node is not an accepting node. All other
	 *         positive values indicate the type of accept this node defines. Typically, this number corresponds to the
	 *         Token index.
	 */
	int getAcceptState();

	/**
	 * Get the outbound transition target for the specified input.
	 * 
	 * @param index
	 *            The outbound transition index
	 * @return Returns the outbound target node index for the given input. A value of -1 means there is no valid
	 *         transition out of this node for the given input.
	 */
	int getItem(int index);

	// TODO: These should probably be in separate interfaces, but that has a significant impact on the current design,
	// so I'm putting here for now

	/**
	 * Initialize this node via a data input stream
	 * 
	 * @param input
	 *            The stream to use to initialize this runner
	 * @throws IOException
	 */
	void read(DataInput input) throws IOException;

	/**
	 * Write out all the state for this node that is needed to initialize it at a later point in time
	 * 
	 * @param output
	 *            The output stream to write this runner's state
	 * @throws IOException
	 */
	void write(DataOutput output) throws IOException;
}
