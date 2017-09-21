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

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Provides a utility to map eight dot patterns to six dot patterns. If the resulting
 * patterns are aligned without any row spacing, the patterns will appear the same as the 
 * original 8-dot patterns. This can be useful when printing 8-dot files to an embosser 
 * using a 6-dot table.
 * 
 * @author Joel Håkansson
 *
 */
public class EightToSixDotMapper {
	static final int[] UNICODE_BIT_MAP = {0x01, 0x08, 0x02, 0x10, 0x04, 0x20, 0x40, 0x80};
	private final int[] bitMap;
	private final int width;
	private final int cellHeight;
	private final int cellWidth;
	private final char baseCharacter;
	private ArrayList<BitSet> bs;
	private StringBuilder sb;
	
	public static class Builder {
		private int width;
		private int cellHeight=3;
		private int cellWidth=2;
		private char baseCharacter=0x2800;
		private int[] bitMap = UNICODE_BIT_MAP;

		public Builder(int width) {
			this.width = width;
		}
		
		public Builder cellHeight(int value) {
			if (value<1 || value>4) {
				throw new IllegalArgumentException("Value out of range [1, 4]");
			}
			this.cellHeight = value;
			return this;
		}
		
		public Builder cellWidth(int value) {
			if (value<1 || value>2) {
				throw new IllegalArgumentException("Value out of range [1, 2]");
			}
			this.cellWidth = value;
			return this;
		}
		
		public Builder baseCharacter(char value) {
			this.baseCharacter = value;
			return this;
		}
		
		public Builder bitMap(int[] value) {
			this.bitMap = checkBitMap(value);
			return this;
		}
		
		public EightToSixDotMapper build() {
			return new EightToSixDotMapper(this);
		}
	}

	/**
	 * Creates a new SixDotMapper with the specified line length
	 * @param width the length of the lines, in characters
	 */
	public EightToSixDotMapper(int width) {
		this(new Builder(width));
	}
	
	private EightToSixDotMapper(Builder builder) {
		this.width = builder.width;
		this.bitMap = builder.bitMap;
		this.cellHeight = builder.cellHeight;
		this.cellWidth = builder.cellWidth;
		this.baseCharacter = builder.baseCharacter;
		this.bs = new ArrayList<>();
		sb = new StringBuilder();		
	}

	/**
	 * Writes a string of braille. Values must be between 0x2800 and 0x28FF.
	 * @param braille characters in the range 0x2800 to 0x28FF
	 * @throws IllegalArgumentException if the number of characters exceeds the line width
	 */
	public void write(String braille) {
		if (sb.length()+braille.length()>width) {
			throw new IllegalArgumentException("The maximum number of characters on a line was exceeded.");
		}
		sb.append(braille);
	}

	/**
	 * Starts a new line
	 * @param rowgap the row gap following the line currently in the buffer
	 */
	public void newLine(int rowgap) {
		flush();
		for (int i=0; i<rowgap; i++) {
			bs.add(new BitSet(width*2));
		}
	}

	/**
	 * Flushes the last line of characters. This will empty the buffer.
	 */
	public void flush() {
		flushToBitSet();
	}

	private void flushToBitSet() {
		String t = sb.toString();
		for (int i=0; i<4; i++) {
			BitSet s = new BitSet(width*2);
			int j=0;
			for (char c : t.toCharArray()) {
				s.set(j, (c&UNICODE_BIT_MAP[i*2])==UNICODE_BIT_MAP[i*2]);
				s.set(j+1, (c&UNICODE_BIT_MAP[i*2+1])==UNICODE_BIT_MAP[i*2+1]);
				j=j+2;
			}
			//System.err.println(s);
			bs.add(s);
		}
		sb = new StringBuilder();
	}

	public boolean hasMoreFullLines() {
		return bs.size()>=cellHeight;
	}

	public boolean hasMoreLines() {
		return bs.size()>0;
	}

	/**
	 * Reads a line from the output buffer. When the last line is read, the grid alignment resets (the
	 * characters are padded to their full cell height).
	 * @return returns the line or null if the buffer is empty
	 */
	public String readLine() {
		if (bs.size()==0) {
			return null;
		}
		String res = getFirstRow();
		removeRemoveRow();
		return res;
	}
	
	/**
	 * Converts the upper part of the bit set to a row of characters.
	 * @return returns the top row as characters
	 */
	String getFirstRow() {
		StringBuilder res = new StringBuilder();
		BitSet s;
		// make a row
		for (int j=0; j<width*(3-cellWidth); j++) {
			char c = baseCharacter;
			for (int i=0; i<cellHeight; i++) {
				if (bs.size()>i) {
					s = bs.get(i);
					for (int k=0; k<cellWidth; k++) {
						if (s.get(j*cellWidth+k)) {
							c |= bitMap[i*cellWidth+k];
						}
					}
				}
			}
			res.append(c);
		}
		return res.toString();
	}

	void removeRemoveRow() {
		for (int i=0; i<cellHeight; i++) {
			if (bs.size()>0) {
				bs.remove(0);
			}
		}		
	}

	static int[] checkBitMap(int[] value) {
		int a = 0;
		for (int v : value) {
			if (!isPowerOfTwo(v)) {
				throw new IllegalArgumentException("Value " + v + " is not a power of two.");
			}
			int ax = a;
			a |= v;
			if (ax==a) {
				throw new IllegalArgumentException("A value in the bit map isn't unique.");
			}
		}
		return value;
	}

	/**
	 * Checks that a value is a power of two.
	 * @param x the value to test
	 * @return returns true if the value is a power of two, false otherwise
	 */
	static boolean isPowerOfTwo(int x) {
		return (x & (x - 1)) == 0;
	}

}