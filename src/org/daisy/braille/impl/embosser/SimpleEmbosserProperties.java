/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.braille.impl.embosser;

import org.daisy.braille.utils.api.embosser.EmbosserWriterProperties;

/**
 * Provides a simple way to implement EmbosserProperties
 * @author Joel Håkansson
 */
public class SimpleEmbosserProperties implements EmbosserWriterProperties {
	private double cellWidth = 6;
	private double cellHeight = 10;
	private boolean supportsDuplex=false;
	private boolean supportsAligning=false;
	private final int maxHeight;
	private final int maxWidth;

	/**
	 * Creates a new SimpleEmbosserProperties with all "supports" properties set to false and cell width = 6
	 * and cell height = 10
	 * @param maxWidth the maximum width, in characters
	 * @param maxHeight the maximum height, in rows
	 */
	public SimpleEmbosserProperties(int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}


	/**
	 * Sets the value of duplex support
	 * @param val the new value
	 * @return returns this object
	 */
	public SimpleEmbosserProperties supportsDuplex(boolean val) { supportsDuplex = val; return this; }

	/**
	 * Sets the value of aligning support
	 * @param val the new value
	 * @return returns this object
	 */
	public SimpleEmbosserProperties supportsAligning(boolean val) { supportsAligning = val; return this; }

	/**
	 * Sets the value of cell width
	 * @param val the new value
	 * @return returns this object
	 */
	public SimpleEmbosserProperties cellWidth(double val) { cellWidth = val; return this; }

	/**
	 * Sets the value of cell height
	 * @param val the new value
	 * @return returns this object
	 */
	public SimpleEmbosserProperties cellHeight(double val) { cellHeight = val; return this; }

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	@Deprecated
	public boolean supports8dot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsAligning() {
		return supportsAligning;
	}

	public boolean supportsDuplex() {
		return supportsDuplex;
	}

	@Override
	@Deprecated
	public boolean supportsVolumes() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean supportsZFolding() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean supportsPrintMode(PrintMode mode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the cell width, in millimeters
	 * @return returns the cell width, in millimeters
	 */
	public double getCellWidth() {
		return cellWidth;
	}

	/**
	 * Gets the cell height, in millimeters
	 * @return returns the cell height, in millimeters
	 */
	public double getCellHeight() {
		return cellHeight;
	}
}
