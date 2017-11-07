package com_indexbraille;

import org.daisy.braille.utils.impl.tools.embosser.InternalEmbosserWriterProperties;

interface IndexHeader extends InternalEmbosserWriterProperties {

	static interface Builder {
		Builder transparentMode(boolean transparentMode);
		IndexHeader build();
	}

	byte[] getIndexHeader();
}
