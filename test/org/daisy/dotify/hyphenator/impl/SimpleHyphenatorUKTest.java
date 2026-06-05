package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the SimpleHyphenator with the same phrases as the old Latex hyphenator.
 */
public class SimpleHyphenatorUKTest {
    private HyphenatorInterface hyphenator;

    @Before
    public void before() {
        try {
            hyphenator = new SimpleHyphenatorFactory().newHyphenator("en");
        } catch (Exception ignored) {
        }
    }

    private String hyph(String hyph) {
        return hyphenator.hyphenate(hyph);
    }

    @Test
    public void testComplicatedWords() {
        assertEquals("Hy\u00ADphen\u00ADa\u00ADtion", hyph("Hyphenation"));
        assertEquals("Lit\u00ADer\u00ADally", hyph("Literally"));
        assertEquals("Ironic", hyph("Ironic"));
        assertEquals("Ir\u00ADregard\u00ADless", hyph("Irregardless"));
        assertEquals("Whom", hyph("Whom"));
        assertEquals("Col\u00ADonel", hyph("Colonel"));
        assertEquals("Non\u00ADplussed", hyph("Nonplussed"));
        assertEquals("Dis\u00ADin\u00ADter\u00ADested", hyph("Disinterested"));
        assertEquals("Enorm\u00ADity", hyph("Enormity"));
        assertEquals("Lieu\u00ADten\u00ADant", hyph("Lieutenant"));
        assertEquals("Un\u00ADabashed", hyph("Unabashed"));

        assertEquals("between", hyph("between"));
        assertEquals("beowulf", hyph("beowulf"));
        assertEquals("aca\u00ADdemic", hyph("academic"));

        assertEquals("some\u00ADthing", hyph("something"));

        assertEquals(
            "Some\u00ADtimes to un\u00ADder\u00ADstand a word's mean\u00ADing you need more than a " +
            "defin\u00ADi\u00ADtion; you need to see the word used in a sen\u00ADtence.",
            hyph(
                "Sometimes to understand a word's meaning you need more than a definition; you need to see the " +
                "word used in a sentence."
            )
        );
    }
}
