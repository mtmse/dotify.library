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
package com_indexbraille;

import java.io.OutputStream;

import org.daisy.braille.impl.embosser.ConfigurableEmbosser;
import org.daisy.braille.impl.embosser.SimpleEmbosserProperties;
import org.daisy.braille.utils.api.embosser.EmbosserFactoryException;
import org.daisy.braille.utils.api.embosser.EmbosserWriter;
import org.daisy.braille.utils.api.embosser.EmbosserWriterProperties;
import org.daisy.braille.utils.api.embosser.PrintPage;
import org.daisy.braille.utils.api.embosser.StandardLineBreaks;
import org.daisy.braille.utils.api.embosser.UnsupportedPaperException;
import org.daisy.braille.utils.api.paper.Length;
import org.daisy.braille.utils.api.paper.PageFormat;
import org.daisy.braille.utils.api.table.TableCatalogService;
import org.daisy.braille.utils.api.table.TableFilter;

import com_indexbraille.IndexEmbosserProvider.EmbosserType;

/**
 * Provides an implementation for Index V4/V5 embossers.
 */
public class IndexV4Embosser extends IndexEmbosser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3888325825465502071L;
	static final String TABLE6DOT = "org.daisy.braille.impl.table.DefaultTableProvider.TableType.EN_US";
	static final TableFilter tableFilter = (object) -> {
		return object == null?false:object.getIdentifier().equals(TABLE6DOT);
	};
	private int bindingMargin = 0;

	public IndexV4Embosser(TableCatalogService service, EmbosserType props) {
		super(service, props);

		setTable = service.newTable(TABLE6DOT);
		duplexEnabled = true;

		switch (type) {
		case INDEX_BASIC_D_V4:
		case INDEX_BASIC_D_V5:
		case INDEX_FANFOLD_V5:
			maxCellsInWidth = 49;
			break;
		case INDEX_EVEREST_D_V4:
		case INDEX_EVEREST_D_V5:
		case INDEX_BRAILLE_BOX_V4:
		case INDEX_BRAILLE_BOX_V5:
			maxCellsInWidth = 48;
			break;
		default:
			throw new IllegalArgumentException("Unsupported embosser type");
		}

		maxNumberOfCopies = 10000;
		maxMarginInner = 10;
		maxMarginOuter = 10;
		maxMarginTop = 10;
	}
	
	@Override
	public boolean supportsDuplex() {
		return true;
	}

	@Override
	public TableFilter getTableFilter() {
		return tableFilter;
	}

	@Override
	public boolean supportsPrintPage(PrintPage dim) {
		if (dim==null) {
			return false;
		}
		if (type == EmbosserType.INDEX_BRAILLE_BOX_V4 || type == EmbosserType.INDEX_BRAILLE_BOX_V5) {
			Length across = dim.getLengthAcrossFeed();
			Length along = dim.getLengthAlongFeed();
			if (saddleStitchEnabled) {
				return (across.asMillimeter() == 297 && along.asMillimeter() == 420) ||
						(across.asInches()     == 11  && along.asInches()     == 17);
			} else {
				return (across.asMillimeter() == 297 && along.asMillimeter() == 210) ||
						(across.asInches()     == 11  && along.asInches()     == 8.5) ||
						(across.asInches()     == 11  && along.asInches()     == 11.5);
			}
		} else {
			return super.supportsPrintPage(dim);
		}
	}

	@Override
	public EmbosserWriter newEmbosserWriter(OutputStream os) {

		PageFormat page = getPageFormat();
		if (!supportsPageFormat(page)) {
			throw new IllegalArgumentException(new UnsupportedPaperException("Unsupported paper"));
		}

		int cellsInWidth = (int)Math.floor(getPrintArea(page).getWidth()/getCellWidth());

		if (cellsInWidth > maxCellsInWidth) {
			throw new IllegalArgumentException(new UnsupportedPaperException("Unsupported paper"));
		}
		if (numberOfCopies > maxNumberOfCopies || numberOfCopies < 1) {
			throw new IllegalArgumentException(new EmbosserFactoryException("Invalid number of copies: " + numberOfCopies + " is not in [1, 10000]"));
		}

		byte[] header = getIndexV4Header();
		byte[] footer = new byte[0];

		SimpleEmbosserProperties props =
				new SimpleEmbosserProperties(getMaxWidth(page), getMaxHeight(page))
				.supportsDuplex(duplexEnabled)
				.supportsAligning(supportsAligning());

		if (eightDotsEnabled) {
			return new IndexTransparentEmbosserWriter(os,
					setTable.newBrailleConverter(),
					true,
					header,
					footer,
					props);
		} else {
			return new ConfigurableEmbosser.Builder(os, setTable.newBrailleConverter())
					.breaks(new StandardLineBreaks(StandardLineBreaks.Type.DOS))
					.padNewline(ConfigurableEmbosser.Padding.NONE)
					.footer(footer)
					.embosserProperties(props)
					.header(header)
					.build();
		}
	}

	private byte[] getIndexV4Header() {
		StringBuilder header = new StringBuilder();

		header.append((char)0x1b);
		header.append("D");                                           // Activate temporary formatting properties of a document
		header.append("BT0");                                         // Default braille table
		header.append(",TD0");                                        // Text dot distance = 2.5 mm
		header.append(",LS50");                                       // Line spacing = 5 mm
		header.append(",DP");

		// Page mode
		if (saddleStitchEnabled && !duplexEnabled) {
			header.append('8');
		/*
		} else if (swZFoldingEnabled && !duplexEnabled) {
			header.append('7'); 
		} else if (swZFoldingEnabled) {
			header.append('6');
		*/
		} else if (zFoldingEnabled && !duplexEnabled) {
			header.append('5'); 
		} else if (saddleStitchEnabled) {
			header.append('4');
		} else if (zFoldingEnabled) {
			header.append('3');
		} else if (duplexEnabled) {
			header.append('2');
		} else {
			header.append('1');
		}
		if (numberOfCopies > 1) {
			header.append(",MC");
			header.append(String.valueOf(numberOfCopies));            // Multiple copies
		}
		//header.append(",MI1");                                      // Multiple impact = 1
		header.append(",PN0");                                        // No page number
		header.append(",CH");
		header.append(String.valueOf(getMaxWidth(getPageFormat())));  // Characters per line
		header.append(",LP");
		header.append(String.valueOf(getMaxHeight(getPageFormat()))); // Lines per page
		header.append(",BI");
		header.append(String.valueOf(bindingMargin));                 // Binding margin
		header.append(",TM");
		header.append(String.valueOf(marginTop));                     // Top margin

		header.append(";");

		return header.toString().getBytes();
	}
}
