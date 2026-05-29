package org.daisy.dotify.translator.impl.liblouis;

class LiblouisTranslatable {
    private final String text;
    private final int[] charAtts;
    private final int[] interCharAtts;
    /**
     * Break-candidate flag for the position immediately after the last input character.
     * Inter-character attributes only encode positions <em>between</em> two input
     * characters, so a soft hyphen or zero-width space that appears at the very end
     * of the hyphenated source needs to be carried separately. The value uses the same
     * encoding as {@link #interCharAtts} entries.
     */
    private final int trailingAtt;

    LiblouisTranslatable(String text, int[] charAtts, int[] interCharAtts) {
        this(text, charAtts, interCharAtts, 0);
    }

    LiblouisTranslatable(String text, int[] charAtts, int[] interCharAtts, int trailingAtt) {
        this.text = text;
        this.charAtts = charAtts;
        this.interCharAtts = interCharAtts;
        this.trailingAtt = trailingAtt;
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

    int getTrailingAtt() {
        return trailingAtt;
    }

}
