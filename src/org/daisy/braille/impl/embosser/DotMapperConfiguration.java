package org.daisy.braille.impl.embosser;

public class DotMapperConfiguration {
	private final int[] bitMap;
	private final int cellHeight;
	private final int cellWidth;
	private final char baseCharacter;
	
	public static class Builder {
		private int cellHeight=3;
		private int cellWidth=2;
		private char baseCharacter=0x2800;
		private int[] bitMap = DotMapper.UNICODE_BIT_MAP;

		private Builder() {
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
		
		public Builder map(int[] value) {
			this.bitMap = checkBitMap(value);
			return this;
		}
		
		public DotMapperConfiguration build() {
			return new DotMapperConfiguration(this);
		}
	}


	private DotMapperConfiguration(Builder builder) {
		this.bitMap = builder.bitMap;
		this.cellHeight = builder.cellHeight;
		this.cellWidth = builder.cellWidth;
		this.baseCharacter = builder.baseCharacter;
	}
	
	public static DotMapperConfiguration.Builder builder() {
		return new DotMapperConfiguration.Builder();
	}
	
	public int[] getBitMap() {
		return bitMap;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public char getBaseCharacter() {
		return baseCharacter;
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
