package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO: write java doc.
 */
public class NorwegianHyphenationTest {
    private final HyphenatorInterface hyph_nb_NO;

    public NorwegianHyphenationTest() throws HyphenatorConfigurationException {
        HyphenatorInterface h2;
        try {
            String locale = "nb-NO";
            HyphenatorFactory hf = new LatexHyphenatorFactory(LatexHyphenatorCore.getInstance());
            h2 = hf.newHyphenator(locale);
        } catch (HyphenatorConfigurationException e) {
            h2 = null;
        }
        hyph_nb_NO = h2;
    }

    @Test
    public void testHyphenation_Nb_ZeroWidthSpace() throws HyphenatorConfigurationException {
        assertEquals("akevit­testens", hyph_nb_NO.hyphenate("akevittestens"));
    }

    @Test
    public void testHyphenation_Nb_001() throws HyphenatorConfigurationException {
        assertEquals("allmen­nyttige", hyph_nb_NO.hyphenate("allmennyttige"));
    }

    @Test
    public void testHyphenation_Nb_002() throws HyphenatorConfigurationException {
        assertEquals("trafik­knute­punkt", hyph_nb_NO.hyphenate("trafikknutepunkt"));
    }

    @Test
    public void testCompoundWord_Nb_001() throws HyphenatorConfigurationException {
        assertEquals("tryk­k­okingen", hyph_nb_NO.hyphenate("trykkokingen"));
    }
}
