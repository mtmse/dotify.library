package org.daisy.dotify.translator.impl.liblouis;

class LiblouisTranslatable {
	private final String text;
	private final int[] charAtts;
	private final int[] interCharAtts;
	private final short[] typeForm;

	LiblouisTranslatable(String text, int[] charAtts, int[] interCharAtts, short[] typeForm) {
		this.text = text;
		this.charAtts = charAtts;
		this.interCharAtts = interCharAtts;
		this.typeForm = typeForm;
	}

	String getText() {
		return text;
	}

	int[] getCharAtts() {
		return charAtts;
	}

	int[] getInterCharAtts() {
		return interCharAtts;
	}

	short[] getTypeForm() {
		return typeForm;
	}

}