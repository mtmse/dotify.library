package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.NumeralStyle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write java doc.
 */
@SuppressWarnings("javadoc")
public class OBFLParserImplTest {

    @Test
    public void testParseNumeralStyle_01() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("upper-alpha");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }

    @Test
    public void testParseNumeralStyle_02() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("upper_alpha");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }

    @Test
    public void testParseNumeralStyle_03() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("A");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }
}
