package org.daisy.braille.utils.impl.tools.embosser;

public class DotMapperConfiguration {
	private final int[] bitMap;
	private final int cellHeight;
	private final int cellWidth;
	private final int inputCellHeight;
	private final char baseCharacter;
	
	public static class Builder {
		private int cellHeight=3;
		private int cellWidth=2;
		private int inputCellHeight=4;
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
		
		/**
		 * Sets the cell height of the input data. For values less than 4, 
		 * some input data may be ignored. For example, if this option is set to 3, 
		 * dots 7 and 8 will be ignored. This is useful when mapping a 6-dot graphic 
		 * onto 8-dot cells.
		 * @param value the height, in the range [1, 4].
		 * @return this builder
		 */
		public Builder inputCellHeight(int value) {
			if (value<1 || value>4) {
				throw new IllegalArgumentException("Value out of range [1, 4]");
			}
			this.inputCellHeight = value;
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
		this.inputCellHeight = builder.inputCellHeight;
		this.baseCharacter = builder.baseCharacter;
	}
	
	/**
	 * Creates a new builder with the default configuration.
	 * The default configuration maps 8-dot unicode patterns to 
	 * 6-dot unicode patterns: Dots 7 and 8 of the first cell
	 * are shifted to dots 1 and 4 of the first cell of the
	 * following line. Dots 1 and 4 of the first cell of the
	 * second line are subsequently shifted to dots 2 and 5 of
	 * the first cell of the second line and so on.
	 * @return returns a new builder
	 */
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
	
	/**
	 * Gets the height of the input cell. If the value is less
	 * than 4, some dots at the bottom of the cell will be ignored.
	 * @return the input cell height
	 */
	public int getInputCellHeight() {
		return inputCellHeight;
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
